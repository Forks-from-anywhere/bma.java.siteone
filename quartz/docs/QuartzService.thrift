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
namespace php quartz
namespace java bma.siteone.quartz.thrift

struct TJobForm {
	1: string type,
	2: string name,
	3: string group,
	4: bool durability,
	5: bool recover,
	6: bool disallowConcurrent,
	7: bool persistAfterExecution,
	8: map<string,string> jobDatas,
}

struct TTriggerForm {
	1: string type,
	2: string name,
	3: string group,
	4: i32 priority = 0,
	5: string startTime,
	6: string endTime,
	7: string missfire,
	8: i32 repeat = 0,
	9: i32 interval = 0,
	10: string cron,
}

struct TJobInfo {
	1: string type,
	2: string name,
	3: string group,
	4: bool durability,	
	5: bool disallowConcurrent,
	6: bool persistAfterExecution,
	7: map<string,string> jobDatas,
}

struct TTriggerInfo {
	1: string name,
	2: string group,
	3: i32 priority = 0,
	4: string startTime,
	5: string endTime,
	6: string missfire,
	7: string nextFireTime,
	8: string previousFireTime,
	9: string schedule,
}

service TQuartzService {

	bool pause(1:string serviceName, 2:bool pause),

   	string newJob(1:string serviceName, 2:TJobForm job, 3:TTriggerForm trigger),
   
 	string newTrigger(1:string serviceName, 2:string jobName, 3:string jobGroup, 4:TTriggerForm trigger),

	bool removeJob(1:string serviceName, 2:string jobName, 3:string jobGroup),
   
	bool removeTrigger(1:string serviceName, 2:string triggerName, 3:string triggerGroup),
	
	TJobInfo queryJob(1:string serviceName, 2:string jobName, 3:string jobGroup),
	
	TTriggerInfo queryTrigger(1:string serviceName, 2:string triggerName, 3:string triggerGroup),
   
}