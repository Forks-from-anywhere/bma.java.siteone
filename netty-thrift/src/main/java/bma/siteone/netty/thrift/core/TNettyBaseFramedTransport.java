package bma.siteone.netty.thrift.core;

import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;

public abstract class TNettyBaseFramedTransport extends TTransport {

	protected int maxLength;

	/**
	 * Buffer for output
	 */
	protected ChannelBuffer writeBuffer = ChannelBuffers.dynamicBuffer(1024);

	protected Channel channel;

	public TNettyBaseFramedTransport(Channel ch, int maxlen) {
		super();
		this.channel = ch;
		this.maxLength = maxlen;
	}

	public void open() throws TTransportException {

	}

	public boolean isOpen() {
		return channel.isOpen();
	}

	public void close() {

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

}
