/**
 * 报警系统thrift文档
 * version v1.0.0
 */
 
include "alarmclient.thrift"

namespace php bma.alarm.thrift
namespace java bma.siteone.alarm.thrift

struct TAlarm {
  1: i32 id,
  2: i32 type,
  3: i32 level,
  4: string system,
  5: string stype1,
  6: string stype2,
  7: string content,
  8: i32 starttime,
  9: i32 endtime,
  10: i32 frequency,
  11: i32 times,
  12: i32 status,
  13: string op_time,
  14: string op_user,
}

struct TAlarmQueryForm {
  1: optional i32 type,
  2: optional i32 level,
  3: optional string system,
  4: optional string stype1,
  5: optional string stype2,
  6: optional string content,
  7: optional i32 starttime,
  8: optional i32 endtime,
  9: optional i32 status,
}

struct TAlarmQueryResult {
  1: i32 total,
  2: list<TAlarm> alarms,
}

service TAlarmService extends alarmclient.TAlarmClient {

	TAlarmQueryResult queryAlarm(1:TAlarmQueryForm alarmQueryForm, 2:i32 page, 3:i32 pageSize, 4:string orderBy, 5:string order),	//查询报警历史
	TAlarmQueryResult queryAlarmByIds(1:list<i32> ids);	//查询报警历史
	
	bool clearAlarm(1:TAlarmQueryForm alarmQueryForm),	//清除报警历史
	bool clearAlarmByIds(1:list<i32> ids),	//清除报警历史
	
	bool releaseAlarm(1:TAlarmQueryForm alarmQueryForm),		//解除报警
	bool releaseAlarmByIds(1:list<i32> ids),		//解除报警

}