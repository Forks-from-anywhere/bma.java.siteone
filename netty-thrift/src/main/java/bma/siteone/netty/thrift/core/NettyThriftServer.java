package bma.siteone.netty.thrift.core;

import java.util.concurrent.Executor;

import org.apache.thrift.TProcessor;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;

import bma.common.langutil.core.ExceptionUtil;
import bma.common.langutil.core.SizeUtil;
import bma.common.langutil.core.SizeUtil.Unit;
import bma.common.langutil.core.StringUtil;
import bma.common.langutil.core.ValueUtil;
import bma.common.netty.NettyServer;
import bma.common.netty.handler.ChannelHandlerExceptionClose;
import bma.common.thrift.TProcessorRouter;
import bma.common.thrift.ThriftClientConfig;
import bma.common.thrift.servicehub.ThriftServiceNode;

public class NettyThriftServer extends NettyServer {

	final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(NettyThriftServer.class);

	protected TProcessor processor;
	protected int maxLength;

	public NettyThriftServer() {
		super();
	}

	public TProcessor getProcessor() {
		return processor;
	}

	public void setProcessor(TProcessor processor) {
		this.processor = processor;
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

	protected ThriftServiceNode node;
	protected Executor processExecutor;

	public void setProcessExecutor(Executor processExecutor) {
		this.processExecutor = processExecutor;
		makeNodeProcessor();
	}

	public void setNode(ThriftServiceNode node) {
		this.node = node;

		String type = node.getType();
		if (!(ValueUtil.empty(type) || StringUtil.equals(
				ThriftClientConfig.TYPE_SOCKET, type))) {
			throw new IllegalArgumentException("type(" + type
					+ ") must 'socket'");
		}
		setHostPort(node.getHostport());
		setMaxLength(node.getFrameLength());
		makeNodeProcessor();
	}

	protected void makeNodeProcessor() {
		if (node == null)
			return;
		if (processExecutor == null)
			return;

		TProcessorRouter pr = new TProcessorRouter();
		pr.setDefaultModule(node.getDefaultModule());
		pr.setProcessorBeans(node.getServices());

		TProcessorNetty p = new TProcessorNetty();
		p.setExecutor(processExecutor);
		p.setProcessor(pr);

		setProcessor(p);
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
		pipeline.addLast("request", new NCHThriftRequest(processor));
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
