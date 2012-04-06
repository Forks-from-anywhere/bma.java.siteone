package bma.siteone.admin.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

import bma.common.langutil.core.PagerResult;
import bma.siteone.admin.po.AdminApp;
import bma.siteone.admin.po.AdminOp;
import bma.siteone.admin.po.AdminOpLog;
import bma.siteone.admin.po.AdminRole;
import bma.siteone.admin.po.AdminRoleOp;
import bma.siteone.admin.po.AdminSync.RoleOp;
import bma.siteone.admin.po.AdminUser;

/**
 * 后台服务接口
 * @author liaozhuojie
 *
 */

public interface AdminService {

	public boolean createUser(UserForm userForm) throws UnsupportedEncodingException;
	
	public boolean deleteUser(String userName);
	
	public boolean changePassword(String userName, String oldPassword, String newPassword) throws UnsupportedEncodingException;
	
	public boolean resetPassword(String userName, String newPassword) throws UnsupportedEncodingException;
	
	public boolean checkUserPassword(String userName, String password) throws UnsupportedEncodingException;
	
	public boolean syncApp(String syncContent) throws JsonParseException, JsonMappingException, IOException;
	
	public List<AdminRole> queryRoles(String userName);
	
	public boolean addUserAuth(String userName, String appName, String roleName);
	
	public boolean deleteUserAuth(String userName, String appName, String roleName);
	
	public boolean checkUserAuth(String userName, String appName, String opName);
	
	public boolean addOpLog(OpLogForm opLogForm);
	
	public PagerResult<AdminOpLog> queryOpLogs(OpLogQueryForm opLogQueryForm);
	

	public boolean createApp(AdminApp adminApp);

	public boolean deleteApp(String appName);

	public boolean createRole(AdminRole adminRole);

	public boolean createOp(AdminOp adminOp);

	public boolean createRoleOp(String appName, String roleName, String opName);

	public List<AdminRoleOp> queryRoleOps(String roleName, String appName);

	
	
	

}
