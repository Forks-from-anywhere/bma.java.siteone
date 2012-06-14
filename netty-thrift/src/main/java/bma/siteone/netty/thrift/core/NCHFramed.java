package bma.siteone.netty.thrift.core;

import org.apache.thrift.transport.TTransportException;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;

import bma.common.netty.framereader.FrameReaderDecoder;

public class NCHFramed extends FrameReaderDecoder {

	public static class FramedChannelBuffer {
		private ChannelBuffer buffer;
		private int maxLength;

		public FramedChannelBuffer(ChannelBuffer buffer, int maxLength) {
			super();
			this.buffer = buffer;
			this.maxLength = maxLength;
		}

		public ChannelBuffer getBuffer() {
			return buffer;
		}

		public void setBuffer(ChannelBuffer buffer) {
			this.buffer = buffer;
		}

		public int getMaxLength() {
			return maxLength;
		}

		public void setMaxLength(int maxLength) {
			this.maxLength = maxLength;
		}

	}

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
		buffer.markReaderIndex();
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
			buffer.resetReaderIndex();
			return null;
		}
		ChannelBuffer cb = buffer.readBytes(size);
		return new FramedChannelBuffer(cb, this.maxLength);
		// return new TNettyFramedTransport(ctx.getChannel(), cb,
		// this.maxLength);
	}

	public static final int decodeFrameSize(final byte[] buf) {
		return ((buf[0] & 0xff) << 24) | ((buf[1] & 0xff) << 16)
				| ((buf[2] & 0xff) << 8) | ((buf[3] & 0xff));
	}
	
	public static final void encodeFrameSize(final int frameSize,
			final byte[] buf) {
		buf[0] = (byte) (0xff & (frameSize >> 24));
		buf[1] = (byte) (0xff & (frameSize >> 16));
		buf[2] = (byte) (0xff & (frameSize >> 8));
		buf[3] = (byte) (0xff & (frameSize));
	}

}
