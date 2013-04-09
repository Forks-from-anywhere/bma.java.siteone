package bma.siteone.config.service.simple;

import java.util.Properties;

import bma.siteone.config.service.ConfigGroup;
import bma.siteone.config.service.ConfigItem;

public class SimpleConfigGroup extends ConfigGroup {

	private Properties configs;

	public SimpleConfigGroup() {
		super();
	}

	public SimpleConfigGroup(Properties configs) {
		super();
		this.configs = configs;
	}

	public Properties getConfigs() {
		return configs;
	}

	public void setConfigs(Properties configs) {
		this.configs = configs;
	}

	@Override
	public ConfigItem get(String name) {
		String v = configs == null ? null : configs.getProperty(name);
		if (v == null) {
			return null;
		}
		return new StringConfigItem(v);
	}

}
