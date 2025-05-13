-- 清空现有数据
DELETE FROM reservations;
DELETE FROM venues;

-- 重置自增ID
ALTER TABLE venues AUTO_INCREMENT = 1;
ALTER TABLE reservations AUTO_INCREMENT = 1;

-- 插入场馆数据
INSERT INTO venues (name, type, description, is_available, location, capacity, price_per_hour, status)
VALUES 
('篮球场A', 'basketball', '标准篮球场', 1, '体育馆一楼', 20, 100.00, 'ACTIVE'),
('篮球场B', 'basketball', '标准篮球场', 1, '体育馆一楼', 20, 100.00, 'ACTIVE'),
('羽毛球场A', 'badminton', '标准羽毛球场', 1, '体育馆二楼', 4, 50.00, 'ACTIVE'),
('羽毛球场B', 'badminton', '标准羽毛球场', 1, '体育馆二楼', 4, 50.00, 'ACTIVE'),
('网球场A', 'tennis', '标准网球场', 1, '体育馆三楼', 4, 80.00, 'ACTIVE'),
('网球场B', 'tennis', '标准网球场', 1, '体育馆三楼', 4, 80.00, 'ACTIVE'),
('游泳池', 'swimming', '标准游泳池', 1, '体育馆负一楼', 50, 30.00, 'ACTIVE'),
('乒乓球室A', 'table_tennis', '标准乒乓球室', 1, '体育馆四楼', 4, 20.00, 'ACTIVE'),
('乒乓球室B', 'table_tennis', '标准乒乓球室', 1, '体育馆四楼', 4, 20.00, 'ACTIVE'); 