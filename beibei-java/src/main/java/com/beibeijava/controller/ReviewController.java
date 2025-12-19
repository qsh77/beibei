package com.beibeijava.controller;

import com.beibeijava.common.Result;
import com.beibeijava.dto.CreateReviewRequest;
import com.beibeijava.entity.Review;
import com.beibeijava.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 评价控制器
 */
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Tag(name = "评价管理", description = "订单评价相关的接口")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @Operation(summary = "创建评价", description = "用户对已完成的订单进行评价")
    public Result<Review> createReview(@Valid @RequestBody CreateReviewRequest request) {
        try {
            Review review = reviewService.createReview(request);
            return Result.success("评价成功", review);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "获取订单评价", description = "根据订单ID获取评价信息")
    public Result<Review> getReviewByOrderId(
            @Parameter(description = "订单ID") @PathVariable Long orderId) {
        try {
            Review review = reviewService.getReviewByOrderId(orderId);
            if (review == null) {
                return Result.error("该订单暂无评价");
            }
            return Result.success("获取成功", review);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/worker/{workerId}")
    @Operation(summary = "获取阿姨的评价列表", description = "获取指定阿姨的所有评价")
    public Result<List<Review>> getWorkerReviews(
            @Parameter(description = "阿姨ID") @PathVariable Long workerId) {
        try {
            List<Review> reviews = reviewService.getWorkerReviews(workerId);
            return Result.success("获取成功", reviews);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/service/{serviceId}")
    @Operation(summary = "获取服务的评价列表", description = "获取指定服务的所有评价")
    public Result<List<Review>> getServiceReviews(
            @Parameter(description = "服务ID") @PathVariable Long serviceId) {
        try {
            List<Review> reviews = reviewService.getServiceReviews(serviceId);
            return Result.success("获取成功", reviews);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/{reviewId}")
    @Operation(summary = "删除评价", description = "管理员删除指定评价")
    public Result<String> deleteReview(
            @Parameter(description = "评价ID") @PathVariable Long reviewId) {
        try {
            reviewService.deleteReview(reviewId);
            return Result.success("删除成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/order/{orderId}")
    @Operation(summary = "删除订单评价", description = "管理员根据订单ID删除评价")
    public Result<String> deleteReviewByOrderId(
            @Parameter(description = "订单ID") @PathVariable Long orderId) {
        try {
            reviewService.deleteReviewByOrderId(orderId);
            return Result.success("删除成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
