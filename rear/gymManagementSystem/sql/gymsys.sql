-- ----------------------------
-- Table structure for reservations
-- ----------------------------
DROP TABLE IF EXISTS `reservations`;
CREATE TABLE `reservations` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `venue_id` bigint NOT NULL,
    `user_id` bigint NOT NULL,
    `date` date NOT NULL,
    `start_time` varchar(5) NOT NULL,
    `end_time` varchar(5) NOT NULL,
    `number_of_people` int NOT NULL,
    `cost` decimal(10, 2) NOT NULL,
    `status` varchar(20) NOT NULL,
    `reservation_type` varchar(20) DEFAULT 'NORMAL',
    `remarks` text,
    `cancel_reason` text,
    `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_venue_id` (`venue_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_date` (`date`),
    CONSTRAINT `fk_reservations_venue` FOREIGN KEY (`venue_id`) REFERENCES `venues` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT `fk_reservations_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of reservations
-- ----------------------------
INSERT INTO `reservations` VALUES 
(1, 1, 1, '2025-04-26', '09:00', '10:00', 2, 100.00, 'CONFIRMED', 'NORMAL', '测试预约1', NULL, '2025-04-26 10:32:48', '2025-04-26 10:32:48'),
(2, 2, 2, '2025-04-26', '14:00', '15:00', 4, 100.00, 'USED', 'NORMAL', '测试预约2', NULL, '2025-04-26 10:32:48', '2025-04-26 10:32:48'),
(3, 3, 3, '2025-04-26', '16:00', '17:00', 10, 200.00, 'PENDING', 'NORMAL', '测试预约3', NULL, '2025-04-26 10:32:48', '2025-04-26 10:32:48'); 