package bma.siteone.cloud;

import bma.common.langutil.ai.stack.AIStack;

public interface CloudElement {

	public boolean getDesc(AIStack<String> stack);

	public boolean cloudCall(AIStack<CloudResponse> stack, CloudRequest req);
}
