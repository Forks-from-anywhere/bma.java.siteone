package bma.siteone.message.service;

import java.io.Serializable;

import bma.common.langutil.core.ToStringUtil;

public class SendMessageForm implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String app;
	private int type;
	private String receiver;
	private String sender;
	private String title;
	private String content;
	
	
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


	@Override
	public String toString() {
		return ToStringUtil.fieldReflect(this);
	}


}
