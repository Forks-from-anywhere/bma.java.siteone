package bma.siteone.admin.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mchange.v2.c3p0.impl.NewPooledConnection;

import bma.common.jdbctemplate.JdbcTemplateHelper;
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

@Transactional
public class AdminServiceImpl implements AdminService{

	final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(AdminServiceImpl.class);
	
	private AdminServiceImpl() {
		super();
	}
	
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
	@Override
	public boolean createUser(UserForm userForm) throws UnsupportedEncodingException{
		
		CommonFieldValues fvs = new CommonFieldValues();
		fvs.addString("user_name", userForm.getUserName());
		fvs.addString("password", StringUtil.md5(userForm.getPassword().getBytes("utf-8")));
		fvs.addString("user_description", userForm.getUserDescription());
		fvs.addSysdate("create_time");
		fvs.addInt("status",1);
		
		helper.executeInsert(adminUserTableName, fvs, null);
		return true;
		
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	@Override
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
		int c = helper.executeDelete(adminUserTableName, tj);
		
		return c == 1;
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
	@Override
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
	@Override
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
	@Override
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
	@Override
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
		UserForm userForm = new UserForm();
		userForm.setUserName(manager.getUserName());
		userForm.setPassword(manager.getPassword());
		userForm.setUserDescription(manager.getUserDescription());
		createUser(userForm);
			
		return true;
		
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public boolean createApp(AdminApp adminApp){
		
		CommonFieldValues fvs = new CommonFieldValues();
		fvs.addString("app_name", adminApp.getAppName());
		fvs.addString("app_description", adminApp.getAppDescription());		
		fvs.addSysdate("create_time");
		fvs.addInt("status",1);
		
		helper.executeInsert(adminAppTableName, fvs, null);
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
		
		helper.executeInsert(adminAppRoleTableName, fvs, null);
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
	@Override
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
		
		helper.executeInsert(adminAuthUserRoleTableName, fvs, userName);
		return true;
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	@Override
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
	@Override
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
		List<AdminRole> rolesList = queryRoles(userName);
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
	@Override
	public List<AdminRole> queryRoles(String userName){
		
		String sql = "SELECT * FROM "+adminAuthUserRoleTableName+" WHERE user_name=?";
		
		List<AdminAuth> authsList = jdbcTemplate.query(sql, new Object[]{userName}, new AdminAuthRowMapper());
		
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
		
		helper.executeInsert(adminAppOpTableName, fvs, null);
		return true;
		
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
		
		helper.executeInsert(adminAppRoleOpTableName, fvs, null);
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
	
	@Transactional(propagation = Propagation.REQUIRED)
	@Override
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
	
	@SuppressWarnings("deprecation")
	@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
	@Override
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
	
}
