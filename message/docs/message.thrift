
namespace php bma.message.thrift
namespace java bma.siteone.message.thrift

struct TMessageInfo {
  1: i32 id ,			//消息id
  2: string app,		//应用标识，默认为live
  3: i32 type,			//消息类型，1系统消息（默认）
  4: string receiver,		//接收者
  5: string sender,		//发送者
  6: string title,		//消息标题
  7: string content,		//消息主体内容
  8: string sendTime,		//发送时间,格式 2012-12-21 10:10:10
  9: i32 isRead,		//是否已读，0未读（默认），1已读
}

struct TSendMessageForm {
  1: string app,
  2: i32 type, 
  3: string receiver,
  4: string sender,
  5: string title,
  6: string content,
}

struct TMessageSearchResult {
  1: i32 total,
  2: list<TMessageInfo> result,
}

struct TSearchReceiverMessageForm {
  1: string receiver,
  2: string app,
  3: optional i32 page = 1,
  4: optional i32 pageSize = 10,
}

service TMessageService {

   i32 sendMessage(1:TSendMessageForm sendMessageForm),		//发送一条消息

   i32 deleteMessages(1:list<i32> ids),				//删除消息

   TMessageSearchResult searchReceiverMessage(1:TSearchReceiverMessageForm searchForm),		//查询接收者的消息（按照发送时间倒序排序）

   i32 setMessagesRead(1:list<i32> ids),			//把消息置为已读

   bool deleteExpireMessages(),					//自动删除过期的消息,默认超过半年为过期

   i32 getUnreadMessageNum(1:string receiver,2: string app)	//查询未读数

}