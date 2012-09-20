package bma.siteone.netty.thrift.gate.impl;

import bma.common.langutil.ai.stack.AIStack;
import bma.siteone.netty.thrift.gate.MessageContext;
import bma.siteone.netty.thrift.gate.NTGAgent;
import bma.siteone.netty.thrift.gate.NTGAgentFactory;
import bma.siteone.netty.thrift.gate.NTGDispatcher;

public class NTGDispatcherSimple implements NTGDispatcher {

	private NTGAgentFactory agentFactory;

	public NTGAgentFactory getAgentFactory() {
		return agentFactory;
	}

	public void setAgentFactory(NTGAgentFactory agent) {
		this.agentFactory = agent;
	}

	@Override
	public boolean dispatch(AIStack<NTGAgent> stack, MessageContext ctx) {
		return stack.success(agentFactory.newAgent());
	}

}
