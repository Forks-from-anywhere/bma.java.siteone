package bma.siteone.admin.service;

import java.io.IOException;
import java.util.ArrayList;
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

	@Override
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
		if (log.isDebugEnabled()) {
			log.debug("initAppAuth");
		}
		
		try{
			if(syncInit.isEmpty()){
				return false;
			}
			
			List<SyncContent> syncInitList = new ArrayList<SyncContent>();
			for (TSync tSync : syncInit) {
				SyncContent sc = new SyncContent();
				sc.setOpCode(tSync.getOpCode());
				sc.setContent(tSync.getContent());
				syncInitList.add(sc);
			}
			return service.initAppAuth(syncInitList);
		}catch (Exception e) {
			e.printStackTrace();
			if (log.isWarnEnabled()) {
				log.warn("[initAppAuth] exception=>({})",e.getMessage());
			}
			throw new TException("[initAppAuth] exception=> ["+e.getClass()+"] "+e.getMessage());
		}
		
	}

	@Override
	public boolean upgradeAppAuth(List<TSync> syncUpgrade) throws TException {
		if (log.isDebugEnabled()) {
			log.debug("upgradeAppAuth");
		}
		
		try{
			if(syncUpgrade.isEmpty()){
				return false;
			}
			
			List<SyncContent> syncUpgradeList = new ArrayList<SyncContent>();
			for (TSync tSync : syncUpgrade) {
				SyncContent sc = new SyncContent();
				sc.setOpCode(tSync.getOpCode());
				sc.setContent(tSync.getContent());
				syncUpgradeList.add(sc);
			}
			return service.upgradeAppAuth(syncUpgradeList);
		}catch (Exception e) {
			if (log.isWarnEnabled()) {
				log.warn("[upgradeAppAuth] exception=>({})",e.getMessage());
			}
			throw new TException("[upgradeAppAuth] exception=> ["+e.getClass()+"] "+e.getMessage());
		}
	}

	@Override
	public List<TSync> exportAppAuth(String appName) throws TException {
		if (log.isDebugEnabled()) {
			log.debug("exportAppAuth");
		}
		
		List<TSync> tSyncList = new ArrayList<TSync>();
		
		try{
			if(appName == null || appName == ""){
				return tSyncList;
			}
			
			List<SyncContent> syncList = service.exportAppAuth(appName);
			if(!syncList.isEmpty()){
				for (SyncContent syncContent : syncList) {
					TSync tSync = new TSync();
					tSync.setOpCode(syncContent.getOpCode());
					tSync.setContent(syncContent.getContent());
					tSyncList.add(tSync);
				}
			}
			return tSyncList;
		}catch (Exception e) {
			if (log.isWarnEnabled()) {
				log.warn("[exportAppAuth] exception=>({})",e.getMessage());
			}
			return tSyncList;
		}
		
	}


}
