package bma.siteone.comments.service;

import java.util.Map;
import java.util.TreeMap;

import bma.common.langutil.core.PagerResult;
import bma.siteone.comments.po.CommentInfo;
import bma.siteone.comments.po.CommentPoint;

public class CommentPointCacheItem {

	private CommentPoint point;

	private Map<String, PagerResult> parts = new TreeMap<String, PagerResult>();

	public CommentPoint getPoint() {
		return point;
	}

	public void setPoint(CommentPoint point) {
		this.point = point;
	}

	public Map<String, PagerResult> getParts() {
		return parts;
	}

	public void setParts(Map<String, PagerResult> parts) {
		this.parts = parts;
	}

	public synchronized void clear(String key) {
		parts.remove(key);
	}

	public synchronized void put(String key, PagerResult<CommentInfo> result) {
		parts.put(key, result);
	}

	public synchronized PagerResult<CommentInfo> get(String cache) {
		return parts.get(cache);
	}

}
