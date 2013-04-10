package bma.siteone.config.service;

import java.util.Date;

public abstract class ConfigGroup {

	public String getString(String name) {
		ConfigItem item = get(name);
		if (item != null)
			return item.stringValue();
		throw new IllegalArgumentException("config '" + name + "' not exists");
	}

	public String getString(String name, String def) {
		ConfigItem item = get(name);
		if (item != null)
			return item.stringValue();
		return def;
	}
	
	public int getInt(String name) {
		ConfigItem item = get(name);
		if (item != null)
			return item.intValue();
		throw new IllegalArgumentException("config '" + name + "' not exists");
	}

	public int getInt(String name, int def) {
		ConfigItem item = get(name);
		if (item != null)
			return item.intValue();
		return def;
	}

	public long getLong(String name) {
		ConfigItem item = get(name);
		if (item != null)
			return item.longValue();
		throw new IllegalArgumentException("config '" + name + "' not exists");
	}

	public long getLong(String name, long def) {
		ConfigItem item = get(name);
		if (item != null)
			return item.longValue();
		return def;
	}

	public Date getDate(String name) {
		ConfigItem item = get(name);
		if (item != null)
			return item.dateValue();
		throw new IllegalArgumentException("config '" + name + "' not exists");
	}

	public Date getDate(String name, Date def) {
		ConfigItem item = get(name);
		if (item != null)
			return item.dateValue();
		return def;
	}

	public double getDouble(String name) {
		ConfigItem item = get(name);
		if (item != null)
			return item.doubleValue();
		throw new IllegalArgumentException("config '" + name + "' not exists");
	}

	public double getDouble(String name, double def) {
		ConfigItem item = get(name);
		if (item != null)
			return item.doubleValue();
		return def;
	}

	public float getFloat(String name) {
		ConfigItem item = get(name);
		if (item != null)
			return item.floatValue();
		throw new IllegalArgumentException("config '" + name + "' not exists");
	}

	public float getFloat(String name, float def) {
		ConfigItem item = get(name);
		if (item != null)
			return item.floatValue();
		return def;
	}

	public boolean getBoolean(String name) {
		ConfigItem item = get(name);
		if (item != null)
			return item.booleanValue();
		throw new IllegalArgumentException("config '" + name + "' not exists");
	}

	public boolean getBoolean(String name, boolean def) {
		ConfigItem item = get(name);
		if (item != null)
			return item.booleanValue();
		return def;
	}
	
	public <TYPE> TYPE get(String name, ConfigConvert cc) {
		if (cc == null)
			return null;
		ConfigItem item = get(name);
		if (item == null)
			return null;
		return cc.convert(item);
	}

	public abstract ConfigItem get(String name);

}
