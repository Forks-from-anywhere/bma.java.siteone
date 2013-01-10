package bma.siteone.alarm.service;

import java.util.List;
import java.util.Map;

import org.apache.thrift.TException;

import bma.siteone.alarm.thrift.TAlarm;
import bma.siteone.alarm.thrift.TAlarmQueryForm;
import bma.siteone.alarm.thrift.TAlarmQueryResult;
import bma.siteone.alarm.thrift.TAlarmService;
import bma.siteone.alarmclient.thrift.TAlarmCommitForm;

public class AlarmServiceThrift implements TAlarmService.Iface {

	final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AlarmServiceThrift.class);

	private AlarmService service;

	public AlarmService getService() {
		return service;
	}

	public void setService(AlarmService service) {
		this.service = service;
	}

	@Override
	public TAlarmQueryResult queryAlarmByIds(List<Integer> ids) throws TException {
		TAlarmQueryResult result = new TAlarmQueryResult();
		result.setAlarms(service.queryAlarmByIds(ids));
		result.setTotal(service.queryAlarmByIdsCount(ids));
		return result;
	}

	@Override
	public boolean clearAlarm(TAlarmQueryForm alarmQueryForm) throws TException {
		throw new TException("clearAlarm(TAlarmQueryForm alarmQueryForm) is not complete");
	}

	@Override
	public boolean clearAlarmByIds(List<Integer> ids) throws TException {
		service.deleteTAlarmByIds(ids);
		return true;
	}

	@Override
	public boolean releaseAlarm(TAlarmQueryForm alarmQueryForm) throws TException {
		throw new TException("releaseAlarm(TAlarmQueryForm alarmQueryForm) is not complete");
	}

	@Override
	public boolean releaseAlarmByIds(List<Integer> ids) throws TException {
		service.updateTAlarmStatusByIds(ids, 3);
		return true;
	}

	@Override
	public void commitAlarm(TAlarmCommitForm alarmCommitForm) throws TException {
		WorkerThread thread = new WorkerThread(service,alarmCommitForm);
		thread.start();
	}

	@Override
	public TAlarmQueryResult queryAlarm(TAlarmQueryForm alarmQueryForm, int page, int pageSize,
			Map<String, String> orders) throws TException {
		TAlarmQueryResult result = new TAlarmQueryResult();
		result.setAlarms(service.queryAlarm(alarmQueryForm, page, pageSize, orders));
		result.setTotal(service.queryAlarmCount(alarmQueryForm));
		return result;
	}

	private class WorkerThread extends Thread {

		final org.slf4j.Logger logThread = org.slf4j.LoggerFactory.getLogger(AlarmServiceThrift.class);

		private AlarmService service;
		private TAlarmCommitForm alarmCommitForm;

		public WorkerThread(AlarmService service,TAlarmCommitForm alarmCommitForm) {
			this.service = service;
			this.alarmCommitForm = alarmCommitForm;
		}

		public void run() {
			logThread.info("into  thread , insert begin .. ");
			int insertId = 0;
			try {
				TAlarm alarm = new TAlarm();
				if (alarmCommitForm.getType() == 0) {
					throw new TException("miss type");
				}
				if (alarmCommitForm.getLevel() == 0) {
					alarmCommitForm.setLevel(5);
				}
				if (alarmCommitForm.getContent() == null || alarmCommitForm.getContent().trim().equals("")) {
					throw new TException("miss content");
				}
				if (alarmCommitForm.getSystem() == null || alarmCommitForm.getSystem().trim().equals("")) {
					throw new TException("miss system");
				}
				if (alarmCommitForm.getStype1() == null || alarmCommitForm.getStype1().trim().equals("")) {
					throw new TException("miss stype1");
				}
				if (alarmCommitForm.getStype2() == null || alarmCommitForm.getStype2().trim().equals("")) {
					throw new TException("miss stype2");
				}
				alarm.setType(alarmCommitForm.getType());
				alarm.setLevel(alarmCommitForm.getLevel());
				alarm.setContent(alarmCommitForm.getContent());
				alarm.setSystem(alarmCommitForm.getSystem());
				alarm.setStype1(alarmCommitForm.getStype1());
				alarm.setStype2(alarmCommitForm.getStype2());
				alarm.setStarttime(alarmCommitForm.getStarttime());
				alarm.setEndtime(alarmCommitForm.getEndtime());
				alarm.setFrequency(alarmCommitForm.getFrequency());
				alarm.setTimes(alarmCommitForm.getTimes());
				insertId = service.createTAlarm(alarm);
			} catch (Exception e) {
				logThread.error(e.getMessage());
			}
			logThread.info("into thread , insert success , Id = " + insertId);
		}
	}

}
