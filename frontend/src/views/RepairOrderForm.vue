<template>
  <div class="page-container">
    <div class="form-container">
      <div class="page-header">
        <h1 class="page-title">{{ isEdit ? '编辑修复工单' : '新增修复工单' }}</h1>
        <n-button @click="goBack">返回列表</n-button>
      </div>

      <n-form ref="formRef" :model="formData" :rules="rules" label-placement="left" label-width="120px">
        <n-card title="基本信息" class="card-margin">
          <n-grid :cols="2" :x-gap="24">
            <n-grid-item>
              <n-form-item label="工单编号">
                <n-input v-model:value="formData.orderNo" placeholder="系统自动生成，可留空" :disabled="isEdit" />
              </n-form-item>
            </n-grid-item>
            <n-grid-item>
              <n-form-item label="申请日期" path="applyDate">
                <n-date-picker
                  v-model:value="formData.applyDate"
                  type="date"
                  placeholder="请选择申请日期"
                  style="width: 100%"
                />
              </n-form-item>
            </n-grid-item>
            <n-grid-item>
              <n-form-item label="申请人" path="applicant">
                <n-input v-model:value="formData.applicant" placeholder="请输入申请人姓名" />
              </n-form-item>
            </n-grid-item>
            <n-grid-item>
              <n-form-item label="修复人员">
                <n-input v-model:value="formData.repairer" placeholder="请输入修复人员姓名" />
              </n-form-item>
            </n-grid-item>
          </n-grid>
        </n-card>

        <n-card title="关联遗物" class="card-margin">
          <n-grid :cols="2" :x-gap="24">
            <n-grid-item>
              <n-form-item label="选择遗物" path="relicId">
                <n-select
                  v-model:value="formData.relicId"
                  :options="relicOptions"
                  placeholder="请选择关联的遗物"
                  filterable
                  :disabled="isEdit && formData.status === 'COMPLETED'"
                />
              </n-form-item>
            </n-grid-item>
            <n-grid-item>
              <n-form-item label="预计完成日期">
                <n-date-picker
                  v-model:value="formData.expectedCompleteDate"
                  type="date"
                  placeholder="请选择预计完成日期"
                  style="width: 100%"
                />
              </n-form-item>
            </n-grid-item>
          </n-grid>
          <div v-if="selectedRelicInfo" class="relic-info-box">
            <n-descriptions :column="2" bordered size="small">
              <n-descriptions-item label="遗物编号">{{ selectedRelicInfo.relicNo || '-' }}</n-descriptions-item>
              <n-descriptions-item label="遗物名称">{{ selectedRelicInfo.name || '-' }}</n-descriptions-item>
              <n-descriptions-item label="类别">{{ selectedRelicInfo.category || '-' }}</n-descriptions-item>
              <n-descriptions-item label="保存状态">{{ selectedRelicInfo.preservationStatus || '-' }}</n-descriptions-item>
            </n-descriptions>
          </div>
        </n-card>

        <n-card title="破损与修复信息" class="card-margin">
          <n-form-item label="破损情况" path="damageDescription">
            <n-input type="textarea" v-model:value="formData.damageDescription" placeholder="请详细描述破损情况" :rows="4" />
          </n-form-item>
          <n-form-item label="修复要求">
            <n-input type="textarea" v-model:value="formData.repairRequirement" placeholder="请描述修复要求和预期效果" :rows="3" />
          </n-form-item>
          <n-form-item label="修复过程">
            <n-input type="textarea" v-model:value="formData.repairProcess" placeholder="请记录修复过程" :rows="4" />
          </n-form-item>
          <n-form-item label="修复结果">
            <n-input type="textarea" v-model:value="formData.repairResult" placeholder="请描述修复结果" :rows="3" />
          </n-form-item>
        </n-card>

        <n-card title="备注信息" class="card-margin">
          <n-form-item label="备注">
            <n-input type="textarea" v-model:value="formData.remark" placeholder="其他需要记录的信息" :rows="3" />
          </n-form-item>
        </n-card>

        <div style="text-align: center; margin-top: 24px;">
          <n-space>
            <n-button size="large" @click="goBack">取消</n-button>
            <n-button size="large" type="primary" :loading="submitting" @click="handleSubmit">
              {{ isEdit ? '保存修改' : '提交工单' }}
            </n-button>
          </n-space>
        </div>
      </n-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch, onMounted } from 'vue'
import { useMessage } from 'naive-ui'
import { useRouter, useRoute } from 'vue-router'
import { repairOrderApi, relicApi } from '../api'

const router = useRouter()
const route = useRoute()
const message = useMessage()

const formRef = ref(null)
const submitting = ref(false)
const relicOptions = ref([])
const relicList = ref([])

const isEdit = computed(() => !!route.params.id)

const selectedRelicInfo = computed(() => {
  if (!formData.relicId) return null
  return relicList.value.find(r => r.id === formData.relicId)
})

const loadRelics = async () => {
  try {
    const res = await relicApi.getAllRelics()
    relicList.value = res.data
    relicOptions.value = res.data.map(relic => ({
      label: `${relic.relicNo} - ${relic.name}`,
      value: relic.id
    }))
  } catch (e) {
    message.error('加载遗物列表失败')
  }
}

const rules = {
  relicId: [{ required: true, message: '请选择关联的遗物', trigger: 'change' }],
  applicant: [{ required: true, message: '请输入申请人', trigger: 'blur' }],
  applyDate: [{ required: true, message: '请选择申请日期', trigger: 'change' }],
  damageDescription: [{ required: true, message: '请输入破损情况', trigger: 'blur' }]
}

const formData = reactive({
  id: null,
  orderNo: '',
  relicId: null,
  status: 'PENDING',
  damageDescription: '',
  repairRequirement: '',
  applicant: '',
  applyDate: null,
  repairer: '',
  expectedCompleteDate: null,
  repairProcess: '',
  repairResult: '',
  remark: ''
})

const loadOrder = async (id) => {
  try {
    const res = await repairOrderApi.getOrderById(id)
    const data = res.data
    Object.assign(formData, data)
    if (data.applyDate) {
      formData.applyDate = new Date(data.applyDate).getTime()
    }
    if (data.expectedCompleteDate) {
      formData.expectedCompleteDate = new Date(data.expectedCompleteDate).getTime()
    }
  } catch (e) {
    message.error('加载数据失败')
  }
}

const formatDate = (timestamp) => {
  if (!timestamp) return null
  const d = new Date(timestamp)
  return d.getFullYear() + '-' +
    String(d.getMonth() + 1).padStart(2, '0') + '-' +
    String(d.getDate()).padStart(2, '0')
}

const handleSubmit = async () => {
  if (!formRef.value) return
  const valid = await formRef.value.validate().catch((e) => false)
  if (!valid) return

  submitting.value = true
  try {
    const submitData = { ...formData }
    if (submitData.applyDate && typeof submitData.applyDate === 'number') {
      submitData.applyDate = formatDate(submitData.applyDate)
    }
    if (submitData.expectedCompleteDate && typeof submitData.expectedCompleteDate === 'number') {
      submitData.expectedCompleteDate = formatDate(submitData.expectedCompleteDate)
    }
    delete submitData.relicNo
    delete submitData.relicName
    delete submitData.createTime
    delete submitData.updateTime

    if (isEdit.value) {
      await repairOrderApi.updateOrder(formData.id, submitData)
      message.success('修改成功')
    } else {
      await repairOrderApi.createOrder(submitData)
      message.success('创建成功')
    }
    router.push('/repair-orders')
  } catch (e) {
    message.error(isEdit.value ? '修改失败' : '创建失败')
  } finally {
    submitting.value = false
  }
}

const goBack = () => {
  router.push('/repair-orders')
}

onMounted(() => {
  loadRelics()
  if (isEdit.value) {
    loadOrder(route.params.id)
  }
})
</script>

<style scoped>
.relic-info-box {
  margin-top: 16px;
  padding: 16px;
  background-color: #f5f7fa;
  border-radius: 4px;
}
</style>
