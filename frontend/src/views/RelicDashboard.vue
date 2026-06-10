<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">遗物统计看板</h1>
      <n-button @click="loadStatistics">
        刷新数据
      </n-button>
    </div>

    <n-grid :cols="4" :x-gap="16" class="stats-cards">
      <n-grid-item>
        <n-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon total-icon">
              <span class="icon-text">总</span>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.total || 0 }}</div>
              <div class="stat-label">遗物总数</div>
            </div>
          </div>
        </n-card>
      </n-grid-item>
      <n-grid-item>
        <n-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon category-icon">
              <span class="icon-text">类</span>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.byCategory?.length || 0 }}</div>
              <div class="stat-label">类别数</div>
            </div>
          </div>
        </n-card>
      </n-grid-item>
      <n-grid-item>
        <n-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon era-icon">
              <span class="icon-text">年</span>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.byEra?.length || 0 }}</div>
              <div class="stat-label">年代数</div>
            </div>
          </div>
        </n-card>
      </n-grid-item>
      <n-grid-item>
        <n-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon material-icon">
              <span class="icon-text">材</span>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.byMaterial?.length || 0 }}</div>
              <div class="stat-label">材质数</div>
            </div>
          </div>
        </n-card>
      </n-grid-item>
    </n-grid>

    <n-grid :cols="2" :x-gap="16" class="chart-row">
      <n-grid-item>
        <n-card title="按类别统计" class="chart-card">
          <div ref="categoryBarChart" class="chart-container"></div>
        </n-card>
      </n-grid-item>
      <n-grid-item>
        <n-card title="类别占比" class="chart-card">
          <div ref="categoryPieChart" class="chart-container"></div>
        </n-card>
      </n-grid-item>
    </n-grid>

    <n-grid :cols="2" :x-gap="16" class="chart-row">
      <n-grid-item>
        <n-card title="按年代统计" class="chart-card">
          <div ref="eraBarChart" class="chart-container"></div>
        </n-card>
      </n-grid-item>
      <n-grid-item>
        <n-card title="年代占比" class="chart-card">
          <div ref="eraPieChart" class="chart-container"></div>
        </n-card>
      </n-grid-item>
    </n-grid>

    <n-grid :cols="2" :x-gap="16" class="chart-row">
      <n-grid-item>
        <n-card title="按材质统计" class="chart-card">
          <div ref="materialBarChart" class="chart-container"></div>
        </n-card>
      </n-grid-item>
      <n-grid-item>
        <n-card title="材质占比" class="chart-card">
          <div ref="materialPieChart" class="chart-container"></div>
        </n-card>
      </n-grid-item>
    </n-grid>

    <n-grid :cols="2" :x-gap="16" class="chart-row">
      <n-grid-item>
        <n-card title="按保存状态统计" class="chart-card">
          <div ref="statusBarChart" class="chart-container"></div>
        </n-card>
      </n-grid-item>
      <n-grid-item>
        <n-card title="保存状态占比" class="chart-card">
          <div ref="statusPieChart" class="chart-container"></div>
        </n-card>
      </n-grid-item>
    </n-grid>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onBeforeUnmount, nextTick, watch } from 'vue'
import { useRoute } from 'vue-router'
import { useMessage } from 'naive-ui'
import * as echarts from 'echarts'
import { relicApi } from '../api'

const route = useRoute()
const message = useMessage()

const loading = ref(false)
const statistics = reactive({
  total: 0,
  byCategory: [],
  byEra: [],
  byMaterial: [],
  byPreservationStatus: []
})

const categoryBarChart = ref(null)
const categoryPieChart = ref(null)
const eraBarChart = ref(null)
const eraPieChart = ref(null)
const materialBarChart = ref(null)
const materialPieChart = ref(null)
const statusBarChart = ref(null)
const statusPieChart = ref(null)

const chartInstances = []

const statusColorMap = {
  '完好': '#18a058',
  '轻微破损': '#f0a020',
  '严重破损': '#d03050',
  '已修复': '#2080f0'
}

const createBarChart = (el, data, color) => {
  const chart = echarts.init(el)
  chartInstances.push(chart)

  const total = data.reduce((sum, item) => sum + item.value, 0)

  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      },
      formatter: (params) => {
        const item = params[0]
        const percent = total > 0 ? ((item.value / total) * 100).toFixed(1) : 0
        return `${item.name}<br/>数量: ${item.value}<br/>占比: ${percent}%`
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      top: '10%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: data.map(item => item.name),
      axisLabel: {
        interval: 0,
        rotate: data.length > 5 ? 30 : 0,
        fontSize: 12
      }
    },
    yAxis: {
      type: 'value',
      minInterval: 1
    },
    series: [
      {
        type: 'bar',
        data: data.map(item => ({
          value: item.value,
          itemStyle: {
            color: color
          }
        })),
        barWidth: '50%',
        label: {
          show: true,
          position: 'top',
          fontSize: 12
        }
      }
    ]
  }

  chart.setOption(option)
  return chart
}

const createPieChart = (el, data, colorPalette) => {
  const chart = echarts.init(el)
  chartInstances.push(chart)

  const total = data.reduce((sum, item) => sum + item.value, 0)

  const option = {
    tooltip: {
      trigger: 'item',
      formatter: (params) => {
        const percent = total > 0 ? params.percent.toFixed(1) : 0
        return `${params.name}<br/>数量: ${params.value}<br/>占比: ${percent}%`
      }
    },
    legend: {
      orient: 'vertical',
      left: 'left',
      top: 'center'
    },
    series: [
      {
        type: 'pie',
        radius: ['40%', '70%'],
        center: ['60%', '50%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 4,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: true,
          formatter: '{b}: {d}%'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 14,
            fontWeight: 'bold'
          }
        },
        data: data.map(item => ({
          value: item.value,
          name: item.name,
          itemStyle: {
            color: colorPalette ? colorPalette[item.name] : undefined
          }
        }))
      }
    ]
  }

  chart.setOption(option)
  return chart
}

const disposeCharts = () => {
  chartInstances.forEach(chart => {
    chart.dispose()
  })
  chartInstances.length = 0
}

const initCharts = async () => {
  await nextTick()

  disposeCharts()

  if (statistics.byCategory?.length > 0) {
    createBarChart(categoryBarChart.value, statistics.byCategory, '#667eea')
    createPieChart(categoryPieChart.value, statistics.byCategory)
  }

  if (statistics.byEra?.length > 0) {
    createBarChart(eraBarChart.value, statistics.byEra, '#764ba2')
    createPieChart(eraPieChart.value, statistics.byEra)
  }

  if (statistics.byMaterial?.length > 0) {
    createBarChart(materialBarChart.value, statistics.byMaterial, '#f093fb')
    createPieChart(materialPieChart.value, statistics.byMaterial)
  }

  if (statistics.byPreservationStatus?.length > 0) {
    createBarChart(statusBarChart.value, statistics.byPreservationStatus, '#4facfe')
    createPieChart(statusPieChart.value, statistics.byPreservationStatus, statusColorMap)
  }
}

const loadStatistics = async () => {
  loading.value = true
  try {
    const res = await relicApi.getStatistics()
    statistics.total = res.data.total
    statistics.byCategory = res.data.byCategory || []
    statistics.byEra = res.data.byEra || []
    statistics.byMaterial = res.data.byMaterial || []
    statistics.byPreservationStatus = res.data.byPreservationStatus || []
    await initCharts()
    message.success('数据加载成功')
  } catch (e) {
    message.error('加载统计数据失败')
  } finally {
    loading.value = false
  }
}

const handleResize = () => {
  chartInstances.forEach(chart => {
    chart.resize()
  })
}

watch(
  () => route.fullPath,
  () => {
    if (route.name === 'RelicDashboard') {
      loadStatistics()
    }
  }
)

onMounted(() => {
  loadStatistics()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  disposeCharts()
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
.page-container {
  padding: 24px;
  max-width: 1400px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  margin: 0;
  color: #333;
}

.stats-cards {
  margin-bottom: 24px;
}

.stat-card {
  border-radius: 8px;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.icon-text {
  font-size: 20px;
  font-weight: 700;
}

.total-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.category-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.era-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.material-icon {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #333;
  line-height: 1.2;
}

.stat-label {
  font-size: 14px;
  color: #999;
  margin-top: 4px;
}

.chart-row {
  margin-bottom: 16px;
}

.chart-card {
  border-radius: 8px;
}

.chart-container {
  width: 100%;
  height: 320px;
}
</style>
