package bma.siteone.netty.thrift.gate.impl;

import java.net.URL;

import bma.common.langutil.core.StringUtil;
import bma.common.langutil.io.HostPort;
import bma.common.netty.pool.NettyChannelPool;
import bma.siteone.netty.thrift.remote.RuntimeRemote;

public class ProxyInfo {

	private String type;
	private HostPort host;
	private URL url;
	private String vhost;
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
		this.host =new HostPort();
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
}
