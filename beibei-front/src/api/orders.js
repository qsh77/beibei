import request from './request.js'

// 订单相关API
export const ordersApi = {
  // 创建订单
  createOrder(data) {
    return request.post('/api/orders', data)
  },

  // 获取订单列表
  getUserOrders() {
    return request.get('/api/orders')
  },

  // 根据ID获取订单详情
  getOrderById(id) {
    return request.get(`/api/orders/${id}`)
  },

  // 取消订单
  cancelOrder(id) {
    return request.put(`/api/orders/${id}/cancel`)
  },

  // 获取待分配订单（管理员/阿姨使用）
  getPendingOrders() {
    return request.get('/api/orders/pending')
  },

  // 分配订单
  assignOrder(id, workerId) {
    return request.put(`/api/orders/${id}/assign`, null, { params: { workerId } })
  }
}

// 管理员订单API
export const adminOrdersApi = {
  // 分页查询订单列表
  getOrderList(params) {
    return request.get('/api/admin/orders', { params })
  },

  // 获取订单详情
  getOrderDetail(id) {
    return request.get(`/api/admin/orders/${id}`)
  },

  // 获取订单统计
  getOrderStats() {
    return request.get('/api/admin/orders/stats')
  },

  // 更新订单状态
  updateOrderStatus(data) {
    return request.put('/api/admin/orders/status', data)
  }
}

// 阿姨端订单API
export const workerOrdersApi = {
  // 获取阿姨的订单列表
  getWorkerOrders() {
    return request.get('/api/worker/orders')
  },

  // 获取订单详情
  getOrderDetail(id) {
    return request.get(`/api/worker/orders/${id}`)
  },

  // 接受订单
  acceptOrder(id) {
    return request.put(`/api/worker/orders/${id}/accept`)
  },

  // 完成订单
  completeOrder(id) {
    return request.put(`/api/worker/orders/${id}/complete`)
  }
}
