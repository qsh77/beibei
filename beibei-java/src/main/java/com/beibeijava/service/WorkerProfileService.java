package com.beibeijava.service;

import com.beibeijava.dto.WorkerProfileUpdateRequest;
import com.beibeijava.entity.UserProfile;
import com.beibeijava.entity.Worker;
import com.beibeijava.mapper.UserMapper;
import com.beibeijava.mapper.UserProfileMapper;
import com.beibeijava.mapper.WorkerMapper;
import com.beibeijava.vo.WorkerDetailVO;
import com.beibeijava.vo.WorkerProfileVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class WorkerProfileService {

    private final WorkerMapper workerMapper;
    private final UserMapper userMapper;
    private final UserProfileMapper userProfileMapper;

    public WorkerProfileVO getCurrentProfile() {
        Long userId = getCurrentUserId();
        Worker worker = workerMapper.findByUserId(userId);
        if (worker == null) {
            throw new RuntimeException("当前用户未认证为阿姨");
        }

        WorkerDetailVO detail = workerMapper.findWorkerDetail(worker.getId());
        if (detail == null) {
            throw new RuntimeException("未找到阿姨详细信息");
        }

        WorkerProfileVO vo = new WorkerProfileVO();
        vo.setWorkerId(detail.getId());
        vo.setUserId(detail.getUserId());
        vo.setName(detail.getName());
        vo.setPhone(detail.getPhone());
        vo.setGender(detail.getGender());
        vo.setBirthday(detail.getBirthday());
        vo.setAvatar(detail.getAvatar());
        vo.setEmail(detail.getEmail());
        vo.setAddress(detail.getAddress());
        vo.setLevel(detail.getLevel());
        vo.setYears(detail.getYears());
        vo.setBio(detail.getBio());
        vo.setScore(detail.getScore() != null ? detail.getScore().doubleValue() : null);
        vo.setTotalOrders(detail.getTotalOrders());
        vo.setCompletedOrders(detail.getCompletedOrders());
        vo.setAvgRating(detail.getAvgRating());
        vo.setTotalReviews(detail.getTotalReviews());
        return vo;
    }

    @Transactional
    public void updateProfile(WorkerProfileUpdateRequest request) {
        Long userId = getCurrentUserId();
        Worker worker = workerMapper.findByUserId(userId);
        if (worker == null) {
            throw new RuntimeException("当前用户未认证为阿姨");
        }

        // 更新用户资料
        UserProfile profile = userProfileMapper.selectById(userId);
        if (profile == null) {
            profile = new UserProfile();
            profile.setUserId(userId);
            profile.setName(request.getName());
            profile.setGender(request.getGender());
            profile.setBirthday(request.getBirthday());
            profile.setEmail(request.getEmail());
            profile.setAddress(request.getAddress());
            userProfileMapper.insert(profile);
            profile = userProfileMapper.selectById(userId);
        }

        if (request.getName() != null) {
            profile.setName(request.getName());
        }
        if (request.getGender() != null) {
            profile.setGender(request.getGender());
        }
        if (request.getBirthday() != null) {
            profile.setBirthday(request.getBirthday());
        }
        if (request.getEmail() != null) {
            profile.setEmail(request.getEmail());
        }
        if (request.getAddress() != null) {
            profile.setAddress(request.getAddress());
        }
        if (StringUtils.hasText(request.getAvatar())) {
            profile.setAvatar(request.getAvatar());
        }
        userProfileMapper.update(profile);

        // 更新阿姨专业信息
        if (request.getYears() != null) {
            worker.setYears(request.getYears());
        }
        if (StringUtils.hasText(request.getBio())) {
            worker.setBio(request.getBio());
        }
        if (request.getLevel() != null) {
            worker.setLevel(request.getLevel());
        }
        workerMapper.update(worker);
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("用户未登录");
        }
        Object details = authentication.getDetails();
        if (details instanceof Long) {
            return (Long) details;
        }
        throw new RuntimeException("无法获取当前用户ID");
    }
}
