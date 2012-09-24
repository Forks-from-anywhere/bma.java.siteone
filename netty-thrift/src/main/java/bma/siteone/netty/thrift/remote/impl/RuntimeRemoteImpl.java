package bma.siteone.netty.thrift.remote.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;

import bma.common.langutil.ai.AIUtil;
import bma.common.langutil.ai.executor.AIExecutor;
import bma.common.langutil.ai.stack.AIStackROOT;
import bma.common.langutil.concurrent.ProcessTimerTask;
import bma.common.langutil.concurrent.TimerManager;
import bma.common.langutil.core.ExceptionUtil;
import bma.common.langutil.core.SizeUtil;
import bma.common.langutil.core.SizeUtil.Unit;
import bma.common.langutil.core.ValueUtil;
import bma.common.langutil.io.HostPort;
import bma.common.langutil.log.LogLatch;
import bma.common.netty.SupportedNettyChannel;
import bma.common.thrift.ThriftClient;
import bma.common.thrift.ThriftClientFactoryConfig;
import bma.common.thrift.entry.AIThriftEntry;
import bma.siteone.netty.thrift.remote.RuntimeRemote;
import bma.siteone.netty.thrift.remote.thrift.TAIRemoteInfoService;
import bma.siteone.netty.thrift.remote.thrift.TAIRemoteInfoService.Client;
import bma.siteone.netty.thrift.remote.thrift.TRemoteInfo;

public class RuntimeRemoteImpl implements RuntimeRemote {

	final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(RuntimeRemoteImpl.class);

	// config
	private AIThriftEntry thriftEntry;
	private int frameMaxLength = 1024 * 1024;
	private String module = "remoteInfo";

	private TimerManager timer;
	private int retryPeriod = 3 * 1000;
	private int maxIdleTime = 24 * 3600 * 1000;

	private int logLatchPeriod = 5 * 60 * 1000;

	// query support
	private boolean defaultQuery = false;
	private int queryPeriod = 5 * 1000;

	// init connect
	private int initCheckDelay = 5 * 1000;
	private List<HostPort> initCheckHostList;

	// runtime
	private class INFO {
		public TRemoteInfo remoteInfo;
		public ThriftClient client;
		public Client service;
		public AtomicBoolean retry = new AtomicBoolean();
		public boolean querySupport = true;
		public boolean closed;
		public long activeTime;

		public void close() {
			ThriftClient ch = client;
			if (ch != null) {
				client = null;
				ch.close();
			}
		}
	}

	private LogLatch logLatch = new LogLatch(128);

	private ConcurrentHashMap<HostPort, INFO> remoteInfoMap = new ConcurrentHashMap<HostPort, INFO>();

	public RuntimeRemoteImpl() {
		super();
		logLatch.setSilencePeriod(1);
	}

	public boolean isDefaultQuery() {
		return defaultQuery;
	}

	public void setDefaultQuery(boolean defaultQuery) {
		this.defaultQuery = defaultQuery;
	}

	public int getInitCheckDelay() {
		return initCheckDelay;
	}

	public void setInitCheckDelay(int initCheckDelay) {
		this.initCheckDelay = initCheckDelay;
	}

	public List<HostPort> getInitCheckHostList() {
		return initCheckHostList;
	}

	public void setInitCheckHostList(List<HostPort> initCheckList) {
		this.initCheckHostList = initCheckList;
	}

	public void setInitCheckList(List<String> list) {
		if (this.initCheckHostList == null)
			this.initCheckHostList = new ArrayList<HostPort>();
		for (String s : list) {
			HostPort h = new HostPort();
			h.setHostString(s, 9090);
			this.initCheckHostList.add(h);
		}
	}

	public TimerManager getTimer() {
		if (timer == null)
			timer = AIExecutor.getTimerManager();
		return timer;
	}

	public void setTimer(TimerManager timer) {
		this.timer = timer;
	}

	public int getLogLatchPeriod() {
		return logLatchPeriod;
	}

	public void setLogLatchPeriod(int retryLogPeriod) {
		this.logLatchPeriod = retryLogPeriod;
		logLatch.setSilencePeriod(this.logLatchPeriod);
	}

	public int getRetryPeriod() {
		return retryPeriod;
	}

	public void setRetryPeriod(int retryPeriod) {
		this.retryPeriod = retryPeriod;
	}

	public int getMaxIdleTime() {
		return maxIdleTime;
	}

	public void setMaxIdleTime(int maxRetryTime) {
		this.maxIdleTime = maxRetryTime;
	}

	public int getQueryPeriod() {
		return queryPeriod;
	}

	public void setQueryPeriod(int pingPeriod) {
		this.queryPeriod = pingPeriod;
	}

	public AIThriftEntry getThriftEntry() {
		return thriftEntry;
	}

	public void setThriftEntry(AIThriftEntry thriftEntry) {
		this.thriftEntry = thriftEntry;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public int getFrameMaxLength() {
		return frameMaxLength;
	}

	public void setFrameMaxLength(int frameBuffer) {
		this.frameMaxLength = frameBuffer;
	}

	public void setFrameSize(String sz) {
		try {
			this.frameMaxLength = (int) SizeUtil.convert(sz, 1024, Unit.B);
		} catch (Exception e) {
			throw ExceptionUtil.throwRuntime(e);
		}
	}

	public void start() {
		if (this.initCheckHostList != null && this.initCheckHostList.size() > 0) {
			ProcessTimerTask task = TimerManager.delay(new Runnable() {

				@Override
				public void run() {
					for (HostPort h : initCheckHostList) {
						isRemoteBreak(h);
					}
				}
			}, initCheckDelay);
			getTimer().postTimerTask(task);
		}
	}

	public void close() {
		List<INFO> tmp = new ArrayList<INFO>(this.remoteInfoMap.values());
		this.remoteInfoMap.clear();

		for (INFO info : tmp) {
			info.closed = true;
			info.close();
		}
	}

	@Override
	public boolean isRemoteBreak(HostPort host) {
		INFO info = remoteInfoMap.get(host);
		if (info == null) {
			newRemote(host);
		}
		if (info != null && info.remoteInfo != null) {
			info.activeTime = System.currentTimeMillis();
			return !info.remoteInfo.isValid();
		}

		return false;
	}

	@Override
	public boolean isRemoteValid(HostPort host) {
		INFO info = remoteInfoMap.get(host);
		if (info == null) {
			newRemote(host);
		}
		if (info != null && info.remoteInfo != null) {
			info.activeTime = System.currentTimeMillis();
			return info.remoteInfo.isValid();
		}
		return false;
	}

	@Override
	public Map<String, String> getRemoteInfo(HostPort host) {
		INFO info = remoteInfoMap.get(host);
		if (info == null) {
			newRemote(host);
		}
		if (info != null && info.remoteInfo != null) {
			Map<String, String> p = info.remoteInfo.getProperties();
			if (p != null) {
				return new HashMap<String, String>(p);
			}
		}
		return Collections.emptyMap();
	}

	@Override
	public String getRemoteInfo(HostPort host, String name, String def) {
		INFO info = remoteInfoMap.get(host);
		if (info != null && info.remoteInfo != null) {
			Map<String, String> prop = info.remoteInfo.getProperties();
			if (prop != null) {
				return ValueUtil.stringValue(prop.get(name), def);
			}
		}
		return def;
	}

	public void remoteBreak(HostPort host) {
		INFO info = remoteInfoMap.get(host);
		if (info != null && info.remoteInfo != null) {
			info.remoteInfo.setValid(false);
		}
	}

	protected boolean validInfo(HostPort host, INFO info) {
		INFO cur = remoteInfoMap.get(host);
		if (info != cur || info.closed) {
			info.close();
			return false;
		}
		if (System.currentTimeMillis() - info.activeTime > maxIdleTime) {
			if (log.isDebugEnabled()) {
				log.debug("host[{}] max idle time reach, closeIt", host);
			}
			remoteInfoMap.remove(host);
			info.close();
			return false;
		}
		return true;
	}

	protected void retry(final HostPort host, final INFO info) {
		if (!validInfo(host, info)) {
			if (log.isDebugEnabled()) {
				log.debug("remoteRetry [{}] stop", host);
			}
		}
		if (!info.retry.compareAndSet(false, true)) {
			if (log.isDebugEnabled()) {
				log.debug("retry conflict,skip");
			}
			return;
		}
		if (info.remoteInfo == null) {
			info.remoteInfo = new TRemoteInfo();
		}
		info.remoteInfo.setValid(false);
		info.client = null;
		info.service = null;
		if (log.isDebugEnabled() && logLatch.checkLogable("retry-" + host)) {
			log.debug("connect [" + host + "] retry...");
		}
		ProcessTimerTask task = TimerManager.delay(new Runnable() {

			@Override
			public void run() {
				info.retry.set(false);
				connect(host, info);
			}
		}, retryPeriod);
		getTimer().postTimerTask(task);
	}

	protected void connect(final HostPort host, final INFO info) {
		if (log.isDebugEnabled() && logLatch.checkLogable("connect-" + host)) {
			log.debug("connect remote({}) ...", host);
		}
		// create a remoteInfo client
		ThriftClientFactoryConfig cfg = new ThriftClientFactoryConfig();
		cfg.setHost(host);
		cfg.setModule(module);
		cfg.setFrameMaxLength(frameMaxLength);

		AIStackROOT<ThriftClient> stack = new AIStackROOT<ThriftClient>() {

			@Override
			public boolean end(ThriftClient client, Throwable t) {
				if (!validInfo(host, info)) {
					if (log.isDebugEnabled()) {
						log.debug("connect [{}] stop", host);
					}
					return true;
				}

				if (t != null) {
					if (log.isDebugEnabled()
							&& logLatch.checkLogable("connect_fail-" + host)) {
						log.debug("connect [" + host + "] fail", t);
					}
					retry(host, info);
				} else {
					Channel channel = null;
					if (client.getTransport() instanceof SupportedNettyChannel) {
						channel = ((SupportedNettyChannel) client
								.getTransport()).getChannel();
						channel.getCloseFuture().addListener(
								new ChannelFutureListener() {

									@Override
									public void operationComplete(
											ChannelFuture future)
											throws Exception {
										retry(host, info);
									}
								});
					}
					info.client = client;
					info.service = client
							.createAIClient(TAIRemoteInfoService.Client.class);
					if (info.remoteInfo != null) {
						info.remoteInfo.setValid(true);
					}
					if (info.querySupport) {
						requery(host, info, channel == null);
					} else {
						if (log.isDebugEnabled()) {
							log.debug("query not support, skip");
						}
					}
				}
				return false;
			}
		};
		try {
			thriftEntry.createClient(stack, cfg.toEntry());
		} catch (Exception e) {
			AIUtil.safeFailure(stack, e);
		}
	}

	protected void requery(final HostPort host, final INFO info,
			final boolean closeAfterEnd) {
		if (!validInfo(host, info)) {
			if (log.isDebugEnabled()) {
				log.debug("remoteQuery [{}] stop", host);
			}
		}
		ProcessTimerTask task = TimerManager.delay(new Runnable() {

			@Override
			public void run() {
				query(host, info, closeAfterEnd);
			}
		}, queryPeriod);
		getTimer().postTimerTask(task);
	}

	protected void query(final HostPort host, final INFO info,
			final boolean closeAfterEnd) {
		if (!validInfo(host, info)) {
			return;
		}
		Client service = info.service;
		if (service == null) {
			return;
		}

		if (log.isDebugEnabled() && logLatch.checkLogable("query-" + host)) {
			log.debug("query remote({},{}) ...", host, closeAfterEnd);
		}

		AIStackROOT<TRemoteInfo> stack = new AIStackROOT<TRemoteInfo>() {

			@Override
			public boolean end(TRemoteInfo result, Throwable t) {
				if (!validInfo(host, info)) {
					if (log.isDebugEnabled()) {
						log.debug("query [{}] stop", host);
					}
					return true;
				}
				boolean closeIt = closeAfterEnd;
				if (t != null) {
					info.querySupport = false;
					closeIt = true;
					if (log.isDebugEnabled()) {
						log.debug("query [" + host + "] fail", t);
					}
				} else {
					if (log.isDebugEnabled()
							&& logLatch.checkLogable("query_resp-" + host)) {
						log.debug("query remoteInfo [{}] => {}", host, result);
					}
					info.remoteInfo = result;
				}
				if (closeIt) {
					info.close();
					retry(host, info);
				} else {
					requery(host, info, false);
				}
				return true;
			}
		};
		try {
			service.getRuntimeRemoteInfo(stack);
		} catch (Exception e) {
			AIUtil.safeFailure(stack, e);
		}
	}

	public void newRemote(HostPort host) {
		INFO info = new INFO();
		info.activeTime = System.currentTimeMillis();
		info.querySupport = defaultQuery;
		remoteInfoMap.putIfAbsent(host, info);
		if (info == remoteInfoMap.get(host)) {
			if (log.isDebugEnabled()) {
				log.debug("new remote [{}]", host);
			}
			connect(host, info);
		}
	}
}
