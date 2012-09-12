package bma.siteone.clound;

import bma.common.langutil.core.ToStringUtil;

public class CloundRequest {

	private CloundEntry entry;
	private String content;
	private CloundEntry callback;
	private boolean logtrack;

	public CloundEntry getEntry() {
		return entry;
	}

	public void setEntry(CloundEntry entry) {
		this.entry = entry;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String params) {
		this.content = params;
	}

	public CloundEntry getCallback() {
		return callback;
	}

	public void setCallback(CloundEntry callback) {
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
