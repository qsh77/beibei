package com.beibeijava.mapper;

import com.beibeijava.entity.ServiceEntity;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;

import java.util.List;

/**
 * 服务数据访问层
 */
@Mapper
public interface ServiceMapper {

    /**
     * 查询所有启用的服务
     */
    @Select("SELECT * FROM service WHERE status = 1 ORDER BY hot DESC, created_at DESC")
    List<ServiceEntity> findAllActive();

    /**
     * 根据ID查询服务
     */
    @Select("SELECT * FROM service WHERE id = #{id} AND status = 1")
    ServiceEntity findById(@Param("id") Long id);

    /**
     * 查询热门服务
     */
    @Select("SELECT * FROM service WHERE status = 1 AND hot = 1 ORDER BY rating DESC, created_at DESC LIMIT #{limit}")
    List<ServiceEntity> findHotServices(@Param("limit") int limit);

    /**
     * 搜索服务
     */
    @Select("SELECT * FROM service WHERE status = 1 AND (name LIKE CONCAT('%', #{keyword}, '%') OR description LIKE CONCAT('%', #{keyword}, '%')) ORDER BY hot DESC, rating DESC")
    List<ServiceEntity> searchServices(@Param("keyword") String keyword);

    // 管理员使用的方法

    /**
     * 查询所有服务（包括禁用的）
     */
    @Select("SELECT * FROM service ORDER BY created_at DESC")
    List<ServiceEntity> findAll();

    /**
     * 根据ID查询服务（管理员版本，包括禁用状态）
     */
    @Select("SELECT * FROM service WHERE id = #{id}")
    ServiceEntity findByIdForAdmin(@Param("id") Long id);

    /**
     * 插入新服务
     */
    @Insert("INSERT INTO service (name, description, base_price, unit, status, hot) " +
            "VALUES (#{name}, #{description}, #{basePrice}, #{unit}, 1, #{hot})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ServiceEntity service);

    /**
     * 更新服务
     */
    @Update("UPDATE service SET name = #{name}, description = #{description}, " +
            "base_price = #{basePrice}, unit = #{unit}, hot = #{hot}, status = #{status} WHERE id = #{id}")
    int update(ServiceEntity service);

    /**
     * 更新服务状态
     */
    @Update("UPDATE service SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 更新热门状态
     */
    @Update("UPDATE service SET hot = #{hot} WHERE id = #{id}")
    int updateHot(@Param("id") Long id, @Param("hot") Integer hot);

    /**
     * 删除服务
     */
    @Delete("DELETE FROM service WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    /**
     * 分页查询服务（管理员版本，支持条件筛选）
     */
    @Select("""
            <script>
            SELECT * FROM service
            <where>
                <if test="keyword != null and keyword != ''">
                    AND (name LIKE CONCAT('%', #{keyword}, '%') OR description LIKE CONCAT('%', #{keyword}, '%'))
                </if>
                <if test="status != null">
                    AND status = #{status}
                </if>
                <if test="isHot != null">
                    AND hot = #{isHot}
                </if>
            </where>
            ORDER BY created_at DESC
            LIMIT #{offset}, #{pageSize}
            </script>
            """)
    List<ServiceEntity> findAllWithPaging(@Param("keyword") String keyword,
            @Param("status") Integer status,
            @Param("isHot") Integer isHot,
            @Param("offset") int offset,
            @Param("pageSize") Integer pageSize);

    /**
     * 统计服务总数（管理员版本，支持条件筛选）
     */
    @Select("""
            <script>
            SELECT COUNT(*) FROM service
            <where>
                <if test="keyword != null and keyword != ''">
                    AND (name LIKE CONCAT('%', #{keyword}, '%') OR description LIKE CONCAT('%', #{keyword}, '%'))
                </if>
                <if test="status != null">
                    AND status = #{status}
                </if>
                <if test="isHot != null">
                    AND hot = #{isHot}
                </if>
            </where>
            </script>
            """)
    Long countAll(@Param("keyword") String keyword,
            @Param("status") Integer status,
            @Param("isHot") Integer isHot);

    /**
     * 查询销量前几的服务ID
     */
    @Select("SELECT service_id FROM `order` WHERE status != 'CANCELED' GROUP BY service_id ORDER BY COUNT(*) DESC LIMIT #{limit}")
    List<Long> findTopServiceIds(@Param("limit") int limit);
}
