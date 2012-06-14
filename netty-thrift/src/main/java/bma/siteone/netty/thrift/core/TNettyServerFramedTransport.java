package bma.siteone.netty.thrift.core;

import org.apache.thrift.transport.TTransportException;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;

public class TNettyServerFramedTransport extends TNettyBaseFramedTransport {

	/**
	 * Buffer for input
	 */
	private ChannelBuffer readBuffer;

	public TNettyServerFramedTransport(Channel ch, ChannelBuffer in, int maxlen) {
		super(ch, maxlen);
		this.readBuffer = in;
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
}
