/*
Navicat MySQL Data Transfer

Source Server         : localhost_3306
Source Server Version : 80029
Source Host           : localhost:3306
Source Database       : employment_tracking

Target Server Type    : MYSQL
Target Server Version : 80029
File Encoding         : 65001

Date: 2026-08-28 09:51:33
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `attachment`
-- ----------------------------
DROP TABLE IF EXISTS `attachment`;
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
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='附件表';

-- ----------------------------
-- Records of attachment
-- ----------------------------
INSERT INTO `attachment` VALUES ('1', '0', '5fc1c49915153dbaee78f8d8814cd9cc_1.jpg', '20260825/8655e0f57b3142fcb07423384f4a87a6.jpg', '109138', 'image/jpeg', '2026-08-25 09:51:10');
INSERT INTO `attachment` VALUES ('2', '0', '5fc1c49915153dbaee78f8d8814cd9cc_1.jpg', '20260826/8c7784e25ffa49c7bb2e37e72a5bdeb0.jpg', '109138', 'image/jpeg', '2026-08-26 14:28:49');

-- ----------------------------
-- Table structure for `audit_log`
-- ----------------------------
DROP TABLE IF EXISTS `audit_log`;
CREATE TABLE `audit_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `record_id` bigint NOT NULL COMMENT '就业记录ID',
  `action` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '操作: PASS/REJECT',
  `comment` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '审核意见',
  `operator_id` bigint NOT NULL COMMENT '操作人ID',
  `operator_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作人姓名',
  `operator_role` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作人角色',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_record_id` (`record_id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='审核日志表';

-- ----------------------------
-- Records of audit_log
-- ----------------------------
INSERT INTO `audit_log` VALUES ('1', '4', 'SUBMIT', '学生自主提交就业信息', '18', '任缘', 'GRADUATE', '2026-08-19 09:05:51');
INSERT INTO `audit_log` VALUES ('2', '4', 'FIRST_PASS', '教师初审通过', '3', '张老师', 'TEACHER', '2026-08-19 09:07:00');
INSERT INTO `audit_log` VALUES ('3', '4', 'FINAL_PASS', '管理员终审通过', '17', '王主任', 'COLLEGE_ADMIN', '2026-08-19 09:07:24');
INSERT INTO `audit_log` VALUES ('4', '63', 'SUBMIT', '学生自主提交就业信息', '15', '赵鑫鑫', 'GRADUATE', '2026-08-19 10:18:50');
INSERT INTO `audit_log` VALUES ('5', '63', 'FIRST_PASS', '教师初审通过', '3', '张老师', 'TEACHER', '2026-08-19 10:20:16');
INSERT INTO `audit_log` VALUES ('6', '63', 'FINAL_PASS', '管理员终审通过', '17', '王主任', 'COLLEGE_ADMIN', '2026-08-19 10:22:34');
INSERT INTO `audit_log` VALUES ('7', '64', 'SUBMIT', '学生自主提交就业信息', '19', '李天悦', 'GRADUATE', '2026-08-25 09:50:47');
INSERT INTO `audit_log` VALUES ('8', '64', 'FIRST_REJECT', '教师初审驳回: 没有薪资', '10', '张老师', 'TEACHER', '2026-08-25 09:52:10');
INSERT INTO `audit_log` VALUES ('9', '64', 'UPDATE', '学生修改后重新提交就业信息', '19', '李天悦', 'GRADUATE', '2026-08-25 09:53:03');
INSERT INTO `audit_log` VALUES ('10', '64', 'FIRST_PASS', '教师初审通过', '10', '张老师', 'TEACHER', '2026-08-25 09:53:39');
INSERT INTO `audit_log` VALUES ('11', '64', 'FINAL_PASS', '管理员终审通过', '17', '王主任', 'COLLEGE_ADMIN', '2026-08-25 09:54:03');

-- ----------------------------
-- Table structure for `class_info`
-- ----------------------------
DROP TABLE IF EXISTS `class_info`;
CREATE TABLE `class_info` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '班级名称',
  `major_id` bigint NOT NULL COMMENT '所属专业ID',
  `grade` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '年级(如2022)',
  `sort_order` int DEFAULT '0' COMMENT '排序号',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态',
  `enrollment_year` int DEFAULT NULL,
  `graduation_year` int DEFAULT NULL,
  `description` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `deleted` tinyint DEFAULT '0',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_major_id` (`major_id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='班级表';

-- ----------------------------
-- Records of class_info
-- ----------------------------
INSERT INTO `class_info` VALUES ('1', '计算机2201班', '1', '2022', '1', '1', '2022', '2026', null, '0', '2026-08-19 10:09:46', '2026-08-19 10:09:46');
INSERT INTO `class_info` VALUES ('2', '计算机2202班', '1', '2022', '2', '1', '2022', '2026', null, '0', '2026-08-19 10:09:46', '2026-08-19 10:09:46');
INSERT INTO `class_info` VALUES ('3', '软件工程2201班', '2', '2022', '3', '1', '2022', '2026', null, '0', '2026-08-19 10:09:46', '2026-08-19 10:09:46');
INSERT INTO `class_info` VALUES ('4', '物联网工程2201班', '3', '2022', '4', '1', '2022', '2026', null, '0', '2026-08-19 10:09:46', '2026-08-19 10:09:46');
INSERT INTO `class_info` VALUES ('5', '电子信息工程2201班', '4', '2022', '5', '1', '2022', '2026', null, '0', '2026-08-19 10:09:46', '2026-08-19 10:09:46');
INSERT INTO `class_info` VALUES ('6', '电子信息工程2202班', '4', '2022', '6', '1', '2022', '2026', null, '0', '2026-08-19 10:09:46', '2026-08-19 10:09:46');
INSERT INTO `class_info` VALUES ('7', '通信工程2201班', '5', '2022', '7', '1', '2022', '2026', null, '0', '2026-08-19 10:09:46', '2026-08-19 10:09:46');
INSERT INTO `class_info` VALUES ('8', '机械设计制造2201班', '6', '2022', '8', '1', '2022', '2026', null, '0', '2026-08-19 10:09:46', '2026-08-19 10:09:46');
INSERT INTO `class_info` VALUES ('9', '机械设计制造2202班', '6', '2022', '9', '1', '2022', '2026', null, '0', '2026-08-19 10:09:46', '2026-08-19 10:09:46');
INSERT INTO `class_info` VALUES ('10', '车辆工程2201班', '7', '2022', '10', '1', '2022', '2026', null, '0', '2026-08-19 10:09:46', '2026-08-19 10:09:46');
INSERT INTO `class_info` VALUES ('11', '工商管理2201班', '8', '2022', '11', '1', '2022', '2026', null, '0', '2026-08-19 10:09:46', '2026-08-19 10:09:46');
INSERT INTO `class_info` VALUES ('12', '会计学2201班', '9', '2022', '12', '1', '2022', '2026', null, '0', '2026-08-19 10:09:46', '2026-08-19 10:09:46');
INSERT INTO `class_info` VALUES ('13', '法学2201班', '10', '2022', '13', '1', '2022', '2026', null, '0', '2026-08-19 10:09:46', '2026-08-19 10:09:46');
INSERT INTO `class_info` VALUES ('14', '视觉传达设计2201班', '11', '2022', '14', '1', '2022', '2026', null, '0', '2026-08-19 10:09:46', '2026-08-19 10:09:46');
INSERT INTO `class_info` VALUES ('15', '数学与应用数学2201班', '12', '2022', '15', '1', '2022', '2026', null, '0', '2026-08-19 10:09:46', '2026-08-19 10:09:46');
INSERT INTO `class_info` VALUES ('16', '数学2202班', '12', '2022', '0', '1', '2022', '2025', '', '0', '2026-08-26 10:01:54', '2026-08-26 10:01:54');

-- ----------------------------
-- Table structure for `department`
-- ----------------------------
DROP TABLE IF EXISTS `department`;
CREATE TABLE `department` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '院系名称',
  `code` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '院系编码',
  `sort_order` int DEFAULT '0' COMMENT '排序号',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态',
  `description` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `deleted` tinyint DEFAULT '0',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='院系表';

-- ----------------------------
-- Records of department
-- ----------------------------
INSERT INTO `department` VALUES ('1', '计算机科学与技术学院', 'CS', '1', '1', null, '0', '2026-08-07 10:36:22', '2026-08-07 10:36:22');
INSERT INTO `department` VALUES ('2', '电子信息工程学院', 'EE', '2', '1', null, '0', '2026-08-07 10:36:22', '2026-08-07 10:36:22');
INSERT INTO `department` VALUES ('3', '机械工程学院', 'ME', '3', '1', null, '0', '2026-08-07 10:36:22', '2026-08-07 10:36:22');
INSERT INTO `department` VALUES ('4', '经济管理学院', 'EM', '4', '1', null, '0', '2026-08-07 10:36:22', '2026-08-07 10:36:22');
INSERT INTO `department` VALUES ('5', '文法学院', 'LA', '5', '1', null, '0', '2026-08-07 10:36:22', '2026-08-07 10:36:22');
INSERT INTO `department` VALUES ('6', '艺术设计学院', 'AD', '6', '1', null, '0', '2026-08-07 10:36:22', '2026-08-07 10:36:22');
INSERT INTO `department` VALUES ('7', '理学院', 'SC', '7', '1', null, '0', '2026-08-07 10:36:22', '2026-08-07 10:36:22');

-- ----------------------------
-- Table structure for `dict_item`
-- ----------------------------
DROP TABLE IF EXISTS `dict_item`;
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

-- ----------------------------
-- Records of dict_item
-- ----------------------------
INSERT INTO `dict_item` VALUES ('1', '1', '已就业', 'EMPLOYED', '1', '1', '2026-08-07 10:36:22', '2026-08-07 10:36:22');
INSERT INTO `dict_item` VALUES ('2', '1', '未就业', 'UNEMPLOYED', '2', '1', '2026-08-07 10:36:22', '2026-08-07 10:36:22');
INSERT INTO `dict_item` VALUES ('3', '1', '升学', 'FURTHER_STUDY', '3', '1', '2026-08-07 10:36:22', '2026-08-07 10:36:22');
INSERT INTO `dict_item` VALUES ('4', '1', '入伍', 'MILITARY', '4', '1', '2026-08-07 10:36:22', '2026-08-07 10:36:22');
INSERT INTO `dict_item` VALUES ('5', '1', '创业', 'ENTREPRENEURSHIP', '5', '1', '2026-08-07 10:36:22', '2026-08-07 10:36:22');
INSERT INTO `dict_item` VALUES ('6', '1', '待业', 'WAITING', '6', '1', '2026-08-07 10:36:22', '2026-08-07 10:36:22');
INSERT INTO `dict_item` VALUES ('7', '2', '待审核', 'PENDING', '1', '1', '2026-08-07 10:36:22', '2026-08-07 10:36:22');
INSERT INTO `dict_item` VALUES ('8', '2', '审核通过', 'PASSED', '2', '1', '2026-08-07 10:36:22', '2026-08-07 10:36:22');
INSERT INTO `dict_item` VALUES ('9', '2', '审核退回', 'REJECTED', '3', '1', '2026-08-07 10:36:22', '2026-08-07 10:36:22');
INSERT INTO `dict_item` VALUES ('10', '5', '签约就业', 'SIGNED', '1', '1', '2026-08-07 10:36:22', '2026-08-07 10:36:22');
INSERT INTO `dict_item` VALUES ('11', '5', '升学深造', 'FURTHER_STUDY', '2', '1', '2026-08-07 10:36:22', '2026-08-07 10:36:22');
INSERT INTO `dict_item` VALUES ('12', '5', '出国留学', 'ABROAD', '3', '1', '2026-08-07 10:36:22', '2026-08-07 10:36:22');
INSERT INTO `dict_item` VALUES ('13', '5', '自主创业', 'ENTREPRENEURSHIP', '4', '1', '2026-08-07 10:36:22', '2026-08-07 10:36:22');
INSERT INTO `dict_item` VALUES ('14', '5', '灵活就业', 'FLEXIBLE', '5', '1', '2026-08-07 10:36:22', '2026-08-07 10:36:22');
INSERT INTO `dict_item` VALUES ('15', '5', '待就业', 'WAITING', '6', '1', '2026-08-07 10:36:22', '2026-08-07 10:36:22');
INSERT INTO `dict_item` VALUES ('16', '3', '国有企业', 'STATE_OWNED', '1', '1', '2026-08-07 10:36:22', '2026-08-07 10:36:22');
INSERT INTO `dict_item` VALUES ('17', '3', '民营企业', 'PRIVATE', '2', '1', '2026-08-07 10:36:22', '2026-08-07 10:36:22');
INSERT INTO `dict_item` VALUES ('18', '3', '外资企业', 'FOREIGN', '3', '1', '2026-08-07 10:36:22', '2026-08-07 10:36:22');
INSERT INTO `dict_item` VALUES ('19', '3', '事业单位', 'INSTITUTION', '4', '1', '2026-08-07 10:36:22', '2026-08-07 10:36:22');
INSERT INTO `dict_item` VALUES ('20', '3', '政府机关', 'GOVERNMENT', '5', '1', '2026-08-07 10:36:22', '2026-08-07 10:36:22');
INSERT INTO `dict_item` VALUES ('21', '4', '互联网/IT', 'IT', '1', '1', '2026-08-07 10:36:22', '2026-08-07 10:36:22');
INSERT INTO `dict_item` VALUES ('22', '4', '金融', 'FINANCE', '2', '1', '2026-08-07 10:36:22', '2026-08-07 10:36:22');
INSERT INTO `dict_item` VALUES ('23', '4', '教育', 'EDUCATION', '3', '1', '2026-08-07 10:36:22', '2026-08-07 10:36:22');
INSERT INTO `dict_item` VALUES ('24', '4', '制造业', 'MANUFACTURING', '4', '1', '2026-08-07 10:36:22', '2026-08-07 10:36:22');
INSERT INTO `dict_item` VALUES ('25', '4', '医疗健康', 'HEALTHCARE', '5', '1', '2026-08-07 10:36:22', '2026-08-07 10:36:22');
INSERT INTO `dict_item` VALUES ('26', '4', '房地产/建筑', 'REAL_ESTATE', '6', '1', '2026-08-07 10:36:22', '2026-08-07 10:36:22');
INSERT INTO `dict_item` VALUES ('27', '1', '已就业', 'EMPLOYED', '1', '1', '2026-08-07 10:59:59', '2026-08-07 10:59:59');
INSERT INTO `dict_item` VALUES ('28', '1', '未就业', 'UNEMPLOYED', '2', '1', '2026-08-07 10:59:59', '2026-08-07 10:59:59');
INSERT INTO `dict_item` VALUES ('29', '1', '升学', 'FURTHER_STUDY', '3', '1', '2026-08-07 10:59:59', '2026-08-07 10:59:59');
INSERT INTO `dict_item` VALUES ('30', '1', '入伍', 'MILITARY', '4', '1', '2026-08-07 10:59:59', '2026-08-07 10:59:59');
INSERT INTO `dict_item` VALUES ('31', '1', '创业', 'ENTREPRENEURSHIP', '5', '1', '2026-08-07 10:59:59', '2026-08-07 10:59:59');
INSERT INTO `dict_item` VALUES ('32', '1', '待业', 'WAITING', '6', '1', '2026-08-07 10:59:59', '2026-08-07 10:59:59');
INSERT INTO `dict_item` VALUES ('33', '2', '待审核', 'PENDING', '1', '1', '2026-08-07 10:59:59', '2026-08-07 10:59:59');
INSERT INTO `dict_item` VALUES ('34', '2', '审核通过', 'PASSED', '2', '1', '2026-08-07 10:59:59', '2026-08-07 10:59:59');
INSERT INTO `dict_item` VALUES ('35', '2', '审核退回', 'REJECTED', '3', '1', '2026-08-07 10:59:59', '2026-08-07 10:59:59');
INSERT INTO `dict_item` VALUES ('36', '3', '国有企业', 'STATE_OWNED', '1', '1', '2026-08-07 10:59:59', '2026-08-07 10:59:59');
INSERT INTO `dict_item` VALUES ('37', '3', '民营企业', 'PRIVATE', '2', '1', '2026-08-07 10:59:59', '2026-08-07 10:59:59');
INSERT INTO `dict_item` VALUES ('38', '3', '外资企业', 'FOREIGN', '3', '1', '2026-08-07 10:59:59', '2026-08-07 10:59:59');
INSERT INTO `dict_item` VALUES ('39', '3', '事业单位', 'INSTITUTION', '4', '1', '2026-08-07 10:59:59', '2026-08-07 10:59:59');
INSERT INTO `dict_item` VALUES ('40', '3', '政府机关', 'GOVERNMENT', '5', '1', '2026-08-07 10:59:59', '2026-08-07 10:59:59');
INSERT INTO `dict_item` VALUES ('41', '4', '互联网/IT', 'IT', '1', '1', '2026-08-07 10:59:59', '2026-08-07 10:59:59');
INSERT INTO `dict_item` VALUES ('42', '4', '金融', 'FINANCE', '2', '1', '2026-08-07 10:59:59', '2026-08-07 10:59:59');
INSERT INTO `dict_item` VALUES ('43', '4', '教育', 'EDUCATION', '3', '1', '2026-08-07 10:59:59', '2026-08-07 10:59:59');
INSERT INTO `dict_item` VALUES ('44', '4', '制造业', 'MANUFACTURING', '4', '1', '2026-08-07 10:59:59', '2026-08-07 10:59:59');
INSERT INTO `dict_item` VALUES ('45', '4', '医疗健康', 'HEALTHCARE', '5', '1', '2026-08-07 10:59:59', '2026-08-07 10:59:59');
INSERT INTO `dict_item` VALUES ('46', '4', '房地产/建筑', 'REAL_ESTATE', '6', '1', '2026-08-07 10:59:59', '2026-08-07 10:59:59');
INSERT INTO `dict_item` VALUES ('47', '5', '签约就业', 'SIGNED', '1', '1', '2026-08-07 10:59:59', '2026-08-07 10:59:59');
INSERT INTO `dict_item` VALUES ('48', '5', '升学深造', 'FURTHER_STUDY', '2', '1', '2026-08-07 10:59:59', '2026-08-07 10:59:59');
INSERT INTO `dict_item` VALUES ('49', '5', '出国留学', 'ABROAD', '3', '1', '2026-08-07 10:59:59', '2026-08-07 10:59:59');
INSERT INTO `dict_item` VALUES ('50', '5', '自主创业', 'ENTREPRENEURSHIP', '4', '1', '2026-08-07 10:59:59', '2026-08-07 10:59:59');
INSERT INTO `dict_item` VALUES ('51', '5', '灵活就业', 'FLEXIBLE', '5', '1', '2026-08-07 10:59:59', '2026-08-07 10:59:59');
INSERT INTO `dict_item` VALUES ('52', '5', '待就业', 'WAITING', '6', '1', '2026-08-07 10:59:59', '2026-08-07 10:59:59');

-- ----------------------------
-- Table structure for `dict_type`
-- ----------------------------
DROP TABLE IF EXISTS `dict_type`;
CREATE TABLE `dict_type` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '字典编码',
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '字典名称',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字典类型表';

-- ----------------------------
-- Records of dict_type
-- ----------------------------
INSERT INTO `dict_type` VALUES ('1', 'employment_status', '就业状态', '1', '2026-08-07 10:36:22', '2026-08-07 10:36:22');
INSERT INTO `dict_type` VALUES ('2', 'review_status', '审核状态', '1', '2026-08-07 10:36:22', '2026-08-07 10:36:22');
INSERT INTO `dict_type` VALUES ('3', 'company_type', '单位性质', '1', '2026-08-07 10:36:22', '2026-08-07 10:36:22');
INSERT INTO `dict_type` VALUES ('4', 'industry', '所属行业', '1', '2026-08-07 10:36:22', '2026-08-07 10:36:22');
INSERT INTO `dict_type` VALUES ('5', 'destination', '去向类型', '1', '2026-08-07 10:36:22', '2026-08-07 10:36:22');

-- ----------------------------
-- Table structure for `employment_record`
-- ----------------------------
DROP TABLE IF EXISTS `employment_record`;
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
  `is_proxy` tinyint DEFAULT '0',
  `submitter_id` bigint DEFAULT NULL,
  `submitter_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `evidence_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `stage` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `teacher_comment` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `admin_comment` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `teacher_review_time` datetime DEFAULT NULL,
  `admin_review_time` datetime DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_graduate_id` (`graduate_id`),
  KEY `idx_review_status` (`review_status`),
  KEY `idx_destination` (`destination`)
) ENGINE=InnoDB AUTO_INCREMENT=65 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='就业记录表';

-- ----------------------------
-- Records of employment_record
-- ----------------------------
INSERT INTO `employment_record` VALUES ('4', '4', '字节跳动', '民营企业', '互联网/IT/软件', '软件工程师', '12000-20000', '', '签约就业', 'APPROVED', '', '17', '2026-08-19 09:07:24', '0', '18', '任缘', null, 'COMPLETED', '已通过', '已通过', '2026-08-19 09:05:50', '2026-08-25 10:03:52', '2026-08-19 09:05:50', '2026-08-25 10:03:52');
INSERT INTO `employment_record` VALUES ('5', '5', '美团', '民营企业', '互联网/IT/软件', '测试工程师', '12000-20000', '重庆', '签约就业', 'APPROVED', '就业信息真实有效，符合要求', '17', '2025-10-20 14:02:00', '0', '20', '张伟', null, 'COMPLETED', '同意提交', '审核通过', '2025-10-20 14:02:00', '2025-10-20 14:02:00', '2026-08-19 10:00:00', '2026-08-25 10:03:52');
INSERT INTO `employment_record` VALUES ('6', '6', '腾讯', '民营企业', '互联网/IT/软件', '后端工程师', '8000-12000', '杭州', '签约就业', 'APPROVED', '就业信息真实有效，符合要求', '17', '2025-10-06 09:33:00', '0', '21', '王芳', null, 'COMPLETED', '同意提交', '审核通过', '2025-10-06 09:33:00', '2025-10-06 09:33:00', '2026-08-19 10:00:00', '2026-08-25 10:03:52');
INSERT INTO `employment_record` VALUES ('7', '7', '大疆创新', '民营企业', '互联网/IT/软件', '软件工程师', '20000-30000', '南京', '签约就业', 'APPROVED', '就业信息真实有效，符合要求', '17', '2025-10-22 11:41:00', '0', '22', '李娜', null, 'COMPLETED', '同意提交', '审核通过', '2025-10-22 11:41:00', '2025-10-22 11:41:00', '2026-08-19 10:00:00', '2026-08-25 10:03:52');
INSERT INTO `employment_record` VALUES ('8', '8', '美团', '民营企业', '互联网/IT/软件', '测试工程师', '8000-12000', '上海', '签约就业', 'APPROVED', '就业信息真实有效，符合要求', '17', '2025-10-24 11:09:00', '0', '23', '刘洋', null, 'COMPLETED', '同意提交', '审核通过', '2025-10-24 11:09:00', '2025-10-24 11:09:00', '2026-08-19 10:00:00', '2026-08-25 10:03:52');
INSERT INTO `employment_record` VALUES ('9', '9', '京东', '民营企业', '互联网/IT/软件', '数据工程师', '8000-12000', '西安', '签约就业', 'APPROVED', '就业信息真实有效，符合要求', '17', '2025-11-15 09:01:00', '0', '24', '陈静', null, 'COMPLETED', '同意提交', '审核通过', '2025-11-15 09:01:00', '2025-11-15 09:01:00', '2026-08-19 10:00:00', '2026-08-25 10:03:52');
INSERT INTO `employment_record` VALUES ('10', '10', '中国移动', '国有企业', '互联网/IT/软件', '网络工程师', '30000以上', '西安', '签约就业', 'APPROVED', '就业信息真实有效，符合要求', '17', '2025-11-23 12:08:00', '0', '25', '杨帆', null, 'COMPLETED', '同意提交', '审核通过', '2025-11-23 12:08:00', '2025-11-23 12:08:00', '2026-08-19 10:00:00', '2026-08-25 10:03:52');
INSERT INTO `employment_record` VALUES ('11', '11', '京东', '民营企业', '互联网/IT/软件', '数据工程师', '20000-30000', '上海', '签约就业', 'APPROVED', '就业信息真实有效，符合要求', '17', '2025-11-22 12:15:00', '0', '26', '赵磊', null, 'COMPLETED', '同意提交', '审核通过', '2025-11-22 12:15:00', '2025-11-22 12:15:00', '2026-08-19 10:00:00', '2026-08-25 10:03:52');
INSERT INTO `employment_record` VALUES ('12', '12', '腾讯', '民营企业', '互联网/IT/软件', '后端工程师', '30000以上', '武汉', '签约就业', 'APPROVED', '就业信息真实有效，符合要求', '17', '2025-11-18 13:33:00', '0', '27', '黄敏', null, 'COMPLETED', '同意提交', '审核通过', '2025-11-18 13:33:00', '2025-11-18 13:33:00', '2026-08-19 10:00:00', '2026-08-25 10:03:52');
INSERT INTO `employment_record` VALUES ('13', '13', '网易', '民营企业', '互联网/IT/软件', '游戏开发工程师', '12000-20000', '成都', '签约就业', 'APPROVED', '就业信息真实有效，符合要求', '17', '2025-11-04 12:45:00', '0', '28', '周涛', null, 'COMPLETED', '同意提交', '审核通过', '2025-11-04 12:45:00', '2025-11-04 12:45:00', '2026-08-19 10:00:00', '2026-08-25 10:03:52');
INSERT INTO `employment_record` VALUES ('14', '14', '京东', '民营企业', '互联网/IT/软件', '测试开发工程师', '20000-30000', '苏州', '签约就业', 'APPROVED', '就业信息真实有效，符合要求', '17', '2025-12-03 13:03:00', '0', '29', '吴雪', null, 'COMPLETED', '同意提交', '审核通过', '2025-12-03 13:03:00', '2025-12-03 13:03:00', '2026-08-19 10:00:00', '2026-08-25 10:03:52');
INSERT INTO `employment_record` VALUES ('15', '15', '腾讯', '民营企业', '互联网/IT/软件', '前端工程师', '8000-12000', '成都', '签约就业', 'APPROVED', '就业信息真实有效，符合要求', '17', '2025-12-08 12:43:00', '0', '30', '徐强', null, 'COMPLETED', '同意提交', '审核通过', '2025-12-08 12:43:00', '2025-12-08 12:43:00', '2026-08-19 10:00:00', '2026-08-25 10:03:52');
INSERT INTO `employment_record` VALUES ('16', '16', '快手', '民营企业', '互联网/IT/软件', '算法工程师', '12000-20000', '西安', '签约就业', 'APPROVED', '就业信息真实有效，符合要求', '17', '2025-12-07 12:36:00', '0', '31', '孙丽', null, 'COMPLETED', '同意提交', '审核通过', '2025-12-07 12:36:00', '2025-12-07 12:36:00', '2026-08-19 10:00:00', '2026-08-25 10:03:52');
INSERT INTO `employment_record` VALUES ('17', '17', '中国电信', '国有企业', '互联网/IT/软件', '物联网工程师', '30000以上', '南京', '签约就业', 'APPROVED', '就业信息真实有效，符合要求', '17', '2025-12-23 12:53:00', '0', '32', '马超', null, 'COMPLETED', '同意提交', '审核通过', '2025-12-23 12:53:00', '2025-12-23 12:53:00', '2026-08-19 10:00:00', '2026-08-25 10:03:52');
INSERT INTO `employment_record` VALUES ('18', '18', '大疆创新', '民营企业', '互联网/IT/软件', '嵌入式软件工程师', '12000-20000', '北京', '签约就业', 'APPROVED', '就业信息真实有效，符合要求', '17', '2025-12-05 16:50:00', '0', '33', '朱婷', null, 'COMPLETED', '同意提交', '审核通过', '2025-12-05 16:50:00', '2025-12-05 16:50:00', '2026-08-19 10:00:00', '2026-08-25 10:03:52');
INSERT INTO `employment_record` VALUES ('19', '19', '中国电信', '国有企业', '互联网/IT/软件', '物联网工程师', '30000以上', '西安', '签约就业', 'APPROVED', '就业信息真实有效，符合要求', '17', '2026-01-24 15:56:00', '0', '34', '胡军', null, 'COMPLETED', '同意提交', '审核通过', '2026-01-24 15:56:00', '2026-01-24 15:56:00', '2026-08-19 10:00:00', '2026-08-25 10:03:52');
INSERT INTO `employment_record` VALUES ('20', '20', '中兴通讯', '国有企业', '电子通信', '通信工程师', '12000-20000', '西安', '签约就业', 'APPROVED', '就业信息真实有效，符合要求', '17', '2026-01-11 11:37:00', '0', '35', '郭靖', null, 'COMPLETED', '同意提交', '审核通过', '2026-01-11 11:37:00', '2026-01-11 11:37:00', '2026-08-19 10:00:00', '2026-08-25 10:03:52');
INSERT INTO `employment_record` VALUES ('21', '21', '歌尔股份', '民营企业', '制造业', '声学工程师', '30000以上', '广州', '签约就业', 'APPROVED', '就业信息真实有效，符合要求', '17', '2026-01-22 13:05:00', '0', '36', '何雨', null, 'COMPLETED', '同意提交', '审核通过', '2026-01-22 13:05:00', '2026-01-22 13:05:00', '2026-08-19 10:00:00', '2026-08-25 10:03:52');
INSERT INTO `employment_record` VALUES ('22', '22', '海康威视', '民营企业', '互联网/IT/软件', '算法工程师', '20000-30000', '西安', '签约就业', 'APPROVED', '就业信息真实有效，符合要求', '17', '2026-01-04 13:48:00', '0', '37', '高翔', null, 'COMPLETED', '同意提交', '审核通过', '2026-01-04 13:48:00', '2026-01-04 13:48:00', '2026-08-19 10:00:00', '2026-08-25 10:03:52');
INSERT INTO `employment_record` VALUES ('23', '23', '中兴通讯', '国有企业', '电子通信', '通信工程师', '20000-30000', '南京', '签约就业', 'APPROVED', '就业信息真实有效，符合要求', '17', '2026-01-03 13:35:00', '0', '38', '林晓', null, 'COMPLETED', '同意提交', '审核通过', '2026-01-03 13:35:00', '2026-01-03 13:35:00', '2026-08-19 10:00:00', '2026-08-25 10:03:52');
INSERT INTO `employment_record` VALUES ('24', '24', '歌尔股份', '民营企业', '制造业', '声学工程师', '30000以上', '南京', '签约就业', 'APPROVED', '就业信息真实有效，符合要求', '17', '2026-01-26 14:54:00', '0', '39', '罗晨', null, 'COMPLETED', '同意提交', '审核通过', '2026-01-26 14:54:00', '2026-01-26 14:54:00', '2026-08-19 10:00:00', '2026-08-25 10:03:52');
INSERT INTO `employment_record` VALUES ('25', '25', '海康威视', '民营企业', '互联网/IT/软件', '算法工程师', '20000-30000', '杭州', '签约就业', 'APPROVED', '就业信息真实有效，符合要求', '17', '2026-02-16 10:31:00', '0', '40', '郑浩', null, 'COMPLETED', '同意提交', '审核通过', '2026-02-16 10:31:00', '2026-02-16 10:31:00', '2026-08-19 10:00:00', '2026-08-25 10:03:52');
INSERT INTO `employment_record` VALUES ('26', '26', '歌尔股份', '民营企业', '制造业', '声学工程师', '8000-12000', '上海', '签约就业', 'APPROVED', '就业信息真实有效，符合要求', '17', '2026-02-22 09:38:00', '0', '41', '梁宇', null, 'COMPLETED', '同意提交', '审核通过', '2026-02-22 09:38:00', '2026-02-22 09:38:00', '2026-08-19 10:00:00', '2026-08-25 10:03:52');
INSERT INTO `employment_record` VALUES ('27', '27', '中国电子科技集团', '国有企业', '电子通信', '电子工程师', '30000以上', '北京', '签约就业', 'APPROVED', '就业信息真实有效，符合要求', '17', '2026-02-19 15:33:00', '0', '42', '谢飞', null, 'COMPLETED', '同意提交', '审核通过', '2026-02-19 15:33:00', '2026-02-19 15:33:00', '2026-08-19 10:00:00', '2026-08-25 10:03:52');
INSERT INTO `employment_record` VALUES ('28', '28', '中兴通讯', '国有企业', '电子通信', '无线工程师', '20000-30000', '杭州', '签约就业', 'APPROVED', '就业信息真实有效，符合要求', '17', '2026-02-06 09:15:00', '0', '43', '宋佳', null, 'COMPLETED', '同意提交', '审核通过', '2026-02-06 09:15:00', '2026-02-06 09:15:00', '2026-08-19 10:00:00', '2026-08-25 10:03:52');
INSERT INTO `employment_record` VALUES ('29', '29', '华为', '民营企业', '电子通信', '5G通信工程师', '20000-30000', '长沙', '签约就业', 'APPROVED', '就业信息真实有效，符合要求', '17', '2026-02-02 14:38:00', '0', '44', '唐毅', null, 'COMPLETED', '同意提交', '审核通过', '2026-02-02 14:38:00', '2026-02-02 14:38:00', '2026-08-19 10:00:00', '2026-08-25 10:03:52');
INSERT INTO `employment_record` VALUES ('30', '30', '中兴通讯', '国有企业', '电子通信', '无线工程师', '20000-30000', '西安', '签约就业', 'APPROVED', '就业信息真实有效，符合要求', '17', '2026-02-09 10:55:00', '0', '45', '韩雪', null, 'COMPLETED', '同意提交', '审核通过', '2026-02-09 10:55:00', '2026-02-09 10:55:00', '2026-08-19 10:00:00', '2026-08-25 10:03:52');
INSERT INTO `employment_record` VALUES ('31', '31', '比亚迪', '民营企业', '制造业', '结构工程师', '12000-20000', '杭州', '签约就业', 'APPROVED', '就业信息真实有效，符合要求', '17', '2026-03-04 09:21:00', '0', '46', '冯刚', null, 'COMPLETED', '同意提交', '审核通过', '2026-03-04 09:21:00', '2026-03-04 09:21:00', '2026-08-19 10:00:00', '2026-08-25 10:03:52');
INSERT INTO `employment_record` VALUES ('32', '32', '美的集团', '民营企业', '制造业', '产品工程师', '8000-12000', '重庆', '签约就业', 'APPROVED', '就业信息真实有效，符合要求', '17', '2026-03-03 14:32:00', '0', '47', '董倩', null, 'COMPLETED', '同意提交', '审核通过', '2026-03-03 14:32:00', '2026-03-03 14:32:00', '2026-08-19 10:00:00', '2026-08-25 10:03:52');
INSERT INTO `employment_record` VALUES ('33', '33', '美的集团', '民营企业', '制造业', '产品工程师', '30000以上', '上海', '签约就业', 'APPROVED', '就业信息真实有效，符合要求', '17', '2026-03-23 15:48:00', '0', '48', '萧然', null, 'COMPLETED', '同意提交', '审核通过', '2026-03-23 15:48:00', '2026-03-23 15:48:00', '2026-08-19 10:00:00', '2026-08-25 10:03:52');
INSERT INTO `employment_record` VALUES ('34', '34', '中国中车', '国有企业', '制造业', '机械设计工程师', '8000-12000', '苏州', '签约就业', 'APPROVED', '就业信息真实有效，符合要求', '17', '2026-03-21 12:18:00', '0', '49', '程亮', null, 'COMPLETED', '同意提交', '审核通过', '2026-03-21 12:18:00', '2026-03-21 12:18:00', '2026-08-19 10:00:00', '2026-08-25 10:03:52');
INSERT INTO `employment_record` VALUES ('35', '35', '美的集团', '民营企业', '制造业', '产品工程师', '8000-12000', '长沙', '签约就业', 'APPROVED', '就业信息真实有效，符合要求', '17', '2026-03-14 14:08:00', '0', '50', '曹阳', null, 'COMPLETED', '同意提交', '审核通过', '2026-03-14 14:08:00', '2026-03-14 14:08:00', '2026-08-19 10:00:00', '2026-08-25 10:03:52');
INSERT INTO `employment_record` VALUES ('36', '36', '格力电器', '国有企业', '制造业', '工艺工程师', '8000-12000', '武汉', '签约就业', 'APPROVED', '就业信息真实有效，符合要求', '17', '2026-03-07 16:56:00', '0', '51', '袁媛', null, 'COMPLETED', '同意提交', '审核通过', '2026-03-07 16:56:00', '2026-03-07 16:56:00', '2026-08-19 10:00:00', '2026-08-25 10:03:52');
INSERT INTO `employment_record` VALUES ('37', '37', '中国中车', '国有企业', '制造业', '机械设计工程师', '12000-20000', '西安', '签约就业', 'APPROVED', '就业信息真实有效，符合要求', '17', '2026-04-02 16:50:00', '0', '52', '邓超', null, 'COMPLETED', '同意提交', '审核通过', '2026-04-02 16:50:00', '2026-04-02 16:50:00', '2026-08-19 10:00:00', '2026-08-25 10:03:52');
INSERT INTO `employment_record` VALUES ('38', '38', '蔚来汽车', '民营企业', '制造业', '电控工程师', '30000以上', '上海', '签约就业', 'APPROVED', '就业信息真实有效，符合要求', '17', '2026-04-06 12:46:00', '0', '53', '许晴', null, 'COMPLETED', '同意提交', '审核通过', '2026-04-06 12:46:00', '2026-04-06 12:46:00', '2026-08-19 10:00:00', '2026-08-25 10:03:52');
INSERT INTO `employment_record` VALUES ('39', '39', '一汽大众', '国有企业', '制造业', '整车工程师', '20000-30000', '广州', '签约就业', 'APPROVED', '就业信息真实有效，符合要求', '17', '2026-04-03 10:22:00', '0', '54', '傅敏', null, 'COMPLETED', '同意提交', '审核通过', '2026-04-03 10:22:00', '2026-04-03 10:22:00', '2026-08-19 10:00:00', '2026-08-25 10:03:52');
INSERT INTO `employment_record` VALUES ('40', '40', '比亚迪', '民营企业', '制造业', '车辆工程师', '30000以上', '南京', '签约就业', 'APPROVED', '就业信息真实有效，符合要求', '17', '2026-04-02 16:55:00', '0', '55', '沈静', null, 'COMPLETED', '同意提交', '审核通过', '2026-04-02 16:55:00', '2026-04-02 16:55:00', '2026-08-19 10:00:00', '2026-08-25 10:03:52');
INSERT INTO `employment_record` VALUES ('41', '41', '京东', '民营企业', '互联网/IT/软件', '市场专员', '20000-30000', '北京', '签约就业', 'APPROVED', '就业信息真实有效，符合要求', '17', '2026-04-08 16:45:00', '0', '56', '曾强', null, 'COMPLETED', '同意提交', '审核通过', '2026-04-08 16:45:00', '2026-04-08 16:45:00', '2026-08-19 10:00:00', '2026-08-25 10:03:52');
INSERT INTO `employment_record` VALUES ('42', '42', '中国工商银行', '国有企业', '金融', '信贷专员', '30000以上', '北京', '签约就业', 'APPROVED', '就业信息真实有效，符合要求', '17', '2026-04-21 09:59:00', '0', '57', '彭磊', null, 'COMPLETED', '同意提交', '审核通过', '2026-04-21 09:59:00', '2026-04-21 09:59:00', '2026-08-19 10:00:00', '2026-08-25 10:03:52');
INSERT INTO `employment_record` VALUES ('43', '43', '天健会计师事务所', '民营企业', '金融', '审计助理', '20000-30000', '杭州', '签约就业', 'APPROVED', '就业信息真实有效，符合要求', '17', '2026-04-03 13:31:00', '0', '58', '吕倩', null, 'COMPLETED', '同意提交', '审核通过', '2026-04-03 13:31:00', '2026-04-03 13:31:00', '2026-08-19 10:00:00', '2026-08-25 10:03:52');
INSERT INTO `employment_record` VALUES ('44', '44', '厦门大学', '事业单位', '教育', '硕士研究生', '', '深圳', '升学', 'APPROVED', '就业信息真实有效，符合要求', '17', '2026-05-09 09:50:00', '0', '59', '苏航', null, 'COMPLETED', '同意提交', '审核通过', '2026-05-09 09:50:00', '2026-05-09 09:50:00', '2026-08-19 10:00:00', '2026-08-25 10:03:52');
INSERT INTO `employment_record` VALUES ('45', '45', '中央财经大学', '事业单位', '教育', '硕士研究生', '', '重庆', '升学', 'APPROVED', '就业信息真实有效，符合要求', '17', '2026-05-26 16:03:00', '0', '60', '卢俊', null, 'COMPLETED', '同意提交', '审核通过', '2026-05-26 16:03:00', '2026-05-26 16:03:00', '2026-08-19 10:00:00', '2026-08-25 10:03:52');
INSERT INTO `employment_record` VALUES ('46', '46', '上海财经大学', '事业单位', '教育', '硕士研究生', '', '武汉', '升学', 'APPROVED', '就业信息真实有效，符合要求', '17', '2026-05-19 15:19:00', '0', '61', '蒋欣', null, 'COMPLETED', '同意提交', '审核通过', '2026-05-19 15:19:00', '2026-05-19 15:19:00', '2026-08-19 10:00:00', '2026-08-25 10:03:52');
INSERT INTO `employment_record` VALUES ('47', '47', '西南财经大学', '事业单位', '教育', '硕士研究生', '', '上海', '升学', 'APPROVED', '就业信息真实有效，符合要求', '17', '2026-05-13 13:10:00', '0', '62', '蔡宇', null, 'COMPLETED', '同意提交', '审核通过', '2026-05-13 13:10:00', '2026-05-13 13:10:00', '2026-08-19 10:00:00', '2026-08-25 10:03:52');
INSERT INTO `employment_record` VALUES ('48', '48', '上海财经大学', '事业单位', '教育', '硕士研究生', '', '北京', '升学', 'APPROVED', '就业信息真实有效，符合要求', '17', '2026-05-20 13:09:00', '0', '63', '贾琳', null, 'COMPLETED', '同意提交', '审核通过', '2026-05-20 13:09:00', '2026-05-20 13:09:00', '2026-08-19 10:00:00', '2026-08-25 10:03:52');
INSERT INTO `employment_record` VALUES ('49', '49', '西南财经大学', '事业单位', '教育', '硕士研究生', '', '武汉', '升学', 'APPROVED', '就业信息真实有效，符合要求', '17', '2026-05-20 10:24:00', '0', '64', '丁伟', null, 'COMPLETED', '同意提交', '审核通过', '2026-05-20 10:24:00', '2026-05-20 10:24:00', '2026-08-19 10:00:00', '2026-08-25 10:03:52');
INSERT INTO `employment_record` VALUES ('50', '50', '上海财经大学', '事业单位', '教育', '硕士研究生', '', '武汉', '升学', 'APPROVED', '就业信息真实有效，符合要求', '17', '2026-05-26 16:03:00', '0', '65', '魏然', null, 'COMPLETED', '同意提交', '审核通过', '2026-05-26 16:03:00', '2026-05-26 16:03:00', '2026-08-19 10:00:00', '2026-08-25 10:03:52');
INSERT INTO `employment_record` VALUES ('51', '51', '华东政法大学', '事业单位', '教育', '硕士研究生', '', '长沙', '升学', 'APPROVED', '就业信息真实有效，符合要求', '17', '2026-06-13 09:55:00', '0', '66', '薛峰', null, 'COMPLETED', '同意提交', '审核通过', '2026-06-13 09:55:00', '2026-06-13 09:55:00', '2026-08-19 10:00:00', '2026-08-25 10:03:52');
INSERT INTO `employment_record` VALUES ('52', '52', '华东政法大学', '事业单位', '教育', '硕士研究生', '', '上海', '升学', 'APPROVED', '就业信息真实有效，符合要求', '17', '2026-06-27 14:36:00', '0', '67', '叶静', null, 'COMPLETED', '同意提交', '审核通过', '2026-06-27 14:36:00', '2026-06-27 14:36:00', '2026-08-19 10:00:00', '2026-08-25 10:03:52');
INSERT INTO `employment_record` VALUES ('53', '53', '阎凯文化科技有限公司', '民营企业', '文化传媒', '创始人', '', '杭州', '创业', 'APPROVED', '就业信息真实有效，符合要求', '17', '2026-06-25 10:16:00', '0', '68', '阎凯', null, 'COMPLETED', '同意提交', '审核通过', '2026-06-25 10:16:00', '2026-06-25 10:16:00', '2026-08-19 10:00:00', '2026-08-25 10:03:52');
INSERT INTO `employment_record` VALUES ('54', '54', '余华文化科技有限公司', '民营企业', '文化传媒', '创始人', '', '杭州', '创业', 'APPROVED', '就业信息真实有效，符合要求', '17', '2026-06-06 09:55:00', '0', '69', '余华', null, 'COMPLETED', '同意提交', '审核通过', '2026-06-06 09:55:00', '2026-06-06 09:55:00', '2026-08-19 10:00:00', '2026-08-25 10:03:52');
INSERT INTO `employment_record` VALUES ('55', '55', '潘婷文化科技有限公司', '民营企业', '文化传媒', '创始人', '', '杭州', '创业', 'APPROVED', '就业信息真实有效，符合要求', '17', '2026-06-02 16:28:00', '0', '70', '潘婷', null, 'COMPLETED', '同意提交', '审核通过', '2026-06-02 16:28:00', '2026-06-02 16:28:00', '2026-08-19 10:00:00', '2026-08-25 10:03:52');
INSERT INTO `employment_record` VALUES ('56', '56', '杜宇文化科技有限公司', '民营企业', '文化传媒', '创始人', '', '杭州', '创业', 'APPROVED', '就业信息真实有效，符合要求', '17', '2026-06-06 13:09:00', '0', '71', '杜宇', null, 'COMPLETED', '同意提交', '审核通过', '2026-06-06 13:09:00', '2026-06-06 13:09:00', '2026-08-19 10:00:00', '2026-08-25 10:03:52');
INSERT INTO `employment_record` VALUES ('57', '57', '悉尼大学', '事业单位', '教育', '硕士研究生', '', '悉尼', '出国留学', 'APPROVED', '就业信息真实有效，符合要求', '17', '2026-07-11 13:51:00', '0', '72', '戴琳', null, 'COMPLETED', '同意提交', '审核通过', '2026-07-11 13:51:00', '2026-07-11 13:51:00', '2026-08-19 10:00:00', '2026-08-25 10:03:52');
INSERT INTO `employment_record` VALUES ('58', '58', '纽约大学', '事业单位', '教育', '硕士研究生', '', '纽约', '出国留学', 'APPROVED', '就业信息真实有效，符合要求', '17', '2026-07-14 14:29:00', '0', '73', '夏磊', null, 'COMPLETED', '同意提交', '审核通过', '2026-07-14 14:29:00', '2026-07-14 14:29:00', '2026-08-19 10:00:00', '2026-08-25 10:03:52');
INSERT INTO `employment_record` VALUES ('59', '59', '新南威尔士大学', '事业单位', '教育', '硕士研究生', '', '墨尔本', '出国留学', 'APPROVED', '就业信息真实有效，符合要求', '17', '2026-07-17 12:09:00', '0', '74', '钟声', null, 'COMPLETED', '同意提交', '审核通过', '2026-07-17 12:09:00', '2026-07-17 12:09:00', '2026-08-19 10:00:00', '2026-08-25 10:03:52');
INSERT INTO `employment_record` VALUES ('60', '60', '纽约大学', '事业单位', '教育', '硕士研究生', '', '纽约', '出国留学', 'APPROVED', '就业信息真实有效，符合要求', '17', '2026-07-03 12:05:00', '0', '75', '汪洋', null, 'COMPLETED', '同意提交', '审核通过', '2026-07-03 12:05:00', '2026-07-03 12:05:00', '2026-08-19 10:00:00', '2026-08-25 10:03:52');
INSERT INTO `employment_record` VALUES ('61', '61', '灵活就业（自由职业）', '民营企业', '文化传媒', '自由职业', '', '成都', '灵活就业', 'APPROVED', '就业信息真实有效，符合要求', '17', '2026-07-06 10:59:00', '0', '76', '田甜', null, 'COMPLETED', '同意提交', '审核通过', '2026-07-06 10:59:00', '2026-07-06 10:59:00', '2026-08-19 10:00:00', '2026-08-25 10:03:52');
INSERT INTO `employment_record` VALUES ('62', '62', '灵活就业（自由职业）', '民营企业', '文化传媒', '自由职业', '', '成都', '灵活就业', 'APPROVED', '就业信息真实有效，符合要求', '17', '2026-07-11 14:35:00', '0', '77', '任杰', null, 'COMPLETED', '同意提交', '审核通过', '2026-07-11 14:35:00', '2026-07-11 14:35:00', '2026-08-19 10:00:00', '2026-08-25 10:03:52');
INSERT INTO `employment_record` VALUES ('63', '65', '中国建筑集团', '国有企业', '互联网/IT/软件', '', '5000-8000', '内蒙古自治区 - 巴彦淖尔市 - 乌拉特中旗', '签约就业', 'APPROVED', '', '17', '2026-08-19 10:22:34', '0', '15', '赵鑫鑫', null, 'COMPLETED', '已通过', '已通过', '2026-08-19 10:18:50', '2026-08-25 10:03:52', '2026-08-19 10:18:50', '2026-08-25 10:03:52');
INSERT INTO `employment_record` VALUES ('64', '66', '麻省理工', '其他', '其他', '博士后', '20000', '美国洛杉矶', '出国', 'APPROVED', '', '17', '2026-08-25 09:54:03', '0', '19', '李天悦', null, 'COMPLETED', '已通过', '已通过', '2026-08-25 09:50:46', '2026-08-25 10:03:52', '2026-08-25 09:50:46', '2026-08-25 10:03:52');

-- ----------------------------
-- Table structure for `employment_status_change`
-- ----------------------------
DROP TABLE IF EXISTS `employment_status_change`;
CREATE TABLE `employment_status_change` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `graduate_id` bigint NOT NULL COMMENT '毕业生ID',
  `from_status` varchar(30) NOT NULL COMMENT '变更前状态',
  `to_status` varchar(30) NOT NULL COMMENT '变更后状态',
  `reason` varchar(100) DEFAULT NULL COMMENT '失业原因(主动离职/企业裁员等)',
  `evidence_url` varchar(500) DEFAULT NULL COMMENT '佐证材料URL(录取通知书/离职证明等)',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `operator_id` bigint DEFAULT NULL COMMENT '操作人ID',
  `operator_name` varchar(50) DEFAULT NULL COMMENT '操作人姓名',
  `is_reminder` tinyint DEFAULT '0' COMMENT '是否触发失业跟踪提醒(0否/1是)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '变更时间',
  PRIMARY KEY (`id`),
  KEY `idx_graduate` (`graduate_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='就业状态变更记录';

-- ----------------------------
-- Records of employment_status_change
-- ----------------------------
INSERT INTO `employment_status_change` VALUES ('1', '66', 'UNEMPLOYED', 'ABROAD', null, '1', null, '19', '李天悦', '0', '2026-08-25 09:51:11');
INSERT INTO `employment_status_change` VALUES ('2', '4', 'UNEMPLOYED', 'EMPLOYED', null, '2', null, '18', '任缘', '0', '2026-08-26 14:28:50');

-- ----------------------------
-- Table structure for `graduate`
-- ----------------------------
DROP TABLE IF EXISTS `graduate`;
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
  `grade` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'grade',
  `graduate_year` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'grad_year',
  `employment_status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'UNEMPLOYED' COMMENT '就业状态',
  `graduate_status` tinyint DEFAULT '1',
  `deleted` tinyint DEFAULT '0',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `track_status` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT 'UNEMPLOYED' COMMENT '就业跟踪状态:UNEMPLOYED未就业/PENDING待审核/EMPLOYED已就业/UNEMPLOYED_AFTER失业/POSTGRADUATE升学/ABROAD出国',
  PRIMARY KEY (`id`),
  UNIQUE KEY `student_no` (`student_no`),
  KEY `idx_dept_id` (`dept_id`),
  KEY `idx_major_id` (`major_id`),
  KEY `idx_class_id` (`class_id`),
  KEY `idx_status` (`employment_status`)
) ENGINE=InnoDB AUTO_INCREMENT=71 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='毕业生表';

-- ----------------------------
-- Records of graduate
-- ----------------------------
INSERT INTO `graduate` VALUES ('4', '202383501034', '任缘', '女', null, null, null, '1', '1', '1', '2022', '2024', '已就业', '1', '0', '2026-08-19 09:05:50', '2026-08-26 14:28:50', 'EMPLOYED');
INSERT INTO `graduate` VALUES ('5', '202201000101', '张伟', '男', '44226219990709361911', '13994096867', '202201000101@stu.edu.cn', '1', '1', '1', '2022', '2026', '已就业', '1', '0', '2026-08-19 10:09:46', '2026-08-19 10:09:46', 'UNEMPLOYED');
INSERT INTO `graduate` VALUES ('6', '202201000202', '王芳', '女', '44902719990912841421', '13871633938', '202201000202@stu.edu.cn', '1', '1', '2', '2022', '2026', '已就业', '1', '0', '2026-08-19 10:09:46', '2026-08-19 10:09:46', 'UNEMPLOYED');
INSERT INTO `graduate` VALUES ('7', '202201000303', '李娜', '男', '44775719990810805211', '13104108369', '202201000303@stu.edu.cn', '1', '1', '3', '2022', '2026', '已就业', '1', '0', '2026-08-19 10:09:46', '2026-08-19 10:09:46', 'UNEMPLOYED');
INSERT INTO `graduate` VALUES ('8', '202201000404', '刘洋', '女', '44159719990127162821', '18337659945', '202201000404@stu.edu.cn', '1', '1', '4', '2022', '2026', '已就业', '1', '0', '2026-08-19 10:09:46', '2026-08-19 10:09:46', 'UNEMPLOYED');
INSERT INTO `graduate` VALUES ('9', '202201000505', '陈静', '男', '44697719990701724511', '19895322507', '202201000505@stu.edu.cn', '1', '1', '5', '2022', '2026', '已就业', '1', '0', '2026-08-19 10:09:46', '2026-08-19 10:09:46', 'UNEMPLOYED');
INSERT INTO `graduate` VALUES ('10', '202201000601', '杨帆', '女', '44700119990203606721', '18139332824', '202201000601@stu.edu.cn', '1', '1', '6', '2022', '2026', '已就业', '1', '0', '2026-08-19 10:09:46', '2026-08-19 10:09:46', 'UNEMPLOYED');
INSERT INTO `graduate` VALUES ('11', '202201000702', '赵磊', '男', '44955019990324282411', '18674534055', '202201000702@stu.edu.cn', '1', '1', '7', '2022', '2026', '已就业', '1', '0', '2026-08-19 10:09:46', '2026-08-19 10:09:46', 'UNEMPLOYED');
INSERT INTO `graduate` VALUES ('12', '202201000803', '黄敏', '女', '44918419990314811921', '13254528952', '202201000803@stu.edu.cn', '1', '1', '8', '2022', '2026', '已就业', '1', '0', '2026-08-19 10:09:46', '2026-08-19 10:09:46', 'UNEMPLOYED');
INSERT INTO `graduate` VALUES ('13', '202201000904', '周涛', '男', '44863019991020409811', '18357610219', '202201000904@stu.edu.cn', '1', '1', '9', '2022', '2026', '已就业', '1', '0', '2026-08-19 10:09:47', '2026-08-19 10:09:47', 'UNEMPLOYED');
INSERT INTO `graduate` VALUES ('14', '202202001001', '吴雪', '女', '44327619990217430621', '17399881529', '202202001001@stu.edu.cn', '1', '2', '10', '2022', '2026', '已就业', '1', '0', '2026-08-19 10:09:47', '2026-08-19 10:09:47', 'UNEMPLOYED');
INSERT INTO `graduate` VALUES ('15', '202202001102', '徐强', '男', '44674519990726684411', '17420102167', '202202001102@stu.edu.cn', '1', '2', '11', '2022', '2026', '已就业', '1', '0', '2026-08-19 10:09:47', '2026-08-19 10:09:47', 'UNEMPLOYED');
INSERT INTO `graduate` VALUES ('16', '202202001203', '孙丽', '女', '44643419990706820821', '19625486038', '202202001203@stu.edu.cn', '1', '2', '12', '2022', '2026', '已就业', '1', '0', '2026-08-19 10:09:47', '2026-08-19 10:09:47', 'UNEMPLOYED');
INSERT INTO `graduate` VALUES ('17', '202203001301', '马超', '男', '44783719990419174211', '13384360141', '202203001301@stu.edu.cn', '1', '3', '13', '2022', '2026', '已就业', '1', '0', '2026-08-19 10:09:47', '2026-08-19 10:09:47', 'UNEMPLOYED');
INSERT INTO `graduate` VALUES ('18', '202203001402', '朱婷', '女', '44762519990227954121', '17778921677', '202203001402@stu.edu.cn', '1', '3', '14', '2022', '2026', '已就业', '1', '0', '2026-08-19 10:09:47', '2026-08-19 10:09:47', 'UNEMPLOYED');
INSERT INTO `graduate` VALUES ('19', '202203001503', '胡军', '男', '44368519990828345911', '15751041445', '202203001503@stu.edu.cn', '1', '3', '15', '2022', '2026', '已就业', '1', '0', '2026-08-19 10:09:47', '2026-08-19 10:09:47', 'UNEMPLOYED');
INSERT INTO `graduate` VALUES ('20', '202204001601', '郭靖', '女', '44115919991013841221', '19201434762', '202204001601@stu.edu.cn', '2', '4', '16', '2022', '2026', '已就业', '1', '0', '2026-08-19 10:09:47', '2026-08-19 10:09:47', 'UNEMPLOYED');
INSERT INTO `graduate` VALUES ('21', '202204001702', '何雨', '男', '44457519990815206111', '19742786297', '202204001702@stu.edu.cn', '2', '4', '17', '2022', '2026', '已就业', '1', '0', '2026-08-19 10:09:47', '2026-08-19 10:09:47', 'UNEMPLOYED');
INSERT INTO `graduate` VALUES ('22', '202204001803', '高翔', '女', '44746919991027956721', '19360370884', '202204001803@stu.edu.cn', '2', '4', '18', '2022', '2026', '已就业', '1', '0', '2026-08-19 10:09:47', '2026-08-19 10:09:47', 'UNEMPLOYED');
INSERT INTO `graduate` VALUES ('23', '202204001904', '林晓', '男', '44979819990702780011', '13718671826', '202204001904@stu.edu.cn', '2', '4', '19', '2022', '2026', '已就业', '1', '0', '2026-08-19 10:09:47', '2026-08-19 10:09:47', 'UNEMPLOYED');
INSERT INTO `graduate` VALUES ('24', '202204002005', '罗晨', '女', '44280619990210517521', '15940256632', '202204002005@stu.edu.cn', '2', '4', '20', '2022', '2026', '已就业', '1', '0', '2026-08-19 10:09:47', '2026-08-19 10:09:47', 'UNEMPLOYED');
INSERT INTO `graduate` VALUES ('25', '202204002101', '郑浩', '男', '44923919990507307911', '15793569464', '202204002101@stu.edu.cn', '2', '4', '21', '2022', '2026', '已就业', '1', '0', '2026-08-19 10:09:47', '2026-08-19 10:09:47', 'UNEMPLOYED');
INSERT INTO `graduate` VALUES ('26', '202204002202', '梁宇', '女', '44462319990219625221', '17218375262', '202204002202@stu.edu.cn', '2', '4', '22', '2022', '2026', '已就业', '1', '0', '2026-08-19 10:09:47', '2026-08-19 10:09:47', 'UNEMPLOYED');
INSERT INTO `graduate` VALUES ('27', '202204002303', '谢飞', '男', '44635519991213915311', '13644062193', '202204002303@stu.edu.cn', '2', '4', '23', '2022', '2026', '已就业', '1', '0', '2026-08-19 10:09:47', '2026-08-19 10:09:47', 'UNEMPLOYED');
INSERT INTO `graduate` VALUES ('28', '202205002401', '宋佳', '女', '44988519990216879821', '18424033287', '202205002401@stu.edu.cn', '2', '5', '24', '2022', '2026', '已就业', '1', '0', '2026-08-19 10:09:47', '2026-08-19 10:09:47', 'UNEMPLOYED');
INSERT INTO `graduate` VALUES ('29', '202205002502', '唐毅', '男', '44217019990325753811', '13423628545', '202205002502@stu.edu.cn', '2', '5', '25', '2022', '2026', '已就业', '1', '0', '2026-08-19 10:09:47', '2026-08-19 10:09:47', 'UNEMPLOYED');
INSERT INTO `graduate` VALUES ('30', '202205002603', '韩雪', '女', '44418219991220705121', '17392489037', '202205002603@stu.edu.cn', '2', '5', '26', '2022', '2026', '已就业', '1', '0', '2026-08-19 10:09:47', '2026-08-19 10:09:47', 'UNEMPLOYED');
INSERT INTO `graduate` VALUES ('31', '202206002701', '冯刚', '男', '44129019991001336011', '19504922266', '202206002701@stu.edu.cn', '3', '6', '27', '2022', '2026', '已就业', '1', '0', '2026-08-19 10:09:47', '2026-08-19 10:09:47', 'UNEMPLOYED');
INSERT INTO `graduate` VALUES ('32', '202206002802', '董倩', '女', '44202719990808169521', '17622304880', '202206002802@stu.edu.cn', '3', '6', '28', '2022', '2026', '已就业', '1', '0', '2026-08-19 10:09:47', '2026-08-19 10:09:47', 'UNEMPLOYED');
INSERT INTO `graduate` VALUES ('33', '202206002903', '萧然', '男', '44839819990103641311', '17480484126', '202206002903@stu.edu.cn', '3', '6', '29', '2022', '2026', '已就业', '1', '0', '2026-08-19 10:09:47', '2026-08-19 10:09:47', 'UNEMPLOYED');
INSERT INTO `graduate` VALUES ('34', '202206003004', '程亮', '女', '44810619990105183721', '15161755277', '202206003004@stu.edu.cn', '3', '6', '30', '2022', '2026', '已就业', '1', '0', '2026-08-19 10:09:47', '2026-08-19 10:09:47', 'UNEMPLOYED');
INSERT INTO `graduate` VALUES ('35', '202206003105', '曹阳', '男', '44197119990514497011', '13155062661', '202206003105@stu.edu.cn', '3', '6', '31', '2022', '2026', '已就业', '1', '0', '2026-08-19 10:09:47', '2026-08-19 10:09:47', 'UNEMPLOYED');
INSERT INTO `graduate` VALUES ('36', '202206003201', '袁媛', '女', '44536919991227956521', '17160809314', '202206003201@stu.edu.cn', '3', '6', '32', '2022', '2026', '已就业', '1', '0', '2026-08-19 10:09:47', '2026-08-19 10:09:47', 'UNEMPLOYED');
INSERT INTO `graduate` VALUES ('37', '202206003302', '邓超', '男', '44869219990704332611', '15473899181', '202206003302@stu.edu.cn', '3', '6', '33', '2022', '2026', '已就业', '1', '0', '2026-08-19 10:09:47', '2026-08-19 10:09:47', 'UNEMPLOYED');
INSERT INTO `graduate` VALUES ('38', '202207003401', '许晴', '女', '44420019990606692221', '13659258258', '202207003401@stu.edu.cn', '3', '7', '34', '2022', '2026', '已就业', '1', '0', '2026-08-19 10:09:47', '2026-08-19 10:09:47', 'UNEMPLOYED');
INSERT INTO `graduate` VALUES ('39', '202207003502', '傅敏', '男', '44744019991205307911', '18386981473', '202207003502@stu.edu.cn', '3', '7', '35', '2022', '2026', '已就业', '1', '0', '2026-08-19 10:09:47', '2026-08-19 10:09:47', 'UNEMPLOYED');
INSERT INTO `graduate` VALUES ('40', '202207003603', '沈静', '女', '44169719991215136021', '18130429835', '202207003603@stu.edu.cn', '3', '7', '36', '2022', '2026', '已就业', '1', '0', '2026-08-19 10:09:47', '2026-08-19 10:09:47', 'UNEMPLOYED');
INSERT INTO `graduate` VALUES ('41', '202208003701', '曾强', '男', '44821819990405779411', '19148433388', '202208003701@stu.edu.cn', '4', '8', '37', '2022', '2026', '已就业', '1', '0', '2026-08-19 10:09:47', '2026-08-19 10:09:47', 'UNEMPLOYED');
INSERT INTO `graduate` VALUES ('42', '202208003802', '彭磊', '女', '44497219990727345921', '15907351577', '202208003802@stu.edu.cn', '4', '8', '38', '2022', '2026', '已就业', '1', '0', '2026-08-19 10:09:47', '2026-08-19 10:09:47', 'UNEMPLOYED');
INSERT INTO `graduate` VALUES ('43', '202208003903', '吕倩', '男', '44617019990915147311', '17706574104', '202208003903@stu.edu.cn', '4', '8', '39', '2022', '2026', '已就业', '1', '0', '2026-08-19 10:09:47', '2026-08-19 10:09:47', 'UNEMPLOYED');
INSERT INTO `graduate` VALUES ('44', '202208004004', '苏航', '女', '44709219990908952421', '19333960475', '202208004004@stu.edu.cn', '4', '8', '40', '2022', '2026', '已就业', '1', '0', '2026-08-19 10:09:47', '2026-08-19 10:09:47', 'UNEMPLOYED');
INSERT INTO `graduate` VALUES ('45', '202208004105', '卢俊', '男', '44245519990412518611', '13760779934', '202208004105@stu.edu.cn', '4', '8', '41', '2022', '2026', '已就业', '1', '0', '2026-08-19 10:09:47', '2026-08-19 10:09:47', 'UNEMPLOYED');
INSERT INTO `graduate` VALUES ('46', '202209004201', '蒋欣', '女', '44221619990108535221', '18290134343', '202209004201@stu.edu.cn', '4', '9', '42', '2022', '2026', '已就业', '1', '0', '2026-08-19 10:09:47', '2026-08-19 10:09:47', 'UNEMPLOYED');
INSERT INTO `graduate` VALUES ('47', '202209004302', '蔡宇', '男', '44632419990114307511', '13914416798', '202209004302@stu.edu.cn', '4', '9', '43', '2022', '2026', '已就业', '1', '0', '2026-08-19 10:09:47', '2026-08-19 10:09:47', 'UNEMPLOYED');
INSERT INTO `graduate` VALUES ('48', '202209004403', '贾琳', '女', '44916719991104364721', '15718181335', '202209004403@stu.edu.cn', '4', '9', '44', '2022', '2026', '已就业', '1', '0', '2026-08-19 10:09:47', '2026-08-19 10:09:47', 'UNEMPLOYED');
INSERT INTO `graduate` VALUES ('49', '202209004504', '丁伟', '男', '44380519990418484611', '15521848326', '202209004504@stu.edu.cn', '4', '9', '45', '2022', '2026', '已就业', '1', '0', '2026-08-19 10:09:47', '2026-08-19 10:09:47', 'UNEMPLOYED');
INSERT INTO `graduate` VALUES ('50', '202209004605', '魏然', '女', '44212919990721474321', '17172509449', '202209004605@stu.edu.cn', '4', '9', '46', '2022', '2026', '已就业', '1', '0', '2026-08-19 10:09:47', '2026-08-19 10:09:47', 'UNEMPLOYED');
INSERT INTO `graduate` VALUES ('51', '202210004701', '薛峰', '男', '44893419990823878411', '18188083396', '202210004701@stu.edu.cn', '5', '10', '47', '2022', '2026', '已就业', '1', '0', '2026-08-19 10:09:47', '2026-08-19 10:09:47', 'UNEMPLOYED');
INSERT INTO `graduate` VALUES ('52', '202210004802', '叶静', '女', '44548019990419665021', '18693287231', '202210004802@stu.edu.cn', '5', '10', '48', '2022', '2026', '已就业', '1', '0', '2026-08-19 10:09:47', '2026-08-19 10:09:47', 'UNEMPLOYED');
INSERT INTO `graduate` VALUES ('53', '202210004903', '阎凯', '男', '44654719990907240711', '18857042312', '202210004903@stu.edu.cn', '5', '10', '49', '2022', '2026', '已就业', '1', '0', '2026-08-19 10:09:47', '2026-08-19 10:09:47', 'UNEMPLOYED');
INSERT INTO `graduate` VALUES ('54', '202210005004', '余华', '女', '44138019991018534721', '18185773665', '202210005004@stu.edu.cn', '5', '10', '50', '2022', '2026', '已就业', '1', '0', '2026-08-19 10:09:47', '2026-08-19 10:09:47', 'UNEMPLOYED');
INSERT INTO `graduate` VALUES ('55', '202210005105', '潘婷', '男', '44739519990319966711', '18720711016', '202210005105@stu.edu.cn', '5', '10', '51', '2022', '2026', '已就业', '1', '0', '2026-08-19 10:09:47', '2026-08-19 10:09:47', 'UNEMPLOYED');
INSERT INTO `graduate` VALUES ('56', '202211005201', '杜宇', '女', '44596619990728125021', '15403255797', '202211005201@stu.edu.cn', '6', '11', '52', '2022', '2026', '已就业', '1', '0', '2026-08-19 10:09:47', '2026-08-19 10:09:47', 'UNEMPLOYED');
INSERT INTO `graduate` VALUES ('57', '202211005302', '戴琳', '男', '44817019990319355111', '15196537241', '202211005302@stu.edu.cn', '6', '11', '53', '2022', '2026', '已就业', '1', '0', '2026-08-19 10:09:47', '2026-08-19 10:09:47', 'UNEMPLOYED');
INSERT INTO `graduate` VALUES ('58', '202211005403', '夏磊', '女', '44721019991009318021', '13799284563', '202211005403@stu.edu.cn', '6', '11', '54', '2022', '2026', '已就业', '1', '0', '2026-08-19 10:09:47', '2026-08-19 10:09:47', 'UNEMPLOYED');
INSERT INTO `graduate` VALUES ('59', '202211005504', '钟声', '男', '44994319991228407011', '18520049793', '202211005504@stu.edu.cn', '6', '11', '55', '2022', '2026', '已就业', '1', '0', '2026-08-19 10:09:47', '2026-08-19 10:09:47', 'UNEMPLOYED');
INSERT INTO `graduate` VALUES ('60', '202211005605', '汪洋', '女', '44884919990417572621', '18549828295', '202211005605@stu.edu.cn', '6', '11', '56', '2022', '2026', '已就业', '1', '0', '2026-08-19 10:09:47', '2026-08-19 10:09:47', 'UNEMPLOYED');
INSERT INTO `graduate` VALUES ('61', '202212005701', '田甜', '男', '44354819990706446111', '13279294058', '202212005701@stu.edu.cn', '7', '12', '57', '2022', '2026', '已就业', '1', '0', '2026-08-19 10:09:47', '2026-08-19 10:09:47', 'UNEMPLOYED');
INSERT INTO `graduate` VALUES ('62', '202212005802', '任杰', '女', '44927519991215952621', '19190088283', '202212005802@stu.edu.cn', '7', '12', '58', '2022', '2026', '已就业', '1', '0', '2026-08-19 10:09:47', '2026-08-19 10:09:47', 'UNEMPLOYED');
INSERT INTO `graduate` VALUES ('63', '202212005903', '姜涛', '男', '44255219990910831811', '18236328991', '202212005903@stu.edu.cn', '7', '12', '59', '2022', '2026', '待就业', '1', '0', '2026-08-19 10:09:47', '2026-08-19 10:09:47', 'UNEMPLOYED');
INSERT INTO `graduate` VALUES ('64', '202212006004', '范斌', '女', '44232219990725244321', '19576287423', '202212006004@stu.edu.cn', '7', '12', '60', '2022', '2026', '待就业', '1', '0', '2026-08-19 10:09:47', '2026-08-19 10:09:47', 'UNEMPLOYED');
INSERT INTO `graduate` VALUES ('65', '202383501037', '赵鑫鑫', '女', null, null, null, '1', '1', '1', '2022', '2024', '已就业', '1', '0', '2026-08-19 10:18:50', '2026-08-19 15:17:50', 'UNEMPLOYED');
INSERT INTO `graduate` VALUES ('66', '202383501038', '李天悦', '女', null, '', '', '1', '2', '3', null, null, '待就业', '1', '0', '2026-08-25 09:49:07', '2026-08-25 09:51:11', 'ABROAD');
INSERT INTO `graduate` VALUES ('67', '202383501033', '覃玲甜', '女', null, '', '', '1', '2', '3', null, null, '待就业', '1', '0', '2026-08-25 09:55:47', '2026-08-25 09:55:47', 'UNEMPLOYED');
INSERT INTO `graduate` VALUES ('68', '2022120161', '董明明', '男', null, '13900001211', 'dongming0101@stu.college.edu.cn', '7', '12', '16', '2022', '2025', '待就业', '1', '0', '2026-08-26 10:21:45', '2026-08-26 10:21:45', 'UNEMPLOYED');
INSERT INTO `graduate` VALUES ('69', '2022120162', '袁圆圆', '女', null, '13900001222', 'yuanyuan0202@stu.college.edu.cn', '7', '12', '16', '2022', '2025', '待就业', '1', '0', '2026-08-26 10:21:45', '2026-08-26 10:21:45', 'UNEMPLOYED');
INSERT INTO `graduate` VALUES ('70', '2022120163', '金鑫鑫', '女', null, '13900001233', 'jinxinxin03@stu.college.edu.cn', '7', '12', '16', '2022', '2025', '待就业', '1', '0', '2026-08-26 10:21:45', '2026-08-26 10:21:45', 'UNEMPLOYED');

-- ----------------------------
-- Table structure for `login_log`
-- ----------------------------
DROP TABLE IF EXISTS `login_log`;
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

-- ----------------------------
-- Records of login_log
-- ----------------------------

-- ----------------------------
-- Table structure for `major`
-- ----------------------------
DROP TABLE IF EXISTS `major`;
CREATE TABLE `major` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '专业名称',
  `code` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '专业编码',
  `dept_id` bigint NOT NULL COMMENT '所属院系ID',
  `sort_order` int DEFAULT '0' COMMENT '排序号',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态',
  `description` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `deleted` tinyint DEFAULT '0',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`),
  KEY `idx_dept_id` (`dept_id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='专业表';

-- ----------------------------
-- Records of major
-- ----------------------------
INSERT INTO `major` VALUES ('1', '计算机科学与技术', 'CS01', '1', '1', '1', null, '0', '2026-08-07 10:36:22', '2026-08-07 10:36:22');
INSERT INTO `major` VALUES ('2', '软件工程', 'CS02', '1', '2', '1', null, '0', '2026-08-07 10:36:22', '2026-08-07 10:36:22');
INSERT INTO `major` VALUES ('3', '物联网工程', 'CS03', '1', '3', '1', null, '0', '2026-08-07 10:36:22', '2026-08-07 10:36:22');
INSERT INTO `major` VALUES ('4', '电子信息工程', 'EE01', '2', '1', '1', null, '0', '2026-08-07 10:36:22', '2026-08-07 10:36:22');
INSERT INTO `major` VALUES ('5', '通信工程', 'EE02', '2', '2', '1', null, '0', '2026-08-07 10:36:22', '2026-08-07 10:36:22');
INSERT INTO `major` VALUES ('6', '机械设计制造', 'ME01', '3', '1', '1', null, '0', '2026-08-07 10:36:22', '2026-08-07 10:36:22');
INSERT INTO `major` VALUES ('7', '车辆工程', 'ME02', '3', '2', '1', null, '0', '2026-08-07 10:36:22', '2026-08-07 10:36:22');
INSERT INTO `major` VALUES ('8', '工商管理', 'EM01', '4', '1', '1', null, '0', '2026-08-07 10:36:22', '2026-08-07 10:36:22');
INSERT INTO `major` VALUES ('9', '会计学', 'EM02', '4', '2', '1', null, '0', '2026-08-07 10:36:22', '2026-08-07 10:36:22');
INSERT INTO `major` VALUES ('10', '法学', 'LA01', '5', '1', '1', null, '0', '2026-08-07 10:36:22', '2026-08-07 10:36:22');
INSERT INTO `major` VALUES ('11', '视觉传达设计', 'AD01', '6', '1', '1', null, '0', '2026-08-07 10:36:22', '2026-08-07 10:36:22');
INSERT INTO `major` VALUES ('12', '数学与应用数学', 'SC01', '7', '1', '1', null, '0', '2026-08-07 10:36:22', '2026-08-07 10:36:22');

-- ----------------------------
-- Table structure for `notification`
-- ----------------------------
DROP TABLE IF EXISTS `notification`;
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

-- ----------------------------
-- Records of notification
-- ----------------------------

-- ----------------------------
-- Table structure for `operation_log`
-- ----------------------------
DROP TABLE IF EXISTS `operation_log`;
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

-- ----------------------------
-- Records of operation_log
-- ----------------------------

-- ----------------------------
-- Table structure for `sys_config`
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
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

-- ----------------------------
-- Records of sys_config
-- ----------------------------

-- ----------------------------
-- Table structure for `sys_user`
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码(BCrypt加密)',
  `real_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '真实姓名',
  `gender` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '性别（男/女）',
  `student_number` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '学号/工号',
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '头像URL',
  `role` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'GRADUATE' COMMENT '角色: GRADUATE-毕业生, TEACHER-教师, COLLEGE_ADMIN-校级管理员, SYSTEM_ADMIN-系统管理员',
  `dept_id` bigint DEFAULT NULL COMMENT '所属院系ID',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态: 1启用 0禁用',
  `deleted` tinyint DEFAULT '0',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  KEY `idx_role` (`role`),
  KEY `idx_dept_id` (`dept_id`)
) ENGINE=InnoDB AUTO_INCREMENT=85 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统用户表';

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES ('2', 'admin', '$2a$10$AtxrOxn1e2n5wHmPJ2u2i.sioQwZr5XqexBlJtra6xyd8PJHRmpnm', '系统管理员', '男', 'admin', '13800000000', null, null, 'SYSTEM_ADMIN', null, '1', '0', '2026-08-07 11:40:31', '2026-08-19 14:18:37');
INSERT INTO `sys_user` VALUES ('3', 'teacher', '$2b$10$kN./zF7JZNtMtkwP2E.ypuedQfuL6UbaH7GAIdFLTqfFjJ501pRtu', '张老师', null, 'teacher', null, null, null, 'TEACHER', '1', '1', '0', '2026-08-07 11:40:31', '2026-08-19 11:20:24');
INSERT INTO `sys_user` VALUES ('4', 'student', '$2b$10$kN./zF7JZNtMtkwP2E.ypuedQfuL6UbaH7GAIdFLTqfFjJ501pRtu', '李同学', null, 'student', null, null, null, 'GRADUATE', '1', '1', '0', '2026-08-07 11:40:31', '2026-08-19 11:20:24');
INSERT INTO `sys_user` VALUES ('5', 'testuser', '$2b$10$kN./zF7JZNtMtkwP2E.ypuedQfuL6UbaH7GAIdFLTqfFjJ501pRtu', '????', null, 'testuser', null, null, null, 'GRADUATE', null, '1', '0', '2026-08-07 13:59:40', '2026-08-19 11:20:24');
INSERT INTO `sys_user` VALUES ('7', 'zxx', '$2b$10$kN./zF7JZNtMtkwP2E.ypuedQfuL6UbaH7GAIdFLTqfFjJ501pRtu', '赵鑫鑫', null, 'zxx', null, null, null, 'GRADUATE', null, '1', '0', '2026-08-07 14:01:01', '2026-08-19 11:20:24');
INSERT INTO `sys_user` VALUES ('8', 'testdebug', '$2b$10$kN./zF7JZNtMtkwP2E.ypuedQfuL6UbaH7GAIdFLTqfFjJ501pRtu', '????', null, 'testdebug', null, null, null, 'GRADUATE', null, '1', '0', '2026-08-07 14:18:01', '2026-08-19 11:20:24');
INSERT INTO `sys_user` VALUES ('9', 'qqq', '$2b$10$kN./zF7JZNtMtkwP2E.ypuedQfuL6UbaH7GAIdFLTqfFjJ501pRtu', '覃', null, 'qqq', null, null, null, 'GRADUATE', null, '1', '0', '2026-08-07 14:41:44', '2026-08-19 11:20:24');
INSERT INTO `sys_user` VALUES ('10', 'teacher01', '$2b$10$kN./zF7JZNtMtkwP2E.ypuedQfuL6UbaH7GAIdFLTqfFjJ501pRtu', '张老师', null, 'T20230001', null, null, null, 'TEACHER', '1', '1', '0', '2026-08-07 15:02:34', '2026-08-19 11:20:24');
INSERT INTO `sys_user` VALUES ('11', 'student01', '$2b$10$kN./zF7JZNtMtkwP2E.ypuedQfuL6UbaH7GAIdFLTqfFjJ501pRtu', '李同学', null, '20230001', null, null, null, 'GRADUATE', '1', '1', '0', '2026-08-07 15:02:35', '2026-08-19 11:20:24');
INSERT INTO `sys_user` VALUES ('12', 'qzzzz', '$2b$10$kN./zF7JZNtMtkwP2E.ypuedQfuL6UbaH7GAIdFLTqfFjJ501pRtu', '覃', null, '', null, null, null, 'GRADUATE', null, '1', '0', '2026-08-10 10:22:37', '2026-08-28 09:50:58');
INSERT INTO `sys_user` VALUES ('15', '202383501037', '$2b$10$kN./zF7JZNtMtkwP2E.ypuedQfuL6UbaH7GAIdFLTqfFjJ501pRtu', '赵鑫鑫', null, '202383501037', null, null, null, 'GRADUATE', '1', '1', '0', '2026-08-11 09:54:36', '2026-08-19 15:17:50');
INSERT INTO `sys_user` VALUES ('17', 'C20230001', '$2b$10$kN./zF7JZNtMtkwP2E.ypuedQfuL6UbaH7GAIdFLTqfFjJ501pRtu', '王主任', '女', 'C20230001', null, null, null, 'COLLEGE_ADMIN', '1', '1', '0', '2026-08-12 09:07:02', '2026-08-19 14:18:37');
INSERT INTO `sys_user` VALUES ('18', '202383501034', '$2b$10$kN./zF7JZNtMtkwP2E.ypuedQfuL6UbaH7GAIdFLTqfFjJ501pRtu', '任缘', null, '202383501034', null, null, null, 'GRADUATE', '1', '1', '0', '2026-08-13 09:23:22', '2026-08-19 15:18:04');
INSERT INTO `sys_user` VALUES ('19', '202383501038', '$2b$10$kN./zF7JZNtMtkwP2E.ypuedQfuL6UbaH7GAIdFLTqfFjJ501pRtu', '李天悦', null, '202383501038', '', '', null, 'GRADUATE', '1', '1', '0', '2026-08-18 11:05:59', '2026-08-25 09:49:07');
INSERT INTO `sys_user` VALUES ('20', '202201000101', '$2b$10$kN./zF7JZNtMtkwP2E.ypuedQfuL6UbaH7GAIdFLTqfFjJ501pRtu', '张伟', null, '202201000101', '13994096867', '202201000101@stu.edu.cn', null, 'GRADUATE', '1', '1', '0', '2026-08-19 10:09:46', '2026-08-19 11:20:24');
INSERT INTO `sys_user` VALUES ('21', '202201000202', '$2b$10$kN./zF7JZNtMtkwP2E.ypuedQfuL6UbaH7GAIdFLTqfFjJ501pRtu', '王芳', null, '202201000202', '13871633938', '202201000202@stu.edu.cn', null, 'GRADUATE', '1', '1', '0', '2026-08-19 10:09:46', '2026-08-19 11:20:24');
INSERT INTO `sys_user` VALUES ('22', '202201000303', '$2b$10$kN./zF7JZNtMtkwP2E.ypuedQfuL6UbaH7GAIdFLTqfFjJ501pRtu', '李娜', null, '202201000303', '13104108369', '202201000303@stu.edu.cn', null, 'GRADUATE', '1', '1', '0', '2026-08-19 10:09:46', '2026-08-19 11:20:24');
INSERT INTO `sys_user` VALUES ('23', '202201000404', '$2b$10$kN./zF7JZNtMtkwP2E.ypuedQfuL6UbaH7GAIdFLTqfFjJ501pRtu', '刘洋', null, '202201000404', '18337659945', '202201000404@stu.edu.cn', null, 'GRADUATE', '1', '1', '0', '2026-08-19 10:09:46', '2026-08-19 11:20:24');
INSERT INTO `sys_user` VALUES ('24', '202201000505', '$2b$10$kN./zF7JZNtMtkwP2E.ypuedQfuL6UbaH7GAIdFLTqfFjJ501pRtu', '陈静', null, '202201000505', '19895322507', '202201000505@stu.edu.cn', null, 'GRADUATE', '1', '1', '0', '2026-08-19 10:09:46', '2026-08-19 11:20:24');
INSERT INTO `sys_user` VALUES ('25', '202201000601', '$2b$10$kN./zF7JZNtMtkwP2E.ypuedQfuL6UbaH7GAIdFLTqfFjJ501pRtu', '杨帆', null, '202201000601', '18139332824', '202201000601@stu.edu.cn', null, 'GRADUATE', '1', '1', '0', '2026-08-19 10:09:46', '2026-08-19 11:20:24');
INSERT INTO `sys_user` VALUES ('26', '202201000702', '$2b$10$kN./zF7JZNtMtkwP2E.ypuedQfuL6UbaH7GAIdFLTqfFjJ501pRtu', '赵磊', null, '202201000702', '18674534055', '202201000702@stu.edu.cn', null, 'GRADUATE', '1', '1', '0', '2026-08-19 10:09:46', '2026-08-19 11:20:24');
INSERT INTO `sys_user` VALUES ('27', '202201000803', '$2b$10$kN./zF7JZNtMtkwP2E.ypuedQfuL6UbaH7GAIdFLTqfFjJ501pRtu', '黄敏', null, '202201000803', '13254528952', '202201000803@stu.edu.cn', null, 'GRADUATE', '1', '1', '0', '2026-08-19 10:09:46', '2026-08-19 11:20:24');
INSERT INTO `sys_user` VALUES ('28', '202201000904', '$2b$10$kN./zF7JZNtMtkwP2E.ypuedQfuL6UbaH7GAIdFLTqfFjJ501pRtu', '周涛', null, '202201000904', '18357610219', '202201000904@stu.edu.cn', null, 'GRADUATE', '1', '1', '0', '2026-08-19 10:09:46', '2026-08-19 11:20:24');
INSERT INTO `sys_user` VALUES ('29', '202202001001', '$2b$10$kN./zF7JZNtMtkwP2E.ypuedQfuL6UbaH7GAIdFLTqfFjJ501pRtu', '吴雪', null, '202202001001', '17399881529', '202202001001@stu.edu.cn', null, 'GRADUATE', '1', '1', '0', '2026-08-19 10:09:46', '2026-08-19 11:20:24');
INSERT INTO `sys_user` VALUES ('30', '202202001102', '$2b$10$kN./zF7JZNtMtkwP2E.ypuedQfuL6UbaH7GAIdFLTqfFjJ501pRtu', '徐强', null, '202202001102', '17420102167', '202202001102@stu.edu.cn', null, 'GRADUATE', '1', '1', '0', '2026-08-19 10:09:46', '2026-08-19 11:20:24');
INSERT INTO `sys_user` VALUES ('31', '202202001203', '$2b$10$kN./zF7JZNtMtkwP2E.ypuedQfuL6UbaH7GAIdFLTqfFjJ501pRtu', '孙丽', null, '202202001203', '19625486038', '202202001203@stu.edu.cn', null, 'GRADUATE', '1', '1', '0', '2026-08-19 10:09:46', '2026-08-19 11:20:24');
INSERT INTO `sys_user` VALUES ('32', '202203001301', '$2b$10$kN./zF7JZNtMtkwP2E.ypuedQfuL6UbaH7GAIdFLTqfFjJ501pRtu', '马超', null, '202203001301', '13384360141', '202203001301@stu.edu.cn', null, 'GRADUATE', '1', '1', '0', '2026-08-19 10:09:46', '2026-08-19 11:20:24');
INSERT INTO `sys_user` VALUES ('33', '202203001402', '$2b$10$kN./zF7JZNtMtkwP2E.ypuedQfuL6UbaH7GAIdFLTqfFjJ501pRtu', '朱婷', null, '202203001402', '17778921677', '202203001402@stu.edu.cn', null, 'GRADUATE', '1', '1', '0', '2026-08-19 10:09:46', '2026-08-19 11:20:24');
INSERT INTO `sys_user` VALUES ('34', '202203001503', '$2b$10$kN./zF7JZNtMtkwP2E.ypuedQfuL6UbaH7GAIdFLTqfFjJ501pRtu', '胡军', null, '202203001503', '15751041445', '202203001503@stu.edu.cn', null, 'GRADUATE', '1', '1', '0', '2026-08-19 10:09:46', '2026-08-19 11:20:24');
INSERT INTO `sys_user` VALUES ('35', '202204001601', '$2b$10$kN./zF7JZNtMtkwP2E.ypuedQfuL6UbaH7GAIdFLTqfFjJ501pRtu', '郭靖', null, '202204001601', '19201434762', '202204001601@stu.edu.cn', null, 'GRADUATE', '2', '1', '0', '2026-08-19 10:09:46', '2026-08-19 11:20:24');
INSERT INTO `sys_user` VALUES ('36', '202204001702', '$2b$10$kN./zF7JZNtMtkwP2E.ypuedQfuL6UbaH7GAIdFLTqfFjJ501pRtu', '何雨', null, '202204001702', '19742786297', '202204001702@stu.edu.cn', null, 'GRADUATE', '2', '1', '0', '2026-08-19 10:09:46', '2026-08-19 11:20:24');
INSERT INTO `sys_user` VALUES ('37', '202204001803', '$2b$10$kN./zF7JZNtMtkwP2E.ypuedQfuL6UbaH7GAIdFLTqfFjJ501pRtu', '高翔', null, '202204001803', '19360370884', '202204001803@stu.edu.cn', null, 'GRADUATE', '2', '1', '0', '2026-08-19 10:09:46', '2026-08-19 11:20:24');
INSERT INTO `sys_user` VALUES ('38', '202204001904', '$2b$10$kN./zF7JZNtMtkwP2E.ypuedQfuL6UbaH7GAIdFLTqfFjJ501pRtu', '林晓', null, '202204001904', '13718671826', '202204001904@stu.edu.cn', null, 'GRADUATE', '2', '1', '0', '2026-08-19 10:09:46', '2026-08-19 11:20:24');
INSERT INTO `sys_user` VALUES ('39', '202204002005', '$2b$10$kN./zF7JZNtMtkwP2E.ypuedQfuL6UbaH7GAIdFLTqfFjJ501pRtu', '罗晨', null, '202204002005', '15940256632', '202204002005@stu.edu.cn', null, 'GRADUATE', '2', '1', '0', '2026-08-19 10:09:46', '2026-08-19 11:20:24');
INSERT INTO `sys_user` VALUES ('40', '202204002101', '$2b$10$kN./zF7JZNtMtkwP2E.ypuedQfuL6UbaH7GAIdFLTqfFjJ501pRtu', '郑浩', null, '202204002101', '15793569464', '202204002101@stu.edu.cn', null, 'GRADUATE', '2', '1', '0', '2026-08-19 10:09:46', '2026-08-19 11:20:24');
INSERT INTO `sys_user` VALUES ('41', '202204002202', '$2b$10$kN./zF7JZNtMtkwP2E.ypuedQfuL6UbaH7GAIdFLTqfFjJ501pRtu', '梁宇', null, '202204002202', '17218375262', '202204002202@stu.edu.cn', null, 'GRADUATE', '2', '1', '0', '2026-08-19 10:09:46', '2026-08-19 11:20:24');
INSERT INTO `sys_user` VALUES ('42', '202204002303', '$2b$10$kN./zF7JZNtMtkwP2E.ypuedQfuL6UbaH7GAIdFLTqfFjJ501pRtu', '谢飞', null, '202204002303', '13644062193', '202204002303@stu.edu.cn', null, 'GRADUATE', '2', '1', '0', '2026-08-19 10:09:46', '2026-08-19 11:20:24');
INSERT INTO `sys_user` VALUES ('43', '202205002401', '$2b$10$kN./zF7JZNtMtkwP2E.ypuedQfuL6UbaH7GAIdFLTqfFjJ501pRtu', '宋佳', null, '202205002401', '18424033287', '202205002401@stu.edu.cn', null, 'GRADUATE', '2', '1', '0', '2026-08-19 10:09:46', '2026-08-19 11:20:24');
INSERT INTO `sys_user` VALUES ('44', '202205002502', '$2b$10$kN./zF7JZNtMtkwP2E.ypuedQfuL6UbaH7GAIdFLTqfFjJ501pRtu', '唐毅', null, '202205002502', '13423628545', '202205002502@stu.edu.cn', null, 'GRADUATE', '2', '1', '0', '2026-08-19 10:09:46', '2026-08-19 11:20:24');
INSERT INTO `sys_user` VALUES ('45', '202205002603', '$2b$10$kN./zF7JZNtMtkwP2E.ypuedQfuL6UbaH7GAIdFLTqfFjJ501pRtu', '韩雪', null, '202205002603', '17392489037', '202205002603@stu.edu.cn', null, 'GRADUATE', '2', '1', '0', '2026-08-19 10:09:46', '2026-08-19 11:20:24');
INSERT INTO `sys_user` VALUES ('46', '202206002701', '$2b$10$kN./zF7JZNtMtkwP2E.ypuedQfuL6UbaH7GAIdFLTqfFjJ501pRtu', '冯刚', null, '202206002701', '19504922266', '202206002701@stu.edu.cn', null, 'GRADUATE', '3', '1', '0', '2026-08-19 10:09:46', '2026-08-19 11:20:24');
INSERT INTO `sys_user` VALUES ('47', '202206002802', '$2b$10$kN./zF7JZNtMtkwP2E.ypuedQfuL6UbaH7GAIdFLTqfFjJ501pRtu', '董倩', null, '202206002802', '17622304880', '202206002802@stu.edu.cn', null, 'GRADUATE', '3', '1', '0', '2026-08-19 10:09:46', '2026-08-19 11:20:24');
INSERT INTO `sys_user` VALUES ('48', '202206002903', '$2b$10$kN./zF7JZNtMtkwP2E.ypuedQfuL6UbaH7GAIdFLTqfFjJ501pRtu', '萧然', null, '202206002903', '17480484126', '202206002903@stu.edu.cn', null, 'GRADUATE', '3', '1', '0', '2026-08-19 10:09:46', '2026-08-19 11:20:24');
INSERT INTO `sys_user` VALUES ('49', '202206003004', '$2b$10$kN./zF7JZNtMtkwP2E.ypuedQfuL6UbaH7GAIdFLTqfFjJ501pRtu', '程亮', null, '202206003004', '15161755277', '202206003004@stu.edu.cn', null, 'GRADUATE', '3', '1', '0', '2026-08-19 10:09:46', '2026-08-19 11:20:24');
INSERT INTO `sys_user` VALUES ('50', '202206003105', '$2b$10$kN./zF7JZNtMtkwP2E.ypuedQfuL6UbaH7GAIdFLTqfFjJ501pRtu', '曹阳', null, '202206003105', '13155062661', '202206003105@stu.edu.cn', null, 'GRADUATE', '3', '1', '0', '2026-08-19 10:09:46', '2026-08-19 11:20:24');
INSERT INTO `sys_user` VALUES ('51', '202206003201', '$2b$10$kN./zF7JZNtMtkwP2E.ypuedQfuL6UbaH7GAIdFLTqfFjJ501pRtu', '袁媛', null, '202206003201', '17160809314', '202206003201@stu.edu.cn', null, 'GRADUATE', '3', '1', '0', '2026-08-19 10:09:46', '2026-08-19 11:20:24');
INSERT INTO `sys_user` VALUES ('52', '202206003302', '$2b$10$kN./zF7JZNtMtkwP2E.ypuedQfuL6UbaH7GAIdFLTqfFjJ501pRtu', '邓超', null, '202206003302', '15473899181', '202206003302@stu.edu.cn', null, 'GRADUATE', '3', '1', '0', '2026-08-19 10:09:46', '2026-08-19 11:20:24');
INSERT INTO `sys_user` VALUES ('53', '202207003401', '$2b$10$kN./zF7JZNtMtkwP2E.ypuedQfuL6UbaH7GAIdFLTqfFjJ501pRtu', '许晴', null, '202207003401', '13659258258', '202207003401@stu.edu.cn', null, 'GRADUATE', '3', '1', '0', '2026-08-19 10:09:46', '2026-08-19 11:20:24');
INSERT INTO `sys_user` VALUES ('54', '202207003502', '$2b$10$kN./zF7JZNtMtkwP2E.ypuedQfuL6UbaH7GAIdFLTqfFjJ501pRtu', '傅敏', null, '202207003502', '18386981473', '202207003502@stu.edu.cn', null, 'GRADUATE', '3', '1', '0', '2026-08-19 10:09:46', '2026-08-19 11:20:24');
INSERT INTO `sys_user` VALUES ('55', '202207003603', '$2b$10$kN./zF7JZNtMtkwP2E.ypuedQfuL6UbaH7GAIdFLTqfFjJ501pRtu', '沈静', null, '202207003603', '18130429835', '202207003603@stu.edu.cn', null, 'GRADUATE', '3', '1', '0', '2026-08-19 10:09:46', '2026-08-19 11:20:24');
INSERT INTO `sys_user` VALUES ('56', '202208003701', '$2b$10$kN./zF7JZNtMtkwP2E.ypuedQfuL6UbaH7GAIdFLTqfFjJ501pRtu', '曾强', null, '202208003701', '19148433388', '202208003701@stu.edu.cn', null, 'GRADUATE', '4', '1', '0', '2026-08-19 10:09:46', '2026-08-19 11:20:24');
INSERT INTO `sys_user` VALUES ('57', '202208003802', '$2b$10$kN./zF7JZNtMtkwP2E.ypuedQfuL6UbaH7GAIdFLTqfFjJ501pRtu', '彭磊', null, '202208003802', '15907351577', '202208003802@stu.edu.cn', null, 'GRADUATE', '4', '1', '0', '2026-08-19 10:09:46', '2026-08-19 11:20:24');
INSERT INTO `sys_user` VALUES ('58', '202208003903', '$2b$10$kN./zF7JZNtMtkwP2E.ypuedQfuL6UbaH7GAIdFLTqfFjJ501pRtu', '吕倩', null, '202208003903', '17706574104', '202208003903@stu.edu.cn', null, 'GRADUATE', '4', '1', '0', '2026-08-19 10:09:46', '2026-08-19 11:20:24');
INSERT INTO `sys_user` VALUES ('59', '202208004004', '$2b$10$kN./zF7JZNtMtkwP2E.ypuedQfuL6UbaH7GAIdFLTqfFjJ501pRtu', '苏航', null, '202208004004', '19333960475', '202208004004@stu.edu.cn', null, 'GRADUATE', '4', '1', '0', '2026-08-19 10:09:46', '2026-08-19 11:20:24');
INSERT INTO `sys_user` VALUES ('60', '202208004105', '$2b$10$kN./zF7JZNtMtkwP2E.ypuedQfuL6UbaH7GAIdFLTqfFjJ501pRtu', '卢俊', null, '202208004105', '13760779934', '202208004105@stu.edu.cn', null, 'GRADUATE', '4', '1', '0', '2026-08-19 10:09:46', '2026-08-19 11:20:24');
INSERT INTO `sys_user` VALUES ('61', '202209004201', '$2b$10$kN./zF7JZNtMtkwP2E.ypuedQfuL6UbaH7GAIdFLTqfFjJ501pRtu', '蒋欣', null, '202209004201', '18290134343', '202209004201@stu.edu.cn', null, 'GRADUATE', '4', '1', '0', '2026-08-19 10:09:46', '2026-08-19 11:20:24');
INSERT INTO `sys_user` VALUES ('62', '202209004302', '$2b$10$kN./zF7JZNtMtkwP2E.ypuedQfuL6UbaH7GAIdFLTqfFjJ501pRtu', '蔡宇', null, '202209004302', '13914416798', '202209004302@stu.edu.cn', null, 'GRADUATE', '4', '1', '0', '2026-08-19 10:09:46', '2026-08-19 11:20:24');
INSERT INTO `sys_user` VALUES ('63', '202209004403', '$2b$10$kN./zF7JZNtMtkwP2E.ypuedQfuL6UbaH7GAIdFLTqfFjJ501pRtu', '贾琳', null, '202209004403', '15718181335', '202209004403@stu.edu.cn', null, 'GRADUATE', '4', '1', '0', '2026-08-19 10:09:46', '2026-08-19 11:20:24');
INSERT INTO `sys_user` VALUES ('64', '202209004504', '$2b$10$kN./zF7JZNtMtkwP2E.ypuedQfuL6UbaH7GAIdFLTqfFjJ501pRtu', '丁伟', null, '202209004504', '15521848326', '202209004504@stu.edu.cn', null, 'GRADUATE', '4', '1', '0', '2026-08-19 10:09:46', '2026-08-19 11:20:24');
INSERT INTO `sys_user` VALUES ('65', '202209004605', '$2b$10$kN./zF7JZNtMtkwP2E.ypuedQfuL6UbaH7GAIdFLTqfFjJ501pRtu', '魏然', null, '202209004605', '17172509449', '202209004605@stu.edu.cn', null, 'GRADUATE', '4', '1', '0', '2026-08-19 10:09:46', '2026-08-19 11:20:24');
INSERT INTO `sys_user` VALUES ('66', '202210004701', '$2b$10$kN./zF7JZNtMtkwP2E.ypuedQfuL6UbaH7GAIdFLTqfFjJ501pRtu', '薛峰', null, '202210004701', '18188083396', '202210004701@stu.edu.cn', null, 'GRADUATE', '5', '1', '0', '2026-08-19 10:09:46', '2026-08-19 11:20:24');
INSERT INTO `sys_user` VALUES ('67', '202210004802', '$2b$10$kN./zF7JZNtMtkwP2E.ypuedQfuL6UbaH7GAIdFLTqfFjJ501pRtu', '叶静', null, '202210004802', '18693287231', '202210004802@stu.edu.cn', null, 'GRADUATE', '5', '1', '0', '2026-08-19 10:09:46', '2026-08-19 11:20:24');
INSERT INTO `sys_user` VALUES ('68', '202210004903', '$2b$10$kN./zF7JZNtMtkwP2E.ypuedQfuL6UbaH7GAIdFLTqfFjJ501pRtu', '阎凯', null, '202210004903', '18857042312', '202210004903@stu.edu.cn', null, 'GRADUATE', '5', '1', '0', '2026-08-19 10:09:46', '2026-08-19 11:20:24');
INSERT INTO `sys_user` VALUES ('69', '202210005004', '$2b$10$kN./zF7JZNtMtkwP2E.ypuedQfuL6UbaH7GAIdFLTqfFjJ501pRtu', '余华', null, '202210005004', '18185773665', '202210005004@stu.edu.cn', null, 'GRADUATE', '5', '1', '0', '2026-08-19 10:09:46', '2026-08-19 11:20:24');
INSERT INTO `sys_user` VALUES ('70', '202210005105', '$2b$10$kN./zF7JZNtMtkwP2E.ypuedQfuL6UbaH7GAIdFLTqfFjJ501pRtu', '潘婷', null, '202210005105', '18720711016', '202210005105@stu.edu.cn', null, 'GRADUATE', '5', '1', '0', '2026-08-19 10:09:46', '2026-08-19 11:20:24');
INSERT INTO `sys_user` VALUES ('71', '202211005201', '$2b$10$kN./zF7JZNtMtkwP2E.ypuedQfuL6UbaH7GAIdFLTqfFjJ501pRtu', '杜宇', null, '202211005201', '15403255797', '202211005201@stu.edu.cn', null, 'GRADUATE', '6', '1', '0', '2026-08-19 10:09:46', '2026-08-19 11:20:24');
INSERT INTO `sys_user` VALUES ('72', '202211005302', '$2b$10$kN./zF7JZNtMtkwP2E.ypuedQfuL6UbaH7GAIdFLTqfFjJ501pRtu', '戴琳', null, '202211005302', '15196537241', '202211005302@stu.edu.cn', null, 'GRADUATE', '6', '1', '0', '2026-08-19 10:09:46', '2026-08-19 11:20:24');
INSERT INTO `sys_user` VALUES ('73', '202211005403', '$2b$10$kN./zF7JZNtMtkwP2E.ypuedQfuL6UbaH7GAIdFLTqfFjJ501pRtu', '夏磊', null, '202211005403', '13799284563', '202211005403@stu.edu.cn', null, 'GRADUATE', '6', '1', '0', '2026-08-19 10:09:46', '2026-08-19 11:20:24');
INSERT INTO `sys_user` VALUES ('74', '202211005504', '$2b$10$kN./zF7JZNtMtkwP2E.ypuedQfuL6UbaH7GAIdFLTqfFjJ501pRtu', '钟声', null, '202211005504', '18520049793', '202211005504@stu.edu.cn', null, 'GRADUATE', '6', '1', '0', '2026-08-19 10:09:46', '2026-08-19 11:20:24');
INSERT INTO `sys_user` VALUES ('75', '202211005605', '$2b$10$kN./zF7JZNtMtkwP2E.ypuedQfuL6UbaH7GAIdFLTqfFjJ501pRtu', '汪洋', null, '202211005605', '18549828295', '202211005605@stu.edu.cn', null, 'GRADUATE', '6', '1', '0', '2026-08-19 10:09:46', '2026-08-19 11:20:24');
INSERT INTO `sys_user` VALUES ('76', '202212005701', '$2b$10$kN./zF7JZNtMtkwP2E.ypuedQfuL6UbaH7GAIdFLTqfFjJ501pRtu', '田甜', null, '202212005701', '13279294058', '202212005701@stu.edu.cn', null, 'GRADUATE', '7', '1', '0', '2026-08-19 10:09:46', '2026-08-19 11:20:24');
INSERT INTO `sys_user` VALUES ('77', '202212005802', '$2b$10$kN./zF7JZNtMtkwP2E.ypuedQfuL6UbaH7GAIdFLTqfFjJ501pRtu', '任杰', null, '202212005802', '19190088283', '202212005802@stu.edu.cn', null, 'GRADUATE', '7', '1', '0', '2026-08-19 10:09:46', '2026-08-19 11:20:24');
INSERT INTO `sys_user` VALUES ('78', '202212005903', '$2b$10$kN./zF7JZNtMtkwP2E.ypuedQfuL6UbaH7GAIdFLTqfFjJ501pRtu', '姜涛', null, '202212005903', '18236328991', '202212005903@stu.edu.cn', null, 'GRADUATE', '7', '1', '0', '2026-08-19 10:09:46', '2026-08-19 11:20:24');
INSERT INTO `sys_user` VALUES ('79', '202212006004', '$2b$10$kN./zF7JZNtMtkwP2E.ypuedQfuL6UbaH7GAIdFLTqfFjJ501pRtu', '范斌', null, '202212006004', '19576287423', '202212006004@stu.edu.cn', null, 'GRADUATE', '7', '1', '0', '2026-08-19 10:09:46', '2026-08-19 14:04:26');
INSERT INTO `sys_user` VALUES ('80', '202383501033', '$2a$10$NST44gY75T2XWgnmM2YUw.CdSi4FwwXQy5RYqfnfQkBofNPjihODW', '覃玲甜', null, '202383501033', '', '', null, 'GRADUATE', '1', '1', '0', '2026-08-25 09:55:47', '2026-08-28 09:50:48');
INSERT INTO `sys_user` VALUES ('81', 'T20230001', '$2a$10$Xr6JMg0ZnmHfTBTpPSotQ.14FOjka/7sXedTHbFvuZ.1cnprw6phG', '李老师', null, null, '', '', null, 'TEACHER', '1', '1', '0', '2026-08-25 09:57:28', '2026-08-25 09:57:28');
INSERT INTO `sys_user` VALUES ('82', '2022120161', '$2a$10$mCS14pbri/gHMpGoblcWuu2zffYaW1aiuustLO6XNJJyLZVcSNmJe', '董明明', null, null, '13900001211', 'dongming0101@stu.college.edu.cn', null, 'GRADUATE', '7', '1', '0', '2026-08-26 10:21:45', '2026-08-26 10:21:45');
INSERT INTO `sys_user` VALUES ('83', '2022120162', '$2a$10$CdTHy/KbiJkt5SRNjwsOdOo5AL/3erfd/Y32ANeZ6aWzCV1oveJS6', '袁圆圆', null, null, '13900001222', 'yuanyuan0202@stu.college.edu.cn', null, 'GRADUATE', '7', '1', '0', '2026-08-26 10:21:45', '2026-08-26 10:21:45');
INSERT INTO `sys_user` VALUES ('84', '2022120163', '$2a$10$i/8o96S01ECWBVr.hM94YeGaCBmkLuGnCFOqopxvTReaDPpiKnNb.', '金鑫鑫', null, null, '13900001233', 'jinxinxin03@stu.college.edu.cn', null, 'GRADUATE', '7', '1', '0', '2026-08-26 10:21:45', '2026-08-26 10:21:45');
