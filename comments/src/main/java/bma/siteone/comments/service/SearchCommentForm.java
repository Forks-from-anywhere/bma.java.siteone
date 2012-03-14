package bma.siteone.comments.service;

import bma.common.langutil.core.SearchForm;
import bma.common.langutil.core.ToStringUtil;

public class SearchCommentForm extends SearchForm {

	private int pointId = 0;

	private int replyId = 0;

	/**
	 * CommentPoint.name
	 */
	private String point;

	/**
	 * CommentPoint.title
	 */
	private String subject;

	/**
	 * CommentPoint.url
	 */
	private String url;

	private String userName;
	private int userId;

	private String content;

	private String ip;

	private String startTime;

	private String endTime;

	private int needAuth = -1;

	private int status = -1;

	private int hide = -1;

	public int getReplyId() {
		return replyId;
	}

	public void setReplyId(int replyId) {
		this.replyId = replyId;
	}

	public String getIp() {
		return this.ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPointId() {
		return this.pointId;
	}

	public void setPointId(int pointId) {
		this.pointId = pointId;
	}

	public String getPoint() {
		return this.point;
	}

	public void setPoint(String point) {
		this.point = point;
	}

	public String getSubject() {
		return this.subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getStartTime() {
		return this.startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return this.endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public int getNeedAuth() {
		return needAuth;
	}

	public void setNeedAuth(int needAuth) {
		this.needAuth = needAuth;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getHide() {
		return hide;
	}

	public void setHide(int hide) {
		this.hide = hide;
	}

	@Override
	public String toString() {
		return ToStringUtil.fieldReflect(this);
	}
}
