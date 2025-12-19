<template>
  <div class="min-h-screen bg-slate-50 font-sans">
    <!-- 顶部导航栏 -->
    <header class="bg-white border-b border-gray-100 sticky top-0 z-50">
      <div class="container-responsive h-20 flex items-center justify-between">
        <!-- Logo -->
        <div class="flex items-center gap-3 cursor-pointer" @click="$router.push('/')">
          <div class="w-10 h-10 bg-primary-50 rounded-lg flex items-center justify-center text-primary-600">
            <el-icon size="24"><House /></el-icon>
          </div>
          <span class="text-xl font-bold text-gray-800 tracking-tight">贝贝家政</span>
        </div>
        
        <!-- Desktop Navigation -->
        <nav class="hidden md:flex items-center gap-8">
          <router-link to="/home" class="text-gray-600 hover:text-primary-600 font-medium transition-colors">首页</router-link>
          <router-link to="/services" class="text-gray-600 hover:text-primary-600 font-medium transition-colors">服务项目</router-link>
          <router-link to="/service-process" class="text-gray-600 hover:text-primary-600 font-medium transition-colors">服务流程</router-link>
          <router-link v-if="authStore.isAuthenticated" to="/orders" class="text-gray-600 hover:text-primary-600 font-medium transition-colors">我的订单</router-link>
        </nav>
        
        <!-- User Actions -->
        <div class="flex items-center gap-4">
          <template v-if="authStore.isAuthenticated">
            <el-dropdown trigger="click">
              <div class="flex items-center gap-2 cursor-pointer hover:bg-gray-50 px-3 py-2 rounded-lg transition-colors">
                <el-avatar :size="32" :src="userProfile?.avatar" class="bg-primary-100 text-primary-600">
                  <el-icon><User /></el-icon>
                </el-avatar>
                <span class="text-sm font-medium text-gray-700">{{ authStore.user?.name || userProfile?.name || '用户' }}</span>
                <el-icon class="text-gray-400"><ArrowDown /></el-icon>
              </div>
              <template #dropdown>
                <el-dropdown-menu class="min-w-[160px]">
                  <el-dropdown-item @click="$router.push('/profile')">
                    <el-icon><User /></el-icon>个人中心
                  </el-dropdown-item>
                  <el-dropdown-item divided @click="handleLogout">
                    <el-icon><SwitchButton /></el-icon>退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <button @click="$router.push('/login')" class="text-gray-600 hover:text-primary-600 font-medium px-4">登录</button>
            <button @click="$router.push('/register')" class="btn-primary">注册账号</button>
          </template>
        </div>
      </div>
    </header>

    <!-- Hero Section -->
    <section class="relative pt-16 pb-24 lg:pt-32 lg:pb-40 overflow-hidden">
      <div class="container-responsive relative z-10">
        <div class="grid lg:grid-cols-2 gap-12 lg:gap-8 items-center">
          <!-- Text Content -->
          <div class="text-center lg:text-left">
            <div class="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-primary-50 text-primary-700 text-sm font-medium mb-6">
              <span class="relative flex h-2 w-2">
                <span class="animate-ping absolute inline-flex h-full w-full rounded-full bg-primary-400 opacity-75"></span>
                <span class="relative inline-flex rounded-full h-2 w-2 bg-primary-500"></span>
              </span>
            </div>
            <h1 class="text-4xl lg:text-6xl font-bold text-gray-900 leading-tight mb-6">
              让家更洁净，<br/>
              <span class="text-primary-600">生活更轻松</span>
            </h1>
            <p class="text-lg text-gray-600 mb-8 leading-relaxed max-w-2xl mx-auto lg:mx-0">
              贝贝家政为您提供专业的家庭保洁、保姆月嫂、家电清洗服务。所有服务人员均经过严格背景调查与专业培训，只为给您一个温馨舒适的家。
            </p>
            <div class="flex flex-col sm:flex-row items-center justify-center lg:justify-start gap-4">
              <button @click="$router.push('/services')" class="btn-primary w-full sm:w-auto px-8 py-3 text-lg shadow-lg shadow-primary-500/20">
                立即预约服务
              </button>
              <button @click="$router.push('/service-process')" class="btn-secondary w-full sm:w-auto px-8 py-3 text-lg flex items-center justify-center gap-2">
                <el-icon><VideoPlay /></el-icon> 了解服务流程
              </button>
            </div>
            
            <div class="mt-10 flex items-center justify-center lg:justify-start gap-8 text-sm text-gray-500">
              <div class="flex items-center gap-2">
                <el-icon class="text-green-500"><CircleCheckFilled /></el-icon> 实名认证
              </div>
              <div class="flex items-center gap-2">
                <el-icon class="text-green-500"><CircleCheckFilled /></el-icon> 专业培训
              </div>
              <div class="flex items-center gap-2">
                <el-icon class="text-green-500"><CircleCheckFilled /></el-icon> 售后保障
              </div>
            </div>
          </div>
          
          <!-- Image/Illustration Placeholder -->
          <div class="relative lg:block">
            <div class="absolute inset-0 bg-secondary-100 rounded-[2rem] transform rotate-3 scale-95 opacity-50"></div>
            <div class="relative bg-white rounded-[2rem] shadow-xl overflow-hidden aspect-[4/3] flex items-center justify-center border border-gray-100">
              <!-- 使用一个抽象的温馨家居场景占位 -->
              <div class="absolute inset-0 bg-primary-50 flex flex-col items-center justify-center text-primary-200">
                 <el-icon size="120"><House /></el-icon>
                 <p class="mt-4 text-primary-300 font-medium">温馨整洁的家</p>
              </div>
              <!-- 如果有真实图片，这里应该是 <img src="..." class="w-full h-full object-cover" /> -->
              
              <!-- 悬浮卡片效果 -->
              <div class="absolute bottom-8 left-8 right-8 bg-white/95 backdrop-blur rounded-xl p-4 shadow-soft border border-gray-100 flex items-center gap-4">
                <div class="w-12 h-12 bg-green-100 rounded-full flex items-center justify-center text-green-600">
                  <el-icon size="24"><StarFilled /></el-icon>
                </div>
                <div>
                  <p class="font-bold text-gray-900">4.9/5.0 用户好评</p>
                  <p class="text-sm text-gray-500">基于 10,000+ 次服务评价</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Services Section -->
    <section class="py-20 bg-white">
      <div class="container-responsive">
        <div class="text-center max-w-3xl mx-auto mb-16">
          <h2 class="text-3xl font-bold text-gray-900 mb-4">热门家庭服务</h2>
          <p class="text-gray-500 text-lg">一站式解决您的家庭琐事，让您有更多时间享受生活</p>
        </div>

        <div class="grid md:grid-cols-2 lg:grid-cols-4 gap-6">
          <div v-for="service in services" :key="service.id" 
               class="group bg-white rounded-xl p-6 border border-gray-100 hover:border-primary-100 hover:shadow-lg transition-all duration-300 cursor-pointer"
               @click="$router.push('/services')">
            <div class="w-14 h-14 bg-primary-50 text-primary-600 rounded-xl flex items-center justify-center mb-6 group-hover:scale-110 transition-transform duration-300">
              <el-icon size="28">
                <component :is="service.icon" />
              </el-icon>
            </div>
            <h3 class="text-xl font-bold text-gray-900 mb-3">{{ service.name }}</h3>
            <p class="text-gray-500 mb-6 line-clamp-2">{{ service.description }}</p>
            <div class="flex items-center text-primary-600 font-medium group-hover:translate-x-1 transition-transform">
              立即预约 <el-icon class="ml-1"><ArrowRight /></el-icon>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Stats Section -->
    <section class="py-20 bg-primary-900 text-white relative overflow-hidden">
      <!-- 装饰背景 -->
      <div class="absolute top-0 left-0 w-full h-full opacity-10 pointer-events-none">
         <div class="absolute right-0 top-0 w-96 h-96 bg-white rounded-full mix-blend-overlay filter blur-3xl translate-x-1/2 -translate-y-1/2"></div>
         <div class="absolute left-0 bottom-0 w-64 h-64 bg-secondary-500 rounded-full mix-blend-overlay filter blur-3xl -translate-x-1/2 translate-y-1/2"></div>
      </div>

      <div class="container-responsive relative z-10">
        <div class="grid grid-cols-2 md:grid-cols-4 gap-8 text-center">
          <div v-for="stat in stats" :key="stat.id" class="space-y-2">
            <div class="text-4xl lg:text-5xl font-bold text-white mb-2">{{ stat.number }}</div>
            <div class="text-primary-200 font-medium">{{ stat.label }}</div>
          </div>
        </div>
      </div>
    </section>

    <!-- Features Section -->
    <section class="py-20 bg-secondary-50">
      <div class="container-responsive">
        <div class="grid lg:grid-cols-2 gap-16 items-center">
          <div>
            <h2 class="text-3xl font-bold text-gray-900 mb-6">为什么选择贝贝家政？</h2>
            <div class="space-y-8">
              <div v-for="feature in features" :key="feature.id" class="flex gap-4">
                <div class="flex-shrink-0 w-12 h-12 bg-white text-primary-600 rounded-full flex items-center justify-center shadow-sm">
                  <el-icon size="24">
                    <component :is="feature.icon" />
                  </el-icon>
                </div>
                <div>
                  <h4 class="text-lg font-bold text-gray-900 mb-2">{{ feature.title }}</h4>
                  <p class="text-gray-600 leading-relaxed">{{ feature.description }}</p>
                </div>
              </div>
            </div>
          </div>
          <!-- Right side banner/image placeholder -->
          <div class="relative h-full min-h-[400px] bg-white rounded-2xl p-8 shadow-soft border border-gray-100 flex flex-col justify-center items-center text-center">
             <div class="w-20 h-20 bg-yellow-100 text-yellow-600 rounded-full flex items-center justify-center mb-6">
                <el-icon size="40"><Trophy /></el-icon>
             </div>
             <h3 class="text-2xl font-bold text-gray-900 mb-4">金牌服务承诺</h3>
             <p class="text-gray-600 mb-8 max-w-md">我们承诺：服务不满意免费重做，财产损失全额赔付，迟到早退双倍赔偿。</p>
          </div>
        </div>
      </div>
    </section>

    <!-- Footer -->
    <footer class="bg-white border-t border-gray-100 pt-16 pb-8">
      <div class="container-responsive">
        <div class="grid md:grid-cols-4 gap-8 mb-12">
          <div class="col-span-1 md:col-span-1">
             <div class="flex items-center gap-2 mb-4">
               <div class="w-8 h-8 bg-primary-50 rounded-lg flex items-center justify-center text-primary-600">
                  <el-icon size="18"><House /></el-icon>
               </div>
               <span class="text-lg font-bold text-gray-900">贝贝家政</span>
             </div>
             <p class="text-gray-500 text-sm leading-relaxed">致力于为每个家庭提供专业、安全、贴心的家政服务体验。</p>
          </div>
          <div>
            <h4 class="font-bold text-gray-900 mb-4">服务项目</h4>
            <ul class="space-y-2 text-sm text-gray-600">
              <li><a href="#" class="hover:text-primary-600">家庭保洁</a></li>
              <li><a href="#" class="hover:text-primary-600">深度清洁</a></li>
              <li><a href="#" class="hover:text-primary-600">保姆月嫂</a></li>
              <li><a href="#" class="hover:text-primary-600">家电清洗</a></li>
            </ul>
          </div>
          <div>
            <h4 class="font-bold text-gray-900 mb-4">关于我们</h4>
            <ul class="space-y-2 text-sm text-gray-600">
              <li><a href="#" class="hover:text-primary-600">公司简介</a></li>
              <li><a href="#" class="hover:text-primary-600">加入我们</a></li>
              <li><a href="#" class="hover:text-primary-600">联系客服</a></li>
              <li><a href="#" class="hover:text-primary-600">隐私政策</a></li>
            </ul>
          </div>
          <div>
            <h4 class="font-bold text-gray-900 mb-4">联系方式</h4>
            <div class="text-sm text-gray-600 space-y-2">
              <p>电话：400-123-4567</p>
              <p>邮箱：contact@beibei.com</p>
              <p>地址：某某市某某区某某街道123号</p>
            </div>
          </div>
        </div>
        <div class="border-t border-gray-100 pt-8 text-center text-sm text-gray-500">
          © 2025 贝贝家政服务有限公司. All rights reserved.
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  User, Star, Clock, House, Brush, KnifeFork, 
  ArrowDown, SwitchButton, VideoPlay, ArrowRight,
  CircleCheckFilled, StarFilled, Trophy
} from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'
import { userApi } from '@/api/user.js'

const router = useRouter()
const route = useRoute()

// 获取 authStore
const authStore = useAuthStore()
const userProfile = ref(null)

const services = ref([
  {
    id: 1,
    name: '家庭保洁',
    description: '日常保洁、擦窗、收纳整理，让您的家焕然一新',
    icon: House
  },
  {
    id: 2,
    name: '深度清洁',
    description: '厨房去油污、卫生间消毒、全屋除尘除螨',
    icon: Brush
  },
  {
    id: 3,
    name: '做饭服务',
    description: '营养搭配、口味定制、食材代购，享受家的味道',
    icon: KnifeFork
  },
  {
    id: 4,
    name: '育儿嫂',
    description: '科学育儿、早教陪护、生活照料，专业呵护宝宝',
    icon: User
  }
])

const features = ref([
  {
    id: 1,
    title: '严格筛选 身份认证',
    description: '所有服务人员均通过公安系统背景调查，实名认证，健康体检，持证上岗。',
    icon: User
  },
  {
    id: 2,
    title: '专业培训 考核上岗',
    description: '建立完善的岗前培训体系，理论+实操双重考核，确保服务标准化。',
    icon: Star
  },
  {
    id: 3,
    title: '全程保险 安全无忧',
    description: '为每笔订单投保家政责任险，包含人身意外和财产损失，双重保障。',
    icon: Trophy
  },
  {
    id: 4,
    title: '不满意 免费重做',
    description: '建立严格的售后服务机制，服务质量不达标，我们将免费为您重新服务。',
    icon: CircleCheckFilled
  }
])

const stats = ref([
  {
    id: 1,
    number: '50,000+',
    label: '服务家庭'
  },
  {
    id: 2,
    number: '1,200+',
    label: '专业阿姨'
  },
  {
    id: 3,
    number: '99%',
    label: '用户好评率'
  },
  {
    id: 4,
    number: '30min',
    label: '最快上门'
  }
])

const handleLogout = () => {
  authStore.logout()
  ElMessage.success('已退出登录')
  router.push('/')
}

// 获取用户资料信息
const fetchUserProfile = async () => {
  if (!authStore.isAuthenticated) return

  try {
    const response = await userApi.getUserProfile()
    userProfile.value = response.data || response
  } catch (error) {
    console.error('获取用户资料失败:', error)
  }
}

// 监听认证状态变化
watch(() => authStore.isAuthenticated, (newVal) => {
  if (newVal) {
    fetchUserProfile()
  } else {
    userProfile.value = null
  }
}, { immediate: true })

onMounted(() => {
  console.log('Home.vue mounted successfully')
})
</script>

<style scoped>
/* Scoped styles are mostly replaced by Tailwind utility classes */
</style>
