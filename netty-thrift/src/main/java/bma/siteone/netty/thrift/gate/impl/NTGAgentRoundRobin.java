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

	public static class INFO {
		public int weight;
		public int totalWeight;
		public NTGAgentProcess agent;
	}

	protected int totalWeight;
	protected List<INFO> agentList;
	protected RoundRobinInteger roundRobin;

	public NTGAgentRoundRobin(RoundRobinInteger counter, List<INFO> agentList) {
		super();
		this.roundRobin = counter;
		setAgentList(agentList);
	}

	public List<INFO> getAgentList() {
		return agentList;
	}

	public void setAgentList(List<INFO> agentList) {
		this.agentList = new ArrayList<INFO>(agentList);
		totalWeight = 0;
		for (INFO info : this.agentList) {
			totalWeight += info.weight;
			info.totalWeight = totalWeight;
		}
	}

	public int posByWeight(int w) {
		int pos = 0;
		if (this.agentList != null) {
			for (INFO info : this.agentList) {
				if (w < info.totalWeight)
					return pos;
				pos++;
			}
		}
		return pos;
	}

	@Override
	public boolean process(AIStack<Boolean> root, final MessageContext ctx) {
		if (this.agentList == null || this.agentList.isEmpty()) {
			throw new RemoteBreakException("agentList is empty");
		}

		int weight = roundRobin.next(totalWeight);
		final int start = posByWeight(weight);

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
						log.debug("remote break, roundRobin next[{}] - {}",
								cur, t.getMessage());
					}
					NTGAgentProcess agent = agentList.get(cur).agent;
					return agent.process(this, ctx);
				}
				return super.failure(t);
			}
		};
		if (log.isDebugEnabled()) {
			log.debug("roundRobin[{}/{}]", start, weight);
		}
		NTGAgentProcess agent = agentList.get(start).agent;
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
			for (INFO info : agentList) {
				NTGAgentProcess p = info.agent;
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
