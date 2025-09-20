package com.beibeijava.mapper;

import com.beibeijava.entity.User;
import com.beibeijava.vo.UserDetailVO;
import com.beibeijava.vo.UserListVO;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户数据访问层
 */
@Mapper
public interface UserMapper {

    /**
     * 根据手机号查询用户
     */
    @Select("SELECT * FROM user WHERE phone = #{phone} AND status = 1")
    User findByPhone(@Param("phone") String phone);

    /**
     * 根据ID查询用户
     */
    @Select("SELECT * FROM user WHERE id = #{id}")
    User findById(@Param("id") Long id);

    /**
     * 检查手机号是否已存在
     */
    @Select("SELECT COUNT(*) FROM user WHERE phone = #{phone}")
    int countByPhone(@Param("phone") String phone);

    /**
     * 插入用户
     */
    @Insert("INSERT INTO user (phone, password_hash, role, status, created_at, updated_at) " +
            "VALUES (#{phone}, #{passwordHash}, #{role}, #{status}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    /**
     * 更新用户密码
     */
    @Update("UPDATE user SET password_hash = #{passwordHash}, updated_at = #{updatedAt} WHERE id = #{id}")
    int updatePassword(@Param("id") Long id, @Param("passwordHash") String passwordHash, @Param("updatedAt") java.time.LocalDateTime updatedAt);

    // ===== 管理员用户管理方法 =====

    /**
     * 分页查询用户列表
     */
    @Select("<script>" +
            "SELECT " +
            "u.id, u.phone, u.role, u.status, u.created_at, " +
            "up.name, up.avatar, " +
            "COUNT(o.id) as total_orders " +
            "FROM user u " +
            "LEFT JOIN user_profile up ON u.id = up.user_id " +
            "LEFT JOIN `order` o ON u.id = o.customer_id " +
            "WHERE 1=1 " +
            "<if test='keyword != null and keyword != \"\"'>" +
            "AND (u.phone LIKE CONCAT('%', #{keyword}, '%') OR up.name LIKE CONCAT('%', #{keyword}, '%')) " +
            "</if>" +
            "<if test='role != null and role != \"\"'>" +
            "AND u.role = #{role} " +
            "</if>" +
            "<if test='status != null'>" +
            "AND u.status = #{status} " +
            "</if>" +
            "<if test='startDate != null and startDate != \"\"'>" +
            "AND u.created_at >= #{startDate} " +
            "</if>" +
            "<if test='endDate != null and endDate != \"\"'>" +
            "AND u.created_at &lt;= #{endDate} " +
            "</if>" +
            "GROUP BY u.id, u.phone, u.role, u.status, u.created_at, up.name, up.avatar " +
            "ORDER BY u.created_at DESC " +
            "LIMIT #{offset}, #{size}" +
            "</script>")
    List<UserListVO> findUserList(@Param("keyword") String keyword,
                                  @Param("role") String role,
                                  @Param("status") Integer status,
                                  @Param("startDate") String startDate,
                                  @Param("endDate") String endDate,
                                  @Param("offset") Integer offset,
                                  @Param("size") Integer size);

    /**
     * 查询用户总数
     */
    @Select("<script>" +
            "SELECT COUNT(DISTINCT u.id) " +
            "FROM user u " +
            "LEFT JOIN user_profile up ON u.id = up.user_id " +
            "WHERE 1=1 " +
            "<if test='keyword != null and keyword != \"\"'>" +
            "AND (u.phone LIKE CONCAT('%', #{keyword}, '%') OR up.name LIKE CONCAT('%', #{keyword}, '%')) " +
            "</if>" +
            "<if test='role != null and role != \"\"'>" +
            "AND u.role = #{role} " +
            "</if>" +
            "<if test='status != null'>" +
            "AND u.status = #{status} " +
            "</if>" +
            "<if test='startDate != null and startDate != \"\"'>" +
            "AND u.created_at >= #{startDate} " +
            "</if>" +
            "<if test='endDate != null and endDate != \"\"'>" +
            "AND u.created_at &lt;= #{endDate} " +
            "</if>" +
            "</script>")
    Long countUsers(@Param("keyword") String keyword,
                    @Param("role") String role,
                    @Param("status") Integer status,
                    @Param("startDate") String startDate,
                    @Param("endDate") String endDate);

    /**
     * 查询用户详情
     */
    @Select("SELECT " +
            "u.id, u.phone, u.role, u.status, u.created_at, u.updated_at, " +
            "up.name, up.gender, up.birthday, up.avatar, up.email, up.address, " +
            "COUNT(o.id) as total_orders, " +
            "COUNT(CASE WHEN o.status = 'DONE' THEN 1 END) as completed_orders " +
            "FROM user u " +
            "LEFT JOIN user_profile up ON u.id = up.user_id " +
            "LEFT JOIN `order` o ON u.id = o.customer_id " +
            "WHERE u.id = #{userId} " +
            "GROUP BY u.id, u.phone, u.role, u.status, u.created_at, u.updated_at, " +
            "up.name, up.gender, up.birthday, up.avatar, up.email, up.address")
    UserDetailVO findUserDetail(@Param("userId") Long userId);

    /**
     * 更新用户状态
     */
    @Update("UPDATE user SET status = #{status}, updated_at = #{updatedAt} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status, @Param("updatedAt") LocalDateTime updatedAt);

    /**
     * 重置用户密码
     */
    @Update("UPDATE user SET password_hash = #{passwordHash}, updated_at = #{updatedAt} WHERE id = #{id}")
    int resetPassword(@Param("id") Long id, @Param("passwordHash") String passwordHash, @Param("updatedAt") LocalDateTime updatedAt);

    /**
     * 更新用户角色
     */
    @Update("UPDATE user SET role = #{role}, updated_at = #{updatedAt} WHERE id = #{id}")
    int updateRole(@Param("id") Long id, @Param("role") String role, @Param("updatedAt") LocalDateTime updatedAt);
}
