package bma.siteone.netty.thrift.gate.impl;

import org.slf4j.Logger;

import bma.common.langutil.ai.stack.AIStackROOT;
import bma.common.netty.NettyUtil;
import bma.siteone.netty.thrift.gate.GateUtil;
import bma.siteone.netty.thrift.gate.MessageContext;

public class AIStackAgentROOT extends AIStackROOT<Boolean> {

	private Logger log;
	private MessageContext context;

	public AIStackAgentROOT(Logger log, MessageContext ctx) {
		super();
		this.log = log;
		this.context = ctx;
	}

	@Override
	public boolean end(Boolean result, Throwable t) {
		if (log.isDebugEnabled()) {
			if (t != null) {
				log.debug("process fail", t);
			} else {
				log.debug("process end - {}", result);
			}
		}
		if (t != null || !result) {
			GateUtil.responseError(context, t);
			NettyUtil.closeOnFlush(context.getNettyChannel());
		}
		return true;
	}
}
