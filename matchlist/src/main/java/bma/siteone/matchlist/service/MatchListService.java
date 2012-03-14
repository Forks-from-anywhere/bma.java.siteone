package bma.siteone.matchlist.service;

import java.util.List;

import bma.common.hibernate.HibernateSearch;
import bma.common.langutil.core.PagerResult;
import bma.siteone.matchlist.po.MatchInfo;

public interface MatchListService {

	/**
	 * 获得数据
	 * 
	 * @param name
	 * @return
	 */
	public MatchInfo getOne(int id);

	/**
	 * 匹配数据
	 * 
	 * @param name
	 * @param ip
	 * @return
	 */
	public boolean match(String groupId, String v, String type);

	/**
	 * 列出所有数据的group
	 * 
	 * @return
	 */
	public List<String> listGroupId();

	public List<MatchInfo> listGroup(String groupId);

	/**
	 * 搜索
	 * 
	 * @param hsql
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public PagerResult<MatchInfo> search(HibernateSearch search, int page,
			int pageSize);

	/**
	 * 创建数据
	 * 
	 * @param name
	 * @param title
	 * @param content
	 * @return
	 */
	public int create(MatchInfo info);

	/**
	 * 更新数据
	 * 
	 * @param name
	 * @param title
	 * @param content
	 * @return
	 */
	public boolean update(MatchInfo info);

	/**
	 * 更新数据
	 * 
	 * @param name
	 * @return
	 */
	public boolean delete(int id);
}
