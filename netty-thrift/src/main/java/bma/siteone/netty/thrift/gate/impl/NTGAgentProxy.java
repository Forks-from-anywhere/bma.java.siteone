package bma.siteone.netty.thrift.gate.impl;

import bma.common.langutil.ai.stack.AIStack;
import bma.common.langutil.ai.stack.AIStackAbstractDelegate;
import bma.common.langutil.io.HostPort;
import bma.common.netty.pool.NettyChannelPool;
import bma.siteone.netty.thrift.gate.MessageContext;
import bma.siteone.netty.thrift.gate.NTGAgent;
import bma.siteone.netty.thrift.remote.RuntimeRemote;

public class NTGAgentProxy extends ProxyObject implements NTGAgent,
		NTGAgentProcess {

	final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(NTGAgentProxy.class);

	public NTGAgentProxy(NettyChannelPool pool, HostPort host, RuntimeRemote rr) {
		super(pool, host);
		setRuntimeRemote(rr);
	}

	@Override
	public boolean process(final MessageContext ctx) {
		AIStackAgentROOT root = new AIStackAgentROOT(log, ctx);
		return process(root, ctx);
	}

	@Override
	public boolean process(AIStack<Boolean> stack, MessageContext ctx) {
		
		setContext(ctx);
		
		AIStackAbstractDelegate<Boolean> callStack = new AIStackAbstractDelegate<Boolean>(stack) {

			@Override
			protected boolean done() {
				returnChannel();
				return super.done();
			}
		};
		return create(callStack);
	}

	@Override
	public String toString() {
		return "Proxy[" + super.host + "]";
	}
}
