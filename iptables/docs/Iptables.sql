CREATE TABLE so_iptables (
  id int NOT NULL AUTO_INCREMENT,  
  group_type varchar(16) NOT NULL default '' COMMENT '分组类型',
  inet varchar(1024) COMMENT 'IP段数据',
  type varchar(16) COMMENT '类型',
  PRIMARY KEY (id),
  INDEX so_iptables_i1 (group_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;