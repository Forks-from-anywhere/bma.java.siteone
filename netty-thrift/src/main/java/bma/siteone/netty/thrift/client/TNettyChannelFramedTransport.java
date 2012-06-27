package bma.siteone.netty.thrift.client;

import java.util.LinkedList;

import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.TFieldIdEnum;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TTransportException;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import bma.common.langutil.ai.AIUtil;
import bma.common.langutil.ai.stack.AIStack;
import bma.common.thrift.ai.AIThriftInvoker;
import bma.common.thrift.ai.TAIBaseServiceClient;
import bma.siteone.netty.thrift.core.NCHFramed;
import bma.siteone.netty.thrift.core.TNettyChannelTransport;

public class TNettyChannelFramedTransport extends TNettyChannelTransport
		implements AIThriftInvoker {

	protected int maxLength;
	protected ChannelBuffer writeBuffer;

	public TNettyChannelFramedTransport(Channel ch, int maxlen) {
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
		ChannelPipeline p = channel.getPipeline();
		ChannelHandler ch = new NCHFramed(this.maxLength);
		ChannelHandler ch2 = new SimpleChannelUpstreamHandler() {
			@Override
			public void channelClosed(ChannelHandlerContext ctx,
					ChannelStateEvent e) throws Exception {
				processError(new IllegalStateException("closed"));
				super.channelClosed(ctx, e);
			}
		};
		if (p.get(PIPELINE_NAME) != null) {
			p.addBefore(PIPELINE_NAME, "framed", ch);
			p.addBefore(PIPELINE_NAME, "closed", ch2);
		} else {
			p.addLast("framed", ch);
			p.addBefore(PIPELINE_NAME, "closed", ch2);
		}
		super.bindHandler();
	}

	@Override
	public void addReadBuffer(ChannelBuffer cb) {
		super.addReadBuffer(cb);
		processReceiver();
	}

	protected boolean processReceiver() {
		synchronized (this) {
			if (!event.checkEvent()) {
				return false;
			}
			if (receiverList.isEmpty())
				return false;
			Request r = receiverList.remove();
			r.run();
			return true;
		}
	}

	protected void processError(Throwable t) {
		synchronized (this) {
			while (!receiverList.isEmpty()) {
				Request r = receiverList.remove();
				r.error(t);
			}
		}
	}

	private final class Request<TYPE> {
		private final TProtocol in;
		private final String name;
		private final int seqid;
		private final TBase result;
		private final AIStack<TYPE> stack;

		private Request(TProtocol in, String name, int seqid, TBase result,
				AIStack<TYPE> stack) {
			this.in = in;
			this.name = name;
			this.seqid = seqid;
			this.result = result;
			this.stack = stack;
		}

		public void run() {
			try {
				TAIBaseServiceClient.readBase(stack, in, seqid, name, result);
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

		public void error(Throwable t) {
			AIUtil.safeFailure(stack, t);
		}

		@Override
		public String toString() {
			return "req[" + seqid + "]";
		}
	}

	private LinkedList<Request> receiverList = new LinkedList<Request>();

	@Override
	public <TYPE> boolean invoke(AIStack<TYPE> s, final TProtocol in,
			TProtocol out, final int seqid, final TBase result,
			final String name, TBase avgs) throws TException {
		TAIBaseServiceClient.sendBase(out, name, avgs, seqid);
		if (result == null)
			return s.success(null);
		final AIStack<TYPE> stack = AIUtil.fork(s);
		Request<TYPE> r = new Request<TYPE>(in, name, seqid, result, stack);
		synchronized (receiverList) {
			receiverList.add(r);
		}
		return processReceiver();
	}

}
