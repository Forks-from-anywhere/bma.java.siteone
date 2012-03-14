CREATE TABLE comment_point (
  id int NOT NULL AUTO_INCREMENT,  
  name varchar(255) NOT NULL default '' COMMENT '���۵�ı�־',
  url varchar(255) NOT NULL default '' COMMENT '���۵���������',
  title varchar(255) NOT NULL default '' COMMENT '���۵�ı���',
  comment_amount int unsigned NOT NULL default '0' COMMENT '���۵��������',
  status int unsigned NOT NULL default '0'  COMMENT '���۵��״̬ 0-��Ч 1-����',
  
  reserve1 int NOT NULL default 0,
  reserve2 int NOT NULL default 0,
  reserve3 varchar(32) NOT NULL default '',
  reserve4 varchar(32) NOT NULL default '',  
  
  last_comment_time datetime NOT NULL COMMENT '�������ʱ��',
  create_time datetime NOT NULL COMMENT '���۵�Ĵ���ʱ��',
  last_update_time datetime NOT NULL  COMMENT '���۵�ĸ���ʱ��',
  
  PRIMARY KEY  (id),
  UNIQUE INDEX comment_point_u1 (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE comment_info (
  id int NOT NULL AUTO_INCREMENT,  
  point_id int unsigned NOT NULL COMMENT '���������۵�',
  reply_id int unsigned NOT NULL COMMENT '�ظ������۵�',
  user_id int unsigned NOT NULL COMMENT '���۵��û����',
  user_name varchar(255) NOT NULL default '' COMMENT '��ʾ���û���',
  content text COMMENT '��������',
  ip varchar(255) NOT NULL default '' COMMENT '����ʱ���IP',
  status int unsigned NOT NULL default '0' COMMENT '����״̬ 0-��ͨ 1-ͷ��',
  support int(10) unsigned NOT NULL default '0' COMMENT '֧����',
  oppose int(10) unsigned NOT NULL default '0' COMMENT '������',
  need_auth int(10) unsigned NOT NULL default '0' COMMENT '�Ƿ���Ҫ��� 0-����Ҫ 1-��Ҫ',
  hide_flag int(10) unsigned NOT NULL default '0' COMMENT '���ر�־ 0-������ 1-����',
  
  reserve1 int NOT NULL default 0,
  reserve2 int NOT NULL default 0,
  reserve3 varchar(32) NOT NULL default '',
  reserve4 varchar(32) NOT NULL default '',  
  
  create_time datetime NOT NULL default '0000-00-00 00:00:00',
  
  PRIMARY KEY  (id),
  INDEX comment_info_i1 (point_id,hide_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

