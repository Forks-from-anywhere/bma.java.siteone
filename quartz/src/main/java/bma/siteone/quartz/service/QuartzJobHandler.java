package bma.siteone.quartz.service;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public interface QuartzJobHandler {

	public void execute(QuartzService service, String type,
			JobExecutionContext context) throws JobExecutionException;
}
