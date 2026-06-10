<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">修复工单管理</h1>
      <n-button type="primary" @click="goToCreate">
        新增修复工单
      </n-button>
    </div>

    <n-card class="card-margin">
      <div class="search-bar">
        <n-select
          v-model:value="statusFilter"
          :options="statusOptions"
          placeholder="全部状态"
          clearable
          style="width: 160px"
          @update:value="handleSearch"
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
        :data="orders"
        :loading="loading"
      />
    </n-card>
  </div>
</template>

<script setup>
import { ref, onMounted, h, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { NTag, NSpace, NButton, useMessage, useDialog } from 'naive-ui'
import { repairOrderApi, relicApi } from '../api'
import { format } from 'date-fns'
import * as XLSX from 'xlsx'
import { saveAs } from 'file-saver'

const router = useRouter()
const message = useMessage()
const dialog = useDialog()

const orders = ref([])
const loading = ref(false)
const exporting = ref(false)
const searchField = ref('orderNo')
const searchKeyword = ref('')
const statusFilter = ref(null)

const statusOptions = [
  { label: '待修复', value: 'PENDING' },
  { label: '修复中', value: 'IN_PROGRESS' },
  { label: '已完成', value: 'COMPLETED' }
]

const searchOptions = [
  { label: '工单编号', value: 'orderNo' },
  { label: '遗物编号', value: 'relicNo' },
  { label: '遗物名称', value: 'relicName' }
]

const statusMap = {
  PENDING: { type: 'warning', text: '待修复' },
  IN_PROGRESS: { type: 'info', text: '修复中' },
  COMPLETED: { type: 'success', text: '已完成' }
}

const columns = [
  {
    title: '工单编号',
    key: 'orderNo',
    width: 180
  },
  {
    title: '遗物编号',
    key: 'relicNo',
    width: 150
  },
  {
    title: '遗物名称',
    key: 'relicName',
    width: 200
  },
  {
    title: '状态',
    key: 'status',
    width: 100,
    render: (row) => {
      const status = statusMap[row.status] || { type: 'default', text: row.status }
      return h(NTag, { type: status.type }, () => status.text)
    }
  },
  {
    title: '申请人',
    key: 'applicant',
    width: 120
  },
  {
    title: '申请日期',
    key: 'applyDate',
    width: 120,
    render: (row) => formatDate(row.applyDate)
  },
  {
    title: '修复人员',
    key: 'repairer',
    width: 120,
    render: (row) => row.repairer || '-'
  },
  {
    title: '开始日期',
    key: 'startDate',
    width: 120,
    render: (row) => formatDate(row.startDate)
  },
  {
    title: '完成日期',
    key: 'actualCompleteDate',
    width: 120,
    render: (row) => formatDate(row.actualCompleteDate)
  },
  {
    title: '操作',
    key: 'actions',
    width: 280,
    render: (row) => {
      const actions = []
      actions.push(h(NButton, { size: 'small', type: 'primary', onClick: () => goToDetail(row.id) }, () => '详情'))

      if (row.status === 'PENDING') {
        actions.push(h(NButton, { size: 'small', type: 'info', onClick: () => handleStartRepair(row) }, () => '开始修复'))
      }
      if (row.status === 'IN_PROGRESS') {
        actions.push(h(NButton, { size: 'small', type: 'success', onClick: () => handleCompleteRepair(row) }, () => '完成修复'))
      }
      if (row.status !== 'COMPLETED') {
        actions.push(h(NButton, { size: 'small', onClick: () => goToEdit(row.id) }, () => '编辑'))
      }
      actions.push(h(NButton, { size: 'small', type: 'error', onClick: () => handleDelete(row.id, row.orderNo) }, () => '删除'))

      return h(NSpace, null, () => actions)
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

const loadOrders = async () => {
  loading.value = true
  try {
    const res = await repairOrderApi.getAllOrders()
    orders.value = res.data
  } catch (e) {
    message.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = async () => {
  loading.value = true
  try {
    const params = {}
    if (statusFilter.value) {
      params.status = statusFilter.value
    }
    if (searchKeyword.value.trim()) {
      params[searchField.value] = searchKeyword.value
    }
    const res = await repairOrderApi.searchOrders(params)
    orders.value = res.data
  } catch (e) {
    message.error('搜索失败')
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  searchKeyword.value = ''
  statusFilter.value = null
  loadOrders()
}

const handleStartRepair = (row) => {
  dialog.warning({
    title: '开始修复',
    content: `确定要开始工单「${row.orderNo}」的修复工作吗？`,
    positiveText: '确定',
    negativeText: '取消',
    onPositiveClick: async () => {
      try {
        await repairOrderApi.startRepair(row.id)
        message.success('已开始修复')
        loadOrders()
      } catch (e) {
        message.error('操作失败')
      }
    }
  })
}

const handleCompleteRepair = (row) => {
  dialog.warning({
    title: '完成修复',
    content: `确定要完成工单「${row.orderNo}」的修复工作吗？`,
    positiveText: '确定',
    negativeText: '取消',
    onPositiveClick: async () => {
      try {
        await repairOrderApi.completeRepair(row.id)
        message.success('修复已完成')
        loadOrders()
      } catch (e) {
        message.error('操作失败')
      }
    }
  })
}

const handleDelete = (id, orderNo) => {
  dialog.warning({
    title: '确认删除',
    content: `确定要删除工单「${orderNo}」吗？此操作不可恢复。`,
    positiveText: '删除',
    negativeText: '取消',
    onPositiveClick: async () => {
      try {
        await repairOrderApi.deleteOrder(id)
        message.success('删除成功')
        loadOrders()
      } catch (e) {
        message.error('删除失败')
      }
    }
  })
}

const goToCreate = () => {
  router.push('/repair-orders/new')
}

const goToEdit = (id) => {
  router.push(`/repair-orders/${id}/edit`)
}

const goToDetail = (id) => {
  router.push(`/repair-orders/${id}`)
}

const getCurrentFilterParams = () => {
  const params = {}
  if (statusFilter.value) {
    params.status = statusFilter.value
  }
  if (searchKeyword.value.trim()) {
    params[searchField.value] = searchKeyword.value
  }
  return params
}

const statusTextMap = {
  PENDING: '待修复',
  IN_PROGRESS: '修复中',
  COMPLETED: '已完成'
}

const transformOrderForExport = (order) => {
  return {
    '工单编号': order.orderNo || '',
    '遗物编号': order.relicNo || '',
    '遗物名称': order.relicName || '',
    '状态': statusTextMap[order.status] || order.status || '',
    '申请人': order.applicant || '',
    '申请日期': formatDate(order.applyDate),
    '修复人员': order.repairer || '',
    '开始日期': formatDate(order.startDate),
    '预计完成日期': formatDate(order.expectedCompleteDate),
    '实际完成日期': formatDate(order.actualCompleteDate),
    '破损情况': order.damageDescription || '',
    '修复要求': order.repairRequirement || '',
    '修复过程': order.repairProcess || '',
    '修复结果': order.repairResult || '',
    '备注': order.remark || ''
  }
}

const handleExport = async () => {
  exporting.value = true
  try {
    const params = getCurrentFilterParams()
    let res
    if (Object.keys(params).length > 0) {
      res = await repairOrderApi.searchOrders(params)
    } else {
      res = await repairOrderApi.getAllOrders()
    }
    const data = res.data || []
    if (data.length === 0) {
      message.warning('暂无数据可导出')
      return
    }
    const exportData = data.map(transformOrderForExport)
    const worksheet = XLSX.utils.json_to_sheet(exportData)
    const workbook = XLSX.utils.book_new()
    XLSX.utils.book_append_sheet(workbook, worksheet, '修复工单列表')
    const excelBuffer = XLSX.write(workbook, { bookType: 'xlsx', type: 'array' })
    const blob = new Blob([excelBuffer], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8' })
    const fileName = `修复工单列表_${format(new Date(), 'yyyyMMdd_HHmmss')}.xlsx`
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
    loadOrders()
  })
})
</script>
