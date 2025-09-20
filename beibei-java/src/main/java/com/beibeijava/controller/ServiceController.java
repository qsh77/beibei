package com.beibeijava.controller;

import com.beibeijava.common.Result;
import com.beibeijava.entity.ServiceEntity;
import com.beibeijava.entity.ServiceCategory;
import com.beibeijava.service.ServiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 服务控制器
 */
@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
@Tag(name = "服务管理", description = "服务相关的接口")
public class ServiceController {
    
    private final ServiceService serviceService;
    
    @GetMapping
    @Operation(summary = "获取所有服务", description = "获取所有启用的服务列表")
    public Result<List<ServiceEntity>> getAllServices() {
        try {
            List<ServiceEntity> services = serviceService.getAllActiveServices();
            return Result.success("获取成功", services);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "获取服务详情", description = "根据ID获取服务详细信息")
    public Result<ServiceEntity> getServiceById(
            @Parameter(description = "服务ID") @PathVariable Long id) {
        try {
            ServiceEntity service = serviceService.getServiceById(id);
            if (service == null) {
                return Result.error("服务不存在");
            }
            return Result.success("获取成功", service);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    
    @GetMapping("/hot")
    @Operation(summary = "获取热门服务", description = "获取热门服务列表")
    public Result<List<ServiceEntity>> getHotServices(
            @Parameter(description = "数量限制") @RequestParam(defaultValue = "6") int limit) {
        try {
            List<ServiceEntity> services = serviceService.getHotServices(limit);
            return Result.success("获取成功", services);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping("/search")
    @Operation(summary = "搜索服务", description = "根据关键词搜索服务")
    public Result<List<ServiceEntity>> searchServices(
            @Parameter(description = "搜索关键词") @RequestParam String keyword) {
        try {
            List<ServiceEntity> services = serviceService.searchServices(keyword);
            return Result.success("搜索成功", services);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping("/categories")
    @Operation(summary = "获取所有类目", description = "获取所有服务类目")
    public Result<List<ServiceCategory>> getAllCategories() {
        try {
            List<ServiceCategory> categories = serviceService.getAllCategories();
            return Result.success("获取成功", categories);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping("/categories/top")
    @Operation(summary = "获取顶级类目", description = "获取顶级服务类目")
    public Result<List<ServiceCategory>> getTopLevelCategories() {
        try {
            List<ServiceCategory> categories = serviceService.getTopLevelCategories();
            return Result.success("获取成功", categories);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping("/categories/{parentId}/children")
    @Operation(summary = "获取子类目", description = "根据父类目ID获取子类目")
    public Result<List<ServiceCategory>> getCategoriesByParentId(
            @Parameter(description = "父类目ID") @PathVariable Long parentId) {
        try {
            List<ServiceCategory> categories = serviceService.getCategoriesByParentId(parentId);
            return Result.success("获取成功", categories);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping("/categories/{id}")
    @Operation(summary = "获取类目详情", description = "根据ID获取类目详细信息")
    public Result<ServiceCategory> getCategoryById(
            @Parameter(description = "类目ID") @PathVariable Long id) {
        try {
            ServiceCategory category = serviceService.getCategoryById(id);
            if (category == null) {
                return Result.error("类目不存在");
            }
            return Result.success("获取成功", category);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
