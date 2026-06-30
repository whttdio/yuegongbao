<template>
  <div class="app-container warning-stat-page" :class="pageClass">
    <section class="stat-hero" :style="heroStyle">
      <div>
        <p class="stat-hero__eyebrow">{{ pageMeta.eyebrow }}</p>
        <h1 class="stat-hero__title">预警统计分析</h1>
        <p class="stat-hero__desc">
          按级别、来源、区域与处理状态汇总预警工单，并评估超48小时待办与72小时内办结时效。
        </p>
      </div>
      <div class="stat-hero__meta">
        <div class="stat-hero__meta-label">当前筛选预警总量</div>
        <div class="stat-hero__meta-value">{{ totalCount }}<span>条</span></div>
        <div class="stat-hero__meta-note">办结率 {{ closedRate }}%，超48小时待办 {{ overduePendingCount }} 条</div>
        <el-button type="primary" plain class="stat-hero__action" @click="goWarningCenter">进入预警中心</el-button>
      </div>
    </section>

    <el-card class="stat-search-card" shadow="never">
      <el-form :inline="true" class="stat-filter-form">
        <el-form-item label="区域">
          <el-select v-model="queryParams.regionCode" clearable style="width: 180px">
            <el-option v-for="item in regionOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="企业">
          <el-select v-model="queryParams.enterpriseId" :disabled="isEnterpriseFilterLocked" clearable filterable style="width: 220px">
            <el-option
              v-for="item in enterpriseOptions"
              :key="item.enterpriseId"
              :label="item.enterpriseName"
              :value="item.enterpriseId"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" icon="Search" @click="loadAll">查询</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <div class="stat-kpi-grid">
      <div v-for="item in summaryCards" :key="item.key" class="stat-kpi-card" :class="item.cardClass">
        <div class="stat-kpi-card__label">{{ item.label }}</div>
        <div class="stat-kpi-card__value">
          {{ item.value }}
          <span class="stat-kpi-card__unit">{{ item.unit }}</span>
        </div>
        <div class="stat-kpi-card__note">{{ item.note }}</div>
      </div>
    </div>

    <div class="stat-section-title">
      <strong>时效评估</strong>
      <span>关注超48小时未闭环与72小时内高效办结的预警工单</span>
    </div>

    <div class="stat-sla-grid">
      <div v-for="item in slaCards" :key="item.key" class="stat-kpi-card" :class="item.cardClass">
        <div class="stat-kpi-card__label">{{ item.label }}</div>
        <div class="stat-kpi-card__value">
          {{ item.value }}
          <span class="stat-kpi-card__unit">{{ item.unit }}</span>
        </div>
        <div class="stat-kpi-card__note">{{ item.note }}</div>
      </div>
    </div>

    <div class="stat-section-title">
      <strong>多维分布</strong>
      <span>点击维度可下钻至预警中心并带入筛选条件</span>
    </div>

    <div class="stat-analysis-grid">
      <el-card v-for="section in analysisSections" :key="section.key" class="stat-analysis-card" shadow="never">
        <template #header>
          <div class="stat-analysis-card__head">
            <div>
              <div class="stat-analysis-card__title">{{ section.title }}</div>
              <div class="stat-analysis-card__desc">{{ section.desc }}</div>
            </div>
            <span class="stat-analysis-card__total">共 {{ section.total }} 条</span>
          </div>
        </template>
        <div v-if="section.items.length" class="stat-analysis-list">
          <button
            v-for="item in section.items"
            :key="`${section.key}-${item.dimensionKey}`"
            type="button"
            class="stat-analysis-item"
            @click="drillDown(section.filterField, item)"
          >
            <div class="stat-analysis-item__row">
              <span class="stat-analysis-item__label">{{ item.dimensionLabel }}</span>
              <span class="stat-analysis-item__value">{{ item.dimensionCount }} 条 · {{ item.percent }}%</span>
            </div>
            <el-progress
              :percentage="item.percent"
              :show-text="false"
              :stroke-width="8"
              :color="section.progressColor"
            />
          </button>
        </div>
        <el-empty v-else description="暂无统计数据" :image-size="56" />
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getWarningAnalysis, getWarningSummary } from '@/api/ygb/warning'
import { optionselectEnterprise } from '@/api/ygb/enterprise'
import { getActivePortalCode } from '@/utils/portal'
import { filterAuthorizedRegionOptions } from '@/utils/regionScope'
import {
  applyLockedEnterpriseQuery,
  filterAuthorizedEnterpriseOptions,
  isEnterpriseFilterLocked,
  lockedEnterpriseId
} from '@/utils/enterpriseScope'
import {
  baseRegionOptions,
  formatRegionName,
  optionLabel,
  sourceModuleOptions,
  warnLevelOptions,
  warnStatusOptions
} from '@/views/warning/useWarningPage'

const props = defineProps({
  portalCode: {
    type: String,
    default: ''
  }
})

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const warningSummary = ref({})
const warningAnalysis = ref({})
const enterpriseOptions = ref([])
const regionOptions = filterAuthorizedRegionOptions(baseRegionOptions)

const queryParams = reactive({
  regionCode: undefined,
  enterpriseId: undefined
})

const activePortalCode = computed(() => props.portalCode || getActivePortalCode(route) || 'ygb')
const pageClass = computed(() => (activePortalCode.value === 'azb' ? 'warning-stat-page--azb' : 'warning-stat-page--ygb'))

const pageMeta = computed(() => (
  activePortalCode.value === 'azb'
    ? { eyebrow: '风险处置', primary: '#0b6b78', accent: 'rgba(11, 107, 120, 0.10)', soft: '#f3fafb' }
    : { eyebrow: '预警治理', primary: '#0f5ea8', accent: 'rgba(15, 94, 168, 0.10)', soft: '#f4f8fc' }
))

const heroStyle = computed(() => ({
  '--stat-primary': pageMeta.value.primary,
  '--stat-accent': pageMeta.value.accent,
  '--stat-soft': pageMeta.value.soft
}))

const totalCount = computed(() => Number(warningSummary.value.totalCount || 0))
const overduePendingCount = computed(() => Number(warningAnalysis.value.overduePendingCount || 0))
const closedRate = computed(() => {
  const total = totalCount.value
  if (!total) {
    return '0.0'
  }
  const closed = Number(warningSummary.value.closedCount || 0)
  return ((closed / total) * 100).toFixed(1)
})

const summaryCards = computed(() => ([
  {
    key: 'pendingCount',
    label: '待处理预警',
    value: warningSummary.value.pendingCount ?? 0,
    unit: '条',
    note: '需要继续核实的预警工单。',
    cardClass: 'stat-kpi-card--warning'
  },
  {
    key: 'processingCount',
    label: '处理中预警',
    value: warningSummary.value.processingCount ?? 0,
    unit: '条',
    note: '已进入整改或核实阶段。',
    cardClass: 'stat-kpi-card--primary'
  },
  {
    key: 'redCount',
    label: '红警预警',
    value: warningSummary.value.redCount ?? 0,
    unit: '条',
    note: '需优先督办的高等级预警。',
    cardClass: 'stat-kpi-card--danger'
  },
  {
    key: 'closedCount',
    label: '已办结预警',
    value: warningSummary.value.closedCount ?? 0,
    unit: '条',
    note: '已完成归档闭环的预警工单。',
    cardClass: 'stat-kpi-card--success'
  }
]))

const slaCards = computed(() => ([
  {
    key: 'overduePendingCount',
    label: '超48小时待办',
    value: warningAnalysis.value.overduePendingCount ?? 0,
    unit: '条',
    note: '超过48小时仍未闭环的待处理或处理中预警。',
    cardClass: 'stat-kpi-card--warning'
  },
  {
    key: 'closedWithin72hCount',
    label: '72小时内办结',
    value: warningAnalysis.value.closedWithin72hCount ?? 0,
    unit: '条',
    note: '从创建到闭环在72小时内完成的预警。',
    cardClass: 'stat-kpi-card--success'
  }
]))

const analysisSections = computed(() => ([
  buildAnalysisSection('level', '预警级别分布', '红警、黄警、提示类结构分布', 'warnLevel', pageMeta.value.primary, warningAnalysis.value.levelStats, 'warnLevel'),
  buildAnalysisSection('source', '来源模块分布', '社保、个税、扩面、设备等来源集中点', 'sourceModule', pageMeta.value.primary, warningAnalysis.value.sourceStats, 'sourceModule'),
  buildAnalysisSection('region', '区域分布 Top5', '当前范围内预警最集中的区域', 'regionCode', '#64748b', warningAnalysis.value.regionStats, 'regionCode'),
  buildAnalysisSection('status', '处理状态分布', '待处理、处理中、已办结结构', 'warnStatus', pageMeta.value.primary, warningAnalysis.value.statusStats, 'warnStatus')
]))

const extraSourceLabels = {
  salary: '工资联动',
  SALARY: '工资联动',
  AQINS: '安责险联动',
  NEWFORM: '新业态联动',
  UNION: '工会监督',
  COCKPIT: '驾驶舱联动'
}

function buildAnalysisSection(key, title, desc, filterField, progressColor, items, labelField) {
  const normalizedItems = normalizeAnalysisItems(items, labelField)
  const total = normalizedItems.reduce((sum, item) => sum + item.dimensionCount, 0)
  return {
    key,
    title,
    desc,
    filterField,
    progressColor,
    total,
    items: normalizedItems.map(item => ({
      ...item,
      percent: total ? Math.round((item.dimensionCount / total) * 100) : 0
    }))
  }
}

function normalizeAnalysisItems(items = [], field) {
  return (Array.isArray(items) ? items : []).map(item => ({
    ...item,
    dimensionKey: item.dimensionKey,
    dimensionLabel: resolveAnalysisLabel(field, item.dimensionKey, item.dimensionLabel),
    dimensionCount: Number(item.dimensionCount || 0)
  }))
}

function resolveAnalysisLabel(field, value, fallback) {
  if (field === 'warnLevel') {
    return optionLabel(warnLevelOptions, value, fallback)
  }
  if (field === 'sourceModule') {
    return optionLabel(sourceModuleOptions, value, extraSourceLabels[value] || humanizeCode(fallback || value))
  }
  if (field === 'warnStatus') {
    return optionLabel(warnStatusOptions, value, fallback)
  }
  if (field === 'regionCode') {
    return formatRegionName(value, fallback || value || '未配置区域')
  }
  return fallback || value || '-'
}

function humanizeCode(value) {
  if (!value) {
    return '其他来源'
  }
  const text = String(value)
  if (/^[\u4e00-\u9fa5]/.test(text)) {
    return text
  }
  return text.replace(/_/g, ' ').replace(/([a-z])([A-Z])/g, '$1 $2')
}

function buildSummaryQuery() {
  return applyLockedEnterpriseQuery({
    regionCode: queryParams.regionCode,
    enterpriseId: queryParams.enterpriseId
  })
}

function loadAll() {
  const scopedQuery = buildSummaryQuery()
  queryParams.enterpriseId = scopedQuery.enterpriseId
  loading.value = true
  Promise.all([
    getWarningSummary(scopedQuery),
    getWarningAnalysis(scopedQuery)
  ]).then(([summaryResponse, analysisResponse]) => {
    warningSummary.value = summaryResponse.data || {}
    warningAnalysis.value = analysisResponse.data || {}
  }).finally(() => {
    loading.value = false
  })
}

function resetQuery() {
  queryParams.regionCode = undefined
  queryParams.enterpriseId = lockedEnterpriseId()
  loadAll()
}

function warningCenterPath() {
  return activePortalCode.value === 'azb' ? '/azb-warning/warning' : '/warning-center/workOrder'
}

function goWarningCenter() {
  router.push({ path: warningCenterPath() })
}

function drillDown(field, row) {
  router.push({
    path: warningCenterPath(),
    query: {
      [field]: row.dimensionKey,
      regionCode: queryParams.regionCode,
      enterpriseId: queryParams.enterpriseId
    }
  })
}

onMounted(() => {
  queryParams.enterpriseId = lockedEnterpriseId()
  optionselectEnterprise().then(response => {
    enterpriseOptions.value = filterAuthorizedEnterpriseOptions(response.data || [])
  })
  loadAll()
})
</script>

<style scoped lang="scss">
.warning-stat-page {
  .stat-hero {
    display: grid;
    grid-template-columns: minmax(0, 1fr) 260px;
    gap: 20px;
    padding: 24px 28px;
    margin-bottom: 16px;
    border: 1px solid #dbe5f0;
    border-radius: 16px;
    background:
      linear-gradient(135deg, var(--stat-accent) 0%, rgba(255, 255, 255, 0.96) 56%),
      #fff;
  }

  .stat-hero__eyebrow {
    margin: 0;
    color: var(--stat-primary);
    font-size: 12px;
    font-weight: 600;
  }

  .stat-hero__title {
    margin: 8px 0 0;
    color: #13243a;
    font-size: 30px;
    line-height: 1.2;
  }

  .stat-hero__desc {
    margin: 12px 0 0;
    max-width: 68ch;
    color: #526377;
    font-size: 14px;
    line-height: 1.8;
  }

  .stat-hero__meta {
    padding: 18px 20px;
    border-radius: 14px;
    background: var(--stat-soft);
    border: 1px solid rgba(148, 163, 184, 0.25);
  }

  .stat-hero__meta-label {
    color: #64748b;
    font-size: 12px;
  }

  .stat-hero__meta-value {
    margin-top: 10px;
    color: var(--stat-primary);
    font-size: 34px;
    font-weight: 700;
    line-height: 1;

    span {
      margin-left: 4px;
      font-size: 14px;
      font-weight: 500;
    }
  }

  .stat-hero__meta-note {
    margin-top: 10px;
    color: #64748b;
    font-size: 13px;
    line-height: 1.7;
  }

  .stat-hero__action {
    width: 100%;
    margin-top: 14px;
  }

  .stat-search-card,
  .stat-analysis-card {
    margin-bottom: 16px;
    border-color: #dbe5f0;
    border-radius: 14px;
  }

  .stat-search-card :deep(.el-card__body),
  .stat-analysis-card :deep(.el-card__body) {
    padding: 18px 20px;
  }

  .stat-analysis-card :deep(.el-card__header) {
    padding: 18px 20px 8px;
    border-bottom: none;
  }

  .stat-kpi-grid,
  .stat-sla-grid,
  .stat-analysis-grid {
    display: grid;
    gap: 14px;
    margin-bottom: 16px;
  }

  .stat-kpi-grid {
    grid-template-columns: repeat(4, minmax(0, 1fr));
  }

  .stat-sla-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .stat-analysis-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .stat-section-title {
    display: flex;
    align-items: baseline;
    gap: 12px;
    margin: 4px 0 12px;

    strong {
      color: #13243a;
      font-size: 16px;
    }

    span {
      color: #64748b;
      font-size: 13px;
    }
  }

  .stat-kpi-card {
    padding: 16px 18px;
    border: 1px solid #dbe5f0;
    border-radius: 14px;
    background: #fff;
  }

  .stat-kpi-card__label {
    color: #64748b;
    font-size: 13px;
  }

  .stat-kpi-card__value {
    margin-top: 10px;
    color: #0f172a;
    font-size: 28px;
    font-weight: 700;
    line-height: 1;
  }

  .stat-kpi-card__unit {
    margin-left: 4px;
    color: #64748b;
    font-size: 13px;
    font-weight: 500;
  }

  .stat-kpi-card__note {
    margin-top: 8px;
    color: #64748b;
    font-size: 12px;
    line-height: 1.7;
  }

  .stat-kpi-card--primary {
    background: linear-gradient(180deg, #fff 0%, #f2f7fd 100%);
  }

  .stat-kpi-card--success {
    background: linear-gradient(180deg, #fff 0%, #f3fbf5 100%);
  }

  .stat-kpi-card--warning {
    background: linear-gradient(180deg, #fff 0%, #fff9ef 100%);
  }

  .stat-kpi-card--danger {
    background: linear-gradient(180deg, #fff 0%, #fff5f5 100%);
  }

  .stat-analysis-card__head {
    display: flex;
    align-items: flex-start;
    justify-content: space-between;
    gap: 12px;
  }

  .stat-analysis-card__title {
    color: #13243a;
    font-size: 16px;
    font-weight: 700;
  }

  .stat-analysis-card__desc {
    margin-top: 4px;
    color: #64748b;
    font-size: 13px;
    line-height: 1.6;
  }

  .stat-analysis-card__total {
    color: var(--stat-primary);
    font-size: 13px;
    white-space: nowrap;
  }

  .stat-analysis-list {
    display: grid;
    gap: 10px;
  }

  .stat-analysis-item {
    width: 100%;
    padding: 12px 14px;
    border: 1px solid #e2e8f0;
    border-radius: 12px;
    background: #fff;
    text-align: left;
    cursor: pointer;
    transition: border-color 0.2s ease, box-shadow 0.2s ease, transform 0.2s ease;
  }

  .stat-analysis-item:hover {
    border-color: rgba(15, 94, 168, 0.35);
    box-shadow: 0 8px 20px rgba(15, 23, 42, 0.06);
    transform: translateY(-1px);
  }

  .stat-analysis-item__row {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 12px;
    margin-bottom: 8px;
  }

  .stat-analysis-item__label {
    color: #13243a;
    font-size: 14px;
    font-weight: 600;
  }

  .stat-analysis-item__value {
    color: #64748b;
    font-size: 12px;
    white-space: nowrap;
  }

  @media (max-width: 1280px) {
    .stat-hero,
    .stat-kpi-grid,
    .stat-analysis-grid {
      grid-template-columns: 1fr;
    }

    .stat-kpi-grid {
      grid-template-columns: repeat(2, minmax(0, 1fr));
    }
  }

  @media (max-width: 768px) {
    .stat-kpi-grid,
    .stat-sla-grid,
    .stat-analysis-grid {
      grid-template-columns: 1fr;
    }

    .stat-section-title {
      flex-direction: column;
      align-items: flex-start;
      gap: 4px;
    }
  }
}
</style>
