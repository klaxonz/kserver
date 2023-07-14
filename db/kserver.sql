/*
 Navicat Premium Data Transfer

 Source Server         : kserver
 Source Server Type    : MySQL
 Source Server Version : 80031 (8.0.31)
 Source Host           : localhost:13306
 Source Schema         : kserver

 Target Server Type    : MySQL
 Target Server Version : 80031 (8.0.31)
 File Encoding         : 65001

 Date: 11/03/2023 16:39:38
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_account
-- ----------------------------
DROP TABLE IF EXISTS `t_account`;
CREATE TABLE `t_account`  (
  `id` bigint NOT NULL COMMENT '主键 id',
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '电子邮箱',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_template
-- ----------------------------
DROP TABLE IF EXISTS `t_template`;
CREATE TABLE `t_template`  (
  `id` bigint NOT NULL COMMENT '主键 id',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_webpage
-- ----------------------------
DROP TABLE IF EXISTS `t_web_page`;
CREATE TABLE `t_web_page`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `user_id` bigint NOT NULL COMMENT '用户 ID',
  `url` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '链接',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '标题',
  `is_star` tinyint NOT NULL default 0 COMMENT '是否星标 0-否 1-是',
  `is_delete` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除 0-否 1-是',
  `content` text NOT NULL COMMENT '网页内容',
  `source` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '来源',
  `favicon` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '网站icon',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1631314602740539394 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '网页表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_web_page_task
-- ----------------------------
DROP TABLE IF EXISTS `t_web_page_task`;
CREATE TABLE `t_web_page_task`  (
   `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键 id',
   `user_id` bigint NOT NULL COMMENT '用户id',
   `web_page_id` bigint NOT NULL COMMENT '网页id',
   `video_progress` int NOT NULL DEFAULT 0 COMMENT '视频下载进度',
   `video_downloaded_size` bigint NOT NULL DEFAULT 0 COMMENT '视频已下载字节大小',
   `file_path` varchar(255) NOT NULL DEFAULT '' COMMENT '文件路径',
   `thumbnail_path` varchar(255) NOT NULL DEFAULT '' COMMENT '缩略图路径',
   `video_size` bigint NOT NULL DEFAULT 0 COMMENT '视频大小',
   `video_duration` int NOT NULL DEFAULT 0 COMMENT '视频时长',
   `type` int NOT NULL DEFAULT 0 COMMENT '视频类型',
   `status` int NOT NULL DEFAULT 0 COMMENT '视频下载状态',
   `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
   `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
   PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS `t_web_page_video_task`;
CREATE TABLE `t_web_page_video_task`  (
   `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键 id',
   `task_id` bigint NOT NULL COMMENT '任务id',
   `user_id` bigint NOT NULL COMMENT '用户id',
   `web_page_id` bigint NOT NULL COMMENT '网页id',
   `video_progress` int NOT NULL DEFAULT 0 COMMENT '视频下载进度',
   `audio_progress` int NOT NULL DEFAULT 0 COMMENT '音频下载进度',
   `video_length` bigint NOT NULL DEFAULT 0 COMMENT '视频文件大小',
   `audio_length` bigint NOT NULL DEFAULT 0 COMMENT '音频文件大小',
   `video_downloaded_length` bigint NOT NULL DEFAULT 0 COMMENT '视频已下载字节大小',
   `audio_downloaded_length` bigint NOT NULL DEFAULT 0 COMMENT '音频已下载字节大小',
   `video_path` varchar(1000) NOT NULL DEFAULT '' COMMENT '视频存储路径',
   `audio_path` varchar(1000) NOT NULL DEFAULT '' COMMENT '音频存储路径',
   `file_path` varchar(1000) NOT NULL DEFAULT '' COMMENT '文件存储路径',
   `thumbnail_path` varchar(1000) NOT NULL DEFAULT '' COMMENT '缩略图存储路径',
   `video_size` bigint NOT NULL DEFAULT 0 COMMENT '文件大小',
   `video_duration` int NOT NULL DEFAULT 0 COMMENT '视频时长',
   `is_merge` tinyint NOT NULL DEFAULT 0 COMMENT  '是否合并 0-否 1-是',
   `width` int NOT NULL DEFAULT 0 COMMENT '分辨率宽',
   `height` int NOT NULL DEFAULT 0 COMMENT '分辨率长',
   `video_index` int NOT NULL DEFAULT 0 COMMENT '视频顺序',
   `title` varchar(255) NOT NULL DEFAULT '' COMMENT '视频标题',
   `type` tinyint NOT NULL DEFAULT 0 COMMENT '视频类型',
   `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
   `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
   PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;


SET FOREIGN_KEY_CHECKS = 1;
