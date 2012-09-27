package bma.siteone.netty.thrift;

import org.junit.Test;

import bma.common.langutil.ai.stack.AIStackSimple;
import bma.common.langutil.core.ObjectUtil;
import bma.common.netty.client.NettyClientBootstrap;
import bma.common.thrift.ThriftClient;
import bma.common.thrift.sample.Hello;
import bma.common.thrift.sample.Hello4AI;
import bma.siteone.netty.thrift.client.AIThriftClientProviderNetty;

/**
 * ThriftEntry测试用例
 * 
 * @author 关中
 * 
 */
public class NettyThriftClientProviderTest {

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

		AIThriftClientProviderNetty te = new AIThriftClientProviderNetty();
		te.setBootstrap(b);
		AIStackSimple<ThriftClient> stack = new AIStackSimple<ThriftClient>(
				null);
		te.createClient(stack, entry1());

		ThriftClient cl = stack.get();
		Hello.Client hello = new Hello.Client(cl.getProtocol());

		hello.say("world");
		String w = hello.name("Mr.");
		System.out.println(w);
		// hello.error("error");

		ObjectUtil.waitFor(this, 1 * 1000);
		cl.close();
	}

	@Test
	public void http() throws Exception {
		NettyClientBootstrap b = new NettyClientBootstrap();
		b.init();

		AIThriftClientProviderNetty te = new AIThriftClientProviderNetty();
		te.setBootstrap(b);
		te.setTraceBufferSize(100);
		AIStackSimple<ThriftClient> stack = new AIStackSimple<ThriftClient>(
				null);
		// te.createClient(stack, entry2());
		te.createClient(stack, entry3());

		ThriftClient cl = stack.get();

		if (cl.toString() == null) {
			Hello4AI.Client hello = new Hello4AI.Client(cl.getProtocol());
			AIStackSimple<String> st = new AIStackSimple<String>(null);
			hello.name(st, "Mr. AI");
			System.out.println(st.get());
		} else {
			Hello.Client hello = new Hello.Client(cl.getProtocol());

			hello.say("world");
			String w = hello.name("Mr. X");
			System.out.println(w);
			try {
				hello.error("error");
			} catch(Exception e) {				
				e.printStackTrace();
				System.err.println("receive error!!!!");
			}

			ObjectUtil.waitFor(this, 1 * 1000);
			cl.close();
		}
	}
}
