package com.beibeijava.vo;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * 阿姨档期返回视图
 */
@Data
public class WorkerScheduleDayVO {

    private LocalDate date;

    private boolean workDay;

    private List<String> timeSlots;

    private List<String> busySlots;
}
