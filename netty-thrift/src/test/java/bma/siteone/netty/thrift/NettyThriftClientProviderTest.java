package bma.siteone.netty.thrift;

import org.junit.Test;

import bma.common.langutil.ai.stack.AIStackSimple;
import bma.common.langutil.core.ObjectUtil;
import bma.common.netty.client.NettyClientBootstrap;
import bma.common.thrift.ThriftClient;
import bma.common.thrift.sample.Hello;
import bma.siteone.netty.thrift.client.AIThriftClientProviderNetty;

/**
 * ThriftEntry测试用例
 * 
 * @author 关中
 * 
 */
public class NettyThriftClientProviderTest {

	protected String entry1() {
		return "socket://127.0.0.1:9093/?module=hello&frameSize=10m";
	}

	protected String entry2() {
		return "http://localhost:8080/common.test/thrift/thrift_service.php";
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
		te.createClient(stack, entry2());

		ThriftClient cl = stack.get();
		Hello.Client hello = new Hello.Client(cl.getProtocol());

		// hello.say("world");
		String w = hello.name("Mr. X");
		System.out.println(w);
		w = hello.name("Mr. W");
		System.out.println(w);
		// hello.error("error");

		ObjectUtil.waitFor(this, 1000 * 1000);
		cl.close();
	}
}
