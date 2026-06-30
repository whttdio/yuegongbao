<template>
  <div class="app-container ygb-page">
    <section class="gov-page-header ygb-page__header">
      <div>
        <p class="ygb-page__eyebrow">驾驶舱</p>
        <h1 class="ygb-page__title">地图可视化</h1>
        <p class="ygb-page__desc">展示驾驶舱地图要素集合，包含要素类型、状态、区域和 GeoJSON 属性。</p>
      </div>
    </section>

    <div class="ygb-summary-grid">
      <div v-for="item in summaryCards" :key="item.key" class="ygb-summary-card" :class="item.cardClass">
        <div class="ygb-summary-card__label">{{ item.label }}</div>
        <div class="ygb-summary-card__value">
          {{ item.value }}
          <span class="ygb-summary-card__unit">{{ item.unit }}</span>
        </div>
        <div class="ygb-summary-card__note">{{ item.note }}</div>
      </div>
    </div>

    <el-card class="search-card ygb-search-card" shadow="never">
      <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
        <el-form-item label="区域" prop="regionCode">
          <el-input v-model="queryParams.regionCode" placeholder="请输入区域编码" clearable style="width: 180px" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="统计日期" prop="statDate">
          <el-date-picker v-model="queryParams.statDate" type="date" placeholder="请选择日期" format="YYYY-MM-DD" value-format="YYYY-MM-DD" style="width: 170px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="toolbar-card ygb-toolbar-card" shadow="never">
      <el-row :gutter="10">
        <el-col :span="1.5">
          <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['ygb:cockpit:export']">导出趋势</el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="refreshAll" />
      </el-row>
    </el-card>

    <el-card class="table-card ygb-table-card" shadow="never">
      <template #header>
        <div class="ygb-card-head ygb-card-head--between">
          <div>
            <div class="ygb-card-head__title">地图要素台账</div>
            <div class="ygb-card-head__desc">按区域展示企业、预警和风险点位分布</div>
          </div>
          <div class="ygb-card-head__desc">统计日期 {{ mapStatDate || '-' }}，要素 {{ total }} 个</div>
        </div>
      </template>
      <el-table v-loading="loading" :data="list">
        <el-table-column label="要素ID" align="center" prop="id" width="90" />
        <el-table-column label="要素名称" align="center" prop="featureName" min-width="180" show-overflow-tooltip />
        <el-table-column label="要素类型" align="center" prop="featureType" width="120" />
        <el-table-column label="几何类型" align="center" prop="geometryType" width="110" />
        <el-table-column label="区域编码" align="center" prop="regionCode" width="140" />
        <el-table-column label="状态" align="center" prop="featureStatus" width="100" />
        <el-table-column label="来源" align="center" prop="sourceMode" width="120" />
        <el-table-column label="属性摘要" align="center" prop="propertySummary" min-width="260" show-overflow-tooltip />
        <el-table-column class-name="table-fill-column" min-width="1" />
        <el-table-column label="操作" fixed="right" align="center" width="120" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-button link type="info" icon="View" @click.stop="openDetail(scope.row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <page-detail-dialog v-model="detailOpen" title="地图要素详情" width="820px">
      <template v-if="detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="要素ID">{{ detail.id ?? '-' }}</el-descriptions-item>
          <el-descriptions-item label="要素名称">{{ detail.featureName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="要素类型">{{ detail.featureType || '-' }}</el-descriptions-item>
          <el-descriptions-item label="状态">{{ detail.featureStatus || '-' }}</el-descriptions-item>
          <el-descriptions-item label="区域编码">{{ detail.regionCode || '-' }}</el-descriptions-item>
          <el-descriptions-item label="几何类型">{{ detail.geometryType || '-' }}</el-descriptions-item>
          <el-descriptions-item label="来源">{{ detail.sourceMode || '-' }}</el-descriptions-item>
          <el-descriptions-item label="坐标" :span="2">{{ detail.coordinatesText || '-' }}</el-descriptions-item>
          <el-descriptions-item label="属性" :span="2">{{ detail.propertySummary || '-' }}</el-descriptions-item>
        </el-descriptions>
      </template>
    </page-detail-dialog>
  </div>
</template>

<script setup name="YgbCockpitMap">
import { ref, onMounted, getCurrentInstance } from 'vue'
import { getCockpitMap, getCockpitIndicators } from '@/api/ygb/cockpit'

function valueOrDefault(value, fallback = 0) {
  return value === undefined || value === null ? fallback : value
}

function toFeatureRow(feature) {
  const properties = feature?.properties || {}
  const geometry = feature?.geometry || {}
  const propertySummary = Object.entries(properties)
    .map(([key, value]) => `${key}: ${value}`)
    .join('；')
  return {
    id: feature?.id,
    featureType: properties.featureType,
    featureName: properties.featureName,
    featureStatus: properties.featureStatus,
    regionCode: properties.regionCode,
    sourceMode: properties.sourceMode,
    geometryType: geometry.type,
    coordinatesText: JSON.stringify(geometry.coordinates || []),
    propertySummary
  }
}

const loading = ref(false)
const list = ref([])
const total = ref(0)
const showSearch = ref(true)
const detailOpen = ref(false)
const detail = ref(null)
const mapStatDate = ref('')
const { proxy } = getCurrentInstance()

const queryParams = ref({
  regionCode: undefined,
  statDate: undefined
})

const summaryCards = ref([])

async function getList() {
  loading.value = true
  try {
    const res = await getCockpitMap(queryParams.value)
    const data = res.data || {}
    mapStatDate.value = data.statDate || queryParams.value.statDate || ''
    list.value = (data.features || []).map(toFeatureRow)
    total.value = list.value.length
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

async function getSummary() {
  try {
    const res = await getCockpitIndicators(queryParams.value)
    const data = res.data || res || {}
    summaryCards.value = [
      { key: 'dispatchCompanyCount', label: '派遣单位', value: valueOrDefault(data.dispatchCompanyCount, 0), unit: '家', note: '当前区域', cardClass: '' },
      { key: 'employerCount', label: '用工单位', value: valueOrDefault(data.employerCount, 0), unit: '家', note: '当前区域', cardClass: '' },
      { key: 'highRiskEnterpriseCount', label: '高风险企业', value: valueOrDefault(data.highRiskEnterpriseCount, 0), unit: '家', note: '需重点关注', cardClass: 'danger' },
      { key: 'pendingWarningCount', label: '待处置预警', value: valueOrDefault(data.pendingWarningCount, 0), unit: '条', note: '未闭环预警', cardClass: 'warning' }
    ]
  } catch (e) {
    console.error(e)
  }
}

function refreshAll() {
  getList()
  getSummary()
}

function handleQuery() {
  refreshAll()
}

function resetQuery() {
  queryParams.value = {
    regionCode: undefined,
    statDate: undefined
  }
  refreshAll()
}

function openDetail(row) {
  detail.value = row
  detailOpen.value = true
}

function handleExport() {
  proxy.download('ygb/cockpit/export', { ...queryParams.value }, `cockpit_map_${Date.now()}.xlsx`)
}

onMounted(() => {
  refreshAll()
})
</script>
