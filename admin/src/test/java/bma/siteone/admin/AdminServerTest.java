package bma.siteone.admin;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import bma.common.jdbctemplate.JdbcTemplateUtil;
import bma.common.langutil.core.ObjectUtil;
import bma.common.langutil.testcase.SpringTestcaseUtil;
import bma.common.thrift.ThriftServer;
import bma.common.thrift.ThriftServer.ThreadPoolServer;

/**
 * admin管理后台服务端进程启动 
 * @author liaozhuojie
 *
 */
public class AdminServerTest {

	FileSystemXmlApplicationContext context;
	
	@Before
	public void setUp() throws Exception {
		SpringTestcaseUtil.disableDebug();
		JdbcTemplateUtil.disableDebug(true);
		context = new SpringTestcaseUtil.ApplicationContextBuilder().resource(
				AdminServerTest.class, "admin.xml").build();
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
//		ThreadPoolServer s1 = context.getBean("server", ThreadPoolServer.class);
		ThriftServer s1 = context.getBean("server", ThriftServer.class);
		System.out.println(s1.toString());

		s1.start();

		ObjectUtil.waitFor(this, 30 * 60 * 1000 * 100);
	}

}
