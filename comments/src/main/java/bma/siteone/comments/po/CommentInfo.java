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

	@Override
	public String toString() {
		return ToStringUtil.fieldReflect(this);
	}
}
