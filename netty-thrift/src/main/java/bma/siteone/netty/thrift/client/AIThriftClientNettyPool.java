package bma.siteone.netty.thrift.client;

import org.jboss.netty.channel.Channel;

import bma.common.langutil.io.HostPort;
import bma.common.netty.client.NettyClient;
import bma.common.netty.pool.NettyChannelPool;
import bma.common.thrift.ai.AIThriftInvoker;

public class AIThriftClientNettyPool extends AIThriftClientNetty {

	NettyChannelPool pool;
	HostPort key;

	public AIThriftClientNettyPool(NettyChannelPool pool, HostPort key,
			NettyClient client, AIThriftInvoker invoker) {
		super(client, invoker);
		this.pool = pool;
		this.key = key;
	}

	@Override
	public void close() {
		if (this.pool != null) {
			NettyClient nc = this.getClient();
			if (nc != null) {
				super.client = null;
				Channel ch = nc.getChannel();
				if (ch != null) {
					this.pool.returnObject(key, ch);
				}
			}
			return;
		}
		super.close();
	}

}
