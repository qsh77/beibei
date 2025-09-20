<template>
  <el-dialog
    v-model="visible"
    title="阿姨详情"
    width="800px"
    :before-close="handleClose"
  >
    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="6" animated />
    </div>

    <div v-else-if="workerDetail" class="worker-detail">
      <!-- 基本信息 -->
      <div class="detail-section">
        <h4 class="section-title">基本信息</h4>
        <el-row :gutter="20">
          <el-col :span="12">
            <div class="info-item">
              <span class="label">姓名：</span>
              <span class="value">{{ workerDetail.name || '未设置' }}</span>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="info-item">
              <span class="label">手机号：</span>
              <span class="value">{{ workerDetail.phone }}</span>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="info-item">
              <span class="label">性别：</span>
              <span class="value">{{ getGenderText(workerDetail.gender) }}</span>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="info-item">
              <span class="label">生日：</span>
              <span class="value">{{ workerDetail.birthday || '未设置' }}</span>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="info-item">
              <span class="label">邮箱：</span>
              <span class="value">{{ workerDetail.email || '未设置' }}</span>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="info-item">
              <span class="label">地址：</span>
              <span class="value">{{ workerDetail.address || '未设置' }}</span>
            </div>
          </el-col>
        </el-row>
      </div>

      <!-- 专业信息 -->
      <div class="detail-section">
        <h4 class="section-title">专业信息</h4>
        <el-row :gutter="20">
          <el-col :span="12">
            <div class="info-item">
              <span class="label">等级：</span>
              <span class="value">
                <el-rate
                  :model-value="workerDetail.level"
                  disabled
                  show-text
                  :texts="['一星', '二星', '三星', '四星', '五星']"
                />
              </span>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="info-item">
              <span class="label">工作年限：</span>
              <span class="value">{{ workerDetail.years }} 年</span>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="info-item">
              <span class="label">评分：</span>
              <span class="value">
                <el-rate
                  :model-value="workerDetail.score"
                  disabled
                  allow-half
                />
                <span class="score-text">{{ workerDetail.score }} 分</span>
              </span>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="info-item">
              <span class="label">状态：</span>
              <span class="value">
                <el-tag :type="workerDetail.status === 1 ? 'success' : 'danger'">
                  {{ workerDetail.status === 1 ? '启用' : '禁用' }}
                </el-tag>
              </span>
            </div>
          </el-col>
        </el-row>

        <div class="info-item full-width">
          <span class="label">个人简介：</span>
          <div class="bio-content">
            {{ workerDetail.bio || '暂无个人简介' }}
          </div>
        </div>
      </div>

      <!-- 统计信息 -->
      <div class="detail-section">
        <h4 class="section-title">工作统计</h4>
        <el-row :gutter="20">
          <el-col :span="8">
            <div class="stat-item">
              <div class="stat-value">{{ workerDetail.totalOrders || 0 }}</div>
              <div class="stat-label">总订单数</div>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="stat-item">
              <div class="stat-value">{{ workerDetail.completedOrders || 0 }}</div>
              <div class="stat-label">完成订单数</div>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="stat-item">
              <div class="stat-value">
                {{ workerDetail.totalOrders > 0 ?
                    Math.round((workerDetail.completedOrders / workerDetail.totalOrders) * 100) : 0 }}%
              </div>
              <div class="stat-label">完成率</div>
            </div>
          </el-col>
        </el-row>
      </div>

      <!-- 时间信息 -->
      <div class="detail-section">
        <h4 class="section-title">时间信息</h4>
        <el-row :gutter="20">
          <el-col :span="12">
            <div class="info-item">
              <span class="label">注册时间：</span>
              <span class="value">{{ formatDate(workerDetail.createdAt) }}</span>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="info-item">
              <span class="label">最后更新：</span>
              <span class="value">{{ formatDate(workerDetail.updatedAt) }}</span>
            </div>
          </el-col>
        </el-row>
      </div>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">关闭</el-button>
        <el-button type="primary" @click="handleEdit">编辑信息</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { getWorkerDetail } from '@/api/admin'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  workerId: {
    type: Number,
    default: null
  }
})

const emit = defineEmits(['update:modelValue', 'edit'])

const visible = ref(props.modelValue)
const loading = ref(false)
const workerDetail = ref(null)

// 监听 modelValue 变化
watch(() => props.modelValue, (newVal) => {
  visible.value = newVal
  if (newVal && props.workerId) {
    fetchWorkerDetail()
  }
})

// 监听 visible 变化
watch(visible, (newVal) => {
  emit('update:modelValue', newVal)
})

// 获取阿姨详情
const fetchWorkerDetail = async () => {
  if (!props.workerId) return

  loading.value = true
  try {
    const response = await getWorkerDetail(props.workerId)
    workerDetail.value = response.data
  } catch (error) {
    ElMessage.error('获取阿姨详情失败：' + (error.message || '请求失败'))
  } finally {
    loading.value = false
  }
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return '未知'
  return new Date(dateStr).toLocaleString('zh-CN')
}

// 获取性别文本
const getGenderText = (gender) => {
  const genderMap = {
    'M': '男',
    'F': '女',
    'U': '未设置'
  }
  return genderMap[gender] || '未知'
}

// 编辑
const handleEdit = () => {
  emit('edit', workerDetail.value)
  handleClose()
}

// 关闭对话框
const handleClose = () => {
  visible.value = false
  workerDetail.value = null
}
</script>

<style scoped>
.loading-container {
  padding: 20px;
}

.worker-detail {
  max-height: 600px;
  overflow-y: auto;
}

.detail-section {
  margin-bottom: 24px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
}

.detail-section:last-child {
  margin-bottom: 0;
}

.section-title {
  margin: 0 0 16px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  border-bottom: 2px solid #409eff;
  padding-bottom: 8px;
}

.info-item {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
  min-height: 32px;
}

.info-item.full-width {
  flex-direction: column;
  align-items: flex-start;
}

.label {
  font-weight: 500;
  color: #606266;
  width: 80px;
  flex-shrink: 0;
}

.value {
  color: #303133;
  flex: 1;
}

.bio-content {
  margin-top: 8px;
  padding: 12px;
  background: white;
  border-radius: 4px;
  line-height: 1.6;
  min-height: 60px;
  white-space: pre-wrap;
}

.score-text {
  margin-left: 8px;
  color: #606266;
  font-size: 14px;
}

.stat-item {
  text-align: center;
  padding: 16px;
  background: white;
  border-radius: 8px;
  border: 1px solid #e4e7ed;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #409eff;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.dialog-footer {
  text-align: right;
}

:deep(.el-rate) {
  align-items: center;
}

:deep(.el-rate__text) {
  color: #409eff;
  font-weight: 500;
}
</style>