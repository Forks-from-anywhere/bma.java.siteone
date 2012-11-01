package bma.siteone.netty.thrift.remote.impl;

import java.util.List;

import org.apache.thrift.TException;

import bma.common.langutil.ai.stack.AIStack;
import bma.siteone.netty.thrift.remote.thrift.TAIRemoteInfoService.Iface;
import bma.siteone.netty.thrift.remote.thrift.TRemoteInfo;

public class RemoteInfoService4ThriftAI implements Iface {

	private List<TRemoteInfoBuilder> builderList;

	public List<TRemoteInfoBuilder> getBuilderList() {
		return builderList;
	}

	public void setBuilderList(List<TRemoteInfoBuilder> builderList) {
		this.builderList = builderList;
	}

	@Override
	public boolean getRuntimeRemoteInfo(AIStack<TRemoteInfo> stack)
			throws TException {
		TRemoteInfo r = new TRemoteInfo();
		r.setValid(true);
		if (builderList != null) {
			for (TRemoteInfoBuilder b : builderList) {
				b.build(r);
			}
		}
		return stack.success(r);
	}
}
