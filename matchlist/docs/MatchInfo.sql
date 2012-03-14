CREATE TABLE sot_match_info (
  id int NOT NULL AUTO_INCREMENT,  
  group_id varchar(16) NOT NULL default '' COMMENT '分组类型',
  title varchar(64) NOT NULL default '' COMMENT '说明',
  content varchar(1024) COMMENT '内容',
  
  reserve1 int NOT NULL default 0,
  reserve2 int NOT NULL default 0,
  reserve3 varchar(32) NOT NULL default '',
  reserve4 varchar(32) NOT NULL default '',  
  create_time datetime NOT NULL default '0000-00-00 00:00:00',
  last_update_time datetime NOT NULL default '0000-00-00 00:00:00',  
  
  PRIMARY KEY  (id),
  INDEX sot_match_info_i1 (group_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;