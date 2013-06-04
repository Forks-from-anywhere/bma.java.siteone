package bma.siteone.nick.po;

public class NickUser {
	private Long uid ;
	private String nick ;
	private int overdue_time;
	private int modify_time ;
	public Long getUid() {
		return uid;
	}
	public void setUid(Long uid) {
		this.uid = uid;
	}
	public String getNick() {
		return nick;
	}
	public void setNick(String nick) {
		this.nick = nick;
	}
	public int getOverdue_time() {
		return overdue_time;
	}
	public void setOverdue_time(int overdue_time) {
		this.overdue_time = overdue_time;
	}
	public int getModify_time() {
		return modify_time;
	}
	public void setModify_time(int modify_time) {
		this.modify_time = modify_time;
	}
}
