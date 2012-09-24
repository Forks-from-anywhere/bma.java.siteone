package bma.siteone.netty.thrift.gate.impl;

import bma.common.langutil.ai.stack.AIStack;
import bma.siteone.netty.thrift.gate.MessageContext;

public interface NTGAgentProcess {

	public boolean process(AIStack<Boolean> stack, MessageContext ctx);
}
