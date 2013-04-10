package bma.siteone.config.service.simple;

import bma.siteone.config.service.ConfigGroup;
import bma.siteone.config.service.ConfigItem;

public class EmptyConfigGroup extends ConfigGroup {

	public static EmptyConfigGroup INSTANCE = new EmptyConfigGroup();

	@Override
	public ConfigItem get(String name) {
		return null;
	}

}
