package bma.siteone.message.service;

import java.util.List;

import bma.common.langutil.core.PagerResult;
import bma.siteone.message.po.MessageInfo;

public interface MessageService {
	
	/**
	 * 发送一条消息
	 * @param sendMessageForm
	 * @return
	 */
	public int sendMessage(SendMessageForm sendMessageForm);
	
	/**
	 * 删除消息
	 * @param ids
	 * @return
	 */
	public int deleteMessages(List<Integer> ids);
	
	/**
	 * 查询接收者的消息（按照发送时间倒序排序）
	 * @param searchForm
	 * @return
	 */
	public PagerResult<MessageInfo> searchReceiverMessage(SearchReceiverMessageForm searchForm);
	
	/**
	 * 把消息置为已读
	 * @param ids
	 * @return
	 */
	public int setMessagesRead(List<Integer> ids);
	
	/**
	 * 自动删除过期的消息（默认超过半年为过期）
	 * @return
	 */
	public boolean deleteExpireMessages();
	

}
