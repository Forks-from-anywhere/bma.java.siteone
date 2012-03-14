package bma.siteone.matchlist;

import org.hibernate.Query;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import bma.common.hibernate.HibernateSearch;
import bma.common.hibernate.HibernateUtil;
import bma.common.langutil.core.PagerResult;
import bma.common.langutil.testcase.SpringTestcaseUtil;
import bma.siteone.matchlist.po.MatchInfo;
import bma.siteone.matchlist.service.MatchListService;
import bma.siteone.matchlist.service.MatchListServiceImpl;

/**
 * Thrift客户端测试用例
 * 
 * @author 关中
 * 
 */
public class MatchListTest {

	FileSystemXmlApplicationContext context;

	@Before
	public void setUp() throws Exception {
		SpringTestcaseUtil.disableDebug();
		HibernateUtil.disableDebug(true);
		context = new SpringTestcaseUtil.ApplicationContextBuilder().resource(
				MatchListTest.class, "matchlist.xml").build();
	}

	@After
	public void tearDown() throws Exception {
		if (context != null)
			context.close();
	}

	@Test
	public void testService_GetOne() throws Exception {
		MatchListService s1 = context
				.getBean("service", MatchListService.class);
		System.out.println(s1.getOne(1));
	}

	@Test
	public void testService_Save() throws Exception {
		MatchListService s1 = context
				.getBean("service", MatchListService.class);
		MatchInfo po = new MatchInfo();
		po.setGroupId("test");
		po.setContent("content");
		po.setReserve1(1);
		po.setReserve2(2);
		po.setReserve3("3");
		po.setReserve4("4");
		po.setTitle("title");
		System.out.println(s1.create(po));
	}

	@Test
	public void testService_Update() throws Exception {
		MatchListService s1 = context
				.getBean("service", MatchListService.class);
		MatchInfo po = s1.getOne(1);
		if (po == null) {
			System.out.println("Object not found");
		} else {
			po.setContent("content2");
			System.out.println(s1.update(po));
		}
	}

	@Test
	public void testService_Delete() throws Exception {
		MatchListService s1 = context
				.getBean("service", MatchListService.class);
		System.out.println(s1.delete(2));
	}

	@Test
	public void testService_ListGroupId() throws Exception {
		MatchListService s1 = context
				.getBean("service", MatchListService.class);
		System.out.println(s1.listGroupId());
	}

	@Test
	public void testService_ListGroup() throws Exception {
		MatchListService s1 = context
				.getBean("service", MatchListService.class);
		System.out.println(s1.listGroup("test"));
	}

	@Test
	public void testService_search() throws Exception {
		MatchListService s1 = context
				.getBean("service", MatchListService.class);

		HibernateSearch search = new HibernateSearch() {

			@Override
			public String hsql(boolean count) {
				return "FROM MatchInfo WHERE groupId = ?";
			}

			@Override
			public void bind(Query q) {
				q.setString(0, "test");
			}

		};
		PagerResult<MatchInfo> pr = s1.search(search, 0, 10);
		System.out.println(pr.getPager());
		System.out.println(pr.getResult());
	}

	@Test
	public void testService_Match() throws Exception {
		MatchListService s1 = context
				.getBean("service", MatchListService.class);
		String groupId = "type3";
		String v = "傻逼2";
		v = "testipads";
		String type = MatchListServiceImpl.TYPE_REGEX;
		
		System.out.println(s1.match(groupId, v, type));
	}
}
