package bma.siteone.clound;

import bma.common.langutil.ai.stack.AIStack;

public interface CloundRouter {

	public boolean getNode(AIStack<CloundNode> stack, String nodeId);

	public boolean getApp(AIStack<CloundApp> stack, String nodeId, String appId);

	public boolean getService(AIStack<CloundService> stack, String nodeId,
			String appId, String serviceId);

	public boolean getApi(AIStack<CloundApi> stack, String nodeId,
			String appId, String serviceId, String apiId);
}
