<template>
  <div class="worker-schedule">
    <div class="page-header">
      <h2>档期管理</h2>
      <p>管理您的工作时间安排</p>
    </div>

    <!-- 日历和设置区域 -->
    <div class="schedule-content">
      <!-- 左侧日历 -->
      <div class="calendar-section">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>工作日历</span>
              <el-button size="small" @click="goToToday">今天</el-button>
            </div>
          </template>

          <el-calendar v-model="selectedDate">
            <template #date-cell="{ data }">
              <div class="calendar-cell" :class="getCellClass(data)">
                <span class="date-number">{{ data.day.split('-')[2] }}</span>
                <div class="cell-content">
                  <div v-if="isWorkDay(data.day)" class="work-indicator">
                    <span v-if="hasOrders(data.day)" class="order-badge">有预约</span>
                    <span v-else class="available-badge">可接单</span>
                  </div>
                  <div v-else class="rest-indicator">休息</div>
                </div>
              </div>
            </template>
          </el-calendar>
        </el-card>
      </div>

      <!-- 右侧设置面板 -->
      <div class="settings-section">
        <!-- 当日设置 -->
        <el-card class="day-settings">
          <template #header>
            <span>{{ formatSelectedDate }} 设置</span>
          </template>

          <div class="setting-item">
            <el-switch
              v-model="currentDaySettings.isWorkDay"
              active-text="工作日"
              inactive-text="休息日"
              @change="updateDaySettings"
            />
          </div>

          <div v-if="currentDaySettings.isWorkDay" class="work-hours">
            <div class="setting-item">
              <span class="label">工作时间：</span>
              <el-time-picker
                v-model="currentDaySettings.startTime"
                placeholder="开始时间"
                format="HH:mm"
                value-format="HH:mm"
                @change="updateDaySettings"
              />
              <span class="time-separator">至</span>
              <el-time-picker
                v-model="currentDaySettings.endTime"
                placeholder="结束时间"
                format="HH:mm"
                value-format="HH:mm"
                @change="updateDaySettings"
              />
            </div>

            <div class="setting-item">
              <span class="label">已预约订单：</span>
              <div class="order-list">
                <div
                  v-for="order in getDateOrders(selectedDateStr)"
                  :key="order.id"
                  class="order-item"
                >
                  <span class="order-time">{{ order.timeSlot }}</span>
                  <span class="order-service">{{ order.serviceName }}</span>
                </div>
                <div v-if="getDateOrders(selectedDateStr).length === 0" class="no-orders">
                  暂无预约
                </div>
              </div>
            </div>
          </div>
        </el-card>

        <!-- 快速设置 -->
        <el-card class="quick-settings">
          <template #header>
            <span>快速设置</span>
          </template>

          <div class="setting-item">
            <el-button @click="setWeekDays" size="small" type="primary">
              设置周一至周五为工作日
            </el-button>
          </div>

          <div class="setting-item">
            <el-button @click="setWeekends" size="small">
              设置周末为休息日
            </el-button>
          </div>

          <div class="setting-item">
            <span class="label">默认工作时间：</span>
            <el-time-picker
              v-model="defaultWorkHours.start"
              placeholder="开始时间"
              format="HH:mm"
              value-format="HH:mm"
              size="small"
            />
            <span class="time-separator">至</span>
            <el-time-picker
              v-model="defaultWorkHours.end"
              placeholder="结束时间"
              format="HH:mm"
              value-format="HH:mm"
              size="small"
            />
          </div>

          <div class="setting-item">
            <el-button @click="applyDefaultHours" size="small" type="success">
              应用到工作日
            </el-button>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'

// 响应式数据
const selectedDate = ref(new Date())
const scheduleData = ref({}) // 档期数据 { date: { isWorkDay, startTime, endTime } }
const orderData = ref([]) // 订单数据

// 默认工作时间
const defaultWorkHours = ref({
  start: '09:00',
  end: '18:00'
})

// 计算属性
const selectedDateStr = computed(() => {
  return selectedDate.value.toISOString().split('T')[0]
})

const formatSelectedDate = computed(() => {
  return selectedDate.value.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    weekday: 'long'
  })
})

const currentDaySettings = computed({
  get() {
    const dateStr = selectedDateStr.value
    return scheduleData.value[dateStr] || {
      isWorkDay: true,
      startTime: '09:00',
      endTime: '18:00'
    }
  },
  set(value) {
    const dateStr = selectedDateStr.value
    scheduleData.value[dateStr] = { ...value }
  }
})

// 页面初始化
onMounted(() => {
  loadScheduleData()
  loadOrderData()
})

// 监听日期变化
watch(selectedDate, () => {
  // 日期变化时可以加载该日期的详细信息
})

// 加载档期数据
const loadScheduleData = () => {
  // 这里应该从后端加载档期数据
  // 现在使用模拟数据
  const today = new Date()
  for (let i = -7; i <= 30; i++) {
    const date = new Date(today)
    date.setDate(today.getDate() + i)
    const dateStr = date.toISOString().split('T')[0]

    // 默认周一到周五是工作日
    const dayOfWeek = date.getDay()
    scheduleData.value[dateStr] = {
      isWorkDay: dayOfWeek >= 1 && dayOfWeek <= 5,
      startTime: '09:00',
      endTime: '18:00'
    }
  }
}

// 加载订单数据
const loadOrderData = () => {
  // 这里应该从后端加载订单数据
  // 模拟一些订单数据
  orderData.value = [
    {
      id: 1,
      scheduleDate: '2024-12-20',
      timeSlot: '10:00-12:00',
      serviceName: '家庭保洁'
    },
    {
      id: 2,
      scheduleDate: '2024-12-22',
      timeSlot: '14:00-16:00',
      serviceName: '月嫂服务'
    }
  ]
}

// 工具函数
const isWorkDay = (dateStr) => {
  return scheduleData.value[dateStr]?.isWorkDay || false
}

const hasOrders = (dateStr) => {
  return orderData.value.some(order => order.scheduleDate === dateStr)
}

const getDateOrders = (dateStr) => {
  return orderData.value.filter(order => order.scheduleDate === dateStr)
}

const getCellClass = (data) => {
  const dateStr = data.day
  const classes = []

  if (isWorkDay(dateStr)) {
    classes.push('work-day')
  } else {
    classes.push('rest-day')
  }

  if (hasOrders(dateStr)) {
    classes.push('has-orders')
  }

  if (dateStr === selectedDateStr.value) {
    classes.push('selected')
  }

  return classes
}

// 事件处理
const goToToday = () => {
  selectedDate.value = new Date()
}

const updateDaySettings = () => {
  const dateStr = selectedDateStr.value
  scheduleData.value[dateStr] = { ...currentDaySettings.value }
  ElMessage.success('档期设置已保存')
}

const setWeekDays = () => {
  const today = new Date()
  let count = 0

  for (let i = 0; i <= 30; i++) {
    const date = new Date(today)
    date.setDate(today.getDate() + i)
    const dayOfWeek = date.getDay()

    if (dayOfWeek >= 1 && dayOfWeek <= 5) {
      const dateStr = date.toISOString().split('T')[0]
      if (!scheduleData.value[dateStr]) {
        scheduleData.value[dateStr] = {}
      }
      scheduleData.value[dateStr].isWorkDay = true
      count++
    }
  }

  ElMessage.success(`已设置 ${count} 个工作日`)
}

const setWeekends = () => {
  const today = new Date()
  let count = 0

  for (let i = 0; i <= 30; i++) {
    const date = new Date(today)
    date.setDate(today.getDate() + i)
    const dayOfWeek = date.getDay()

    if (dayOfWeek === 0 || dayOfWeek === 6) {
      const dateStr = date.toISOString().split('T')[0]
      if (!scheduleData.value[dateStr]) {
        scheduleData.value[dateStr] = {}
      }
      scheduleData.value[dateStr].isWorkDay = false
      count++
    }
  }

  ElMessage.success(`已设置 ${count} 个休息日`)
}

const applyDefaultHours = () => {
  if (!defaultWorkHours.value.start || !defaultWorkHours.value.end) {
    ElMessage.warning('请先设置默认工作时间')
    return
  }

  let count = 0
  Object.keys(scheduleData.value).forEach(dateStr => {
    if (scheduleData.value[dateStr].isWorkDay) {
      scheduleData.value[dateStr].startTime = defaultWorkHours.value.start
      scheduleData.value[dateStr].endTime = defaultWorkHours.value.end
      count++
    }
  })

  ElMessage.success(`已应用默认时间到 ${count} 个工作日`)
}
</script>

<style scoped>
.worker-schedule {
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

.schedule-content {
  display: flex;
  gap: 24px;
}

.calendar-section {
  flex: 2;
}

.settings-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* 日历单元格样式 */
.calendar-cell {
  height: 80px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  position: relative;
}

.date-number {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 4px;
}

.cell-content {
  font-size: 12px;
  text-align: center;
}

.work-day .date-number {
  color: #1890ff;
}

.rest-day .date-number {
  color: #999;
}

.work-day {
  background-color: #f0f9ff;
}

.rest-day {
  background-color: #f5f5f5;
}

.has-orders {
  background-color: #e6f7ff;
  border: 1px solid #91d5ff;
}

.selected {
  border: 2px solid #1890ff !important;
}

.work-indicator .order-badge {
  background: #52c41a;
  color: white;
  padding: 2px 6px;
  border-radius: 10px;
  font-size: 10px;
}

.work-indicator .available-badge {
  background: #1890ff;
  color: white;
  padding: 2px 6px;
  border-radius: 10px;
  font-size: 10px;
}

.rest-indicator {
  color: #999;
  font-size: 10px;
}

/* 设置面板样式 */
.setting-item {
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.label {
  min-width: 80px;
  font-size: 14px;
  color: #666;
}

.time-separator {
  margin: 0 8px;
  color: #999;
}

.work-hours {
  padding-left: 16px;
  border-left: 3px solid #1890ff;
}

.order-list {
  flex: 1;
}

.order-item {
  display: flex;
  justify-content: space-between;
  padding: 4px 8px;
  background: #f9f9f9;
  border-radius: 4px;
  margin-bottom: 4px;
  font-size: 12px;
}

.order-time {
  color: #1890ff;
  font-weight: 600;
}

.order-service {
  color: #666;
}

.no-orders {
  color: #999;
  font-size: 12px;
  text-align: center;
  padding: 8px;
}

.quick-settings .setting-item {
  flex-direction: column;
  align-items: stretch;
}

.quick-settings .label {
  margin-bottom: 8px;
}

@media (max-width: 1200px) {
  .schedule-content {
    flex-direction: column;
  }

  .calendar-section,
  .settings-section {
    flex: none;
  }
}

@media (max-width: 768px) {
  .worker-schedule {
    padding: 16px;
  }

  .setting-item {
    flex-direction: column;
    align-items: stretch;
  }

  .label {
    min-width: auto;
    margin-bottom: 4px;
  }
}
</style>