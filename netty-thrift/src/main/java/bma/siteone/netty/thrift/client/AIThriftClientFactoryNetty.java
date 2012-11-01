package bma.siteone.netty.thrift.client;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.thrift.TException;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpClientCodec;
import org.jboss.netty.handler.codec.http.HttpContentDecompressor;

import bma.common.langutil.ai.stack.AIStack;
import bma.common.langutil.ai.stack.AIStackStep;
import bma.common.langutil.core.ExceptionUtil;
import bma.common.langutil.io.HostPort;
import bma.common.langutil.log.LogPrinter.LEVEL;
import bma.common.langutil.pipeline.CommonPipelineBuilder;
import bma.common.netty.NettyChannelPipeline;
import bma.common.netty.client.NettyClient;
import bma.common.netty.client.NettyClientBootstrap;
import bma.common.netty.handler.ChannelHandlerExceptionClose;
import bma.common.netty.handler.ChannelHandlerLog;
import bma.common.netty.handler.ChannelHandlerLog.TYPE;
import bma.common.netty.handler.ChannelHandlerShowConnect;
import bma.common.thrift.ThriftClientConfig;
import bma.common.thrift.ai.AIThriftClient;
import bma.common.thrift.ai.AIThriftClientFactory;
import bma.siteone.netty.thrift.core.NCHFramed;

public class AIThriftClientFactoryNetty implements AIThriftClientFactory {

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

	protected void beforeBuildPipeline(ChannelPipeline pipeline) {

	}

	protected void afterBuildPipeline(ChannelPipeline pipeline) {

	}

	@Override
	public boolean createThriftClient(AIStack<AIThriftClient> stack,
			ThriftClientConfig cfg) throws TException {
		if (cfg.isHttp()) {
			return createHttpThriftClient(stack, cfg);
		} else {
			return createSocketThriftClient(stack, cfg);
		}
	}

	public boolean createSocketThriftClient(AIStack<AIThriftClient> stack,
			final ThriftClientConfig cfg) throws TException {

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
				pipeline.addLast("frame",
						new NCHFramed(cfg.getFrameMaxLength()));
				NettyClient.addPlaceholder(pipeline);
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

		AIStackStep<NettyClient, AIThriftClient> callStack = new AIStackStep<NettyClient, AIThriftClient>(
				stack) {

			@Override
			protected boolean next(NettyClient result) {
				AIThriftInvokerNettySocket inv = new AIThriftInvokerNettySocket(
						result, cfg);
				return successForward(new AIThriftClientNetty(result, inv));
			}
		};
		return bootstrap.connect(callStack, cfg.getHost()
				.createInetSocketAddress(), null, fac);
	}

	public boolean createHttpThriftClient(AIStack<AIThriftClient> stack,
			final ThriftClientConfig cfg) throws TException {

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
				pipeline.addLast("codec", new HttpClientCodec());
				pipeline.addLast("chunkded",
						new HttpChunkAggregator(cfg.getFrameMaxLength()));
				pipeline.addLast("inflater", new HttpContentDecompressor());
				NettyClient.addPlaceholder(pipeline);
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

		URL url;
		try {
			url = new URL(cfg.getUrl());
		} catch (MalformedURLException e) {
			throw new TException(e);
		}
		HostPort host = new HostPort(url.getHost(), url.getPort() == -1 ? 80
				: url.getPort());
		AIStackStep<NettyClient, AIThriftClient> callStack = new AIStackStep<NettyClient, AIThriftClient>(
				stack) {

			@Override
			protected boolean next(NettyClient result) {
				AIThriftInvokerNettyHttp inv = new AIThriftInvokerNettyHttp(
						result, cfg);
				return successForward(new AIThriftClientNetty(result, inv));
			}
		};
		return bootstrap.connect(callStack, host.createInetSocketAddress(),
				null, fac);
	}
}
