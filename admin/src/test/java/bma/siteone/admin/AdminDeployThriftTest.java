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
import bma.siteone.admin.thrift.TAdminDeployService;
import bma.siteone.admin.thrift.TAdminManagerService;
import bma.siteone.admin.thrift.TOpLogQueryForm;
import bma.siteone.admin.thrift.TOpLogRessult;
import bma.siteone.admin.thrift.TRole;
import bma.siteone.admin.thrift.TSync;
import bma.siteone.admin.thrift.TUser;
import bma.siteone.admin.thrift.TUserForm;
import bma.siteone.admin.po.AdminApp;
import bma.siteone.admin.po.AdminOp;
import bma.siteone.admin.po.AdminRole;
import bma.siteone.admin.po.AdminSync;
import bma.siteone.admin.po.AdminSync.RoleOp;
import bma.siteone.admin.po.AdminUser;

/**
 * 管理后台服务handler层AdminServiceThrift测试用例
 * @author liaozhuojie
 *
 */
public class AdminDeployThriftTest {

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
	public void testInitAppAuth() throws Exception {
		TAdminDeployService.Iface s = context.getBean("deploy_handler", TAdminDeployService.Iface.class);
		
		List<TSync> syncInit = new ArrayList<TSync>();
		
		TSync app = new TSync();
		app.setOpCode(0);
		app.setContent("app`app desc");
		syncInit.add(app);
		
		TSync op1 = new TSync();
		op1.setOpCode(1);
		op1.setContent("op1`op1 desc");
		syncInit.add(op1);
		
		TSync op2 = new TSync();
		op2.setOpCode(1);
		op2.setContent("op2`op2 desc");
		syncInit.add(op2);
		
		TSync role1 = new TSync();
		role1.setOpCode(2);
		role1.setContent("role1`role1 desc");
		syncInit.add(role1);
		
		TSync role2 = new TSync();
		role2.setOpCode(2);
		role2.setContent("role2`role2 desc");
		syncInit.add(role2);
		
		TSync user1 = new TSync();
		user1.setOpCode(3);
		user1.setContent("user1`111111``user1 desc");
		syncInit.add(user1);
		
		TSync user2 = new TSync();
		user2.setOpCode(3);
		user2.setContent("user2``123456`");
		syncInit.add(user2);
		
		TSync role_op1 = new TSync();
		role_op1.setOpCode(4);
		role_op1.setContent("role1`op1,op2");
		syncInit.add(role_op1);
		
		TSync role_op2 = new TSync();
		role_op2.setOpCode(4);
		role_op2.setContent("role2`op2");
		syncInit.add(role_op2);
		
		TSync user_role1 = new TSync();
		user_role1.setOpCode(5);
		user_role1.setContent("user1`role1,role2");
		syncInit.add(user_role1);
		
		TSync user_role2 = new TSync();
		user_role2.setOpCode(5);
		user_role2.setContent("user2`role2");
		syncInit.add(user_role2);
		
		System.out.println(s.initAppAuth(syncInit));
		
	}
	
	@Test
	public void testUpgradeAppAuth() throws Exception {
		TAdminDeployService.Iface s = context.getBean("deploy_handler", TAdminDeployService.Iface.class);
		
		List<TSync> syncUpgrade = new ArrayList<TSync>();
		
		TSync app = new TSync();
		app.setOpCode(0);
		app.setContent("app");
		syncUpgrade.add(app);
		
		TSync op1 = new TSync();
		op1.setOpCode(1);
		op1.setContent("op1`");
		syncUpgrade.add(op1);
		
		TSync op2 = new TSync();
		op2.setOpCode(1);
		op2.setContent("op2`op2 desc");
		syncUpgrade.add(op2);
		
		TSync op3 = new TSync();
		op3.setOpCode(1);
		op3.setContent("op3`op3 desc");
		syncUpgrade.add(op3);
		
//		TSync delOp1 = new TSync();
//		delOp1.setOpCode(-1);
//		delOp1.setContent("op1");
//		syncUpgrade.add(delOp1);
//		
//		TSync delOp2 = new TSync();
//		delOp2.setOpCode(-1);
//		delOp2.setContent("op3");
//		syncUpgrade.add(delOp2);
		
		TSync role1 = new TSync();
		role1.setOpCode(2);
		role1.setContent("role1`role1 desc");
		syncUpgrade.add(role1);
		
		TSync role2 = new TSync();
		role2.setOpCode(2);
		role2.setContent("role2`role2 desc");
		syncUpgrade.add(role2);
		
//		TSync delRole1 = new TSync();
//		delRole1.setOpCode(-2);
//		delRole1.setContent("role1");
//		syncUpgrade.add(delRole1);
		
		
		TSync role_op1 = new TSync();
		role_op1.setOpCode(4);
		role_op1.setContent("role1`op1,op2");
		syncUpgrade.add(role_op1);
		
		TSync role_op2 = new TSync();
		role_op2.setOpCode(4);
		role_op2.setContent("role2`op2");
		syncUpgrade.add(role_op2);
		
		TSync del_role_op1 = new TSync();
		del_role_op1.setOpCode(-4);
		del_role_op1.setContent("role1`op1");
		syncUpgrade.add(del_role_op1);
		
		TSync del_role_op2 = new TSync();
		del_role_op2.setOpCode(-4);
		del_role_op2.setContent("role2`op2");
		syncUpgrade.add(del_role_op2);
		
		TSync op_role1 = new TSync();
		op_role1.setOpCode(6);
		op_role1.setContent("op2`role1,role2");
		syncUpgrade.add(op_role1);
		
		TSync op_role2 = new TSync();
		op_role2.setOpCode(6);
		op_role2.setContent("op3`role1,role2");
		syncUpgrade.add(op_role2);
		
		TSync delete_op_role1 = new TSync();
		delete_op_role1.setOpCode(-6);
		delete_op_role1.setContent("op2`role1");
		syncUpgrade.add(delete_op_role1);
		
		TSync delete_op_role2 = new TSync();
		delete_op_role2.setOpCode(-6);
		delete_op_role2.setContent("op3`role1");
		syncUpgrade.add(delete_op_role2);

		
		System.out.println(s.upgradeAppAuth(syncUpgrade));
		
	}
	
	@Test
	public void testExportAppAuth() throws Exception {
		TAdminDeployService.Iface s = context.getBean("deploy_handler", TAdminDeployService.Iface.class);
		
		List<TSync> r = s.exportAppAuth("app");
		for (TSync tSync : r) {
			System.out.println(tSync);
		}
	}
	
	@Test
	public void testSyncApp() throws Exception {
		TAdminManagerService.Iface s = context.getBean("mng_handler", TAdminManagerService.Iface.class);
		
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
		
//		assertTrue(s.syncApp(syncContent));
	}
	
}
