package bma.siteone.netty.thrift.gate.impl;

import bma.common.netty.pool.NettyChannelPool;
import bma.siteone.netty.thrift.remote.RuntimeRemote;

public interface NTGAgentFactoryCoreParent {

	public NettyChannelPool getPool();
	
	public RuntimeRemote getRuntimeRemote();
}