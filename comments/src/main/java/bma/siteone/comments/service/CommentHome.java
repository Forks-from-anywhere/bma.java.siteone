package bma.siteone.comments.service;

import java.util.List;

import bma.siteone.comments.po.CommentPoint;

/**
 * 评论的缓冲数据
 * 
 * @author guanzhong
 * 
 */
public class CommentHome {

	public static final CommentPoint NULL = new CommentPoint();

	private CommentPoint point;
	private int total = -1;
	private List<Integer> comments;

	public CommentPoint getPoint() {
		return point;
	}

	public void setPoint(CommentPoint point) {
		this.point = point;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public List<Integer> getComments() {
		return comments;
	}

	public void setComments(List<Integer> comments) {
		this.comments = comments;
	}

	public CommentPoint sure() {
		if (this.point == NULL)
			return null;
		return this.point;
	}

	public boolean isValid() {
		return this.point == null ? false : this.point != NULL;
	}

	public boolean match(int pageSize) {
		if (total < 0)
			return false;
		if (this.comments == null)
			return false;
		if (this.comments.size() < pageSize) {
			return this.total == this.comments.size();
		}
		return true;
	}

	@Override
	public String toString() {
		return "CHome[total=" + total + ";size="
				+ (comments == null ? 0 : comments.size()) + "]";
	}
}
