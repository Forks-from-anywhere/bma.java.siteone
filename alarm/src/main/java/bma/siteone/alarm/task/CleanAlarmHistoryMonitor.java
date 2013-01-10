package bma.siteone.alarm.task;

import java.util.Calendar;

import org.apache.thrift.TException;

import bma.siteone.alarm.service.AlarmService;

public class CleanAlarmHistoryMonitor {
	
	final org.slf4j.Logger log = org.slf4j.LoggerFactory
	.getLogger(CleanAlarmHistoryMonitor.class);
	
	private AlarmService  service;
	
	private int days;
	
	public void doTask() {
		log.info("into CleanAlarmHistoryMonitor task");
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -days);    //得到days天前的时间
		try {
			service.deleteHistoryByOptime(calendar.getTime());
			log.info("deleteHistoryByOptime op_time <= " + calendar.getTime());
		} catch (TException e) {
			log.warn("deleteHistoryByOptime fail..." + e);
		}
	}

	public void setService(AlarmService service) {
		this.service = service;
	}

	public AlarmService getService() {
		return service;
	}

	public void setDays(int days) {
		this.days = days;
	}

	public int getDays() {
		return days;
	}
}
 