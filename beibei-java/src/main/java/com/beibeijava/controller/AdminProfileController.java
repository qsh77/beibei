package com.beibeijava.controller;

import com.beibeijava.common.Result;
import com.beibeijava.dto.ChangePasswordRequest;
import com.beibeijava.dto.UpdateProfileRequest;
import com.beibeijava.entity.UserProfile;
import com.beibeijava.service.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员资料控制器
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "管理员资料管理", description = "管理员个人资料相关的接口")
public class AdminProfileController {

    private final UserProfileService userProfileService;

    @GetMapping("/profile")
    @Operation(summary = "获取管理员资料", description = "获取当前管理员的个人资料")
    public Result<UserProfile> getAdminProfile() {
        try {
            UserProfile profile = userProfileService.getCurrentUserProfile();
            return Result.success("获取成功", profile);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/profile")
    @Operation(summary = "更新管理员资料", description = "更新当前管理员的个人资料")
    public Result<String> updateAdminProfile(@Valid @RequestBody UpdateProfileRequest request) {
        try {
            userProfileService.updateProfile(request);
            return Result.success("更新成功", "管理员资料更新成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/password")
    @Operation(summary = "修改密码", description = "修改当前管理员的登录密码")
    public Result<String> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        try {
            userProfileService.changePassword(request);
            return Result.success("修改成功", "密码修改成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
