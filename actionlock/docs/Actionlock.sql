CREATE TABLE so_actionlock (
  group_type varchar(16) NOT NULL default '' COMMENT '锁分组',
  item_id varchar(64) NOT NULL default ''  COMMENT '锁项编号',  
  
  access_count int NOT NULL default '0' COMMENT '访问次数',
  clean_time datetime NOT NULL COMMENT '清除时间',
 
  create_time datetime NOT NULL COMMENT '创建时间',
  last_update_time datetime NOT NULL  COMMENT '更新时间',
  
  PRIMARY KEY  (group_type,item_id),
  INDEX actionlock_i1 (clean_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
