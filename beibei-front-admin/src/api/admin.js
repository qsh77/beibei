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
export const updateUserStatus = (userId, status) => {
  return request({
    url: `/api/admin/users/${userId}/status`,
    method: 'put',
    params: { status }
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
 * 获取订单统计
 */
export const getOrderStats = () => {
  return request({
    url: '/api/admin/orders/stats',
    method: 'get'
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
 * 获取阿姨的评价列表
 */
export const getWorkerReviews = (workerId) => {
  return request({
    url: `/api/reviews/worker/${workerId}`,
    method: 'get'
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
 * 获取服务详情
 */
export const getServiceDetail = (serviceId) => {
  return request({
    url: `/api/admin/services/${serviceId}`,
    method: 'get'
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

// 统计相关API

/**
 * 获取概览统计
 */
export const getOverviewStats = () => {
  return request({
    url: '/api/admin/stats/overview',
    method: 'get'
  })
}

/**
 * 获取订单日趋势
 */
export const getDailyOrderTrend = (days = 30) => {
  return request({
    url: '/api/admin/stats/order/trend/daily',
    method: 'get',
    params: { days }
  })
}

/**
 * 获取订单月趋势
 */
export const getMonthlyOrderTrend = (months = 12) => {
  return request({
    url: '/api/admin/stats/order/trend/monthly',
    method: 'get',
    params: { months }
  })
}

/**
 * 获取订单状态统计
 */
export const getOrderStatusStats = () => {
  return request({
    url: '/api/admin/stats/order/status',
    method: 'get'
  })
}

/**
 * 获取热门服务统计
 */
export const getTopServices = (limit = 10) => {
  return request({
    url: '/api/admin/stats/service/top',
    method: 'get',
    params: { limit }
  })
}

/**
 * 获取用户注册趋势
 */
export const getUserRegistrationTrend = (days = 30) => {
  return request({
    url: '/api/admin/stats/user/registration/trend',
    method: 'get',
    params: { days }
  })
}

/**
 * 获取用户角色统计
 */
export const getUserRoleStats = () => {
  return request({
    url: '/api/admin/stats/user/role',
    method: 'get'
  })
}

/**
 * 获取优秀阿姨统计
 */
export const getTopWorkers = (limit = 10) => {
  return request({
    url: '/api/admin/stats/worker/top',
    method: 'get',
    params: { limit }
  })
}

// 管理员个人资料相关API

/**
 * 获取管理员资料
 */
export const getAdminProfile = () => {
  return request({
    url: '/api/admin/profile',
    method: 'get'
  })
}

/**
 * 更新管理员资料
 */
export const updateAdminProfile = (data) => {
  return request({
    url: '/api/admin/profile',
    method: 'put',
    data
  })
}

/**
 * 修改管理员密码
 */
export const changeAdminPassword = (data) => {
  return request({
    url: '/api/admin/password',
    method: 'put',
    data
  })
}