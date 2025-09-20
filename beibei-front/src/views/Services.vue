<template>
  <div class="services-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <h1>家政服务</h1>
      <p>专业的家政服务，让生活更美好</p>
    </div>

    <!-- 搜索栏 -->
    <div class="search-section">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索服务..."
        size="large"
        clearable
        @input="handleSearch"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
    </div>

    <!-- 服务类目 -->
    <div class="categories-section" v-if="categories.length > 0">
      <h2>服务分类</h2>
      <div class="categories-grid">
        <div
          v-for="category in categories"
          :key="category.id"
          class="category-card"
          :class="{ active: selectedCategory === category.id }"
          @click="selectCategory(category.id)"
        >
          <div class="category-icon">
            <el-icon><House /></el-icon>
          </div>
          <span>{{ category.name }}</span>
        </div>
      </div>
    </div>

    <!-- 热门服务 -->
    <div class="hot-services-section" v-if="hotServices.length > 0">
      <h2>热门服务</h2>
      <div class="services-grid">
        <div
          v-for="service in hotServices"
          :key="service.id"
          class="service-card"
          @click="viewServiceDetail(service)"
        >
          <div class="service-image">
            <el-icon><Star /></el-icon>
          </div>
          <div class="service-info">
            <h3>{{ service.name }}</h3>
            <p>{{ service.description }}</p>
            <div class="service-price">
              <span class="price">¥{{ service.basePrice }}</span>
              <span class="unit">/{{ service.unit }}</span>
            </div>
            <div class="service-rating" v-if="service.rating">
              <el-rate
                v-model="service.rating"
                disabled
                show-score
                text-color="#ff9900"
              />
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 服务列表 -->
    <div class="services-section">
      <h2>{{ selectedCategory ? '分类服务' : '全部服务' }}</h2>
      <div class="services-grid" v-if="services.length > 0">
        <div
          v-for="service in services"
          :key="service.id"
          class="service-card"
          @click="viewServiceDetail(service)"
        >
          <div class="service-image">
            <el-icon><Tools /></el-icon>
          </div>
          <div class="service-info">
            <h3>{{ service.name }}</h3>
            <p>{{ service.description }}</p>
            <div class="service-price">
              <span class="price">¥{{ service.basePrice }}</span>
              <span class="unit">/{{ service.unit }}</span>
            </div>
            <div class="service-rating" v-if="service.rating">
              <el-rate
                v-model="service.rating"
                disabled
                show-score
                text-color="#ff9900"
              />
            </div>
            <div class="service-actions">
              <el-button type="primary" @click.stop="bookService(service)">
                立即预约
              </el-button>
            </div>
          </div>
        </div>
      </div>
      <div v-else class="empty-state">
        <el-empty description="暂无服务" />
      </div>
    </div>

    <!-- 服务详情对话框 -->
    <el-dialog
      v-model="serviceDetailVisible"
      :title="selectedService?.name"
      width="600px"
    >
      <div v-if="selectedService" class="service-detail">
        <div class="service-image-large">
          <el-icon><Tools /></el-icon>
        </div>
        <div class="service-detail-info">
          <h3>{{ selectedService.name }}</h3>
          <p class="description">{{ selectedService.description }}</p>
          <div class="price-info">
            <span class="price">¥{{ selectedService.basePrice }}</span>
            <span class="unit">/{{ selectedService.unit }}</span>
          </div>
          <div class="rating-info" v-if="selectedService.rating">
            <el-rate
              v-model="selectedService.rating"
              disabled
              show-score
              text-color="#ff9900"
            />
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="serviceDetailVisible = false">取消</el-button>
        <el-button type="primary" @click="bookService(selectedService)">
          立即预约
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search, House, Star, Tools } from '@element-plus/icons-vue'
import { servicesApi } from '@/api/services.js'

const router = useRouter()

// 响应式数据
const searchKeyword = ref('')
const categories = ref([])
const services = ref([])
const hotServices = ref([])
const selectedCategory = ref(null)
const serviceDetailVisible = ref(false)
const selectedService = ref(null)

// 加载数据
const loadData = async () => {
  try {
    // 加载类目
    const categoriesData = await servicesApi.getTopLevelCategories()
    categories.value = categoriesData || []
    
    // 加载热门服务
    const hotData = await servicesApi.getHotServices(6)
    hotServices.value = hotData || []
    
    // 加载所有服务
    const servicesData = await servicesApi.getAllServices()
    services.value = servicesData || []
  } catch (error) {
    console.error('加载数据失败:', error)
    ElMessage.error('加载数据失败')
  }
}

// 搜索服务
const handleSearch = async () => {
  if (!searchKeyword.value.trim()) {
    await loadServices()
    return
  }
  
  try {
    const data = await servicesApi.searchServices(searchKeyword.value)
    services.value = data || []
  } catch (error) {
    console.error('搜索失败:', error)
    ElMessage.error('搜索失败')
  }
}

// 选择类目
const selectCategory = async (categoryId) => {
  selectedCategory.value = categoryId
  try {
    const data = await servicesApi.getServicesByCategory(categoryId)
    services.value = data || []
  } catch (error) {
    console.error('加载分类服务失败:', error)
    ElMessage.error('加载分类服务失败')
  }
}

// 加载服务
const loadServices = async () => {
  try {
    const data = await servicesApi.getAllServices()
    services.value = data || []
  } catch (error) {
    console.error('加载服务失败:', error)
    ElMessage.error('加载服务失败')
  }
}

// 查看服务详情
const viewServiceDetail = (service) => {
  selectedService.value = service
  serviceDetailVisible.value = true
}

// 预约服务
const bookService = (service) => {
  if (!service) return
  
  // 检查是否已登录
  const token = localStorage.getItem('token')
  if (!token) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  // 跳转到预约页面，传递服务信息
  router.push({
    name: 'BookService',
    params: { serviceId: service.id },
    query: { service: JSON.stringify(service) }
  })
}

// 监听搜索关键词变化
watch(searchKeyword, (newVal) => {
  if (!newVal) {
    loadServices()
  }
})

// 组件挂载时加载数据
onMounted(() => {
  loadData()
})
</script>

<style scoped>
.services-page {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  text-align: center;
  margin-bottom: 40px;
}

.page-header h1 {
  font-size: 2.5rem;
  color: #2c3e50;
  margin-bottom: 10px;
}

.page-header p {
  font-size: 1.1rem;
  color: #7f8c8d;
}

.search-section {
  margin-bottom: 40px;
  max-width: 600px;
  margin-left: auto;
  margin-right: auto;
}

.categories-section,
.hot-services-section,
.services-section {
  margin-bottom: 40px;
}

.categories-section h2,
.hot-services-section h2,
.services-section h2 {
  font-size: 1.8rem;
  color: #2c3e50;
  margin-bottom: 20px;
  text-align: center;
}

.categories-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.category-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
  border: 2px solid #e1e8ed;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  background: white;
}

.category-card:hover,
.category-card.active {
  border-color: #409eff;
  background: #f0f9ff;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
}

.category-icon {
  font-size: 2rem;
  color: #409eff;
  margin-bottom: 10px;
}

.category-card span {
  font-weight: 500;
  color: #2c3e50;
}

.services-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.service-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid #e1e8ed;
}

.service-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.service-image {
  text-align: center;
  margin-bottom: 15px;
}

.service-image .el-icon {
  font-size: 3rem;
  color: #409eff;
}

.service-info h3 {
  font-size: 1.3rem;
  color: #2c3e50;
  margin-bottom: 10px;
  text-align: center;
}

.service-info p {
  color: #7f8c8d;
  margin-bottom: 15px;
  text-align: center;
  line-height: 1.5;
}

.service-price {
  text-align: center;
  margin-bottom: 15px;
}

.price {
  font-size: 1.5rem;
  font-weight: bold;
  color: #e74c3c;
}

.unit {
  color: #7f8c8d;
  margin-left: 5px;
}

.service-rating {
  text-align: center;
  margin-bottom: 15px;
}

.service-actions {
  text-align: center;
}

.empty-state {
  text-align: center;
  padding: 40px;
}

.service-detail {
  display: flex;
  gap: 20px;
}

.service-image-large {
  flex-shrink: 0;
  text-align: center;
}

.service-image-large .el-icon {
  font-size: 4rem;
  color: #409eff;
}

.service-detail-info {
  flex: 1;
}

.service-detail-info h3 {
  font-size: 1.5rem;
  color: #2c3e50;
  margin-bottom: 15px;
}

.description {
  color: #7f8c8d;
  line-height: 1.6;
  margin-bottom: 20px;
}

.price-info {
  margin-bottom: 15px;
}

.rating-info {
  margin-bottom: 20px;
}

@media (max-width: 768px) {
  .services-page {
    padding: 15px;
  }
  
  .page-header h1 {
    font-size: 2rem;
  }
  
  .categories-grid {
    grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
    gap: 15px;
  }
  
  .services-grid {
    grid-template-columns: 1fr;
  }
  
  .service-detail {
    flex-direction: column;
  }
}
</style>
