-- 修改start_time和end_time字段的长度
ALTER TABLE reservations MODIFY COLUMN start_time VARCHAR(50);
ALTER TABLE reservations MODIFY COLUMN end_time VARCHAR(50); 