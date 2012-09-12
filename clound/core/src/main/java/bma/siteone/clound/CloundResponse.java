package bma.siteone.clound;

import java.util.ArrayList;
import java.util.List;

import bma.common.langutil.core.ToStringUtil;

public class CloundResponse {

	public static final int TYPE_ERROR = -1;
	public static final int TYPE_AICALL = 0;
	public static final int TYPE_DONE = 1;
	public static final int TYPE_REDIRECT = 2;

	private int type;
	private String content;
	private List<String> logTrack;

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public List<String> getLogTrack() {
		return logTrack;
	}

	public void setLogTrack(List<String> logTrack) {
		this.logTrack = logTrack;
	}

	public void insertLog(String log) {
		if (this.logTrack == null)
			this.logTrack = new ArrayList<String>();
		this.logTrack.add(0, log);
	}

	public void appendLog(String log) {
		if (this.logTrack == null)
			this.logTrack = new ArrayList<String>();
		this.logTrack.add(log);
	}

	@Override
	public String toString() {
		return ToStringUtil.fieldReflect(this);
	}
}
