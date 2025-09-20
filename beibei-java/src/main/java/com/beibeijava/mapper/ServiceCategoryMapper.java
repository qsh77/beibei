package com.beibeijava.mapper;

import com.beibeijava.entity.ServiceCategory;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 服务类目数据访问层
 */
@Mapper
public interface ServiceCategoryMapper {
    
    /**
     * 查询所有类目
     */
    @Select("SELECT * FROM service_category ORDER BY sort ASC, id ASC")
    List<ServiceCategory> findAll();
    
    /**
     * 根据ID查询类目
     */
    @Select("SELECT * FROM service_category WHERE id = #{id}")
    ServiceCategory findById(@Param("id") Long id);
    
    /**
     * 查询顶级类目
     */
    @Select("SELECT * FROM service_category WHERE parent_id IS NULL ORDER BY sort ASC, id ASC")
    List<ServiceCategory> findTopLevel();
    
    /**
     * 根据父类目ID查询子类目
     */
    @Select("SELECT * FROM service_category WHERE parent_id = #{parentId} ORDER BY sort ASC, id ASC")
    List<ServiceCategory> findByParentId(@Param("parentId") Long parentId);

    // 管理员使用的方法

    /**
     * 插入新类目
     */
    @Insert("INSERT INTO service_category (parent_id, name, sort) VALUES (#{parentId}, #{name}, #{sort})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ServiceCategory category);

    /**
     * 更新类目
     */
    @Update("UPDATE service_category SET parent_id = #{parentId}, name = #{name}, sort = #{sort} WHERE id = #{id}")
    int update(ServiceCategory category);

    /**
     * 删除类目
     */
    @Delete("DELETE FROM service_category WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    /**
     * 检查类目下是否有服务
     */
    @Select("SELECT COUNT(*) FROM service WHERE category_id = #{categoryId}")
    int countServicesByCategory(@Param("categoryId") Long categoryId);

    /**
     * 检查类目下是否有子类目
     */
    @Select("SELECT COUNT(*) FROM service_category WHERE parent_id = #{parentId}")
    int countChildCategories(@Param("parentId") Long parentId);
}
