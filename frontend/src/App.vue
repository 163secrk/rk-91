<template>
  <n-config-provider>
  <n-message-provider>
    <n-dialog-provider>
      <div class="app-header">
        <div class="header-content">
          <h1 class="app-title">考古发掘现场遗物出土登记系统</h1>
          <div class="header-nav">
            <n-button @click="goHome" quaternary>
              遗物列表
            </n-button>
            <n-button @click="goUnits" quaternary>
              探方管理
            </n-button>
            <n-button @click="goRepairOrders" quaternary>
              修复工单
            </n-button>
            <n-button @click="goDashboard" quaternary>
              统计看板
            </n-button>
          </div>
        </div>
      </div>
      <router-view />
    </n-dialog-provider>
  </n-message-provider>
  </n-config-provider>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const goHome = () => {
  router.push('/relics')
}

const goUnits = () => {
  router.push('/excavation-units')
}

const goRepairOrders = () => {
  router.push('/repair-orders')
}

const goDashboard = () => {
  router.push('/dashboard')
}
</script>

<style scoped>
.app-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 0 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.header-content {
  max-width: 1400px;
  margin: 0 auto;
  height: 64px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.app-title {
  font-size: 20px;
  font-weight: 600;
  margin: 0;
}

.header-nav {
  display: flex;
  gap: 12px;
}

.header-nav :deep(.n-button) {
  color: white;
}

.header-nav :deep(.n-button:hover) {
  background-color: rgba(255, 255, 255, 0.15);
}
</style>
