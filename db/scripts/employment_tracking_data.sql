
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

/*!40000 DROP DATABASE IF EXISTS `employment_tracking`*/;

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `employment_tracking` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `employment_tracking`;
DROP TABLE IF EXISTS `attachment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `attachment` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `record_id` bigint NOT NULL COMMENT '关联记录ID',
  `file_name` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '原始文件名',
  `file_path` varchar(300) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '存储路径',
  `file_size` bigint DEFAULT '0' COMMENT '文件大小(字节)',
  `file_type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件类型',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_record_id` (`record_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='附件表';
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `attachment` WRITE;
/*!40000 ALTER TABLE `attachment` DISABLE KEYS */;
/*!40000 ALTER TABLE `attachment` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `audit_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `audit_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `record_id` bigint NOT NULL COMMENT '就业记录ID',
  `action` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '操作: PASS/REJECT',
  `comment` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '审核意见',
  `operator_id` bigint NOT NULL COMMENT '操作人ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_record_id` (`record_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='审核日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `audit_log` WRITE;
/*!40000 ALTER TABLE `audit_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `audit_log` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `class_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `class_info` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '班级名称',
  `major_id` bigint NOT NULL COMMENT '所属专业ID',
  `grade` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '年级(如2022)',
  `sort_order` int DEFAULT '0' COMMENT '排序号',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_major_id` (`major_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='班级表';
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `class_info` WRITE;
/*!40000 ALTER TABLE `class_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `class_info` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `department`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `department` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '院系名称',
  `code` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '院系编码',
  `sort_order` int DEFAULT '0' COMMENT '排序号',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='院系表';
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `department` WRITE;
/*!40000 ALTER TABLE `department` DISABLE KEYS */;
INSERT INTO `department` VALUES (1,'计算机科学与技术学院','CS',1,1,'2026-08-07 10:36:22','2026-08-07 10:36:22'),(2,'电子信息工程学院','EE',2,1,'2026-08-07 10:36:22','2026-08-07 10:36:22'),(3,'机械工程学院','ME',3,1,'2026-08-07 10:36:22','2026-08-07 10:36:22'),(4,'经济管理学院','EM',4,1,'2026-08-07 10:36:22','2026-08-07 10:36:22'),(5,'文法学院','LA',5,1,'2026-08-07 10:36:22','2026-08-07 10:36:22'),(6,'艺术设计学院','AD',6,1,'2026-08-07 10:36:22','2026-08-07 10:36:22'),(7,'理学院','SC',7,1,'2026-08-07 10:36:22','2026-08-07 10:36:22');
/*!40000 ALTER TABLE `department` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `dict_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dict_item` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `type_id` bigint NOT NULL COMMENT '字典类型ID',
  `label` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '字典标签',
  `value` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '字典值',
  `sort_order` int DEFAULT '0' COMMENT '排序号',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_type_id` (`type_id`)
) ENGINE=InnoDB AUTO_INCREMENT=53 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字典项表';
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `dict_item` WRITE;
/*!40000 ALTER TABLE `dict_item` DISABLE KEYS */;
INSERT INTO `dict_item` VALUES (1,1,'已就业','EMPLOYED',1,1,'2026-08-07 10:36:22','2026-08-07 10:36:22'),(2,1,'未就业','UNEMPLOYED',2,1,'2026-08-07 10:36:22','2026-08-07 10:36:22'),(3,1,'升学','FURTHER_STUDY',3,1,'2026-08-07 10:36:22','2026-08-07 10:36:22'),(4,1,'入伍','MILITARY',4,1,'2026-08-07 10:36:22','2026-08-07 10:36:22'),(5,1,'创业','ENTREPRENEURSHIP',5,1,'2026-08-07 10:36:22','2026-08-07 10:36:22'),(6,1,'待业','WAITING',6,1,'2026-08-07 10:36:22','2026-08-07 10:36:22'),(7,2,'待审核','PENDING',1,1,'2026-08-07 10:36:22','2026-08-07 10:36:22'),(8,2,'审核通过','PASSED',2,1,'2026-08-07 10:36:22','2026-08-07 10:36:22'),(9,2,'审核退回','REJECTED',3,1,'2026-08-07 10:36:22','2026-08-07 10:36:22'),(10,5,'签约就业','SIGNED',1,1,'2026-08-07 10:36:22','2026-08-07 10:36:22'),(11,5,'升学深造','FURTHER_STUDY',2,1,'2026-08-07 10:36:22','2026-08-07 10:36:22'),(12,5,'出国留学','ABROAD',3,1,'2026-08-07 10:36:22','2026-08-07 10:36:22'),(13,5,'自主创业','ENTREPRENEURSHIP',4,1,'2026-08-07 10:36:22','2026-08-07 10:36:22'),(14,5,'灵活就业','FLEXIBLE',5,1,'2026-08-07 10:36:22','2026-08-07 10:36:22'),(15,5,'待就业','WAITING',6,1,'2026-08-07 10:36:22','2026-08-07 10:36:22'),(16,3,'国有企业','STATE_OWNED',1,1,'2026-08-07 10:36:22','2026-08-07 10:36:22'),(17,3,'民营企业','PRIVATE',2,1,'2026-08-07 10:36:22','2026-08-07 10:36:22'),(18,3,'外资企业','FOREIGN',3,1,'2026-08-07 10:36:22','2026-08-07 10:36:22'),(19,3,'事业单位','INSTITUTION',4,1,'2026-08-07 10:36:22','2026-08-07 10:36:22'),(20,3,'政府机关','GOVERNMENT',5,1,'2026-08-07 10:36:22','2026-08-07 10:36:22'),(21,4,'互联网/IT','IT',1,1,'2026-08-07 10:36:22','2026-08-07 10:36:22'),(22,4,'金融','FINANCE',2,1,'2026-08-07 10:36:22','2026-08-07 10:36:22'),(23,4,'教育','EDUCATION',3,1,'2026-08-07 10:36:22','2026-08-07 10:36:22'),(24,4,'制造业','MANUFACTURING',4,1,'2026-08-07 10:36:22','2026-08-07 10:36:22'),(25,4,'医疗健康','HEALTHCARE',5,1,'2026-08-07 10:36:22','2026-08-07 10:36:22'),(26,4,'房地产/建筑','REAL_ESTATE',6,1,'2026-08-07 10:36:22','2026-08-07 10:36:22'),(27,1,'已就业','EMPLOYED',1,1,'2026-08-07 10:59:59','2026-08-07 10:59:59'),(28,1,'未就业','UNEMPLOYED',2,1,'2026-08-07 10:59:59','2026-08-07 10:59:59'),(29,1,'升学','FURTHER_STUDY',3,1,'2026-08-07 10:59:59','2026-08-07 10:59:59'),(30,1,'入伍','MILITARY',4,1,'2026-08-07 10:59:59','2026-08-07 10:59:59'),(31,1,'创业','ENTREPRENEURSHIP',5,1,'2026-08-07 10:59:59','2026-08-07 10:59:59'),(32,1,'待业','WAITING',6,1,'2026-08-07 10:59:59','2026-08-07 10:59:59'),(33,2,'待审核','PENDING',1,1,'2026-08-07 10:59:59','2026-08-07 10:59:59'),(34,2,'审核通过','PASSED',2,1,'2026-08-07 10:59:59','2026-08-07 10:59:59'),(35,2,'审核退回','REJECTED',3,1,'2026-08-07 10:59:59','2026-08-07 10:59:59'),(36,3,'国有企业','STATE_OWNED',1,1,'2026-08-07 10:59:59','2026-08-07 10:59:59'),(37,3,'民营企业','PRIVATE',2,1,'2026-08-07 10:59:59','2026-08-07 10:59:59'),(38,3,'外资企业','FOREIGN',3,1,'2026-08-07 10:59:59','2026-08-07 10:59:59'),(39,3,'事业单位','INSTITUTION',4,1,'2026-08-07 10:59:59','2026-08-07 10:59:59'),(40,3,'政府机关','GOVERNMENT',5,1,'2026-08-07 10:59:59','2026-08-07 10:59:59'),(41,4,'互联网/IT','IT',1,1,'2026-08-07 10:59:59','2026-08-07 10:59:59'),(42,4,'金融','FINANCE',2,1,'2026-08-07 10:59:59','2026-08-07 10:59:59'),(43,4,'教育','EDUCATION',3,1,'2026-08-07 10:59:59','2026-08-07 10:59:59'),(44,4,'制造业','MANUFACTURING',4,1,'2026-08-07 10:59:59','2026-08-07 10:59:59'),(45,4,'医疗健康','HEALTHCARE',5,1,'2026-08-07 10:59:59','2026-08-07 10:59:59'),(46,4,'房地产/建筑','REAL_ESTATE',6,1,'2026-08-07 10:59:59','2026-08-07 10:59:59'),(47,5,'签约就业','SIGNED',1,1,'2026-08-07 10:59:59','2026-08-07 10:59:59'),(48,5,'升学深造','FURTHER_STUDY',2,1,'2026-08-07 10:59:59','2026-08-07 10:59:59'),(49,5,'出国留学','ABROAD',3,1,'2026-08-07 10:59:59','2026-08-07 10:59:59'),(50,5,'自主创业','ENTREPRENEURSHIP',4,1,'2026-08-07 10:59:59','2026-08-07 10:59:59'),(51,5,'灵活就业','FLEXIBLE',5,1,'2026-08-07 10:59:59','2026-08-07 10:59:59'),(52,5,'待就业','WAITING',6,1,'2026-08-07 10:59:59','2026-08-07 10:59:59');
/*!40000 ALTER TABLE `dict_item` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `dict_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dict_type` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '字典编码',
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '字典名称',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字典类型表';
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `dict_type` WRITE;
/*!40000 ALTER TABLE `dict_type` DISABLE KEYS */;
INSERT INTO `dict_type` VALUES (1,'employment_status','就业状态',1,'2026-08-07 10:36:22','2026-08-07 10:36:22'),(2,'review_status','审核状态',1,'2026-08-07 10:36:22','2026-08-07 10:36:22'),(3,'company_type','单位性质',1,'2026-08-07 10:36:22','2026-08-07 10:36:22'),(4,'industry','所属行业',1,'2026-08-07 10:36:22','2026-08-07 10:36:22'),(5,'destination','去向类型',1,'2026-08-07 10:36:22','2026-08-07 10:36:22');
/*!40000 ALTER TABLE `dict_type` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `employment_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `employment_record` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `graduate_id` bigint NOT NULL COMMENT '毕业生ID',
  `company_name` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '单位名称',
  `company_type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '单位性质(国企/民企/外企等)',
  `industry` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '所属行业',
  `position` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '职位',
  `salary_range` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '薪资范围',
  `city` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '工作城市',
  `destination` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '去向: 签约就业/升学/出国/创业/灵活就业/待就业',
  `review_status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDING' COMMENT '审核状态: PENDING/PASSED/REJECTED',
  `review_comment` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '审核意见',
  `reviewer_id` bigint DEFAULT NULL COMMENT '审核人ID',
  `review_time` datetime DEFAULT NULL COMMENT '审核时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_graduate_id` (`graduate_id`),
  KEY `idx_review_status` (`review_status`),
  KEY `idx_destination` (`destination`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='就业记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `employment_record` WRITE;
/*!40000 ALTER TABLE `employment_record` DISABLE KEYS */;
/*!40000 ALTER TABLE `employment_record` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `graduate`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `graduate` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_no` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '学号',
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '姓名',
  `gender` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '性别',
  `id_card` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '身份证号',
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '邮箱',
  `dept_id` bigint NOT NULL COMMENT '院系ID',
  `major_id` bigint NOT NULL COMMENT '专业ID',
  `class_id` bigint NOT NULL COMMENT '班级ID',
  `grade` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '年级',
  `graduate_year` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '毕业年份',
  `employment_status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'UNEMPLOYED' COMMENT '就业状态',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `student_no` (`student_no`),
  KEY `idx_dept_id` (`dept_id`),
  KEY `idx_major_id` (`major_id`),
  KEY `idx_class_id` (`class_id`),
  KEY `idx_status` (`employment_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='毕业生表';
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `graduate` WRITE;
/*!40000 ALTER TABLE `graduate` DISABLE KEYS */;
/*!40000 ALTER TABLE `graduate` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `login_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `login_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint DEFAULT NULL COMMENT '用户ID',
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '登录用户名',
  `ip` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '登录IP',
  `user_agent` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '浏览器UA',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '1成功 0失败',
  `message` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='登录日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `login_log` WRITE;
/*!40000 ALTER TABLE `login_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `login_log` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `major`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `major` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '专业名称',
  `code` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '专业编码',
  `dept_id` bigint NOT NULL COMMENT '所属院系ID',
  `sort_order` int DEFAULT '0' COMMENT '排序号',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`),
  KEY `idx_dept_id` (`dept_id`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='专业表';
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `major` WRITE;
/*!40000 ALTER TABLE `major` DISABLE KEYS */;
INSERT INTO `major` VALUES (1,'计算机科学与技术','CS01',1,1,1,'2026-08-07 10:36:22','2026-08-07 10:36:22'),(2,'软件工程','CS02',1,2,1,'2026-08-07 10:36:22','2026-08-07 10:36:22'),(3,'物联网工程','CS03',1,3,1,'2026-08-07 10:36:22','2026-08-07 10:36:22'),(4,'电子信息工程','EE01',2,1,1,'2026-08-07 10:36:22','2026-08-07 10:36:22'),(5,'通信工程','EE02',2,2,1,'2026-08-07 10:36:22','2026-08-07 10:36:22'),(6,'机械设计制造','ME01',3,1,1,'2026-08-07 10:36:22','2026-08-07 10:36:22'),(7,'车辆工程','ME02',3,2,1,'2026-08-07 10:36:22','2026-08-07 10:36:22'),(8,'工商管理','EM01',4,1,1,'2026-08-07 10:36:22','2026-08-07 10:36:22'),(9,'会计学','EM02',4,2,1,'2026-08-07 10:36:22','2026-08-07 10:36:22'),(10,'法学','LA01',5,1,1,'2026-08-07 10:36:22','2026-08-07 10:36:22'),(11,'视觉传达设计','AD01',6,1,1,'2026-08-07 10:36:22','2026-08-07 10:36:22'),(12,'数学与应用数学','SC01',7,1,1,'2026-08-07 10:36:22','2026-08-07 10:36:22');
/*!40000 ALTER TABLE `major` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `notification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notification` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '接收用户ID',
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '通知标题',
  `content` text COLLATE utf8mb4_unicode_ci COMMENT '通知内容',
  `is_read` tinyint NOT NULL DEFAULT '0' COMMENT '0未读 1已读',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_is_read` (`is_read`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知表';
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `notification` WRITE;
/*!40000 ALTER TABLE `notification` DISABLE KEYS */;
/*!40000 ALTER TABLE `notification` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `operation_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `operation_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint DEFAULT NULL COMMENT '操作用户ID',
  `username` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作用户名',
  `module` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作模块',
  `action` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作描述',
  `method` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '请求方法',
  `params` text COLLATE utf8mb4_unicode_ci COMMENT '请求参数',
  `ip` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '请求IP',
  `duration` bigint DEFAULT '0' COMMENT '耗时(ms)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `operation_log` WRITE;
/*!40000 ALTER TABLE `operation_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `operation_log` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `sys_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_config` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `config_key` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '配置键',
  `config_value` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '配置值',
  `description` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '配置说明',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `sys_config` WRITE;
/*!40000 ALTER TABLE `sys_config` DISABLE KEYS */;
/*!40000 ALTER TABLE `sys_config` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `sys_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码(BCrypt加密)',
  `real_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '真实姓名',
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '头像URL',
  `role` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'STUDENT' COMMENT '角色: ADMIN/TEACHER/STUDENT',
  `dept_id` bigint DEFAULT NULL COMMENT '所属院系ID',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态: 1启用 0禁用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  KEY `idx_role` (`role`),
  KEY `idx_dept_id` (`dept_id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `sys_user` WRITE;
/*!40000 ALTER TABLE `sys_user` DISABLE KEYS */;
INSERT INTO `sys_user` VALUES (2,'admin','$2a$10$AtxrOxn1e2n5wHmPJ2u2i.sioQwZr5XqexBlJtra6xyd8PJHRmpnm','系统管理员','13800000000',NULL,NULL,'ADMIN',NULL,1,'2026-08-07 11:40:31','2026-08-07 11:40:31'),(3,'teacher','$2a$10$ZCxsr9nx5.jKg5XfxL6hEu7Yd2XY7eNJPAQtG0dKsaLNEAJBmALvW','张老师',NULL,NULL,NULL,'TEACHER',1,1,'2026-08-07 11:40:31','2026-08-07 14:02:15'),(4,'student','$2a$10$alCN69NdmDIo4/SoYD3jUuRluc3uX5ciBrfpZRmykmY0TOvS0vcaS','李同学',NULL,NULL,NULL,'STUDENT',1,1,'2026-08-07 11:40:31','2026-08-07 14:02:16'),(5,'testuser','$2a$10$0/mNwnLn.ala3DZirQaumujK3kJnHyDmzuMSwEbBSp7ZczlhfHmUy','????',NULL,NULL,NULL,'STUDENT',NULL,1,'2026-08-07 13:59:40','2026-08-07 14:46:18'),(7,'zxx','$2a$10$9FYaRdCE.fsSBpq1d561zOF2FGjOCneMLPb5y4R7T79X.UOENp5D6','赵鑫鑫',NULL,NULL,NULL,'STUDENT',NULL,1,'2026-08-07 14:01:01','2026-08-07 14:46:18'),(8,'testdebug','$2a$10$i7EMDL.5STdaFeFObke90u.8r6vePi.B5994A.36rmbBzvjkuH3Xq','????',NULL,NULL,NULL,'STUDENT',NULL,1,'2026-08-07 14:18:01','2026-08-07 14:46:18'),(9,'qqq','$2a$10$LyV1bXQP1wGpkcot0ZwL7eagjP9ZvtBMpSdQdQH/iu.lBrZmuBZyK','覃',NULL,NULL,NULL,'STUDENT',NULL,1,'2026-08-07 14:41:44','2026-08-07 14:46:18'),(10,'teacher01','$2a$10$NHeNdW3jAbDRNTvMtB2VLul8roCiK8U7839aATmm7T.FvezoPoF.C','张老师',NULL,NULL,NULL,'TEACHER',1,1,'2026-08-07 15:02:34','2026-08-07 15:02:34'),(11,'student01','$2a$10$B6Clt9Cl/pIldTqr0MX1T.7w.4mC0uvPtqTF62Ojq6EclCRuGi2Ay','李同学',NULL,NULL,NULL,'STUDENT',1,1,'2026-08-07 15:02:35','2026-08-07 15:02:35');
/*!40000 ALTER TABLE `sys_user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

