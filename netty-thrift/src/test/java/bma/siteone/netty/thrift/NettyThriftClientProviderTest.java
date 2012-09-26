package bma.siteone.netty.thrift;

import org.junit.Test;

import bma.common.langutil.core.ObjectUtil;
import bma.common.thrift.ThriftClient;
import bma.common.thrift.provider.ThriftClientProviderSimple;
import bma.common.thrift.sample.Hello;

/**
 * ThriftEntry测试用例
 * 
 * @author 关中
 * 
 */
public class NettyThriftClientProviderTest {

	protected String entry1() {
		return "socket://127.0.0.1:9091/?module=hello&frameSize=10m";
	}

	protected String entry2() {
		return "http://localhost:8080/common.test/thrift/thrift_service.php";
	}

	@Test
	public void simple() throws Exception {
		ThriftClientProviderSimple te = new ThriftClientProviderSimple();
		ThriftClient cl = te.createClient(entry1());

		Hello.Client hello = new Hello.Client(cl.getProtocol());

		// hello.say("world");
		String w = hello.name("Mr.");
		System.out.println(w);
		// hello.error("error");

		ObjectUtil.waitFor(this, 1 * 1000);
		cl.close();
	}

	@Test
	public void http() throws Exception {
		ThriftClientProviderSimple te = new ThriftClientProviderSimple();
		ThriftClient cl = te.createClient(entry2());

		Hello.Client hello = new Hello.Client(cl.getProtocol());

		// hello.say("world");
		String w = hello.name("Mr.");
		System.out.println(w);
		// hello.error("error");

		ObjectUtil.waitFor(this, 1 * 1000);
		cl.close();
	}
}
