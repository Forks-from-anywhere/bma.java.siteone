package bma.siteone.comments.po;

public class CommentInfoShared {

	private int pointId;

	private int replyId;

	private String userName;

	private int userId;

	private String content;

	private String ip;

	private int reserve1;

	private int reserve2;

	private String reserve3 = "";

	private String reserve4 = "";

	private int status;

	private int support;

	private int oppose;

	private int needAuth;

	private int hideFlag;

	public CommentInfoShared() {
		super();
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getIp() {
		return this.ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public CommentInfoShared(int commentPointId) {
		this.pointId = commentPointId;
	}

	public int getPointId() {
		return this.pointId;
	}

	public void setPointId(int id) {
		this.pointId = id;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String name) {
		this.userName = name;
	}

	public int getReserve1() {
		return reserve1;
	}

	public void setReserve1(int reserve1) {
		this.reserve1 = reserve1;
	}

	public int getReserve2() {
		return reserve2;
	}

	public void setReserve2(int reserve2) {
		this.reserve2 = reserve2;
	}

	public String getReserve3() {
		return reserve3;
	}

	public void setReserve3(String reserve3) {
		this.reserve3 = reserve3;
	}

	public String getReserve4() {
		return reserve4;
	}

	public void setReserve4(String reserve4) {
		this.reserve4 = reserve4;
	}

	public int getReplyId() {
		return replyId;
	}

	public void setReplyId(int replyId) {
		this.replyId = replyId;
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
}
