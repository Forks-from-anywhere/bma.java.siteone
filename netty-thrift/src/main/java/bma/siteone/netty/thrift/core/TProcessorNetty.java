package bma.siteone.netty.thrift.core;

import java.util.concurrent.Executor;

import org.apache.thrift.TException;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TMessage;
import org.apache.thrift.protocol.TProtocol;

import bma.common.thrift.TProcessorDispatch;
import bma.common.thrift.TProcessorDispatchAbstract;
import bma.common.thrift.TProtocolCacheMessage;
import bma.common.thrift.ai.TAIProcessor;

public class TProcessorNetty extends TProcessorDispatchAbstract implements
		TProcessor {

	protected Executor executor;
	protected TProcessor processor;
	protected TProcessorDispatch dispatcher;

	public Executor getExecutor() {
		return executor;
	}

	public void setExecutor(Executor executor) {
		this.executor = executor;
	}

	public TProcessor getProcessor() {
		return processor;
	}

	public void setProcessor(TProcessor processor) {
		this.processor = processor;
		if (processor != null) {
			if (processor instanceof TProcessorDispatch) {
				dispatcher = (TProcessorDispatch) processor;
			}
		}
	}

	@Override
	public TProcessor dispatch(TMessage msg, TProtocol in, TProtocol out)
			throws TException {
		if (dispatcher != null) {
			return dispatcher.dispatch(msg, in, out);
		}
		return processor;
	}

	@Override
	protected boolean process(final TProcessor p, TProtocol in,
			final TProtocol out) throws TException {
		final org.slf4j.Logger log = org.slf4j.LoggerFactory
				.getLogger(TProcessorNetty.class);
		if (executor != null) {
			if (!(p instanceof TAIProcessor)) {
				log.debug("executor.process");
				final TProtocolCacheMessage inw = wrapIn(in);
				final TMessage msg = inw.readMessageBegin();
				executor.execute(new Runnable() {
					@Override
					public void run() {
						try {
							TProcessorNetty.super.process(p, inw, out);
						} catch (TException e) {
							handleException(e, msg, out);
						}
					}
				});
				return true;
			}
		}
		log.debug("super.process");
		return super.process(p, in, out);
	}

}
