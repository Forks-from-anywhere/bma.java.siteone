package bma.siteone.netty.thrift.core;

import org.apache.thrift.TException;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

/**
 * 处理Thrift请求
 * 
 * @author guanzhong
 * 
 */
public class NCHThriftRequest extends SimpleChannelUpstreamHandler {

	final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(NCHThriftRequest.class);

	private TProcessor processor;
	private TProtocolFactory protocolFactory;

	public NCHThriftRequest() {
		super();
	}

	public NCHThriftRequest(TProcessor processor) {
		super();
		this.processor = processor;
	}

	public TProcessor getProcessor() {
		return processor;
	}

	public void setProcessor(TProcessor processor) {
		this.processor = processor;
	}

	public TProtocolFactory getProtocolFactory() {
		return protocolFactory;
	}

	public void setProtocolFactory(TProtocolFactory protocolFactory) {
		this.protocolFactory = protocolFactory;
	}

	public TProtocol createProtocol(TTransport trans) {
		if (this.protocolFactory != null) {
			return this.protocolFactory.getProtocol(trans);
		}
		return new TBinaryProtocol(trans);
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		Object obj = e.getMessage();
		if (!(obj instanceof TTransport)) {
			ctx.sendUpstream(e);
			return;
		}

		TTransport client = (TTransport) obj;
		TTransport inputTransport = null;
		TTransport outputTransport = null;
		TProtocol inputProtocol = null;
		TProtocol outputProtocol = null;
		try {
			inputTransport = client;
			outputTransport = client;
			inputProtocol = createProtocol(inputTransport);
			outputProtocol = createProtocol(outputTransport);
			processor.process(inputProtocol, outputProtocol);
		} catch (TTransportException ttx) {
			// Client died, just move on
		} catch (TException tx) {
			log.error("Thrift error occurred during processing of message.", tx);
		} catch (Exception x) {
			log.error("Error occurred during processing of message.", x);
		}
	}
}
