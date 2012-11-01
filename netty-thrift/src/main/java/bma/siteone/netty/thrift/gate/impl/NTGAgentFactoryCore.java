package bma.siteone.netty.thrift.gate.impl;

import java.net.MalformedURLException;
import java.net.URL;

import bma.common.langutil.ai.executor.AIExecutor;
import bma.common.langutil.concurrent.TimerManager;
import bma.common.langutil.core.DateTimeUtil;
import bma.common.langutil.core.ExceptionUtil;
import bma.common.langutil.io.HostPort;
import bma.common.langutil.runtime.RuntimeConfig;
import bma.common.netty.pool.NettyChannelPool;
import bma.siteone.netty.thrift.gate.NTGAgent;
import bma.siteone.netty.thrift.gate.NTGAgentFactory;
import bma.siteone.netty.thrift.remote.RuntimeRemote;

public class NTGAgentFactoryCore implements NTGAgentFactory,
		NTGAgentFactoryCoreParent {

	protected NTGAgentFactoryCoreParent parent;

	protected NettyChannelPool pool;
	protected RuntimeRemote runtimeRemote;

	protected ProxyInfoGroup mainInfoGroup;
	protected ProxyInfoGroup failoverInfoGroup;

	protected Long timeout;
	protected TimerManager timer;

	@Override
	public long getTimeout() {
		if (this.timeout != null)
			return this.timeout.longValue();
		if (parent != null)
			return parent.getTimeout();
		return 0;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	public void setTimeoutValue(String v) {
		this.timeout = DateTimeUtil.parsePeriodValue(v,
				this.timeout == null ? 0 : this.timeout);
	}

	public TimerManager getTimerManager() {
		if (this.timer != null)
			return this.timer;
		if (this.parent != null)
			return parent.getTimerManager();
		return AIExecutor.getTimerManager();
	}

	public void setTimer(TimerManager timer) {
		this.timer = timer;
	}

	public ProxyInfoGroup getMainInfoGroup() {
		return mainInfoGroup;
	}

	public void setMainInfoGroup(ProxyInfoGroup mainInfoGroup) {
		this.mainInfoGroup = mainInfoGroup;
	}

	public ProxyInfoGroup getFailoverInfoGroup() {
		return failoverInfoGroup;
	}

	public void setFailoverInfoGroup(ProxyInfoGroup failoverInfoGroup) {
		this.failoverInfoGroup = failoverInfoGroup;
	}

	public void setUrl(URL url) {
		ProxyInfo pi = new ProxyInfo();
		pi.setType("http");
		pi.setUrl(url);
		setMainInfo(pi);
	}

	public void setUrlString(String url) {
		try {
			setUrl(new URL(url));
		} catch (MalformedURLException e) {
			throw ExceptionUtil.throwRuntime(e);
		}
	}

	public void setFailoverUrl(URL failoverUrl) {
		ProxyInfo pi = new ProxyInfo();
		pi.setType("http");
		pi.setUrl(failoverUrl);
		setFailoverInfo(pi);
	}

	public void setFailoverUrlString(String url) {
		try {
			setFailoverUrl(new URL(url));
		} catch (MalformedURLException e) {
			throw ExceptionUtil.throwRuntime(e);
		}
	}

	public void setHost(HostPort host) {
		ProxyInfo pi = new ProxyInfo();
		pi.setType("socket");
		pi.setHost(host);
		setMainInfo(pi);
	}

	public void setHostString(String v) {
		HostPort host = new HostPort();
		host.setHostString(v, 9090);
		setHost(host);
	}

	@Override
	public NettyChannelPool getPool() {
		if (pool != null)
			return pool;
		if (parent != null)
			return parent.getPool();
		return null;
	}

	public void setPool(NettyChannelPool pool) {
		this.pool = pool;
	}

	public NTGAgentFactoryCoreParent getParent() {
		return parent;
	}

	public void setParent(NTGAgentFactoryCoreParent parent) {
		this.parent = parent;
	}

	@Override
	public RuntimeRemote getRuntimeRemote() {
		if (runtimeRemote != null)
			return runtimeRemote;
		if (parent != null)
			return parent.getRuntimeRemote();
		return null;
	}

	public void setRuntimeRemote(RuntimeRemote runtimeRemote) {
		this.runtimeRemote = runtimeRemote;
	}

	public void setFailoverHost(HostPort failoverHost) {
		ProxyInfo pi = new ProxyInfo();
		pi.setType("socket");
		pi.setHost(failoverHost);
		setFailoverInfo(pi);
	}

	public void setFailoverHostString(String v) {
		HostPort host = new HostPort();
		host.setHostString(v, 9090);
		setFailoverHost(host);
	}

	public void setMainInfo(ProxyInfo pi) {
		mainInfoGroup = ProxyInfoGroup.single(pi);
	}

	public void setFailoverInfo(ProxyInfo pi) {
		failoverInfoGroup = ProxyInfoGroup.single(pi);
	}

	@Override
	public NTGAgent newAgent() {
		if (mainInfoGroup == null) {
			return null;
		}

		NTGAgentProcess main = mainInfoGroup.create(getPool(),
				getRuntimeRemote(), failoverInfoGroup != null);
		NTGAgentProcess r = main;
		if (failoverInfoGroup != null) {
			NTGAgentProcess second = failoverInfoGroup.create(getPool(),
					getRuntimeRemote(), false);
			r = new NTGAgentFailover(main, second);
		}
		NTGAgentTimeout agentTimeout = new NTGAgentTimeout(r, getTimeout());
		agentTimeout.setTimer(getTimerManager());
		return agentTimeout;
	}

	public void readFromConfig(RuntimeConfig cfg, String mkey) {
		mainInfoGroup = ProxyInfoGroup.readFromConfig(cfg, mkey + ".main");
		failoverInfoGroup = ProxyInfoGroup.readFromConfig(cfg, mkey
				+ ".failover");
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("main=").append(mainInfoGroup).append(";");
		if (failoverInfoGroup != null) {
			sb.append("failover=").append(failoverInfoGroup).append(";");
		}
		sb.append("timeout=").append(getTimeout()).append(";");
		return sb.toString();
	}
}
