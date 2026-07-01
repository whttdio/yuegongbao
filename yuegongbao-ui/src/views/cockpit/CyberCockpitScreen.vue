<template>
  <div
    class="cockpit-screen"
    :class="{
      'cockpit-screen--standalone': isStandalone,
      'is-immersive': isImmersive,
      'is-loading': loading,
    }"
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
      <header class="cockpit-command">
        <section class="cockpit-command__hero screen-panel screen-panel--layer-primary">
          <div class="cockpit-command__headline">
            <div class="cockpit-command__status">
              <span class="cockpit-command__status-pill">
                <i class="cockpit-command__status-dot" />
                系统运行正常
              </span>
              <span>{{ regionLabel }}</span>
              <span>{{ roleLabel }}</span>
              <strong>{{ currentTimeText }}</strong>
            </div>
            <div class="cockpit-command__title-wrap">
              <h1 class="cockpit-command__title">{{ pageConfig.title }}</h1>
            </div>
          </div>
        </section>
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

      <section
        v-if="isPanelVisible('panel-metrics')"
        class="cockpit-metrics-row"
        :style="{ '--metric-column-count': visibleMetricCards.length }"
      >
        <cockpit-metric-card
          v-for="item in visibleMetricCards"
          :key="item.key"
          :label="item.label"
          :value="item.value"
          :unit="item.unit"
          :tone="item.tone"
          :delta-tone="item.deltaTone"
          :delta-text="item.deltaText"
          :share-text="item.shareText"
          :spark-values="item.sparkValues"
          :hero="isRiskHeroKey(item.key)"
          compact
          @click="item.action && openModule(item.action)"
        />
      </section>

      <section class="cockpit-stage">
        <aside class="cockpit-stage__side cockpit-stage__side--left">
          <article v-if="isPanelVisible('panel-warning')" class="screen-panel screen-panel--layer-primary">
            <cockpit-section-header
              title="实时预警流"
              subtitle="级别 / 位置 / 摘要 / 处置状态"
              extra="滚动监测"
              dot-tone="danger"
            />
            <cockpit-warning-stream :items="warningItems" @open-module="openModule" />
          </article>

          <article v-if="isPanelVisible('panel-region-summary')" class="screen-panel screen-panel--layer-primary">
            <cockpit-section-header
              :title="pageConfig.regionSummaryTitle"
              subtitle="参保、预警、点位、扩面四类监测摘要"
              extra="区域态势"
              dot-tone="green"
            />
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
          </article>
        </aside>

        <main class="cockpit-stage__center">
          <article v-if="isPanelVisible('panel-map')" class="screen-panel screen-panel--map screen-panel--layer-map map-panel">
            <cockpit-section-header
              title="地图可视化"
              :subtitle="pageConfig.mapTitle"
              eyebrow="空间监测"
              dot-tone="cyan"
            >
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
            </cockpit-section-header>
            <div class="map-wrap">
              <div ref="mapChartRef" class="map-wrap__chart" />
              <div class="map-wrap__legend map-wrap__legend--points">
                <div v-for="item in mapPointLegendItems" :key="item.label" class="cockpit-chart-legend__item legend-item">
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
        </main>

        <aside class="cockpit-stage__side cockpit-stage__side--right">
          <article v-if="isPanelVisible('panel-risk-ranking')" class="screen-panel screen-panel--layer-primary">
            <cockpit-section-header
              title="红黄绿码企业分类"
              subtitle="企业风险排名与风险因子概览"
              extra="TOP 风险企业"
              dot-tone="warning"
            />
            <cockpit-risk-ranking-panel
              ref="riskRankingRef"
              layout="table"
              :credit-summary="dashboardData.creditScoreSummary || {}"
              :ranking-list="creditRankingList"
              :colors="COLORS"
              @open-module="openModule"
            />
          </article>

          <article v-if="isPanelVisible('panel-risk-ranking')" class="screen-panel screen-panel--layer-primary">
            <cockpit-section-header
              title="红黄绿码企业分布"
              subtitle="高风险、中风险、正常企业结构占比"
              extra="结构分布"
              dot-tone="warning"
            />
            <cockpit-risk-ranking-panel
              ref="riskDonutRef"
              layout="chart"
              :credit-summary="dashboardData.creditScoreSummary || {}"
              :ranking-list="creditRankingList"
              :colors="COLORS"
            />
          </article>
        </aside>
      </section>

      <section
        v-if="isPanelVisible('panel-trend') || isPanelVisible('panel-table')"
        class="cockpit-detail-row"
      >
        <article v-if="isPanelVisible('panel-trend')" class="screen-panel screen-panel--layer-primary cockpit-detail-row__trend">
          <cockpit-section-header
            :title="pageConfig.trendTitle"
            subtitle="工伤事故、预警、扩面、设备、职业病五类趋势协同"
            extra="趋势分析"
          />
          <div class="cockpit-trend-panel">
            <div ref="trendChartRef" class="chart-box chart-box--trend" />
          </div>
        </article>

        <article v-if="isPanelVisible('panel-table')" class="screen-panel screen-panel--layer-table cockpit-detail-row__table">
          <cockpit-section-header
            title="地图可视范围内企业点位明细"
            subtitle="地图缩放和平移后自动联动当前视野企业"
            extra="联动明细"
          />
          <cockpit-enterprise-table :rows="visibleEnterpriseRows" @row-click="handleEnterpriseRowClick" />
        </article>
      </section>

      <cockpit-config-bar
        :visible-panels="prefs.visiblePanels"
        :region-code="queryParams.regionCode"
        :stat-month="queryParams.statMonth"
        :days="queryParams.days"
        :refresh-seconds="prefs.refreshSeconds"
        :region-options="regionOptions"
        :day-options="dayOptions"
        :last-refresh-label="lastRefreshLabel"
        :is-immersive="isImmersive"
        @toggle-panel="togglePanelVisible"
        @update:region-code="handleConfigRegionChange"
        @update:stat-month="handleConfigStatMonthChange"
        @update:days="handleConfigDaysChange"
        @update:refresh-seconds="handleRefreshSecondsChange"
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
import { getAzbCockpitDashboard, getYgbCockpitDashboard } from "@/api/ygb/cockpit"
import { listCreditScore } from "@/api/ygb/creditScore"
import { listWarning } from "@/api/ygb/warning"
import { useWorkbenchAssist } from "@/composables/useWorkbenchAssist"
import useAppStore from "@/store/modules/app"
import { toRegionPrefix } from "@/utils/regionScope"
import { useAuthorizedRegionOptions } from "@/utils/regionScope"
import { useRoleViewMode } from "@/utils/roleView"
import CockpitConfigBar from "@/views/cockpit/components/CockpitConfigBar.vue"
import CockpitConfigDialog from "@/views/cockpit/components/CockpitConfigDialog.vue"
import CockpitEnterpriseTable from "@/views/cockpit/components/CockpitEnterpriseTable.vue"
import CockpitMetricCard from "@/views/cockpit/components/CockpitMetricCard.vue"
import CockpitRiskRankingPanel from "@/views/cockpit/components/CockpitRiskRankingPanel.vue"
import CockpitSectionHeader from "@/views/cockpit/components/CockpitSectionHeader.vue"
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
const route = useRoute()
const isStandalone = computed(() => Boolean(route.meta?.standalone) || route.path.startsWith('/cockpit-screen/'))
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
  updateRefreshSeconds,
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

function handleRefreshSecondsChange(value) {
  updateRefreshSeconds(value)
  setupRefreshTimer()
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

const mapPointLegendItems = computed(() => [
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
  const center = mapConfig.value.center || [113.42, 23.08]
  const zoom = mapConfig.value.zoom ?? 1

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
          type: "continuous",
          min: 0,
          max: Math.max(...cityRiskItems.value.map((item) => item.riskScore), 20),
          calculable: false,
          orient: "vertical",
          left: 10,
          bottom: 24,
          itemWidth: 12,
          itemHeight: 108,
          text: ["高", "低"],
          textGap: 8,
          textStyle: { color: "rgba(204, 226, 232, 0.82)", fontSize: 10 },
          inRange: { color: ["#123d55", "#167c9d", "#00b8db", "#00e5ff", "#ffcc00", "#ff4d4f"] },
          borderColor: "rgba(0, 229, 255, 0.22)",
          backgroundColor: "rgba(4, 12, 26, 0.76)",
          padding: 8,
        }
      : undefined,
    geo: {
      map: MAP_NAME,
      roam: true,
      center,
      zoom,
      layoutCenter: ["50%", "50%"],
      layoutSize: "100%",
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
  const regionCode = toRegionPrefix(queryParams.value.regionCode)
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
  await loadConfig(queryParams.value.regionCode)
  setupRefreshTimer()
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
  width: 100%;
  margin: 0;
  padding: 4px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  color: #ebfaff;
  background-color: var(--cockpit-bg);
  background:
    radial-gradient(circle at 18% 12%, rgba(0, 255, 200, 0.12), transparent 28%),
    radial-gradient(circle at 82% 8%, rgba(0, 140, 255, 0.14), transparent 24%),
    radial-gradient(circle at 50% 32%, rgba(0, 229, 255, 0.2), transparent 30%),
    radial-gradient(circle at 50% 88%, rgba(255, 77, 79, 0.06), transparent 32%),
    linear-gradient(180deg, #030810 0%, #061021 48%, #030810 100%);
}

.cockpit-screen--standalone {
  min-height: 100vh;
  height: 100vh;
  padding: 0;
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
  opacity: 0.92;
  background:
    radial-gradient(circle at 20% 18%, rgba(0, 255, 200, 0.12), transparent 28%),
    radial-gradient(circle at 78% 22%, rgba(0, 160, 255, 0.1), transparent 26%),
    radial-gradient(circle at 50% 0%, rgba(0, 229, 255, 0.22), transparent 36%),
    radial-gradient(circle at 50% 58%, rgba(0, 85, 128, 0.18), transparent 52%);
  animation: cockpit-aurora-drift 18s ease-in-out infinite alternate;
}

.cockpit-screen__grid {
  inset: 0;
  opacity: 0.38;
  background:
    linear-gradient(rgba(0, 229, 255, 0.05) 1px, transparent 1px),
    linear-gradient(90deg, rgba(0, 229, 255, 0.05) 1px, transparent 1px);
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

.readonly-banner {
  margin: 0;
}

.cockpit-command {
  display: grid;
  gap: 6px;
}

.cockpit-command__hero {
  padding: 6px 16px 8px;
}

.cockpit-command__headline {
  position: relative;
  display: grid;
  gap: 4px;
  min-height: 48px;
}

.cockpit-command__title-wrap {
  min-width: 0;
  display: grid;
  justify-items: center;
  text-align: center;
}

.cockpit-command__title {
  margin: 0;
  color: #effcff;
  font-size: clamp(22px, 1.8vw, 30px);
  line-height: 1.1;
  font-weight: 800;
  text-shadow: 0 0 14px rgba(0, 229, 255, 0.22);
}

.cockpit-command__status {
  position: absolute;
  top: 0;
  right: 0;
  display: inline-flex;
  align-items: center;
  align-self: start;
  gap: 10px;
  flex-wrap: wrap;
  justify-content: flex-end;
  color: rgba(213, 236, 242, 0.78);
  font-size: 12px;
  max-width: min(44%, 540px);
}

.cockpit-command__status strong {
  color: #ebfcff;
  font-size: 13px;
}

.cockpit-command__status-pill {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 4px 10px;
  border: 1px solid rgba(255, 255, 255, 0.14);
  background: rgba(0, 229, 255, 0.1);
  backdrop-filter: blur(14px) saturate(160%);
  -webkit-backdrop-filter: blur(14px) saturate(160%);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.12);
}

.cockpit-command__status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #00ffc8;
  box-shadow: 0 0 10px rgba(0, 255, 200, 0.5);
}

.cockpit-metrics-row {
  display: grid;
  grid-template-columns: repeat(var(--metric-column-count, 10), minmax(0, 1fr));
  gap: 6px;
}

.cockpit-stage {
  flex: 1;
  min-height: 0;
  display: grid;
  grid-template-columns: minmax(260px, 0.95fr) minmax(0, 2.2fr) minmax(300px, 1.05fr);
  gap: 8px;
}

.cockpit-stage__side,
.cockpit-stage__center {
  min-height: 0;
  display: grid;
  gap: 8px;
}

.cockpit-stage__side {
  backdrop-filter: blur(24px) saturate(165%);
  -webkit-backdrop-filter: blur(24px) saturate(165%);
  background:
    linear-gradient(135deg, rgba(255, 255, 255, 0.06) 0%, transparent 42%),
    linear-gradient(180deg, rgba(8, 24, 46, 0.32), rgba(4, 12, 26, 0.44));
  border: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow:
    inset 0 1px 0 rgba(255, 255, 255, 0.12),
    0 12px 40px rgba(0, 0, 0, 0.22);
  padding: 4px 6px;
  gap: 6px;
}

.cockpit-stage__side .screen-panel {
  border: 1px solid rgba(255, 255, 255, 0.08);
  background:
    linear-gradient(135deg, rgba(255, 255, 255, 0.05) 0%, transparent 40%),
    rgba(6, 20, 38, 0.24);
  backdrop-filter: blur(18px) saturate(160%);
  -webkit-backdrop-filter: blur(18px) saturate(160%);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.08);
  padding: 6px 4px;
}

.cockpit-stage__side .screen-panel--layer-primary {
  background:
    linear-gradient(135deg, rgba(0, 229, 255, 0.08) 0%, transparent 42%),
    rgba(6, 20, 38, 0.26);
  box-shadow:
    inset 0 1px 0 rgba(255, 255, 255, 0.1),
    0 0 20px rgba(0, 229, 255, 0.06);
}

.cockpit-stage__side .screen-panel::before {
  display: block;
  border-color: rgba(0, 229, 255, 0.18);
}

.cockpit-stage__side .screen-panel::after {
  display: block;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.06), transparent 22%);
}

.cockpit-stage__side .screen-panel > :deep(.risk-ranking-panel) {
  flex: 1;
  min-height: 0;
}

.cockpit-stage__side--left {
  grid-template-rows: minmax(0, 1fr) minmax(0, 0.92fr);
}

.cockpit-stage__center {
  grid-template-rows: minmax(0, 1fr);
}

.cockpit-stage__center .map-panel {
  min-height: 0;
}

.cockpit-detail-row {
  flex-shrink: 0;
  display: grid;
  grid-template-columns: minmax(0, 1fr) minmax(0, 1.2fr);
  gap: 8px;
  min-height: clamp(240px, 28vh, 320px);
  max-height: clamp(280px, 32vh, 360px);
  margin-bottom: 2px;
  position: relative;
  z-index: 1;
}

.cockpit-detail-row:has(.cockpit-detail-row__trend:only-child),
.cockpit-detail-row:has(.cockpit-detail-row__table:only-child) {
  grid-template-columns: minmax(0, 1fr);
}

.cockpit-detail-row__trend,
.cockpit-detail-row__table {
  min-height: 0;
  overflow: hidden;
}

.cockpit-stage__side--right {
  grid-template-rows: minmax(0, 1fr) minmax(0, 0.88fr);
}

.screen-panel {
  position: relative;
  display: flex;
  flex-direction: column;
  padding: 8px 8px 7px;
  min-height: 0;
  overflow: hidden;
}

.screen-panel > :deep(.warning-stream),
.screen-panel > .region-summary-grid,
.screen-panel > .map-wrap,
.screen-panel > .chart-box--trend,
.screen-panel > .cockpit-trend-panel,
.screen-panel > :deep(.enterprise-table) {
  flex: 1;
  min-height: 0;
}

.screen-panel::before {
  content: "";
  position: absolute;
  inset: 0;
  border: 1px solid rgba(0, 229, 255, 0.22);
  pointer-events: none;
  z-index: 1;
}

.screen-panel::after {
  content: "";
  position: absolute;
  inset: 0;
  background:
    linear-gradient(135deg, rgba(255, 255, 255, 0.08) 0%, transparent 28%),
    linear-gradient(180deg, rgba(255, 255, 255, 0.04), transparent 20%);
  pointer-events: none;
  z-index: 0;
}

.chart-box--trend {
  flex: 1;
  width: 100%;
  min-height: 0;
}

.cockpit-trend-panel {
  display: flex;
  min-height: 220px;
  padding: 4px 6px 0;
  border: 1px solid rgba(255, 255, 255, 0.1);
  background:
    linear-gradient(135deg, rgba(255, 255, 255, 0.05) 0%, transparent 38%),
    rgba(6, 20, 38, 0.22);
  backdrop-filter: blur(16px) saturate(160%);
  -webkit-backdrop-filter: blur(16px) saturate(160%);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.08);
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
  min-height: 0;
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
  flex-direction: column;
  gap: 4px;
  max-width: min(160px, 28%);
  padding: 6px 8px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  background: rgba(6, 20, 38, 0.36);
  backdrop-filter: blur(16px) saturate(165%);
  -webkit-backdrop-filter: blur(16px) saturate(165%);
  box-shadow:
    inset 0 1px 0 rgba(255, 255, 255, 0.1),
    0 8px 24px rgba(0, 0, 0, 0.22);
}

.map-wrap__legend--points {
  left: 58px;
  bottom: 8px;
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
  border: 1px solid rgba(255, 255, 255, 0.1);
  background: rgba(6, 20, 38, 0.36);
  backdrop-filter: blur(14px) saturate(165%);
  -webkit-backdrop-filter: blur(14px) saturate(165%);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.08);
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
  gap: 6px;
}

.layer-switch {
  padding: 4px 10px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  background: rgba(6, 20, 38, 0.32);
  backdrop-filter: blur(12px) saturate(160%);
  -webkit-backdrop-filter: blur(12px) saturate(160%);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.08);
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
  flex: 1;
  min-height: 0;
  overflow: auto;
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 6px;
  align-content: start;
}

.region-summary-card {
  display: grid;
  gap: 3px;
  padding: 8px 10px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  background:
    linear-gradient(135deg, rgba(255, 255, 255, 0.08) 0%, transparent 40%),
    linear-gradient(180deg, rgba(8, 28, 52, 0.34), rgba(5, 18, 34, 0.42));
  backdrop-filter: blur(16px) saturate(165%);
  -webkit-backdrop-filter: blur(16px) saturate(165%);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.08);
  transition: transform 0.2s ease, box-shadow 0.2s ease, border-color 0.2s ease;
}

.region-summary-card:hover {
  transform: translateY(-1px);
  border-color: rgba(0, 229, 255, 0.32);
  box-shadow:
    inset 0 1px 0 rgba(255, 255, 255, 0.12),
    0 0 20px rgba(0, 229, 255, 0.16);
}

.region-summary-card--danger {
  border-color: rgba(255, 77, 79, 0.32);
  background:
    linear-gradient(135deg, rgba(255, 77, 79, 0.12) 0%, transparent 42%),
    linear-gradient(180deg, rgba(36, 10, 14, 0.38), rgba(16, 6, 10, 0.46));
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
.cockpit-screen :deep(.el-alert__description) {
  color: #ebfaff;
}

.cockpit-screen :deep(.el-button.is-circle) {
  width: 34px;
  height: 34px;
  padding: 0;
}

.cockpit-stage__side::-webkit-scrollbar,
.cockpit-stage__center::-webkit-scrollbar {
  width: 4px;
}

.cockpit-stage__side::-webkit-scrollbar-thumb,
.cockpit-stage__center::-webkit-scrollbar-thumb {
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

@keyframes cockpit-aurora-drift {
  0% {
    transform: translate3d(0, 0, 0) scale(1);
    opacity: 0.88;
  }
  100% {
    transform: translate3d(0, -1.5%, 0) scale(1.03);
    opacity: 1;
  }
}

.screen-panel--layer-map {
  animation: cockpit-panel-breathe 4.5s ease-in-out infinite;
}

@media (max-width: 1680px) {
  .cockpit-screen {
    --cockpit-col-left: clamp(250px, 17%, 300px);
    --cockpit-col-right: clamp(280px, 18%, 320px);
  }

  .cockpit-metrics-row {
    grid-template-columns: repeat(5, minmax(0, 1fr));
  }

  .cockpit-command__status {
    max-width: min(48%, 500px);
  }

  .cockpit-detail-row {
    min-height: clamp(220px, 26vh, 300px);
    max-height: clamp(260px, 30vh, 340px);
  }
}

@media (max-width: 1440px) {
  .cockpit-command__headline {
    min-height: auto;
    padding-top: 42px;
  }

  .cockpit-command__status {
    max-width: 100%;
  }

  .cockpit-metrics-row {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }

  .cockpit-stage {
    grid-template-columns: 1fr;
    overflow: auto;
  }

  .cockpit-stage__side--left,
  .cockpit-stage__side--right,
  .cockpit-stage__center {
    grid-template-rows: auto;
  }

  .cockpit-detail-row {
    grid-template-columns: 1fr;
    max-height: none;
  }

  .cockpit-trend-panel {
    min-height: 260px;
  }
}

@media (max-width: 980px) {
  .cockpit-metrics-row,
  .region-summary-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .cockpit-command__headline {
    gap: 12px;
    padding-top: 0;
  }

  .cockpit-command__status {
    position: static;
    justify-content: flex-start;
    max-width: 100%;
  }
}

@media (max-width: 768px) {
  .cockpit-screen {
    height: auto;
    min-height: calc(100vh - var(--layout-header-height, 50px) - var(--layout-tags-height, 34px));
  }

  .cockpit-command__title-wrap {
    justify-items: start;
    text-align: left;
  }

  .cockpit-metrics-row,
  .region-summary-grid {
    grid-template-columns: 1fr;
  }

  .layer-switches {
    justify-content: flex-start;
  }

  .map-wrap__legend,
  .map-wrap__stats {
    max-width: calc(100% - 16px);
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

body.cockpit-page-active {
  overflow: hidden;
  background: #040a17;
}

.cockpit-config-dialog :deep(.el-overlay) {
  backdrop-filter: blur(8px);
  background: rgba(0, 0, 0, 0.48);
}
</style>
