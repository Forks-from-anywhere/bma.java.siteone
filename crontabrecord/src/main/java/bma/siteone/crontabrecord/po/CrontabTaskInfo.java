package bma.siteone.crontabrecord.po;

public class CrontabTaskInfo {
	public enum CrontabRunStatus {
		RUNNING, SUCCESSFUL, FAILED
	}

	/**
	 * 英文名称
	 */
	private String eName;
	/**
	 * 中文名称
	 */
	private String cName;

	/**
	 * 服务名称
	 */
	private String serviceName;
	/**
	 * 详细描述
	 */
	private String description;
	/**
	 *运行状态
	 */
	private CrontabRunStatus status;

	/**
	 * 开始运行时间(s的时间戳)
	 */
	private int startTime;
	/**
	 * 消耗时间（ms）
	 */
	private int elapsedTime;
	/**
	 * 运行结束时间(s的时间戳)
	 */
	private int lastRunTime;

	public String geteName() {
		return eName;
	}

	public void seteName(String eName) {
		this.eName = eName;
	}

	public String getcName() {
		return cName;
	}

	public void setcName(String cName) {
		this.cName = cName;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public CrontabRunStatus getStatus() {
		return status;
	}

	public void setStatus(CrontabRunStatus status) {
		this.status = status;
	}

	public int getStartTime() {
		return startTime;
	}

	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}

	public int getElapsedTime() {
		return elapsedTime;
	}

	public void setElapsedTime(int elapsedTime) {
		this.elapsedTime = elapsedTime;
	}

	public int getLastRunTime() {
		return lastRunTime;
	}

	public void setLastRunTime(int lastRunTime) {
		this.lastRunTime = lastRunTime;
	}

}
