package bma.siteone.admin.po;

import java.io.Serializable;

import bma.common.langutil.core.ToStringUtil;

public class AdminAuth implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String userName;
	
	private String appName;
	
	private String roleName;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

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
	
	@Override
	public String toString() {
		return ToStringUtil.fieldReflect(this);
	}
	
}
