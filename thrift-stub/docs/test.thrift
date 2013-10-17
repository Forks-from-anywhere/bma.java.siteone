namespace java bma.common.thrift.sample

struct TMedal {
  1: i32 id,
  2: string name,
  3: string description,
  4: string activity_url,
  5: string highlight_icon,
  6: string ash_icon,
  7: string hd_icon,
  8: optional string icon_url,
  9: i32 num,
  10: optional string rules,
  11: i32 start_time,
  12: i32 end_time,
}

struct TMedalLstResult {
  1: list<TMedal> medalList,
}

service Test {

   oneway void useMedal(1:TMedal medal),

   set<string> medalNames(),
   
   map<string, list<TMedal>> getMedal(1:list<string> ids);

   void error(1:string msg),
   
   void sleep(1:i32 time),
}