package bma.siteone.wordstock;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import bma.common.jdbctemplate.JdbcTemplateUtil;
import bma.common.langutil.testcase.SpringTestcaseUtil;
import bma.siteone.wordstock.po.WordInfo;
import bma.siteone.wordstock.service.WordStockService;

/**
 * WordStock服务对象接口
 * 
 * @author 关中
 * 
 */
public class WordStockTC {

	FileSystemXmlApplicationContext context;

	@Before
	public void setUp() throws Exception {
		SpringTestcaseUtil.disableDebug();
		JdbcTemplateUtil.disableDebug(true);
		context = new SpringTestcaseUtil.ApplicationContextBuilder().resource(
				WordStockTC.class, "wordstock.xml").build();
	}

	@After
	public void tearDown() throws Exception {
		if (context != null)
			context.close();
	}

	@Test
	public void test_getOne() throws Exception {
		WordStockService s = context.getBean("service", WordStockService.class);
		Object o = s.getOne(2);
		System.out.println(o);
	}

	@Test
	public void test_match() throws Exception {
		WordStockService s = context.getBean("service", WordStockService.class);
		Object o = s.match("test", "test3");
		System.out.println(o);
	}

	@Test
	public void test_listGroupType() throws Exception {
		WordStockService s = context.getBean("service", WordStockService.class);
		Object o = s.listGroupType();
		System.out.println(o);
	}

	@Test
	public void test_listInfo() throws Exception {
		WordStockService s = context.getBean("service", WordStockService.class);
		Object o = s.listInfo("test");
		System.out.println(o);
	}

	@Test
	public void test_search() throws Exception {
		WordStockService s = context.getBean("service", WordStockService.class);
		Object o = s.search("FROM TABLE WHERE 1=1 ORDER BY id DESC", 1, 10);
		System.out.println(o);
	}

	@Test
	public void test_create() throws Exception {
		WordStockService s = context.getBean("service", WordStockService.class);
		WordInfo info = new WordInfo();
		info.setGroupType("test");
		info.setTitle("test");
		info.setWords("test1,test2,test3");
		info.setType(WordStockService.TYPE_TEXT);
		Object o = s.create(info);
		System.out.println(o);
	}

	@Test
	public void test_update() throws Exception {
		WordStockService s = context.getBean("service", WordStockService.class);
		WordInfo info = new WordInfo();
		info.setId(1);
		info.setGroupType("test");
		info.setTitle("test");
		info.setWords("test1,test2,test4");
		info.setType(WordStockService.TYPE_TEXT);
		Object o = s.update(info);
		System.out.println(o);
	}

	@Test
	public void test_delete() throws Exception {
		WordStockService s = context.getBean("service", WordStockService.class);
		Object o = s.delete(4);
		System.out.println(o);
	}

}
