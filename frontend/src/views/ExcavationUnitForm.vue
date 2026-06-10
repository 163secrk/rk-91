<template>
  <div class="page-container">
    <div class="form-container">
      <div class="page-header">
        <h1 class="page-title">{{ isEdit ? '编辑探方信息' : '新增探方' }}</h1>
        <n-button @click="goBack">返回列表</n-button>
      </div>

      <n-form ref="formRef" :model="formData" :rules="rules" label-placement="left" label-width="120px">
        <n-card title="基本信息" class="card-margin">
          <n-grid :cols="2" :x-gap="24">
            <n-grid-item>
              <n-form-item label="探方编号" path="unitNo">
                <n-input v-model:value="formData.unitNo" placeholder="如：T0101、T0203" />
              </n-form-item>
            </n-grid-item>
            <n-grid-item>
              <n-form-item label="负责人" path="director">
                <n-input v-model:value="formData.director" placeholder="请输入负责人姓名" />
              </n-form-item>
            </n-grid-item>
            <n-grid-item>
              <n-form-item label="发掘状态" path="status">
                <n-select
                  v-model:value="formData.status"
                  :options="statusOptions"
                  placeholder="请选择发掘状态"
                />
              </n-form-item>
            </n-grid-item>
            <n-grid-item>
              <n-form-item label="位置">
                <n-input v-model:value="formData.location" placeholder="如：A区第1列第1行" />
              </n-form-item>
            </n-grid-item>
          </n-grid>
        </n-card>

        <n-card title="尺寸信息" class="card-margin">
          <n-grid :cols="3" :x-gap="24">
            <n-grid-item>
              <n-form-item label="长度（米）" path="length">
                <n-input-number
                  v-model:value="formData.length"
                  :min="0"
                  :max="100"
                  :step="0.5"
                  placeholder="长度"
                  style="width: 100%"
                />
              </n-form-item>
            </n-grid-item>
            <n-grid-item>
              <n-form-item label="宽度（米）" path="width">
                <n-input-number
                  v-model:value="formData.width"
                  :min="0"
                  :max="100"
                  :step="0.5"
                  placeholder="宽度"
                  style="width: 100%"
                />
              </n-form-item>
            </n-grid-item>
            <n-grid-item>
              <n-form-item label="深度（米）">
                <n-input-number
                  v-model:value="formData.depth"
                  :min="0"
                  :max="100"
                  :step="0.1"
                  placeholder="深度（可选）"
                  style="width: 100%"
                />
              </n-form-item>
            </n-grid-item>
          </n-grid>
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
              {{ isEdit ? '保存修改' : '提交' }}
            </n-button>
          </n-space>
        </div>
      </n-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useMessage } from 'naive-ui'
import { useRouter, useRoute } from 'vue-router'
import { excavationUnitApi } from '../api'

const router = useRouter()
const route = useRoute()
const message = useMessage()

const formRef = ref(null)
const submitting = ref(false)

const isEdit = computed(() => !!route.params.id)

const statusOptions = [
  { label: '未开始', value: '未开始' },
  { label: '发掘中', value: '发掘中' },
  { label: '已暂停', value: '已暂停' },
  { label: '已完成', value: '已完成' }
]

const validateNumber = (rule, value) => {
  if (value === null || value === undefined || value === '') {
    return new Error(rule.message)
  }
  return true
}

const rules = {
  unitNo: [{ required: true, message: '请输入探方编号', trigger: 'blur' }],
  director: [{ required: true, message: '请输入负责人', trigger: 'blur' }],
  status: [{ required: true, message: '请选择发掘状态', trigger: 'change' }],
  length: [{ validator: validateNumber, message: '请输入长度', trigger: 'blur' }],
  width: [{ validator: validateNumber, message: '请输入宽度', trigger: 'blur' }]
}

const formData = reactive({
  id: null,
  unitNo: '',
  location: '',
  length: null,
  width: null,
  depth: null,
  status: '未开始',
  director: '',
  remark: ''
})

const loadUnit = async (id) => {
  try {
    const res = await excavationUnitApi.getUnitById(id)
    Object.assign(formData, res.data)
  } catch (e) {
    message.error('加载数据失败')
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  const valid = await formRef.value.validate().catch((e) => false)
  if (!valid) return

  submitting.value = true
  try {
    const submitData = { ...formData }
    if (isEdit.value) {
      await excavationUnitApi.updateUnit(formData.id, submitData)
      message.success('修改成功')
    } else {
      const res = await excavationUnitApi.createUnit(submitData)
      if (res.data && res.data.message) {
        message.error(res.data.message)
        submitting.value = false
        return
      }
      message.success('创建成功')
    }
    router.push('/excavation-units')
  } catch (e) {
    if (e.response && e.response.data && e.response.data.message) {
      message.error(e.response.data.message)
    } else {
      message.error(isEdit.value ? '修改失败' : '创建失败')
    }
  } finally {
    submitting.value = false
  }
}

const goBack = () => {
  router.push('/excavation-units')
}

onMounted(() => {
  if (isEdit.value) {
    loadUnit(route.params.id)
  }
})
</script>
