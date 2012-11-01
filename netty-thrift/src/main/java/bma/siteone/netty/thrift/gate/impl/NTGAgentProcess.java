package bma.siteone.netty.thrift.gate.impl;

import bma.common.langutil.ai.stack.AIStack;
import bma.siteone.netty.thrift.gate.MessageContext;
import bma.siteone.netty.thrift.gate.NTGAgent;

public interface NTGAgentProcess extends NTGAgent {

	public boolean process(AIStack<Boolean> stack, MessageContext ctx);
}
