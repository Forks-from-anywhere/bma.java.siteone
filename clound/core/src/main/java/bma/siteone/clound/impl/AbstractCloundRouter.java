package bma.siteone.clound.impl;

import bma.common.langutil.ai.stack.AIStack;
import bma.common.langutil.ai.stack.AIStackConvert;
import bma.siteone.clound.CloundApi;
import bma.siteone.clound.CloundApp;
import bma.siteone.clound.CloundNode;
import bma.siteone.clound.CloundRouter;
import bma.siteone.clound.CloundService;

public abstract class AbstractCloundRouter implements CloundRouter {

	@Override
	public boolean getApp(AIStack<CloundApp> stack, String nodeId,
			final String appId) {
		return getNode(new AIStackConvert<CloundNode, CloundApp>(stack) {
			@Override
			protected boolean convert(CloundNode result) {
				if (result == null)
					return super.successConvert(null);
				return result.getApp(super.delegate(), appId);
			}
		}, nodeId);

	}

	@Override
	public boolean getService(AIStack<CloundService> stack, String nodeId,
			String appId, final String serviceId) {
		return getApp(new AIStackConvert<CloundApp, CloundService>(stack) {
			@Override
			protected boolean convert(CloundApp result) {
				if (result == null)
					return super.successConvert(null);
				return result.getService(super.delegate(), serviceId);
			}
		}, nodeId, appId);
	}

	@Override
	public boolean getApi(AIStack<CloundApi> stack, String nodeId,
			String appId, String serviceId, final String apiId) {
		return getService(new AIStackConvert<CloundService, CloundApi>(stack) {
			@Override
			protected boolean convert(CloundService result) {
				if (result == null)
					return super.successConvert(null);
				return result.getApi(super.delegate(), apiId);
			}
		}, nodeId, appId, serviceId);
	}

}
