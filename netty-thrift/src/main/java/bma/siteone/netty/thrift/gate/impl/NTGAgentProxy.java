package bma.siteone.netty.thrift.gate.impl;

import bma.common.langutil.ai.stack.AIStackROOT;
import bma.common.langutil.io.HostPort;
import bma.common.netty.NettyUtil;
import bma.common.netty.pool.NettyChannelPool;
import bma.siteone.netty.thrift.gate.NTGAgent;

public class NTGAgentProxy implements NTGAgent {

	final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(NTGAgentProxy.class);

	protected HostPort host;
	protected ProxyObject proxy;

	public NTGAgentProxy(HostPort host) {
		super();
		this.host = host;
	}

	public HostPort getHost() {
		return host;
	}

	public void setHost(HostPort host) {
		this.host = host;
	}

	@Override
	public boolean process(final NettyChannelPool pool,
			final SimpleMessageContext ctx) {

		AIStackROOT<Boolean> root = new AIStackROOT<Boolean>() {

			@Override
			public boolean end(Boolean result, Throwable t) {
				if (log.isDebugEnabled()) {
					if (t != null) {
						log.debug("process fail", t);
					} else {
						log.debug("process end - {}", result);
					}
				}
				if (proxy != null) {
					proxy.returnChannel();
				}
				if (t != null || !result) {
					NettyUtil.closeOnFlush(ctx.getNettyChannel());
				}
				return true;
			}
		};

		proxy = new ProxyObject(pool, host, ctx);
		return proxy.create(root);
	}

}
