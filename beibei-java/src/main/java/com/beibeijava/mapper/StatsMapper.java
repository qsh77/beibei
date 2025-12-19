package com.beibeijava.mapper;

import com.beibeijava.vo.OverviewStatsVO;
import com.beibeijava.vo.OrderTrendVO;
import com.beibeijava.vo.ServiceStatsVO;
import com.beibeijava.vo.UserRegistrationTrendVO;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface StatsMapper {

        @Select("SELECT " +
                        "(SELECT COUNT(*) FROM user WHERE role = 'USER') as totalUsers, " +
                        "(SELECT COUNT(*) FROM user WHERE role = 'WORKER') as totalWorkers, " +
                        "(SELECT COUNT(*) FROM `order`) as totalOrders, " +
                        "(SELECT COALESCE(SUM(amount), 0) FROM `order` WHERE status != 'CANCELED') as totalRevenue, " +
                        "(SELECT COUNT(*) FROM `order` WHERE DATE(created_at) = CURDATE()) as todayOrders, " +
                        "(SELECT COALESCE(SUM(amount), 0) FROM `order` WHERE DATE(created_at) = CURDATE() AND status != 'CANCELED') as todayRevenue, "
                        +
                        "(SELECT COUNT(*) FROM `order` WHERE YEAR(created_at) = YEAR(CURDATE()) AND MONTH(created_at) = MONTH(CURDATE())) as monthOrders, "
                        +
                        "(SELECT COALESCE(SUM(amount), 0) FROM `order` WHERE YEAR(created_at) = YEAR(CURDATE()) AND MONTH(created_at) = MONTH(CURDATE()) AND status != 'CANCELED') as monthRevenue")
        OverviewStatsVO getOverviewStats();

        @Select("SELECT " +
                        "DATE(created_at) as date, " +
                        "COUNT(*) as orderCount, " +
                        "COALESCE(SUM(CASE WHEN status != 'CANCELED' THEN amount ELSE 0 END), 0) as revenue " +
                        "FROM `order` " +
                        "WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL #{days} DAY) " +
                        "GROUP BY DATE(created_at) " +
                        "ORDER BY date")
        List<OrderTrendVO> getOrderTrend(@Param("days") int days);

        @Select("SELECT " +
                        "DATE(created_at) as date, " +
                        "YEAR(created_at) as year, " +
                        "MONTH(created_at) as month, " +
                        "COUNT(*) as orderCount, " +
                        "COALESCE(SUM(CASE WHEN status != 'CANCELED' THEN amount ELSE 0 END), 0) as revenue " +
                        "FROM `order` " +
                        "WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL #{months} MONTH) " +
                        "GROUP BY YEAR(created_at), MONTH(created_at) " +
                        "ORDER BY year, month")
        List<OrderTrendVO> getMonthlyOrderTrend(@Param("months") int months);

        @Select("SELECT " +
                        "s.id as serviceId, " +
                        "s.name as serviceName, " +
                        "COUNT(o.id) as orderCount, " +
                        "COALESCE(SUM(CASE WHEN o.status != 'CANCELED' THEN o.amount ELSE 0 END), 0) as revenue, " +
                        "COALESCE(s.rating, 0) as avgRating " +
                        "FROM service s " +
                        "LEFT JOIN `order` o ON s.id = o.service_id " +
                        "WHERE s.status = 1 " +
                        "GROUP BY s.id, s.name, s.rating " +
                        "ORDER BY orderCount DESC, revenue DESC " +
                        "LIMIT #{limit}")
        List<ServiceStatsVO> getTopServices(@Param("limit") int limit);

        @Select("SELECT " +
                        "DATE(created_at) as date, " +
                        "COUNT(CASE WHEN role = 'USER' THEN 1 END) as userCount, " +
                        "COUNT(CASE WHEN role = 'WORKER' THEN 1 END) as workerCount " +
                        "FROM user " +
                        "WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL #{days} DAY) " +
                        "GROUP BY DATE(created_at) " +
                        "ORDER BY date")
        List<UserRegistrationTrendVO> getUserRegistrationTrend(@Param("days") int days);

        @Select("SELECT " +
                        "status, " +
                        "COUNT(*) as count " +
                        "FROM `order` " +
                        "GROUP BY status")
        List<Map<String, Object>> getOrderStatusStats();

        @Select("SELECT " +
                        "role, " +
                        "COUNT(*) as count " +
                        "FROM user " +
                        "WHERE status = 1 " +
                        "GROUP BY role")
        List<Map<String, Object>> getUserRoleStats();

        @Select("SELECT " +
                        "w.id as workerId, " +
                        "up.name as workerName, " +
                        "COUNT(o.id) as orderCount, " +
                        "COALESCE(SUM(CASE WHEN o.status != 'CANCELED' THEN o.amount ELSE 0 END), 0) as revenue, " +
                        "COALESCE(w.score, 0) as score " +
                        "FROM worker w " +
                        "LEFT JOIN user_profile up ON w.user_id = up.user_id " +
                        "LEFT JOIN `order` o ON w.id = o.worker_id " +
                        "WHERE w.status = 1 " +
                        "GROUP BY w.id, up.name, w.score " +
                        "ORDER BY score DESC, orderCount DESC " +
                        "LIMIT #{limit}")
        List<Map<String, Object>> getTopWorkers(@Param("limit") int limit);
}