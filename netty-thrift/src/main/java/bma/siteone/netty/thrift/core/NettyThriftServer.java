package bma.siteone.netty.thrift.core;

import org.apache.thrift.TProcessor;
import org.jboss.netty.channel.ChannelPipeline;

import bma.common.langutil.log.LogPrinter.LEVEL;
import bma.common.netty.NettyServer;
import bma.common.netty.handler.ChannelHandlerExceptionClose;
import bma.common.netty.handler.ChannelHandlerLog;
import bma.common.netty.handler.ChannelHandlerLog.TYPE;

public class NettyThriftServer extends NettyServer {

	final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(NettyThriftServer.class);

	protected TProcessor processor;
	protected int maxLength;
	protected int traceBufferSize = 0;

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

	public int getTraceBufferSize() {
		return traceBufferSize;
	}

	public void setTraceBufferSize(int traceBufferSize) {
		this.traceBufferSize = traceBufferSize;
	}

	@Override
	protected void beforeBuildPipeline(ChannelPipeline pipeline) {
		super.beforeBuildPipeline(pipeline);
		if (traceBufferSize > 0) {
			pipeline.addLast("uplog", new ChannelHandlerLog(log, LEVEL.DEBUG,
					TYPE.UPSTREAM, traceBufferSize));
		}
		pipeline.addLast("framed", new NCHFramed(maxLength));
		pipeline.addLast("request", new NCHThriftRequest(processor));
		if (traceBufferSize > 0) {
			pipeline.addLast("downlog", new ChannelHandlerLog(log, LEVEL.DEBUG,
					TYPE.DOWNSTREAM, traceBufferSize));
		}
		pipeline.addLast("error", new ChannelHandlerExceptionClose());
	}
}