package bma.siteone.admin.po;

import java.io.Serializable;
import java.util.Date;

import bma.common.langutil.core.ToStringUtil;

/**
 * 用户
 * @author liaozhuojie
 *
 */
public class AdminUser extends UserShared implements Serializable{

	private static final long serialVersionUID = 1L;

	private Date createTime;
	
	private int status;
	
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
