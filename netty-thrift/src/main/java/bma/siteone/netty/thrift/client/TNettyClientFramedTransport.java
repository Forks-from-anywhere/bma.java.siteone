package bma.siteone.netty.thrift.client;

import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.TFieldIdEnum;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TTransportException;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;

import bma.common.langutil.ai.AIUtil;
import bma.common.langutil.ai.stack.AIStack;
import bma.common.thrift.ai.AIThriftInvoker;
import bma.common.thrift.ai.TAIBaseServiceClient;
import bma.siteone.netty.thrift.core.NCHFramed;
import bma.siteone.netty.thrift.core.TNettyChannelTransport;

public class TNettyClientFramedTransport extends TNettyChannelTransport
		implements AIThriftInvoker {

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
		channel.getPipeline().addLast("framed", new NCHFramed(this.maxLength));
		super.bindHandler();
	}

	@Override
	public void addReadBuffer(ChannelBuffer cb) {
		super.addReadBuffer(cb);
		if (receiver != null) {
			try {
				receiver.run();
			} finally {
				receiver = null;
			}
		}
	}

	private volatile Runnable receiver;

	@Override
	public <TYPE> boolean invoke(AIStack<TYPE> s, final TProtocol in,
			TProtocol out, final int seqid, final TBase result,
			final String name, TBase avgs) throws TException {
		TAIBaseServiceClient.sendBase(out, name, avgs, seqid);
		if (result == null)
			return s.success(null);
		final AIStack<TYPE> stack = AIUtil.fork(s);
		receiver = new Runnable() {

			@Override
			public void run() {
				try {
					TAIBaseServiceClient.readBase(stack, in, seqid, name,
							result);
					TFieldIdEnum field = result.fieldForId(0);
					if (field != null) {
						stack.success((TYPE) result.getFieldValue(field));
					} else {
						stack.success(null);
					}
					return;
				} catch (Exception err) {
					stack.failure(err);
				}
			}
		};
		if (event.checkEvent()) {
			receiver.run();
			receiver = null;
			return true;
		}
		return false;
	}

}
