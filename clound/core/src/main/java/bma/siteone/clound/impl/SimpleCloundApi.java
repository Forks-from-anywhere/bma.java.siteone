package bma.siteone.clound.impl;

import java.util.HashMap;
import java.util.Map;

import bma.common.json.JsonUtil;
import bma.common.langutil.ai.stack.AIStack;
import bma.siteone.clound.CloundApi;

public abstract class SimpleCloundApi implements CloundApi {

	private String apiId;

	public SimpleCloundApi() {
		super();
	}

	public SimpleCloundApi(String apiId) {
		super();
		this.apiId = apiId;
	}

	public String getApiId() {
		return apiId;
	}

	public void setApiId(String apiId) {
		this.apiId = apiId;
	}

	@Override
	public boolean getDesc(AIStack<String> stack) {
		Map<String, Object> desc = getDesc();
		if (desc == null) {
			desc = new HashMap<String, Object>();
		}
		desc.put("apiId", apiId);
		try {
			return stack.success(JsonUtil.getDefaultMapper()
					.writeValueAsString(desc));
		} catch (Exception e) {
			return stack.failure(e);
		}
	}

	public abstract Map<String, Object> getDesc();

}
