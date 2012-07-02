package bma.siteone.netty.thrift.gate;

import java.util.concurrent.Executor;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;

import bma.common.langutil.core.ExceptionUtil;
import bma.common.langutil.core.SizeUtil;
import bma.common.langutil.core.SizeUtil.Unit;
import bma.common.netty.NettyServer;
import bma.common.netty.handler.ChannelHandlerExceptionClose;
import bma.siteone.netty.thrift.core.NCHFramed;
import bma.siteone.netty.thrift.core.TNettyServerFramedTransport;

public class NettyThriftGate extends NettyServer {

	final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(NettyThriftGate.class);

	protected Executor executor;
	protected int maxLength;

	public NettyThriftGate() {
		super();
	}

	public Executor getExecutor() {
		return executor;
	}

	public void setExecutor(Executor executor) {
		this.executor = executor;
	}

	public int getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

	public void setFrameSize(String sz) {
		try {
			this.maxLength = (int) SizeUtil.convert(sz, 1024, Unit.B);
		} catch (Exception e) {
			throw ExceptionUtil.throwRuntime(e);
		}
	}

	@Override
	protected void beforeBuildPipeline(ChannelPipeline pipeline) {
		super.beforeBuildPipeline(pipeline);
		pipeline.addLast("framed", new NCHFramed(maxLength));
		pipeline.addLast("transport", new OneToOneDecoder() {
			@Override
			protected Object decode(ChannelHandlerContext ctx, Channel channel,
					Object msg) throws Exception {
				if (msg instanceof ChannelBuffer) {
					ChannelBuffer cb = (ChannelBuffer) msg;
					TNettyServerFramedTransport t = new TNettyServerFramedTransport(
							ctx.getChannel(), cb, maxLength);
					return t;
				}
				return msg;
			}
		});
		// pipeline.addLast("request", new NCHThriftRequest(processor));
		pipeline.addLast("error", new ChannelHandlerExceptionClose() {
			@Override
			protected void logException(ChannelHandlerContext ctx,
					ExceptionEvent e) {
				if (log.isWarnEnabled()) {
					Throwable t = ExceptionUtil.cause(e.getCause());
					log.warn("{} error {}/{}", new Object[] {
							ctx.getChannel().getRemoteAddress(),
							t.getClass().getName(), t.getMessage() });
				}
			}
		});
	}
}
