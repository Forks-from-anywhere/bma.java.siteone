package bma.siteone.config.service.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import bma.siteone.config.service.ConfigGroup;
import bma.siteone.config.service.ConfigService;
import bma.siteone.config.service.simple.EmptyConfigGroup;

public class DbConfigService implements ConfigService {

	final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(DbConfigService.class);
	
	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	
	
	public String app;	//区分是哪个应用的配置

	public void setApp(String app) {
		this.app = app;
	}

	private Map<String, ConfigGroup> groups;
	
	public Map<String, ConfigGroup> getGroups() {
		return groups;
	}

	public void setGroups(Map<String, ConfigGroup> groups) {
		this.groups = groups;
	}

	private String configTableName = "so_config";

	public void setConfigTableName(String configTableName) {
		this.configTableName = configTableName;
	}

	private void initConfigGroups() {
		this.groups = new HashMap<String, ConfigGroup>();
		groups.clear();
		
		//查询当前应用在数据库中的所有配置
		String sql = "SELECT * FROM "+configTableName+" WHERE app='"+ app +"'";
		List<DbConfig> appConfigGroupList = jdbcTemplate.query(sql, new DbConfigRowMapper());
		
		if(!appConfigGroupList.isEmpty()){
			Map<String, Map<String,String>> groupConfigs = new HashMap<String, Map<String,String>>();
			
			for (DbConfig dbConfig : appConfigGroupList) {
				if(groupConfigs.get(dbConfig.getGroup()) == null){
					
					Map<String,String> configs = new HashMap<String, String>();
					configs.put(dbConfig.getName(), dbConfig.getValue());
					
					groupConfigs.put(dbConfig.getGroup(), configs);
				}else{
					groupConfigs.get(dbConfig.getGroup()).put(dbConfig.getName(), dbConfig.getValue());
				}
			}
			
			if(!groupConfigs.isEmpty()){
				for (Entry<String, Map<String, String>> e : groupConfigs.entrySet()) {
					this.groups.put(e.getKey(), new DbConfigGroup(e.getValue()));
				}
			}
		}
		
	}
	
	public class DbConfigRowMapper implements RowMapper<DbConfig> {

		public DbConfig mapRow(ResultSet rs, int index) throws SQLException {
			DbConfig dc = new DbConfig();
			
			dc.setApp(rs.getString("app"));
			dc.setGroup(rs.getString("cGroup"));
			dc.setName(rs.getString("cName"));
			dc.setValue(rs.getString("cValue"));
			
			return dc;
		}
	}
	
	
	@Override
	public ConfigGroup getGroup(String name) {
		if(groups == null){
			initConfigGroups();
		}
		
		ConfigGroup r = groups.get(name);
		return r == null ? EmptyConfigGroup.INSTANCE : r;
	}
	
	
	public boolean refreshConfig(String app, String group){
		try{
			initConfigGroups();
			
			return true;
		}catch (Exception e) {
			log.error("[refreshConfig] exception => "+e.getMessage());
			return false;
		}
	}
	
	public boolean setConfig(String app, String group, String name, String value){
		try{
			String sql = "INSERT INTO "+configTableName+" VALUES('"+app+"','"+group+"','"+name+"','"+value+"') ON DUPLICATE KEY UPDATE cValue='"+value+"'";
			
			jdbcTemplate.update(sql);
			
			initConfigGroups();
			return true;
		}catch (Exception e) {
			log.error("[setConfig] exception => "+e.getMessage());
			return false;
		}
		
	}
	
	public boolean deleteConfig(String app, String group, String name){
		try{
			String sql = "DELETE FROM "+configTableName+" WHERE app='"+app+"' and cGroup='"+group+"' and cName='"+name+"'";
			
			jdbcTemplate.update(sql);
			
			initConfigGroups();
			return true;
		}catch (Exception e) {
			log.error("[deleteConfig] exception => "+e.getMessage());
			return false;
		}
	}

}
