package com.beibeijava.mapper;

import com.beibeijava.entity.Review;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 评价Mapper
 */
@Mapper
public interface ReviewMapper {

        @Insert("INSERT INTO review (order_id, rating, content, imgs, created_by, created_at) " +
                        "VALUES (#{orderId}, #{rating}, #{content}, #{imgs}, #{createdBy}, NOW())")
        @Options(useGeneratedKeys = true, keyProperty = "id")
        int insert(Review review);

        @Select("SELECT * FROM review WHERE order_id = #{orderId}")
        Review findByOrderId(Long orderId);

        @Select("SELECT * FROM review WHERE id = #{id}")
        Review findById(Long id);

        @Select("SELECT r.* FROM review r " +
                        "INNER JOIN `order` o ON r.order_id = o.id " +
                        "WHERE o.worker_id = #{workerId} " +
                        "ORDER BY r.created_at DESC")
        List<Review> findByWorkerId(Long workerId);

        @Select("SELECT r.* FROM review r " +
                        "INNER JOIN `order` o ON r.order_id = o.id " +
                        "WHERE o.service_id = #{serviceId} " +
                        "ORDER BY r.created_at DESC")
        List<Review> findByServiceId(Long serviceId);

        @Delete("DELETE FROM review WHERE id = #{id}")
        int deleteById(Long id);

        @Delete("DELETE FROM review WHERE order_id = #{orderId}")
        int deleteByOrderId(Long orderId);

        /**
         * 计算阿姨的平均评分
         */
        @Select("SELECT AVG(r.rating) FROM review r " +
                        "INNER JOIN `order` o ON r.order_id = o.id " +
                        "WHERE o.worker_id = #{workerId}")
        Double calculateWorkerAvgRating(@Param("workerId") Long workerId);
}
