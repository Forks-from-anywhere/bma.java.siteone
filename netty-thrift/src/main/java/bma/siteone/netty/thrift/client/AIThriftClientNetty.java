package bma.siteone.netty.thrift.client;

import java.lang.reflect.Constructor;

import bma.common.langutil.core.ExceptionUtil;
import bma.common.netty.client.NettyClient;
import bma.common.thrift.ai.AIThriftClient;
import bma.common.thrift.ai.AIThriftInvoker;
import bma.common.thrift.ai.TAIBaseServiceClient;

public class AIThriftClientNetty implements AIThriftClient {

	protected NettyClient client;
	protected AIThriftInvoker invoker;

	public AIThriftClientNetty(NettyClient client, AIThriftInvoker invoker) {
		super();
		this.client = client;
		this.invoker = invoker;
	}

	@Override
	public void close() {
		client.close();
	}

	@Override
	public <TYPE extends TAIBaseServiceClient> TYPE createAIObject(
			Class<TYPE> cls) {
		try {
			Constructor<TYPE> c = cls.getConstructor(AIThriftInvoker.class);
			return c.newInstance(invoker);
		} catch (Exception e) {
			throw ExceptionUtil.throwRuntime(e);
		}
	}

}
