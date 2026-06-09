<template>
  <div class="page-container">
    <div class="detail-container">
      <div class="page-header">
        <h1 class="page-title">遗物详情</h1>
        <n-space>
          <n-button @click="goBack">返回列表</n-button>
          <n-button type="primary" @click="goToEdit">编辑信息</n-button>
        </n-space>
      </div>

      <div v-if="loading" style="text-align: center; padding: 40px;">
        加载中...
      </div>

      <div v-else>
        <n-card class="card-margin">
          <template #header>
            <div style="display: flex; align-items: center; gap: 16px;">
              <span style="font-size: 20px; font-weight: 600;">{{ relic.name }}</span>
              <n-tag>{{ relic.relicNo }}</n-tag>
              <n-tag :type="statusType">{{ relic.preservationStatus || '未设置' }}</n-tag>
            </div>
          </template>

          <n-descriptions :column="2" bordered>
            <n-descriptions-item label="类别">{{ relic.category || '-' }}</n-descriptions-item>
            <n-descriptions-item label="材质">{{ relic.material || '-' }}</n-descriptions-item>
            <n-descriptions-item label="年代">{{ relic.era || '-' }}</n-descriptions-item>
            <n-descriptions-item label="出土日期">{{ formatDate(relic.excavateDate) }}</n-descriptions-item>
            <n-descriptions-item label="发掘人员">{{ relic.excavator || '-' }}</n-descriptions-item>
            <n-descriptions-item label="出土地点">{{ relic.excavationSite || '-' }}</n-descriptions-item>
            <n-descriptions-item label="所属探方">{{ getExcavationUnitDisplay() }}</n-descriptions-item>
            <n-descriptions-item label="地层">{{ relic.stratum || '-' }}</n-descriptions-item>
            <n-descriptions-item label="登记时间">{{ formatDate(relic.createTime) }}</n-descriptions-item>
            <n-descriptions-item label="描述" :span="2">{{ relic.description || '-' }}</n-descriptions-item>
            <n-descriptions-item label="备注" :span="2">{{ relic.remark || '-' }}</n-descriptions-item>
          </n-descriptions>
        </n-card>

        <n-card title="三维坐标" class="card-margin">
          <div v-if="relic.coordinate">
            <div class="coordinate-display">
              <div class="coord-item">
                <span class="coord-label">X:</span>
                <span class="coord-value">{{ relic.coordinate.x }}</span>
              </div>
              <div class="coord-item">
                <span class="coord-label">Y:</span>
                <span class="coord-value">{{ relic.coordinate.y }}</span>
              </div>
              <div class="coord-item">
                <span class="coord-label">Z:</span>
                <span class="coord-value">{{ relic.coordinate.z }}</span>
              </div>
            </div>
            <n-divider style="margin: 16px 0;" />
            <n-descriptions :column="2" bordered size="small">
              <n-descriptions-item label="坐标系">{{ relic.coordinate.coordinateSystem || '-' }}</n-descriptions-item>
              <n-descriptions-item label="备注">{{ relic.coordinate.remark || '-' }}</n-descriptions-item>
            </n-descriptions>
          </div>
          <div v-else>
            暂无坐标信息
          </div>
        </n-card>

        <n-card class="card-margin">
          <template #header>
            <div style="display: flex; justify-content: space-between; align-items: center;">
              <span class="section-title">修复档案记录</span>
              <n-button type="primary" size="small" @click="showAddModal = true">
                添加修复记录
              </n-button>
            </div>
          </template>

          <div v-if="restorationRecords.length === 0" style="text-align: center; padding: 40px; color: #999;">
            暂无修复记录
          </div>

          <div v-else>
            <div v-for="(record, index) in restorationRecords" :key="record.id" style="margin-bottom: 16px;">
              <n-card size="small" :bordered="true">
                <div style="display: flex; justify-content: space-between; align-items: flex-start;">
                  <div style="flex: 1;">
                    <div style="display: flex; align-items: center; gap: 12px; margin-bottom: 12px;">
                      <n-tag type="info">记录 #{{ index + 1 }}</n-tag>
                      <span style="font-weight: 600;">{{ formatDate(record.recordDate) }}</span>
                      <span style="color: #666;">操作人员：{{ record.operator }}</span>
                    </div>
                    <n-descriptions :column="1" bordered size="small">
                      <n-descriptions-item label="破损情况">{{ record.damageDescription }}</n-descriptions-item>
                      <n-descriptions-item label="修复方法">{{ record.restorationMethod }}</n-descriptions-item>
                      <n-descriptions-item label="使用材料">{{ record.materialsUsed || '-' }}</n-descriptions-item>
                      <n-descriptions-item label="修复效果">{{ record.restorationEffect || '-' }}</n-descriptions-item>
                      <n-descriptions-item label="备注">{{ record.remark || '-' }}</n-descriptions-item>
                    </n-descriptions>
                  </div>
                  <n-button
                    type="error"
                    size="small"
                    @click="deleteRestorationRecord(record.id, index + 1)"
                    style="margin-left: 16px;"
                  >
                    删除
                  </n-button>
                </div>
              </n-card>
            </div>
          </div>
        </n-card>
      </div>
    </div>

    <n-modal
      v-model:show="showAddModal"
      preset="card"
      title="添加修复记录"
      :mask-closable="false"
      style="width: 600px;"
    >
      <n-form ref="restorationFormRef" :model="restorationForm" :rules="restorationRules" label-placement="left" label-width="100px">
        <n-form-item label="记录日期" path="recordDate">
          <n-date-picker
            v-model:value="restorationForm.recordDate"
            type="date"
            placeholder="请选择记录日期"
            style="width: 100%;"
          />
        </n-form-item>
        <n-form-item label="操作人员" path="operator">
          <n-input v-model:value="restorationForm.operator" placeholder="请输入操作人员姓名" />
        </n-form-item>
        <n-form-item label="破损情况" path="damageDescription">
          <n-input type="textarea" v-model:value="restorationForm.damageDescription" placeholder="请描述破损情况" :rows="2" />
        </n-form-item>
        <n-form-item label="修复方法" path="restorationMethod">
          <n-input type="textarea" v-model:value="restorationForm.restorationMethod" placeholder="请描述修复方法" :rows="3" />
        </n-form-item>
        <n-form-item label="使用材料">
          <n-input type="textarea" v-model:value="restorationForm.materialsUsed" placeholder="请输入使用的材料" :rows="2" />
        </n-form-item>
        <n-form-item label="修复效果">
          <n-input type="textarea" v-model:value="restorationForm.restorationEffect" placeholder="请描述修复效果" :rows="2" />
        </n-form-item>
        <n-form-item label="备注">
          <n-input type="textarea" v-model:value="restorationForm.remark" placeholder="其他备注信息" :rows="2" />
        </n-form-item>
      </n-form>
      <template #footer>
        <n-space>
          <n-button @click="showAddModal = false">取消</n-button>
          <n-button type="primary" :loading="submitting" @click="submitRestorationRecord">确认添加</n-button>
        </n-space>
      </template>
    </n-modal>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useMessage, useDialog } from 'naive-ui'
import { useRouter, useRoute } from 'vue-router'
import { relicApi, excavationUnitApi } from '../api'
import { format } from 'date-fns'

const router = useRouter()
const route = useRoute()
const message = useMessage()
const dialog = useDialog()

const loading = ref(false)
const relic = ref({})
const restorationRecords = ref([])
const showAddModal = ref(false)
const restorationFormRef = ref(null)
const submitting = ref(false)
const excavationUnitMap = ref({})

const statusType = computed(() => {
  const map = {
    '完好': 'success',
    '轻微破损': 'warning',
    '严重破损': 'error',
    '已修复': 'info'
  }
  return map[relic.value.preservationStatus] || 'default'
})

const restorationRules = {
  recordDate: [{ required: true, message: '请选择记录日期', trigger: 'change' }],
  operator: [{ required: true, message: '请输入操作人员', trigger: 'blur' }],
  damageDescription: [{ required: true, message: '请输入破损情况', trigger: 'blur' }],
  restorationMethod: [{ required: true, message: '请输入修复方法', trigger: 'blur' }]
}

const restorationForm = reactive({
  recordDate: null,
  operator: '',
  damageDescription: '',
  restorationMethod: '',
  materialsUsed: '',
  restorationEffect: '',
  remark: ''
})

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  try {
    return format(new Date(dateStr), 'yyyy-MM-dd')
  } catch (e) {
    return dateStr
  }
}

const loadExcavationUnits = async () => {
  try {
    const res = await excavationUnitApi.getAllUnits()
    const map = {}
    res.data.forEach(unit => {
      map[unit.id] = unit.unitNo
    })
    excavationUnitMap.value = map
  } catch (e) {
    console.error('加载探方列表失败', e)
  }
}

const getExcavationUnitDisplay = () => {
  if (relic.value.excavationUnitId && excavationUnitMap.value[relic.value.excavationUnitId]) {
    return excavationUnitMap.value[relic.value.excavationUnitId]
  }
  return '-'
}

const loadRelicDetail = async () => {
  loading.value = true
  try {
    const res = await relicApi.getRelicById(route.params.id)
    relic.value = res.data
    loadRestorationRecords()
  } catch (e) {
    message.error('加载详情失败')
  } finally {
    loading.value = false
  }
}

const loadRestorationRecords = async () => {
  try {
    const res = await relicApi.getRestorationRecords(route.params.id)
    restorationRecords.value = res.data
  } catch (e) {
    message.error('加载修复记录失败')
  }
}

const submitRestorationRecord = async () => {
  if (!restorationFormRef.value) return
  const valid = await restorationFormRef.value.validate().catch((e) => false)
  if (!valid) return

  submitting.value = true
  try {
    const submitData = { ...restorationForm }
    await relicApi.addRestorationRecord(route.params.id, submitData)
    message.success('添加成功')
    showAddModal.value = false
    resetRestorationForm()
    loadRestorationRecords()
  } catch (e) {
    message.error('添加失败')
  } finally {
    submitting.value = false
  }
}

const resetRestorationForm = () => {
  restorationForm.recordDate = null
  restorationForm.operator = ''
  restorationForm.damageDescription = ''
  restorationForm.restorationMethod = ''
  restorationForm.materialsUsed = ''
  restorationForm.restorationEffect = ''
  restorationForm.remark = ''
}

const deleteRestorationRecord = (recordId, index) => {
  dialog.warning({
    title: '确认删除',
    content: `确定要删除第 ${index} 条修复记录吗？`,
    positiveText: '删除',
    negativeText: '取消',
    onPositiveClick: async () => {
      try {
        await relicApi.deleteRestorationRecord(recordId)
        message.success('删除成功')
        loadRestorationRecords()
      } catch (e) {
        message.error('删除失败')
      }
    }
  })
}

const goBack = () => {
  router.push('/relics')
}

const goToEdit = () => {
  router.push(`/relics/${route.params.id}/edit`)
}

onMounted(() => {
  loadExcavationUnits()
  loadRelicDetail()
})
</script>
