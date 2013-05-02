package bma.siteone.config.service.db;

import org.apache.thrift.TException;

import bma.siteone.config.thrift.TConfigAdminService;
import bma.siteone.config.thrift.TConfigInfo;

public class DbConfigThrift implements TConfigAdminService.Iface{

	final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(DbConfigThrift.class);
	
	DbConfigService dbConfigService;
	
	public DbConfigService getDbConfigService() {
		return dbConfigService;
	}

	public void setDbConfigService(DbConfigService dbConfigService) {
		this.dbConfigService = dbConfigService;
	}

	@Override
	public boolean refreshConfig(String app, String group) throws TException {
		try{
			return dbConfigService.refreshConfig(app, group);
		}catch (Exception e) {
			log.error("[refreshConfig] exception => "+e.getMessage());
		}
		return false;
	}

	@Override
	public boolean setConfig(String app, TConfigInfo info) throws TException {
		try{
			return dbConfigService.setConfig(app, info.getGroup(), info.getName(), info.getValue());
		}catch (Exception e) {
			log.error("[refreshConfig] exception => "+e.getMessage());
		}
		return false;
	}

	@Override
	public boolean deleteConfig(String app, String group, String name)
			throws TException {
		try{
			return dbConfigService.deleteConfig(app, group, name);
		}catch (Exception e) {
			log.error("[refreshConfig] exception => "+e.getMessage());
		}
		return false;
	}

	
	
}
