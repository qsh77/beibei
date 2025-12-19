<template>
  <el-drawer
    v-model="drawerVisible"
    title="订单详情"
    direction="rtl"
    size="500px"
    :before-close="handleClose"
  >
    <div v-loading="loading" class="order-detail">
      <template v-if="orderDetail">
        <!-- 订单状态 -->
        <div class="status-header" :class="statusClass">
          <div class="status-icon">
            <el-icon :size="24">
              <component :is="statusIcon" />
            </el-icon>
          </div>
          <div class="status-info">
            <div class="status-text">{{ statusText }}</div>
            <div class="status-tip">{{ statusTip }}</div>
          </div>
        </div>

        <!-- 服务信息 -->
        <div class="section">
          <div class="section-title">
            <el-icon><Goods /></el-icon>
            服务信息
          </div>
          <div class="info-card">
            <div class="service-name">{{ orderDetail.serviceName }}</div>
            <div v-if="orderDetail.categoryName" class="service-category">
              <el-tag size="small" type="info">{{ orderDetail.categoryName }}</el-tag>
            </div>
            <div v-if="orderDetail.serviceDescription" class="service-desc">
              {{ orderDetail.serviceDescription }}
            </div>
            <div class="service-amount">
              <span class="label">订单金额</span>
              <span class="amount">¥{{ formatAmount(orderDetail.amount) }}</span>
            </div>
          </div>
        </div>

        <!-- 阿姨信息 -->
        <div class="section">
          <div class="section-title">
            <el-icon><User /></el-icon>
            阿姨信息
          </div>
          <div v-if="orderDetail.workerId" class="info-card worker-card">
            <div class="worker-avatar">
              <el-avatar :size="56" :src="orderDetail.workerAvatar">
                <el-icon :size="28"><UserFilled /></el-icon>
              </el-avatar>
            </div>
            <div class="worker-info">
              <div class="worker-name">{{ orderDetail.workerName || '阿姨' }}</div>
              <div class="worker-level">
                <el-rate
                  v-model="orderDetail.workerLevel"
                  disabled
                  :max="5"
                  size="small"
                />
                <span class="level-text">{{ getLevelText(orderDetail.workerLevel) }}</span>
              </div>
              <div class="worker-stats">
                <span v-if="orderDetail.workerYears" class="stat-item">
                  <el-icon><Calendar /></el-icon>
                  {{ orderDetail.workerYears }}年经验
                </span>
                <span v-if="orderDetail.workerScore" class="stat-item">
                  <el-icon><TrophyBase /></el-icon>
                  {{ orderDetail.workerScore }}分
                </span>
              </div>
            </div>
            <div v-if="orderDetail.workerPhone" class="worker-contact">
              <el-button type="primary" link @click="handleCallWorker">
                <el-icon><Phone /></el-icon>
                联系阿姨
              </el-button>
            </div>
          </div>
          <div v-else class="info-card empty-worker">
            <el-icon :size="32" class="empty-icon"><UserFilled /></el-icon>
            <span>暂未分配阿姨</span>
          </div>
        </div>

        <!-- 预约时间 -->
        <div class="section">
          <div class="section-title">
            <el-icon><Clock /></el-icon>
            预约时间
          </div>
          <div class="info-card time-card">
            <div class="time-date">{{ formatDate(orderDetail.scheduleDate) }}</div>
            <el-tag>{{ orderDetail.timeSlot }}</el-tag>
          </div>
        </div>

        <!-- 服务地址 -->
        <div class="section">
          <div class="section-title">
            <el-icon><Location /></el-icon>
            服务地址
          </div>
          <div class="info-card address-card">
            <div class="address-text">{{ orderDetail.addressText }}</div>
            <div v-if="orderDetail.contactName || orderDetail.contactPhone" class="contact-info">
              <span v-if="orderDetail.contactName">{{ orderDetail.contactName }}</span>
              <span v-if="orderDetail.contactPhone">{{ orderDetail.contactPhone }}</span>
            </div>
          </div>
        </div>

        <!-- 备注 -->
        <div v-if="orderDetail.remark" class="section">
          <div class="section-title">
            <el-icon><EditPen /></el-icon>
            备注
          </div>
          <div class="info-card remark-card">
            {{ orderDetail.remark }}
          </div>
        </div>

        <!-- 订单信息 -->
        <div class="section">
          <div class="section-title">
            <el-icon><Document /></el-icon>
            订单信息
          </div>
          <div class="info-card order-info-card">
            <div class="info-row">
              <span class="label">订单编号</span>
              <span class="value font-mono">{{ orderDetail.orderNo }}</span>
            </div>
            <div class="info-row">
              <span class="label">创建时间</span>
              <span class="value">{{ formatDateTime(orderDetail.createdAt) }}</span>
            </div>
            <div v-if="orderDetail.updatedAt" class="info-row">
              <span class="label">更新时间</span>
              <span class="value">{{ formatDateTime(orderDetail.updatedAt) }}</span>
            </div>
          </div>
        </div>
      </template>
    </div>

    <!-- 底部操作栏 -->
    <template #footer>
      <div class="drawer-footer">
        <el-button @click="handleClose">关闭</el-button>
        <el-button
          v-if="canCancel"
          type="danger"
          plain
          @click="handleCancel"
        >
          取消订单
        </el-button>
        <el-button
          v-if="canReview"
          type="primary"
          @click="handleReview"
        >
          评价订单
        </el-button>
        <el-button
          v-if="hasReview"
          type="primary"
          plain
          @click="handleViewReview"
        >
          查看评价
        </el-button>
      </div>
    </template>
  </el-drawer>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  User, UserFilled, Goods, Clock, Location, Phone, Calendar,
  TrophyBase, EditPen, Document, CircleCheck, Loading, Close, Warning
} from '@element-plus/icons-vue'
import { getOrderDetail, cancelOrder } from '@/api/orders.js'
import { getReviewByOrderId } from '@/api/reviews.js'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  orderId: {
    type: [Number, String],
    default: null
  }
})

const emit = defineEmits(['update:modelValue', 'review', 'viewReview', 'refresh'])

const drawerVisible = ref(props.modelValue)
const loading = ref(false)
const orderDetail = ref(null)
const hasReview = ref(false)

// 状态相关计算属性
const statusClass = computed(() => {
  const status = orderDetail.value?.status
  return {
    'status-created': status === 'CREATED',
    'status-assigned': status === 'ASSIGNED',
    'status-doing': status === 'DOING',
    'status-done': status === 'DONE',
    'status-canceled': status === 'CANCELED'
  }
})

const statusIcon = computed(() => {
  const status = orderDetail.value?.status
  switch (status) {
    case 'CREATED': return Loading
    case 'ASSIGNED': return User
    case 'DOING': return Loading
    case 'DONE': return CircleCheck
    case 'CANCELED': return Close
    default: return Warning
  }
})

const statusText = computed(() => {
  const status = orderDetail.value?.status
  const map = {
    CREATED: '等待抢单',
    ASSIGNED: '已接单',
    DOING: '服务中',
    DONE: '已完成',
    CANCELED: '已取消'
  }
  return map[status] || '未知状态'
})

const statusTip = computed(() => {
  const status = orderDetail.value?.status
  switch (status) {
    case 'CREATED': return '订单已发布，等待阿姨抢单中...'
    case 'ASSIGNED': return '阿姨已接单，请保持电话畅通'
    case 'DOING': return '阿姨正在为您提供服务'
    case 'DONE': return '服务已完成，感谢您的使用'
    case 'CANCELED': return '订单已取消'
    default: return ''
  }
})

const canCancel = computed(() => {
  return orderDetail.value?.status === 'CREATED'
})

const canReview = computed(() => {
  return orderDetail.value?.status === 'DONE' && !hasReview.value
})

// 监听 props 变化
watch(() => props.modelValue, (val) => {
  drawerVisible.value = val
  if (val && props.orderId) {
    loadOrderDetail()
  }
})

watch(drawerVisible, (val) => {
  emit('update:modelValue', val)
})

// 加载订单详情
const loadOrderDetail = async () => {
  if (!props.orderId) return
  
  loading.value = true
  try {
    const response = await getOrderDetail(props.orderId)
    orderDetail.value = response.data
    
    // 检查是否已评价
    if (orderDetail.value?.status === 'DONE') {
      try {
        const reviewResponse = await getReviewByOrderId(props.orderId, { silent: true })
        hasReview.value = !!reviewResponse?.data
      } catch {
        hasReview.value = false
      }
    } else {
      hasReview.value = false
    }
  } catch (error) {
    console.error('加载订单详情失败', error)
    ElMessage.error('加载订单详情失败')
  } finally {
    loading.value = false
  }
}

// 关闭抽屉
const handleClose = () => {
  drawerVisible.value = false
  orderDetail.value = null
}

// 取消订单
const handleCancel = async () => {
  try {
    await ElMessageBox.confirm(
      `确认取消订单 ${orderDetail.value.orderNo} 吗？`,
      '取消订单',
      {
        confirmButtonText: '确认取消',
        cancelButtonText: '再想想',
        type: 'warning'
      }
    )
    
    await cancelOrder(props.orderId)
    ElMessage.success('订单已取消')
    emit('refresh')
    handleClose()
  } catch (error) {
    if (error !== 'cancel' && error !== 'close') {
      console.error('取消订单失败', error)
      ElMessage.error('取消订单失败')
    }
  }
}

// 评价订单
const handleReview = () => {
  emit('review', orderDetail.value)
}

// 查看评价
const handleViewReview = () => {
  emit('viewReview', orderDetail.value)
}

// 联系阿姨
const handleCallWorker = () => {
  if (orderDetail.value?.workerPhone) {
    window.location.href = `tel:${orderDetail.value.workerPhone}`
  }
}

// 工具函数
const getLevelText = (level) => {
  if (!level) return '未评级'
  const texts = ['', '一星', '二星', '三星', '四星', '五星']
  return texts[level] || '未知'
}

const formatAmount = (amount) => {
  if (amount === undefined || amount === null) return '0.00'
  return Number(amount).toFixed(2)
}

const formatDate = (value) => {
  if (!value) return '—'
  const date = typeof value === 'string' && value.length === 10
    ? new Date(`${value}T00:00:00`)
    : new Date(value)
  if (Number.isNaN(date.getTime())) return String(value)
  return date.toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric' })
}

const formatDateTime = (value) => {
  if (!value) return '—'
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return String(value)
  return date.toLocaleString('zh-CN')
}
</script>

<style scoped>
.order-detail {
  padding: 0 4px;
}

/* 状态头部 */
.status-header {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  border-radius: 12px;
  margin-bottom: 24px;
}

.status-header.status-created {
  background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
  color: #92400e;
}

.status-header.status-assigned,
.status-header.status-doing {
  background: linear-gradient(135deg, #dbeafe 0%, #bfdbfe 100%);
  color: #1e40af;
}

.status-header.status-done {
  background: linear-gradient(135deg, #d1fae5 0%, #a7f3d0 100%);
  color: #065f46;
}

.status-header.status-canceled {
  background: linear-gradient(135deg, #f3f4f6 0%, #e5e7eb 100%);
  color: #6b7280;
}

.status-icon {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
}

.status-text {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 4px;
}

.status-tip {
  font-size: 13px;
  opacity: 0.8;
}

/* 分区 */
.section {
  margin-bottom: 20px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  font-weight: 600;
  color: #374151;
  margin-bottom: 12px;
}

.info-card {
  background: #f9fafb;
  border-radius: 10px;
  padding: 16px;
  border: 1px solid #f3f4f6;
}

/* 服务信息 */
.service-name {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 8px;
}

.service-category {
  margin-bottom: 8px;
}

.service-desc {
  font-size: 13px;
  color: #6b7280;
  margin-bottom: 12px;
  line-height: 1.5;
}

.service-amount {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px dashed #e5e7eb;
}

.service-amount .label {
  color: #6b7280;
  font-size: 14px;
}

.service-amount .amount {
  font-size: 20px;
  font-weight: 700;
  color: #059669;
}

/* 阿姨信息 */
.worker-card {
  display: flex;
  align-items: center;
  gap: 16px;
}

.worker-info {
  flex: 1;
}

.worker-name {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 4px;
}

.worker-level {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 6px;
}

.level-text {
  font-size: 12px;
  color: #f59e0b;
  font-weight: 500;
}

.worker-stats {
  display: flex;
  gap: 12px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #6b7280;
}

.empty-worker {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 24px;
  color: #9ca3af;
  gap: 8px;
}

.empty-icon {
  opacity: 0.5;
}

/* 时间信息 */
.time-card {
  display: flex;
  align-items: center;
  gap: 12px;
}

.time-date {
  font-size: 15px;
  font-weight: 500;
  color: #1f2937;
}

/* 地址信息 */
.address-text {
  font-size: 14px;
  color: #1f2937;
  line-height: 1.5;
  margin-bottom: 8px;
}

.contact-info {
  display: flex;
  gap: 12px;
  font-size: 13px;
  color: #6b7280;
}

/* 备注 */
.remark-card {
  font-size: 14px;
  color: #4b5563;
  line-height: 1.6;
}

/* 订单信息 */
.order-info-card .info-row {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  border-bottom: 1px solid #f3f4f6;
}

.order-info-card .info-row:last-child {
  border-bottom: none;
}

.order-info-card .label {
  color: #6b7280;
  font-size: 13px;
}

.order-info-card .value {
  color: #1f2937;
  font-size: 13px;
}

.font-mono {
  font-family: ui-monospace, monospace;
}

/* 底部 */
.drawer-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
</style>
