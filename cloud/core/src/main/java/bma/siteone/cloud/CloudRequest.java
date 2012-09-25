package bma.siteone.cloud;

import java.util.Map;

import bma.common.langutil.core.ToStringUtil;

public class CloudRequest {

	private CloudEntry entry;
	private String contentType;
	private byte[] content;
	private Map<String, String> context;
	private CloudEntry callback;
	private boolean logtrack;

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public Map<String, String> getContext() {
		return context;
	}

	public void setContext(Map<String, String> context) {
		this.context = context;
	}

	public CloudEntry getEntry() {
		return entry;
	}

	public void setEntry(CloudEntry entry) {
		this.entry = entry;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	public CloudEntry getCallback() {
		return callback;
	}

	public void setCallback(CloudEntry callback) {
		this.callback = callback;
	}

	public boolean isLogtrack() {
		return logtrack;
	}

	public void setLogtrack(boolean logtrack) {
		this.logtrack = logtrack;
	}

	@Override
	public String toString() {
		return ToStringUtil.fieldReflect(this);
	}
}
