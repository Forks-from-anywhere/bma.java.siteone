package bma.siteone.cloud.impl;

import java.util.HashMap;
import java.util.Map;

import bma.common.langutil.ai.stack.AIStack;
import bma.siteone.cloud.CloudApi;
import bma.siteone.cloud.CloudEntry;
import bma.siteone.cloud.CloudRequest;
import bma.siteone.cloud.CloudResponse;
import bma.siteone.cloud.impl.BaseCloudEntry;
import bma.siteone.cloud.local.LocalCloudApi;
import bma.siteone.cloud.local.LocalCloudService;

public class LocalCloudService4Test extends LocalCloudService {

	@Override
	public String getTrackString() {
		return "LocalCloudService4Test";
	}

	@Override
	public Map<String, CloudApi> createApis() {
		Map<String, CloudApi> r = new HashMap<String, CloudApi>();
		r.put("api1", new LocalCloudApi("api1") {

			@Override
			public Map<String, Object> getDesc() {
				return null;
			}

			@Override
			public boolean cloudCall(AIStack<CloudResponse> stack,
					CloudRequest req) {
				CloudResponse rep = new CloudResponse();
				rep.setType(CloudResponse.TYPE_DONE);
				rep.setContent(req.getContent());
				return stack.success(rep);
			}
		});
		r.put("api2", new LocalCloudApi("api2") {

			@Override
			public Map<String, Object> getDesc() {
				return null;
			}

			@Override
			public boolean cloudCall(AIStack<CloudResponse> stack,
					CloudRequest req) {
				CloudResponse rep = new CloudResponse();
				rep.setType(CloudResponse.TYPE_DONE);
				rep.setContent(req.toString());
				return stack.success(rep);
			}
		});
		r.put("api3", new LocalCloudApi("api3") {

			@Override
			public Map<String, Object> getDesc() {
				Map<String, Object> r = new HashMap<String, Object>();
				r.put("title", "redirect");
				return r;
			}

			@Override
			public boolean cloudCall(AIStack<CloudResponse> stack,
					CloudRequest req) {
				CloudEntry re = req.getEntry();
				BaseCloudEntry e = new BaseCloudEntry();
				e.setNodeId(re.getNodeId());
				e.setAppId(re.getAppId());
				e.setServiceId(re.getServiceId());
				e.setApiId("api2");

				CloudResponse rep = new CloudResponse();
				rep.setType(CloudResponse.TYPE_REDIRECT);
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
