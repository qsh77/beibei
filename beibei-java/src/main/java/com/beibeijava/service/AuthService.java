package com.beibeijava.service;

import com.beibeijava.dto.LoginRequest;
import com.beibeijava.dto.RegisterRequest;
import com.beibeijava.entity.User;
import com.beibeijava.entity.UserProfile;
import com.beibeijava.mapper.UserMapper;
import com.beibeijava.mapper.UserProfileMapper;
import com.beibeijava.security.JwtUtils;
import com.beibeijava.vo.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 认证服务
 */
@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final UserMapper userMapper;
    private final UserProfileMapper userProfileMapper;
    
    /**
     * 用户注册
     */
    @Transactional
    public void register(RegisterRequest request) {
        // 检查手机号是否已存在
        if (userMapper.countByPhone(request.getPhone()) > 0) {
            throw new RuntimeException("手机号已存在");
        }
        
        // 创建用户
        User user = new User();
        user.setPhone(request.getPhone());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER"); // 默认角色为用户
        user.setStatus(1); // 启用状态
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        
        // 保存用户
        userMapper.insert(user);
        
        // 创建用户资料（确保完整性）
        UserProfile profile = new UserProfile();
        profile.setUserId(user.getId());
        profile.setName(request.getName() != null ? request.getName() : "用户");
        profile.setGender("U"); // 默认未知性别
        profile.setEmail(null); // 可选，注册时可能不填
        profile.setAddress(null); // 可选，注册时可能不填
        profile.setBirthday(null); // 可选，注册时可能不填
        profile.setAvatar("/uploads/avatars/default/user1.jpeg"); // 设置默认头像

        int profileResult = userProfileMapper.insert(profile);
        if (profileResult <= 0) {
            throw new RuntimeException("用户资料创建失败");
        }
    }
    
    /**
     * 用户登录 2420710313_齐世浩
     */

    public LoginResponse login(LoginRequest request) {
        // 根据手机号查询用户
        User user = userMapper.findByPhone(request.getPhone());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 检查用户状态
        if (user.getStatus() != 1) {
            throw new RuntimeException("用户已被禁用");
        }
        
        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("密码错误");
        }
        
        // 查询用户资料
        UserProfile profile = userProfileMapper.selectById(user.getId());
        
        // 生成JWT Token
        String token = jwtUtils.generateToken(user.getId(), user.getPhone(), user.getRole());
        
        // 构建响应
        LoginResponse response = new LoginResponse();
        response.setToken(token);

        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo();
        userInfo.setId(user.getId());
        userInfo.setPhone(user.getPhone());
        userInfo.setRole(user.getRole());
        userInfo.setName(profile != null ? profile.getName() : "用户");
        userInfo.setAvatar(profile != null ? profile.getAvatar() : null);
        response.setUserInfo(userInfo);

        return response;
    }
}
