CREATE TABLE so_evaluate (
  group_type varchar(16) NOT NULL default '' COMMENT '评价分组',
  item_id varchar(32) NOT NULL default ''  COMMENT '评价项编号',  
  url varchar(255) NOT NULL default '' COMMENT '评价的相关链点',
  title varchar(255) NOT NULL default '' COMMENT '评价的标题',
  
  eva_amount int unsigned NOT NULL default '0' COMMENT '评价的人次',
  option1 int NOT NULL default 0,
  option2 int NOT NULL default 0,
  option3 int NOT NULL default 0,
  option4 int NOT NULL default 0,
  option5 int NOT NULL default 0,
  option6 int NOT NULL default 0,
  option7 int NOT NULL default 0,
  option8 int NOT NULL default 0,
  option9 int NOT NULL default 0,
  option10 int NOT NULL default 0,
  
  status int unsigned NOT NULL default '0'  COMMENT '评价的状态 0-无效 1-正常',
  
  reserve1 int NOT NULL default 0,
  reserve2 int NOT NULL default 0,
  reserve3 varchar(32) NOT NULL default '',
  reserve4 varchar(32) NOT NULL default '',  
  
  create_time datetime NOT NULL COMMENT '评价的创建时间',
  last_update_time datetime NOT NULL  COMMENT '评论点的更新时间',
  
  PRIMARY KEY  (group_type,item_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
