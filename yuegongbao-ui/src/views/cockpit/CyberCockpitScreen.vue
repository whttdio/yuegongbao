<template>
  <div class="cockpit-screen" :class="{ 'is-immersive': isImmersive }" v-loading="loading">
    <div class="cockpit-screen__grid" />
    <div class="cockpit-screen__shell">
      <header class="screen-header">
        <div class="screen-header__brand">
          <div class="screen-header__eyebrow">{{ pageConfig.kicker }}</div>
          <h1>{{ pageConfig.title }}</h1>
          <div class="screen-header__chips">
            <span>{{ regionLabel }}</span>
            <span>{{ roleLabel }}</span>
            <span>{{ currentTimeText }}</span>
          </div>
        </div>

        <cockpit-hero-counter
          class="screen-header__hero"
          :label="heroMetric.label"
          :value="heroMetric.value"
          :unit="heroMetric.unit"
          :meta-items="heroMetaItems"
        />

        <div class="screen-header__tools">
          <el-select v-model="queryParams.regionCode" class="header-control" @change="handleRegionChange">
            <el-option v-for="item in regionOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
          <el-date-picker
            v-model="queryParams.statMonth"
            class="header-control header-control--month"
            type="month"
            value-format="YYYY-MM"
            format="YYYY-MM"
            @change="refreshDashboard"
          />
          <el-select v-model="queryParams.days" class="header-control header-control--short" @change="refreshDashboard">
            <el-option v-for="item in dayOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
          <el-button type="primary" :icon="Refresh" circle @click="refreshDashboard" title="刷新" />
          <el-button plain :icon="Download" circle @click="handleExport" v-hasPermi="['ygb:cockpit:export']" title="导出" />
          <el-button plain :icon="Setting" circle @click="openConfigDialog" title="驾驶舱配置" />
          <el-button plain circle @click="toggleImmersive" :title="isImmersive ? '退出全屏' : '全屏'">
            {{ isImmersive ? "退出" : "全屏" }}
          </el-button>
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

      <section class="screen-layout">
        <aside class="layout-side layout-side--left">
          <article v-if="isPanelVisible('panel-metrics')" class="screen-panel">
            <div class="panel-header">
              <div>
                <span class="panel-header__tag">指标总览</span>
                <h2>{{ pageConfig.metricTitle }}</h2>
              </div>
              <span class="panel-header__badge">{{ visibleMetricCards.length }} 项</span>
            </div>
            <div class="metric-grid">
              <button
                v-for="item in visibleMetricCards"
                :key="item.key"
                type="button"
                class="metric-card"
                :class="`metric-card--${item.tone}`"
                @click="item.action && openModule(item.action)"
              >
                <div class="metric-card__head">
                  <span>{{ item.label }}</span>
                  <em>{{ item.unit }}</em>
                </div>
                <strong>{{ item.value }}</strong>
                <div class="metric-card__foot">
                  <span :class="['metric-card__delta', item.deltaTone]">{{ item.deltaText }}</span>
                  <span>{{ item.shareText }}</span>
                </div>
                <div class="metric-card__spark">
                  <i v-for="(bar, index) in item.sparkValues" :key="`${item.key}-${index}`" :style="{ height: `${bar}%` }" />
                </div>
              </button>
            </div>
          </article>

          <article v-if="isPanelVisible('panel-risk-ranking')" class="screen-panel">
            <div class="panel-header">
              <div>
                <span class="panel-header__tag">风险分层</span>
                <h2>红黄绿码企业分类与风险排名</h2>
              </div>
              <span class="panel-header__badge">TOP 排行</span>
            </div>
            <cockpit-risk-ranking-panel
              ref="riskRankingRef"
              :credit-summary="dashboardData.creditScoreSummary || {}"
              :ranking-list="creditRankingList"
              :colors="COLORS"
              @open-module="openModule"
            />
          </article>

          <article v-if="isPanelVisible('panel-trend')" class="screen-panel panel-grow">
            <div class="panel-header">
              <div>
                <span class="panel-header__tag">趋势分析</span>
                <h2>{{ pageConfig.trendTitle }}</h2>
              </div>
              <span class="panel-header__badge">5 类业务</span>
            </div>
            <div ref="trendChartRef" class="chart-box chart-box--trend" />
          </article>
        </aside>

        <main class="layout-center">
          <article v-if="isPanelVisible('panel-map')" class="screen-panel screen-panel--map">
            <div class="panel-header panel-header--map">
              <div>
                <span class="panel-header__tag">地图可视化</span>
                <h2>{{ pageConfig.mapTitle }}</h2>
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
            <div class="map-wrap">
              <div ref="mapChartRef" class="map-wrap__chart" />
              <div class="map-wrap__legend">
                <div v-for="item in mapLegendItems" :key="item.label" class="legend-item">
                  <i :style="{ background: item.color }" />
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

          <article v-if="isPanelVisible('panel-table')" class="screen-panel screen-panel--table">
            <div class="panel-header">
              <div>
                <span class="panel-header__tag">点位明细</span>
                <h2>地图可视范围内企业点位明细</h2>
              </div>
            </div>
            <cockpit-enterprise-table :rows="visibleEnterpriseRows" @row-click="handleEnterpriseRowClick" />
          </article>
        </main>

        <aside class="layout-side layout-side--right">
          <article v-if="isPanelVisible('panel-region-summary')" class="screen-panel">
            <div class="panel-header">
              <div>
                <span class="panel-header__tag">区域态势</span>
                <h2>{{ pageConfig.regionSummaryTitle }}</h2>
              </div>
            </div>
            <div class="region-summary-grid">
              <div v-for="item in regionSummaryItems" :key="item.label" class="region-summary-card">
                <span>{{ item.label }}</span>
                <strong>{{ item.value }}</strong>
                <em>{{ item.note }}</em>
              </div>
            </div>
          </article>

          <article v-if="isPanelVisible('panel-warning')" class="screen-panel panel-grow">
            <div class="panel-header">
              <div>
                <span class="panel-header__tag">实时预警</span>
                <h2>{{ pageConfig.warningTitle }}</h2>
              </div>
              <span class="panel-header__badge">{{ warningItems.length }} 条</span>
            </div>
            <cockpit-warning-stream :items="warningItems" @open-module="openModule" />
          </article>
        </aside>
      </section>
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
import { normalizeRouteTarget } from "@/utils/routeAlias"
import { useRoleViewMode } from "@/utils/roleView"
import CockpitConfigDialog from "@/views/cockpit/components/CockpitConfigDialog.vue"
import CockpitEnterpriseTable from "@/views/cockpit/components/CockpitEnterpriseTable.vue"
import CockpitHeroCounter from "@/views/cockpit/components/CockpitHeroCounter.vue"
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

const MAP_NAME = "guangdong-cockpit-map"
if (!echarts.getMap(MAP_NAME)) {
  echarts.registerMap(MAP_NAME, JSON.parse(guangdongGeoJsonRaw))
}

const COLORS = Object.freeze({
  cyan: "#52e6ff",
  blue: "#3d7bff",
  green: "#3df2b2",
  lime: "#99ff70",
  amber: "#ffbe62",
  danger: "#ff6f91",
  violet: "#7c8dff",
  text: "#ebf8ff",
  muted: "rgba(177, 214, 237, 0.72)",
  border: "rgba(82, 230, 255, 0.18)",
  split: "rgba(72, 173, 227, 0.12)",
  tooltip: "rgba(4, 12, 22, 0.96)",
})

const TREND_SERIES_FIVE = Object.freeze([
  { name: "工伤事故趋势", type: "line", dataKey: "overdueInjuryCount", yAxisIndex: 0 },
  { name: "预警数量趋势", type: "bar", dataKey: "todayWarningCount", yAxisIndex: 0, barMaxWidth: 14 },
  { name: "参保扩面进度", type: "line", dataKey: "expandCompletionRate", yAxisIndex: 1 },
  { name: "设备改造进度", type: "line", dataKey: "onlineDeviceCount", yAxisIndex: 0 },
  { name: "职业病发病趋势", type: "line", dataKey: "newInjuryRate", yAxisIndex: 1 },
])

const PAGE_CONFIG = Object.freeze({
  ygb: {
    kicker: "粤工保 · 领导驾驶舱",
    title: "广东省用工保障综合驾驶舱",
    metricTitle: "核心监管指标",
    trendTitle: "工伤 / 预警 / 扩面 / 设备 / 职业病趋势",
    mapTitle: "企业风险热力与空间态势图",
    regionSummaryTitle: "区域监管态势摘要",
    warningTitle: "滚动预警台账",
    description: "聚焦企业、人员、参保、预警和扩面治理联动。",
    fetchDashboard: getYgbCockpitDashboard,
    exportPrefix: "ygb_cockpit",
    warningPath: "/warning-center/workOrder",
    trendColors: [COLORS.danger, COLORS.cyan, COLORS.lime, COLORS.blue, COLORS.amber],
    trendSeries: TREND_SERIES_FIVE,
  },
  azb: {
    kicker: "安责保 · 领导驾驶舱",
    title: "广东省安全治理综合驾驶舱",
    metricTitle: "核心治理指标",
    trendTitle: "工伤 / 预警 / 扩面 / 设备 / 职业病趋势",
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
  isPanelVisible,
  isMetricVisible,
} = useCockpitConfig(computed(() => props.mode))

const activeMapLayers = ref(["risk", "points", "fence", "route", "station"])
const isImmersive = ref(false)
const currentTimeText = ref("")
const warningRecords = ref([])
const creditRankingList = ref([])
const riskRankingRef = ref(null)
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
  const base = props.mode === "azb"
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

const metricCardOptions = computed(() => metricCards.value.map(item => ({ key: item.key, label: item.label })))
const visibleMetricCards = computed(() => metricCards.value.filter(item => isMetricVisible(item.key)))

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
    label: "派遣员工总数",
    value: formatNumber(indicators.value.dispatchedWorkerCount),
    unit: "人",
  }
})

const heroMetaItems = computed(() => {
  if (props.mode === "azb") {
    return [
      { label: "安责险覆盖", value: formatPercent(indicators.value.aqInsuranceRate) },
      { label: "待处置预警", value: `${formatNumber(indicators.value.pendingWarningCount)} 条` },
      { label: "在线设备", value: `${formatNumber(indicators.value.onlineDeviceCount)} 台` },
    ]
  }
  return [
    { label: "工伤参保率", value: formatPercent(indicators.value.insuranceRate) },
    { label: "高危企业", value: `${formatNumber(indicators.value.highRiskEnterpriseCount)} 家` },
    { label: "当日预警", value: `${formatNumber(indicators.value.todayWarningCount)} 条` },
  ]
})

const regionSummaryItems = computed(() => {
  if (props.mode === "azb") {
    return [
      { label: "安责险覆盖率", value: formatPercent(indicators.value.aqInsuranceRate), note: "承保协同" },
      { label: "待处置预警", value: `${formatNumber(indicators.value.pendingWarningCount)} 条`, note: "实时处置" },
      { label: "在线设备", value: `${formatNumber(indicators.value.onlineDeviceCount)} 台`, note: "设备在线" },
      { label: "红码企业", value: `${formatNumber(dashboardData.value.creditScoreSummary?.redCount)} 家`, note: "重点复核" },
    ]
  }
  return [
    { label: "工伤参保率", value: formatPercent(indicators.value.insuranceRate), note: "参保水平" },
    { label: "高危企业", value: `${formatNumber(indicators.value.highRiskEnterpriseCount)} 家`, note: "风险聚焦" },
    { label: "扩面完成率", value: formatPercent(indicators.value.expandCompletionRate), note: "扩面进度" },
    { label: "企业点位", value: `${formatNumber(mapStats.value[0]?.value)} 个`, note: "地图覆盖" },
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
  { label: "高风险热力", color: "linear-gradient(90deg, #1ec9b5, #ff6f91)" },
  { label: "企业 / 设备点位", color: COLORS.cyan },
  { label: "电子围栏", color: COLORS.amber },
  { label: "人员轨迹", color: COLORS.lime },
  { label: "服务站点", color: COLORS.violet },
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
    if (type === "ENTERPRISE") bucket.enterprise += 1
    if (type === "DEVICE") bucket.device += 1
    if (String(item.properties?.featureStatus || "") === "2") bucket.warning += 1
    bucket.score += type === "ENTERPRISE" ? 9 : 6
    if (String(item.properties?.featureStatus || "") === "2") bucket.score += 10
  })

  const geo = JSON.parse(guangdongGeoJsonRaw)
  return geo.features.map((item, index) => {
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
  return [{
    key: "empty",
    level: "蓝色提示",
    location: regionLabel.value,
    summary: "当前暂无新增预警，系统保持持续监测。",
    status: "持续监测",
    time: queryParams.value.statMonth || "",
    tone: "normal",
    action: { path: pageConfig.value.warningPath },
  }]
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
    tooltip: {
      trigger: "axis",
      backgroundColor: COLORS.tooltip,
      borderColor: COLORS.border,
      textStyle: { color: COLORS.text },
    },
    legend: { top: 0, textStyle: { color: COLORS.muted }, type: "scroll" },
    grid: { left: 42, right: 24, top: 56, bottom: 24 },
    xAxis: {
      type: "category",
      data: trendList.map((item) => formatTrendDate(item.statDate)),
      axisLine: { lineStyle: { color: COLORS.border } },
      axisLabel: { color: COLORS.muted },
    },
    yAxis: [
      {
        type: "value",
        name: "数量",
        minInterval: 1,
        axisLabel: { color: COLORS.muted },
        splitLine: { lineStyle: { color: COLORS.split } },
      },
      {
        type: "value",
        name: "比率",
        axisLabel: { formatter: "{value}%", color: COLORS.muted },
        splitLine: { show: false },
      },
    ],
    series: trendSeries.map((item, index) => {
      if (item.type === "bar") {
        return {
          name: item.name,
          type: "bar",
          yAxisIndex: item.yAxisIndex || 0,
          barMaxWidth: item.barMaxWidth || 14,
          data: trendList.map((row) => numeric(row[item.dataKey])),
          itemStyle: {
            borderRadius: [10, 10, 2, 2],
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: pageConfig.value.trendColors[index] },
              { offset: 1, color: `${pageConfig.value.trendColors[index]}22` },
            ]),
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
        symbolSize: 5,
        data: trendList.map((row) => numeric(row[item.dataKey])),
        lineStyle: { width: 2, color },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: `${color}55` },
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
    tooltip: {
      trigger: "item",
      confine: true,
      backgroundColor: COLORS.tooltip,
      borderColor: COLORS.border,
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
          `风险码：${raw.colorCode || raw.riskLevel || "--"}`,
          `参保率：${raw.insuranceRate != null ? `${raw.insuranceRate}%` : "--"}`,
          `区域：${safeRegionName(raw.regionCode) || regionLabel.value}`,
        ].join("<br/>")
      },
    },
    visualMap: showRisk ? {
      min: 0,
      max: Math.max(...cityRiskItems.value.map((item) => item.riskScore), 20),
      show: false,
      inRange: { color: ["#123d55", "#167c9d", "#1fd0bc", "#99ff70", "#ffbe62", "#ff6f91"] },
    } : undefined,
    geo: {
      map: MAP_NAME,
      roam: true,
      center,
      zoom,
      layoutCenter: ["50%", "50%"],
      layoutSize: "105%",
      label: { show: true, color: "rgba(212, 239, 250, 0.65)", fontSize: 9 },
      itemStyle: {
        areaColor: {
          type: "radial",
          x: 0.5,
          y: 0.5,
          r: 0.85,
          colorStops: [
            { offset: 0, color: "rgba(25, 72, 98, 0.95)" },
            { offset: 1, color: "rgba(12, 38, 58, 0.88)" },
          ],
        },
        borderColor: "rgba(116, 228, 255, 0.55)",
        borderWidth: 1.2,
        shadowBlur: 24,
        shadowColor: "rgba(16, 111, 158, 0.35)",
      },
      emphasis: {
        label: { color: "#ebf8ff", fontSize: 11 },
        itemStyle: {
          areaColor: "rgba(37, 118, 160, 0.85)",
          borderColor: "#52e6ff",
          borderWidth: 1.5,
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
      showPoints ? {
        name: "企业点位",
        type: "scatter",
        coordinateSystem: "geo",
        symbolSize: 12,
        itemStyle: { color: COLORS.cyan, shadowBlur: 16, shadowColor: `${COLORS.cyan}66` },
        data: enterprisePoints.map((item) => ({
          name: item.properties?.featureName || "企业点位",
          value: item.geometry.coordinates || [0, 0],
          raw: item.properties || {},
        })),
      } : null,
      showPoints ? {
        name: "设备点位",
        type: "scatter",
        coordinateSystem: "geo",
        symbolSize: 10,
        itemStyle: { color: COLORS.green, shadowBlur: 16, shadowColor: `${COLORS.green}66` },
        data: devicePoints.map((item) => ({
          name: item.properties?.featureName || "设备点位",
          value: item.geometry.coordinates || [0, 0],
          raw: item.properties || {},
        })),
      } : null,
      showStation ? {
        name: "服务站点",
        type: "effectScatter",
        coordinateSystem: "geo",
        symbolSize: 14,
        rippleEffect: { scale: 3 },
        itemStyle: { color: COLORS.violet, shadowBlur: 18, shadowColor: `${COLORS.violet}66` },
        data: stationPoints.map((item) => ({
          name: item.properties?.featureName || "服务站点",
          value: item.geometry.coordinates || [0, 0],
          raw: item.properties || {},
        })),
      } : null,
      showFence && polygons.length ? {
        name: "电子围栏",
        type: "lines",
        coordinateSystem: "geo",
        polyline: true,
        data: polygons.map((item) => ({
          name: item.properties?.featureName || "电子围栏",
          coords: item.geometry.coordinates?.[0] || [],
        })),
        lineStyle: { color: COLORS.amber, width: 1.4, opacity: 0.65, type: "dashed" },
      } : null,
      showRoute && routeLines.length ? {
        name: "人员轨迹",
        type: "lines",
        coordinateSystem: "geo",
        data: routeLines,
        lineStyle: { color: COLORS.lime, width: 1.2, opacity: 0.45, curveness: 0.24 },
        effect: { show: true, period: 4, trailLength: 0.3, symbolSize: 4, color: COLORS.lime },
      } : null,
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
    creditRankingList.value = dashboardData.value.creditRanking?.length
      ? dashboardData.value.creditRanking
      : remoteRows
  } else {
    creditRankingList.value = dashboardData.value.creditRanking || []
  }
  nextTick(() => riskRankingRef.value?.resize?.())
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

watch(() => [activeMapLayers.value.join("|"), mapConfig.value.center?.join(","), mapConfig.value.zoom], () => {
  nextTick(() => {
    loadDashboard().then(() => updateMapViewBoundsFromChart())
  })
})

watchEffect(() => {
  setPageGuide({
    title: pageConfig.value.title,
    description: pageDescription.value,
    selection: [
      { label: "当前区域", value: regionLabel.value },
      { label: "统计月份", value: queryParams.value.statMonth || "--" },
      { label: "角色视角", value: roleLabel.value },
      { label: "核心规模", value: `${heroMetric.value.value}${heroMetric.value.unit}` },
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
})

onActivated(async () => {
  await loadDashboard()
  await loadAuxiliaryData()
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
  --cockpit-cyan: #52e6ff;
  --cockpit-glow: rgba(82, 230, 255, 0.35);
  position: relative;
  height: calc(100vh - var(--layout-header-height, 50px) - var(--layout-tags-height, 34px));
  margin: 0;
  padding: 10px 12px 12px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  color: #ebf8ff;
  background:
    radial-gradient(ellipse 80% 50% at 50% -10%, rgba(32, 194, 255, 0.22), transparent 55%),
    radial-gradient(circle at 90% 20%, rgba(61, 242, 178, 0.08), transparent 35%),
    linear-gradient(180deg, #020810 0%, #061018 45%, #020810 100%);
}

.cockpit-screen.is-immersive {
  position: fixed;
  inset: 0;
  z-index: 2000;
  height: 100vh;
  margin: 0;
  padding: 12px;
}

.cockpit-screen__grid {
  position: absolute;
  inset: 0;
  pointer-events: none;
  opacity: 0.45;
  background:
    linear-gradient(rgba(82, 171, 222, 0.04) 1px, transparent 1px),
    linear-gradient(90deg, rgba(82, 171, 222, 0.04) 1px, transparent 1px);
  background-size: 40px 40px;
  mask-image: radial-gradient(circle at 50% 40%, #000 40%, transparent 85%);
}

.cockpit-screen__shell {
  position: relative;
  z-index: 1;
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.screen-header {
  display: grid;
  grid-template-columns: minmax(0, 1fr) minmax(280px, 1.15fr) auto;
  gap: 12px;
  align-items: center;
  flex-shrink: 0;
  padding: 10px 14px;
  border: 1px solid rgba(82, 230, 255, 0.2);
  background:
    linear-gradient(180deg, rgba(8, 22, 38, 0.95), rgba(4, 12, 22, 0.92)),
    radial-gradient(circle at 50% 0%, rgba(82, 230, 255, 0.1), transparent 60%);
  box-shadow: 0 0 24px rgba(12, 121, 170, 0.12), inset 0 1px 0 rgba(255, 255, 255, 0.04);
}

.screen-header__brand {
  padding: 4px 2px;
}

.screen-header__brand h1 {
  margin: 4px 0 6px;
  font-size: 20px;
  font-weight: 700;
  letter-spacing: 0.02em;
  text-shadow: 0 0 20px rgba(82, 230, 255, 0.15);
}

.screen-header__chips {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  color: rgba(177, 214, 237, 0.65);
  font-size: 11px;
}

.screen-header__chips span {
  padding: 2px 8px;
  border: 1px solid rgba(82, 230, 255, 0.12);
  background: rgba(82, 230, 255, 0.04);
}

.screen-header__tools {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: flex-end;
  gap: 8px;
}

.screen-header__hero {
  justify-self: center;
}

.screen-header__brand,
.screen-panel {
  position: relative;
  border: 1px solid rgba(82, 230, 255, 0.16);
  background:
    linear-gradient(180deg, rgba(6, 17, 31, 0.92), rgba(4, 11, 22, 0.9)),
    radial-gradient(circle at top, rgba(82, 230, 255, 0.06), transparent 42%);
  box-shadow:
    inset 0 0 0 1px rgba(255, 255, 255, 0.02),
    0 0 18px rgba(12, 121, 170, 0.1);
}

.screen-panel::before {
  content: "";
  position: absolute;
  inset: 0;
  border: 1px solid rgba(82, 230, 255, 0.06);
  clip-path: polygon(0 12px, 12px 0, calc(100% - 12px) 0, 100% 12px, 100% calc(100% - 12px), calc(100% - 12px) 100%, 12px 100%, 0 calc(100% - 12px));
  pointer-events: none;
}

.screen-header__eyebrow,
.panel-header__tag {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  color: #7de8ff;
  font-size: 11px;
  letter-spacing: 0.12em;
  text-transform: uppercase;
}

.panel-header__tag::before {
  content: "▶";
  font-size: 9px;
  color: var(--cockpit-cyan);
  text-shadow: 0 0 8px var(--cockpit-glow);
}

.header-control {
  width: 118px;
}

.header-control--month {
  width: 108px;
}

.header-control--short {
  width: 96px;
}

.readonly-banner {
  flex-shrink: 0;
  margin: 0;
}

.screen-layout {
  flex: 1;
  min-height: 0;
  display: grid;
  grid-template-columns: minmax(280px, 0.82fr) minmax(0, 1.45fr) minmax(260px, 0.78fr);
  gap: 10px;
}

.layout-side,
.layout-center {
  display: grid;
  gap: 10px;
  min-height: 0;
  overflow-y: auto;
  overflow-x: hidden;
  padding-right: 2px;
}

.layout-center {
  grid-template-rows: minmax(0, 1fr) minmax(168px, auto);
  overflow: hidden;
}

.layout-side--left {
  grid-template-rows: auto auto minmax(200px, 1fr);
}

.layout-side--right {
  grid-template-rows: auto minmax(220px, 1fr);
}

.panel-grow {
  min-height: 0;
  display: flex;
  flex-direction: column;
}

.screen-panel {
  padding: 12px 14px;
  min-height: 0;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
  flex-shrink: 0;
}

.panel-header h2 {
  margin: 4px 0 0;
  font-size: 14px;
  font-weight: 600;
  line-height: 1.3;
  color: rgba(235, 248, 255, 0.92);
}

.panel-header__badge {
  padding: 3px 8px;
  color: rgba(177, 214, 237, 0.76);
  font-size: 11px;
  border: 1px solid rgba(82, 230, 255, 0.14);
  background: rgba(82, 230, 255, 0.06);
  white-space: nowrap;
}

.panel-header--map {
  align-items: flex-start;
}

.metric-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 8px;
  max-height: 300px;
  overflow-y: auto;
}

.metric-card {
  display: grid;
  gap: 6px;
  padding: 10px 12px;
  color: inherit;
  text-align: left;
  border: 1px solid rgba(82, 230, 255, 0.1);
  background: linear-gradient(135deg, rgba(17, 42, 68, 0.5), rgba(7, 18, 32, 0.88));
  cursor: pointer;
  transition: border-color 0.18s ease, box-shadow 0.18s ease;
}

.metric-card:hover {
  border-color: rgba(82, 230, 255, 0.32);
  box-shadow: 0 0 16px rgba(82, 230, 255, 0.08);
}

.metric-card__head span {
  color: rgba(192, 226, 244, 0.78);
  font-size: 12px;
}

.metric-card__head em {
  color: rgba(123, 229, 255, 0.72);
  font-size: 11px;
  font-style: normal;
}

.metric-card strong {
  font-size: 22px;
  line-height: 1;
  text-shadow: 0 0 12px rgba(82, 230, 255, 0.12);
}

.metric-card__foot {
  color: rgba(177, 214, 237, 0.65);
  font-size: 11px;
}

.metric-card__delta.up { color: #94ffbb; }
.metric-card__delta.down { color: #ff8ea3; }

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
  border-radius: 999px 999px 2px 2px;
  background: linear-gradient(180deg, rgba(82, 230, 255, 0.9), rgba(82, 230, 255, 0.1));
}

.metric-card--green .metric-card__spark i { background: linear-gradient(180deg, rgba(61, 242, 178, 0.9), rgba(61, 242, 178, 0.1)); }
.metric-card--danger .metric-card__spark i { background: linear-gradient(180deg, rgba(255, 111, 145, 0.9), rgba(255, 111, 145, 0.1)); }
.metric-card--amber .metric-card__spark i { background: linear-gradient(180deg, rgba(255, 190, 98, 0.9), rgba(255, 190, 98, 0.1)); }
.metric-card--lime .metric-card__spark i { background: linear-gradient(180deg, rgba(153, 255, 112, 0.9), rgba(153, 255, 112, 0.1)); }
.metric-card--violet .metric-card__spark i { background: linear-gradient(180deg, rgba(124, 141, 255, 0.9), rgba(124, 141, 255, 0.1)); }

.chart-box--trend {
  flex: 1;
  min-height: 200px;
  height: 220px;
}

.screen-panel--map {
  display: flex;
  flex-direction: column;
  min-height: 0;
  overflow: hidden;
}

.screen-panel--table {
  flex-shrink: 0;
  max-height: 240px;
  overflow: hidden;
}

.map-wrap {
  position: relative;
  flex: 1;
  min-height: 0;
}

.map-wrap__chart {
  height: 100%;
  min-height: 180px;
}

.map-wrap__legend,
.map-wrap__stats {
  position: absolute;
  display: grid;
  gap: 6px;
  z-index: 2;
}

.map-wrap__legend {
  left: 10px;
  bottom: 10px;
}

.legend-item {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 4px 8px;
  border: 1px solid rgba(82, 230, 255, 0.12);
  background: rgba(4, 11, 22, 0.82);
  backdrop-filter: blur(4px);
  color: rgba(192, 226, 244, 0.82);
  font-size: 11px;
}

.legend-item i {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  display: block;
}

.map-wrap__stats {
  right: 10px;
  bottom: 10px;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  width: 220px;
}

.map-stat {
  display: grid;
  gap: 2px;
  padding: 8px 10px;
  border: 1px solid rgba(82, 230, 255, 0.12);
  background: rgba(4, 11, 22, 0.82);
  backdrop-filter: blur(4px);
}

.map-stat span {
  color: rgba(177, 214, 237, 0.65);
  font-size: 11px;
}

.map-stat strong {
  font-size: 16px;
}

.layer-switches {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-end;
  gap: 6px;
}

.layer-switch {
  padding: 4px 10px;
  border: 1px solid rgba(82, 230, 255, 0.14);
  background: rgba(82, 230, 255, 0.04);
  color: rgba(177, 214, 237, 0.74);
  font-size: 11px;
  cursor: pointer;
}

.layer-switch.is-active,
.layer-switch:hover {
  color: #03111f;
  border-color: transparent;
  background: linear-gradient(90deg, rgba(82, 230, 255, 0.92), rgba(153, 255, 112, 0.88));
  box-shadow: 0 0 12px rgba(82, 230, 255, 0.25);
}

.region-summary-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 8px;
}

.region-summary-card {
  display: grid;
  gap: 4px;
  padding: 12px;
  border: 1px solid rgba(82, 230, 255, 0.1);
  background: linear-gradient(180deg, rgba(17, 42, 68, 0.45), rgba(7, 18, 32, 0.88));
}

.region-summary-card span {
  color: rgba(177, 214, 237, 0.68);
  font-size: 11px;
}

.region-summary-card strong {
  font-size: 20px;
  text-shadow: 0 0 10px rgba(82, 230, 255, 0.1);
}

.region-summary-card em {
  color: rgba(123, 229, 255, 0.65);
  font-size: 10px;
  font-style: normal;
}

.cockpit-screen :deep(.el-input__wrapper),
.cockpit-screen :deep(.el-select__wrapper),
.cockpit-screen :deep(.el-date-editor.el-input__wrapper) {
  background: rgba(6, 17, 31, 0.88);
  box-shadow: inset 0 0 0 1px rgba(82, 230, 255, 0.16);
}

.cockpit-screen :deep(.el-input__inner),
.cockpit-screen :deep(.el-select__placeholder),
.cockpit-screen :deep(.el-alert__title),
.cockpit-screen :deep(.el-alert__description) {
  color: #ebf8ff;
}

.cockpit-screen :deep(.el-button) {
  border-radius: 4px;
  border-color: rgba(82, 230, 255, 0.16);
  background: rgba(82, 230, 255, 0.05);
  color: #ebf8ff;
}

.cockpit-screen :deep(.el-button--primary) {
  border-color: transparent;
  background: linear-gradient(90deg, rgba(61, 123, 255, 0.92), rgba(82, 230, 255, 0.92));
}

.cockpit-screen :deep(.el-button.is-circle) {
  width: 32px;
  height: 32px;
  padding: 0;
}

.layout-side::-webkit-scrollbar,
.layout-center::-webkit-scrollbar,
.metric-grid::-webkit-scrollbar {
  width: 4px;
}

.layout-side::-webkit-scrollbar-thumb,
.layout-center::-webkit-scrollbar-thumb,
.metric-grid::-webkit-scrollbar-thumb {
  background: rgba(82, 230, 255, 0.2);
  border-radius: 4px;
}

@media (max-width: 1400px) {
  .screen-header {
    grid-template-columns: 1fr;
    gap: 10px;
  }

  .screen-header__hero {
    justify-self: stretch;
  }

  .screen-layout {
    grid-template-columns: 1fr;
    overflow-y: auto;
  }

  .layout-center {
    grid-template-rows: 360px auto;
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
</style>
