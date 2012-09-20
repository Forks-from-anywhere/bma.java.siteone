package bma.siteone.netty.thrift.gate;

import org.apache.thrift.TApplicationException;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TMessage;
import org.apache.thrift.protocol.TMessageType;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TTransport;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;

import bma.siteone.netty.thrift.client.TNettyChannelFramedTransport;
import bma.siteone.netty.thrift.core.TNettyChannelBufferTransport;
import bma.siteone.netty.thrift.gate.impl.SimpleMessageContext;

public class GateUtil {

	public static void responseException(SimpleMessageContext ctx, Throwable t) {
		Channel ch = ctx.getNettyChannel();
		if (ch != null && ch.isOpen()) {

		}
	}

	public static TMessage readTMessage(ChannelBuffer buf) throws TException {
		TNettyChannelBufferTransport transport = new TNettyChannelBufferTransport(
				buf);
		TBinaryProtocol prop = new TBinaryProtocol(transport);
		buf.markReaderIndex();
		try {
			return prop.readMessageBegin();
		} finally {
			buf.resetReaderIndex();
		}
	}

	public static void responseError(MessageContext ctx, Throwable t) {
		TMessage msg = getTMessage(ctx);
		if (msg != null) {
			responseError(ctx.getNettyChannel(), msg, t);
		}
	}

	public static void responseError(Channel ch, TMessage req, Throwable t) {
		TTransport transport = new TNettyChannelFramedTransport(ch,
				Integer.MAX_VALUE);
		TProtocol out = new TBinaryProtocol(transport);

		TApplicationException x;
		if (t instanceof TApplicationException) {
			x = (TApplicationException) t;
		} else {
			x = new TApplicationException(TApplicationException.INTERNAL_ERROR,
					t.getMessage());
		}
		try {
			out.writeMessageBegin(new TMessage(req.name,
					TMessageType.EXCEPTION, req.seqid));
			x.write(out);
			out.writeMessageEnd();
			transport.flush();
		} catch (Exception e) {
		}
	}

	public static void setTMessage(MessageContext ctx, TMessage msg) {
		ctx.setProperty("TMessage", msg);
	}

	public static TMessage getTMessage(MessageContext ctx) {
		return ctx.getProperty("TMessage", TMessage.class);
	}
}
