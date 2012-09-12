package bma.siteone.clound.local;

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
import bma.siteone.clound.CloundApp;
import bma.siteone.clound.CloundException;
import bma.siteone.clound.CloundNode;
import bma.siteone.clound.CloundRequest;
import bma.siteone.clound.CloundResponse;
import bma.siteone.clound.CloundTrackable;
import bma.siteone.clound.impl.LogTrack;

public class LocalCloundNode implements CloundNode, CloundTrackable {

	protected String nodeId;
	protected String title;
	protected Map<String, CloundApp> appMap;

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

	public Map<String, CloundApp> getAppMap() {
		return appMap;
	}

	public void setAppMap(Map<String, CloundApp> appMap) {
		this.appMap = appMap;
	}

	public void setApps(List<CloundApp> list) {
		if (this.appMap == null)
			this.appMap = new HashMap<String, CloundApp>();
		for (CloundApp s : list) {
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
		desc.put("nodeId", getNodeId());
		desc.put("title", getTitle());
		desc.put("local", true);
		desc.put("appSize", this.appMap == null ? 0 : this.appMap.size());
		try {
			String r = JsonUtil.getDefaultMapper().writeValueAsString(desc);
			return stack.success(r);
		} catch (Exception e) {
			throw ExceptionUtil.throwRuntime(e);
		}
	}

	@Override
	public boolean cloundCall(AIStack<CloundResponse> stack,
			final CloundRequest req) {
		final String appId = req.getEntry().getAppId();
		return getApp(new AIStackConvert<CloundApp, CloundResponse>(stack) {
			@Override
			protected boolean convert(CloundApp result) {
				if (result == null) {
					super.failure(new CloundException("app[" + appId
							+ "] not exists"));
				}
				return LogTrack.call(result, delegate(), req,
						LocalCloundNode.this);
			}
		}, appId);
	}

	@Override
	public String getTrackString() {
		return "localNode(" + this.getNodeId() + ")";
	}

	@Override
	public boolean getApp(AIStack<CloundApp> stack, String id) {
		CloundApp r = null;
		if (this.appMap != null)
			r = this.appMap.get(id);
		return stack.success(r);
	}

	@Override
	public boolean listApp(AIStack<List<CloundApp>> stack) {
		List<CloundApp> r;
		if (this.appMap == null) {
			r = Collections.emptyList();
		} else {
			r = new ArrayList<CloundApp>(this.appMap.values());
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
