package bma.siteone.message.service;

import java.util.List;

import org.apache.thrift.TException;

import bma.common.langutil.core.Function;
import bma.common.langutil.core.ListUtil;
import bma.common.langutil.core.PagerResult;
import bma.siteone.message.po.MessageInfo;
import bma.siteone.message.thrift.*;
public class MessageServiceThrift implements TMessageService.Iface{

	final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(MessageServiceThrift.class);
	
	private MessageService service;
	
	
	public MessageService getService() {
		return service;
	}

	public void setService(MessageService service) {
		this.service = service;
	}
	

	@Override
	public int sendMessage(TSendMessageForm sendMessageForm) throws TException {
		if (log.isDebugEnabled()) {
			log.debug("sendMessage(rec:{})",sendMessageForm.getReceiver());
		}
		
		SendMessageForm sendForm = new SendMessageForm();
		sendForm.setApp(sendMessageForm.getApp());
		sendForm.setType(sendMessageForm.getType());
		sendForm.setReceiver(sendMessageForm.getReceiver());
		sendForm.setSender(sendMessageForm.getSender());
		sendForm.setTitle(sendMessageForm.getTitle());
		sendForm.setContent(sendMessageForm.getContent());
		return service.sendMessage(sendForm);
	}

	@Override
	public int deleteMessages(List<Integer> ids) throws TException {
		if (log.isDebugEnabled()) {
			log.debug("deleteMessages");
		}
		return service.deleteMessages(ids);
	}

	@Override
	public TMessageSearchResult searchReceiverMessage(
			TSearchReceiverMessageForm searchForm) throws TException {
		if (log.isDebugEnabled()) {
			log.debug("searchReceiverMessage({},{})",searchForm.getReceiver(),searchForm.getApp());
		}
		
		SearchReceiverMessageForm sForm = new SearchReceiverMessageForm();
		sForm.setReceiver(searchForm.getReceiver());
		sForm.setApp(searchForm.getApp());
		sForm.setPage(searchForm.getPage());
		sForm.setPageSize(searchForm.getPageSize());
		
		PagerResult<MessageInfo> pr = service.searchReceiverMessage(sForm);
		
		TMessageSearchResult result = new TMessageSearchResult();
		result.setTotal(pr.getPager().getTotal());
		result.setResult(ListUtil.toList(pr.getResult(),
				new Function<MessageInfo, TMessageInfo>() {
					@SuppressWarnings("deprecation")
					@Override
					public TMessageInfo apply(MessageInfo input) {
						// TODO Auto-generated method stub
						TMessageInfo tInfo = new TMessageInfo();
						tInfo.setId(input.getId());
						tInfo.setApp(input.getApp());
						tInfo.setType(input.getType());
						tInfo.setReceiver(input.getReceiver());
						tInfo.setSender(input.getSender());
						tInfo.setTitle(input.getTitle());
						tInfo.setContent(input.getContent());
						tInfo.setSendTime(input.getSendTime().toLocaleString());
						tInfo.setIsRead(input.getIsRead());
						return tInfo;
					}
				}));
		
		return result;
	}

	@Override
	public int setMessagesRead(List<Integer> ids) throws TException {
		if (log.isDebugEnabled()) {
			log.debug("setMessagesRead({})",bma.common.langutil.core.StringUtil.join(ids, ","));
		}
		return service.setMessagesRead(ids);
	}

	@Override
	public boolean deleteExpireMessages() throws TException {
		if (log.isDebugEnabled()) {
			log.debug("deleteExpireMessages");
		}
		service.deleteExpireMessages();
		return true;
	}

	@Override
	public int getUnreadMessageNum(String receiver, String app)
			throws TException {
		if (log.isDebugEnabled()) {
			log.debug("getUnreadMessageNum({},{})",receiver,app);
		}
		return service.getUnreadMessageNum(receiver, app);
	}
	
}
