package bma.siteone.cloud;

import bma.common.langutil.ai.stack.AIStack;

public interface CloudRouter {

	public boolean getNode(AIStack<CloudNode> stack, String nodeId);

	public boolean getApp(AIStack<CloudApp> stack, String nodeId, String appId);

	public boolean getService(AIStack<CloudService> stack, String nodeId,
			String appId, String serviceId);

	public boolean getApi(AIStack<CloudApi> stack, String nodeId,
			String appId, String serviceId, String apiId);
}
