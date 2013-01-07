/**
 * 报警系统客户端thrift文档
 * version v1.0.0
 */

namespace php bma.alarm.thrift
namespace java bma.siteone.alarmclient.thrift


struct TAlarmCommitForm {
  1: i32 type,
  2: i32 level = 5,
  3: string system,
  4: string stype1,
  5: string stype2,
  6: string content,
  7: optional i32 starttime,
  8: optional i32 endtime,
  9: optional i32 frequency,
  10: optional i32 times,
}


service TAlarmClient {

	oneway void commitAlarm(1:TAlarmCommitForm alarmCommitForm),		//提交报警

}