-- MySQL dump 10.13  Distrib 8.0.32, for macos13 (arm64)
--
-- Host: localhost    Database: beibei
-- ------------------------------------------------------
-- Server version	8.0.32

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `beibei`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `beibei` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `beibei`;

--
-- Table structure for table `order`
--

DROP TABLE IF EXISTS `order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_no` varchar(40) COLLATE utf8mb4_unicode_ci NOT NULL,
  `customer_id` bigint NOT NULL,
  `worker_id` bigint DEFAULT NULL,
  `service_id` bigint NOT NULL,
  `schedule_date` date NOT NULL,
  `time_slot` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `address_id` bigint DEFAULT NULL,
  `address_text` varchar(300) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `amount` decimal(10,2) NOT NULL,
  `status` enum('CREATED','ASSIGNED','DOING','DONE','CANCELED') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'CREATED',
  `remark` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `order_no` (`order_no`),
  KEY `fk_ord_addr` (`address_id`),
  KEY `idx_ord_user` (`customer_id`,`status`,`created_at`),
  KEY `idx_ord_worker` (`worker_id`,`status`,`schedule_date`),
  KEY `idx_ord_service` (`service_id`),
  CONSTRAINT `fk_ord_addr` FOREIGN KEY (`address_id`) REFERENCES `user_address` (`id`),
  CONSTRAINT `fk_ord_service` FOREIGN KEY (`service_id`) REFERENCES `service` (`id`),
  CONSTRAINT `fk_ord_user` FOREIGN KEY (`customer_id`) REFERENCES `user` (`id`),
  CONSTRAINT `fk_ord_worker` FOREIGN KEY (`worker_id`) REFERENCES `worker` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order`
--

LOCK TABLES `order` WRITE;
/*!40000 ALTER TABLE `order` DISABLE KEYS */;
INSERT INTO `order` VALUES (1,'BB1758897096592A530',5,NULL,2,'2025-09-26','晚上',3,'广东广州从化区No. 548 Guangcong South Road\nTaiping Town, Chahua District',80.00,'DONE','干净卫生','2025-09-26 14:31:37','2025-09-27 01:43:16'),(2,'BB1758954951236F114',5,3,6,'2025-09-26','下午',3,'广东广州从化区No. 548 Guangcong South Road\nTaiping Town, Chahua District',200.00,'DONE','无','2025-09-27 06:35:51','2025-09-27 06:53:02'),(3,'BB17601682809968CBD',5,3,5,'2025-10-10','晚上',3,'广东广州从化区No. 548 Guangcong South Road\nTaiping Town, Chahua District',98.00,'DONE','123','2025-10-11 07:38:01','2025-12-13 02:06:43'),(4,'BB17656010273581905',5,3,6,'2025-12-13','下午',3,'广东广州从化区No. 548 Guangcong South Road\nTaiping Town, Chahua District',200.00,'DONE','没有','2025-12-13 04:43:47','2025-12-13 04:49:40'),(5,'BB176562600277280BD',13,3,6,'2025-12-13','下午',5,'guangdongguanghzoutapingNo. 548 Guangcong South Road\nTaiping Town, Chahua District',200.00,'DONE','认证护理','2025-12-13 11:40:03','2025-12-13 11:49:22'),(6,'BB176562745179465FC',5,NULL,2,'2025-12-13','下午',3,'广东广州从化区No. 548 Guangcong South Road\nTaiping Town, Chahua District',80.00,'CREATED','无','2025-12-13 12:04:12','2025-12-13 12:04:12');
/*!40000 ALTER TABLE `order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_log`
--

DROP TABLE IF EXISTS `order_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NOT NULL,
  `from_status` enum('CREATED','ASSIGNED','DOING','DONE','CANCELED') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `to_status` enum('CREATED','ASSIGNED','DOING','DONE','CANCELED') COLLATE utf8mb4_unicode_ci NOT NULL,
  `op_by` bigint DEFAULT NULL,
  `note` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_olog_order` (`order_id`,`created_at`),
  CONSTRAINT `fk_olog_order` FOREIGN KEY (`order_id`) REFERENCES `order` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_log`
--

LOCK TABLES `order_log` WRITE;
/*!40000 ALTER TABLE `order_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `order_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `review`
--

DROP TABLE IF EXISTS `review`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `review` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NOT NULL,
  `rating` tinyint NOT NULL,
  `content` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `imgs` json DEFAULT NULL,
  `created_by` bigint NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `order_id` (`order_id`),
  KEY `idx_review_user` (`created_by`,`created_at`),
  CONSTRAINT `fk_review_order` FOREIGN KEY (`order_id`) REFERENCES `order` (`id`),
  CONSTRAINT `review_chk_1` CHECK ((`rating` between 1 and 5))
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `review`
--

LOCK TABLES `review` WRITE;
/*!40000 ALTER TABLE `review` DISABLE KEYS */;
INSERT INTO `review` VALUES (2,3,5,NULL,NULL,5,'2025-12-13 03:01:54'),(4,4,5,'很好','[\"/files/2025/12/13/7488bd47-bbcb-49e7-8108-07350a48cb77.jpg\"]',5,'2025-12-13 04:50:47');
/*!40000 ALTER TABLE `review` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `service`
--

DROP TABLE IF EXISTS `service`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `service` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `base_price` decimal(10,2) NOT NULL,
  `unit` enum('小时','天','单') COLLATE utf8mb4_unicode_ci NOT NULL,
  `category_id` bigint DEFAULT NULL,
  `status` tinyint NOT NULL DEFAULT '1',
  `hot` tinyint NOT NULL DEFAULT '0',
  `rating` decimal(3,2) DEFAULT '5.00',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_srv_cat` (`status`,`hot`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `service`
--

LOCK TABLES `service` WRITE;
/*!40000 ALTER TABLE `service` DISABLE KEYS */;
INSERT INTO `service` VALUES (1,'日常保洁','客厅、卧室、厨房、卫生间全面清洁',50.00,'小时',1,1,1,4.80,'2025-09-14 14:04:05'),(2,'深度保洁','包含日常保洁+家电清洁+玻璃清洁',80.00,'小时',1,1,1,4.90,'2025-09-14 14:04:05'),(3,'月嫂服务','专业月嫂，24小时照顾产妇和新生儿',300.00,'天',4,1,0,4.70,'2025-09-14 14:04:05'),(4,'育儿嫂','专业育儿嫂，照顾0-3岁婴幼儿',200.00,'天',4,1,0,4.60,'2025-09-14 14:04:05'),(5,'Test Service','Test',98.00,'天',NULL,1,0,4.50,'2025-09-14 14:04:05'),(6,'老年护理','服务老人',200.00,'天',4,1,1,5.00,'2025-09-19 04:18:43');
/*!40000 ALTER TABLE `service` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `service_category`
--

DROP TABLE IF EXISTS `service_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `service_category` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `parent_id` bigint DEFAULT NULL,
  `status` tinyint NOT NULL DEFAULT '1',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `service_category`
--

LOCK TABLES `service_category` WRITE;
/*!40000 ALTER TABLE `service_category` DISABLE KEYS */;
INSERT INTO `service_category` VALUES (1,'家庭保洁','专业的家庭清洁服务',NULL,1,'2025-09-22 15:12:40'),(2,'深度清洁','彻底的深度清洁服务',NULL,1,'2025-09-22 15:12:40'),(3,'做饭服务','营养美味的家常菜服务',NULL,1,'2025-09-22 15:12:40'),(4,'育儿服务','专业的育儿和看护服务',NULL,1,'2025-09-22 15:12:40');
/*!40000 ALTER TABLE `service_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password_hash` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `role` enum('USER','WORKER','ADMIN') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'USER',
  `status` tinyint NOT NULL DEFAULT '1',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `phone` (`phone`),
  KEY `idx_user_phone` (`phone`),
  KEY `idx_user_role_status` (`role`,`status`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'13800000000','$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi','ADMIN',1,'2025-09-14 14:04:05','2025-09-17 15:52:52'),(2,'13800000001','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi','USER',1,'2025-09-14 14:04:05','2025-09-16 13:57:31'),(3,'13800000002','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi','WORKER',1,'2025-09-14 14:04:05','2025-09-14 14:04:05'),(4,'13800000003','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi','ADMIN',1,'2025-09-14 16:01:21','2025-09-17 15:51:25'),(5,'13937549244','$2a$10$ZmIXkBGqRHt8fp0GsDfpveX4OvW8TwoTXqg/zUYb.SrAy5ZHG35bq','USER',1,'2025-09-15 03:33:13','2025-09-22 15:00:21'),(6,'13534848985','$2a$10$fwv9Rey2G1Ohu7RsRWt2U.2rPwROW18/SXeQmDhSXj0UFz0HJt56.','ADMIN',1,'2025-09-16 12:24:54','2025-09-16 12:24:54'),(8,'19074178020','$2a$10$px1HMGOb45zOacpM952xMul0OPi5e4D7RiQnvwLsX2y66ZAfznUhe','WORKER',1,'2025-09-16 12:45:07','2025-09-16 13:57:24'),(10,'13534848984','$2a$10$1rKxGLfvZUOT2MiNHuqQV.KOabJCJc.YuapckK/YcGoLBuUMeJ2Fy','WORKER',1,'2025-09-17 14:43:08','2025-09-17 14:43:08'),(11,'13999999999','$2a$10$dXJ3SW6G7P2YNDyMLgkr6OVN8Q0Gf7Q3WfRvqjGi1KKXg8LpDJzUm','WORKER',1,'2025-09-22 14:59:20','2025-12-13 06:56:53'),(12,'18888888888','$2a$10$ZmIXkBGqRHt8fp0GsDfpveX4OvW8TwoTXqg/zUYb.SrAy5ZHG35bq','USER',1,'2025-09-22 14:59:30','2025-09-22 14:59:30'),(13,'13937549243','$2a$10$mx7J/NbFawPmepXpg4WgpuiXdI3LioB8X4zaRfWY2egwvv9Ol3Kou','USER',1,'2025-12-13 10:00:43','2025-12-13 10:00:43');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `user_profile_auto_create` AFTER INSERT ON `user` FOR EACH ROW BEGIN
    
    INSERT INTO user_profile (user_id, name, gender, birthday, avatar, email, address)
    VALUES (NEW.id, '用户', 'U', NULL, NULL, NULL, NULL);
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `user_address`
--

DROP TABLE IF EXISTS `user_address`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_address` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `contact_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `contact_phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `province` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `city` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `district` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `detail` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_default` tinyint NOT NULL DEFAULT '0',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_addr_user` (`user_id`,`is_default`),
  CONSTRAINT `fk_addr_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_address`
--

LOCK TABLES `user_address` WRITE;
/*!40000 ALTER TABLE `user_address` DISABLE KEYS */;
INSERT INTO `user_address` VALUES (2,5,'qsh','13937549244','afasfasf','guanghzou','sadfas','No. 548 Guangcong South Road\nTaiping Town, Chahua District',0,'2025-09-15 07:58:15'),(3,5,'qsh','13937549244','广东','广州','从化区','No. 548 Guangcong South Road\nTaiping Town, Chahua District',1,'2025-09-15 07:58:29'),(4,5,'qfasdfsa','13937549244','guangdong','guanghzou','a\'s\'fa\'d\'f','No. 548 Guangcong South Road\nTaiping Town, Chahua District',0,'2025-09-15 08:02:43'),(5,13,'user','13937549244','guangdong','guanghzou','taping','No. 548 Guangcong South Road\nTaiping Town, Chahua District',0,'2025-12-13 10:02:00'),(6,13,'user2','13937549233','guangdong','guanghzou','taiping','No. 548 Guangcong South Road\nTaiping Town, Chahua District',1,'2025-12-13 11:53:57');
/*!40000 ALTER TABLE `user_address` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_profile`
--

DROP TABLE IF EXISTS `user_profile`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_profile` (
  `user_id` bigint NOT NULL,
  `name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `gender` enum('M','F','U') COLLATE utf8mb4_unicode_ci DEFAULT 'U',
  `birthday` date DEFAULT NULL,
  `avatar` text COLLATE utf8mb4_unicode_ci,
  `email` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `address` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  KEY `idx_user_profile_user_id` (`user_id`),
  CONSTRAINT `fk_profile_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_profile`
--

LOCK TABLES `user_profile` WRITE;
/*!40000 ALTER TABLE `user_profile` DISABLE KEYS */;
INSERT INTO `user_profile` VALUES (1,'qsh222','M','1990-01-01','http://localhost:8080/avatars/2025/09/15/7eb20e4f-34f1-4835-bc07-7140ceef0856.jpg','admin@beibei.com','北京市朝阳区管理中心大厦'),(2,'张女士','F','1985-03-15','/uploads/avatars/default/user1.jpg','zhang@example.com','北京市海淀区中关村软件园'),(3,'李阿姨','F','1978-08-20','/uploads/avatars/default/worker.jpg','li@example.com','北京市丰台区南三环中路'),(4,'管理员2','U','1992-01-01','/uploads/avatars/default/admin.jpg','admin2@beibei.com','北京市海淀区中关村大街'),(5,'qsh','M','2002-06-19','http://localhost:8080/avatars/2025/09/16/dcec48f4-97f8-47f3-980a-46d0c6fa7231.jpg','qsh@example.com','上海市浦东新区张江高科技园区'),(6,'系统管理员','F','2025-10-08','http://localhost:8080/avatars/2025/10/02/1b87427d-074f-48cd-9adc-a229d2dcc139.jpg','7758qilinke@gmail.com','No. 548 Guangcong South Road\nTaiping Town, Chahua District'),(8,'王阿姨','F',NULL,'/uploads/avatars/default/worker.jpg',NULL,NULL),(10,'李阿姨','F','2025-09-16','/uploads/avatars/default/worker.jpg','7758qilinke@gmail.com','No. 548 Guangcong South Road'),(11,'用户','U',NULL,'/uploads/avatars/default/worker.jpg',NULL,NULL),(12,'测试用户','U',NULL,'/uploads/avatars/default/user1.jpeg',NULL,NULL),(13,'user','U',NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `user_profile` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `worker`
--

DROP TABLE IF EXISTS `worker`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `worker` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `level` tinyint DEFAULT '1',
  `years` smallint DEFAULT '0',
  `bio` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `score` decimal(3,2) NOT NULL DEFAULT '5.00',
  `status` tinyint NOT NULL DEFAULT '1',
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id` (`user_id`),
  CONSTRAINT `fk_worker_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `worker`
--

LOCK TABLES `worker` WRITE;
/*!40000 ALTER TABLE `worker` DISABLE KEYS */;
INSERT INTO `worker` VALUES (1,3,3,5,'专业月嫂，有5年经验，擅长新生儿护理和产妇护理',4.80,1,'2025-09-14 22:04:05','2025-09-14 22:04:05'),(2,8,2,3,'专业家政服务员，擅长家庭保洁和日常护理，工作认真负责',4.50,1,'2025-09-16 20:45:07','2025-09-16 21:57:24'),(3,10,4,11,'擅长扫地',4.67,1,'2025-09-17 22:43:08','2025-09-17 22:43:08'),(4,11,1,6,'新晋阿姨',5.00,1,'2025-09-22 22:59:20','2025-12-13 14:56:53');
/*!40000 ALTER TABLE `worker` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `worker_schedule`
--

DROP TABLE IF EXISTS `worker_schedule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `worker_schedule` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `worker_id` bigint NOT NULL,
  `date` date NOT NULL,
  `time_slot` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `status` enum('FREE','BUSY') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'FREE',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_ws` (`worker_id`,`date`,`time_slot`),
  CONSTRAINT `fk_ws_worker` FOREIGN KEY (`worker_id`) REFERENCES `worker` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=104 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `worker_schedule`
--

LOCK TABLES `worker_schedule` WRITE;
/*!40000 ALTER TABLE `worker_schedule` DISABLE KEYS */;
INSERT INTO `worker_schedule` VALUES (1,1,'2025-09-15','AM','FREE','2025-09-14 14:04:05'),(2,1,'2025-09-15','PM','FREE','2025-09-14 14:04:05'),(3,1,'2025-09-16','AM','FREE','2025-09-14 14:04:05'),(4,1,'2025-09-16','PM','FREE','2025-09-14 14:04:05'),(5,1,'2025-09-17','AM','FREE','2025-09-14 14:04:05'),(6,1,'2025-09-17','PM','FREE','2025-09-14 14:04:05'),(7,3,'2025-09-26','下午','FREE','2025-09-27 06:46:14'),(8,3,'2025-09-26','晚上','FREE','2025-09-27 06:46:14'),(9,3,'2025-09-27','下午','FREE','2025-09-27 06:46:14'),(10,3,'2025-09-27','晚上','FREE','2025-09-27 06:46:14'),(11,3,'2025-12-01','morning','FREE','2025-12-13 02:01:51'),(12,3,'2025-12-01','afternoon','FREE','2025-12-13 02:01:51'),(13,3,'2025-12-01','evening','FREE','2025-12-13 02:01:51'),(14,3,'2025-12-02','morning','FREE','2025-12-13 02:01:51'),(15,3,'2025-12-02','afternoon','FREE','2025-12-13 02:01:51'),(16,3,'2025-12-02','evening','FREE','2025-12-13 02:01:51'),(17,3,'2025-12-03','morning','FREE','2025-12-13 02:01:51'),(18,3,'2025-12-03','afternoon','FREE','2025-12-13 02:01:51'),(19,3,'2025-12-03','evening','FREE','2025-12-13 02:01:51'),(20,3,'2025-12-04','morning','FREE','2025-12-13 02:01:51'),(21,3,'2025-12-04','afternoon','FREE','2025-12-13 02:01:51'),(22,3,'2025-12-04','evening','FREE','2025-12-13 02:01:51'),(23,3,'2025-12-05','morning','FREE','2025-12-13 02:01:51'),(24,3,'2025-12-05','afternoon','FREE','2025-12-13 02:01:51'),(25,3,'2025-12-05','evening','FREE','2025-12-13 02:01:51'),(26,3,'2025-12-06','morning','FREE','2025-12-13 02:01:51'),(27,3,'2025-12-06','afternoon','FREE','2025-12-13 02:01:51'),(28,3,'2025-12-06','evening','FREE','2025-12-13 02:01:51'),(29,3,'2025-12-07','morning','FREE','2025-12-13 02:01:51'),(30,3,'2025-12-07','afternoon','FREE','2025-12-13 02:01:51'),(31,3,'2025-12-07','evening','FREE','2025-12-13 02:01:51'),(32,3,'2025-12-08','morning','FREE','2025-12-13 02:01:51'),(33,3,'2025-12-08','afternoon','FREE','2025-12-13 02:01:51'),(34,3,'2025-12-08','evening','FREE','2025-12-13 02:01:51'),(35,3,'2025-12-09','morning','FREE','2025-12-13 02:01:51'),(36,3,'2025-12-09','afternoon','FREE','2025-12-13 02:01:51'),(37,3,'2025-12-09','evening','FREE','2025-12-13 02:01:51'),(38,3,'2025-12-10','morning','FREE','2025-12-13 02:01:51'),(39,3,'2025-12-10','afternoon','FREE','2025-12-13 02:01:51'),(40,3,'2025-12-10','evening','FREE','2025-12-13 02:01:51'),(41,3,'2025-12-11','morning','FREE','2025-12-13 02:01:51'),(42,3,'2025-12-11','afternoon','FREE','2025-12-13 02:01:51'),(43,3,'2025-12-11','evening','FREE','2025-12-13 02:01:51'),(44,3,'2025-12-12','morning','FREE','2025-12-13 02:01:51'),(45,3,'2025-12-12','afternoon','FREE','2025-12-13 02:01:51'),(46,3,'2025-12-12','evening','FREE','2025-12-13 02:01:51'),(47,3,'2025-12-13','morning','FREE','2025-12-13 02:01:51'),(48,3,'2025-12-13','afternoon','FREE','2025-12-13 02:01:51'),(49,3,'2025-12-13','evening','FREE','2025-12-13 02:01:51'),(50,3,'2025-12-14','morning','FREE','2025-12-13 02:01:51'),(51,3,'2025-12-14','afternoon','FREE','2025-12-13 02:01:51'),(52,3,'2025-12-14','evening','FREE','2025-12-13 02:01:51'),(53,3,'2025-12-15','morning','FREE','2025-12-13 02:01:51'),(54,3,'2025-12-15','afternoon','FREE','2025-12-13 02:01:51'),(55,3,'2025-12-15','evening','FREE','2025-12-13 02:01:51'),(56,3,'2025-12-16','morning','FREE','2025-12-13 02:01:51'),(57,3,'2025-12-16','afternoon','FREE','2025-12-13 02:01:51'),(58,3,'2025-12-16','evening','FREE','2025-12-13 02:01:51'),(59,3,'2025-12-17','morning','FREE','2025-12-13 02:01:51'),(60,3,'2025-12-17','afternoon','FREE','2025-12-13 02:01:51'),(61,3,'2025-12-17','evening','FREE','2025-12-13 02:01:51'),(62,3,'2025-12-18','morning','FREE','2025-12-13 02:01:51'),(63,3,'2025-12-18','afternoon','FREE','2025-12-13 02:01:51'),(64,3,'2025-12-18','evening','FREE','2025-12-13 02:01:51'),(65,3,'2025-12-19','morning','FREE','2025-12-13 02:01:51'),(66,3,'2025-12-19','afternoon','FREE','2025-12-13 02:01:51'),(67,3,'2025-12-19','evening','FREE','2025-12-13 02:01:51'),(68,3,'2025-12-20','morning','FREE','2025-12-13 02:01:51'),(69,3,'2025-12-20','afternoon','FREE','2025-12-13 02:01:51'),(70,3,'2025-12-20','evening','FREE','2025-12-13 02:01:51'),(71,3,'2025-12-21','morning','FREE','2025-12-13 02:01:51'),(72,3,'2025-12-21','afternoon','FREE','2025-12-13 02:01:51'),(73,3,'2025-12-21','evening','FREE','2025-12-13 02:01:51'),(74,3,'2025-12-22','morning','FREE','2025-12-13 02:01:51'),(75,3,'2025-12-22','afternoon','FREE','2025-12-13 02:01:51'),(76,3,'2025-12-22','evening','FREE','2025-12-13 02:01:51'),(77,3,'2025-12-23','morning','FREE','2025-12-13 02:01:51'),(78,3,'2025-12-23','afternoon','FREE','2025-12-13 02:01:51'),(79,3,'2025-12-23','evening','FREE','2025-12-13 02:01:51'),(80,3,'2025-12-24','morning','FREE','2025-12-13 02:01:51'),(81,3,'2025-12-24','afternoon','FREE','2025-12-13 02:01:51'),(82,3,'2025-12-24','evening','FREE','2025-12-13 02:01:51'),(83,3,'2025-12-25','morning','FREE','2025-12-13 02:01:51'),(84,3,'2025-12-25','afternoon','FREE','2025-12-13 02:01:51'),(85,3,'2025-12-25','evening','FREE','2025-12-13 02:01:51'),(86,3,'2025-12-26','morning','FREE','2025-12-13 02:01:51'),(87,3,'2025-12-26','afternoon','FREE','2025-12-13 02:01:51'),(88,3,'2025-12-26','evening','FREE','2025-12-13 02:01:51'),(89,3,'2025-12-27','morning','FREE','2025-12-13 02:01:51'),(90,3,'2025-12-27','afternoon','FREE','2025-12-13 02:01:51'),(91,3,'2025-12-27','evening','FREE','2025-12-13 02:01:51'),(92,3,'2025-12-28','morning','FREE','2025-12-13 02:01:51'),(93,3,'2025-12-28','afternoon','FREE','2025-12-13 02:01:51'),(94,3,'2025-12-28','evening','FREE','2025-12-13 02:01:51'),(95,3,'2025-12-29','morning','FREE','2025-12-13 02:01:51'),(96,3,'2025-12-29','afternoon','FREE','2025-12-13 02:01:51'),(97,3,'2025-12-29','evening','FREE','2025-12-13 02:01:51'),(98,3,'2025-12-30','morning','FREE','2025-12-13 02:01:51'),(99,3,'2025-12-30','afternoon','FREE','2025-12-13 02:01:51'),(100,3,'2025-12-30','evening','FREE','2025-12-13 02:01:51'),(101,3,'2025-12-31','morning','FREE','2025-12-13 02:01:51'),(102,3,'2025-12-31','afternoon','FREE','2025-12-13 02:01:51'),(103,3,'2025-12-31','evening','FREE','2025-12-13 02:01:51');
/*!40000 ALTER TABLE `worker_schedule` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-12-15 10:34:25
