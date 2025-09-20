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

        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getOrderStatusType(row.status)">
              {{ getOrderStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="scheduleDate" label="服务时间" width="180">
          <template #default="{ row }">
            <div>{{ formatDate(row.scheduleDate) }}</div>
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
            <el-button
              type="primary"
              link
              @click="handleViewDetail(row)"
            >
              详情
            </el-button>

            <el-button
              v-if="row.status === 'CREATED'"
              type="warning"
              link
              @click="handleAssignOrder(row)"
            >
              分配
            </el-button>

            <el-dropdown @command="(command) => handleMoreAction(command, row)">
              <el-button type="primary" link>
                更多 <el-icon><ArrowDown /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="updateStatus">更新状态</el-dropdown-item>
                  <el-dropdown-item command="viewTimeline">查看时间线</el-dropdown-item>
                  <el-dropdown-item command="contact" divided>联系客户</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
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

    <!-- 订单分配对话框 -->
    <OrderAssignDialog
      v-model:visible="assignDialogVisible"
      :order="selectedOrder"
      @success="handleSearch"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, reactive, watch } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Search,
  Refresh,
  ArrowDown
} from '@element-plus/icons-vue'
import OrderAssignDialog from '@/components/admin/OrderAssignDialog.vue'
import { adminOrdersApi } from '@/api/orders.js'

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

const assignDialogVisible = ref(false)
const selectedOrder = ref(null)

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
      orderList.value = response.data.list || []
      total.value = response.data.total || 0
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

// 选择变更
const handleSelectionChange = (selection) => {
  selectedOrders.value = selection
}

// 查看订单详情
const handleViewDetail = async (order) => {
  try {
    const response = await adminOrdersApi.getOrderDetail(order.id)
    if (response.code === 200) {
      // 这里可以弹出详情对话框，暂时先用消息提示
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

// 分配订单
const handleAssignOrder = (order) => {
  selectedOrder.value = order
  assignDialogVisible.value = true
}

// 更多操作
const handleMoreAction = async (command, order) => {
  switch (command) {
    case 'updateStatus':
      await handleUpdateStatus(order)
      break
    case 'viewTimeline':
      ElMessage.info('查看时间线功能开发中...')
      break
    case 'contact':
      ElMessage.info('联系客户功能开发中...')
      break
  }
}

// 更新订单状态
const handleUpdateStatus = async (order) => {
  try {
    // 简单的状态流转逻辑
    let newStatus = ''
    switch (order.status) {
      case 'CREATED':
        newStatus = 'ASSIGNED'
        break
      case 'ASSIGNED':
        newStatus = 'DOING'
        break
      case 'DOING':
        newStatus = 'DONE'
        break
      default:
        ElMessage.warning('当前状态无法更新')
        return
    }

    const response = await adminOrdersApi.updateOrderStatus({
      orderId: order.id,
      status: newStatus,
      note: '管理员手动更新'
    })

    if (response.code === 200) {
      ElMessage.success('状态更新成功')
      loadOrders() // 重新加载订单列表
    } else {
      ElMessage.error(response.message || '状态更新失败')
    }
  } catch (error) {
    console.error('状态更新失败', error)
    ElMessage.error('状态更新失败')
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
</style>