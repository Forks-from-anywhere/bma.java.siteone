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
import bma.siteone.admin.po.AdminApp;
import bma.siteone.admin.po.AdminOp;
import bma.siteone.admin.po.AdminRole;
import bma.siteone.admin.po.AdminSync;
import bma.siteone.admin.po.AdminUser;
import bma.siteone.admin.po.AdminSync.RoleOp;
import bma.siteone.admin.thrift.TAdminService;
import bma.siteone.admin.thrift.TAllUsersResult;
import bma.siteone.admin.thrift.TAppUsersResult;
import bma.siteone.admin.thrift.TOpLogForm;
import bma.siteone.admin.thrift.TOpLogQueryForm;
import bma.siteone.admin.thrift.TOpLogRessult;
import bma.siteone.admin.thrift.TRole;
import bma.siteone.admin.thrift.TUser;
import bma.siteone.admin.thrift.TUserForm;

/**
 * 管理后台服务handler层AdminServiceThrift测试用例
 * @author liaozhuojie
 *
 */
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
		userForm.setUserName("liaozj5");
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
		adminApp.setAppName("mms_admin");
		adminApp.setAppDescription("mms desc");
		sync.setAdminApp(adminApp);
		
		List<AdminRole> adminRoles = new ArrayList<AdminRole>();
		//role
		AdminRole adminRole = new AdminRole();
		adminRole.setAppName("mms_admin");
		adminRole.setRoleName("admin");
		adminRole.setRoleDescription("admin desc");
		//role2
		AdminRole adminRole2 = new AdminRole();
		adminRole2.setAppName("mms_admin");
		adminRole2.setRoleName("default");
		adminRole2.setRoleDescription("default desc");
		
		adminRoles.add(adminRole);
		adminRoles.add(adminRole2);
		sync.setAdminRoles(adminRoles);
		
		List<AdminOp> adminOps = new ArrayList<AdminOp>();
		//op1
		AdminOp adminOp = new AdminOp();
		adminOp.setAppName("mms_admin");
		adminOp.setOpName("changePassword");
		adminOp.setOpDescription("changePassword desc");
		
		adminOps.add(adminOp);
		sync.setAdminOps(adminOps);
		
		
		List<RoleOp> roleOps = new ArrayList<AdminSync.RoleOp>();
		//roleOp1
		RoleOp roleOp = new RoleOp();
		roleOp.setRoleName("admin");
		roleOp.setOpName("changePassword");
		//roleOp2
		RoleOp roleOp2 = new RoleOp();
		roleOp2.setRoleName("default");
		roleOp2.setOpName("changePassword");		
		
		roleOps.add(roleOp);
		roleOps.add(roleOp2);
		sync.setRoleOps(roleOps);
		
		AdminUser admin = new AdminUser();
		admin.setUserName("admin");
		admin.setPassword("admin");
		admin.setUserDescription("admin desc");
		
		sync.setManager(admin);
		
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

/*	
	@Test
	public void testQueryAppUsers() throws Exception {
		TAdminService.Iface s = context.getBean("handler", TAdminService.Iface.class);
		
		String appName = "mms_admin";
		TAppUsersResult r = s.queryAppUsers(appName,2,3);
		System.out.println(r);
	}
*/
	
	@Test
	public void testQueryAppRoles() throws Exception {
		TAdminService.Iface s = context.getBean("handler", TAdminService.Iface.class);
		
		String appName = "mms_admin";
		List<String> rolesList = s.queryAppRoles(appName);
		System.out.println(rolesList);
	}
	
	@Test
	public void testCheckUserExist() throws Exception {
		TAdminService.Iface s = context.getBean("handler", TAdminService.Iface.class);
		
		String userName = "admin";
		assertTrue(s.checkUserExist(userName));
//		String userName = "xxx";
//		assertFalse(s.checkUserExist(userName));
		
	}
	
	@Test
	public void testGetUser() throws Exception {
		TAdminService.Iface s = context.getBean("handler", TAdminService.Iface.class);
		
		String userName = "admin";
		TUser user = s.getUser(userName);
		System.out.println(user);
	}
	
/*
	@Test
	public void testQueryAllUsers() throws Exception {
		TAdminService.Iface s = context.getBean("handler", TAdminService.Iface.class);
		
		TAllUsersResult r = s.queryAllUsers(2,2);
		System.out.println(r);
	}
*/
	
	@Test
	public void testQueryAllUser() throws Exception {
		TAdminService.Iface s = context.getBean("handler", TAdminService.Iface.class);
		
		List<TUser> r = s.queryAllUser();
		System.out.println(r);
	}
	
}
