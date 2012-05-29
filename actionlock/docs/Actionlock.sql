CREATE TABLE so_actionlock (
  group_type varchar(16) NOT NULL default '' COMMENT '������',
  item_id varchar(64) NOT NULL default ''  COMMENT '������',  
  
  access_count int NOT NULL default '0' COMMENT '���ʴ���',
  clean_time datetime NOT NULL COMMENT '���ʱ��',
 
  create_time datetime NOT NULL COMMENT '����ʱ��',
  last_update_time datetime NOT NULL  COMMENT '����ʱ��',
  
  PRIMARY KEY  (group_type,item_id),
  INDEX actionlock_i1 (clean_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
