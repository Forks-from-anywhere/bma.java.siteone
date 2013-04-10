package bma.siteone.config.service.simple;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import bma.siteone.config.service.ConfigGroup;
import bma.siteone.config.service.ConfigService;

public class SimpleConfigService implements ConfigService {

	private Map<String, ConfigGroup> groups = new HashMap<String, ConfigGroup>();

	public Map<String, ConfigGroup> getGroups() {
		return groups;
	}

	public void setGroups(Map<String, ConfigGroup> groups) {
		this.groups = groups;
	}

	public void setConfigs(Map<String, String> groups) throws Exception {
		for (Map.Entry<String, String> e : groups.entrySet()) {
			Properties p = new Properties();
			p.load(new StringReader(e.getValue()));
			this.groups.put(e.getKey(), new SimpleConfigGroup(p));
		}
	}

	@Override
	public ConfigGroup getGroup(String name) {
		ConfigGroup r = groups.get(name);
		return r == null ? EmptyConfigGroup.INSTANCE : r;
	}

}
