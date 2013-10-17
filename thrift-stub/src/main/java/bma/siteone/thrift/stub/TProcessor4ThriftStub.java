package bma.siteone.thrift.stub;

import java.util.List;

import org.apache.thrift.TException;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TProtocol;
import org.codehaus.jackson.JsonNode;

import bma.common.json.JsonUtil;
import bma.common.json.xdom.XdomJson;
import bma.common.langutil.core.ExceptionUtil;
import bma.common.langutil.xdom.Xdom;
import bma.common.langutil.xdom.XdomUtil;
import bma.common.thrift.TProcessor4Error;
import bma.common.thrift.xdom.TProcessorXdom;
import bma.common.thrift.xdom.impl.DynamicThriftManagerHub;
import bma.common.thrift.xdom.impl.DynamicThriftManagerSimple;

public class TProcessor4ThriftStub extends TProcessor4Error {

	final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(TProcessor4ThriftStub.class);

	private ThriftStubStore store;

	private long lastTime;
	private TProcessorXdom processor;

	public ThriftStubStore getStore() {
		return store;
	}

	public void setStore(ThriftStubStore store) {
		this.store = store;
	}

	@Override
	public boolean process(TProtocol in, TProtocol out) throws TException {
		return process(getProcessor(), in, out);
	}

	public TProcessor getProcessor() {
		synchronized (this) {
			if (processor != null) {
				if (store.lastModified() > lastTime) {
					processor = null;
				}
			}
			if (processor == null) {
				createProcessor();
			}
		}
		return processor;
	}

	protected void createProcessor() {

		DynamicThriftManagerHub hub = new DynamicThriftManagerHub();
		List<ThriftStubDesc> dlist = this.store.listAllDesc();
		for (ThriftStubDesc tdesc : dlist) {
			try {
				JsonNode node = JsonUtil.parse(tdesc.getContent());
				Xdom desc = new XdomJson().bind(node);
				DynamicThriftManagerSimple m = new DynamicThriftManagerSimple();
				m.init(desc);
				hub.addManager(m);
			} catch (Exception e) {
				if (log.isWarnEnabled()) {
					log.warn("parse '" + tdesc.getName() + "' fail", e);
				}
			}
		}

		final List<ThriftStubInfo> ilist = this.store.listAllInfo();
		processor = new TProcessorXdom() {

			@Override
			public Xdom handle(String name, Xdom param) {
				return handleRequest(ilist, name, param);
			}
		};
		processor.setDescManager(hub);

	}

	protected Xdom handleRequest(List<ThriftStubInfo> infos, String name,
			Xdom param) {
		try {
			if (log.isDebugEnabled()) {
				log.debug("METHOD: {}", name);
				log.debug("REQUEST: {}", XdomUtil.toString(param));
			}
			for (ThriftStubInfo info : infos) {
				if (info.getMethod().equals(name)) {
					log.debug("HANDLE: {}", info.getName());
					Xdom rep = handleRequest(info, param);
					if (log.isDebugEnabled()) {
						log.debug("RESPONSE: {}", XdomUtil.toString(rep));
					}
					return rep;
				}
			}
			throw new IllegalArgumentException("invalid method '" + name + "'");
		} catch (Exception e) {
			if (log.isWarnEnabled()) {
				log.warn("handle '" + name + "' fail", e);
			}
			throw ExceptionUtil.throwRuntime(e);
		}
	}

	protected Xdom handleRequest(ThriftStubInfo info, Xdom param)
			throws Exception {
		// data
		JsonNode node = JsonUtil.parse(info.getContent());
		return new XdomJson().bind(node);
	}
}
