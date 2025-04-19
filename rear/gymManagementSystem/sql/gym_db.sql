/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80026
 Source Host           : localhost:3306
 Source Schema         : gym_db

 Target Server Type    : MySQL
 Target Server Version : 80026
 File Encoding         : 65001

 Date: 19/04/2025 20:39:40
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for announcements
-- ----------------------------
DROP TABLE IF EXISTS `announcements`;
CREATE TABLE `announcements`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `active` bit(1) NULL DEFAULT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `expire_time` datetime(6) NULL DEFAULT NULL,
  `publish_time` datetime(6) NOT NULL,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 231 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of announcements
-- ----------------------------

-- ----------------------------
-- Table structure for competition
-- ----------------------------
DROP TABLE IF EXISTS `competition`;
CREATE TABLE `competition`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '赛事名称',
  `type` tinyint(0) NOT NULL DEFAULT 0 COMMENT '赛事类型(0为乒乓球赛，1为篮球赛)',
  `category` tinyint(0) NOT NULL DEFAULT 0 COMMENT '赛事类别(0为淘汰赛，1为循环赛)',
  `hoster` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '举办方',
  `start_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '赛事开始时间',
  `end_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '赛事结束时间',
  `sign_up_deadline` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '报名截止时间',
  `sign_up_num` int(0) NOT NULL DEFAULT 0 COMMENT '已报人数',
  `max_sign_up_num` int(0) NOT NULL DEFAULT 0 COMMENT '最大报名人数',
  `status` tinyint(0) NOT NULL DEFAULT 0 COMMENT '赛事状态(0为未开始，1为正在进行，2为已结束，3为报名已截止)',
  `is_team_competition` tinyint(0) NOT NULL DEFAULT 0 COMMENT '是否为团体赛事',
  `team_max_num` int(0) NOT NULL DEFAULT 0 COMMENT '每队人数上限',
  `team_min_num` int(0) NOT NULL DEFAULT 0 COMMENT '每队人数下限',
  `requirement` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '参赛要求',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '赛事描述',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `name`(`name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '赛事表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of competition
-- ----------------------------

-- ----------------------------
-- Table structure for competition_equipment_relation
-- ----------------------------
DROP TABLE IF EXISTS `competition_equipment_relation`;
CREATE TABLE `competition_equipment_relation`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `competition_id` bigint(0) NOT NULL COMMENT '赛事id',
  `equipment_id` bigint(0) NOT NULL COMMENT '器材id',
  `num` int(0) NOT NULL COMMENT '数量',
  `start_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '开始时间',
  `end_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '结束时间',
  `status` tinyint(0) NOT NULL DEFAULT 0 COMMENT '状态(0为预约中，1为预约成功，2为预约失败)',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '赛事与器材关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of competition_equipment_relation
-- ----------------------------

-- ----------------------------
-- Table structure for competition_item
-- ----------------------------
DROP TABLE IF EXISTS `competition_item`;
CREATE TABLE `competition_item`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '项目名',
  `type` tinyint(0) NOT NULL DEFAULT 0 COMMENT '赛事类型(0为乒乓球赛，1为篮球赛)',
  `category` tinyint(0) NOT NULL DEFAULT 0 COMMENT '赛事类别(0为淘汰赛，1为循环赛)',
  `is_team_competition` tinyint(0) NOT NULL DEFAULT 0 COMMENT '是否为团体赛事',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `name`(`name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '赛事项目表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of competition_item
-- ----------------------------
INSERT INTO `competition_item` VALUES (1, '乒乓球男子单打淘汰赛', 0, 0, 0, '2025-04-19 18:23:24', '2025-04-19 18:23:24');
INSERT INTO `competition_item` VALUES (2, '乒乓球男子单打循环赛', 0, 1, 0, '2025-04-19 18:23:24', '2025-04-19 18:23:24');
INSERT INTO `competition_item` VALUES (3, '乒乓球女子单打淘汰赛', 0, 0, 0, '2025-04-19 18:23:24', '2025-04-19 18:23:24');
INSERT INTO `competition_item` VALUES (4, '乒乓球女子单打循环赛', 0, 1, 0, '2025-04-19 18:23:24', '2025-04-19 18:23:24');
INSERT INTO `competition_item` VALUES (5, '乒乓球男子双打淘汰赛', 0, 0, 1, '2025-04-19 18:23:24', '2025-04-19 18:23:24');
INSERT INTO `competition_item` VALUES (6, '乒乓球男子双打循环赛', 0, 1, 1, '2025-04-19 18:23:24', '2025-04-19 18:23:24');
INSERT INTO `competition_item` VALUES (7, '乒乓球女子双打淘汰赛', 0, 0, 1, '2025-04-19 18:23:24', '2025-04-19 18:23:24');
INSERT INTO `competition_item` VALUES (8, '乒乓球女子双打循环赛', 0, 1, 1, '2025-04-19 18:23:24', '2025-04-19 18:23:24');
INSERT INTO `competition_item` VALUES (9, '乒乓球混合双打淘汰赛', 0, 0, 1, '2025-04-19 18:23:24', '2025-04-19 18:23:24');
INSERT INTO `competition_item` VALUES (10, '乒乓球混合双打循环赛', 0, 1, 1, '2025-04-19 18:23:24', '2025-04-19 18:23:24');
INSERT INTO `competition_item` VALUES (11, '篮球男子组淘汰赛', 1, 0, 1, '2025-04-19 18:23:24', '2025-04-19 18:23:24');
INSERT INTO `competition_item` VALUES (12, '篮球男子组循环赛', 1, 1, 1, '2025-04-19 18:23:24', '2025-04-19 18:23:24');
INSERT INTO `competition_item` VALUES (13, '篮球女子组淘汰赛', 1, 0, 1, '2025-04-19 18:23:24', '2025-04-19 18:23:24');
INSERT INTO `competition_item` VALUES (14, '篮球女子组循环赛', 1, 1, 1, '2025-04-19 18:23:24', '2025-04-19 18:23:24');
INSERT INTO `competition_item` VALUES (15, '篮球混合友谊赛', 1, 1, 1, '2025-04-19 18:23:24', '2025-04-19 18:23:24');
INSERT INTO `competition_item` VALUES (16, '篮球公开组淘汰赛', 1, 0, 1, '2025-04-19 18:23:24', '2025-04-19 18:23:24');
INSERT INTO `competition_item` VALUES (17, '篮球公开组循环赛', 1, 1, 1, '2025-04-19 18:23:24', '2025-04-19 18:23:24');

-- ----------------------------
-- Table structure for competition_item_relation
-- ----------------------------
DROP TABLE IF EXISTS `competition_item_relation`;
CREATE TABLE `competition_item_relation`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `competition_id` bigint(0) NOT NULL COMMENT '赛事id',
  `competition_item_id` bigint(0) NOT NULL COMMENT '赛事项目id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '赛事与项目关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of competition_item_relation
-- ----------------------------

-- ----------------------------
-- Table structure for competition_sign_up_team
-- ----------------------------
DROP TABLE IF EXISTS `competition_sign_up_team`;
CREATE TABLE `competition_sign_up_team`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `competition_id` bigint(0) NOT NULL COMMENT '赛事id',
  `team_id` bigint(0) NOT NULL COMMENT '团队id',
  `team_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '团队名字',
  `leader_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '领队姓名',
  `leader_phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '联系电话',
  `depart_id` int(0) NOT NULL COMMENT '部门id',
  `competition_item_id` bigint(0) NOT NULL COMMENT '赛事项目id',
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '备注',
  `status` tinyint(0) NOT NULL DEFAULT 0 COMMENT '状态(0为报名中，1为报名成功，2为报名失败)',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '赛事团体报名表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of competition_sign_up_team
-- ----------------------------

-- ----------------------------
-- Table structure for competition_sign_up_user
-- ----------------------------
DROP TABLE IF EXISTS `competition_sign_up_user`;
CREATE TABLE `competition_sign_up_user`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `competition_id` bigint(0) NOT NULL COMMENT '赛事id',
  `user_id` bigint(0) NOT NULL COMMENT '用户id',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '姓名',
  `user_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '学号或职工号',
  `sex` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '性别',
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '电话',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '邮箱',
  `depart_id` int(0) NOT NULL COMMENT '部门id',
  `competition_item_id` bigint(0) NOT NULL COMMENT '赛事项目id',
  `user_stuorfac` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '区分学生或教职工 0 学生 1教师',
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '备注',
  `status` tinyint(0) NOT NULL DEFAULT 0 COMMENT '状态(0为报名中，1为报名成功，2为报名失败)',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '赛事个人报名表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of competition_sign_up_user
-- ----------------------------

-- ----------------------------
-- Table structure for competition_stage
-- ----------------------------
DROP TABLE IF EXISTS `competition_stage`;
CREATE TABLE `competition_stage`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `competition_id` bigint(0) NOT NULL COMMENT '赛事id',
  `competition_item_id` bigint(0) NOT NULL COMMENT '赛事项目id',
  `venue_id` bigint(0) NOT NULL COMMENT '场地id',
  `stage_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '阶段名称',
  `start_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '开始时间',
  `end_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '结束时间',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `stage_name`(`stage_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '赛事阶段表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of competition_stage
-- ----------------------------

-- ----------------------------
-- Table structure for competition_stage_referee_relation
-- ----------------------------
DROP TABLE IF EXISTS `competition_stage_referee_relation`;
CREATE TABLE `competition_stage_referee_relation`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `competition_id` bigint(0) NOT NULL COMMENT '赛事id',
  `competition_item_id` bigint(0) NOT NULL COMMENT '赛事项目id',
  `stage_id` bigint(0) NOT NULL COMMENT '阶段id',
  `referee_id` bigint(0) NOT NULL COMMENT '裁判id',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '赛事阶段与裁判关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of competition_stage_referee_relation
-- ----------------------------

-- ----------------------------
-- Table structure for competition_venue_relation
-- ----------------------------
DROP TABLE IF EXISTS `competition_venue_relation`;
CREATE TABLE `competition_venue_relation`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `competition_id` bigint(0) NOT NULL COMMENT '赛事id',
  `venue_id` bigint(0) NOT NULL COMMENT '场地id',
  `responsible_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '负责人姓名',
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '联系电话',
  `start_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '开始时间',
  `end_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '结束时间',
  `status` tinyint(0) NOT NULL DEFAULT 0 COMMENT '状态(0为预约中，1为预约成功，2为预约失败)',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '赛事与场地关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of competition_venue_relation
-- ----------------------------

-- ----------------------------
-- Table structure for reservations
-- ----------------------------
DROP TABLE IF EXISTS `reservations`;
CREATE TABLE `reservations`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `card_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `end_time` datetime(6) NOT NULL,
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `reservation_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `start_time` datetime(6) NOT NULL,
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `venue_id` bigint(0) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FKfghu9w3k11q38ti8x2xiyp3gj`(`venue_id`) USING BTREE,
  CONSTRAINT `FKfghu9w3k11q38ti8x2xiyp3gj` FOREIGN KEY (`venue_id`) REFERENCES `venues` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 606 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of reservations
-- ----------------------------

-- ----------------------------
-- Table structure for sys_depart
-- ----------------------------
DROP TABLE IF EXISTS `sys_depart`;
CREATE TABLE `sys_depart`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `depart_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '部门名称',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '部门表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_depart
-- ----------------------------
INSERT INTO `sys_depart` VALUES (9, '软件工程系', '2025-04-18 21:37:16', '2025-04-18 21:37:15', '软件系老师所属部门');
INSERT INTO `sys_depart` VALUES (10, '软件系学生会', '2025-04-18 21:39:26', '2025-04-18 21:39:25', '所属于软件系相关同学以及部门教职工的部门');
INSERT INTO `sys_depart` VALUES (11, '软件系团委', '2025-04-18 21:40:00', '2025-04-18 21:40:00', '所属于软件系相关同学以及部分教职工的部门');
INSERT INTO `sys_depart` VALUES (12, '广东海洋大学体育馆管理组', '2025-04-18 21:41:47', '2025-04-18 21:41:47', '所属于部分教职工的部门');
INSERT INTO `sys_depart` VALUES (13, '广东海洋大学数学与计算机学院', '2025-04-19 08:00:00', '2025-04-19 09:10:35', '所属于该学院的学生与教职工');
INSERT INTO `sys_depart` VALUES (14, '广东海洋大学机械工程学院', '2025-04-19 09:10:30', '2025-04-19 09:10:30', '所属于该学院的学生与教职工');

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `parent_id` int(0) NULL DEFAULT NULL COMMENT '父级菜单id，0表示无父级菜单',
  `parent_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '父级菜单名称',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '菜单名称',
  `code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '权限字段',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '路由名称',
  `path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '路由地址',
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '组件地址',
  `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '菜单类型 0目录 1菜单 2按钮',
  `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图标',
  `order_num` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '序号',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FK_menu_parent_id`(`parent_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '菜单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES (4, 0, '顶级菜单', '系统管理', 'sys:manage', 'system', '/system', '', '0', 'Setting', '2', '2025-04-19 19:17:26', '2025-04-19 19:17:26');
INSERT INTO `sys_menu` VALUES (5, 4, '系统管理', '用户管理', 'sys:user:index', 'userList', '/userList', '../views/system/User/UserList.vue', '1', 'UserFilled', '5', '2025-04-19 19:21:24', '2025-04-19 19:31:32');
INSERT INTO `sys_menu` VALUES (6, 4, '顶级菜单', '角色管理', 'sys:role:index', 'roleList', '/roleList', '../views/system/Role/RoleList.vue', '1', 'Wallet', '6', '2025-04-19 19:26:43', '2025-04-19 19:31:32');
INSERT INTO `sys_menu` VALUES (7, 4, '系统管理', '菜单管理', 'sys:menu:index', 'menuList', '/menuList', '../views/system/Menu/MenuList.vue', '1', 'Menu', '7', '2025-04-19 19:29:39', '2025-04-19 19:31:32');

-- ----------------------------
-- Table structure for sys_menu_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu_role`;
CREATE TABLE `sys_menu_role`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `role_id` int(0) NOT NULL COMMENT '角色id',
  `menu_id` int(0) NOT NULL COMMENT '菜单id',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FK_menu_role_role_id`(`role_id`) USING BTREE,
  INDEX `FK_menu_role_menu_id`(`menu_id`) USING BTREE,
  CONSTRAINT `FK_menu_role_menu_id` FOREIGN KEY (`menu_id`) REFERENCES `sys_menu` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_menu_role_role_id` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色菜单关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_menu_role
-- ----------------------------

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `role_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色名称',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '备注',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '类型',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 19 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (5, '普通学生', '无', '2025-04-17 19:16:41', '2025-04-17 19:16:41', NULL);
INSERT INTO `sys_role` VALUES (6, '普通教师', '无', '2025-04-17 19:16:47', '2025-04-17 19:16:47', NULL);
INSERT INTO `sys_role` VALUES (7, '普通职工', '无', '2025-04-17 19:17:04', '2025-04-17 19:17:04', NULL);
INSERT INTO `sys_role` VALUES (8, '器材管理员', '无', '2025-04-17 19:17:14', '2025-04-17 19:17:14', NULL);
INSERT INTO `sys_role` VALUES (9, '场地管理员', '无', '2025-04-17 19:17:21', '2025-04-17 19:17:21', NULL);
INSERT INTO `sys_role` VALUES (10, '赛事管理员', '无', '2025-04-17 19:17:28', '2025-04-17 19:17:28', NULL);
INSERT INTO `sys_role` VALUES (11, '用户管理员', '无', '2025-04-17 19:32:50', '2025-04-17 19:32:50', NULL);

-- ----------------------------
-- Table structure for sys_section
-- ----------------------------
DROP TABLE IF EXISTS `sys_section`;
CREATE TABLE `sys_section`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `section_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '班级名称',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '备注',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '班级表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_section
-- ----------------------------
INSERT INTO `sys_section` VALUES (1, '软件1221', '无', '2025-04-18 21:42:18', '2025-04-18 21:42:18');
INSERT INTO `sys_section` VALUES (2, '软件1222', '无', '2025-04-18 21:42:27', '2025-04-18 21:42:27');
INSERT INTO `sys_section` VALUES (3, '软件1223', '无', '2025-04-18 21:42:39', '2025-04-18 21:42:39');
INSERT INTO `sys_section` VALUES (4, '软件1224', '无', '2025-04-18 21:42:52', '2025-04-18 21:42:52');

-- ----------------------------
-- Table structure for sys_use_depart
-- ----------------------------
DROP TABLE IF EXISTS `sys_use_depart`;
CREATE TABLE `sys_use_depart`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` int(0) NOT NULL COMMENT '用户id',
  `depart_id` int(0) NOT NULL COMMENT '角色id',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FK_use_depart_user_id`(`user_id`) USING BTREE,
  INDEX `FK_use_depart_role_id`(`depart_id`) USING BTREE,
  CONSTRAINT `FK_use_depart_role_id` FOREIGN KEY (`depart_id`) REFERENCES `sys_depart` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_use_depart_user_id` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户部门关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_use_depart
-- ----------------------------
INSERT INTO `sys_use_depart` VALUES (1, 1, 13);
INSERT INTO `sys_use_depart` VALUES (4, 3, 10);

-- ----------------------------
-- Table structure for sys_use_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_use_role`;
CREATE TABLE `sys_use_role`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` int(0) NOT NULL COMMENT '用户id',
  `role_id` int(0) NOT NULL COMMENT '角色id',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FK_use_role_user_id`(`user_id`) USING BTREE,
  INDEX `FK_use_role_role_id`(`role_id`) USING BTREE,
  CONSTRAINT `FK_use_role_role_id` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_use_role_user_id` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户角色关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_use_role
-- ----------------------------
INSERT INTO `sys_use_role` VALUES (1, 1, 5);
INSERT INTO `sys_use_role` VALUES (4, 3, 6);

-- ----------------------------
-- Table structure for sys_use_section
-- ----------------------------
DROP TABLE IF EXISTS `sys_use_section`;
CREATE TABLE `sys_use_section`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` int(0) NOT NULL COMMENT '用户id',
  `section_id` int(0) NOT NULL COMMENT '角色id',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FK_use_section_user_id`(`user_id`) USING BTREE,
  INDEX `FK_use_section_role_id`(`section_id`) USING BTREE,
  CONSTRAINT `FK_use_section_role_id` FOREIGN KEY (`section_id`) REFERENCES `sys_section` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_use_section_user_id` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户班级关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_use_section
-- ----------------------------
INSERT INTO `sys_use_section` VALUES (1, 1, 1);
INSERT INTO `sys_use_section` VALUES (2, 3, 2);

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学号或职工号',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户密码',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '姓名',
  `sex` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '性别',
  `age` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '年龄',
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '电话',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '邮箱',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `user_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户类型',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '学生用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, '202211701001', '123456', '张三', '男', '22', '12345678912', '12345678912@qq.com', '2025-04-19 09:15:20', '2025-04-19 09:15:20', '学生');
INSERT INTO `sys_user` VALUES (3, '2552552', '123456', '李四', '男', '44', '25252525', '252542424@qq.com', '2025-04-19 08:00:00', '2025-04-19 12:08:32', '学生');

-- ----------------------------
-- Table structure for team
-- ----------------------------
DROP TABLE IF EXISTS `team`;
CREATE TABLE `team`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `team_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '团队名字',
  `leader_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '领队姓名',
  `leader_phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '联系电话',
  `depart_id` int(0) NOT NULL COMMENT '部门id',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `team_name`(`team_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '团队表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of team
-- ----------------------------

-- ----------------------------
-- Table structure for team_member_relation
-- ----------------------------
DROP TABLE IF EXISTS `team_member_relation`;
CREATE TABLE `team_member_relation`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `team_id` bigint(0) NOT NULL COMMENT '团队id',
  `user_id` bigint(0) NOT NULL COMMENT '用户id',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '团队与队员关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of team_member_relation
-- ----------------------------

-- ----------------------------
-- Table structure for usages
-- ----------------------------
DROP TABLE IF EXISTS `usages`;
CREATE TABLE `usages`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `card_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `cost` decimal(19, 2) NULL DEFAULT NULL,
  `end_time` datetime(6) NULL DEFAULT NULL,
  `paid` bit(1) NULL DEFAULT NULL,
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `start_time` datetime(6) NOT NULL,
  `reservation_id` bigint(0) NULL DEFAULT NULL,
  `venue_id` bigint(0) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FKld3pe2uigikuc9dpknq5f2yp4`(`reservation_id`) USING BTREE,
  INDEX `FKo3fufdtu3cfgnv9gxjdp2wm3i`(`venue_id`) USING BTREE,
  CONSTRAINT `FKld3pe2uigikuc9dpknq5f2yp4` FOREIGN KEY (`reservation_id`) REFERENCES `reservations` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKo3fufdtu3cfgnv9gxjdp2wm3i` FOREIGN KEY (`venue_id`) REFERENCES `venues` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 313 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of usages
-- ----------------------------

-- ----------------------------
-- Table structure for venue_entity
-- ----------------------------
DROP TABLE IF EXISTS `venue_entity`;
CREATE TABLE `venue_entity`  (
  `id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of venue_entity
-- ----------------------------

-- ----------------------------
-- Table structure for venues
-- ----------------------------
DROP TABLE IF EXISTS `venues`;
CREATE TABLE `venues`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `capacity` int(0) NULL DEFAULT NULL,
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `is_available` bit(1) NOT NULL,
  `location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `price_per_hour` decimal(19, 2) NOT NULL,
  `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 592 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of venues
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
