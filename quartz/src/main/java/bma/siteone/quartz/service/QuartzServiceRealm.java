package bma.siteone.quartz.service;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public interface QuartzServiceRealm {

	public void execute(JobExecutionContext context)
			throws JobExecutionException;

}
