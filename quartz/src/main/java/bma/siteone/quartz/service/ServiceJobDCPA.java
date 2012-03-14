package bma.siteone.quartz.service;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;

@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class ServiceJobDCPA extends ServiceJob {

}
