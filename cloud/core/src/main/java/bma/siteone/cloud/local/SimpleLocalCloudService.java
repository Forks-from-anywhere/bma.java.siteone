package bma.siteone.cloud.local;

import java.util.Collections;
import java.util.Map;

import bma.siteone.cloud.CloudApi;

public class SimpleLocalCloudService extends LocalCloudService {

	@Override
	public String getTrackString() {
		return "service(" + getServiceId() + ")";
	}

	@Override
	public Map<String, CloudApi> createApis() {
		return Collections.emptyMap();
	}

}
