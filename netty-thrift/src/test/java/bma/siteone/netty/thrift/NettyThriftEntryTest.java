package bma.siteone.netty.thrift;

import org.junit.Test;

import bma.common.langutil.core.ObjectUtil;
import bma.common.netty.client.NettyClientBootstrap;
import bma.common.thrift.ThriftClient;
import bma.common.thrift.sample.Hello;
import bma.siteone.netty.thrift.client.AIThriftEntryNetty;

/**
 * AIThriftEntryNetty测试用例
 * 
 * @author 关中
 * 
 */
public class NettyThriftEntryTest {

	protected String entry1() {
		return "socket://127.0.0.1:9091/?module=hello&frameSize=10m";
	}

	@Test
	public void netty() throws Exception {
		AIThriftEntryNetty te = new AIThriftEntryNetty();
		te.setBootstrap(new NettyClientBootstrap().init());
		
		ThriftClient cl = te.createClient(entry1());

		Hello.Client hello = new Hello.Client(cl.getProtocol());

		// hello.say("world");
		String w = hello.name("Mr.");
		System.out.println(w);
		// hello.error("error");

		ObjectUtil.waitFor(this, 1 * 1000);
		cl.close();
	}
}
