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
namespace php bma.evaluate.thrift
namespace java bma.siteone.evaluate.thrift

struct TEvaluateInfo {  
  1: string groupType,
  2: string itemId,
  3: optional string url,
  4: optional string title,
  5: optional i32 evaAmount = -1,
  6: optional i32 option1 = -1,
  7: optional i32 option2 = -1,
  8: optional i32 option3 = -1,
  9: optional i32 option4 = -1,
  10: optional i32 option5 = -1,
  11: optional i32 option6 = -1,
  12: optional i32 option7 = -1,
  13: optional i32 option8 = -1,
  14: optional i32 option9 = -1,
  15: optional i32 option10 = -1,
  
  16: optional i32 status = 0,
  
  17: optional i32 reserve1 = 0,
  18: optional i32 reserve2 = 0,
  19: optional string reserve3 = '',
  20: optional string reserve4 = '',
  
  21: optional string createTime,
  22: optional string lastUpdateTime,  
}

struct TEvaluateSearchResult {
  1: i32 total,
  2: list<TEvaluateInfo> result,
}

service TEvaluateService {

   bool voteEvaluate(1:TEvaluateInfo info),

   bool updateEvaluate(1:TEvaluateInfo info),

   bool deleteEvaluate(1:string groupType, 2:string itemId),
   
   bool deleteEvaluateGroup(1:string groupType),
   
   TEvaluateInfo getEvaluate(1:string groupType, 2:string itemId),
   
   list<string> listEvaluateGroupType(),
   
   TEvaluateSearchResult searchEvaluate(1:string sql, 2:i32 page, 3:i32 pageSize),
   
}