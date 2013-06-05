package bma.siteone.crontabrecord.service;

public interface CrontabInterface {
	
	/**
	 * 执行具体的定时任务工作
	 */
	public void execute();
	
	/**
	 * 获取服务名称
	 * @return
	 */
	public String getService() ;
	
	
	public void stopRankService();
}
