package bma.siteone.clound.impl;

import java.util.HashMap;
import java.util.Map;

import bma.common.langutil.ai.stack.AIStack;
import bma.siteone.clound.CloundApi;
import bma.siteone.clound.CloundEntry;
import bma.siteone.clound.CloundRequest;
import bma.siteone.clound.CloundResponse;
import bma.siteone.clound.local.LocalCloundApi;
import bma.siteone.clound.local.LocalCloundService;

public class LocalCloundService4Test extends LocalCloundService {

	@Override
	public String getTrackString() {
		return "LocalCloundService4Test";
	}

	@Override
	public Map<String, CloundApi> createApis() {
		Map<String, CloundApi> r = new HashMap<String, CloundApi>();
		r.put("api1", new LocalCloundApi("api1") {

			@Override
			public Map<String, Object> getDesc() {
				return null;
			}

			@Override
			public boolean cloundCall(AIStack<CloundResponse> stack,
					CloundRequest req) {
				CloundResponse rep = new CloundResponse();
				rep.setType(CloundResponse.TYPE_DONE);
				rep.setContent(req.getContent());
				return stack.success(rep);
			}
		});
		r.put("api2", new LocalCloundApi("api2") {

			@Override
			public Map<String, Object> getDesc() {
				return null;
			}

			@Override
			public boolean cloundCall(AIStack<CloundResponse> stack,
					CloundRequest req) {
				CloundResponse rep = new CloundResponse();
				rep.setType(CloundResponse.TYPE_DONE);
				rep.setContent(req.toString());
				return stack.success(rep);
			}
		});
		r.put("api3", new LocalCloundApi("api3") {

			@Override
			public Map<String, Object> getDesc() {
				Map<String, Object> r = new HashMap<String, Object>();
				r.put("title", "redirect");
				return r;
			}

			@Override
			public boolean cloundCall(AIStack<CloundResponse> stack,
					CloundRequest req) {
				CloundEntry re = req.getEntry();
				BaseCloundEntry e = new BaseCloundEntry();
				e.setNodeId(re.getNodeId());
				e.setAppId(re.getAppId());
				e.setServiceId(re.getServiceId());
				e.setApiId("api2");

				CloundResponse rep = new CloundResponse();
				rep.setType(CloundResponse.TYPE_REDIRECT);
				rep.setContent(e.toEntryString());
				return stack.success(rep);
			}
		});
		return r;
	}

	@Override
	public Map<String, Object> getDesc() {
		Map<String, Object> r = new HashMap<String, Object>();
		r.put("test", true);
		r.put("instance", System.identityHashCode(this));
		return r;
	}

}
