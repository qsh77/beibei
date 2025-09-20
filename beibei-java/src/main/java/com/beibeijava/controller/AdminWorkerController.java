package com.beibeijava.controller;

import com.beibeijava.common.Result;
import com.beibeijava.dto.CreateWorkerRequest;
import com.beibeijava.dto.UpdateWorkerRequest;
import com.beibeijava.dto.WorkerQueryRequest;
import com.beibeijava.service.AdminWorkerService;
import com.beibeijava.vo.PageResult;
import com.beibeijava.vo.WorkerDetailVO;
import com.beibeijava.vo.WorkerListVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理员阿姨管理控制器
 */
@RestController
@RequestMapping("/api/admin/workers")
@RequiredArgsConstructor
@Tag(name = "管理员阿姨管理", description = "管理员阿姨管理相关接口")
@PreAuthorize("hasRole('ADMIN')")
public class AdminWorkerController {

    private final AdminWorkerService adminWorkerService;

    @GetMapping
    @Operation(summary = "分页查询阿姨列表", description = "支持按关键字、等级、状态筛选")
    public Result<PageResult<WorkerListVO>> getWorkerList(WorkerQueryRequest request) {
        try {
            PageResult<WorkerListVO> result = adminWorkerService.getWorkerList(request);
            return Result.success("查询成功", result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取阿姨详情", description = "根据阿姨ID获取详细信息")
    public Result<WorkerDetailVO> getWorkerDetail(
            @Parameter(description = "阿姨ID") @PathVariable Long id) {
        try {
            WorkerDetailVO workerDetail = adminWorkerService.getWorkerDetail(id);
            return Result.success("获取成功", workerDetail);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新阿姨信息", description = "更新阿姨的等级、经验、简介等信息")
    public Result<String> updateWorker(
            @Parameter(description = "阿姨ID") @PathVariable Long id,
            @Valid @RequestBody UpdateWorkerRequest request) {
        try {
            adminWorkerService.updateWorker(id, request);
            return Result.success("阿姨信息更新成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "更新阿姨状态", description = "启用或禁用阿姨")
    public Result<String> updateWorkerStatus(
            @Parameter(description = "阿姨ID") @PathVariable Long id,
            @Parameter(description = "状态：1-启用，0-禁用") @RequestParam Integer status) {
        try {
            adminWorkerService.updateWorkerStatus(id, status);
            String action = status == 1 ? "启用" : "禁用";
            return Result.success("阿姨" + action + "成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/batch/status")
    @Operation(summary = "批量更新阿姨状态", description = "批量启用或禁用阿姨")
    public Result<String> batchUpdateWorkerStatus(
            @Parameter(description = "阿姨ID列表") @RequestParam List<Long> workerIds,
            @Parameter(description = "状态：1-启用，0-禁用") @RequestParam Integer status) {
        try {
            adminWorkerService.batchUpdateWorkerStatus(workerIds, status);
            String action = status == 1 ? "启用" : "禁用";
            return Result.success("批量" + action + "阿姨成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/create")
    @Operation(summary = "直接创建阿姨账号", description = "创建完整的阿姨账号（包含用户、资料、阿姨信息）")
    public Result<String> createWorker(@Valid @RequestBody CreateWorkerRequest request) {
        try {
            adminWorkerService.createWorkerDirectly(request);
            return Result.success("阿姨账号创建成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/create-from-user")
    @Operation(summary = "从用户转换为阿姨", description = "将普通用户转换为阿姨")
    public Result<String> createWorkerFromUser(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "等级") @RequestParam(required = false, defaultValue = "1") Integer level,
            @Parameter(description = "工作年限") @RequestParam(required = false, defaultValue = "0") Integer years,
            @Parameter(description = "个人简介") @RequestParam(required = false) String bio) {
        try {
            adminWorkerService.createWorkerFromUser(userId, level, years, bio);
            return Result.success("阿姨账号创建成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除阿姨", description = "将阿姨转为普通用户（软删除）")
    public Result<String> removeWorker(
            @Parameter(description = "阿姨ID") @PathVariable Long id) {
        try {
            adminWorkerService.removeWorker(id);
            return Result.success("阿姨删除成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/stats")
    @Operation(summary = "获取阿姨统计信息", description = "获取阿姨数量统计信息")
    public Result<AdminWorkerService.WorkerStatsVO> getWorkerStats() {
        try {
            AdminWorkerService.WorkerStatsVO stats = adminWorkerService.getWorkerStats();
            return Result.success("获取成功", stats);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/search")
    @Operation(summary = "快速搜索阿姨", description = "根据关键字快速搜索阿姨")
    public Result<List<WorkerListVO>> searchWorkers(
            @Parameter(description = "搜索关键字") @RequestParam String keyword,
            @Parameter(description = "最大返回数量") @RequestParam(defaultValue = "10") Integer limit) {
        try {
            List<WorkerListVO> workers = adminWorkerService.searchWorkers(keyword, limit);
            return Result.success("搜索成功", workers);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/{id}/upgrade-check")
    @Operation(summary = "检查阿姨等级升级条件", description = "检查阿姨是否满足等级升级条件")
    public Result<Boolean> checkUpgradeCondition(
            @Parameter(description = "阿姨ID") @PathVariable Long id,
            @Parameter(description = "目标等级") @RequestParam Integer targetLevel) {
        try {
            boolean canUpgrade = adminWorkerService.canUpgradeLevel(id, targetLevel);
            return Result.success("检查完成", canUpgrade);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/levels/distribution")
    @Operation(summary = "获取阿姨等级分布", description = "获取各等级阿姨的数量分布")
    public Result<AdminWorkerService.WorkerStatsVO> getLevelDistribution() {
        try {
            AdminWorkerService.WorkerStatsVO stats = adminWorkerService.getWorkerStats();
            return Result.success("获取成功", stats);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}