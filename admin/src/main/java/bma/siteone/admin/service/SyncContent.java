package bma.siteone.admin.service;

import java.io.Serializable;

import bma.common.langutil.core.ToStringUtil;

public class SyncContent implements Serializable{

	private static final long serialVersionUID = 1L;
	
	/**
	 * 
		struct TSync {
		  1: i32 opCode,
		  2: string content,
		}
	 */
	private int opCode;
	
	private String content;
	
	public int getOpCode() {
		return opCode;
	}

	public void setOpCode(int opCode) {
		this.opCode = opCode;
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
