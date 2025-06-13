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

-- 确保您正在操作正确的数据库
USE `petshop_db`;

-- 禁用外键检查并清空旧的分类数据
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE `categories`;
SET FOREIGN_KEY_CHECKS = 1;


-- 插入全新的三级分类数据
INSERT INTO `categories` (`id`, `name`, `parent_id`, `sort_order`) VALUES
-- =================================================
-- 大类 (Top-Level Categories)
-- =================================================
(1, '活体宠物', 0, 10),
(2, '宠物食品', 0, 20),
(3, '日常用品', 0, 30),
(4, '医疗保健', 0, 40),

-- =================================================
-- 中类 (Mid-Level Categories)
-- =================================================
-- --- 活体宠物 (父ID: 1) ---
(101, '狗狗', 1, 1),
(102, '猫咪', 1, 2),
(103, '小宠/异宠', 1, 3),

-- --- 宠物食品 (父ID: 2) ---
(201, '犬粮', 2, 1),
(202, '猫粮', 2, 2),
(203, '宠物零食', 2, 3),
(204, '湿粮/罐头', 2, 4),

-- --- 日常用品 (父ID: 3) ---
(301, '宠物玩具', 3, 1),
(302, '清洁美容', 3, 2),
(303, '出行装备', 3, 3),
(304, '猫砂/厕所', 3, 4),

-- --- 医疗保健 (父ID: 4) ---
(401, '内外驱虫', 4, 1),
(402, '营养保健', 4, 2),


-- =================================================
-- 小类 (Sub-Level Categories)
-- =================================================
-- --- 狗狗 (父ID: 101) ---
(10101, '拉布拉多', 101, 0),
(10102, '金毛寻回犬', 101, 0),
(10103, '柯基', 101, 0),
(10104, '萨摩耶', 101, 0),
(10105, '比熊', 101, 0),
(10106, '柴犬', 101, 0),

-- --- 猫咪 (父ID: 102) ---
(10201, '英国短毛猫', 102, 0),
(10202, '布偶猫', 102, 0),
(10203, '美国短毛猫', 102, 0),
(10204, '橘猫', 102, 0),

-- --- 小宠/异宠 (父ID: 103) ---
(10301, '兔子', 103, 0),
(10302, '仓鼠', 103, 0),
(10303, '龙猫', 103, 0);


-- ===============================================================
-- PetShop Project - Categories & Products Schema & Test Data
-- ===============================================================

-- 确保您正在操作正确的数据库
USE `petshop_db`;

-- 禁用外键检查，以便顺利删除和创建表
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 1. 表结构定义 (DDL)
-- ----------------------------

-- 分类表
DROP TABLE IF EXISTS `categories`;
CREATE TABLE `categories` (
                              `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分类ID',
                              `name` varchar(50) NOT NULL COMMENT '分类名称',
                              `parent_id` bigint NOT NULL DEFAULT '0' COMMENT '父分类ID (0表示顶级分类)',
                              `icon_url` varchar(255) DEFAULT NULL COMMENT '分类图标URL',
                              `sort_order` int DEFAULT '0' COMMENT '排序值',
                              PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=402 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品与宠物分类表';

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
                            KEY `idx_category_id` (`category_id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品表';


-- ----------------------------
-- 2. 插入测试数据 (Test Data Insertion)
-- ----------------------------

-- 插入分类数据
INSERT INTO `categories` (`id`, `name`, `parent_id`, `sort_order`) VALUES
                                                                       (1, '活体宠物', 0, 10),
                                                                       (2, '宠物主粮', 0, 20),
                                                                       (3, '宠物零食', 0, 30),
                                                                       (4, '日用玩具', 0, 40),
                                                                       (5, '医疗保健', 0, 50),
                                                                       (101, '狗狗专区', 1, 1),
                                                                       (102, '猫咪专区', 1, 2),
                                                                       (103, '小宠专区', 1, 3),
                                                                       (201, '犬粮', 2, 1),
                                                                       (202, '猫粮', 2, 2),
                                                                       (301, '磨牙洁齿', 3, 1),
                                                                       (302, '肉干/肉条', 3, 2),
                                                                       (303, '猫条/湿粮包', 3, 3),
                                                                       (401, '益智玩具', 4, 1),
                                                                       (402, '牵引绳/胸背带', 4, 2),
                                                                       (403, '猫抓板/猫爬架', 4, 3),
                                                                       (501, '内外驱虫', 5, 1),
                                                                       (502, '营养膏/化毛膏', 5, 2);


-- 插入商品数据
INSERT INTO `products` (`id`, `store_id`, `category_id`, `name`, `breed`, `age`, `sex`, `weight`, `color`, `description`, `health_info`, `price`, `stock`, `main_image_url`) VALUES
-- 活体宠物 (ID: 1-6)
(1, 1, 101, '阳光-金毛寻回犬', '金毛寻回犬', '8个月', '雄性', 25.00, '淡金色', '阳光是一只非常可爱的金毛幼犬，性格温顺，已完成基础训练。', '{"疫苗":"已完成三针", "驱虫":"已完成"}', 6800.00, 1, 'http://47.122.155.110:9000/petshop/dog_golden.jpg'),
(2, 2, 102, '月光-布偶猫', '布偶猫', '5个月', '雌性', 2.50, '海豹双色', '性格温顺粘人，被称为“小狗猫”，是理想的室内伴侣。', '{"疫苗":"已完成两针", "驱虫":"已完成"}', 8500.00, 1, 'http://47.122.155.110:9000/petshop/cat_ragdoll.jpg'),
(3, 1, 101, '豆豆-柯基犬', '柯基犬', '1岁', '雄性', 12.00, '黄白双色', '精力充沛的小短腿，聪明活泼，笑容治愈。', '{"疫苗":"已全部完成", "驱虫":"已完成"}', 7500.00, 1, 'http://47.122.155.110:9000/petshop/dog_corgi.jpg'),
(4, 3, 102, '煤球-英国短毛猫', '英国短毛猫', '4个月', '雄性', 2.20, '蓝色', '体格强壮，好奇心强，喜欢与人亲近，经典的蓝猫。', '{"疫苗":"已完成首针", "驱虫":"已完成"}', 4500.00, 1, 'http://47.122.155.110:9000/petshop/cat_british.jpg'),
(5, 1, 101, '将军-柴犬', '柴犬', '6个月', '雄性', 8.50, '赤色', '独立的思考者，表情包大户，非常忠诚。', '{"疫苗":"已全部完成", "驱虫":"已完成"}', 6000.00, 1, 'http://47.122.155.110:9000/petshop/dog_shiba.jpg'),
(6, 2, 103, '团子-侏儒兔', '侏儒兔', '5个月', '雌性', 1.10, '白色', '体型小巧，安静可爱，适合公寓饲养。', '{"疫苗":"已完成", "驱虫":"已完成"}', 800.00, 1, 'http://47.122.155.110:9000/petshop/rabbit_dwarf.jpg'),
-- 宠物用品 (ID: 7-15)
(7, 1, 201, '皇家大型犬幼犬粮 15kg', NULL, NULL, NULL, NULL, NULL, '专为大型犬幼犬设计的营养犬粮，支持骨骼与关节健康。', NULL, 589.00, 75, 'http://47.122.155.110:9000/petshop/food_dog_royal.jpg'),
(8, 2, 202, '渴望Orijen全阶段猫粮 5.4kg', NULL, NULL, NULL, NULL, NULL, '采用85%优质肉类含量，符合猫的生理天性，提供全面营养。', NULL, 620.00, 40, 'http://47.122.155.110:9000/petshop/food_cat_orijen.jpg'),
(9, 1, 301, '倍林格 洁齿棒中大型犬适用 28支装', NULL, NULL, NULL, NULL, NULL, '独特的Z字造型，有效清洁牙齿，减少牙垢和牙菌斑。', NULL, 128.00, 90, 'http://47.122.155.110:9000/petshop/snack_dog_greenies.jpg'),
(10, 2, 303, 'CIAO 啾噜系列猫条 20支装', NULL, NULL, NULL, NULL, NULL, '日本进口，高含肉量，美味与补水一次满足。', NULL, 45.00, 200, 'http://47.122.155.110:9000/petshop/snack_cat_ciao.jpg'),
(11, 1, 401, 'KONG 经典葫芦玩具-中号', NULL, NULL, NULL, NULL, NULL, '经典的红色天然橡胶不倒翁玩具，耐咬且可填充零食。', NULL, 88.00, 150, 'http://47.122.155.110:9000/petshop/toy_dog_kong.jpg'),
(12, 2, 403, '剑麻猫抓板（含猫薄荷）', NULL, NULL, NULL, NULL, NULL, '高密度瓦楞纸，耐抓耐磨，附赠天然猫薄荷，吸引猫咪玩耍。', NULL, 29.90, 300, 'http://47.122.155.110:9000/petshop/toy_cat_scratcher.jpg'),
(13, 3, 501, '福来恩Flea-Tick 猫用体外驱虫滴剂 3支', NULL, NULL, NULL, NULL, NULL, '每月一支，有效预防和杀灭跳蚤、蜱虫等体外寄生虫。', NULL, 145.00, 65, 'http://47.122.155.110:9000/petshop/med_cat_frontline.jpg'),
(14, 3, 402, 'Truelove 舒适胸背带 L码', NULL, NULL, NULL, NULL, NULL, '采用牛津布材质，内衬柔软网布，舒适透气，带有反光条，保障夜间出行安全。', NULL, 159.00, 85, 'http://47.122.155.110:9000/petshop/gear_dog_harness.jpg'),
(15, 2, 502, '骏宝GimCat麦芽化毛膏 50g', NULL, NULL, NULL, NULL, NULL, '德国进口，帮助猫咪温和排出体内毛球，预防毛球症。', NULL, 58.00, 110, 'http://47.122.155.110:9000/petshop/med_cat_hairball.jpg');

-- 重新启用外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- 确保您正在操作正确的数据库
USE `petshop_db`;

-- 插入24条商店测试数据
INSERT INTO `stores` (`id`, `name`, `address_text`, `logo_url`, `contact_phone`) VALUES
-- 厦门 (Xiamen)
(101, '鼓浪屿猫咪咖啡馆', '厦门市思明区鼓浪屿龙头路123号', 'http://47.122.155.110:9000/petshop/logo_xm_1.png', '0592-2060111'),
(102, '宠物之家(莲前东路店)', '厦门市思明区莲前东路567号', 'http://47.122.155.110:9000/petshop/logo_xm_2.png', '0592-5987222'),
(103, '萌宠乐园(湖里万达店)', '厦门市湖里区仙岳路4666号', 'http://47.122.155.110:9000/petshop/logo_xm_3.png', '0592-3338444'),

-- 上海 (Shanghai)
(201, '汪先生的店(静安寺店)', '上海市静安区南京西路1601号', 'http://47.122.155.110:9000/petshop/logo_sh_1.png', '021-62881234'),
(202, '喵星人俱乐部(新天地店)', '上海市黄浦区太仓路181弄', 'http://47.122.155.110:9000/petshop/logo_sh_2.png', '021-63865678'),
(203, 'PETSMART(陆家嘴中心店)', '上海市浦东新区浦东南路899号', 'http://47.122.155.110:9000/petshop/logo_sh_3.png', '021-58889012'),

-- 北京 (Beijing)
(301, '汪汪之家 (三里屯旗舰店)', '北京市朝阳区三里屯路19号院', 'http://47.122.155.110:9000/petshop/logo_bj_1.png', '010-64161111'),
(302, '爱宠族(中关村店)', '北京市海淀区中关村大街27号', 'http://47.122.155.110:9000/petshop/logo_bj_2.png', '010-82622222'),
(303, '宠物总动员 (后海店)', '北京市西城区地安门外大街57号', 'http://47.122.155.110:9000/petshop/logo_bj_3.png', '010-64043333'),

-- 广州 (Guangzhou)
(401, '萌宠大本营(天河城店)', '广州市天河区天河路208号', 'http://47.122.155.110:9000/petshop/logo_gz_1.png', '020-85591111'),
(402, 'Pet Paradise(珠江新城店)', '广州市天河区珠江东路6号', 'http://47.122.155.110:9000/petshop/logo_gz_2.png', '020-38682222'),
(403, '友宠生活馆(北京路店)', '广州市越秀区北京路步行街238号', 'http://47.122.155.110:9000/petshop/logo_gz_3.png', '020-83333333'),

-- 深圳 (Shenzhen)
(501, '宠物时光(海岸城店)', '深圳市南山区文心五路33号', 'http://47.122.155.110:9000/petshop/logo_sz_1.png', '0755-86121111'),
(502, '它博它博宠物(万象天地店)', '深圳市南山区深南大道9668号', 'http://47.122.155.110:9000/petshop/logo_sz_2.png', '0755-86682222'),
(503, '极宠家(福田中心店)', '深圳市福田区福华三路', 'http://47.122.155.110:9000/petshop/logo_sz_3.png', '0755-23993333'),

-- 杭州 (Hangzhou)
(601, '猫的天空之城(西湖店)', '杭州市上城区湖滨路步行街55号', 'http://47.122.155.110:9000/petshop/logo_hz_1.png', '0571-87021111'),
(602, '爪子宠物(滨江龙湖天街店)', '杭州市滨江区江汉路1515号', 'http://47.122.155.110:9000/petshop/logo_hz_2.png', '0571-86622222'),
(603, '爱宠有家(城西银泰店)', '杭州市拱墅区丰潭路380号', 'http://47.122.155.110:9000/petshop/logo_hz_3.png', '0571-88173333'),

-- 南京 (Nanjing)
(701, '宠儿宠物生活馆(新街口店)', '南京市秦淮区中山南路1号', 'http://47.122.155.110:9000/petshop/logo_nj_1.png', '025-84721111'),
(702, '顽皮天使(夫子庙店)', '南京市秦淮区贡院西街122号', 'http://47.122.155.110:9000/petshop/logo_nj_2.png', '025-52302222'),
(703, '萌主驾到(仙林大学城店)', '南京市栖霞区文苑路2号', 'http://47.122.155.110:9000/petshop/logo_nj_3.png', '025-85793333'),

-- 成都 (Chengdu)
(801, '熊猫与宠物(春熙路店)', '成都市锦江区春熙路步行街', 'http://47.122.155.110:9000/petshop/logo_cd_1.png', '028-86651111'),
(802, '爱之家动物救助中心', '成都市双流区万顺路二段', 'http://47.122.155.110:9000/petshop/logo_cd_2.png', '028-85852222'),
(803, '佩奇宠物(太古里店)', '成都市锦江区中纱帽街8号', 'http://47.122.155.110:9000/petshop/logo_cd_3.png', '028-65963333');