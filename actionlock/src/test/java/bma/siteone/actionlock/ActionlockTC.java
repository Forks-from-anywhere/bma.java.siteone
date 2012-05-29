package bma.siteone.actionlock;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import bma.common.jdbctemplate.JdbcTemplateUtil;
import bma.common.langutil.core.ObjectUtil;
import bma.common.langutil.testcase.SpringTestcaseUtil;
import bma.siteone.actionlock.service.ActionlockService;

/**
 * 服务对象接口
 * 
 * @author 关中
 * 
 */
public class ActionlockTC {

	FileSystemXmlApplicationContext context;

	@Before
	public void setUp() throws Exception {
		SpringTestcaseUtil.disableDebug();
		JdbcTemplateUtil.disableDebug(true);
		context = new SpringTestcaseUtil.ApplicationContextBuilder().resource(
				ActionlockTC.class, "actionlock.xml").build();
	}

	@After
	public void tearDown() throws Exception {
		if (context != null)
			context.close();
	}

	@Test
	public void test_getOne() throws Exception {
		ActionlockService s = context.getBean("service",
				ActionlockService.class);
		Object o = s.getOne("test", "1", true);
		System.out.println(o);
	}

	@Test
	public void test_listGroupType() throws Exception {
		ActionlockService s = context.getBean("service",
				ActionlockService.class);
		Object o = s.listGroupType();
		System.out.println(o);
	}

	@Test
	public void test_search() throws Exception {
		ActionlockService s = context.getBean("service",
				ActionlockService.class);
		Object o = s.search("FROM TABLE WHERE 1=1 ORDER BY group_type DESC", 1,
				10);
		System.out.println(o);
	}

	@Test
	public void test_lock() throws Exception {
		ActionlockService s = context.getBean("service",
				ActionlockService.class);
		Object o = s.lock("test", "1", 2, 0);
		System.out.println(o);
	}

	@Test
	public void test_checkLock() throws Exception {
		ActionlockService s = context.getBean("service",
				ActionlockService.class);
		Object o = s.checkLock("test", "1", 1, false);
		System.out.println(o);
	}

	@Test
	public void test_delete() throws Exception {
		ActionlockService s = context.getBean("service",
				ActionlockService.class);
		Object o = s.deleteGroup("test");
		System.out.println(o);

		Object o2 = s.delete("test", "1");
		System.out.println(o2);
	}

	@Test
	public void test_clean() throws Exception {
		ActionlockService s = context.getBean("service",
				ActionlockService.class);
		s.clean("test");
		s.clean(null);
	}

	@Test
	public void test_timeout() throws Exception {
		ActionlockService s = context.getBean("service",
				ActionlockService.class);
		s.lock("test", "1", 1, 1000);
		ObjectUtil.waitFor(this, 2000);
		Object o = s.getOne("test", "1", false);
		System.out.println(o);
	}

}
