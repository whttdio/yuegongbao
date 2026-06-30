<template>
  <div class="app-container ygb-page">
    <section class="gov-page-header ygb-page__header">
      <div>
        <p class="ygb-page__eyebrow">工伤监管</p>
        <h1 class="ygb-page__title">工伤统计分析</h1>
        <p class="ygb-page__desc">从行业、区域、企业、时间等维度对工伤事件进行统计分析，发现高发规律。</p>
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
        <el-form-item label="人员姓名" prop="personName">
          <el-input v-model="queryParams.personName" placeholder="请输入人员姓名" clearable style="width: 180px" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="企业" prop="enterpriseId">
          <el-select v-model="queryParams.enterpriseId" placeholder="请选择企业" clearable filterable style="width: 200px">
            <el-option v-for="item in enterpriseOptions" :key="item.enterpriseId" :label="item.enterpriseName" :value="item.enterpriseId" />
          </el-select>
        </el-form-item>
        <el-form-item label="事件状态" prop="injuryStatus">
          <el-select v-model="queryParams.injuryStatus" placeholder="请选择事件状态" clearable style="width: 140px">
            <el-option v-for="item in injuryStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
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
          <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['ygb:injuryEvent:export']">导出</el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>
    </el-card>

    <el-card class="table-card ygb-table-card" shadow="never">
      <template #header>
        <div class="ygb-card-head ygb-card-head--between">
          <div>
            <div class="ygb-card-head__title">工伤维度分析</div>
            <div class="ygb-card-head__desc">按状态、区域、企业和预警维度输出统计结果，用于识别工伤高发对象。</div>
          </div>
          <div class="ygb-card-head__desc">分析样本 {{ analysisData.totalCount || 0 }} 件</div>
        </div>
      </template>
      <div class="analysis-summary">
        <div class="analysis-summary__item">
          <span>认定中</span>
          <strong>{{ analysisData.recognizingCount || 0 }}</strong>
        </div>
        <div class="analysis-summary__item warning">
          <span>待遇申领中</span>
          <strong>{{ analysisData.claimingCount || 0 }}</strong>
        </div>
        <div class="analysis-summary__item danger">
          <span>超期事件</span>
          <strong>{{ analysisData.overdueCount || 0 }}</strong>
        </div>
        <div class="analysis-summary__item">
          <span>预警占比</span>
          <strong>{{ warningRate }}%</strong>
        </div>
      </div>
      <el-row :gutter="16" class="analysis-grid">
        <el-col :xs="24" :md="12">
          <div class="analysis-panel">
            <div class="analysis-panel__title">事件状态分布</div>
            <el-table :data="statusStats" size="small" border>
              <el-table-column label="状态" prop="label" />
              <el-table-column label="数量" prop="count" width="100" align="right" />
            </el-table>
          </div>
        </el-col>
        <el-col :xs="24" :md="12">
          <div class="analysis-panel">
            <div class="analysis-panel__title">预警分布</div>
            <el-table :data="warningStats" size="small" border>
              <el-table-column label="类型" prop="label" />
              <el-table-column label="数量" prop="count" width="100" align="right" />
            </el-table>
          </div>
        </el-col>
        <el-col :xs="24" :md="12">
          <div class="analysis-panel">
            <div class="analysis-panel__title">区域高发排行</div>
            <el-table :data="regionStats" size="small" border>
              <el-table-column label="区域" prop="label" />
              <el-table-column label="数量" prop="count" width="100" align="right" />
            </el-table>
          </div>
        </el-col>
        <el-col :xs="24" :md="12">
          <div class="analysis-panel">
            <div class="analysis-panel__title">企业高发排行</div>
            <el-table :data="enterpriseStats" size="small" border>
              <el-table-column label="企业" prop="label" show-overflow-tooltip />
              <el-table-column label="数量" prop="count" width="100" align="right" />
            </el-table>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <el-card class="table-card ygb-table-card" shadow="never">
      <template #header>
        <div class="ygb-card-head ygb-card-head--between">
          <div>
            <div class="ygb-card-head__title">工伤统计分析台账</div>
            <div class="ygb-card-head__desc">统计工伤事件、风险类型、区域分布和处置进展，支撑风险研判。</div>
          </div>
          <div class="ygb-card-head__desc">当前总量 {{ total }} 条</div>
        </div>
      </template>
      <el-table v-loading="loading" :data="list" @selection-change="handleSelectionChange">
        <el-table-column label="ID" align="center" prop="eventId" width="80" />
        <el-table-column label="事件ID" align="center" prop="eventId" width="90" />
        <el-table-column label="企业" align="center" prop="enterpriseName" min-width="200" />
        <el-table-column label="人员" align="center" prop="personName" width="120" />
        <el-table-column label="事故日期" align="center" width="120">
          <template #default="scope">
            <span>{{ parseTime(scope.row.eventDate, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="受伤部位" align="center" prop="injuryPart" width="120" />
        <el-table-column label="事件状态" align="center" prop="injuryStatus" width="120">
          <template #default="scope">
            <dict-tag :options="injuryStatusOptions" :value="scope.row.injuryStatus" />
          </template>
        </el-table-column>
        <el-table-column class-name="table-fill-column" min-width="1" />
        <el-table-column label="操作" fixed="right" align="center" width="120" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-button link type="info" icon="View" @click.stop="openDetail(scope.row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
      <pagination
        v-show="total > 0"
        :total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
      />
    </el-card>

    <page-detail-dialog v-model="detailOpen" title="工伤统计分析详情" width="720px">
      <template v-if="detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="事件ID" :span="1">{{ detail.eventId || '-' }}</el-descriptions-item>
          <el-descriptions-item label="企业" :span="1">{{ detail.enterpriseName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="人员" :span="1">{{ detail.personName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="事故日期" :span="1">{{ parseTime(detail.eventDate, '{y}-{m}-{d}') || '-' }}</el-descriptions-item>
          <el-descriptions-item label="受伤部位" :span="1">{{ detail.injuryPart || '-' }}</el-descriptions-item>
          <el-descriptions-item label="事件状态" :span="1">{{ formatInjuryStatus(detail.injuryStatus) }}</el-descriptions-item>
          <el-descriptions-item label="受伤地点" :span="1">{{ detail.injuryLocation || '-' }}</el-descriptions-item>
          <el-descriptions-item label="预警状态" :span="1">{{ detail.warningStatus || '-' }}</el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ detail.remark || '-' }}</el-descriptions-item>
        </el-descriptions>
      </template>
    </page-detail-dialog>
  </div>
</template>

<script setup name="YgbInjuryStatisticalAnalysis">
import { computed, ref, onMounted, getCurrentInstance } from 'vue'
import { listInjuryEvent, getInjuryEventAnalysis, getInjuryEventSummary } from '@/api/ygb/injuryEvent'
import { parseTime } from '@/utils/yuegongbao'
import { optionselectEnterprise } from '@/api/ygb/enterprise'

function formatMoney(value) {
  if (value === undefined || value === null || value === '') return '-'
  return '¥ ' + Number(value).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}
function valueOrDefault(value, fallback = 0) {
  return value === undefined || value === null ? fallback : value
}

const loading = ref(false)
const list = ref([])
const total = ref(0)
const showSearch = ref(true)
const detailOpen = ref(false)
const detail = ref(null)
const enterpriseOptions = ref([])
const { proxy } = getCurrentInstance()
const analysisData = ref({})
const injuryStatusOptions = [
  { label: '已报告', value: '0' },
  { label: '认定中', value: '1' },
  { label: '已认定', value: '2' },
  { label: '待遇申领中', value: '3' },
  { label: '已完结', value: '4' }
]

const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  personName: undefined,
  enterpriseId: undefined,
  injuryStatus: undefined
})

const summaryCards = ref([])
const statusStats = computed(() => normalizeStats(analysisData.value.statusStats, formatInjuryStatus))
const warningStats = computed(() => normalizeStats(analysisData.value.warningStats, formatWarningStatus))
const regionStats = computed(() => normalizeStats(analysisData.value.regionStats).slice(0, 10))
const enterpriseStats = computed(() => normalizeStats(analysisData.value.enterpriseStats).slice(0, 10))
const warningRate = computed(() => {
  const total = Number(analysisData.value.totalCount || 0)
  const warning = warningStats.value.find(item => item.key === 'warning')?.count || 0
  if (!total) return '0.00'
  return ((Number(warning) / total) * 100).toFixed(2)
})

async function getList() {
  loading.value = true
  try {
    const res = await listInjuryEvent(queryParams.value)
    list.value = res.rows || res.data || []
    total.value = res.total || 0
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}



async function getSummary() {
  try {
    const res = await getInjuryEventSummary(queryParams.value)
    const data = res.data || res || {}
    summaryCards.value = [
      { key: 'totalCount', label: '工伤事件', value: valueOrDefault(data.totalCount, 0), unit: '件', note: '当前范围', cardClass: '' },
      { key: 'warningCount', label: '预警事件', value: valueOrDefault(data.warningCount, 0), unit: '件', note: '重点关注', cardClass: 'danger' },
      { key: 'recognizingCount', label: '认定中', value: valueOrDefault(data.recognizingCount, 0), unit: '件', note: '认定流程', cardClass: 'warning' },
      { key: 'finishedCount', label: '已完结', value: valueOrDefault(data.finishedCount, 0), unit: '件', note: '闭环事件', cardClass: 'success' }
    ].filter(Boolean)
  } catch (e) {
    console.error(e)
  }
}

async function getAnalysis() {
  try {
    const res = await getInjuryEventAnalysis(queryParams.value)
    analysisData.value = res.data || {}
  } catch (e) {
    console.error(e)
  }
}

function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
  getSummary()
  getAnalysis()
}

function resetQuery() {
  queryParams.value = {
    pageNum: 1,
    pageSize: 10,
    personName: undefined,
    enterpriseId: undefined,
    injuryStatus: undefined
  }
  getList()
  getSummary()
  getAnalysis()
}

function handleSelectionChange(selection) {
}

function openDetail(row) {
  detail.value = row
  detailOpen.value = true
}

function handleExport() {
  proxy.download('ygb/injury/event/export', { ...queryParams.value }, `injury_statistical_analysis_${Date.now()}.xlsx`)
}

function formatInjuryStatus(value) {
  return injuryStatusOptions.find(item => item.value === value)?.label || '-'
}

function formatWarningStatus(value) {
  if (value === 'warning') return '预警事件'
  if (value === 'normal') return '正常事件'
  return value || '-'
}

function normalizeStats(list = [], formatter) {
  return [...(list || [])]
    .map(item => {
      const key = item.dimension == null ? '-' : String(item.dimension)
      return {
        key,
        label: formatter ? formatter(key) : key,
        count: Number(item.count || 0)
      }
    })
    .sort((a, b) => b.count - a.count)
}

async function loadEnterpriseOptions() {
  try {
    const res = await optionselectEnterprise()
    enterpriseOptions.value = res.data || []
  } catch (e) {}
}

onMounted(() => {
  loadEnterpriseOptions()
  getList()
  getSummary()
  getAnalysis()
})
</script>

<style scoped>
.analysis-summary {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
  margin-bottom: 16px;
}

.analysis-summary__item {
  padding: 14px 16px;
  border: 1px solid var(--el-border-color-light);
  border-radius: 6px;
  background: var(--el-bg-color);
}

.analysis-summary__item span {
  display: block;
  color: var(--el-text-color-secondary);
  font-size: 13px;
}

.analysis-summary__item strong {
  display: block;
  margin-top: 8px;
  color: var(--el-text-color-primary);
  font-size: 24px;
  font-weight: 600;
}

.analysis-summary__item.warning strong {
  color: var(--el-color-warning);
}

.analysis-summary__item.danger strong {
  color: var(--el-color-danger);
}

.analysis-grid {
  row-gap: 16px;
}

.analysis-panel {
  min-height: 220px;
}

.analysis-panel__title {
  margin-bottom: 10px;
  color: var(--el-text-color-primary);
  font-size: 14px;
  font-weight: 600;
}

@media (max-width: 768px) {
  .analysis-summary {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}
</style>
