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
namespace php bma.cloud.thrift
namespace java bma.siteone.cloud.thrift

struct TCloudEntry {
  1: string nodeId,
  2: string appId,
  3: string serviceId,
  4: string apiId,
}

struct TCloudRequest {
  1: TCloudEntry entry,
  2: string contentType,
  3: binary content,
  4: map<string,string> context,
  5: TCloudEntry callback,
  6: bool logtrack,  
}

struct TCloudResponse {
  1: i32 type,				// TYPE_ERROR = -1;TYPE_AICALL = 0;TYPE_DONE = 1;TYPE_REDIRECT = 2;
  2: string contentType,
  3: binary content,
  4: list<string> logtrack,
}

service TCloud {

	/* CloudNode */
	string getCloudNodeDesc(1:string nodeId),
	
	list<string> listCloudAppDesc(1:string nodeId),
	
	bool createCloudApp(1:string nodeId, 2:string appId, 3:string appName),
	
	bool closeCloudApp(1:string nodeId, 2:string appId),
	
	/* CloudApp */
	string getCloudAppDesc(1:string nodeId, 2:string appId),
	
	list<string> listCloudServiceDesc(1:string nodeId, 2:string appId),

	/* CloudService */
	string getCloudServiceDesc(1:string nodeId, 2:string appId, 3:string serviceId),
	
	list<string> listCloudApiDesc(1:string nodeId, 2:string appId, 3:string serviceId),
	
	/* CloudAPi */
	string getCloudApiDesc(1:string nodeId, 2:string appId, 3:string serviceId, 4:string apiId),

	/* Call */
	TCloudResponse cloudCall(1:TCloudRequest req),
}