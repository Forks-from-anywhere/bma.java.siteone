/**
 * The first thing to know about are types. The available types in Thrift are:
 *
 *  bool        Boolean, one byte
 *  byte        Signed byte
 *  i16         Signed 16-bit integer
 *  i32         Signed 32-bit integer
 *  i64         Signed 64-bit integer
 *  double      64-bit floating point value
 *  string      String
 *  binary      Blob (byte array)
 *  map<t1,t2>  Map from one type to another
 *  list<t1>    Ordered list of one type
 *  set<t1>     Set of unique elements of one type
 *
 * Did you also notice that Thrift supports C style comments?
 */
namespace php comments
namespace java bma.siteone.comments.thrift

struct TCommentPoint {
  1: i32 id = 0,
  2: string name,
  3: string url,
  4: string title,
  5: i32 reserve1,
  6: i32 reserve2,
  7: string reserve3,
  8: string reserve4,
  9: i32 status,
  10: i32 commentAmount,
  11: string lastCommentTime,
  12: string createTime,
  13: string lastUpdateTime,
}

struct TCommentPointForm {
  1: string name,
  2: string url,
  3: string title,
  4: optional i32 reserve1 = 0,
  5: optional i32 reserve2 = 0,
  6: optional string reserve3,
  7: optional string reserve4,  
}

struct TSearchCommentPointForm {
  1: optional string name,
  2: optional string url,
  3: optional string title,
  4: i32 page,
  5: i32 pageSize,
  6: optional string sort,
  7: optional string sortDir,
}

struct TCommentPointSearchResult {
  1: i32 total,
  2: list<TCommentPoint> result,
}

struct TCommentInfo {
  1: i32 id = 0,
  2: i32 pointId,
  3: i32 replyId,
  4: string userName,
  5: i32 userId,
  6: string content,
  7: string ip,
  8: i32 reserve1,
  9: i32 reserve2,
  10: string reserve3,
  11: string reserve4,
  12: i32 status,
  13: i32 support,
  14: i32 oppose,
  15: i32 needAuth,
  16: i32 hideFlag
  17: string createTime,
}

struct TCommentForm {
  1: i32 pointId,
  2: i32 replyId,
  3: string userName,
  4: i32 userId,
  5: string content,
  6: string ip,
  7: optional i32 reserve1 = 0,
  8: optional i32 reserve2 = 0,
  9: optional string reserve3,
  10: optional string reserve4,  
}

struct TSearchCommentForm {
  1: optional i32 pointId,
  2: optional string point,  
  3: optional i32 replyId,
  4: optional string subject,
  5: optional string url,
  6: optional string userName,
  7: optional i32 userId,
  8: optional string content,
  9: optional string ip,
  10: optional string startTime,
  11: optional string endTime,
  12: optional i32 needAuth = -1,
  13: optional i32 status = -1,
  14: optional i32 hide = -1
  16: i32 page,
  17: i32 pageSize,
  18: optional string sort,
  19: optional string sortDir,
}

struct TCommentSearchResult {
  1: i32 total,
  2: list<TCommentInfo> result,
}

struct TCommentHome {
  1: TCommentPoint point,
  2: i32 total,
  3: list<TCommentInfo> result,
}

service TCommentsService {

   i32 createComment(1:TCommentForm info, 2:TCommentPointForm pointForm),
   
   bool deleteComment(1:i32 id),
   
   TCommentInfo getComment(1:i32 id),
   
   TCommentSearchResult searchComment(1:TSearchCommentForm form),   
   
   TCommentSearchResult listComment(1:i32 pointId, 2:i32 page, 3:i32 pageSize),
   
   bool supportComment(1:i32 id, 2:bool oppose),
   
   bool authComment(1:i32 id, 2:bool authDone),
   
   bool reportComment(1:i32 id, 2:bool hide),
   
   i32 createCommentPoint(1:TCommentPointForm pointForm),
   
   bool updateCommentPoint(1:i32 id, 2:TCommentPointForm pointForm),
   
   bool deleteCommentPoint(1:i32 id),

   TCommentPoint getCommentPoint(1:i32 id),
   
   TCommentPoint getCommentPointByName(1:string name),
   
   TCommentPointSearchResult searchCommentPoint(1:TSearchCommentPointForm form),   
   
   TCommentHome getHome(1:string point, 2:i32 pageSize),
   
}