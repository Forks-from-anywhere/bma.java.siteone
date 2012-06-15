package bma.siteone.netty.thrift.core;

import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import bma.common.langutil.concurrent.Event;

/**
 * Netty Channel implementation of the TTransport interface.
 * 
 */
public class TNettyChannelTransport extends TTransport {

	protected Channel channel;
	protected ChannelBuffer readBuffer;
	protected long readTimeout = Long.MAX_VALUE;
	protected Event event;

	public TNettyChannelTransport(Channel channel) {
		super();
		this.channel = channel;
		event = Event.createManulResetEvent();
	}

	public Channel getChannel() {
		return channel;
	}

	public long getReadTimeout() {
		return readTimeout;
	}

	public void setReadTimeout(long readTimeout) {
		this.readTimeout = readTimeout;
	}

	public ChannelBuffer getReadBuffer() {
		return readBuffer;
	}

	public void addReadBuffer(ChannelBuffer cb) {
		if (this.readBuffer == null) {
			this.readBuffer = ChannelBuffers.dynamicBuffer(1024);
		}
		this.readBuffer.writeBytes(cb);
		event.setEvent();
	}

	@Override
	public boolean isOpen() {
		return channel.isConnected();
	}

	@Override
	public void open() throws TTransportException {

	}

	@Override
	public void close() {
		channel.close();
	}

	public int read(byte[] buf, int off, int len) throws TTransportException {
		if (!event.checkAndWait(readTimeout))
			return 0;
		int got = Math.min(readBuffer.readableBytes(), len);
		readBuffer.readBytes(buf, off, got);
		if (readBuffer.readableBytes() == 0)
			event.resetEvent();
		return got;
	}

	@Override
	public void consumeBuffer(int len) {
		readBuffer.skipBytes(len);
	}

	/**
	 * Writes to the underlying output stream if not null.
	 */
	public void write(byte[] buf, int off, int len) throws TTransportException {
		channel.write(ChannelBuffers.copiedBuffer(buf, off, len));
	}

	public void bindHandler() {
		channel.getPipeline().addLast("nettyChannelTransport",
				new SimpleChannelUpstreamHandler() {
					@Override
					public void messageReceived(ChannelHandlerContext ctx,
							MessageEvent e) throws Exception {
						Object obj = e.getMessage();
						if (obj instanceof ChannelBuffer) {
							ChannelBuffer cb = (ChannelBuffer) obj;
							addReadBuffer(cb);
							return;
						}
						super.messageReceived(ctx, e);
					}
				});
	}
}
