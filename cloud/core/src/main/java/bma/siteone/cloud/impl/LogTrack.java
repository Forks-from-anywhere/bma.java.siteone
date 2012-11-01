package bma.siteone.cloud.impl;

import bma.common.langutil.ai.stack.AIStack;
import bma.common.langutil.ai.stack.AIStackFilter;
import bma.siteone.cloud.CloudElement;
import bma.siteone.cloud.CloudRequest;
import bma.siteone.cloud.CloudResponse;
import bma.siteone.cloud.CloudTrackable;

public class LogTrack {

	public static boolean call(CloudElement el, AIStack<CloudResponse> stack,
			CloudRequest req, CloudTrackable ct) {
		return el.cloudCall(filter(stack, req, ct), req);
	}

	public static boolean response(AIStack<CloudResponse> stack,
			CloudRequest req, CloudResponse resp, CloudTrackable ct) {
		if (req.isLogtrack()) {
			resp.appendLog(ct.getTrackString());
		}
		return stack.success(resp);
	}

	public static AIStack<CloudResponse> filter(AIStack<CloudResponse> stack,
			CloudRequest req, final CloudTrackable lt) {
		if (!req.isLogtrack()) {
			return stack;
		}
		AIStackFilter<CloudResponse> filter = new AIStackFilter<CloudResponse>(
				stack) {
			@Override
			public boolean success(CloudResponse result) {
				if (result != null) {
					result.appendLog(lt.getTrackString());
				}
				return super.success(result);
			}
		};
		return filter;
	}
}
