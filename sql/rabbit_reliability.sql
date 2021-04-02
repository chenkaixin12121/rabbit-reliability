/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 50718
 Source Host           : localhost:3306
 Source Schema         : rabbit_reliability

 Target Server Type    : MySQL
 Target Server Version : 50718
 File Encoding         : 65001

 Date: 28/11/2020 15:12:01
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for msg_log
-- ----------------------------
DROP TABLE IF EXISTS `msg_log`;
CREATE TABLE `msg_log`  (
  `msg_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键id',
  `msg` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '消息体, json格式化',
  `exchange` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '交换机',
  `routing_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '路由键',
  `status` int(11) NOT NULL DEFAULT 0 COMMENT '状态: 0-发送中 1-发送成功 2-发送失败 3-消费成功 4-消费失败',
  `try_count` int(11) NULL DEFAULT 0 COMMENT '重试次数',
  `next_try_time` datetime(0) NULL DEFAULT NULL COMMENT '下一次重试时间',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`msg_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '消息投递日志' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
