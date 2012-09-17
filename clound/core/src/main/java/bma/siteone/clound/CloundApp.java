package bma.siteone.clound;

import java.util.List;

import bma.common.langutil.ai.stack.AIStack;

public interface CloundApp extends CloundElement {

	public String getAppId();

	public String getAppName();

	public boolean getService(AIStack<CloundService> stack, String id);

	public boolean listService(AIStack<List<CloundService>> stack);
}
