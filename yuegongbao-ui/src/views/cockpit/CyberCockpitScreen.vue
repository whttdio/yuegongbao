<template>
  <div
    class="cockpit-screen"
    :class="{ 'is-immersive': isImmersive, 'is-loading': loading }"
    v-loading="loading"
    element-loading-text="数据加载中..."
    element-loading-custom-class="cockpit-loading"
  >
    <div class="cockpit-screen__aurora" />
    <div class="cockpit-screen__grid" />
    <div class="cockpit-screen__beam cockpit-screen__beam--left" />
    <div class="cockpit-screen__beam cockpit-screen__beam--right" />
    <div class="cockpit-screen__corner cockpit-screen__corner--tl" />
    <div class="cockpit-screen__corner cockpit-screen__corner--tr" />
    <div class="cockpit-screen__corner cockpit-screen__corner--bl" />
    <div class="cockpit-screen__corner cockpit-screen__corner--br" />

    <div class="cockpit-screen__shell">
      <header class="cockpit-header">
        <div class="cockpit-header__left">
          <div class="cockpit-header__logo">🛡</div>
          <div>
            <div class="cockpit-header__title">{{ pageConfig.title }}</div>
            <div class="cockpit-header__subtitle">{{ pageConfig.kicker }} · {{ pageDescription }}</div>
          </div>
        </div>
        <div class="cockpit-header__right">
          <span><i class="cockpit-header__dot" /> 系统运行正常</span>
          <span>{{ regionLabel }} · {{ roleLabel }}</span>
          <span class="cockpit-header__time">{{ currentTimeText }}</span>
        </div>
      </header>

      <el-alert
        v-if="isReadOnlyRole"
        :title="readOnlyAlertTitle"
        :description="readOnlyAlertDescription"
        type="info"
        :closable="false"
        show-icon
        class="readonly-banner"
      />

      <section v-if="isPanelVisible('panel-metrics')" class="cockpit-metrics-row">
        <button
          v-for="item in visibleMetricCards"
          :key="item.key"
          type="button"
          class="metric-card-v4"
          :class="[
            accentClass(item),
            { 'metric-card-v4--hero': isRiskHeroKey(item.key) },
          ]"
          @click="item.action && openModule(item.action)"
        >
          <span class="metric-card-v4__shimmer" />
          <span class="metric-card-v4__label">{{ item.label }}</span>
          <strong class="metric-card-v4__value">{{ item.value }}<em>{{ item.unit }}</em></strong>
          <span class="metric-card-v4__sub">{{ item.deltaText }}</span>
        </button>
      </section>

      <section class="cockpit-main-row">
        <article v-if="isPanelVisible('panel-warning')" class="screen-panel screen-panel--layer-primary">
          <div class="panel-header-v4">
            <span class="dot-indicator dot-indicator--red" />
            <span>实时预警流</span>
            <span class="panel-header-v4__extra">{{ warningItems.length }} 条</span>
          </div>
          <div class="panel-body">
            <cockpit-warning-stream :items="warningItems" @open-module="openModule" />
          </div>
        </article>

        <article v-if="isPanelVisible('panel-map')" class="screen-panel screen-panel--map screen-panel--layer-map map-panel">
          <div class="panel-header-v4 panel-header-v4--map">
            <div class="panel-header-v4__titles">
              <span class="dot-indicator" />
              <span>地图可视化</span>
              <strong>{{ pageConfig.mapTitle }}</strong>
            </div>
            <div class="layer-switches">
              <button
                v-for="item in mapLayerOptions"
                :key="item.key"
                type="button"
                class="layer-switch"
                :class="{ 'is-active': activeMapLayers.includes(item.key) }"
                @click="toggleMapLayer(item.key)"
              >
                {{ item.label }}
              </button>
            </div>
          </div>
          <div class="panel-body map-wrap">
            <div ref="mapChartRef" class="map-wrap__chart" />
            <div class="map-wrap__legend cockpit-chart-legend">
              <div class="map-wrap__legend-bar map-wrap__legend-bar--breathe" />
              <div v-for="item in mapLegendItems" :key="item.label" class="cockpit-chart-legend__item legend-item">
                <i class="cockpit-chart-legend__dot" :style="{ background: item.color, color: item.color }" />
                <span>{{ item.label }}</span>
              </div>
            </div>
            <div class="map-wrap__stats">
              <div v-for="item in mapStats" :key="item.label" class="map-stat">
                <span>{{ item.label }}</span>
                <strong>{{ item.value }}</strong>
              </div>
            </div>
          </div>
        </article>

        <article v-if="isPanelVisible('panel-risk-ranking')" class="screen-panel screen-panel--layer-primary">
          <div class="panel-header-v4">
            <span class="dot-indicator dot-indicator--yellow" />
            <span>红黄绿码 · 企业风险排名</span>
          </div>
          <div class="panel-body">
            <cockpit-risk-ranking-panel
              ref="riskRankingRef"
              layout="table"
              :credit-summary="dashboardData.creditScoreSummary || {}"
              :ranking-list="creditRankingList"
              :colors="COLORS"
              @open-module="openModule"
            />
          </div>
        </article>
      </section>

      <section class="cockpit-bottom-row cockpit-bottom-row--1">
        <article v-if="isPanelVisible('panel-trend')" class="screen-panel screen-panel--layer-primary cockpit-bottom-span-2">
          <div class="panel-header-v4">
            <span class="dot-indicator" />
            <span>{{ pageConfig.trendTitle }}</span>
          </div>
          <div class="panel-body">
            <div ref="trendChartRef" class="chart-box chart-box--trend" />
          </div>
        </article>

        <article v-if="isPanelVisible('panel-region-summary')" class="screen-panel screen-panel--layer-primary">
          <div class="panel-header-v4">
            <span class="dot-indicator dot-indicator--green" />
            <span>{{ pageConfig.regionSummaryTitle }}</span>
          </div>
          <div class="panel-body">
            <div class="region-summary-grid">
              <div
                v-for="item in regionSummaryItems"
                :key="item.label"
                class="region-summary-card"
                :class="item.tone ? `region-summary-card--${item.tone}` : ''"
              >
                <span>{{ item.label }}</span>
                <strong>{{ item.value }}</strong>
                <em>{{ item.note }}</em>
              </div>
            </div>
          </div>
        </article>
      </section>

      <section class="cockpit-bottom-row cockpit-bottom-row--2">
        <article v-if="isPanelVisible('panel-table')" class="screen-panel screen-panel--layer-table cockpit-bottom-span-2">
          <div class="panel-header-v4">
            <span class="dot-indicator" />
            <span>地图可视范围内企业点位明细</span>
          </div>
          <div class="panel-body">
            <cockpit-enterprise-table compact :rows="visibleEnterpriseRows" @row-click="handleEnterpriseRowClick" />
          </div>
        </article>

        <article v-if="isPanelVisible('panel-risk-ranking')" class="screen-panel screen-panel--layer-primary">
          <div class="panel-header-v4">
            <span class="dot-indicator dot-indicator--yellow" />
            <span>红黄绿码企业分布</span>
          </div>
          <div class="panel-body">
            <cockpit-risk-ranking-panel
              ref="riskDonutRef"
              layout="chart"
              :credit-summary="dashboardData.creditScoreSummary || {}"
              :ranking-list="creditRankingList"
              :colors="COLORS"
            />
          </div>
        </article>
      </section>

      <cockpit-config-bar
        :visible-panels="prefs.visiblePanels"
        :region-code="queryParams.regionCode"
        :stat-month="queryParams.statMonth"
        :days="queryParams.days"
        :region-options="regionOptions"
        :day-options="dayOptions"
        :last-refresh-label="lastRefreshLabel"
        :is-immersive="isImmersive"
        @toggle-panel="togglePanelVisible"
        @update:region-code="handleConfigRegionChange"
        @update:stat-month="handleConfigStatMonthChange"
        @update:days="handleConfigDaysChange"
        @refresh="refreshDashboard"
        @export="handleExport"
        @open-advanced="openConfigDialog"
        @toggle-immersive="toggleImmersive"
      />
    </div>

    <cockpit-config-dialog
      v-model="configDialogVisible"
      :draft="draftPrefs"
      :metric-options="metricCardOptions"
      @save="saveConfig"
    />
  </div>
</template>

<script setup name="CyberCockpitScreen">
import { computed, nextTick, onActivated, onBeforeUnmount, onDeactivated, onMounted, ref, watch, watchEffect } from "vue"
import * as echarts from "echarts"
import { Download, Refresh, Setting } from "@element-plus/icons-vue"
import { getAzbCockpitDashboard, getYgbCockpitDashboard } from "@/api/ygb/cockpit"
import { listCreditScore } from "@/api/ygb/creditScore"
import { listWarning } from "@/api/ygb/warning"
import { useWorkbenchAssist } from "@/composables/useWorkbenchAssist"
import useAppStore from "@/store/modules/app"
import { useAuthorizedRegionOptions } from "@/utils/regionScope"
import { useRoleViewMode } from "@/utils/roleView"
import CockpitConfigBar from "@/views/cockpit/components/CockpitConfigBar.vue"
import CockpitConfigDialog from "@/views/cockpit/components/CockpitConfigDialog.vue"
import CockpitEnterpriseTable from "@/views/cockpit/components/CockpitEnterpriseTable.vue"
import CockpitRiskRankingPanel from "@/views/cockpit/components/CockpitRiskRankingPanel.vue"
import CockpitWarningStream from "@/views/cockpit/components/CockpitWarningStream.vue"
import { useCockpitConfig } from "@/views/cockpit/useCockpitConfig"
import {
  cockpitDayOptions as dayOptions,
  cockpitRegionNameMap as regionNameMap,
  cockpitRegionOptions as allRegionOptions,
  formatTrendDate,
  useCockpitPage,
} from "@/views/cockpit/useCockpitPage"
import guangdongGeoJsonRaw from "@/assets/geo/guangdong.geojson?raw"

const props = defineProps({
  mode: {
    type: String,
    default: "ygb",
  },
})

const RISK_HERO_KEYS = new Set([
  "creditRed",
  "warningPending",
  "highRiskEnterpriseCount",
  "todayWarningCount",
  "riskPolicy",
  "newInjuryRate",
])

function isRiskHeroKey(key) {
  return RISK_HERO_KEYS.has(key)
}

const CHART_LEGEND_STYLE = Object.freeze({
  icon: "circle",
  itemHeight: 8,
  itemWidth: 8,
  itemGap: 14,
  textStyle: { color: "rgba(207, 230, 236, 0.82)", fontSize: 11 },
  pageIconColor: "#00e5ff",
  pageTextStyle: { color: "rgba(207, 230, 236, 0.72)", fontSize: 11 },
})

const MAP_NAME = "guangdong-cockpit-map"
const guangdongGeoJson = JSON.parse(guangdongGeoJsonRaw)
if (!echarts.getMap(MAP_NAME)) {
  echarts.registerMap(MAP_NAME, guangdongGeoJson)
}

const COLORS = Object.freeze({
  cyan: "#00e5ff",
  blue: "#2ea4ff",
  green: "#00ffc8",
  lime: "#78ffd2",
  amber: "#ffcc00",
  danger: "#ff4d4f",
  violet: "#31c7ff",
  text: "#ebfaff",
  muted: "rgba(208, 231, 237, 0.72)",
  border: "rgba(0, 229, 255, 0.18)",
  split: "rgba(0, 229, 255, 0.1)",
  tooltip: "rgba(5, 15, 28, 0.96)",
})

const TREND_SERIES_FIVE = Object.freeze([
  { name: "工伤事故趋势", type: "line", dataKey: "overdueInjuryCount", yAxisIndex: 0 },
  { name: "预警数量趋势", type: "bar", dataKey: "todayWarningCount", yAxisIndex: 0, barMaxWidth: 16 },
  { name: "参保扩面进度", type: "line", dataKey: "expandCompletionRate", yAxisIndex: 1 },
  { name: "设备改造进度", type: "line", dataKey: "onlineDeviceCount", yAxisIndex: 0 },
  { name: "职业病发病趋势", type: "line", dataKey: "newInjuryRate", yAxisIndex: 1 },
])

const PAGE_CONFIG = Object.freeze({
  ygb: {
    kicker: "粤工保 领导驾驶舱",
    title: "广东省用工保障综合驾驶舱",
    metricTitle: "核心监管指标",
    trendTitle: "工伤 预警 扩面 设备 职业病趋势",
    mapTitle: "企业风险热力与空间态势图",
    regionSummaryTitle: "区域监管态势摘要",
    warningTitle: "滚动预警台账",
    description: "聚焦企业、人员、参保、预警和扩面治理联动。",
    fetchDashboard: getYgbCockpitDashboard,
    exportPrefix: "ygb_cockpit",
    warningPath: "/warning-center/workOrder",
    trendColors: [COLORS.danger, COLORS.cyan, COLORS.green, COLORS.blue, COLORS.amber],
    trendSeries: TREND_SERIES_FIVE,
  },
  azb: {
    kicker: "安责保 领导驾驶舱",
    title: "广东省安全治理综合驾驶舱",
    metricTitle: "核心治理指标",
    trendTitle: "工伤 预警 扩面 设备 职业病趋势",
    mapTitle: "安全风险热力与点位态势图",
    regionSummaryTitle: "区域治理态势摘要",
    warningTitle: "滚动预警台账",
    description: "聚焦预警、设备、工伤、安责险和区域治理联动。",
    fetchDashboard: getAzbCockpitDashboard,
    exportPrefix: "azb_cockpit",
    warningPath: "/azb/warning",
    trendColors: [COLORS.danger, COLORS.cyan, COLORS.green, COLORS.blue, COLORS.amber],
    trendSeries: TREND_SERIES_FIVE,
  },
})

const pageConfig = computed(() => PAGE_CONFIG[props.mode] || PAGE_CONFIG.ygb)
const regionOptions = useAuthorizedRegionOptions(allRegionOptions)
const appStore = useAppStore()
const { setPageGuide } = useWorkbenchAssist()
const { isReadOnlyRole, readOnlyRoleLabel, readOnlyRoleDescription } = useRoleViewMode()

const {
  prefs,
  draftPrefs,
  dialogVisible: configDialogVisible,
  mapConfig,
  loadConfig,
  openDialog: openConfigDialog,
  applyDraft,
  togglePanel,
  isPanelVisible,
  isMetricVisible,
} = useCockpitConfig(computed(() => props.mode))

const activeMapLayers = ref(["risk", "points", "fence", "route", "station"])
const isImmersive = ref(false)
const currentTimeText = ref("")
const warningRecords = ref([])
const creditRankingList = ref([])
const riskRankingRef = ref(null)
const riskDonutRef = ref(null)
const lastRefreshLabel = ref("上次刷新：--")
const sidebarOpenedBeforeImmersive = ref(true)

let clockTimer = null
let refreshTimer = null

const {
  loading,
  queryParams,
  dashboardData,
  indicators,
  trendList,
  featureCollection,
  visibleEnterpriseRows,
  trendChartRef,
  mapChartRef,
  handleQuery,
  handleExport,
  openModule,
  loadDashboard,
  highlightMapPoint,
  updateMapViewBoundsFromChart,
} = useCockpitPage({
  fetchDashboard: (query) => pageConfig.value.fetchDashboard(query),
  exportFilePrefix: pageConfig.value.exportPrefix,
  trendColors: pageConfig.value.trendColors,
  trendSeries: pageConfig.value.trendSeries,
  mapConfigRef: mapConfig,
  createTrendChartOption,
  createMapChartOption,
})

function refreshDashboard() {
  handleQuery()
  loadAuxiliaryData()
  lastRefreshLabel.value = `上次刷新：${formatTimeText()}`
}

function accentClass(item) {
  if (item.tone === "danger") return "accent-danger"
  if (item.tone === "amber") return "accent-warn"
  if (item.tone === "green" || item.tone === "lime") return "accent-good"
  return "accent-cyan"
}

function togglePanelVisible(key, visible) {
  togglePanel(key, visible)
  nextTick(() => {
    riskRankingRef.value?.resize?.()
    riskDonutRef.value?.resize?.()
  })
}

function handleConfigRegionChange(value) {
  queryParams.value.regionCode = value
  handleRegionChange()
}

function handleConfigStatMonthChange(value) {
  queryParams.value.statMonth = value
  refreshDashboard()
}

function handleConfigDaysChange(value) {
  queryParams.value.days = value
  refreshDashboard()
}

function handleRegionChange() {
  loadConfig(queryParams.value.regionCode)
  refreshDashboard()
}

function saveConfig() {
  applyDraft(metricCardOptions.value)
  setupRefreshTimer()
  refreshDashboard()
}

function numeric(value, fallback = 0) {
  const number = Number(value ?? fallback)
  return Number.isFinite(number) ? number : fallback
}

function formatNumber(value, digits = 0) {
  return new Intl.NumberFormat("zh-CN", {
    minimumFractionDigits: digits,
    maximumFractionDigits: digits,
  }).format(numeric(value))
}

function formatPercent(value, digits = 1) {
  return `${formatNumber(value, digits)}%`
}

function safeRegionName(code) {
  return regionNameMap[String(code || "")] || String(code || "")
}

function formatTimeText() {
  const now = new Date()
  const pad = (value) => String(value).padStart(2, "0")
  return `${now.getFullYear()}-${pad(now.getMonth() + 1)}-${pad(now.getDate())} ${pad(now.getHours())}:${pad(now.getMinutes())}:${pad(now.getSeconds())}`
}

function sparkFromTrend(dataKey) {
  const values = trendList.value.map((item) => numeric(item?.[dataKey]))
  if (!values.length) return [22, 34, 48, 42, 56, 68]
  const max = Math.max(...values, 1)
  return values.slice(-8).map((item) => Math.max(18, Math.round((item / max) * 100)))
}

function diffText(dataKey, unit = "", digits = 0) {
  const values = trendList.value.map((item) => numeric(item?.[dataKey]))
  const current = values[values.length - 1] ?? 0
  const previous = values[values.length - 2] ?? 0
  const diff = current - previous
  if (!diff) return { tone: "flat", text: "环比持平", share: "变化 0%" }
  const ratio = previous ? (diff / previous) * 100 : 100
  return {
    tone: diff > 0 ? "up" : "down",
    text: `${diff > 0 ? "环比 +" : "环比 "}${formatNumber(diff, digits)}${unit}`,
    share: `${ratio > 0 ? "增长" : "下降"} ${formatNumber(Math.abs(ratio), 1)}%`,
  }
}

function pickMetric(item) {
  return item.unit === "%" ? formatNumber(item.value, item.digits || 1) : formatNumber(item.value, item.digits || 0)
}

const metricCards = computed(() => {
  const base =
    props.mode === "azb"
      ? [
          { key: "policyTotal", label: "在保企业总数", value: numeric(dashboardData.value.aqInsuranceSummary?.totalCount ?? indicators.value.employerCount), unit: "家", seriesKey: "employerCount", action: { path: "/aq-insurance/insurance" }, tone: "cyan" },
          { key: "riskPolicy", label: "风险保单数量", value: numeric(dashboardData.value.aqInsuranceSummary?.riskCount), unit: "单", seriesKey: "todayWarningCount", action: { path: "/aq-insurance/insurance", query: { policyStatus: "2" } }, tone: "danger" },
          { key: "deviceOnline", label: "在线设备数量", value: numeric(dashboardData.value.deviceSummary?.onlineCount ?? indicators.value.onlineDeviceCount), unit: "台", seriesKey: "onlineDeviceCount", action: { path: "/device/ledger" }, tone: "green" },
          { key: "warningPending", label: "待处置预警", value: numeric(indicators.value.pendingWarningCount), unit: "条", seriesKey: "todayWarningCount", action: { path: pageConfig.value.warningPath }, tone: "amber" },
          { key: "aqInsuranceRate", label: "安责险覆盖率", value: numeric(indicators.value.aqInsuranceRate), unit: "%", digits: 1, seriesKey: "aqInsuranceRate", action: { path: "/aq-insurance/insurance" }, tone: "lime" },
          { key: "insuranceRate", label: "工伤参保率", value: numeric(indicators.value.insuranceRate), unit: "%", digits: 1, seriesKey: "insuranceRate", action: { path: "/ygb/socialPayment" }, tone: "blue" },
          { key: "fundBalance", label: "预防资金余额", value: numeric(dashboardData.value.preventionFundSummary?.remainingAmountTotal), unit: "万", seriesKey: "aqInsuranceRate", action: { path: "/aqInsurance/preventionFund" }, tone: "violet" },
          { key: "heightWorkActive", label: "高处作业在途", value: numeric(dashboardData.value.heightWorkReportSummary?.activeCount), unit: "项", seriesKey: "todayWarningCount", action: { path: "/heightWorkReport" }, tone: "cyan" },
          { key: "creditRed", label: "红码企业数量", value: numeric(dashboardData.value.creditScoreSummary?.redCount), unit: "家", seriesKey: "todayWarningCount", action: { path: "/credit/score", query: { colorCode: "RED" } }, tone: "danger" },
          { key: "reportReady", label: "治理月报产出", value: numeric(dashboardData.value.statReportSummary?.generatedCount), unit: "份", seriesKey: "aqInsuranceRate", action: { path: "/statReport" }, tone: "green" },
        ]
      : [
          { key: "dispatchCompanyCount", label: "派遣企业数", value: numeric(indicators.value.dispatchCompanyCount), unit: "家", seriesKey: "dispatchCompanyCount", action: { path: "/enterprisePortal/dispatch" }, tone: "cyan" },
          { key: "employerCount", label: "用工单位数", value: numeric(indicators.value.employerCount), unit: "家", seriesKey: "employerCount", action: { path: "/enterprisePortal/employer" }, tone: "blue" },
          { key: "dispatchedWorkerCount", label: "派遣员工总数", value: numeric(indicators.value.dispatchedWorkerCount), unit: "人", seriesKey: "dispatchedWorkerCount", action: { path: "/person" }, tone: "green" },
          { key: "highRiskEnterpriseCount", label: "高危企业数", value: numeric(indicators.value.highRiskEnterpriseCount), unit: "家", seriesKey: "highRiskEnterpriseCount", action: { path: pageConfig.value.warningPath, query: { riskLevel: "HIGH" } }, tone: "danger" },
          { key: "insuranceRate", label: "工伤参保率", value: numeric(indicators.value.insuranceRate), unit: "%", digits: 1, seriesKey: "insuranceRate", action: { path: "/socialPayment" }, tone: "lime" },
          { key: "aqInsuranceRate", label: "安责险覆盖率", value: numeric(indicators.value.aqInsuranceRate), unit: "%", digits: 1, seriesKey: "aqInsuranceRate", action: { path: "/aq-insurance/insurance" }, tone: "blue" },
          { key: "expandCompletionRate", label: "扩面完成率", value: numeric(indicators.value.expandCompletionRate), unit: "%", digits: 1, seriesKey: "expandCompletionRate", action: { path: "/uninsuredList" }, tone: "amber" },
          { key: "todayWarningCount", label: "当日预警数", value: numeric(indicators.value.todayWarningCount), unit: "条", seriesKey: "todayWarningCount", action: { path: pageConfig.value.warningPath }, tone: "violet" },
          { key: "newformCoverageCount", label: "新业态参保人数", value: numeric(dashboardData.value.newformWorkerSummary?.insuredCount ?? dashboardData.value.newformWorkerSummary?.totalCount), unit: "人", seriesKey: "dispatchedWorkerCount", action: { path: "/newformWorker" }, tone: "cyan" },
          { key: "newInjuryRate", label: "职业伤害发生率", value: numeric(indicators.value.newInjuryRate), unit: "%", digits: 2, seriesKey: "newInjuryRate", action: { path: "/occupationMonitor" }, tone: "danger" },
        ]

  return base.map((item) => {
    const diff = diffText(item.seriesKey, item.unit === "%" ? "%" : "", item.unit === "%" ? item.digits || 1 : 0)
    return {
      ...item,
      value: pickMetric(item),
      deltaTone: diff.tone,
      deltaText: diff.text,
      shareText: diff.share,
      sparkValues: sparkFromTrend(item.seriesKey),
    }
  })
})

const metricCardOptions = computed(() => metricCards.value.map((item) => ({ key: item.key, label: item.label })))
const visibleMetricCards = computed(() => metricCards.value.filter((item) => isMetricVisible(item.key)))

const regionLabel = computed(() => indicators.value.regionName || safeRegionName(queryParams.value.regionCode) || "广东省")
const roleLabel = computed(() => dashboardData.value.roleLabel || (props.mode === "azb" ? "安责保视角" : "粤工保视角"))
const pageDescription = computed(() => dashboardData.value.homeSummary || dashboardData.value.roleDescription || pageConfig.value.description)

const heroMetric = computed(() => {
  if (props.mode === "azb") {
    return {
      label: "在保企业总数",
      value: formatNumber(dashboardData.value.aqInsuranceSummary?.totalCount ?? indicators.value.employerCount),
      unit: "家",
    }
  }
  return {
    label: "在企企业数",
    value: formatNumber(indicators.value.employerCount),
    unit: "家",
  }
})

const heroMetaItems = computed(() => {
  if (props.mode === "azb") {
    return [
      { label: "安责险覆盖", value: formatPercent(indicators.value.aqInsuranceRate), tone: "green" },
      { label: "待处置预警", value: `${formatNumber(indicators.value.pendingWarningCount)} 条`, tone: "danger" },
      { label: "在线设备", value: `${formatNumber(indicators.value.onlineDeviceCount)} 台`, tone: "cyan" },
    ]
  }
  return [
    { label: "派遣员工", value: `${formatNumber(indicators.value.dispatchedWorkerCount)} 人`, tone: "cyan" },
    { label: "工伤参保率", value: formatPercent(indicators.value.insuranceRate), tone: "green" },
    { label: "当日预警", value: `${formatNumber(indicators.value.todayWarningCount)} 条`, tone: "danger" },
  ]
})

const regionSummaryItems = computed(() => {
  if (props.mode === "azb") {
    return [
      { label: "安责险覆盖率", value: formatPercent(indicators.value.aqInsuranceRate), note: "承保协同", tone: "green" },
      { label: "待处置预警", value: `${formatNumber(indicators.value.pendingWarningCount)} 条`, note: "实时处置", tone: "danger" },
      { label: "在线设备", value: `${formatNumber(indicators.value.onlineDeviceCount)} 台`, note: "设备在线", tone: "cyan" },
      { label: "红码企业", value: `${formatNumber(dashboardData.value.creditScoreSummary?.redCount)} 家`, note: "重点复核", tone: "danger" },
    ]
  }
  return [
    { label: "工伤参保率", value: formatPercent(indicators.value.insuranceRate), note: "参保水平", tone: "green" },
    { label: "高危企业", value: `${formatNumber(indicators.value.highRiskEnterpriseCount)} 家`, note: "风险聚焦", tone: "danger" },
    { label: "扩面完成率", value: formatPercent(indicators.value.expandCompletionRate), note: "扩面进度", tone: "warning" },
    { label: "企业点位", value: `${formatNumber(mapStats.value[0]?.value)} 个`, note: "地图覆盖", tone: "cyan" },
  ]
})

function splitFeatures() {
  const features = featureCollection.value.features || []
  return {
    points: features.filter((item) => item.geometry?.type === "Point"),
    polygons: features.filter((item) => item.geometry?.type === "Polygon" || item.geometry?.type === "MultiPolygon"),
  }
}

const mapStats = computed(() => {
  const { points, polygons } = splitFeatures()
  const enterpriseCount = points.filter((item) => item.properties?.featureType === "ENTERPRISE").length
  const deviceCount = points.filter((item) => item.properties?.featureType === "DEVICE").length
  const stationCount = points.filter((item) => String(item.properties?.sourceMode || "").toLowerCase().includes("station")).length
  return [
    { label: "企业点位", value: enterpriseCount },
    { label: "设备点位", value: deviceCount },
    { label: "围栏区域", value: polygons.length },
    { label: "服务站点", value: stationCount },
  ]
})

const mapLegendItems = computed(() => [
  { label: "低风险", color: COLORS.cyan },
  { label: "中风险", color: COLORS.amber },
  { label: "高风险", color: COLORS.danger },
  { label: "企业点位", color: COLORS.cyan },
  { label: "设备点位", color: COLORS.green },
  { label: "电子围栏", color: COLORS.amber },
])

const mapLayerOptions = [
  { key: "risk", label: "热力" },
  { key: "points", label: "点位" },
  { key: "fence", label: "围栏" },
  { key: "route", label: "轨迹" },
  { key: "station", label: "站点" },
]

function regionBucket(code) {
  return String(code || "").slice(0, 4)
}

const cityRiskItems = computed(() => {
  const { points } = splitFeatures()
  const buckets = new Map()
  points.forEach((item) => {
    const key = regionBucket(item.properties?.regionCode)
    if (!key) return
    if (!buckets.has(key)) buckets.set(key, { enterprise: 0, device: 0, warning: 0, score: 0 })
    const bucket = buckets.get(key)
    const type = item.properties?.featureType
    const riskLevel = String(item.properties?.colorCode || item.properties?.riskLevel || "").toUpperCase()
    if (type === "ENTERPRISE") bucket.enterprise += 1
    if (type === "DEVICE") bucket.device += 1
    if (String(item.properties?.featureStatus || "") === "2") bucket.warning += 1
    bucket.score += type === "ENTERPRISE" ? 8 : 5
    if (riskLevel === "RED") bucket.score += 16
    if (riskLevel === "YELLOW") bucket.score += 8
    if (String(item.properties?.featureStatus || "") === "2") bucket.score += 10
  })

  return guangdongGeoJson.features.map((item, index) => {
    const adcode = String(item.properties?.adcode || "")
    const bucket = buckets.get(regionBucket(adcode)) || { enterprise: 0, device: 0, warning: 0, score: 0 }
    return {
      name: safeRegionName(adcode) || item.properties?.name || `区域${index + 1}`,
      adcode,
      enterpriseCount: bucket.enterprise,
      deviceCount: bucket.device,
      warningCount: bucket.warning,
      riskScore: bucket.score + bucket.warning * 6,
      insuranceRate: Math.max(45, Math.min(99, numeric(indicators.value.insuranceRate) - index * 0.4 + bucket.enterprise * 0.7)),
    }
  })
})

function warnLevelMeta(level) {
  if (String(level) === "3") return { level: "红色预警", tone: "critical" }
  if (String(level) === "2") return { level: "黄色预警", tone: "warning" }
  return { level: "蓝色预警", tone: "info" }
}

function warnStatusText(status) {
  if (String(status) === "1") return "处理中"
  if (String(status) === "2") return "已办结"
  if (String(status) === "3") return "误报"
  if (String(status) === "4") return "已升级"
  return "待处理"
}

const warningItems = computed(() => {
  if (warningRecords.value.length) {
    return warningRecords.value.map((item, index) => {
      const meta = warnLevelMeta(item.warnLevel)
      return {
        key: item.warnId || `warning-${index}`,
        level: meta.level,
        location: safeRegionName(item.regionCode) || item.enterpriseName || regionLabel.value,
        summary: item.content || item.warnType || "关注异常变化并及时处置。",
        status: warnStatusText(item.warnStatus),
        time: item.createTime || queryParams.value.statMonth || "",
        tone: meta.tone,
        action: { path: pageConfig.value.warningPath, query: { warnId: item.warnId } },
      }
    })
  }
  return [
    {
      key: "empty",
      level: "蓝色提示",
      location: regionLabel.value,
      summary: "当前暂无新增预警，系统保持持续监测。",
      status: "持续监测",
      time: queryParams.value.statMonth || "",
      tone: "normal",
      action: { path: pageConfig.value.warningPath },
    },
  ]
})

const readOnlyAlertTitle = computed(() => `${readOnlyRoleLabel.value}：当前仅保留总览、明细和导出入口`)
const readOnlyAlertDescription = computed(() => `${readOnlyRoleDescription.value} 当前驾驶舱保留可视化总览和联动跳转，不展示录入型操作。`)

function toggleMapLayer(key) {
  const current = new Set(activeMapLayers.value)
  if (current.has(key)) current.delete(key)
  else current.add(key)
  activeMapLayers.value = [...current]
}

function handleEnterpriseRowClick(row) {
  highlightMapPoint(row)
}

function createTrendChartOption({ trendList, trendSeries }) {
  return {
    color: pageConfig.value.trendColors,
    backgroundColor: "transparent",
    tooltip: {
      trigger: "axis",
      backgroundColor: COLORS.tooltip,
      borderColor: "rgba(0, 229, 255, 0.3)",
      borderWidth: 1,
      textStyle: { color: COLORS.text },
    },
    legend: {
      top: 0,
      ...CHART_LEGEND_STYLE,
      type: "scroll",
    },
    grid: { left: 42, right: 24, top: 56, bottom: 24 },
    xAxis: {
      type: "category",
      data: trendList.map((item) => formatTrendDate(item.statDate)),
      axisLine: { lineStyle: { color: COLORS.border } },
      axisLabel: { color: COLORS.muted },
      splitLine: { show: false },
    },
    yAxis: [
      {
        type: "value",
        name: "数量",
        minInterval: 1,
        axisLabel: { color: COLORS.muted },
        axisLine: { show: false },
        splitLine: { lineStyle: { color: COLORS.split } },
      },
      {
        type: "value",
        name: "比率",
        axisLabel: { formatter: "{value}%", color: COLORS.muted },
        axisLine: { show: false },
        splitLine: { show: false },
      },
    ],
    series: trendSeries.map((item, index) => {
      if (item.type === "bar") {
        return {
          name: item.name,
          type: "bar",
          yAxisIndex: item.yAxisIndex || 0,
          barMaxWidth: item.barMaxWidth || 16,
          data: trendList.map((row) => numeric(row[item.dataKey])),
          itemStyle: {
            borderRadius: [6, 6, 0, 0],
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: pageConfig.value.trendColors[index] },
              { offset: 1, color: `${pageConfig.value.trendColors[index]}20` },
            ]),
            shadowBlur: 10,
            shadowColor: `${pageConfig.value.trendColors[index]}55`,
          },
        }
      }
      const color = pageConfig.value.trendColors[index]
      return {
        name: item.name,
        type: "line",
        yAxisIndex: item.yAxisIndex || 0,
        smooth: true,
        symbol: "circle",
        symbolSize: 6,
        data: trendList.map((row) => numeric(row[item.dataKey])),
        lineStyle: { width: 2, color },
        itemStyle: {
          color,
          borderColor: "#dffcff",
          borderWidth: 1,
          shadowBlur: 10,
          shadowColor: `${color}aa`,
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: `${color}44` },
            { offset: 1, color: `${color}06` },
          ]),
        },
      }
    }),
  }
}

function createMapChartOption() {
  const { points, polygons } = splitFeatures()
  const showRisk = activeMapLayers.value.includes("risk")
  const showPoints = activeMapLayers.value.includes("points")
  const showFence = activeMapLayers.value.includes("fence")
  const showRoute = activeMapLayers.value.includes("route")
  const showStation = activeMapLayers.value.includes("station")
  const center = mapConfig.value.center || [113.28, 23.13]
  const zoom = mapConfig.value.zoom || 7

  const enterprisePoints = points.filter((item) => item.properties?.featureType === "ENTERPRISE")
  const devicePoints = points.filter((item) => item.properties?.featureType === "DEVICE")
  const stationPoints = points.filter((item) => String(item.properties?.sourceMode || "").toLowerCase().includes("station"))

  const routeLines = []
  const routeLength = Math.min(enterprisePoints.length, devicePoints.length, 6)
  for (let index = 0; index < routeLength; index += 1) {
    routeLines.push({
      coords: [enterprisePoints[index].geometry.coordinates, devicePoints[index].geometry.coordinates],
      name: `${enterprisePoints[index].properties?.featureName || "企业"} -> ${devicePoints[index].properties?.featureName || "设备"}`,
    })
  }

  return {
    backgroundColor: "transparent",
    tooltip: {
      trigger: "item",
      confine: true,
      backgroundColor: COLORS.tooltip,
      borderColor: "rgba(0, 229, 255, 0.28)",
      borderWidth: 1,
      textStyle: { color: COLORS.text },
      formatter: (params) => {
        if (params.seriesType === "map") {
          const item = params.data || {}
          return [
            `<strong>${item.name || params.name}</strong>`,
            `风险热度：${formatNumber(item.riskScore)}`,
            `企业点位：${formatNumber(item.enterpriseCount)}`,
            `设备点位：${formatNumber(item.deviceCount)}`,
            `预警数量：${formatNumber(item.warningCount)}`,
            `参保水平：${formatPercent(item.insuranceRate)}`,
          ].join("<br/>")
        }
        const raw = params.data?.raw || {}
        return [
          `<strong>${params.data?.name || raw.featureName || params.seriesName}</strong>`,
          `类型：${raw.featureType || "点位"}`,
          `风险等级：${raw.colorCode || raw.riskLevel || "--"}`,
          `参保率：${raw.insuranceRate != null ? `${raw.insuranceRate}%` : "--"}`,
          `区域：${safeRegionName(raw.regionCode) || regionLabel.value}`,
        ].join("<br/>")
      },
    },
    visualMap: showRisk
      ? {
          min: 0,
          max: Math.max(...cityRiskItems.value.map((item) => item.riskScore), 20),
          show: false,
          inRange: { color: ["#123d55", "#167c9d", "#00b8db", "#00e5ff", "#ffcc00", "#ff4d4f"] },
        }
      : undefined,
    geo: {
      map: MAP_NAME,
      roam: true,
      center,
      zoom,
      layoutCenter: ["50%", "50%"],
      layoutSize: "112%",
      label: { show: true, color: "rgba(217, 241, 247, 0.68)", fontSize: 10 },
      itemStyle: {
        areaColor: {
          type: "radial",
          x: 0.5,
          y: 0.48,
          r: 0.86,
          colorStops: [
            { offset: 0, color: "rgba(24, 88, 126, 0.96)" },
            { offset: 0.56, color: "rgba(12, 55, 85, 0.94)" },
            { offset: 1, color: "rgba(7, 29, 52, 0.92)" },
          ],
        },
        borderColor: "rgba(0, 229, 255, 0.72)",
        borderWidth: 1.2,
        shadowBlur: 20,
        shadowColor: "rgba(0, 229, 255, 0.18)",
      },
      emphasis: {
        label: { color: "#ebfaff", fontSize: 11 },
        itemStyle: {
          areaColor: "rgba(0, 164, 255, 0.66)",
          borderColor: "#00e5ff",
          borderWidth: 1.6,
        },
      },
    },
    series: [
      {
        name: "风险热力",
        type: "map",
        geoIndex: 0,
        data: cityRiskItems.value.map((item) => ({ ...item, value: showRisk ? item.riskScore : 0 })),
      },
      showPoints
        ? {
            name: "企业点位",
            type: "scatter",
            coordinateSystem: "geo",
            symbol: "circle",
            symbolSize: 14,
            itemStyle: { color: COLORS.cyan, shadowBlur: 18, shadowColor: `${COLORS.cyan}88` },
            data: enterprisePoints.map((item) => ({
              name: item.properties?.featureName || "企业点位",
              value: item.geometry.coordinates || [0, 0],
              raw: item.properties || {},
            })),
          }
        : null,
      showPoints
        ? {
            name: "设备点位",
            type: "scatter",
            coordinateSystem: "geo",
            symbol: "rect",
            symbolSize: 10,
            itemStyle: { color: COLORS.green, shadowBlur: 18, shadowColor: `${COLORS.green}88` },
            data: devicePoints.map((item) => ({
              name: item.properties?.featureName || "设备点位",
              value: item.geometry.coordinates || [0, 0],
              raw: item.properties || {},
            })),
          }
        : null,
      showStation
        ? {
            name: "服务站点",
            type: "effectScatter",
            coordinateSystem: "geo",
            symbolSize: 16,
            rippleEffect: { scale: 3 },
            itemStyle: { color: COLORS.violet, shadowBlur: 18, shadowColor: `${COLORS.violet}88` },
            data: stationPoints.map((item) => ({
              name: item.properties?.featureName || "服务站点",
              value: item.geometry.coordinates || [0, 0],
              raw: item.properties || {},
            })),
          }
        : null,
      showFence && polygons.length
        ? {
            name: "电子围栏",
            type: "lines",
            coordinateSystem: "geo",
            polyline: true,
            data: polygons.map((item) => ({
              name: item.properties?.featureName || "电子围栏",
              coords: item.geometry.coordinates?.[0] || [],
            })),
            lineStyle: { color: COLORS.amber, width: 1.4, opacity: 0.72, type: "dashed" },
          }
        : null,
      showRoute && routeLines.length
        ? {
            name: "人员轨迹",
            type: "lines",
            coordinateSystem: "geo",
            data: routeLines,
            lineStyle: { color: COLORS.green, width: 1.2, opacity: 0.5, curveness: 0.24 },
            effect: { show: true, period: 4, trailLength: 0.3, symbolSize: 4, color: COLORS.green },
          }
        : null,
    ].filter(Boolean),
  }
}

async function loadAuxiliaryData() {
  const regionCode = queryParams.value.regionCode
  const [warningRes, creditRes] = await Promise.allSettled([
    listWarning({ regionCode, pageNum: 1, pageSize: 30 }),
    listCreditScore({ regionCode, pageNum: 1, pageSize: 10, orderByColumn: "total_score", isAsc: "asc" }),
  ])
  if (warningRes.status === "fulfilled") {
    warningRecords.value = warningRes.value.rows || warningRes.value.data || []
  }
  if (creditRes.status === "fulfilled") {
    const remoteRows = creditRes.value.rows || creditRes.value.data || []
    creditRankingList.value = dashboardData.value.creditRanking?.length ? dashboardData.value.creditRanking : remoteRows
  } else {
    creditRankingList.value = dashboardData.value.creditRanking || []
  }
  nextTick(() => {
    riskRankingRef.value?.resize?.()
    riskDonutRef.value?.resize?.()
  })
}

function setupRefreshTimer() {
  if (refreshTimer) {
    clearInterval(refreshTimer)
    refreshTimer = null
  }
  const seconds = Number(prefs.value.refreshSeconds || 0)
  if (seconds > 0) {
    refreshTimer = window.setInterval(() => refreshDashboard(), seconds * 1000)
  }
}

function toggleImmersive() {
  if (!isImmersive.value) {
    sidebarOpenedBeforeImmersive.value = appStore.sidebar.opened
    appStore.toggleSideBarHide(true)
    document.body.classList.add("cockpit-immersive")
    isImmersive.value = true
    document.querySelector(".cockpit-screen")?.requestFullscreen?.()
    return
  }
  exitImmersive()
}

function exitImmersive() {
  isImmersive.value = false
  document.body.classList.remove("cockpit-immersive")
  appStore.toggleSideBarHide(false)
  if (!sidebarOpenedBeforeImmersive.value) {
    appStore.closeSideBar({ withoutAnimation: true })
  }
  if (document.fullscreenElement) {
    document.exitFullscreen?.()
  }
}

function syncFullscreenState() {
  if (!document.fullscreenElement && isImmersive.value) {
    exitImmersive()
  }
}

watch(
  () => [activeMapLayers.value.join("|"), mapConfig.value.center?.join(","), mapConfig.value.zoom],
  () => {
    nextTick(() => {
      loadDashboard().then(() => updateMapViewBoundsFromChart())
    })
  },
)

watchEffect(() => {
  setPageGuide({
    title: pageConfig.value.title,
    description: pageDescription.value,
    selection: [
      { label: "当前区域", value: regionLabel.value },
      { label: "统计月份", value: queryParams.value.statMonth || "--" },
      { label: "角色视角", value: roleLabel.value },
      { label: "核心指标", value: `${heroMetric.value.value}${heroMetric.value.unit}` },
    ],
    focus: warningItems.value,
  })
})

onMounted(async () => {
  document.body.classList.add("cockpit-page-active")
  currentTimeText.value = formatTimeText()
  clockTimer = window.setInterval(() => {
    currentTimeText.value = formatTimeText()
  }, 1000)
  document.addEventListener("fullscreenchange", syncFullscreenState)
  await loadConfig(queryParams.value.regionCode)
  setupRefreshTimer()
  await loadDashboard()
  await loadAuxiliaryData()
  lastRefreshLabel.value = `上次刷新：${formatTimeText()}`
})

onActivated(async () => {
  await loadDashboard()
  await loadAuxiliaryData()
  lastRefreshLabel.value = `上次刷新：${formatTimeText()}`
})

onDeactivated(() => {
  if (refreshTimer) clearInterval(refreshTimer)
})

onBeforeUnmount(() => {
  document.body.classList.remove("cockpit-page-active")
  if (clockTimer) clearInterval(clockTimer)
  if (refreshTimer) clearInterval(refreshTimer)
  document.removeEventListener("fullscreenchange", syncFullscreenState)
  document.body.classList.remove("cockpit-immersive")
})
</script>

<style scoped lang="scss">
.cockpit-screen {
  --cockpit-bg: #040a17;
  --cockpit-card-bg: rgba(6, 22, 44, 0.72);
  --cockpit-cyan: #00e5ff;
  --cockpit-green: #00ffc8;
  --cockpit-danger: #ff4d4f;
  --cockpit-amber: #ffcc00;
  --cockpit-col-left: clamp(288px, 19%, 350px);
  --cockpit-col-center: minmax(0, 1fr);
  --cockpit-col-right: clamp(272px, 17%, 330px);
  position: relative;
  min-height: calc(100vh - var(--layout-header-height, 50px) - var(--layout-tags-height, 34px));
  height: calc(100vh - var(--layout-header-height, 50px) - var(--layout-tags-height, 34px));
  margin: 0;
  padding: 4px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  color: #ebfaff;
  background:
    radial-gradient(circle at 50% 32%, rgba(0, 229, 255, 0.18), transparent 26%),
    radial-gradient(circle at 18% 20%, rgba(0, 255, 200, 0.08), transparent 26%),
    radial-gradient(circle at 82% 18%, rgba(0, 229, 255, 0.06), transparent 20%),
    linear-gradient(180deg, #040a17 0%, #061021 50%, #040a17 100%);
}

.cockpit-screen.is-immersive {
  position: fixed;
  inset: 0;
  z-index: 2000;
  height: 100vh;
}

.cockpit-screen__aurora,
.cockpit-screen__grid,
.cockpit-screen__beam,
.cockpit-screen__corner {
  position: absolute;
  pointer-events: none;
}

.cockpit-screen__aurora {
  inset: 0;
  background:
    radial-gradient(circle at 50% 0%, rgba(0, 229, 255, 0.18), transparent 34%),
    radial-gradient(circle at 50% 45%, rgba(0, 85, 128, 0.16), transparent 48%);
}

.cockpit-screen__grid {
  inset: 0;
  opacity: 0.32;
  background:
    linear-gradient(rgba(0, 229, 255, 0.04) 1px, transparent 1px),
    linear-gradient(90deg, rgba(0, 229, 255, 0.04) 1px, transparent 1px);
  background-size: 40px 40px;
  mask-image: radial-gradient(circle at 50% 42%, #000 42%, transparent 92%);
}

.cockpit-screen__beam {
  top: 74px;
  bottom: 18px;
  width: 1px;
  background: linear-gradient(180deg, transparent, rgba(0, 229, 255, 0.78), transparent);
  box-shadow: 0 0 16px rgba(0, 229, 255, 0.42);
}

.cockpit-screen__beam--left {
  left: 4px;
}

.cockpit-screen__beam--right {
  right: 4px;
}

.cockpit-screen__corner {
  width: 32px;
  height: 32px;
  border-color: rgba(0, 229, 255, 0.82);
  border-style: solid;
}

.cockpit-screen__corner--tl {
  top: 2px;
  left: 2px;
  border-width: 2px 0 0 2px;
}

.cockpit-screen__corner--tr {
  top: 2px;
  right: 2px;
  border-width: 2px 2px 0 0;
}

.cockpit-screen__corner--bl {
  bottom: 2px;
  left: 2px;
  border-width: 0 0 2px 2px;
}

.cockpit-screen__corner--br {
  bottom: 2px;
  right: 2px;
  border-width: 0 2px 2px 0;
}

.cockpit-screen__shell {
  position: relative;
  z-index: 1;
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.screen-header {
  position: relative;
  display: grid;
  grid-template-columns: var(--cockpit-col-left) var(--cockpit-col-center) var(--cockpit-col-right);
  grid-template-areas: "summary title tools";
  column-gap: 8px;
  row-gap: 0;
  align-items: center;
  min-height: 112px;
  padding: 6px 10px 4px;
  border: 1px solid rgba(0, 229, 255, 0.22);
  background:
    linear-gradient(180deg, rgba(6, 22, 44, 0.9), rgba(4, 14, 30, 0.96)),
    radial-gradient(circle at 50% 0%, rgba(0, 229, 255, 0.08), transparent 48%);
  box-shadow:
    inset 0 0 0 1px rgba(255, 255, 255, 0.02),
    0 0 18px rgba(0, 229, 255, 0.08);
}

.screen-header::before {
  content: "";
  position: absolute;
  left: 14px;
  right: 14px;
  top: 0;
  height: 2px;
  background: linear-gradient(90deg, transparent, rgba(0, 229, 255, 0.96), transparent);
  box-shadow: 0 0 16px rgba(0, 229, 255, 0.5);
}

.screen-header__titlebar {
  grid-area: title;
  min-width: 0;
  display: grid;
  justify-items: center;
  text-align: center;
  align-self: center;
  gap: 1px;
}

.screen-header__eyebrow {
  color: rgba(149, 240, 249, 0.86);
  font-size: 11px;
  letter-spacing: 0.14em;
}

.screen-header__titlebar h1 {
  margin: 0;
  color: #effcff;
  font-size: clamp(30px, 2vw, 36px);
  font-weight: 800;
  letter-spacing: 0.04em;
  line-height: 1.08;
  text-align: center;
  text-shadow: 0 0 12px rgba(0, 229, 255, 0.26);
}

.screen-header__titlebar p {
  margin: 0;
  color: rgba(205, 226, 232, 0.72);
  max-width: 520px;
  font-size: 10px;
  line-height: 1.2;
  text-align: center;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.screen-header__summary {
  grid-area: summary;
  align-self: stretch;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  gap: 6px;
  min-width: 0;
  min-height: 88px;
  padding: 8px 10px;
  border: 1px solid rgba(0, 229, 255, 0.14);
  background: linear-gradient(180deg, rgba(7, 24, 46, 0.82), rgba(5, 16, 32, 0.9));
  box-shadow: 0 0 14px rgba(0, 229, 255, 0.1);
}

.screen-header__summary-head {
  display: grid;
  gap: 6px;
}

.screen-header__summary-label {
  color: rgba(149, 240, 249, 0.82);
  font-size: 11px;
  letter-spacing: 0.12em;
}

.screen-header__chips {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.screen-header__chips span {
  padding: 2px 8px;
  border: 1px solid rgba(0, 229, 255, 0.14);
  background: rgba(0, 229, 255, 0.05);
  color: rgba(213, 236, 242, 0.78);
  font-size: 10px;
}

.screen-header__hero {
  align-self: stretch;
  justify-self: stretch;
  width: 100%;
}

.screen-header__tools {
  grid-area: tools;
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 6px;
  align-content: start;
  align-self: stretch;
  min-width: 0;
  min-height: 88px;
  padding: 8px 10px;
  border: 1px solid rgba(0, 229, 255, 0.14);
  background: linear-gradient(180deg, rgba(7, 24, 46, 0.82), rgba(5, 16, 32, 0.9));
  box-shadow: 0 0 14px rgba(0, 229, 255, 0.1);
}

.screen-header__tools-label {
  grid-column: 1 / -1;
  color: rgba(149, 240, 249, 0.82);
  font-size: 11px;
  letter-spacing: 0.12em;
  text-align: left;
}

.screen-header__tool-buttons {
  display: flex;
  grid-column: 1 / -1;
  gap: 8px;
  flex-wrap: wrap;
  justify-content: flex-end;
}

.screen-header__fullscreen {
  padding-inline: 14px;
}

.header-control {
  width: 100%;
  min-width: 0;
}

.header-control--month {
  width: 100%;
}

.header-control--short {
  width: 100%;
}

.readonly-banner {
  margin: 0;
}

.screen-layout {
  flex: 1;
  min-height: 0;
  display: grid;
  grid-template-columns: var(--cockpit-col-left) var(--cockpit-col-center) var(--cockpit-col-right);
  gap: 6px;
}

.layout-side,
.layout-center {
  min-height: 0;
}

.layout-side {
  display: grid;
  gap: 6px;
  overflow: hidden;
}

.layout-side--right {
  grid-template-rows: auto minmax(0, 1fr);
}

.layout-side--left {
  grid-template-rows: minmax(0, 1fr) minmax(0, 0.92fr) minmax(0, 1.08fr);
}

.layout-center {
  display: grid;
  gap: 4px;
  grid-template-rows: minmax(0, 56fr) minmax(0, 44fr);
}

.panel-grow,
.screen-panel {
  min-height: 0;
}

.screen-panel {
  position: relative;
  display: flex;
  flex-direction: column;
  padding: 8px;
}

.screen-panel > :not(.panel-header) {
  flex: 1;
  min-height: 0;
}

.screen-panel--compact > :not(.panel-header) {
  flex: 0;
}

.screen-panel::before {
  content: "";
  position: absolute;
  inset: 0;
  border: 1px solid rgba(0, 119, 153, 0.45);
  pointer-events: none;
}

.screen-panel::after {
  content: "";
  position: absolute;
  inset: 0;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.02), transparent 18%);
  pointer-events: none;
}

.panel-header {
  position: relative;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 8px;
  margin-bottom: 6px;
  padding-bottom: 4px;
  flex-shrink: 0;
}

.panel-header::after {
  content: "";
  position: absolute;
  left: 0;
  bottom: 0;
  width: 100%;
  height: 1px;
  background: linear-gradient(90deg, rgba(0, 229, 255, 0.84), rgba(0, 229, 255, 0.08));
}

.panel-header__tag {
  display: inline-flex;
  align-items: center;
  color: #00e5ff;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.08em;
}

.panel-header h2 {
  margin: 4px 0 0;
  color: #f0fcff;
  font-size: 16px;
  font-weight: 700;
  line-height: 1.4;
}

.panel-header__badge {
  padding: 4px 8px;
  border: 1px solid rgba(0, 229, 255, 0.18);
  background: rgba(0, 229, 255, 0.08);
  color: rgba(211, 234, 240, 0.76);
  font-size: 11px;
  white-space: nowrap;
}

.panel-header--map {
  align-items: center;
}

.metric-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 8px;
  flex: 1;
  min-height: 0;
  align-content: stretch;
  overflow: auto;
}

.metric-card {
  position: relative;
  display: grid;
  gap: 6px;
  min-height: 120px;
  padding: 10px 12px;
  border: 1px solid rgba(0, 229, 255, 0.14);
  background:
    linear-gradient(155deg, rgba(255, 255, 255, 0.03) 0%, transparent 40%),
    linear-gradient(180deg, rgba(8, 29, 54, 0.72), rgba(5, 18, 34, 0.88));
  backdrop-filter: blur(10px);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.03), 0 0 12px rgba(0, 229, 255, 0.1);
  color: inherit;
  text-align: left;
  cursor: pointer;
  overflow: hidden;
  transition: transform 0.22s ease, box-shadow 0.22s ease, border-color 0.22s ease, background 0.22s ease;
}

.metric-card::before {
  content: "";
  position: absolute;
  inset: 0;
  background: linear-gradient(120deg, rgba(0, 229, 255, 0.06), transparent 42%);
  opacity: 0;
  transition: opacity 0.22s ease;
  pointer-events: none;
}

.metric-card:hover {
  transform: translateY(-2px);
  border-color: rgba(0, 229, 255, 0.38);
  box-shadow:
    inset 0 1px 0 rgba(255, 255, 255, 0.05),
    0 0 22px rgba(0, 229, 255, 0.2);
}

.metric-card:hover::before {
  opacity: 1;
}

.metric-card--risk-hero {
  border-color: rgba(255, 77, 79, 0.32);
  background:
    linear-gradient(155deg, rgba(255, 77, 79, 0.1) 0%, transparent 42%),
    linear-gradient(180deg, rgba(42, 12, 18, 0.78), rgba(18, 8, 14, 0.92));
  box-shadow:
    inset 0 1px 0 rgba(255, 77, 79, 0.12),
    0 0 20px rgba(255, 77, 79, 0.18);
}

.metric-card--risk-hero.metric-card--amber {
  border-color: rgba(255, 204, 0, 0.32);
  background:
    linear-gradient(155deg, rgba(255, 204, 0, 0.1) 0%, transparent 42%),
    linear-gradient(180deg, rgba(36, 28, 8, 0.78), rgba(18, 14, 6, 0.92));
  box-shadow:
    inset 0 1px 0 rgba(255, 204, 0, 0.12),
    0 0 20px rgba(255, 204, 0, 0.16);
}

.metric-card--risk-hero:hover {
  box-shadow:
    inset 0 1px 0 rgba(255, 255, 255, 0.06),
    0 0 28px rgba(255, 77, 79, 0.28);
}

.metric-card--risk-hero.metric-card--amber:hover {
  box-shadow:
    inset 0 1px 0 rgba(255, 255, 255, 0.06),
    0 0 28px rgba(255, 204, 0, 0.24);
}

.metric-card__head {
  display: flex;
  justify-content: space-between;
  gap: 8px;
  align-items: baseline;
}

.metric-card__head span {
  color: rgba(207, 228, 234, 0.74);
  font-size: 12px;
}

.metric-card__head em {
  color: rgba(0, 229, 255, 0.72);
  font-size: 11px;
  font-style: normal;
}

.metric-card strong {
  color: #00ffc8;
  font-size: 24px;
  font-weight: 800;
  line-height: 1;
  text-align: center;
  text-shadow: 0 0 14px rgba(0, 229, 255, 0.26);
}

.metric-card--danger strong {
  color: var(--risk-red);
  text-shadow: 0 0 16px var(--risk-red-glow);
}

.metric-card--amber strong {
  color: var(--risk-yellow);
  text-shadow: 0 0 14px var(--risk-yellow-glow);
}

.metric-card--risk-hero strong {
  font-size: clamp(28px, 2.2vw, 34px);
  animation: cockpit-risk-pulse 2.8s ease-in-out infinite;
}

.metric-card--risk-hero.metric-card--amber strong {
  color: var(--risk-yellow);
  animation: cockpit-warning-pulse 2.8s ease-in-out infinite;
}

.metric-card__foot {
  display: flex;
  justify-content: space-between;
  gap: 8px;
  color: rgba(202, 223, 229, 0.68);
  font-size: 11px;
}

.metric-card__delta.up {
  color: #00ffc8;
}

.metric-card__delta.down {
  color: #ff4d4f;
}

.metric-card__delta.flat {
  color: #cfe5eb;
}

.metric-card__spark {
  display: grid;
  grid-template-columns: repeat(8, minmax(0, 1fr));
  align-items: end;
  gap: 4px;
  height: 28px;
}

.metric-card__spark i {
  display: block;
  min-height: 6px;
  background: linear-gradient(180deg, rgba(0, 229, 255, 0.92), rgba(0, 229, 255, 0.08));
}

.metric-card--green .metric-card__spark i,
.metric-card--lime .metric-card__spark i {
  background: linear-gradient(180deg, rgba(0, 255, 200, 0.92), rgba(0, 255, 200, 0.08));
}

.metric-card--danger .metric-card__spark i {
  background: linear-gradient(180deg, rgba(255, 77, 79, 0.92), rgba(255, 77, 79, 0.08));
}

.metric-card--amber .metric-card__spark i {
  background: linear-gradient(180deg, rgba(255, 204, 0, 0.92), rgba(255, 204, 0, 0.08));
}

.metric-card--blue .metric-card__spark i,
.metric-card--violet .metric-card__spark i {
  background: linear-gradient(180deg, rgba(46, 164, 255, 0.92), rgba(46, 164, 255, 0.08));
}

.chart-box--trend {
  flex: 1;
  min-height: 240px;
}

.screen-panel--map {
  overflow: hidden;
}

.screen-panel--table {
  overflow: hidden;
}

.map-wrap {
  position: relative;
  flex: 1;
  min-height: 0;
}

.map-wrap__chart {
  width: 100%;
  height: 100%;
  min-height: 240px;
}

.map-wrap__legend,
.map-wrap__stats {
  position: absolute;
  z-index: 2;
}

.map-wrap__legend {
  left: 8px;
  bottom: 8px;
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 4px 8px;
  max-width: min(420px, 46%);
  padding: 6px 8px;
  border: 1px solid rgba(0, 229, 255, 0.14);
  background: rgba(4, 12, 26, 0.72);
  backdrop-filter: blur(10px);
}

.map-wrap__legend-bar {
  flex: 1 1 100%;
  height: 6px;
  border: 1px solid rgba(0, 229, 255, 0.2);
  background: linear-gradient(90deg, #173e61, #00e5ff 45%, #ffcc00 72%, #ff4d4f 100%);
  box-shadow: 0 0 10px rgba(0, 229, 255, 0.18);
}

.map-wrap__legend-bar--breathe {
  animation: cockpit-heat-breathe 3.2s ease-in-out infinite;
}

.legend-item {
  padding: 0;
  border: none;
  background: transparent;
}

.map-wrap__stats {
  right: 8px;
  bottom: 8px;
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-end;
  gap: 4px;
  max-width: min(360px, 42%);
}

.map-stat {
  display: grid;
  gap: 2px;
  padding: 5px 8px;
  min-width: 72px;
  border: 1px solid rgba(0, 229, 255, 0.12);
  background: rgba(4, 12, 26, 0.72);
  backdrop-filter: blur(8px);
  transition: box-shadow 0.2s ease, border-color 0.2s ease;
}

.map-stat:hover {
  border-color: rgba(0, 229, 255, 0.28);
  box-shadow: 0 0 14px rgba(0, 229, 255, 0.14);
}

.map-stat span {
  color: rgba(204, 226, 232, 0.72);
  font-size: 11px;
}

.map-stat strong {
  color: #eafcff;
  font-size: 15px;
}

.layer-switches {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-end;
  gap: 8px;
}

.layer-switch {
  padding: 4px 12px;
  border: 1px solid rgba(0, 229, 255, 0.14);
  background: rgba(0, 229, 255, 0.05);
  color: rgba(206, 229, 235, 0.74);
  font-size: 11px;
  cursor: pointer;
  transition: all 0.18s ease;
}

.layer-switch.is-active,
.layer-switch:hover {
  color: #041017;
  border-color: transparent;
  background: linear-gradient(90deg, rgba(0, 229, 255, 0.96), rgba(0, 255, 200, 0.88));
  box-shadow: 0 0 12px rgba(0, 229, 255, 0.24);
}

.region-summary-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 6px;
  align-content: start;
}

.region-summary-card {
  display: grid;
  gap: 3px;
  padding: 8px 10px;
  border: 1px solid rgba(0, 229, 255, 0.14);
  background:
    linear-gradient(155deg, rgba(255, 255, 255, 0.03) 0%, transparent 40%),
    linear-gradient(180deg, rgba(8, 29, 54, 0.68), rgba(5, 18, 34, 0.86));
  backdrop-filter: blur(8px);
  transition: transform 0.2s ease, box-shadow 0.2s ease, border-color 0.2s ease;
}

.region-summary-card:hover {
  transform: translateY(-1px);
  border-color: rgba(0, 229, 255, 0.28);
  box-shadow: 0 0 16px rgba(0, 229, 255, 0.12);
}

.region-summary-card--danger {
  border-color: rgba(255, 77, 79, 0.28);
  background:
    linear-gradient(155deg, rgba(255, 77, 79, 0.1) 0%, transparent 42%),
    linear-gradient(180deg, rgba(36, 10, 14, 0.72), rgba(16, 6, 10, 0.88));
}

.region-summary-card--danger strong {
  color: var(--risk-red);
  font-size: clamp(26px, 2vw, 32px);
  animation: cockpit-risk-pulse 2.8s ease-in-out infinite;
}

.region-summary-card--warning {
  border-color: rgba(255, 204, 0, 0.26);
  background:
    linear-gradient(155deg, rgba(255, 204, 0, 0.08) 0%, transparent 42%),
    linear-gradient(180deg, rgba(32, 24, 8, 0.72), rgba(14, 10, 4, 0.88));
}

.region-summary-card--warning strong {
  color: var(--risk-yellow);
  text-shadow: 0 0 14px var(--risk-yellow-glow);
}

.region-summary-card--green strong {
  color: var(--risk-green);
  text-shadow: 0 0 12px var(--risk-green-glow);
}

.region-summary-card span {
  color: rgba(204, 226, 232, 0.72);
  font-size: 11px;
}

.region-summary-card strong {
  color: #f0fdff;
  font-size: 20px;
  font-weight: 800;
  text-shadow: 0 0 14px rgba(0, 229, 255, 0.24);
}

.region-summary-card em {
  color: rgba(0, 255, 200, 0.76);
  font-size: 10px;
  font-style: normal;
}

.cockpit-screen :deep(.el-alert) {
  border: 1px solid rgba(0, 229, 255, 0.14);
  background: rgba(6, 22, 44, 0.76);
  backdrop-filter: blur(8px);
}

.cockpit-screen :deep(.el-alert__title),
.cockpit-screen :deep(.el-alert__description),
.cockpit-screen :deep(.el-input__inner),
.cockpit-screen :deep(.el-select__placeholder) {
  color: #ebfaff;
}

.cockpit-screen :deep(.el-button.is-circle) {
  width: 34px;
  height: 34px;
  padding: 0;
}

.layout-side::-webkit-scrollbar,
.metric-grid::-webkit-scrollbar {
  width: 4px;
}

.layout-side::-webkit-scrollbar-thumb,
.metric-grid::-webkit-scrollbar-thumb {
  background: rgba(0, 229, 255, 0.32);
}

@keyframes panel-breathe {
  0%,
  100% {
    box-shadow: 0 0 14px rgba(0, 229, 255, 0.16);
  }
  50% {
    box-shadow: 0 0 20px rgba(0, 229, 255, 0.22);
  }
}

.screen-panel--layer-map {
  animation: cockpit-panel-breathe 4.5s ease-in-out infinite;
}

@media (max-width: 1680px) {
  .cockpit-screen {
    --cockpit-col-left: clamp(270px, 18%, 320px);
    --cockpit-col-right: clamp(255px, 16%, 300px);
  }
}

@media (max-width: 1440px) {
  .screen-header {
    grid-template-columns: minmax(0, 1fr);
    grid-template-areas:
      "title"
      "summary"
      "tools";
    min-height: auto;
  }

  .screen-header__summary,
  .screen-header__tools {
    justify-self: stretch;
  }

  .screen-header__summary {
    justify-items: center;
  }

  .screen-header__tools {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .screen-layout {
    grid-template-columns: 1fr;
    overflow: auto;
  }

  .layout-side--left,
  .layout-side--right,
  .layout-center {
    grid-template-rows: auto;
  }

  .screen-panel--map {
    min-height: 420px;
  }
}
</style>

<style lang="scss">
body.cockpit-immersive {
  .main-container > .navbar,
  .main-container > .tags-view-container,
  .sidebar-container {
    display: none !important;
  }

  .main-container > .app-main {
    margin-left: 0 !important;
    padding: 0 !important;
  }
}

.cockpit-config-dialog :deep(.el-overlay) {
  backdrop-filter: blur(8px);
  background: rgba(0, 0, 0, 0.48);
}
</style>




