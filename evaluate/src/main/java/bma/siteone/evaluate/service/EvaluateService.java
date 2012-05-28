package bma.siteone.evaluate.service;

import java.util.List;
import java.util.Set;

import bma.common.langutil.core.PagerResult;
import bma.siteone.evaluate.po.EvaluateInfo;

public interface EvaluateService {

	/**
	 * 获得数据
	 * 
	 * @param name
	 * @return
	 */
	public EvaluateInfo getOne(String groupType, String itemId);

	/**
	 * 列出所有数据的group
	 * 
	 * @return
	 */
	public List<String> listGroupType();

	/**
	 * 搜索
	 * 
	 * @param hsql
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public PagerResult<EvaluateInfo> search(String sql, int page, int pageSize);

	/**
	 * 评论
	 * 
	 * @param name
	 * @param title
	 * @param content
	 * @return
	 */
	public boolean vote(EvaluateInfo info, Set<Integer> options);

	/**
	 * 更新数据
	 * 
	 * @param name
	 * @param title
	 * @param content
	 * @return
	 */
	public boolean update(EvaluateInfo info);

	/**
	 * 删除数据
	 * 
	 * @param name
	 * @return
	 */
	public boolean delete(String groupType, String itemId);
	
	public boolean deleteGroup(String groupType);
}
