package bma.siteone.actionlock.po;

import java.io.Serializable;
import java.util.Date;

/**
 * 锁信息
 * 
 * @author guanzhong
 * 
 */
public class ActionlockInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private String groupType;

	private String itemId;

	private int accessCount;

	private Date cleanTime;

	private Date createTime;

	private Date lastUpdateTime;

	public String getGroupType() {
		return groupType;
	}

	public void setGroupType(String groupType) {
		this.groupType = groupType;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public int getAccessCount() {
		return accessCount;
	}

	public void setAccessCount(int accessCount) {
		this.accessCount = accessCount;
	}

	public Date getCleanTime() {
		return cleanTime;
	}

	public void setCleanTime(Date cleanTime) {
		this.cleanTime = cleanTime;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

}
