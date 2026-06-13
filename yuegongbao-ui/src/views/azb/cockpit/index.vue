<template>
  <div class="app-container azb-cockpit" v-loading="loading">
    <section class="page-header">
      <div>
        <p class="page-eyebrow">安责险治理驾驶舱</p>
        <h1 class="page-title">{{ roleTitle }}</h1>
        <p class="page-desc">{{ roleDescription }}</p>
      </div>
      <div class="page-tip-group">
        <div class="page-tip-item">
          <span>当前视角</span>
          <strong>{{ roleBadge }}</strong>
        </div>
        <div class="page-tip-item">
          <span>工作链路</span>
          <strong>{{ primaryPathLabel }}</strong>
        </div>
        <div class="page-tip-item">
          <span>当前区域</span>
          <strong>{{ regionLabel }}</strong>
        </div>
      </div>
    </section>

    <section class="summary-grid">
      <article v-for="item in summaryCards" :key="item.key" class="summary-card">
        <div class="summary-label">{{ item.label }}</div>
        <div class="summary-value">
          {{ item.value }}
          <span class="summary-unit">{{ item.unit }}</span>
        </div>
        <div class="summary-note">{{ item.note }}</div>
      </article>
    </section>

    <section class="focus-grid">
      <el-card class="card" shadow="never">
        <template #header>
          <div class="card-head">
            <div>
              <div class="card-title">治理焦点队列</div>
              <div class="card-desc">先锁定当前最关键的治理对象，再继续进入模块处置。</div>
            </div>
          </div>
        </template>

        <div class="focus-list">
          <button
            v-for="item in focusItems"
            :key="item.key"
            type="button"
            class="focus-item"
            :class="{ 'is-active': activeFocus?.key === item.key }"
            @click="activeFocusKey = item.key"
          >
            <div>
              <strong>{{ item.title }}</strong>
              <p>{{ item.desc || item.summary || '查看该对象的风险摘要与协同入口。' }}</p>
            </div>
            <div class="focus-side">
              <span>{{ formatCountText(item) }}</span>
              <span>{{ item.actionText || '进入' }}</span>
            </div>
          </button>
          <el-empty v-if="!focusItems.length" description="暂无焦点对象" :image-size="60" />
        </div>
      </el-card>

      <el-card class="card" shadow="never">
        <template #header>
          <div class="card-head">
            <div>
              <div class="card-title">当前焦点对象</div>
              <div class="card-desc">统一展示治理摘要、跟进建议和推荐模块。</div>
            </div>
          </div>
        </template>

        <div class="highlight-panel">
          <span class="highlight-label">优先对象</span>
          <div class="highlight-head">
            <strong>{{ activeFocus?.title || '暂无焦点' }}</strong>
            <span class="highlight-count">{{ activeFocusCount }}</span>
          </div>
          <p>{{ activeFocusSummary }}</p>
        </div>

        <div class="overview-grid">
          <div class="overview-item">
            <div class="overview-label">来源</div>
            <div class="overview-value">{{ activeFocus?.sourceLabel || '驾驶舱聚合' }}</div>
          </div>
          <div class="overview-item">
            <div class="overview-label">区域</div>
            <div class="overview-value">{{ regionLabel }}</div>
          </div>
          <div class="overview-item">
            <div class="overview-label">统计月份</div>
            <div class="overview-value">{{ queryParams.statMonth || '--' }}</div>
          </div>
          <div class="overview-item">
            <div class="overview-label">推荐动作</div>
            <div class="overview-value">{{ activeFocus?.actionText || '进入模块处理' }}</div>
          </div>
        </div>

        <div class="recommend-panel">
          <div class="recommend-title">跟进建议</div>
          <div class="recommend-summary">{{ activeFocusSummary }}</div>
          <div class="tag-list">
            <el-tag v-for="item in hintTags" :key="item.label" :type="resolveTagType(item.type)" effect="plain">{{ item.label }}</el-tag>
          </div>
        </div>

        <div class="action-row">
          <el-button type="primary" :disabled="!activeFocus?.path" @click="openModule(activeFocus)">进入当前模块</el-button>
          <el-button plain @click="openModule(defaultWarningAction)">查看预警中心</el-button>
          <el-button plain @click="openModule(defaultStatReportAction)">查看统计月报</el-button>
        </div>
      </el-card>
    </section>

    <section class="focus-grid">
      <el-card class="card" shadow="never">
        <template #header>
          <div class="card-head">
            <div>
              <div class="card-title">治理路径</div>
              <div class="card-desc">{{ rolePathDescription }}</div>
            </div>
          </div>
        </template>

        <div class="workflow-list">
          <div v-for="(item, index) in workflowSteps" :key="`${item.label}-${index}`" class="workflow-item">
            <span class="workflow-index">{{ String(index + 1).padStart(2, '0') }}</span>
            <div>
              <strong>{{ item.label }}</strong>
              <p>{{ item.desc }}</p>
            </div>
          </div>
        </div>
      </el-card>

      <el-card class="card" shadow="never">
        <template #header>
          <div class="card-head">
            <div>
              <div class="card-title">筛选与导出</div>
              <div class="card-desc">按区域、日期和趋势周期查看当前治理态势。</div>
            </div>
          </div>
        </template>

        <el-form :model="queryParams" inline>
          <el-form-item label="区域">
            <el-select v-model="queryParams.regionCode" style="width: 180px">
              <el-option v-for="item in regionOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="统计日期">
            <el-date-picker v-model="queryParams.statDate" type="date" value-format="YYYY-MM-DD" format="YYYY-MM-DD" style="width: 180px" />
          </el-form-item>
          <el-form-item label="统计月份">
            <el-date-picker v-model="queryParams.statMonth" type="month" value-format="YYYY-MM" format="YYYY-MM" style="width: 180px" />
          </el-form-item>
          <el-form-item label="趋势天数">
            <el-select v-model="queryParams.days" style="width: 140px">
              <el-option v-for="item in dayOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="Search" @click="handleQuery">查询</el-button>
            <el-button icon="Refresh" @click="resetQuery">重置</el-button>
            <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['ygb:cockpit:export']">导出趋势</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </section>

    <el-alert
      v-if="isReadOnlyRole"
      :title="readOnlyAlertTitle"
      :description="readOnlyAlertDescription"
      type="info"
      :closable="false"
      show-icon
      style="margin-bottom: 16px;"
    />

    <section class="module-grid">
      <el-card class="card" shadow="never">
        <div class="chart-head">
          <div>
            <div class="card-title">快捷入口</div>
            <div class="card-desc">{{ quickActionDescription }}</div>
          </div>
        </div>
        <div class="quick-grid">
          <button v-for="item in quickActions" :key="item.key" type="button" class="quick-card" @click="openModule(item)">
            <span class="quick-badge">{{ item.badge || '常用' }}</span>
            <strong>{{ item.label }}</strong>
            <p>{{ item.desc || item.summary || '进入对应治理模块。' }}</p>
          </button>
          <el-empty v-if="!quickActions.length" description="暂无快捷入口" :image-size="60" />
        </div>
      </el-card>

      <el-card class="card" shadow="never">
        <div class="chart-head">
          <div>
            <div class="card-title">模块联动</div>
            <div class="card-desc">按照当前治理焦点承接模块联动。</div>
          </div>
        </div>
        <div class="module-table">
          <div class="module-row module-row--head">
            <span>模块</span>
            <span>当前重点</span>
            <span>下一步</span>
          </div>
          <button v-for="item in moduleRows" :key="item.key" type="button" class="module-row" @click="openModule(item)">
            <span>{{ item.label }}</span>
            <span>{{ item.metric || formatCountText(item) }}</span>
            <span>{{ item.nextStep || item.desc || '进入模块处理' }}</span>
          </button>
          <el-empty v-if="!moduleRows.length" description="暂无模块联动数据" :image-size="60" />
        </div>
      </el-card>
    </section>

    <section class="chart-grid">
      <el-card class="card" shadow="never">
        <div class="chart-head">
          <div>
            <div class="card-title">治理趋势</div>
            <div class="card-desc">展示近期预警、安责险和工伤参保趋势变化。</div>
          </div>
        </div>
        <div ref="trendChartRef" class="chart-surface" />
      </el-card>

      <el-card class="card" shadow="never">
        <div class="chart-head">
          <div>
            <div class="card-title">来源分布</div>
            <div class="card-desc">识别风险主要来自哪些治理链路。</div>
          </div>
        </div>
        <div ref="distributionChartRef" class="chart-surface" />
      </el-card>

      <el-card class="card" shadow="never">
        <div class="chart-head">
          <div>
            <div class="card-title">地图点位</div>
            <div class="card-desc">展示企业、设备和围栏要素点位。</div>
          </div>
        </div>
        <div ref="mapChartRef" class="chart-surface" />
      </el-card>

      <el-card class="card" shadow="never">
        <div class="chart-head">
          <div>
            <div class="card-title">点位明细</div>
            <div class="card-desc">当前地图点位与围栏要素明细。</div>
          </div>
        </div>
        <el-table :data="featureTableList" height="320" size="small">
          <el-table-column label="名称" prop="featureName" min-width="140" show-overflow-tooltip />
          <el-table-column label="类型" prop="featureType" width="110" />
          <el-table-column label="区域" prop="regionName" width="130" />
          <el-table-column label="状态" prop="featureStatus" width="110" />
          <el-table-column label="几何" prop="geometryType" width="90" />
          <el-table-column label="坐标摘要" prop="coordinateText" min-width="180" show-overflow-tooltip />
        </el-table>
      </el-card>
    </section>
  </div>
</template>

<script setup name="AzbCockpit">
import { computed, ref, watch, watchEffect } from 'vue'
import { useWorkbenchAssist } from '@/composables/useWorkbenchAssist'
import { decoratePortalExplanationItems, resolvePortalExplanationSummary } from '@/utils/portalExplanation'
import { useRoleViewMode } from '@/utils/roleView'
import { resolveDefaultStatReportRoute } from '@/views/statReport/reportConfigs'
import {
  cockpitDayOptions as dayOptions,
  cockpitRegionNameMap as regionNameMap,
  cockpitRegionOptions as allRegionOptions,
  distributionName,
  sortByKeyOrder,
  useCockpitPage
} from '@/views/cockpit/useCockpitPage'
import { getAzbCockpitDashboard } from '@/api/ygb/cockpit'
import { useAuthorizedRegionOptions } from '@/utils/regionScope'

const { isReadOnlyRole, readOnlyRoleLabel, readOnlyRoleDescription, isBankRole, isInsurerRole } = useRoleViewMode()
const regionOptions = useAuthorizedRegionOptions(allRegionOptions)
const { setPageGuide } = useWorkbenchAssist()

const {
  loading,
  queryParams,
  dashboardData,
  indicators,
  featureTableList,
  trendChartRef,
  distributionChartRef,
  mapChartRef,
  handleQuery,
  resetQuery,
  handleExport,
  openModule
} = useCockpitPage({
  fetchDashboard: getAzbCockpitDashboard,
  exportFilePrefix: 'azb_cockpit',
  distributionLabelFormatter: item => distributionName(item.dimensionName || item.dimensionCode),
  trendColors: ['#0b6b78', '#f59e0b', '#dc2626'],
  trendSeries: [
    { name: '当日预警', type: 'bar', dataKey: 'todayWarningCount', yAxisIndex: 0, barMaxWidth: 24 },
    { name: '安责险覆盖率', type: 'line', dataKey: 'aqInsuranceRate', yAxisIndex: 1 },
    { name: '工伤参保率', type: 'line', dataKey: 'insuranceRate', yAxisIndex: 1 }
  ],
  distributionColor: '#0b6b78',
  pointColors: {
    ENTERPRISE: '#0b6b78',
    DEVICE: '#1d7a46',
    FENCE: '#d97706'
  }
})

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

function resolveTagType(type) {
  return ['success', 'warning', 'danger', 'info', 'primary'].includes(type) ? type : 'info'
}

const roleTitle = computed(() => {
  if (isBankRole.value) return '银行协同治理驾驶舱'
  if (isInsurerRole.value) return '保险协同治理驾驶舱'
  return dashboardData.value.roleLabel || '安责险治理综合驾驶舱'
})
const roleBadge = computed(() => {
  if (isBankRole.value) return '银行只读视角'
  if (isInsurerRole.value) return '保险只读视角'
  return dashboardData.value.roleLabel || '治理视角'
})
const roleDescription = computed(() => dashboardData.value.homeSummary || dashboardData.value.roleDescription || '聚焦预警、设备、工伤、安责险和区域治理联动。')
const rolePathDescription = computed(() => '先锁定治理焦点，再进入对应模块处理，最后回到驾驶舱复核整体态势。')
const quickActionDescription = computed(() => '将当前高频治理入口集中在同一屏，减少菜单跳转。')
const primaryPathLabel = computed(() => '焦点识别 -> 模块治理 -> 驾驶舱复核')
const regionLabel = computed(() => indicators.value.regionName || regionNameMap[queryParams.value.regionCode] || '全部区域')

const portalExplanationItems = computed(() => decoratePortalExplanationItems(dashboardData.value.azbExplanation || [], {
  portalCode: 'azb',
  panelTitle: '6.1 治理解释',
  panelDescription: '门户解释用于补充治理摘要、证据和推荐下钻入口。'
}))
const portalExplanationSummary = computed(() => resolvePortalExplanationSummary(portalExplanationItems.value, ''))

const summaryCards = computed(() => {
  const cards = dashboardData.value.summaryCards || []
  if (cards.length) {
    return cards.slice(0, 8)
  }
  return [
    { key: 'warning', label: '待处置预警', value: indicators.value.pendingWarningCount || 0, unit: '条', note: '待闭环风险工单' },
    { key: 'insurance', label: '安责险覆盖率', value: indicators.value.aqInsuranceRate || 0, unit: '%', note: '当前安责险覆盖水平' },
    { key: 'injury', label: '超期工伤', value: indicators.value.overdueInjuryCount || 0, unit: '起', note: '需继续压降的工伤事件' },
    { key: 'device', label: '在线设备', value: indicators.value.onlineDeviceCount || 0, unit: '台', note: '现场在线感知设备规模' }
  ]
})

const focusItems = computed(() => sortByKeyOrder((dashboardData.value.focusQueues || []).map(item => normalizeAction(item)), []))
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
const activeFocusCount = computed(() => formatCountText(activeFocus.value || {}))
const activeFocusSummary = computed(() => activeFocus.value?.summary || activeFocus.value?.desc || activeFocus.value?.sourceDescription || portalExplanationSummary.value || '等待驾驶舱汇总当前治理重点。')

const workflowSteps = computed(() => {
  const steps = dashboardData.value.workflowSteps || []
  if (steps.length) {
    return steps
  }
  return [
    { label: '锁定焦点对象', desc: '先识别当前最关键的治理对象和风险模块。' },
    { label: '进入模块处置', desc: '从驾驶舱直接进入预警、设备、安责险等治理模块。' },
    { label: '回看月报复核', desc: '通过统计月报验证治理结果和区域变化。' }
  ]
})

const hintTags = computed(() => dashboardData.value.hintTags || [])
const quickActions = computed(() => (dashboardData.value.quickActions || []).map(item => normalizeAction(item)).slice(0, 8))
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

const defaultWarningAction = computed(() => ({ path: '/azb/warning' }))
const defaultStatReportAction = computed(() => ({ path: resolveDefaultStatReportRoute('azb') }))
const readOnlyAlertTitle = computed(() => `${readOnlyRoleLabel.value}：当前仅保留总览、明细和导出`)
const readOnlyAlertDescription = computed(() => `${readOnlyRoleDescription.value} 当前工作台仍展示焦点对象和治理路径，但不承接录入类动作。`)

function handlePortalExplanationAction(action) {
  openModule(action)
}

watchEffect(() => {
  setPageGuide({
    title: roleTitle.value || '????????',
    description: roleDescription.value || '?????????????????????????????????',
    portalExplanation: portalExplanationItems.value,
    focus: focusItems.value,
    selection: [
      { label: '????', value: regionLabel.value },
      { label: '????', value: queryParams.statMonth || '--' },
      { label: '????', value: activeFocus.value?.title || primaryPathLabel.value },
      { label: '????', value: activeFocusSummary.value }
    ],
    workflow: workflowSteps.value,
    hints: hintTags.value
  })
})
</script>

<style scoped lang="scss">
.azb-cockpit {
  padding: 20px;
  border-radius: 20px;
  background:
    radial-gradient(circle at top right, rgba(14, 165, 233, 0.08), transparent 20%),
    linear-gradient(180deg, #f4f8fb 0%, #eef4f8 100%);
}

.page-header,
.summary-grid,
.focus-grid,
.module-grid,
.chart-grid {
  display: grid;
  gap: 16px;
  margin-bottom: 16px;
}

.page-header {
  grid-template-columns: minmax(0, 1.4fr) minmax(320px, 0.9fr);
  align-items: stretch;
}

.page-eyebrow {
  margin: 0 0 8px;
  color: #0b6b78;
  font-size: 13px;
  font-weight: 600;
}

.page-title {
  margin: 0;
  color: #0f172a;
  font-size: 34px;
  line-height: 1.1;
}

.page-desc,
.summary-note,
.card-desc,
.focus-item p,
.workflow-item p,
.quick-card p,
.recommend-summary,
.overview-value {
  color: #64748b;
  line-height: 1.7;
}

.page-tip-group {
  display: grid;
  gap: 12px;
}

.page-tip-item,
.summary-card,
.card {
  border: 1px solid #dbe7f3;
  border-radius: 14px;
  background: #fff;
}

.page-tip-item {
  display: grid;
  gap: 6px;
  padding: 16px 18px;
}

.page-tip-item span,
.highlight-label,
.overview-label,
.summary-label {
  color: #0b6b78;
  font-size: 12px;
  font-weight: 600;
}

.page-tip-item strong,
.focus-item strong,
.workflow-item strong,
.quick-card strong,
.overview-value,
.highlight-head strong {
  color: #0f172a;
}

.summary-grid {
  grid-template-columns: repeat(4, minmax(0, 1fr));
}

.summary-card {
  padding: 18px 20px;
}

.summary-value {
  margin-top: 10px;
  color: #0f172a;
  font-size: 28px;
  font-weight: 700;
}

.summary-unit {
  margin-left: 4px;
  color: #64748b;
  font-size: 13px;
}

.focus-grid,
.module-grid,
.chart-grid {
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

.card :deep(.el-card__header) {
  padding: 18px 20px 0;
  border-bottom: 0;
}

.card :deep(.el-card__body) {
  padding: 16px 20px 20px;
}

.card-head,
.chart-head {
  display: flex;
  justify-content: space-between;
  gap: 16px;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #0f172a;
}

.focus-list,
.workflow-list,
.tag-list {
  display: grid;
  gap: 12px;
}

.focus-item {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  width: 100%;
  padding: 14px 16px;
  border: 1px solid #dbe7f3;
  border-radius: 10px;
  background: #f8fbff;
  text-align: left;
  cursor: pointer;
}

.focus-item:hover,
.focus-item.is-active,
.quick-card:hover,
.module-row:hover {
  border-color: #0b6b78;
  box-shadow: 0 10px 24px rgba(11, 107, 120, 0.08);
}

.focus-side {
  min-width: 120px;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  justify-content: center;
  gap: 8px;
  color: #0b6b78;
  font-size: 13px;
  font-weight: 600;
}

.highlight-panel,
.overview-item,
.recommend-panel,
.quick-card {
  border: 1px solid #dbe7f3;
  border-radius: 12px;
  background: #f8fbff;
}

.highlight-panel {
  padding: 16px;
}

.highlight-head {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin-top: 8px;
}

.highlight-count {
  color: #0b6b78;
  font-weight: 700;
}

.overview-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
  margin-top: 16px;
}

.overview-item {
  padding: 14px 16px;
}

.recommend-panel {
  margin-top: 16px;
  padding: 16px;
  background: linear-gradient(135deg, rgba(11, 107, 120, 0.08), rgba(29, 78, 216, 0.05));
}

.recommend-title {
  font-size: 13px;
  font-weight: 600;
  color: #0b6b78;
}

.action-row {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 16px;
}

.workflow-item {
  display: flex;
  gap: 14px;
  align-items: flex-start;
  padding: 14px 0;
  border-bottom: 1px dashed #d9e3ef;
}

.workflow-item:last-child {
  border-bottom: 0;
}

.workflow-index {
  width: 42px;
  height: 42px;
  border-radius: 50%;
  background: #0b6b78;
  color: #fff;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  flex: none;
}

.quick-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
}

.quick-card {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 16px;
  text-align: left;
  cursor: pointer;
}

.quick-badge {
  display: inline-flex;
  width: fit-content;
  padding: 2px 10px;
  border-radius: 999px;
  background: #e0f2fe;
  color: #075985;
  font-size: 12px;
}

.module-table {
  display: flex;
  flex-direction: column;
  border: 1px solid #dbe7f3;
  border-radius: 12px;
  overflow: hidden;
  background: #f8fbff;
}

.module-row {
  display: grid;
  grid-template-columns: 120px minmax(0, 1fr) minmax(0, 1fr);
  gap: 16px;
  align-items: center;
  width: 100%;
  padding: 14px 16px;
  border: 0;
  border-bottom: 1px solid #dbe7f3;
  background: transparent;
  text-align: left;
  cursor: pointer;
  color: #334155;
}

.module-row:last-child {
  border-bottom: 0;
}

.module-row--head {
  background: #edf4fb;
  font-size: 12px;
  font-weight: 600;
  color: #64748b;
  cursor: default;
}

.chart-surface {
  height: 320px;
}

@media (max-width: 1200px) {
  .summary-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 992px) {
  .page-header,
  .focus-grid,
  .module-grid,
  .chart-grid,
  .quick-grid,
  .overview-grid {
    grid-template-columns: 1fr;
  }

  .module-row {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .summary-grid {
    grid-template-columns: 1fr;
  }
}
</style>
