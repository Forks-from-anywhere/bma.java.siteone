package bma.siteone.config.service.simple;

import java.util.Date;

import bma.common.langutil.core.ValueUtil;
import bma.siteone.config.service.ConfigItem;

public class StringConfigItem implements ConfigItem {

	private String value;

	public StringConfigItem(String value) {
		super();
		this.value = value;
	}

	@Override
	public int type() {
		return TYPE_STRING;
	}

	@Override
	public String stringValue() {
		return value;
	}

	@Override
	public int intValue() {
		return ValueUtil.intValue(value, 0);
	}

	@Override
	public long longValue() {
		return ValueUtil.longValue(value, 0);
	}

	@Override
	public Date dateValue() {
		return ValueUtil.datetimeValue(value, null);
	}

	@Override
	public double doubleValue() {
		return ValueUtil.doubleValue(value, 0);
	}

	@Override
	public float floatValue() {
		return ValueUtil.floatValue(value, 0);
	}

	@Override
	public boolean booleanValue() {
		return ValueUtil.booleanValue(value, false);
	}

}
