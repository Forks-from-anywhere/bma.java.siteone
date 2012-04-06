package bma.siteone.admin.service;

import bma.common.langutil.core.SearchForm;
import bma.common.langutil.core.ToStringUtil;

public class OpLogQueryForm extends SearchForm{
	
	/**
	 *
	struct TOpLogQueryForm {
	  1: optional string user_name,
	  2: optional string app_name,
	  3: optional string role_name,
	  4: optional string op_name,
	  5: optional string description,
	  6: optional string start_time,
	  7: optional string end_time,
	  8: i32 page,
	  9: i32 pageSize,
	  10: optional string sort,
	  11: optional string sortDir,
	}
	 */
	
	private String userName;
	
	private String appName;
	
	private String roleName;
	
	private String opName;
	
	private String description;
	
	private String startTime;
	
	private String endTime;
	
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

	public String getOpName() {
		return opName;
	}

	public void setOpName(String opName) {
		this.opName = opName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	@Override
	public String toString() {
		return ToStringUtil.fieldReflect(this);
	}

}
