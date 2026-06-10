<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">探方管理</h1>
      <n-button type="primary" @click="goToCreate">
        新增探方
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
        :data="units"
        :loading="loading"
        :pagination="pagination"
      />
    </n-card>
  </div>
</template>

<script setup>
import { ref, onMounted, h, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { NTag, NSpace, NButton, useMessage, useDialog } from 'naive-ui'
import { excavationUnitApi } from '../api'
import { format } from 'date-fns'

const router = useRouter()
const route = useRoute()
const message = useMessage()
const dialog = useDialog()

const units = ref([])
const loading = ref(false)
const searchField = ref('unitNo')
const searchKeyword = ref('')

const searchOptions = [
  { label: '探方编号', value: 'unitNo' },
  { label: '负责人', value: 'director' },
  { label: '状态', value: 'status' }
]

const pagination = ref({
  pageSize: 10,
  showSizePicker: true,
  pageSizes: [10, 20, 50]
})

const statusMap = {
  '未开始': { type: 'default', text: '未开始' },
  '发掘中': { type: 'primary', text: '发掘中' },
  '已暂停': { type: 'warning', text: '已暂停' },
  '已完成': { type: 'success', text: '已完成' }
}

const columns = [
  {
    title: '探方编号',
    key: 'unitNo',
    width: 150
  },
  {
    title: '位置',
    key: 'location',
    width: 200
  },
  {
    title: '尺寸（长×宽×深）',
    key: 'size',
    width: 180,
    render: (row) => {
      const depth = row.depth ? ` × ${row.depth}` : ''
      return `${row.length} × ${row.width}${depth} m`
    }
  },
  {
    title: '发掘状态',
    key: 'status',
    width: 120,
    render: (row) => {
      const status = statusMap[row.status] || { type: 'default', text: row.status }
      return h(NTag, { type: status.type }, () => status.text)
    }
  },
  {
    title: '负责人',
    key: 'director',
    width: 120
  },
  {
    title: '出土遗物数',
    key: 'relics',
    width: 100,
    render: (row) => row.relics ? row.relics.length : 0
  },
  {
    title: '创建时间',
    key: 'createTime',
    width: 120,
    render: (row) => formatDate(row.createTime)
  },
  {
    title: '操作',
    key: 'actions',
    width: 200,
    render: (row) => {
      return h(NSpace, null, () => [
        h(NButton, { size: 'small', onClick: () => goToEdit(row.id) }, () => '编辑'),
        h(NButton, { size: 'small', type: 'error', onClick: () => handleDelete(row.id, row.unitNo) }, () => '删除')
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

const loadUnits = async () => {
  loading.value = true
  try {
    const res = await excavationUnitApi.getAllUnits()
    units.value = res.data
  } catch (e) {
    message.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = async () => {
  if (!searchKeyword.value.trim()) {
    loadUnits()
    return
  }
  loading.value = true
  try {
    const params = {}
    params[searchField.value] = searchKeyword.value
    const res = await excavationUnitApi.searchUnits(params)
    units.value = res.data
  } catch (e) {
    message.error('搜索失败')
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  searchKeyword.value = ''
  loadUnits()
}

const handleDelete = (id, unitNo) => {
  dialog.warning({
    title: '确认删除',
    content: `确定要删除探方「${unitNo}」吗？该探方下的遗物将解除关联。`,
    positiveText: '删除',
    negativeText: '取消',
    onPositiveClick: async () => {
      try {
        await excavationUnitApi.deleteUnit(id)
        message.success('删除成功')
        loadUnits()
      } catch (e) {
        message.error('删除失败')
      }
    }
  })
}

const goToCreate = () => {
  router.push('/excavation-units/new')
}

const goToEdit = (id) => {
  router.push(`/excavation-units/${id}/edit`)
}

watch(
  () => route.fullPath,
  () => {
    if (route.name === 'ExcavationUnitList') {
      loadUnits()
    }
  }
)

onMounted(() => {
  loadUnits()
})
</script>
