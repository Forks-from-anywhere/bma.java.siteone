package bma.siteone.cloud.impl;

import bma.common.json.JsonUtil;
import bma.common.langutil.core.ExceptionUtil;
import bma.common.langutil.core.ToStringUtil;
import bma.siteone.cloud.CloudEntry;

public class BaseCloudEntry implements CloudEntry {

	private String nodeId;
	private String appId;
	private String serviceId;
	private String apiId;

	@Override
	public String getNodeId() {
		return nodeId;
	}

	@Override
	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	@Override
	public String getAppId() {
		return appId;
	}

	@Override
	public void setAppId(String appId) {
		this.appId = appId;
	}

	@Override
	public String getServiceId() {
		return serviceId;
	}

	@Override
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	@Override
	public String getApiId() {
		return apiId;
	}

	@Override
	public void setApiId(String apiId) {
		this.apiId = apiId;
	}

	@Override
	public String toString() {
		return ToStringUtil.fieldReflect(this);
	}

	public String toEntryString() {
		try {
			return JsonUtil.getDefaultMapper().writeValueAsString(this);
		} catch (Exception e) {
			throw ExceptionUtil.throwRuntime(e);
		}
	}
}
