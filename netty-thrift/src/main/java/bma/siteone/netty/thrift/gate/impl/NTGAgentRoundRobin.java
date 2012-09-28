package bma.siteone.netty.thrift.gate.impl;

import java.util.ArrayList;
import java.util.List;

import bma.common.langutil.ai.stack.AIStack;
import bma.common.langutil.ai.stack.AIStackAbstractDelegate;
import bma.common.langutil.core.RoundRobinInteger;
import bma.siteone.netty.thrift.gate.MessageContext;
import bma.siteone.netty.thrift.remote.RemoteBreakException;
import bma.siteone.netty.thrift.remote.RuntimeRemote;

public class NTGAgentRoundRobin implements NTGAgentProcess, RuntimeRemoteAware {

	final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(NTGAgentRoundRobin.class);

	protected List<NTGAgentProcess> agentList;
	protected RoundRobinInteger roundRobin;

	public NTGAgentRoundRobin(RoundRobinInteger counter,
			List<NTGAgentProcess> agentList) {
		super();
		this.roundRobin = counter;
		this.agentList = agentList;
	}

	public List<NTGAgentProcess> getAgentList() {
		return agentList;
	}

	public void setAgentList(List<NTGAgentProcess> agentList) {
		this.agentList = new ArrayList<NTGAgentProcess>(agentList);
	}

	@Override
	public boolean process(AIStack<Boolean> root, final MessageContext ctx) {
		if (this.agentList == null || this.agentList.isEmpty()) {
			throw new RemoteBreakException("agentList is empty");
		}

		final int start = roundRobin.next(agentList.size());

		AIStackAbstractDelegate<Boolean> callStack = new AIStackAbstractDelegate<Boolean>(
				root) {

			int cur = -1;

			public boolean failure(Throwable t) {
				if (t instanceof RemoteBreakException) {
					if (cur == -1) {
						cur = start;
					}
					cur = (cur + 1) % agentList.size();
					if (cur == start) {
						return super
								.failure(new RemoteBreakException("agents["
										+ cur + "/" + agentList.size()
										+ "] all break"));
					}
					if (log.isDebugEnabled()) {
						log.debug("remote break, roundRobin next[{}]",
								new Object[] { cur });
					}
					NTGAgentProcess agent = agentList.get(cur);
					return agent.process(this, ctx);
				}
				return super.failure(t);
			}
		};
		if (log.isDebugEnabled()) {
			log.debug("roundRobin[{}]", new Object[] { start });
		}
		NTGAgentProcess agent = agentList.get(start);
		return agent.process(callStack, ctx);
	}

	@Override
	public boolean process(MessageContext ctx) {
		AIStackAgentROOT root = new AIStackAgentROOT(log, ctx);
		return process(root, ctx);
	}

	@Override
	public void setRuntimeRemote(RuntimeRemote rr) {
		if (agentList != null) {
			for (NTGAgentProcess p : agentList) {
				if (p instanceof RuntimeRemoteAware) {
					RuntimeRemoteAware a = (RuntimeRemoteAware) p;
					a.setRuntimeRemote(rr);
				}
			}
		}
	}

	@Override
	public String toString() {
		return "RoundRobin[agents="
				+ (agentList == null ? 0 : agentList.size()) + ",round="
				+ roundRobin.getCounter() + "]";
	}

}
