package bma.siteone.admin.po;

import java.util.List;

public class AdminSync {

	private AdminApp adminApp;
	
	private List<AdminRole> adminRoles;
	
	private List<AdminOp> adminOps;
	
	public static class RoleOp{
		
		private String roleName;
		
		private String opName;

		public String getRoleName() {
			return roleName;
		}

		public void setRoleName(String roleName) {
			this.roleName = roleName;
		}

		public String getOpName() {
			return opName;
		}

		public void setOpName(String opName) {
			this.opName = opName;
		}
		
	}
	
	private List<RoleOp> roleOps;
	
	private AdminUser manager;

	public AdminApp getAdminApp() {
		return adminApp;
	}

	public void setAdminApp(AdminApp adminApp) {
		this.adminApp = adminApp;
	}

	public List<AdminRole> getAdminRoles() {
		return adminRoles;
	}

	public void setAdminRoles(List<AdminRole> adminRoles) {
		this.adminRoles = adminRoles;
	}

	public List<AdminOp> getAdminOps() {
		return adminOps;
	}

	public void setAdminOps(List<AdminOp> adminOps) {
		this.adminOps = adminOps;
	}

	public List<RoleOp> getRoleOps() {
		return roleOps;
	}

	public void setRoleOps(List<RoleOp> roleOps) {
		this.roleOps = roleOps;
	}

	public AdminUser getManager() {
		return manager;
	}

	public void setManager(AdminUser manager) {
		this.manager = manager;
	}
	
}
