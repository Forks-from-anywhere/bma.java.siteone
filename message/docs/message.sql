CREATE TABLE `so_message` (
  `id` INT(10) NOT NULL AUTO_INCREMENT COMMENT '��Ϣid��������',
  `app` VARCHAR(32) DEFAULT 'live' COMMENT 'Ӧ�ñ�ʶ��Ĭ��Ϊlive',
  `type` INT(4) DEFAULT '1' COMMENT '��Ϣ���ͣ�1ϵͳ��Ϣ��Ĭ�ϣ�',
  `receiver` VARCHAR(32) DEFAULT NULL COMMENT '������',
  `sender` VARCHAR(32) DEFAULT NULL COMMENT '������',
  `title` VARCHAR(128) DEFAULT NULL COMMENT '��Ϣ����',
  `content` VARCHAR(512) DEFAULT NULL COMMENT '��Ϣ��������',
  `send_time` DATETIME DEFAULT NULL COMMENT '����ʱ��',
  `is_read` INT(2) DEFAULT '0' COMMENT '�Ƿ��Ѷ���0δ����Ĭ�ϣ���1�Ѷ�',
  PRIMARY KEY (`id`),
  KEY `index_receiver` (`receiver`,`app`)
) ENGINE=INNODB DEFAULT CHARSET=utf8