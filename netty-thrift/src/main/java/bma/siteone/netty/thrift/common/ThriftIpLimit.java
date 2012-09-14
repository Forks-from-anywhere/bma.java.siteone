package bma.siteone.netty.thrift.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import bma.common.langutil.core.ValueUtil;
import bma.common.langutil.io.InetNetwork;
import bma.common.langutil.runtime.RuntimeConfig;
import bma.common.langutil.runtime.RuntimeConfigListener;
import bma.common.netty.handler.ChannelHandlerIpFilter;

public class ThriftIpLimit extends ChannelHandlerIpFilter {

	final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(ThriftIpLimit.class);

	protected RuntimeConfig runtimeConfig;
	protected String configKey = "thrift.iplimit.";

	// runtime
	protected List<InetNetwork> orgWhiteList;
	protected List<InetNetwork> orgBlackList;

	public RuntimeConfig getRuntimeConfig() {
		return runtimeConfig;
	}

	public void setRuntimeConfig(RuntimeConfig runtimeConfig) {
		this.runtimeConfig = runtimeConfig;
	}

	public String getConfigKey() {
		return configKey;
	}

	public void setConfigKey(String configKey) {
		this.configKey = configKey;
	}

	public void init() {
		if (orgWhiteList == null) {
			orgWhiteList = queryWhiteList();
		}
		if (orgBlackList == null) {
			orgBlackList = queryBlackList();
		}
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
			log.info("read IpLimit runtime config");
		}

		List<InetNetwork> wlist = new ArrayList<InetNetwork>();
		List<InetNetwork> blist = new ArrayList<InetNetwork>();

		int c;
		wlist.addAll(orgWhiteList);
		c = ValueUtil.intValue(
				runtimeConfig.getConfig(configKey + "whiteList.count"), 0);
		for (int i = 1; i <= c; i++) {
			String s = runtimeConfig.getConfig(configKey + "whiteList." + i);
			if (ValueUtil.notEmpty(s)) {
				List<InetNetwork> tmp = create(s);
				if (tmp != null)
					wlist.addAll(tmp);
			}
		}

		blist.addAll(orgBlackList);
		c = ValueUtil.intValue(
				runtimeConfig.getConfig(configKey + "blackList.count"), 0);
		for (int i = 1; i <= c; i++) {
			String s = runtimeConfig.getConfig(configKey + "blackList." + i);
			if (ValueUtil.notEmpty(s)) {
				List<InetNetwork> tmp = create(s);
				if (tmp != null)
					blist.addAll(tmp);
			}
		}

		replaceWhiteList(wlist);
		replaceBlackList(blist);

	}
}
