package bma.siteone.netty.thrift.core;

import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.jboss.netty.buffer.ChannelBuffer;

/**
 * Netty ChannelBuffer implementation of the TTransport interface.
 * 
 */
public class TNettyChannelBufferTransport extends TTransport {

	protected ChannelBuffer channelBuffer;

	public TNettyChannelBufferTransport(ChannelBuffer buf) {
		super();
		this.channelBuffer = buf;
	}

	public ChannelBuffer getChannelBuffer() {
		return channelBuffer;
	}

	public void setChannelBuffer(ChannelBuffer readBuffer) {
		this.channelBuffer = readBuffer;
	}

	@Override
	public boolean isOpen() {
		return true;
	}

	@Override
	public void open() throws TTransportException {

	}

	@Override
	public void close() {

	}

	public int read(byte[] buf, int off, int len) throws TTransportException {
		int got = Math.min(channelBuffer.readableBytes(), len);
		channelBuffer.readBytes(buf, off, got);
		return got;
	}

	@Override
	public void consumeBuffer(int len) {
		channelBuffer.skipBytes(len);
	}

	/**
	 * Writes to the underlying output stream if not null.
	 */
	public void write(byte[] buf, int off, int len) throws TTransportException {
		channelBuffer.writeBytes(buf, off, len);
	}
}
