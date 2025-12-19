package com.beibeijava.service;

import com.beibeijava.dto.OrderQueryRequest;
import com.beibeijava.mapper.OrderMapper;
import com.beibeijava.vo.OrderDetailVO;
import com.beibeijava.vo.OrderListVO;
import com.beibeijava.vo.OrderStatsVO;
import com.beibeijava.vo.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

}