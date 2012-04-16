package bma.siteone.admin.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.thrift.TException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

import bma.common.langutil.core.PagerResult;
import bma.siteone.admin.po.AdminApp;
import bma.siteone.admin.po.AdminOp;
import bma.siteone.admin.po.AdminOpLog;
import bma.siteone.admin.po.AdminRole;
import bma.siteone.admin.po.AdminRoleOp;
import bma.siteone.admin.po.AdminUser;

/**
 * 后台服务接口
 * @author liaozhuojie
 *
 */

public interface AdminService {

	/**
	 * 创建用户
	 * @param userForm
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public boolean createUser(UserForm userForm) throws UnsupportedEncodingException;
	
	/**
	 * 删除用户
	 * @param userName
	 * @return
	 */
	public boolean deleteUser(String userName);
	
	/**
	 * 用户修改密码
	 * @param userName
	 * @param oldPassword
	 * @param newPassword
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public boolean changePassword(String userName, String oldPassword, String newPassword) throws UnsupportedEncodingException;
	
	/**
	 * 重置密码
	 * @param userName
	 * @param newPassword
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public boolean resetPassword(String userName, String newPassword) throws UnsupportedEncodingException;
	
	/**
	 * 校验用户密码
	 * @param userName
	 * @param password
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public boolean checkUserPassword(String userName, String password) throws UnsupportedEncodingException;
	
	/**
	 * 应用配置同步
	 * @param syncContent
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public boolean syncApp(String syncContent) throws JsonParseException, JsonMappingException, IOException;
	
	/**
	 * 查询用户的角色
	 * @param userName
	 * @return
	 */
	public List<AdminRole> queryRoles(String userName);
	
	/**
	 * 增加用户授权
	 * @param userName
	 * @param appName
	 * @param roleName
	 * @return
	 */
	public boolean addUserAuth(String userName, String appName, String roleName);
	
	/**
	 * 删除用户授权
	 * @param userName
	 * @param appName
	 * @param roleName
	 * @return
	 */
	public boolean deleteUserAuth(String userName, String appName, String roleName);
	
	/**
	 * 检查用户授权
	 * @param userName
	 * @param appName
	 * @param opName
	 * @return
	 */
	public boolean checkUserAuth(String userName, String appName, String opName);
	
	/**
	 * 增加操作记录
	 * @param opLogForm
	 * @return
	 */
	public boolean addOpLog(OpLogForm opLogForm);
	
	/**
	 * 查询操作记录
	 * @param opLogQueryForm
	 * @return
	 */
	public PagerResult<AdminOpLog> queryOpLogs(OpLogQueryForm opLogQueryForm);
	
	/**
	 * 创建应用
	 * @param adminApp
	 * @return
	 */
	public boolean createApp(AdminApp adminApp);

	/**
	 * 删除应用
	 * @param appName
	 * @return
	 */
	public boolean deleteApp(String appName);

	/**
	 * 创建应用的角色
	 * @param adminRole
	 * @return
	 */
	public boolean createRole(AdminRole adminRole);

	/**
	 * 创建应用的操作
	 * @param adminOp
	 * @return
	 */
	public boolean createOp(AdminOp adminOp);

	/**
	 * 创建角色对应的操作
	 * @param appName
	 * @param roleName
	 * @param opName
	 * @return
	 */
	public boolean createRoleOp(String appName, String roleName, String opName);

	/**
	 * 查询角色对应的操作
	 * @param roleName
	 * @param appName
	 * @return
	 */
	public List<AdminRoleOp> queryRoleOps(String roleName, String appName);

	/**
	 * 查询该应用的所有用户
	 * @param appName
	 * @return
	 */
//	public List<AdminAuth> queryAppUsers(String appName);

	/**
	 * 查询应用的所用角色
	 * @param appName
	 * @return
	 * @throws TException
	 */
	public List<String> queryAppRoles(String appName) throws TException;

	/**
	 * 检查用户是否已经存在
	 * @param userName
	 * @return
	 * @throws TException
	 */
	public boolean checkUserExist(String userName) throws TException;

	/**
	 * 获取用户
	 * @param userName
	 * @return
	 */
	public AdminUser getUser(String userName);

//	public PagerResult<String> queryAppUsers(String appName, int page, int pageSize);
//
//	public PagerResult<String> queryAllUsers(int page, int pageSize);

	public List<AdminUser> queryAllUser() throws TException;
	
}
