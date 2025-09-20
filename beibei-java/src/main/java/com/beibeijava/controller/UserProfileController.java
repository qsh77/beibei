package com.beibeijava.controller;

import com.beibeijava.common.Result;
import com.beibeijava.dto.ChangePasswordRequest;
import com.beibeijava.dto.UpdateProfileRequest;
import com.beibeijava.entity.User;
import com.beibeijava.entity.UserProfile;
import com.beibeijava.service.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户资料控制器
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "用户资料管理", description = "用户个人资料相关的接口")
public class UserProfileController {
    
    private final UserProfileService userProfileService;
    
    @GetMapping("/profile")
    @Operation(summary = "获取用户资料", description = "获取当前用户的个人资料")
    public Result<UserProfile> getUserProfile() {
        try {
            UserProfile profile = userProfileService.getCurrentUserProfile();
            return Result.success("获取成功", profile);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PutMapping("/profile")
    @Operation(summary = "更新用户资料", description = "更新当前用户的个人资料")
    public Result<String> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        try {
            System.out.println("收到更新请求: " + request);
            userProfileService.updateProfile(request);
            System.out.println("用户资料更新成功");
            return Result.success("更新成功", "用户资料更新成功");
        } catch (Exception e) {
            System.err.println("用户资料更新失败: " + e.getMessage());
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
    }
    
    @PutMapping("/password")
    @Operation(summary = "修改密码", description = "修改当前用户的登录密码")
    public Result<String> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        try {
            userProfileService.changePassword(request);
            return Result.success("修改成功", "密码修改成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping("/info")
    @Operation(summary = "获取用户信息", description = "获取当前用户的基本信息")
    public Result<User> getUserInfo() {
        try {
            User user = userProfileService.getCurrentUser();
            return Result.success("获取成功", user);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
