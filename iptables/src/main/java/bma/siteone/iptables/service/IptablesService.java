package bma.siteone.iptables.service;

import java.util.List;

import bma.common.langutil.core.PagerResult;
import bma.siteone.iptables.po.IptableInfo;

public interface IptablesService {

	public static final String TYPE_NOMATCH = "NOMATCH";
	public static final String TYPE_ACCEPT = "ACCEPT";
	public static final String TYPE_REJECT = "REJECT";

	/**
	 * 获得数据
	 * 
	 * @param name
	 * @return
	 */
	public IptableInfo getOne(int id);

	/**
	 * 匹配IP
	 * 
	 * @param name
	 * @param ip
	 * @return
	 */
	public String match(String groupId, String ip);

	/**
	 * 列出所有数据的group
	 * 
	 * @return
	 */
	public List<String> listGroupType();

	public List<IptableInfo> listInfo(String groupId);

	/**
	 * 搜索
	 * 
	 * @param hsql
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public PagerResult<IptableInfo> search(String sql, int page, int pageSize);

	/**
	 * 创建数据
	 * 
	 * @param name
	 * @param title
	 * @param content
	 * @return
	 */
	public int create(IptableInfo info);

	/**
	 * 更新数据
	 * 
	 * @param name
	 * @param title
	 * @param content
	 * @return
	 */
	public boolean update(IptableInfo info);

	/**
	 * 更新数据
	 * 
	 * @param name
	 * @return
	 */
	public boolean delete(int id);
}
