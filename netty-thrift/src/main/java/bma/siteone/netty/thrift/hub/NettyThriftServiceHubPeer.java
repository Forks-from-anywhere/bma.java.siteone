package bma.siteone.netty.thrift.hub;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransport;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;

import bma.common.langutil.ai.common.AIEvent;
import bma.common.langutil.ai.common.AIFunction;
import bma.common.langutil.ai.executor.AIExecutor;
import bma.common.langutil.ai.stack.AIStack;
import bma.common.langutil.ai.stack.AIStackAbstract;
import bma.common.langutil.ai.stack.AIStackConvert;
import bma.common.langutil.ai.stack.AIStackNone;
import bma.common.langutil.ai.stack.AIStackWrap;
import bma.common.langutil.concurrent.TimerManager;
import bma.common.langutil.core.DateTimeUtil;
import bma.common.langutil.spring.ServerBooter;
import bma.common.netty.SupportedNettyChannel;
import bma.common.thrift.ThriftClient;
import bma.common.thrift.entry.AIThriftEntry;
import bma.common.thrift.servicehub.protocol.TAIServiceHub4Peer;
import bma.common.thrift.servicehub.protocol.TServicePointInfo;

/**
 * Hub的Netty远程终端
 * 
 * @author guanzhong
 * 
 */
public class NettyThriftServiceHubPeer implements ServerBooter {

	final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(NettyThriftServiceHubPeer.class);

	protected TimerManager timer;
	protected long heartbeatTime = 0;
	protected long reconnectTime = 3 * 1000;

	protected AIThriftEntry entry;
	protected List<String> hubUrlList;

	// runtime
	protected ThriftClient client;
	protected TAIServiceHub4Peer.Client api;
	protected boolean connecting = false;
	protected boolean logRetry = false;
	protected AIEvent<Boolean> event = AIEvent.createManulResetEvent();

	public List<String> getHubUrlList() {
		return hubUrlList;
	}

	public void setHubUrlList(List<String> hubUrlList) {
		this.hubUrlList = hubUrlList;
	}

	public void setHubUrl(String url) {
		if (this.hubUrlList == null)
			this.hubUrlList = new LinkedList<String>();
		this.hubUrlList.add(url);
	}

	public AIThriftEntry getEntry() {
		return entry;
	}

	public void setEntry(AIThriftEntry entry) {
		this.entry = entry;
	}

	public TimerManager getTimer() {
		if (timer != null)
			return timer;
		return AIExecutor.getTimerManager();
	}

	public void setTimer(TimerManager timer) {
		this.timer = timer;
	}

	public long getHeartbeatTime() {
		return heartbeatTime;
	}

	public long queryHeartbeatTime() {
		if (heartbeatTime <= 0)
			return reconnectTime;
		return heartbeatTime;
	}

	public void setHeartbeatTime(long heartBeartTime) {
		this.heartbeatTime = heartBeartTime;
	}

	public void setHeartbeat(String p) {
		setHeartbeatTime(DateTimeUtil.parsePeriodValue(p, -1));
	}

	public long getReconnectTime() {
		return reconnectTime;
	}

	public void setReconnectTime(long reconnectTime) {
		this.reconnectTime = reconnectTime;
	}

	public void setReconnect(String p) {
		setReconnectTime(DateTimeUtil.parsePeriodValue(p, reconnectTime));
	}

	protected boolean open(AIStack<Boolean> stack, boolean skipOnBreak,
			boolean skipWaitConnect) {
		boolean connect = false;
		synchronized (this) {
			if (this.client == null || !this.client.isOpen()) {
				this.client = null;
				this.api = null;
				if (!connecting) {
					connect = true;
					connecting = true;
				}
				event.resetEvent();
			}
		}

		if (this.client != null) {
			return stack.success(true);
		}
		if (skipOnBreak) {
			return stack.success(false);
		}

		if (connect) {
			AIStackConvert<ThriftClient, Boolean> st = new AIStackConvert<ThriftClient, Boolean>(
					stack) {

				@Override
				protected boolean convert(ThriftClient result) {
					synchronized (this) {
						client = result;
						api = new TAIServiceHub4Peer.Client(
								client.getProtocol());
						connecting = false;
						logRetry = false;
					}

					TTransport ts = result.getTransport();
					if (ts instanceof SupportedNettyChannel) {
						Channel ch = ((SupportedNettyChannel) ts).getChannel();
						ch.getCloseFuture().addListener(
								new ChannelFutureListener() {

									@Override
									public void operationComplete(
											ChannelFuture future)
											throws Exception {
										retry();
									}
								});
					}

					// register all serviceNode
					// register listener
					List<TServicePointInfo> infoList = new ArrayList<TServicePointInfo>(
							0);
					String listenerEntry = null;
					try {
						api.registerThriftServicePeer(
								new AIStackNone<Boolean>(), infoList,
								listenerEntry);
					} catch (TException e) {
						return failure(e);
					}

					// load all service from Hub

					// do heartbeat
					doHeartbeat();

					boolean r = successConvert(true);
					event.checkEvent();
					return r;
				}

				@Override
				public boolean failure(Throwable t) {
					synchronized (this) {
						connecting = false;
					}
					try {
						boolean r = super.failure(t);
						event.allFailure(t);
						return r;
					} finally {
						retry();
					}
				}
			};

			try {
				String url = hubUrlList.get(0);
				return entry.createClient(st, url);
			} catch (TException e) {
				return st.failure(e);
			}
		} else {
			if (skipWaitConnect) {
				return stack.success(null);
			}
			return event.waitEvent(new AIFunction<Boolean>() {
				@Override
				public boolean apply(AIStack<Boolean> stack) {
					return stack.success(true);
				}
			}, stack);
		}

	}

	public void retry() {

		if (!logRetry) {
			if (log.isInfoEnabled()) {
				log.info("wait {} to retry",
						DateTimeUtil.formatPeriod(reconnectTime));
			}
			logRetry = true;
		} else {
			if (log.isDebugEnabled()) {
				log.debug("wait {} to retry",
						DateTimeUtil.formatPeriod(reconnectTime));
			}
		}

		getTimer().postTimerTask(TimerManager.delay(new Runnable() {

			@Override
			public void run() {
				open(new AIStackNone<Boolean>(), false, true);
			}
		}, reconnectTime));
	}

	public void doHeartbeat() {

		if (log.isDebugEnabled()) {
			log.debug("wait {} to heartbeat",
					DateTimeUtil.formatPeriod(queryHeartbeatTime()));
		}

		getTimer().postTimerTask(TimerManager.delay(new Runnable() {

			@Override
			public void run() {
				open(new AIStackAbstract<Boolean>() {

					@Override
					public boolean success(Boolean result) {
						if (result != null && result.booleanValue()) {
							try {
								return api
										.activeThriftServicePeer(new AIStackWrap<Boolean>(
												this) {
											public boolean success(
													Boolean result) {
												doHeartbeat();
												return true;
											}
										});
							} catch (TException e) {
								return failure(e);
							}
						}
						return true;
					}

					@Override
					public boolean failure(Throwable t) {
						if (log.isDebugEnabled()) {
							log.debug("heartbeat fail", t);
						}
						return true;
					}

					@Override
					public AIStack<?> getParent() {
						return null;
					}

				}, false, false);
			}
		}, queryHeartbeatTime()));
	}

	public void start() {
		open(new AIStackNone<Boolean>(), false, true);
	}

	public void close() {
		if (this.client != null) {
			this.client.close();
			this.client = null;
			this.api = null;
		}
	}

	@Override
	public void startServer() {
		start();
	}

	@Override
	public void stopServer() {
		close();
	}

}
