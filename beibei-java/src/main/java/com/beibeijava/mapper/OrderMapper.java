package com.beibeijava.mapper;

import com.beibeijava.entity.Order;
import com.beibeijava.vo.OrderDetailVO;
import com.beibeijava.vo.OrderListVO;
import com.beibeijava.vo.OrderStatsVO;
import com.beibeijava.vo.WorkerEarningDetailVO;
import com.beibeijava.vo.WorkerEarningTrendVO;
import com.beibeijava.vo.WorkerEarningsAggregate;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 订单数据访问层
 */
@Mapper
public interface OrderMapper {

        /**
         * 根据用户ID查询订单列表
         */
        @Select("SELECT * FROM `order` WHERE customer_id = #{customerId} ORDER BY created_at DESC")
        List<Order> findByCustomerId(@Param("customerId") Long customerId);

        /**
         * 根据ID查询订单
         */
        @Select("SELECT * FROM `order` WHERE id = #{id}")
        Order findById(@Param("id") Long id);

        /**
         * 根据订单号查询订单
         */
        @Select("SELECT * FROM `order` WHERE order_no = #{orderNo}")
        Order findByOrderNo(@Param("orderNo") String orderNo);

        /**
         * 插入订单
         */
        @Insert("INSERT INTO `order` (order_no, customer_id, service_id, worker_id, schedule_date, time_slot, address_id, address_text, amount, status, remark, created_at, updated_at) "
                        +
                        "VALUES (#{orderNo}, #{customerId}, #{serviceId}, #{workerId}, #{scheduleDate}, #{timeSlot}, #{addressId}, #{addressText}, #{amount}, #{status}, #{remark}, #{createdAt}, #{updatedAt})")
        @Options(useGeneratedKeys = true, keyProperty = "id")
        int insert(Order order);

        /**
         * 更新订单状态
         */
        @Update("UPDATE `order` SET status = #{status}, updated_at = #{updatedAt} WHERE id = #{id}")
        int updateStatus(@Param("id") Long id, @Param("status") String status,
                        @Param("updatedAt") java.time.LocalDateTime updatedAt);

        /**
         * 更新订单阿姨ID
         */
        @Update("UPDATE `order` SET worker_id = #{workerId}, status = #{status}, updated_at = #{updatedAt} WHERE id = #{id}")
        int assignWorker(@Param("id") Long id, @Param("workerId") Long workerId, @Param("status") String status,
                        @Param("updatedAt") java.time.LocalDateTime updatedAt);

        /**
         * 根据阿姨ID查询订单列表
         */
        @Select("SELECT * FROM `order` WHERE worker_id = #{workerId} ORDER BY created_at DESC")
        List<Order> findByWorkerId(@Param("workerId") Long workerId);

        /**
         * 查询待分配订单
         */
        @Select("SELECT * FROM `order` WHERE status = 'CREATED' ORDER BY created_at ASC")
        List<Order> findPendingOrders();

        /**
         * 查询所有订单（基础查询）
         */
        @Select("SELECT o.id, o.order_no as orderNo, " +
                        "up1.name as customerName, u1.phone as customerPhone, " +
                        "up2.name as workerName, " +
                        "s.name as serviceName, " +
                        "o.schedule_date as scheduleDate, o.time_slot as timeSlot, " +
                        "o.amount, o.status, o.created_at as createdAt, o.updated_at as updatedAt " +
                        "FROM `order` o " +
                        "LEFT JOIN user u1 ON o.customer_id = u1.id " +
                        "LEFT JOIN user_profile up1 ON u1.id = up1.user_id " +
                        "LEFT JOIN worker w ON o.worker_id = w.id " +
                        "LEFT JOIN user u2 ON w.user_id = u2.id " +
                        "LEFT JOIN user_profile up2 ON u2.id = up2.user_id " +
                        "LEFT JOIN service s ON o.service_id = s.id " +
                        "ORDER BY o.created_at DESC " +
                        "LIMIT #{offset}, #{size}")
        List<OrderListVO> findAllOrdersWithPaging(@Param("offset") int offset, @Param("size") int size);

        /**
         * 查询阿姨自己的订单列表
         */
        @Select("SELECT o.id, o.order_no as orderNo, " +
                        "up1.name as customerName, u1.phone as customerPhone, " +
                        "up2.name as workerName, " +
                        "s.name as serviceName, " +
                        "o.schedule_date as scheduleDate, o.time_slot as timeSlot, " +
                        "o.amount, o.status, o.created_at as createdAt, o.updated_at as updatedAt " +
                        "FROM `order` o " +
                        "LEFT JOIN user u1 ON o.customer_id = u1.id " +
                        "LEFT JOIN user_profile up1 ON u1.id = up1.user_id " +
                        "LEFT JOIN worker w ON o.worker_id = w.id " +
                        "LEFT JOIN user u2 ON w.user_id = u2.id " +
                        "LEFT JOIN user_profile up2 ON u2.id = up2.user_id " +
                        "LEFT JOIN service s ON o.service_id = s.id " +
                        "WHERE o.worker_id = #{workerId} " +
                        "ORDER BY o.created_at DESC")
        List<OrderListVO> findWorkerOrderList(@Param("workerId") Long workerId);

        /**
         * 根据状态查询订单
         */
        @Select("SELECT o.id, o.order_no as orderNo, " +
                        "up1.name as customerName, u1.phone as customerPhone, " +
                        "up2.name as workerName, " +
                        "s.name as serviceName, " +
                        "o.schedule_date as scheduleDate, o.time_slot as timeSlot, " +
                        "o.amount, o.status, o.created_at as createdAt, o.updated_at as updatedAt " +
                        "FROM `order` o " +
                        "LEFT JOIN user u1 ON o.customer_id = u1.id " +
                        "LEFT JOIN user_profile up1 ON u1.id = up1.user_id " +
                        "LEFT JOIN worker w ON o.worker_id = w.id " +
                        "LEFT JOIN user u2 ON w.user_id = u2.id " +
                        "LEFT JOIN user_profile up2 ON u2.id = up2.user_id " +
                        "LEFT JOIN service s ON o.service_id = s.id " +
                        "WHERE o.status = #{status} " +
                        "ORDER BY o.created_at DESC " +
                        "LIMIT #{offset}, #{size}")
        List<OrderListVO> findOrdersByStatusWithPaging(@Param("status") String status, @Param("offset") int offset,
                        @Param("size") int size);

        /**
         * 根据关键字搜索订单
         */
        @Select("SELECT o.id, o.order_no as orderNo, " +
                        "up1.name as customerName, u1.phone as customerPhone, " +
                        "up2.name as workerName, " +
                        "s.name as serviceName, " +
                        "o.schedule_date as scheduleDate, o.time_slot as timeSlot, " +
                        "o.amount, o.status, o.created_at as createdAt, o.updated_at as updatedAt " +
                        "FROM `order` o " +
                        "LEFT JOIN user u1 ON o.customer_id = u1.id " +
                        "LEFT JOIN user_profile up1 ON u1.id = up1.user_id " +
                        "LEFT JOIN worker w ON o.worker_id = w.id " +
                        "LEFT JOIN user u2 ON w.user_id = u2.id " +
                        "LEFT JOIN user_profile up2 ON u2.id = up2.user_id " +
                        "LEFT JOIN service s ON o.service_id = s.id " +
                        "WHERE (o.order_no LIKE CONCAT('%', #{keyword}, '%') " +
                        "OR up1.name LIKE CONCAT('%', #{keyword}, '%') " +
                        "OR u1.phone LIKE CONCAT('%', #{keyword}, '%')) " +
                        "ORDER BY o.created_at DESC " +
                        "LIMIT #{offset}, #{size}")
        List<OrderListVO> findOrdersByKeywordWithPaging(@Param("keyword") String keyword, @Param("offset") int offset,
                        @Param("size") int size);

        /**
         * 统计所有订单数量
         */
        @Select("SELECT COUNT(*) FROM `order`")
        long countAllOrders();

        /**
         * 根据状态统计订单数量
         */
        @Select("SELECT COUNT(*) FROM `order` WHERE status = #{status}")
        long countOrdersByStatus(@Param("status") String status);

        /**
         * 根据关键字统计订单数量
         */
        @Select("SELECT COUNT(*) FROM `order` o " +
                        "LEFT JOIN user u1 ON o.customer_id = u1.id " +
                        "LEFT JOIN user_profile up1 ON u1.id = up1.user_id " +
                        "WHERE (o.order_no LIKE CONCAT('%', #{keyword}, '%') " +
                        "OR up1.name LIKE CONCAT('%', #{keyword}, '%') " +
                        "OR u1.phone LIKE CONCAT('%', #{keyword}, '%'))")
        long countOrdersByKeyword(@Param("keyword") String keyword);

        /**
         * 获取订单详情（包含关联信息）
         */
        @Select("SELECT o.id, o.order_no as orderNo, " +
                        "o.customer_id as customerId, up1.name as customerName, u1.phone as customerPhone, up1.avatar as customerAvatar, "
                        +
                        "o.worker_id as workerId, up2.name as workerName, u2.phone as workerPhone, up2.avatar as workerAvatar, "
                        +
                        "w.level as workerLevel, w.years as workerYears, w.score as workerScore, " +
                        "o.service_id as serviceId, s.name as serviceName, s.description as serviceDescription, s.unit as serviceUnit, "
                        +
                        "sc.name as categoryName, " +
                        "o.schedule_date as scheduleDate, o.time_slot as timeSlot, " +
                        "o.address_id as addressId, o.address_text as addressText, " +
                        "ua.contact_name as contactName, ua.contact_phone as contactPhone, " +
                        "o.amount, o.status, o.remark, o.created_at as createdAt, o.updated_at as updatedAt " +
                        "FROM `order` o " +
                        "LEFT JOIN user u1 ON o.customer_id = u1.id " +
                        "LEFT JOIN user_profile up1 ON u1.id = up1.user_id " +
                        "LEFT JOIN worker w ON o.worker_id = w.id " +
                        "LEFT JOIN user u2 ON w.user_id = u2.id " +
                        "LEFT JOIN user_profile up2 ON u2.id = up2.user_id " +
                        "LEFT JOIN service s ON o.service_id = s.id " +
                        "LEFT JOIN service_category sc ON s.category_id = sc.id " +
                        "LEFT JOIN user_address ua ON o.address_id = ua.id " +
                        "WHERE o.id = #{id}")
        OrderDetailVO findOrderDetailById(@Param("id") Long id);

        /**
         * 获取订单统计数据
         */
        @Select("SELECT " +
                        "COUNT(*) as total, " +
                        "SUM(CASE WHEN status = 'CREATED' THEN 1 ELSE 0 END) as pending, " +
                        "SUM(CASE WHEN status = 'ASSIGNED' THEN 1 ELSE 0 END) as assigned, " +
                        "SUM(CASE WHEN status = 'DOING' THEN 1 ELSE 0 END) as doing, " +
                        "SUM(CASE WHEN status = 'DONE' THEN 1 ELSE 0 END) as done, " +
                        "SUM(CASE WHEN status = 'CANCELED' THEN 1 ELSE 0 END) as canceled " +
                        "FROM `order`")
        OrderStatsVO getOrderStats();

        /**
         * 聚合阿姨收入
         */
        @Select("<script>" +
                        "SELECT COALESCE(SUM(amount),0) as total, COUNT(*) as orderCount " +
                        "FROM `order` " +
                        "WHERE worker_id = #{workerId} AND status = 'DONE' " +
                        "<if test='startAt != null'>AND updated_at &gt;= #{startAt} </if>" +
                        "<if test='endAt != null'>AND updated_at &lt;= #{endAt} </if>" +
                        "</script>")
        WorkerEarningsAggregate aggregateWorkerEarnings(@Param("workerId") Long workerId,
                        @Param("startAt") java.time.LocalDateTime startAt,
                        @Param("endAt") java.time.LocalDateTime endAt);

        /**
         * 查询阿姨收入趋势
         */
        @Select("SELECT DATE(updated_at) as date, COALESCE(SUM(amount),0) as amount " +
                        "FROM `order` WHERE worker_id = #{workerId} AND status = 'DONE' " +
                        "AND DATE(updated_at) BETWEEN #{startDate} AND #{endDate} " +
                        "GROUP BY DATE(updated_at) ORDER BY DATE(updated_at)")
        List<WorkerEarningTrendVO> findWorkerEarningTrend(@Param("workerId") Long workerId,
                        @Param("startDate") java.time.LocalDate startDate,
                        @Param("endDate") java.time.LocalDate endDate);

        /**
         * 查询阿姨收入明细
         */
        @Select("<script>" +
                        "SELECT o.id as orderId, o.order_no as orderNo, s.name as serviceName, " +
                        "up1.name as customerName, u1.phone as customerPhone, o.amount, o.status, o.updated_at as completedAt "
                        +
                        "FROM `order` o " +
                        "LEFT JOIN service s ON o.service_id = s.id " +
                        "LEFT JOIN user u1 ON o.customer_id = u1.id " +
                        "LEFT JOIN user_profile up1 ON u1.id = up1.user_id " +
                        "WHERE o.worker_id = #{workerId} AND o.status = 'DONE' " +
                        "<if test='startDate != null'>AND DATE(o.updated_at) &gt;= #{startDate} </if>" +
                        "<if test='endDate != null'>AND DATE(o.updated_at) &lt;= #{endDate} </if>" +
                        "ORDER BY o.updated_at DESC " +
                        "LIMIT #{offset}, #{size}" +
                        "</script>")
        List<WorkerEarningDetailVO> findWorkerEarningDetails(@Param("workerId") Long workerId,
                        @Param("startDate") java.time.LocalDate startDate,
                        @Param("endDate") java.time.LocalDate endDate,
                        @Param("offset") int offset,
                        @Param("size") int size);

        /**
         * 统计阿姨收入明细数量
         */
        @Select("<script>" +
                        "SELECT COUNT(*) FROM `order` o WHERE o.worker_id = #{workerId} AND o.status = 'DONE' " +
                        "<if test='startDate != null'>AND DATE(o.updated_at) &gt;= #{startDate} </if>" +
                        "<if test='endDate != null'>AND DATE(o.updated_at) &lt;= #{endDate} </if>" +
                        "</script>")
        Long countWorkerEarningDetails(@Param("workerId") Long workerId,
                        @Param("startDate") java.time.LocalDate startDate,
                        @Param("endDate") java.time.LocalDate endDate);

        /**
         * 查询指定阿姨可接的订单（默认空闲，仅排除显式设置为忙碌的时段）
         */
        @Select("SELECT o.id, o.order_no as orderNo, " +
                        "up1.name as customerName, u1.phone as customerPhone, " +
                        "s.name as serviceName, s.description as serviceDescription, " +
                        "o.schedule_date as scheduleDate, o.time_slot as timeSlot, " +
                        "o.address_text as addressText, " +
                        "o.amount, o.status, o.remark, o.created_at as createdAt " +
                        "FROM `order` o " +
                        "LEFT JOIN user u1 ON o.customer_id = u1.id " +
                        "LEFT JOIN user_profile up1 ON u1.id = up1.user_id " +
                        "LEFT JOIN service s ON o.service_id = s.id " +
                        "WHERE o.status = 'CREATED' AND o.worker_id IS NULL " +
                        "AND NOT EXISTS (" +
                        "  SELECT 1 FROM worker_schedule ws " +
                        "  WHERE ws.worker_id = #{workerId} " +
                        "  AND ws.date = o.schedule_date " +
                        "  AND (ws.time_slot = o.time_slot OR ws.time_slot = 'FULL_DAY') " +
                        "  AND ws.status = 'BUSY'" +
                        ") " +
                        "ORDER BY o.created_at DESC")
        List<OrderListVO> findAvailableOrdersForWorker(@Param("workerId") Long workerId);
}
