<template>
  <div class="worker-schedule">
    <div class="page-header">
      <h2>档期管理</h2>
      <p>管理您的工作时间安排</p>
    </div>

    <!-- 日历视图 -->
    <div class="calendar-section">
      <el-card v-loading="loading">
        <template #header>
          <div class="card-header">
            <span>工作档期</span>
            <div class="header-actions">
              <el-date-picker
                v-model="selectedMonth"
                type="month"
                placeholder="选择月份"
                format="YYYY年MM月"
                value-format="YYYY-MM"
                @change="loadSchedule"
                size="small"
              />
              <el-button type="primary" size="small" @click="handleSaveSchedule">
                <el-icon><Check /></el-icon>
                保存档期
              </el-button>
            </div>
          </div>
        </template>

        <div class="calendar-container">
          <el-calendar v-model="selectedDate" @pick="handleDatePick">
            <template #date-cell="{ data }">
              <div class="calendar-day" :class="getDayClass(data)">
                <div class="day-number">{{ data.day.split('-')[2] }}</div>
                <div class="day-status">
                  <el-tag
                    v-if="getScheduleInfo(data.day)"
                    :type="getScheduleInfo(data.day).available ? 'success' : 'danger'"
                    size="small"
                  >
                    {{ getScheduleInfo(data.day).available ? '有空' : '忙碌' }}
                  </el-tag>
                  <el-button
                    v-else
                    size="small"
                    type="primary"
                    @click="handleSetSchedule(data.day)"
                  >
                    设置
                  </el-button>
                </div>
              </div>
            </template>
          </el-calendar>
        </div>
      </el-card>
    </div>

    <!-- 档期详情弹窗 -->
    <el-dialog
      v-model="scheduleDialogVisible"
      :title="`设置 ${selectedDateStr} 的档期`"
      width="500px"
    >
      <el-form ref="scheduleFormRef" :model="scheduleForm" label-width="80px">
        <el-form-item label="是否有空">
          <el-radio-group v-model="scheduleForm.available">
            <el-radio :label="true">有空</el-radio>
            <el-radio :label="false">忙碌</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item v-if="scheduleForm.available" label="可服务时段">
          <el-checkbox-group v-model="scheduleForm.timeSlots">
            <el-checkbox label="morning">上午 (8:00-12:00)</el-checkbox>
            <el-checkbox label="afternoon">下午 (13:00-17:00)</el-checkbox>
            <el-checkbox label="evening">晚上 (18:00-22:00)</el-checkbox>
          </el-checkbox-group>
        </el-form-item>

        <el-form-item label="备注">
          <el-input
            v-model="scheduleForm.notes"
            type="textarea"
            :rows="3"
            placeholder="可填写特殊说明..."
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="scheduleDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleConfirmSchedule">确认</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 批量设置弹窗 -->
    <el-dialog v-model="batchDialogVisible" title="批量设置档期" width="600px">
      <el-form ref="batchFormRef" :model="batchForm" label-width="100px">
        <el-form-item label="日期范围" required>
          <el-date-picker
            v-model="batchForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>

        <el-form-item label="重复模式">
          <el-radio-group v-model="batchForm.repeatType">
            <el-radio label="none">不重复</el-radio>
            <el-radio label="weekly">按周重复</el-radio>
            <el-radio label="daily">每日重复</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item v-if="batchForm.repeatType === 'weekly'" label="重复的星期">
          <el-checkbox-group v-model="batchForm.weekDays">
            <el-checkbox label="1">周一</el-checkbox>
            <el-checkbox label="2">周二</el-checkbox>
            <el-checkbox label="3">周三</el-checkbox>
            <el-checkbox label="4">周四</el-checkbox>
            <el-checkbox label="5">周五</el-checkbox>
            <el-checkbox label="6">周六</el-checkbox>
            <el-checkbox label="0">周日</el-checkbox>
          </el-checkbox-group>
        </el-form-item>

        <el-form-item label="是否有空">
          <el-radio-group v-model="batchForm.available">
            <el-radio :label="true">有空</el-radio>
            <el-radio :label="false">忙碌</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item v-if="batchForm.available" label="可服务时段">
          <el-checkbox-group v-model="batchForm.timeSlots">
            <el-checkbox label="morning">上午 (8:00-12:00)</el-checkbox>
            <el-checkbox label="afternoon">下午 (13:00-17:00)</el-checkbox>
            <el-checkbox label="evening">晚上 (18:00-22:00)</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="batchDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleBatchSet">确认批量设置</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 操作按钮 -->
    <div class="action-buttons">
      <el-button type="primary" @click="handleBatchSetting">
        <el-icon><Calendar /></el-icon>
        批量设置
      </el-button>
      <el-button @click="loadSchedule">
        <el-icon><Refresh /></el-icon>
        刷新
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Check, Calendar, Refresh } from '@element-plus/icons-vue'
import { getWorkerSchedule, updateWorkerSchedule } from '@/api/worker.js'

// 响应式数据
const loading = ref(false)
const selectedDate = ref(new Date())
const selectedMonth = ref(new Date().getFullYear() + '-' + String(new Date().getMonth() + 1).padStart(2, '0'))
const scheduleData = ref([])

const scheduleDialogVisible = ref(false)
const batchDialogVisible = ref(false)
const currentEditDate = ref('')

// 表单数据
const scheduleForm = ref({
  available: true,
  timeSlots: ['morning', 'afternoon'],
  notes: ''
})

const batchForm = ref({
  dateRange: [],
  repeatType: 'none',
  weekDays: [],
  available: true,
  timeSlots: ['morning', 'afternoon']
})

// 计算属性
const selectedDateStr = computed(() => {
  return currentEditDate.value ? new Date(currentEditDate.value).toLocaleDateString('zh-CN') : ''
})

// 页面初始化
onMounted(() => {
  loadSchedule()
})

// 加载档期数据
const loadSchedule = async () => {
  loading.value = true
  try {
    const [year, month] = selectedMonth.value.split('-')
    const startDate = `${year}-${month}-01`
    const endDate = `${year}-${month}-31`

    const response = await getWorkerSchedule({
      startDate,
      endDate
    })

    if (response.code === 200) {
      // 转换后端数据格式以匹配前端期望
      scheduleData.value = (response.data || []).map(item => ({
        date: item.date,
        available: item.workDay,
        timeSlots: item.timeSlots || [],
        notes: '' // 后端暂时不返回notes字段
      }))
    } else {
      ElMessage.error(response.message || '加载档期数据失败')
    }
  } catch (error) {
    console.error('加载档期数据失败', error)
    ElMessage.error('加载档期数据失败')
  } finally {
    loading.value = false
  }
}

// 获取日期的档期信息
const getScheduleInfo = (date) => {
  return scheduleData.value.find(item => item.date === date)
}

// 获取日期的样式类
const getDayClass = (data) => {
  const scheduleInfo = getScheduleInfo(data.day)
  if (scheduleInfo) {
    return scheduleInfo.available ? 'available-day' : 'busy-day'
  }
  return ''
}

// 处理日期点击
const handleDatePick = (value) => {
  selectedDate.value = value
}

// 设置档期
const handleSetSchedule = (date) => {
  currentEditDate.value = date
  const existing = getScheduleInfo(date)

  if (existing) {
    scheduleForm.value = {
      available: existing.available,
      timeSlots: existing.timeSlots || [],
      notes: existing.notes || ''
    }
  } else {
    scheduleForm.value = {
      available: true,
      timeSlots: ['morning', 'afternoon'],
      notes: ''
    }
  }

  scheduleDialogVisible.value = true
}

// 确认单个档期设置
const handleConfirmSchedule = () => {
  const scheduleInfo = {
    date: currentEditDate.value,
    available: scheduleForm.value.available,
    timeSlots: scheduleForm.value.available ? scheduleForm.value.timeSlots : [],
    notes: scheduleForm.value.notes
  }

  // 更新本地数据
  const existingIndex = scheduleData.value.findIndex(item => item.date === currentEditDate.value)
  if (existingIndex >= 0) {
    scheduleData.value[existingIndex] = scheduleInfo
  } else {
    scheduleData.value.push(scheduleInfo)
  }

  scheduleDialogVisible.value = false
  ElMessage.success('档期设置成功，请记得保存')
}

// 批量设置
const handleBatchSetting = () => {
  batchForm.value = {
    dateRange: [],
    repeatType: 'none',
    weekDays: [],
    available: true,
    timeSlots: ['morning', 'afternoon']
  }
  batchDialogVisible.value = true
}

// 确认批量设置
const handleBatchSet = () => {
  if (!batchForm.value.dateRange || batchForm.value.dateRange.length !== 2) {
    ElMessage.warning('请选择日期范围')
    return
  }

  const [startDate, endDate] = batchForm.value.dateRange
  const dates = generateDateRange(startDate, endDate, batchForm.value.repeatType, batchForm.value.weekDays)

  dates.forEach(date => {
    const scheduleInfo = {
      date,
      available: batchForm.value.available,
      timeSlots: batchForm.value.available ? batchForm.value.timeSlots : [],
      notes: ''
    }

    const existingIndex = scheduleData.value.findIndex(item => item.date === date)
    if (existingIndex >= 0) {
      scheduleData.value[existingIndex] = scheduleInfo
    } else {
      scheduleData.value.push(scheduleInfo)
    }
  })

  batchDialogVisible.value = false
  ElMessage.success(`已批量设置 ${dates.length} 天的档期，请记得保存`)
}

// 生成日期范围
const generateDateRange = (startDate, endDate, repeatType, weekDays) => {
  const dates = []
  const start = new Date(startDate)
  const end = new Date(endDate)

  while (start <= end) {
    const dateStr = start.toISOString().split('T')[0]
    const dayOfWeek = start.getDay()

    if (repeatType === 'none' || repeatType === 'daily') {
      dates.push(dateStr)
    } else if (repeatType === 'weekly' && weekDays.includes(String(dayOfWeek))) {
      dates.push(dateStr)
    }

    start.setDate(start.getDate() + 1)
  }

  return dates
}

// 保存档期
const handleSaveSchedule = async () => {
  try {
    await ElMessageBox.confirm('确认保存当前的档期设置吗？', '确认保存', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'info'
    })

    // 转换数据格式以匹配后端期望
    const items = scheduleData.value.map(schedule => ({
      date: schedule.date,
      workDay: schedule.available,
      timeSlots: schedule.available ? schedule.timeSlots : []
    }))

    const response = await updateWorkerSchedule({
      items: items
    })

    if (response.code === 200) {
      ElMessage.success('档期保存成功')
      loadSchedule() // 重新加载
    } else {
      ElMessage.error(response.message || '档期保存失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('档期保存失败', error)
      ElMessage.error('档期保存失败')
    }
  }
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

.calendar-section {
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

.calendar-container {
  margin-top: 16px;
}

.calendar-day {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 4px;
  min-height: 60px;
  justify-content: center;
}

.day-number {
  font-weight: 600;
  margin-bottom: 4px;
}

.day-status {
  font-size: 12px;
}

.available-day {
  background-color: #f6ffed;
  border: 1px solid #b7eb8f;
}

.busy-day {
  background-color: #fff2f0;
  border: 1px solid #ffadd2;
}

.action-buttons {
  display: flex;
  gap: 12px;
  justify-content: center;
  margin-top: 24px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

:deep(.el-calendar-table .el-calendar-day) {
  height: 80px;
  padding: 8px;
}

:deep(.el-calendar__body) {
  padding: 12px;
}

@media (max-width: 768px) {
  .header-actions {
    flex-direction: column;
    gap: 8px;
  }

  .action-buttons {
    flex-direction: column;
    align-items: center;
  }
}
</style>