<template>
  <div class="admin-profile">
    <el-row :gutter="20">
      <el-col :span="24">
        <el-tabs v-model="activeTab" type="card">
          <el-tab-pane label="查看资料" name="view">
            <AdminProfileView v-if="activeTab === 'view'" ref="profileViewRef" @edit="handleEdit" />
          </el-tab-pane>
          <el-tab-pane label="编辑资料" name="edit">
            <AdminProfileForm
              v-if="activeTab === 'edit'"
              @save-success="handleSaveSuccess"
              @cancel="handleCancel"
            />
          </el-tab-pane>
          <el-tab-pane label="修改密码" name="password">
            <AdminPasswordChange v-if="activeTab === 'password'" />
          </el-tab-pane>
        </el-tabs>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import AdminProfileView from '@/components/AdminProfileView.vue'
import AdminProfileForm from '@/components/AdminProfileForm.vue'
import AdminPasswordChange from '@/components/AdminPasswordChange.vue'

const activeTab = ref('view')
const profileViewRef = ref()

const handleEdit = () => {
  activeTab.value = 'edit'
}

const handleSaveSuccess = () => {
  activeTab.value = 'view'
  // 刷新查看页面
  if (profileViewRef.value) {
    profileViewRef.value.refresh()
  }
}

const handleCancel = () => {
  activeTab.value = 'view'
}
</script>

<style scoped>
.admin-profile {
  padding: 20px;
}
</style>
