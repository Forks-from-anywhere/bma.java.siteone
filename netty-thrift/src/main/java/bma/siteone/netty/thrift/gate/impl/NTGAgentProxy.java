package bma.siteone.netty.thrift.gate.impl;

import java.net.URL;

import bma.common.langutil.ai.stack.AIStack;
import bma.common.langutil.ai.stack.AIStackAbstractDelegate;
import bma.common.langutil.io.HostPort;
import bma.common.netty.pool.NettyChannelPool;
import bma.siteone.netty.thrift.gate.MessageContext;
import bma.siteone.netty.thrift.gate.NTGAgent;
import bma.siteone.netty.thrift.remote.RuntimeRemote;

public class NTGAgentProxy implements NTGAgent, NTGAgentProcess,
		RuntimeRemoteAware {

	final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(NTGAgentProxy.class);

	protected ProxyObjectBase proxy;
	protected boolean checkRuntimeRemote;

	public NTGAgentProxy(NettyChannelPool pool, HostPort host, boolean crr,
			RuntimeRemote rr) {
		super();
		proxy = new ProxyObjectSocket(pool, host);
		checkRuntimeRemote = crr;
		if (crr) {
			proxy.setRuntimeRemote(rr);
		}
	}

	public NTGAgentProxy(NettyChannelPool pool, URL url) {
		super();
		proxy = new ProxyObjectHttp(pool, url);
	}

	public boolean isSocket() {
		return proxy instanceof ProxyObjectSocket;
	}

	public boolean isCheckRuntimeRemote() {
		return checkRuntimeRemote;
	}

	public void setCheckRuntimeRemote(boolean checkRuntimeRemote) {
		this.checkRuntimeRemote = checkRuntimeRemote;
	}

	@Override
	public void setRuntimeRemote(RuntimeRemote rr) {
		if (checkRuntimeRemote) {
			proxy.setRuntimeRemote(rr);
		}
	}

	@Override
	public boolean process(final MessageContext ctx) {
		AIStackAgentROOT root = new AIStackAgentROOT(log, ctx);
		return process(root, ctx);
	}

	@Override
	public boolean process(AIStack<Boolean> stack, MessageContext ctx) {

		proxy.setContext(ctx);

		AIStackAbstractDelegate<Boolean> callStack = new AIStackAbstractDelegate<Boolean>(
				stack) {

			@Override
			protected boolean done() {
				proxy.returnChannel();
				return super.done();
			}
		};
		return proxy.create(callStack);
	}

	@Override
	public String toString() {
		return proxy.toString();
	}

	public void setVHost(String vhost) {
		if (proxy instanceof ProxyObjectHttp) {
			ProxyObjectHttp o = (ProxyObjectHttp) proxy;
			o.setVhost(vhost);
		}
	}
}
