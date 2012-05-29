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
namespace php bma.actionlock.thrift
namespace java bma.siteone.actionlock.thrift

struct TActionlockInfo {
	1: string groupType,
	2: string itemId,
	3: i32 accessCount,
	4: string cleanTime,
	5: optional string createTime,
}

struct TActionlockSearchResult {
	1: i32 total,
	2: list<TActionlockInfo> result,
}

service TActionlockService {

	TActionlockInfo getActionlock(1:string groupType, 2:string itemId, 3:bool timeout),

	i32 setActionlock(1:string groupType, 2:string itemId, 3:i32 accessCount, 4:i32 cleanDelay),

	bool checkActionlock(1:string groupType, 2:string itemId, 3:i32 accessCount, 4:bool release),

	bool deleteActionlock(1:string groupType, 2:string itemId),
	
	bool deleteActionlockGroup(1:string groupType),
	
	oneway void cleanActionlock(1:string groupType),
      
	list<string> listActionlockGroupType(),
   
	TActionlockSearchResult searchActionlock(1:string sql, 2:i32 page, 3:i32 pageSize),
   
}