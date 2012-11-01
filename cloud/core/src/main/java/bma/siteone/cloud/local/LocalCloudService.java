package bma.siteone.cloud.local;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bma.common.json.JsonUtil;
import bma.common.langutil.ai.stack.AIStack;
import bma.common.langutil.ai.stack.AIStackConvert;
import bma.common.langutil.core.ExceptionUtil;
import bma.common.langutil.core.ValueUtil;
import bma.siteone.cloud.CloudApi;
import bma.siteone.cloud.CloudException;
import bma.siteone.cloud.CloudRequest;
import bma.siteone.cloud.CloudResponse;
import bma.siteone.cloud.CloudService;
import bma.siteone.cloud.CloudTrackable;
import bma.siteone.cloud.impl.LogTrack;

public abstract class LocalCloudService implements CloudService,
		CloudTrackable {

	protected String serviceId;
	protected String title;
	protected Map<String, CloudApi> apiMap;

	private Map<String, String> serviceDesc;

	public Map<String, String> getServiceDesc() {
		return serviceDesc;
	}

	public void setServiceDesc(Map<String, String> simpleDesc) {
		this.serviceDesc = simpleDesc;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public Map<String, CloudApi> getApiMap() {
		return apiMap;
	}

	public void setApiMap(Map<String, CloudApi> apiMap) {
		this.apiMap = apiMap;
	}

	public void setApis(List<CloudApi> list) {
		if (this.apiMap == null)
			this.apiMap = new HashMap<String, CloudApi>();
		for (CloudApi s : list) {
			String id = s.getApiId();
			if (ValueUtil.empty(id)) {
				throw new IllegalArgumentException(s + " apiId is empty");
			}
			this.apiMap.put(id, s);
		}
	}

	public Map<String, CloudApi> sureApis() {
		if (this.apiMap == null)
			this.apiMap = createApis();
		return this.apiMap;
	}

	public abstract Map<String, CloudApi> createApis();

	@Override
	public boolean getDesc(AIStack<String> stack) {
		Map<String, Object> desc = getDesc();
		if (desc == null) {
			desc = new HashMap<String, Object>();
		}
		desc.put("serviceId", getServiceId());
		if (ValueUtil.notEmpty(title)) {
			desc.put("title", title);
		}

		try {
			String r = JsonUtil.getDefaultMapper().writeValueAsString(desc);
			return stack.success(r);
		} catch (Exception e) {
			throw ExceptionUtil.throwRuntime(e);
		}
	}

	public Map<String, Object> getDesc() {
		Map<String, Object> r = new HashMap<String, Object>();
		if (this.serviceDesc != null) {
			for (Map.Entry<String, String> e : this.serviceDesc.entrySet()) {
				r.put(e.getKey(), e.getValue());
			}
		}
		return r;
	}

	@Override
	public boolean cloudCall(AIStack<CloudResponse> stack,
			final CloudRequest req) {
		final String apiId = req.getEntry().getApiId();
		return getApi(new AIStackConvert<CloudApi, CloudResponse>(stack) {
			@Override
			protected boolean convert(CloudApi result) {
				if (result == null) {
					super.failure(new CloudException("api[" + apiId
							+ "] not exists"));
				}
				return LogTrack.call(result, delegate(), req,
						LocalCloudService.this);
			}
		}, apiId);

	}

	@Override
	public boolean getApi(AIStack<CloudApi> stack, String id) {
		Map<String, CloudApi> apis = sureApis();
		return stack.success(apis.get(id));
	}

	@Override
	public boolean listApi(AIStack<List<CloudApi>> stack) {
		Map<String, CloudApi> apis = sureApis();
		return stack.success(new ArrayList<CloudApi>(apis.values()));
	}

}
