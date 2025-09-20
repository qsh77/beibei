import request from './request'

export function getOverviewStats() {
  return request({
    url: '/api/admin/stats/overview',
    method: 'get'
  })
}

export function getDailyOrderTrend(days = 30) {
  return request({
    url: '/api/admin/stats/order/trend/daily',
    method: 'get',
    params: { days }
  })
}

export function getMonthlyOrderTrend(months = 12) {
  return request({
    url: '/api/admin/stats/order/trend/monthly',
    method: 'get',
    params: { months }
  })
}

export function getOrderStatusStats() {
  return request({
    url: '/api/admin/stats/order/status',
    method: 'get'
  })
}

export function getTopServices(limit = 10) {
  return request({
    url: '/api/admin/stats/service/top',
    method: 'get',
    params: { limit }
  })
}

export function getUserRegistrationTrend(days = 30) {
  return request({
    url: '/api/admin/stats/user/registration/trend',
    method: 'get',
    params: { days }
  })
}

export function getUserRoleStats() {
  return request({
    url: '/api/admin/stats/user/role',
    method: 'get'
  })
}

export function getTopWorkers(limit = 10) {
  return request({
    url: '/api/admin/stats/worker/top',
    method: 'get',
    params: { limit }
  })
}