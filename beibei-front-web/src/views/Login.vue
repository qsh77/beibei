<template>
  <div class="min-h-screen flex items-center justify-center bg-slate-50 py-12 px-4 sm:px-6 lg:px-8">
    <div class="max-w-md w-full space-y-8 bg-white p-10 rounded-2xl shadow-soft border border-gray-100">
      <div class="text-center">
        <div class="mx-auto h-16 w-16 bg-primary-50 rounded-xl flex items-center justify-center text-primary-600 mb-4">
          <el-icon size="32"><House /></el-icon>
        </div>
        <h2 class="text-3xl font-bold text-gray-900">贝贝家政</h2>
        <p class="mt-2 text-sm text-gray-600">欢迎回来，请登录您的账号</p>
      </div>
      
      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        class="mt-8 space-y-6"
        @submit.prevent="handleLogin"
        size="large"
      >
        <div class="space-y-4">
          <el-form-item prop="phone">
            <el-input
              v-model="loginForm.phone"
              placeholder="请输入手机号"
              prefix-icon="Phone"
              maxlength="11"
              class="w-full"
            />
          </el-form-item>
          
          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              prefix-icon="Lock"
              show-password
              class="w-full"
            />
          </el-form-item>
        </div>
        
        <div class="flex items-center justify-between text-sm">
           <div class="flex items-center">
             <input id="remember-me" name="remember-me" type="checkbox" class="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded">
             <label for="remember-me" class="ml-2 block text-gray-900">记住我</label>
           </div>
           <div class="text-sm">
             <a href="#" class="font-medium text-primary-600 hover:text-primary-500">忘记密码?</a>
           </div>
        </div>
        
        <el-button
          type="primary"
          class="w-full !rounded-lg !h-12 !text-base !font-medium"
          :loading="loading"
          @click="handleLogin"
        >
          {{ loading ? '登录中...' : '登录' }}
        </el-button>
      </el-form>
      
      <div class="text-center text-sm text-gray-600">
        还没有账号？
        <router-link to="/register" class="font-medium text-primary-600 hover:text-primary-500">
          立即注册
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { House, Phone, Lock } from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'
import { getHomePageByRole } from '@/router'

const router = useRouter()
const authStore = useAuthStore()

const loginFormRef = ref()
const loading = ref(false)

const loginForm = reactive({
  phone: '',
  password: ''
})

const loginRules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return

  try {
    await loginFormRef.value.validate()
    loading.value = true

    await authStore.loginUser(loginForm)

    // 根据用户角色跳转到不同的页面
    const userRole = authStore.userRole
    const homePage = getHomePageByRole(userRole)

    if (userRole !== 'USER') {
      ElMessage.error('当前账号需要使用后台入口登录，请更换普通用户账号')
      authStore.logout()
      return
    }

    ElMessage.success('欢迎回来！')

    await router.push(homePage)
  } catch (error) {
    console.error('Login error:', error)
    ElMessage.error(error.message || '登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
/* Scoped styles replaced by Tailwind classes */
</style>
