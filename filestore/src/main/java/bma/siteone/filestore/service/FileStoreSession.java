package bma.siteone.filestore.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileStoreSession {

	private String sessionId;
	private String appId;
	private long lastRequestTime;
	private List<FileStoreRequest> requests = Collections
			.synchronizedList(new ArrayList<FileStoreRequest>());

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public long getLastRequestTime() {
		return lastRequestTime;
	}

	public void setLastRequestTime(long lastRequestTime) {
		this.lastRequestTime = lastRequestTime;
	}

	public List<FileStoreRequest> getRequests() {
		return requests;
	}

}
