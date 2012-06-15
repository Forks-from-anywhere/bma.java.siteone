package bma.siteone.netty.thrift.client;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocol;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import bma.common.langutil.ai.stack.AIStack;
import bma.common.langutil.core.ExceptionUtil;
import bma.common.langutil.core.Preconditions;
import bma.common.langutil.core.ValueUtil;
import bma.common.langutil.log.LogPrinter.LEVEL;
import bma.common.netty.client.NettyClientBootstrap;
import bma.common.netty.handler.ChannelHandlerExceptionClose;
import bma.common.netty.handler.ChannelHandlerLog;
import bma.common.netty.handler.ChannelHandlerLog.TYPE;
import bma.common.thrift.ThriftClient;
import bma.common.thrift.ThriftClientFactoryConfig;
import bma.siteone.netty.thrift.core.NCHFramed;

public class ThriftClient4AIFactoryNetty extends ThriftClientFactoryConfig {

	final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(ThriftClient4AIFactoryNetty.class);

	protected NettyClientBootstrap bootstrap;
	protected int traceBufferSize = 0;

	public int getTraceBufferSize() {
		return traceBufferSize;
	}

	public void setTraceBufferSize(int traceBufferSize) {
		this.traceBufferSize = traceBufferSize;
	}

	public NettyClientBootstrap getBootstrap() {
		return bootstrap;
	}

	public void setBootstrap(NettyClientBootstrap bootstrap) {
		this.bootstrap = bootstrap;
	}

	protected ThriftClient newThriftClient() {
		return null;
	}

	public boolean createThriftClient(final AIStack<Object> stack)
			throws TException {

		Preconditions.checkNotNull(this.bootstrap);
		if (ValueUtil.notEmpty(type) && !TYPE_SOCKET.equals(type)) {
			throw new IllegalArgumentException("type('" + type
					+ "') must be socket");
		}
		Preconditions.checkNotNull(host);
		if (this.frameMaxLength <= 0) {
			throw new IllegalArgumentException("frameSize must >0");
		}

		if (host.getPort() == 0) {
			host.setPort(9090);
		}

		final TNettyClientFramedTransport transport = null;
		// new TNettyClientFramedTransport(frameMaxLength);

		ChannelPipelineFactory fac = new ChannelPipelineFactory() {

			@Override
			public ChannelPipeline getPipeline() throws Exception {
				ChannelPipeline p = Channels.pipeline();

				if (traceBufferSize > 0) {
					p.addLast("uplog", new ChannelHandlerLog(log, LEVEL.DEBUG,
							TYPE.UPSTREAM, traceBufferSize));
				}
				p.addLast("framed", new NCHFramed(frameMaxLength));
				p.addLast("transport", new SimpleChannelUpstreamHandler() {

					@Override
					public void messageReceived(ChannelHandlerContext ctx,
							MessageEvent e) throws Exception {
						super.messageReceived(ctx, e);
					}
				});
				if (traceBufferSize > 0) {
					p.addLast("downlog", new ChannelHandlerLog(log,
							LEVEL.DEBUG, TYPE.DOWNSTREAM, traceBufferSize));
				}
				p.addLast("error", new ChannelHandlerExceptionClose() {
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

				return p;
			}
		};

		if (log.isDebugEnabled()) {
			log.debug("create netty client - {}", host);
		}
		this.bootstrap.connect(host.createInetSocketAddress(), null, fac)
				.addListener(new ChannelFutureListener() {

					@Override
					public void operationComplete(ChannelFuture future)
							throws Exception {
						if (future.isSuccess()) {
							//  transport.setChannel(future.getChannel());
							TProtocol pro = createProtocol(transport);

							stack.success(null);
						} else {
							stack.failure(future.getCause());
						}
					}
				});
		return false;
	}
}
