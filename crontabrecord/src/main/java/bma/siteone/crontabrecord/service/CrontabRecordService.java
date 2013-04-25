package bma.siteone.crontabrecord.service;

import bma.siteone.crontabrecord.po.CrontabTaskInfo;

public interface CrontabRecordService {

	/**
	 * 开始运行定时任务
	 */
	public void startCrontabTask(CrontabTaskInfo info);

	/**
	 * 顺利运行任务
	 * 
	 * @param info
	 * @param elapsedTime
	 *            消耗时间(ms)
	 */
	public void endCrontabTaskWhenSuccessfully(CrontabTaskInfo info, int elapsedTime);

	/**
	 * 运行任务失败
	 * 
	 * @param info
	 * @param elapsedTime
	 *            消耗时间 (ms)
	 */
	public void endCrontabTaskWhenFailed(CrontabTaskInfo info, int elapsedTime);
}
