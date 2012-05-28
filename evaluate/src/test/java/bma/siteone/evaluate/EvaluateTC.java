package bma.siteone.evaluate;

import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import bma.common.jdbctemplate.JdbcTemplateUtil;
import bma.common.langutil.testcase.SpringTestcaseUtil;
import bma.siteone.evaluate.po.EvaluateInfo;
import bma.siteone.evaluate.service.EvaluateService;

/**
 * 评论的服务对象接口
 * 
 * @author 关中
 * 
 */
public class EvaluateTC {

	FileSystemXmlApplicationContext context;

	@Before
	public void setUp() throws Exception {
		SpringTestcaseUtil.disableDebug();
		JdbcTemplateUtil.disableDebug(true);
		context = new SpringTestcaseUtil.ApplicationContextBuilder().resource(
				EvaluateTC.class, "evaluate.xml").build();
	}

	@After
	public void tearDown() throws Exception {
		if (context != null)
			context.close();
	}

	@Test
	public void test_getOne() throws Exception {
		EvaluateService s = context.getBean("service", EvaluateService.class);
		Object o = s.getOne("test", "1");
		s.getOne("test", "1");
		System.out.println(o);
	}

	@Test
	public void test_listGroupType() throws Exception {
		EvaluateService s = context.getBean("service", EvaluateService.class);
		Object o = s.listGroupType();
		System.out.println(o);
	}

	@Test
	public void test_search() throws Exception {
		EvaluateService s = context.getBean("service", EvaluateService.class);
		Object o = s.search("FROM TABLE WHERE 1=1 ORDER BY group_type DESC", 1,
				10);
		System.out.println(o);
	}

	@Test
	public void test_vote() throws Exception {
		EvaluateService s = context.getBean("service", EvaluateService.class);
		EvaluateInfo info = new EvaluateInfo();
		info.setGroupType("test");
		info.setItemId("1");
		info.setUrl("www");
		info.setTitle("test point");
		Set<Integer> ops = new HashSet<Integer>(20);
		for (int i = 1; i <= EvaluateInfo.MAX_OPTION; i++) {
			info.setOptionValue(i, 1);
			ops.add(i);
		}
		Object o = s.vote(info, ops);
		System.out.println(o);
	}

	@Test
	public void test_update() throws Exception {
		EvaluateService s = context.getBean("service", EvaluateService.class);
		EvaluateInfo info = new EvaluateInfo();
		info.setGroupType("test");
		info.setItemId("1");
		info.setUrl("www");
		info.setTitle("test point2");
		info.setEvaAmount(-1);
		for (int i = 1; i <= EvaluateInfo.MAX_OPTION; i++) {
			info.setOptionValue(i, -1);
		}
		Object o = s.update(info);
		System.out.println(o);
	}

	@Test
	public void test_delete() throws Exception {
		EvaluateService s = context.getBean("service", EvaluateService.class);
		Object o = s.deleteGroup("test2");
		System.out.println(o);

		Object o2 = s.delete("test", "1");
		System.out.println(o2);
	}

}
