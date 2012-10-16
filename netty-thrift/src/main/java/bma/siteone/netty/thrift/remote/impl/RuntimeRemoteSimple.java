package bma.siteone.netty.thrift.remote.impl;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import bma.common.langutil.core.ValueUtil;
import bma.common.langutil.io.HostPort;
import bma.siteone.netty.thrift.remote.RuntimeRemote;

public class RuntimeRemoteSimple implements RuntimeRemote {

	protected long timeout = 60 * 1000;

	// runtime
	private ConcurrentHashMap<HostPort, Long> remotes = new ConcurrentHashMap<HostPort, Long>();

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	public void setTimeoutValue(String s) {
		this.timeout = ValueUtil.periodValue(s, this.timeout);
	}

	@Override
	public boolean isRemoteBreak(HostPort host) {
		Long v = remotes.get(host);
		if (v == null)
			return false;
		if (v.longValue() < System.currentTimeMillis() - timeout) {
			remotes.remove(host);
			return false;
		}
		return true;
	}

	@Override
	public boolean isRemoteValid(HostPort host) {
		return !isRemoteValid(host);
	}

	@Override
	public void setRemoteBreak(HostPort host, boolean isBreak) {
		if (isBreak) {
			remotes.putIfAbsent(host, System.currentTimeMillis());
		} else {
			remotes.remove(host);
		}
	}

	@Override
	public Map<String, String> getRemoteInfo(HostPort host) {
		return Collections.emptyMap();
	}

	@Override
	public String getRemoteInfo(HostPort host, String name, String def) {
		return def;
	}

}
