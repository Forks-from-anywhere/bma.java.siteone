package bma.siteone.config.service.db;

import java.util.Map;

import bma.siteone.config.service.ConfigGroup;
import bma.siteone.config.service.ConfigItem;

public class DbConfigGroup extends ConfigGroup {

	private Map<String, String> configs;
	
	public DbConfigGroup() {
		super();
	}

	public DbConfigGroup(Map<String, String> configs) {
		super();
		this.configs = configs;
	}

	public Map<String, String> getConfigs() {
		return configs;
	}

	public void setConfigs(Map<String, String> configs) {
		this.configs = configs;
	}
	
	@Override
	public ConfigItem get(String name) {
		
		String value = configs == null ? null : configs.get(name);
		if (value == null) {
			return null;
		}
		return new DbConfigItem(value);
		
	}

	
}
