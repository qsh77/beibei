<template>
  <div class="min-h-screen w-full flex overflow-hidden bg-slate-50">
    <!-- 左侧品牌区 (Desktop Only) -->
    <div class="hidden lg:flex lg:w-1/2 relative bg-gradient-primary overflow-hidden items-center justify-center p-12">
      <!-- 装饰性背景元素 -->
      <div class="absolute top-0 left-0 w-full h-full opacity-20">
        <div class="absolute top-10 left-10 w-32 h-32 rounded-full bg-white blur-3xl"></div>
        <div class="absolute bottom-20 right-20 w-64 h-64 rounded-full bg-indigo-300 blur-3xl"></div>
      </div>

      <div class="relative z-10 text-white max-w-lg">
        <div class="mb-8 flex items-center gap-4">
          <div class="w-12 h-12 bg-white/20 backdrop-blur rounded-xl flex items-center justify-center">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6" />
            </svg>
          </div>
          <h1 class="text-3xl font-bold tracking-tight">贝贝家政</h1>
        </div>
        <h2 class="text-4xl font-extrabold mb-6 leading-tight">
          为您提供最专业的<br/>家庭服务管理平台
        </h2>
        <p class="text-indigo-100 text-lg leading-relaxed opacity-90">
          高效管理用户、阿姨与订单。让每一次服务都值得信赖，让每一个家庭都充满温馨。
        </p>
        
        <div class="mt-12 flex gap-4">
          <div class="px-4 py-2 bg-white/10 backdrop-blur rounded-lg border border-white/20 text-sm">
            ✨ 智能调度
          </div>
          <div class="px-4 py-2 bg-white/10 backdrop-blur rounded-lg border border-white/20 text-sm">
            🛡️ 安全保障
          </div>
          <div class="px-4 py-2 bg-white/10 backdrop-blur rounded-lg border border-white/20 text-sm">
            📊 数据洞察
          </div>
        </div>
      </div>
    </div>

    <!-- 右侧登录表单 -->
    <div class="w-full lg:w-1/2 flex items-center justify-center p-8 lg:p-12">
      <div class="w-full max-w-md bg-white p-8 rounded-2xl shadow-card animate-fade-in-up">
        <div class="text-center mb-10">
          <h2 class="text-2xl font-bold text-slate-800">欢迎回来</h2>
          <p class="text-slate-500 mt-2">请登录您的管理账号</p>
        </div>

        <el-form
          ref="loginFormRef"
          :model="loginForm"
          :rules="loginRules"
          class="space-y-6"
          @submit.prevent="handleLogin"
          size="large"
        >
          <el-form-item prop="phone">
            <el-input
              v-model="loginForm.phone"
              placeholder="请输入手机号"
              class="custom-input"
            >
              <template #prefix>
                <el-icon class="text-slate-400"><Phone /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              show-password
              class="custom-input"
            >
              <template #prefix>
                <el-icon class="text-slate-400"><Lock /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <div class="flex items-center justify-between text-sm">
            <el-checkbox v-model="rememberMe">记住我</el-checkbox>
            <a href="#" class="text-primary-600 hover:text-primary-500 font-medium">忘记密码？</a>
          </div>

          <el-button
            type="primary"
            class="w-full !h-12 !text-lg !rounded-xl shadow-lg shadow-primary-500/30 hover:shadow-primary-500/40 transition-all duration-300"
            :loading="loading"
            @click="handleLogin"
          >
            {{ loading ? '登录中...' : '立即登录' }}
          </el-button>
        </el-form>
        
        <div class="mt-8 text-center text-sm text-slate-400">
          &copy; {{ new Date().getFullYear() }} 贝贝家政服务平台. All rights reserved.
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import { getHomePageByRole } from '@/router'
import { Phone, Lock } from '@element-plus/icons-vue'

const router = useRouter()
const authStore = useAuthStore()

const loginFormRef = ref()
const loading = ref(false)
const rememberMe = ref(false)

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
    
    const userRole = authStore.userRole
    const homePage = getHomePageByRole(userRole)

    if (homePage === '/unauthorized') {
      ElMessage.error('当前账号无权访问后台，请使用前台站点登录')
      authStore.logout()
      return
    }

    const roleMessages = {
      ADMIN: '欢迎回来，管理员！',
      WORKER: '工作愉快，记得查看今日订单哦！'
    }

    ElMessage.success(roleMessages[userRole] || '登录成功')
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
/* 自定义 Element Plus 输入框样式以匹配设计 */
:deep(.el-input__wrapper) {
  box-shadow: 0 0 0 1px #e2e8f0 inset;
  background-color: #f8fafc;
  border-radius: 0.75rem; /* 12px */
  padding: 8px 15px;
  transition: all 0.3s ease;
}

:deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #cbd5e1 inset;
  background-color: #fff;
}

:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px var(--el-color-primary) inset !important;
  background-color: #fff;
}

:deep(.el-input__inner) {
  height: 32px;
  font-size: 16px;
}
</style>