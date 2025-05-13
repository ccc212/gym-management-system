DROP TABLE IF EXISTS `usages`;
DROP TABLE IF EXISTS `reservations`;
CREATE TABLE `reservations` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `venue_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `date` date NOT NULL,
  `start_time` varchar(10) NOT NULL,
  `end_time` varchar(10) NOT NULL,
  `number_of_people` int NOT NULL,
  `status` varchar(20) NOT NULL,
  `reservation_type` varchar(20) DEFAULT 'NORMAL',
  `remarks` varchar(255) DEFAULT NULL,
  `cancel_reason` varchar(255) DEFAULT NULL,
  `cost` decimal(10,2) DEFAULT NULL,
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `actual_start_time` varchar(10) DEFAULT NULL,
  `actual_end_time` varchar(10) DEFAULT NULL,
  `duration` varchar(50) DEFAULT NULL,
  `actual_cost` decimal(10,2) DEFAULT NULL,
  `payment_method` varchar(20) DEFAULT NULL,
  `payment_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_venue_id` (`venue_id`),
  CONSTRAINT `FK_venue_id` FOREIGN KEY (`venue_id`) REFERENCES `venues` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci; 