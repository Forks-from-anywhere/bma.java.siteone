package bma.siteone.quartz.service;

import java.util.Map;

import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;

import bma.common.langutil.core.ToStringUtil;

public class QuartzJobForm {

	public static final String KEY_TYPE = "__type";

	private String name;
	private String group;
	private boolean durability;
	private boolean recover;
	private boolean disallowConcurrent;
	private boolean persistAfterExecution;
	private String type;
	private Map<String, String> jobDatas;

	public boolean isDurability() {
		return durability;
	}

	public void setDurability(boolean durability) {
		this.durability = durability;
	}

	public boolean isRecover() {
		return recover;
	}

	public void setRecover(boolean recover) {
		this.recover = recover;
	}

	public boolean isDisallowConcurrent() {
		return disallowConcurrent;
	}

	public void setDisallowConcurrent(boolean disallowConcurrent) {
		this.disallowConcurrent = disallowConcurrent;
	}

	public boolean isPersistAfterExecution() {
		return persistAfterExecution;
	}

	public void setPersistAfterExecution(boolean persistAfterExecution) {
		this.persistAfterExecution = persistAfterExecution;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public Map<String, String> getJobDatas() {
		return jobDatas;
	}

	public void setJobDatas(Map<String, String> jobDatas) {
		this.jobDatas = jobDatas;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public JobDetail build() {
		Class<? extends Job> clz = ServiceJob.class;
		if (this.disallowConcurrent) {
			if (this.persistAfterExecution) {
				clz = ServiceJobDCPA.class;
			} else {
				clz = ServiceJobDC.class;
			}
		} else {
			if (this.persistAfterExecution) {
				clz = ServiceJobPA.class;
			}
		}
		JobBuilder b = JobBuilder.newJob(clz).withIdentity(getName(),
				getGroup());
		if (durability) {
			b.storeDurably(durability);
		}
		if (recover) {
			b.requestRecovery(recover);
		}

		if (this.jobDatas != null) {
			for (Map.Entry<String, String> e : this.jobDatas.entrySet()) {
				b.usingJobData(e.getKey(), e.getValue());
			}
		}
		if (this.type != null) {
			b.usingJobData(KEY_TYPE, this.type);
		}

		return b.build();
	}
	
	@Override
	public String toString() {
		return ToStringUtil.fieldReflect(this);
	}
}
