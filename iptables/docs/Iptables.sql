CREATE TABLE so_iptables (
  id int NOT NULL AUTO_INCREMENT,  
  group_type varchar(16) NOT NULL default '' COMMENT '��������',
  inet varchar(1024) COMMENT 'IP������',
  type varchar(16) COMMENT '����',
  PRIMARY KEY (id),
  INDEX so_iptables_i1 (group_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;