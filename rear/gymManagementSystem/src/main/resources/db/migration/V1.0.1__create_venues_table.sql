SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 先删除可能存在的引用venues表的外键约束
DROP TABLE IF EXISTS `reservations`;
DROP TABLE IF EXISTS `venue_schedules`;
DROP TABLE IF EXISTS `venue_maintenance`;

-- ----------------------------
-- Table structure for venues
-- ----------------------------
DROP TABLE IF EXISTS `venues`;
CREATE TABLE `venues`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `capacity` int NULL DEFAULT NULL,
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `is_available` bit(1) NOT NULL,
  `location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `price_per_hour` decimal(19, 2) NOT NULL,
  `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'ACTIVE',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 651 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of venues
-- ----------------------------
INSERT INTO `venues` VALUES (642, 20, '标准篮球场', b'1', '体育馆一楼', '篮球场A', 90.00, 'basketball', 'NORMAL', '2025-05-10 09:09:18', '2025-05-10 14:44:13');
INSERT INTO `venues` VALUES (643, 20, '标准篮球场', b'1', '体育馆一楼', '篮球场B', 100.00, 'basketball', 'NORMAL', '2025-05-10 09:09:18', '2025-05-10 15:17:57');
INSERT INTO `venues` VALUES (644, 4, '标准羽毛球场', b'1', '体育馆二楼', '羽毛球场A', 50.00, 'badminton', 'NORMAL', '2025-05-10 09:09:18', '2025-05-12 16:38:52');
INSERT INTO `venues` VALUES (645, 4, '标准羽毛球场', b'1', '体育馆二楼', '羽毛球场B', 50.00, 'badminton', 'NORMAL', '2025-05-10 09:09:18', '2025-05-10 14:44:13');
INSERT INTO `venues` VALUES (646, 4, '标准网球场', b'1', '体育馆三楼', '网球场A', 80.00, 'tennis', 'NORMAL', '2025-05-10 09:09:18', '2025-05-10 14:44:13');
INSERT INTO `venues` VALUES (647, 4, '标准网球场', b'1', '体育馆三楼', '网球场B', 100.00, 'tennis', 'NORMAL', '2025-05-10 09:09:18', '2025-05-10 14:44:13');
INSERT INTO `venues` VALUES (648, 50, '标准游泳池', b'1', '体育馆负一楼', '游泳池', 30.00, 'swimming', 'NORMAL', '2025-05-10 09:09:18', '2025-05-10 14:44:13');
INSERT INTO `venues` VALUES (649, 4, '标准乒乓球室', b'1', '体育馆四楼', '乒乓球室A', 20.00, 'table_tennis', 'NORMAL', '2025-05-10 09:09:18', '2025-05-10 14:44:13');
INSERT INTO `venues` VALUES (650, 4, '标准乒乓球室', b'1', '体育馆四楼', '乒乓球室B', 20.00, 'table_tennis', 'NORMAL', '2025-05-10 09:09:18', '2025-05-10 14:44:13');

SET FOREIGN_KEY_CHECKS = 1; 