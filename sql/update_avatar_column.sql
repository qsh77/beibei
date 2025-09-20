-- 修改用户资料表的头像字段长度
-- 将 VARCHAR(255) 改为 TEXT 以支持 base64 图片数据

USE beibei;

-- 修改 avatar 字段类型
ALTER TABLE user_profile MODIFY COLUMN avatar TEXT;

-- 验证修改结果
DESCRIBE user_profile;