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
import bma.siteone.clound.CloundRequest;
import bma.siteone.clound.CloundResponse;
import bma.siteone.clound.CloundService;
import bma.siteone.clound.CloundTrackable;
import bma.siteone.clound.impl.LogTrack;

public class LocalCloundApp implements CloundApp, CloundTrackable {

	protected String appId;
	protected String appName;
	protected String title;
	private Map<String, String> appDesc;
	protected Map<String, CloundService> serviceMap;

	public Map<String, String> getAppDesc() {
		return appDesc;
	}

	public void setAppDesc(Map<String, String> simpleDesc) {
		this.appDesc = simpleDesc;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Map<String, CloundService> getServiceMap() {
		return serviceMap;
	}

	public void setServiceMap(Map<String, CloundService> serviceMap) {
		this.serviceMap = serviceMap;
	}

	public void setServices(List<CloundService> list) {
		if (this.serviceMap == null)
			this.serviceMap = new HashMap<String, CloundService>();
		for (CloundService s : list) {
			String sid = s.getServiceId();
			if (ValueUtil.empty(sid)) {
				throw new IllegalArgumentException(s + " serviceId is empty");
			}
			this.serviceMap.put(sid, s);
		}
	}

	@Override
	public boolean getDesc(AIStack<String> stack) {
		Map<String, Object> desc = new HashMap<String, Object>();
		if (this.appDesc != null) {
			desc.putAll(this.appDesc);
		}
		desc.put("appId", getAppId());
		desc.put("appName", getAppName());
		desc.put("title", getTitle());	
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
		final String serviceId = req.getEntry().getServiceId();
		return getService(new AIStackConvert<CloundService, CloundResponse>(
				stack) {
			@Override
			protected boolean convert(CloundService result) {
				if (result == null) {
					super.failure(new CloundException("service[" + serviceId
							+ "] not exists"));
				}
				return LogTrack.call(result, delegate(), req,
						LocalCloundApp.this);
			}
		}, serviceId);
	}

	@Override
	public String getTrackString() {
		return "localApp(" + this.getAppId() + "," + this.getAppName() + ")";
	}

	@Override
	public boolean getService(AIStack<CloundService> stack, String id) {
		CloundService s = null;
		if (this.serviceMap != null)
			s = this.serviceMap.get(id);
		return stack.success(s);
	}

	@Override
	public boolean listService(AIStack<List<CloundService>> stack) {
		List<CloundService> r = null;
		if (this.serviceMap == null) {
			r = Collections.emptyList();
		} else {
			r = new ArrayList<CloundService>(this.serviceMap.values());
		}
		return stack.success(r);
	}

}
