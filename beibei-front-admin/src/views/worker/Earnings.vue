<template>
  <div class="worker-earnings">
    <div class="page-header">
      <h2>收入管理</h2>
      <p>查看收入数据和明细</p>
    </div>

    <!-- 收入概览 -->
    <div class="overview-section">
      <el-card v-loading="overviewLoading">
        <template #header>
          <div class="card-header">
            <span>收入概览</span>
            <el-button type="primary" size="small" @click="refreshOverview">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>
        </template>

        <el-row :gutter="24" class="overview-stats">
          <el-col :span="6">
            <div class="stat-item">
              <div class="stat-value">¥{{ formatAmount(overview.todayEarnings || 0) }}</div>
              <div class="stat-label">今日收入</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-item">
              <div class="stat-value">¥{{ formatAmount(overview.weekEarnings || 0) }}</div>
              <div class="stat-label">本周收入</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-item">
              <div class="stat-value">¥{{ formatAmount(overview.monthEarnings || 0) }}</div>
              <div class="stat-label">本月收入</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-item">
              <div class="stat-value">¥{{ formatAmount(overview.totalEarnings || 0) }}</div>
              <div class="stat-label">总收入</div>
            </div>
          </el-col>
        </el-row>
      </el-card>
    </div>

    <!-- 收入趋势 -->
    <div class="trend-section">
      <el-card v-loading="trendLoading">
        <template #header>
          <div class="card-header">
            <span>收入趋势</span>
            <el-select v-model="trendDays" @change="loadTrend" size="small">
              <el-option label="近7天" :value="7" />
              <el-option label="近15天" :value="15" />
              <el-option label="近30天" :value="30" />
            </el-select>
          </div>
        </template>

        <div ref="chartContainer" class="chart-container">
          <div v-if="!trendData || trendData.length === 0" class="empty-chart">
            <el-empty description="暂无趋势数据" />
          </div>
        </div>
      </el-card>
    </div>

    <!-- 收入明细 -->
    <div class="details-section">
      <el-card v-loading="detailsLoading">
        <template #header>
          <div class="card-header">
            <span>收入明细</span>
            <div class="header-actions">
              <el-date-picker
                v-model="dateRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                @change="loadDetails"
                size="small"
              />
              <el-button type="primary" size="small" @click="loadDetails">
                <el-icon><Search /></el-icon>
                查询
              </el-button>
            </div>
          </div>
        </template>

        <el-table :data="detailsList" style="width: 100%" stripe>
          <el-table-column prop="completedAt" label="日期" width="120">
            <template #default="scope">
              {{ formatDate(scope.row?.completedAt) }}
            </template>
          </el-table-column>
          <el-table-column prop="orderNo" label="订单号" width="200">
            <template #default="scope">
              {{ scope.row?.orderNo || '-' }}
            </template>
          </el-table-column>
          <el-table-column prop="serviceName" label="服务项目">
            <template #default="scope">
              {{ scope.row?.serviceName || '-' }}
            </template>
          </el-table-column>
          <el-table-column prop="customerName" label="客户" width="120">
            <template #default="scope">
              {{ scope.row?.customerName || '-' }}
            </template>
          </el-table-column>
          <el-table-column prop="earnings" label="收入金额" width="120" align="right">
            <template #default="scope">
              <span class="earnings-amount">¥{{ formatAmount(scope.row?.earnings) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100" align="center">
            <template #default="scope">
              <el-tag :type="getStatusType(scope.row?.status)" size="small">
                {{ getStatusText(scope.row?.status) }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 -->
        <div class="pagination-container">
          <el-pagination
            v-model:current-page="pagination.page"
            v-model:page-size="pagination.size"
            :page-sizes="[10, 20, 50, 100]"
            :total="pagination.total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="loadDetails"
            @current-change="loadDetails"
          />
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh, Search } from '@element-plus/icons-vue'
import { getEarningsOverview, getEarningsTrend, getEarningsDetails } from '@/api/worker.js'
import * as echarts from 'echarts'

// 响应式数据
const overviewLoading = ref(false)
const trendLoading = ref(false)
const detailsLoading = ref(false)

const overview = ref({
  todayEarnings: 0,
  weekEarnings: 0,
  monthEarnings: 0,
  totalEarnings: 0
})
const trendData = ref([])
const trendDays = ref(7)
const detailsList = ref([])

const dateRange = ref([])
const pagination = ref({
  page: 1,
  size: 10,
  total: 0
})

const chartContainer = ref(null)
let chartInstance = null
const isComponentMounted = ref(false)

// 页面初始化
onMounted(async () => {
  isComponentMounted.value = true

  // 并行加载数据
  try {
    await Promise.allSettled([
      loadOverview(),
      loadTrend(),
      loadDetails()
    ])
  } catch (error) {
    console.error('初始化数据加载失败', error)
  }

  // 确保DOM完全渲染后初始化图表
  nextTick(() => {
    setTimeout(() => {
      initChart()
    }, 200)
  })
})

// 组件卸载时清理图表
onUnmounted(() => {
  isComponentMounted.value = false
  if (chartInstance) {
    try {
      chartInstance.dispose()
    } catch (error) {
      console.warn('清理图表实例失败', error)
    }
    chartInstance = null
  }
})

// 监听趋势数据变化
watch(() => trendData.value, (newData) => {
  if (!newData || !isComponentMounted.value) return

  nextTick(() => {
    if (chartContainer.value && chartInstance && trendData.value?.length > 0) {
      updateChart()
    }
  })
}, { deep: true, immediate: false })

// 加载收入概览
const loadOverview = async () => {
  if (!isComponentMounted.value) return

  overviewLoading.value = true
  try {
    const response = await getEarningsOverview()
    if (response?.code === 200) {
      // 确保 overview.value 存在后再赋值
      if (overview.value) {
        overview.value = {
          todayEarnings: response.data?.todayEarnings || 0,
          weekEarnings: response.data?.weekEarnings || 0,
          monthEarnings: response.data?.monthEarnings || 0,
          totalEarnings: response.data?.totalEarnings || 0
        }
      }
    } else {
      ElMessage.error(response?.message || '加载收入概览失败')
    }
  } catch (error) {
    console.error('加载收入概览失败', error)
    ElMessage.error('加载收入概览失败')
  } finally {
    overviewLoading.value = false
  }
}

// 刷新概览
const refreshOverview = () => {
  loadOverview()
  loadTrend()
}

// 加载收入趋势
const loadTrend = async () => {
  if (!isComponentMounted.value) return

  trendLoading.value = true
  try {
    const response = await getEarningsTrend(trendDays.value)
    if (response?.code === 200) {
      // 确保 trendData.value 存在后再赋值
      if (trendData.value !== null && trendData.value !== undefined) {
        trendData.value = Array.isArray(response.data) ? response.data : []
      }
    } else {
      ElMessage.error(response?.message || '加载收入趋势失败')
      if (trendData.value !== null && trendData.value !== undefined) {
        trendData.value = []
      }
    }
  } catch (error) {
    console.error('加载收入趋势失败', error)
    ElMessage.error('加载收入趋势失败')
    if (trendData.value !== null && trendData.value !== undefined) {
      trendData.value = []
    }
  } finally {
    trendLoading.value = false
  }
}

// 加载收入明细
const loadDetails = async () => {
  if (!isComponentMounted.value) return

  detailsLoading.value = true
  try {
    const params = {
      page: pagination.value?.page || 1,
      size: pagination.value?.size || 10
    }

    if (dateRange.value && Array.isArray(dateRange.value) && dateRange.value.length === 2) {
      params.startDate = dateRange.value[0]
      params.endDate = dateRange.value[1]
    }

    const response = await getEarningsDetails(params)
    if (response?.code === 200) {
      const data = response.data || {}
      // 确保 detailsList.value 存在后再赋值
      if (detailsList.value !== null && detailsList.value !== undefined) {
        detailsList.value = Array.isArray(data.records) ? data.records : []
      }
      // 确保 pagination.value 存在后再赋值
      if (pagination.value !== null && pagination.value !== undefined) {
        pagination.value = {
          ...pagination.value,
          total: data.total || 0
        }
      }
    } else {
      ElMessage.error(response?.message || '加载收入明细失败')
      if (detailsList.value !== null && detailsList.value !== undefined) {
        detailsList.value = []
      }
    }
  } catch (error) {
    console.error('加载收入明细失败', error)
    ElMessage.error('加载收入明细失败')
    if (detailsList.value !== null && detailsList.value !== undefined) {
      detailsList.value = []
    }
  } finally {
    detailsLoading.value = false
  }
}

// 初始化图表
const initChart = () => {
  if (!isComponentMounted.value || !chartContainer.value) {
    console.warn('图表容器未准备好或组件未挂载')
    return
  }

  try {
    // 确保容器有尺寸
    if (chartContainer.value.offsetWidth === 0 || chartContainer.value.offsetHeight === 0) {
      console.warn('图表容器尺寸为0，延迟初始化')
      setTimeout(() => {
        if (isComponentMounted.value) {
          initChart()
        }
      }, 300)
      return
    }

    // 销毁现有图表实例
    if (chartInstance) {
      try {
        chartInstance.dispose()
      } catch (error) {
        console.warn('销毁图表实例失败', error)
      }
      chartInstance = null
    }

    chartInstance = echarts.init(chartContainer.value)

    // 添加错误事件监听
    chartInstance.on('error', (error) => {
      console.error('ECharts 渲染错误', error)
    })

    updateChart()
  } catch (error) {
    console.error('初始化图表失败', error)
  }
}

// 更新图表
const updateChart = () => {
  if (!isComponentMounted.value || !chartInstance || !trendData.value?.length) return

  try {
    const option = {
      title: {
        text: `近${trendDays.value}天收入趋势`,
        left: 'center',
        textStyle: {
          fontSize: 16,
          color: '#333'
        }
      },
      tooltip: {
        trigger: 'axis',
        formatter: '{b}<br/>收入: ¥{c}'
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        data: trendData.value.map(item => item?.date || ''),
        axisLine: {
          lineStyle: {
            color: '#ddd'
          }
        }
      },
      yAxis: {
        type: 'value',
        axisLine: {
          lineStyle: {
            color: '#ddd'
          }
        },
        axisLabel: {
          formatter: '¥{value}'
        }
      },
      series: [{
        name: '收入',
        type: 'line',
        data: trendData.value.map(item => item?.amount || 0),
        smooth: true,
        itemStyle: {
          color: '#409EFF'
        },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [{
              offset: 0,
              color: 'rgba(64, 158, 255, 0.3)'
            }, {
              offset: 1,
              color: 'rgba(64, 158, 255, 0.1)'
            }]
          }
        }
      }]
    }

    chartInstance.setOption(option, true) // 清空之前的option
  } catch (error) {
    console.error('更新图表失败', error)
  }
}

// 工具函数
const formatAmount = (amount) => {
  try {
    if (amount === null || amount === undefined || amount === '') return '0'
    const num = Number(amount)
    return isNaN(num) ? '0' : num.toLocaleString()
  } catch (error) {
    console.warn('格式化金额失败', amount, error)
    return '0'
  }
}

const formatDate = (dateStr) => {
  try {
    if (!dateStr) return ''
    const date = new Date(dateStr)
    if (isNaN(date.getTime())) {
      // 尝试处理LocalDateTime格式 "2025-09-27T10:30:00"
      const isoDate = dateStr.includes('T') ? dateStr : dateStr + 'T00:00:00'
      const parsedDate = new Date(isoDate)
      return isNaN(parsedDate.getTime()) ? dateStr : parsedDate.toLocaleDateString('zh-CN')
    }
    return date.toLocaleDateString('zh-CN')
  } catch (error) {
    console.warn('格式化日期失败', dateStr, error)
    return dateStr || ''
  }
}

const getStatusType = (status) => {
  if (!status) return ''
  const statusMap = {
    'COMPLETED': 'success',
    'PENDING': 'warning'
  }
  return statusMap[status] || ''
}

const getStatusText = (status) => {
  if (!status) return '未知'
  const statusMap = {
    'COMPLETED': '已结算',
    'PENDING': '待结算'
  }
  return statusMap[status] || '未知'
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

.overview-section,
.trend-section,
.details-section {
  margin-bottom: 24px;
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

.overview-stats {
  margin-top: 16px;
}

.stat-item {
  text-align: center;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #1890ff;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #8c8c8c;
}

.chart-container {
  height: 400px;
  width: 100%;
}

.empty-chart {
  height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.earnings-amount {
  font-weight: 600;
  color: #f5222d;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}

@media (max-width: 768px) {
  .overview-stats {
    flex-direction: column;
    gap: 16px;
  }

  .header-actions {
    flex-direction: column;
    gap: 8px;
  }
}
</style>