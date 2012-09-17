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
import bma.siteone.cloud.CloudRequest;
import bma.siteone.cloud.CloudResponse;
import bma.siteone.cloud.CloudService;
import bma.siteone.cloud.CloudTrackable;
import bma.siteone.cloud.impl.LogTrack;

public class LocalCloudApp implements CloudApp, CloudTrackable {

	protected String appId;
	protected String appName;
	protected String title;
	private Map<String, String> appDesc;
	protected Map<String, CloudService> serviceMap;

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

	public Map<String, CloudService> getServiceMap() {
		return serviceMap;
	}

	public void setServiceMap(Map<String, CloudService> serviceMap) {
		this.serviceMap = serviceMap;
	}

	public void setServices(List<CloudService> list) {
		if (this.serviceMap == null)
			this.serviceMap = new HashMap<String, CloudService>();
		for (CloudService s : list) {
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
	public boolean cloudCall(AIStack<CloudResponse> stack,
			final CloudRequest req) {
		final String serviceId = req.getEntry().getServiceId();
		return getService(new AIStackConvert<CloudService, CloudResponse>(
				stack) {
			@Override
			protected boolean convert(CloudService result) {
				if (result == null) {
					super.failure(new CloudException("service[" + serviceId
							+ "] not exists"));
				}
				return LogTrack.call(result, delegate(), req,
						LocalCloudApp.this);
			}
		}, serviceId);
	}

	@Override
	public String getTrackString() {
		return "localApp(" + this.getAppId() + "," + this.getAppName() + ")";
	}

	@Override
	public boolean getService(AIStack<CloudService> stack, String id) {
		CloudService s = null;
		Map<String, CloudService> m = getServiceMap();
		if (m != null)
			s = m.get(id);
		return stack.success(s);
	}

	@Override
	public boolean listService(AIStack<List<CloudService>> stack) {
		List<CloudService> r = null;
		Map<String, CloudService> m = getServiceMap();
		if (m == null) {
			r = Collections.emptyList();
		} else {
			r = new ArrayList<CloudService>(m.values());
		}
		return stack.success(r);
	}

}
