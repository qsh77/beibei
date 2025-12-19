package com.beibeijava.controller;

import com.beibeijava.common.Result;
import com.beibeijava.dto.WorkerProfileUpdateRequest;
import com.beibeijava.service.WorkerProfileService;
import com.beibeijava.vo.WorkerProfileVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/worker/profile")
@RequiredArgsConstructor
@PreAuthorize("hasRole('WORKER')")
@Tag(name = "阿姨个人资料")
public class WorkerProfileController {

    private final WorkerProfileService workerProfileService;

    @GetMapping
    @Operation(summary = "获取阿姨个人资料")
    public Result<WorkerProfileVO> getProfile() {
        try {
            return Result.success(workerProfileService.getCurrentProfile());
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping
    @Operation(summary = "更新阿姨个人资料")
    public Result<String> updateProfile(@Valid @RequestBody WorkerProfileUpdateRequest request) {
        try {
            workerProfileService.updateProfile(request);
            return Result.success("更新成功", "资料已更新");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
