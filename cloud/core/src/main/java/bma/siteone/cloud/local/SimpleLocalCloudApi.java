package bma.siteone.cloud.local;

import bma.common.json.JsonUtil;
import bma.common.langutil.ai.stack.AIStack;
import bma.common.langutil.core.ValueUtil;
import bma.siteone.cloud.CloudRequest;
import bma.siteone.cloud.CloudResponse;
import bma.siteone.cloud.CloudTrackable;
import bma.siteone.cloud.impl.LogTrack;

public abstract class SimpleLocalCloudApi<REQ_TYPE, REP_TYPE> extends
		LocalCloudApi implements CloudTrackable {

	@Override
	public String getTrackString() {
		return "api(" + getApiId() + ")";
	}

	@Override
	public boolean cloudCall(AIStack<CloudResponse> stack, CloudRequest req) {
		try {
			String ct = req.getContent();
			REQ_TYPE param = null;
			if (ValueUtil.notEmpty(ct)) {
				Class<REQ_TYPE> cls = getParamClass();
				if (cls.isInstance(ct)) {
					param = (REQ_TYPE) ct;
				} else {
					param = JsonUtil.getDefaultMapper().readValue(ct, cls);
				}
			}
			REP_TYPE rt = execute(param);
			CloudResponse cr = new CloudResponse();
			cr.setType(CloudResponse.TYPE_DONE);
			if (rt instanceof String) {
				cr.setContent((String) rt);
			} else {
				cr.setContent(JsonUtil.getDefaultMapper()
						.writeValueAsString(rt));
			}
			return LogTrack.response(stack, req, cr, this);
		} catch (Exception e) {
			return stack.failure(e);
		}
	}

	public abstract Class<REQ_TYPE> getParamClass();

	public abstract REP_TYPE execute(REQ_TYPE req);

}
