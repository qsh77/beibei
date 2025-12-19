<template>
  <div class="profile-form">
    <el-card shadow="never">
      <template #header>
        <span>个人信息</span>
      </template>

      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
        class="profile-form-content"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input
                v-model="formData.name"
                placeholder="请输入姓名"
                clearable
              />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-radio-group v-model="formData.gender">
                <el-radio value="M">男</el-radio>
                <el-radio value="F">女</el-radio>
                <el-radio value="U">保密</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="生日" prop="birthday">
              <el-date-picker
                v-model="formData.birthday"
                type="date"
                placeholder="请选择生日"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input
                v-model="formData.email"
                placeholder="请输入邮箱"
                clearable
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="地址" prop="address">
          <el-input
            v-model="formData.address"
            type="textarea"
            :rows="3"
            placeholder="请输入地址"
            maxlength="255"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="头像" prop="avatar">
          <div class="avatar-upload">
            <el-upload
              :show-file-list="false"
              :before-upload="beforeAvatarUpload"
              :on-success="handleAvatarSuccess"
              :on-error="handleAvatarError"
              action="#"
              :http-request="uploadAvatar"
              class="avatar-uploader"
            >
              <img v-if="formData.avatar" :src="formData.avatar" class="avatar" />
              <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
            </el-upload>
            <div class="upload-tip">
              支持 JPG、PNG 格式，文件大小不超过 2MB
            </div>
          </div>
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            :loading="loading"
            @click="handleSubmit"
          >
            保存
          </el-button>
          <el-button @click="handleReset">
            重置
          </el-button>
          <el-button @click="handleCancel">
            取消
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { userApi } from '@/api/user.js'

// 定义事件
const emit = defineEmits(['save-success', 'cancel'])

const formRef = ref()
const loading = ref(false)

const formData = reactive({
  name: '',
  gender: 'U',
  birthday: '',
  email: '',
  address: '',
  avatar: ''
})

const formRules = {
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  email: [
    {
      pattern: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
      message: '请输入正确的邮箱格式',
      trigger: 'blur'
    }
  ]
}

const fetchUserProfile = async () => {
  try {
    const response = await userApi.getUserProfile()
    if (response.code === 200 && response.data) {
      Object.assign(formData, response.data)
    }
  } catch (error) {
    console.error('获取用户资料失败:', error)
  }
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()

    console.log('提交的表单数据:', formData)

    loading.value = true
    const response = await userApi.updateProfile(formData)

    console.log('更新资料响应:', response)

    // 由于响应拦截器处理，成功的话直接是data
    ElMessage.success('个人信息更新成功')
    await fetchUserProfile()
    emit('save-success')
  } catch (error) {
    if (error.errors) {
      return
    }
    console.error('更新失败详情:', error)
    ElMessage.error('更新失败：' + error.message)
  } finally {
    loading.value = false
  }
}

const handleReset = async () => {
  try {
    await ElMessageBox.confirm('确定要重置表单吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await fetchUserProfile()
    ElMessage.success('表单已重置')
  } catch (error) {
    // 用户取消操作
  }
}

const handleCancel = () => {
  emit('cancel')
}

const beforeAvatarUpload = (file) => {
  const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isJPG) {
    ElMessage.error('头像只能是 JPG/PNG 格式!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('头像大小不能超过 2MB!')
    return false
  }
  return true
}

const uploadAvatar = async (options) => {
  try {
    console.log('开始上传文件:', options.file.name)

    // 使用真实的文件上传API
    const response = await userApi.uploadAvatar(options.file)

    console.log('上传响应:', response)
    console.log('响应数据类型:', typeof response)

    // 处理后端返回的数据结构 {code: 200, message: '上传成功', data: {url, filename, ...}}
    const uploadData = response.data || response;

    if (uploadData && uploadData.url) {
      // 构建完整的头像URL（包含服务器地址）
      const baseUrl = 'http://localhost:8080'
      const fullUrl = baseUrl + uploadData.url
      formData.avatar = fullUrl

      console.log('头像URL设置为:', fullUrl)
      ElMessage.success('头像上传成功')

      // 调用成功回调
      handleAvatarSuccess(uploadData)
      return Promise.resolve(uploadData)
    } else {
      const errorMsg = '上传失败：响应格式不正确'
      console.error('上传失败:', errorMsg, response)
      ElMessage.error(errorMsg)
      return Promise.reject(new Error(errorMsg))
    }
  } catch (error) {
    console.error('上传异常:', error)
    ElMessage.error('上传失败：' + error.message)
    return Promise.reject(error)
  }
}

const handleAvatarSuccess = (response) => {
  // 处理上传成功的回调
  console.log('Element Plus 上传成功回调:', response)
}

const handleAvatarError = (error) => {
  // 处理上传失败的回调
  console.error('Element Plus 上传失败回调:', error)
  ElMessage.error('头像上传失败')
}

onMounted(() => {
  fetchUserProfile()
})
</script>

<style scoped>
.profile-form {
  max-width: 800px;
}

.profile-form-content {
  padding: 20px 0;
}

.avatar-upload {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.avatar-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: border-color 0.3s;
  width: 120px;
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-uploader:hover {
  border-color: #409eff;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
}

.avatar {
  width: 120px;
  height: 120px;
  object-fit: cover;
  display: block;
}

.upload-tip {
  margin-top: 8px;
  font-size: 12px;
  color: #999;
}
</style>