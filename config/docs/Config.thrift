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
namespace java bma.siteone.config.thrift

struct TConfigInfo {
  1: string group,
  2: string name,
  3: string value,
}

service TConfigAdminService {

   bool refreshConfig(1:string app, 2:string group),

   bool setConfig(1:string app, 2:TConfigInfo info),
   
   bool deleteConfig(1:string app, 2:string group, 3:string name),
   
}