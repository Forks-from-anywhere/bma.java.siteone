package bma.siteone.nick;

import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import bma.common.jdbctemplate.JdbcTemplateUtil;
import bma.common.langutil.core.ToStringUtil;
import bma.common.langutil.testcase.SpringTestcaseUtil;
import bma.siteone.nick.po.NickUser;
import bma.siteone.nick.service.NickService;

public class NickServiceImplTest {

	FileSystemXmlApplicationContext context;
	private NickService service;

	@Before
	public void setUp() throws Exception {
		SpringTestcaseUtil.disableDebug();
		JdbcTemplateUtil.disableDebug(true);
		context = new SpringTestcaseUtil.ApplicationContextBuilder().project("src/test/resources/service.xml").build();
		service = context.getBean("nickService", NickService.class);
	}

	@After
	public void tearDown() throws Exception {
		if (context != null)
			context.close();
	}

	@Test
	public void testGetNicks() throws Exception {
		List<Long> uids = Arrays.asList(50000539l , 50000538l , 50000537l);
		int overdueTime = (int) (new GregorianCalendar().getTimeInMillis() / 1000);
		List<NickUser> nicks = service.getNicks(uids, overdueTime);
		for(NickUser c : nicks){
			System.out.println(ToStringUtil.fieldReflect(c));
		}
		
	}

}
