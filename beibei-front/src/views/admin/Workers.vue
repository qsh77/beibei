<template>
  <div class="admin-workers">
    <div class="page-header">
      <div class="header-content">
        <h2>阿姨管理</h2>
        <p>管理平台所有阿姨账号和认证信息</p>
      </div>

      <!-- 统计卡片 -->
      <div class="stats-row">
        <div class="stat-item">
          <span class="stat-number">{{ workerStats.totalWorkers || 0 }}</span>
          <span class="stat-label">总阿姨数</span>
        </div>
        <div class="stat-item">
          <span class="stat-number">{{ workerStats.activeWorkers || 0 }}</span>
          <span class="stat-label">正常</span>
        </div>
        <div class="stat-item">
          <span class="stat-number">{{ workerStats.level5Count || 0 }}</span>
          <span class="stat-label">五星阿姨</span>
        </div>
        <div class="stat-item">
          <span class="stat-number">{{ workerStats.avgScore ? workerStats.avgScore.toFixed(1) : '0.0' }}</span>
          <span class="stat-label">平均评分</span>
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
              placeholder="搜索阿姨姓名或手机号"
              prefix-icon="Search"
              style="width: 250px"
              @keyup.enter="handleSearch"
              clearable
            />
          </el-form-item>

          <el-form-item>
            <el-select
              v-model="searchForm.level"
              placeholder="等级筛选"
              style="width: 120px"
              clearable
            >
              <el-option label="一星" :value="1" />
              <el-option label="二星" :value="2" />
              <el-option label="三星" :value="3" />
              <el-option label="四星" :value="4" />
              <el-option label="五星" :value="5" />
            </el-select>
          </el-form-item>

          <el-form-item>
            <el-select
              v-model="searchForm.status"
              placeholder="状态筛选"
              style="width: 120px"
              clearable
            >
              <el-option label="正常" :value="1" />
              <el-option label="禁用" :value="0" />
            </el-select>
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

      <div class="action-buttons">
        <el-button type="primary" @click="handleAddWorker">
          <el-icon><Plus /></el-icon>
          添加阿姨
        </el-button>
      </div>
    </div>

    <!-- 阿姨列表 -->
    <div class="table-section">
      <el-table
        v-loading="loading"
        :data="workerList"
        style="width: 100%"
      >
        <el-table-column label="阿姨信息" min-width="200">
          <template #default="{ row }">
            <div class="worker-info">
              <el-avatar :size="50" :src="row.avatar" />
              <div class="worker-details">
                <div class="worker-name">{{ row.name || '未设置' }}</div>
                <div class="worker-phone">{{ row.phone }}</div>
                <div class="worker-experience">{{ row.years || 0 }}年经验</div>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="level" label="等级" width="100">
          <template #default="{ row }">
            <el-rate
              v-model="row.level"
              disabled
              show-score
              text-color="#ff9900"
              score-template="{value}星"
            />
          </template>
        </el-table-column>

        <el-table-column prop="score" label="评分" width="100">
          <template #default="{ row }">
            <span class="score">{{ row.score || 5.0 }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="bio" label="简介" min-width="200" show-overflow-tooltip />

        <el-table-column prop="createdAt" label="注册时间" width="180">
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
              type="warning"
              link
              @click="handleEditWorker(row)"
            >
              编辑
            </el-button>

            <el-button
              v-if="row.status === 1"
              type="danger"
              link
              @click="handleDisableWorker(row)"
            >
              禁用
            </el-button>
            <el-button
              v-else
              type="success"
              link
              @click="handleEnableWorker(row)"
            >
              启用
            </el-button>
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

    <!-- 添加阿姨对话框 -->
    <AddWorkerDialog
      v-model="addWorkerDialogVisible"
      @success="handleAddWorkerSuccess"
    />

    <WorkerDetailDialog
      v-model="workerDetailDialogVisible"
      :worker-id="selectedWorkerId"
      @edit="handleEditFromDetail"
    />

    <WorkerEditDialog
      v-model="workerEditDialogVisible"
      :worker-info="selectedWorkerInfo"
      @success="handleEditWorkerSuccess"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Search,
  Refresh,
  Plus
} from '@element-plus/icons-vue'
import {
  getAdminWorkers,
  getWorkerDetail,
  updateWorkerStatus,
  batchUpdateWorkerStatus,
  getWorkerStats,
  searchWorkers
} from '@/api/admin'
import AddWorkerDialog from '@/components/admin/AddWorkerDialog.vue'
import WorkerDetailDialog from '@/components/admin/WorkerDetailDialog.vue'
import WorkerEditDialog from '@/components/admin/WorkerEditDialog.vue'

// 响应式数据
const loading = ref(false)
const workerList = ref([])
const workerStats = ref({})

const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)

const searchForm = reactive({
  keyword: '',
  level: null,
  status: null
})

const addWorkerDialogVisible = ref(false)
const workerDetailDialogVisible = ref(false)
const workerEditDialogVisible = ref(false)
const selectedWorkerId = ref(null)
const selectedWorkerInfo = ref(null)

// 页面初始化
onMounted(() => {
  loadWorkers()
  loadWorkerStats()
})

// 加载阿姨统计
const loadWorkerStats = async () => {
  try {
    const response = await getWorkerStats()
    workerStats.value = response.data
  } catch (error) {
    console.error('加载阿姨统计失败', error)
  }
}

// 加载阿姨列表
const loadWorkers = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      ...searchForm
    }
    const response = await getAdminWorkers(params)

    workerList.value = response.data.records
    total.value = response.data.total
  } catch (error) {
    ElMessage.error('加载阿姨列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索处理
const handleSearch = () => {
  currentPage.value = 1
  loadWorkers()
}

// 重置搜索
const handleReset = () => {
  Object.assign(searchForm, {
    keyword: '',
    level: null,
    status: null
  })
  handleSearch()
}

// 分页处理
const handlePageChange = (page) => {
  currentPage.value = page
  loadWorkers()
}

const handlePageSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  loadWorkers()
}


// 启用阿姨
const handleEnableWorker = async (worker) => {
  try {
    await updateWorkerStatus(worker.id, 1)
    ElMessage.success('阿姨启用成功')
    loadWorkers()
    loadWorkerStats()
  } catch (error) {
    ElMessage.error(error.message || '启用失败')
  }
}

// 禁用阿姨
const handleDisableWorker = async (worker) => {
  try {
    await ElMessageBox.confirm(`确定要禁用阿姨 ${worker.name || worker.phone} 吗？`, '确认操作', {
      type: 'warning'
    })

    await updateWorkerStatus(worker.id, 0)
    ElMessage.success('阿姨禁用成功')
    loadWorkers()
    loadWorkerStats()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '禁用失败')
    }
  }
}

// 添加阿姨
const handleAddWorker = () => {
  addWorkerDialogVisible.value = true
}

// 添加阿姨成功
const handleAddWorkerSuccess = () => {
  loadWorkers()
  loadWorkerStats()
}

// 查看阿姨详情
const handleViewDetail = (worker) => {
  selectedWorkerId.value = worker.id
  workerDetailDialogVisible.value = true
}

// 编辑阿姨
const handleEditWorker = (worker) => {
  selectedWorkerInfo.value = worker
  workerEditDialogVisible.value = true
}

// 编辑成功
const handleEditWorkerSuccess = () => {
  loadWorkers()
  loadWorkerStats()
}

// 从详情对话框跳转到编辑
const handleEditFromDetail = (workerInfo) => {
  selectedWorkerInfo.value = workerInfo
  workerEditDialogVisible.value = true
}

// 工具函数
const formatDate = (dateStr) => {
  return new Date(dateStr).toLocaleString('zh-CN')
}
</script>

<style scoped>
.admin-workers {
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
  color: #52c41a;
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

.worker-info {
  display: flex;
  align-items: center;
}

.worker-details {
  margin-left: 12px;
}

.worker-name {
  font-weight: 500;
  color: #262626;
  margin-bottom: 4px;
}

.worker-phone {
  font-size: 12px;
  color: #8c8c8c;
  margin-bottom: 2px;
}

.worker-experience {
  font-size: 12px;
  color: #1890ff;
}

.score {
  font-weight: 600;
  color: #fa8c16;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

@media (max-width: 768px) {
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