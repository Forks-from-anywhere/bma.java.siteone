package bma.siteone.admin;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import bma.common.jdbctemplate.JdbcTemplateUtil;
import bma.common.langutil.testcase.SpringTestcaseUtil;
import bma.siteone.admin.thrift.TAdminManagerService;
import bma.siteone.admin.thrift.TOp;
import bma.siteone.admin.thrift.TOpLogQueryForm;
import bma.siteone.admin.thrift.TOpLogRessult;
import bma.siteone.admin.thrift.TRole;
import bma.siteone.admin.thrift.TSetRoleOpsForm;
import bma.siteone.admin.thrift.TUser;
import bma.siteone.admin.thrift.TUserForm;

/**
 * 管理后台服务handler层AdminServiceThrift测试用例
 * @author liaozhuojie
 *
 */
public class AdminManagerThriftTest {

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
		TAdminManagerService.Iface s = context.getBean("mng_handler", TAdminManagerService.Iface.class);
		
		TUserForm userForm = new TUserForm();
		userForm.setUserName("liaozj5");
		userForm.setPassword("111111");
		userForm.setUserDescription("desc1111");
		
//		System.out.println(s.createUser(userForm));
		assertTrue(s.createUser(userForm));
	}
	
	@Test
	public void testDeleteUser() throws Exception {
		TAdminManagerService.Iface s = context.getBean("mng_handler", TAdminManagerService.Iface.class);
		
		String userName = "liaozj4";
		assertTrue(s.deleteUser(userName));
	}
	
	@Test
	public void testChangePassword() throws Exception {
		TAdminManagerService.Iface s = context.getBean("mng_handler", TAdminManagerService.Iface.class);
		
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
		TAdminManagerService.Iface s = context.getBean("mng_handler", TAdminManagerService.Iface.class);
		
		String userName = "liaozj2";
		String newPassword = "222222";
		
		assertTrue(s.resetPassword(userName,newPassword));
	}
	
	@Test
	public void testCheckUserPassword() throws Exception {
		TAdminManagerService.Iface s = context.getBean("mng_handler", TAdminManagerService.Iface.class);
		
		//1.密码正确
		String userName = "liaozj2";
		String password = "222222";
		//2.密码不正确
//		String userName = "liaozj2";
//		String password = "qwqwqw";
		
		assertTrue(s.checkUserPassword(userName,password));
	}
	
	

	
	@Test
	public void testQueryRoles() throws Exception {
		TAdminManagerService.Iface s = context.getBean("mng_handler", TAdminManagerService.Iface.class);
		
		String userName = "admin";
		
		List<TRole> roleslist = s.queryRoles(userName);
		System.out.println(roleslist);
		
	}
	
	@Test
	public void testAddUserAuth() throws Exception {
		TAdminManagerService.Iface s = context.getBean("mng_handler", TAdminManagerService.Iface.class);
		
		String userName = "liaozj3";
		String appName = "app1";
		String roleName = "liaozj1";
		
		assertTrue(s.addUserAuth(userName,appName,roleName));
	}
	
	@Test
	public void testDeleteUserAuth() throws Exception {
		TAdminManagerService.Iface s = context.getBean("mng_handler", TAdminManagerService.Iface.class);
		
		String userName = "liaozj3";
		String appName = "app3";
		String roleName = "role3";
		
		assertTrue(s.deleteUserAuth(userName,appName,roleName));
	}
	
	
	@Test
	public void testQueryOpLogs() throws Exception {
		TAdminManagerService.Iface s = context.getBean("mng_handler", TAdminManagerService.Iface.class);
		
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
	
	@Test
	public void testQueryAppRoles() throws Exception {
		TAdminManagerService.Iface s = context.getBean("mng_handler", TAdminManagerService.Iface.class);
		
		String appName = "mms_admin";
		List<String> rolesList = s.queryAppRoles(appName);
		System.out.println(rolesList);
	}
	
	@Test
	public void testCheckUserExist() throws Exception {
		TAdminManagerService.Iface s = context.getBean("mng_handler", TAdminManagerService.Iface.class);
		
		String userName = "admin";
		assertTrue(s.checkUserExist(userName));
//		String userName = "xxx";
//		assertFalse(s.checkUserExist(userName));
		
	}
	
	@Test
	public void testGetUser() throws Exception {
		TAdminManagerService.Iface s = context.getBean("mng_handler", TAdminManagerService.Iface.class);
		
		String userName = "admin";
		TUser user = s.getUser(userName);
		System.out.println(user);
	}
	
/*
	@Test
	public void testQueryAllUsers() throws Exception {
		TAdminManagerService.Iface s = context.getBean("mng_handler", TAdminManagerService.Iface.class);
		
		TAllUsersResult r = s.queryAllUsers(2,2);
		System.out.println(r);
	}
*/
	
	@Test
	public void testQueryAllUser() throws Exception {
		TAdminManagerService.Iface s = context.getBean("mng_handler", TAdminManagerService.Iface.class);
		
		List<TUser> r = s.queryAllUser();
		System.out.println(r);
	}
	
	@Test
	public void testQueryAppOps() throws Exception {
		TAdminManagerService.Iface s = context.getBean("mng_handler", TAdminManagerService.Iface.class);
		
		List<TOp> r = s.queryAppOps("app1");
		System.out.println(r);
	}
	
	@Test
	public void testQueryAppRolesByOps() throws Exception {
		TAdminManagerService.Iface s = context.getBean("mng_handler", TAdminManagerService.Iface.class);
		
		List<String> opNameList = new ArrayList<String>();
		opNameList.add("appserver");
		opNameList.add("deleteUser");
		Map<String, List<String>> r = s.queryAppRolesByOps("duowan_admin",opNameList);
		System.out.println(r);
	}
	
	@Test
	public void testQueryAppOpsByRole() throws Exception {
		TAdminManagerService.Iface s = context.getBean("mng_handler", TAdminManagerService.Iface.class);
		
		List<String> r = s.queryAppOpsByRole("duowan_admin","xxtt");
		System.out.println(r);
	}
	
	@Test
	public void testCreateRole() throws Exception {
		TAdminManagerService.Iface s = context.getBean("mng_handler", TAdminManagerService.Iface.class);
		
		TRole role = new TRole();
		role.setAppName("duowan_admin");
		role.setRoleName("xxtt");
		role.setRoleDescription("xxttyyyy");
		boolean r = s.createRole(role);
		System.out.println(r);
	}
	
	@Test
	public void testDeleteRole() throws Exception {
		TAdminManagerService.Iface s = context.getBean("mng_handler", TAdminManagerService.Iface.class);
		
		boolean r = s.deleteRole("duowan_admin","xxtt");
		System.out.println(r);
	}
	
	@Test
	public void testResetRoleOps() throws Exception {
		TAdminManagerService.Iface s = context.getBean("mng_handler", TAdminManagerService.Iface.class);
		
		TSetRoleOpsForm roleOpsForm = new TSetRoleOpsForm();
		roleOpsForm.setRoleName("xxtt");
		List<String> opNameList = new ArrayList<String>();
		opNameList.add("qqqqqq111");
//		opNameList.add("wwwww222");
		roleOpsForm.setOpNameList(opNameList);
		
		boolean r = s.resetRoleOps("duowan_admin",roleOpsForm);
		System.out.println(r);
	}
	
}
