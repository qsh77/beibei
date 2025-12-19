<template>
  <div class="min-h-screen bg-slate-50 font-sans">
    <!-- 顶部导航栏 (复用 Home.vue 的部分结构，保持一致性) -->
    <header class="bg-white border-b border-gray-100 sticky top-0 z-50">
      <div class="container-responsive h-20 flex items-center justify-between">
        <div class="flex items-center gap-3 cursor-pointer" @click="$router.push('/')">
          <div class="w-10 h-10 bg-primary-50 rounded-lg flex items-center justify-center text-primary-600">
            <el-icon size="24"><House /></el-icon>
          </div>
          <span class="text-xl font-bold text-gray-800 tracking-tight">贝贝家政</span>
        </div>
        <nav class="hidden md:flex items-center gap-8">
          <router-link to="/home" class="text-gray-600 hover:text-primary-600 font-medium transition-colors">首页</router-link>
          <router-link to="/services" class="text-gray-600 hover:text-primary-600 font-medium transition-colors">服务项目</router-link>
          <router-link to="/service-process" class="text-gray-600 hover:text-primary-600 font-medium transition-colors">服务流程</router-link>
          <router-link v-if="authStore.isAuthenticated" to="/orders" class="text-gray-600 hover:text-primary-600 font-medium transition-colors">我的订单</router-link>
        </nav>
        <div class="flex items-center gap-4">
          <button @click="$router.push('/services')" class="btn-primary text-sm px-4 py-2">
            立即预约
          </button>
        </div>
      </div>
    </header>

    <!-- Page Header -->
    <section class="bg-primary-600 py-16 text-white text-center">
      <div class="container-responsive">
        <h1 class="text-3xl md:text-4xl font-bold mb-4">服务流程</h1>
        <p class="text-primary-100 text-lg max-w-2xl mx-auto">
          标准化、透明化的服务流程，让您每一次的体验都舒心、放心。
        </p>
      </div>
    </section>

    <!-- Process Steps -->
    <section class="py-16 md:py-24">
      <div class="container-responsive">
        <div class="max-w-4xl mx-auto">
          <!-- Desktop Timeline (hidden on small screens) -->
          <div class="hidden md:block relative">
            <!-- Connecting Line -->
            <div class="absolute left-1/2 top-0 bottom-0 w-0.5 bg-gray-200 -translate-x-1/2"></div>

            <!-- Steps -->
            <div v-for="(step, index) in steps" :key="index" class="relative mb-16 last:mb-0">
              <div class="flex items-center justify-between w-full" :class="{ 'flex-row-reverse': index % 2 !== 0 }">
                <!-- Content Side -->
                <div class="w-5/12 p-6 bg-white rounded-xl shadow-sm border border-gray-100 hover:shadow-md transition-shadow">
                  <div class="flex items-center gap-3 mb-3 text-primary-600">
                    <el-icon size="24"><component :is="step.icon" /></el-icon>
                    <span class="font-bold text-lg">STEP {{ index + 1 }}</span>
                  </div>
                  <h3 class="text-xl font-bold text-gray-900 mb-2">{{ step.title }}</h3>
                  <p class="text-gray-600 leading-relaxed text-sm">{{ step.description }}</p>
                </div>

                <!-- Center Marker -->
                <div class="absolute left-1/2 -translate-x-1/2 flex items-center justify-center w-10 h-10 rounded-full bg-primary-100 border-4 border-white shadow-sm text-primary-600 z-10">
                  <span class="font-bold text-sm">{{ index + 1 }}</span>
                </div>

                <!-- Empty Side for spacing -->
                <div class="w-5/12"></div>
              </div>
            </div>
          </div>

          <!-- Mobile Timeline (shown on small screens) -->
          <div class="md:hidden space-y-8">
            <div v-for="(step, index) in steps" :key="index" class="relative pl-8 border-l-2 border-primary-100">
              <div class="absolute -left-[9px] top-0 w-4 h-4 rounded-full bg-primary-500 border-2 border-white"></div>
              <div class="bg-white rounded-xl p-6 shadow-sm border border-gray-100">
                 <div class="flex items-center gap-3 mb-3 text-primary-600">
                    <el-icon size="20"><component :is="step.icon" /></el-icon>
                    <span class="font-bold text-sm">STEP {{ index + 1 }}</span>
                  </div>
                <h3 class="text-lg font-bold text-gray-900 mb-2">{{ step.title }}</h3>
                <p class="text-gray-600 text-sm leading-relaxed">{{ step.description }}</p>
              </div>
            </div>
          </div>

        </div>
      </div>
    </section>
    
    <!-- CTA Section -->
    <section class="bg-white py-16 border-t border-gray-100">
      <div class="container-responsive text-center">
         <h2 class="text-2xl font-bold text-gray-900 mb-6">准备好体验优质服务了吗？</h2>
         <div class="flex flex-col sm:flex-row justify-center gap-4">
            <button @click="$router.push('/services')" class="btn-primary px-8 py-3 text-lg">
              立即预约
            </button>
            <button @click="$router.push('/home')" class="btn-secondary px-8 py-3 text-lg">
              返回首页
            </button>
         </div>
      </div>
    </section>

    <!-- Footer (Simplified) -->
    <footer class="bg-slate-50 border-t border-gray-200 py-8 text-center text-sm text-gray-500">
      <div class="container-responsive">
        © 2025 贝贝家政服务有限公司. All rights reserved.
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import {
  House, Mouse, Document as DocumentIcon, 
  Location, Timer, CircleCheck, Star
} from '@element-plus/icons-vue'

const router = useRouter()
const authStore = useAuthStore()

const steps = ref([
  {
    title: '在线预约',
    description: '通过网站选择您需要的服务项目（如日常保洁、家电清洗等），填写服务地址和期望上门时间。',
    icon: Mouse
  },
  {
    title: '订单确认',
    description: '系统自动为您匹配最合适的专业服务人员。确认订单信息无误。',
    icon: DocumentIcon
  },
  {
    title: '上门服务',
    description: '服务人员身着工装，携带专业工具，按照约定时间准时到达您的家中。',
    icon: Location
  },
  {
    title: '专业作业',
    description: '严格按照服务标准和作业流程进行清洁或护理工作，期间您可以随时沟通需求。',
    icon: Timer
  },
  {
    title: '验收完成',
    description: '服务完成后，请您对服务结果进行检查验收。',
    icon: CircleCheck
  },
  {
    title: '评价反馈',
    description: '您对本次服务的评价将帮助我们不断提升服务质量，也是对服务人员工作的肯定。',
    icon: Star
  }
])
</script>

<style scoped>
/* 
  Additional custom styles if needed, mostly using Tailwind.
  Responsive design is handled by Tailwind classes (md:hidden, md:block, etc.)
*/
</style>
