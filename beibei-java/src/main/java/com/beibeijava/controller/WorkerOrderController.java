package com.beibeijava.controller;

import com.beibeijava.common.Result;
import com.beibeijava.entity.Order;
import com.beibeijava.service.WorkerOrderService;
import com.beibeijava.vo.OrderDetailVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 阿姨端订单控制器
 */
@RestController
@RequestMapping("/api/worker/orders")
@RequiredArgsConstructor
@PreAuthorize("hasRole('WORKER')")
@Tag(name = "阿姨订单管理", description = "阿姨端订单相关的接口")
public class WorkerOrderController {

    private final WorkerOrderService workerOrderService;

    @GetMapping
    @Operation(summary = "获取阿姨的订单列表", description = "获取分配给当前阿姨的订单列表")
    public Result<List<Order>> getWorkerOrders() {
        try {
            List<Order> orders = workerOrderService.getWorkerOrders();
            return Result.success("获取成功", orders);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取订单详情", description = "获取分配给当前阿姨的订单详情")
    public Result<OrderDetailVO> getOrderDetail(
            @Parameter(description = "订单ID") @PathVariable Long id) {
        try {
            OrderDetailVO order = workerOrderService.getOrderDetail(id);
            return Result.success("获取成功", order);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}/accept")
    @Operation(summary = "接受订单", description = "阿姨接受分配的订单，开始服务")
    public Result<String> acceptOrder(
            @Parameter(description = "订单ID") @PathVariable Long id) {
        try {
            workerOrderService.acceptOrder(id);
            return Result.success("订单已接受", "订单状态已更新为服务中");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}/complete")
    @Operation(summary = "完成订单", description = "阿姨标记订单为已完成")
    public Result<String> completeOrder(
            @Parameter(description = "订单ID") @PathVariable Long id) {
        try {
            workerOrderService.completeOrder(id);
            return Result.success("订单已完成", "订单状态已更新为已完成");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}