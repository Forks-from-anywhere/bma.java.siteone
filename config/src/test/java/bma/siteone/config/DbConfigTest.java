package bma.siteone.config;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import bma.common.jdbctemplate.JdbcTemplateUtil;
import bma.common.langutil.testcase.SpringTestcaseUtil;
import bma.siteone.config.service.ConfigGroup;
import bma.siteone.config.service.db.DbConfigService;

public class DbConfigTest {

	FileSystemXmlApplicationContext context;
	
	@Before
	public void setUp() throws Exception {
		SpringTestcaseUtil.disableDebug();
		JdbcTemplateUtil.disableDebug(true);
		context = new SpringTestcaseUtil.ApplicationContextBuilder().project("src/test/resources/config.xml").build();
	}

	@After
	public void tearDown() throws Exception {
		if (context != null)
			context.close();
	}
	
	@Test
	public void testDoGoldbeanDailyStat() throws Exception {
		DbConfigService s = context.getBean("configService", DbConfigService.class);
		
		ConfigGroup cg = s.getGroup("aa");
		
		System.out.println(cg.getString("22"));
		
		ConfigGroup cg2 = s.getGroup("aa");
		System.out.println(cg2.getString("11"));
		
		System.out.println("done");
	}
}
