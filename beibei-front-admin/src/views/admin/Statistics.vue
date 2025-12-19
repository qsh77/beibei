<template>
  <div class="admin-statistics">
    <div class="page-header animate-fade-in">
      <h2>数据统计</h2>
      <p>查看平台运营数据和趋势分析</p>
    </div>

    <!-- 统计概览 -->
    <div class="overview-section animate-slide-up" style="animation-delay: 0.1s">
      <el-row :gutter="24">
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-content">
              <div class="stat-text">
                <div class="stat-label">总用户数</div>
                <div class="stat-value">{{ overviewData.totalUsers?.toLocaleString() || 0 }}</div>
              </div>
              <div class="stat-icon-wrapper user-bg">
                <el-icon><User /></el-icon>
              </div>
            </div>

          </div>
        </el-col>

        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-content">
              <div class="stat-text">
                <div class="stat-label">阿姨总数</div>
                <div class="stat-value">{{ overviewData.totalWorkers?.toLocaleString() || 0 }}</div>
              </div>
              <div class="stat-icon-wrapper worker-bg">
                <el-icon><UserFilled /></el-icon>
              </div>
            </div>

          </div>
        </el-col>

        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-content">
              <div class="stat-text">
                <div class="stat-label">订单总数</div>
                <div class="stat-value">{{ overviewData.totalOrders?.toLocaleString() || 0 }}</div>
              </div>
              <div class="stat-icon-wrapper order-bg">
                <el-icon><Document /></el-icon>
              </div>
            </div>

          </div>
        </el-col>

        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-content">
              <div class="stat-text">
                <div class="stat-label">总收入</div>
                <div class="stat-value">¥{{ formatAmount(overviewData.totalRevenue || 0) }}</div>
              </div>
              <div class="stat-icon-wrapper revenue-bg">
                <el-icon><Money /></el-icon>
              </div>
            </div>

          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 图表区域 -->
    <div class="charts-section animate-slide-up" style="animation-delay: 0.2s">
      <el-row :gutter="24">
        <!-- 服务分布 -->
        <el-col :span="12">
          <el-card class="chart-card" shadow="hover">
            <template #header>
              <span class="card-title">服务分布</span>
            </template>
            <div ref="serviceDistributionChart" class="chart-container">
              <div v-if="serviceDistributionLoading" class="chart-loading">
                <el-icon class="is-loading"><Loading /></el-icon>
              </div>
            </div>
          </el-card>
        </el-col>

        <!-- 订单状态分布 -->
        <el-col :span="12">
          <el-card class="chart-card" shadow="hover">
            <template #header>
              <span class="card-title">订单状态分布</span>
            </template>
            <div ref="orderStatusChart" class="chart-container">
              <div v-if="orderStatusLoading" class="chart-loading">
                <el-icon class="is-loading"><Loading /></el-icon>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 数据详情 -->
    <div class="details-section animate-slide-up" style="animation-delay: 0.25s">
      <el-row :gutter="24">
        <!-- 热门服务 -->
        <el-col :span="12">
          <el-card class="detail-card" shadow="hover">
            <template #header>
              <span class="card-title">热门服务排行</span>
            </template>
            <el-table :data="hotServices" style="width: 100%" v-loading="hotServicesLoading" :header-cell-style="{ background: '#f8f9fa' }">
              <el-table-column prop="serviceName" label="服务名称">
                 <template #default="scope">
                   <div class="service-name-cell">
                     <span class="rank-badge" :class="'rank-' + (scope.$index + 1)">{{ scope.$index + 1 }}</span>
                     {{ scope.row.serviceName }}
                   </div>
                 </template>
              </el-table-column>
              <el-table-column prop="orderCount" label="订单数量" width="120" align="right" />
              <el-table-column prop="revenue" label="收入" width="150" align="right">
                <template #default="scope">
                  <span class="money-text">¥{{ formatAmount(scope.row.revenue) }}</span>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-col>

        <!-- 活跃阿姨 -->
        <el-col :span="12">
          <el-card class="detail-card" shadow="hover">
            <template #header>
              <span class="card-title">阿姨评分排行</span>
            </template>
            <el-table :data="activeWorkers" style="width: 100%" v-loading="activeWorkersLoading" :header-cell-style="{ background: '#f8f9fa' }">
              <el-table-column prop="workerName" label="阿姨姓名" />
              <el-table-column prop="score" label="评分" width="180">
                <template #default="scope">
                  <el-rate
                    v-model="scope.row.score"
                    disabled
                    show-score
                    text-color="#ff9900"
                    score-template="{value}"
                  />
                </template>
              </el-table-column>
              <el-table-column prop="completedOrders" label="完成订单" width="100" align="right" />
              <el-table-column prop="earnings" label="收入" width="120" align="right">
                <template #default="scope">
                  <span class="money-text">¥{{ formatAmount(scope.row.earnings) }}</span>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 趋势图表 -->
    <div class="trend-section animate-slide-up" style="animation-delay: 0.3s">
      <el-row :gutter="24">
        <!-- 订单趋势 -->
        <el-col :span="12">
          <el-card class="chart-card" shadow="hover">
            <template #header>
              <div class="chart-header">
                <span class="card-title">订单趋势</span>
                <el-select v-model="orderTrendDays" @change="loadOrderTrend" size="small" class="trend-select">
                  <el-option label="近7天" :value="7" />
                  <el-option label="近15天" :value="15" />
                  <el-option label="近30天" :value="30" />
                </el-select>
              </div>
            </template>
            <div ref="orderTrendChart" class="chart-container">
              <div v-if="orderTrendLoading" class="chart-loading">
                <el-icon class="is-loading"><Loading /></el-icon>
              </div>
            </div>
          </el-card>
        </el-col>

        <!-- 收入趋势 -->
        <el-col :span="12">
          <el-card class="chart-card" shadow="hover">
            <template #header>
              <div class="chart-header">
                <span class="card-title">收入趋势</span>
                <el-select v-model="revenueTrendDays" @change="loadRevenueTrend" size="small" class="trend-select">
                  <el-option label="近7天" :value="7" />
                  <el-option label="近15天" :value="15" />
                  <el-option label="近30天" :value="30" />
                </el-select>
              </div>
            </template>
            <div ref="revenueTrendChart" class="chart-container">
              <div v-if="revenueTrendLoading" class="chart-loading">
                <el-icon class="is-loading"><Loading /></el-icon>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { User, UserFilled, Document, Money, Loading, Top } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import {
  getOverviewStats,
  getDailyOrderTrend,
  getOrderStatusStats,
  getTopServices,
  getTopWorkers
} from '@/api/admin'

// 响应式数据
const overviewData = ref({})
const orderTrendDays = ref(7)
const revenueTrendDays = ref(7)
const orderTrendData = ref([])
const revenueTrendData = ref([])
const serviceDistributionData = ref([])
const orderStatusData = ref([])
const hotServices = ref([])
const activeWorkers = ref([])

// 加载状态
const orderTrendLoading = ref(false)
const revenueTrendLoading = ref(false)
const serviceDistributionLoading = ref(false)
const orderStatusLoading = ref(false)
const hotServicesLoading = ref(false)
const activeWorkersLoading = ref(false)

// 图表实例
const orderTrendChart = ref(null)
const revenueTrendChart = ref(null)
const serviceDistributionChart = ref(null)
const orderStatusChart = ref(null)

let orderTrendChartInstance = null
let revenueTrendChartInstance = null
let serviceDistributionChartInstance = null
let orderStatusChartInstance = null


// 页面初始化
onMounted(async () => {
  await loadOverviewData()
  await loadAllChartData()

  nextTick(() => {
    initCharts()
    window.addEventListener('resize', handleResize)
  })
})

const handleResize = () => {
  orderTrendChartInstance?.resize()
  revenueTrendChartInstance?.resize()
  serviceDistributionChartInstance?.resize()
  orderStatusChartInstance?.resize()
}

// 加载概览数据
const loadOverviewData = async () => {
  try {
    const response = await getOverviewStats()
    if (response.code === 200) {
      overviewData.value = response.data
    } else {
      throw new Error(response.message || '加载失败')
    }
  } catch (error) {
    console.error('加载概览数据失败', error)
    // ElMessage.error(error.message || '加载概览数据失败') // 静默失败，显示0
    overviewData.value = {
      totalUsers: 0,
      totalWorkers: 0,
      totalOrders: 0,
      totalRevenue: 0
    }
  }
}

// 加载所有图表数据
const loadAllChartData = async () => {
  await Promise.all([
    loadOrderTrend(),
    loadRevenueTrend(),
    loadServiceDistribution(),
    loadOrderStatus(),
    loadHotServices(),
    loadActiveWorkers()
  ])
}

// 加载订单趋势
const loadOrderTrend = async () => {
  orderTrendLoading.value = true
  try {
    const response = await getDailyOrderTrend(orderTrendDays.value)
    if (response.code === 200) {
      // 转换数据格式以适应前端使用
      orderTrendData.value = response.data.map(item => ({
        date: item.date,
        orders: item.orderCount
      }))
    } else {
      throw new Error(response.message || '加载失败')
    }

    nextTick(() => {
      updateOrderTrendChart()
    })
  } catch (error) {
    console.error('加载订单趋势失败', error)
    orderTrendData.value = []
  } finally {
    orderTrendLoading.value = false
  }
}

// 加载收入趋势
const loadRevenueTrend = async () => {
  revenueTrendLoading.value = true
  try {
    const response = await getDailyOrderTrend(revenueTrendDays.value)
    if (response.code === 200) {
      // 转换数据格式以适应前端使用
      revenueTrendData.value = response.data.map(item => ({
        date: item.date,
        revenue: item.revenue
      }))
    } else {
      throw new Error(response.message || '加载失败')
    }

    nextTick(() => {
      updateRevenueTrendChart()
    })
  } catch (error) {
    console.error('加载收入趋势失败', error)
    revenueTrendData.value = []
  } finally {
    revenueTrendLoading.value = false
  }
}

// 加载服务分布
const loadServiceDistribution = async () => {
  serviceDistributionLoading.value = true
  try {
    const response = await getTopServices(20)
    if (response.code === 200) {
      // 转换为饼图需要的格式
      const totalCount = response.data.reduce((sum, item) => sum + item.orderCount, 0)
      serviceDistributionData.value = response.data.map(item => ({
        name: item.serviceName,
        value: item.orderCount
      }))
    } else {
      throw new Error(response.message || '加载失败')
    }

    nextTick(() => {
      updateServiceDistributionChart()
    })
  } catch (error) {
    console.error('加载服务分布失败', error)
    serviceDistributionData.value = []
  } finally {
    serviceDistributionLoading.value = false
  }
}

// 加载订单状态分布
const loadOrderStatus = async () => {
  orderStatusLoading.value = true
  try {
    const response = await getOrderStatusStats()
    if (response.code === 200) {
      // 转换状态名称为中文
      const statusMap = {
        'PENDING': '待分配',
        'ASSIGNED': '已分配',
        'IN_PROGRESS': '进行中',
        'COMPLETED': '已完成',
        'CANCELED': '已取消'
      }
      orderStatusData.value = response.data.map(item => ({
        name: statusMap[item.status] || item.status,
        value: item.count
      }))
    } else {
      throw new Error(response.message || '加载失败')
    }

    nextTick(() => {
      updateOrderStatusChart()
    })
  } catch (error) {
    console.error('加载订单状态分布失败', error)
    orderStatusData.value = []
  } finally {
    orderStatusLoading.value = false
  }
}

// 加载热门服务
const loadHotServices = async () => {
  hotServicesLoading.value = true
  try {
    const response = await getTopServices(5)
    if (response.code === 200) {
      hotServices.value = response.data.map(item => ({
        serviceName: item.serviceName,
        orderCount: item.orderCount,
        revenue: item.revenue
      }))
    } else {
      throw new Error(response.message || '加载失败')
    }
  } catch (error) {
    console.error('加载热门服务失败', error)
    hotServices.value = []
  } finally {
    hotServicesLoading.value = false
  }
}

// 加载活跃阿姨
const loadActiveWorkers = async () => {
  activeWorkersLoading.value = true
  try {
    const response = await getTopWorkers(5)
    if (response.code === 200) {
      activeWorkers.value = response.data.map(item => ({
        workerName: item.workerName || '未知',
        score: item.score,
        completedOrders: item.orderCount,
        earnings: item.revenue
      }))
    } else {
      throw new Error(response.message || '加载失败')
    }
  } catch (error) {
    console.error('加载活跃阿姨失败', error)
    activeWorkers.value = []
  } finally {
    activeWorkersLoading.value = false
  }
}

// 初始化图表
const initCharts = () => {
  if (orderTrendChart.value) {
    orderTrendChartInstance = echarts.init(orderTrendChart.value)
    updateOrderTrendChart()
  }

  if (revenueTrendChart.value) {
    revenueTrendChartInstance = echarts.init(revenueTrendChart.value)
    updateRevenueTrendChart()
  }

  if (serviceDistributionChart.value) {
    serviceDistributionChartInstance = echarts.init(serviceDistributionChart.value)
    updateServiceDistributionChart()
  }

  if (orderStatusChart.value) {
    orderStatusChartInstance = echarts.init(orderStatusChart.value)
    updateOrderStatusChart()
  }
}

// 更新订单趋势图
const updateOrderTrendChart = () => {
  if (!orderTrendChartInstance || !orderTrendData.value.length) return

  const option = {
    color: ['#409EFF'],
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'line',
        lineStyle: {
          color: '#409EFF',
          type: 'dashed'
        }
      },
      backgroundColor: 'rgba(255, 255, 255, 0.9)',
      borderColor: '#e4e7ed',
      textStyle: {
        color: '#606266'
      }
    },
    grid: {
      left: '2%',
      right: '2%',
      bottom: '10%',
      top: '10%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: orderTrendData.value.map(item => item.date.split('-')[2]),
      boundaryGap: false,
      axisLine: { show: false },
      axisTick: { show: false },
      axisLabel: { color: '#909399' }
    },
    yAxis: {
      type: 'value',
      splitLine: {
        lineStyle: {
          type: 'dashed',
          color: '#f2f6fc'
        }
      },
      axisLabel: { color: '#909399' }
    },
    series: [{
      name: '订单数量',
      type: 'line',
      data: orderTrendData.value.map(item => item.orders),
      smooth: true,
      symbol: 'circle',
      symbolSize: 8,
      itemStyle: {
        color: '#409EFF',
        borderWidth: 2,
        borderColor: '#fff'
      },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(64, 158, 255, 0.5)' },
          { offset: 1, color: 'rgba(64, 158, 255, 0.01)' }
        ])
      }
    }]
  }

  orderTrendChartInstance.setOption(option)
}

// 更新收入趋势图
const updateRevenueTrendChart = () => {
  if (!revenueTrendChartInstance || !revenueTrendData.value.length) return

  const option = {
    tooltip: {
      trigger: 'axis',
      formatter: '{b}<br/>收入: ¥{c}',
      backgroundColor: 'rgba(255, 255, 255, 0.9)',
      borderColor: '#e4e7ed',
      textStyle: {
        color: '#606266'
      }
    },
    grid: {
      left: '2%',
      right: '2%',
      bottom: '10%',
      top: '10%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: revenueTrendData.value.map(item => item.date.split('-')[2]),
      axisLine: { show: false },
      axisTick: { show: false },
      axisLabel: { color: '#909399' }
    },
    yAxis: {
      type: 'value',
      splitLine: {
        lineStyle: {
          type: 'dashed',
          color: '#f2f6fc'
        }
      },
      axisLabel: {
        color: '#909399'
      }
    },
    series: [{
      name: '收入',
      type: 'bar',
      barWidth: '40%',
      data: revenueTrendData.value.map(item => item.revenue),
      itemStyle: {
        borderRadius: [4, 4, 0, 0],
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#36D1DC' },
          { offset: 1, color: '#5B86E5' }
        ])
      }
    }]
  }

  revenueTrendChartInstance.setOption(option)
}

// 更新服务分布图
const updateServiceDistributionChart = () => {
  if (!serviceDistributionChartInstance || !serviceDistributionData.value.length) return

  const option = {
    color: ['#73C0DE', '#3BA272', '#FC8452', '#9A60B4', '#EA7CCC'],
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)',
      backgroundColor: 'rgba(255, 255, 255, 0.9)',
      borderColor: '#e4e7ed',
      textStyle: {
        color: '#606266'
      }
    },
    legend: {
      orient: 'vertical',
      right: 10,
      top: 'center',
      textStyle: {
        color: '#606266'
      }
    },
    series: [{
      name: '服务分布',
      type: 'pie',
      radius: ['50%', '70%'],
      center: ['40%', '50%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 10,
        borderColor: '#fff',
        borderWidth: 2
      },
      label: {
        show: false,
        position: 'center'
      },
      emphasis: {
        label: {
          show: true,
          fontSize: '18',
          fontWeight: 'bold',
          formatter: '{b}\n{d}%'
        }
      },
      data: serviceDistributionData.value
    }]
  }

  serviceDistributionChartInstance.setOption(option)
}

// 更新订单状态分布图
const updateOrderStatusChart = () => {
  if (!orderStatusChartInstance || !orderStatusData.value.length) return

  const option = {
    color: ['#5470C6', '#91CC75', '#E6A23C', '#F56C6C', '#909399'],
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)',
      backgroundColor: 'rgba(255, 255, 255, 0.9)',
      borderColor: '#e4e7ed',
      textStyle: {
        color: '#606266'
      }
    },
    legend: {
      orient: 'vertical',
      right: 10,
      top: 'center',
      textStyle: {
        color: '#606266'
      }
    },
    series: [{
      name: '订单状态',
      type: 'pie',
      radius: ['50%', '70%'],
      center: ['40%', '50%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 10,
        borderColor: '#fff',
        borderWidth: 2
      },
      label: {
        show: false,
        position: 'center'
      },
      emphasis: {
        label: {
          show: true,
          fontSize: '18',
          fontWeight: 'bold',
          formatter: '{b}\n{d}%'
        }
      },
      data: orderStatusData.value
    }]
  }

  orderStatusChartInstance.setOption(option)
}

// 工具函数
const formatAmount = (amount) => {
  return amount ? Number(amount).toLocaleString() : '0'
}
</script>

<style scoped>
.admin-statistics {
  padding: 24px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.page-header {
  margin-bottom: 32px;
}

.page-header h2 {
  margin: 0 0 8px 0;
  font-size: 28px;
  font-weight: 700;
  color: #1a1a1a;
  letter-spacing: -0.5px;
}

.page-header p {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

/* 概览卡片优化 */
.overview-section {
  margin-bottom: 32px;
}

.stat-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  height: 140px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.03);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  border: 1px solid #ebeef5;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
}

.stat-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  color: #303133;
  margin-top: 8px;
  font-family: 'DIN Alternate', 'Roboto', sans-serif;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  font-weight: 500;
}

.stat-icon-wrapper {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.user-bg {
  background-color: #ecf5ff;
  color: #409eff;
}

.worker-bg {
  background-color: #fdf6ec;
  color: #e6a23c;
}

.order-bg {
  background-color: #f0f9eb;
  color: #67c23a;
}

.revenue-bg {
  background-color: #fef0f0;
  color: #f56c6c;
}

.stat-footer {
  font-size: 13px;
  color: #909399;
  display: flex;
  align-items: center;
}

.trend-up {
  color: #67c23a;
  display: flex;
  align-items: center;
  gap: 4px;
  font-weight: 500;
}

/* 图表和详情区域共用样式 */
.chart-card,
.detail-card {
  border-radius: 16px;
  border: none;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.03) !important;
  margin-bottom: 24px;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-container {
  height: 350px;
  position: relative;
}

.chart-loading {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 30px;
  color: #409eff;
}

/* 详情表格优化 */
.rank-badge {
  display: inline-block;
  width: 24px;
  height: 24px;
  line-height: 24px;
  text-align: center;
  border-radius: 50%;
  background-color: #f0f2f5;
  color: #909399;
  font-size: 12px;
  font-weight: 600;
  margin-right: 8px;
}

.rank-1 { background-color: #f56c6c; color: white; }
.rank-2 { background-color: #e6a23c; color: white; }
.rank-3 { background-color: #409eff; color: white; }

.service-name-cell {
  display: flex;
  align-items: center;
}

.money-text {
  font-weight: 600;
  color: #67c23a;
}

/* 动画类 */
.animate-fade-in {
  animation: fadeIn 0.6s ease-out;
}

.animate-slide-up {
  animation: slideUp 0.6s ease-out backwards;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

:deep(.el-card__header) {
  border-bottom: 1px solid #f0f2f5;
  padding: 16px 24px;
}

:deep(.el-table th.el-table__cell) {
  background-color: #fafafa !important;
  font-weight: 600;
  color: #606266;
}

@media (max-width: 768px) {
  .admin-statistics {
    padding: 16px;
  }

  .overview-section .el-col {
    margin-bottom: 16px;
  }
}
</style>