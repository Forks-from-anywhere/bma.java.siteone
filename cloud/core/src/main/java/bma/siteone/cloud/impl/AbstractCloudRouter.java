package bma.siteone.cloud.impl;

import bma.common.langutil.ai.stack.AIStack;
import bma.common.langutil.ai.stack.AIStackConvert;
import bma.siteone.cloud.CloudApi;
import bma.siteone.cloud.CloudApp;
import bma.siteone.cloud.CloudNode;
import bma.siteone.cloud.CloudRouter;
import bma.siteone.cloud.CloudService;

public abstract class AbstractCloudRouter implements CloudRouter {

	@Override
	public boolean getApp(AIStack<CloudApp> stack, String nodeId,
			final String appId) {
		return getNode(new AIStackConvert<CloudNode, CloudApp>(stack) {
			@Override
			protected boolean convert(CloudNode result) {
				if (result == null)
					return super.successConvert(null);
				return result.getApp(super.delegate(), appId);
			}
		}, nodeId);

	}

	@Override
	public boolean getService(AIStack<CloudService> stack, String nodeId,
			String appId, final String serviceId) {
		return getApp(new AIStackConvert<CloudApp, CloudService>(stack) {
			@Override
			protected boolean convert(CloudApp result) {
				if (result == null)
					return super.successConvert(null);
				return result.getService(super.delegate(), serviceId);
			}
		}, nodeId, appId);
	}

	@Override
	public boolean getApi(AIStack<CloudApi> stack, String nodeId,
			String appId, String serviceId, final String apiId) {
		return getService(new AIStackConvert<CloudService, CloudApi>(stack) {
			@Override
			protected boolean convert(CloudService result) {
				if (result == null)
					return super.successConvert(null);
				return result.getApi(super.delegate(), apiId);
			}
		}, nodeId, appId, serviceId);
	}

}
