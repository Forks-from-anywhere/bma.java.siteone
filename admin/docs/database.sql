/*Table structure for table `admin_app` */

DROP TABLE IF EXISTS `admin_app`;

CREATE TABLE `admin_app` (
  `app_name` varchar(32) NOT NULL COMMENT '后台应用名称',
  `app_description` varchar(256) DEFAULT NULL COMMENT '信息描述',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `status` int(2) NOT NULL DEFAULT '1' COMMENT '状态：0无效 1有效',
  PRIMARY KEY (`app_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `admin_app_op` */

DROP TABLE IF EXISTS `admin_app_op`;

CREATE TABLE `admin_app_op` (
  `app_name` varchar(32) NOT NULL COMMENT '后台应用名称',
  `op_name` varchar(32) NOT NULL COMMENT '操作名称',
  `op_description` varchar(256) DEFAULT NULL COMMENT '信息描述',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `status` int(2) NOT NULL DEFAULT '1' COMMENT '状态：0无效 1有效',
  PRIMARY KEY (`app_name`,`op_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `admin_app_role` */

DROP TABLE IF EXISTS `admin_app_role`;

CREATE TABLE `admin_app_role` (
  `app_name` varchar(32) NOT NULL COMMENT '后台应用名称',
  `role_name` varchar(32) NOT NULL COMMENT '角色名称',
  `role_description` varchar(256) DEFAULT NULL COMMENT '信息描述',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `status` int(2) NOT NULL DEFAULT '1' COMMENT '状态：0无效 1有效',
  PRIMARY KEY (`app_name`,`role_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `admin_app_role_op` */

DROP TABLE IF EXISTS `admin_app_role_op`;

CREATE TABLE `admin_app_role_op` (
  `app_name` varchar(32) NOT NULL COMMENT '后台应用名称',
  `role_name` varchar(32) NOT NULL COMMENT '角色名称',
  `op_name` varchar(32) NOT NULL COMMENT '操作名称',
  PRIMARY KEY (`app_name`,`role_name`,`op_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `admin_auth_user_role` */

DROP TABLE IF EXISTS `admin_auth_user_role`;

CREATE TABLE `admin_auth_user_role` (
  `user_name` varchar(32) NOT NULL COMMENT '用户名',
  `app_name` varchar(32) NOT NULL COMMENT '后台应用名称',
  `role_name` varchar(32) NOT NULL COMMENT '角色名称',
  PRIMARY KEY (`user_name`,`app_name`,`role_name`),
  KEY `app_name_index` (`app_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `admin_op_log` */

DROP TABLE IF EXISTS `admin_op_log`;

CREATE TABLE `admin_op_log` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `user_name` varchar(32) NOT NULL COMMENT '用户名',
  `app_name` varchar(32) DEFAULT NULL COMMENT '后台应用名称',
  `role_name` varchar(32) DEFAULT NULL COMMENT '角色名称',
  `op_name` varchar(32) DEFAULT NULL COMMENT '操作名称',
  `time` datetime NOT NULL COMMENT '操作时间',
  `description` varchar(256) DEFAULT NULL COMMENT '信息描述',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=189 DEFAULT CHARSET=utf8;

/*Table structure for table `admin_user` */

DROP TABLE IF EXISTS `admin_user`;

CREATE TABLE `admin_user` (
  `user_name` varchar(32) NOT NULL COMMENT '用户名',
  `password` varchar(32) NOT NULL COMMENT '密码',
  `user_description` varchar(256) DEFAULT NULL COMMENT '信息描述',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `status` int(2) NOT NULL DEFAULT '1' COMMENT '状态：0无效 1有效',
  PRIMARY KEY (`user_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `admin_user` */

insert  into `admin_user`(`user_name`,`password`,`user_description`,`create_time`,`status`) values ('admin','96e79218965eb72c92a549dd5a330112','admin desc','2012-04-17 11:38:53',1);

