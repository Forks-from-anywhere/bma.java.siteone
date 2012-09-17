package bma.siteone.cloud.local;

import java.util.HashMap;
import java.util.Map;

import bma.common.json.JsonUtil;
import bma.common.langutil.ai.stack.AIStack;
import bma.common.langutil.core.ValueUtil;
import bma.siteone.cloud.CloudApi;

public abstract class LocalCloudApi implements CloudApi {

	private String apiId;
	private String title;
	private Map<String, String> apiDesc;

	public LocalCloudApi() {
		super();
	}

	public LocalCloudApi(String apiId) {
		super();
		this.apiId = apiId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String getApiId() {
		return apiId;
	}

	public void setApiId(String apiId) {
		this.apiId = apiId;
	}

	public Map<String, String> getApiDesc() {
		return apiDesc;
	}

	public void setApiDesc(Map<String, String> simpleDesc) {
		this.apiDesc = simpleDesc;
	}

	public Map<String, Object> getDesc() {
		Map<String, Object> r = new HashMap<String, Object>();
		if (this.apiDesc != null) {
			for (Map.Entry<String, String> e : this.apiDesc.entrySet()) {
				r.put(e.getKey(), e.getValue());
			}
		}
		return r;
	}

	@Override
	public boolean getDesc(AIStack<String> stack) {
		Map<String, Object> desc = getDesc();
		if (desc == null) {
			desc = new HashMap<String, Object>();
		}
		desc.put("apiId", apiId);
		if (ValueUtil.notEmpty(title)) {
			desc.put("title", title);
		}
		try {
			return stack.success(JsonUtil.getDefaultMapper()
					.writeValueAsString(desc));
		} catch (Exception e) {
			return stack.failure(e);
		}
	}

}
