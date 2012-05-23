package bma.siteone.comments.service;

import bma.common.langutil.core.ValueUtil;

public class CacheForm {

	private int commentPointId;
	private String commentPointName;
	private String cache;
	private int commentId;

	public boolean isCommentPoint() {
		if (commentPointId > 0)
			return true;
		if (ValueUtil.notEmpty(commentPointName))
			return true;
		return false;
	}

	public int getCommentPointId() {
		return commentPointId;
	}

	public void setCommentPointId(int commentPointId) {
		this.commentPointId = commentPointId;
	}

	public String getCommentPointName() {
		return commentPointName;
	}

	public void setCommentPointName(String commentPointName) {
		this.commentPointName = commentPointName;
	}

	public String getCache() {
		return cache;
	}

	public void setCache(String cache) {
		this.cache = cache;
	}

	public int getCommentId() {
		return commentId;
	}

	public void setCommentId(int commentId) {
		this.commentId = commentId;
	}

}
