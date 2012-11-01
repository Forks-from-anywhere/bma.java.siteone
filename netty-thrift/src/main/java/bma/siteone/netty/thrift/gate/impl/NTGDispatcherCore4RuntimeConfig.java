package bma.siteone.netty.thrift.gate.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import bma.common.langutil.ai.executor.AIExecutor;
import bma.common.langutil.ai.stack.AIStack;
import bma.common.langutil.concurrent.TimerManager;
import bma.common.langutil.core.DateTimeUtil;
import bma.common.langutil.core.ValueUtil;
import bma.common.langutil.runtime.RuntimeConfig;
import bma.common.langutil.runtime.RuntimeConfigListener;
import bma.common.netty.pool.NettyChannelPool;
import bma.siteone.netty.thrift.gate.MessageContext;
import bma.siteone.netty.thrift.gate.NTGAgent;
import bma.siteone.netty.thrift.gate.NTGAgentFactory;
import bma.siteone.netty.thrift.gate.NTGDispatcher;
import bma.siteone.netty.thrift.remote.RemoteBreakException;
import bma.siteone.netty.thrift.remote.RuntimeRemote;

public class NTGDispatcherCore4RuntimeConfig implements NTGDispatcher,
		NTGAgentFactoryCoreParent {

	final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(NTGDispatcherCore4RuntimeConfig.class);

	protected RuntimeConfig runtimeConfig;
	protected NettyChannelPool pool;
	protected RuntimeRemote runtimeRemote;
	protected long timeout;
	protected TimerManager timer;
	protected String configKey = "thrift.gate.";

	// runtime
	private NTGDispatcherCore dispatcher;

	public String getConfigKey() {
		return configKey;
	}

	public void setConfigKey(String configKey) {
		this.configKey = configKey;
	}

	@Override
	public NettyChannelPool getPool() {
		return pool;
	}

	public void setPool(NettyChannelPool pool) {
		this.pool = pool;
	}

	public RuntimeConfig getRuntimeConfig() {
		return runtimeConfig;
	}

	public void setRuntimeConfig(RuntimeConfig runtimeConfig) {
		this.runtimeConfig = runtimeConfig;
	}

	@Override
	public RuntimeRemote getRuntimeRemote() {
		return runtimeRemote;
	}

	public void setRuntimeRemote(RuntimeRemote runtimeRemote) {
		this.runtimeRemote = runtimeRemote;
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	public void setTimeoutValue(String s) {
		this.timeout = DateTimeUtil.parsePeriodValue(s, this.timeout);
	}

	public TimerManager getTimer() {
		return timer;
	}

	public void setTimer(TimerManager timer) {
		this.timer = timer;
	}

	@Override
	public TimerManager getTimerManager() {
		if (this.timer != null) {
			return this.timer;
		}
		return AIExecutor.getTimerManager();
	}

	@Override
	public boolean dispatch(AIStack<NTGAgent> stack, MessageContext ctx) {
		if (this.dispatcher != null) {
			return this.dispatcher.dispatch(stack, ctx);
		} else {
			return stack.failure(new RemoteBreakException(
					"empty dispatch config"));
		}
	}

	public void init() {
		if (this.runtimeConfig != null) {
			this.runtimeConfig.addListener(new RuntimeConfigListener() {

				@Override
				public void runtimeConfigChange(Collection<String> keys) {
					for (String k : keys) {
						if (k.startsWith(configKey)) {
							readRuntimeConfig();
							return;
						}
					}
				}
			});
			readRuntimeConfig();
		}
	}

	protected void readRuntimeConfig() {
		if (this.runtimeConfig == null)
			return;

		if (log.isInfoEnabled()) {
			log.info("read netty-thritft-gate dispatcher runtime config");
		}

		int c;
		c = ValueUtil.intValue(runtimeConfig.getConfig(configKey + "count"), 0);
		Map<String, NTGAgentFactory> mfMap = new HashMap<String, NTGAgentFactory>(
				c);
		for (int i = 1; i <= c; i++) {
			String mkey = configKey + "module." + i;
			String moduleName = runtimeConfig.getConfig(mkey);
			NTGAgentFactoryCore fac = new NTGAgentFactoryCore();
			fac.setParent(this);
			fac.readFromConfig(runtimeConfig, mkey);
			if (log.isInfoEnabled()) {
				log.info("  {} => {}", moduleName, fac);
			}
			mfMap.put(moduleName, fac);
		}
		NTGDispatcherCore o = new NTGDispatcherCore();
		o.setAgentFactoryMap(mfMap);
		this.dispatcher = o;
	}
}
