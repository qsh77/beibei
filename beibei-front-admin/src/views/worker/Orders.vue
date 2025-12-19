<template>
  <div class="worker-orders-page p-6">
    <div class="mb-6">
      <h2 class="text-2xl font-bold text-gray-800">订单管理</h2>
      <p class="text-gray-500 mt-1">查看和管理您的订单任务</p>
    </div>

    <!-- 状态统计卡片 (Admin Style) -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-6 mb-6">
      <div class="bg-white rounded-lg p-5 shadow-sm border border-gray-100 flex items-center justify-between">
        <div>
          <span class="block text-3xl font-bold text-indigo-600 mb-1">{{ orderStats.assigned || 0 }}</span>
          <span class="text-sm text-gray-500">待接单</span>
        </div>
        <div class="w-12 h-12 rounded-full bg-indigo-50 flex items-center justify-center text-indigo-600">
           <el-icon :size="24"><Clock /></el-icon>
        </div>
      </div>
      
      <div class="bg-white rounded-lg p-5 shadow-sm border border-gray-100 flex items-center justify-between">
        <div>
          <span class="block text-3xl font-bold text-blue-600 mb-1">{{ orderStats.doing || 0 }}</span>
          <span class="text-sm text-gray-500">服务中</span>
        </div>
        <div class="w-12 h-12 rounded-full bg-blue-50 flex items-center justify-center text-blue-600">
           <el-icon :size="24"><User /></el-icon>
        </div>
      </div>
      
      <div class="bg-white rounded-lg p-5 shadow-sm border border-gray-100 flex items-center justify-between">
        <div>
          <span class="block text-3xl font-bold text-green-600 mb-1">{{ orderStats.done || 0 }}</span>
          <span class="text-sm text-gray-500">已完成</span>
        </div>
        <div class="w-12 h-12 rounded-full bg-green-50 flex items-center justify-center text-green-600">
           <el-icon :size="24"><Trophy /></el-icon>
        </div>
      </div>
      
      <div 
        class="bg-white rounded-lg p-5 shadow-sm border border-gray-100 flex items-center justify-between cursor-pointer hover:shadow-md transition-shadow"
        @click="activeTab = 'available-orders'"
      >
        <div>
          <span class="block text-3xl font-bold text-orange-500 mb-1">{{ availableOrderList.length || 0 }}</span>
          <span class="text-sm text-gray-500">可抢单</span>
        </div>
        <div class="w-12 h-12 rounded-full bg-orange-50 flex items-center justify-center text-orange-500">
           <el-icon :size="24"><Document /></el-icon>
        </div>
      </div>
    </div>

    <!-- 内容区域 -->
    <div class="bg-white rounded-lg shadow-sm border border-gray-100 overflow-hidden">
      <!-- Tabs (Admin Style) -->
      <div class="border-b border-gray-100 px-6">
        <div class="flex space-x-8">
          <button 
            class="py-4 text-sm font-medium border-b-2 transition-colors relative"
            :class="activeTab === 'my-orders' ? 'border-indigo-600 text-indigo-600' : 'border-transparent text-gray-500 hover:text-gray-700'"
            @click="handleTabClick('my-orders')"
          >
            我的订单
          </button>
          <button 
            class="py-4 text-sm font-medium border-b-2 transition-colors flex items-center gap-2"
            :class="activeTab === 'available-orders' ? 'border-indigo-600 text-indigo-600' : 'border-transparent text-gray-500 hover:text-gray-700'"
            @click="handleTabClick('available-orders')"
          >
            抢单大厅
            <span v-if="availableOrderList.length > 0" class="px-1.5 py-0.5 rounded-full bg-red-100 text-red-600 text-xs font-bold">{{ availableOrderList.length }}</span>
          </button>
        </div>
      </div>

      <!-- 工具栏 -->
      <div class="p-4 flex justify-between items-center bg-gray-50/50 border-b border-gray-100">
        <div class="text-sm text-gray-500">
          共找到 {{ activeTab === 'my-orders' ? orderList.length : availableOrderList.length }} 条记录
        </div>
        <el-button @click="loadCurrentTabData" :loading="loading" plain size="small">
          <el-icon class="mr-1"><Refresh /></el-icon> 刷新
        </el-button>
      </div>

      <!-- 订单列表 (表格形式) -->
      <div v-loading="loading" class="min-h-[300px]">
        <el-table 
          v-if="activeTab === 'my-orders'"
          :data="orderList" 
          style="width: 100%"
          :header-cell-style="{ background: '#f8fafc', color: '#64748b' }"
        >
          <el-table-column prop="orderNo" label="订单号" width="180">
            <template #default="{ row }">
              <span class="font-mono text-gray-600">{{ row.orderNo }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="serviceName" label="服务项目" width="150" />
          <el-table-column label="客户信息" min-width="150">
             <template #default="{ row }">
               <div>{{ row.customerName }}</div>
             </template>
          </el-table-column>
          <el-table-column label="服务时间" width="200">
             <template #default="{ row }">
               <div class="text-gray-900">{{ formatDate(row.scheduleDate) }}</div>
               <div class="text-xs text-gray-500">{{ row.timeSlot }}</div>
             </template>
          </el-table-column>
          <el-table-column label="金额" width="120">
             <template #default="{ row }">
               <span class="font-medium text-orange-600">¥{{ formatAmount(row.amount) }}</span>
             </template>
          </el-table-column>
          <el-table-column label="状态" width="140">
             <template #default="{ row }">
                <order-status-badge :status="row.status" />
             </template>
          </el-table-column>
          <el-table-column label="操作" width="220" fixed="right">
             <template #default="{ row }">
                <el-button link type="primary" size="small" @click="handleViewDetail(row)">
                  <el-icon class="mr-1"><Document /></el-icon>
                  详情
                </el-button>
                <el-button 
                  v-if="row.status === 'ASSIGNED'" 
                  type="primary" 
                  size="small" 
                  class="!bg-indigo-600 !border-indigo-600 hover:!bg-indigo-700 !text-white !px-3"
                  @click="handleAcceptOrder(row)"
                >
                  <el-icon class="mr-1"><Check /></el-icon>
                  接单
                </el-button>
                <el-button 
                  v-if="row.status === 'DOING'" 
                  type="success" 
                  size="small" 
                  class="!px-3"
                  @click="handleCompleteOrder(row)"
                >
                   <el-icon class="mr-1"><Trophy /></el-icon>
                   完成
                </el-button>
             </template>
          </el-table-column>
          <template #empty>
             <el-empty description="暂无订单" />
          </template>
        </el-table>

        <!-- 抢单列表 (表格形式) -->
        <el-table 
          v-else
          :data="availableOrderList" 
          style="width: 100%"
          :header-cell-style="{ background: '#f8fafc', color: '#64748b' }"
        >
          <el-table-column label="服务项目" width="180">
            <template #default="{ row }">
              <div class="flex items-center">
                 <span class="bg-red-500 text-white text-xs px-1.5 rounded mr-2">NEW</span>
                 {{ row.serviceName }}
              </div>
            </template>
          </el-table-column>
          <el-table-column label="服务时间" width="200">
             <template #default="{ row }">
               <div class="text-gray-900">{{ formatDate(row.scheduleDate) }}</div>
               <div class="text-xs text-gray-500">{{ row.timeSlot }}</div>
             </template>
          </el-table-column>
          <el-table-column label="地址" min-width="200">
             <template #default="{ row }">
               <div class="truncate text-gray-600" :title="row.addressText">
                 <el-icon class="mr-1 top-0.5 relative"><Location /></el-icon>
                 {{ row.addressText || '地址隐私保护中' }}
               </div>
             </template>
          </el-table-column>
          <el-table-column label="金额" width="120">
             <template #default="{ row }">
               <span class="font-bold text-red-600 text-lg">¥{{ formatAmount(row.amount) }}</span>
             </template>
          </el-table-column>
          <el-table-column label="操作" width="120" fixed="right">
             <template #default="{ row }">
                <el-button 
                  type="primary" 
                  size="small" 
                  class="!bg-indigo-600 !border-indigo-600 hover:!bg-indigo-700"
                  @click="handleTakeOrder(row)"
                >
                   <el-icon class="mr-1"><Pointer /></el-icon>
                   立即抢单
                </el-button>
             </template>
          </el-table-column>
          <template #empty>
             <el-empty description="暂无可抢订单" />
          </template>
        </el-table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, h } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Refresh, Document, Trophy, User, Clock, Location, Money, 
  ArrowRight, Check, Timer, Pointer
} from '@element-plus/icons-vue'
import { workerOrdersApi } from '@/api/orders.js'
import OrderStatusBadge from '@/components/OrderStatusBadge.vue'

const router = useRouter()

// 响应式数据
const loading = ref(false)
const orderList = ref([])
const availableOrderList = ref([])
const activeTab = ref('my-orders')

// ... (stats functions remain the same) ...

const orderStats = computed(() => {
  const stats = { assigned: 0, doing: 0, done: 0 }
  orderList.value.forEach(order => {
    if (order.status === 'ASSIGNED') stats.assigned++
    else if (order.status === 'DOING') stats.doing++
    else if (order.status === 'DONE') stats.done++
  })
  return stats
})

// 页面初始化
onMounted(() => {
  loadOrders()
  loadAvailableOrders()
})

// 加载我的订单列表
const loadOrders = async () => {
  loading.value = true
  try {
    const response = await workerOrdersApi.getWorkerOrders()
    if (response.code === 200) {
      orderList.value = response.data || []
    } else {
      ElMessage.error(response.message || '加载订单列表失败')
    }
  } catch (error) {
    console.error('加载订单列表失败', error)
    ElMessage.error('加载订单列表失败')
  } finally {
    loading.value = false
  }
}

// 加载可抢单列表
const loadAvailableOrders = async () => {
  loading.value = true
  try {
    const response = await workerOrdersApi.getAvailableOrders()
    if (response.code === 200) {
      availableOrderList.value = response.data || []
    } else {
      ElMessage.error(response.message || '加载可抢单列表失败')
    }
  } catch (error) {
    console.error('加载可抢单列表失败', error)
    ElMessage.error('加载可抢单列表失败')
  } finally {
    loading.value = false
  }
}

// 标签页切换
const handleTabClick = (tabName) => {
  activeTab.value = tabName
  loadCurrentTabData()
}

// 加载当前标签页数据
const loadCurrentTabData = () => {
  if (activeTab.value === 'my-orders') {
    loadOrders()
  } else if (activeTab.value === 'available-orders') {
    loadAvailableOrders()
  }
}

// 查看订单详情
const handleViewDetail = async (order) => {
  // 暂时使用 router 跳转或者弹窗，这里保持打印
  console.log('订单详情：', order)
  ElMessage.info('功能开发中...')
}

// 接受订单
const handleAcceptOrder = async (order) => {
  try {
    await ElMessageBox.confirm(
      `确认接受订单 ${order.orderNo} 吗？\n请确保您能在 ${formatDate(order.scheduleDate)} ${order.timeSlot} 准时到达服务地点。`,
      '确认接单',
      {
        confirmButtonText: '确认接单',
        cancelButtonText: '再想想',
        type: 'warning',
        icon: Timer,
        center: true
      }
    )

    const response = await workerOrdersApi.acceptOrder(order.id)
    if (response.code === 200) {
      ElMessage.success({
        message: '接单成功！请准时提供服务',
        type: 'success',
        duration: 3000
      })
      loadOrders() // 重新加载订单列表
    } else {
      ElMessage.error(response.message || '接受订单失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('接受订单失败', error)
      ElMessage.error('接受订单失败')
    }
  }
}

// 抢单
const handleTakeOrder = async (order) => {
  try {
    await ElMessageBox.confirm(
      `确认抢单 ${order.orderNo} 吗？\n服务时间：${formatDate(order.scheduleDate)} ${order.timeSlot}\n预计收入：¥${formatAmount(order.amount)}`,
      '立即抢单',
      {
        confirmButtonText: '确认抢单',
        cancelButtonText: '取消',
        type: 'warning',
        center: true
      }
    )

    const response = await workerOrdersApi.takeOrder(order.id)
    if (response.code === 200) {
      ElMessage.success('抢单成功！祝您工作顺利')
      loadOrders()
      loadAvailableOrders()
    } else {
      ElMessage.error(response.message || '抢单失败，可能已被抢走')
      loadAvailableOrders() // 刷新列表
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('抢单失败', error)
      ElMessage.error('抢单失败')
    }
  }
}

// 完成订单
const handleCompleteOrder = async (order) => {
  try {
    await ElMessageBox.confirm(
      `确认您已完成订单 ${order.orderNo} 的所有服务内容吗？`,
      '服务完成确认',
      {
        confirmButtonText: '确认完成',
        cancelButtonText: '取消',
        type: 'success',
        icon: Check,
        center: true
      }
    )

    const response = await workerOrdersApi.completeOrder(order.id)
    if (response.code === 200) {
      ElMessage.success('订单已完成！辛苦了！')
      loadOrders() // 重新加载订单列表
    } else {
      ElMessage.error(response.message || '操作失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('完成订单失败', error)
      ElMessage.error('完成订单失败')
    }
  }
}

// 工具函数
const formatAmount = (amount) => {
  return amount ? Number(amount).toLocaleString() : '0'
}

const formatDate = (dateStr) => {
  return new Date(dateStr).toLocaleDateString('zh-CN')
}
</script>

<style scoped>
/* Scoped styles are minimal as we use Tailwind CSS mostly */
</style>