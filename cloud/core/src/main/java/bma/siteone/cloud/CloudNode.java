package bma.siteone.cloud;

import java.util.List;

import bma.common.langutil.ai.stack.AIStack;

public interface CloudNode extends CloudElement {

	public String getNodeId();

	public boolean createApp(AIStack<Boolean> stack, String id, String appName);

	public boolean getApp(AIStack<CloudApp> stack, String id);

	public boolean listApp(AIStack<List<CloudApp>> stack);

	public boolean closeApp(AIStack<Boolean> stack, String id);

}
