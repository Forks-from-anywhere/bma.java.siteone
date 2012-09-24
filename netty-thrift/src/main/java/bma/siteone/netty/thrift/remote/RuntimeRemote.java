package bma.siteone.netty.thrift.remote;

import java.util.Map;

import bma.common.langutil.io.HostPort;

public interface RuntimeRemote {

	public boolean isRemoteBreak(HostPort host);

	public boolean isRemoteValid(HostPort host);

	public Map<String, String> getRemoteInfo(HostPort host);

	public String getRemoteInfo(HostPort host, String name, String def);

}
