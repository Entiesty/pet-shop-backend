-- 创建数据库
CREATE DATABASE IF NOT EXISTS `petshop_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE `petshop_db`;

-- ----------------------------
-- 1. 创建表 (Table Creation)
-- ----------------------------

-- 用户表
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
                         `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
                         `username` varchar(50) NOT NULL UNIQUE COMMENT '用户名',
                         `password` varchar(255) NOT NULL COMMENT '加密后的密码',
                         `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
                         `email` varchar(100) DEFAULT NULL UNIQUE COMMENT '电子邮箱',
                         `avatar_url` varchar(255) DEFAULT NULL COMMENT '头像URL',
                         `role` tinyint(1) NOT NULL DEFAULT '0' COMMENT '角色 (0-会员, 1-管理员)',
                         `balance` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '用户余额/代币',
                         `openid` varchar(100) DEFAULT NULL UNIQUE COMMENT '第三方登录ID',
                         `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                         `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                         PRIMARY KEY (`id`)
) ENGINE=InnoDB COMMENT='用户表';

-- 收货地址表
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
                           KEY `idx_user_id` (`user_id`),
                           FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB COMMENT='收货地址表';

-- 商店表
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

-- 分类表
DROP TABLE IF EXISTS `categories`;
CREATE TABLE `categories` (
                              `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分类ID',
                              `name` varchar(50) NOT NULL COMMENT '分类名称',
                              `parent_id` bigint NOT NULL DEFAULT '0' COMMENT '父分类ID (0表示顶级分类)',
                              `icon_url` varchar(255) DEFAULT NULL COMMENT '分类图标URL',
                              `sort_order` int DEFAULT '0' COMMENT '排序值',
                              PRIMARY KEY (`id`)
) ENGINE=InnoDB COMMENT='商品与宠物分类表';

-- 商品表
DROP TABLE IF EXISTS `products`;
CREATE TABLE `products` (
                            `id` bigint NOT NULL AUTO_INCREMENT COMMENT '商品ID',
                            `store_id` bigint NOT NULL COMMENT '所属商店ID',
                            `category_id` bigint DEFAULT NULL COMMENT '所属分类ID',
                            `name` varchar(100) NOT NULL COMMENT '商品名称',
                            `breed` varchar(50) DEFAULT NULL COMMENT '品种',
                            `age` varchar(30) DEFAULT NULL COMMENT '年龄',
                            `sex` varchar(10) DEFAULT NULL COMMENT '性别',
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
                            CONSTRAINT `fk_store_id` FOREIGN KEY (`store_id`) REFERENCES `stores` (`id`),
                            CONSTRAINT `fk_category_id` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`)
) ENGINE=InnoDB COMMENT='商品表';

-- 评价表
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

-- 购物车表
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

-- 订单表
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

-- 订单项表
DROP TABLE IF EXISTS `order_items`;
CREATE TABLE `order_items` (
                               `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单项ID',
                               `order_id` bigint NOT NULL COMMENT '订单ID',
                               `product_id` bigint NOT NULL COMMENT '商品ID',
                               `quantity` int NOT NULL COMMENT '购买数量',
                               `unit_price` decimal(10,2) NOT NULL COMMENT '购买时单价',
                               PRIMARY KEY (`id`),
                               KEY `idx_order_id` (`order_id`),
                               FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB COMMENT='订单项表';


-- ----------------------------
-- 2. 插入测试数据 (Test Data Insertion)
-- ----------------------------

-- 插入用户 (密码都是 password123, 经过BCrypt加密)
INSERT INTO `users` (`id`, `username`, `password`, `nickname`, `email`, `role`, `balance`) VALUES
                                                                                               (1, 'admin', '$2a$10$tP.q.1sXQ5xX/5zFj0o4.u1d2j/3u0p2uF.5t5uH7qK8bO2k0j.C.i', '管理员', 'admin@petshop.com', 1, 9999.00),
                                                                                               (2, 'user01', '$2a$10$tP.q.1sXQ5xX/5zFj0o4.u1d2j/3u0p2uF.5t5uH7qK8bO2k0j.C.i', '张三', 'zhangsan@qq.com', 0, 500.00);

-- 插入地址
INSERT INTO `address` (`id`, `user_id`, `contact_name`, `phone`, `province`, `city`, `district`, `street`, `is_default`) VALUES
                                                                                                                             (1, 2, '张三', '13800138000', '上海市', '上海市', '浦东新区', '世纪大道100号', 1),
                                                                                                                             (2, 2, '张三备用', '13900139000', '北京市', '北京市', '海淀区', '中关村大街1号', 0);

-- 插入商店
INSERT INTO stores (id, name, address_text, logo_url, contact_phone, created_at, updated_at) VALUES
                                                                                                 (1, '汪汪之家 (西直门店)', '北京市西城区西直门外大街1号院', 'https://example.com/logos/store1.png', '010-88881111', NOW(), NOW()),
                                                                                                 (2, '喵星人俱乐部 (中关村店)', '北京市海淀区中关村大街27号', 'https://example.com/logos/store2.png', '010-88882222', NOW(), NOW()),
                                                                                                 (3, '宠物总动员 (国贸店)', '北京市朝阳区建国门外大街1号', 'https://example.com/logos/store3.png', '010-88883333', NOW(), NOW());

-- 插入分类
INSERT INTO `categories` (`id`, `name`, `parent_id`) VALUES
                                                         (1, '活体宠物', 0), (2, '宠物用品', 0),
                                                         (101, '猫咪', 1), (102, '狗狗', 1),
                                                         (201, '宠物主粮', 2), (202, '宠物玩具', 2);

-- 插入商品
INSERT INTO `products` (`id`, `store_id`, `category_id`, `name`, `breed`, `age`, `sex`, `weight`, `color`, `description`, `health_info`, `price`, `stock`) VALUES
                                                                                                                                                               (1, 1, 102, '阳光', '金毛寻回犬', '8个月', '雄性', 25.00, '淡金色', '阳光是一只非常可爱的金毛幼犬...', '{"疫苗":"已完成三针", "驱虫":"已完成"}', 6800.00, 1),
                                                                                                                                                               (2, 2, 101, '月光', '布偶猫', '5个月', '雌性', 2.50, '蓝双色', '性格温顺粘人，被称为“小狗猫”。', '{"疫苗":"已完成两针", "驱虫":"已完成"}', 8500.00, 1),
                                                                                                                                                               (3, 1, 201, '皇家大型犬幼犬粮3kg', NULL, NULL, NULL, NULL, NULL, '专为大型犬幼犬设计的营养犬粮', NULL, 189.00, 50),
                                                                                                                                                               (4, 2, 202, 'KONG 经典葫芦玩具', NULL, NULL, NULL, NULL, NULL, '经典的红色天然橡胶玩具，耐咬且可填充零食。', NULL, 78.00, 120);

-- 注意：购物车、订单、评价等数据建议通过调用API进行测试来生成，以确保流程的完整性。
INSERT INTO `stores` (`id`, `name`, `address_text`, `logo_url`, `contact_phone`) VALUES
                                                                                     (10, 'ペットのコジマ 大井町店', '東京都品川区大井１丁目５０−５', 'https://example.com/logos/kojima.png', '03-5742-7111'),
                                                                                     (11, 'P''s-first 品川シーサイド店', '東京都品川区東品川４丁目１２−６', 'https://example.com/logos/psfirst.png', '03-5783-4111'),
                                                                                     (12, '犬ごころ ららぽーと豊洲店', '東京都江東区豊洲２丁目４−９', 'https://example.com/logos/inugokoro.png', '03-6910-1331');
ALTER TABLE `users`
    ADD COLUMN `phone` VARCHAR(20) NULL UNIQUE COMMENT '手机号码' AFTER `email`;