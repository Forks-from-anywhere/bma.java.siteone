package bma.siteone.comments.po;

import java.io.Serializable;
import java.util.Date;

import bma.common.langutil.core.ToStringUtil;

/**
 * 评论点
 * 
 * @author guanzhong
 * 
 */
public class CommentPoint extends CommentPointShared implements Serializable {

	private static final long serialVersionUID = 1L;

	private int id;

	private int commentAmount;

	public final static int ST_DISABLED = 0;

	public final static int ST_NORMAL = 1;

	private int status;

	private Date lastCommentTime;

	private Date createTime;

	private Date lastUpdateTime;

	public CommentPoint() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCommentAmount() {
		return this.commentAmount;
	}

	public void setCommentAmount(int commentAmount) {
		this.commentAmount = commentAmount;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getLastCommentTime() {
		return this.lastCommentTime;
	}

	public void setLastCommentTime(Date lastCommentTime) {
		this.lastCommentTime = lastCommentTime;
	}

	public Date getLastUpdateTime() {
		return this.lastUpdateTime;
	}

	public void setLastUpdateTime(Date updateTime) {
		this.lastUpdateTime = updateTime;
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
