package com.beibeijava.controller;

import com.beibeijava.common.Result;
import com.beibeijava.service.StatsService;
import com.beibeijava.vo.OverviewStatsVO;
import com.beibeijava.vo.OrderTrendVO;
import com.beibeijava.vo.ServiceStatsVO;
import com.beibeijava.vo.UserRegistrationTrendVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/stats")
@RequiredArgsConstructor
// @PreAuthorize("hasRole('ADMIN')") // 临时注释掉权限检查用于测试
@Tag(name = "数据统计管理")
public class StatsController {

    private final StatsService statsService;

    @GetMapping("/overview")
    @Operation(summary = "获取概览统计")
    public Result<OverviewStatsVO> getOverviewStats() {
        OverviewStatsVO stats = statsService.getOverviewStats();
        return Result.success(stats);
    }

    @GetMapping("/order/trend/daily")
    @Operation(summary = "获取订单日趋势")
    public Result<List<OrderTrendVO>> getDailyOrderTrend(@RequestParam(defaultValue = "30") int days) {
        List<OrderTrendVO> trend = statsService.getDailyOrderTrend(days);
        return Result.success(trend);
    }

    @GetMapping("/order/trend/monthly")
    @Operation(summary = "获取订单月趋势")
    public Result<List<OrderTrendVO>> getMonthlyOrderTrend(@RequestParam(defaultValue = "12") int months) {
        List<OrderTrendVO> trend = statsService.getMonthlyOrderTrend(months);
        return Result.success(trend);
    }

    @GetMapping("/order/status")
    @Operation(summary = "获取订单状态统计")
    public Result<List<Map<String, Object>>> getOrderStatusStats() {
        List<Map<String, Object>> stats = statsService.getOrderStatusStats();
        return Result.success(stats);
    }

    @GetMapping("/service/top")
    @Operation(summary = "获取热门服务统计")
    public Result<List<ServiceStatsVO>> getTopServices(@RequestParam(defaultValue = "10") int limit) {
        List<ServiceStatsVO> services = statsService.getTopServices(limit);
        return Result.success(services);
    }

    @GetMapping("/user/registration/trend")
    @Operation(summary = "获取用户注册趋势")
    public Result<List<UserRegistrationTrendVO>> getUserRegistrationTrend(@RequestParam(defaultValue = "30") int days) {
        List<UserRegistrationTrendVO> trend = statsService.getUserRegistrationTrend(days);
        return Result.success(trend);
    }

    @GetMapping("/user/role")
    @Operation(summary = "获取用户角色统计")
    public Result<List<Map<String, Object>>> getUserRoleStats() {
        List<Map<String, Object>> stats = statsService.getUserRoleStats();
        return Result.success(stats);
    }

    @GetMapping("/worker/top")
    @Operation(summary = "获取优秀阿姨统计")
    public Result<List<Map<String, Object>>> getTopWorkers(@RequestParam(defaultValue = "10") int limit) {
        List<Map<String, Object>> workers = statsService.getTopWorkers(limit);
        return Result.success(workers);
    }
}