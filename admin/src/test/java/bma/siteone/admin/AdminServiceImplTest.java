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
import bma.siteone.admin.po.AdminAuth;
import bma.siteone.admin.po.AdminOp;
import bma.siteone.admin.po.AdminOpLog;
import bma.siteone.admin.po.AdminRole;
import bma.siteone.admin.po.AdminRoleOp;
import bma.siteone.admin.po.AdminSync;
import bma.siteone.admin.po.AdminUser;
import bma.siteone.admin.po.AdminSync.RoleOp;
import bma.siteone.admin.service.AdminService;
import bma.siteone.admin.service.OpLogForm;
import bma.siteone.admin.service.OpLogQueryForm;
import bma.siteone.admin.service.UserForm;

/**
 * 管理后台服务service层AdminServiceImpl测试用例
 * @author liaozhuojie
 *
 */
public class AdminServiceImplTest {

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
		AdminService s = context.getBean("service", AdminService.class);
		
		UserForm userForm = new UserForm();
		userForm.setUserName("liaozj3");
		userForm.setPassword("111111");
		userForm.setUserDescription("desc !!");
		
//		System.out.println(s.createUser(userForm));
		assertTrue(s.createUser(userForm));
	}
	
	@Test
	public void testDeleteUser() throws Exception {
		AdminService s = context.getBean("service", AdminService.class);
		
		String userName = "liaozj1";
		assertTrue(s.deleteUser(userName));
	}
	
	@Test
	public void testChangePassword() throws Exception {
		AdminService s = context.getBean("service", AdminService.class);
		
		//1.密码正确
		String userName = "liaozj3";
		String oldPassword = "222222";
		String newPassword = "111111";
		//2.密码不正确
//		String userName = "liaozj3";
//		String oldPassword = "222222";
//		String newPassword = "111111";
		assertTrue(s.changePassword(userName,oldPassword,newPassword));
	}
	
	
	@Test
	public void testResetPassword() throws Exception {
		AdminService s = context.getBean("service", AdminService.class);
		
		String userName = "liaozj3";
		String newPassword = "222222";
		
		assertTrue(s.resetPassword(userName,newPassword));
	}
	
	@Test
	public void testCheckUserPassword() throws Exception {
		AdminService s = context.getBean("service", AdminService.class);
		
		//1.密码正确
//		String userName = "liaozj3";
//		String password = "222222";
		//2.密码不正确
		String userName = "liaozj3";
		String password = "qwqwqw";
		
		assertTrue(s.checkUserPassword(userName,password));
	}
	
	@Test
	public void testCreateApp() throws Exception {
		AdminService s = context.getBean("service", AdminService.class);
		
		AdminApp adminApp = new AdminApp();
		adminApp.setAppName("app3");
		adminApp.setAppDescription("desc 121233");
		
//		System.out.println(s.createUser(userForm));
		assertTrue(s.createApp(adminApp));
	}
	
	
	@Test
	public void testDeleteApp() throws Exception {
		AdminService s = context.getBean("service", AdminService.class);
		
		String appName = "mms_admin";
//		System.out.println(s.createUser(userForm));
		assertTrue(s.deleteApp(appName));
	}
	
	@Test
	public void testCreateRole() throws Exception {
		AdminService s = context.getBean("service", AdminService.class);
		
		AdminRole adminRole = new AdminRole();
		adminRole.setAppName("app3");
		adminRole.setRoleName("role4");
		adminRole.setRoleDescription("desc xxxx");
		
		assertTrue(s.createRole(adminRole));
	}
	
	@Test
	public void testAddUserAuth() throws Exception {
		AdminService s = context.getBean("service", AdminService.class);
		
		String userName = "liaozj3";
		String appName = "app3";
		String roleName = "role10";
		
		assertTrue(s.addUserAuth(userName,appName,roleName));
	}
	
	@Test
	public void testDeleteUserAuth() throws Exception {
		AdminService s = context.getBean("service", AdminService.class);
		
		String userName = "liaozj3";
		String appName = "app3";
		String roleName = "role5";
		
		assertTrue(s.deleteUserAuth(userName,appName,roleName));
	}
	
	@Test
	public void testQueryRoles() throws Exception {
		AdminService s = context.getBean("service", AdminService.class);
		
		String userName = "liaozj3";
		
		List<AdminRole> roleslist = s.queryRoles(userName);
		System.out.println(roleslist);
		
	}

	@Test
	public void testCreateOp() throws Exception {
		AdminService s = context.getBean("service", AdminService.class);
		
		AdminOp adminOp = new AdminOp();
		adminOp.setAppName("app3");
		adminOp.setOpName("op9");
		adminOp.setOpDescription("desc opopop");
		
		assertTrue(s.createOp(adminOp));
	}
	
	@Test
	public void testCreateRoleOp() throws Exception {
		AdminService s = context.getBean("service", AdminService.class);
		
		String appName = "app3";
		String roleName = "role4";
		String opName = "op9";
		
		assertTrue(s.createRoleOp(appName,roleName,opName));
	}
	
	
	@Test
	public void testQueryRoleOps() throws Exception {
		AdminService s = context.getBean("service", AdminService.class);
		
		String appName = "app3";
		String roleName = "role3";
		
		List<AdminRoleOp> roleOpsList = s.queryRoleOps(roleName,appName);
		System.out.println(roleOpsList);
	}

	
	@Test
	public void testCheckUserAuth() throws Exception {
		AdminService s = context.getBean("service", AdminService.class);
		
		String userName = "liaozj3";
		String appName = "app3";
		String opName = "op3";
		
		assertTrue(s.checkUserAuth(userName,appName,opName));
//		assertFalse(s.checkUserAuth(userName,appName,opName));
	}
	
	@Test
	public void testAddOpLog() throws Exception {
		AdminService s = context.getBean("service", AdminService.class);
		

		OpLogForm opLogForm = new OpLogForm();
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
		AdminService s = context.getBean("service", AdminService.class);
		
		OpLogQueryForm opLogQueryForm = new OpLogQueryForm();
//		opLogQueryForm.setAppName("test11");
//		opLogQueryForm.setDescription("log");
//		opLogQueryForm.setOpName("test");
//		opLogQueryForm.setRoleName("test");
//		opLogQueryForm.setUserName("test");
		
//		Date date = new Date();
//		opLogQueryForm.setStartTime(date.toLocaleString());
//		opLogQueryForm.setEndTime(date.toLocaleString());
		opLogQueryForm.setPage(1);
		opLogQueryForm.setPageSize(2);
		
		PagerResult<AdminOpLog> r = s.queryOpLogs(opLogQueryForm);
		System.out.println(r.getResult());
	}
	
	@Test
	public void testSyncApp() throws Exception {
		AdminService s = context.getBean("service", AdminService.class);

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
		AdminRole adminRole3 = new AdminRole();
		adminRole3.setAppName("mms_admin");
		adminRole3.setRoleName("default2");
		adminRole3.setRoleDescription("default desc");
		
		adminRoles.add(adminRole);
		adminRoles.add(adminRole2);
		adminRoles.add(adminRole3);
		sync.setAdminRoles(adminRoles);
		
		List<AdminOp> adminOps = new ArrayList<AdminOp>();
		//op1
		AdminOp adminOp = new AdminOp();
		adminOp.setAppName("mms_admin");
		adminOp.setOpName("changePassword");
		adminOp.setOpDescription("changePassword desc");
		adminOps.add(adminOp);
		
		//op_createUser
		AdminOp adminOp_createUser = new AdminOp();
		adminOp_createUser.setAppName("mms_admin");
		adminOp_createUser.setOpName("createUser");
		adminOp_createUser.setOpDescription("createUser desc");
		adminOps.add(adminOp_createUser);
		
		//op_listUser
		AdminOp op_listUser = new AdminOp();
		op_listUser.setAppName("mms_admin");
		op_listUser.setOpName("listUser");
		op_listUser.setOpDescription("op_listUser desc");
		adminOps.add(op_listUser);
		
		sync.setAdminOps(adminOps);
		
		
		List<RoleOp> roleOps = new ArrayList<AdminSync.RoleOp>();
		//roleOp1
		RoleOp roleOp = new RoleOp();
		roleOp.setRoleName("admin");
		roleOp.setOpName("changePassword");
		roleOps.add(roleOp);
		
		//roleOp_createUser
		RoleOp roleOp_createUser = new RoleOp();
		roleOp_createUser.setRoleName("admin");
		roleOp_createUser.setOpName("createUser");
		roleOps.add(roleOp_createUser);
		
		//roleOp_listUser
		RoleOp roleOp_listUser = new RoleOp();
		roleOp_listUser.setRoleName("admin");
		roleOp_listUser.setOpName("listUser");
		roleOps.add(roleOp_listUser);
		
		//roleOp2
		RoleOp roleOp2 = new RoleOp();
		roleOp2.setRoleName("default");
		roleOp2.setOpName("changePassword");		
		roleOps.add(roleOp2);
		
		
		sync.setRoleOps(roleOps);
		
		AdminUser admin = new AdminUser();
		admin.setUserName("admin");
		admin.setPassword("admin");
		admin.setUserDescription("admin desc");
		
		sync.setManager(admin);
		
		List<AdminAuth> adminAuths = new ArrayList<AdminAuth>();
		//auth1
		AdminAuth auth1 = new AdminAuth();
		auth1.setAppName("mms_admin");
		auth1.setUserName("admin");
		auth1.setRoleName("admin");
		
		adminAuths.add(auth1);
		sync.setAdminAuths(adminAuths);
		
		ObjectMapper mapper = new ObjectMapper();
		String syncContent = mapper.writeValueAsString(sync);
		System.out.println(syncContent);
		
		assertTrue(s.syncApp(syncContent));
//		assertFalse(s.checkUserAuth(userName,appName,opName));
	}

/*	
	@Test
	public void testQueryAppUsers() throws Exception {
		AdminService s = context.getBean("service", AdminService.class);
		
		String appName = "mms_admin";
		PagerResult<String> authsList = s.queryAppUsers(appName,3,2);
		System.out.println(authsList);
	}
*/
	
	@Test
	public void testQueryAppRoles() throws Exception {
		AdminService s = context.getBean("service", AdminService.class);
		
		String appName = "mms_admin";
		List<String> rolesList = s.queryAppRoles(appName);
		System.out.println(rolesList);
	}
	
	@Test
	public void testCheckUserExist() throws Exception {
		AdminService s = context.getBean("service", AdminService.class);
		
//		String userName = "admin";
//		assertTrue(s.checkUserExist(userName));
		
		String userName = "xxx";
		assertFalse(s.checkUserExist(userName));
	}

/*
	@Test
	public void testQueryAllUsers() throws Exception {
		AdminService s = context.getBean("service", AdminService.class);
		
		PagerResult<String> authsList = s.queryAllUsers(2,2);
		System.out.println(authsList);
	}
*/
	
	@Test
	public void testQueryAllUser() throws Exception {
		AdminService s = context.getBean("service", AdminService.class);
		
		List<AdminUser> usersList = s.queryAllUser();
		System.out.println(usersList);
	}

}
