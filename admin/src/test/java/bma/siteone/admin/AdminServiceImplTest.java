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
		
		//创建应用
		AdminApp adminApp = new AdminApp();
		adminApp.setAppName("duowan_admin");
		adminApp.setAppDescription("duowan_admin desc");
		sync.setAdminApp(adminApp);
		
		//创建角色
		List<AdminRole> adminRoles = new ArrayList<AdminRole>();
		List<String> roles = new ArrayList<String>();
		roles.add("admin");
		roles.add("editor");
		for(String role : roles){
			AdminRole adminRole = new AdminRole();
			adminRole.setAppName("duowan_admin");
			adminRole.setRoleName(role);
			adminRole.setRoleDescription(role+" desc");
			adminRoles.add(adminRole);
		}
		sync.setAdminRoles(adminRoles);
		
		//创建操作
		List<AdminOp> adminOps = new ArrayList<AdminOp>();	
		List<String> ops = new ArrayList<String>();
		ops.add("changePassword");
		ops.add("listUser");
		ops.add("createUser");
		ops.add("deleteUser");
		ops.add("resetPassword");
		ops.add("editUser");
		ops.add("authUser");
		
//		ops.add("listGroup");
//		ops.add("editGroup");
//		ops.add("deleteGroup");
//		ops.add("createGroup");
//		ops.add("listProvider");
//		ops.add("deleteProvider");
//		ops.add("editProvider");
//		ops.add("createProvider");
//		ops.add("listProgram");
//		ops.add("deleteProgram");
//		ops.add("createProgram");
//		ops.add("editProgram");
//		ops.add("authProgram");
		
//		ops.add("listAd");
//		ops.add("listAdBefore");
//		ops.add("deleteAd");
//		ops.add("createAd");
//		ops.add("editAd");
		
		ops.add("listProfile");
		ops.add("createProfile");
		ops.add("deleteProfile");
		ops.add("refreshProfile");
		ops.add("showProfile");
		ops.add("editProfile");
		ops.add("deleteIdcard");
		ops.add("deleteAvatar");
		ops.add("sync");
		ops.add("authProfile");
		
		ops.add("listGame");
		ops.add("deleteGame");
		ops.add("editGame");
		ops.add("createGame");
		
		for(String op : ops){
			AdminOp adminOp = new AdminOp();
			adminOp.setAppName("duowan_admin");
			adminOp.setOpName(op);
			adminOp.setOpDescription(op+" desc");
			adminOps.add(adminOp);	
		}
		sync.setAdminOps(adminOps);
		
		//绑定角色的操作
		List<RoleOp> roleOps = new ArrayList<AdminSync.RoleOp>();
		List<String> ops_admin = new ArrayList<String>();
		ops_admin.add("changePassword");
		ops_admin.add("listUser");
		ops_admin.add("createUser");
		ops_admin.add("deleteUser");
		ops_admin.add("resetPassword");
		ops_admin.add("editUser");
		ops_admin.add("authUser");
//		ops_admin.add("listGroup");
//		ops_admin.add("editGroup");
//		ops_admin.add("deleteGroup");
//		ops_admin.add("createGroup");
//		ops_admin.add("listProvider");
//		ops_admin.add("deleteProvider");
//		ops_admin.add("editProvider");
//		ops_admin.add("createProvider");
//		ops_admin.add("listProgram");
//		ops_admin.add("deleteProgram");
//		ops_admin.add("createProgram");
//		ops_admin.add("editProgram");
//		ops_admin.add("authProgram");
		
//		ops_admin.add("listAd");
//		ops_admin.add("listAdBefore");
//		ops_admin.add("deleteAd");
//		ops_admin.add("createAd");
//		ops_admin.add("editAd");
		
		ops_admin.add("listProfile");
		ops_admin.add("createProfile");
		ops_admin.add("deleteProfile");
		ops_admin.add("refreshProfile");
		ops_admin.add("showProfile");
		
		ops_admin.add("editProfile");
		ops_admin.add("deleteIdcard");
		ops_admin.add("deleteAvatar");
		ops_admin.add("sync");
		ops_admin.add("authProfile");
		
		ops_admin.add("listGame");
		ops_admin.add("deleteGame");
		ops_admin.add("editGame");
		ops_admin.add("createGame");
		
		for(String op : ops_admin){
			RoleOp roleOp = new RoleOp();
			roleOp.setRoleName("admin");
			roleOp.setOpName(op);
			roleOps.add(roleOp);
		}
		

		List<String> ops_editor = new ArrayList<String>();
		ops_editor.add("changePassword");
		
		ops_editor.add("listProfile");
		ops_editor.add("createProfile");
		ops_editor.add("deleteProfile");
		ops_editor.add("refreshProfile");
		ops_editor.add("showProfile");
		
		ops_editor.add("editProfile");
		ops_editor.add("deleteIdcard");
		ops_editor.add("deleteAvatar");
		ops_editor.add("sync");
		
		ops_editor.add("listGame");
		ops_editor.add("deleteGame");
		ops_editor.add("editGame");
		ops_editor.add("createGame");
		
		
		for(String op : ops_editor){
			RoleOp roleOp = new RoleOp();
			roleOp.setRoleName("editor");
			roleOp.setOpName(op);
			roleOps.add(roleOp);
		}
		
		
		sync.setRoleOps(roleOps);
		
		//创建管理员
		AdminUser admin = new AdminUser();
		admin.setUserName("admin");
		admin.setPassword("admin");
		admin.setUserDescription("admin desc");
		
		sync.setManager(admin);
		
		//管理员授权
		List<AdminAuth> adminAuths = new ArrayList<AdminAuth>();
		//auth_admin
		AdminAuth auth_admin = new AdminAuth();
		auth_admin.setAppName("duowan_admin");
		auth_admin.setUserName("admin");
		auth_admin.setRoleName("admin");
		adminAuths.add(auth_admin);
		
		sync.setAdminAuths(adminAuths);
		
		ObjectMapper mapper = new ObjectMapper();
		String syncContent = mapper.writeValueAsString(sync);
		System.out.println(syncContent);
		
		assertTrue(s.syncApp(syncContent));
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
