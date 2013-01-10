package bma.siteone.alarm.task;

import java.util.Calendar;
import java.util.Date;

import org.apache.thrift.TException;

import bma.siteone.alarm.service.AlarmService;
import bma.siteone.alarm.thrift.TAlarm;


public class AlarmSelfMonitor {
	
	final org.slf4j.Logger log = org.slf4j.LoggerFactory
	.getLogger(AlarmSelfMonitor.class);
	
	private AlarmService  service;
	
	public void doTask() {
		log.info("into AlarmSelfMonitor task");
		TAlarm alarm = new TAlarm();
		alarm.setType(1);
		alarm.setLevel(2);
		alarm.setSystem("alarm");
		alarm.setStype1("system");
		alarm.setStype2("selfMonitor");
		alarm.setContent("监控报警系统自监控");
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, +1);    //得到后一天
		Date startDate = new Date();
		int start = (int)(startDate.getTime()/1000);
		Date endDate = calendar.getTime();
		int end = (int)(endDate.getTime()/1000);
		alarm.setStarttime(start);
		alarm.setEndtime(end);
		alarm.setFrequency(1);
		alarm.setTimes(1);
		alarm.setStatus(0);
		try {
			int id = service.createTAlarm(alarm);
			log.info("create alarm id = " + id);
		} catch (TException e) {
			log.warn("create fail..." + e);
		}
	}

	public void setService(AlarmService service) {
		this.service = service;
	}

	public AlarmService getService() {
		return service;
	}
}
