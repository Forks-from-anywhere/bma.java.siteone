package bma.siteone.quartz.service;

import java.util.Date;

import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerKey;

import bma.common.langutil.core.ValueUtil;

public class QuartzServiceImpl implements QuartzService, QuartzServiceRealm {

	final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(QuartzServiceImpl.class);

	private SchedulerFactory factory;
	private QuartzJobDispatcher dispatcher;

	private int startDelay;
	private boolean stopWaitForJobs = true;

	private transient Scheduler scheduler;

	public QuartzJobDispatcher getDispatcher() {
		return dispatcher;
	}

	public void setDispatcher(QuartzJobDispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}

	public SchedulerFactory getFactory() {
		return factory;
	}

	public void setFactory(SchedulerFactory factory) {
		this.factory = factory;
	}

	public int getStartDelay() {
		return startDelay;
	}

	public void setStartDelay(int startDelay) {
		this.startDelay = startDelay;
	}

	public boolean isStopWaitForJobs() {
		return stopWaitForJobs;
	}

	public void setStopWaitForJobs(boolean stopWaitForJobs) {
		this.stopWaitForJobs = stopWaitForJobs;
	}

	public Scheduler getScheduler() {
		return scheduler;
	}

	@Override
	public void start() throws SchedulerException {
		if (this.scheduler == null) {
			this.scheduler = factory.getScheduler();
			QuartzServiceUtil.bind(this.scheduler, this);
		}
		if (!this.scheduler.isStarted()) {
			if (startDelay > 0) {
				this.scheduler.startDelayed(startDelay);
			} else {
				this.scheduler.startDelayed(startDelay);
			}
		}
	}

	@Override
	public boolean pause(boolean pause) throws SchedulerException {
		if (this.scheduler == null) {
			return false;
		}
		if (pause) {
			if (this.scheduler.isStarted() && !this.scheduler.isInStandbyMode()) {
				this.scheduler.standby();
				return true;
			} else {
				return false;
			}
		} else {
			if (this.scheduler.isStarted() && this.scheduler.isInStandbyMode()) {
				this.scheduler.start();
				return true;
			} else {
				return false;
			}
		}
	}

	@Override
	public void stop() throws SchedulerException {
		if (this.scheduler != null) {
			if (this.scheduler.isStarted()) {
				this.scheduler.shutdown(stopWaitForJobs);
			}
		}
	}

	@Override
	public Date newJob(QuartzJobForm job, QuartzTriggerForm trigger)
			throws SchedulerException {
		String type = job.getType();
		if (ValueUtil.notEmpty(type)) {
			if (this.dispatcher != null) {
				QuartzJobHandler h = this.dispatcher.getHandler(type);
				if (h == null) {
					if (log.isDebugEnabled()) {
						log.debug("jobType[{}] not support,{}", type, job);
					}
					throw new SchedulerException("jobType[" + type
							+ "] not support");
				}
			}
		}

		JobDetail jobj = job.build();
		Trigger tobj = trigger.build(null, null);
		// Schedule the job with the trigger
		return this.scheduler.scheduleJob(jobj, tobj);
	}

	@Override
	public Date newTrigger(String jobName, String jobGroup,
			QuartzTriggerForm trigger) throws SchedulerException {
		Trigger tobj = trigger.build(jobName, jobGroup);
		return this.scheduler.scheduleJob(tobj);
	}

	@Override
	public boolean removeJob(String name, String group)
			throws SchedulerException {
		return this.scheduler.deleteJob(new JobKey(name, group));
	}

	@Override
	public boolean removeTrigger(String name, String group)
			throws SchedulerException {
		TriggerKey key = new TriggerKey(name, group);
		Trigger t = this.scheduler.getTrigger(key);
		this.scheduler.resumeTrigger(new TriggerKey(name, group));
		return t != null;
	}

	@Override
	public JobDetail queryJob(String name, String group)
			throws SchedulerException {
		return this.scheduler.getJobDetail(new JobKey(name, group));
	}

	@Override
	public Trigger queryTrigger(String name, String group)
			throws SchedulerException {
		return this.scheduler.getTrigger(new TriggerKey(name, group));
	}

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		JobDetail job = context.getJobDetail();
		String type = job.getJobDataMap().getString(QuartzJobForm.KEY_TYPE);
		if (ValueUtil.empty(type)) {
			log.warn("execute type<null>\n{}\n{}", job, context.getTrigger());
			return;
		}

		QuartzJobHandler handler = this.dispatcher == null ? null
				: this.dispatcher.getHandler(type);
		if (handler == null) {
			log.warn("execute type<{}> not exists\n{}\n{}", new Object[] {
					type, job, context.getTrigger() });
			return;
		}

		handler.execute(this, type, context);
	}

}
