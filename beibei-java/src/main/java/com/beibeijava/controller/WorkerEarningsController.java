package com.beibeijava.controller;

import com.beibeijava.common.Result;
import com.beibeijava.service.WorkerEarningsService;
import com.beibeijava.vo.PageResult;
import com.beibeijava.vo.WorkerEarningDetailVO;
import com.beibeijava.vo.WorkerEarningTrendVO;
import com.beibeijava.vo.WorkerEarningsOverviewVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/worker/earnings")
@RequiredArgsConstructor
@PreAuthorize("hasRole('WORKER')")
@Tag(name = "阿姨收入管理")
public class WorkerEarningsController {

    private final WorkerEarningsService workerEarningsService;

    @GetMapping("/overview")
    @Operation(summary = "收入概览")
    public Result<WorkerEarningsOverviewVO> getOverview() {
        try {
            return Result.success(workerEarningsService.getOverview());
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/trend")
    @Operation(summary = "收入趋势")
    public Result<List<WorkerEarningTrendVO>> getTrend(@RequestParam(defaultValue = "7") int days) {
        try {
            return Result.success(workerEarningsService.getTrend(days));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/details")
    @Operation(summary = "收入明细")
    public Result<PageResult<WorkerEarningDetailVO>> getDetails(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            return Result.success(workerEarningsService.getEarningDetails(startDate, endDate, page, size));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
