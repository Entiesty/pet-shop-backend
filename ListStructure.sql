-- ===============================================================
-- PetShop Project - Final Database Schema
-- ===============================================================

-- 创建并使用数据库
CREATE DATABASE IF NOT EXISTS `petshop_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `petshop_db`;

-- 禁用外键检查，以便顺利删除和创建表
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 用户表 (users)
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
                         `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
                         `username` varchar(50) NOT NULL UNIQUE COMMENT '用户名',
                         `password` varchar(255) NOT NULL COMMENT '加密后的密码',
                         `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
                         `email` varchar(100) DEFAULT NULL UNIQUE COMMENT '电子邮箱',
                         `phone` varchar(20) DEFAULT NULL UNIQUE COMMENT '手机号码',
                         `avatar_url` varchar(255) DEFAULT NULL COMMENT '头像URL',
                         `role` tinyint(1) NOT NULL DEFAULT '0' COMMENT '角色 (0-会员, 1-管理员)',
                         `balance` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '用户余额/代币',
                         `openid` varchar(100) DEFAULT NULL UNIQUE COMMENT '第三方登录ID',
                         `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                         `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                         PRIMARY KEY (`id`)
) ENGINE=InnoDB COMMENT='用户表';

-- ----------------------------
-- 收货地址表 (address)
-- ----------------------------
DROP TABLE IF EXISTS `address`;
CREATE TABLE `address` (
                           `id` bigint NOT NULL AUTO_INCREMENT COMMENT '地址ID',
                           `user_id` bigint NOT NULL COMMENT '用户ID',
                           `contact_name` varchar(50) NOT NULL COMMENT '联系人',
                           `phone` varchar(20) NOT NULL COMMENT '手机号',
                           `province` varchar(50) NOT NULL COMMENT '省份',
                           `city` varchar(50) NOT NULL COMMENT '城市',
                           `district` varchar(50) NOT NULL COMMENT '区/县',
                           `street` varchar(255) NOT NULL COMMENT '街道/详细地址',
                           `is_default` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否为默认地址',
                           `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                           `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                           PRIMARY KEY (`id`),
                           CONSTRAINT `fk_address_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB COMMENT='收货地址表';

-- ----------------------------
-- 商店表 (stores)
-- ----------------------------
DROP TABLE IF EXISTS `stores`;
CREATE TABLE `stores` (
                          `id` bigint NOT NULL AUTO_INCREMENT COMMENT '商店ID',
                          `name` varchar(100) NOT NULL COMMENT '商店名称',
                          `address_text` varchar(255) NOT NULL COMMENT '文本地址',
                          `logo_url` varchar(255) DEFAULT NULL COMMENT '商店Logo',
                          `contact_phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
                          `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                          `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                          PRIMARY KEY (`id`)
) ENGINE=InnoDB COMMENT='商店表';

-- ----------------------------
-- 分类表 (categories)
-- ----------------------------
DROP TABLE IF EXISTS `categories`;
CREATE TABLE `categories` (
                              `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分类ID',
                              `name` varchar(50) NOT NULL COMMENT '分类名称',
                              `parent_id` bigint NOT NULL DEFAULT '0' COMMENT '父分类ID (0表示顶级分类)',
                              `icon_url` varchar(255) DEFAULT NULL COMMENT '分类图标URL',
                              `sort_order` int DEFAULT '0' COMMENT '排序值',
                              PRIMARY KEY (`id`)
) ENGINE=InnoDB COMMENT='商品与宠物分类表';

-- ----------------------------
-- 商品表 (products)
-- ----------------------------
DROP TABLE IF EXISTS `products`;
CREATE TABLE `products` (
                            `id` bigint NOT NULL AUTO_INCREMENT COMMENT '商品ID',
                            `store_id` bigint NOT NULL COMMENT '所属商店ID',
                            `category_id` bigint DEFAULT NULL COMMENT '所属分类ID',
                            `name` varchar(100) NOT NULL COMMENT '商品名称',
                            `breed` varchar(50) DEFAULT NULL COMMENT '品种',
                            `age` varchar(30) DEFAULT NULL COMMENT '年龄',
                            `sex` enum('雄性','雌性') DEFAULT NULL COMMENT '性别',
                            `weight` decimal(5,2) DEFAULT NULL COMMENT '体重 (kg)',
                            `color` varchar(30) DEFAULT NULL COMMENT '颜色',
                            `description` text COMMENT '商品描述',
                            `health_info` text COMMENT '健康信息 (可用JSON或格式化文本存储)',
                            `price` decimal(10,2) NOT NULL COMMENT '价格',
                            `stock` int NOT NULL DEFAULT '1' COMMENT '库存',
                            `main_image_url` varchar(255) DEFAULT NULL COMMENT '主图URL',
                            `video_url` varchar(255) DEFAULT NULL COMMENT '介绍视频URL',
                            `video_gen_status` varchar(20) DEFAULT 'NONE' COMMENT '视频生成状态 (NONE, PENDING, SUCCEEDED, FAILED)',
                            `average_rating` decimal(3,1) NOT NULL DEFAULT '0.0' COMMENT '平均评分',
                            `review_count` int NOT NULL DEFAULT '0' COMMENT '评价总数',
                            `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                            PRIMARY KEY (`id`),
                            KEY `idx_store_id` (`store_id`),
                            KEY `idx_category_id` (`category_id`),
                            CONSTRAINT `fk_products_store_id` FOREIGN KEY (`store_id`) REFERENCES `stores` (`id`),
                            CONSTRAINT `fk_products_category_id` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`)
) ENGINE=InnoDB COMMENT='商品表';

-- ----------------------------
-- 评价表 (reviews)
-- ----------------------------
DROP TABLE IF EXISTS `reviews`;
CREATE TABLE `reviews` (
                           `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评价ID',
                           `user_id` bigint NOT NULL COMMENT '用户ID',
                           `product_id` bigint NOT NULL COMMENT '商品ID',
                           `order_id` bigint NOT NULL COMMENT '订单ID，用于验证购买行为',
                           `rating` tinyint NOT NULL COMMENT '评分 (例如1-5星)',
                           `content` text COMMENT '评价内容',
                           `image_urls` text COMMENT '评价图片URL列表 (JSON数组格式)',
                           `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                           `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                           PRIMARY KEY (`id`),
                           KEY `idx_product_id` (`product_id`),
                           KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB COMMENT='商品评价表';

-- ----------------------------
-- 购物车表 (shopping_cart)
-- ----------------------------
DROP TABLE IF EXISTS `shopping_cart`;
CREATE TABLE `shopping_cart` (
                                 `id` bigint NOT NULL AUTO_INCREMENT COMMENT '购物车项ID',
                                 `user_id` bigint NOT NULL COMMENT '用户ID',
                                 `product_id` bigint NOT NULL COMMENT '商品ID',
                                 `quantity` int NOT NULL COMMENT '商品数量',
                                 `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                 `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                 PRIMARY KEY (`id`),
                                 KEY `idx_user_id` (`user_id`),
                                 UNIQUE KEY `uk_user_product` (`user_id`,`product_id`)
) ENGINE=InnoDB COMMENT='购物车表';

-- ----------------------------
-- 订单表 (orders)
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders` (
                          `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单ID',
                          `order_no` varchar(64) NOT NULL UNIQUE COMMENT '订单号',
                          `user_id` bigint NOT NULL COMMENT '用户ID',
                          `total_amount` decimal(10,2) NOT NULL COMMENT '订单总金额',
                          `status` int NOT NULL DEFAULT '10' COMMENT '订单状态(10-待付款, 20-待发货, 30-待收货, 40-已完成, 0-已取消)',
                          `address_id` bigint NOT NULL COMMENT '收货地址ID',
                          `payment_time` timestamp NULL DEFAULT NULL COMMENT '支付时间',
                          `shipping_carrier` varchar(50) DEFAULT NULL COMMENT '物流公司',
                          `tracking_number` varchar(100) DEFAULT NULL COMMENT '物流单号',
                          `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                          `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                          PRIMARY KEY (`id`),
                          KEY `idx_user_id` (`user_id`),
                          KEY `idx_order_no` (`order_no`)
) ENGINE=InnoDB COMMENT='订单表';

-- ----------------------------
-- 订单项表 (order_items)
-- ----------------------------
DROP TABLE IF EXISTS `order_items`;
CREATE TABLE `order_items` (
                               `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单项ID',
                               `order_id` bigint NOT NULL COMMENT '订单ID',
                               `product_id` bigint NOT NULL COMMENT '商品ID',
                               `quantity` int NOT NULL COMMENT '购买数量',
                               `unit_price` decimal(10,2) NOT NULL COMMENT '购买时单价',
                               PRIMARY KEY (`id`),
                               KEY `idx_order_id` (`order_id`),
                               CONSTRAINT `fk_order_items_order_id` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB COMMENT='订单项表';


-- 重新启用外键检查
SET FOREIGN_KEY_CHECKS = 1;