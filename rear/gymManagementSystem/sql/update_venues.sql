-- 添加 status 列
ALTER TABLE venues ADD COLUMN status varchar(20) DEFAULT 'ACTIVE';

-- 添加 created_at 列
ALTER TABLE venues ADD COLUMN created_at datetime DEFAULT CURRENT_TIMESTAMP;
 
-- 添加 updated_at 列
ALTER TABLE venues ADD COLUMN updated_at datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP; 