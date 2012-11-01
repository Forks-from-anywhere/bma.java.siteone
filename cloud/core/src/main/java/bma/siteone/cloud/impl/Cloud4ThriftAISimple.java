package bma.siteone.cloud.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bma.common.langutil.core.ValueUtil;
import bma.siteone.cloud.CloudNode;
import bma.siteone.cloud.CloudTrackable;
import bma.siteone.cloud.thrift.TAICloud.Iface;

public class Cloud4ThriftAISimple extends AbstractCloud4ThriftAI implements
		Iface, CloudTrackable {

	final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(Cloud4ThriftAISimple.class);

	private Map<String, CloudNode> cloudNodeMap;
	private String thriftTitle;

	public String getThriftTitle() {
		return thriftTitle;
	}

	public void setThriftTitle(String thriftTitle) {
		this.thriftTitle = thriftTitle;
	}

	public Map<String, CloudNode> getCloudNodeMap() {
		return cloudNodeMap;
	}

	public void setCloudNodeMap(Map<String, CloudNode> cloudNodeMap) {
		this.cloudNodeMap = cloudNodeMap;
	}

	public void setCloudNodes(List<CloudNode> list) {
		if (cloudNodeMap == null) {
			cloudNodeMap = new HashMap<String, CloudNode>();
		}
		for (CloudNode node : list) {
			if (ValueUtil.empty(node.getNodeId())) {
				throw new IllegalArgumentException(node + " nodeId is empty");
			}
			cloudNodeMap.put(node.getNodeId(), node);
		}
	}

	@Override
	public CloudNode getCloudNode(String nodeId) {
		return cloudNodeMap.get(nodeId);
	}

	@Override
	public String getTrackString() {
		return ValueUtil.empty(thriftTitle) ? "thrift.face" : thriftTitle;
	}

}
