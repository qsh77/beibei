<template>
  <div class="worker-earnings">
    <div class="page-header">
      <h2>收入统计</h2>
      <p>查看您的收入统计和明细</p>
    </div>

    <!-- 收入概览 -->
    <div class="earnings-overview">
      <div class="overview-card">
        <div class="card-icon">💰</div>
        <div class="card-content">
          <h3>本月收入</h3>
          <div class="amount">¥{{ formatAmount(monthlyEarnings.total) }}</div>
          <div class="sub-info">已完成 {{ monthlyEarnings.orderCount }} 单</div>
        </div>
      </div>

      <div class="overview-card">
        <div class="card-icon">📈</div>
        <div class="card-content">
          <h3>累计收入</h3>
          <div class="amount">¥{{ formatAmount(totalEarnings.total) }}</div>
          <div class="sub-info">总共 {{ totalEarnings.orderCount }} 单</div>
        </div>
      </div>

      <div class="overview-card">
        <div class="card-icon">📊</div>
        <div class="card-content">
          <h3>平均单价</h3>
          <div class="amount">¥{{ formatAmount(avgOrderAmount) }}</div>
          <div class="sub-info">近30天平均</div>
        </div>
      </div>

      <div class="overview-card">
        <div class="card-icon">⭐</div>
        <div class="card-content">
          <h3>服务评分</h3>
          <div class="amount">{{ serviceRating }}</div>
          <div class="sub-info">满分5.0分</div>
        </div>
      </div>
    </div>

    <!-- 图表和明细 -->
    <div class="earnings-content">
      <!-- 收入趋势图 -->
      <div class="chart-section">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>收入趋势</span>
              <el-select v-model="chartPeriod" @change="updateChart">
                <el-option label="最近7天" value="week" />
                <el-option label="最近30天" value="month" />
                <el-option label="最近90天" value="quarter" />
              </el-select>
            </div>
          </template>

          <div class="chart-container">
            <div class="chart-placeholder">
              <el-icon><TrendCharts /></el-icon>
              <p>收入趋势图</p>
              <div class="chart-data">
                <div v-for="(item, index) in chartData" :key="index" class="data-item">
                  <div class="data-date">{{ item.date }}</div>
                  <div class="data-bar">
                    <div class="bar" :style="{ width: getBarWidth(item.amount) + '%' }"></div>
                  </div>
                  <div class="data-amount">¥{{ formatAmount(item.amount) }}</div>
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 收入明细 -->
      <div class="details-section">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>收入明细</span>
              <div class="header-actions">
                <el-date-picker
                  v-model="dateRange"
                  type="monthrange"
                  range-separator="至"
                  start-placeholder="开始月份"
                  end-placeholder="结束月份"
                  format="YYYY年MM月"
                  value-format="YYYY-MM"
                  @change="loadEarningsDetails"
                />
                <el-button @click="exportData">导出</el-button>
              </div>
            </div>
          </template>

          <el-table
            v-loading="loading"
            :data="earningsDetails"
            stripe
            style="width: 100%"
          >
            <el-table-column prop="orderNo" label="订单号" width="150" />
            <el-table-column prop="serviceName" label="服务类型" width="120" />
            <el-table-column prop="customerName" label="客户" width="100" />
            <el-table-column prop="completedDate" label="完成时间" width="120">
              <template #default="{ row }">
                {{ formatDate(row.completedDate) }}
              </template>
            </el-table-column>
            <el-table-column prop="amount" label="订单金额" width="100" align="right">
              <template #default="{ row }">
                ¥{{ formatAmount(row.amount) }}
              </template>
            </el-table-column>
            <el-table-column prop="commission" label="平台抽成" width="100" align="right">
              <template #default="{ row }">
                ¥{{ formatAmount(row.commission) }}
              </template>
            </el-table-column>
            <el-table-column prop="earnings" label="实际收入" width="100" align="right">
              <template #default="{ row }">
                <span class="earnings-amount">¥{{ formatAmount(row.earnings) }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="80">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)">{{ row.status }}</el-tag>
              </template>
            </el-table-column>
          </el-table>

          <!-- 分页 -->
          <div class="pagination-container">
            <el-pagination
              v-model:current-page="pagination.currentPage"
              v-model:page-size="pagination.pageSize"
              :page-sizes="[10, 20, 50, 100]"
              layout="total, sizes, prev, pager, next, jumper"
              :total="pagination.total"
              @size-change="loadEarningsDetails"
              @current-change="loadEarningsDetails"
            />
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { TrendCharts } from '@element-plus/icons-vue'

// 响应式数据
const loading = ref(false)
const chartPeriod = ref('month')
const dateRange = ref([])

// 收入统计数据
const monthlyEarnings = ref({
  total: 8500.00,
  orderCount: 15
})

const totalEarnings = ref({
  total: 32800.00,
  orderCount: 68
})

const serviceRating = ref(4.8)

// 图表数据
const chartData = ref([
  { date: '12-15', amount: 450 },
  { date: '12-16', amount: 580 },
  { date: '12-17', amount: 320 },
  { date: '12-18', amount: 720 },
  { date: '12-19', amount: 650 },
  { date: '12-20', amount: 480 },
  { date: '12-21', amount: 590 }
])

// 收入明细数据
const earningsDetails = ref([
  {
    id: 1,
    orderNo: 'BB20241201001',
    serviceName: '家庭保洁',
    customerName: '张女士',
    completedDate: '2024-12-20',
    amount: 300,
    commission: 15,
    earnings: 285,
    status: '已结算'
  },
  {
    id: 2,
    orderNo: 'BB20241201002',
    serviceName: '月嫂服务',
    customerName: '李女士',
    completedDate: '2024-12-19',
    amount: 800,
    commission: 40,
    earnings: 760,
    status: '已结算'
  },
  {
    id: 3,
    orderNo: 'BB20241201003',
    serviceName: '老人陪护',
    customerName: '王先生',
    completedDate: '2024-12-18',
    amount: 450,
    commission: 22.5,
    earnings: 427.5,
    status: '待结算'
  }
])

// 分页数据
const pagination = ref({
  currentPage: 1,
  pageSize: 10,
  total: 3
})

// 计算属性
const avgOrderAmount = computed(() => {
  return totalEarnings.value.orderCount > 0
    ? totalEarnings.value.total / totalEarnings.value.orderCount
    : 0
})

const maxChartAmount = computed(() => {
  return Math.max(...chartData.value.map(item => item.amount))
})

// 页面初始化
onMounted(() => {
  // 设置默认日期范围为最近3个月
  const now = new Date()
  const threeMonthsAgo = new Date(now.getFullYear(), now.getMonth() - 2, 1)
  dateRange.value = [
    formatMonth(threeMonthsAgo),
    formatMonth(now)
  ]

  loadEarningsData()
  loadEarningsDetails()
})

// 数据加载
const loadEarningsData = async () => {
  // 这里应该从后端加载收入统计数据
  // 现在使用模拟数据
}

const loadEarningsDetails = async () => {
  loading.value = true
  try {
    // 这里应该从后端加载收入明细
    // 根据 dateRange 和 pagination 参数查询数据

    // 模拟延迟
    setTimeout(() => {
      loading.value = false
    }, 1000)
  } catch (error) {
    ElMessage.error('加载收入明细失败')
    loading.value = false
  }
}

const updateChart = () => {
  // 根据选择的时间周期更新图表数据
  if (chartPeriod.value === 'week') {
    chartData.value = [
      { date: '12-15', amount: 450 },
      { date: '12-16', amount: 580 },
      { date: '12-17', amount: 320 },
      { date: '12-18', amount: 720 },
      { date: '12-19', amount: 650 },
      { date: '12-20', amount: 480 },
      { date: '12-21', amount: 590 }
    ]
  } else if (chartPeriod.value === 'month') {
    chartData.value = [
      { date: '11-22', amount: 1200 },
      { date: '11-29', amount: 1580 },
      { date: '12-06', amount: 1320 },
      { date: '12-13', amount: 1720 },
      { date: '12-20', amount: 1650 }
    ]
  }
}

// 工具函数
const formatAmount = (amount) => {
  return Number(amount || 0).toLocaleString('zh-CN', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  })
}

const formatDate = (dateStr) => {
  return new Date(dateStr).toLocaleDateString('zh-CN')
}

const formatMonth = (date) => {
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}`
}

const getBarWidth = (amount) => {
  return maxChartAmount.value > 0 ? (amount / maxChartAmount.value) * 100 : 0
}

const getStatusType = (status) => {
  return status === '已结算' ? 'success' : 'warning'
}

const exportData = () => {
  ElMessage.success('数据导出功能开发中...')
}
</script>

<style scoped>
.worker-earnings {
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

/* 收入概览样式 */
.earnings-overview {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
}

.overview-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  gap: 16px;
}

.card-icon {
  font-size: 40px;
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1890ff, #40a9ff);
  border-radius: 12px;
  color: white;
}

.card-content h3 {
  margin: 0 0 8px 0;
  font-size: 14px;
  color: #8c8c8c;
  font-weight: normal;
}

.amount {
  font-size: 28px;
  font-weight: 600;
  color: #262626;
  margin-bottom: 4px;
}

.sub-info {
  font-size: 12px;
  color: #8c8c8c;
}

/* 主内容区域 */
.earnings-content {
  display: flex;
  gap: 24px;
}

.chart-section {
  flex: 1;
}

.details-section {
  flex: 2;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: 12px;
  align-items: center;
}

/* 图表样式 */
.chart-container {
  height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.chart-placeholder {
  text-align: center;
  color: #8c8c8c;
  width: 100%;
}

.chart-placeholder .el-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.chart-data {
  margin-top: 24px;
  text-align: left;
}

.data-item {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
  gap: 12px;
}

.data-date {
  width: 60px;
  font-size: 12px;
  color: #666;
}

.data-bar {
  flex: 1;
  height: 20px;
  background: #f0f0f0;
  border-radius: 10px;
  overflow: hidden;
}

.bar {
  height: 100%;
  background: linear-gradient(90deg, #1890ff, #40a9ff);
  border-radius: 10px;
  transition: width 0.3s ease;
}

.data-amount {
  width: 80px;
  text-align: right;
  font-size: 12px;
  font-weight: 600;
  color: #1890ff;
}

/* 表格样式 */
.earnings-amount {
  font-weight: 600;
  color: #52c41a;
}

.pagination-container {
  margin-top: 20px;
  text-align: right;
}

/* 响应式设计 */
@media (max-width: 1400px) {
  .earnings-content {
    flex-direction: column;
  }

  .chart-section,
  .details-section {
    flex: none;
  }
}

@media (max-width: 768px) {
  .worker-earnings {
    padding: 16px;
  }

  .earnings-overview {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .overview-card {
    padding: 16px;
  }

  .card-icon {
    font-size: 32px;
    width: 50px;
    height: 50px;
  }

  .amount {
    font-size: 24px;
  }

  .header-actions {
    flex-direction: column;
    align-items: stretch;
    gap: 8px;
  }

  .chart-container {
    height: 250px;
  }
}
</style>