package bma.siteone.admin.service;

import java.io.IOException;
import java.util.List;

import org.apache.thrift.TException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

import bma.siteone.admin.thrift.*;
import bma.siteone.admin.service.BaseServiceImpl;

public class AdminDeployThrift implements TAdminDeployService.Iface{
	
	final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(AdminDeployThrift.class);

	private BaseServiceImpl service;
	
	public BaseServiceImpl getService() {
		return service;
	}

	public void setService(BaseServiceImpl service) {
		this.service = service;
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

	@Override
	public boolean initAppAuth(List<TSync> syncInit) throws TException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean upgradeAppAuth(List<TSync> syncUpgrade) throws TException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<TSync> exportAppAuth(String appName) throws TException {
		// TODO Auto-generated method stub
		return null;
	}


}
