package bma.siteone.netty.thrift.client;

import org.apache.thrift.transport.TTransportException;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import bma.siteone.netty.thrift.core.NCHFramed;

public class TNettyClientFramedTransport extends TAINettyClientTransport {

	protected int maxLength;
	protected ChannelBuffer writeBuffer;

	public TNettyClientFramedTransport(Channel ch, int maxlen) {
		super(ch);
		this.maxLength = maxlen;
	}

	public void write(byte[] buf, int off, int len) throws TTransportException {
		if (writeBuffer == null) {
			writeBuffer = ChannelBuffers.dynamicBuffer(1024);
		}
		if (writeBuffer.writerIndex() + len > maxLength) {
			throw new TTransportException("Frame size ("
					+ (writeBuffer.writerIndex() + len)
					+ ") larger than max length (" + maxLength + ")!");
		}
		writeBuffer.writeBytes(buf, off, len);
	}

	@Override
	public void flush() throws TTransportException {
		int len = writeBuffer.writerIndex();
		byte[] i32buf = new byte[4];
		NCHFramed.encodeFrameSize(len, i32buf);
		if (channel.isOpen()) {
			channel.write(ChannelBuffers.copiedBuffer(i32buf));
			channel.write(writeBuffer);
			writeBuffer = null;
		}
	}

	public void bindHandler() {
		ChannelPipeline p = channel.getPipeline();
		ChannelHandler ch = new NCHFramed(this.maxLength);
		ChannelHandler ch2 = new SimpleChannelUpstreamHandler() {
			@Override
			public void channelClosed(ChannelHandlerContext ctx,
					ChannelStateEvent e) throws Exception {
				processError(new IllegalStateException("closed"));
				super.channelClosed(ctx, e);
			}
		};
		if (p.get(PIPELINE_NAME) != null) {
			p.addBefore(PIPELINE_NAME, "framed", ch);
			p.addBefore(PIPELINE_NAME, "closed", ch2);
		} else {
			p.addLast("framed", ch);
			p.addBefore(PIPELINE_NAME, "closed", ch2);
		}
		super.bindHandler();
	}

}
