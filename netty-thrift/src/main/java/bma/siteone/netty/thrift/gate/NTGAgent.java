package bma.siteone.netty.thrift.gate;

import bma.common.netty.pool.NettyChannelPool;
import bma.siteone.netty.thrift.gate.impl.SimpleMessageContext;

public interface NTGAgent {

	public boolean process(NettyChannelPool pool, SimpleMessageContext ctx);

}
