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
      </div>

      <n-table
        :columns="columns"
        :data="relics"
        :loading="loading"
        :pagination="pagination"
      />
    </n-card>
  </div>
</template>

<script setup>
import { ref, onMounted, h } from 'vue'
import { useRouter } from 'vue-router'
import { NTag, NSpace, NButton, useMessage, useDialog } from 'naive-ui'
import { relicApi } from '../api'
import { format } from 'date-fns'

const router = useRouter()
const message = useMessage()
const dialog = useDialog()

const relics = ref([])
const loading = ref(false)
const searchField = ref('name')
const searchKeyword = ref('')

const searchOptions = [
  { label: '名称', value: 'name' },
  { label: '编号', value: 'relicNo' },
  { label: '类别', value: 'category' }
]

const pagination = ref({
  pageSize: 10,
  showSizePicker: true,
  pageSizes: [10, 20, 50]
})

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

const handleReset = () => {
  searchKeyword.value = ''
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

onMounted(() => {
  loadRelics()
})
</script>
