package bma.siteone.netty.thrift.gate.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TMessage;

import bma.common.langutil.ai.stack.AIStack;
import bma.common.langutil.io.HostPort;
import bma.common.netty.pool.NettyChannelPool;
import bma.siteone.netty.thrift.gate.GateUtil;
import bma.siteone.netty.thrift.gate.MessageContext;
import bma.siteone.netty.thrift.gate.NTGAgent;
import bma.siteone.netty.thrift.gate.NTGAgentFactory;
import bma.siteone.netty.thrift.gate.NTGDispatcher;

public class NTGDispatcherCore implements NTGDispatcher {

	final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(NTGDispatcherCore.class);

	private Map<String, NTGAgentFactory> agentFactoryMap;
	private NettyChannelPool pool;

	public NettyChannelPool getPool() {
		return pool;
	}

	public void setPool(NettyChannelPool pool) {
		this.pool = pool;
	}

	public Map<String, NTGAgentFactory> getAgentFactoryMap() {
		return agentFactoryMap;
	}

	public void setAgentFactoryMap(Map<String, NTGAgentFactory> map) {
		if (this.agentFactoryMap == null) {
			this.agentFactoryMap = new HashMap<String, NTGAgentFactory>();
		}
		this.agentFactoryMap.putAll(map);
	}

	public void setServiceHost(Map<String, String> shost) {
		if (agentFactoryMap == null) {
			agentFactoryMap = new HashMap<String, NTGAgentFactory>();
		}
		for (Map.Entry<String, String> e : shost.entrySet()) {
			HostPort host = new HostPort();
			host.setHostString(e.getValue(), 9090);
			NTGAgentFactoryCore fac = new NTGAgentFactoryCore();
			fac.setPool(pool);
			fac.setHost(host);
			agentFactoryMap.put(e.getKey(), fac);
		}
	}

	@Override
	public boolean dispatch(AIStack<NTGAgent> stack, MessageContext ctx) {
		try {
			TMessage msg = GateUtil.readTMessage(ctx);
			return dispatchByMessage(stack, ctx, msg);
		} catch (Exception e) {
			return stack.failure(e);
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
			// find module by name
		}
		if (module == null) {
			String emsg = "thrift method['" + name + "'] unknow";
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
