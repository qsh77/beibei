<template>
  <div class="order-status-badge">
    <div class="badge-content" :class="statusClass">
      <div class="status-indicator">
        <div class="dot" :class="{ 'pulsing': status === 'DOING' }"></div>
      </div>
      <span class="status-text">{{ statusText }}</span>
    </div>
    
    <!-- 线性进度条 -->
    <div class="linear-progress" v-if="status !== 'CANCELED'">
      <div 
        v-for="i in 4" 
        :key="i"
        class="progress-step"
        :class="{ 
          'active': i <= currentStep,
          'current': i === currentStep
        }"
      ></div>
    </div>
    <div class="linear-progress canceled" v-else>
      <div class="progress-step active"></div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  status: {
    type: String,
    required: true
  }
})

const statusMap = {
  'CREATED': { text: '待分配', step: 1, class: 'status-created' },
  'ASSIGNED': { text: '待接单', step: 2, class: 'status-assigned' }, // Admin端叫已分配，Worker端叫待接单，统一叫"待接单"或根据上下文？
  // 此时组件不知道上下文，取一个中性名字"已分配"或"待服务"？
  // 保持通用：Admin端"已分配"更准确，Worker端"待接单"更准确。
  // 这里我们用通用文案，或者允许外部传入 text? 
  // 为了精致，我们内置文案。
  // Admin: CREATED(待分配) -> ASSIGNED(已分配) -> DOING(服务中) -> DONE(已完成)
  // Worker View uses the same statuses.
  // We'll stick to standard names.
  'DOING': { text: '服务中', step: 3, class: 'status-doing' },
  'DONE': { text: '已完成', step: 4, class: 'status-done' },
  'CANCELED': { text: '已取消', step: 0, class: 'status-canceled' }
}

const statusConfig = computed(() => {
  return statusMap[props.status] || { text: props.status, step: 0, class: 'status-unknown' }
})

const statusText = computed(() => {
  // 特殊处理：ASSIGNED 在不同端可能叫法不同，这里统一显示"已分配/待接单"
  // 或者简单点 "待服务"？
  if (props.status === 'ASSIGNED') return '待服务'
  return statusConfig.value.text
})

const statusClass = computed(() => statusConfig.value.class)
const currentStep = computed(() => statusConfig.value.step)

</script>

<style scoped>
.order-status-badge {
  display: inline-flex;
  flex-direction: column;
  gap: 4px;
}

.badge-content {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  font-weight: 500;
}

.dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: currentColor;
}

.pulsing {
  box-shadow: 0 0 0 0 rgba(currentColor, 0.7);
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0% {
    transform: scale(0.95);
    box-shadow: 0 0 0 0 rgba(currentColor, 0.7);
  }
  70% {
    transform: scale(1);
    box-shadow: 0 0 0 4px rgba(currentColor, 0);
  }
  100% {
    transform: scale(0.95);
    box-shadow: 0 0 0 0 rgba(currentColor, 0);
  }
}

/* Colors */
.status-created { color: #64748b; } /* Slate 500 */
.status-assigned { color: #f59e0b; } /* Amber 500 */
.status-doing { color: #3b82f6; } /* Blue 500 */
.status-done { color: #10b981; } /* Emerald 500 */
.status-canceled { color: #9ca3af; } /* Gray 400 */
.status-unknown { color: #9ca3af; }

/* Linear Progress Bar */
.linear-progress {
  display: flex;
  gap: 2px;
  width: 60px; /* Fixed width for consistency */
  height: 3px;
}

.progress-step {
  flex: 1;
  background-color: #e2e8f0; /* Slate 200 */
  border-radius: 1.5px;
  transition: all 0.3s ease;
}

/* Steps Coloring */
/* Created: Step 1 active */
.status-created ~ .linear-progress .progress-step:nth-child(1).active { background-color: #64748b; }

/* Assigned: Step 1,2 active */
.status-assigned ~ .linear-progress .progress-step:nth-child(1).active { background-color: #f59e0b; opacity: 0.5; }
.status-assigned ~ .linear-progress .progress-step:nth-child(2).active { background-color: #f59e0b; }

/* Doing: Step 1,2,3 active */
.status-doing ~ .linear-progress .progress-step:nth-child(1).active,
.status-doing ~ .linear-progress .progress-step:nth-child(2).active { background-color: #3b82f6; opacity: 0.5; }
.status-doing ~ .linear-progress .progress-step:nth-child(3).active { background-color: #3b82f6; }

/* Done: All active */
.status-done ~ .linear-progress .progress-step.active { background-color: #10b981; }

.canceled .progress-step.active {
  background-color: #cbd5e1;
  width: 100%;
}

</style>
