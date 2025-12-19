package com.beibeijava.service;

import com.beibeijava.entity.Worker;
import com.beibeijava.mapper.OrderMapper;
import com.beibeijava.mapper.WorkerMapper;
import com.beibeijava.vo.PageResult;
import com.beibeijava.vo.WorkerEarningDetailVO;
import com.beibeijava.vo.WorkerEarningTrendVO;
import com.beibeijava.vo.WorkerEarningsAggregate;
import com.beibeijava.vo.WorkerEarningsOverviewVO;
import com.beibeijava.vo.WorkerDetailVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WorkerEarningsService {

    private final OrderMapper orderMapper;
    private final WorkerMapper workerMapper;

    public WorkerEarningsOverviewVO getOverview() {
        Long workerId = getCurrentWorkerId();
        LocalDateTime now = LocalDateTime.now();

        // 今日开始时间
        LocalDateTime todayStart = now.toLocalDate().atStartOfDay();

        // 本周开始时间 (周一)
        LocalDateTime weekStart = now.toLocalDate().minusDays(now.toLocalDate().getDayOfWeek().getValue() - 1).atStartOfDay();

        // 本月开始时间
        LocalDateTime monthStart = now.withDayOfMonth(1).toLocalDate().atStartOfDay();

        // 查询各时间段收入
        WorkerEarningsAggregate today = orderMapper.aggregateWorkerEarnings(workerId, todayStart, now);
        WorkerEarningsAggregate week = orderMapper.aggregateWorkerEarnings(workerId, weekStart, now);
        WorkerEarningsAggregate month = orderMapper.aggregateWorkerEarnings(workerId, monthStart, now);
        WorkerEarningsAggregate total = orderMapper.aggregateWorkerEarnings(workerId, null, null);

        WorkerEarningsOverviewVO vo = new WorkerEarningsOverviewVO();

        // 直接使用订单金额，不扣除佣金
        BigDecimal todayTotal = today.getTotal() != null ? today.getTotal() : BigDecimal.ZERO;
        BigDecimal weekTotal = week.getTotal() != null ? week.getTotal() : BigDecimal.ZERO;
        BigDecimal monthTotal = month.getTotal() != null ? month.getTotal() : BigDecimal.ZERO;
        BigDecimal totalAmount = total.getTotal() != null ? total.getTotal() : BigDecimal.ZERO;

        vo.setTodayEarnings(todayTotal);
        vo.setWeekEarnings(weekTotal);
        vo.setMonthEarnings(monthTotal);
        vo.setTotalEarnings(totalAmount);

        vo.setTotalOrderCount(total.getOrderCount() != null ? total.getOrderCount() : 0L);

        if (vo.getTotalOrderCount() != null && vo.getTotalOrderCount() > 0) {
            vo.setAverageOrderAmount(vo.getTotalEarnings().divide(new BigDecimal(vo.getTotalOrderCount()), 2, RoundingMode.HALF_UP));
        } else {
            vo.setAverageOrderAmount(BigDecimal.ZERO);
        }

        WorkerDetailVO detailVO = workerMapper.findWorkerDetail(workerId);
        if (detailVO != null) {
            vo.setServiceRating(detailVO.getAvgRating() != null ? detailVO.getAvgRating() : (detailVO.getScore() != null ? detailVO.getScore().doubleValue() : null));
        }
        return vo;
    }

    public List<WorkerEarningTrendVO> getTrend(int days) {
        if (days <= 0) {
            days = 7;
        }
        Long workerId = getCurrentWorkerId();
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);

        List<WorkerEarningTrendVO> dbList = orderMapper.findWorkerEarningTrend(workerId, startDate, endDate);
        Map<LocalDate, BigDecimal> amountMap = new HashMap<>();
        for (WorkerEarningTrendVO item : dbList) {
            // 直接使用订单金额，不扣除佣金
            BigDecimal amount = item.getAmount() != null ? item.getAmount() : BigDecimal.ZERO;
            amountMap.put(item.getDate(), amount);
        }

        List<WorkerEarningTrendVO> result = new ArrayList<>();
        LocalDate cursor = startDate;
        while (!cursor.isAfter(endDate)) {
            WorkerEarningTrendVO vo = new WorkerEarningTrendVO();
            vo.setDate(cursor);
            vo.setAmount(amountMap.getOrDefault(cursor, BigDecimal.ZERO));
            result.add(vo);
            cursor = cursor.plusDays(1);
        }
        return result;
    }

    public PageResult<WorkerEarningDetailVO> getEarningDetails(LocalDate startDate, LocalDate endDate, int page, int size) {
        int pageNo = Math.max(page, 1);
        int pageSize = Math.min(Math.max(size, 10), 100);
        int offset = (pageNo - 1) * pageSize;

        Long workerId = getCurrentWorkerId();
        List<WorkerEarningDetailVO> records = orderMapper.findWorkerEarningDetails(workerId, startDate, endDate, offset, pageSize);
        for (WorkerEarningDetailVO record : records) {
            BigDecimal amount = record.getAmount() != null ? record.getAmount() : BigDecimal.ZERO;
            // 直接使用订单金额作为收入，不扣除佣金
            record.setCommission(BigDecimal.ZERO);
            record.setEarnings(amount);
        }

        Long total = orderMapper.countWorkerEarningDetails(workerId, startDate, endDate);
        return new PageResult<>(records, total, pageNo, pageSize);
    }

    private Long getCurrentWorkerId() {
        Long userId = getCurrentUserId();
        Worker worker = workerMapper.findByUserId(userId);
        if (worker == null) {
            throw new RuntimeException("当前用户未认证为阿姨");
        }
        return worker.getId();
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
        if (details instanceof Integer) {
            return ((Integer) details).longValue();
        }
        if (details instanceof String) {
            try {
                return Long.parseLong((String) details);
            } catch (NumberFormatException e) {
                throw new RuntimeException("无法解析用户ID: " + details);
            }
        }
        throw new RuntimeException("无法获取当前用户ID，details类型: " + (details != null ? details.getClass().getSimpleName() : "null"));
    }
}
