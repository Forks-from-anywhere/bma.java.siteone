package bma.siteone.admin;

import java.util.ArrayList;
import java.util.List;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.codehaus.jackson.map.ObjectMapper;

import bma.siteone.admin.po.AdminApp;
import bma.siteone.admin.po.AdminOp;
import bma.siteone.admin.po.AdminRole;
import bma.siteone.admin.po.AdminSync;
import bma.siteone.admin.po.AdminUser;
import bma.siteone.admin.po.AdminSync.RoleOp;
import bma.siteone.admin.thrift.TAdminAppService;
import bma.siteone.admin.thrift.TAdminManagerService;
import bma.siteone.admin.thrift.TOpLogForm;
import bma.siteone.admin.thrift.TOpLogQueryForm;
import bma.siteone.admin.thrift.TOpLogRessult;
import bma.siteone.admin.thrift.TRole;
import bma.siteone.admin.thrift.TUserForm;

/**
 * admin管理后台服务测试client
 * 
 * @author liaozhuojie
 *
 */
public class AdminClient {
	
	  public static void main(String [] args) {

	    try {	
	      TTransport transport;
          transport = new TSocket("127.0.0.1", 9091);
          transport.open();
          transport = new TFramedTransport(transport,50*1024*1024);
	      TProtocol protocol = new  TBinaryProtocol(transport);
	      
	      TAdminManagerService.Client client = new TAdminManagerService.Client(protocol);
	      TAdminAppService.Client appClient = new TAdminAppService.Client(protocol);

	      AdminClient adminClient = new AdminClient();
	      try {
	    	  
//	    	  adminClient.createUser(client);
//	    	  adminClient.deleteUser(client);
//	    	  adminClient.changePassword(client);
//	          adminClient.resetPassword(client);
//	    	  adminClient.checkUserPassword(client);
//	    	  adminClient.queryRoles(client);
//	    	  adminClient.addUserAuth(client);
//	    	  adminClient.deleteUserAuth(client);
	    	  adminClient.checkUserAuth(appClient);
//	    	  adminClient.addOpLog(client);
//	    	  adminClient.queryOpLogs(client);
//	    	  adminClient.syncApp(client);
	    	  
		   } catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	      transport.close();
	    } catch (TException x) {
	      x.printStackTrace();
	    } 
	  }
	  
	  
		public void createUser(TAdminManagerService.Client client) throws Exception {
			
			TUserForm userForm = new TUserForm();
			userForm.setUserName("liaozj4");
			userForm.setPassword("111111");
			userForm.setUserDescription("desc1111");
			
			boolean r = client.createUser(userForm);
			if(r){
				System.out.println("createUser [ok]");
			}else{
				System.out.println("createUser [fail]");
			}
			
		}
		
		public void deleteUser(TAdminManagerService.Client client) throws Exception {
			
			String userName = "liaozj4";
			boolean r = client.deleteUser(userName);
			if(r){
				System.out.println("deleteUser [ok]");
			}else{
				System.out.println("deleteUser [fail]");
			}
		}
		
		public void changePassword(TAdminManagerService.Client client) throws Exception {
			
			//1.密码正确
//			String userName = "liaozj2";
//			String oldPassword = "111111";
//			String newPassword = "222222";
			//2.密码不正确
			String userName = "liaozj2";
			String oldPassword = "222222";
			String newPassword = "111111";
			boolean r = client.changePassword(userName,oldPassword,newPassword);
			if(r){
				System.out.println("changePassword [ok]");
			}else{
				System.out.println("changePassword [fail]");
			}
		}

		public void resetPassword(TAdminManagerService.Client client) throws Exception {
			
			String userName = "liaozj2";
			String newPassword = "111111";
			
			boolean r = client.resetPassword(userName,newPassword);
			if(r){
				System.out.println("resetPassword [ok]");
			}else{
				System.out.println("resetPassword [fail]");
			}
		}
		
		public void checkUserPassword(TAdminManagerService.Client client) throws Exception {
			
			//1.密码正确
			String userName = "liaozj2";
			String password = "111111";
			//2.密码不正确
//			String userName = "liaozj2";
//			String password = "qwqwqw";
			boolean r = client.checkUserPassword(userName,password);
			if(r){
				System.out.println("checkUserPassword [ok]");
			}else{
				System.out.println("checkUserPassword [fail]");
			}
		}
		
		
		public void queryRoles(TAdminManagerService.Client client) throws Exception {
			
			String userName = "liaozj3";
			
			List<TRole> roleslist = client.queryAppRoles(userName);
			
			System.out.println(roleslist);
			
		}
		
		public void addUserAuth(TAdminManagerService.Client client) throws Exception {
			
			String userName = "liaozj1";
			String appName = "app3";
			String roleName = "role4";
			
			boolean r = client.addUserAuth(userName,appName,roleName);
			if(r){
				System.out.println("addUserAuth [ok]");
			}else{
				System.out.println("addUserAuth [fail]");
			}
		}
		
		public void deleteUserAuth(TAdminManagerService.Client client) throws Exception {
			
			String userName = "liaozj1";
			String appName = "app3";
			String roleName = "role4";
			
			boolean r = client.deleteUserAuth(userName,appName,roleName);
			if(r){
				System.out.println("deleteUserAuth [ok]");
			}else{
				System.out.println("deleteUserAuth [fail]");
			}
		}
		
		
		public void checkUserAuth(TAdminAppService.Client client) throws Exception {
			System.out.println("xxxxxxxxxxxxxxx");
			String userName = "liaozj3";
			String appName = "app1";
			String opName = "op1";
			
			boolean r = client.checkUserAuth(userName,appName,opName);
			if(r){
				System.out.println("checkUserAuth [ok]");
			}else{
				System.out.println("checkUserAuth [fail]");
			}
		}
		
		
		public void addOpLog(TAdminAppService.Client client) throws Exception {
			
			TOpLogForm opLogForm = new TOpLogForm();
			opLogForm.setAppName("test11");
			opLogForm.setDescription("test desc !!");
			opLogForm.setOpName("test");
			opLogForm.setRoleName("test");
			opLogForm.setUserName("test");
			
			boolean r = client.addOpLog(opLogForm);
			if(r){
				System.out.println("addOpLog [ok]");
			}else{
				System.out.println("addOpLog [fail]");
			}
		}
		
		
		public void queryOpLogs(TAdminManagerService.Client client) throws Exception {
			
			TOpLogQueryForm opLogQueryForm = new TOpLogQueryForm();
//			opLogQueryForm.setAppName("app1");
//			opLogQueryForm.setDescription("log");
//			opLogQueryForm.setOpName("test");
//			opLogQueryForm.setRoleName("test");
//			opLogQueryForm.setUserName("test");
			
//			Date date = new Date();
//			opLogQueryForm.setStartTime("2012-04-06 13:53:11");
//			opLogQueryForm.setEndTime(date.toLocaleString());
//			opLogQueryForm.setPage(1);
//			opLogQueryForm.setPageSize(4);
			
			TOpLogRessult r = client.queryOpLogs(opLogQueryForm);
			System.out.println(r.getResult());
		}
		
		/*
		public void syncApp(TAdminManagerService.Client client) throws Exception {
			
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
			liaozj1.setUserName("liaozj12");
			liaozj1.setPassword("111111");
			liaozj1.setUserDescription("liaozj desc 123123");
			
			sync.setManager(liaozj1);
			
			ObjectMapper mapper = new ObjectMapper();
			String syncContent = mapper.writeValueAsString(sync);
			System.out.println(syncContent);
			
			boolean r = client.syncApp(syncContent);
			if(r){
				System.out.println("syncApp [ok]");
			}else{
				System.out.println("syncApp [fail]");
			}
		}
		*/
		
}
