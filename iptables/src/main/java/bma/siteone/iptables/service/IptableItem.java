package bma.siteone.iptables.service;

import java.util.List;

import bma.common.langutil.io.InetNetwork;
import bma.siteone.iptables.po.IptableInfo;

public class IptableItem {

	private IptableInfo info;
	List<InetNetwork> list;

	public IptableInfo getInfo() {
		return info;
	}

	public void setInfo(IptableInfo info) {
		this.info = info;
	}

	public List<InetNetwork> getList() {
		return list;
	}

	public void setList(List<InetNetwork> list) {
		this.list = list;
	}

	@Override
	public String toString() {
		return info.toString();
	}
}
