package bma.siteone.netty.thrift.gate.impl;

import bma.siteone.netty.thrift.gate.MessageContext;
import bma.siteone.netty.thrift.gate.NTGAgent;
import bma.siteone.netty.thrift.remote.RemoteBreakException;

public class NTGAgentFailover implements NTGAgent {

	final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(NTGAgentFailover.class);

	protected NTGAgentProcess main;
	protected NTGAgentProcess second;

	public NTGAgentFailover(NTGAgentProcess main, NTGAgentProcess second) {
		super();
		this.main = main;
		this.second = second;
	}

	public NTGAgentProcess getMain() {
		return main;
	}

	public void setMain(NTGAgentProcess main) {
		this.main = main;
	}

	public NTGAgentProcess getSecond() {
		return second;
	}

	public void setSecond(NTGAgentProcess second) {
		this.second = second;
	}

	@Override
	public boolean process(final MessageContext ctx) {

		AIStackAgentROOT root = new AIStackAgentROOT(log, ctx) {
			public boolean failure(Throwable t) {
				if (t instanceof RemoteBreakException) {
					if (log.isDebugEnabled()) {
						log.debug("{} break, switch to {} - {}",
								new Object[] { main, second, t });
					}
					AIStackAgentROOT root = new AIStackAgentROOT(log, ctx);
					return second.process(root, ctx);
				}
				return super.failure(t);
			}
		};
		return main.process(root, ctx);

	}

	@Override
	public String toString() {
		return "Failover[main=" + main + ",second=" + second + "]";
	}

}
