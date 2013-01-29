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
		
//		s.initAppAuth(syncInit);
		
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
