CREATE TABLE so_evaluate (
  group_type varchar(16) NOT NULL default '' COMMENT '���۷���',
  item_id varchar(32) NOT NULL default ''  COMMENT '��������',  
  url varchar(255) NOT NULL default '' COMMENT '���۵��������',
  title varchar(255) NOT NULL default '' COMMENT '���۵ı���',
  
  eva_amount int unsigned NOT NULL default '0' COMMENT '���۵��˴�',
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
  
  status int unsigned NOT NULL default '0'  COMMENT '���۵�״̬ 0-��Ч 1-����',
  
  reserve1 int NOT NULL default 0,
  reserve2 int NOT NULL default 0,
  reserve3 varchar(32) NOT NULL default '',
  reserve4 varchar(32) NOT NULL default '',  
  
  create_time datetime NOT NULL COMMENT '���۵Ĵ���ʱ��',
  last_update_time datetime NOT NULL  COMMENT '���۵�ĸ���ʱ��',
  
  PRIMARY KEY  (group_type,item_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
