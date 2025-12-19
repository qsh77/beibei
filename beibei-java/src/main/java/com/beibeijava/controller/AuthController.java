package com.beibeijava.controller;

import com.beibeijava.common.Result;
import com.beibeijava.dto.LoginRequest;
import com.beibeijava.dto.RegisterRequest;
import com.beibeijava.entity.User;
import com.beibeijava.entity.UserProfile;
import com.beibeijava.mapper.UserMapper;
import com.beibeijava.mapper.UserProfileMapper;
import com.beibeijava.service.AuthService;
import com.beibeijava.vo.LoginResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "认证管理", description = "用户注册、登录等认证相关接口")
public class AuthController {
    
    private final AuthService authService;
    private final UserMapper userMapper;
    private final UserProfileMapper userProfileMapper;
    
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "新用户注册接口")
    public Result<String> register(@Valid @RequestBody RegisterRequest request) {
        try {
            authService.register(request);
            return Result.success("注册成功", "注册成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户登录接口")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.login(request);
            return Result.success("登录成功", response);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping("/me")
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的详细信息")
    public Result<LoginResponse.UserInfo> me() {
        try {
            // 从 SecurityContext 获取当前用户信息
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return Result.error("用户未登录");
            }

            // 从JWT认证过滤器设置的details中获取用户ID
            Object details = authentication.getDetails();
            if (!(details instanceof Long)) {
                return Result.error("无法获取用户ID");
            }

            Long userId = (Long) details;

            // 查询用户信息
            User user = userMapper.findById(userId);
            if (user == null) {
                return Result.error("用户不存在");
            }

            // 查询用户资料
            UserProfile profile = userProfileMapper.selectById(userId);

            // 构建响应
            LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo();
            userInfo.setId(user.getId());
            userInfo.setPhone(user.getPhone());
            userInfo.setRole(user.getRole());
            userInfo.setName(profile != null ? profile.getName() : "用户");
            userInfo.setAvatar(profile != null ? profile.getAvatar() : null);

            return Result.success("获取成功", userInfo);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping("/test")
    @Operation(summary = "测试接口", description = "测试接口连通性")
    public Result<String> test() {
        return Result.success("接口连通正常");
    }

}
