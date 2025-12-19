package com.beibeijava.entity;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 阿姨档期表实体
 */
@Data
public class WorkerSchedule {

    private Long id;

    /**
     * 阿姨ID
     */
    private Long workerId;

    /**
     * 日期
     */
    private LocalDate date;

    /**
     * 时间段描述，比如 AM/PM 或 09:00-12:00
     */
    private String timeSlot;

    /**
     * 档期状态：FREE、BUSY
     */
    private String status;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
