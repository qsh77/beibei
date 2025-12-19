package com.beibeijava.controller;

import com.beibeijava.common.Result;
import com.beibeijava.dto.CreateOrderRequest;
import com.beibeijava.entity.Order;
import com.beibeijava.service.OrderService;
import com.beibeijava.vo.OrderDetailVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单控制器
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "订单管理", description = "订单相关的接口")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "创建订单", description = "用户创建新订单")
    public Result<Order> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        try {
            Order order = orderService.createOrder(request);
            return Result.success("订单创建成功", order);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping
    @Operation(summary = "获取订单列表", description = "获取当前用户的订单列表")
    public Result<List<Order>> getUserOrders() {
        try {
            List<Order> orders = orderService.getUserOrders();
            return Result.success("获取成功", orders);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取订单详情", description = "根据ID获取订单详细信息")
    public Result<Order> getOrderById(
            @Parameter(description = "订单ID") @PathVariable Long id) {
        try {
            Order order = orderService.getOrderById(id);
            return Result.success("获取成功", order);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/{id}/detail")
    @Operation(summary = "获取订单完整详情", description = "获取订单完整详情，包含客户、阿姨、服务等关联信息")
    public Result<OrderDetailVO> getOrderDetail(
            @Parameter(description = "订单ID") @PathVariable Long id) {
        try {
            OrderDetailVO detail = orderService.getOrderDetail(id);
            return Result.success("获取成功", detail);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}/cancel")
    @Operation(summary = "取消订单", description = "取消指定订单")
    public Result<String> cancelOrder(
            @Parameter(description = "订单ID") @PathVariable Long id) {
        try {
            orderService.cancelOrder(id);
            return Result.success("订单取消成功", "订单已取消");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/pending")
    @Operation(summary = "获取待分配订单", description = "获取待分配的订单列表（管理员/阿姨使用）")
    public Result<List<Order>> getPendingOrders() {
        try {
            List<Order> orders = orderService.getPendingOrders();
            return Result.success("获取成功", orders);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}/assign")
    @Operation(summary = "分配订单", description = "将订单分配给指定阿姨")
    public Result<String> assignOrder(
            @Parameter(description = "订单ID") @PathVariable Long id,
            @Parameter(description = "阿姨ID") @RequestParam Long workerId) {
        try {
            orderService.assignOrder(id, workerId);
            return Result.success("订单分配成功", "订单已分配给阿姨");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
