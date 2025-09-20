package com.beibeijava.controller;

import com.beibeijava.common.Result;
import com.beibeijava.dto.AddressRequest;
import com.beibeijava.entity.UserAddress;
import com.beibeijava.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 地址控制器
 */
@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
@Tag(name = "地址管理", description = "用户地址相关的接口")
public class AddressController {
    
    private final AddressService addressService;
    
    @GetMapping
    @Operation(summary = "获取地址列表", description = "获取当前用户的地址列表")
    public Result<List<UserAddress>> getAddresses() {
        try {
            List<UserAddress> addresses = addressService.getUserAddresses();
            return Result.success("获取成功", addresses);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "获取地址详情", description = "根据ID获取地址详细信息")
    public Result<UserAddress> getAddressById(
            @Parameter(description = "地址ID") @PathVariable Long id) {
        try {
            UserAddress address = addressService.getAddressById(id);
            return Result.success("获取成功", address);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping("/default")
    @Operation(summary = "获取默认地址", description = "获取当前用户的默认地址")
    public Result<UserAddress> getDefaultAddress() {
        try {
            UserAddress address = addressService.getDefaultAddress();
            return Result.success("获取成功", address);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping
    @Operation(summary = "添加地址", description = "添加新的用户地址")
    public Result<String> addAddress(@Valid @RequestBody AddressRequest request) {
        try {
            System.out.println("收到添加地址请求: " + request);
            addressService.addAddress(request);
            System.out.println("地址添加成功");
            return Result.success("添加成功", "地址添加成功");
        } catch (Exception e) {
            System.err.println("地址添加失败: " + e.getMessage());
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "更新地址", description = "更新指定地址信息")
    public Result<String> updateAddress(
            @Parameter(description = "地址ID") @PathVariable Long id,
            @Valid @RequestBody AddressRequest request) {
        try {
            addressService.updateAddress(id, request);
            return Result.success("更新成功", "地址更新成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "删除地址", description = "删除指定地址")
    public Result<String> deleteAddress(
            @Parameter(description = "地址ID") @PathVariable Long id) {
        try {
            addressService.deleteAddress(id);
            return Result.success("删除成功", "地址删除成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PutMapping("/{id}/default")
    @Operation(summary = "设置默认地址", description = "设置指定地址为默认地址")
    public Result<String> setDefaultAddress(
            @Parameter(description = "地址ID") @PathVariable Long id) {
        try {
            addressService.setDefaultAddress(id);
            return Result.success("设置成功", "默认地址设置成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
