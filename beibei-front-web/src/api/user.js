import request from './request'

// 用户相关API

/**
 * 获取用户资料
 */
export const getUserProfile = () => {
  return request({
    url: '/api/user/profile',
    method: 'get'
  })
}

/**
 * 更新用户资料
 */
export const updateProfile = (data) => {
  return request({
    url: '/api/user/profile',
    method: 'put',
    data
  })
}

/**
 * 修改密码
 */
export const changePassword = (data) => {
  return request({
    url: '/api/user/password',
    method: 'put',
    data
  })
}

/**
 * 获取用户信息
 */
export const getUserInfo = () => {
  return request({
    url: '/api/user/info',
    method: 'get'
  })
}

/**
 * 上传头像
 */
export const uploadAvatar = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/api/upload/avatar',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 删除头像
 */
export const deleteAvatar = (url) => {
  return request({
    url: '/api/upload/avatar',
    method: 'delete',
    params: { url }
  })
}

// 兼容旧的导出格式
export const userApi = {
  getUserProfile,
  updateProfile,
  changePassword,
  getUserInfo,
  uploadAvatar,
  deleteAvatar
}