package bma.siteone.admin.po;

import java.io.Serializable;
import java.util.Date;

import bma.common.langutil.core.ToStringUtil;

/**
 * 应用的操作
 * @author liaozhuojie
 *
 */
public class AdminOp implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String appName;
	
	private String opName;
	
	private String opDescription;
	
	private Date createTime;
	
	private int status;

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getOpName() {
		return opName;
	}

	public void setOpName(String opName) {
		this.opName = opName;
	}

	public String getOpDescription() {
		return opDescription;
	}

	public void setOpDescription(String opDescription) {
		this.opDescription = opDescription;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
	@Override
	public String toString() {
		return ToStringUtil.fieldReflect(this);
	}

}
