package bma.siteone.comments;

import java.util.LinkedList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import bma.common.jdbctemplate.JdbcTemplateUtil;
import bma.common.langutil.testcase.SpringTestcaseUtil;
import bma.siteone.comments.service.CommentForm;
import bma.siteone.comments.service.CommentPointForm;
import bma.siteone.comments.service.CommentsService;
import bma.siteone.comments.service.SearchCommentForm;
import bma.siteone.comments.service.SearchCommentPointForm;

/**
 * 评论的服务对象接口
 * 
 * @author 关中
 * 
 */
public class CommentsTest {

	FileSystemXmlApplicationContext context;

	@Before
	public void setUp() throws Exception {
		SpringTestcaseUtil.disableDebug();
		JdbcTemplateUtil.disableDebug(true);
		context = new SpringTestcaseUtil.ApplicationContextBuilder().resource(
				CommentsTest.class, "comments.xml").build();
	}

	@After
	public void tearDown() throws Exception {
		if (context != null)
			context.close();
	}

	@Test
	public void testCommentPoint_GetById() throws Exception {
		CommentsService s = context.getBean("service", CommentsService.class);
		Object o = s.getCommentPoint(1);
		System.out.println(o);
		o = s.getCommentPoint(1);
		System.out.println(o);
	}

	@Test
	public void testCommentPoint_GetByName() throws Exception {
		CommentsService s = context.getBean("service", CommentsService.class);
		Object o = s.getCommentPoint("name");
		System.out.println(o);
		o = s.getCommentPoint("name");
		System.out.println(o);
		o = s.getCommentPoint("name2");
		System.out.println(o);
		o = s.getCommentPoint("name2");
		System.out.println(o);
	}

	@Test
	public void testCommentPoint_Create() throws Exception {
		CommentsService s = context.getBean("service", CommentsService.class);

		CommentPointForm form = new CommentPointForm();
		form.setName("name4");
		form.setReserve1(1);
		form.setReserve2(2);
		form.setReserve3("r3");
		form.setReserve4("r4");
		form.setTitle("title");
		form.setUrl("url");

		System.out.println(s.createCommentPoint(form));
	}

	@Test
	public void testCommentPoint_Update() throws Exception {
		CommentsService s = context.getBean("service", CommentsService.class);

		CommentPointForm form = new CommentPointForm();
		form.setName("name1");
		form.setReserve1(1);
		form.setReserve2(2);
		form.setReserve3("r3");
		form.setReserve4("r4");
		form.setTitle("title");
		form.setUrl("url");

		System.out.println(s.updateCommentPoint(1, form));
	}

	@Test
	public void testCommentPoint_Delete() throws Exception {
		CommentsService s = context.getBean("service", CommentsService.class);
		Object o = s.deleteCommentPoint(11);
		System.out.println(o);
	}

	@Test
	public void testCommentPoint_Search() throws Exception {
		CommentsService s = context.getBean("service", CommentsService.class);

		SearchCommentPointForm form = new SearchCommentPointForm();
		form.setName("name");
		form.setPage(1);
		form.setPageSize(10);

		Object o = s.searchCommentPoint(form);
		System.out.println(o);
	}

	@Test
	public void testComment_GetById() throws Exception {
		CommentsService s = context.getBean("service", CommentsService.class);
		Object o = s.getComment(1);
		System.out.println(o);
		o = s.getComment(1);
		System.out.println(o);
	}

	@Test
	public void testComment_Create() throws Exception {
		CommentsService s = context.getBean("service", CommentsService.class);

		int cp = 13;

		CommentPointForm f = new CommentPointForm();
		f.setName("name5");
		f.setTitle("title");
		f.setUrl("url");

		CommentForm f2 = new CommentForm();
		f2.setContent("我是评论内容");
		f2.setIp("127.0.0.1");
		f2.setPointId(cp);
		f2.setReplyId(0);
		f2.setUserId(0);
		f2.setUserName("昵称");

		System.out.println(s.createComment(f2, f));
	}

	@Test
	public void testComment_Update() throws Exception {
		CommentsService s = context.getBean("service", CommentsService.class);

		int id = 13;

		CommentForm f2 = new CommentForm();
		f2.setContent("我是评论内容");
		f2.setUserId(0);
		f2.setUserName("昵称");

		List<String> fs = new LinkedList<String>();
		fs.add("content");
		fs.add("hide_flag");
		System.out.println(s.updateComment(id, f2, fs));
	}

	@Test
	public void testComment_Delete() throws Exception {
		CommentsService s = context.getBean("service", CommentsService.class);

		System.out.println(s.deleteComment(5));
	}

	@Test
	public void testComment_Search() throws Exception {
		CommentsService s = context.getBean("service", CommentsService.class);

		SearchCommentForm form = new SearchCommentForm();
		// form.setContent("我是");
		// form.setPointId(15);
		// form.setPoint("name1");
		form.setPage(1);
		form.setPageSize(10);

		Object o = s.searchComment(form);
		System.out.println(o);
	}

}
