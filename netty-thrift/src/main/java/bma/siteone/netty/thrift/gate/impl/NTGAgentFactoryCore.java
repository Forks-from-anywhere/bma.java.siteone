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

	protected ProxyInfoGroup mainInfoGroup;
	protected ProxyInfoGroup failoverInfoGroup;

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

		NTGAgentProcess main = mainInfoGroup.create(pool, runtimeRemote,
				failoverInfoGroup != null);
		NTGAgent r = main;
		if (failoverInfoGroup != null) {
			NTGAgentProcess second = failoverInfoGroup.create(pool,
					runtimeRemote, false);
			r = new NTGAgentFailover(main, second);
		}
		return r;
	}
}
