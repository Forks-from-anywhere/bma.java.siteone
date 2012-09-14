package bma.siteone.clound.local;

import bma.common.json.JsonUtil;
import bma.common.langutil.ai.stack.AIStack;
import bma.common.langutil.core.ValueUtil;
import bma.siteone.clound.CloundRequest;
import bma.siteone.clound.CloundResponse;
import bma.siteone.clound.CloundTrackable;
import bma.siteone.clound.impl.LogTrack;

public abstract class SimpleLocalCloundApi<REQ_TYPE, REP_TYPE> extends
		LocalCloundApi implements CloundTrackable {

	@Override
	public String getTrackString() {
		return "api(" + getApiId() + ")";
	}

	@Override
	public boolean cloundCall(AIStack<CloundResponse> stack, CloundRequest req) {
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
			CloundResponse cr = new CloundResponse();
			cr.setType(CloundResponse.TYPE_DONE);
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
