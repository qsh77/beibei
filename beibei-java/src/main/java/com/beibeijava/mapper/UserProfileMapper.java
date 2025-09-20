package com.beibeijava.mapper;

import com.beibeijava.entity.UserProfile;
import org.apache.ibatis.annotations.*;

/**
 * 用户资料数据访问层
 */
@Mapper
public interface UserProfileMapper {

    /**
     * 根据用户ID查询用户资料
     */
    @Select("SELECT * FROM user_profile WHERE user_id = #{userId}")
    UserProfile selectById(@Param("userId") Long userId);

    /**
     * 插入用户资料（如果用户ID已存在则更新）
     */
    @Insert("INSERT INTO user_profile (user_id, name, gender, birthday, email, address) " +
            "VALUES (#{userId}, #{name}, #{gender}, #{birthday}, #{email}, #{address}) " +
            "ON DUPLICATE KEY UPDATE " +
            "name = VALUES(name), " +
            "gender = VALUES(gender), " +
            "birthday = VALUES(birthday), " +
            "email = VALUES(email), " +
            "address = VALUES(address)")
    int insert(UserProfile userProfile);
    
    /**
     * 更新用户资料
     */
    @Update("UPDATE user_profile SET name = #{name}, gender = #{gender}, birthday = #{birthday}, " +
            "avatar = #{avatar}, email = #{email}, address = #{address} WHERE user_id = #{userId}")
    int update(UserProfile userProfile);
}
