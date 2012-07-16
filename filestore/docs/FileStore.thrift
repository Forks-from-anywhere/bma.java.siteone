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
namespace php bma.filestore.thrift
namespace java bma.siteone.filestore.thrift

service TFileStore {

   string beginSession(1:string appId),

   void sendFile(1:string token, 2:string path, 3:binary content, 4:string vcode),
   
   void removeFile(1:string token, 2:string path, 3:string vcode), 

   void commitSession(1:string token, 2:string vcode),
   
   binary readFile(1:string appId, 2:string path, 3:i32 pos, 4:i32 readSize, 5:string vcode),
}