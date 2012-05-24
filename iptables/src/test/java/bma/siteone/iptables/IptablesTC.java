package bma.siteone.iptables;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import bma.common.jdbctemplate.JdbcTemplateUtil;
import bma.common.langutil.testcase.SpringTestcaseUtil;
import bma.siteone.iptables.po.IptableInfo;
import bma.siteone.iptables.service.IptablesService;

/**
 * 评论的服务对象接口
 * 
 * @author 关中
 * 
 */
public class IptablesTC {

	FileSystemXmlApplicationContext context;

	@Before
	public void setUp() throws Exception {
		SpringTestcaseUtil.disableDebug();
		JdbcTemplateUtil.disableDebug(true);
		context = new SpringTestcaseUtil.ApplicationContextBuilder().resource(
				IptablesTC.class, "iptables.xml").build();
	}

	@After
	public void tearDown() throws Exception {
		if (context != null)
			context.close();
	}

	@Test
	public void test_getOne() throws Exception {
		IptablesService s = context.getBean("service", IptablesService.class);
		Object o = s.getOne(1);
		System.out.println(o);
	}

	@Test
	public void test_match() throws Exception {
		IptablesService s = context.getBean("service", IptablesService.class);
		Object o = s.match("test", "127.0.0.2");
		System.out.println(o);
	}

	@Test
	public void test_listGroupType() throws Exception {
		IptablesService s = context.getBean("service", IptablesService.class);
		Object o = s.listGroupType();
		System.out.println(o);
	}

	@Test
	public void test_listInfo() throws Exception {
		IptablesService s = context.getBean("service", IptablesService.class);
		Object o = s.listInfo("test");
		System.out.println(o);
	}
	
	@Test
	public void test_search() throws Exception {
		IptablesService s = context.getBean("service", IptablesService.class);
		Object o = s.search("FROM TABLE WHERE 1=1 ORDER BY id DESC", 1, 10);
		System.out.println(o);
	}

	@Test
	public void test_create() throws Exception {
		IptablesService s = context.getBean("service", IptablesService.class);
		IptableInfo info = new IptableInfo();
		info.setGroupType("test");
		info.setInet("127.0.0.1");
		info.setType(IptablesService.TYPE_ACCEPT);
		Object o = s.create(info);
		System.out.println(o);
	}
	
	
	@Test
	public void test_update() throws Exception {
		IptablesService s = context.getBean("service", IptablesService.class);
		IptableInfo info = new IptableInfo();
		info.setId(1);
		info.setGroupType("test");
		info.setInet("*");
		info.setType(IptablesService.TYPE_ACCEPT);
		Object o = s.update(info);
		System.out.println(o);
	}
	
	@Test
	public void test_delete() throws Exception {
		IptablesService s = context.getBean("service", IptablesService.class);		
		Object o = s.delete(4);
		System.out.println(o);
	}
	
}
