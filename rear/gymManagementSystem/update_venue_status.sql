-- 更新所有场地状态为NORMAL
UPDATE venues SET status = 'NORMAL' WHERE status = 'ACTIVE'; 