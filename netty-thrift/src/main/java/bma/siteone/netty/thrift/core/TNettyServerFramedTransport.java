package bma.siteone.netty.thrift.core;

import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;

import bma.common.netty.SupportedNettyChannel;

public class TNettyServerFramedTransport extends TTransport implements
		SupportedNettyChannel {

	protected Channel channel;
	private ChannelBuffer readBuffer;
	protected int maxLength;

	/**
	 * Buffer for output
	 */
	protected ChannelBuffer writeBuffer = ChannelBuffers.dynamicBuffer(1024);

	public TNettyServerFramedTransport(Channel ch, ChannelBuffer rcb, int maxlen) {
		super();
		channel = ch;
		this.maxLength = maxlen;
		this.readBuffer = rcb;
	}

	@Override
	public Channel getChannel() {
		return channel;
	}

	@Override
	public boolean isOpen() {
		return channel.isConnected();
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
		NCHFramed.encodeFrameSize(len, i32buf);
		if (channel.isOpen()) {
			channel.write(ChannelBuffers.copiedBuffer(i32buf));
			channel.write(writeBuffer);
		}
	}

	@Override
	public void open() throws TTransportException {

	}

	@Override
	public void close() {

	}

}
