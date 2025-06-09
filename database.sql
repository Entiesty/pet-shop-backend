-- 用户表
CREATE TABLE `users` (
                         `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
                         `username` varchar(50) NOT NULL UNIQUE COMMENT '用户名',
                         `password` varchar(255) NOT NULL COMMENT '加密后的密码',
                         `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
                         `avatar_url` varchar(255) DEFAULT NULL COMMENT '头像URL',
                         `openid` varchar(100) DEFAULT NULL UNIQUE COMMENT '第三方登录ID',
                         `role` tinyint(1) NOT NULL DEFAULT '0' COMMENT '角色 (0-会员, 1-管理员)',
                         `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                         `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                         PRIMARY KEY (`id`)
) ENGINE=InnoDB COMMENT='用户表';

-- 收货地址表
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
                           `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                           `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                           PRIMARY KEY (`id`),
                           KEY `idx_user_id` (`user_id`),
                           FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB COMMENT='收货地址表';

-- 商店表
CREATE TABLE `stores` (
                          `id` bigint NOT NULL AUTO_INCREMENT COMMENT '商店ID',
                          `name` varchar(100) NOT NULL COMMENT '商店名称',
                          `address_text` varchar(255) NOT NULL COMMENT '文本地址',
                          `logo_url` varchar(255) DEFAULT NULL COMMENT '商店Logo',
                          `contact_phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
                          `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                          `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                          PRIMARY KEY (`id`)
) ENGINE=InnoDB COMMENT='商店表';

-- 商品表
CREATE TABLE `products` (
                            `id` bigint NOT NULL AUTO_INCREMENT COMMENT '商品ID',
                            `store_id` bigint NOT NULL COMMENT '所属商店ID',
                            `name` varchar(100) NOT NULL COMMENT '商品名称',
                            `description` text COMMENT '商品描述',
                            `price` decimal(10,2) NOT NULL COMMENT '价格',
                            `product_type` tinyint(1) NOT NULL COMMENT '商品类型 (1-宠物, 2-周边)',
                            `stock` int NOT NULL DEFAULT '1' COMMENT '库存 (宠物为1, 周边>0)',
                            `main_image_url` varchar(255) DEFAULT NULL COMMENT '主图URL',
                            `video_url` varchar(255) DEFAULT NULL COMMENT '介绍视频URL',
                            `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                            PRIMARY KEY (`id`),
                            KEY `idx_store_id` (`store_id`),
                            FOREIGN KEY (`store_id`) REFERENCES `stores` (`id`)
) ENGINE=InnoDB COMMENT='商品表';

-- 订单表
CREATE TABLE `orders` (
                          `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单ID',
                          `order_no` varchar(64) NOT NULL UNIQUE COMMENT '订单号',
                          `user_id` bigint NOT NULL COMMENT '用户ID',
                          `total_amount` decimal(10,2) NOT NULL COMMENT '订单总金额',
                          `status` int NOT NULL DEFAULT '10' COMMENT '订单状态(10-待付款, 20-待发货, 30-待收货, 40-已完成, 0-已取消)',
                          `address_id` bigint NOT NULL COMMENT '收货地址ID',
                          `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                          `payment_time` datetime DEFAULT NULL COMMENT '支付时间',
                          `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                          PRIMARY KEY (`id`),
                          KEY `idx_user_id` (`user_id`),
                          KEY `idx_order_no` (`order_no`)
) ENGINE=InnoDB COMMENT='订单表';

-- 订单项表
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

-- 购物车表
CREATE TABLE `shopping_cart` (
                                 `id` bigint NOT NULL AUTO_INCREMENT COMMENT '购物车项ID',
                                 `user_id` bigint NOT NULL COMMENT '用户ID',
                                 `product_id` bigint NOT NULL COMMENT '商品ID',
                                 `quantity` int NOT NULL COMMENT '商品数量',
                                 `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                 `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                 PRIMARY KEY (`id`),
                                 KEY `idx_user_id` (`user_id`),
                                 UNIQUE KEY `uk_user_product` (`user_id`, `product_id`) COMMENT '用户和商品的组合唯一索引，防止重复添加'
) ENGINE=InnoDB COMMENT='购物车表';

-- 评价表
CREATE TABLE `reviews` (
                           `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评价ID',
                           `user_id` bigint NOT NULL COMMENT '用户ID',
                           `product_id` bigint NOT NULL COMMENT '商品ID',
                           `order_id` bigint NOT NULL COMMENT '订单ID，用于验证购买行为',
                           `rating` tinyint NOT NULL COMMENT '评分 (例如1-5星)',
                           `content` text COMMENT '评价内容',
                           `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                           `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                           PRIMARY KEY (`id`),
                           KEY `idx_product_id` (`product_id`),
                           KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB COMMENT='商品评价表';

-- 确保你正在操作正确的数据库
USE petshop_db;

-- 插入与MongoDB中 storeId: 1, 2, 3 对应的商店详细信息
-- 注意：我们显式地指定了ID，以确保与MongoDB中的数据匹配

INSERT INTO stores (id, name, address_text, logo_url, contact_phone, created_at, updated_at) VALUES
                                                                                                 (1, '汪汪之家 (西直门店)', '北京市西城区西直门外大街1号院', 'https://example.com/logos/store1.png', '010-88881111', NOW(), NOW()),
                                                                                                 (2, '喵星人俱乐部 (中关村店)', '北京市海淀区中关村大街27号', 'https://example.com/logos/store2.png', '010-88882222', NOW(), NOW()),
                                                                                                 (3, '宠物总动员 (国贸店)', '北京市朝阳区建国门外大街1号', 'https://example.com/logos/store3.png', '010-88883333', NOW(), NOW());
-- 确保您在 petshop_db 数据库中
INSERT INTO products (id, store_id, name, description, price, product_type, stock, main_image_url, created_at, updated_at)
VALUES (101, 1, '耐嚼磨牙棒', '一款非常耐咬的宠物磨牙零食', 25.50, 2, 100, 'http://example.com/products/p101.png', NOW(), NOW());

UPDATE users SET role = 1 WHERE username = 'testuser01';