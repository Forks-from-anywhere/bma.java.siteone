package bma.siteone.quartz;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import bma.common.httpclient.HttpClientUtil;
import bma.common.langutil.core.ObjectUtil;
import bma.common.langutil.log.LogbackUtil;
import bma.common.langutil.testcase.SpringTestcaseUtil;
import bma.siteone.quartz.service.QuartzJobForm;
import bma.siteone.quartz.service.QuartzService;
import bma.siteone.quartz.service.QuartzServiceImpl;
import bma.siteone.quartz.service.QuartzTriggerForm;

public class QuartzTest {

	FileSystemXmlApplicationContext context;

	@Before
	public void setUp() throws Exception {
		SpringTestcaseUtil.disableDebug();
		HttpClientUtil.disableDebug(true);
		LogbackUtil.setLevel("com.mchange.v2", "INFO");
		LogbackUtil.setLevel("org.quartz", "INFO");
		context = new SpringTestcaseUtil.ApplicationContextBuilder().resource(
				getClass(), "quartz.xml").build();
	}

	@After
	public void tearDown() throws Exception {
		if (context != null)
			context.close();
	}

	@Test
	public void testService_Boot() throws Exception {
		QuartzService s = context.getBean("service", QuartzService.class);
		System.out.println(s);

		ObjectUtil.waitFor(this, 5000);
	}

	@Test
	public void testService_Clear() throws Exception {
		QuartzService s = context.getBean("service", QuartzService.class);
		System.out.println(s);
		Scheduler sch = ((QuartzServiceImpl) s).getScheduler();
		sch.clear();
	}

	@Test
	public void testService_Start() throws Exception {
		QuartzService s = context.getBean("service", QuartzService.class);

		QuartzJobForm jform = new QuartzJobForm();
		jform.setName("j1");
		jform.setGroup("job");

		QuartzTriggerForm tform = new QuartzTriggerForm();
		tform.setName("t1");
		tform.setGroup("trigger");
		tform.setStartTime(new Date(System.currentTimeMillis() + 1000));

		Object o = s.newJob(jform, tform);
		System.out.println(o);

		ObjectUtil.waitFor(this, 2000);
	}

	@Test
	public void testService_Schedule() throws Exception {
		QuartzService s = context.getBean("service", QuartzService.class);

		QuartzJobForm jform = new QuartzJobForm();
		jform.setName("j1");
		jform.setGroup("job");

		QuartzTriggerForm tform = new QuartzTriggerForm();
		tform.setName("t1");
		tform.setGroup("trigger");
		tform.setInterval(1000);
		tform.setRepeat(-1);

		Object o = s.newJob(jform, tform);
		System.out.println(o);

		ObjectUtil.waitFor(this, 5000);
	}

	@Test
	public void testService_Cron() throws Exception {
		QuartzService s = context.getBean("service", QuartzService.class);
		QuartzJobForm jform = new QuartzJobForm();
		jform.setName("j1");
		jform.setGroup("job");
		jform.setDisallowConcurrent(true);

		QuartzTriggerForm tform = new QuartzTriggerForm();
		tform.setName("t1");
		tform.setGroup("trigger");
		tform.setType(QuartzTriggerForm.TYPE_CRON);
		tform.setCron("0/2 * * * * ?");
		tform.setMissfire(QuartzTriggerForm.MISS_NOTHING);
		// tform.setMissfire(QuarzTriggerForm.MISS_NOW);

		Object o = s.newJob(jform, tform);
		System.out.println(o);

		ObjectUtil.waitFor(this, 5000);
	}

	@Test
	public void testService_Durability() throws Exception {
		String name = "j1";
		String group = "job";

		QuartzService s = context.getBean("service", QuartzService.class);

		QuartzTriggerForm tform = new QuartzTriggerForm();
		tform.setName("t1");
		tform.setGroup("trigger");
		// tform.setInterval(1000);

		JobDetail job = s.queryJob(name, group);
		if (job != null) {
			Object o = s.newTrigger(name, group, tform);
			System.out.println(o);
		} else {
			QuartzJobForm jform = new QuartzJobForm();
			jform.setName(name);
			jform.setGroup(group);
			jform.setDurability(true);

			Object o = s.newJob(jform, tform);
			System.out.println(o);
		}

		ObjectUtil.waitFor(this, 5000);
	}

	@Test
	public void testService_TypeDispatch() throws Exception {
		QuartzService s = context.getBean("service", QuartzService.class);

		QuartzJobForm jform = new QuartzJobForm();
		jform.setName("j1");
		jform.setGroup("job");
		jform.setType("test1");

		QuartzTriggerForm tform = new QuartzTriggerForm();
		tform.setName("t1");
		tform.setGroup("trigger");

		Object o = s.newJob(jform, tform);
		System.out.println(o);

		ObjectUtil.waitFor(this, 2000);
	}

	@Test
	public void httpHandler_base() throws Exception {
		QuartzService s = context.getBean("service", QuartzService.class);

		Map<String, String> params = new HashMap<String, String>();
		params.put("url", "http://cn.bing.com/");
		params.put("PARAM_q", "韩寒");
		params.put("retry", "2");
		params.put("retryTime", "1000");

		QuartzJobForm jform = new QuartzJobForm();
		jform.setName("http_job_1");
		jform.setGroup("job");
		jform.setType("http");
		jform.setJobDatas(params);

		QuartzTriggerForm tform = new QuartzTriggerForm();
		tform.setName("http_job_1");
		tform.setGroup("trigger");

		Object o = s.newJob(jform, tform);
		System.out.println(o);

		ObjectUtil.waitFor(this, 3000);
	}

	@Test
	public void query_Job() throws Exception {
		QuartzService s = context.getBean("service", QuartzService.class);
		Object o = s.queryJob("c1", "DEFAULT");
		System.out.println(o);
	}

}
