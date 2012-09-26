package bma.siteone.netty.thrift.client;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TTransport;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import bma.common.langutil.ai.AIUtil;
import bma.common.langutil.ai.stack.AIStack;
import bma.common.langutil.ai.stack.AIStackSimple;
import bma.common.langutil.core.ExceptionUtil;
import bma.common.langutil.log.LogPrinter.LEVEL;
import bma.common.langutil.pipeline.CommonPipelineBuilder;
import bma.common.netty.NettyChannelPipeline;
import bma.common.netty.client.NettyClientBootstrap;
import bma.common.netty.handler.ChannelHandlerExceptionClose;
import bma.common.netty.handler.ChannelHandlerLog;
import bma.common.netty.handler.ChannelHandlerLog.TYPE;
import bma.common.netty.handler.ChannelHandlerPlaceholder;
import bma.common.netty.handler.ChannelHandlerShowConnect;
import bma.common.thrift.ThriftClient;
import bma.common.thrift.ThriftClientFactory;
import bma.common.thrift.ThriftClientFactoryConfig;
import bma.common.thrift.ai.AIThriftClientFactory;
import bma.siteone.netty.thrift.core.TNettyChannelTransport;

public class AIThriftClientFactoryNetty extends ThriftClientFactoryConfig
		implements ThriftClientFactory, AIThriftClientFactory {

	final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(AIThriftClientFactoryNetty.class);

	protected NettyClientBootstrap bootstrap;
	protected CommonPipelineBuilder<ChannelHandler> pipelineBuilder;
	protected int traceBufferSize = 0;

	public NettyClientBootstrap getBootstrap() {
		return bootstrap;
	}

	public void setBootstrap(NettyClientBootstrap bootstrap) {
		this.bootstrap = bootstrap;
	}

	public CommonPipelineBuilder<ChannelHandler> getPipelineBuilder() {
		return pipelineBuilder;
	}

	public void setPipelineBuilder(
			CommonPipelineBuilder<ChannelHandler> pipelineBuilder) {
		this.pipelineBuilder = pipelineBuilder;
	}

	public int getTraceBufferSize() {
		return traceBufferSize;
	}

	public void setTraceBufferSize(int traceBufferSize) {
		this.traceBufferSize = traceBufferSize;
	}

	@Override
	public ThriftClient createThriftClient() throws TException {
		AIStackSimple<ThriftClient> stack = new AIStackSimple<ThriftClient>(
				null);
		createThriftClient(stack);
		try {
			return stack.get();
		} catch (Exception e) {
			throw ExceptionUtil.throwRuntime(e);
		}
	}

	protected void beforeBuildPipeline(ChannelPipeline pipeline) {

	}

	protected void afterBuildPipeline(ChannelPipeline pipeline) {

	}

	@Override
	public boolean createThriftClient(final AIStack<ThriftClient> stack)
			throws TException {
		if (TYPE_HTTP.equals(type)) {
			return false;
		} else {
			return createSocketThriftClient(stack);
		}
	}

	public boolean createSocketThriftClient(final AIStack<ThriftClient> stack)
			throws TException {

		ChannelPipelineFactory fac = new ChannelPipelineFactory() {

			@Override
			public ChannelPipeline getPipeline() throws Exception {
				ChannelPipeline pipeline = Channels.pipeline();
				beforeBuildPipeline(pipeline);
				if (traceBufferSize > 0) {
					pipeline.addLast("uplog", new ChannelHandlerLog(log,
							LEVEL.DEBUG, TYPE.UPSTREAM, traceBufferSize));
				} else {
					pipeline.addLast("showConnect",
							new ChannelHandlerShowConnect(log, LEVEL.DEBUG));
				}
				pipeline.addLast(TNettyChannelTransport.PIPELINE_NAME,
						new ChannelHandlerPlaceholder());
				ChannelHandler sch = new SimpleChannelUpstreamHandler() {
					@Override
					public void channelConnected(ChannelHandlerContext ctx,
							ChannelStateEvent e) throws Exception {
						try {
							Channel ch = ctx.getChannel();
							TNettyChannelTransport transport = null;
							if (frameMaxLength > 0) {
								transport = new TNettyChannelFramedTransport(
										ch, frameMaxLength);
							} else {
								transport = new TNettyChannelTransport(ch);
							}
							transport.bindHandler();
							TProtocol pro = createProtocol(transport);
							AIUtil.safeSuccess(stack, new ThriftClient(
									transport, pro));
						} catch (Exception err) {
							AIUtil.safeFailure(stack, err);
						}
						super.channelConnected(ctx, e);
					}
				};
				pipeline.addLast("startu[", sch);
				if (traceBufferSize > 0) {
					pipeline.addLast("downlog", new ChannelHandlerLog(log,
							LEVEL.DEBUG, TYPE.DOWNSTREAM, traceBufferSize));
				}
				pipeline.addLast("error", new ChannelHandlerExceptionClose() {
					@Override
					protected void logException(ChannelHandlerContext ctx,
							ExceptionEvent e) {
						if (log.isDebugEnabled()) {
							Throwable t = ExceptionUtil.cause(e.getCause());
							log.debug("{} error {}/{}", new Object[] {
									ctx.getChannel().getRemoteAddress(),
									t.getClass().getName(), t.getMessage() });
						}
					}
				});

				if (pipelineBuilder != null) {
					pipelineBuilder.buildPipeline(new NettyChannelPipeline(
							pipeline));
				}
				afterBuildPipeline(pipeline);
				return pipeline;
			}
		};

		bootstrap.connect(host.createInetSocketAddress(), null, fac)
				.addListener(new ChannelFutureListener() {

					@Override
					public void operationComplete(ChannelFuture cf)
							throws Exception {
						if (cf.isSuccess()) {

						} else {
							AIUtil.safeFailure(stack, cf.getCause());
						}
					}
				});
		return false;
	}

}
