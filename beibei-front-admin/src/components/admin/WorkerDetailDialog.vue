<template>
  <el-dialog
    v-model="visible"
    title="阿姨详情"
    width="800px"
    :before-close="handleClose"
    custom-class="worker-detail-dialog"
  >
    <div v-loading="loading" class="worker-detail-content">
      <!-- 头部信息 -->
      <div class="header-section">
        <div class="header-left">
          <el-avatar :size="80" :src="workerInfo?.avatar" />
          <div class="header-info">
            <div class="name-row">
              <span class="worker-name">{{ workerInfo?.name || '未设置' }}</span>
              <el-tag :type="workerInfo?.status === 1 ? 'success' : 'danger'" size="small">
                {{ workerInfo?.status === 1 ? '正常' : '禁用' }}
              </el-tag>
            </div>
            <div class="worker-id">ID: {{ workerInfo?.id }}</div>
            <div class="worker-phone">
                <el-icon><Iphone /></el-icon>
                {{ workerInfo?.phone }}
            </div>
          </div>
        </div>
        <div class="header-right">
             <!-- 可以在这里放一些操作按钮 -->
        </div>
      </div>

      <el-divider />

      <!-- 数据概览 -->
      <div class="stats-cards">
        <div class="stat-card">
          <div class="stat-value">{{ workerInfo?.totalOrders || 0 }}</div>
          <div class="stat-label">总订单</div>
        </div>
        <div class="stat-card">
          <div class="stat-value">{{ formatCompletionRate(workerInfo) }}%</div>
          <div class="stat-label">完成率</div>
        </div>
        <div class="stat-card">
          <div class="stat-value highlight">{{ workerInfo?.score ? workerInfo.score.toFixed(1) : '5.0' }}</div>
          <div class="stat-label">系统评分</div>
        </div>
        <div class="stat-card">
          <div class="stat-value">{{ workerInfo?.totalReviews || 0 }}</div>
          <div class="stat-label">收到评价</div>
        </div>
      </div>

      <!-- 详细信息 -->
      <div class="info-section">
        <h4 class="section-title">基本信息</h4>
        <el-descriptions :column="2" border>
            <el-descriptions-item label="性别">{{ getGenderText(workerInfo?.gender) }}</el-descriptions-item>
            <el-descriptions-item label="年龄">{{ workerInfo?.age ? workerInfo.age + '岁' : '未知' }}</el-descriptions-item>
            <el-descriptions-item label="工作年限">{{ workerInfo?.years || 0 }}年</el-descriptions-item>
             <el-descriptions-item label="等级">
                 <el-rate
                    :model-value="workerInfo?.level || 1"
                    disabled
                    :max="5"
                    size="small"
                 />
             </el-descriptions-item>
             <el-descriptions-item label="注册时间">{{ formatDate(workerInfo?.createdAt) }}</el-descriptions-item>
             <el-descriptions-item label="邮箱">{{ workerInfo?.email || '未设置' }}</el-descriptions-item>
        </el-descriptions>
        
        <div class="bio-section" v-if="workerInfo?.bio">
            <p class="bio-label">个人简介：</p>
            <div class="bio-content">{{ workerInfo.bio }}</div>
        </div>
      </div>

      <!-- 历史评价 -->
      <div class="reviews-section">
        <h4 class="section-title">历史评价</h4>
        <div v-if="reviews.length > 0" class="review-list">
            <div v-for="review in reviews" :key="review.id" class="review-item">
                <div class="review-header">
                    <div class="review-user">
                        <span>订单 #{{ review.orderId }}</span>
                        <!-- 如果有用户信息可以显示，这里暂时只显示订单号 -->
                    </div>
                    <el-rate
                        :model-value="review.rating"
                        disabled
                        size="small"
                        show-score
                        text-color="#ff9900"
                    />
                </div>
                <div class="review-content">{{ review.content }}</div>
                <div class="review-footer">
                    <span class="review-time">{{ formatDate(review.createdAt) }}</span>
                </div>
                <div v-if="review.imgs" class="review-images">
                    <!-- 图片展示逻辑，假设imgs是JSON字符串 -->
                </div>
            </div>
        </div>
        <div v-else class="empty-reviews">
            <el-empty description="暂无历史评价" :image-size="80" />
        </div>
      </div>

    </div>
    
    <template #footer>
        <span class="dialog-footer">
            <el-button @click="handleClose">关闭</el-button>
        </span>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, watch } from 'vue'
import { Iphone } from '@element-plus/icons-vue'
import { getWorkerDetail, getWorkerReviews } from '@/api/admin'
import { ElMessage } from 'element-plus'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  workerId: {
    type: [Number, String],
    default: null
  }
})

const emit = defineEmits(['update:modelValue'])

const visible = ref(false)
const loading = ref(false)
const workerInfo = ref({})
const reviews = ref([])

// 监听props变化
watch(() => props.modelValue, (val) => {
    visible.value = val
    if (val && props.workerId) {
        fetchData()
    }
})

watch(visible, (val) => {
    emit('update:modelValue', val)
})

const fetchData = async () => {
    if (!props.workerId) return
    loading.value = true
    try {
        const [detailRes, reviewsRes] = await Promise.all([
            getWorkerDetail(props.workerId),
            getWorkerReviews(props.workerId)
        ])
        
        workerInfo.value = detailRes.data
        reviews.value = reviewsRes.data || []
    } catch (error) {
        // 部分接口可能失败，比如评价接口如果为空可能报错（取决于后端实现，通常应该返回空数组）
        // 这里分开处理可能更好，但Promise.all简单
        console.error(error)
        // 如果详情失败，还是提示一下
        if (!workerInfo.value.id) {
             ElMessage.error('获取阿姨详情失败')
        }
    } finally {
        loading.value = false
    }
}

const handleClose = () => {
    visible.value = false
    workerInfo.value = {}
    reviews.value = []
}

// 辅助函数
const getGenderText = (gender) => {
    const map = { 'M': '男', 'F': '女', 'U': '未知' }
    return map[gender] || '未设置'
}

const formatDate = (dateStr) => {
    if (!dateStr) return '-'
    return new Date(dateStr).toLocaleString('zh-CN')
}

const formatCompletionRate = (info) => {
    if (!info || !info.totalOrders) return 0
    return ((info.completedOrders || 0) / info.totalOrders * 100).toFixed(0)
}

</script>

<style scoped>
.header-section {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding-bottom: 20px;
}
.header-left {
    display: flex;
    gap: 20px;
    align-items: center;
}
.header-info {
    display: flex;
    flex-direction: column;
    gap: 8px;
}
.name-row {
    display: flex;
    align-items: center;
    gap: 12px;
}
.worker-name {
    font-size: 20px;
    font-weight: bold;
    color: #333;
}
.worker-id, .worker-phone {
    font-size: 13px;
    color: #666;
    display: flex;
    align-items: center;
    gap: 4px;
}

.stats-cards {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 16px;
    margin-bottom: 24px;
}
.stat-card {
    background: #f8f9fa;
    border-radius: 8px;
    padding: 16px;
    text-align: center;
}
.stat-value {
    font-size: 24px;
    font-weight: bold;
    color: #333;
    margin-bottom: 4px;
}
.stat-value.highlight {
    color: #ff9900;
}
.stat-label {
    font-size: 13px;
    color: #999;
}

.section-title {
    font-size: 16px;
    font-weight: bold;
    margin: 0 0 16px 0;
    padding-left: 10px;
    border-left: 4px solid #409eff;
}

.info-section {
    margin-bottom: 24px;
}
.bio-section {
    margin-top: 16px;
    background: #f8f9fa;
    padding: 12px;
    border-radius: 4px;
}
.bio-label {
    font-weight: bold;
    margin-bottom: 8px;
    color: #333;
}
.bio-content {
    color: #666;
    line-height: 1.6;
    font-size: 14px;
}

.reviews-section {
    margin-top: 24px;
}
.review-list {
    display: flex;
    flex-direction: column;
    gap: 16px;
    max-height: 400px;
    overflow-y: auto;
}
.review-item {
    border: 1px solid #ebeef5;
    border-radius: 8px;
    padding: 16px;
}
.review-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 8px;
}
.review-user {
    font-weight: bold;
    font-size: 14px;
}
.review-content {
    color: #333;
    font-size: 14px;
    margin-bottom: 8px;
    line-height: 1.5;
}
.review-footer {
    font-size: 12px;
    color: #999;
}
.empty-reviews {
    padding: 20px 0;
}
</style>
