package com.beibeijava.mapper;

import com.beibeijava.entity.WorkerSchedule;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 阿姨档期数据访问
 */
@Mapper
public interface WorkerScheduleMapper {

    /**
     * 查询指定日期范围内的档期
     */
    @Select("SELECT * FROM worker_schedule WHERE worker_id = #{workerId} AND date BETWEEN #{startDate} AND #{endDate}")
    List<WorkerSchedule> findByWorkerAndRange(@Param("workerId") Long workerId,
                                              @Param("startDate") LocalDate startDate,
                                              @Param("endDate") LocalDate endDate);

    /**
     * 删除某日档期
     */
    @Delete("DELETE FROM worker_schedule WHERE worker_id = #{workerId} AND date = #{date}")
    int deleteByWorkerAndDate(@Param("workerId") Long workerId, @Param("date") LocalDate date);

    /**
     * 插入档期
     */
    @Insert("INSERT INTO worker_schedule (worker_id, date, time_slot, status) " +
            "VALUES (#{workerId}, #{date}, #{timeSlot}, #{status})")
    int insert(WorkerSchedule schedule);
}
