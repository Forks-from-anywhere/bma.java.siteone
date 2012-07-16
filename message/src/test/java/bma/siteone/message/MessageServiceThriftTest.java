package bma.siteone.message;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import bma.common.jdbctemplate.JdbcTemplateUtil;
import bma.common.langutil.testcase.SpringTestcaseUtil;
import bma.siteone.message.thrift.TMessageInfo;
import bma.siteone.message.thrift.TMessageSearchResult;
import bma.siteone.message.thrift.TMessageService;
import bma.siteone.message.thrift.TSearchReceiverMessageForm;
import bma.siteone.message.thrift.TSendMessageForm;


public class MessageServiceThriftTest {

	FileSystemXmlApplicationContext context;
	
	@Before
	public void setUp() throws Exception {
		SpringTestcaseUtil.disableDebug();
		JdbcTemplateUtil.disableDebug(false);
		context = new SpringTestcaseUtil.ApplicationContextBuilder().project("src/test/resources/spring_server.xml").build();
	}

	@After
	public void tearDown() throws Exception {
		if (context != null)
			context.close();
	}
	
	@Test
	public void testSendMessage() throws Exception {
		TMessageService.Iface s = context.getBean("handler", TMessageService.Iface.class);
		
		TSendMessageForm sendMessageForm = new TSendMessageForm();
		sendMessageForm.setApp("live2");
		sendMessageForm.setType(1);
		sendMessageForm.setReceiver("aabbcc");
		sendMessageForm.setSender("sys");
		sendMessageForm.setTitle("你的申请已通过");
		sendMessageForm.setContent("你的申请已通过\n签约频道：90");
		int r = s.sendMessage(sendMessageForm);
		
		System.out.println(r);
	}
	
	@Test
	public void testDeleteMessages() throws Exception {
		TMessageService.Iface s = context.getBean("handler", TMessageService.Iface.class);
		
		List<Integer> ids = new ArrayList<Integer>();
		ids.add(new Integer(7));
		ids.add(new Integer(8));
		int r = s.deleteMessages(ids);
		
		System.out.println(r);
	}
	
	@Test
	public void testSetMessagesRead() throws Exception {
		TMessageService.Iface s = context.getBean("handler", TMessageService.Iface.class);
		
		List<Integer> ids = new ArrayList<Integer>();
		ids.add(new Integer(5));
		ids.add(new Integer(9));
		int r = s.setMessagesRead(ids);
		
		System.out.println(r);
	}
	
	@Test
	public void testSearchReceiverMessage() throws Exception {
		TMessageService.Iface s = context.getBean("handler", TMessageService.Iface.class);
		
		TSearchReceiverMessageForm searchForm = new TSearchReceiverMessageForm();
		searchForm.setReceiver("aabbcc");
		searchForm.setApp("live2");
		searchForm.setPage(1);
		searchForm.setPageSize(20);
		TMessageSearchResult r = s.searchReceiverMessage(searchForm);
		
		System.out.println("total=> "+r.getTotal());
		for (TMessageInfo m : r.getResult()) {
			System.out.println(m);
			System.out.println("-------------");
		}
	}

	@Test
	public void testDeleteExpireMessages() throws Exception {
		TMessageService.Iface s = context.getBean("handler", TMessageService.Iface.class);
		
		boolean r = s.deleteExpireMessages();
		
		System.out.println(r);
	}
	
	@Test
	public void testGetUnreadMessageNum() throws Exception {
		TMessageService.Iface s = context.getBean("handler", TMessageService.Iface.class);
		
		String receiver = "123111";
		String app = "live";
		int r = s.getUnreadMessageNum(receiver,app);
		
		System.out.println(r);
	}
	
}
