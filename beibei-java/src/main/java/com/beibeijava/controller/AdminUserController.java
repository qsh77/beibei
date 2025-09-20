package com.beibeijava.controller;

import com.beibeijava.common.Result;
import com.beibeijava.dto.UpdateUserStatusRequest;
import com.beibeijava.dto.UserQueryRequest;
import com.beibeijava.service.AdminUserService;
import com.beibeijava.vo.PageResult;
import com.beibeijava.vo.UserDetailVO;
import com.beibeijava.vo.UserListVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理员用户管理控制器
 */
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@Tag(name = "管理员用户管理", description = "管理员用户管理相关接口")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping
    @Operation(summary = "分页查询用户列表", description = "支持按关键字、角色、状态筛选")
    public Result<PageResult<UserListVO>> getUserList(UserQueryRequest request) {
        try {
            PageResult<UserListVO> result = adminUserService.getUserList(request);
            return Result.success("查询成功", result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取用户详情", description = "根据用户ID获取用户详细信息")
    public Result<UserDetailVO> getUserDetail(
            @Parameter(description = "用户ID") @PathVariable Long id) {
        try {
            UserDetailVO userDetail = adminUserService.getUserDetail(id);
            return Result.success("获取成功", userDetail);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "更新用户状态", description = "启用或禁用用户")
    public Result<String> updateUserStatus(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @Valid @RequestBody UpdateUserStatusRequest request) {
        try {
            adminUserService.updateUserStatus(id, request);
            String action = request.getStatus() == 1 ? "启用" : "禁用";
            return Result.success("用户" + action + "成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}/password/reset")
    @Operation(summary = "重置用户密码", description = "重置用户密码为默认密码123456")
    public Result<String> resetUserPassword(
            @Parameter(description = "用户ID") @PathVariable Long id) {
        try {
            adminUserService.resetUserPassword(id);
            return Result.success("密码重置成功，新密码为：123456");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}/role")
    @Operation(summary = "更新用户角色", description = "更新用户角色（USER/WORKER）")
    public Result<String> updateUserRole(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @Parameter(description = "新角色") @RequestParam String role) {
        try {
            adminUserService.updateUserRole(id, role);
            return Result.success("用户角色更新成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/batch/status")
    @Operation(summary = "批量更新用户状态", description = "批量启用或禁用用户")
    public Result<String> batchUpdateUserStatus(
            @Parameter(description = "用户ID列表") @RequestParam List<Long> userIds,
            @Parameter(description = "状态：1-启用，0-禁用") @RequestParam Integer status) {
        try {
            adminUserService.batchUpdateUserStatus(userIds, status);
            String action = status == 1 ? "启用" : "禁用";
            return Result.success("批量" + action + "用户成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/stats")
    @Operation(summary = "获取用户统计信息", description = "获取用户数量统计信息")
    public Result<AdminUserService.UserStatsVO> getUserStats() {
        try {
            AdminUserService.UserStatsVO stats = adminUserService.getUserStats();
            return Result.success("获取成功", stats);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/export")
    @Operation(summary = "导出用户数据", description = "导出用户列表为Excel文件")
    public Result<String> exportUsers(UserQueryRequest request) {
        try {
            // TODO: 实现用户数据导出功能
            // 可以使用Apache POI生成Excel文件
            return Result.success("用户数据导出功能开发中...");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/search")
    @Operation(summary = "快速搜索用户", description = "根据关键字快速搜索用户")
    public Result<List<UserListVO>> searchUsers(
            @Parameter(description = "搜索关键字") @RequestParam String keyword,
            @Parameter(description = "最大返回数量") @RequestParam(defaultValue = "10") Integer limit) {
        try {
            UserQueryRequest request = new UserQueryRequest();
            request.setKeyword(keyword);
            request.setPage(1);
            request.setSize(limit);

            PageResult<UserListVO> result = adminUserService.getUserList(request);
            return Result.success("搜索成功", result.getRecords());
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}