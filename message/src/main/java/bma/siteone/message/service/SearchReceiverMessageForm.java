package bma.siteone.message.service;

import bma.common.langutil.core.ToStringUtil;


public class SearchReceiverMessageForm {
	
	/*
	 * 
		struct TSearchReceiverMessageForm {
		  1: string receiver,
		  2: string app,
		  3: optional i32 page = 1,
		  4: optional i32 pageSize = 10,
		}
	 *	
	 */
	
	private String receiver;
	private String app;
	private int page = 1;
	private int pageSize = 10;
	
	
	public String getReceiver() {
		return receiver;
	}


	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}


	public String getApp() {
		return app;
	}


	public void setApp(String app) {
		this.app = app;
	}


	public int getPage() {
		return page;
	}


	public void setPage(int page) {
		this.page = page;
	}


	public int getPageSize() {
		return pageSize;
	}


	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}


	@Override
	public String toString() {
		return ToStringUtil.fieldReflect(this);
	}
	
}
