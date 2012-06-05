package bma.siteone.netty.thrift.core;

import org.apache.thrift.transport.TTransportException;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;

import bma.common.netty.framereader.FrameReaderDecoder;

public class NCHFramed extends FrameReaderDecoder {

	private int maxLength;

	protected NCHFramed(int maxLength) {
		super();
		this.maxLength = maxLength;
	}

	/**
	 * Decodes the received packets so far into a frame.
	 * 
	 * @param ctx
	 *            the context of this handler
	 * @param channel
	 *            the current channel
	 * @param buffer
	 *            the cumulative buffer of received packets so far. Note that
	 *            the buffer might be empty, which means you should not make an
	 *            assumption that the buffer contains at least one byte in your
	 *            decoder implementation.
	 * 
	 * @return the decoded frame if a full frame was received and decoded.
	 *         {@code null} if there's not enough data in the buffer to decode a
	 *         frame.
	 */
	protected Object decode(ChannelHandlerContext ctx, Channel channel,
			ChannelBuffer buffer) throws Exception {
		if (buffer.readableBytes() < 4)
			return null;
		byte[] i32buf = new byte[4];
		buffer.readBytes(i32buf);
		int size = decodeFrameSize(i32buf);

		if (size < 0) {
			throw new TTransportException("Read a negative frame size (" + size
					+ ")!");
		}
		if (size > maxLength) {
			throw new TTransportException("Frame size (" + size
					+ ") larger than max length (" + maxLength + ")!");
		}
		if (buffer.readableBytes() < size) {
			return null;
		}
		ChannelBuffer cb = buffer.readBytes(size);
		return new TNettyFramedTransport(ctx.getChannel(), cb, this.maxLength);
	}

	public static final int decodeFrameSize(final byte[] buf) {
		return ((buf[0] & 0xff) << 24) | ((buf[1] & 0xff) << 16)
				| ((buf[2] & 0xff) << 8) | ((buf[3] & 0xff));
	}

}
