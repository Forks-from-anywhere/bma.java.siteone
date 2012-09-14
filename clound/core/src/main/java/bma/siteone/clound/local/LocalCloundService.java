package bma.siteone.clound.local;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bma.common.json.JsonUtil;
import bma.common.langutil.ai.stack.AIStack;
import bma.common.langutil.ai.stack.AIStackConvert;
import bma.common.langutil.core.ExceptionUtil;
import bma.common.langutil.core.ValueUtil;
import bma.siteone.clound.CloundApi;
import bma.siteone.clound.CloundException;
import bma.siteone.clound.CloundRequest;
import bma.siteone.clound.CloundResponse;
import bma.siteone.clound.CloundService;
import bma.siteone.clound.CloundTrackable;
import bma.siteone.clound.impl.LogTrack;

public abstract class LocalCloundService implements CloundService,
		CloundTrackable {

	protected String serviceId;
	protected String title;
	protected Map<String, CloundApi> apiMap;

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

	public Map<String, CloundApi> getApiMap() {
		return apiMap;
	}

	public void setApiMap(Map<String, CloundApi> apiMap) {
		this.apiMap = apiMap;
	}

	public Map<String, CloundApi> sureApis() {
		if (this.apiMap == null)
			this.apiMap = createApis();
		return this.apiMap;
	}

	public abstract Map<String, CloundApi> createApis();

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
	public boolean cloundCall(AIStack<CloundResponse> stack,
			final CloundRequest req) {
		final String apiId = req.getEntry().getApiId();
		return getApi(new AIStackConvert<CloundApi, CloundResponse>(stack) {
			@Override
			protected boolean convert(CloundApi result) {
				if (result == null) {
					super.failure(new CloundException("api[" + apiId
							+ "] not exists"));
				}
				return LogTrack.call(result, delegate(), req,
						LocalCloundService.this);
			}
		}, apiId);

	}

	@Override
	public boolean getApi(AIStack<CloundApi> stack, String id) {
		Map<String, CloundApi> apis = sureApis();
		return stack.success(apis.get(id));
	}

	@Override
	public boolean listApi(AIStack<List<CloundApi>> stack) {
		Map<String, CloundApi> apis = sureApis();
		return stack.success(new ArrayList<CloundApi>(apis.values()));
	}

}
