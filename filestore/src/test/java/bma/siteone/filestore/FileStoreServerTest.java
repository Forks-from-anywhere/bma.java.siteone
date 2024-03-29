package bma.siteone.filestore;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import bma.common.langutil.core.ObjectUtil;
import bma.common.langutil.testcase.SpringTestcaseUtil;
import bma.common.thrift.ThriftServer;

/**
 * Thrift服务测试用例
 * 
 * @author 关中
 * 
 */
public class FileStoreServerTest {

	FileSystemXmlApplicationContext context;

	@Before
	public void setUp() throws Exception {
		SpringTestcaseUtil.disableDebug();
		context = new SpringTestcaseUtil.ApplicationContextBuilder().resource(
				FileStoreServerTest.class, "filestore.xml").build();
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

		ObjectUtil.waitFor(this, 30 * 60 * 1000 * 100);
	}

}
