CREATE TABLE so_wordstock (
  id int NOT NULL AUTO_INCREMENT,  
  group_type varchar(16) NOT NULL default '' COMMENT '��������',
  title varchar(16) NOT NULL default '',
  words text NOT NULL default '' COMMENT '����',
  type varchar(16) NOT NULL default '' COMMENT '����',
  PRIMARY KEY (id),
  INDEX so_wordstock_i1 (group_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;