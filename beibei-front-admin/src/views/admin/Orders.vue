<template>
  <div class="admin-orders">
    <div class="page-header">
      <div class="header-content">
        <h2>订单管理</h2>
        <p>管理平台所有订单信息</p>
      </div>

      <!-- 统计卡片 -->
      <div class="stats-row">
        <div class="stat-item">
          <span class="stat-number">{{ orderStats.total || 0 }}</span>
          <span class="stat-label">总订单</span>
        </div>
        <div class="stat-item">
          <span class="stat-number">{{ orderStats.pending || 0 }}</span>
          <span class="stat-label">待分配</span>
        </div>
        <div class="stat-item">
          <span class="stat-number">{{ (orderStats.assigned || 0) + (orderStats.doing || 0) }}</span>
          <span class="stat-label">进行中</span>
        </div>
        <div class="stat-item">
          <span class="stat-number">{{ orderStats.done || 0 }}</span>
          <span class="stat-label">已完成</span>
        </div>
      </div>
    </div>

    <!-- 搜索和筛选 -->
    <div class="search-section">
      <div class="search-form">
        <el-form :model="searchForm" inline>
          <el-form-item>
            <el-input
              v-model="searchForm.keyword"
              placeholder="搜索订单号或客户信息"
              prefix-icon="Search"
              style="width: 250px"
              @keyup.enter="handleSearch"
              clearable
            />
          </el-form-item>

          <el-form-item>
            <el-select
              v-model="searchForm.status"
              placeholder="状态筛选"
              style="width: 120px"
              clearable
            >
              <el-option label="待分配" value="CREATED" />
              <el-option label="已分配" value="ASSIGNED" />
              <el-option label="服务中" value="DOING" />
              <el-option label="已完成" value="DONE" />
              <el-option label="已取消" value="CANCELED" />
            </el-select>
          </el-form-item>

          <el-form-item>
            <el-date-picker
              v-model="dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              style="width: 240px"
            />
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="handleSearch">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
            <el-button @click="handleReset">
              <el-icon><Refresh /></el-icon>
              重置
            </el-button>
          </el-form-item>
        </el-form>
      </div>

    </div>

    <!-- 订单列表 -->
    <div class="table-section">
      <el-table
        v-loading="loading"
        :data="orderList"
        @selection-change="handleSelectionChange"
        style="width: 100%"
      >
        <el-table-column type="selection" width="50" />

        <el-table-column prop="orderNo" label="订单号" width="150" />

        <el-table-column label="客户信息" min-width="150">
          <template #default="{ row }">
            <div class="customer-info">
              <div class="customer-name">{{ row.customerName }}</div>
              <div class="customer-phone">{{ row.customerPhone }}</div>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="serviceName" label="服务" width="150" />

        <el-table-column label="阿姨" width="120">
          <template #default="{ row }">
            <span v-if="row.workerName">{{ row.workerName }}</span>
            <el-tag v-else type="warning" size="small">未分配</el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="amount" label="金额" width="100">
          <template #default="{ row }">
            ¥{{ formatAmount(row.amount) }}
          </template>
        </el-table-column>

        <el-table-column prop="status" label="状态" width="140">
          <template #default="{ row }">
            <order-status-badge :status="row.status" />
          </template>
        </el-table-column>

        <el-table-column prop="scheduleDate" label="服务时间" width="180">
          <template #default="{ row }">
            <div class="font-medium">{{ formatDate(row.scheduleDate) }}</div>
            <div class="time-slot">{{ row.timeSlot }}</div>
          </template>
        </el-table-column>

        <el-table-column prop="createdAt" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>

        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button
                v-if="row.status === 'DONE'"
                type="primary"
                link
                size="small"
                @click="handleViewReview(row)"
              >
                <el-icon class="mr-1"><ChatDotRound /></el-icon>
                查看评价
              </el-button>

              <el-button
                v-if="row.status === 'DONE' && row.hasReview"
                type="danger"
                link
                size="small"
                @click="handleDeleteReview(row)"
              >
                <el-icon class="mr-1"><Delete /></el-icon>
                删除评价
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handlePageSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </div>

    <!-- 查看评价对话框 -->
    <el-dialog
      v-model="reviewDialogVisible"
      title="订单评价"
      width="600px"
    >
      <div v-if="currentReview" class="review-detail">
        <div class="review-header">
          <el-rate
            v-model="currentReview.rating"
            disabled
            show-score
            text-color="#ff9900"
          />
          <span class="review-time">{{ formatDateTime(currentReview.createdAt) }}</span>
        </div>
        <div v-if="currentReview.content" class="review-content">
          {{ currentReview.content }}
        </div>
        <div v-if="reviewImages.length > 0" class="review-images">
          <el-image
            v-for="(img, index) in reviewImages"
            :key="index"
            :src="img"
            fit="cover"
            class="review-image"
            :preview-src-list="reviewImages"
            :initial-index="index"
          />
        </div>
      </div>
      <div v-else class="no-review">
        <el-empty description="该订单暂无评价" />
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive, watch, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Search,
  Refresh,
  ChatDotRound,
  Delete
} from '@element-plus/icons-vue'
import { adminOrdersApi } from '@/api/orders.js'
import { getReviewByOrderId, deleteReviewByOrderId } from '@/api/reviews.js'
import OrderStatusBadge from '@/components/OrderStatusBadge.vue'

// 响应式数据
const loading = ref(false)
const orderList = ref([])
const selectedOrders = ref([])
const orderStats = ref({})
const dateRange = ref([])

const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)

const searchForm = reactive({
  keyword: '',
  status: '',
  startDate: '',
  endDate: '',
  page: 1,
  size: 20
})

const reviewDialogVisible = ref(false)
const currentReview = ref(null)

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

// 监听分页变化
watch([currentPage, pageSize], () => {
  searchForm.page = currentPage.value
  searchForm.size = pageSize.value
  loadOrders()
})

// 监听日期范围变化
watch(dateRange, (newRange) => {
  if (newRange && newRange.length === 2) {
    searchForm.startDate = newRange[0]
    searchForm.endDate = newRange[1]
  } else {
    searchForm.startDate = ''
    searchForm.endDate = ''
  }
})

// 页面初始化
onMounted(() => {
  loadOrders()
  loadOrderStats()
})

// 加载订单列表
const loadOrders = async () => {
  loading.value = true
  try {
    const params = {
      ...searchForm,
      page: currentPage.value,
      size: pageSize.value
    }

    const response = await adminOrdersApi.getOrderList(params)
    if (response.code === 200) {
      const pageData = normalizePageResult(response.data)
      // 检查订单是否有评价
      const ordersWithReview = await Promise.all(
        pageData.records.map(async (order) => {
          if (order.status === 'DONE') {
            try {
              const reviewData = await getReviewByOrderId(order.id)
              // 检查是否有有效的评价数据
              const hasReview = reviewData && reviewData.code === 200 && reviewData.data
              return { ...order, hasReview }
            } catch (error) {
              // 静默处理错误，不显示任何提示
              return { ...order, hasReview: false }
            }
          }
          return { ...order, hasReview: false }
        })
      )
      orderList.value = ordersWithReview
      total.value = pageData.total
      // 如果后端返回最新页码与页大小，保持同步
      if (pageData.page !== currentPage.value) {
        currentPage.value = pageData.page
      }
      if (pageData.size !== pageSize.value) {
        pageSize.value = pageData.size
      }
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

// 加载订单统计
const loadOrderStats = async () => {
  try {
    const response = await adminOrdersApi.getOrderStats()
    if (response.code === 200) {
      orderStats.value = response.data || {}
    } else {
      console.error('加载订单统计失败', response.message)
    }
  } catch (error) {
    console.error('加载订单统计失败', error)
  }
}

// 搜索处理
const handleSearch = () => {
  currentPage.value = 1
  loadOrders()
}

// 重置搜索
const handleReset = () => {
  Object.assign(searchForm, {
    keyword: '',
    status: '',
    startDate: '',
    endDate: '',
    page: 1,
    size: 20
  })
  dateRange.value = []
  currentPage.value = 1
  handleSearch()
}

// 分页处理
const handlePageChange = (page) => {
  currentPage.value = page
}

const handlePageSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
}

const normalizePageResult = (raw) => {
  const data = raw || {}
  const records = Array.isArray(data.records)
    ? data.records
    : Array.isArray(data.list)
      ? data.list
      : []

  const totalCount = typeof data.total === 'number'
    ? data.total
    : typeof data.total === 'string'
      ? Number(data.total) || records.length
      : records.length

  const pageNo = typeof data.page === 'number' && data.page > 0
    ? data.page
    : currentPage.value

  const sizeValue = typeof data.size === 'number' && data.size > 0
    ? data.size
    : pageSize.value

  return {
    records,
    total: totalCount,
    page: pageNo,
    size: sizeValue
  }
}

// 选择变更
const handleSelectionChange = (selection) => {
  selectedOrders.value = selection
}

// 查看评价
const handleViewReview = async (order) => {
  try {
    const response = await getReviewByOrderId(order.id)
    if (response.data) {
      currentReview.value = response.data
      reviewDialogVisible.value = true
    } else {
      ElMessage.warning('该订单暂无评价')
    }
  } catch (error) {
    console.error('加载评价失败', error)
    ElMessage.error('加载评价失败')
  }
}

// 删除评价
const handleDeleteReview = async (order) => {
  try {
    await ElMessageBox.confirm(
      '确认删除该订单的评价吗？此操作不可恢复。',
      '删除评价',
      {
        confirmButtonText: '确认删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await deleteReviewByOrderId(order.id)
    ElMessage.success('评价已删除')
    loadOrders()
  } catch (error) {
    if (error === 'cancel' || error === 'close') {
      return
    }
    console.error('删除评价失败', error)
    ElMessage.error(error?.message || '删除评价失败')
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
    'CREATED': '',
    'ASSIGNED': 'warning',
    'DOING': 'primary',
    'DONE': 'success',
    'CANCELED': 'danger'
  }
  return statusMap[status] || ''
}

const getOrderStatusText = (status) => {
  const statusMap = {
    'CREATED': '待分配',
    'ASSIGNED': '已分配',
    'DOING': '服务中',
    'DONE': '已完成',
    'CANCELED': '已取消'
  }
  return statusMap[status] || '未知'
}

const formatDateTime = (value) => {
  if (!value) return '—'
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return String(value)
  return date.toLocaleString('zh-CN')
}
</script>

<style scoped>
.admin-orders {
  padding: 24px;
}

.page-header {
  margin-bottom: 24px;
}

.header-content h2 {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 600;
  color: #262626;
}

.header-content p {
  margin: 0 0 20px 0;
  color: #8c8c8c;
}

.stats-row {
  display: flex;
  gap: 24px;
}

.stat-item {
  background: white;
  padding: 16px 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  text-align: center;
  min-width: 100px;
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

.search-section {
  background: white;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  flex: 1;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

.table-section {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 20px;
}

.customer-info {
  line-height: 1.4;
}

.customer-name {
  font-weight: 500;
  color: #262626;
  margin-bottom: 2px;
}

.customer-phone {
  font-size: 12px;
  color: #8c8c8c;
}

.time-slot {
  font-size: 12px;
  color: #8c8c8c;
  margin-top: 2px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

@media (max-width: 768px) {
  .stats-row {
    flex-wrap: wrap;
    gap: 16px;
  }

  .search-section {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }

  .action-buttons {
    justify-content: flex-start;
  }
}

/* 评价对话框样式 */
.review-detail {
  padding: 8px;
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #e5e7eb;
}

.review-time {
  font-size: 13px;
  color: #9ca3af;
}

.review-content {
  font-size: 15px;
  color: #1f2937;
  line-height: 1.6;
  margin-bottom: 16px;
  white-space: pre-wrap;
}

.review-images {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.review-image {
  width: 120px;
  height: 120px;
  border-radius: 8px;
  cursor: pointer;
  border: 1px solid #e5e7eb;
}

.no-review {
  padding: 20px 0;
}
</style>
