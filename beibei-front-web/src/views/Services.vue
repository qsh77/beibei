<template>
  <div class="min-h-screen bg-slate-50 pb-20">
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
                <el-avatar :size="32" :src="authStore.user?.avatar" class="bg-primary-100 text-primary-600">
                  <el-icon><User /></el-icon>
                </el-avatar>
                <span class="text-sm font-medium text-gray-700">{{ authStore.user?.name || '用户' }}</span>
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

    <!-- 搜索与筛选区域 -->
    <section class="bg-white border-b border-gray-100 py-6 sticky top-16 z-30 shadow-sm">
      <div class="container-responsive">
        <div class="flex flex-col md:flex-row gap-4">
          <!-- 搜索框 -->
          <div class="flex-1 relative">
            <el-icon class="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 z-10"><Search /></el-icon>
            <input 
              v-model="searchKeyword" 
              type="text" 
              placeholder="搜索服务名称或描述..." 
              class="w-full pl-10 pr-4 py-2.5 bg-gray-50 border border-gray-200 rounded-lg text-gray-900 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500 transition-all"
            />
          </div>
          
          <!-- 排序选择 -->
          <div class="w-full md:w-48">
            <select 
              v-model="selectedSort"
              class="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-lg text-gray-900 focus:outline-none focus:ring-2 focus:ring-primary-500/20 focus:border-primary-500 appearance-none cursor-pointer"
            >
              <option v-for="option in sortOptions" :key="option.value" :value="option.value">
                {{ option.label }}
              </option>
            </select>
          </div>
          
          <!-- 重置按钮 -->
          <button 
            v-if="isFilterActive" 
            @click="resetFilters"
            class="px-4 py-2.5 text-gray-600 hover:text-gray-900 hover:bg-gray-100 rounded-lg font-medium transition-colors flex items-center justify-center gap-2"
          >
            <el-icon><Refresh /></el-icon> 重置
          </button>
        </div>
      </div>
    </section>

    <div class="container-responsive py-8">
      <el-alert
        v-if="errorMessage"
        type="error"
        :closable="false"
        class="mb-6"
        :title="errorMessage"
      />

      <!-- 热门服务推荐 (仅当没有搜索时显示) -->
      <div v-if="shouldShowHotServices && hotServices.length > 0" class="mb-12">
        <div class="flex items-center gap-2 mb-6">
          <el-icon class="text-orange-500"><Trophy /></el-icon>
          <h2 class="text-lg font-bold text-gray-900">热门推荐</h2>
        </div>
        
        <div class="grid md:grid-cols-3 gap-6">
          <div
            v-for="service in hotServices"
            :key="service.id"
            class="bg-white rounded-xl border border-orange-100 p-5 hover:shadow-md transition-shadow cursor-pointer relative overflow-hidden group"
            @click="viewServiceDetail(service)"
          >
            <!-- Hot Badge -->
            <div class="absolute top-0 right-0 bg-orange-500 text-white text-xs font-bold px-2 py-1 rounded-bl-lg z-10">
              HOT
            </div>
            
            <div class="flex items-start gap-4">
              <div class="w-16 h-16 bg-orange-50 rounded-lg flex items-center justify-center text-orange-500 flex-shrink-0 group-hover:scale-105 transition-transform">
                <el-icon size="32"><StarFilled /></el-icon>
              </div>
              <div class="flex-1 min-w-0">
                <h3 class="text-lg font-bold text-gray-900 mb-1 truncate">{{ service.name }}</h3>
                <div class="flex items-center gap-1 text-orange-500 text-sm font-medium mb-2">
                   <el-icon><Star /></el-icon> {{ service.ratingValue }} 分
                </div>
                <div class="flex items-baseline gap-1">
                  <span class="text-xl font-bold text-red-600">¥{{ service.priceText }}</span>
                  <span class="text-sm text-gray-400">/{{ service.unit }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 全部服务列表 -->
      <div class="mb-6">
        <h2 class="text-lg font-bold text-gray-900 mb-6">服务列表</h2>
        
        <!-- Loading State -->
        <div v-if="isLoading" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          <div v-for="n in 6" :key="n" class="bg-white rounded-xl border border-gray-100 p-6 animate-pulse">
            <div class="w-12 h-12 bg-gray-200 rounded-lg mb-4"></div>
            <div class="h-6 bg-gray-200 rounded w-3/4 mb-3"></div>
            <div class="h-4 bg-gray-200 rounded w-full mb-2"></div>
            <div class="h-4 bg-gray-200 rounded w-2/3 mb-6"></div>
            <div class="h-10 bg-gray-200 rounded w-full"></div>
          </div>
        </div>

        <!-- Empty State -->
        <div v-else-if="filteredServices.length === 0" class="text-center py-20 bg-white rounded-xl border border-gray-100 border-dashed">
          <div class="w-16 h-16 bg-gray-50 rounded-full flex items-center justify-center mx-auto mb-4 text-gray-400">
            <el-icon size="32"><Search /></el-icon>
          </div>
          <h3 class="text-lg font-medium text-gray-900 mb-2">未找到相关服务</h3>
          <p class="text-gray-500 mb-6">{{ noResultsMessage }}</p>
          <button @click="resetFilters" class="btn-secondary">
            清除筛选条件
          </button>
        </div>

        <!-- Service Grid -->
        <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
          <div
            v-for="service in filteredServices"
            :key="service.id"
            class="bg-white rounded-xl border border-gray-200 p-0 hover:border-primary-200 hover:shadow-md transition-all cursor-pointer flex flex-col h-full group overflow-hidden"
            @click="viewServiceDetail(service)"
          >
            <div class="p-6 flex-1">
              <div class="flex justify-between items-start mb-4">
                <div class="w-12 h-12 bg-primary-50 text-primary-600 rounded-xl flex items-center justify-center group-hover:bg-primary-600 group-hover:text-white transition-colors">
                  <el-icon size="24"><Tools /></el-icon>
                </div>
                <div v-if="service.ratingValue > 0" class="flex items-center gap-1 text-sm font-medium text-gray-500 bg-gray-50 px-2 py-1 rounded-md">
                   <el-icon class="text-yellow-400"><StarFilled /></el-icon> {{ service.ratingValue }}
                </div>
              </div>
              
              <h3 class="text-lg font-bold text-gray-900 mb-2 group-hover:text-primary-600 transition-colors">{{ service.name }}</h3>
              <p class="text-gray-500 text-sm leading-relaxed mb-4 line-clamp-2">{{ service.description }}</p>
            </div>
            
            <div class="p-4 bg-gray-50 border-t border-gray-100 flex items-center justify-between mt-auto">
              <div class="flex items-baseline gap-1">
                <span class="text-lg font-bold text-gray-900">¥{{ service.priceText }}</span>
                <span class="text-xs text-gray-500">/{{ service.unit }}</span>
              </div>
              <button 
                class="bg-white text-primary-600 border border-primary-200 hover:bg-primary-600 hover:text-white hover:border-primary-600 px-3 py-1.5 rounded-lg text-sm font-medium transition-all"
                @click.stop="bookService(service)"
              >
                立即预约
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Service Detail Dialog -->
    <el-dialog
      v-model="serviceDetailVisible"
      title="服务详情"
      width="500px"
      align-center
      class="rounded-xl overflow-hidden"
    >
      <div v-if="selectedService" class="p-2">
        <div class="flex items-center gap-4 mb-6">
          <div class="w-16 h-16 bg-primary-50 rounded-2xl flex items-center justify-center text-primary-600">
            <el-icon size="32"><Tools /></el-icon>
          </div>
          <div>
            <h3 class="text-xl font-bold text-gray-900">{{ selectedService.name }}</h3>
            <div class="flex items-center gap-2 mt-1">
              <span class="text-lg font-bold text-primary-600">¥{{ selectedService.priceText }}</span>
              <span class="text-gray-400 text-sm">/{{ selectedService.unit }}</span>
            </div>
          </div>
        </div>
        
        <div class="bg-gray-50 rounded-xl p-4 mb-6">
          <h4 class="text-sm font-bold text-gray-900 mb-2">服务介绍</h4>
          <p class="text-gray-600 text-sm leading-relaxed">{{ selectedService.description }}</p>
        </div>
        
        <div class="flex gap-3">
          <button @click="serviceDetailVisible = false" class="flex-1 btn-secondary py-2.5">取消</button>
          <button @click="bookService(selectedService)" class="flex-1 btn-primary py-2.5">立即预约</button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search, Star, Tools, Calendar, Refresh, Back, Trophy, StarFilled, House, User, ArrowDown, SwitchButton } from '@element-plus/icons-vue'
import { servicesApi } from '@/api/services.js'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const handleLogout = () => {
  authStore.logout()
  ElMessage.success('已退出登录')
  router.push('/')
}

const searchKeyword = ref('')
const selectedSort = ref('default')

const allServices = ref([])

const isLoading = ref(false)
const errorMessage = ref('')

const serviceDetailVisible = ref(false)
const selectedService = ref(null)

const sortOptions = [
  { value: 'default', label: '默认排序' },
  { value: 'priceAsc', label: '价格：低到高' },
  { value: 'priceDesc', label: '价格：高到低' },
  { value: 'ratingDesc', label: '评分：高到低' }
]

const extractList = (response, fallback = []) => {
  if (!response) return fallback
  if (Array.isArray(response)) return response
  if (typeof response === 'object' && response.code === 200) {
    return Array.isArray(response.data) ? response.data : fallback
  }
  return fallback
}

const toNumber = (value, defaultValue = 0) => {
  const numberValue = Number(value)
  return Number.isFinite(numberValue) ? numberValue : defaultValue
}

const formatPrice = (value) => {
  const price = toNumber(value, 0)
  return price.toFixed(2)
}

const normalizeService = (service) => {
  const ratingNumber = toNumber(service.rating, 0)
  const clampedRating = Math.min(Math.max(ratingNumber, 0), 5)
  return {
    ...service,
    priceValue: toNumber(service.basePrice, 0),
    priceText: formatPrice(service.basePrice),
    ratingValue: Number(clampedRating.toFixed(1)),
    unit: service.unit || '次',
    description: service.description || '暂无服务介绍',
    hot: service.hot === 1 || service.hot === true
  }
}

const loadServices = async () => {
  isLoading.value = true
  errorMessage.value = ''
  try {
    const response = await servicesApi.getAllServices()
    const list = extractList(response, [])
    allServices.value = list.map(normalizeService)
  } catch (error) {
    console.error('加载服务失败:', error)
    errorMessage.value = error?.message || '加载服务失败，请稍后重试'
    allServices.value = []
  } finally {
    isLoading.value = false
  }
}

const hotServices = computed(() => {
  return allServices.value
    .filter((service) => service.hot)
    .sort((a, b) => b.ratingValue - a.ratingValue)
    .slice(0, 3)
})

const filteredServices = computed(() => {
  let list = [...allServices.value]

  const keyword = searchKeyword.value.trim().toLowerCase()
  if (keyword) {
    list = list.filter((service) => {
      const name = service.name ? service.name.toLowerCase() : ''
      const description = service.description ? service.description.toLowerCase() : ''
      return name.includes(keyword) || description.includes(keyword)
    })
  }

  switch (selectedSort.value) {
    case 'priceAsc':
      list.sort((a, b) => a.priceValue - b.priceValue)
      break
    case 'priceDesc':
      list.sort((a, b) => b.priceValue - a.priceValue)
      break
    case 'ratingDesc':
      list.sort((a, b) => b.ratingValue - a.ratingValue)
      break
    default:
      list.sort((a, b) => {
        if (a.hot !== b.hot) {
          return Number(b.hot) - Number(a.hot)
        }
        return b.ratingValue - a.ratingValue
      })
  }

  return list
})

const isFilterActive = computed(() => {
  return !!searchKeyword.value.trim() || selectedSort.value !== 'default'
})

const shouldShowHotServices = computed(() => {
  // 只要有热门服务且没有搜索筛选，就显示热门服务区块
  return !errorMessage.value && hotServices.value.length > 0 && !isFilterActive.value
})

const noResultsMessage = computed(() => {
  if (errorMessage.value) {
    return errorMessage.value
  }

  if (allServices.value.length === 0) {
    return '暂无服务，敬请期待'
  }

  if (searchKeyword.value.trim()) {
    return '没有找到符合条件的服务'
  }

  return '暂无符合条件的服务'
})


const resetFilters = () => {
  searchKeyword.value = ''
  selectedSort.value = 'default'
}

const goToHome = () => {
  router.push('/')
}

const viewServiceDetail = (service) => {
  selectedService.value = { ...service }
  serviceDetailVisible.value = true
}

const bookService = (service) => {
  if (!service) return
  
  // Close dialog if open
  serviceDetailVisible.value = false

  const token = localStorage.getItem('beibei-web-token')
  if (!token) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }

  router.push({
    name: 'BookService',
    params: { serviceId: service.id },
    query: { service: JSON.stringify(service) }
  })
}

onMounted(() => {
  loadServices()
})
</script>

<style scoped>
/* 移除所有自定义 CSS，完全依赖 Tailwind utility classes 和全局 style.css */
</style>
