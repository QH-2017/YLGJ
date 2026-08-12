-- =====================================================================
-- 医疗管家健康管理系统 数据库初始化脚本
-- 数据库：MySQL 8.0+
-- 说明：执行前请确保数据库用户有建库权限；密码默认 admin123
-- =====================================================================

CREATE DATABASE IF NOT EXISTS `ylgj` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `ylgj`;

-- ---------------------------------------------------------------------
-- 1. 用户表
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user` (
  `id`        INT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `birthday`  DATE DEFAULT NULL COMMENT '生日',
  `gender`    VARCHAR(20) DEFAULT NULL COMMENT '性别',
  `username`  VARCHAR(50) DEFAULT NULL COMMENT '用户名',
  `password`  VARCHAR(100) DEFAULT NULL COMMENT '密码（BCrypt 加密）',
  `remark`    VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `station`   VARCHAR(20) DEFAULT NULL COMMENT '状态：1-启用 0-停用',
  `telephone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ---------------------------------------------------------------------
-- 2. 角色表
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `t_role`;
CREATE TABLE `t_role` (
  `id`          INT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name`        VARCHAR(50) DEFAULT NULL COMMENT '角色名称',
  `keyword`     VARCHAR(50) DEFAULT NULL COMMENT '角色关键字（唯一标识）',
  `description` VARCHAR(500) DEFAULT NULL COMMENT '角色描述',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- ---------------------------------------------------------------------
-- 3. 用户-角色关联表
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `t_user_role`;
CREATE TABLE `t_user_role` (
  `user_id` INT NOT NULL COMMENT '用户ID',
  `role_id` INT NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`, `role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- ---------------------------------------------------------------------
-- 4. 菜单表（RBAC 动态菜单）
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `t_menu`;
CREATE TABLE `t_menu` (
  `id`            INT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name`          VARCHAR(50) DEFAULT NULL COMMENT '菜单名称',
  `linkUrl`       VARCHAR(200) DEFAULT NULL COMMENT '链接地址',
  `path`          VARCHAR(200) DEFAULT NULL COMMENT '前端路由路径',
  `priority`      INT DEFAULT NULL COMMENT '排序优先级',
  `icon`          VARCHAR(100) DEFAULT NULL COMMENT '图标',
  `description`   VARCHAR(500) DEFAULT NULL COMMENT '描述',
  `parentMenuId`  INT DEFAULT NULL COMMENT '父菜单ID',
  `level`         INT DEFAULT NULL COMMENT '菜单层级',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='菜单表';

-- ---------------------------------------------------------------------
-- 5. 角色-菜单关联表
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `t_role_menu`;
CREATE TABLE `t_role_menu` (
  `role_id` INT NOT NULL COMMENT '角色ID',
  `menu_id` INT NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`role_id`, `menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联表';

-- ---------------------------------------------------------------------
-- 6. 权限表
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `t_permission`;
CREATE TABLE `t_permission` (
  `id`          INT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name`        VARCHAR(50) DEFAULT NULL COMMENT '权限名称',
  `keyword`     VARCHAR(50) DEFAULT NULL COMMENT '权限关键字',
  `description` VARCHAR(500) DEFAULT NULL COMMENT '权限描述',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- ---------------------------------------------------------------------
-- 7. 角色-权限关联表
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `t_role_permission`;
CREATE TABLE `t_role_permission` (
  `role_id`       INT NOT NULL COMMENT '角色ID',
  `permission_id` INT NOT NULL COMMENT '权限ID',
  PRIMARY KEY (`role_id`, `permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- ---------------------------------------------------------------------
-- 8. 检查项表
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `t_checkitem`;
CREATE TABLE `t_checkitem` (
  `id`        INT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `code`      VARCHAR(32) DEFAULT NULL COMMENT '项目编码',
  `name`      VARCHAR(30) DEFAULT NULL COMMENT '项目名称',
  `sex`       CHAR(1) DEFAULT NULL COMMENT '适用性别：1-男 2-女 3-通用',
  `age`       VARCHAR(30) DEFAULT NULL COMMENT '适用年龄范围',
  `price`     DECIMAL(10,2) DEFAULT NULL COMMENT '价格',
  `type`      INT DEFAULT NULL COMMENT '项目类型：1-检查 2-检验',
  `attention` VARCHAR(500) DEFAULT NULL COMMENT '注意事项',
  `remark`    VARCHAR(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='检查项表';

-- ---------------------------------------------------------------------
-- 9. 检查组表
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `t_checkgroup`;
CREATE TABLE `t_checkgroup` (
  `id`        INT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `code`      VARCHAR(32) DEFAULT NULL COMMENT '检查组编码',
  `name`      VARCHAR(30) DEFAULT NULL COMMENT '检查组名称',
  `help_code` VARCHAR(20) DEFAULT NULL COMMENT '助记码',
  `sex`       CHAR(1) DEFAULT NULL COMMENT '适用性别：1-男 2-女 3-通用',
  `remark`    VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `attention` VARCHAR(500) DEFAULT NULL COMMENT '注意事项',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='检查组表';

-- ---------------------------------------------------------------------
-- 10. 检查组-检查项关联表
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `t_checkgroup_checkitem`;
CREATE TABLE `t_checkgroup_checkitem` (
  `checkgroup_id` INT NOT NULL COMMENT '检查组ID',
  `checkitem_id`  INT NOT NULL COMMENT '检查项ID',
  PRIMARY KEY (`checkgroup_id`, `checkitem_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='检查组检查项关联表';

-- ---------------------------------------------------------------------
-- 11. 体检套餐表
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `t_setmeal`;
CREATE TABLE `t_setmeal` (
  `id`        INT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name`      VARCHAR(100) DEFAULT NULL COMMENT '套餐名称',
  `code`      VARCHAR(32) DEFAULT NULL COMMENT '套餐编码',
  `help_code` VARCHAR(20) DEFAULT NULL COMMENT '助记码',
  `sex`       CHAR(1) DEFAULT NULL COMMENT '适用性别：1-男 2-女 3-通用',
  `age`       VARCHAR(30) DEFAULT NULL COMMENT '适用年龄范围',
  `price`     DECIMAL(10,2) DEFAULT NULL COMMENT '套餐价格',
  `remark`    VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `attention` VARCHAR(500) DEFAULT NULL COMMENT '注意事项',
  `img`       VARCHAR(200) DEFAULT NULL COMMENT '套餐图片地址',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='体检套餐表';

-- ---------------------------------------------------------------------
-- 12. 套餐-检查组关联表
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `t_setmeal_checkgroup`;
CREATE TABLE `t_setmeal_checkgroup` (
  `setmeal_id`    INT NOT NULL COMMENT '套餐ID',
  `checkgroup_id` INT NOT NULL COMMENT '检查组ID',
  PRIMARY KEY (`setmeal_id`, `checkgroup_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='套餐检查组关联表';

-- ---------------------------------------------------------------------
-- 13. 会员表
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `t_member`;
CREATE TABLE `t_member` (
  `id`           INT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `fileNumber`   VARCHAR(32) DEFAULT NULL COMMENT '档案号',
  `name`         VARCHAR(30) DEFAULT NULL COMMENT '姓名',
  `sex`          VARCHAR(10) DEFAULT NULL COMMENT '性别',
  `idCard`       VARCHAR(18) DEFAULT NULL COMMENT '身份证号',
  `phoneNumber`  VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  `regTime`      DATE DEFAULT NULL COMMENT '注册时间',
  `password`     VARCHAR(100) DEFAULT NULL COMMENT '密码',
  `email`        VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `birthday`     DATE DEFAULT NULL COMMENT '生日',
  `remark`       VARCHAR(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_phone` (`phoneNumber`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='会员表';

-- ---------------------------------------------------------------------
-- 14. 预约订单表
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `t_order`;
CREATE TABLE `t_order` (
  `id`           INT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `member_id`    INT DEFAULT NULL COMMENT '会员ID',
  `orderDate`    DATE DEFAULT NULL COMMENT '预约日期',
  `orderType`    VARCHAR(20) DEFAULT NULL COMMENT '预约类型：电话预约/微信预约',
  `orderStatus`  VARCHAR(20) DEFAULT NULL COMMENT '预约状态：未到诊/已到诊/已过期/已取消',
  `setmeal_id`   INT DEFAULT NULL COMMENT '套餐ID',
  PRIMARY KEY (`id`),
  KEY `idx_member` (`member_id`),
  KEY `idx_orderdate` (`orderDate`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='预约订单表';

-- ---------------------------------------------------------------------
-- 15. 预约条件设置表（每日可预约人数）
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `t_ordersetting`;
CREATE TABLE `t_ordersetting` (
  `id`           INT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `orderDate`    DATE DEFAULT NULL COMMENT '预约日期',
  `number`       INT DEFAULT NULL COMMENT '可预约人数',
  `reservations` INT DEFAULT NULL COMMENT '已预约人数',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_orderdate` (`orderDate`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='预约条件设置表';

-- =====================================================================
-- 初始数据
-- =====================================================================

-- 管理员账号：admin / admin123（密码为 BCrypt 加密）
INSERT INTO `t_user` (`id`, `username`, `password`, `gender`, `station`, `telephone`, `remark`)
VALUES (1, 'admin', '$2b$10$NlXoHRQW8dsa2/ySnCRaZuAHYcyStgeS5eDNYJmfOPpUNZ50tPKPK', '男', '1', '13800000000', '系统超级管理员');

-- 角色
INSERT INTO `t_role` (`id`, `name`, `keyword`, `description`)
VALUES (1, '系统管理员', 'ADMIN', '拥有系统全部权限');

-- 用户-角色
INSERT INTO `t_user_role` (`user_id`, `role_id`) VALUES (1, 1);

-- 菜单（管理端）
INSERT INTO `t_menu` (`id`, `name`, `linkUrl`, `path`, `priority`, `icon`, `description`, `parentMenuId`, `level`) VALUES
(1, '数据概览', '', '/admin/dashboard', 1, 'Odometer', '仪表盘', NULL, 1),
(2, '套餐信息管理', '', '/admin/setmeal', 2, 'DishDot', '体检套餐管理', NULL, 1),
(3, '检查组管理', '', '/admin/checkgroup', 3, 'Collection', '检查组管理', NULL, 1),
(4, '检查项管理', '', '/admin/checkitem', 4, 'List', '检查项管理', NULL, 1),
(5, '会员管理', '', '/admin/member', 5, 'User', '会员管理', NULL, 1),
(6, '预约管理', '', '/admin/order', 6, 'Calendar', '预约订单管理', NULL, 1),
(7, '预约条件设置', '', '/admin/ordersetting', 7, 'Setting', '每日可预约人数', NULL, 1),
(8, '统计分析', '', '/admin/report', 8, 'TrendCharts', '报表统计', NULL, 1),
(9, '系统用户管理', '', '/admin/users', 9, 'UserFilled', '系统用户管理', NULL, 1),
(10, '数据导出中心', '', '/admin/export', 10, 'Download', '数据导出', NULL, 1),
(11, '健康资讯管理', '', '/admin/news', 11, 'Bell', '健康资讯', NULL, 1);

-- 角色-菜单（管理员拥有全部）
INSERT INTO `t_role_menu` (`role_id`, `menu_id`) VALUES
(1,1),(1,2),(1,3),(1,4),(1,5),(1,6),(1,7),(1,8),(1,9),(1,10),(1,11);

-- 权限
INSERT INTO `t_permission` (`id`, `name`, `keyword`, `description`) VALUES
(1, '用户管理', 'user:manage', '用户增删改查'),
(2, '套餐管理', 'setmeal:manage', '套餐增删改查'),
(3, '订单管理', 'order:manage', '订单管理');

-- 角色-权限
INSERT INTO `t_role_permission` (`role_id`, `permission_id`) VALUES (1,1),(1,2),(1,3);

-- 示例：检查项
INSERT INTO `t_checkitem` (`id`, `code`, `name`, `sex`, `age`, `price`, `type`, `attention`, `remark`) VALUES
(1, '0001', '一般检查', '3', '全部', 10.00, 1, '测身高、体重、血压', NULL),
(2, '0002', '血常规', '3', '全部', 20.00, 2, '需空腹抽血', NULL),
(3, '0003', '尿常规', '3', '全部', 15.00, 2, NULL, NULL),
(4, '0004', '肝功能', '3', '全部', 30.00, 2, '需空腹抽血', NULL),
(5, '0005', '心电图', '3', '全部', 25.00, 1, NULL, NULL),
(6, '0006', '胸部X光', '3', '全部', 40.00, 1, NULL, NULL),
(7, '0007', '腹部彩超', '3', '全部', 80.00, 1, '需空腹', NULL),
(8, '0008', '妇科检查', '2', '女性', 50.00, 1, NULL, NULL);

-- 示例：检查组
INSERT INTO `t_checkgroup` (`id`, `code`, `name`, `help_code`, `sex`, `remark`, `attention`) VALUES
(1, 'CG001', '基础检查', 'JCJC', '3', '基础体检项目', NULL),
(2, 'CG002', '实验室检查', 'SYSJC', '3', '抽血化验项目', '需空腹'),
(3, 'CG003', '影像学检查', 'YXXJC', '3', '影像类项目', NULL);

-- 检查组-检查项
INSERT INTO `t_checkgroup_checkitem` (`checkgroup_id`, `checkitem_id`) VALUES
(1,1),(1,5),(2,2),(2,3),(2,4),(3,6),(3,7);

-- 示例：体检套餐
INSERT INTO `t_setmeal` (`id`, `name`, `code`, `help_code`, `sex`, `age`, `price`, `remark`, `attention`, `img`) VALUES
(1, '入职体检套餐', 'SM001', 'RZTJ', '3', '全部', 300.00, '适合入职体检', '体检当天需空腹', NULL),
(2, '阳光爸妈升级肿瘤筛查', 'SM002', 'YGQZ', '3', '55-100', 1400.00, '中老年肿瘤筛查', '需空腹', NULL),
(3, '粉红珍爱女性套餐', 'SM003', 'FHZA', '2', '20-55', 1200.00, '女性专属体检', '避开生理期', NULL);

-- 套餐-检查组
INSERT INTO `t_setmeal_checkgroup` (`setmeal_id`, `checkgroup_id`) VALUES
(1,1),(1,2),(2,1),(2,2),(2,3),(3,1),(3,2);

-- 示例：预约条件设置（未来 7 天每天可约 50 人）
INSERT INTO `t_ordersetting` (`orderDate`, `number`, `reservations`)
SELECT DATE_ADD(CURDATE(), INTERVAL n DAY), 50, 0
FROM (SELECT 1 n UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7) t;
