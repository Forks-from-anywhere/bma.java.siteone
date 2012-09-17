package bma.siteone.clound;

import java.util.List;

import bma.common.langutil.ai.stack.AIStack;

public interface CloundService extends CloundElement {

	public String getServiceId();

	public boolean getApi(AIStack<CloundApi> stack, String id);

	public boolean listApi(AIStack<List<CloundApi>> stack);
}
