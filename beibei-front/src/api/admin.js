import request from './request'

// 管理员用户管理相关API

/**
 * 获取用户列表
 */
export const getAdminUsers = (params) => {
  return request({
    url: '/api/admin/users',
    method: 'get',
    params
  })
}

/**
 * 获取用户详情
 */
export const getUserDetail = (userId) => {
  return request({
    url: `/api/admin/users/${userId}`,
    method: 'get'
  })
}

/**
 * 更新用户状态
 */
export const updateUserStatus = (userId, data) => {
  return request({
    url: `/api/admin/users/${userId}/status`,
    method: 'put',
    data
  })
}

/**
 * 重置用户密码
 */
export const resetUserPassword = (userId) => {
  return request({
    url: `/api/admin/users/${userId}/password/reset`,
    method: 'put'
  })
}

/**
 * 更新用户角色
 */
export const updateUserRole = (userId, role) => {
  return request({
    url: `/api/admin/users/${userId}/role`,
    method: 'put',
    params: { role }
  })
}

/**
 * 批量更新用户状态
 */
export const batchUpdateUserStatus = (userIds, status) => {
  return request({
    url: '/api/admin/users/batch/status',
    method: 'put',
    params: {
      userIds: userIds.join(','),
      status
    }
  })
}

/**
 * 获取用户统计信息
 */
export const getUserStats = () => {
  return request({
    url: '/api/admin/users/stats',
    method: 'get'
  })
}

/**
 * 搜索用户
 */
export const searchUsers = (keyword, limit = 10) => {
  return request({
    url: '/api/admin/users/search',
    method: 'get',
    params: { keyword, limit }
  })
}

/**
 * 导出用户数据
 */
export const exportUsers = (params) => {
  return request({
    url: '/api/admin/users/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

// 管理员订单管理相关API

/**
 * 获取订单列表
 */
export const getAdminOrders = (params) => {
  return request({
    url: '/api/admin/orders',
    method: 'get',
    params
  })
}

/**
 * 获取订单详情
 */
export const getOrderDetail = (orderId) => {
  return request({
    url: `/api/admin/orders/${orderId}`,
    method: 'get'
  })
}

/**
 * 更新订单状态
 */
export const updateOrderStatus = (orderId, status) => {
  return request({
    url: `/api/admin/orders/${orderId}/status`,
    method: 'put',
    data: { status }
  })
}

/**
 * 分配订单给阿姨
 */
export const assignOrder = (orderId, workerId) => {
  return request({
    url: `/api/admin/orders/${orderId}/assign`,
    method: 'put',
    params: { workerId }
  })
}

// 管理员统计相关API

/**
 * 获取仪表盘统计数据
 */
export const getDashboardStats = () => {
  return request({
    url: '/api/admin/dashboard/stats',
    method: 'get'
  })
}

/**
 * 获取订单趋势数据
 */
export const getOrderTrend = (period = '7days') => {
  return request({
    url: '/api/admin/statistics/order-trend',
    method: 'get',
    params: { period }
  })
}

/**
 * 获取收入统计数据
 */
export const getRevenueStats = (params) => {
  return request({
    url: '/api/admin/statistics/revenue',
    method: 'get',
    params
  })
}

// 管理员阿姨管理相关API

/**
 * 获取阿姨列表
 */
export const getAdminWorkers = (params) => {
  return request({
    url: '/api/admin/workers',
    method: 'get',
    params
  })
}

/**
 * 获取阿姨详情
 */
export const getWorkerDetail = (workerId) => {
  return request({
    url: `/api/admin/workers/${workerId}`,
    method: 'get'
  })
}

/**
 * 更新阿姨信息
 */
export const updateWorker = (workerId, data) => {
  return request({
    url: `/api/admin/workers/${workerId}`,
    method: 'put',
    data
  })
}

/**
 * 更新阿姨状态
 */
export const updateWorkerStatus = (workerId, status) => {
  return request({
    url: `/api/admin/workers/${workerId}/status`,
    method: 'put',
    params: { status }
  })
}

/**
 * 批量更新阿姨状态
 */
export const batchUpdateWorkerStatus = (workerIds, status) => {
  return request({
    url: '/api/admin/workers/batch/status',
    method: 'put',
    params: {
      workerIds: workerIds.join(','),
      status
    }
  })
}

/**
 * 直接创建阿姨账号
 */
export const createWorker = (data) => {
  return request({
    url: '/api/admin/workers/create',
    method: 'post',
    data
  })
}

/**
 * 从用户转换为阿姨
 */
export const createWorkerFromUser = (userId, level, years, bio) => {
  return request({
    url: '/api/admin/workers/create-from-user',
    method: 'post',
    params: { userId, level, years, bio }
  })
}

/**
 * 删除阿姨
 */
export const removeWorker = (workerId) => {
  return request({
    url: `/api/admin/workers/${workerId}`,
    method: 'delete'
  })
}

/**
 * 获取阿姨统计信息
 */
export const getWorkerStats = () => {
  return request({
    url: '/api/admin/workers/stats',
    method: 'get'
  })
}

/**
 * 搜索阿姨
 */
export const searchWorkers = (keyword, limit = 10) => {
  return request({
    url: '/api/admin/workers/search',
    method: 'get',
    params: { keyword, limit }
  })
}

/**
 * 检查阿姨等级升级条件
 */
export const checkWorkerUpgrade = (workerId, targetLevel) => {
  return request({
    url: `/api/admin/workers/${workerId}/upgrade-check`,
    method: 'get',
    params: { targetLevel }
  })
}

/**
 * 导出阿姨数据
 */
export const exportWorkers = (params) => {
  return request({
    url: '/api/admin/workers/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

/**
 * 获取阿姨等级分布
 */
export const getWorkerLevelDistribution = () => {
  return request({
    url: '/api/admin/workers/levels/distribution',
    method: 'get'
  })
}

/**
 * 审核阿姨认证
 */
export const reviewWorkerCert = (workerId, data) => {
  return request({
    url: `/api/admin/workers/${workerId}/certification`,
    method: 'put',
    data
  })
}

// 管理员服务管理相关API

/**
 * 获取服务列表
 */
export const getAdminServices = (params) => {
  return request({
    url: '/api/admin/services',
    method: 'get',
    params
  })
}

/**
 * 创建服务
 */
export const createService = (data) => {
  return request({
    url: '/api/admin/services',
    method: 'post',
    data
  })
}

/**
 * 更新服务
 */
export const updateService = (serviceId, data) => {
  return request({
    url: `/api/admin/services/${serviceId}`,
    method: 'put',
    data
  })
}

/**
 * 删除服务
 */
export const deleteService = (serviceId) => {
  return request({
    url: `/api/admin/services/${serviceId}`,
    method: 'delete'
  })
}

/**
 * 获取服务分类
 */
export const getServiceCategories = () => {
  return request({
    url: '/api/admin/services/categories',
    method: 'get'
  })
}

/**
 * 创建服务分类
 */
export const createCategory = (data) => {
  return request({
    url: '/api/admin/services/categories',
    method: 'post',
    data
  })
}

/**
 * 更新服务分类
 */
export const updateCategory = (categoryId, data) => {
  return request({
    url: `/api/admin/services/categories/${categoryId}`,
    method: 'put',
    data
  })
}

/**
 * 删除服务分类
 */
export const deleteCategory = (categoryId) => {
  return request({
    url: `/api/admin/services/categories/${categoryId}`,
    method: 'delete'
  })
}

/**
 * 获取所有分类（用于组件加载）
 */
export const getAllCategories = () => {
  return request({
    url: '/api/admin/services/categories',
    method: 'get'
  })
}

/**
 * 更新服务状态
 */
export const updateServiceStatus = (serviceId, status) => {
  return request({
    url: `/api/admin/services/${serviceId}/status`,
    method: 'put',
    params: { status }
  })
}

/**
 * 更新服务热门状态
 */
export const updateServiceHot = (serviceId, hot) => {
  return request({
    url: `/api/admin/services/${serviceId}/hot`,
    method: 'put',
    params: { hot }
  })
}

// 管理员系统设置相关API

/**
 * 获取系统配置
 */
export const getSystemConfig = () => {
  return request({
    url: '/api/admin/system/config',
    method: 'get'
  })
}

/**
 * 更新系统配置
 */
export const updateSystemConfig = (data) => {
  return request({
    url: '/api/admin/system/config',
    method: 'put',
    data
  })
}

/**
 * 获取操作日志
 */
export const getOperationLogs = (params) => {
  return request({
    url: '/api/admin/system/logs',
    method: 'get',
    params
  })
}