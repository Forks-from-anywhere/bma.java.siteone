package bma.siteone.message.po;

import java.io.Serializable;
import java.util.Date;

import bma.common.langutil.core.ToStringUtil;

public class MessageInfo implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private int id;
	private String app;
	private int type;
	private String receiver;
	private String sender;
	private String title;
	private String content;
	private Date sendTime;
	private int isRead;
	

	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}
	

	public String getApp() {
		return app;
	}


	public void setApp(String app) {
		this.app = app;
	}


	public int getType() {
		return type;
	}


	public void setType(int type) {
		this.type = type;
	}


	public String getReceiver() {
		return receiver;
	}


	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}


	public String getSender() {
		return sender;
	}


	public void setSender(String sender) {
		this.sender = sender;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public String getContent() {
		return content;
	}


	public void setContent(String content) {
		this.content = content;
	}


	public Date getSendTime() {
		return sendTime;
	}


	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}


	public int getIsRead() {
		return isRead;
	}


	public void setIsRead(int isRead) {
		this.isRead = isRead;
	}


	@Override
	public String toString() {
		return ToStringUtil.fieldReflect(this);
	}

}
