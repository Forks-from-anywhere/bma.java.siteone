package bma.siteone.netty.thrift.gate.impl;

import java.net.MalformedURLException;
import java.net.URL;

import bma.common.langutil.core.ExceptionUtil;
import bma.common.langutil.io.HostPort;
import bma.common.netty.pool.NettyChannelPool;
import bma.siteone.netty.thrift.gate.NTGAgent;
import bma.siteone.netty.thrift.gate.NTGAgentFactory;
import bma.siteone.netty.thrift.remote.RuntimeRemote;

public class NTGAgentFactoryCore implements NTGAgentFactory,
		NTGAgentFactoryCoreParent {

	protected NTGAgentFactoryCoreParent parent;

	protected NettyChannelPool pool;
	protected RuntimeRemote runtimeRemote;

	protected ProxyInfo mainInfo;
	protected ProxyInfo failoverInfo;

	public void setUrl(URL url) {
		mainInfo = new ProxyInfo();
		mainInfo.setType("http");
		mainInfo.setUrl(url);
	}

	public void setUrlString(String url) {
		try {
			setUrl(new URL(url));
		} catch (MalformedURLException e) {
			throw ExceptionUtil.throwRuntime(e);
		}
	}

	public void setFailoverUrl(URL failoverUrl) {
		failoverInfo = new ProxyInfo();
		failoverInfo.setType("http");
		failoverInfo.setUrl(failoverUrl);
	}

	public void setFailoverUrlString(String url) {
		try {
			setFailoverUrl(new URL(url));
		} catch (MalformedURLException e) {
			throw ExceptionUtil.throwRuntime(e);
		}
	}

	public void setHost(HostPort host) {
		mainInfo = new ProxyInfo();
		mainInfo.setType("socket");
		mainInfo.setHost(host);
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
		failoverInfo = new ProxyInfo();
		failoverInfo.setType("socket");
		failoverInfo.setHost(failoverHost);
	}

	public void setFailoverHostString(String v) {
		HostPort host = new HostPort();
		host.setHostString(v, 9090);
		setFailoverHost(host);
	}

	public ProxyInfo getMainInfo() {
		return mainInfo;
	}

	public void setMainInfo(ProxyInfo mainInfo) {
		this.mainInfo = mainInfo;
	}

	public ProxyInfo getFailoverInfo() {
		return failoverInfo;
	}

	public void setFailoverInfo(ProxyInfo failoverInfo) {
		this.failoverInfo = failoverInfo;
	}

	public NTGAgentProxy create(ProxyInfo info, boolean rr) {
		if (info.isSocket()) {
			return new NTGAgentProxy(pool, info.getHost(), rr
					&& info.isCheckRuntimeRemote() ? this.runtimeRemote : null);
		} else {
			NTGAgentProxy proxy = new NTGAgentProxy(pool, info.getUrl());
			proxy.setVHost(info.getVhost());
			return proxy;
		}
	}

	@Override
	public NTGAgent newAgent() {
		NTGAgentProxy main = create(mainInfo, false);
		NTGAgent r = main;
		if (failoverInfo != null) {
			if (mainInfo.isCheckRuntimeRemote()) {
				main.setRuntimeRemote(runtimeRemote);
			}
			NTGAgentProxy second = create(failoverInfo, true);
			r = new NTGAgentFailover(main, second);
		}
		return r;
	}
}
