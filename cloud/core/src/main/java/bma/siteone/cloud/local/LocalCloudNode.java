package bma.siteone.cloud.local;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bma.common.json.JsonUtil;
import bma.common.langutil.ai.stack.AIStack;
import bma.common.langutil.ai.stack.AIStackConvert;
import bma.common.langutil.core.ExceptionUtil;
import bma.common.langutil.core.ValueUtil;
import bma.siteone.cloud.CloudApp;
import bma.siteone.cloud.CloudException;
import bma.siteone.cloud.CloudNode;
import bma.siteone.cloud.CloudRequest;
import bma.siteone.cloud.CloudResponse;
import bma.siteone.cloud.CloudTrackable;
import bma.siteone.cloud.impl.LogTrack;

public class LocalCloudNode implements CloudNode, CloudTrackable {

	protected String nodeId;
	protected String title;
	protected Map<String, CloudApp> appMap;
	private Map<String, String> nodeDesc;

	public Map<String, String> getNodeDesc() {
		return nodeDesc;
	}

	public void setNodeDesc(Map<String, String> nodeDesc) {
		this.nodeDesc = nodeDesc;
	}

	@Override
	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Map<String, CloudApp> getAppMap() {
		return appMap;
	}

	public void setAppMap(Map<String, CloudApp> appMap) {
		this.appMap = appMap;
	}

	public void setApps(List<CloudApp> list) {
		if (this.appMap == null)
			this.appMap = new HashMap<String, CloudApp>();
		for (CloudApp s : list) {
			String id = s.getAppId();
			if (ValueUtil.empty(id)) {
				throw new IllegalArgumentException(s + " appId is empty");
			}
			this.appMap.put(id, s);
		}
	}

	@Override
	public boolean getDesc(AIStack<String> stack) {
		Map<String, Object> desc = new HashMap<String, Object>();
		if (this.nodeDesc != null) {
			desc.putAll(this.nodeDesc);
		}
		desc.put("nodeId", getNodeId());
		desc.put("title", getTitle());		
		try {
			String r = JsonUtil.getDefaultMapper().writeValueAsString(desc);
			return stack.success(r);
		} catch (Exception e) {
			throw ExceptionUtil.throwRuntime(e);
		}
	}

	@Override
	public boolean cloudCall(AIStack<CloudResponse> stack,
			final CloudRequest req) {
		final String appId = req.getEntry().getAppId();
		return getApp(new AIStackConvert<CloudApp, CloudResponse>(stack) {
			@Override
			protected boolean convert(CloudApp result) {
				if (result == null) {
					super.failure(new CloudException("app[" + appId
							+ "] not exists"));
				}
				return LogTrack.call(result, delegate(), req,
						LocalCloudNode.this);
			}
		}, appId);
	}

	@Override
	public String getTrackString() {
		return "localNode(" + this.getNodeId() + ")";
	}

	@Override
	public boolean getApp(AIStack<CloudApp> stack, String id) {
		CloudApp r = null;
		if (this.appMap != null)
			r = this.appMap.get(id);
		return stack.success(r);
	}

	@Override
	public boolean listApp(AIStack<List<CloudApp>> stack) {
		List<CloudApp> r;
		if (this.appMap == null) {
			r = Collections.emptyList();
		} else {
			r = new ArrayList<CloudApp>(this.appMap.values());
		}
		return stack.success(r);
	}

	@Override
	public boolean createApp(AIStack<Boolean> stack, String id, String appName) {
		if (this.appMap != null) {
			return stack.success(this.appMap.get(id) != null);
		}
		return stack.success(false);
	}

	@Override
	public boolean closeApp(AIStack<Boolean> stack, String id) {
		return stack.success(false);
	}

}
