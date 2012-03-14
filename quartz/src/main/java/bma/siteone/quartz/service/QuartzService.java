package bma.siteone.quartz.service;

import java.util.Date;

import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.Trigger;

public interface QuartzService {

	public void start() throws SchedulerException;

	public boolean pause(boolean pause) throws SchedulerException;

	public void stop() throws SchedulerException;

	public Date newJob(QuartzJobForm job, QuartzTriggerForm trigger)
			throws SchedulerException;

	public Date newTrigger(String jobName, String jobGroup,
			QuartzTriggerForm trigger) throws SchedulerException;

	public boolean removeJob(String name, String group)
			throws SchedulerException;

	public boolean removeTrigger(String name, String group)
			throws SchedulerException;

	public JobDetail queryJob(String name, String group)
			throws SchedulerException;

	public Trigger queryTrigger(String name, String group)
			throws SchedulerException;
}
