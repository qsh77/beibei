package com.beibeijava.service;

import com.beibeijava.mapper.StatsMapper;
import com.beibeijava.vo.OverviewStatsVO;
import com.beibeijava.vo.OrderTrendVO;
import com.beibeijava.vo.ServiceStatsVO;
import com.beibeijava.vo.UserRegistrationTrendVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final StatsMapper statsMapper;

    public OverviewStatsVO getOverviewStats() {
        return statsMapper.getOverviewStats();
    }

    public List<OrderTrendVO> getDailyOrderTrend(int days) {
        return statsMapper.getOrderTrend(days);
    }

    public List<OrderTrendVO> getMonthlyOrderTrend(int months) {
        return statsMapper.getMonthlyOrderTrend(months);
    }

    public List<ServiceStatsVO> getTopServices(int limit) {
        return statsMapper.getTopServices(limit);
    }

    public List<UserRegistrationTrendVO> getUserRegistrationTrend(int days) {
        return statsMapper.getUserRegistrationTrend(days);
    }

    public List<Map<String, Object>> getOrderStatusStats() {
        return statsMapper.getOrderStatusStats();
    }

    public List<Map<String, Object>> getUserRoleStats() {
        return statsMapper.getUserRoleStats();
    }

    public List<Map<String, Object>> getTopWorkers(int limit) {
        return statsMapper.getTopWorkers(limit);
    }
}