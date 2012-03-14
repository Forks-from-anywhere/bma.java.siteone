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
namespace php matchlist
namespace java bma.siteone.matchlist.thrift

struct TMatchInfo {
  1: i32 id = 0,
  2: string groupId,
  3: optional string title,
  4: string content,
  5: i32 reserve1,
  6: i32 reserve2,
  7: string reserve3,
  8: string reserve4,
  9: string createTime,
  10: string lastUpdateTime,
}

struct TMatchSearchResult {
  1: i32 total,
  2: list<TMatchInfo> result,
}

service TMatchListService {

   i32 createOne(1:TMatchInfo info),

   bool updateOne(1:TMatchInfo info),

   bool deleteOne(1:i32 id),
   
   TMatchInfo getOne(1:i32 id),
   
   bool match(1:string groupId, 2:string v, 3:string type),
   
   list<string> listGroupId(),
   
   list<TMatchInfo> listGroup(1:string groupId),
   
   TMatchSearchResult search(1:string hsql, 2:i32 page, 3:i32 pageSize),
   
}