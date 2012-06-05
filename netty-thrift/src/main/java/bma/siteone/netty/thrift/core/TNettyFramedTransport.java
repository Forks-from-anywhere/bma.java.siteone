package bma.siteone.netty.thrift.core;

import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;

public class TNettyFramedTransport extends TTransport {

	private int maxLength;

	/**
	 * Buffer for output
	 */
	private ChannelBuffer writeBuffer = ChannelBuffers.dynamicBuffer(1024);

	/**
	 * Buffer for input
	 */
	private ChannelBuffer readBuffer;

	private Channel channel;

	public TNettyFramedTransport(Channel ch, ChannelBuffer in, int maxlen) {
		super();
		this.channel = ch;
		this.readBuffer = in;
		this.maxLength = maxlen;
	}

	public void open() throws TTransportException {

	}

	public boolean isOpen() {
		return channel.isOpen();
	}

	public void close() {

	}

	public int read(byte[] buf, int off, int len) throws TTransportException {
		int got = Math.min(readBuffer.readableBytes(), len);
		readBuffer.readBytes(buf, off, got);
		return got;
	}

	@Override
	public void consumeBuffer(int len) {
		readBuffer.skipBytes(len);
	}

	public void write(byte[] buf, int off, int len) throws TTransportException {
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
		encodeFrameSize(len, i32buf);
		if (channel.isOpen()) {
			channel.write(ChannelBuffers.copiedBuffer(i32buf));
			channel.write(writeBuffer);
		}
	}

	public static final void encodeFrameSize(final int frameSize,
			final byte[] buf) {
		buf[0] = (byte) (0xff & (frameSize >> 24));
		buf[1] = (byte) (0xff & (frameSize >> 16));
		buf[2] = (byte) (0xff & (frameSize >> 8));
		buf[3] = (byte) (0xff & (frameSize));
	}

}
