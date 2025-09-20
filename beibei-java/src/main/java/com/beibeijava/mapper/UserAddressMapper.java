package com.beibeijava.mapper;

import com.beibeijava.entity.UserAddress;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 用户地址数据访问层
 */
@Mapper
public interface UserAddressMapper {
    
    /**
     * 根据用户ID查询地址列表
     */
    @Select("SELECT * FROM user_address WHERE user_id = #{userId} ORDER BY is_default DESC, created_at DESC")
    List<UserAddress> findByUserId(@Param("userId") Long userId);
    
    /**
     * 根据ID查询地址
     */
    @Select("SELECT * FROM user_address WHERE id = #{id}")
    UserAddress findById(@Param("id") Long id);
    
    /**
     * 查询用户的默认地址
     */
    @Select("SELECT * FROM user_address WHERE user_id = #{userId} AND is_default = 1 LIMIT 1")
    UserAddress findDefaultByUserId(@Param("userId") Long userId);
    
    /**
     * 插入地址
     */
    @Insert("INSERT INTO user_address (user_id, contact_name, contact_phone, province, city, district, detail, is_default, created_at) " +
            "VALUES (#{userId}, #{contactName}, #{contactPhone}, #{province}, #{city}, #{district}, #{detail}, #{isDefault}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(UserAddress userAddress);
    
    /**
     * 更新地址
     */
    @Update("UPDATE user_address SET contact_name = #{contactName}, contact_phone = #{contactPhone}, " +
            "province = #{province}, city = #{city}, district = #{district}, detail = #{detail}, " +
            "is_default = #{isDefault} WHERE id = #{id}")
    int update(UserAddress userAddress);
    
    /**
     * 删除地址
     */
    @Delete("DELETE FROM user_address WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
    
    /**
     * 取消用户的所有默认地址
     */
    @Update("UPDATE user_address SET is_default = 0 WHERE user_id = #{userId}")
    int clearDefaultByUserId(@Param("userId") Long userId);
    
    /**
     * 设置默认地址
     */
    @Update("UPDATE user_address SET is_default = 1 WHERE id = #{id}")
    int setDefaultById(@Param("id") Long id);
}
