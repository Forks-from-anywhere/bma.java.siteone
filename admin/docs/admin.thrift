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

namespace php bma.admin.thrift
namespace java bma.siteone.admin.thrift

/**
*********************** 应用相关 **********************
*/

struct TOpLogForm {
  1: string userName,
  2: string appName,
  3: string roleName,
  4: string opName,
  5: string description,
}

service TAdminAppService {

	bool checkUserAuth(1:string userName, 2:string appName, 3:string opName),

	bool addOpLog(1:TOpLogForm opLogForm),

}


/**
*********************** 管理后台相关 **********************
*/

struct TUserForm {
  1: string userName,
  2: string password,
  3: string userDescription,
}

struct TUser {
  1: string userName,
  2: string password,
  3: optional string userDescription,
  4: optional string createTime,
  5: optional i32 status,
}

struct TRole {
  1: string appName,
  2: string roleName,
  3: optional string roleDescription,
  4: optional string createTime,
  5: optional i32 status,
}

struct TOp {
  1: string appName,
  2: string opName,
  3: optional string opDescription,
  4: optional string createTime,
  5: optional i32 status,
}

struct TSetRoleOpsForm {
  1: string roleName,
  2: list<string> opNameList,
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

struct TOpLog {
  1: i32 id,
  2: string userName,
  3: string appName,
  4: string roleName,
  5: string opName,
  6: string time,
  7: string description,
}

struct TOpLogRessult {
  1: i32 total,
  2: list<TOpLog> result,
}

service TAdminManagerService {
	
	/**
	*********************** 用户相关 **********************
	*/

	bool createUser(1:TUserForm userForm),

	bool deleteUser(1:string userName),

	TUser getUser(1:string userName),

	list<TUser> queryAllUser(),

	list<TRole> queryUserRoles(1:string userName),

	bool checkUserExist(1:string userName),

	bool changePassword(1:string userName, 2:string oldPassword, 3:string newPassword),

	bool resetPassword(1:string userName, 2:string newPassword),

	bool checkUserPassword(1:string userName, 2:string password),


	/**
	*********************** 应用设置 **********************
	*/

	list<TRole> queryAppRoles(1:string appName),

	list<TOp> queryAppOps(1:string appName),				//查询应用的所有操作

	map<string,list<string>> queryAppRolesByOps(1:string appName, 2:list<string> opNames),	//查询操作对应的所有角色（批查）

	list<string> queryAppOpsByRole(1:string appName, 2:string roleName),	//查询角色对应的所有操作

        bool createRole(1:TRole role),						//增加角色

	bool deleteRole(1:string appName, 2:string roleName),			//删除角色

	bool resetRoleOps(1:string appName, 2:TSetRoleOpsForm roleOpsForm),	//重设角色对应的操作


	/**
	*********************** 应用授权 **********************
	*/

	bool addUserAuth(1:string userName, 2:string appName, 3:string roleName),

	bool deleteUserAuth(1:string userName, 2:string appName, 3:string roleName),


	/**
	*********************** 操作日志 **********************
	*/

	TOpLogRessult queryOpLogs(1:TOpLogQueryForm opLogQueryForm),
}



/**
*********************** 部署相关 **********************
*/

struct TSync {
  1: i32 opCode,
  2: string content,
}

service TAdminDeployService {

	bool initAppAuth(1:list<TSync> syncInit),				//初始化应用权限

	bool upgradeAppAuth(1:list<TSync> syncUpgrade),				//升级应用权限

	list<TSync> exportAppAuth(1:string appName),				//导出应用权限

	bool syncApp(1:string syncContent),					//权限同步
    
}