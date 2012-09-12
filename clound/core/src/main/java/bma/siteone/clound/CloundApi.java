package bma.siteone.clound;

import bma.common.langutil.ai.stack.AIStack;

public interface CloundApi {
	
	public boolean getDesc(AIStack<String> stack);

	public boolean cloundCall(AIStack<CloundResponse> stack, CloundRequest req);
}
