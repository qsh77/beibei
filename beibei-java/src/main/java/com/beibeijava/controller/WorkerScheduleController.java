package com.beibeijava.controller;

import com.beibeijava.common.Result;
import com.beibeijava.dto.WorkerScheduleUpdateRequest;
import com.beibeijava.service.WorkerScheduleService;
import com.beibeijava.vo.WorkerScheduleDayVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/worker/schedule")
@RequiredArgsConstructor
@PreAuthorize("hasRole('WORKER')")
@Tag(name = "阿姨档期管理")
public class WorkerScheduleController {

    private final WorkerScheduleService workerScheduleService;

    @GetMapping
    @Operation(summary = "查询档期")
    public Result<List<WorkerScheduleDayVO>> getSchedule(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            return Result.success(workerScheduleService.getSchedule(startDate, endDate));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping
    @Operation(summary = "批量更新档期")
    public Result<String> updateSchedule(@Valid @RequestBody WorkerScheduleUpdateRequest request) {
        try {
            workerScheduleService.updateSchedule(request);
            return Result.success("档期已更新", "档期已更新");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
