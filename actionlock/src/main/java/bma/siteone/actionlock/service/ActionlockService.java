package bma.siteone.actionlock.service;

import java.util.List;

import bma.common.langutil.core.PagerResult;
import bma.siteone.actionlock.po.ActionlockInfo;

public interface ActionlockService {

	/**
	 * 获得数据
	 * 
	 * @param groupType
	 * @param itemId
	 * @param timeout
	 *            是否查询超时数据
	 * @return
	 */
	public ActionlockInfo getOne(String groupType, String itemId,
			boolean timeout);

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
	public PagerResult<ActionlockInfo> search(String sql, int page, int pageSize);

	/**
	 * 加锁
	 * 
	 * @param groupType
	 * @param itemId
	 * @param accessCount
	 * @param cleanTime
	 * @return
	 */
	public int lock(String groupType, String itemId, int accessCount,
			long cleanDelay);

	/**
	 * 检测锁
	 * 
	 * @param groupType
	 * @param itemId
	 * @param accessCount
	 *            >=该数则返回true
	 * @param release
	 *            返回true的时候是否清除
	 * @return
	 */
	public boolean checkLock(String groupType, String itemId, int accessCount,
			boolean release);

	/**
	 * 删除数据
	 * 
	 * @param name
	 * @return
	 */
	public boolean delete(String groupType, String itemId);

	public boolean deleteGroup(String groupType);

	/**
	 * 清理过期
	 * 
	 * @param groupType
	 *            =null表示全部
	 */
	public void clean(String groupType);
}
