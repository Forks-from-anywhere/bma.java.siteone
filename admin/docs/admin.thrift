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

namespace php admin
namespace java bma.siteone.admin.thrift

struct TUser {
  1: string userName,
  2: string password,
  3: string userDescription,
  4: string createTime,
  5: i32 status,
}

struct TUserForm {
  1: string userName,
  2: string password,
  3: string userDescription,
}

struct TOp {
  1: string appName,
  2: string opName,
  3: string opDescription,
  4: string createTime,
  5: i32 status,
}

struct TRole {
  1: string appName,
  2: string roleName,
  3: string roleDescription,
  4: string createTime,
  5: i32 status,
}

struct TOpLog {
  1: i32 id,
  2: string userName,
  3: string appName,
  4: string roleName,
  5: string opName,
  6: string time,
  7: string description,
}

struct TOpLogForm {
  1: string userName,
  2: string appName,
  3: string roleName,
  4: string opName,
  5: string description,
}

struct TOpLogRessult {
  1: i32 total,
  2: list<TOpLog> result,
}

struct TOpLogQueryForm {
  1: optional string userName,
  2: optional string appName,
  3: optional string roleName,
  4: optional string opName,
  5: optional string description,
  6: optional string startTime,
  7: optional string endTime,
  8: i32 page,
  9: i32 pageSize,
  10: optional string sort,
  11: optional string sortDir,
}

service TAdminService {

	bool createUser(1:TUserForm userForm),

	bool deleteUser(1:string userName),

	bool changePassword(1:string userName, 2:string oldPassword, 3:string newPassword),

	bool resetPassword(1:string userName, 2:string newPassword),

	bool checkUserPassword(1:string userName, 2:string password),

	bool syncApp(1:string syncContent),

	list<TRole> queryRoles(1:string userName),

	bool addUserAuth(1:string userName, 2:string appName, 3:string roleName),

	bool deleteUserAuth(1:string userName, 2:string appName, 3:string roleName),

	bool checkUserAuth(1:string userName, 2:string appName, 3:string opName),

	bool addOpLog(1:TOpLogForm opLogForm),

	TOpLogRessult queryOpLogs(1:TOpLogQueryForm opLogQueryForm)

}