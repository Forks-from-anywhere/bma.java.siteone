CREATE TABLE `so_message` (
  `id` INT(10) NOT NULL AUTO_INCREMENT COMMENT '消息id，自增长',
  `app` VARCHAR(32) DEFAULT 'live' COMMENT '应用标识，默认为live',
  `type` INT(4) DEFAULT '1' COMMENT '消息类型，1系统消息（默认）',
  `receiver` VARCHAR(32) DEFAULT NULL COMMENT '接收者',
  `sender` VARCHAR(32) DEFAULT NULL COMMENT '发送者',
  `title` VARCHAR(128) DEFAULT NULL COMMENT '消息标题',
  `content` VARCHAR(512) DEFAULT NULL COMMENT '消息主体内容',
  `send_time` DATETIME DEFAULT NULL COMMENT '发送时间',
  `is_read` INT(2) DEFAULT '0' COMMENT '是否已读，0未读（默认），1已读',
  PRIMARY KEY (`id`),
  KEY `index_receiver` (`receiver`,`app`)
) ENGINE=INNODB DEFAULT CHARSET=utf8