package bma.siteone.alarm;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import bma.common.jdbctemplate.JdbcTemplateUtil;
import bma.common.langutil.core.ObjectUtil;
import bma.common.langutil.testcase.SpringTestcaseUtil;
import bma.common.netty.thrift.core.NettyThriftServer;

public class AlarmServerTest {

	FileSystemXmlApplicationContext context;

	@Before
	public void setUp() throws Exception {
		SpringTestcaseUtil.disableDebug();
		JdbcTemplateUtil.disableDebug(true);
		context = new SpringTestcaseUtil.ApplicationContextBuilder().project(
				"src/test/resources/spring_server.xml").build();
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
		NettyThriftServer s1 = context.getBean("server",
				NettyThriftServer.class);
		System.out.println(s1.toString());

		s1.start();

		while (new File("d:\\lock.txt").exists()) {
			ObjectUtil.waitFor(this, 5000000);
			// System.out.println("sleep 5");
		}

		s1.close();

		System.out.println("### close ###");

	}

}
