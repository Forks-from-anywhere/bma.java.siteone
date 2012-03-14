package bma.siteone.quartz.service;

import java.util.Map;

import bma.common.langutil.core.ValueUtil;

public class QuartzJobDispatcher {

	private String defaultHandler;
	private Map<String, QuartzJobHandler> handlers;

	public String getDefaultHandler() {
		return defaultHandler;
	}

	public void setDefaultHandler(String defaultHandler) {
		this.defaultHandler = defaultHandler;
	}

	public Map<String, QuartzJobHandler> getHandlers() {
		return handlers;
	}

	public void setHandlers(Map<String, QuartzJobHandler> handlers) {
		this.handlers = handlers;
	}

	public QuartzJobHandler getHandler(String type) {
		QuartzJobHandler r = null;
		if (ValueUtil.notEmpty(type)) {
			if (this.handlers != null) {
				r = this.handlers.get(type);
			}
		}

		if (r == null) {
			if (this.handlers != null) {
				r = this.handlers.get(defaultHandler);
			}
		}

		return r;
	}
}
