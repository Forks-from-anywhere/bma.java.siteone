package bma.siteone.quartz;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import bma.common.httpclient.HttpClientUtil;
import bma.common.langutil.core.ObjectUtil;
import bma.common.langutil.log.LogbackUtil;
import bma.common.langutil.testcase.SpringTestcaseUtil;
import bma.common.thrift.ThriftServer;

/**
 * Thrift服务测试用例
 * 
 * @author 关中
 * 
 */
public class QuartzServerTest {

	FileSystemXmlApplicationContext context;

	@Before
	public void setUp() throws Exception {
		SpringTestcaseUtil.disableDebug();
		HttpClientUtil.disableDebug(true);
		LogbackUtil.setLevel("com.mchange.v2", "INFO");
		LogbackUtil.setLevel("org.quartz", "INFO");
		context = new SpringTestcaseUtil.ApplicationContextBuilder().resource(
				QuartzServerTest.class, "quartz.xml").build();
	}

	@After
	public void tearDown() throws Exception {
		if (context != null)
			context.close();
	}

	/**
	 * 测试服务端启动
	 * 
	 * @throws Exception
	 */
	@Test
	public void testServer() throws Exception {
		ThriftServer s1 = context.getBean("server", ThriftServer.class);
		System.out.println(s1.toString());

		s1.start();

		ObjectUtil.waitFor(this, 30 * 60 * 1000);
	}

}
