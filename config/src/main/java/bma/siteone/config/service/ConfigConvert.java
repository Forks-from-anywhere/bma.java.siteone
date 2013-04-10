package bma.siteone.config.service;

public interface ConfigConvert {

	public <TYPE> TYPE convert(ConfigItem item);
}
