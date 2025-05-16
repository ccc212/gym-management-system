-- 使用正确的数据库
USE gym_db;

-- 删除指定的预约记录
DELETE FROM reservations WHERE id IN (6,7,8,9,10,11,12,13,14,15);
 
-- 验证删除结果
SELECT * FROM reservations WHERE id IN (6,7,8,9,10,11,12,13,14,15); 