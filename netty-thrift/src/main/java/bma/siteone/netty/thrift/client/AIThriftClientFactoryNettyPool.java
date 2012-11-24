package bma.siteone.netty.thrift.client;

import org.apache.thrift.TException;
import org.jboss.netty.channel.Channel;

import bma.common.langutil.ai.stack.AIStack;
import bma.common.langutil.ai.stack.AIStackStep;
import bma.common.netty.client.NettyClient;
import bma.common.netty.pool.NettyChannelPool;
import bma.common.thrift.ThriftClientConfig;
import bma.common.thrift.ai.AIThriftClient;
import bma.common.thrift.ai.AIThriftClientFactory;

public class AIThriftClientFactoryNettyPool implements AIThriftClientFactory {

	final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(AIThriftClientFactoryNettyPool.class);

	protected NettyChannelPool pool;

	public NettyChannelPool getPool() {
		return pool;
	}

	public void setPool(NettyChannelPool pool) {
		this.pool = pool;
	}

	@Override
	public boolean createThriftClient(AIStack<AIThriftClient> stack,
			ThriftClientConfig cfg) throws TException {
		if (cfg.isHttp()) {
			throw new IllegalStateException("not support http");
			// return createHttpThriftClient(stack, cfg);
		} else {
			return createSocketThriftClient(stack, cfg);
		}
	}

	public boolean createSocketThriftClient(AIStack<AIThriftClient> stack,
			final ThriftClientConfig cfg) throws TException {

		AIStackStep<Channel, AIThriftClient> poolStack = new AIStackStep<Channel, AIThriftClient>(
				stack) {

			@Override
			protected boolean next(Channel result) {
				NettyClient nc = NettyClient.bind(result);
				AIThriftInvokerNettySocket inv = new AIThriftInvokerNettySocket(
						nc, cfg);
				return successForward(new AIThriftClientNettyPool(pool,
						cfg.getHost(), nc, inv));
			}
		};
		return pool.borrowObject(poolStack, cfg.getHost());
	}

}
