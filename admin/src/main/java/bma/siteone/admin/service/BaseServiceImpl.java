package bma.siteone.admin.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.thrift.TException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import bma.common.jdbctemplate.JdbcTemplateHelper;
import bma.common.jdbctemplate.SQLFilter;
import bma.common.langutil.core.DateTimeUtil;
import bma.common.langutil.core.Pager;
import bma.common.langutil.core.PagerResult;
import bma.common.langutil.core.StringUtil;
import bma.common.langutil.core.ValueUtil;
import bma.common.langutil.jdbc.CommonFieldValues;
import bma.siteone.admin.po.AdminApp;
import bma.siteone.admin.po.AdminAuth;
import bma.siteone.admin.po.AdminOp;
import bma.siteone.admin.po.AdminOpLog;
import bma.siteone.admin.po.AdminRole;
import bma.siteone.admin.po.AdminRoleOp;
import bma.siteone.admin.po.AdminSync;
import bma.siteone.admin.po.AdminSync.RoleOp;
import bma.siteone.admin.po.AdminUser;
import bma.siteone.admin.service.OpLogForm;
import bma.siteone.admin.service.OpLogQueryForm;
import bma.siteone.admin.service.UserForm;
import bma.siteone.admin.thrift.TSync;

/**
 * 管理后台服务层
 * @author liaozhuojie
 *
 */

@Transactional
public class BaseServiceImpl{

	final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(BaseServiceImpl.class);
	
	private String adminUserTableName = "admin_user";
	private String adminAppTableName = "admin_app";
	private String adminAppOpTableName = "admin_app_op";
	private String adminAppRoleTableName = "admin_app_role";
	private String adminAppRoleOpTableName = "admin_app_role_op";
	private String adminAuthUserRoleTableName = "admin_auth_user_role";
	private String adminOpLogTableName = "admin_op_log";
	/**
	 * JdbcTemplate对象
	 */
	private transient JdbcTemplateHelper helper;
	private JdbcTemplate jdbcTemplate;
	

	public String getAdminUserTableName() {
		return adminUserTableName;
	}

	public void setAdminUserTableName(String adminUserTableName) {
		this.adminUserTableName = adminUserTableName;
	}

	public String getAdminAppTableName() {
		return adminAppTableName;
	}

	public void setAdminAppTableName(String adminAppTableName) {
		this.adminAppTableName = adminAppTableName;
	}

	public String getAdminAppOpTableName() {
		return adminAppOpTableName;
	}

	public void setAdminAppOpTableName(String adminAppOpTableName) {
		this.adminAppOpTableName = adminAppOpTableName;
	}

	public String getAdminAppRoleTableName() {
		return adminAppRoleTableName;
	}

	public void setAdminAppRoleTableName(String adminAppRoleTableName) {
		this.adminAppRoleTableName = adminAppRoleTableName;
	}

	public String getAdminAppRoleOpTableName() {
		return adminAppRoleOpTableName;
	}

	public void setAdminAppRoleOpTableName(String adminAppRoleOpTableName) {
		this.adminAppRoleOpTableName = adminAppRoleOpTableName;
	}

	public String getAdminAuthUserRoleTableName() {
		return adminAuthUserRoleTableName;
	}

	public void setAdminAuthUserRoleTableName(String adminAuthUserRoleTableName) {
		this.adminAuthUserRoleTableName = adminAuthUserRoleTableName;
	}

	public String getAdminOpLogTableName() {
		return adminOpLogTableName;
	}

	public void setAdminOpLogTableName(String adminOpLogTableName) {
		this.adminOpLogTableName = adminOpLogTableName;
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
		this.helper = new JdbcTemplateHelper(jdbcTemplate);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public boolean createUser(UserForm userForm) throws UnsupportedEncodingException{
		
		CommonFieldValues fvs = new CommonFieldValues();
		fvs.addString("user_name", userForm.getUserName());
		fvs.addString("password", StringUtil.md5(userForm.getPassword().getBytes("utf-8")));
		fvs.addString("user_description", userForm.getUserDescription());
		fvs.addSysdate("create_time");
		fvs.addInt("status",1);
		
		helper.executeInsert(adminUserTableName, fvs, null,new SQLFilter() {
			@Override
			public String filter(String sql) {
				return sql.replaceAll("^INSERT INTO", "REPLACE INTO");
			}
		});
		return true;
		
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public boolean createUserWithEncodePass(UserForm userForm) throws UnsupportedEncodingException{
		
		CommonFieldValues fvs = new CommonFieldValues();
		fvs.addString("user_name", userForm.getUserName());
		fvs.addString("password", userForm.getPassword());
		fvs.addString("user_description", userForm.getUserDescription());
		fvs.addSysdate("create_time");
		fvs.addInt("status",1);
		
		helper.executeInsert(adminUserTableName, fvs, null,new SQLFilter() {
			@Override
			public String filter(String sql) {
				return sql.replaceAll("^INSERT INTO", "REPLACE INTO");
			}
		});
		return true;
		
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public boolean deleteUser(String userName){
		AdminUser user = getUser(userName);
		if(user == null){
			if (log.isWarnEnabled()) {
				log.warn("deleteUser( user not exist : {} )",userName);
			}
			return false;
		}
		
		CommonFieldValues tj = new CommonFieldValues();
		tj.addString("user_name", userName);
		helper.executeDelete(adminUserTableName, tj);
		
		//删除用户对应的授权
		CommonFieldValues authtj = new CommonFieldValues();
		authtj.addString("user_name", userName);
		helper.executeDelete(adminAuthUserRoleTableName, authtj);
		
		return true;
	}
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
	public AdminUser getUser(String userName){
		
		AdminUser user = helper.selectOne(adminUserTableName, "user_name", userName,
				new AdminUserRowMapper());
		return user;
	}
	
	public class AdminUserRowMapper implements RowMapper<AdminUser> {
		
		public AdminUser mapRow(ResultSet rs, int index) throws SQLException {
			AdminUser user = new AdminUser();
			user.setUserName(rs.getString("user_name"));
			user.setPassword(rs.getString("password"));
			user.setUserDescription(rs.getString("user_description"));
			user.setCreateTime(new Date(rs.getTimestamp("create_time").getTime()));
			user.setStatus(rs.getInt("status"));
			
			return user;
		}
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public boolean changePassword(String userName, String oldPassword, String newPassword) throws UnsupportedEncodingException{
		
		//校验旧密码
		AdminUser user = getUser(userName);
		if(user == null){
			if (log.isWarnEnabled()) {
				log.warn("changePassword( user not exit : {})",userName);
			}
			return false;
		}
		if(!user.getPassword().equalsIgnoreCase(StringUtil.md5(oldPassword.getBytes("utf-8")))){
			if (log.isWarnEnabled()) {
				log.warn("changePassword( user oldpassword check fail : {})",userName);
			}
			return false;
		}
		
		//更新密码
		CommonFieldValues fvs = new CommonFieldValues();
		fvs.addString("password", StringUtil.md5(newPassword.getBytes("utf-8")));
		
		CommonFieldValues tj = new CommonFieldValues();
		tj.addString("user_name", userName);
		
		int c = helper.executeUpdate(adminUserTableName, fvs, tj);
		
		return c == 1;
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public boolean resetPassword(String userName, String newPassword) throws UnsupportedEncodingException{
		
		AdminUser user = getUser(userName);
		if(user == null){
			return false;
		}
		
		//更新密码
		CommonFieldValues fvs = new CommonFieldValues();
		fvs.addString("password", StringUtil.md5(newPassword.getBytes("utf-8")));
		
		CommonFieldValues tj = new CommonFieldValues();
		tj.addString("user_name", userName);
		
		int c = helper.executeUpdate(adminUserTableName, fvs, tj);
		
		return c == 1;
	}
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
	public boolean checkUserPassword(String userName, String password) throws UnsupportedEncodingException{
		
		AdminUser user = getUser(userName);
		if(user == null){
			if (log.isWarnEnabled()) {
				log.warn("checkUserPassword( user not exit : {})",userName);
			}
			return false;
		}
		
		if(!user.getPassword().equalsIgnoreCase(StringUtil.md5(password.getBytes("utf-8")))){
			return false;
		}
		
		return true;
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public boolean syncApp(String syncContent) throws JsonParseException, JsonMappingException, IOException{
		
		ObjectMapper mapper = new ObjectMapper();
		AdminSync sync = mapper.readValue(syncContent, AdminSync.class);
		String appName = sync.getAdminApp().getAppName();
		
		AdminApp app = getApp(appName);
		if(app != null){
			//删除应用
			if(!deleteApp(appName)){
				return false;
			}
		}
		
		//创建应用
		createApp(sync.getAdminApp());
		//创建应用的角色
		List<AdminRole> adminRoles = sync.getAdminRoles();
		for(AdminRole role : adminRoles){
			createRole(role);
		}
		//创建应用的操作
		List<AdminOp> adminOps = sync.getAdminOps();
		for(AdminOp op : adminOps){
			createOp(op);
		}
		//创建应用中的角色与操作的关联
		List<RoleOp> roleOps = sync.getRoleOps();
		for(RoleOp roleOp : roleOps){
			createRoleOp(appName,roleOp.getRoleName(),roleOp.getOpName());
		}
		//创建对应的管理员
		AdminUser manager = sync.getManager();
		AdminUser user = getUser(manager.getUserName());
		if(user == null){
			UserForm userForm = new UserForm();
			userForm.setUserName(manager.getUserName());
			userForm.setPassword(manager.getPassword());
			userForm.setUserDescription(manager.getUserDescription());
			createUser(userForm);
		}
		//创建管理员的授权
		List<AdminAuth> adminAuths = sync.getAdminAuths();
		for(AdminAuth auth : adminAuths){
			//先删除
			deleteUserAuth(auth.getUserName(), auth.getAppName(), auth.getRoleName());
			addUserAuth(auth.getUserName(), auth.getAppName(), auth.getRoleName());
		}
			
		return true;
		
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public boolean createApp(AdminApp adminApp){
		
		CommonFieldValues fvs = new CommonFieldValues();
		fvs.addString("app_name", adminApp.getAppName());
		fvs.addString("app_description", adminApp.getAppDescription());		
		fvs.addSysdate("create_time");
		fvs.addInt("status",1);
		
		helper.executeInsert(adminAppTableName, fvs, null,new SQLFilter() {
			@Override
			public String filter(String sql) {
				return sql.replaceAll("^INSERT INTO", "REPLACE INTO");
			}
		});
		return true;
		
	}
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
	public AdminApp getApp(String appName){
		
		AdminApp app = helper.selectOne(adminAppTableName, "app_name", appName,
				new AdminAppRowMapper());
		return app;
	}
	
	public class AdminAppRowMapper implements RowMapper<AdminApp> {
		
		public AdminApp mapRow(ResultSet rs, int index) throws SQLException {
			AdminApp app = new AdminApp();
			app.setAppName(rs.getString("app_name"));
			app.setAppDescription(rs.getString("app_description"));
			app.setCreateTime(new Date(rs.getTimestamp("create_time").getTime()));
			app.setStatus(rs.getInt("status"));
			
			return app;
		}
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public boolean deleteApp(String appName){
		AdminApp app = getApp(appName);
		if(app == null){
			if (log.isDebugEnabled()) {
				log.debug("deleteApp( app not exist : {})",appName);
			}
			return false;
		}
		
		CommonFieldValues tj = new CommonFieldValues();
		tj.addString("app_name", appName);
		//删除应用
		int cDeleteApp = helper.executeDelete(adminAppTableName, tj);
		if(cDeleteApp == 1){
			//删除应用的角色
			helper.executeDelete(adminAppRoleTableName, tj);
			//删除应用的操作
			helper.executeDelete(adminAppOpTableName, tj);
			//删除应用中的角色与操作的关联
			helper.executeDelete(adminAppRoleOpTableName, tj);
			
			return true;
		}else{
			return false;
		}
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public boolean createRole(AdminRole adminRole){
		
		CommonFieldValues fvs = new CommonFieldValues();
		fvs.addString("app_name", adminRole.getAppName());
		fvs.addString("role_name", adminRole.getRoleName());
		fvs.addString("role_description", adminRole.getRoleDescription());		
		fvs.addSysdate("create_time");
		fvs.addInt("status",1);
		
		helper.executeInsert(adminAppRoleTableName, fvs, null,new SQLFilter() {
			@Override
			public String filter(String sql) {
				return sql.replaceAll("^INSERT INTO", "REPLACE INTO");
			}
		});
		return true;
		
	}
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
	public AdminRole getRole(String roleName,String appName){
		
		StringBuilder sql = new StringBuilder();
		
		sql.append("SELECT * FROM ").append(adminAppRoleTableName).append(" WHERE ");
		
		if(!appName.isEmpty()){
			sql.append(" app_name='").append(appName).append("' and ");
		}
		
		sql.append(" role_name='").append(roleName).append("'");
		
		AdminRole role = null;
		try{
			role = jdbcTemplate.queryForObject(sql.toString(), new AdminRoleRowMapper());
		}catch(EmptyResultDataAccessException e){
			if (log.isWarnEnabled()) {
				log.warn("getRole( role not exist : {},{}), exception => EmptyResultDataAccessException({})",new Object[]{ roleName,appName,e.getMessage()});
			}
		}
		return role;
	}
	
	public class AdminRoleRowMapper implements RowMapper<AdminRole> {
		
		public AdminRole mapRow(ResultSet rs, int index) throws SQLException {
			AdminRole role = new AdminRole();
			role.setAppName(rs.getString("app_name"));
			role.setRoleName(rs.getString("role_name"));
			role.setRoleDescription(rs.getString("role_description"));
			role.setCreateTime(new Date(rs.getTimestamp("create_time").getTime()));
			role.setStatus(rs.getInt("status"));
			
			return role;
		}
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public boolean addUserAuth(String userName, String appName, String roleName){
		
		AdminUser user = getUser(userName);
		if(user == null){
			if (log.isWarnEnabled()) {
				log.warn("addUserAuth( user not exist : {})",user);
			}
			return false;
		}
		
		AdminApp app = getApp(appName);
		if(app == null){
			if (log.isWarnEnabled()) {
				log.warn("addUserAuth( app not exist : {})",appName);
			}
			return false;
		}
		
		AdminRole role = getRole(roleName,appName);
		if(role == null){
			if (log.isWarnEnabled()) {
				log.warn("addUserAuth( role not exist : {})",roleName);
			}
			return false;
		}
		
		CommonFieldValues fvs = new CommonFieldValues();
		fvs.addString("user_name", userName);
		fvs.addString("app_name", appName);
		fvs.addString("role_name", roleName);
		
		helper.executeInsert(adminAuthUserRoleTableName, fvs, userName,new SQLFilter() {
			@Override
			public String filter(String sql) {
				return sql.replaceAll("^INSERT INTO", "REPLACE INTO");
			}
		});
		return true;
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public boolean deleteUserAuth(String userName, String appName, String roleName){
		
		AdminUser user = getUser(userName);
		if(user == null){
			if (log.isWarnEnabled()) {
				log.warn("deleteUserAuth( user not exist : {})",user);
			}
			return false;
		}
		
		AdminApp app = getApp(appName);
		if(app == null){
			if (log.isWarnEnabled()) {
				log.warn("deleteUserAuth( app not exist : {})",appName);
			}
			return false;
		}
		
		AdminRole role = getRole(roleName,appName);
		if(role == null){
			if (log.isWarnEnabled()) {
				log.warn("deleteUserAuth( role not exist : {},{})",roleName,appName);
			}
			return false;
		}
		
		CommonFieldValues tj = new CommonFieldValues();
		tj.addString("user_name", userName);
		tj.addString("app_name", appName);
		tj.addString("role_name", roleName);
		
		int c = helper.executeDelete(adminAuthUserRoleTableName, tj);
		
		return c == 1;
	}
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
	public boolean checkUserAuth(String userName, String appName, String opName){
		
		AdminUser user = getUser(userName);
		if(user == null){
			if (log.isWarnEnabled()) {
				log.warn("checkUserAuth( user not exist : {})",user);
			}
			return false;
		}
		
		AdminApp app = getApp(appName);
		if(app == null){
			if (log.isWarnEnabled()) {
				log.warn("checkUserAuth( app not exist : {})",appName);
			}
			return false;
		}
		
		AdminOp op = getOp(opName,appName);
		if(op == null){
			if (log.isWarnEnabled()) {
				log.warn("checkUserAuth( op not exist : {},{})",opName,appName);
			}
			return false;
		}
		
		//查询用户的角色
		List<AdminRole> rolesList = queryUserAppRoles(userName, appName);
		for(AdminRole _role : rolesList){
			String _roleName = _role.getRoleName();
			//查询角色对应的操作
			List<AdminRoleOp> roleOpsList = queryRoleOps(_roleName, appName);
			for(AdminRoleOp _roleOp : roleOpsList ){
				if(_roleOp.getOpName().equals(opName)){
					return true;
				}
			}
		}
		
		return false;
	}
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
	public List<AdminRole> queryUserRoles(String userName){
		
		String sql = "SELECT * FROM "+adminAuthUserRoleTableName+" WHERE user_name='"+userName+"'";
		
		List<AdminAuth> authsList = jdbcTemplate.query(sql, new AdminAuthRowMapper());
		
		List<AdminRole> rolesList = new ArrayList<AdminRole>();
		for(AdminAuth auth : authsList){
			AdminRole role = getRole(auth.getRoleName(), auth.getAppName());
			if(role != null){
				rolesList.add(role);
			}
		}
		
		return rolesList;
	}
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
	public List<AdminRole> queryUserAppRoles(String userName, String appName){
		
		String sql = "SELECT * FROM "+adminAuthUserRoleTableName+" WHERE user_name='"+userName+"' and app_name='"+appName+"'";
		
		List<AdminAuth> authsList = jdbcTemplate.query(sql, new AdminAuthRowMapper());
		
		List<AdminRole> rolesList = new ArrayList<AdminRole>();
		for(AdminAuth auth : authsList){
			AdminRole role = getRole(auth.getRoleName(), auth.getAppName());
			if(role != null){
				rolesList.add(role);
			}
		}
		
		return rolesList;
	}
	
	public class AdminAuthRowMapper implements RowMapper<AdminAuth> {
		
		public AdminAuth mapRow(ResultSet rs, int index) throws SQLException {
			AdminAuth auth = new AdminAuth();
			auth.setUserName(rs.getString("user_name"));
			auth.setAppName(rs.getString("app_name"));
			auth.setRoleName(rs.getString("role_name"));
			
			return auth;
		}
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public boolean createOp(AdminOp adminOp){
		
		CommonFieldValues fvs = new CommonFieldValues();
		fvs.addString("app_name", adminOp.getAppName());
		fvs.addString("op_name", adminOp.getOpName());
		fvs.addString("op_description", adminOp.getOpDescription());		
		fvs.addSysdate("create_time");
		fvs.addInt("status",1);
		
		helper.executeInsert(adminAppOpTableName, fvs, null,new SQLFilter() {
			@Override
			public String filter(String sql) {
				return sql.replaceAll("^INSERT INTO", "REPLACE INTO");
			}
		});
		return true;
		
	}
	
	public boolean deleteOp(String appName, String opName){
		
		CommonFieldValues tj = new CommonFieldValues();
		tj.addString("app_name", appName);
		tj.addString("op_name", opName);
		//删除操作
		int cDeleteOp = helper.executeDelete(adminAppOpTableName, tj);
		if(cDeleteOp == 1){
			//删除角色与操作的关联
			helper.executeDelete(adminAppRoleOpTableName, tj);
			return true;
		}else{
			return false;
		}
		
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public boolean createRoleOp(String appName, String roleName, String opName){
		
		AdminApp app = getApp(appName);
		if(app == null){
			if (log.isWarnEnabled()) {
				log.warn("createRoleOp( app not exist : {})",appName);
			}
			return false;
		}
		
		AdminRole role = getRole(roleName, appName);
		if(role == null){
			if (log.isWarnEnabled()) {
				log.warn("createRoleOp( role not exist : {},{})",roleName,appName);
			}
			return false;
		}
		
		AdminOp op = getOp(opName, appName);
		if(op == null){
			if (log.isWarnEnabled()) {
				log.warn("createRoleOp( op not exist : {},{})",opName,appName);
			}
			return false;
		}
		
		CommonFieldValues fvs = new CommonFieldValues();
		fvs.addString("app_name", appName);
		fvs.addString("role_name", roleName);
		fvs.addString("op_name", opName);		
		
		helper.executeInsert(adminAppRoleOpTableName, fvs, null,new SQLFilter() {
			@Override
			public String filter(String sql) {
				return sql.replaceAll("^INSERT INTO", "REPLACE INTO");
			}
		});
		return true;
		
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public boolean deleteRoleOp(String appName, String roleName, String opName){
		
		CommonFieldValues tj = new CommonFieldValues();
		tj.addString("app_name", appName);
		tj.addString("role_name", roleName);
		tj.addString("op_name", opName);		
		
		//删除角色与操作的关联
		helper.executeDelete(adminAppRoleOpTableName, tj);
		
		return true;
	}
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
	public AdminOp getOp(String opName,String appName){
		
		StringBuilder sql = new StringBuilder();
		
		sql.append("SELECT * FROM ").append(adminAppOpTableName).append(" WHERE ");
		
		if(!appName.isEmpty()){
			sql.append(" app_name='").append(appName).append("' and ");
		}
		
		sql.append(" op_name='").append(opName).append("'");
		
		AdminOp op = null;
		try{
			op = jdbcTemplate.queryForObject(sql.toString(), new AdminOpRowMapper());
		}catch(EmptyResultDataAccessException e){
			if (log.isWarnEnabled()) {
				log.warn("getOp( op not exist : {},{}), exception => EmptyResultDataAccessException({})",new Object[]{ opName,appName,e.getMessage()});
			}
		}
		
		return op;
	}
	
	public class AdminOpRowMapper implements RowMapper<AdminOp> {
		
		public AdminOp mapRow(ResultSet rs, int index) throws SQLException {
			AdminOp op = new AdminOp();
			op.setAppName(rs.getString("app_name"));
			op.setOpName(rs.getString("op_name"));
			op.setOpDescription(rs.getString("op_description"));
			op.setCreateTime(new Date(rs.getTimestamp("create_time").getTime()));
			op.setStatus(rs.getInt("status"));
			
			return op;
		}
	}
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
	public List<AdminRoleOp> queryRoleOps(String roleName,String appName){
		
		StringBuilder sql = new StringBuilder();
		
		sql.append("SELECT * FROM ").append(adminAppRoleOpTableName).append(" WHERE ");
		
		if(!appName.isEmpty()){
			sql.append(" app_name='").append(appName).append("' and ");
		}
		
		sql.append(" role_name='").append(roleName).append("'");
		
		List<AdminRoleOp> roleOpsList = jdbcTemplate.query(sql.toString(), new AdminRoleOpRowMapper());

		return roleOpsList;
	}
	
	public class AdminRoleOpRowMapper implements RowMapper<AdminRoleOp> {
		
		public AdminRoleOp mapRow(ResultSet rs, int index) throws SQLException {
			AdminRoleOp roleOp = new AdminRoleOp();
			roleOp.setAppName(rs.getString("app_name"));
			roleOp.setRoleName(rs.getString("role_name"));
			roleOp.setOpName(rs.getString("op_name"));
			
			return roleOp;
		}
	}
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
	public List<AdminOp> queryAppOps(String appName){
		
		String sql = "SELECT * FROM "+adminAppOpTableName+" WHERE app_name='"+appName+"'";
		
		List<AdminOp> opsList = jdbcTemplate.query(sql, new AdminOpRowMapper());

		return opsList;
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public boolean addOpLog(OpLogForm opLogForm){
		
		CommonFieldValues fvs = new CommonFieldValues();
		
		fvs.addString("user_name", opLogForm.getUserName());
		fvs.addString("app_name", opLogForm.getAppName());
		fvs.addString("role_name", opLogForm.getRoleName());
		fvs.addString("op_name", opLogForm.getOpName());
		fvs.addString("description", opLogForm.getDescription());
		fvs.addSysdate("time");
		
		helper.executeInsert(adminOpLogTableName, fvs, "id");
		return true;
	}
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
	public PagerResult<AdminOpLog> queryOpLogs(OpLogQueryForm opLogQueryForm){
		
		CommonFieldValues tj = new CommonFieldValues();

		if (ValueUtil.notEmpty(opLogQueryForm.getUserName())) {
			tj.addString("user_name", opLogQueryForm.getUserName());
		}
		if (ValueUtil.notEmpty(opLogQueryForm.getAppName())) {
			tj.addString("app_name", opLogQueryForm.getAppName());
		}
		if (ValueUtil.notEmpty(opLogQueryForm.getRoleName())) {
			tj.addString("role_name", opLogQueryForm.getRoleName());
		}
		if (ValueUtil.notEmpty(opLogQueryForm.getOpName())) {
			tj.addString("op_name", opLogQueryForm.getOpName());
		}
		if (ValueUtil.notEmpty(opLogQueryForm.getDescription())) {
			tj.addLikeString("description", opLogQueryForm.getDescription());
		}
		if (ValueUtil.notEmpty(opLogQueryForm.getStartTime())) {
			tj.addTimestamp("time", DateTimeUtil.parseDateTime(opLogQueryForm.getStartTime(), null),">=");
		}
		if (ValueUtil.notEmpty(opLogQueryForm.getEndTime())) {
			tj.addTimestamp("time", DateTimeUtil.parseDateTime(opLogQueryForm.getEndTime(), null),"<=");
		}
		
		int total = helper.selectCount(adminOpLogTableName, tj);
		
		Pager pager = new Pager(total, opLogQueryForm.getPage(), opLogQueryForm.getPageSize());
		List<AdminOpLog> list = helper.selectLimit(adminOpLogTableName, tj,
				opLogQueryForm.orderStr("time", "DESC"), pager.getStart(),
				pager.getPageSize(), new AdminOpLogRowMapper());
		
		PagerResult<AdminOpLog> result = new PagerResult<AdminOpLog>();
		result.setPager(pager);
		result.setResult(list);
		
		return result;
	}
	
	public class AdminOpLogRowMapper implements RowMapper<AdminOpLog> {
		
		public AdminOpLog mapRow(ResultSet rs, int index) throws SQLException {
			AdminOpLog opLog = new AdminOpLog();
			opLog.setId(rs.getInt("id"));
			opLog.setUserName(rs.getString("user_name"));
			opLog.setAppName(rs.getString("app_name"));
			opLog.setRoleName(rs.getString("role_name"));
			opLog.setOpName(rs.getString("op_name"));
			opLog.setTime(new Date(rs.getTimestamp("time").getTime()));
			opLog.setDescription(rs.getString("description"));
			
			return opLog;
		}
	}

	
	/**
	 * 查询当前应用授权的用户
	 */
/*	
	@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
	@Override
	public PagerResult<String> queryAppUsers(String appName, int page, int pageSize){
		
		String sql = "SELECT COUNT( DISTINCT user_name) FROM "+adminAuthUserRoleTableName+" WHERE app_name=?";
		int total = jdbcTemplate.queryForInt(sql, new Object[]{appName});
		
		Pager pager = new Pager(total, page, pageSize);
		
		String sql2 = "SELECT  DISTINCT user_name FROM "+adminAuthUserRoleTableName+" WHERE app_name=? LIMIT ?,?";
		List<String> usernames = jdbcTemplate.queryForList(sql2, new Object[]{appName,pager.getStart(),pager.getPageSize()}, new int[]{java.sql.Types.VARCHAR,java.sql.Types.INTEGER,java.sql.Types.INTEGER}, String.class);

		PagerResult<String> result = new PagerResult<String>();
		result.setPager(pager);
		result.setResult(usernames);
		
		return result;
		

	
		
		String sql = "SELECT * FROM "+adminAuthUserRoleTableName+" WHERE app_name=?";
		
//		List<String> usernames = jdbcTemplate.queryForList(sql, new Object[]{appName}, new int[]{java.sql.Types.VARCHAR}, String.class);
		List<AdminAuth> authsList = jdbcTemplate.query(sql, new Object[]{appName}, new AdminAuthRowMapper());
		
		List<AdminAuth> auths = new ArrayList<AdminAuth>();
		//只保留有效的
		for(AdminAuth auth : authsList){
			if(getUser(auth.getUserName()) != null ){	//用户存在
				auths.add(auth);
			}
		}
		return auths;
		
		
	}
*/

	@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
	public List<String> queryAppRoleNames(String appName) throws TException {
		
		String sql = "SELECT role_name FROM "+adminAppRoleTableName+" WHERE app_name=?";
		
		List<String> rolesList = jdbcTemplate.queryForList(sql, new Object[]{appName}, new int[]{java.sql.Types.VARCHAR}, String.class);
		
		return rolesList;
	}
	
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
	public List<AdminRole> queryAppRoles(String appName) throws TException {
		
		String sql = "SELECT * FROM "+adminAppRoleTableName+" WHERE app_name='"+appName+"'";
		
		List<AdminRole> rolesList = jdbcTemplate.query(sql, new AdminRoleRowMapper());

		return rolesList;
		
	}
	
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
	public boolean checkUserExist(String userName) throws TException {
		AdminUser user = getUser(userName);
		if(user == null){
			return false;
		}
		return true;
	}
	
	/**
	 * 查询所有的用户
	 */
/*	
	@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
	public PagerResult<String> queryAllUsers(int page, int pageSize){
		
		String sql = "SELECT COUNT(1) FROM "+adminUserTableName;
		int total = jdbcTemplate.queryForInt(sql);
		
		Pager pager = new Pager(total, page, pageSize);
		
		String sql2 = "SELECT user_name FROM "+adminUserTableName+" LIMIT ?,?";
		List<String> usernames = jdbcTemplate.queryForList(sql2, new Object[]{pager.getStart(),pager.getPageSize()}, new int[]{java.sql.Types.INTEGER,java.sql.Types.INTEGER}, String.class);

		PagerResult<String> result = new PagerResult<String>();
		result.setPager(pager);
		result.setResult(usernames);
		
		return result;
		
	}
*/
	
	public List<AdminUser> queryAllUser() throws TException {
		
		String sql = "SELECT * FROM "+adminUserTableName;
		List<AdminUser> usersList = jdbcTemplate.query(sql, new AdminUserRowMapper());

		return usersList;
	}
	
	public Map<String, List<String>> queryAppRolesByOps(String appName, List<String> opNames){
		
		List<String> opNamesIns = new ArrayList<String>(); 
		for (String op : opNames) {
			opNamesIns.add("'"+op+"'");
		}
		
		String sql = "SELECT * FROM "+adminAppRoleOpTableName+" WHERE app_name='"+appName+"' and op_name in ("+(StringUtil.join(opNamesIns, ","))+")";
		
		List<AdminRoleOp> roleOpsList = jdbcTemplate.query(sql, new AdminRoleOpRowMapper());
		
		Map<String, List<String>> opRolesMap = new HashMap<String, List<String>>();
		
		if(roleOpsList.size() != 0){
			for (AdminRoleOp adminRoleOp : roleOpsList) {
				if(!opRolesMap.containsKey(adminRoleOp.getOpName())){
					List<String> opRolesList = new ArrayList<String>();
					opRolesList.add(adminRoleOp.getRoleName());
					
					opRolesMap.put(adminRoleOp.getOpName(), opRolesList);
				}else{
					opRolesMap.get(adminRoleOp.getOpName()).add(adminRoleOp.getRoleName());
				}
			}
		}
		
		return opRolesMap;
	}
	
	public List<String> queryAppOpsByRole(String appName, String roleName){
	
		String sql = "SELECT op_name FROM "+adminAppRoleOpTableName+" WHERE app_name='"+appName+"' and role_name='"+roleName+"'";
		
		List<String> opList = jdbcTemplate.queryForList(sql, String.class);
		
		return opList;
	}
	
	public boolean deleteRole(String appName, String roleName){
		
		CommonFieldValues tj = new CommonFieldValues();
		tj.addString("app_name", appName);
		tj.addString("role_name", roleName);
		//删除角色
		int cDeleteRole = helper.executeDelete(adminAppRoleTableName, tj);
		if(cDeleteRole == 1){
			//删除角色与操作的关联
			helper.executeDelete(adminAppRoleOpTableName, tj);
			//删除角色与用户的关联
			helper.executeDelete(adminAuthUserRoleTableName, tj);
			
			return true;
		}else{
			return false;
		}
		
	}
	
	public boolean resetRoleOps(String appName,String roleName,List<String> opNameList){
		
		//先删除角色对应的所有操作
		CommonFieldValues tj = new CommonFieldValues();
		tj.addString("app_name", appName);
		tj.addString("role_name", roleName);
		
		helper.executeDelete(adminAppRoleOpTableName, tj);
		
		//重新设置
		for (String opName : opNameList) {
			CommonFieldValues fvs = new CommonFieldValues();
			fvs.addString("app_name", appName);
			fvs.addString("role_name", roleName);
			fvs.addString("op_name", opName);
			
			helper.executeInsert(adminAppRoleOpTableName, fvs, null,new SQLFilter() {
				@Override
				public String filter(String sql) {
					return sql.replaceAll("^INSERT INTO", "REPLACE INTO");
				}
			});
		}
		
		return true;
		
	}
	
	/**
	 * 初始化应用权限
	 * @param syncInitList
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public boolean initAppAuth(List<SyncContent> syncInitList) throws UnsupportedEncodingException{
		/*
		权限初始化数据格式：
		     0`应用`描述
		     1`操作1`描述
		     1`操作2`描述
		     1`操作3`描述
		     2`角色1`描述
		     2`角色2`描述
		     2`角色3`描述
		     3`用户1`原始密码`加密密码`描述
		     3`用户2`原始密码`加密密码`描述
		     4`角色1`操作1,操作2,操作3,操作4
		     4`角色2`操作5,操作6,操作7,操作8
		     4`角色3`操作9,操作10
		     5`用户1`角色1,角色2
		     5`用户2`角色3
		 */
		
		//按照code分组
		Map<Integer, List<String>> initMap = prepareSyncContent(syncInitList);
		
		if(initMap.get(0) == null || initMap.get(0).isEmpty()){
			//没有传递app
			log.warn("[initAppAuth] no app");
			return false;
		}
		if(initMap.get(0).size() >1){
			//app大于一个
			log.warn("[initAppAuth] app more than 1");
			return false;
		}
		
		//0`应用`描述
		List<String> appStrings = StringUtil.strSplit(initMap.get(0).get(0), "`");
		String appName = appStrings.get(0);
		String appDesc = appStrings.get(1);
		AdminApp app = getApp(appName);
		if(app != null){
			//删除应用
			if(!deleteApp(appName)){
				return false;
			}
		}

		//创建应用
		AdminApp initApp = new AdminApp();
		initApp.setAppName(appName);
		initApp.setAppDescription(appDesc);
		createApp(initApp);
		
		//创建应用的操作
	    //1`操作1`描述
	    //1`操作2`描述
	    //1`操作3`描述
		if(initMap.get(1) != null){
			for (String opAndDesc : initMap.get(1)) {
				List<String> opStrings = StringUtil.strSplit(opAndDesc, "`");
				String opName = opStrings.get(0);
				String opDesc = opStrings.get(1);
				
				AdminOp op = new AdminOp();
				op.setAppName(appName);
				op.setOpName(opName);
				op.setOpDescription(opDesc);
				
				createOp(op);
			}
		}
		
	
		//创建应用的角色
	    //2`角色1`描述
	    //2`角色2`描述
	    //2`角色3`描述
		if(initMap.get(2) != null){
			for (String roleAndDesc : initMap.get(2)) {
				List<String> roleStrings = StringUtil.strSplit(roleAndDesc, "`");
				String roleName = roleStrings.get(0);
				String roleDesc = roleStrings.get(1);
				
				AdminRole role = new AdminRole();
				role.setAppName(appName);
				role.setRoleName(roleName);
				role.setRoleDescription(roleDesc);
				
				createRole(role);
			}
		}
		
		//创建应用中的角色与操作的关联
	    //4`角色1`操作1,操作2,操作3,操作4
	    //4`角色2`操作5,操作6,操作7,操作8
	    //4`角色3`操作9,操作10
		if(initMap.get(4) != null){
			for (String roleAndOp : initMap.get(4)) {
				List<String> roleOpStrings = StringUtil.strSplit(roleAndOp, "`");
				String roleName = roleOpStrings.get(0);
				List<String> opNames = StringUtil.strSplit(roleOpStrings.get(1), ",");
				
				if(opNames.size() !=0){
					for (String opName : opNames) {
						createRoleOp(appName,roleName,opName);
					}
				}
			}
		}
		
		//创建用户
	    //3`用户1`原始密码`加密密码`描述
	    //3`用户2`原始密码`加密密码`描述
		if(initMap.get(3) != null){
			for (String userData : initMap.get(3)) {
				List<String> userStrings = StringUtil.strSplit(userData, "`");
				String userName = userStrings.get(0);
				String rawPass = userStrings.get(1);
				String encodePass = userStrings.get(2);
				String userDesc = userStrings.get(3);
				
				AdminUser user = getUser(userName);
				if(user == null){
					if(!rawPass.isEmpty()){
						UserForm userForm = new UserForm();
						userForm.setUserName(userName);
						userForm.setPassword(rawPass);
						userForm.setUserDescription(userDesc);
						createUser(userForm);
					}else if(!encodePass.isEmpty()){
						UserForm userForm = new UserForm();
						userForm.setUserName(userName);
						userForm.setPassword(encodePass);
						userForm.setUserDescription(userDesc);
						createUserWithEncodePass(userForm);
					}
				}
			}
		}

		//创建用户的授权
	    //5`用户1`角色1,角色2
	    //5`用户2`角色3
		if(initMap.get(5) != null){
			for (String userAndRole : initMap.get(5)) {
				List<String> userRoleStrings = StringUtil.strSplit(userAndRole, "`");
				String userName = userRoleStrings.get(0);
				List<String> roleNames = StringUtil.strSplit(userRoleStrings.get(1), ",");
				
				if(roleNames.size() !=0){
					for (String roleName : roleNames) {
						addUserAuth(userName, appName, roleName);
					}
				}
			}
		}
		
		return true;
		
	}
	
	/**
	 * 升级应用权限
	 * @param syncInitList
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public boolean upgradeAppAuth(List<SyncContent> syncUpgradeList) throws UnsupportedEncodingException{
		/*
		权限初始化数据格式：
		     0`应用
		     1`操作1`描述          （设置操作1）
		    -1`操作2               （删除操作2）
		     2`角色1`描述          （设置角色1）
		    -2`角色2               （删除角色1）
		     4`角色1`操作1,操作2   （角色1增加操作1，操作2）
		    -4`角色2`操作3,操作4    (角色2取消操作3，操作4)
		     6`操作1`角色1,角色2   （操作1增加角色1，角色2）
		    -6`操作1`角色3,角色4   （操作1取消角色3，角色4）
		 */
		
		//按照code分组
		Map<Integer, List<String>> upgradeMap = prepareSyncContent(syncUpgradeList);
		
		if(upgradeMap.get(0) == null || upgradeMap.get(0).isEmpty()){
			//没有传递app
			log.warn("[upgradeAppAuth] no app");
			return false;
		}
		if(upgradeMap.get(0).size() >1){
			//app大于一个
			log.warn("[upgradeAppAuth] app more than 1");
			return false;
		}
		
		//0`应用
		String appName = upgradeMap.get(0).get(0);
		AdminApp app = getApp(appName);
		if(app == null){
			//没有该app
			log.warn("[upgradeAppAuth] app not found");
			return false;
		}
		
		//创建应用的操作
	    //1`操作1`描述          （设置操作1）
		if(upgradeMap.get(1) != null){
			for (String opAndDesc : upgradeMap.get(1)) {
				List<String> opStrings = StringUtil.strSplit(opAndDesc, "`");
				String opName = opStrings.get(0);
				String opDesc = opStrings.get(1);
				
				AdminOp op = new AdminOp();
				op.setAppName(appName);
				op.setOpName(opName);
				op.setOpDescription(opDesc);
				
				createOp(op);
			}
		}
		
		//删除应用的操作
		//-1`操作2               （删除操作2）
		if(upgradeMap.get(-1) != null){
			for (String delOpName : upgradeMap.get(-1)) {
				//查询操作是否存在，不存在则跳过
				AdminOp op = getOp(delOpName, appName);
				if(op == null){
					log.warn("[upgradeAppAuth] delete op => op not exist : {},{})",delOpName,appName);
				}
				//删除操作
				//删除角色-操作关系
				deleteOp(appName,delOpName);
			}
		}
		
		
		//创建应用的角色
	    //2`角色1`描述          （设置角色1）
		if(upgradeMap.get(2) != null){
			for (String roleAndDesc : upgradeMap.get(2)) {
				List<String> roleStrings = StringUtil.strSplit(roleAndDesc, "`");
				String roleName = roleStrings.get(0);
				String roleDesc = roleStrings.get(1);
				
				AdminRole role = new AdminRole();
				role.setAppName(appName);
				role.setRoleName(roleName);
				role.setRoleDescription(roleDesc);
				
				createRole(role);
			}
		}
		
		//删除应用的角色
		//-2`角色2               （删除角色1）
		if(upgradeMap.get(-2) != null){
			for (String delRoleName : upgradeMap.get(-2)) {
				//查询角色是否存在，不存在则跳过
				AdminRole role = getRole(delRoleName, appName);
				if(role == null){
					log.warn("[upgradeAppAuth] delete role => role not exist : {},{})",delRoleName,appName);
					System.out.println("delete role => role not exist");
				}
				//删除角色
				deleteRole(appName,delRoleName);
			}
		}
		
		//创建应用中的角色与操作的关联
	    //4`角色1`操作1,操作2   （角色1增加操作1，操作2）
		if(upgradeMap.get(4) != null){
			for (String roleAndOp : upgradeMap.get(4)) {
				List<String> roleOpStrings = StringUtil.strSplit(roleAndOp, "`");
				String roleName = roleOpStrings.get(0);
				List<String> opNames = StringUtil.strSplit(roleOpStrings.get(1), ",");
				
				if(opNames.size() !=0){
					for (String opName : opNames) {
						createRoleOp(appName,roleName,opName);
					}
				}
			}
		}
		
		//删除应用中的角色与操作的关联
	    //-4`角色2`操作3,操作4    (角色2取消操作3，操作4)
		if(upgradeMap.get(-4) != null){
			for (String delRoleOps : upgradeMap.get(-4)) {
				List<String> delRoleOpsStrings = StringUtil.strSplit(delRoleOps, "`");
				String roleName = delRoleOpsStrings.get(0);
				List<String> deleteOpNames = StringUtil.strSplit(delRoleOpsStrings.get(1), ",");
				
				if(deleteOpNames.size() !=0){
					for (String opName : deleteOpNames) {
						deleteRoleOp(appName,roleName,opName);
					}
				}
			}
		}
		
		//添加应用中的操作与角色的关联
	    //6`操作1`角色1,角色2   （操作1增加角色1，角色2）
		if(upgradeMap.get(6) != null){
			for (String opAndRoles : upgradeMap.get(6)) {
				List<String> opRolesStrings = StringUtil.strSplit(opAndRoles, "`");
				String opName = opRolesStrings.get(0);
				List<String> roleNames = StringUtil.strSplit(opRolesStrings.get(1), ",");
				
				if(roleNames.size() !=0){
					for (String roleName : roleNames) {
						createRoleOp(appName,roleName,opName);
					}
				}
			}
		}
		
		//删除应用中的角色与操作的关联
	    //-6`操作1`角色3,角色4   （操作1取消角色3，角色4）
		if(upgradeMap.get(-6) != null){
			for (String delOpRoles : upgradeMap.get(-6)) {
				List<String> delOpRolesStrings = StringUtil.strSplit(delOpRoles, "`");
				String opName = delOpRolesStrings.get(0);
				List<String> deleteRoleNames = StringUtil.strSplit(delOpRolesStrings.get(1), ",");
				
				if(deleteRoleNames.size() !=0){
					for (String roleName : deleteRoleNames) {
						deleteRoleOp(appName,roleName,opName);
					}
				}
			}
		}
		
		return true;
		
	}
	
	private Map<Integer, List<String>> prepareSyncContent(List<SyncContent> syncUpgradeList){
		//按照code分组
		Map<Integer, List<String>> syncMap = new HashMap<Integer, List<String>>();
		for (SyncContent syncContent : syncUpgradeList) {
			if(syncMap.get(syncContent.getOpCode()) == null){
				syncMap.put(syncContent.getOpCode(), new ArrayList<String>());
			}
			
			syncMap.get(syncContent.getOpCode()).add(syncContent.getContent());
		}
		
		return syncMap;
	}
	
	public List<SyncContent> exportAppAuth(String appName) throws Exception{
		/*
	权限导出
	     0`应用`描述
	     1`操作1`描述
	     1`操作2`描述
	     1`操作3`描述
	     2`角色1`描述
	     2`角色2`描述
	     2`角色3`描述
	     3`用户1``加密密码`描述
	     3`用户2``加密密码`描述
	     4`角色1`操作1,操作2,操作3,操作4
	     4`角色2`操作5,操作6,操作7,操作8
	     4`角色3`操作9,操作10
	     5`用户1`角色1,角色2
	     5`用户2`角色3
		 */
		
		List<SyncContent> syncContentList = new ArrayList<SyncContent>();
		
		//查询app
		AdminApp app = getApp(appName);
		SyncContent scApp = new SyncContent();
		scApp.setOpCode(0);
		scApp.setContent(app.getAppName()+"`"+app.getAppDescription());
		syncContentList.add(scApp);
		
		//查询所有操作
		List<AdminOp> opList = queryAppOps(appName);
		if(!opList.isEmpty()){
			for (AdminOp op : opList) {
				SyncContent scOp = new SyncContent();
				scOp.setOpCode(1);
				scOp.setContent(op.getOpName()+"`"+op.getOpDescription());
				syncContentList.add(scOp);
			}
		}
		
		//查询所有角色
		List<AdminRole> roleList = queryAppRoles(appName);
		if(!roleList.isEmpty()){
			for (AdminRole role : roleList) {
				SyncContent scRole = new SyncContent();
				scRole.setOpCode(2);
				scRole.setContent(role.getRoleName()+"`"+role.getRoleDescription());
				syncContentList.add(scRole);
			}
		}
		
		//查询所有用户
		List<AdminUser> userList = queryAllUser();
		if(!userList.isEmpty()){
			for (AdminUser user : userList) {
				SyncContent scUser = new SyncContent();
				scUser.setOpCode(3);
				scUser.setContent(user.getUserName()+"`"+"`"+user.getPassword()+"`"+user.getUserDescription());
				syncContentList.add(scUser);
			}
		}
		
		//查询角色对应的所有操作
		if(!roleList.isEmpty()){
			for (AdminRole role : roleList) {
				List<String> roleOpList = queryAppOpsByRole(appName,role.getRoleName());
				if(!roleOpList.isEmpty()){
					SyncContent scRoleOps = new SyncContent();
					scRoleOps.setOpCode(4);
					scRoleOps.setContent(role.getRoleName()+"`"+StringUtil.join(roleOpList, ","));
					syncContentList.add(scRoleOps);
				}
			}
		}
		
		//查询用户对应的所有角色
		if(!userList.isEmpty()){
			for (AdminUser user : userList) {
				List<AdminRole> rolesList = queryUserAppRoles(user.getUserName(),appName);
				if(!rolesList.isEmpty()){
					List<String> roles = new ArrayList<String>();
					for (AdminRole adminRole : rolesList) {
						if(adminRole.getAppName().equals(appName)){
							roles.add(adminRole.getRoleName());
						}
					}
					if(!roles.isEmpty()){
						SyncContent scUserRoles = new SyncContent();
						scUserRoles.setOpCode(5);
						scUserRoles.setContent(user.getUserName()+"`"+StringUtil.join(roles, ","));
						syncContentList.add(scUserRoles);
					}
				}
			}
		}
		
		return syncContentList;
		
	}
	
}
