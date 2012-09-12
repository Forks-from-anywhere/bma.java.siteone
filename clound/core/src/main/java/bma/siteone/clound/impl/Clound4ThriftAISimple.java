package bma.siteone.clound.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bma.common.langutil.core.ValueUtil;
import bma.siteone.clound.CloundNode;
import bma.siteone.clound.CloundTrackable;
import bma.siteone.clound.thrift.TAIClound.Iface;

public class Clound4ThriftAISimple extends AbstractClound4ThriftAI implements
		Iface, CloundTrackable {

	final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(Clound4ThriftAISimple.class);

	private Map<String, CloundNode> cloundNodeMap;
	private String thriftTitle;

	public String getThriftTitle() {
		return thriftTitle;
	}

	public void setThriftTitle(String thriftTitle) {
		this.thriftTitle = thriftTitle;
	}

	public Map<String, CloundNode> getCloundNodeMap() {
		return cloundNodeMap;
	}

	public void setCloundNodeMap(Map<String, CloundNode> cloundNodeMap) {
		this.cloundNodeMap = cloundNodeMap;
	}

	public void setCloundNodes(List<CloundNode> list) {
		if (cloundNodeMap == null) {
			cloundNodeMap = new HashMap<String, CloundNode>();
		}
		for (CloundNode node : list) {
			if (ValueUtil.empty(node.getNodeId())) {
				throw new IllegalArgumentException(node + " nodeId is empty");
			}
			cloundNodeMap.put(node.getNodeId(), node);
		}
	}

	@Override
	public CloundNode getCloundNode(String nodeId) {
		return cloundNodeMap.get(nodeId);
	}

	@Override
	public String getTrackString() {
		return ValueUtil.empty(thriftTitle) ? "thrift.face" : thriftTitle;
	}

}
