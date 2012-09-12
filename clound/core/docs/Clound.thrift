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
namespace php bma.clound.thrift
namespace java bma.siteone.clound.thrift

struct TCloundEntry {
  1: string nodeId,
  2: string appId,
  3: string serviceId,
  4: string apiId,
}

struct TCloundRequest {
  1: TCloundEntry entry,
  2: string content,
  3: TCloundEntry callback,
  4: bool logtrack,
  5: TCloundEntry referer,
}

struct TCloundResponse {
  1: i32 type,
  2: string content,
  3: list<string> logtrack,
}

service TClound {

	/* CloundNode */
	string getCloundNodeDesc(1:string nodeId),
	
	list<string> listCloundAppDesc(1:string nodeId),
	
	bool createCloundApp(1:string nodeId, 2:string appId, 3:string appName),
	
	bool closeCloundApp(1:string nodeId, 2:string appId),
	
	/* CloundApp */
	string getCloundAppDesc(1:string nodeId, 2:string appId),
	
	list<string> listCloundServiceDesc(1:string nodeId, 2:string appId),

	/* CloundService */
	string getCloundServiceDesc(1:string nodeId, 2:string appId, 3:string serviceId),
	
	list<string> listCloundApiDesc(1:string nodeId, 2:string appId, 3:string serviceId),
	
	/* CloundAPi */
	string getCloundApiDesc(1:string nodeId, 2:string appId, 3:string serviceId, 4:string apiId),

	/* Call */
	TCloundResponse cloundCall(1:TCloundRequest req),
}