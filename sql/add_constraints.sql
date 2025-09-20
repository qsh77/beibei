-- 添加数据库约束和触发器，确保数据完整性
-- 用于贝贝家政服务平台

USE beibei;

-- 创建触发器：当插入user时自动创建对应的user_profile记录
DROP TRIGGER IF EXISTS user_profile_auto_create;

DELIMITER $$

CREATE TRIGGER user_profile_auto_create
AFTER INSERT ON user
FOR EACH ROW
BEGIN
    -- 自动为新用户创建默认的user_profile记录
    INSERT INTO user_profile (user_id, name, gender, birthday, avatar, email, address)
    VALUES (NEW.id, '用户', 'U', NULL, NULL, NULL, NULL);
END$$

DELIMITER ;

-- 为worker角色的用户创建触发器（可选）
-- 注意：这个触发器可能需要根据业务需求调整
DROP TRIGGER IF EXISTS worker_profile_check;

-- 添加索引优化查询性能（MySQL 8.0+语法）
CREATE INDEX idx_user_profile_user_id ON user_profile(user_id);
CREATE INDEX idx_user_phone ON user(phone);
CREATE INDEX idx_user_role_status ON user(role, status);

-- 验证数据完整性的存储过程
DROP PROCEDURE IF EXISTS check_user_profile_integrity;

DELIMITER $$

CREATE PROCEDURE check_user_profile_integrity()
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE user_id_var BIGINT;
    DECLARE cur CURSOR FOR
        SELECT u.id
        FROM user u
        LEFT JOIN user_profile up ON u.id = up.user_id
        WHERE up.user_id IS NULL;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

    -- 创建临时表记录问题
    CREATE TEMPORARY TABLE IF NOT EXISTS missing_profiles (
        user_id BIGINT,
        phone VARCHAR(20),
        role VARCHAR(10)
    );

    OPEN cur;
    read_loop: LOOP
        FETCH cur INTO user_id_var;
        IF done THEN
            LEAVE read_loop;
        END IF;

        -- 插入缺失的profile记录
        INSERT INTO user_profile (user_id, name, gender, birthday, avatar, email, address)
        VALUES (user_id_var, '用户', 'U', NULL, NULL, NULL, NULL);

        -- 记录修复的用户
        INSERT INTO missing_profiles (user_id, phone, role)
        SELECT u.id, u.phone, u.role
        FROM user u
        WHERE u.id = user_id_var;

    END LOOP;
    CLOSE cur;

    -- 显示修复结果
    SELECT '修复完成，以下用户已补充profile记录:' AS message;
    SELECT * FROM missing_profiles;

    DROP TEMPORARY TABLE IF EXISTS missing_profiles;
END$$

DELIMITER ;

-- 执行数据完整性检查
CALL check_user_profile_integrity();

-- 验证所有用户都有profile记录
SELECT
    '数据完整性检查结果:' AS message,
    COUNT(u.id) AS total_users,
    COUNT(up.user_id) AS users_with_profile,
    CASE
        WHEN COUNT(u.id) = COUNT(up.user_id) THEN '✅ 数据完整'
        ELSE '❌ 存在缺失数据'
    END AS status
FROM user u
LEFT JOIN user_profile up ON u.id = up.user_id;