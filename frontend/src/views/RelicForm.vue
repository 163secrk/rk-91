<template>
  <div class="page-container">
    <div class="form-container">
      <div class="page-header">
        <h1 class="page-title">{{ isEdit ? '编辑遗物信息' : '新增遗物登记' }}</h1>
        <n-button @click="goBack">返回列表</n-button>
      </div>

      <n-form ref="formRef" :model="formData" :rules="rules" label-placement="left" label-width="120px">
        <n-card title="基本信息" class="card-margin">
          <n-grid :cols="2" :x-gap="24">
            <n-grid-item>
              <n-form-item label="遗物编号" path="relicNo">
                <n-input v-model:value="formData.relicNo" placeholder="请输入遗物编号" />
              </n-form-item>
            </n-grid-item>
            <n-grid-item>
              <n-form-item label="遗物名称" path="name">
                <n-input v-model:value="formData.name" placeholder="请输入遗物名称" />
              </n-form-item>
            </n-grid-item>
            <n-grid-item>
              <n-form-item label="类别">
                <n-select
                  v-model:value="formData.category"
                  :options="categoryOptions"
                  placeholder="请选择类别"
                  allow-create
                />
              </n-form-item>
            </n-grid-item>
            <n-grid-item>
              <n-form-item label="材质">
                <n-select
                  v-model:value="formData.material"
                  :options="materialOptions"
                  placeholder="请选择材质"
                  allow-create
                />
              </n-form-item>
            </n-grid-item>
            <n-grid-item>
              <n-form-item label="年代">
                <n-select
                  v-model:value="formData.era"
                  :options="eraOptions"
                  placeholder="请选择年代"
                  allow-create
                />
              </n-form-item>
            </n-grid-item>
            <n-grid-item>
              <n-form-item label="保存状态">
                <n-select
                  v-model:value="formData.preservationStatus"
                  :options="statusOptions"
                  placeholder="请选择保存状态"
                />
              </n-form-item>
            </n-grid-item>
          </n-grid>
          <n-form-item label="描述">
            <n-input type="textarea" v-model:value="formData.description" placeholder="请输入遗物描述" :rows="3" />
          </n-form-item>
        </n-card>

        <n-card title="出土地点与人员" class="card-margin">
          <n-grid :cols="2" :x-gap="24">
            <n-grid-item>
              <n-form-item label="出土日期" path="excavateDate">
                <n-date-picker
                  v-model:value="formData.excavateDate"
                  type="date"
                  placeholder="请选择出土日期"
                  style="width: 100%"
                />
              </n-form-item>
            </n-grid-item>
            <n-grid-item>
              <n-form-item label="发掘人员" path="excavator">
                <n-input v-model:value="formData.excavator" placeholder="请输入发掘人员姓名" />
              </n-form-item>
            </n-grid-item>
            <n-grid-item>
              <n-form-item label="出土地点">
                <n-input v-model:value="formData.excavationSite" placeholder="请输入出土地点" />
              </n-form-item>
            </n-grid-item>
            <n-grid-item>
              <n-form-item label="所属探方">
                <n-select
                  v-model:value="formData.excavationUnitId"
                  :options="excavationUnitOptions"
                  placeholder="请选择所属探方"
                  clearable
                />
              </n-form-item>
            </n-grid-item>
            <n-grid-item>
              <n-form-item label="地层">
                <n-input v-model:value="formData.stratum" placeholder="请输入地层信息" />
              </n-form-item>
            </n-grid-item>
          </n-grid>
        </n-card>

        <n-card title="三维坐标" class="card-margin">
          <n-grid :cols="3" :x-gap="24">
            <n-grid-item>
              <n-form-item label="X 坐标" path="coordinate.x">
                <n-input-number
                  v-model:value="formData.coordinate.x"
                  :min="-10000"
                  :max="10000"
                  :step="0.001"
                  placeholder="X 坐标"
                />
              </n-form-item>
            </n-grid-item>
            <n-grid-item>
              <n-form-item label="Y 坐标" path="coordinate.y">
                <n-input-number
                  v-model:value="formData.coordinate.y"
                  :min="-10000"
                  :max="10000"
                  :step="0.001"
                  placeholder="Y 坐标"
                />
              </n-form-item>
            </n-grid-item>
            <n-grid-item>
              <n-form-item label="Z 坐标（深度）" path="coordinate.z">
                <n-input-number
                  v-model:value="formData.coordinate.z"
                  :min="-1000"
                  :max="1000"
                  :step="0.001"
                  placeholder="Z 坐标"
                />
              </n-form-item>
            </n-grid-item>
          </n-grid>
          <n-grid :cols="2" :x-gap="24">
            <n-grid-item>
              <n-form-item label="坐标系">
                <n-input v-model:value="formData.coordinate.coordinateSystem" placeholder="如：WGS84、地方坐标系" />
              </n-form-item>
            </n-grid-item>
            <n-grid-item>
              <n-form-item label="坐标备注">
                <n-input v-model:value="formData.coordinate.remark" placeholder="坐标测量说明" />
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
              {{ isEdit ? '保存修改' : '提交登记' }}
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
import { relicApi, excavationUnitApi } from '../api'

const router = useRouter()
const route = useRoute()
const message = useMessage()

const formRef = ref(null)
const submitting = ref(false)
const excavationUnitOptions = ref([])

const isEdit = computed(() => !!route.params.id)

const loadExcavationUnits = async () => {
  try {
    const res = await excavationUnitApi.getAllUnits()
    excavationUnitOptions.value = res.data.map(unit => ({
      label: `${unit.unitNo} - ${unit.location || '未设置位置'}`,
      value: unit.id
    }))
  } catch (e) {
    message.error('加载探方列表失败')
  }
}

const categoryOptions = [
  { label: '陶器', value: '陶器' },
  { label: '瓷器', value: '瓷器' },
  { label: '铜器', value: '铜器' },
  { label: '铁器', value: '铁器' },
  { label: '玉器', value: '玉器' },
  { label: '石器', value: '石器' },
  { label: '骨器', value: '骨器' },
  { label: '木器', value: '木器' },
  { label: '钱币', value: '钱币' },
  { label: '其他', value: '其他' }
]

const materialOptions = [
  { label: '陶土', value: '陶土' },
  { label: '青铜', value: '青铜' },
  { label: '铁', value: '铁' },
  { label: '玉', value: '玉' },
  { label: '石', value: '石' },
  { label: '骨', value: '骨' },
  { label: '木', value: '木' },
  { label: '瓷', value: '瓷' },
  { label: '金', value: '金' },
  { label: '银', value: '银' }
]

const eraOptions = [
  { label: '新石器时代', value: '新石器时代' },
  { label: '夏', value: '夏' },
  { label: '商', value: '商' },
  { label: '周', value: '周' },
  { label: '秦', value: '秦' },
  { label: '汉', value: '汉' },
  { label: '魏晋南北朝', value: '魏晋南北朝' },
  { label: '隋', value: '隋' },
  { label: '唐', value: '唐' },
  { label: '宋', value: '宋' },
  { label: '元', value: '元' },
  { label: '明', value: '明' },
  { label: '清', value: '清' },
  { label: '民国', value: '民国' },
  { label: '近现代', value: '近现代' }
]

const statusOptions = [
  { label: '完好', value: '完好' },
  { label: '轻微破损', value: '轻微破损' },
  { label: '严重破损', value: '严重破损' },
  { label: '已修复', value: '已修复' }
]

const validateNumber = (rule, value) => {
  if (value === null || value === undefined || value === '') {
    return new Error(rule.message)
  }
  return true
}

const validateDate = (rule, value) => {
  if (value === null || value === undefined || value === '') {
    return new Error(rule.message)
  }
  return true
}

const rules = {
  relicNo: [{ required: true, message: '请输入遗物编号', trigger: 'blur' }],
  name: [{ required: true, message: '请输入遗物名称', trigger: 'blur' }],
  excavateDate: [{ validator: validateDate, message: '请选择出土日期', trigger: 'change' }],
  excavator: [{ required: true, message: '请输入发掘人员', trigger: 'blur' }],
  'coordinate.x': [{ validator: validateNumber, message: '请输入X坐标', trigger: 'blur' }],
  'coordinate.y': [{ validator: validateNumber, message: '请输入Y坐标', trigger: 'blur' }],
  'coordinate.z': [{ validator: validateNumber, message: '请输入Z坐标', trigger: 'blur' }]
}

const formData = reactive({
  id: null,
  relicNo: '',
  name: '',
  category: '',
  description: '',
  material: '',
  era: '',
  excavateDate: null,
  excavator: '',
  excavationSite: '',
  stratum: '',
  excavationUnitId: null,
  preservationStatus: '',
  coordinate: {
    x: null,
    y: null,
    z: null,
    coordinateSystem: '',
    remark: ''
  },
  remark: ''
})

const loadRelic = async (id) => {
  try {
    const res = await relicApi.getRelicById(id)
    const data = res.data
    Object.assign(formData, data)
    if (data.coordinate) {
      Object.assign(formData.coordinate, data.coordinate)
    }
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
      await relicApi.updateRelic(formData.id, submitData)
      message.success('修改成功')
    } else {
      await relicApi.createRelic(submitData)
      message.success('登记成功')
    }
    router.push('/relics')
  } catch (e) {
    message.error(isEdit.value ? '修改失败' : '登记失败')
  } finally {
    submitting.value = false
  }
}

const goBack = () => {
  router.push('/relics')
}

onMounted(() => {
  loadExcavationUnits()
  if (isEdit.value) {
    loadRelic(route.params.id)
  }
})
</script>
