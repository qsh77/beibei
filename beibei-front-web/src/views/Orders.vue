<template>
  <div class="min-h-screen bg-slate-50 pb-20">
    <!-- 顶部导航 -->
    <header class="bg-white border-b border-gray-100 sticky top-0 z-40">
      <div class="container-responsive h-16 flex items-center justify-between">
        <div class="flex items-center gap-4">
          <button @click="goToHome" class="p-2 hover:bg-gray-50 rounded-lg text-gray-500 transition-colors">
            <el-icon size="20"><Back /></el-icon>
          </button>
          <h1 class="text-xl font-bold text-gray-900">我的订单</h1>
        </div>
        <div class="flex items-center gap-3">
          <button 
            @click="loadOrders" 
            :disabled="loading"
            class="flex items-center gap-1 text-sm text-gray-600 hover:text-primary-600 font-medium px-3 py-1.5 rounded-lg hover:bg-gray-50 transition-colors"
          >
            <el-icon :class="{'animate-spin': loading}"><Refresh /></el-icon>
            刷新
          </button>
          <button @click="goToServices" class="btn-primary text-sm px-4 py-1.5">
            去预约
          </button>
        </div>
      </div>
    </header>

    <div class="container-responsive max-w-5xl py-8">
      <!-- 统计卡片 -->
      <div class="grid grid-cols-2 md:grid-cols-5 gap-4 mb-8">
        <div class="bg-primary-600 rounded-xl p-4 text-white shadow-lg shadow-primary-500/20">
          <div class="text-primary-100 text-sm mb-1">全部订单</div>
          <div class="text-2xl font-bold">{{ orderStats.TOTAL }}</div>
        </div>
        <div
          v-for="item in statusDefinitions"
          :key="item.key"
          class="bg-white rounded-xl p-4 border border-gray-100 shadow-sm"
        >
          <div class="text-gray-500 text-sm mb-1">{{ item.label }}</div>
          <div class="text-2xl font-bold text-gray-800">{{ orderStats[item.key] || 0 }}</div>
        </div>
      </div>

      <!-- 订单列表 -->
      <div v-loading="loading" class="min-h-[200px]">
        <template v-if="!loading && orderList.length === 0">
          <div class="bg-white rounded-xl border border-gray-100 p-12 text-center">
            <div class="w-20 h-20 bg-gray-50 rounded-full flex items-center justify-center mx-auto mb-4 text-gray-400">
              <el-icon size="40"><list /></el-icon>
            </div>
            <h3 class="text-lg font-medium text-gray-900 mb-2">暂无订单记录</h3>
            <p class="text-gray-500 mb-6">您还没有预约过服务，快去体验一下吧</p>
            <button @click="goToServices" class="btn-primary">
              立即预约服务
            </button>
          </div>
        </template>

        <div v-else class="space-y-6">
          <div
            v-for="order in orderList"
            :key="order.id"
            class="bg-white rounded-xl border border-gray-200 overflow-hidden hover:shadow-md transition-shadow group"
          >
            <!-- Order Header -->
            <div class="px-6 py-4 bg-gray-50 border-b border-gray-100 flex flex-wrap items-center justify-between gap-4">
              <div class="flex items-center gap-4">
                <span class="font-mono text-gray-600">#{{ order.orderNo }}</span>
                <span class="text-gray-400 text-sm">|</span>
                <span class="text-gray-500 text-sm">{{ formatDateTime(order.createdAt) }}</span>
              </div>
              <div class="flex items-center gap-2">
                 <span 
                   class="inline-block w-2 h-2 rounded-full"
                   :class="{
                     'bg-yellow-500': order.status === 'CREATED',
                     'bg-blue-500': order.status === 'ASSIGNED' || order.status === 'DOING',
                     'bg-green-500': order.status === 'DONE',
                     'bg-gray-400': order.status === 'CANCELED'
                   }"
                 ></span>
                 <span class="font-medium" :class="{
                     'text-yellow-600': order.status === 'CREATED',
                     'text-blue-600': order.status === 'ASSIGNED' || order.status === 'DOING',
                     'text-green-600': order.status === 'DONE',
                     'text-gray-500': order.status === 'CANCELED'
                 }">{{ getOrderStatusText(order.status) }}</span>
              </div>
            </div>

            <!-- Order Body -->
            <div class="p-6">
              <div class="grid md:grid-cols-4 gap-6 mb-6">
                <div class="md:col-span-2">
                  <div class="text-xs text-gray-400 uppercase tracking-wider mb-1">服务项目</div>
                  <div class="font-bold text-gray-900 text-lg flex items-center gap-2">
                    {{ order.serviceName || `服务ID: ${order.serviceId}` }}
                  </div>
                </div>
                <div>
                  <div class="text-xs text-gray-400 uppercase tracking-wider mb-1">预约时间</div>
                  <div class="font-medium text-gray-700">
                    {{ formatDate(order.scheduleDate) }} 
                    <span class="text-sm bg-gray-100 px-2 py-0.5 rounded text-gray-600 ml-1">{{ order.timeSlot }}</span>
                  </div>
                </div>
                <div>
                  <div class="text-xs text-gray-400 uppercase tracking-wider mb-1">订单金额</div>
                  <div class="font-bold text-primary-600 text-lg">¥{{ formatAmount(order.amount) }}</div>
                </div>
              </div>

              <div class="bg-slate-50 rounded-lg p-4 text-sm text-gray-600 flex items-start gap-3">
                <el-icon class="mt-0.5 text-gray-400"><Location /></el-icon>
                <span>{{ order.addressText || '地址信息加载中...' }}</span>
              </div>
              
              <div v-if="order.remark" class="mt-4 text-sm text-gray-500 flex items-start gap-3 pl-1">
                 <span class="bg-yellow-50 text-yellow-700 px-1.5 rounded text-xs">备注</span>
                 <span>{{ order.remark }}</span>
              </div>
            </div>

            <!-- Order Footer -->
            <div class="px-6 py-4 border-t border-gray-100 flex flex-wrap items-center justify-between gap-4">
              <div class="text-sm text-gray-500 flex items-center gap-2">
                <el-icon class="text-primary-500"><InfoFilled /></el-icon>
                {{ getStatusTip(order) }}
              </div>
              
              <div class="flex items-center gap-3">
                <button
                  @click="handleViewDetail(order)"
                  class="px-4 py-2 rounded-lg border border-gray-200 text-gray-600 hover:bg-gray-50 hover:border-gray-300 transition-colors text-sm font-medium"
                >
                  查看详情
                </button>
                
                <button
                  v-if="canCancel(order.status)"
                  @click="handleCancelOrder(order)"
                  class="px-4 py-2 rounded-lg border border-gray-200 text-gray-600 hover:bg-red-50 hover:text-red-600 hover:border-red-200 transition-colors text-sm font-medium"
                >
                  取消订单
                </button>
                
                <button
                  v-if="canReview(order)"
                  @click="handleReview(order)"
                  class="btn-primary px-4 py-2 text-sm"
                >
                  评价订单
                </button>
                
                <button
                  v-if="order.hasReview"
                  @click="handleViewReview(order)"
                  class="px-4 py-2 rounded-lg border border-primary-200 text-primary-600 hover:bg-primary-50 transition-colors text-sm font-medium"
                >
                  查看评价
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 订单详情抽屉 -->
    <OrderDetailDrawer
      v-model="detailDrawerVisible"
      :order-id="currentDetailOrderId"
      @review="handleReviewFromDetail"
      @viewReview="handleViewReviewFromDetail"
      @refresh="loadOrders"
    />

    <!-- 评价对话框 -->
    <ReviewDialog
      v-model="reviewDialogVisible"
      :order="currentReviewOrder"
      @success="handleReviewSuccess"
    />

    <!-- 查看评价对话框 -->
    <el-dialog
      v-model="viewReviewDialogVisible"
      title="订单评价"
      width="600px"
      align-center
      class="rounded-xl"
    >
      <div v-if="currentReview" class="p-4">
        <div class="flex items-center justify-between mb-4 pb-4 border-b border-gray-100">
          <el-rate
            v-model="currentReview.rating"
            disabled
            show-score
            text-color="#f59e0b"
          />
          <span class="text-sm text-gray-400">{{ formatDateTime(currentReview.createdAt) }}</span>
        </div>
        <div v-if="currentReview.content" class="text-gray-700 leading-relaxed mb-6">
          {{ currentReview.content }}
        </div>
        <div v-if="reviewImages.length > 0" class="grid grid-cols-3 gap-4">
          <el-image
            v-for="(img, index) in reviewImages"
            :key="index"
            :src="img"
            fit="cover"
            class="rounded-lg aspect-square border border-gray-200"
            :preview-src-list="reviewImages"
            :initial-index="index"
          />
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh, Back, List, Location, InfoFilled } from '@element-plus/icons-vue'
import { getUserOrders, cancelOrder, getOrderDetail } from '@/api/orders.js'
import { getAllServices } from '@/api/services.js'
import { getReviewByOrderId } from '@/api/reviews.js'
import ReviewDialog from '@/components/ReviewDialog.vue'
import OrderDetailDrawer from '@/components/OrderDetailDrawer.vue'

const router = useRouter()

const loading = ref(false)
const orderList = ref([])
const serviceMap = ref({})
const reviewDialogVisible = ref(false)
const currentReviewOrder = ref(null)
const viewReviewDialogVisible = ref(false)
const currentReview = ref(null)
const detailDrawerVisible = ref(false)
const currentDetailOrderId = ref(null)
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
const resolveImageUrl = (url) => {
  if (!url) return url
  if (/^https?:\/\//i.test(url)) return url
  if (url.startsWith('/')) return `${API_BASE_URL}${url}`
  return url
}
const reviewImages = computed(() => {
  if (!currentReview.value?.imgs) return []
  try {
    const images = JSON.parse(currentReview.value.imgs)
    return Array.isArray(images) ? images.map(resolveImageUrl) : []
  } catch {
    return []
  }
})

const statusDefinitions = [
  { key: 'CREATED', label: '等待抢单' },
  { key: 'ASSIGNED', label: '已接单' },
  { key: 'DOING', label: '服务中' },
  { key: 'DONE', label: '已完成' },
  { key: 'CANCELED', label: '已取消' }
]

const orderStats = computed(() => {
  const stats = statusDefinitions.reduce((acc, item) => {
    acc[item.key] = 0
    return acc
  }, {})

  orderList.value.forEach((order) => {
    const key = order?.status
    if (key) {
      stats[key] = (stats[key] || 0) + 1
    }
  })

  return {
    ...stats,
    TOTAL: orderList.value.length
  }
})

const extractPayload = (response) => {
  if (!response) return null
  if (Array.isArray(response)) return response
  if (typeof response === 'object' && response.code === 200) {
    return response.data ?? null
  }
  return response
}

const extractList = (response) => {
  const payload = extractPayload(response)
  return Array.isArray(payload) ? payload : []
}

const ensureServiceMap = async () => {
  if (Object.keys(serviceMap.value).length > 0) return
  try {
    const response = await getAllServices()
    const services = extractList(response)
    const map = {}
    services.forEach((service) => {
      if (service && service.id !== undefined && service.id !== null) {
        map[service.id] = service
      }
    })
    serviceMap.value = map
  } catch (error) {
    console.error('加载服务信息失败', error)
  }
}

const enrichOrder = async (order) => {
  if (!order) return order
  const service = serviceMap.value?.[order.serviceId]

  // 检查是否已评价 - 使用静默方式检查，避免弹出错误提示
  let hasReview = false
  if (order.status === 'DONE') {
    try {
      const reviewData = await getReviewByOrderId(order.id, { silent: true })
      hasReview = !!reviewData?.data
    } catch {
      // 静默处理错误，订单没有评价是正常情况
      hasReview = false
    }
  }

  return {
    ...order,
    serviceName: service?.name || '',
    hasReview
  }
}

const loadOrders = async () => {
  loading.value = true
  try {
    await ensureServiceMap()
    const response = await getUserOrders()
    const list = extractList(response)
    orderList.value = await Promise.all(list.map(enrichOrder))
  } catch (error) {
    console.error('加载订单列表失败', error)
    ElMessage.error(error?.message || '加载订单失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

const canCancel = (status) => ['CREATED'].includes(status)

const canReview = (order) => {
  return order.status === 'DONE' && !order.hasReview
}


const handleReview = async (order) => {
  try {
    // 获取订单详情（含阿姨信息）
    const response = await getOrderDetail(order.id)
    currentReviewOrder.value = response.data
    reviewDialogVisible.value = true
  } catch (error) {
    console.error('获取订单详情失败', error)
    // 如果获取详情失败，使用原有订单数据
    currentReviewOrder.value = order
    reviewDialogVisible.value = true
  }
}

const handleReviewSuccess = () => {
  ElMessage.success('评价提交成功')
  loadOrders()
}

const handleViewReview = async (order) => {
  try {
    const response = await getReviewByOrderId(order.id)
    currentReview.value = response.data
    viewReviewDialogVisible.value = true
  } catch (error) {
    console.error('加载评价失败', error)
    ElMessage.error('加载评价失败，请稍后重试')
  }
}

// 查看订单详情
const handleViewDetail = (order) => {
  currentDetailOrderId.value = order.id
  detailDrawerVisible.value = true
}

// 从详情抽屉进入评价
const handleReviewFromDetail = (orderDetail) => {
  detailDrawerVisible.value = false
  currentReviewOrder.value = orderDetail
  reviewDialogVisible.value = true
}

// 从详情抽屉查看评价
const handleViewReviewFromDetail = async (orderDetail) => {
  detailDrawerVisible.value = false
  await handleViewReview(orderDetail)
}

const handleCancelOrder = async (order) => {
  try {
    await ElMessageBox.confirm(
      `确认取消订单 ${order.orderNo} 吗？`,
      '取消订单',
      {
        confirmButtonText: '确认取消',
        cancelButtonText: '再想想',
        type: 'warning'
      }
    )

    await cancelOrder(order.id)
    ElMessage.success('订单已取消')
    loadOrders()
  } catch (error) {
    if (error === 'cancel' || error === 'close') {
      return
    }
    console.error('取消订单失败', error)
    ElMessage.error(error?.message || '取消订单失败，请稍后重试')
  }
}

const goToServices = () => {
  router.push('/services')
}

const goToHome = () => {
  router.push('/')
}

const formatAmount = (amount) => {
  if (amount === undefined || amount === null) return '0.00'
  const numberValue = Number(amount)
  if (Number.isNaN(numberValue)) return String(amount)
  return numberValue.toFixed(2)
}

const formatDate = (value) => {
  if (!value) return '—'
  const date = typeof value === 'string' && value.length === 10
    ? new Date(`${value}T00:00:00`)
    : new Date(value)
  if (Number.isNaN(date.getTime())) return String(value)
  return date.toLocaleDateString('zh-CN')
}

const formatDateTime = (value) => {
  if (!value) return '—'
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return String(value)
  return date.toLocaleString('zh-CN')
}

const getOrderStatusText = (status) => {
  const map = {
    CREATED: '等待抢单',
    ASSIGNED: '已接单',
    DOING: '服务中',
    DONE: '已完成',
    CANCELED: '已取消'
  }
  return map[status] || '未知状态'
}

const getStatusTip = (order) => {
  switch (order.status) {
    case 'CREATED':
      return '订单已发布，等待阿姨抢单中...'
    case 'ASSIGNED':
      return order.workerId
        ? `阿姨已接单，请保持电话畅通等待服务`
        : '阿姨已接单，请保持电话畅通'
    case 'DOING':
      return '阿姨正在为您提供服务'
    case 'DONE':
      return '服务已完成，感谢您的使用'
    case 'CANCELED':
      return '订单已取消'
    default:
      return '订单状态未知'
  }
}

onMounted(() => {
  loadOrders()
})
</script>

<style scoped>
/* 移除所有自定义 CSS，完全依赖 Tailwind utility classes 和全局 style.css */
</style>
