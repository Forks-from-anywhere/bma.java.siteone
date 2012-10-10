package bma.siteone.netty.thrift.gate.impl;

import bma.common.langutil.ai.stack.AIStack;
import bma.common.langutil.ai.stack.AIStackWrap;
import bma.siteone.netty.thrift.gate.MessageContext;
import bma.siteone.netty.thrift.remote.RemoteBreakException;

public class NTGAgentFailover implements NTGAgentProcess {

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
		AIStackAgentROOT root = new AIStackAgentROOT(log, ctx);
		return process(root, ctx);
	}

	@Override
	public boolean process(final AIStack<Boolean> root, final MessageContext ctx) {

		AIStackWrap<Boolean> stack = new AIStackWrap<Boolean>(root) {
			public boolean failure(Throwable t) {
				if (t instanceof RemoteBreakException) {
					if (log.isDebugEnabled()) {
						log.debug("{} break, switch to {} - {}", new Object[] {
								main, second, t });
					}
					return second.process(root, ctx);
				}
				return super.failure(t);
			}
		};
		return main.process(stack, ctx);

	}

	@Override
	public String toString() {
		return "Failover[main=" + main + ",second=" + second + "]";
	}

}
