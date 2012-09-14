package bma.siteone.clound.local;

import java.util.Collections;
import java.util.Map;

import bma.siteone.clound.CloundApi;

public class SimpleLocalCloundService extends LocalCloundService {

	@Override
	public String getTrackString() {
		return "service(" + getServiceId() + ")";
	}

	@Override
	public Map<String, CloundApi> createApis() {
		return Collections.emptyMap();
	}

}
