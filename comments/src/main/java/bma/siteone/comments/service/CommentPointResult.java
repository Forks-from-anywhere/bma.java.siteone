package bma.siteone.comments.service;

import java.util.List;

import bma.siteone.comments.po.CommentInfo;
import bma.siteone.comments.po.CommentPoint;

/**
 * 评论点的结果集合
 * 
 * @author guanzhong
 * 
 */
public class CommentPointResult {

	private CommentPoint point;
	private List<CommentInfo> comments;

	public CommentPoint getPoint() {
		return point;
	}

	public void setPoint(CommentPoint point) {
		this.point = point;
	}

	public List<CommentInfo> getComments() {
		return comments;
	}

	public void setComments(List<CommentInfo> comments) {
		this.comments = comments;
	}

}
