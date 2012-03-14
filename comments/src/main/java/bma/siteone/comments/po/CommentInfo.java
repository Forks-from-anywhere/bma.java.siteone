package bma.siteone.comments.po;

import java.io.Serializable;
import java.util.Date;

import bma.common.langutil.core.ToStringUtil;

/**
 * 评论
 * 
 * @author guanzhong
 * 
 */
public class CommentInfo extends CommentInfoShared implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer id;

	public final static int ST_NORMAL = 1;

	private int status;

	private int support;

	private int oppose;

	private int needAuth;

	private int hideFlag;

	private Date createTime;

	public CommentInfo() {
		super();
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public int getOppose() {
		return this.oppose;
	}

	public void setOppose(int oppose) {
		this.oppose = oppose;
	}

	public int getSupport() {
		return this.support;
	}

	public void setSupport(int support) {
		this.support = support;
	}

	public int getNeedAuth() {
		return this.needAuth;
	}

	public void setNeedAuth(int badComment) {
		this.needAuth = badComment;
	}

	public int getHideFlag() {
		return this.hideFlag;
	}

	public void setHideFlag(int hideFlag) {
		this.hideFlag = hideFlag;
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
