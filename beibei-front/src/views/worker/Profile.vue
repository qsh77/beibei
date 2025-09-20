<template>
  <div class="worker-profile">
    <div class="page-header">
      <h2>个人资料</h2>
      <p>完善您的个人信息，提升接单成功率</p>
    </div>

    <div class="profile-content">
      <!-- 左侧个人信息卡片 -->
      <div class="profile-card-section">
        <el-card class="profile-card">
          <div class="profile-avatar">
            <el-upload
              class="avatar-uploader"
              :action="uploadUrl"
              :show-file-list="false"
              :before-upload="beforeUpload"
              :on-success="handleAvatarSuccess"
            >
              <img v-if="workerInfo.avatar" :src="workerInfo.avatar" class="avatar" />
              <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
            </el-upload>
          </div>

          <div class="profile-basic">
            <h3>{{ workerInfo.name }}</h3>
            <p class="profile-title">{{ workerInfo.title || '家政服务员' }}</p>
            <div class="profile-stats">
              <div class="stat-item">
                <div class="stat-number">{{ workerInfo.rating }}</div>
                <div class="stat-label">评分</div>
              </div>
              <div class="stat-item">
                <div class="stat-number">{{ workerInfo.orderCount }}</div>
                <div class="stat-label">完成订单</div>
              </div>
              <div class="stat-item">
                <div class="stat-number">{{ workerInfo.experience }}</div>
                <div class="stat-label">工作年限</div>
              </div>
            </div>

            <div class="profile-tags">
              <el-tag
                v-for="skill in workerInfo.skills"
                :key="skill"
                size="small"
                class="skill-tag"
              >
                {{ skill }}
              </el-tag>
            </div>
          </div>
        </el-card>

        <!-- 服务评价 -->
        <el-card class="reviews-card">
          <template #header>
            <span>最新评价</span>
          </template>

          <div v-if="reviews.length === 0" class="no-reviews">
            <el-empty description="暂无评价" />
          </div>

          <div v-else class="reviews-list">
            <div v-for="review in reviews" :key="review.id" class="review-item">
              <div class="review-header">
                <span class="customer-name">{{ review.customerName }}</span>
                <el-rate v-model="review.rating" disabled size="small" />
              </div>
              <p class="review-content">{{ review.content }}</p>
              <div class="review-date">{{ formatDate(review.createdDate) }}</div>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 右侧信息编辑区域 -->
      <div class="profile-form-section">
        <!-- 基本信息 -->
        <el-card class="form-card">
          <template #header>
            <span>基本信息</span>
          </template>

          <el-form
            ref="basicFormRef"
            :model="basicForm"
            :rules="basicRules"
            label-width="100px"
          >
            <el-form-item label="姓名" prop="name">
              <el-input v-model="basicForm.name" />
            </el-form-item>

            <el-form-item label="手机号" prop="phone">
              <el-input v-model="basicForm.phone" disabled />
            </el-form-item>

            <el-form-item label="身份证号" prop="idCard">
              <el-input v-model="basicForm.idCard" />
            </el-form-item>

            <el-form-item label="年龄" prop="age">
              <el-input-number v-model="basicForm.age" :min="18" :max="65" />
            </el-form-item>

            <el-form-item label="地址" prop="address">
              <el-input v-model="basicForm.address" type="textarea" :rows="2" />
            </el-form-item>

            <el-form-item label="个人简介" prop="description">
              <el-input
                v-model="basicForm.description"
                type="textarea"
                :rows="4"
                placeholder="请简单介绍您的工作经验和服务特长"
              />
            </el-form-item>

            <el-form-item>
              <el-button type="primary" @click="saveBasicInfo">保存基本信息</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <!-- 工作信息 -->
        <el-card class="form-card">
          <template #header>
            <span>工作信息</span>
          </template>

          <el-form
            ref="workFormRef"
            :model="workForm"
            label-width="100px"
          >
            <el-form-item label="工作年限">
              <el-input-number v-model="workForm.experience" :min="0" :max="50" />
              <span class="form-suffix">年</span>
            </el-form-item>

            <el-form-item label="服务类型">
              <el-checkbox-group v-model="workForm.serviceTypes">
                <el-checkbox label="家庭保洁">家庭保洁</el-checkbox>
                <el-checkbox label="月嫂服务">月嫂服务</el-checkbox>
                <el-checkbox label="育儿嫂">育儿嫂</el-checkbox>
                <el-checkbox label="老人陪护">老人陪护</el-checkbox>
                <el-checkbox label="钟点工">钟点工</el-checkbox>
                <el-checkbox label="做饭阿姨">做饭阿姨</el-checkbox>
              </el-checkbox-group>
            </el-form-item>

            <el-form-item label="技能特长">
              <el-select
                v-model="workForm.skills"
                multiple
                filterable
                allow-create
                placeholder="选择或输入技能特长"
                style="width: 100%"
              >
                <el-option label="普通话标准" value="普通话标准" />
                <el-option label="会做家常菜" value="会做家常菜" />
                <el-option label="有育儿经验" value="有育儿经验" />
                <el-option label="会开车" value="会开车" />
                <el-option label="有护理经验" value="有护理经验" />
                <el-option label="英语口语" value="英语口语" />
              </el-select>
            </el-form-item>

            <el-form-item label="工作时间">
              <el-checkbox-group v-model="workForm.availableTime">
                <el-checkbox label="周一到周五">周一到周五</el-checkbox>
                <el-checkbox label="周末">周末</el-checkbox>
                <el-checkbox label="白班">白班</el-checkbox>
                <el-checkbox label="夜班">夜班</el-checkbox>
                <el-checkbox label="住家">住家</el-checkbox>
              </el-checkbox-group>
            </el-form-item>

            <el-form-item label="期望薪资">
              <el-input-number v-model="workForm.expectedSalary" :min="0" />
              <span class="form-suffix">元/月</span>
            </el-form-item>

            <el-form-item>
              <el-button type="primary" @click="saveWorkInfo">保存工作信息</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <!-- 认证信息 -->
        <el-card class="form-card">
          <template #header>
            <span>认证信息</span>
          </template>

          <div class="cert-list">
            <div class="cert-item">
              <div class="cert-info">
                <span class="cert-name">身份认证</span>
                <el-tag :type="workerInfo.idVerified ? 'success' : 'warning'" size="small">
                  {{ workerInfo.idVerified ? '已认证' : '未认证' }}
                </el-tag>
              </div>
              <el-button v-if="!workerInfo.idVerified" size="small" type="primary">
                去认证
              </el-button>
            </div>

            <div class="cert-item">
              <div class="cert-info">
                <span class="cert-name">健康证</span>
                <el-tag :type="workerInfo.healthCertified ? 'success' : 'warning'" size="small">
                  {{ workerInfo.healthCertified ? '已认证' : '未认证' }}
                </el-tag>
              </div>
              <el-button v-if="!workerInfo.healthCertified" size="small" type="primary">
                去认证
              </el-button>
            </div>

            <div class="cert-item">
              <div class="cert-info">
                <span class="cert-name">技能证书</span>
                <el-tag :type="workerInfo.skillCertified ? 'success' : 'warning'" size="small">
                  {{ workerInfo.skillCertified ? '已认证' : '未认证' }}
                </el-tag>
              </div>
              <el-button v-if="!workerInfo.skillCertified" size="small" type="primary">
                去认证
              </el-button>
            </div>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

// 响应式数据
const uploadUrl = '/api/upload/avatar'

// 阿姨基本信息
const workerInfo = ref({
  id: 1,
  name: '张阿姨',
  title: '资深家政服务员',
  avatar: '',
  rating: 4.8,
  orderCount: 156,
  experience: 5,
  skills: ['家庭保洁', '月嫂服务', '会做家常菜'],
  idVerified: true,
  healthCertified: false,
  skillCertified: true
})

// 基本信息表单
const basicForm = ref({
  name: '张阿姨',
  phone: '13888888888',
  idCard: '330***********1234',
  age: 45,
  address: '浙江省杭州市西湖区',
  description: '有5年家政服务经验，擅长家庭保洁和月嫂服务，工作认真负责，深受客户好评。'
})

const basicRules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  idCard: [{ required: true, message: '请输入身份证号', trigger: 'blur' }],
  age: [{ required: true, message: '请输入年龄', trigger: 'blur' }]
}

// 工作信息表单
const workForm = ref({
  experience: 5,
  serviceTypes: ['家庭保洁', '月嫂服务'],
  skills: ['普通话标准', '会做家常菜', '有育儿经验'],
  availableTime: ['周一到周五', '白班'],
  expectedSalary: 8000
})

// 评价列表
const reviews = ref([
  {
    id: 1,
    customerName: '王女士',
    rating: 5,
    content: '张阿姨服务很好，工作认真负责，家里打扫得很干净，会继续预约的。',
    createdDate: '2024-12-18'
  },
  {
    id: 2,
    customerName: '李先生',
    content: '专业的月嫂服务，经验丰富，照顾产妇和宝宝都很用心。',
    rating: 5,
    createdDate: '2024-12-15'
  },
  {
    id: 3,
    customerName: '陈女士',
    content: '第一次请阿姨，张阿姨很耐心，教了我很多育儿知识，非常满意！',
    rating: 4,
    createdDate: '2024-12-10'
  }
])

const basicFormRef = ref()
const workFormRef = ref()

// 页面初始化
onMounted(() => {
  loadWorkerProfile()
})

// 数据加载
const loadWorkerProfile = async () => {
  // 这里应该从后端加载阿姨的个人信息
  // 现在使用模拟数据
}

// 事件处理
const beforeUpload = (file) => {
  const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isJPG) {
    ElMessage.error('上传头像只能是 JPG/PNG 格式!')
  }
  if (!isLt2M) {
    ElMessage.error('上传头像大小不能超过 2MB!')
  }
  return isJPG && isLt2M
}

const handleAvatarSuccess = (response) => {
  workerInfo.value.avatar = response.data.url
  ElMessage.success('头像上传成功')
}

const saveBasicInfo = async () => {
  try {
    await basicFormRef.value.validate()

    // 这里应该调用后端API保存基本信息
    ElMessage.success('基本信息保存成功')
  } catch (error) {
    ElMessage.error('请填写完整的基本信息')
  }
}

const saveWorkInfo = () => {
  // 这里应该调用后端API保存工作信息
  workerInfo.value.skills = [...workForm.value.skills]
  ElMessage.success('工作信息保存成功')
}

// 工具函数
const formatDate = (dateStr) => {
  return new Date(dateStr).toLocaleDateString('zh-CN')
}
</script>

<style scoped>
.worker-profile {
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

.profile-content {
  display: flex;
  gap: 24px;
}

.profile-card-section {
  flex: 0 0 320px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.profile-form-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 个人信息卡片 */
.profile-card {
  text-align: center;
}

.profile-avatar {
  margin-bottom: 20px;
}

.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 50%;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: all 0.3s;
  width: 120px;
  height: 120px;
}

.avatar-uploader .el-upload:hover {
  border-color: #1890ff;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c8c8c;
  width: 120px;
  height: 120px;
  text-align: center;
  line-height: 120px;
}

.avatar {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  display: block;
  object-fit: cover;
}

.profile-basic h3 {
  margin: 0 0 8px 0;
  font-size: 20px;
  color: #262626;
}

.profile-title {
  margin: 0 0 16px 0;
  color: #8c8c8c;
  font-size: 14px;
}

.profile-stats {
  display: flex;
  justify-content: center;
  gap: 24px;
  margin-bottom: 16px;
  padding: 16px 0;
  border-top: 1px solid #f0f0f0;
  border-bottom: 1px solid #f0f0f0;
}

.stat-item {
  text-align: center;
}

.stat-number {
  font-size: 18px;
  font-weight: 600;
  color: #1890ff;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 12px;
  color: #8c8c8c;
}

.profile-tags {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 8px;
}

.skill-tag {
  margin: 0;
}

/* 评价卡片 */
.reviews-card {
  max-height: 400px;
  overflow-y: auto;
}

.no-reviews {
  padding: 20px;
}

.reviews-list {
  max-height: 300px;
  overflow-y: auto;
}

.review-item {
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.review-item:last-child {
  border-bottom: none;
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.customer-name {
  font-weight: 600;
  color: #262626;
}

.review-content {
  margin: 0 0 8px 0;
  color: #666;
  font-size: 14px;
  line-height: 1.5;
}

.review-date {
  font-size: 12px;
  color: #8c8c8c;
}

/* 表单样式 */
.form-card {
  margin-bottom: 16px;
}

.form-suffix {
  margin-left: 8px;
  color: #8c8c8c;
  font-size: 14px;
}

/* 认证信息 */
.cert-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.cert-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #fafafa;
  border-radius: 8px;
}

.cert-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.cert-name {
  font-weight: 500;
  color: #262626;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .profile-content {
    flex-direction: column;
  }

  .profile-card-section {
    flex: none;
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 16px;
  }
}

@media (max-width: 768px) {
  .worker-profile {
    padding: 16px;
  }

  .profile-content {
    flex-direction: column;
  }

  .profile-card-section {
    display: flex;
    flex-direction: column;
  }

  .profile-stats {
    gap: 16px;
  }

  .cert-item {
    flex-direction: column;
    align-items: stretch;
    gap: 8px;
  }

  .cert-info {
    justify-content: space-between;
  }
}
</style>