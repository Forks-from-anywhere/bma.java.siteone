package bma.siteone.cloud;

import java.util.List;

import bma.common.langutil.ai.stack.AIStack;

public interface CloudService extends CloudElement {

	public String getServiceId();

	public boolean getApi(AIStack<CloudApi> stack, String id);

	public boolean listApi(AIStack<List<CloudApi>> stack);
}
