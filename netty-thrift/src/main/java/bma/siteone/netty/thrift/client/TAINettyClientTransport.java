package bma.siteone.netty.thrift.client;

import java.util.LinkedList;

import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.TFieldIdEnum;
import org.apache.thrift.protocol.TProtocol;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;

import bma.common.langutil.ai.AIUtil;
import bma.common.langutil.ai.stack.AIStack;
import bma.common.thrift.ai.AIThriftInvoker;
import bma.common.thrift.ai.TAIBaseServiceClient;

public abstract class TAINettyClientTransport extends TNettyClientTransport
		implements AIThriftInvoker {

	public TAINettyClientTransport(Channel ch) {
		super(ch);
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
		synchronized (this) {
			receiverList.add(r);
		}
		return processReceiver();
	}

}
