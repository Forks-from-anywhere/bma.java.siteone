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
import bma.common.langutil.core.PagerResult;
import bma.common.langutil.testcase.SpringTestcaseUtil;
import bma.siteone.admin.po.AdminApp;
import bma.siteone.admin.po.AdminOp;
import bma.siteone.admin.po.AdminOpLog;
import bma.siteone.admin.po.AdminRole;
import bma.siteone.admin.po.AdminSync;
import bma.siteone.admin.po.AdminUser;
import bma.siteone.admin.po.AdminSync.RoleOp;
import bma.siteone.admin.service.AdminService;
import bma.siteone.admin.service.OpLogForm;
import bma.siteone.admin.service.OpLogQueryForm;
import bma.siteone.admin.thrift.TAdminService;
import bma.siteone.admin.thrift.TOpLogForm;
import bma.siteone.admin.thrift.TOpLogQueryForm;
import bma.siteone.admin.thrift.TOpLogRessult;
import bma.siteone.admin.thrift.TRole;
import bma.siteone.admin.thrift.TUserForm;

public class AdminServiceThriftTest {

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
	public void testCreateUser() throws Exception {
		TAdminService.Iface s = context.getBean("handler", TAdminService.Iface.class);
		
		TUserForm userForm = new TUserForm();
		userForm.setUserName("liaozj4");
		userForm.setPassword("111111");
		userForm.setUserDescription("desc1111");
		
//		System.out.println(s.createUser(userForm));
		assertTrue(s.createUser(userForm));
	}
	
	@Test
	public void testDeleteUser() throws Exception {
		TAdminService.Iface s = context.getBean("handler", TAdminService.Iface.class);
		
		String userName = "liaozj4";
		assertTrue(s.deleteUser(userName));
	}
	
	@Test
	public void testChangePassword() throws Exception {
		TAdminService.Iface s = context.getBean("handler", TAdminService.Iface.class);
		
		//1.密码正确
//		String userName = "liaozj2";
//		String oldPassword = "111111";
//		String newPassword = "222222";
		//2.密码不正确
		String userName = "liaozj2";
		String oldPassword = "222222";
		String newPassword = "111111";
		assertTrue(s.changePassword(userName,oldPassword,newPassword));
	}
	
	@Test
	public void testResetPassword() throws Exception {
		TAdminService.Iface s = context.getBean("handler", TAdminService.Iface.class);
		
		String userName = "liaozj2";
		String newPassword = "222222";
		
		assertTrue(s.resetPassword(userName,newPassword));
	}
	
	@Test
	public void testCheckUserPassword() throws Exception {
		TAdminService.Iface s = context.getBean("handler", TAdminService.Iface.class);
		
		//1.密码正确
		String userName = "liaozj2";
		String password = "222222";
		//2.密码不正确
//		String userName = "liaozj2";
//		String password = "qwqwqw";
		
		assertTrue(s.checkUserPassword(userName,password));
	}
	
	
	@Test
	public void testSyncApp() throws Exception {
		TAdminService.Iface s = context.getBean("handler", TAdminService.Iface.class);
		
		AdminSync sync = new AdminSync();
		
		AdminApp adminApp = new AdminApp();
		adminApp.setAppName("app1");
		adminApp.setAppDescription("app desc 123123");
		sync.setAdminApp(adminApp);
		
		List<AdminRole> adminRoles = new ArrayList<AdminRole>();
		//role
		AdminRole adminRole = new AdminRole();
		adminRole.setAppName("app1");
		adminRole.setRoleName("liaozj1");
		adminRole.setRoleDescription("role desc 123123");
		//role2
		AdminRole adminRole2 = new AdminRole();
		adminRole2.setAppName("app1");
		adminRole2.setRoleName("liaozj2");
		adminRole2.setRoleDescription("role2 desc 123123");
		//role3
		AdminRole adminRole3 = new AdminRole();
		adminRole3.setAppName("app1");
		adminRole3.setRoleName("liaozj3");
		adminRole3.setRoleDescription("role3 desc 123123");
		
		adminRoles.add(adminRole);
		adminRoles.add(adminRole2);
		adminRoles.add(adminRole3);
		sync.setAdminRoles(adminRoles);
		
		List<AdminOp> adminOps = new ArrayList<AdminOp>();
		//op1
		AdminOp adminOp = new AdminOp();
		adminOp.setAppName("app1");
		adminOp.setOpName("op1");
		adminOp.setOpDescription("op1 desc 123123");
		//op2
		AdminOp adminOp2 = new AdminOp();
		adminOp2.setAppName("app1");
		adminOp2.setOpName("op2");
		adminOp2.setOpDescription("op2 desc 123123");
		//op3
		AdminOp adminOp3 = new AdminOp();
		adminOp3.setAppName("app1");
		adminOp3.setOpName("op3");
		adminOp3.setOpDescription("op3 desc 123123");
		
		adminOps.add(adminOp);
		adminOps.add(adminOp2);
		adminOps.add(adminOp3);
		sync.setAdminOps(adminOps);
		
		
		List<RoleOp> roleOps = new ArrayList<AdminSync.RoleOp>();
		//roleOp1
		RoleOp roleOp = new RoleOp();
		roleOp.setRoleName("liaozj1");
		roleOp.setOpName("op1");
		//roleOp2
		RoleOp roleOp2 = new RoleOp();
		roleOp2.setRoleName("liaozj1");
		roleOp2.setOpName("op2");		
		//roleOp3
		RoleOp roleOp3 = new RoleOp();
		roleOp3.setRoleName("liaozj2");
		roleOp3.setOpName("op3");
		//roleOp4
		RoleOp roleOp4 = new RoleOp();
		roleOp4.setRoleName("liaozj3");
		roleOp4.setOpName("op3");
		
		roleOps.add(roleOp);
		roleOps.add(roleOp2);
		roleOps.add(roleOp3);
		roleOps.add(roleOp4);
		sync.setRoleOps(roleOps);
		
		AdminUser liaozj1 = new AdminUser();
		liaozj1.setUserName("liaozj11");
		liaozj1.setPassword("111111");
		liaozj1.setUserDescription("liaozj desc 123123");
		
		sync.setManager(liaozj1);
		
		ObjectMapper mapper = new ObjectMapper();
		String syncContent = mapper.writeValueAsString(sync);
		System.out.println(syncContent);
		
		assertTrue(s.syncApp(syncContent));
	}
	
	@Test
	public void testQueryRoles() throws Exception {
		TAdminService.Iface s = context.getBean("handler", TAdminService.Iface.class);
		
		String userName = "liaozj1";
		
		List<TRole> roleslist = s.queryRoles(userName);
		System.out.println(roleslist);
		
	}
	
	@Test
	public void testAddUserAuth() throws Exception {
		TAdminService.Iface s = context.getBean("handler", TAdminService.Iface.class);
		
		String userName = "liaozj3";
		String appName = "app1";
		String roleName = "liaozj1";
		
		assertTrue(s.addUserAuth(userName,appName,roleName));
	}
	
	@Test
	public void testDeleteUserAuth() throws Exception {
		TAdminService.Iface s = context.getBean("handler", TAdminService.Iface.class);
		
		String userName = "liaozj3";
		String appName = "app3";
		String roleName = "role3";
		
		assertTrue(s.deleteUserAuth(userName,appName,roleName));
	}
	
	@Test
	public void testCheckUserAuth() throws Exception {
		TAdminService.Iface s = context.getBean("handler", TAdminService.Iface.class);
		
		String userName = "liaozj3";
		String appName = "app1";
		String opName = "op1";
		
		assertTrue(s.checkUserAuth(userName,appName,opName));
//		assertFalse(s.checkUserAuth(userName,appName,opName));
	}
	
	@Test
	public void testAddOpLog() throws Exception {
		TAdminService.Iface s = context.getBean("handler", TAdminService.Iface.class);
		
		TOpLogForm opLogForm = new TOpLogForm();
		opLogForm.setAppName("test11");
		opLogForm.setDescription("test desc !!");
		opLogForm.setOpName("test");
		opLogForm.setRoleName("test");
		opLogForm.setUserName("test");
		
		assertTrue(s.addOpLog(opLogForm));
//		assertFalse(s.checkUserAuth(userName,appName,opName));
	}
	
	
	@Test
	public void testQueryOpLogs() throws Exception {
		TAdminService.Iface s = context.getBean("handler", TAdminService.Iface.class);
		
		TOpLogQueryForm opLogQueryForm = new TOpLogQueryForm();
		opLogQueryForm.setAppName("app1");
//		opLogQueryForm.setDescription("log");
//		opLogQueryForm.setOpName("test");
//		opLogQueryForm.setRoleName("test");
//		opLogQueryForm.setUserName("test");
		
//		Date date = new Date();
//		opLogQueryForm.setStartTime(date.toLocaleString());
//		opLogQueryForm.setEndTime(date.toLocaleString());
		opLogQueryForm.setPage(1);
		opLogQueryForm.setPageSize(2);
		
		TOpLogRessult r = s.queryOpLogs(opLogQueryForm);
		System.out.println(r.getResult());
	}
	
	
}
