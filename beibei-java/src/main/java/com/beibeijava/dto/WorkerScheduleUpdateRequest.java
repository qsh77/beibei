package com.beibeijava.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * 阿姨档期批量更新请求
 */
@Data
public class WorkerScheduleUpdateRequest {

    @NotNull(message = "档期项不能为空")
    @Size(min = 1, message = "至少需要一条档期记录")
    private List<Item> items;

    @Data
    public static class Item {
        @NotNull(message = "日期不能为空")
        private LocalDate date;

        private boolean workDay;

        @Size(max = 8, message = "时间段数量过多")
        private List<String> timeSlots;
    }
}
