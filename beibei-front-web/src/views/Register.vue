<template>
  <div class="min-h-screen flex items-center justify-center bg-slate-50 py-12 px-4 sm:px-6 lg:px-8">
    <div class="max-w-md w-full space-y-8 bg-white p-10 rounded-2xl shadow-soft border border-gray-100">
      <div class="text-center">
        <div class="mx-auto h-16 w-16 bg-primary-50 rounded-xl flex items-center justify-center text-primary-600 mb-4">
          <el-icon size="32"><House /></el-icon>
        </div>
        <h2 class="text-3xl font-bold text-gray-900">注册账号</h2>
        <p class="mt-2 text-sm text-gray-600">加入贝贝家政，享受优质家庭服务</p>
      </div>
      
      <el-form
        ref="registerFormRef"
        :model="registerForm"
        :rules="registerRules"
        class="mt-8 space-y-6"
        @submit.prevent="handleRegister"
        size="large"
      >
        <div class="space-y-4">
          <el-form-item prop="phone">
            <el-input
              v-model="registerForm.phone"
              placeholder="请输入手机号"
              prefix-icon="Phone"
              maxlength="11"
              class="w-full"
            />
          </el-form-item>
          
          <el-form-item prop="name">
            <el-input
              v-model="registerForm.name"
              placeholder="请输入您的姓名"
              prefix-icon="User"
              class="w-full"
            />
          </el-form-item>
          
          <el-form-item prop="password">
            <el-input
              v-model="registerForm.password"
              type="password"
              placeholder="设置登录密码（至少6位）"
              prefix-icon="Lock"
              show-password
              class="w-full"
            />
          </el-form-item>
          
          <el-form-item prop="confirmPassword">
            <el-input
              v-model="registerForm.confirmPassword"
              type="password"
              placeholder="确认登录密码"
              prefix-icon="Lock"
              show-password
              class="w-full"
            />
          </el-form-item>
        </div>
        
        <el-button
          type="primary"
          class="w-full !rounded-lg !h-12 !text-base !font-medium"
          :loading="loading"
          @click="handleRegister"
        >
          {{ loading ? '注册中...' : '注册' }}
        </el-button>
      </el-form>
      
      <div class="text-center text-sm text-gray-600">
        已有账号？
        <router-link to="/login" class="font-medium text-primary-600 hover:text-primary-500">
          立即登录
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { House, Phone, User, Lock } from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const registerFormRef = ref()
const loading = ref(false)

const registerForm = reactive({
  phone: '',
  name: '',
  password: '',
  confirmPassword: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== registerForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const registerRules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { min: 2, max: 10, message: '姓名长度在2到10个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  if (!registerFormRef.value) return
  
  try {
    await registerFormRef.value.validate()
    loading.value = true
    
    await authStore.registerUser({
      phone: registerForm.phone,
      name: registerForm.name,
      password: registerForm.password
    })
    
    ElMessage.success('注册成功')
    router.push('/')
  } catch (error) {
    console.error('Register error:', error)
    ElMessage.error(error.message || '注册失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
/* Scoped styles replaced by Tailwind classes */
</style>
