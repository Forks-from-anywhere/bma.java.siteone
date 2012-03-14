package bma.siteone.quartz.service;

import java.util.Date;

import org.quartz.CronScheduleBuilder;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

import bma.common.langutil.core.ToStringUtil;
import bma.common.langutil.core.ValueUtil;

public class QuartzTriggerForm {

	public static final String TYPE_SIMPLE = "simple";
	public static final String TYPE_CRON = "cron";

	public static final String MISS_IGNORE = "ignore";
	public static final String MISS_NOW = "now";
	public static final String MISS_NOW_REPEAT = "now_repeat";
	public static final String MISS_NOW_REMAIN_REPEAT = "now_remain_repeat";
	public static final String MISS_NEXT_REPEAT = "next_repeat";
	public static final String MISS_NEXT_REMAIN_REPEAT = "next_remain_repeat";
	public static final String MISS_NOTHING = "nothing";

	private String name;
	private String group;
	private int priority;
	private Date startTime;
	private Date endTime;
	private String type = TYPE_SIMPLE;
	private String missfire;

	// simple
	private int repeat = 0;
	private int interval = 0;

	// cron
	private String cron;

	public String getCron() {
		return cron;
	}

	public void setCron(String cron) {
		this.cron = cron;
	}

	public String getMissfire() {
		return missfire;
	}

	public void setMissfire(String v) {
		this.missfire = v == null ? null : v.toLowerCase();
	}

	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

	public int getRepeat() {
		return repeat;
	}

	public void setRepeat(int repeat) {
		this.repeat = repeat;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
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

	public String getType() {
		return type;
	}

	public void setType(String v) {
		this.type = v == null ? TYPE_SIMPLE : v.toLowerCase();
	}

	public Trigger build(String jobName, String jobGroup) {
		TriggerBuilder<Trigger> b = TriggerBuilder.newTrigger();
		b.withIdentity(getName(), getGroup());
		if (startTime == null) {
			b.startNow();
		} else {
			b.startAt(startTime);
		}
		if (this.endTime != null) {
			b.endAt(endTime);
		}

		if (TYPE_CRON.equals(type)) {
			CronScheduleBuilder sb = CronScheduleBuilder.cronSchedule(cron);
			if (missfire != null) {
				if (MISS_IGNORE.equals(missfire)) {
					sb.withMisfireHandlingInstructionIgnoreMisfires();
				}
				if (MISS_NOW.equals(missfire)) {
					sb.withMisfireHandlingInstructionFireAndProceed();
				}
				if (MISS_NOTHING.equals(missfire)) {
					sb.withMisfireHandlingInstructionDoNothing();
				}
			}
			b.withSchedule(sb);
		} else {
			boolean set = false;
			SimpleScheduleBuilder sb = SimpleScheduleBuilder.simpleSchedule();
			if (repeat > 0) {
				sb.withRepeatCount(repeat);
				set = true;
			} else if (repeat < 0) {
				sb.repeatForever();
				set = true;
			}
			if (interval > 0) {
				sb.withIntervalInMilliseconds(interval);
				set = true;
			}

			if (missfire != null) {
				if (MISS_IGNORE.equals(missfire)) {
					sb.withMisfireHandlingInstructionIgnoreMisfires();
					set = true;
				}
				if (MISS_NOW.equals(missfire)) {
					sb.withMisfireHandlingInstructionFireNow();
					set = true;
				}
				if (MISS_NOW_REPEAT.equals(missfire)) {
					sb.withMisfireHandlingInstructionNowWithExistingCount();
					set = true;
				}
				if (MISS_NOW_REMAIN_REPEAT.equals(missfire)) {
					sb.withMisfireHandlingInstructionNowWithRemainingCount();
					set = true;
				}
				if (MISS_NEXT_REPEAT.equals(missfire)) {
					sb.withMisfireHandlingInstructionNextWithExistingCount();
					set = true;
				}
				if (MISS_NEXT_REMAIN_REPEAT.equals(missfire)) {
					sb.withMisfireHandlingInstructionNextWithRemainingCount();
					set = true;
				}
			}

			if (set) {
				b.withSchedule(sb);
			}
		}
		if (priority > 0) {
			b.withPriority(priority);
		}

		if (ValueUtil.notEmpty(jobName) && ValueUtil.notEmpty(jobGroup)) {
			b.forJob(jobName, jobGroup);
		}

		return b.build();
	}
	
	@Override
	public String toString() {
		return ToStringUtil.fieldReflect(this);
	}
}
