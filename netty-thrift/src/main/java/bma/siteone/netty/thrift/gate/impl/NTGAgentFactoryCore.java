package bma.siteone.netty.thrift.gate.impl;

import bma.common.langutil.io.HostPort;
import bma.siteone.netty.thrift.gate.NTGAgent;
import bma.siteone.netty.thrift.gate.NTGAgentFactory;

public class NTGAgentFactoryCore implements NTGAgentFactory {

	protected HostPort host;

	public HostPort getHost() {
		return host;
	}

	public void setHost(HostPort host) {
		this.host = host;
	}

	@Override
	public NTGAgent newAgent() {
		return new NTGAgentProxy(host);
	}
}
