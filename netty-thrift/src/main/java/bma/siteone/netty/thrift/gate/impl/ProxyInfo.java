package bma.siteone.netty.thrift.gate.impl;

import java.net.MalformedURLException;
import java.net.URL;

import bma.common.langutil.core.ExceptionUtil;
import bma.common.langutil.core.StringUtil;
import bma.common.langutil.core.ValueUtil;
import bma.common.langutil.io.HostPort;
import bma.common.langutil.runtime.RuntimeConfig;
import bma.common.netty.pool.NettyChannelPool;
import bma.siteone.netty.thrift.remote.RuntimeRemote;

public class ProxyInfo {

	private String type;
	private HostPort host;
	private URL url;
	private String vhost;
	private int weight = 1;
	private boolean checkRuntimeRemote = true;

	public boolean isSocket() {
		if (StringUtil.equalsIgnoreCase(type, "socket"))
			return true;
		if (host != null)
			return true;
		return false;
	}

	public boolean isCheckRuntimeRemote() {
		if (isSocket())
			return checkRuntimeRemote;
		return false;
	}

	public void setCheckRuntimeRemote(boolean checkRuntimeRemote) {
		this.checkRuntimeRemote = checkRuntimeRemote;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public HostPort getHost() {
		return host;
	}

	public void setHost(HostPort host) {
		this.host = host;
	}

	public void setHostString(String s) {
		this.host = new HostPort();
		this.host.setHostString(s, 9090);
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	public String getVhost() {
		return vhost;
	}

	public void setVhost(String vhost) {
		this.vhost = vhost;
	}

	public NTGAgentProxy create(NettyChannelPool pool, RuntimeRemote rr,
			boolean suggestRR) {
		if (isSocket()) {
			return new NTGAgentProxy(pool, getHost(), isCheckRuntimeRemote(),
					suggestRR ? rr : null);
		} else {
			NTGAgentProxy proxy = new NTGAgentProxy(pool, getUrl());
			proxy.setVHost(getVhost());
			return proxy;
		}
	}

	public static ProxyInfo readFromConfig(RuntimeConfig cfg, String mkey) {
		ProxyInfo o = new ProxyInfo();
		o.type = cfg.getConfig(mkey + ".type");
		String host = cfg.getConfig(mkey + ".host");
		if (ValueUtil.notEmpty(host)) {
			o.host = new HostPort();
			o.host.setHostString(host, 9090);
		}
		String url = cfg.getConfig(mkey + ".url");
		if (ValueUtil.notEmpty(url)) {
			try {
				o.url = new URL(url);
			} catch (MalformedURLException e) {
				throw ExceptionUtil.throwRuntime(e);
			}
		}
		if (o.host == null && o.url == null) {
			return null;
		}
		o.vhost = cfg.getConfig(mkey + ".vhost");
		o.weight = ValueUtil
				.intValue(cfg.getConfig(mkey + ".weight"), o.weight);
		o.checkRuntimeRemote = ValueUtil.booleanValue(
				cfg.getConfig(mkey + ".check"), o.checkRuntimeRemote);
		return o;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		if (isSocket()) {
			sb.append(host);
		} else {
			sb.append(url);
			if (ValueUtil.notEmpty(vhost)) {
				sb.append(";").append("vhost=").append(vhost);
			}
		}
		if (checkRuntimeRemote) {
			sb.append(";CHECK");
		}
		sb.append("]");
		return sb.toString();
	}
}
