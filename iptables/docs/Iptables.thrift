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
namespace php bma.iptables.thrift
namespace java bma.siteone.iptables.thrift

struct TIptableInfo {
  1: i32 id = 0,
  2: string groupType,
  3: string inet,
  4: string type,
}

struct TIptableSearchResult {
  1: i32 total,
  2: list<TIptableInfo> result,
}

service TIptablesService {

   i32 createIptable(1:TIptableInfo info),

   bool updateIptable(1:TIptableInfo info),

   bool deleteIptable(1:i32 id),
   
   TIptableInfo getIptable(1:i32 id),
   
   string matchIptables(1:string groupType, 2:string ip),
   
   list<string> listIptablesGroupType(),
   
   list<TIptableInfo> listIptablesInfo(1:string groupId),
   
   TIptableSearchResult searchIptables(1:string sql, 2:i32 page, 3:i32 pageSize),
   
}