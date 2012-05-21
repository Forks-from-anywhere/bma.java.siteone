package bma.siteone.comments.service;

import java.util.List;

import bma.common.langutil.core.PagerResult;
import bma.siteone.comments.po.CommentInfo;
import bma.siteone.comments.po.CommentPoint;

/**
 * 评论服务
 * 
 * @author guanzhong
 * 
 */
public interface CommentsService {

	/**
	 * 创建评论
	 * 
	 * @param form
	 * @param pointForm
	 * @return
	 */
	public int createComment(CommentForm form, CommentPointForm pointForm);

	/**
	 * 更新评论
	 * 
	 * @param id
	 * @param form
	 * @param fields
	 * @return
	 */
	public boolean updateComment(int id, CommentForm form, List<String> fields);

	/**
	 * 删除评论
	 * 
	 * @param id
	 * @return
	 */
	public boolean deleteComment(int id);

	/**
	 * 获得评论
	 * 
	 * @param id
	 * @return
	 */
	public CommentInfo getComment(int id);

	/**
	 * 搜索评论
	 * 
	 * @param form
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public PagerResult<CommentInfo> searchComment(SearchCommentForm form);

	/**
	 * 获得评论点
	 * 
	 * @param id
	 * @return
	 */
	public CommentPoint getCommentPoint(int id);

	/**
	 * 获得评论点
	 * 
	 * @param name
	 * @return
	 */
	public CommentPoint getCommentPoint(String name);

	/**
	 * 创建评论点
	 * 
	 * @param form
	 * @return
	 */
	public int createCommentPoint(CommentPointForm form);

	/**
	 * 更新评论点
	 * 
	 * @param id
	 * @param form
	 * @return
	 */
	public boolean updateCommentPoint(int id, CommentPointForm form);

	/**
	 * 删除评论点
	 * 
	 * @param id
	 * @return
	 */
	public boolean deleteCommentPoint(int id);

	/**
	 * 搜索评论点
	 * 
	 * @param form
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public PagerResult<CommentPoint> searchCommentPoint(
			SearchCommentPointForm form);
}
