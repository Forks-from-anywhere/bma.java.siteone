package bma.siteone.alarm;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import bma.common.jdbctemplate.JdbcTemplateUtil;
import bma.common.langutil.testcase.SpringTestcaseUtil;
import bma.siteone.alarm.service.AlarmService;
import bma.siteone.alarm.service.AlarmServiceImpl;
import bma.siteone.alarm.task.AlarmSelfMonitor;
import bma.siteone.alarm.task.CleanAlarmHistoryMonitor;
import bma.siteone.alarm.thrift.TAlarm;
import bma.siteone.alarm.thrift.TAlarmQueryForm;

public class AlarmServiceImplTest {


	FileSystemXmlApplicationContext context;
	AlarmService service;
	AlarmSelfMonitor monitor;
	CleanAlarmHistoryMonitor cleanMonitor;
	
	@Before
	public void setUp() throws Exception {
		SpringTestcaseUtil.disableDebug();
		JdbcTemplateUtil.disableDebug(false);
		context = new SpringTestcaseUtil.ApplicationContextBuilder().project("src/test/resources/spring_server.xml").build();
		service = context.getBean("service", AlarmServiceImpl.class);
		monitor = context.getBean("alarmSelfMonitor", AlarmSelfMonitor.class);
		cleanMonitor = context.getBean("cleanAlarmHistoryMonitor", CleanAlarmHistoryMonitor.class);
	}

	@After
	public void tearDown() throws Exception {
		if (context != null)
			context.close();
	}
	
	@Test
	public void testDoTask(){
		monitor.doTask();
	}
	
	@Test
	public void testCleanDoTask(){
		cleanMonitor.doTask();
	}
	
	@Test
	public void testQueryAlarmForm() throws Exception{
		TAlarmQueryForm form = new TAlarmQueryForm();
		form.setContent("异常");
		List<TAlarm> result = service.queryAlarm(form, 0, 0, null);
		System.out.println(result);
	}
	
	@Test
	public void testQueryAlarmCount() throws Exception{
		TAlarmQueryForm form = new TAlarmQueryForm();
		form.setContent("异常");
		int count = service.queryAlarmCount(form);
		System.out.println(count);
	}
	
	@Test
	public void testUpdateTAlarmStatusByIds() throws Exception{
		List<Integer> ids = new ArrayList<Integer>();
		ids.add(1);
		ids.add(2);
		service.updateTAlarmStatusByIds(ids,3);
	}
	
	@Test
	public void testDeleteTAlarmByIds() throws Exception{
		List<Integer> ids = new ArrayList<Integer>();
		ids.add(1);
		ids.add(2);
		service.deleteTAlarmByIds(ids);
	}
	
	@Test
	public void testQueryAlarmByIdsCount() throws Exception{
		List<Integer> ids = new ArrayList<Integer>();
		ids.add(1);
		ids.add(2);
		int result = service.queryAlarmByIdsCount(ids);
		System.out.println(result);
	}
	
	@Test
	public void testQueryAlarmByIds() throws Exception{
		List<Integer> ids = new ArrayList<Integer>();
		ids.add(1);
		ids.add(2);
		List<TAlarm> result = service.queryAlarmByIds(ids);
		System.out.println(result);
	}
	
	@Test
	public void testCreateAlarm() throws Exception{
		TAlarm alarm = new TAlarm();
		alarm.setType(1);
		alarm.setLevel(4);
		alarm.setSystem("live");
		alarm.setStype1("index");
		alarm.setStype2("popularProfile");
		alarm.setContent("无法查看推荐主播");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		Date startDate = sdf.parse("20130109000000");
		int start = (int)startDate.getTime();
		Date endDate = sdf.parse("20130110000000");
		int end = (int)endDate.getTime();
		alarm.setStarttime(start);
		alarm.setEndtime(end);
		alarm.setFrequency(30);
		alarm.setTimes(30);
		alarm.setStatus(0);
		int id = service.createTAlarm(alarm);
		System.out.println(id);
	}
	
	@Test
	public void testDeleteHistoryByOptime() throws Exception{
		Date op_time = new Date();
		service.deleteHistoryByOptime(op_time);
	}
}
