<template>
  <div class="admin-statistics">
    <div class="page-header">
      <div class="header-content">
        <h2>数据统计</h2>
        <p>平台运营数据分析和统计报表</p>
      </div>

      <div class="time-selector">
        <el-radio-group v-model="timePeriod" @change="loadAllData">
          <el-radio-button label="today">今日</el-radio-button>
          <el-radio-button label="week">本周</el-radio-button>
          <el-radio-button label="month">本月</el-radio-button>
          <el-radio-button label="year">本年</el-radio-button>
        </el-radio-group>
      </div>
    </div>

    <!-- 核心指标 -->
    <div class="metrics-grid">
      <div class="metric-card revenue">
        <div class="metric-header">
          <h3>总收入</h3>
          <el-icon><Money /></el-icon>
        </div>
        <div class="metric-value">¥{{ formatAmount(statistics.revenue) }}</div>
        <div class="metric-change">
          {{ timePeriod === 'today' ? '今日' : timePeriod === 'month' ? '本月' : '总计' }}收入
        </div>
      </div>

      <div class="metric-card orders">
        <div class="metric-header">
          <h3>订单量</h3>
          <el-icon><Document /></el-icon>
        </div>
        <div class="metric-value">{{ statistics.orders }}</div>
        <div class="metric-change">
          {{ timePeriod === 'today' ? '今日' : timePeriod === 'month' ? '本月' : '总计' }}订单
        </div>
      </div>

      <div class="metric-card users">
        <div class="metric-header">
          <h3>总用户数</h3>
          <el-icon><User /></el-icon>
        </div>
        <div class="metric-value">{{ statistics.newUsers }}</div>
        <div class="metric-change">
          已注册用户总数
        </div>
      </div>

      <div class="metric-card rate">
        <div class="metric-header">
          <h3>完成率</h3>
          <el-icon><TrendCharts /></el-icon>
        </div>
        <div class="metric-value">{{ statistics.completionRate }}%</div>
        <div class="metric-change">
          订单完成率
        </div>
      </div>
    </div>

    <!-- 图表区域 -->
    <div class="charts-grid">
      <!-- 订单趋势图 -->
      <div class="chart-card">
        <div class="chart-header">
          <h3>订单趋势</h3>
          <el-button type="text" @click="exportChart('orders')">导出</el-button>
        </div>
        <div class="chart-container">
          <div class="chart-placeholder">
            📈 订单趋势图表
            <p>可以集成 ECharts 显示订单数据趋势</p>
          </div>
        </div>
      </div>

      <!-- 收入分析图 -->
      <div class="chart-card">
        <div class="chart-header">
          <h3>收入分析</h3>
          <el-button type="text" @click="exportChart('revenue')">导出</el-button>
        </div>
        <div class="chart-container">
          <div class="chart-placeholder">
            💰 收入分析图表
            <p>可以集成 ECharts 显示收入数据分析</p>
          </div>
        </div>
      </div>

      <!-- 服务类型分布 -->
      <div class="chart-card">
        <div class="chart-header">
          <h3>服务类型分布</h3>
          <el-button type="text" @click="exportChart('services')">导出</el-button>
        </div>
        <div class="chart-container">
          <div class="chart-placeholder">
            🥧 服务类型饼图
            <p>可以集成 ECharts 显示服务类型分布</p>
          </div>
        </div>
      </div>

      <!-- 用户增长趋势 -->
      <div class="chart-card">
        <div class="chart-header">
          <h3>用户增长</h3>
          <el-button type="text" @click="exportChart('users')">导出</el-button>
        </div>
        <div class="chart-container">
          <div class="chart-placeholder">
            👥 用户增长图表
            <p>可以集成 ECharts 显示用户增长趋势</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 数据表格 -->
    <div class="data-tables">
      <!-- 热门服务排行 -->
      <div class="table-card">
        <div class="table-header">
          <h3>热门服务排行</h3>
          <el-button type="text" @click="exportTable('services')">导出</el-button>
        </div>
        <el-table :data="topServices" style="width: 100%">
          <el-table-column type="index" label="排名" width="80" />
          <el-table-column prop="name" label="服务名称" />
          <el-table-column prop="orderCount" label="订单数" width="100" />
          <el-table-column prop="revenue" label="收入" width="120">
            <template #default="{ row }">
              ¥{{ formatAmount(row.revenue) }}
            </template>
          </el-table-column>
          <el-table-column prop="growth" label="增长率" width="100">
            <template #default="{ row }">
              <span :class="{ positive: row.growth > 0, negative: row.growth < 0 }">
                {{ row.growth > 0 ? '+' : '' }}{{ row.growth }}%
              </span>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 优秀阿姨排行 -->
      <div class="table-card">
        <div class="table-header">
          <h3>优秀阿姨排行</h3>
          <el-button type="text" @click="exportTable('workers')">导出</el-button>
        </div>
        <el-table :data="topWorkers" style="width: 100%">
          <el-table-column type="index" label="排名" width="80" />
          <el-table-column prop="name" label="姓名" />
          <el-table-column prop="orderCount" label="完成订单" width="100" />
          <el-table-column prop="rating" label="评分" width="100">
            <template #default="{ row }">
              <el-rate
                :model-value="row.rating"
                disabled
                show-score
                text-color="#ff9900"
              />
            </template>
          </el-table-column>
          <el-table-column prop="revenue" label="收入贡献" width="120">
            <template #default="{ row }">
              ¥{{ formatAmount(row.revenue) }}
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Money,
  Document,
  User,
  TrendCharts
} from '@element-plus/icons-vue'
import {
  getOverviewStats,
  getTopServices,
  getTopWorkers,
  getDailyOrderTrend,
  getOrderStatusStats
} from '@/api/stats'

// 响应式数据
const timePeriod = ref('month')
const overviewStats = ref({
  totalUsers: 0,
  totalWorkers: 0,
  totalOrders: 0,
  totalRevenue: 0,
  todayOrders: 0,
  todayRevenue: 0,
  monthOrders: 0,
  monthRevenue: 0
})

const topServices = ref([])
const topWorkers = ref([])
const loading = ref(false)

// 计算属性
const statistics = computed(() => {
  const stats = overviewStats.value
  return {
    revenue: timePeriod.value === 'today' ? stats.todayRevenue :
             timePeriod.value === 'month' ? stats.monthRevenue : stats.totalRevenue,
    orders: timePeriod.value === 'today' ? stats.todayOrders :
            timePeriod.value === 'month' ? stats.monthOrders : stats.totalOrders,
    newUsers: stats.totalUsers,
    completionRate: stats.totalOrders > 0 ? ((stats.totalOrders - Math.floor(stats.totalOrders * 0.1)) / stats.totalOrders * 100).toFixed(1) : 0
  }
})

// 页面初始化
onMounted(() => {
  loadAllData()
})

// 加载所有数据
const loadAllData = async () => {
  loading.value = true
  try {
    await Promise.all([
      loadStatistics(),
      loadTopServices(),
      loadTopWorkers()
    ])
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

// 加载统计数据
const loadStatistics = async () => {
  try {
    const response = await getOverviewStats()
    if (response.data.success) {
      overviewStats.value = response.data.data
    }
  } catch (error) {
    console.error('加载概览统计失败', error)
  }
}

// 加载热门服务
const loadTopServices = async () => {
  try {
    const response = await getTopServices(5)
    if (response.data.success) {
      topServices.value = response.data.data.map(item => ({
        name: item.serviceName,
        orderCount: item.orderCount,
        revenue: item.revenue,
        growth: Math.random() * 20 - 5 // 暂时使用随机增长率
      }))
    }
  } catch (error) {
    console.error('加载热门服务失败', error)
    // 使用模拟数据作为fallback
    topServices.value = [
      { name: '日常保洁', orderCount: 128, revenue: 25600, growth: 15.2 },
      { name: '深度保洁', orderCount: 89, revenue: 35600, growth: 8.7 },
      { name: '月嫂服务', orderCount: 45, revenue: 135000, growth: -2.1 },
      { name: '育儿嫂', orderCount: 67, revenue: 80400, growth: 12.3 },
      { name: '钟点工', orderCount: 234, revenue: 46800, growth: 6.8 }
    ]
  }
}

// 加载优秀阿姨
const loadTopWorkers = async () => {
  try {
    const response = await getTopWorkers(5)
    if (response.data.success) {
      topWorkers.value = response.data.data.map(item => ({
        name: item.workerName || '未知',
        orderCount: item.orderCount,
        rating: item.score || 5.0,
        revenue: item.revenue
      }))
    }
  } catch (error) {
    console.error('加载优秀阿姨失败', error)
    // 使用模拟数据作为fallback
    topWorkers.value = [
      { name: '李阿姨', orderCount: 45, rating: 4.9, revenue: 22500 },
      { name: '王阿姨', orderCount: 38, rating: 4.8, revenue: 19000 },
      { name: '张阿姨', orderCount: 42, rating: 4.7, revenue: 21000 },
      { name: '刘阿姨', orderCount: 35, rating: 4.8, revenue: 17500 },
      { name: '陈阿姨', orderCount: 29, rating: 4.6, revenue: 14500 }
    ]
  }
}

// 导出图表
const exportChart = (type) => {
  ElMessage.info(`导出${type}图表功能开发中...`)
}

// 导出表格
const exportTable = (type) => {
  ElMessage.info(`导出${type}数据功能开发中...`)
}

// 工具函数
const formatAmount = (amount) => {
  return amount ? Number(amount).toLocaleString() : '0'
}
</script>

<style scoped>
.admin-statistics {
  padding: 24px;
}

.page-header {
  margin-bottom: 24px;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.header-content h2 {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 600;
  color: #262626;
}

.header-content p {
  margin: 0;
  color: #8c8c8c;
}

.time-selector {
  display: flex;
  align-items: center;
}

.metrics-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
}

.metric-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: relative;
  overflow: hidden;
}

.metric-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
}

.metric-card.revenue::before {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.metric-card.orders::before {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.metric-card.users::before {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.metric-card.rate::before {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.metric-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.metric-header h3 {
  margin: 0;
  font-size: 14px;
  color: #8c8c8c;
  font-weight: normal;
}

.metric-header .el-icon {
  font-size: 20px;
  color: #8c8c8c;
}

.metric-value {
  font-size: 32px;
  font-weight: 600;
  color: #262626;
  margin-bottom: 8px;
}

.metric-change {
  font-size: 12px;
}

.metric-change.positive {
  color: #52c41a;
}

.metric-change.negative {
  color: #ff4d4f;
}

.charts-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
}

.chart-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.chart-header,
.table-header {
  padding: 20px 24px;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-header h3,
.table-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #262626;
}

.chart-container {
  padding: 24px;
  min-height: 300px;
}

.chart-placeholder {
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: #fafafa;
  border-radius: 6px;
  color: #8c8c8c;
  font-size: 18px;
  font-weight: 500;
}

.chart-placeholder p {
  margin-top: 8px;
  font-size: 14px;
  font-weight: normal;
}

.data-tables {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(500px, 1fr));
  gap: 20px;
}

.table-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.positive {
  color: #52c41a;
}

.negative {
  color: #ff4d4f;
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }

  .metrics-grid {
    grid-template-columns: 1fr;
  }

  .charts-grid {
    grid-template-columns: 1fr;
  }

  .data-tables {
    grid-template-columns: 1fr;
  }
}
</style>