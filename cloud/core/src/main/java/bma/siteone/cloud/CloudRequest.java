package bma.siteone.cloud;

import bma.common.langutil.core.ToStringUtil;

public class CloudRequest {

	private CloudEntry entry;
	private String content;
	private CloudEntry callback;
	private boolean logtrack;

	public CloudEntry getEntry() {
		return entry;
	}

	public void setEntry(CloudEntry entry) {
		this.entry = entry;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String params) {
		this.content = params;
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
