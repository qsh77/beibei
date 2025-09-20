-- 更新用户资料，添加更完整的个人信息
-- 使用前请确保已经连接到 beibei 数据库

USE beibei;

-- 更新管理员用户的资料（ID=1）
UPDATE user_profile SET
  email = 'admin@beibei.com',
  birthday = '1990-01-01',
  address = '北京市朝阳区管理中心大厦'
WHERE user_id = 1;

-- 更新用户张女士的资料（ID=2）
UPDATE user_profile SET
  email = 'zhang@example.com',
  birthday = '1985-03-15',
  address = '北京市海淀区中关村软件园'
WHERE user_id = 2;

-- 更新阿姨李阿姨的资料（ID=3）
UPDATE user_profile SET
  email = 'li@example.com',
  birthday = '1978-08-20',
  address = '北京市丰台区南三环中路'
WHERE user_id = 3;

-- 检查更新结果
SELECT
  up.user_id,
  up.name,
  up.gender,
  up.birthday,
  up.email,
  up.address,
  up.avatar,
  u.phone,
  u.role
FROM user_profile up
JOIN user u ON up.user_id = u.id
ORDER BY up.user_id;