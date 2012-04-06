package bma.siteone.admin.po;

import java.io.Serializable;
import java.util.Date;

import bma.common.langutil.core.ToStringUtil;

public class AdminOpLog extends AdminOpLogShared implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	
	private Date time;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	@Override
	public String toString() {
		return ToStringUtil.fieldReflect(this);
	}

}
