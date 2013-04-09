package bma.siteone.config;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import bma.siteone.config.service.ConfigGroup;
import bma.siteone.config.service.ConfigService;
import bma.siteone.config.service.simple.SimpleConfigService;

public class ConfigTest {

	@Before
	public void spring() throws Exception {
		Map<String, String> cfgs = new HashMap<String, String>();
		cfgs.put("test", "test1=val1\ntest2=val2");

		SimpleConfigService s = new SimpleConfigService();
		s.setConfigs(cfgs);
		setConfig(s);
	}

	private ConfigService config;

	public void setConfig(ConfigService s) {
		config = s;
	}

	private ConfigGroup config2;

	public void setConfig2(ConfigService s) {
		config2 = s.getGroup(ConfigTest.class.getName());
	}

	@Test
	public void baseUse() {
		ConfigGroup cg = config.getGroup("test");
		System.out.println(cg.getString("test", "defval"));
		System.out.println(cg.getString("test1", "defval"));
		
		ConfigGroup cg2 = config.getGroup("test2");
		System.out.println(cg2.getString("test", "defval"));
		System.out.println(cg2.getString("test1", "defval"));
	}

}
