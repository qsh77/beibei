import request from './request.js'

// 用户相关API
export const userApi = {
  // 获取用户资料
  getUserProfile() {
    return request.get('/api/user/profile')
  },

  // 更新用户资料
  updateProfile(data) {
    return request.put('/api/user/profile', data)
  },

  // 修改密码
  changePassword(data) {
    return request.put('/api/user/password', data)
  },

  // 获取用户信息
  getUserInfo() {
    return request.get('/api/user/info')
  },

  // 上传头像
  uploadAvatar(file) {
    const formData = new FormData()
    formData.append('file', file)
    return request.post('/api/upload/avatar', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  // 删除头像
  deleteAvatar(url) {
    return request.delete('/api/upload/avatar', { params: { url } })
  }
}
