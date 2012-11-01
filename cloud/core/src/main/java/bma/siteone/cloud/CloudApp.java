package bma.siteone.cloud;

import java.util.List;

import bma.common.langutil.ai.stack.AIStack;

public interface CloudApp extends CloudElement {

	public String getAppId();

	public String getAppName();

	public boolean getService(AIStack<CloudService> stack, String id);

	public boolean listService(AIStack<List<CloudService>> stack);
}
