package bma.siteone.cloud.local;

import bma.common.json.JsonUtil;
import bma.common.langutil.ai.stack.AIStack;
import bma.common.langutil.core.ValueUtil;
import bma.siteone.cloud.CloudRequest;
import bma.siteone.cloud.CloudResponse;
import bma.siteone.cloud.CloudTrackable;
import bma.siteone.cloud.CloudUtil;
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
			REQ_TYPE param = createRequest(req);
			REP_TYPE rt = execute(param);
			CloudResponse cr;
			if (rt instanceof CloudResponse) {
				cr = (CloudResponse) rt;
			} else {
				cr = new CloudResponse();
				cr.setType(CloudResponse.TYPE_DONE);
				if (rt instanceof byte[]) {
					cr.setContent((byte[]) rt);
				} else if (rt instanceof String) {
					CloudUtil.setTextContent(cr, (String) rt);
				} else {
					CloudUtil.setJsonContent(cr, JsonUtil.getDefaultMapper()
							.writeValueAsString(rt));
				}
			}
			return LogTrack.response(stack, req, cr, this);
		} catch (Exception e) {
			return stack.failure(e);
		}
	}

	public REQ_TYPE createRequest(CloudRequest req) throws Exception {
		Class<REQ_TYPE> cls = getParamClass();
		if (cls.isInstance(req)) {
			return cls.cast(req);
		}
		if (cls.isAssignableFrom(byte[].class)) {
			byte[] o = req.getContent();
			if (o != null)
				return cls.cast(req.getContent());
			return null;
		}
		String ct = CloudUtil.content2string(req);
		if (ct == null) {
			throw new IllegalArgumentException("request content["
					+ req.getContentType() + "] not text");
		}
		REQ_TYPE param = null;
		if (ValueUtil.notEmpty(ct)) {
			if (cls.isInstance(ct)) {
				param = (REQ_TYPE) ct;
			} else {
				param = JsonUtil.getDefaultMapper().readValue(ct, cls);
			}
		}
		return param;
	}

	public abstract Class<REQ_TYPE> getParamClass();

	public abstract REP_TYPE execute(REQ_TYPE req);

}
