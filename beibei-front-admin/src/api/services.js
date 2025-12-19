import request from './request'

// 服务相关API

/**
 * 获取所有服务
 */
export const getAllServices = () => {
  return request({
    url: '/api/services',
    method: 'get'
  })
}

/**
 * 根据ID获取服务详情
 */
export const getServiceById = (id) => {
  return request({
    url: `/api/services/${id}`,
    method: 'get'
  })
}

/**
 * 获取热门服务
 */
export const getHotServices = (limit = 6) => {
  return request({
    url: '/api/services/hot',
    method: 'get',
    params: { limit }
  })
}

/**
 * 搜索服务
 */
export const searchServices = (keyword) => {
  return request({
    url: '/api/services/search',
    method: 'get',
    params: { keyword }
  })
}


// 兼容旧的导出格式
export const servicesApi = {
  getAllServices,
  getServiceById,
  getHotServices,
  searchServices
}