package bma.siteone.crontabrecord;

import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import bma.common.jdbctemplate.JdbcTemplateUtil;
import bma.common.langutil.core.ObjectUtil;
import bma.common.langutil.testcase.SpringTestcaseUtil;
import bma.common.thrift.ThriftServer;
import bma.siteone.crontabrecord.po.CrontabTaskInfo;
import bma.siteone.crontabrecord.po.CrontabTaskInfo.CrontabRunStatus;
import bma.siteone.crontabrecord.service.CrontabRecordService;

public class CrontabServiceImplTest {

	FileSystemXmlApplicationContext context;
	private CrontabRecordService service;

	@Before
	public void setUp() throws Exception {
		SpringTestcaseUtil.disableDebug();
		JdbcTemplateUtil.disableDebug(true);
		context = new SpringTestcaseUtil.ApplicationContextBuilder().project("src/test/resources/service.xml").build();
		service = context.getBean("crontabRecordService", CrontabRecordService.class);
	}

	@After
	public void tearDown() throws Exception {
		if (context != null)
			context.close();
	}

	@Test
	public void startCrontabTask() throws Exception {
		CrontabTaskInfo record = new CrontabTaskInfo();
		record.seteName("test_e");
		record.setcName("tect_cname");
		record.setServiceName("rank");
		record.setDescription("remark");
		service.startCrontabTask(record);
	}

	@Test
	public void endCrontabTaskWhenSuccessfully() {
		CrontabTaskInfo record = new CrontabTaskInfo();
		record.seteName("test_e");
		record.setcName("tect_cname");
		record.setServiceName("rank");
		record.setDescription("remark");
		service.endCrontabTaskWhenSuccessfully(record, 20);
	}

	@Test
	public void endCrontabTaskWhenFailed() {
		CrontabTaskInfo record = new CrontabTaskInfo();
		record.seteName("test_e");
		record.setcName("tect_cname");
		record.setServiceName("rank");
		record.setDescription("remark");
		service.endCrontabTaskWhenFailed(record, 30);
	}

}
