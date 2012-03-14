CREATE TABLE comment_point (
  id int NOT NULL AUTO_INCREMENT,  
  name varchar(255) NOT NULL default '' COMMENT '评论点的标志',
  url varchar(255) NOT NULL default '' COMMENT '评论点的相关链点',
  title varchar(255) NOT NULL default '' COMMENT '评论点的标题',
  comment_amount int unsigned NOT NULL default '0' COMMENT '评论点的评论数',
  status int unsigned NOT NULL default '0'  COMMENT '评论点的状态 0-无效 1-正常',
  
  reserve1 int NOT NULL default 0,
  reserve2 int NOT NULL default 0,
  reserve3 varchar(32) NOT NULL default '',
  reserve4 varchar(32) NOT NULL default '',  
  
  last_comment_time datetime NOT NULL COMMENT '最后评论时间',
  create_time datetime NOT NULL COMMENT '评论点的创建时间',
  last_update_time datetime NOT NULL  COMMENT '评论点的更新时间',
  
  PRIMARY KEY  (id),
  UNIQUE INDEX comment_point_u1 (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE comment_info (
  id int NOT NULL AUTO_INCREMENT,  
  point_id int unsigned NOT NULL COMMENT '关联的评论点',
  reply_id int unsigned NOT NULL COMMENT '回复的评论点',
  user_id int unsigned NOT NULL COMMENT '评论的用户编号',
  user_name varchar(255) NOT NULL default '' COMMENT '显示的用户名',
  content text COMMENT '评论内容',
  ip varchar(255) NOT NULL default '' COMMENT '评论时候的IP',
  status int unsigned NOT NULL default '0' COMMENT '评论状态 0-普通 1-头条',
  support int(10) unsigned NOT NULL default '0' COMMENT '支持数',
  oppose int(10) unsigned NOT NULL default '0' COMMENT '反对数',
  need_auth int(10) unsigned NOT NULL default '0' COMMENT '是否需要审核 0-不需要 1-需要',
  hide_flag int(10) unsigned NOT NULL default '0' COMMENT '隐藏标志 0-不隐藏 1-隐藏',
  
  reserve1 int NOT NULL default 0,
  reserve2 int NOT NULL default 0,
  reserve3 varchar(32) NOT NULL default '',
  reserve4 varchar(32) NOT NULL default '',  
  
  create_time datetime NOT NULL default '0000-00-00 00:00:00',
  
  PRIMARY KEY  (id),
  INDEX comment_info_i1 (point_id,hide_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

