import request from './request'

// 地址相关API

/**
 * 获取地址列表
 */
export const getAddresses = () => {
  return request({
    url: '/api/addresses',
    method: 'get'
  })
}

/**
 * 根据ID获取地址详情
 */
export const getAddressById = (id) => {
  return request({
    url: `/api/addresses/${id}`,
    method: 'get'
  })
}

/**
 * 获取默认地址
 */
export const getDefaultAddress = () => {
  return request({
    url: '/api/addresses/default',
    method: 'get'
  })
}

/**
 * 添加地址
 */
export const addAddress = (data) => {
  return request({
    url: '/api/addresses',
    method: 'post',
    data
  })
}

/**
 * 更新地址
 */
export const updateAddress = (id, data) => {
  return request({
    url: `/api/addresses/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除地址
 */
export const deleteAddress = (id) => {
  return request({
    url: `/api/addresses/${id}`,
    method: 'delete'
  })
}

/**
 * 设置默认地址
 */
export const setDefaultAddress = (id) => {
  return request({
    url: `/api/addresses/${id}/default`,
    method: 'put'
  })
}

// 兼容旧的导出格式
export const addressesApi = {
  getAddresses,
  getAddressById,
  getDefaultAddress,
  addAddress,
  updateAddress,
  deleteAddress,
  setDefaultAddress
}