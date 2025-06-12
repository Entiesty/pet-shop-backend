-- 额外的MySQL店铺数据
-- 确保MySQL中的stores表与MongoDB中的地理位置数据匹配

USE `petshop_db`;

-- 添加更多店铺数据
INSERT INTO stores (id, name, address_text, logo_url, contact_phone, created_at, updated_at) VALUES
(4, '爱宠之家 (朝阳店)', '北京市朝阳区建国门外大街88号', 'https://example.com/logos/store4.png', '010-88884444', NOW(), NOW()),
(5, '萌宠乐园 (海淀店)', '北京市海淀区中关村大街115号', 'https://example.com/logos/store5.png', '010-88885555', NOW(), NOW()),
(6, '宠物天地 (西城店)', '北京市西城区西单北大街131号', 'https://example.com/logos/store6.png', '010-88886666', NOW(), NOW());

-- 验证数据
SELECT id, name, address_text FROM stores WHERE id IN (1,2,3,4,5,6);

-- 显示插入的店铺信息
SELECT 
    id,
    name,
    address_text,
    CONCAT('店铺ID: ', id, ', 店名: ', name) as store_info
FROM stores 
WHERE id >= 1 AND id <= 6
ORDER BY id; 