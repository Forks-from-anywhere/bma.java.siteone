package bma.siteone.netty.thrift.gate.impl;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import bma.common.langutil.ai.stack.AIStack;
import bma.common.langutil.io.HostPort;
import bma.common.netty.pool.NettyChannelPool;
import bma.siteone.netty.thrift.core.NCHFramed;

public class ProxyObjectSocket extends ProxyObjectBase {

	public ProxyObjectSocket(NettyChannelPool pool, HostPort host) {
		super(pool, host);
	}

	@Override
	protected void removeChannelHandler(Channel ch) {
		remove(ch, "netty_thrift_gate_proxy");
	}

	@Override
	protected void initChannelHandler(Channel ch, AIStack<Boolean> stack) {
		ch.getPipeline().addAfter("handlerPlaceholder",
				"netty_thrift_gate_proxy", new ProxyHandler(stack) {

					@Override
					public void messageReceived(ChannelHandlerContext ctx,
							MessageEvent e) throws Exception {
						if (e.getMessage() instanceof ChannelBuffer) {
							ChannelBuffer buf = (ChannelBuffer) e.getMessage();
							processContent(buf);
							return;
						}
						super.messageReceived(ctx, e);
					}
				});
	}

	@Override
	protected void writeMessage(Channel ch, ChannelBuffer data) {
		byte[] buf = new byte[4];
		int size = data.readableBytes();
		if (log.isDebugEnabled()) {
			log.debug("peer2remote {}", size);
		}
		NCHFramed.encodeFrameSize(size, buf);
		ch.write(ChannelBuffers.wrappedBuffer(buf));
		ch.write(data);
	}

	@Override
	public String toString() {
		return "Proxy[" + this.host + "]";
	}
}
