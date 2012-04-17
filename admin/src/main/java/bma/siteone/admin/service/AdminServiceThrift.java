package bma.siteone.admin.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.thrift.TException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

import bma.common.langutil.bean.copy.BeanCopyTool;
import bma.common.langutil.convert.common.DateFormatConverter;
import bma.common.langutil.core.Function;
import bma.common.langutil.core.ListUtil;
import bma.common.langutil.core.PagerResult;
import bma.siteone.admin.po.AdminOpLog;
import bma.siteone.admin.po.AdminRole;
import bma.siteone.admin.po.AdminUser;
import bma.siteone.admin.thrift.*;

public class AdminServiceThrift implements TAdminService.Iface{
	
	final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(AdminServiceThrift.class);

	private AdminService service;
	
	private transient BeanCopyTool source;
	private transient BeanCopyTool target;
	
	public AdminServiceThrift() {
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

	public AdminService getService() {
		return service;
	}

	public void setService(AdminService service) {
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

    public boolean syncApp(String syncContent) throws TException{
    	
    	if (log.isDebugEnabled()) {
			log.debug("syncApp({})",syncContent);
		}
    	try {
			return service.syncApp(syncContent);
		} catch (JsonParseException e) {
			if (log.isWarnEnabled()) {
				log.warn("syncApp({}), exception=>({})",syncContent,e.getMessage());
			}
			return false;
		} catch (JsonMappingException e) {
			if (log.isWarnEnabled()) {
				log.warn("syncApp({}), exception=>({})",syncContent,e.getMessage());
			}
			return false;
		} catch (IOException e) {
			if (log.isWarnEnabled()) {
				log.warn("syncApp({}), exception=>({})",syncContent,e.getMessage());
			}
			return false;
		}
    }

    public List<TRole> queryRoles(String userName) throws TException{
    	if (log.isDebugEnabled()) {
			log.debug("queryRoles({})",userName);
		}
    	List<AdminRole> rolesList = service.queryRoles(userName);
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

    public boolean checkUserAuth(String userName, String appName, String opName) throws TException{
    	if (log.isDebugEnabled()) {
			log.debug("checkUserAuth({},{},{})",new Object[]{userName,appName,opName});
		}
    	return service.checkUserAuth(userName, appName, opName);
    }

    public boolean addOpLog(TOpLogForm opLogForm) throws TException{
    	if (log.isDebugEnabled()) {
			log.debug("addOpLog({})",opLogForm);
		}
//    	OpLogForm _opLogForm = target.newInstance(null, opLogForm, OpLogForm.class);
    	OpLogForm _opLogForm = new OpLogForm();
    	_opLogForm.setUserName(opLogForm.getUserName());
    	_opLogForm.setAppName(opLogForm.getAppName());
    	_opLogForm.setRoleName(opLogForm.getRoleName());
    	_opLogForm.setOpName(opLogForm.getOpName());
    	_opLogForm.setDescription(opLogForm.getDescription());
    	
    	return service.addOpLog(_opLogForm);
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
	public List<String> queryAppRoles(String appName) throws TException {
    	if (log.isDebugEnabled()) {
			log.debug("queryAppUsers({})",appName);
		}
    	return service.queryAppRoles(appName);
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

/*
	@Override
	public TAllUsersResult queryAllUsers(int page, int pageSize) throws TException {
    	if (log.isDebugEnabled()) {
			log.debug("queryAllUsers({},{},{})", new Object[]{page ,pageSize});
		}
    	
    	PagerResult<String> pr = service.queryAllUsers(page,pageSize);
    	
    	TAllUsersResult result = new TAllUsersResult();
    	
    	result.setTotal(pr.getPager().getTotal());
    	result.setResult(pr.getResult());
    	return result;
	}
*/
	
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
}
