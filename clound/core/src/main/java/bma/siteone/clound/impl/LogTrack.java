package bma.siteone.clound.impl;

import bma.common.langutil.ai.stack.AIStack;
import bma.common.langutil.ai.stack.AIStackFilter;
import bma.siteone.clound.CloundApi;
import bma.siteone.clound.CloundRequest;
import bma.siteone.clound.CloundResponse;
import bma.siteone.clound.CloundTrackable;

public class LogTrack {

	public static boolean call(CloundApi api, AIStack<CloundResponse> stack,
			CloundRequest req, CloundTrackable ct) {
		return api.cloundCall(filter(stack, req, ct), req);
	}

	public static boolean response(AIStack<CloundResponse> stack,
			CloundRequest req, CloundResponse resp, CloundTrackable ct) {
		if (req.isLogtrack()) {
			resp.appendLog(ct.getTrackString());
		}
		return stack.success(resp);
	}

	public static AIStack<CloundResponse> filter(AIStack<CloundResponse> stack,
			CloundRequest req, final CloundTrackable lt) {
		if (!req.isLogtrack()) {
			return stack;
		}
		AIStackFilter<CloundResponse> filter = new AIStackFilter<CloundResponse>(
				stack) {
			@Override
			public boolean success(CloundResponse result) {
				if (result != null) {
					result.appendLog(lt.getTrackString());
				}
				return super.success(result);
			}
		};
		return filter;
	}
}
