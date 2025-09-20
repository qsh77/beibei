<template>
  <div class="worker-orders">
    <div class="page-header">
      <h2>我的订单</h2>
      <p>查看和管理您的订单信息</p>
    </div>

    <!-- 订单状态统计 -->
    <div class="stats-row">
      <div class="stat-item">
        <span class="stat-number">{{ orderStats.assigned || 0 }}</span>
        <span class="stat-label">待接单</span>
      </div>
      <div class="stat-item">
        <span class="stat-number">{{ orderStats.doing || 0 }}</span>
        <span class="stat-label">服务中</span>
      </div>
      <div class="stat-item">
        <span class="stat-number">{{ orderStats.done || 0 }}</span>
        <span class="stat-label">已完成</span>
      </div>
    </div>

    <!-- 订单列表 -->
    <div class="orders-section">
      <el-card v-loading="loading">
        <template #header>
          <div class="card-header">
            <span>订单列表</span>
            <el-button type="primary" @click="loadOrders">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>
        </template>

        <div v-if="orderList.length === 0" class="empty-state">
          <el-empty description="暂无订单" />
        </div>

        <div v-else class="order-list">
          <div
            v-for="order in orderList"
            :key="order.id"
            class="order-item"
          >
            <div class="order-header">
              <span class="order-no">{{ order.orderNo }}</span>
              <el-tag :type="getOrderStatusType(order.status)">
                {{ getOrderStatusText(order.status) }}
              </el-tag>
            </div>

            <div class="order-content">
              <div class="order-info">
                <div class="info-item">
                  <span class="label">客户：</span>
                  <span class="value">{{ order.customerName || '暂无' }}</span>
                </div>
                <div class="info-item">
                  <span class="label">服务：</span>
                  <span class="value">{{ order.serviceName || '暂无' }}</span>
                </div>
                <div class="info-item">
                  <span class="label">时间：</span>
                  <span class="value">{{ formatDate(order.scheduleDate) }} {{ order.timeSlot }}</span>
                </div>
                <div class="info-item">
                  <span class="label">金额：</span>
                  <span class="value amount">¥{{ formatAmount(order.amount) }}</span>
                </div>
              </div>
            </div>

            <div class="order-actions">
              <el-button size="small" @click="handleViewDetail(order)">
                查看详情
              </el-button>

              <el-button
                v-if="order.status === 'ASSIGNED'"
                type="primary"
                size="small"
                @click="handleAcceptOrder(order)"
              >
                接受订单
              </el-button>

              <el-button
                v-if="order.status === 'DOING'"
                type="success"
                size="small"
                @click="handleCompleteOrder(order)"
              >
                完成服务
              </el-button>
            </div>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import { workerOrdersApi } from '@/api/orders.js'

// 响应式数据
const loading = ref(false)
const orderList = ref([])

// 计算订单统计
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
})

// 加载订单列表
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

// 查看订单详情
const handleViewDetail = async (order) => {
  try {
    const response = await workerOrdersApi.getOrderDetail(order.id)
    if (response.code === 200) {
      ElMessage.success('获取订单详情成功')
      console.log('订单详情：', response.data)
    } else {
      ElMessage.error(response.message || '获取订单详情失败')
    }
  } catch (error) {
    console.error('获取订单详情失败', error)
    ElMessage.error('获取订单详情失败')
  }
}

// 接受订单
const handleAcceptOrder = async (order) => {
  try {
    await ElMessageBox.confirm(
      `确认接受订单 ${order.orderNo} 吗？`,
      '确认接单',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'info'
      }
    )

    const response = await workerOrdersApi.acceptOrder(order.id)
    if (response.code === 200) {
      ElMessage.success('订单接受成功，请及时提供服务')
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

// 完成订单
const handleCompleteOrder = async (order) => {
  try {
    await ElMessageBox.confirm(
      `确认完成订单 ${order.orderNo} 吗？`,
      '确认完成',
      {
        confirmButtonText: '确认完成',
        cancelButtonText: '取消',
        type: 'success'
      }
    )

    const response = await workerOrdersApi.completeOrder(order.id)
    if (response.code === 200) {
      ElMessage.success('订单已完成')
      loadOrders() // 重新加载订单列表
    } else {
      ElMessage.error(response.message || '完成订单失败')
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

const getOrderStatusType = (status) => {
  const statusMap = {
    'ASSIGNED': 'warning',
    'DOING': 'primary',
    'DONE': 'success',
    'CANCELED': 'danger'
  }
  return statusMap[status] || ''
}

const getOrderStatusText = (status) => {
  const statusMap = {
    'ASSIGNED': '待接单',
    'DOING': '服务中',
    'DONE': '已完成',
    'CANCELED': '已取消'
  }
  return statusMap[status] || '未知'
}
</script>

<style scoped>
.worker-orders {
  padding: 24px;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h2 {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 600;
  color: #262626;
}

.page-header p {
  margin: 0;
  color: #8c8c8c;
}

.stats-row {
  display: flex;
  gap: 24px;
  margin-bottom: 24px;
}

.stat-item {
  background: white;
  padding: 16px 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  text-align: center;
  min-width: 120px;
  flex: 1;
}

.stat-number {
  display: block;
  font-size: 24px;
  font-weight: 600;
  color: #1890ff;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #8c8c8c;
}

.orders-section {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.empty-state {
  padding: 40px;
  text-align: center;
}

.order-list {
  space-y: 16px;
}

.order-item {
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 16px;
  transition: all 0.3s ease;
}

.order-item:hover {
  border-color: #d9d9d9;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.order-no {
  font-weight: 600;
  color: #262626;
  font-size: 16px;
}

.order-content {
  margin-bottom: 16px;
}

.order-info {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 8px;
}

.info-item {
  display: flex;
  align-items: center;
}

.label {
  color: #8c8c8c;
  font-size: 14px;
  margin-right: 8px;
  min-width: 40px;
}

.value {
  color: #262626;
  font-size: 14px;
}

.amount {
  font-weight: 600;
  color: #f5222d;
}

.order-actions {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}

@media (max-width: 768px) {
  .stats-row {
    flex-direction: column;
    gap: 16px;
  }

  .order-info {
    grid-template-columns: 1fr;
  }

  .order-actions {
    justify-content: flex-start;
    flex-wrap: wrap;
  }
}
</style>