package bma.siteone.alarm;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import bma.siteone.alarm.thrift.TAlarm;
import bma.siteone.alarm.thrift.TAlarmQueryResult;
import bma.siteone.alarm.thrift.TAlarmService;
import bma.siteone.alarmclient.thrift.TAlarmCommitForm;


public class AlarmServiceClientTest {

	TAlarmService.Client client;
	
	TTransport transport;
	
	@Before
	public void setUp() throws Exception {
//		transport = new TSocket("127.0.0.1", 9099);
		transport = new TSocket("183.61.6.61", 9195);
//		transport = new TSocket("183.61.6.60", 9092);
		transport.open();
		transport = new TFramedTransport(transport,100*1024*1024);
		TProtocol protocol = new TBinaryProtocol(transport);
		client = new TAlarmService.Client(protocol);
	}

	@After
	public void tearDown() throws Exception {
		transport.close();
	}
	
	@Test
	public void testCommitAlarm1()throws Exception {
		TAlarmCommitForm alarmCommitForm = new TAlarmCommitForm();
		alarmCommitForm.setContent("获取主播信息出现错误");
		alarmCommitForm.setSystem("live");
		alarmCommitForm.setStype1("index");
		alarmCommitForm.setStype2("profile");
		alarmCommitForm.setType(2);
		alarmCommitForm.setLevel(1);
		client.commitAlarm(alarmCommitForm);
	}
	
	@Test
	public void testCommitAlarm2()throws Exception {
		TAlarmCommitForm alarmCommitForm = new TAlarmCommitForm();
		alarmCommitForm.setContent("网络异常");
		alarmCommitForm.setSystem("live");
		alarmCommitForm.setStype1("index");
		alarmCommitForm.setStype2("main");
		alarmCommitForm.setType(2);
		alarmCommitForm.setLevel(3);
		client.commitAlarm(alarmCommitForm);
	}

	@Test
	public void testQueryAlarmByIds() throws Exception {
		List<Integer> ids = new ArrayList<Integer>();
		ids.add(3);
		ids.add(4);		
		TAlarmQueryResult res = client.queryAlarmByIds(ids);
		for (TAlarm alarm : res.getAlarms()) {
			System.out.println(alarm);
		}
		System.out.println("testQueryAlarmByIds done!");
	} 
}
