package bma.siteone.netty.thrift.client;

import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import bma.common.langutil.concurrent.Event;
import bma.common.netty.SupportedNettyChannel;

/**
 * Netty Channel implementation of the TTransport interface.
 * 
 */
public class TNettyClientTransport extends TTransport implements
		SupportedNettyChannel {

	protected Channel channel;
	protected ChannelBuffer readBuffer;
	protected long readTimeout = Long.MAX_VALUE;
	protected Event event;
	protected Throwable error;

	public TNettyClientTransport(Channel channel) {
		super();
		this.channel = channel;
		event = Event.createManulResetEvent();
	}

	@Override
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

	public Throwable getError() {
		return error;
	}

	public void setError(Throwable error) {
		this.error = error;
		event.setEvent(true);
	}

	public int read(byte[] buf, int off, int len) throws TTransportException {
		if (!event.checkAndWait(readTimeout))
			return 0;
		if (error != null) {
			if (readBuffer != null && readBuffer.readableBytes() == 0) {
				readBuffer.clear();
				event.resetEvent();
			}
			Throwable t = error;
			error = null;
			if (t instanceof TTransportException) {
				throw (TTransportException) t;
			} else {
				throw new TTransportException(t);
			}
		} else {
			int got = Math.min(readBuffer.readableBytes(), len);
			readBuffer.readBytes(buf, off, got);
			if (readBuffer.readableBytes() == 0) {
				readBuffer.clear();
				event.resetEvent();
			}
			return got;
		}
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

	public static final String PIPELINE_NAME = "nettyChannelTransport";

	public void bindHandler() {
		ChannelPipeline p = channel.getPipeline();
		ChannelHandler ch = new SimpleChannelUpstreamHandler() {
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
		};
		if (p.get(PIPELINE_NAME) != null) {
			p.replace(PIPELINE_NAME, PIPELINE_NAME, ch);
		} else {
			p.addLast(PIPELINE_NAME, ch);
		}
	}
}
