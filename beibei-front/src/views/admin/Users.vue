<template>
  <div class="admin-users">
    <div class="page-header">
      <div class="header-content">
        <h2>用户管理</h2>
        <p>管理平台所有用户账号</p>
      </div>

      <!-- 统计卡片 -->
      <div class="stats-row">
        <div class="stat-item">
          <span class="stat-number">{{ userStats.totalUsers || 0 }}</span>
          <span class="stat-label">总用户</span>
        </div>
        <div class="stat-item">
          <span class="stat-number">{{ userStats.normalUsers || 0 }}</span>
          <span class="stat-label">普通用户</span>
        </div>
        <div class="stat-item">
          <span class="stat-number">{{ userStats.workers || 0 }}</span>
          <span class="stat-label">阿姨</span>
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
              placeholder="搜索手机号或用户名"
              prefix-icon="Search"
              style="width: 250px"
              @keyup.enter="handleSearch"
              clearable
            />
          </el-form-item>

          <el-form-item>
            <el-select
              v-model="searchForm.role"
              placeholder="角色筛选"
              style="width: 120px"
              clearable
            >
              <el-option label="用户" value="USER" />
              <el-option label="阿姨" value="WORKER" />
              <el-option label="管理员" value="ADMIN" />
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
    </div>

    <!-- 用户列表 -->
    <div class="table-section">
      <el-table
        v-loading="loading"
        :data="userList"
        @selection-change="handleSelectionChange"
        style="width: 100%"
      >
        <el-table-column type="selection" width="50" />

        <el-table-column label="用户信息" min-width="200">
          <template #default="{ row }">
            <div class="user-info">
              <el-avatar :size="40" :src="row.avatar" />
              <div class="user-details">
                <div class="user-name">{{ row.name || '未设置' }}</div>
                <div class="user-phone">{{ row.phone }}</div>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="role" label="角色" width="100">
          <template #default="{ row }">
            <el-tag :type="getRoleTagType(row.role)">
              {{ getRoleText(row.role) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="totalOrders" label="订单数" width="100" />

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
              v-if="row.status === 1"
              type="danger"
              link
              @click="handleDisableUser(row)"
            >
              禁用
            </el-button>
            <el-button
              v-else
              type="success"
              link
              @click="handleEnableUser(row)"
            >
              启用
            </el-button>

            <el-dropdown @command="(command) => handleMoreAction(command, row)">
              <el-button type="primary" link>
                更多 <el-icon><ArrowDown /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="resetPassword">重置密码</el-dropdown-item>
                  <el-dropdown-item command="changeRole" v-if="row.role !== 'ADMIN'">
                    变更角色
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
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

    <!-- 用户详情对话框 -->
    <UserDetailDialog
      v-model:visible="detailDialogVisible"
      :user-id="selectedUserId"
      @refresh="handleSearch"
    />

    <!-- 角色变更对话框 -->
    <RoleChangeDialog
      v-model:visible="roleDialogVisible"
      :user="selectedUser"
      @success="handleSearch"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Search,
  Refresh,
  Download,
  ArrowDown
} from '@element-plus/icons-vue'
import UserDetailDialog from '@/components/admin/UserDetailDialog.vue'
import RoleChangeDialog from '@/components/admin/RoleChangeDialog.vue'
import { getAdminUsers, updateUserStatus, batchUpdateUserStatus, getUserStats } from '@/api/admin'

// 响应式数据
const loading = ref(false)
const userList = ref([])
const selectedUsers = ref([])
const userStats = ref({})

const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)

const searchForm = reactive({
  keyword: '',
  role: '',
  status: null
})

const detailDialogVisible = ref(false)
const roleDialogVisible = ref(false)
const selectedUserId = ref(null)
const selectedUser = ref(null)

// 页面初始化
onMounted(() => {
  loadUsers()
  loadUserStats()
})

// 加载用户列表
const loadUsers = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      ...searchForm
    }
    const response = await getAdminUsers(params)

    userList.value = response.data.records
    total.value = response.data.total
  } catch (error) {
    ElMessage.error('加载用户列表失败')
  } finally {
    loading.value = false
  }
}

// 加载用户统计
const loadUserStats = async () => {
  try {
    const response = await getUserStats()
    userStats.value = response.data
  } catch (error) {
    console.error('加载用户统计失败', error)
  }
}

// 搜索处理
const handleSearch = () => {
  currentPage.value = 1
  loadUsers()
}

// 重置搜索
const handleReset = () => {
  Object.assign(searchForm, {
    keyword: '',
    role: '',
    status: null
  })
  handleSearch()
}

// 分页处理
const handlePageChange = (page) => {
  currentPage.value = page
  loadUsers()
}

const handlePageSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  loadUsers()
}

// 选择变更
const handleSelectionChange = (selection) => {
  selectedUsers.value = selection
}

// 查看用户详情
const handleViewDetail = (user) => {
  selectedUserId.value = user.id
  detailDialogVisible.value = true
}

// 启用用户
const handleEnableUser = async (user) => {
  try {
    await updateUserStatus(user.id, { status: 1, reason: '管理员手动启用' })
    ElMessage.success('用户启用成功')
    loadUsers()
  } catch (error) {
    ElMessage.error(error.message || '启用失败')
  }
}

// 禁用用户
const handleDisableUser = async (user) => {
  try {
    await ElMessageBox.confirm(`确定要禁用用户 ${user.phone} 吗？`, '确认操作', {
      type: 'warning'
    })

    await updateUserStatus(user.id, { status: 0, reason: '管理员手动禁用' })
    ElMessage.success('用户禁用成功')
    loadUsers()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '禁用失败')
    }
  }
}

// 批量启用
const handleBatchEnable = async () => {
  try {
    const userIds = selectedUsers.value.map(user => user.id)
    await batchUpdateUserStatus(userIds, 1)
    ElMessage.success('批量启用成功')
    loadUsers()
  } catch (error) {
    ElMessage.error(error.message || '批量启用失败')
  }
}

// 批量禁用
const handleBatchDisable = async () => {
  try {
    await ElMessageBox.confirm(`确定要禁用选中的 ${selectedUsers.value.length} 个用户吗？`, '确认操作', {
      type: 'warning'
    })

    const userIds = selectedUsers.value.map(user => user.id)
    await batchUpdateUserStatus(userIds, 0)
    ElMessage.success('批量禁用成功')
    loadUsers()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '批量禁用失败')
    }
  }
}

// 导出用户
const handleExport = () => {
  ElMessage.info('导出功能开发中...')
}

// 更多操作
const handleMoreAction = async (command, user) => {
  switch (command) {
    case 'resetPassword':
      await handleResetPassword(user)
      break
    case 'changeRole':
      selectedUser.value = user
      roleDialogVisible.value = true
      break
  }
}

// 重置密码
const handleResetPassword = async (user) => {
  try {
    await ElMessageBox.confirm(`确定要重置用户 ${user.phone} 的密码吗？`, '确认操作', {
      type: 'warning'
    })

    // TODO: 调用重置密码API
    ElMessage.success('密码重置成功，新密码为：123456')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('密码重置失败')
    }
  }
}

// 工具函数
const formatDate = (dateStr) => {
  return new Date(dateStr).toLocaleString('zh-CN')
}

const getRoleText = (role) => {
  const roleMap = {
    'USER': '用户',
    'WORKER': '阿姨',
    'ADMIN': '管理员'
  }
  return roleMap[role] || '未知'
}

const getRoleTagType = (role) => {
  const typeMap = {
    'USER': '',
    'WORKER': 'success',
    'ADMIN': 'danger'
  }
  return typeMap[role] || ''
}
</script>

<style scoped>
.admin-users {
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
  color: #1890ff;
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

.user-info {
  display: flex;
  align-items: center;
}

.user-details {
  margin-left: 12px;
}

.user-name {
  font-weight: 500;
  color: #262626;
  margin-bottom: 4px;
}

.user-phone {
  font-size: 12px;
  color: #8c8c8c;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

@media (max-width: 768px) {
  .stats-row {
    flex-wrap: wrap;
    gap: 16px;
  }

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