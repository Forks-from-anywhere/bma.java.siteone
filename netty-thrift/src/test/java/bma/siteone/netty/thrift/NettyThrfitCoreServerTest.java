package bma.siteone.netty.thrift;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import bma.common.langutil.core.ObjectUtil;
import bma.common.langutil.testcase.SpringTestcaseUtil;
import bma.common.netty.NettyServer;
import bma.siteone.netty.thrift.core.NCHFramed;

/**
 * Thrift服务测试用例
 * 
 * @author 关中
 * 
 */
public class NettyThrfitCoreServerTest {

	FileSystemXmlApplicationContext context;

	@Before
	public void setUp() throws Exception {
		SpringTestcaseUtil.disableDebug();
		context = new SpringTestcaseUtil.ApplicationContextBuilder().resource(
				NettyThrfitCoreServerTest.class, "netty-thrift-core.xml")
				.build();
	}

	@After
	public void tearDown() throws Exception {
		if (context != null)
			context.close();
	}
	
	@Test
	public void testDecode() throws Exception {
		byte[] b = new byte[]{0,0,0x19,0x53};
		System.out.println("-2147418111 = "+Integer.toHexString(-2147418111));
		System.out.println("HERE ----- "+NCHFramed.decodeFrameSize(b));
	}

	/**
	 * 测试服务端启动
	 * 
	 * @throws Exception
	 */
	@Test
	public void testServer() throws Exception {
		NettyServer s1 = context.getBean("server", NettyServer.class);
		System.out.println(s1.toString());

		s1.start();

		ObjectUtil.waitFor(this, 30 * 60 * 1000);
	}

}
