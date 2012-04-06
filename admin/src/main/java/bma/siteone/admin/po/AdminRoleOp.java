package bma.siteone.admin.po;

import java.io.Serializable;

import bma.common.langutil.core.ToStringUtil;

public class AdminRoleOp implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String appName;
	
	private String roleName;
	
	private String opName;
	
	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getOpName() {
		return opName;
	}

	public void setOpName(String opName) {
		this.opName = opName;
	}

	@Override
	public String toString() {
		return ToStringUtil.fieldReflect(this);
	}

}
