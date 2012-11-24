package bma.siteone.netty.thrift;

import org.junit.Test;

import bma.common.langutil.ai.stack.AIStackNone;
import bma.common.langutil.ai.stack.AIStackSimple;
import bma.common.langutil.core.ExceptionUtil;
import bma.common.langutil.core.Function;
import bma.common.langutil.core.ObjectUtil;
import bma.common.netty.client.NettyClientBootstrap;
import bma.common.netty.pool.NettyChannelPool;
import bma.common.netty.pool.SimpleNettyChannelPoolFactory;
import bma.common.thrift.ThriftClientConfig;
import bma.common.thrift.ai.AIThriftClient;
import bma.common.thrift.ai.AIThriftClientFactory;
import bma.common.thrift.sample.Hello4AI;
import bma.siteone.netty.thrift.client.AIThriftClientFactoryNetty;
import bma.siteone.netty.thrift.client.AIThriftClientFactoryNettyPool;

/**
 * ThriftEntry测试用例
 * 
 * @author 关中
 * 
 */
public class NettyThriftClientFactoryTest {

	protected String entry1() {
		return "socket://127.0.0.1:9093/?_module=hello&_frameSize=10m";
	}

	protected String entry2() {
		return "http://localhost:8080/common.test/thrift/thrift_service.php?_frameSize=10m";
	}

	protected String entry3() {
		// gate
		return "socket://127.0.0.1:9080/?_module=hello&_frameSize=10m";
	}

	@Test
	public void simple() throws Exception {
		NettyClientBootstrap b = new NettyClientBootstrap();
		b.init();

		AIThriftClientFactory fac;
		AIThriftClientFactoryNetty o = new AIThriftClientFactoryNetty();
		o.setBootstrap(b);
		o.setTraceBufferSize(1024);
		fac = o;

		AIStackSimple<AIThriftClient> stack = new AIStackSimple<AIThriftClient>(
				null);
		fac.createThriftClient(stack, ThriftClientConfig.fromEntry(entry1()));

		AIThriftClient cl = stack.get();
		Hello4AI.Client hello = cl.createAIObject(Hello4AI.Client.class);

		hello.say(new AIStackNone<Boolean>(), "world");
		AIStackSimple<String> st1 = new AIStackSimple<String>(null);
		hello.name(st1, "Mr.");
		System.out.println(st1.get());
		// hello.error("error");

		ObjectUtil.waitFor(this, 1 * 1000);
		cl.close();
	}

	@Test
	public void pool() throws Exception {
		NettyClientBootstrap b = new NettyClientBootstrap();
		b.init();

		SimpleNettyChannelPoolFactory pfac = new SimpleNettyChannelPoolFactory();
		pfac.setBootstrap(b);

		NettyChannelPool pool = new NettyChannelPool();
		pool.setFactory(pfac);

		final AIThriftClientFactoryNettyPool fac = new AIThriftClientFactoryNettyPool();
		fac.setPool(pool);

		Function<Boolean, Boolean> f = new Function<Boolean, Boolean>() {

			@Override
			public Boolean apply(Boolean input) {
				try {
					AIStackSimple<AIThriftClient> stack = new AIStackSimple<AIThriftClient>(
							null);
					fac.createThriftClient(stack,
							ThriftClientConfig.fromEntry(entry1()));

					AIThriftClient cl = stack.get();
					Hello4AI.Client hello = cl
							.createAIObject(Hello4AI.Client.class);

					hello.say(new AIStackNone<Boolean>(), "world");
					AIStackSimple<String> st1 = new AIStackSimple<String>(null);
					hello.name(st1, "Mr.");
					System.out.println(st1.get());
					// hello.error("error");

					ObjectUtil.waitFor(this, 1 * 1000);
					cl.close();
					return true;
				} catch (Exception e) {
					throw ExceptionUtil.throwRuntime(e);
				}
			}
		};

		f.apply(true);
		f.apply(true);
		
		pool.close();
	}

	@Test
	public void http() throws Exception {
		NettyClientBootstrap b = new NettyClientBootstrap();
		b.init();

		AIThriftClientFactory fac;
		AIThriftClientFactoryNetty o = new AIThriftClientFactoryNetty();
		o.setBootstrap(b);
		o.setTraceBufferSize(1024);
		fac = o;

		AIStackSimple<AIThriftClient> stack = new AIStackSimple<AIThriftClient>(
				null);
		fac.createThriftClient(stack, ThriftClientConfig.fromEntry(entry2()));

		AIThriftClient cl = stack.get();

		Hello4AI.Client hello = cl.createAIObject(Hello4AI.Client.class);
		AIStackSimple<String> st = new AIStackSimple<String>(null);
		hello.name(st, "Mr. AI");
		System.out.println(st.get());
	}
}
