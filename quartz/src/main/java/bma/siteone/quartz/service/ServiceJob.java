package bma.siteone.quartz.service;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;

public class ServiceJob implements Job {

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		try {
			QuartzServiceRealm s = QuartzServiceUtil.service(context.getScheduler());
			s.execute(context);
		} catch (SchedulerException e) {
			throw new JobExecutionException(e);
		}
	}

}
