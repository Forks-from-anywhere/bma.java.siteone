package bma.siteone.admin.service;

import org.apache.thrift.TException;

import bma.siteone.admin.thrift.*;
import bma.siteone.admin.service.BaseServiceImpl;

public class AdminAppThrift implements TAdminAppService.Iface{
	
	final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(AdminAppThrift.class);

	private BaseServiceImpl service;
	
	public BaseServiceImpl getService() {
		return service;
	}

	public void setService(BaseServiceImpl service) {
		this.service = service;
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
    	OpLogForm _opLogForm = new OpLogForm();
    	_opLogForm.setUserName(opLogForm.getUserName());
    	_opLogForm.setAppName(opLogForm.getAppName());
    	_opLogForm.setRoleName(opLogForm.getRoleName());
    	_opLogForm.setOpName(opLogForm.getOpName());
    	_opLogForm.setDescription(opLogForm.getDescription());
    	
    	return service.addOpLog(_opLogForm);
    }

}
