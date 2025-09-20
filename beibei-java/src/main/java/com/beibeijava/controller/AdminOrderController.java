package com.beibeijava.controller;

import com.beibeijava.common.Result;
import com.beibeijava.dto.OrderQueryRequest;
import com.beibeijava.dto.UpdateOrderStatusRequest;
import com.beibeijava.service.AdminOrderService;
import com.beibeijava.vo.OrderDetailVO;
import com.beibeijava.vo.OrderListVO;
import com.beibeijava.vo.OrderStatsVO;
import com.beibeijava.vo.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员订单控制器
 */
@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "管理员订单管理", description = "管理员订单相关的接口")
public class AdminOrderController {

    private final AdminOrderService adminOrderService;

    @GetMapping
    @Operation(summary = "分页查询订单列表", description = "管理员分页查询订单列表，支持筛选和搜索")
    public Result<PageResult<OrderListVO>> getOrderList(@Valid @ModelAttribute OrderQueryRequest request) {
        try {
            PageResult<OrderListVO> result = adminOrderService.getOrderList(request);
            return Result.success("获取成功", result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取订单详情", description = "根据ID获取订单详细信息")
    public Result<OrderDetailVO> getOrderDetail(
            @Parameter(description = "订单ID") @PathVariable Long id) {
        try {
            OrderDetailVO order = adminOrderService.getOrderDetail(id);
            return Result.success("获取成功", order);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/stats")
    @Operation(summary = "获取订单统计", description = "获取各状态订单数量统计")
    public Result<OrderStatsVO> getOrderStats() {
        try {
            OrderStatsVO stats = adminOrderService.getOrderStats();
            return Result.success("获取成功", stats);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/status")
    @Operation(summary = "更新订单状态", description = "管理员更新订单状态")
    public Result<String> updateOrderStatus(@Valid @RequestBody UpdateOrderStatusRequest request) {
        try {
            adminOrderService.updateOrderStatus(request);
            return Result.success("订单状态更新成功", "状态已更新");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}