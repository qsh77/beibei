<template>
  <div class="max-w-3xl mx-auto">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-position="top"
      class="space-y-6"
    >
      <div class="grid md:grid-cols-2 gap-6">
        <el-form-item label="姓名" prop="name">
          <el-input
            v-model="formData.name"
            placeholder="请输入姓名"
            class="w-full"
            clearable
          />
        </el-form-item>

        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="formData.gender" class="w-full">
            <el-radio-button value="M">男</el-radio-button>
            <el-radio-button value="F">女</el-radio-button>
            <el-radio-button value="U">保密</el-radio-button>
          </el-radio-group>
        </el-form-item>
      </div>

      <div class="grid md:grid-cols-2 gap-6">
        <el-form-item label="生日" prop="birthday">
          <el-date-picker
            v-model="formData.birthday"
            type="date"
            placeholder="请选择生日"
            value-format="YYYY-MM-DD"
            class="!w-full"
          />
        </el-form-item>

        <el-form-item label="邮箱" prop="email">
          <el-input
            v-model="formData.email"
            placeholder="请输入邮箱"
            clearable
          />
        </el-form-item>
      </div>

      <el-form-item label="地址" prop="address">
        <el-input
          v-model="formData.address"
          type="textarea"
          :rows="3"
          placeholder="请输入详细地址"
          maxlength="255"
          show-word-limit
        />
      </el-form-item>

      <el-form-item label="头像" prop="avatar">
        <div class="flex items-start gap-6">
          <div class="relative group">
            <el-upload
              :show-file-list="false"
              :before-upload="beforeAvatarUpload"
              :on-success="handleAvatarSuccess"
              :on-error="handleAvatarError"
              action="#"
              :http-request="uploadAvatar"
              class="block w-32 h-32 rounded-xl overflow-hidden border-2 border-dashed border-gray-300 hover:border-primary-500 transition-colors cursor-pointer"
            >
              <img v-if="formData.avatar" :src="formData.avatar" class="w-full h-full object-cover" />
              <div v-else class="w-full h-full flex flex-col items-center justify-center bg-gray-50 text-gray-400 group-hover:text-primary-500 group-hover:bg-primary-50 transition-colors">
                <el-icon class="text-2xl mb-2"><Plus /></el-icon>
                <span class="text-xs">上传头像</span>
              </div>
            </el-upload>
          </div>
          <div class="text-sm text-gray-500 py-2">
            <p class="mb-1 font-medium text-gray-700">头像要求：</p>
            <ul class="list-disc list-inside space-y-1">
              <li>支持 JPG、PNG 格式</li>
              <li>文件大小不超过 2MB</li>
              <li>建议尺寸 200x200 像素</li>
            </ul>
          </div>
        </div>
      </el-form-item>

      <div class="flex items-center gap-4 pt-6 border-t border-gray-100">
        <button
          type="button"
          @click="handleSubmit"
          class="btn-primary px-8 py-2.5"
          :disabled="loading"
        >
          {{ loading ? '保存中...' : '保存修改' }}
        </button>
        
        <button
          type="button"
          @click="handleReset"
          class="px-6 py-2.5 rounded-lg border border-gray-200 text-gray-600 hover:bg-gray-50 hover:text-gray-900 transition-colors"
        >
          重置
        </button>
        
        <button
          type="button"
          @click="handleCancel"
          class="px-6 py-2.5 rounded-lg text-gray-500 hover:text-gray-700 hover:bg-gray-100 transition-colors ml-auto"
        >
          取消
        </button>
      </div>
    </el-form>
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
    loading.value = true
    await userApi.updateProfile(formData)

    ElMessage.success('个人信息更新成功')
    await fetchUserProfile()
    emit('save-success')
  } catch (error) {
    if (error.errors) return
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
    const response = await userApi.uploadAvatar(options.file)
    const uploadData = response.data || response;

    if (uploadData && uploadData.url) {
      const baseUrl = 'http://localhost:8080'
      const fullUrl = baseUrl + uploadData.url
      formData.avatar = fullUrl

      ElMessage.success('头像上传成功')
      handleAvatarSuccess(uploadData)
      return Promise.resolve(uploadData)
    } else {
      const errorMsg = '上传失败：响应格式不正确'
      ElMessage.error(errorMsg)
      return Promise.reject(new Error(errorMsg))
    }
  } catch (error) {
    ElMessage.error('上传失败：' + error.message)
    return Promise.reject(error)
  }
}

const handleAvatarSuccess = (response) => {
  console.log('Element Plus 上传成功回调:', response)
}

const handleAvatarError = (error) => {
  console.error('Element Plus 上传失败回调:', error)
  ElMessage.error('头像上传失败')
}

onMounted(() => {
  fetchUserProfile()
})
</script>

<style scoped>
/* 移除 scoped 样式，依赖 Tailwind CSS */
</style>