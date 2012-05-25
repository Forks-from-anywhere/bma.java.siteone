CREATE TABLE so_wordstock (
  id int NOT NULL AUTO_INCREMENT,  
  group_type varchar(16) NOT NULL default '' COMMENT '分组类型',
  title varchar(16) NOT NULL default '',
  words text NOT NULL default '' COMMENT '词语',
  type varchar(16) NOT NULL default '' COMMENT '类型',
  PRIMARY KEY (id),
  INDEX so_wordstock_i1 (group_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;