package bma.siteone.wordstock.service;

import java.util.List;

import bma.common.langutil.core.PagerResult;
import bma.siteone.wordstock.po.WordInfo;

public interface WordStockService {

	public static final String TYPE_NORMAL = "normal";
	public static final String TYPE_REGEX = "regex";
	public static final String TYPE_TEXT = "text";

	/**
	 * 获得数据
	 * 
	 * @param name
	 * @return
	 */
	public WordInfo getOne(int id);

	/**
	 * 匹配词汇
	 * 
	 * @param name
	 * @param ip
	 * @return
	 */
	public String match(String groupType, String word);

	/**
	 * 列出所有数据的group
	 * 
	 * @return
	 */
	public List<String> listGroupType();

	public List<WordInfo> listInfo(String groupId);

	/**
	 * 搜索
	 * 
	 * @param hsql
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public PagerResult<WordInfo> search(String sql, int page, int pageSize);

	/**
	 * 创建数据
	 * 
	 * @param name
	 * @param title
	 * @param content
	 * @return
	 */
	public int create(WordInfo info);

	/**
	 * 更新数据
	 * 
	 * @param name
	 * @param title
	 * @param content
	 * @return
	 */
	public boolean update(WordInfo info);

	/**
	 * 更新数据
	 * 
	 * @param name
	 * @return
	 */
	public boolean delete(int id);
}
