package com.beibeijava.service;

import com.beibeijava.dto.WorkerScheduleUpdateRequest;
import com.beibeijava.entity.Worker;
import com.beibeijava.entity.WorkerSchedule;
import com.beibeijava.mapper.WorkerMapper;
import com.beibeijava.mapper.WorkerScheduleMapper;
import com.beibeijava.vo.WorkerScheduleDayVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WorkerScheduleService {

    private final WorkerScheduleMapper workerScheduleMapper;
    private final WorkerMapper workerMapper;

    public List<WorkerScheduleDayVO> getSchedule(LocalDate startDate, LocalDate endDate) {
        LocalDate start = startDate != null ? startDate : LocalDate.now().minusDays(3);
        LocalDate end = endDate != null ? endDate : start.plusDays(30);
        if (end.isBefore(start)) {
            throw new RuntimeException("结束日期不能早于开始日期");
        }

        Long workerId = getCurrentWorkerId();
        List<WorkerSchedule> schedules = workerScheduleMapper.findByWorkerAndRange(workerId, start, end);

        Map<LocalDate, WorkerScheduleDayVO> result = new HashMap<>();
        LocalDate cursor = start;
        while (!cursor.isAfter(end)) {
            WorkerScheduleDayVO day = new WorkerScheduleDayVO();
            day.setDate(cursor);
            day.setWorkDay(false);
            day.setTimeSlots(new ArrayList<>());
            day.setBusySlots(new ArrayList<>());
            result.put(cursor, day);
            cursor = cursor.plusDays(1);
        }

        for (WorkerSchedule schedule : schedules) {
            WorkerScheduleDayVO day = result.get(schedule.getDate());
            if (day == null) {
                continue;
            }
            if ("FREE".equalsIgnoreCase(schedule.getStatus())) {
                day.setWorkDay(true);
                day.getTimeSlots().add(schedule.getTimeSlot());
            } else {
                day.getBusySlots().add(schedule.getTimeSlot());
            }
        }

        for (WorkerScheduleDayVO day : result.values()) {
            if (day.isWorkDay() && day.getTimeSlots().isEmpty()) {
                // 如果存在FREE记录被标记但时间段为空，则将其视为整天可用
                day.getTimeSlots().add("FULL_DAY");
            }
        }

        List<WorkerScheduleDayVO> list = new ArrayList<>(result.values());
        list.sort((a, b) -> a.getDate().compareTo(b.getDate()));
        return list;
    }

    @Transactional
    public void updateSchedule(WorkerScheduleUpdateRequest request) {
        Long workerId = getCurrentWorkerId();
        for (WorkerScheduleUpdateRequest.Item item : request.getItems()) {
            workerScheduleMapper.deleteByWorkerAndDate(workerId, item.getDate());

            if (item.isWorkDay()) {
                // 设置为"有空"状态
                List<String> slots = item.getTimeSlots();
                if (CollectionUtils.isEmpty(slots)) {
                    // 如果没有指定时间段，默认全天可用
                    slots = List.of("FULL_DAY");
                }
                for (String slot : slots) {
                    WorkerSchedule schedule = new WorkerSchedule();
                    schedule.setWorkerId(workerId);
                    schedule.setDate(item.getDate());
                    schedule.setTimeSlot(slot);
                    schedule.setStatus("FREE");
                    workerScheduleMapper.insert(schedule);
                }
            } else {
                // 设置为"忙碌"状态 - 插入一条BUSY记录
                WorkerSchedule schedule = new WorkerSchedule();
                schedule.setWorkerId(workerId);
                schedule.setDate(item.getDate());
                schedule.setTimeSlot("FULL_DAY");
                schedule.setStatus("BUSY");
                workerScheduleMapper.insert(schedule);
            }
        }
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
        throw new RuntimeException(
                "无法获取当前用户ID，details类型: " + (details != null ? details.getClass().getSimpleName() : "null"));
    }
}
