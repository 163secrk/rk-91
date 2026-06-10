<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">出土遗物列表</h1>
      <n-button type="primary" @click="goToCreate">
        新增遗物登记
      </n-button>
    </div>

    <n-card class="card-margin">
      <div class="search-bar">
        <n-select
          v-model:value="selectedExcavationUnit"
          :options="excavationUnitOptions"
          placeholder="全部探方"
          clearable
          style="width: 180px"
          @update:value="handleExcavationUnitFilter"
        />
        <n-select
          v-model:value="searchField"
          :options="searchOptions"
          style="width: 150px"
        />
        <n-input
          v-model:value="searchKeyword"
          placeholder="请输入搜索关键词"
          style="flex: 1"
          @keyup.enter="handleSearch"
        />
        <n-button type="primary" @click="handleSearch">搜索</n-button>
        <n-button @click="handleReset">重置</n-button>
        <n-button type="success" :loading="exporting" @click="handleExport">
          导出Excel
        </n-button>
      </div>

      <n-data-table
        :columns="columns"
        :data="relics"
        :loading="loading"
      />
    </n-card>
  </div>
</template>

<script setup>
import { ref, onMounted, h, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { NTag, NSpace, NButton, useMessage, useDialog } from 'naive-ui'
import { relicApi, excavationUnitApi } from '../api'
import { format } from 'date-fns'
import * as XLSX from 'xlsx'
import { saveAs } from 'file-saver'

const router = useRouter()
const message = useMessage()
const dialog = useDialog()

const relics = ref([])
const loading = ref(false)
const exporting = ref(false)
const searchField = ref('name')
const searchKeyword = ref('')
const excavationUnitMap = ref({})
const excavationUnitOptions = ref([])
const selectedExcavationUnit = ref(null)

const searchOptions = [
  { label: '名称', value: 'name' },
  { label: '编号', value: 'relicNo' },
  { label: '类别', value: 'category' }
]

const loadExcavationUnits = async () => {
  try {
    const res = await excavationUnitApi.getAllUnits()
    const map = {}
    const options = []
    res.data.forEach(unit => {
      map[unit.id] = unit.unitNo
      options.push({ label: unit.unitNo, value: unit.id })
    })
    excavationUnitMap.value = map
    excavationUnitOptions.value = options
  } catch (e) {
    console.error('加载探方列表失败', e)
  }
}

const columns = [
  {
    title: '编号',
    key: 'relicNo',
    width: 150
  },
  {
    title: '名称',
    key: 'name',
    width: 200
  },
  {
    title: '类别',
    key: 'category',
    width: 120
  },
  {
    title: '年代',
    key: 'era',
    width: 120
  },
  {
    title: '所属探方',
    key: 'excavationUnitId',
    width: 120,
    render: (row) => {
      if (row.excavationUnitId && excavationUnitMap.value[row.excavationUnitId]) {
        return excavationUnitMap.value[row.excavationUnitId]
      }
      return '-'
    }
  },
  {
    title: '材质',
    key: 'material',
    width: 120
  },
  {
    title: '出土日期',
    key: 'excavateDate',
    width: 120,
    render: (row) => formatDate(row.excavateDate)
  },
  {
    title: '保存状态',
    key: 'preservationStatus',
    width: 120,
    render: (row) => {
      const statusMap = {
        '完好': { type: 'success', text: '完好' },
        '轻微破损': { type: 'warning', text: '轻微破损' },
        '严重破损': { type: 'error', text: '严重破损' },
        '已修复': { type: 'info', text: '已修复' }
      }
      const status = statusMap[row.preservationStatus] || { type: 'default', text: row.preservationStatus }
      return h(NTag, { type: status.type }, () => status.text)
    }
  },
  {
    title: '三维坐标',
    key: 'coordinate',
    width: 200,
    render: (row) => {
      if (row.coordinate) {
        return `X: ${row.coordinate.x}, Y: ${row.coordinate.y}, Z: ${row.coordinate.z}`
      }
      return '未设置'
    }
  },
  {
    title: '操作',
    key: 'actions',
    width: 200,
    render: (row) => {
      return h(NSpace, null, () => [
        h(NButton, { size: 'small', type: 'primary', onClick: () => goToDetail(row.id) }, () => '详情'),
        h(NButton, { size: 'small', onClick: () => goToEdit(row.id) }, () => '编辑'),
        h(NButton, { size: 'small', type: 'error', onClick: () => handleDelete(row.id, row.name) }, () => '删除')
      ])
    }
  }
]

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  try {
    return format(new Date(dateStr), 'yyyy-MM-dd')
  } catch (e) {
    return dateStr
  }
}

const loadRelics = async () => {
  loading.value = true
  try {
    const res = await relicApi.getAllRelics()
    relics.value = res.data
  } catch (e) {
    message.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = async () => {
  if (selectedExcavationUnit.value) {
    loading.value = true
    try {
      const res = await relicApi.searchRelics({ excavationUnitId: selectedExcavationUnit.value })
      relics.value = res.data
    } catch (e) {
      message.error('搜索失败')
    } finally {
      loading.value = false
    }
    return
  }
  if (!searchKeyword.value.trim()) {
    loadRelics()
    return
  }
  loading.value = true
  try {
    const params = {}
    params[searchField.value] = searchKeyword.value
    const res = await relicApi.searchRelics(params)
    relics.value = res.data
  } catch (e) {
    message.error('搜索失败')
  } finally {
    loading.value = false
  }
}

const handleExcavationUnitFilter = async () => {
  if (selectedExcavationUnit.value) {
    loading.value = true
    try {
      const res = await relicApi.searchRelics({ excavationUnitId: selectedExcavationUnit.value })
      relics.value = res.data
    } catch (e) {
      message.error('按探方筛选失败')
    } finally {
      loading.value = false
    }
  } else {
    loadRelics()
  }
}

const handleReset = () => {
  searchKeyword.value = ''
  selectedExcavationUnit.value = null
  loadRelics()
}

const handleDelete = (id, name) => {
  dialog.warning({
    title: '确认删除',
    content: `确定要删除遗物「${name}」吗？此操作不可恢复。`,
    positiveText: '删除',
    negativeText: '取消',
    onPositiveClick: async () => {
      try {
        await relicApi.deleteRelic(id)
        message.success('删除成功')
        loadRelics()
      } catch (e) {
        message.error('删除失败')
      }
    }
  })
}

const goToCreate = () => {
  router.push('/relics/new')
}

const goToEdit = (id) => {
  router.push(`/relics/${id}/edit`)
}

const goToDetail = (id) => {
  router.push(`/relics/${id}`)
}

const getCurrentFilterParams = () => {
  const params = {}
  if (selectedExcavationUnit.value) {
    params.excavationUnitId = selectedExcavationUnit.value
  }
  if (searchKeyword.value.trim()) {
    params[searchField.value] = searchKeyword.value
  }
  return params
}

const transformRelicForExport = (relic) => {
  return {
    '编号': relic.relicNo || '',
    '名称': relic.name || '',
    '类别': relic.category || '',
    '材质': relic.material || '',
    '年代': relic.era || '',
    '所属探方': (relic.excavationUnitId && excavationUnitMap.value[relic.excavationUnitId]) ? excavationUnitMap.value[relic.excavationUnitId] : '',
    '出土地点': relic.excavationSite || '',
    '出土日期': formatDate(relic.excavateDate),
    '发掘人员': relic.excavator || '',
    '地层': relic.stratum || '',
    '保存状态': relic.preservationStatus || '',
    '三维坐标_X': relic.coordinate ? (relic.coordinate.x || '') : '',
    '三维坐标_Y': relic.coordinate ? (relic.coordinate.y || '') : '',
    '三维坐标_Z': relic.coordinate ? (relic.coordinate.z || '') : '',
    '坐标系': relic.coordinate ? (relic.coordinate.coordinateSystem || '') : '',
    '坐标备注': relic.coordinate ? (relic.coordinate.remark || '') : '',
    '描述': relic.description || '',
    '备注': relic.remark || '',
    '登记时间': formatDate(relic.createTime)
  }
}

const handleExport = async () => {
  exporting.value = true
  try {
    const params = getCurrentFilterParams()
    let res
    if (Object.keys(params).length > 0) {
      res = await relicApi.searchRelics(params)
    } else {
      res = await relicApi.getAllRelics()
    }
    const data = res.data || []
    if (data.length === 0) {
      message.warning('暂无数据可导出')
      return
    }
    const exportData = data.map(transformRelicForExport)
    const worksheet = XLSX.utils.json_to_sheet(exportData)
    const workbook = XLSX.utils.book_new()
    XLSX.utils.book_append_sheet(workbook, worksheet, '遗物列表')
    const excelBuffer = XLSX.write(workbook, { bookType: 'xlsx', type: 'array' })
    const blob = new Blob([excelBuffer], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8' })
    const fileName = `遗物列表_${format(new Date(), 'yyyyMMdd_HHmmss')}.xlsx`
    saveAs(blob, fileName)
    message.success(`导出成功，共 ${data.length} 条记录`)
  } catch (e) {
    console.error('导出失败', e)
    message.error('导出失败')
  } finally {
    exporting.value = false
  }
}

onMounted(() => {
  nextTick(() => {
    loadExcavationUnits()
    loadRelics()
  })
})
</script>
