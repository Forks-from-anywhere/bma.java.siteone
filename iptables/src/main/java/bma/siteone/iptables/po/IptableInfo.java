package bma.siteone.iptables.po;

import java.io.Serializable;

import bma.common.langutil.core.ToStringUtil;

public class IptableInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private int id;

	private String groupType;

	private String inet;

	private String type;

	public String getGroupType() {
		return groupType;
	}

	public void setGroupType(String group) {
		this.groupType = group;
	}

	public String getInet() {
		return inet;
	}

	public void setInet(String inet) {
		this.inet = inet;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return ToStringUtil.fieldReflect(this);
	}
}
