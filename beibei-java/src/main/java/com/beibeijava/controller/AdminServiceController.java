package com.beibeijava.controller;

import com.beibeijava.common.Result;
import com.beibeijava.vo.PageResult;
import com.beibeijava.dto.ServiceCreateRequest;
import com.beibeijava.dto.ServiceQueryRequest;
import com.beibeijava.dto.ServiceUpdateRequest;
import com.beibeijava.entity.ServiceEntity;
import com.beibeijava.service.AdminServiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/services")
@RequiredArgsConstructor
@Tag(name = "管理员-服务管理", description = "管理员服务管理相关接口")
public class AdminServiceController {

    private final AdminServiceService adminServiceService;

    // 服务管理

    @GetMapping
    @Operation(summary = "分页查询服务列表", description = "管理员分页查询服务列表，支持筛选和搜索")
    public Result<PageResult<ServiceEntity>> getServiceList(@Valid @ModelAttribute ServiceQueryRequest request) {
        try {
            PageResult<ServiceEntity> result = adminServiceService.getServiceList(request);
            return Result.success("获取成功", result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取服务详情", description = "根据ID获取服务详细信息")
    public Result<ServiceEntity> getServiceById(
            @Parameter(description = "服务ID") @PathVariable Long id) {
        try {
            ServiceEntity service = adminServiceService.getServiceById(id);
            if (service == null) {
                return Result.error("服务不存在");
            }
            return Result.success("获取成功", service);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping
    @Operation(summary = "创建服务", description = "创建新的服务")
    public Result<ServiceEntity> createService(@Valid @RequestBody ServiceCreateRequest request) {
        try {
            ServiceEntity service = adminServiceService.createService(request);
            return Result.success("创建成功", service);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新服务", description = "更新服务信息")
    public Result<ServiceEntity> updateService(
            @Parameter(description = "服务ID") @PathVariable Long id,
            @Valid @RequestBody ServiceUpdateRequest request) {
        try {
            request.setId(id);
            ServiceEntity service = adminServiceService.updateService(request);
            return Result.success("更新成功", service);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除服务", description = "删除服务")
    public Result<Void> deleteService(
            @Parameter(description = "服务ID") @PathVariable Long id) {
        try {
            adminServiceService.deleteService(id);
            return Result.success("删除成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "更新服务状态", description = "启用或禁用服务")
    public Result<Void> updateServiceStatus(
            @Parameter(description = "服务ID") @PathVariable Long id,
            @Parameter(description = "状态：1-启用，0-禁用") @RequestParam Integer status) {
        try {
            adminServiceService.updateServiceStatus(id, status);
            return Result.success("状态更新成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

}