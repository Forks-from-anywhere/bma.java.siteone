package bma.siteone.netty.thrift.gate.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TMessage;
import org.jboss.netty.buffer.ChannelBuffer;

import bma.common.langutil.ai.stack.AIStack;
import bma.common.langutil.io.HostPort;
import bma.siteone.netty.thrift.gate.GateUtil;
import bma.siteone.netty.thrift.gate.MessageContext;
import bma.siteone.netty.thrift.gate.NTGAgent;
import bma.siteone.netty.thrift.gate.NTGAgentFactory;
import bma.siteone.netty.thrift.gate.NTGDispatcher;

public class NTGDispatcherCore implements NTGDispatcher {

	final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(NTGDispatcherCore.class);

	private Map<String, NTGAgentFactory> agentFactoryMap;

	public Map<String, NTGAgentFactory> getAgentFactoryMap() {
		return agentFactoryMap;
	}

	public void setAgentFactoryMap(Map<String, NTGAgentFactory> agentFactoryMap) {
		this.agentFactoryMap = agentFactoryMap;
	}

	public void setServiceHost(Map<String, String> shost) {
		if (agentFactoryMap == null) {
			agentFactoryMap = new HashMap<String, NTGAgentFactory>();
		}
		for (Map.Entry<String, String> e : shost.entrySet()) {
			HostPort host = new HostPort();
			host.setHostString(e.getValue(), 9090);
			NTGAgentFactoryCore fac = new NTGAgentFactoryCore();
			fac.setHost(host);
			agentFactoryMap.put(e.getKey(), fac);
		}
	}

	@Override
	public boolean dispatch(AIStack<NTGAgent> stack, MessageContext ctx) {
		ChannelBuffer buf = ctx.getMessage();
		TMessage msg = null;
		try {
			msg = GateUtil.readTMessage(buf);
			GateUtil.setTMessage(ctx, msg);
			return dispatchByMessage(stack, ctx, msg);
		} catch (Exception e) {
			if (msg != null) {
				GateUtil.responseError(ctx.getNettyChannel(), msg, e);
				return stack.success(null);
			} else {
				return stack.failure(e);
			}
		}
	}

	public boolean dispatchByMessage(AIStack<NTGAgent> stack,
			MessageContext ctx, TMessage msg) {

		String module = null;
		String name = msg.name;
		int idx = name.indexOf('.');
		if (idx != -1) {
			module = name.substring(0, idx);
			name = name.substring(idx + 1);
		}
		if (module == null) {
			// source module by name
		}
		if (module == null) {
			String emsg = "thrift method['" + name + "'] not found";
			if (log.isWarnEnabled()) {
				log.warn("{} - {}", emsg, ctx.getNettyChannel()
						.getRemoteAddress());
			}
			GateUtil.responseError(ctx, new TException(emsg));
			return stack.success(null);
		}

		NTGAgentFactory agent = this.agentFactoryMap == null ? null
				: this.agentFactoryMap.get(module);
		if (agent == null) {
			String emsg = "thrift module['" + module + "'] not found";
			if (log.isWarnEnabled()) {
				log.warn("{} - {}", emsg, ctx.getNettyChannel()
						.getRemoteAddress());
			}
			GateUtil.responseError(ctx, new TException(emsg));
		}
		if (log.isDebugEnabled()) {
			log.debug("thrift[{}] => {}", msg.name, agent);
		}
		return stack.success(agent.newAgent());
	}
}
