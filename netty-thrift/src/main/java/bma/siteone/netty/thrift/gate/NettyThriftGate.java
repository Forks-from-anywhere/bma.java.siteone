package bma.siteone.netty.thrift.gate;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import bma.common.langutil.ai.stack.AIStack;
import bma.common.langutil.ai.stack.AIStackAbstract;
import bma.common.langutil.core.ExceptionUtil;
import bma.common.langutil.core.SizeUtil;
import bma.common.langutil.core.SizeUtil.Unit;
import bma.common.netty.NettyServer;
import bma.common.netty.NettyUtil;
import bma.common.netty.handler.ChannelHandlerExceptionClose;
import bma.common.netty.pool.NettyChannelPool;
import bma.siteone.netty.thrift.core.NCHFramed;
import bma.siteone.netty.thrift.gate.impl.SimpleMessageContext;

public class NettyThriftGate extends NettyServer {

	final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(NettyThriftGate.class);

	protected int maxLength;
	private NTGDispatcher dispatcher;
	private NettyChannelPool pool;

	public NettyThriftGate() {
		super();
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

	public NTGDispatcher getDispatcher() {
		return dispatcher;
	}

	public void setDispatcher(NTGDispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}

	public NettyChannelPool getPool() {
		return pool;
	}

	public void setPool(NettyChannelPool pool) {
		this.pool = pool;
	}

	@Override
	protected void beforeBuildPipeline(ChannelPipeline pipeline) {
		super.beforeBuildPipeline(pipeline);
		pipeline.addLast("framed", new NCHFramed(maxLength));
		pipeline.addLast("gate", new GATE());
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

	private class GATE extends SimpleChannelUpstreamHandler {

		@Override
		public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
				throws Exception {
			Object v = e.getMessage();
			if (v instanceof ChannelBuffer) {
				ChannelBuffer buf = (ChannelBuffer) v;
				final SimpleMessageContext mctx = new SimpleMessageContext();
				mctx.setMessage(buf);
				mctx.setNettyChannel(e.getChannel());
				AIStackAbstract<NTGAgent> stack = new AIStackAbstract<NTGAgent>() {

					@Override
					public boolean success(NTGAgent result) {
						if (result != null) {
							return result.process(pool, mctx);
						}
						if (log.isDebugEnabled()) {
							log.debug("dispatch ["
									+ mctx.getNettyChannel().getRemoteAddress()
									+ "] null");
						}
						return true;
					}

					@Override
					public AIStack<?> getParent() {
						return null;
					}

					@Override
					public boolean failure(Throwable t) {
						if (log.isWarnEnabled()) {
							log.warn("dispatch ["
									+ mctx.getNettyChannel().getRemoteAddress()
									+ "] fail", t);
						}
						NettyUtil.closeOnFlush(mctx.getNettyChannel());
						return true;
					}
				};
				dispatcher.dispatch(stack, mctx);
				return;
			}
			super.messageReceived(ctx, e);
		}
	}
}
