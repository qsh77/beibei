<template>
  <div class="admin-services">
    <div class="page-header">
      <div class="header-content">
        <h2>服务管理</h2>
        <p>管理平台所有服务项目</p>
      </div>

      <div class="action-buttons">
        <el-button type="primary" @click="handleAddService">
          <el-icon><Plus /></el-icon>
          添加服务
        </el-button>
      </div>
    </div>

    <!-- 搜索和筛选 -->
    <div class="search-section">
      <div class="search-form">
        <el-form :model="searchForm" inline>
          <el-form-item>
            <el-input
              v-model="searchForm.keyword"
              placeholder="搜索服务名称"
              prefix-icon="Search"
              style="width: 200px"
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
    </div>

    <!-- 服务列表 -->
    <div class="table-section">
      <el-table
        v-loading="loading"
        :data="serviceList"
        style="width: 100%"
      >
        <el-table-column prop="name" label="服务名称" width="200" />


        <el-table-column prop="description" label="描述" min-width="250" show-overflow-tooltip />

        <el-table-column prop="basePrice" label="基础价格" width="120">
          <template #default="{ row }">
            ¥{{ formatAmount(row.basePrice) }}/{{ row.unit }}
          </template>
        </el-table-column>

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

        <el-table-column prop="hot" label="热门" width="80">
          <template #default="{ row }">
            <el-tag v-if="row.hot" type="danger" size="small">热门</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>

        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="createdAt" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>

        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              link
              @click="handleEditService(row)"
            >
              编辑
            </el-button>

            <el-button
              v-if="row.status === 1"
              type="danger"
              link
              @click="handleDisableService(row)"
            >
              禁用
            </el-button>
            <el-button
              v-else
              type="success"
              link
              @click="handleEnableService(row)"
            >
              启用
            </el-button>



            <el-button
              type="danger"
              link
              @click="handleDeleteService(row)"
            >
              删除
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
  getAdminServices,
  updateServiceStatus,

  deleteService
} from '@/api/admin'

// 响应式数据
const loading = ref(false)
const serviceList = ref([])

const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)

const searchForm = reactive({
  keyword: '',
  status: null
})


// 页面初始化
onMounted(() => {
  loadServices()
})

// 加载服务列表
const loadServices = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value,
      ...searchForm
    }

    console.log('发送请求参数:', params)
    const response = await getAdminServices(params)
    console.log('收到响应:', response)

    if (response.code === 200) {
      const pageResult = response.data
      serviceList.value = pageResult?.records || []
      total.value = pageResult?.total || 0
      console.log('服务列表数据:', serviceList.value)
    } else {
      throw new Error(response.message || '加载失败')
    }
  } catch (error) {
    console.error('加载服务列表失败:', error)
    console.error('错误详情:', error.response || error)
    ElMessage.error(error.message || '加载服务列表失败')
    serviceList.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}


// 搜索处理
const handleSearch = () => {
  currentPage.value = 1
  loadServices()
}

// 重置搜索
const handleReset = () => {
  Object.assign(searchForm, {
    keyword: '',
    status: null
  })
  handleSearch()
}

// 分页处理
const handlePageChange = (page) => {
  currentPage.value = page
  loadServices()
}

const handlePageSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  loadServices()
}

// 添加服务
const handleAddService = () => {
  ElMessage.info('添加服务功能待开发')
}

// 编辑服务
const handleEditService = (service) => {
  ElMessage.info('编辑服务功能待开发')
}

// 启用服务
const handleEnableService = async (service) => {
  try {
    await updateServiceStatus(service.id, 1)
    ElMessage.success('服务启用成功')
    loadServices()
  } catch (error) {
    console.error('启用服务失败:', error)
    ElMessage.error(error.message || '启用服务失败')
  }
}

// 禁用服务
const handleDisableService = async (service) => {
  try {
    await ElMessageBox.confirm(
      '确定要禁用此服务吗？禁用后用户将无法预订该服务。',
      '禁用服务',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await updateServiceStatus(service.id, 0)
    ElMessage.success('服务禁用成功')
    loadServices()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('禁用服务失败:', error)
      ElMessage.error(error.message || '禁用服务失败')
    }
  }
}



// 删除服务
const handleDeleteService = async (service) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除服务"${service.name}"吗？此操作不可恢复。`,
      '删除服务',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'error'
      }
    )

    await deleteService(service.id)
    ElMessage.success('服务删除成功')
    loadServices()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除服务失败:', error)
      ElMessage.error(error.message || '删除服务失败')
    }
  }
}

// 工具函数
const formatAmount = (amount) => {
  return amount ? Number(amount).toLocaleString() : '0'
}

const formatDate = (dateStr) => {
  return new Date(dateStr).toLocaleString('zh-CN')
}
</script>

<style scoped>
.admin-services {
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

.action-buttons {
  display: flex;
  gap: 8px;
}

.search-section {
  background: white;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.search-form {
  flex: 1;
}

.table-section {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }

  .action-buttons {
    justify-content: flex-start;
  }
}
</style>
