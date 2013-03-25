package bma.siteone.admin;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import bma.common.jdbctemplate.JdbcTemplateUtil;
import bma.common.langutil.testcase.SpringTestcaseUtil;
import bma.siteone.admin.thrift.TAdminAppService;
import bma.siteone.admin.thrift.TOpLogForm;

/**
 * 管理后台服务handler层AdminServiceThrift测试用例
 * @author liaozhuojie
 *
 */
public class AdminAppThriftTest {

	FileSystemXmlApplicationContext context;
	
	@Before
	public void setUp() throws Exception {
		SpringTestcaseUtil.disableDebug();
		JdbcTemplateUtil.disableDebug(false);
		context = new SpringTestcaseUtil.ApplicationContextBuilder().resource(
				AdminServerTest.class, "admin.xml").build();
	}

	@After
	public void tearDown() throws Exception {
		if (context != null)
			context.close();
	}
	
	@Test
	public void testCheckUserAuth() throws Exception {
		TAdminAppService.Iface s = context.getBean("app_handler", TAdminAppService.Iface.class);
		
		String userName = "liaozj3";
		String appName = "app1";
		String opName = "op1";
		
		System.out.println(s.checkUserAuth(userName,appName,opName));
	}
	
	@Test
	public void testAddOpLog() throws Exception {
		TAdminAppService.Iface s = context.getBean("app_handler", TAdminAppService.Iface.class);
		
		TOpLogForm opLogForm = new TOpLogForm();
		opLogForm.setAppName("test11");
		opLogForm.setDescription("test desc !!");
		opLogForm.setOpName("test");
		opLogForm.setRoleName("test");
		opLogForm.setUserName("test");
		
		assertTrue(s.addOpLog(opLogForm));
	}
	
}
