-- 数据库更新脚本
-- 添加商品类型字段来区分活体宠物和宠物用品

-- 1. 给products表添加product_type字段
ALTER TABLE `products` ADD COLUMN `product_type` tinyint NOT NULL DEFAULT '1' COMMENT '商品类型 (1-活体宠物, 2-宠物用品)' AFTER `category_id`;

-- 2. 更新现有商品的product_type
-- 将活体宠物商品设为type=1
UPDATE `products` SET `product_type` = 1 WHERE `category_id` IN (101, 102); -- 猫咪、狗狗

-- 将宠物用品商品设为type=2  
UPDATE `products` SET `product_type` = 2 WHERE `category_id` IN (201, 202); -- 宠物主粮、宠物玩具

-- 3. 插入更多测试数据以便测试
INSERT INTO `products` (`store_id`, `category_id`, `product_type`, `name`, `breed`, `age`, `sex`, `weight`, `color`, `description`, `health_info`, `price`, `stock`) VALUES
-- 更多活体宠物
(1, 101, 1, '小雪球', '英短蓝猫', '3个月', '雌性', 1.20, '蓝色', '非常可爱的英短小猫咪，性格活泼', '{"疫苗":"已完成一针", "驱虫":"已完成"}', 3500.00, 1),
(2, 101, 1, '橘子', '橘猫', '6个月', '雄性', 2.80, '橘色', '性格温顺的大橘猫，很亲人', '{"疫苗":"已完成两针", "驱虫":"已完成"}', 2800.00, 1),
(3, 102, 1, '小黑', '拉布拉多', '4个月', '雄性', 15.00, '黑色', '聪明活泼的拉布拉多幼犬', '{"疫苗":"已完成三针", "驱虫":"已完成"}', 4200.00, 1),
(1, 102, 1, '贝贝', '比熊', '7个月', '雌性', 3.50, '白色', '可爱的小比熊，毛发蓬松', '{"疫苗":"已完成三针", "驱虫":"已完成"}', 5500.00, 1),

-- 更多宠物用品
(2, 201, 2, '希尔思猫粮2kg', NULL, NULL, NULL, NULL, NULL, '高品质成猫粮，营养均衡', NULL, 158.00, 80),
(3, 201, 2, '皇家小型犬成犬粮1.5kg', NULL, NULL, NULL, NULL, NULL, '专为小型犬设计的营养狗粮', NULL, 95.00, 60),
(1, 202, 2, '猫抓板', NULL, NULL, NULL, NULL, NULL, '天然剑麻猫抓板，保护家具', NULL, 35.00, 200),
(2, 202, 2, '狗绳牵引带', NULL, NULL, NULL, NULL, NULL, '可调节长度的宠物牵引带', NULL, 45.00, 150),
(3, 202, 2, '宠物窝垫', NULL, NULL, NULL, NULL, NULL, '柔软舒适的宠物睡垫', NULL, 68.00, 100);

-- 4. 添加更多分类（可选）
INSERT INTO `categories` (`id`, `name`, `parent_id`) VALUES
(103, '兔子', 1),
(104, '鸟类', 1),
(203, '宠物零食', 2),
(204, '宠物用具', 2); 