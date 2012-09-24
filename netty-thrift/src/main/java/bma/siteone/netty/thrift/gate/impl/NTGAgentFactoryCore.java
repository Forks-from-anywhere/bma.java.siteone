package bma.siteone.netty.thrift.gate.impl;

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
	protected HostPort host;
	protected HostPort failoverHost;

	public HostPort getHost() {
		return host;
	}

	public void setHost(HostPort host) {
		this.host = host;
	}

	public void setHostString(String v) {
		HostPort host = new HostPort();
		host.setHostString(v, 9090);
		this.host = host;
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

	public HostPort getFailoverHost() {
		return failoverHost;
	}

	public void setFailoverHost(HostPort failoverHost) {
		this.failoverHost = failoverHost;
	}

	public void setFailoverHostString(String v) {
		HostPort host = new HostPort();
		host.setHostString(v, 9090);
		this.failoverHost = host;
	}

	@Override
	public NTGAgent newAgent() {
		NTGAgentProxy main = new NTGAgentProxy(pool, host, null);
		NTGAgent r = main;
		if (failoverHost != null) {
			main.setRuntimeRemote(runtimeRemote);
			NTGAgentProxy second = new NTGAgentProxy(pool, failoverHost,
					runtimeRemote);
			r = new NTGAgentFailover(main, second);
		}
		return r;
	}
}
