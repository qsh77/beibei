package com.beibeijava.service;

import com.beibeijava.dto.CreateReviewRequest;
import com.beibeijava.entity.Order;
import com.beibeijava.entity.Review;
import com.beibeijava.mapper.OrderMapper;
import com.beibeijava.mapper.ReviewMapper;
import com.beibeijava.mapper.WorkerMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * 评价业务层
 */
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewMapper reviewMapper;
    private final OrderMapper orderMapper;
    private final WorkerMapper workerMapper;
    private final ObjectMapper objectMapper;

    /**
     * 创建评价
     */
    @Transactional
    public Review createReview(CreateReviewRequest request) {
        Long userId = getCurrentUserId();

        // 验证订单是否存在
        Order order = orderMapper.findById(request.getOrderId());
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        // 验证订单是否属于当前用户
        if (!order.getCustomerId().equals(userId)) {
            throw new RuntimeException("无权评价该订单");
        }

        // 验证订单状态是否为已完成
        if (!"DONE".equals(order.getStatus())) {
            throw new RuntimeException("只能评价已完成的订单");
        }

        // 验证订单是否已评价
        Review existingReview = reviewMapper.findByOrderId(request.getOrderId());
        if (existingReview != null) {
            throw new RuntimeException("该订单已评价");
        }

        // 创建评价
        Review review = new Review();
        review.setOrderId(request.getOrderId());
        review.setRating(request.getRating());
        review.setContent(request.getContent());
        review.setCreatedBy(userId);

        // 转换图片列表为JSON
        if (request.getImgs() != null && !request.getImgs().isEmpty()) {
            try {
                review.setImgs(objectMapper.writeValueAsString(request.getImgs()));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("图片数据格式错误", e);
            }
        }

        reviewMapper.insert(review);

        // 更新阿姨综合评分
        if (order.getWorkerId() != null) {
            updateWorkerScore(order.getWorkerId());
        }

        return review;
    }

    /**
     * 重新计算并更新阿姨综合评分和等级
     */
    private void updateWorkerScore(Long workerId) {
        Double avgRating = reviewMapper.calculateWorkerAvgRating(workerId);
        if (avgRating != null) {
            BigDecimal score = BigDecimal.valueOf(avgRating);
            Integer level = calculateLevel(avgRating);
            workerMapper.updateScoreAndLevel(workerId, score, level);
        }
    }

    /**
     * 根据评分计算等级
     * 规则：
     * - 0分（无评价）: 0星
     * - 1.0-1.9分: 1星
     * - 2.0-2.9分: 2星
     * - 3.0-3.9分: 3星
     * - 4.0-4.4分: 4星
     * - 4.5-5.0分: 5星
     */
    private Integer calculateLevel(Double score) {
        if (score == null || score < 1.0) {
            return 0;
        } else if (score < 2.0) {
            return 1;
        } else if (score < 3.0) {
            return 2;
        } else if (score < 4.0) {
            return 3;
        } else if (score < 4.5) {
            return 4;
        } else {
            return 5;
        }
    }

    /**
     * 获取订单评价
     */
    public Review getReviewByOrderId(Long orderId) {
        return reviewMapper.findByOrderId(orderId);
    }

    /**
     * 获取阿姨的所有评价
     */
    public List<Review> getWorkerReviews(Long workerId) {
        return reviewMapper.findByWorkerId(workerId);
    }

    /**
     * 获取服务的所有评价
     */
    public List<Review> getServiceReviews(Long serviceId) {
        return reviewMapper.findByServiceId(serviceId);
    }

    /**
     * 删除评价（管理员）
     */
    @Transactional
    public void deleteReview(Long reviewId) {
        Review review = reviewMapper.findById(reviewId);
        if (review == null) {
            throw new RuntimeException("评价不存在");
        }

        int rows = reviewMapper.deleteById(reviewId);
        if (rows == 0) {
            throw new RuntimeException("删除评价失败");
        }
    }

    /**
     * 根据订单ID删除评价（管理员）
     */
    @Transactional
    public void deleteReviewByOrderId(Long orderId) {
        int rows = reviewMapper.deleteByOrderId(orderId);
        if (rows == 0) {
            throw new RuntimeException("该订单没有评价");
        }
    }

    /**
     * 获取当前用户ID
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("用户未登录");
        }

        Object details = authentication.getDetails();
        if (details instanceof Long) {
            return (Long) details;
        }

        throw new RuntimeException("无法获取当前用户ID");
    }
}
