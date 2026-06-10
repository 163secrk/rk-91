<template>
  <div class="page-container">
    <div class="detail-container">
      <div class="page-header">
        <h1 class="page-title">修复工单详情</h1>
        <n-space>
          <n-button @click="goBack">返回列表</n-button>
          <n-button v-if="order.status !== 'COMPLETED'" type="primary" @click="goToEdit">编辑工单</n-button>
        </n-space>
      </div>

      <div v-if="loading" style="text-align: center; padding: 40px;">
        加载中...
      </div>

      <div v-else>
        <n-card class="card-margin">
          <template #header>
            <div style="display: flex; align-items: center; gap: 16px; flex-wrap: wrap;">
              <span style="font-size: 20px; font-weight: 600;">工单详情</span>
              <n-tag type="info">{{ order.orderNo }}</n-tag>
              <n-tag :type="statusType">{{ statusText }}</n-tag>
            </div>
          </template>

          <n-descriptions :column="2" bordered>
            <n-descriptions-item label="工单编号">{{ order.orderNo || '-' }}</n-descriptions-item>
            <n-descriptions-item label="状态">
              <n-tag :type="statusType">{{ statusText }}</n-tag>
            </n-descriptions-item>
            <n-descriptions-item label="申请人">{{ order.applicant || '-' }}</n-descriptions-item>
            <n-descriptions-item label="申请日期">{{ formatDate(order.applyDate) }}</n-descriptions-item>
            <n-descriptions-item label="修复人员">{{ order.repairer || '-' }}</n-descriptions-item>
            <n-descriptions-item label="开始日期">{{ formatDate(order.startDate) }}</n-descriptions-item>
            <n-descriptions-item label="预计完成日期">{{ formatDate(order.expectedCompleteDate) }}</n-descriptions-item>
            <n-descriptions-item label="实际完成日期">{{ formatDate(order.actualCompleteDate) }}</n-descriptions-item>
          </n-descriptions>
        </n-card>

        <n-card title="关联遗物信息" class="card-margin">
          <n-descriptions :column="2" bordered>
            <n-descriptions-item label="遗物编号">{{ order.relicNo || '-' }}</n-descriptions-item>
            <n-descriptions-item label="遗物名称">{{ order.relicName || '-' }}</n-descriptions-item>
          </n-descriptions>
          <n-button v-if="order.relicId" type="primary" size="small" style="margin-top: 16px;" @click="goToRelicDetail">
            查看遗物详情
          </n-button>
        </n-card>

        <n-card title="破损情况" class="card-margin">
          <div class="text-content">{{ order.damageDescription || '-' }}</div>
        </n-card>

        <n-card title="修复要求" class="card-margin">
          <div class="text-content">{{ order.repairRequirement || '-' }}</div>
        </n-card>

        <n-card v-if="order.repairProcess" title="修复过程" class="card-margin">
          <div class="text-content">{{ order.repairProcess }}</div>
        </n-card>

        <n-card v-if="order.repairResult" title="修复结果" class="card-margin">
          <div class="text-content">{{ order.repairResult }}</div>
        </n-card>

        <n-card v-if="order.remark" title="备注" class="card-margin">
          <div class="text-content">{{ order.remark }}</div>
        </n-card>

        <n-card class="card-margin">
          <template #header>
            <div style="display: flex; justify-content: space-between; align-items: center;">
              <span class="section-title">状态流转</span>
              <n-space>
                <n-button v-if="order.status === 'PENDING'" type="info" @click="showStartModal = true">
                  开始修复
                </n-button>
                <n-button v-if="order.status === 'IN_PROGRESS'" type="success" @click="showCompleteModal = true">
                  完成修复
                </n-button>
              </n-space>
            </div>
          </template>

          <n-steps :current="currentStep" vertical>
            <n-step title="待修复" description="工单已创建，等待开始修复">
              <template #icon>
                <n-tag type="warning">待修复</n-tag>
              </template>
            </n-step>
            <n-step title="修复中" description="修复工作进行中">
              <template #icon>
                <n-tag type="info">修复中</n-tag>
              </template>
            </n-step>
            <n-step title="已完成" description="修复工作已完成">
              <template #icon>
                <n-tag type="success">已完成</n-tag>
              </template>
            </n-step>
          </n-steps>
        </n-card>
      </div>
    </div>

    <n-modal
      v-model:show="showStartModal"
      preset="card"
      title="开始修复"
      :mask-closable="false"
      style="width: 500px;"
    >
      <n-form :model="startForm" label-placement="left" label-width="100px">
        <n-form-item label="修复人员">
          <n-input v-model:value="startForm.repairer" placeholder="请输入修复人员姓名" />
        </n-form-item>
      </n-form>
      <template #footer>
        <n-space>
          <n-button @click="showStartModal = false">取消</n-button>
          <n-button type="primary" :loading="submitting" @click="handleStartRepair">确认开始</n-button>
        </n-space>
      </template>
    </n-modal>

    <n-modal
      v-model:show="showCompleteModal"
      preset="card"
      title="完成修复"
      :mask-closable="false"
      style="width: 600px;"
    >
      <n-form :model="completeForm" label-placement="left" label-width="100px">
        <n-form-item label="修复结果">
          <n-input type="textarea" v-model:value="completeForm.repairResult" placeholder="请描述修复结果" :rows="4" />
        </n-form-item>
      </n-form>
      <template #footer>
        <n-space>
          <n-button @click="showCompleteModal = false">取消</n-button>
          <n-button type="primary" :loading="submitting" @click="handleCompleteRepair">确认完成</n-button>
        </n-space>
      </template>
    </n-modal>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useMessage, useDialog } from 'naive-ui'
import { useRouter, useRoute } from 'vue-router'
import { repairOrderApi } from '../api'
import { format } from 'date-fns'

const router = useRouter()
const route = useRoute()
const message = useMessage()

const loading = ref(false)
const order = ref({})
const showStartModal = ref(false)
const showCompleteModal = ref(false)
const submitting = ref(false)

const startForm = reactive({
  repairer: ''
})

const completeForm = reactive({
  repairResult: ''
})

const statusType = computed(() => {
  const map = {
    PENDING: 'warning',
    IN_PROGRESS: 'info',
    COMPLETED: 'success'
  }
  return map[order.value.status] || 'default'
})

const statusText = computed(() => {
  const map = {
    PENDING: '待修复',
    IN_PROGRESS: '修复中',
    COMPLETED: '已完成'
  }
  return map[order.value.status] || order.value.status
})

const currentStep = computed(() => {
  const map = {
    PENDING: 0,
    IN_PROGRESS: 1,
    COMPLETED: 2
  }
  return map[order.value.status] || 0
})

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  try {
    return format(new Date(dateStr), 'yyyy-MM-dd')
  } catch (e) {
    return dateStr
  }
}

const loadOrderDetail = async () => {
  loading.value = true
  try {
    const res = await repairOrderApi.getOrderById(route.params.id)
    order.value = res.data
  } catch (e) {
    message.error('加载详情失败')
  } finally {
    loading.value = false
  }
}

const handleStartRepair = async () => {
  submitting.value = true
  try {
    await repairOrderApi.startRepair(route.params.id, startForm)
    message.success('修复已开始')
    showStartModal.value = false
    loadOrderDetail()
  } catch (e) {
    message.error('操作失败')
  } finally {
    submitting.value = false
  }
}

const handleCompleteRepair = async () => {
  submitting.value = true
  try {
    await repairOrderApi.completeRepair(route.params.id, completeForm)
    message.success('修复已完成')
    showCompleteModal.value = false
    loadOrderDetail()
  } catch (e) {
    message.error('操作失败')
  } finally {
    submitting.value = false
  }
}

const goBack = () => {
  router.push('/repair-orders')
}

const goToEdit = () => {
  router.push(`/repair-orders/${route.params.id}/edit`)
}

const goToRelicDetail = () => {
  if (order.value.relicId) {
    router.push(`/relics/${order.value.relicId}`)
  }
}

onMounted(() => {
  loadOrderDetail()
})
</script>

<style scoped>
.text-content {
  line-height: 1.8;
  color: #333;
  white-space: pre-wrap;
  word-break: break-word;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
}
</style>
