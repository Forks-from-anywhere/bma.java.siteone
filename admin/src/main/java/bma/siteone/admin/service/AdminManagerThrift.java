package bma.siteone.admin.service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.thrift.TException;

import bma.common.langutil.bean.copy.BeanCopyTool;
import bma.common.langutil.convert.common.DateFormatConverter;
import bma.common.langutil.core.DateTimeUtil;
import bma.common.langutil.core.Function;
import bma.common.langutil.core.ListUtil;
import bma.common.langutil.core.PagerResult;
import bma.siteone.admin.po.AdminOp;
import bma.siteone.admin.po.AdminOpLog;
import bma.siteone.admin.po.AdminRole;
import bma.siteone.admin.po.AdminUser;
import bma.siteone.admin.thrift.*;
import bma.siteone.admin.service.BaseServiceImpl;

public class AdminManagerThrift implements TAdminManagerService.Iface{
	
	final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(AdminManagerThrift.class);

	private transient BeanCopyTool source;
	private transient BeanCopyTool target;
	
	public AdminManagerThrift() {
		super();
		source = new BeanCopyTool();
		initBeanCopy(source);
		target = new BeanCopyTool();
//		target.setSourceStruct(false);
		initBeanCopy(target);
	}
	
	protected void initBeanCopy(BeanCopyTool tool) {
		tool.field("createTime").converter(DateFormatConverter.DATE_TIME);
	}

	private BaseServiceImpl service;
	
	public BaseServiceImpl getService() {
		return service;
	}

	public void setService(BaseServiceImpl service) {
		this.service = service;
	}
	
	
    public boolean createUser(TUserForm userForm) throws TException{
    	if (log.isDebugEnabled()) {
			log.debug("createUser({})",userForm);
		}
    	UserForm _userForm = new UserForm();
    	_userForm.setUserName(userForm.getUserName());
    	_userForm.setPassword(userForm.getPassword());
    	_userForm.setUserDescription(userForm.getUserDescription());
    	
    	try {
			return service.createUser(_userForm);
		} catch (UnsupportedEncodingException e) {
			if (log.isWarnEnabled()) {
				log.warn("createUser({}), exception=>({})",userForm,e.getMessage());
			}
			return false;
		}
    	
    }

    public boolean deleteUser(String userName) throws TException{
    	if (log.isDebugEnabled()) {
			log.debug("deleteUser({})",userName);
		}
    	return service.deleteUser(userName);
    }

    public boolean changePassword(String userName, String oldPassword, String newPassword) throws TException{
    	if (log.isDebugEnabled()) {
			log.debug("changePassword({})",userName);
		}
    	try {
			return service.changePassword(userName, oldPassword, newPassword);
		} catch (UnsupportedEncodingException e) {
			if (log.isWarnEnabled()) {
				log.warn("changePassword({}), exception=>({})",userName,e.getMessage());
			}
			return false;
		}
    }

    public boolean resetPassword(String userName, String newPassword) throws TException{
    	if (log.isDebugEnabled()) {
			log.debug("resetPassword({})",userName);
		}
    	try {
    		return service.resetPassword(userName, newPassword);
		} catch (UnsupportedEncodingException e) {
			if (log.isWarnEnabled()) {
				log.warn("resetPassword({}), exception=>({})",userName,e.getMessage());
			}
			return false;
		}
    	
    }

    public boolean checkUserPassword(String userName, String password) throws TException{
    	if (log.isDebugEnabled()) {
			log.debug("checkUserPassword({})",userName);
		}
    	try {
			return service.checkUserPassword(userName, password);
		} catch (UnsupportedEncodingException e) {
			if (log.isWarnEnabled()) {
				log.warn("checkUserPassword({}), exception=>({})",userName,e.getMessage());
			}
			return false;
		}
    	
    }



    public List<TRole> queryUserRoles(String userName) throws TException{
    	if (log.isDebugEnabled()) {
			log.debug("queryUserRoles({})",userName);
		}
    	List<AdminRole> rolesList = service.queryUserRoles(userName);
    	List<TRole> tRoleList = new ArrayList<TRole>();
    	for(AdminRole role : rolesList){
    		TRole _trole = source.newInstance(null, role, TRole.class);
    		tRoleList.add(_trole);
    	}
    	
    	return tRoleList;
    }

    public boolean addUserAuth(String userName, String appName, String roleName) throws TException{
    	if (log.isDebugEnabled()) {
			log.debug("addUserAuth({},{},{})",new Object[]{userName,appName,roleName});
		}
    	return service.addUserAuth(userName, appName, roleName);
    }

    public boolean deleteUserAuth(String userName, String appName, String roleName) throws TException{
    	if (log.isDebugEnabled()) {
			log.debug("deleteUserAuth({},{},{})",new Object[]{userName,appName,roleName});
		}
    	return service.deleteUserAuth(userName, appName, roleName);
    }


    public TOpLogRessult queryOpLogs(TOpLogQueryForm opLogQueryForm) throws TException{
    	if (log.isDebugEnabled()) {
			log.debug("queryOpLogs({})",opLogQueryForm);
		}
    	
    	OpLogQueryForm queryForm = target.newInstance(null, opLogQueryForm, OpLogQueryForm.class);
    	PagerResult<AdminOpLog> pr = service.queryOpLogs(queryForm);
    	
    	TOpLogRessult r = new TOpLogRessult();
    	r.setTotal(pr.getPager().getTotal());
		r.setResult(ListUtil.toList(pr.getResult(),
				new Function<AdminOpLog, TOpLog>() {
					@Override
					public TOpLog apply(AdminOpLog input) {
						return source.newInstance(null, input,
								TOpLog.class);
					}
				}));
		return r;
    }

/*    
	@Override
	public TAppUsersResult queryAppUsers(String appName, int page, int pageSize) throws TException {
    	if (log.isDebugEnabled()) {
			log.debug("queryAppUsers({},{},{})", new Object[]{appName ,page ,pageSize});
		}
    	
    	PagerResult<String> pr = service.queryAppUsers(appName,page,pageSize);
    	
    	TAppUsersResult result = new TAppUsersResult();
    	
    	result.setTotal(pr.getPager().getTotal());
    	result.setResult(pr.getResult());
    	return result;

	}
*/

	@Override
	public List<TRole> queryAppRoles(String appName) throws TException {
		if (log.isDebugEnabled()) {
			log.debug("queryAppRoles({})",appName);
		}
		
		List<AdminRole> rolesList = service.queryAppRoles(appName);
		List<TRole> tRolesList = new ArrayList<TRole>();
		if(rolesList.size() !=0 ){
			for (AdminRole role : rolesList) {
				TRole t = new TRole();
				t.setAppName(role.getAppName());
				t.setRoleName(role.getRoleName());
				t.setRoleDescription(role.getRoleDescription());
				t.setCreateTime(DateTimeUtil.formatDateTime(role.getCreateTime()));
				t.setStatus(role.getStatus());
				tRolesList.add(t);
			}
		}
		
		return tRolesList;
    	
	}

	@Override
	public boolean checkUserExist(String userName) throws TException {
    	if (log.isDebugEnabled()) {
			log.debug("checkUserExist({})",userName);
		}
    	return service.checkUserExist(userName);
	}

	@Override
	public TUser getUser(String userName) throws TException {
    	if (log.isDebugEnabled()) {
			log.debug("getUser({})",userName);
		}
    	
    	AdminUser adminUser =  service.getUser(userName);
    	
    	TUser user = target.newInstance(null, adminUser, TUser.class);
    	
    	return user;
	}

	
	@Override
	public List<TUser> queryAllUser() throws TException {
		List<AdminUser> usersList = service.queryAllUser();
		List<TUser> tusersList = new ArrayList<TUser>();
		for(AdminUser user : usersList){
			TUser tuser = new TUser();
			tuser.setUserName(user.getUserName());
			tuser.setPassword(user.getPassword());
			tuser.setUserDescription(user.getUserDescription());
			tuser.setCreateTime(user.getCreateTime().toLocaleString());
			tuser.setStatus(user.getStatus());
			tusersList.add(tuser);
		}
		return tusersList;
	}

	@Override
	public List<TOp> queryAppOps(String appName) throws TException {
		if (log.isDebugEnabled()) {
			log.debug("queryAppOps({})",appName);
		}
		
		List<AdminOp> opsList = service.queryAppOps(appName);
		List<TOp> tOpsList = new ArrayList<TOp>();
		if(opsList.size() !=0 ){
			for (AdminOp op : opsList) {
				TOp t = new TOp();
				t.setAppName(op.getAppName());
				t.setOpName(op.getOpName());
				t.setOpDescription(op.getOpDescription());
				t.setCreateTime(DateTimeUtil.formatDateTime(op.getCreateTime()));
				t.setStatus(op.getStatus());
				tOpsList.add(t);
			}
		}
		
		return tOpsList;
	}

	@Override
	public Map<String, List<String>> queryAppRolesByOps(String appName,
			List<String> opNames) throws TException {
		if (log.isDebugEnabled()) {
			log.debug("queryAppRolesByOps()");
		}
		
		Map<String, List<String>> result = new HashMap<String, List<String>>();
		
		if(opNames.size() != 0){
			result = service.queryAppRolesByOps(appName, opNames);
		}
		
		return result;
	}

	@Override
	public List<String> queryAppOpsByRole(String appName, String roleName)
			throws TException {
		if (log.isDebugEnabled()) {
			log.debug("queryAppOpsByRole({},{})",appName,roleName);
		}
		
		List<String> opsList = service.queryAppOpsByRole(appName, roleName);
		
		return opsList;
	}

	@Override
	public boolean createRole(TRole role) throws TException {
		if (log.isDebugEnabled()) {
			log.debug("createRole({},{})",role.getAppName(),role.getRoleName());
		}
		
		AdminRole adminRole = new AdminRole();
		adminRole.setAppName(role.getAppName());
		adminRole.setRoleName(role.getRoleName());
		adminRole.setRoleDescription((role.getRoleDescription() == null ? "":role.getRoleDescription()));
		try{
			return service.createRole(adminRole);
		}catch (Exception e) {
			if (log.isWarnEnabled()) {
				log.warn("createRole({}), exception=>({})",role.getAppName()+","+role.getRoleName(),e.getMessage());
			}
			return false;
		}
	}

	@Override
	public boolean deleteRole(String appName, String roleName)
			throws TException {
		if (log.isDebugEnabled()) {
			log.debug("deleteRole({},{})",appName,roleName);
		}
		
		try{
			return service.deleteRole(appName,roleName);
		}catch (Exception e) {
			if (log.isWarnEnabled()) {
				log.warn("deleteRole({}), exception=>({})",appName+","+roleName,e.getMessage());
			}
			return false;
		}
		
	}

	@Override
	public boolean resetRoleOps(String appName, TSetRoleOpsForm roleOpsForm)
			throws TException {
		if (log.isDebugEnabled()) {
			log.debug("resetRoleOps({},{})",appName,roleOpsForm.getRoleName());
		}
		
		try{
			List<String> roles = service.queryAppRoleNames(appName);
			if(!roles.contains(roleOpsForm.getRoleName())){
				return false;
			}
			
			return service.resetRoleOps(appName,roleOpsForm.getRoleName(),roleOpsForm.getOpNameList());
		}catch (Exception e) {
			if (log.isWarnEnabled()) {
				log.warn("resetRoleOps({}), exception=>({})",appName+","+roleOpsForm.getRoleName(),e.getMessage());
			}
			return false;
		}
		
	}
}
