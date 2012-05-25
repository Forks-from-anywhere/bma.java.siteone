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
namespace php bma.wordstock.thrift
namespace java bma.siteone.wordstock.thrift

struct TWordInfo {
  1: i32 id = 0,
  2: string groupType,
  3: string title, 
  4: string words,
  5: string type,
}

struct TWordStockSearchResult {
  1: i32 total,
  2: list<TWordInfo> result,
}

service TWordStockService {

   i32 createWord(1:TWordInfo info),

   bool updateWord(1:TWordInfo info),

   bool deleteWord(1:i32 id),
   
   TWordInfo getWord(1:i32 id),
   
   bool matchWordStock(1:string groupType, 2:string content),
   
   list<string> listWordStockGroupType(),
   
   list<TWordInfo> listWordStockInfo(1:string groupType),
   
   TWordStockSearchResult searchWordStock(1:string sql, 2:i32 page, 3:i32 pageSize),
   
}