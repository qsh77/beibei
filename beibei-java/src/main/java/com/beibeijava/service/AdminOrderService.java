package com.beibeijava.service;

import com.beibeijava.dto.OrderQueryRequest;
import com.beibeijava.dto.UpdateOrderStatusRequest;
import com.beibeijava.mapper.OrderMapper;
import com.beibeijava.vo.OrderDetailVO;
import com.beibeijava.vo.OrderListVO;
import com.beibeijava.vo.OrderStatsVO;
import com.beibeijava.vo.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * 管理员订单业务层
 */
@Service
@RequiredArgsConstructor
public class AdminOrderService {

    private final OrderMapper orderMapper;

    /**
     * 分页查询订单列表
     */
    public PageResult<OrderListVO> getOrderList(OrderQueryRequest request) {
        int offset = (request.getPage() - 1) * request.getSize();
        List<OrderListVO> orders;
        long total;

        // 根据查询条件选择不同的查询方法
        if (request.getKeyword() != null && !request.getKeyword().trim().isEmpty()) {
            // 关键字搜索
            orders = orderMapper.findOrdersByKeywordWithPaging(request.getKeyword().trim(), offset, request.getSize());
            total = orderMapper.countOrdersByKeyword(request.getKeyword().trim());
        } else if (request.getStatus() != null && !request.getStatus().trim().isEmpty()) {
            // 状态筛选
            orders = orderMapper.findOrdersByStatusWithPaging(request.getStatus(), offset, request.getSize());
            total = orderMapper.countOrdersByStatus(request.getStatus());
        } else {
            // 查询所有
            orders = orderMapper.findAllOrdersWithPaging(offset, request.getSize());
            total = orderMapper.countAllOrders();
        }

        return new PageResult<>(orders, total, request.getPage(), request.getSize());
    }

    /**
     * 获取订单详情
     */
    public OrderDetailVO getOrderDetail(Long id) {
        OrderDetailVO order = orderMapper.findOrderDetailById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        return order;
    }

    /**
     * 获取订单统计信息
     */
    public OrderStatsVO getOrderStats() {
        return orderMapper.getOrderStats();
    }

    /**
     * 更新订单状态
     */
    @Transactional
    public void updateOrderStatus(UpdateOrderStatusRequest request) {
        // 验证状态是否有效
        List<String> validStatuses = Arrays.asList("CREATED", "ASSIGNED", "DOING", "DONE", "CANCELED");
        if (!validStatuses.contains(request.getStatus())) {
            throw new RuntimeException("无效的订单状态");
        }

        // 获取当前订单状态
        com.beibeijava.entity.Order currentOrder = orderMapper.findById(request.getOrderId());
        if (currentOrder == null) {
            throw new RuntimeException("订单不存在");
        }

        // 验证状态转换是否合法
        validateStatusTransition(currentOrder.getStatus(), request.getStatus());

        // 更新订单状态
        int result = orderMapper.updateStatus(request.getOrderId(), request.getStatus(), LocalDateTime.now());
        if (result == 0) {
            throw new RuntimeException("更新订单状态失败");
        }

        // TODO: 记录订单状态变更日志
        // logOrderStatusChange(request.getOrderId(), currentOrder.getStatus(), request.getStatus(), request.getNote());
    }

    /**
     * 验证状态转换是否合法
     */
    private void validateStatusTransition(String fromStatus, String toStatus) {
        // 如果状态相同，不需要更新
        if (fromStatus.equals(toStatus)) {
            throw new RuntimeException("订单状态未发生变化");
        }

        // 定义合法的状态转换
        switch (fromStatus) {
            case "CREATED":
                if (!Arrays.asList("ASSIGNED", "CANCELED").contains(toStatus)) {
                    throw new RuntimeException("待分配订单只能分配给阿姨或取消");
                }
                break;
            case "ASSIGNED":
                if (!Arrays.asList("DOING", "CANCELED").contains(toStatus)) {
                    throw new RuntimeException("已分配订单只能开始服务或取消");
                }
                break;
            case "DOING":
                if (!Arrays.asList("DONE", "CANCELED").contains(toStatus)) {
                    throw new RuntimeException("服务中订单只能完成或取消");
                }
                break;
            case "DONE":
                throw new RuntimeException("已完成订单无法再次修改状态");
            case "CANCELED":
                throw new RuntimeException("已取消订单无法再次修改状态");
            default:
                throw new RuntimeException("未知的订单状态");
        }
    }
}