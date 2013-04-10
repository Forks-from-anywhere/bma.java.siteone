package bma.siteone.config.service.db;

import java.io.Serializable;
import java.util.Date;

import bma.common.langutil.core.ToStringUtil;

/**
 * 配置（数据库）实体
 */
public class DbConfig implements Serializable {

	private static final long serialVersionUID = 1L;

	private String app;
	private String group;
	private String name;
	private String value;

	public String getApp() {
		return app;
	}

	public void setApp(String app) {
		this.app = app;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return ToStringUtil.fieldReflect(this);
	}

}
