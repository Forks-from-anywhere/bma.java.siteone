package bma.siteone.netty.thrift.remote.impl;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import bma.common.langutil.core.DateTimeUtil;
import bma.common.langutil.core.ValueUtil;
import bma.common.langutil.io.HostPort;
import bma.siteone.netty.thrift.remote.RuntimeRemote;

public class RuntimeRemoteSimple implements RuntimeRemote {

	final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(RuntimeRemoteSimple.class);

	protected int maxFails = 1;
	protected long failTime = 10 * 1000;
	protected long timeout = 10 * 1000;

	// runtime
	public static class INFO {
		volatile boolean breaked;
		volatile long time;
		AtomicInteger fails;
	}

	private ConcurrentHashMap<HostPort, INFO> remotes = new ConcurrentHashMap<HostPort, INFO>();

	public int getMaxFails() {
		return maxFails;
	}

	public void setMaxFails(int maxFails) {
		this.maxFails = maxFails;
	}

	public long getFailTime() {
		return failTime;
	}

	public void setFailTime(long failTime) {
		this.failTime = failTime;
	}

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
		INFO v = remotes.get(host);
		if (v == null)
			return false;
		if (v.breaked) {
			if (v.time < System.currentTimeMillis() - timeout) {
				if (log.isDebugEnabled()) {
					log.debug(
							"{} break timeout {}",
							host,
							DateTimeUtil.formatPeriod(System
									.currentTimeMillis() - v.time));
				}
				remotes.remove(host);
				return false;
			}
			if (log.isDebugEnabled()) {
				log.debug("{} BREAK", host);
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean isRemoteValid(HostPort host) {
		return !isRemoteValid(host);
	}

	@Override
	public void setRemoteBreak(HostPort host, boolean isBreak) {
		if (isBreak) {
			INFO info = remotes.get(host);
			if (info == null) {
				info = new INFO();
				info.time = System.currentTimeMillis();
				info.fails = new AtomicInteger();
				INFO old = remotes.putIfAbsent(host, info);
				if (old != null)
					info = old;
			}
			if (!info.breaked) {
				if (info.time < System.currentTimeMillis() - failTime) {
					if (log.isDebugEnabled()) {
						log.debug(
								"{} reset fails[{},{}]",
								new Object[] {
										host,
										info.fails,
										DateTimeUtil.formatPeriod(System
												.currentTimeMillis()
												- info.time) });
					}
					info.time = System.currentTimeMillis();
					info.fails.set(0);
				}
				int f = info.fails.addAndGet(1);
				if (f >= maxFails) {
					if (log.isDebugEnabled()) {
						log.debug("{} set BREAK[{}]", host, info.fails);
					}
					info.time = System.currentTimeMillis();
					info.breaked = true;
				} else {
					if (log.isDebugEnabled()) {
						log.debug("{} fail >> {}", host, info.fails);
					}
				}
			} else {
				info.time = System.currentTimeMillis();
			}
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
