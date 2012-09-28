package bma.siteone.netty.thrift.gate.impl;

import java.util.ArrayList;
import java.util.List;

import bma.common.langutil.core.RoundRobinInteger;
import bma.common.netty.pool.NettyChannelPool;
import bma.siteone.netty.thrift.remote.RuntimeRemote;

public class ProxyInfoGroup {

	private List<ProxyInfo> infoList;
	private RoundRobinInteger roundRobin = new RoundRobinInteger();

	public List<ProxyInfo> getInfoList() {
		return infoList;
	}

	public void setInfoList(List<ProxyInfo> infoList) {
		this.infoList = infoList;
	}

	public static ProxyInfoGroup single(ProxyInfo info) {
		ProxyInfoGroup r = new ProxyInfoGroup();
		r.infoList = new ArrayList<ProxyInfo>(1);
		r.infoList.add(info);
		return r;
	}

	public NTGAgentProcess create(NettyChannelPool pool, RuntimeRemote rr,
			boolean suggestRR) {
		if (infoList == null || infoList.isEmpty()) {
			return null;
		}
		if (infoList.size() == 1) {
			return infoList.get(0).create(pool, rr, suggestRR);
		}
		if (infoList.size() > 1) {
			List<NTGAgentProcess> list = new ArrayList<NTGAgentProcess>();
			for (ProxyInfo pi : infoList) {
				NTGAgentProcess p = pi.create(pool, rr, true);
				list.add(p);
			}
			NTGAgentRoundRobin r = new NTGAgentRoundRobin(roundRobin, list);
			return r;
		}
		return null;
	}

}
