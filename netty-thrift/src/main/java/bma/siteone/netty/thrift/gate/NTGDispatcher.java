package bma.siteone.netty.thrift.gate;

import bma.common.langutil.ai.stack.AIStack;

public interface NTGDispatcher {

	public boolean dispatch(AIStack<NTGAgent> agent, MessageContext ctx);
}
