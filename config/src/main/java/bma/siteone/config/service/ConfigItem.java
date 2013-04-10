package bma.siteone.config.service;

import java.util.Date;

public interface ConfigItem {

	public static final int TYPE_NULL = 0;
	public static final int TYPE_STRING = 1;
	public static final int TYPE_INT = 2;
	public static final int TYPE_LONG = 3;
	public static final int TYPE_DATE = 4;
	public static final int TYPE_DOUBLE = 5;
	public static final int TYPE_FLOAT = 6;
	public static final int TYPE_BOOL = 7;

	public int type();

	public String stringValue();

	public int intValue();

	public long longValue();

	public Date dateValue();

	public double doubleValue();

	public float floatValue();

	public boolean booleanValue();
	
}
