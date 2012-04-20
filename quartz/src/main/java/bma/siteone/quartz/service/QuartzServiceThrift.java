package bma.siteone.quartz.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.thrift.TException;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.ScheduleBuilder;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;

import bma.common.langutil.bean.copy.BeanCopyTool;
import bma.common.langutil.convert.common.DateFormatConverter;
import bma.common.langutil.core.DateTimeUtil;
import bma.common.langutil.core.ValueUtil;
import bma.siteone.quartz.thrift.TJobForm;
import bma.siteone.quartz.thrift.TJobInfo;
import bma.siteone.quartz.thrift.TQuartzService;
import bma.siteone.quartz.thrift.TTriggerForm;
import bma.siteone.quartz.thrift.TTriggerInfo;

public class QuartzServiceThrift implements TQuartzService.Iface {

	final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(QuartzServiceThrift.class);

	private String defaultService;
	private Map<String, QuartzService> services;

	private transient BeanCopyTool target;

	public QuartzServiceThrift() {
		super();
		target = new BeanCopyTool();
		target.setSourceStruct(false);
		initBeanCopy(target);
	}

	protected void initBeanCopy(BeanCopyTool tool) {
		tool.field("startTime").converter(DateFormatConverter.DATE_TIME);
		tool.field("endTime").converter(DateFormatConverter.DATE_TIME);
	}

	public String getDefaultService() {
		return defaultService;
	}

	public void setDefaultService(String defaultService) {
		this.defaultService = defaultService;
	}

	public Map<String, QuartzService> getServices() {
		return services;
	}

	public void setServices(Map<String, QuartzService> services) {
		this.services = services;
	}

	public QuartzService getService(String name) {
		QuartzService r = null;
		if (ValueUtil.notEmpty(name)) {
			if (this.services != null) {
				r = this.services.get(name);
			}
		}

		if (r == null) {
			if (this.services != null) {
				r = this.services.get(defaultService);
			}
		}

		if (r == null) {
			throw new NullPointerException("[" + name
					+ "] QuartzService not found");
		}

		return r;
	}

	@Override
	public boolean pause(String serviceName, boolean pause) throws TException {
		if (log.isDebugEnabled()) {
			log.debug("pause({},{})", serviceName, pause);
		}
		QuartzService s = getService(serviceName);
		try {
			return s.pause(pause);
		} catch (SchedulerException e) {
			throw new TException(e);
		}
	}

	@Override
	public String newJob(String serviceName, TJobForm job, TTriggerForm trigger)
			throws TException {
		if (log.isDebugEnabled()) {
			log.debug("newJob({},{},{})", new Object[] { serviceName, job,
					trigger });
		}
		QuartzService s = getService(serviceName);
		try {
			QuartzJobForm jform = target.newInstance(null, job,
					QuartzJobForm.class);
			QuartzTriggerForm form = target.newInstance(null, trigger,
					QuartzTriggerForm.class);
			Date r = s.newJob(jform, form);
			if (r == null)
				return null;
			return DateTimeUtil.formatDateTime(r);
		} catch (SchedulerException e) {
			throw new TException(e);
		}
	}

	@Override
	public String newTrigger(String serviceName, String jobName,
			String jobGroup, TTriggerForm trigger) throws TException {
		if (log.isDebugEnabled()) {
			log.debug("newTrigger({},{},{},{})", new Object[] { serviceName,
					jobName, jobGroup, trigger });
		}
		QuartzService s = getService(serviceName);
		try {
			QuartzTriggerForm form = target.newInstance(null, trigger,
					QuartzTriggerForm.class);
			Date r = s.newTrigger(jobName, jobGroup, form);
			if (r == null)
				return null;
			return DateTimeUtil.formatDateTime(r);
		} catch (SchedulerException e) {
			throw new TException(e);
		}
	}

	@Override
	public boolean removeJob(String serviceName, String jobName, String jobGroup)
			throws TException {
		if (log.isDebugEnabled()) {
			log.debug("removeJob({},{},{})", new Object[] { serviceName,
					jobName, jobGroup });
		}
		QuartzService s = getService(serviceName);
		try {
			return s.removeJob(jobName, jobGroup);
		} catch (SchedulerException e) {
			throw new TException(e);
		}
	}

	@Override
	public boolean removeTrigger(String serviceName, String triggerName,
			String triggerGroup) throws TException {
		if (log.isDebugEnabled()) {
			log.debug("removeTrigger({},{},{})", new Object[] { serviceName,
					triggerName, triggerGroup });
		}
		QuartzService s = getService(serviceName);
		try {
			return s.removeTrigger(triggerName, triggerGroup);
		} catch (SchedulerException e) {
			throw new TException(e);
		}
	}

	@Override
	public TJobInfo queryJob(String serviceName, String jobName, String jobGroup)
			throws TException {
		if (log.isDebugEnabled()) {
			log.debug("queryJob({},{},{})", new Object[] { serviceName,
					jobName, jobGroup });
		}
		QuartzService s = getService(serviceName);
		try {
			JobDetail o = s.queryJob(jobName, jobGroup);
			if (o == null)
				return null;

			TJobInfo r = new TJobInfo();
			r.setDisallowConcurrent(o.isConcurrentExectionDisallowed());
			r.setDurability(o.isDurable());
			r.setGroup(o.getKey().getGroup());
			r.setName(o.getKey().getName());
			r.setPersistAfterExecution(o.isPersistJobDataAfterExecution());

			JobDataMap jdm = o.getJobDataMap();
			Map<String, String> m = new HashMap<String, String>();
			String[] keys = jdm.getKeys();
			for (String k : keys) {
				if (k.equals(QuartzJobForm.KEY_TYPE))
					continue;
				m.put(k, jdm.getString(k));
			}
			r.setJobDatas(m);
			r.setType(jdm.getString(QuartzJobForm.KEY_TYPE));
			return r;

		} catch (SchedulerException e) {
			throw new TException(e);
		}
	}

	@Override
	public TTriggerInfo queryTrigger(String serviceName, String triggerName,
			String triggerGroup) throws TException {
		if (log.isDebugEnabled()) {
			log.debug("queryTrigger({},{},{})", new Object[] { serviceName,
					triggerName, triggerGroup });
		}
		QuartzService s = getService(serviceName);
		try {
			Trigger o = s.queryTrigger(triggerName, triggerGroup);
			if (o == null)
				return null;

			TTriggerInfo r = new TTriggerInfo();

			r.setEndTime(DateTimeUtil.formatDateTime(o.getEndTime()));
			r.setGroup(o.getKey().getGroup());
			r.setName(o.getKey().getName());
			r.setNextFireTime(DateTimeUtil.formatDateTime(o.getNextFireTime()));
			r.setPreviousFireTime(DateTimeUtil.formatDateTime(o
					.getPreviousFireTime()));
			ScheduleBuilder b = o.getScheduleBuilder();
			if (b != null) {
				r.setSchedule(b.toString());
			}

			String mf = null;
			switch (o.getMisfireInstruction()) {
			case SimpleTrigger.MISFIRE_INSTRUCTION_SMART_POLICY:
				mf = "smart";
				break;
			case SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW:
				mf = "now";
				break;
			case SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_EXISTING_COUNT:
				mf = "next";
				break;
			case SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT:
				mf = "nextRemain";
				break;
			case SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_EXISTING_REPEAT_COUNT:
				mf = "nowRepeat";
				break;
			case SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_REMAINING_REPEAT_COUNT:
				mf = "nowRemain";
				break;
			case SimpleTrigger.MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY:
				mf = "ignore";
				break;
			}

			switch (o.getMisfireInstruction()) {
			case CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING:
				mf = (mf == null ? "" : mf + " or ") + "nothing";
				break;
			}

			r.setMissfire(mf);
			return r;
			
		} catch (SchedulerException e) {
			throw new TException(e);
		}
	}
}
