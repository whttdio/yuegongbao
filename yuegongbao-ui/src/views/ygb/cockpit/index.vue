<template>
  <div class="app-container ygb-cockpit-screen" v-loading="loading">
    <div class="cockpit-shell">
      <header class="shell-topbar">
        <div class="topbar-side">
          <span class="topbar-side__label">当前区域</span>
          <strong>{{ regionLabel }}</strong>
          <span class="topbar-side__meta">统计月份 {{ queryParams.statMonth || '--' }}</span>
        </div>

        <div class="topbar-title">
          <span class="topbar-title__kicker">粤工保治理引擎</span>
          <h1>用工保障综合驾驶舱</h1>
          <p>{{ roleTitle }} / {{ roleBadge }}</p>
        </div>

        <div class="topbar-actions">
          <span class="topbar-clock">{{ currentTimeText }}</span>
          <el-button type="primary" :icon="Search" @click="handleQuery">刷新态势</el-button>
          <el-button plain @click="openModule(defaultWarningAction)">预警中心</el-button>
        </div>
      </header>

      <section class="shell-hero">
        <div class="hero-column">
          <article class="frame-panel">
            <div class="panel-heading">
              <div>
                <span class="panel-tag">核心摘要</span>
                <h2>治理快照</h2>
              </div>
            </div>

            <div class="summary-matrix">
              <div
                v-for="(item, index) in summaryTiles"
                :key="item.key || index"
                class="summary-tile"
                :class="`summary-tile--${item.tone}`"
              >
                <div class="summary-tile__icon">
                  <el-icon>
                    <component :is="item.icon" />
                  </el-icon>
                </div>
                <div class="summary-tile__body">
                  <span class="summary-tile__label">{{ item.label }}</span>
                  <strong class="summary-tile__value">
                    {{ item.value }}
                    <em>{{ item.unit }}</em>
                  </strong>
                  <p class="summary-tile__note">{{ item.note }}</p>
                </div>
              </div>
            </div>
          </article>

          <article class="frame-panel">
            <div class="panel-heading">
              <div>
                <span class="panel-tag">来源分析</span>
                <h2>预警来源占比</h2>
              </div>
            </div>
            <div ref="distributionChartRef" class="chart-surface chart-surface--medium" />
          </article>

          <article class="frame-panel">
            <div class="panel-heading">
              <div>
                <span class="panel-tag">模块压力</span>
                <h2>治理链路压降</h2>
              </div>
            </div>
            <div ref="modulePressureChartRef" class="chart-surface chart-surface--medium" />
          </article>
        </div>

        <div class="hero-center">
          <article class="frame-panel frame-panel--stage">
            <div class="stage-head">
              <div>
                <span class="panel-tag">主舞台</span>
                <h2>{{ roleTitle }}</h2>
                <p>{{ roleDescription }}</p>
              </div>

              <div class="stage-meta">
                <span class="stage-meta__pill">区域 {{ regionLabel }}</span>
                <span class="stage-meta__pill">月份 {{ queryParams.statMonth || '--' }}</span>
                <span class="stage-meta__pill">{{ primaryPathLabel }}</span>
              </div>
            </div>

            <div class="kpi-ribbon">
              <div v-for="item in kpiRibbonItems" :key="item.key" class="kpi-ribbon__item">
                <div class="kpi-ribbon__label">{{ item.label }}</div>
                <div class="kpi-ribbon__value">
                  {{ item.value }}
                  <span>{{ item.unit }}</span>
                </div>
              </div>
            </div>

            <div class="stage-chart-grid">
              <article class="stage-chart-card">
                <div class="stage-chart-card__title">治理体征环</div>
                <div ref="healthGaugeChartRef" class="chart-surface chart-surface--hero" />
              </article>

              <article class="stage-chart-card">
                <div class="stage-chart-card__title">区域对象体量</div>
                <div ref="entityProfileChartRef" class="chart-surface chart-surface--hero" />
              </article>
            </div>
          </article>

          <div class="center-lower-grid">
            <article class="frame-panel">
              <div class="panel-heading">
                <div>
                  <span class="panel-tag">趋势监测</span>
                  <h2>近期开环变化</h2>
                </div>
              </div>
              <div ref="trendChartRef" class="chart-surface chart-surface--large" />
            </article>

            <article class="frame-panel">
              <div class="panel-heading">
                <div>
                  <span class="panel-tag">维度矩阵</span>
                  <h2>合规压力雷达</h2>
                </div>
              </div>
              <div ref="radarChartRef" class="chart-surface chart-surface--large" />
            </article>
          </div>
        </div>

        <div class="hero-column hero-column--right">
          <article class="frame-panel">
            <div class="panel-heading">
              <div>
                <span class="panel-tag">焦点排行</span>
                <h2>治理优先级</h2>
              </div>
            </div>
            <div ref="focusRankChartRef" class="chart-surface chart-surface--medium" />
          </article>

          <article class="frame-panel">
            <div class="panel-heading">
              <div>
                <span class="panel-tag">热度矩阵</span>
                <h2>焦点热区分布</h2>
              </div>
            </div>
            <div ref="focusBubbleChartRef" class="chart-surface chart-surface--medium" />
          </article>

          <article class="frame-panel">
            <div class="panel-heading">
              <div>
                <span class="panel-tag">即时对照</span>
                <h2>风险与在线态势</h2>
              </div>
            </div>
            <div ref="liveCompareChartRef" class="chart-surface chart-surface--medium" />
          </article>
        </div>
      </section>

      <section class="shell-foot">
        <article class="frame-panel frame-panel--map">
          <div class="panel-heading">
            <div>
              <span class="panel-tag">态势点位</span>
              <h2>企业与围栏空间分布</h2>
            </div>

            <div class="micro-stats">
              <span v-for="item in mapStats" :key="item.label" class="micro-stats__item">
                {{ item.label }} {{ item.value }}
              </span>
            </div>
          </div>

          <div ref="mapChartRef" class="chart-surface chart-surface--map" />
        </article>

        <div class="foot-side-stack">
          <article class="frame-panel">
            <div class="panel-heading">
              <div>
                <span class="panel-tag">联动强度</span>
                <h2>模块承接压力</h2>
              </div>
            </div>
            <div ref="moduleLinkChartRef" class="chart-surface chart-surface--medium" />
          </article>

          <article class="frame-panel frame-panel--action">
            <div class="panel-heading">
              <div>
                <span class="panel-tag">联动入口</span>
                <h2>高频处置热度</h2>
              </div>
            </div>

            <div ref="actionHeatChartRef" class="chart-surface chart-surface--action" />

            <div class="quick-strip">
              <button v-for="item in quickActions" :key="item.key" type="button" class="quick-pill" @click="openModule(item)">
                <div class="quick-pill__main">
                  <span class="quick-pill__badge">{{ item.badge || '常用' }}</span>
                  <strong>{{ item.label }}</strong>
                </div>
                <em>{{ metricOrDirectText(item) }}</em>
              </button>
              <el-empty v-if="!quickActions.length" description="暂无快捷入口" :image-size="60" />
            </div>
          </article>
        </div>
      </section>

      <section class="shell-console">
        <article class="frame-panel">
          <div class="panel-heading">
            <div>
              <span class="panel-tag">指挥台</span>
              <h2>筛选与导出</h2>
              <p class="panel-heading__desc">{{ cockpitUseHint }}</p>
            </div>
          </div>

          <el-alert
            v-if="isReadOnlyRole"
            :title="readOnlyAlertTitle"
            :description="readOnlyAlertDescription"
            type="info"
            :closable="false"
            show-icon
            class="readonly-alert"
          />

          <el-form :model="queryParams" inline class="console-form">
            <el-form-item label="区域">
              <el-select v-model="queryParams.regionCode" style="width: 180px">
                <el-option v-for="item in regionOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
            <el-form-item label="统计日期">
              <el-date-picker
                v-model="queryParams.statDate"
                type="date"
                value-format="YYYY-MM-DD"
                format="YYYY-MM-DD"
                style="width: 180px"
              />
            </el-form-item>
            <el-form-item label="统计月份">
              <el-date-picker
                v-model="queryParams.statMonth"
                type="month"
                value-format="YYYY-MM"
                format="YYYY-MM"
                style="width: 180px"
              />
            </el-form-item>
            <el-form-item label="趋势天数">
              <el-select v-model="queryParams.days" style="width: 140px">
                <el-option v-for="item in dayOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :icon="Search" @click="handleQuery">查询</el-button>
              <el-button :icon="Refresh" @click="resetQuery">重置</el-button>
              <el-button type="warning" plain :icon="Download" @click="handleExport" v-hasPermi="['ygb:cockpit:export']">导出趋势</el-button>
              <el-button plain :icon="Setting" @click="openModule({ path: '/ygb/cockpitConfig' })" v-hasPermi="['ygb:cockpitConfig:list']">
                驾驶舱配置
              </el-button>
            </el-form-item>
          </el-form>
        </article>

        <article class="frame-panel">
          <div class="panel-heading">
            <div>
              <span class="panel-tag">底层明细</span>
              <h2>点位清单与模块联动</h2>
            </div>
          </div>

          <div class="data-layout">
            <div class="data-layout__table">
              <el-table :data="featureTableList" height="340" size="small">
                <el-table-column label="名称" prop="featureName" min-width="140" show-overflow-tooltip />
                <el-table-column label="类型" prop="featureType" width="110" />
                <el-table-column label="区域" prop="regionName" width="130" />
                <el-table-column label="状态" prop="featureStatus" width="110" />
                <el-table-column label="几何" prop="geometryType" width="90" />
                <el-table-column label="坐标摘要" prop="coordinateText" min-width="180" show-overflow-tooltip />
              </el-table>
            </div>

            <div class="data-layout__side">
              <div class="module-linkage">
                <button v-for="item in moduleRows" :key="item.key" type="button" class="module-linkage__item" @click="openModule(item)">
                  <div>
                    <strong>{{ item.label }}</strong>
                    <p>{{ item.nextStep || item.desc || '进入模块处理' }}</p>
                  </div>
                  <span>{{ item.metric || formatCountText(item) }}</span>
                </button>
                <el-empty v-if="!moduleRows.length" description="暂无模块联动数据" :image-size="60" />
              </div>
            </div>
          </div>
        </article>
      </section>
    </div>
  </div>
</template>

<script setup name="YgbCockpit">
import { computed, markRaw, nextTick, onActivated, onBeforeUnmount, onDeactivated, onMounted, ref, watch, watchEffect } from 'vue'
import * as echarts from 'echarts'
import {
  Bell,
  Connection,
  DataAnalysis,
  Document,
  Download,
  Monitor,
  OfficeBuilding,
  Refresh,
  Search,
  Setting,
  WarningFilled
} from '@element-plus/icons-vue'
import useUserStore from '@/store/modules/user'
import { useWorkbenchAssist } from '@/composables/useWorkbenchAssist'
import { decoratePortalExplanationItems, resolvePortalExplanationSummary } from '@/utils/portalExplanation'
import { useAuthorizedRegionOptions } from '@/utils/regionScope'
import { useRoleViewMode } from '@/utils/roleView'
import { resolveDefaultStatReportRoute } from '@/views/statReport/reportConfigs'
import {
  cockpitDayOptions as dayOptions,
  cockpitRegionNameMap as regionNameMap,
  cockpitRegionOptions as allRegionOptions,
  formatTrendDate,
  sortByKeyOrder,
  useCockpitPage
} from '@/views/cockpit/useCockpitPage'
import { getYgbCockpitDashboard } from '@/api/ygb/cockpit'

const COCKPIT_COLORS = Object.freeze({
  cyan: '#52e6ff',
  blue: '#3d7bff',
  green: '#3fe0b3',
  warning: '#ffbf69',
  danger: '#ff7c7c',
  violet: '#7c8dff',
  text: '#edf8ff',
  muted: '#89a9c5',
  axis: 'rgba(126, 188, 224, 0.72)',
  split: 'rgba(80, 176, 235, 0.14)',
  panel: 'rgba(7, 22, 38, 0.92)',
  tooltip: 'rgba(6, 17, 31, 0.96)'
})

const FALLBACK_SUMMARY_ICONS = [
  markRaw(OfficeBuilding),
  markRaw(WarningFilled),
  markRaw(Document),
  markRaw(Connection)
]

const SUMMARY_ICON_MAP = Object.freeze({
  enterprisePending: markRaw(OfficeBuilding),
  enterpriseRisk: markRaw(WarningFilled),
  monthStatus: markRaw(Document),
  priorityQueue: markRaw(Connection),
  warning: markRaw(Bell),
  injury: markRaw(WarningFilled),
  insurance: markRaw(DataAnalysis),
  device: markRaw(Monitor)
})

const KPI_META = Object.freeze([
  { key: 'insuranceRate', label: '工伤参保率', unit: '%', digits: 1 },
  { key: 'aqInsuranceRate', label: '安责险覆盖', unit: '%', digits: 1 },
  { key: 'expandCompletionRate', label: '扩面完成率', unit: '%', digits: 1 },
  { key: 'todayWarningCount', label: '当日预警', unit: '条', digits: 0 }
])

const ROLE_META = Object.freeze({
  ygb_hrss_supervisor: {
    title: '人社经办驾驶舱',
    badge: '经办视角',
    desc: '围绕区域预警、参保覆盖、扩面归档和高风险企业进行联动复核。'
  },
  ygb_enterprise_operator: {
    title: '企业经办驾驶舱',
    badge: '企业办理视角',
    desc: '聚焦合同、考勤、工资、归档和异常回写，缩短企业办理链路。'
  },
  ygb_enterprise_admin: {
    title: '企业治理驾驶舱',
    badge: '管理视角',
    desc: '围绕合同、工资、社保、税务和未参保治理形成统一判断。'
  }
})

const userStore = useUserStore()
const { setPageGuide } = useWorkbenchAssist()
const regionOptions = useAuthorizedRegionOptions(allRegionOptions)
const { isReadOnlyRole, readOnlyRoleLabel, readOnlyRoleDescription } = useRoleViewMode()

const {
  loading,
  queryParams,
  dashboardData,
  indicators,
  trendList,
  distributionList,
  featureCollection,
  featureTableList,
  trendChartRef,
  distributionChartRef,
  mapChartRef,
  handleQuery,
  resetQuery,
  handleExport,
  openModule
} = useCockpitPage({
  fetchDashboard: getYgbCockpitDashboard,
  exportFilePrefix: 'ygb_cockpit',
  trendColors: [COCKPIT_COLORS.cyan, COCKPIT_COLORS.blue, COCKPIT_COLORS.green],
  trendSeries: [
    { name: '当日预警', type: 'bar', dataKey: 'todayWarningCount', yAxisIndex: 0, barMaxWidth: 24 },
    { name: '工伤参保率', type: 'line', dataKey: 'insuranceRate', yAxisIndex: 1 },
    { name: '扩面完成率', type: 'line', dataKey: 'expandCompletionRate', yAxisIndex: 1 }
  ],
  createTrendChartOption,
  createDistributionChartOption,
  createMapChartOption
})

const healthGaugeChartRef = ref(null)
const entityProfileChartRef = ref(null)
const radarChartRef = ref(null)
const modulePressureChartRef = ref(null)
const focusRankChartRef = ref(null)
const focusBubbleChartRef = ref(null)
const liveCompareChartRef = ref(null)
const moduleLinkChartRef = ref(null)
const actionHeatChartRef = ref(null)
const currentTimeText = ref(formatCurrentTime())

let clockTimer = null
const extraCharts = new Map()

function normalizeAction(target = {}) {
  const base = typeof target === 'string' ? { path: target } : { ...(target || {}) }
  const path = String(base.path || '')
    .replace('/ygb/statReport', resolveDefaultStatReportRoute('ygb'))
    .replace('/azb/statReport', resolveDefaultStatReportRoute('azb'))
  return {
    ...base,
    path
  }
}

function numeric(value, fallback = 0) {
  const result = Number(value ?? fallback)
  return Number.isFinite(result) ? result : fallback
}

function clampPercent(value) {
  return Math.max(0, Math.min(100, numeric(value)))
}

function formatMetric(value, digits = 0) {
  return new Intl.NumberFormat('zh-CN', {
    minimumFractionDigits: digits,
    maximumFractionDigits: digits
  }).format(numeric(value))
}

function parseMetricValue(item = {}) {
  if (item.count !== undefined && item.count !== null) {
    return numeric(item.count)
  }
  const candidates = [item.metric, item.countText, item.value]
  for (const candidate of candidates) {
    if (candidate === undefined || candidate === null || candidate === '') {
      continue
    }
    const match = String(candidate).replace(/,/g, '').match(/-?\d+(\.\d+)?/)
    if (match) {
      return numeric(match[0])
    }
  }
  return 0
}

function formatCountText(item = {}) {
  if (item.countText) {
    return item.countText
  }
  if (item.metric) {
    return item.metric
  }
  if (item.count !== undefined && item.count !== null) {
    return `${item.count}${item.unit || ''}`
  }
  return '--'
}

function metricOrDirectText(item = {}) {
  const text = formatCountText(item)
  return text === '--' ? '直达' : text
}

function formatCurrentTime() {
  const now = new Date()
  const year = now.getFullYear()
  const month = String(now.getMonth() + 1).padStart(2, '0')
  const day = String(now.getDate()).padStart(2, '0')
  const hours = String(now.getHours()).padStart(2, '0')
  const minutes = String(now.getMinutes()).padStart(2, '0')
  const seconds = String(now.getSeconds()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

function resolveSummaryTone(item = {}, index = 0) {
  const seed = `${item.cardClass || ''}-${item.key || ''}`
  if (seed.includes('warning') || seed.includes('priority')) return 'warning'
  if (seed.includes('danger') || seed.includes('risk') || seed.includes('injury')) return 'danger'
  if (seed.includes('success') || seed.includes('insurance')) return 'success'
  return ['primary', 'warning', 'success', 'danger'][index % 4]
}

function resolveSummaryIcon(item = {}, index = 0) {
  return SUMMARY_ICON_MAP[item.key] || FALLBACK_SUMMARY_ICONS[index % FALLBACK_SUMMARY_ICONS.length]
}

function truncateLabel(value, size = 8) {
  const text = String(value || '')
  return text.length > size ? `${text.slice(0, size)}…` : text
}

function buildBarGradient(fromColor, toColor) {
  return new echarts.graphic.LinearGradient(0, 0, 1, 0, [
    { offset: 0, color: fromColor },
    { offset: 1, color: toColor }
  ])
}

function buildLineGradient(fromColor) {
  return new echarts.graphic.LinearGradient(0, 0, 0, 1, [
    { offset: 0, color: `${fromColor}66` },
    { offset: 1, color: `${fromColor}05` }
  ])
}

const matchedRoleMeta = computed(() => {
  const roles = userStore.roles || []
  return roles.map(role => ROLE_META[role]).find(Boolean) || {
    title: '用工保障综合驾驶舱',
    badge: '综合视角',
    desc: '聚焦重点对象、风险压降、模块协同和处置闭环。'
  }
})

const roleTitle = computed(() => matchedRoleMeta.value.title)
const roleBadge = computed(() => matchedRoleMeta.value.badge)
const roleDescription = computed(() => dashboardData.value.homeSummary || dashboardData.value.roleDescription || matchedRoleMeta.value.desc)
const primaryPathLabel = computed(() => '焦点识别 -> 模块办理 -> 驾驶舱复核')
const cockpitUseHint = computed(() => '先锁定风险，再进入模块办理，最后回到总览复核处置成效。')
const regionLabel = computed(() => indicators.value.regionName || regionNameMap[queryParams.value.regionCode] || '全部区域')

const portalExplanationItems = computed(() => decoratePortalExplanationItems(dashboardData.value.ygbExplanation || [], {
  portalCode: 'ygb',
  panelTitle: '530.1 办理链路解释',
  panelDescription: '门户解释用于补充驾驶舱摘要、证据和推荐下钻入口。'
}))
const portalExplanationSummary = computed(() => resolvePortalExplanationSummary(portalExplanationItems.value, ''))

const summaryCards = computed(() => {
  const cards = dashboardData.value.summaryCards || []
  if (cards.length) {
    return cards.slice(0, 4)
  }
  return [
    { key: 'warning', label: '待处置预警', value: indicators.value.pendingWarningCount || 0, unit: '条', note: '待闭环预警对象', cardClass: 'warning' },
    { key: 'injury', label: '超期工伤', value: indicators.value.overdueInjuryCount || 0, unit: '起', note: '需要压降的工伤事件', cardClass: 'danger' },
    { key: 'insurance', label: '工伤参保率', value: indicators.value.insuranceRate || 0, unit: '%', note: '当前参保覆盖水平', cardClass: 'success' },
    { key: 'device', label: '在线设备', value: indicators.value.onlineDeviceCount || 0, unit: '台', note: '在线感知设备规模', cardClass: 'primary' }
  ]
})

const summaryTiles = computed(() => summaryCards.value.map((item, index) => ({
  ...item,
  icon: resolveSummaryIcon(item, index),
  tone: resolveSummaryTone(item, index)
})))

const focusItems = computed(() => {
  const queues = dashboardData.value.focusQueues || []
  return sortByKeyOrder(queues.map(item => normalizeAction(item)), [])
})

const activeFocusKey = ref('')

watch(focusItems, items => {
  if (!items.length) {
    activeFocusKey.value = ''
    return
  }
  if (!items.some(item => item.key === activeFocusKey.value)) {
    activeFocusKey.value = items[0].key
  }
}, { immediate: true })

const activeFocus = computed(() => focusItems.value.find(item => item.key === activeFocusKey.value) || focusItems.value[0] || null)
const activeFocusSummary = computed(() => activeFocus.value?.summary || activeFocus.value?.desc || activeFocus.value?.sourceDescription || portalExplanationSummary.value || '等待驾驶舱汇总当前优先链路。')

const workflowSteps = computed(() => {
  const steps = dashboardData.value.workflowSteps || []
  if (steps.length) {
    return steps
  }
  return [
    { label: '锁定焦点对象', desc: '优先识别最需要推进的企业、月份链路和预警对象。' },
    { label: '进入模块办理', desc: '从驾驶舱直接下钻到合同、工资、社保、税务或预警模块。' },
    { label: '回到总览复核', desc: '通过趋势、点位和联动结果判断压降是否到位。' }
  ]
})

const hintTags = computed(() => dashboardData.value.hintTags || [])
const quickActions = computed(() => (dashboardData.value.quickActions || []).map(item => normalizeAction(item)).slice(0, 6))
const moduleRows = computed(() => {
  const rows = dashboardData.value.moduleRows || dashboardData.value.moduleTable || []
  if (rows.length) {
    return rows.map(item => normalizeAction(item))
  }
  return quickActions.value.map(item => ({
    ...item,
    metric: item.metric || formatCountText(item),
    nextStep: item.desc || '进入模块处理'
  }))
})

const governanceMatrixItems = computed(() => ([
  { name: '合同备案', value: numeric(dashboardData.value.contractSummary?.pendingCount) },
  { name: '工资发放', value: numeric(dashboardData.value.salaryDetailSummary?.failedCount) },
  { name: '社保比对', value: numeric(dashboardData.value.socialBaseCompareSummary?.abnormalCount) },
  { name: '个税比对', value: numeric(dashboardData.value.taxCompareSummary?.abnormalCount) },
  { name: '未参保治理', value: numeric(dashboardData.value.uninsuredListSummary?.pendingCount) },
  { name: '预警闭环', value: numeric(dashboardData.value.warningSummary?.pendingCount) }
]))

const modulePressureItems = computed(() => [...governanceMatrixItems.value].sort((left, right) => right.value - left.value))
const entityProfileItems = computed(() => ([
  { name: '用工企业', value: numeric(indicators.value.employerCount), color: COCKPIT_COLORS.cyan },
  { name: '派遣单位', value: numeric(indicators.value.dispatchCompanyCount), color: COCKPIT_COLORS.blue },
  { name: '高风险企业', value: numeric(indicators.value.highRiskEnterpriseCount), color: COCKPIT_COLORS.warning },
  { name: '用工人数', value: numeric(indicators.value.dispatchedWorkerCount), color: COCKPIT_COLORS.green }
]))
const focusRankingItems = computed(() => [...focusItems.value]
  .map(item => ({
    name: truncateLabel(item.title, 8),
    fullName: item.title,
    key: item.key,
    value: parseMetricValue(item)
  }))
  .sort((left, right) => right.value - left.value)
  .slice(0, 6))
const moduleLinkChartItems = computed(() => [...moduleRows.value]
  .map(item => ({
    name: truncateLabel(item.label, 7),
    fullName: item.label,
    value: parseMetricValue(item)
  }))
  .sort((left, right) => right.value - left.value)
  .slice(0, 6))
const actionHeatItems = computed(() => {
  const source = (quickActions.value.length ? quickActions.value : moduleRows.value).slice(0, 6)
  const hasMetric = source.some(item => parseMetricValue(item) > 0)
  return source
    .map((item, index) => {
      const label = item.label || item.title || `联动入口 ${index + 1}`
      return {
        key: item.key || `${label}-${index}`,
        name: truncateLabel(label, 6),
        fullName: label,
        value: hasMetric ? Math.max(parseMetricValue(item), 1) : Math.max(source.length - index, 1)
      }
    })
    .sort((left, right) => right.value - left.value)
})

const kpiRibbonItems = computed(() => KPI_META.map(item => ({
  key: item.key,
  label: item.label,
  unit: item.unit,
  value: item.unit === '%' ? formatMetric(indicators.value[item.key], item.digits) : formatMetric(indicators.value[item.key], 0)
})))

const healthMetrics = computed(() => ([
  { name: '工伤参保率', value: clampPercent(indicators.value.insuranceRate), color: COCKPIT_COLORS.cyan, radius: '88%' },
  { name: '安责险覆盖', value: clampPercent(indicators.value.aqInsuranceRate), color: COCKPIT_COLORS.green, radius: '68%' },
  { name: '扩面完成率', value: clampPercent(indicators.value.expandCompletionRate), color: COCKPIT_COLORS.warning, radius: '48%' }
]))
const liveCompareItems = computed(() => ([
  { name: '当日预警', value: numeric(indicators.value.todayWarningCount), color: COCKPIT_COLORS.cyan },
  { name: '待处置', value: numeric(indicators.value.pendingWarningCount), color: COCKPIT_COLORS.warning },
  { name: '在线设备', value: numeric(indicators.value.onlineDeviceCount), color: COCKPIT_COLORS.green },
  { name: '超期工伤', value: numeric(indicators.value.overdueInjuriesCount || indicators.value.overdueInjuryCount), color: COCKPIT_COLORS.danger }
]))

const mapStats = computed(() => {
  const features = featureCollection.value.features || []
  const enterprise = features.filter(item => item.properties?.featureType === 'ENTERPRISE').length
  const device = features.filter(item => item.properties?.featureType === 'DEVICE').length
  const fence = features.filter(item => item.properties?.featureType === 'FENCE' || item.geometry?.type === 'Polygon').length
  return [
    { label: '企业', value: enterprise },
    { label: '设备', value: device },
    { label: '围栏', value: fence }
  ]
})

const defaultWarningAction = computed(() => ({ path: '/ygb/warning' }))
const readOnlyAlertTitle = computed(() => `${readOnlyRoleLabel.value}仅保留总览、联动和导出`)
const readOnlyAlertDescription = computed(() => `${readOnlyRoleDescription.value || ''} 当前驾驶舱仍展示焦点对象和推荐入口，但不承接录入类动作。`.trim())

function createTrendChartOption({ trendList, trendColors, trendSeries }) {
  return {
    color: trendColors,
    tooltip: {
      trigger: 'axis',
      backgroundColor: COCKPIT_COLORS.tooltip,
      borderColor: 'rgba(82, 230, 255, 0.28)',
      textStyle: { color: COCKPIT_COLORS.text }
    },
    legend: {
      top: 0,
      right: 0,
      itemWidth: 12,
      itemHeight: 12,
      textStyle: { color: COCKPIT_COLORS.muted }
    },
    grid: { left: 40, right: 28, top: 48, bottom: 28 },
    xAxis: {
      type: 'category',
      data: trendList.map(item => formatTrendDate(item.statDate)),
      axisTick: { show: false },
      axisLine: { lineStyle: { color: COCKPIT_COLORS.split } },
      axisLabel: { color: COCKPIT_COLORS.axis }
    },
    yAxis: [
      {
        type: 'value',
        name: '数量',
        minInterval: 1,
        nameTextStyle: { color: COCKPIT_COLORS.muted },
        axisLabel: { color: COCKPIT_COLORS.axis },
        splitLine: { lineStyle: { color: COCKPIT_COLORS.split, type: 'dashed' } }
      },
      {
        type: 'value',
        name: '比率',
        nameTextStyle: { color: COCKPIT_COLORS.muted },
        axisLabel: { formatter: '{value}%', color: COCKPIT_COLORS.axis },
        splitLine: { show: false }
      }
    ],
    series: trendSeries.map((item, index) => {
      const color = trendColors[index]
      if ((item.type || 'line') === 'bar') {
        return {
          name: item.name,
          type: 'bar',
          yAxisIndex: item.yAxisIndex || 0,
          barMaxWidth: item.barMaxWidth || 24,
          data: trendList.map(row => numeric(row[item.dataKey])),
          itemStyle: {
            borderRadius: [10, 10, 2, 2],
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color },
              { offset: 1, color: `${color}22` }
            ])
          }
        }
      }
      return {
        name: item.name,
        type: 'line',
        yAxisIndex: item.yAxisIndex || 0,
        smooth: item.smooth !== false,
        symbol: 'circle',
        symbolSize: 8,
        data: trendList.map(row => numeric(row[item.dataKey])),
        lineStyle: { width: 2, color },
        itemStyle: { color, borderColor: '#041321', borderWidth: 2 },
        areaStyle: { color: buildLineGradient(color) }
      }
    })
  }
}

function createDistributionChartOption({ distributionList, distributionLabelFormatter }) {
  const data = distributionList.map((item, index) => ({
    name: distributionLabelFormatter(item),
    value: numeric(item.metricCount),
    itemStyle: {
      color: [COCKPIT_COLORS.cyan, COCKPIT_COLORS.blue, COCKPIT_COLORS.green, COCKPIT_COLORS.warning, COCKPIT_COLORS.violet, COCKPIT_COLORS.danger][index % 6]
    }
  }))
  const total = data.reduce((sum, item) => sum + item.value, 0)

  return {
    tooltip: {
      trigger: 'item',
      backgroundColor: COCKPIT_COLORS.tooltip,
      borderColor: 'rgba(82, 230, 255, 0.28)',
      textStyle: { color: COCKPIT_COLORS.text },
      formatter: params => `${params.name}<br/>数量 ${params.value}<br/>占比 ${params.percent}%`
    },
    series: [
      {
        type: 'pie',
        radius: ['30%', '74%'],
        center: ['50%', '54%'],
        roseType: 'area',
        minAngle: 6,
        label: {
          color: COCKPIT_COLORS.axis,
          formatter: '{b|{b}}\n{d|{d}%}',
          rich: {
            b: { color: COCKPIT_COLORS.text, fontSize: 12, lineHeight: 18 },
            d: { color: COCKPIT_COLORS.muted, fontSize: 12 }
          }
        },
        labelLine: {
          length: 10,
          length2: 12,
          lineStyle: { color: 'rgba(111, 201, 255, 0.42)' }
        },
        itemStyle: {
          borderRadius: 10,
          borderColor: 'rgba(4, 19, 33, 0.85)',
          borderWidth: 2
        },
        data
      }
    ],
    graphic: [
      {
        type: 'text',
        left: 'center',
        top: '43%',
        style: {
          text: formatMetric(total, 0),
          fill: COCKPIT_COLORS.text,
          font: '700 26px "Source Han Sans SC", sans-serif',
          textAlign: 'center'
        }
      },
      {
        type: 'text',
        left: 'center',
        top: '56%',
        style: {
          text: '预警总量',
          fill: COCKPIT_COLORS.muted,
          font: '12px "Source Han Sans SC", sans-serif',
          textAlign: 'center'
        }
      }
    ]
  }
}

function createMapChartOption({ pointBounds, pointSeries, polygonSeries }) {
  const styledPoints = pointSeries.map(series => ({
    ...series,
    type: series.name === '企业点位' ? 'effectScatter' : 'scatter',
    coordinateSystem: 'cartesian2d',
    symbolSize: series.name === '企业点位' ? 18 : 14,
    rippleEffect: series.name === '企业点位'
      ? { scale: 3, brushType: 'stroke' }
      : undefined,
    itemStyle: {
      ...series.itemStyle,
      shadowBlur: 18,
      shadowColor: series.itemStyle.color
    }
  }))

  const styledPolygons = polygonSeries.map(series => ({
    ...series,
    coordinateSystem: 'cartesian2d',
    lineStyle: {
      ...series.lineStyle,
      width: 2,
      shadowBlur: 16,
      shadowColor: COCKPIT_COLORS.warning
    }
  }))

  return {
    tooltip: {
      trigger: 'item',
      backgroundColor: COCKPIT_COLORS.tooltip,
      borderColor: 'rgba(82, 230, 255, 0.28)',
      textStyle: { color: COCKPIT_COLORS.text },
      formatter: params => {
        if (params.seriesType === 'line') {
          return `${params.seriesName}<br/>围栏边界`
        }
        const chartData = params.data || {}
        return `${chartData.name}<br/>${chartData.featureType}<br/>经纬度 ${chartData.value[0]}, ${chartData.value[1]}`
      }
    },
    legend: {
      top: 0,
      right: 0,
      textStyle: { color: COCKPIT_COLORS.muted }
    },
    grid: { left: 12, right: 12, top: 42, bottom: 12 },
    xAxis: {
      type: 'value',
      min: pointBounds.minLng,
      max: pointBounds.maxLng,
      axisLabel: { show: false },
      axisTick: { show: false },
      axisLine: { show: false },
      splitNumber: 6,
      splitLine: { lineStyle: { color: COCKPIT_COLORS.split } }
    },
    yAxis: {
      type: 'value',
      min: pointBounds.minLat,
      max: pointBounds.maxLat,
      axisLabel: { show: false },
      axisTick: { show: false },
      axisLine: { show: false },
      splitNumber: 5,
      splitLine: { lineStyle: { color: COCKPIT_COLORS.split } }
    },
    series: [...styledPoints, ...styledPolygons]
  }
}

function buildHealthGaugeOption() {
  return {
    series: healthMetrics.value.map((item, index) => ({
      type: 'gauge',
      radius: item.radius,
      startAngle: 220,
      endAngle: -40,
      min: 0,
      max: 100,
      progress: {
        show: true,
        width: 14,
        roundCap: true,
        itemStyle: {
          color: item.color,
          shadowBlur: 10,
          shadowColor: item.color
        }
      },
      pointer: { show: false },
      axisLine: {
        roundCap: true,
        lineStyle: {
          width: 14,
          color: [[1, 'rgba(134, 194, 234, 0.12)']]
        }
      },
      splitLine: { show: false },
      axisTick: { show: false },
      axisLabel: { show: false },
      anchor: { show: false },
      title: {
        show: true,
        color: COCKPIT_COLORS.muted,
        fontSize: 12,
        offsetCenter: [0, `${-12 + index * 26}%`]
      },
      detail: {
        valueAnimation: true,
        color: item.color,
        fontSize: 18,
        fontWeight: 700,
        formatter: value => `${formatMetric(value, 1)}%`,
        offsetCenter: [0, `${2 + index * 26}%`]
      },
      data: [{ value: item.value, name: item.name }]
    })),
    graphic: [
      {
        type: 'text',
        left: 'center',
        top: '44%',
        style: {
          text: formatMetric(indicators.value.pendingWarningCount, 0),
          fill: COCKPIT_COLORS.text,
          font: '700 30px "Source Han Sans SC", sans-serif',
          textAlign: 'center'
        }
      },
      {
        type: 'text',
        left: 'center',
        top: '58%',
        style: {
          text: '待闭环预警',
          fill: COCKPIT_COLORS.muted,
          font: '12px "Source Han Sans SC", sans-serif',
          textAlign: 'center'
        }
      }
    ]
  }
}

function buildRadarOption() {
  const values = governanceMatrixItems.value.map(item => item.value)
  const maxValue = Math.max(5, ...values)
  return {
    tooltip: {
      trigger: 'item',
      backgroundColor: COCKPIT_COLORS.tooltip,
      borderColor: 'rgba(82, 230, 255, 0.28)',
      textStyle: { color: COCKPIT_COLORS.text }
    },
    radar: {
      center: ['50%', '52%'],
      radius: '66%',
      splitNumber: 4,
      axisName: { color: COCKPIT_COLORS.text, fontSize: 12 },
      splitLine: { lineStyle: { color: COCKPIT_COLORS.split } },
      splitArea: { areaStyle: { color: ['rgba(15, 43, 71, 0.20)', 'rgba(15, 43, 71, 0.08)'] } },
      axisLine: { lineStyle: { color: COCKPIT_COLORS.split } },
      indicator: governanceMatrixItems.value.map(item => ({
        name: item.name,
        max: Math.ceil(maxValue * 1.25)
      }))
    },
    series: [
      {
        type: 'radar',
        data: [
          {
            value: values,
            name: '当前压力',
            symbol: 'circle',
            symbolSize: 7,
            lineStyle: { color: COCKPIT_COLORS.cyan, width: 2 },
            itemStyle: { color: COCKPIT_COLORS.cyan },
            areaStyle: { color: 'rgba(82, 230, 255, 0.18)' }
          }
        ]
      }
    ]
  }
}

function buildEntityProfileOption() {
  const items = entityProfileItems.value
  return {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
      backgroundColor: COCKPIT_COLORS.tooltip,
      borderColor: 'rgba(82, 230, 255, 0.28)',
      textStyle: { color: COCKPIT_COLORS.text }
    },
    grid: { left: 68, right: 18, top: 18, bottom: 24 },
    xAxis: {
      type: 'value',
      axisLabel: { color: COCKPIT_COLORS.axis },
      splitLine: { lineStyle: { color: COCKPIT_COLORS.split } }
    },
    yAxis: {
      type: 'category',
      axisTick: { show: false },
      axisLine: { show: false },
      axisLabel: { color: COCKPIT_COLORS.text },
      data: items.map(item => item.name)
    },
    series: [
      {
        type: 'bar',
        barWidth: 14,
        data: items.map(item => ({
          value: item.value,
          itemStyle: {
            borderRadius: 999,
            color: buildBarGradient(`${item.color}44`, item.color)
          }
        })),
        label: {
          show: true,
          position: 'right',
          color: COCKPIT_COLORS.text
        }
      }
    ]
  }
}

function buildModulePressureOption() {
  const items = modulePressureItems.value
  const labels = items.map(item => item.name)
  const values = items.map(item => item.value)
  return {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
      backgroundColor: COCKPIT_COLORS.tooltip,
      borderColor: 'rgba(82, 230, 255, 0.28)',
      textStyle: { color: COCKPIT_COLORS.text }
    },
    grid: { left: 82, right: 18, top: 18, bottom: 16 },
    xAxis: {
      type: 'value',
      axisLabel: { color: COCKPIT_COLORS.axis },
      splitLine: { lineStyle: { color: COCKPIT_COLORS.split } }
    },
    yAxis: {
      type: 'category',
      data: labels,
      axisTick: { show: false },
      axisLine: { show: false },
      axisLabel: { color: COCKPIT_COLORS.text }
    },
    series: [
      {
        type: 'bar',
        data: values,
        barWidth: 12,
        showBackground: true,
        backgroundStyle: {
          color: 'rgba(122, 190, 233, 0.08)',
          borderRadius: 999
        },
        itemStyle: {
          borderRadius: 999,
          color: buildBarGradient(COCKPIT_COLORS.blue, COCKPIT_COLORS.cyan)
        },
        label: {
          show: true,
          position: 'right',
          color: COCKPIT_COLORS.text
        }
      }
    ]
  }
}

function buildFocusRankOption() {
  const items = focusRankingItems.value
  const activeKey = activeFocus.value?.key
  return {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
      backgroundColor: COCKPIT_COLORS.tooltip,
      borderColor: 'rgba(82, 230, 255, 0.28)',
      textStyle: { color: COCKPIT_COLORS.text },
      formatter: params => {
        const item = items[params[0]?.dataIndex || 0]
        return `${item?.fullName || params[0]?.name}<br/>数量 ${params[0]?.value || 0}`
      }
    },
    grid: { left: 78, right: 18, top: 18, bottom: 16 },
    xAxis: {
      type: 'value',
      axisLabel: { color: COCKPIT_COLORS.axis },
      splitLine: { lineStyle: { color: COCKPIT_COLORS.split } }
    },
    yAxis: {
      type: 'category',
      inverse: true,
      axisTick: { show: false },
      axisLine: { show: false },
      axisLabel: { color: COCKPIT_COLORS.text },
      data: items.map(item => item.name)
    },
    series: [
      {
        type: 'bar',
        barWidth: 12,
        data: items.map(item => ({
          value: item.value,
          itemStyle: {
            borderRadius: 999,
            color: item.key === activeKey
              ? buildBarGradient(COCKPIT_COLORS.warning, COCKPIT_COLORS.cyan)
              : buildBarGradient('#2450aa', COCKPIT_COLORS.blue)
          }
        })),
        label: {
          show: true,
          position: 'right',
          color: COCKPIT_COLORS.text
        }
      }
    ]
  }
}

function buildFocusBubbleOption() {
  const items = focusRankingItems.value
  const maxValue = Math.max(1, ...items.map(item => item.value))
  return {
    tooltip: {
      trigger: 'item',
      backgroundColor: COCKPIT_COLORS.tooltip,
      borderColor: 'rgba(82, 230, 255, 0.28)',
      textStyle: { color: COCKPIT_COLORS.text },
      formatter: params => `${params.data.fullName}<br/>数量 ${params.data.value[1]}`
    },
    grid: { left: 24, right: 18, top: 16, bottom: 42 },
    xAxis: {
      type: 'category',
      axisTick: { show: false },
      axisLine: { lineStyle: { color: COCKPIT_COLORS.split } },
      axisLabel: { color: COCKPIT_COLORS.axis },
      data: items.map(item => item.name)
    },
    yAxis: {
      type: 'value',
      axisLabel: { color: COCKPIT_COLORS.axis },
      splitLine: { lineStyle: { color: COCKPIT_COLORS.split, type: 'dashed' } }
    },
    series: [
      {
        type: 'scatter',
        data: items.map((item, index) => ({
          value: [item.name, item.value],
          fullName: item.fullName,
          symbolSize: 18 + (item.value / maxValue) * 28,
          itemStyle: {
            color: [COCKPIT_COLORS.cyan, COCKPIT_COLORS.blue, COCKPIT_COLORS.green, COCKPIT_COLORS.warning, COCKPIT_COLORS.violet, COCKPIT_COLORS.danger][index % 6],
            shadowBlur: 18,
            shadowColor: 'rgba(82, 230, 255, 0.28)'
          }
        }))
      }
    ]
  }
}

function buildLiveCompareOption() {
  const items = liveCompareItems.value
  return {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
      backgroundColor: COCKPIT_COLORS.tooltip,
      borderColor: 'rgba(82, 230, 255, 0.28)',
      textStyle: { color: COCKPIT_COLORS.text }
    },
    grid: { left: 20, right: 18, top: 24, bottom: 30 },
    xAxis: {
      type: 'category',
      axisTick: { show: false },
      axisLine: { lineStyle: { color: COCKPIT_COLORS.split } },
      axisLabel: { color: COCKPIT_COLORS.axis },
      data: items.map(item => item.name)
    },
    yAxis: {
      type: 'value',
      axisLabel: { color: COCKPIT_COLORS.axis },
      splitLine: { lineStyle: { color: COCKPIT_COLORS.split } }
    },
    series: [
      {
        type: 'bar',
        barWidth: 18,
        data: items.map(item => ({
          value: item.value,
          itemStyle: {
            borderRadius: [10, 10, 2, 2],
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: item.color },
              { offset: 1, color: `${item.color}22` }
            ])
          }
        })),
        label: {
          show: true,
          position: 'top',
          color: COCKPIT_COLORS.text
        }
      }
    ]
  }
}

function buildModuleLinkOption() {
  const items = moduleLinkChartItems.value
  return {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
      backgroundColor: COCKPIT_COLORS.tooltip,
      borderColor: 'rgba(82, 230, 255, 0.28)',
      textStyle: { color: COCKPIT_COLORS.text },
      formatter: params => {
        const item = items[params[0]?.dataIndex || 0]
        return `${item?.fullName || params[0]?.name}<br/>数量 ${params[0]?.value || 0}`
      }
    },
    grid: { left: 74, right: 18, top: 20, bottom: 18 },
    xAxis: {
      type: 'value',
      axisLabel: { color: COCKPIT_COLORS.axis },
      splitLine: { lineStyle: { color: COCKPIT_COLORS.split } }
    },
    yAxis: {
      type: 'category',
      inverse: true,
      axisTick: { show: false },
      axisLine: { show: false },
      axisLabel: { color: COCKPIT_COLORS.text },
      data: items.map(item => item.name)
    },
    series: [
      {
        type: 'bar',
        barWidth: 12,
        data: items.map(item => ({
          value: item.value,
          itemStyle: {
            borderRadius: 999,
            color: buildBarGradient(COCKPIT_COLORS.violet, COCKPIT_COLORS.cyan)
          }
        })),
        label: {
          show: true,
          position: 'right',
          color: COCKPIT_COLORS.text
        }
      }
    ]
  }
}

function buildActionHeatOption() {
  const items = actionHeatItems.value
  if (!items.length) {
    return {
      graphic: [
        {
          type: 'text',
          left: 'center',
          top: 'center',
          style: {
            text: '暂无联动入口数据',
            fill: COCKPIT_COLORS.muted,
            font: '14px "Source Han Sans SC", sans-serif',
            textAlign: 'center'
          }
        }
      ]
    }
  }

  const maxValue = Math.max(1, ...items.map(item => item.value))
  const palette = [
    [COCKPIT_COLORS.cyan, `${COCKPIT_COLORS.cyan}33`],
    [COCKPIT_COLORS.blue, `${COCKPIT_COLORS.blue}33`],
    [COCKPIT_COLORS.green, `${COCKPIT_COLORS.green}33`],
    [COCKPIT_COLORS.warning, `${COCKPIT_COLORS.warning}33`],
    [COCKPIT_COLORS.violet, `${COCKPIT_COLORS.violet}33`],
    [COCKPIT_COLORS.danger, `${COCKPIT_COLORS.danger}33`]
  ]

  return {
    tooltip: {
      trigger: 'item',
      backgroundColor: COCKPIT_COLORS.tooltip,
      borderColor: 'rgba(82, 230, 255, 0.28)',
      textStyle: { color: COCKPIT_COLORS.text },
      formatter: params => `${params.data.fullName}<br/>热度 ${params.data.value}`
    },
    polar: {
      radius: ['26%', '82%']
    },
    angleAxis: {
      type: 'category',
      data: items.map(item => item.name),
      axisTick: { show: false },
      axisLine: { show: false },
      axisLabel: {
        color: COCKPIT_COLORS.axis,
        interval: 0,
        fontSize: 11
      }
    },
    radiusAxis: {
      min: 0,
      max: Math.ceil(maxValue * 1.18),
      axisLabel: { show: false },
      axisTick: { show: false },
      axisLine: { show: false },
      splitLine: { lineStyle: { color: COCKPIT_COLORS.split } }
    },
    series: [
      {
        type: 'bar',
        coordinateSystem: 'polar',
        roundCap: true,
        barWidth: 18,
        data: items.map((item, index) => {
          const [fromColor, toColor] = palette[index % palette.length]
          return {
            value: item.value,
            fullName: item.fullName,
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: fromColor },
                { offset: 1, color: toColor }
              ]),
              shadowBlur: 16,
              shadowColor: `${fromColor}66`
            }
          }
        })
      }
    ],
    graphic: [
      {
        type: 'text',
        left: 'center',
        top: '40%',
        style: {
          text: `${items.length}`,
          fill: COCKPIT_COLORS.text,
          font: '700 28px "Source Han Sans SC", sans-serif',
          textAlign: 'center'
        }
      },
      {
        type: 'text',
        left: 'center',
        top: '56%',
        style: {
          text: '联动入口',
          fill: COCKPIT_COLORS.muted,
          font: '12px "Source Han Sans SC", sans-serif',
          textAlign: 'center'
        }
      }
    ]
  }
}

function ensureExtraChart(key, element) {
  if (!element) {
    return null
  }
  if (!extraCharts.has(key)) {
    extraCharts.set(key, echarts.init(element))
  }
  return extraCharts.get(key)
}

function renderExtraCharts() {
  ensureExtraChart('health', healthGaugeChartRef.value)?.setOption(buildHealthGaugeOption(), true)
  ensureExtraChart('entityProfile', entityProfileChartRef.value)?.setOption(buildEntityProfileOption(), true)
  ensureExtraChart('radar', radarChartRef.value)?.setOption(buildRadarOption(), true)
  ensureExtraChart('modulePressure', modulePressureChartRef.value)?.setOption(buildModulePressureOption(), true)
  ensureExtraChart('focusRank', focusRankChartRef.value)?.setOption(buildFocusRankOption(), true)
  ensureExtraChart('focusBubble', focusBubbleChartRef.value)?.setOption(buildFocusBubbleOption(), true)
  ensureExtraChart('liveCompare', liveCompareChartRef.value)?.setOption(buildLiveCompareOption(), true)
  ensureExtraChart('moduleLink', moduleLinkChartRef.value)?.setOption(buildModuleLinkOption(), true)
  ensureExtraChart('actionHeat', actionHeatChartRef.value)?.setOption(buildActionHeatOption(), true)
  resizeExtraCharts()
}

function scheduleExtraChartsRender() {
  nextTick(() => {
    window.requestAnimationFrame(() => {
      renderExtraCharts()
    })
  })
}

function resizeExtraCharts() {
  extraCharts.forEach(chart => chart.resize())
}

function disposeExtraCharts() {
  extraCharts.forEach(chart => chart.dispose())
  extraCharts.clear()
}

function startClock() {
  stopClock()
  currentTimeText.value = formatCurrentTime()
  clockTimer = window.setInterval(() => {
    currentTimeText.value = formatCurrentTime()
  }, 1000)
}

function stopClock() {
  if (clockTimer) {
    window.clearInterval(clockTimer)
    clockTimer = null
  }
}

watch(
  [healthMetrics, governanceMatrixItems, modulePressureItems, entityProfileItems, focusRankingItems, moduleLinkChartItems, actionHeatItems, liveCompareItems, activeFocusKey, focusItems],
  scheduleExtraChartsRender,
  { deep: true }
)

watchEffect(() => {
  setPageGuide({
    title: roleTitle.value || '用工保障综合驾驶舱',
    description: roleDescription.value || '聚焦重点对象、模块协同和趋势复核。',
    portalExplanation: portalExplanationItems.value,
    focus: focusItems.value,
    selection: [
      { label: '区域', value: regionLabel.value },
      { label: '月份', value: queryParams.value.statMonth || '--' },
      { label: '链路', value: activeFocus.value?.title || primaryPathLabel.value },
      { label: '摘要', value: activeFocusSummary.value }
    ],
    workflow: workflowSteps.value,
    hints: hintTags.value
  })
})

onMounted(() => {
  startClock()
  window.addEventListener('resize', resizeExtraCharts)
  scheduleExtraChartsRender()
})

onActivated(() => {
  startClock()
  scheduleExtraChartsRender()
})

onDeactivated(() => {
  stopClock()
  disposeExtraCharts()
})

onBeforeUnmount(() => {
  stopClock()
  window.removeEventListener('resize', resizeExtraCharts)
  disposeExtraCharts()
})
</script>

<style scoped lang="scss">
.ygb-cockpit-screen {
  min-height: 100%;
  padding: 18px;
  color: var(--cockpit-text, #edf8ff);
  background:
    radial-gradient(circle at top left, rgba(82, 230, 255, 0.18), transparent 24%),
    radial-gradient(circle at top right, rgba(61, 123, 255, 0.16), transparent 24%),
    radial-gradient(circle at bottom center, rgba(63, 224, 179, 0.08), transparent 28%),
    linear-gradient(180deg, #04111f 0%, #071728 46%, #020913 100%);
  border: 1px solid rgba(87, 192, 255, 0.12);
  border-radius: 24px;
  box-shadow: 0 28px 56px rgba(0, 0, 0, 0.34);
}

.cockpit-shell {
  display: grid;
  gap: 18px;
}

.shell-topbar,
.shell-hero,
.shell-foot,
.shell-console,
.hero-column,
.hero-center,
.foot-side-stack,
.center-lower-grid {
  display: grid;
  gap: 18px;
}

.shell-topbar {
  grid-template-columns: minmax(220px, 1fr) minmax(360px, auto) minmax(280px, 1fr);
  align-items: center;
  padding: 16px 24px;
  border: 1px solid rgba(87, 192, 255, 0.18);
  border-radius: 20px;
  background:
    linear-gradient(90deg, rgba(8, 25, 42, 0.92), rgba(5, 19, 34, 0.84)),
    radial-gradient(circle at top center, rgba(82, 230, 255, 0.16), transparent 44%);
  box-shadow: inset 0 0 0 1px rgba(120, 208, 255, 0.06);
}

.topbar-side {
  display: grid;
  gap: 6px;
}

.topbar-side__label,
.topbar-title__kicker,
.panel-tag {
  color: #59dcff;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.topbar-side strong {
  font-size: 20px;
  color: #f4fbff;
}

.topbar-side__meta,
.topbar-title p,
.panel-heading__desc {
  color: #89a9c5;
}

.topbar-title {
  text-align: center;
}

.topbar-title h1 {
  margin: 8px 0 6px;
  color: #f6fbff;
  font-size: 36px;
  font-weight: 700;
  letter-spacing: 0.06em;
  text-shadow: 0 0 18px rgba(82, 230, 255, 0.22);
}

.topbar-actions {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  flex-wrap: wrap;
  gap: 12px;
}

.topbar-clock {
  display: inline-flex;
  align-items: center;
  min-height: 36px;
  padding: 0 14px;
  border: 1px solid rgba(90, 218, 255, 0.2);
  border-radius: 999px;
  background: rgba(82, 230, 255, 0.08);
  color: #dff7ff;
  font-weight: 600;
}

.shell-hero {
  grid-template-columns: 300px minmax(0, 1fr) 332px;
  align-items: start;
}

.hero-column,
.foot-side-stack {
  align-content: start;
}

.hero-center {
  min-width: 0;
}

.center-lower-grid {
  grid-template-columns: minmax(0, 1.58fr) minmax(300px, 0.88fr);
}

.shell-foot {
  grid-template-columns: minmax(0, 1.6fr) 340px;
  align-items: start;
}

.frame-panel {
  position: relative;
  overflow: hidden;
  min-width: 0;
  padding: 18px;
  border: 1px solid rgba(87, 192, 255, 0.18);
  border-radius: 20px;
  background:
    linear-gradient(180deg, rgba(8, 25, 42, 0.96), rgba(4, 14, 26, 0.94)),
    radial-gradient(circle at top right, rgba(82, 230, 255, 0.08), transparent 32%);
  box-shadow:
    inset 0 0 0 1px rgba(120, 208, 255, 0.05),
    0 16px 36px rgba(1, 9, 18, 0.28);
}

.frame-panel::before,
.frame-panel::after {
  content: '';
  position: absolute;
  width: 44px;
  height: 44px;
  pointer-events: none;
}

.frame-panel::before {
  top: -1px;
  left: -1px;
  border-top: 2px solid rgba(82, 230, 255, 0.56);
  border-left: 2px solid rgba(82, 230, 255, 0.56);
}

.frame-panel::after {
  right: -1px;
  bottom: -1px;
  border-right: 2px solid rgba(61, 123, 255, 0.46);
  border-bottom: 2px solid rgba(61, 123, 255, 0.46);
}

.frame-panel--stage {
  padding: 24px;
  background:
    radial-gradient(circle at center top, rgba(82, 230, 255, 0.12), transparent 34%),
    linear-gradient(180deg, rgba(8, 25, 42, 0.98), rgba(4, 14, 26, 0.96));
}

.frame-panel--map {
  min-height: 100%;
}

.panel-heading {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 16px;
}

.panel-heading h2 {
  margin-top: 8px;
  color: #f4fbff;
  font-size: 20px;
}

.summary-matrix {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
}

.summary-tile {
  display: grid;
  grid-template-columns: 52px minmax(0, 1fr);
  gap: 12px;
  padding: 16px;
  border-radius: 16px;
  border: 1px solid rgba(103, 188, 238, 0.12);
  background: rgba(255, 255, 255, 0.03);
}

.summary-tile__icon {
  width: 52px;
  height: 52px;
  border-radius: 16px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  background: rgba(82, 230, 255, 0.12);
  color: #52e6ff;
}

.summary-tile--warning .summary-tile__icon {
  background: rgba(255, 191, 105, 0.12);
  color: #ffbf69;
}

.summary-tile--danger .summary-tile__icon {
  background: rgba(255, 124, 124, 0.12);
  color: #ff7c7c;
}

.summary-tile--success .summary-tile__icon {
  background: rgba(63, 224, 179, 0.12);
  color: #3fe0b3;
}

.summary-tile__body {
  min-width: 0;
}

.summary-tile__label {
  display: block;
  color: #9cc0da;
  font-size: 13px;
}

.summary-tile__value {
  display: block;
  margin-top: 10px;
  color: #f7fbff;
  font-size: 28px;
  font-weight: 700;
}

.summary-tile__value em {
  margin-left: 6px;
  color: #85a7c2;
  font-style: normal;
  font-size: 12px;
  font-weight: 600;
}

.summary-tile__note {
  margin: 10px 0 0;
  color: #7594ae;
  line-height: 1.6;
}

.stage-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 18px;
  margin-bottom: 18px;
}

.stage-head h2 {
  margin: 8px 0 12px;
  color: #f7fbff;
  font-size: 28px;
}

.stage-head p {
  margin: 0;
  color: #89a9c5;
  line-height: 1.8;
}

.stage-meta {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-end;
  gap: 10px;
}

.stage-meta__pill {
  display: inline-flex;
  align-items: center;
  min-height: 32px;
  padding: 0 12px;
  border-radius: 999px;
  border: 1px solid rgba(82, 230, 255, 0.18);
  background: rgba(82, 230, 255, 0.08);
  color: #dcf7ff;
  font-size: 12px;
}

.kpi-ribbon {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
  margin-bottom: 18px;
}

.kpi-ribbon__item {
  padding: 14px 16px;
  border: 1px solid rgba(103, 188, 238, 0.12);
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.03);
}

.kpi-ribbon__label {
  color: #8fb2ce;
  font-size: 12px;
}

.kpi-ribbon__value {
  margin-top: 8px;
  color: #f4fbff;
  font-size: 24px;
  font-weight: 700;
}

.kpi-ribbon__value span {
  margin-left: 4px;
  color: #88aac5;
  font-size: 12px;
}

.stage-chart-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.stage-chart-card {
  padding: 14px;
  border: 1px solid rgba(103, 188, 238, 0.12);
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.03);
}

.stage-chart-card__title {
  margin-bottom: 12px;
  color: #8fb2ce;
  font-size: 13px;
  font-weight: 600;
}

.quick-pill,
.module-linkage__item {
  border: 1px solid rgba(103, 188, 238, 0.12);
  background: rgba(255, 255, 255, 0.03);
}

.module-linkage__item p {
  color: #89a9c5;
  line-height: 1.7;
}

.module-linkage {
  display: grid;
  gap: 12px;
}

.quick-pill:hover,
.module-linkage__item:hover {
  transform: translateY(-1px);
  border-color: rgba(82, 230, 255, 0.32);
  background: rgba(82, 230, 255, 0.08);
}

.quick-pill strong,
.module-linkage__item strong {
  color: #f7fbff;
}

.quick-pill em {
  color: #52e6ff;
  font-style: normal;
  font-weight: 700;
}

.quick-pill__badge,
.micro-stats__item {
  display: inline-flex;
  align-items: center;
  width: fit-content;
  min-height: 26px;
  padding: 0 10px;
  border-radius: 999px;
  border: 1px solid rgba(82, 230, 255, 0.16);
  background: rgba(82, 230, 255, 0.08);
  color: #d9f6ff;
  font-size: 12px;
}

.chart-surface {
  width: 100%;
  border-radius: 16px;
  background: rgba(4, 18, 32, 0.42);
}

.chart-surface--medium {
  height: 238px;
}

.chart-surface--large {
  height: 320px;
}

.chart-surface--hero {
  height: 352px;
}

.chart-surface--action {
  height: 220px;
}

.chart-surface--map {
  height: 420px;
}

.micro-stats {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-end;
  gap: 10px;
}

.quick-strip {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 10px;
  margin-top: 14px;
}

.quick-pill {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 14px;
  width: 100%;
  min-height: 68px;
  padding: 14px 16px;
  border-radius: 16px;
  color: inherit;
  text-align: left;
  cursor: pointer;
  transition: border-color 0.2s ease, transform 0.2s ease, background 0.2s ease;
}

.quick-pill__main {
  display: grid;
  gap: 8px;
  min-width: 0;
}

.quick-pill strong {
  line-height: 1.5;
}

.shell-console {
  grid-template-columns: 1fr;
}

.readonly-alert {
  margin-bottom: 16px;
}

.console-form {
  margin-top: 8px;
}

.data-layout {
  display: grid;
  grid-template-columns: minmax(0, 1.4fr) 340px;
  gap: 18px;
}

.data-layout__table,
.data-layout__side {
  min-width: 0;
}

.module-linkage__item {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto;
  gap: 12px;
  width: 100%;
  padding: 16px;
  border-radius: 16px;
  color: inherit;
  text-align: left;
  cursor: pointer;
  transition: border-color 0.2s ease, transform 0.2s ease, background 0.2s ease;
}

.module-linkage__item span {
  color: #f4fbff;
  font-weight: 700;
}

.ygb-cockpit-screen :deep(.el-alert),
.ygb-cockpit-screen :deep(.el-table),
.ygb-cockpit-screen :deep(.el-empty) {
  background: transparent;
}

.ygb-cockpit-screen :deep(.el-alert) {
  border: 1px solid rgba(82, 230, 255, 0.16);
  border-radius: 16px;
  background: rgba(82, 230, 255, 0.08);
}

.ygb-cockpit-screen :deep(.el-alert__title),
.ygb-cockpit-screen :deep(.el-alert__description),
.ygb-cockpit-screen :deep(.el-form-item__label),
.ygb-cockpit-screen :deep(.el-empty__description p),
.ygb-cockpit-screen :deep(.el-table th),
.ygb-cockpit-screen :deep(.el-table td),
.ygb-cockpit-screen :deep(.el-table .cell) {
  color: #edf8ff;
}

.ygb-cockpit-screen :deep(.el-empty__description p) {
  color: #89a9c5;
}

.ygb-cockpit-screen :deep(.el-input__wrapper),
.ygb-cockpit-screen :deep(.el-select__wrapper),
.ygb-cockpit-screen :deep(.el-textarea__inner) {
  background: rgba(3, 18, 32, 0.88);
  box-shadow: inset 0 0 0 1px rgba(82, 230, 255, 0.12);
}

.ygb-cockpit-screen :deep(.el-input__inner),
.ygb-cockpit-screen :deep(.el-select__placeholder),
.ygb-cockpit-screen :deep(.el-range-input),
.ygb-cockpit-screen :deep(.el-date-editor .el-range-separator),
.ygb-cockpit-screen :deep(.el-date-editor .el-input__prefix),
.ygb-cockpit-screen :deep(.el-date-editor .el-input__suffix) {
  color: #dff4ff;
}

.ygb-cockpit-screen :deep(.el-card__header),
.ygb-cockpit-screen :deep(.el-card__body) {
  padding: 0;
}

.ygb-cockpit-screen :deep(.el-table) {
  border: 1px solid rgba(82, 230, 255, 0.12);
  border-radius: 16px;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.03);
}

.ygb-cockpit-screen :deep(.el-table th.el-table__cell) {
  background: rgba(61, 123, 255, 0.1) !important;
  border-bottom-color: rgba(82, 230, 255, 0.12) !important;
}

.ygb-cockpit-screen :deep(.el-table tr),
.ygb-cockpit-screen :deep(.el-table td.el-table__cell) {
  background: transparent;
  border-bottom-color: rgba(82, 230, 255, 0.08) !important;
}

.ygb-cockpit-screen :deep(.el-table--enable-row-hover .el-table__body tr:hover > td.el-table__cell) {
  background: rgba(82, 230, 255, 0.06);
}

.ygb-cockpit-screen :deep(.el-button) {
  border-radius: 10px;
}

.ygb-cockpit-screen :deep(.el-button--primary) {
  background: linear-gradient(135deg, #39c7ff, #2c6fff);
  border-color: rgba(61, 123, 255, 0.9);
  color: #02101d;
  font-weight: 700;
}

.ygb-cockpit-screen :deep(.el-button--default),
.ygb-cockpit-screen :deep(.el-button.is-plain) {
  background: rgba(255, 255, 255, 0.04);
  border-color: rgba(82, 230, 255, 0.18);
  color: #dff4ff;
}

.ygb-cockpit-screen :deep(.el-button--warning.is-plain) {
  color: #ffd18e;
  border-color: rgba(255, 191, 105, 0.3);
  background: rgba(255, 191, 105, 0.08);
}

@media (max-width: 1600px) {
  .shell-hero {
    grid-template-columns: 280px minmax(0, 1fr) 310px;
  }

  .shell-foot {
    grid-template-columns: minmax(0, 1fr) 310px;
  }

}

@media (max-width: 1400px) {
  .shell-topbar,
  .shell-hero,
  .shell-foot,
  .center-lower-grid,
  .data-layout {
    grid-template-columns: 1fr;
  }

  .topbar-title {
    order: -1;
  }

  .topbar-actions,
  .micro-stats,
  .stage-meta {
    justify-content: flex-start;
  }
}

@media (max-width: 992px) {
  .summary-matrix,
  .kpi-ribbon,
  .stage-chart-grid,
  .quick-strip {
    grid-template-columns: 1fr;
  }

  .shell-topbar {
    padding: 16px 18px;
  }

  .topbar-title h1 {
    font-size: 28px;
  }
}

@media (max-width: 768px) {
  .ygb-cockpit-screen {
    padding: 12px;
  }

  .frame-panel,
  .frame-panel--stage {
    padding: 16px;
    border-radius: 18px;
  }

  .summary-tile,
  .module-linkage__item {
    grid-template-columns: 1fr;
  }

  .quick-pill {
    align-items: flex-start;
    flex-direction: column;
  }
}

@media (prefers-reduced-motion: reduce) {
  .quick-pill,
  .module-linkage__item {
    transition: none;
  }
}
</style>
