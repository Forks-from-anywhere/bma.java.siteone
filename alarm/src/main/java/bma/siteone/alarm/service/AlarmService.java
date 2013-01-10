package bma.siteone.alarm.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.thrift.TException;

import bma.siteone.alarm.thrift.TAlarm;
import bma.siteone.alarm.thrift.TAlarmQueryForm;

public interface AlarmService {

	public List<TAlarm> queryAlarm(TAlarmQueryForm alarmQueryForm, int page, int pageSize, Map<String, String> orders)
			throws TException;

	public int queryAlarmCount(TAlarmQueryForm alarmQueryForm) throws TException;

	public List<TAlarm> queryAlarmByIds(List<Integer> ids) throws TException;
	
	public int queryAlarmByIdsCount(List<Integer> ids) throws TException;

	public void updateTAlarmStatusByIds(List<Integer> ids, int status) throws TException;

	public void deleteTAlarmByIds(List<Integer> ids) throws TException;

	public int createTAlarm(TAlarm alarm) throws TException;
	
	public void deleteHistoryByOptime(Date opTime) throws TException;

}
