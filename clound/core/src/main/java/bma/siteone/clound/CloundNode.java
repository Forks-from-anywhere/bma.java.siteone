package bma.siteone.clound;

import java.util.List;

import bma.common.langutil.ai.stack.AIStack;

public interface CloundNode extends CloundApi {

	public String getNodeId();

	public boolean createApp(AIStack<Boolean> stack, String id, String appName);

	public boolean getApp(AIStack<CloundApp> stack, String id);

	public boolean listApp(AIStack<List<CloundApp>> stack);

	public boolean closeApp(AIStack<Boolean> stack, String id);

}
