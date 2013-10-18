package bma.siteone.thrift.stub;

import java.util.List;

import org.apache.thrift.TException;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TProtocol;
import org.codehaus.jackson.JsonNode;
import org.mozilla.javascript.ConsString;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import bma.common.json.JsonUtil;
import bma.common.json.xdom.XdomJson;
import bma.common.langutil.core.ExceptionUtil;
import bma.common.langutil.core.StringUtil;
import bma.common.langutil.xdom.Xdom;
import bma.common.langutil.xdom.XdomUtil;
import bma.common.langutil.xdom.core.XdomArray;
import bma.common.langutil.xdom.core.XdomMap;
import bma.common.langutil.xdom.core.XdomString;
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
		if (StringUtil.equals(info.getType(), "js")) {
			return handleJS(info, param);
		}
		// data
		JsonNode node = JsonUtil.parse(info.getContent());
		return new XdomJson().bind(node);
	}

	protected Xdom todom(Object v) {
		Xdom domv = XdomUtil.value(v);
		if (domv != null)
			return domv;
		if (v instanceof ConsString) {
			return new XdomString(((ConsString) v).toString());
		}
		if (v instanceof Scriptable) {
			Scriptable so = (Scriptable) v;
			String cname = so.getClassName();
			if (cname.equals("Object")) {
				XdomMap r = new XdomMap();
				Object[] ids = so.getIds();
				if (ids != null) {
					for (Object keyo : ids) {
						String key = keyo.toString();
						Object sv = so.get(key, so);
						r.put(key, todom(sv));
					}
				}
				return r;
			}
			if (cname.equals("Array")) {
				int sz = ((Number) so.get("length", so)).intValue();
				XdomArray r = new XdomArray();
				for (int i = 0; i < sz; i++) {
					Object sv = so.get(i, so);
					r.addValue(todom(sv));
				}
				return r;
			}
		}
		return new XdomMap();
	}

	protected Xdom handleJS(ThriftStubInfo info, Xdom param) {
		Context ctx = Context.enter();
		try {
			Scriptable scope = ctx.initStandardObjects();
			String strval = "null";
			if (param != null) {
				JsonNode node = XdomJson.fromXdom(param);
				strval = node.toString();
			}
			ctx.evaluateString(scope, "var param = " + strval, "<init-param>",
					0, null);
			ctx.evaluateString(scope, info.getContent(), info.getName(), 1,
					null);
			Object ret = scope.get("success", scope);
			return todom(ret);
		} finally {
			Context.exit();
		}
	}
}
