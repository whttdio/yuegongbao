<template>
  <div class="app-container ygb-cockpit" v-loading="loading">
    <section class="cockpit-hero">
      <div class="hero-main">
        <p class="hero-eyebrow">用工保障驾驶舱</p>
        <div class="hero-title-row">
          <h1 class="hero-title">{{ roleTitle }}</h1>
          <span class="hero-badge">{{ roleBadge }}</span>
        </div>
        <p class="hero-desc">{{ roleDescription }}</p>
        <div class="hero-meta">
          <div class="meta-chip">
            <span>当前区域</span>
            <strong>{{ regionLabel }}</strong>
          </div>
          <div class="meta-chip">
            <span>统计月份</span>
            <strong>{{ queryParams.statMonth || '--' }}</strong>
          </div>
          <div class="meta-chip">
            <span>优先链路</span>
            <strong>{{ activeFocus?.title || primaryPathLabel }}</strong>
          </div>
        </div>
      </div>

      <div class="hero-side">
        <article class="signal-card">
          <span class="signal-label">当前聚焦对象</span>
          <strong class="signal-title">{{ activeFocus?.title || '等待聚焦对象' }}</strong>
          <p class="signal-desc">{{ activeFocusSummary }}</p>
          <div class="signal-foot">
            <span>{{ activeFocusCount }}</span>
            <span>{{ activeFocus?.actionText || '进入处置' }}</span>
          </div>
        </article>

        <div class="tip-list">
          <div class="tip-item">
            <span>视角</span>
            <strong>{{ roleBadge }}</strong>
          </div>
          <div class="tip-item">
            <span>工作提示</span>
            <strong>{{ cockpitUseHint }}</strong>
          </div>
          <div class="tip-item">
            <span>驾驶舱说明</span>
            <strong>{{ portalExplanationSummary || '当前页面用于汇总重点对象、推荐动作和下钻入口。' }}</strong>
          </div>
        </div>
      </div>
    </section>

    <section class="summary-grid">
      <article v-for="item in summaryCards" :key="item.key" class="summary-card" :class="item.cardClass">
        <div class="summary-label">{{ item.label }}</div>
        <div class="summary-value">
          {{ item.value }}
          <span class="summary-unit">{{ item.unit }}</span>
        </div>
        <div class="summary-note">{{ item.note }}</div>
      </article>
    </section>

    <section class="command-grid">
      <el-card class="panel" shadow="never">
        <template #header>
          <div class="panel-head">
            <div>
              <div class="panel-title">焦点队列</div>
              <div class="panel-desc">按当前处理优先级收敛对象，避免在多个业务页之间来回切换。</div>
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
              <p>{{ item.desc || item.summary || '查看该对象的风险摘要与推荐动作。' }}</p>
            </div>
            <div class="focus-side">
              <span>{{ formatCountText(item) }}</span>
              <span>{{ item.actionText || '进入' }}</span>
            </div>
          </button>
          <el-empty v-if="!focusItems.length" description="暂无焦点对象" :image-size="60" />
        </div>
      </el-card>

      <el-card class="panel" shadow="never">
        <template #header>
          <div class="panel-head">
            <div>
              <div class="panel-title">当前焦点</div>
              <div class="panel-desc">统一展示摘要、推荐动作和相关下钻模块。</div>
            </div>
          </div>
        </template>

        <div class="focus-highlight">
          <span class="highlight-label">优先对象</span>
          <div class="highlight-title-row">
            <strong>{{ activeFocus?.title || '暂无焦点' }}</strong>
            <span class="highlight-count">{{ activeFocusCount }}</span>
          </div>
          <p>{{ activeFocusSummary }}</p>
        </div>

        <div class="source-grid">
          <div class="source-item">
            <div class="source-label">来源</div>
            <div class="source-value">{{ activeFocus?.sourceLabel || '驾驶舱聚合' }}</div>
          </div>
          <div class="source-item">
            <div class="source-label">区域</div>
            <div class="source-value">{{ regionLabel }}</div>
          </div>
          <div class="source-item">
            <div class="source-label">统计月份</div>
            <div class="source-value">{{ queryParams.statMonth || '--' }}</div>
          </div>
          <div class="source-item">
            <div class="source-label">推荐动作</div>
            <div class="source-value">{{ activeFocus?.actionText || '进入模块处理' }}</div>
          </div>
        </div>

        <div class="action-row">
          <el-button type="primary" :disabled="!activeFocus?.path" @click="openModule(activeFocus)">进入当前模块</el-button>
          <el-button plain @click="openModule(defaultWarningAction)">查看预警中心</el-button>
          <el-button plain @click="openModule(defaultStatReportAction)">查看统计月报</el-button>
        </div>
      </el-card>

      <div class="side-stack">
        <el-card class="panel" shadow="never">
          <template #header>
            <div class="panel-head">
              <div>
                <div class="panel-title">工作路径</div>
                <div class="panel-desc">{{ rolePathDescription }}</div>
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

        <el-card class="panel" shadow="never">
          <template #header>
            <div class="panel-head">
              <div>
                <div class="panel-title">工作提示</div>
                <div class="panel-desc">优先处理会阻断链路闭环的对象，再继续下钻明细。</div>
              </div>
            </div>
          </template>

          <div class="tag-list">
            <el-tag v-for="item in hintTags" :key="item.label" :type="item.type || 'info'" effect="plain">
              {{ item.label }}
            </el-tag>
            <el-empty v-if="!hintTags.length" description="暂无额外提示" :image-size="60" />
          </div>
        </el-card>
      </div>
    </section>

    <el-alert
      v-if="isReadOnlyRole"
      :title="readOnlyAlertTitle"
      :description="readOnlyAlertDescription"
      type="info"
      :closable="false"
      show-icon
      class="readonly-alert"
    />

    <section class="ops-grid">

      <el-card class="panel" shadow="never">
        <template #header>
          <div class="panel-head">
            <div>
              <div class="panel-title">筛选与导出</div>
              <div class="panel-desc">当前先保留静态驾驶舱布局，并接入查询、导出和图表渲染。</div>
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
            <el-button plain icon="Setting" @click="openModule({ path: '/ygb/cockpitConfig' })" v-hasPermi="['ygb:cockpitConfig:list']">驾驶舱配置</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </section>

    <section class="module-grid">
      <el-card class="panel chart-panel" shadow="never">
        <div class="chart-head">
          <div>
            <div class="panel-title">模块联动总览</div>
            <div class="panel-desc">按当前重点对象串联业务模块，直接进入下一步处理。</div>
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

      <el-card class="panel chart-panel" shadow="never">
        <div class="chart-head">
          <div>
            <div class="panel-title">快捷入口</div>
            <div class="panel-desc">{{ quickActionDescription }}</div>
          </div>
        </div>
        <div class="quick-grid">
          <button v-for="item in quickActions" :key="item.key" type="button" class="quick-card" @click="openModule(item)">
            <span class="quick-badge">{{ item.badge || '常用' }}</span>
            <strong>{{ item.label }}</strong>
            <p>{{ item.desc || item.summary || '进入对应业务模块。' }}</p>
          </button>
          <el-empty v-if="!quickActions.length" description="暂无快捷入口" :image-size="60" />
        </div>
      </el-card>
    </section>

    <section class="chart-grid">
      <el-card class="panel chart-panel chart-panel--wide" shadow="never">
        <div class="chart-head">
          <div>
            <div class="panel-title">核心指标趋势</div>
            <div class="panel-desc">展示近期预警、参保和扩面指标变化，辅助判断当前处理压力。</div>
          </div>
        </div>
        <div ref="trendChartRef" class="chart-surface" />
      </el-card>

      <el-card class="panel chart-panel" shadow="never">
        <div class="chart-head">
          <div>
            <div class="panel-title">预警来源分布</div>
            <div class="panel-desc">识别当前异常主要来自哪些联动模块。</div>
          </div>
        </div>
        <div ref="distributionChartRef" class="chart-surface" />
      </el-card>

      <el-card class="panel chart-panel" shadow="never">
        <div class="chart-head">
          <div>
            <div class="panel-title">点位分布</div>
            <div class="panel-desc">展示企业、设备和围栏点位，用于后续对接地图与现场闭环。</div>
          </div>
        </div>
        <div ref="mapChartRef" class="chart-surface" />
      </el-card>

      <el-card class="panel chart-panel chart-panel--wide" shadow="never">
        <div class="chart-head">
          <div>
            <div class="panel-title">地图要素清单</div>
            <div class="panel-desc">当前地图点位与围栏明细，便于核对设备和地理要素。</div>
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

<script setup name="YgbCockpit">
import { computed, ref, watch, watchEffect } from 'vue'
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
  sortByKeyOrder,
  useCockpitPage
} from '@/views/cockpit/useCockpitPage'
import { getYgbCockpitDashboard } from '@/api/ygb/cockpit'

const userStore = useUserStore()
const { setPageGuide } = useWorkbenchAssist()
const regionOptions = useAuthorizedRegionOptions(allRegionOptions)
const { isReadOnlyRole, readOnlyRoleLabel, readOnlyRoleDescription } = useRoleViewMode()

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
  fetchDashboard: getYgbCockpitDashboard,
  exportFilePrefix: 'ygb_cockpit',
  trendColors: ['#33cff7', '#3f7cff', '#4fe0a1'],
  trendSeries: [
    { name: '当日预警', type: 'bar', dataKey: 'todayWarningCount', yAxisIndex: 0, barMaxWidth: 24 },
    { name: '工伤参保率', type: 'line', dataKey: 'insuranceRate', yAxisIndex: 1 },
    { name: '扩面完成率', type: 'line', dataKey: 'expandCompletionRate', yAxisIndex: 1 }
  ]
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

const ROLE_META = Object.freeze({
  ygb_hrss_supervisor: {
    title: '人社经办驾驶舱',
    badge: '经办视角',
    desc: '聚焦区域异常、扩面减损、工伤闭环和统计归档。'
  },
  ygb_enterprise_operator: {
    title: '企业经办驾驶舱',
    badge: '操作视角',
    desc: '聚焦合同、考勤、高处作业和预警回写。'
  },
  ygb_enterprise_admin: {
    title: '企业治理驾驶舱',
    badge: '管理视角',
    desc: '聚焦合同、工资、社保税务和现场闭环。'
  }
})

const matchedRoleMeta = computed(() => {
  const roles = userStore.roles || []
  return roles.map(role => ROLE_META[role]).find(Boolean) || {
    title: '用工保障综合驾驶舱',
    badge: '综合视角',
    desc: '聚焦重点对象、模块联动和统计复核。'
  }
})

const roleTitle = computed(() => matchedRoleMeta.value.title)
const roleBadge = computed(() => matchedRoleMeta.value.badge)
const roleDescription = computed(() => dashboardData.value.homeSummary || dashboardData.value.roleDescription || matchedRoleMeta.value.desc)
const rolePathDescription = computed(() => '先锁定焦点对象，再进入对应业务模块，最后回到驾驶舱复核结果。')
const cockpitUseHint = computed(() => '先看风险摘要，再点模块处理。')
const quickActionDescription = computed(() => '将当前高频办理入口集中在同一屏，减少模块跳转。')
const primaryPathLabel = computed(() => '焦点对象 -> 模块处理 -> 驾驶舱复核')
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
    return cards.slice(0, 8)
  }
  return [
    { key: 'warning', label: '待处置预警', value: indicators.value.pendingWarningCount || 0, unit: '条', note: '待闭环预警对象', cardClass: 'warning' },
    { key: 'injury', label: '超期工伤', value: indicators.value.overdueInjuryCount || 0, unit: '起', note: '需继续压降的工伤事件', cardClass: 'danger' },
    { key: 'insurance', label: '参保率', value: indicators.value.insuranceRate || 0, unit: '%', note: '当前工伤参保覆盖水平', cardClass: 'success' },
    { key: 'device', label: '在线设备', value: indicators.value.onlineDeviceCount || 0, unit: '台', note: '当前在线感知设备规模', cardClass: 'primary' }
  ]
})

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
const activeFocusCount = computed(() => formatCountText(activeFocus.value || {}))
const activeFocusSummary = computed(() => activeFocus.value?.summary || activeFocus.value?.desc || activeFocus.value?.sourceDescription || portalExplanationSummary.value || '等待驾驶舱汇总当前优先链路。')

const workflowSteps = computed(() => {
  const steps = dashboardData.value.workflowSteps || []
  if (steps.length) {
    return steps
  }
  return [
    { label: '识别焦点对象', desc: '优先锁定当前最需要处理的风险对象。' },
    { label: '进入模块处理', desc: '从驾驶舱直接进入对应业务模块继续处置。' },
    { label: '回看预警与月报', desc: '通过预警中心和统计月报验证处理效果。' }
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

const defaultWarningAction = computed(() => ({ path: '/ygb/warning' }))
const defaultStatReportAction = computed(() => ({ path: resolveDefaultStatReportRoute('ygb') }))

const readOnlyAlertTitle = computed(() => `${readOnlyRoleLabel.value}仅保留总览、联动和导出`)
const readOnlyAlertDescription = computed(() => `${readOnlyRoleDescription.value || ''} 当前驾驶舱仍展示焦点对象和推荐入口，但不承接录入类动作。`.trim())

function handlePortalExplanationAction(action) {
  openModule(action)
}

watchEffect(() => {
  setPageGuide({
    title: roleTitle.value || '???????',
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
.ygb-cockpit {
  min-height: 100%;
  padding: 24px;
  color: #eaf4ff;
  background:
    radial-gradient(circle at top left, rgba(69, 217, 255, 0.16), transparent 24%),
    radial-gradient(circle at top right, rgba(63, 124, 255, 0.18), transparent 24%),
    linear-gradient(180deg, #07131f 0%, #081827 48%, #06111c 100%);
  border: 1px solid rgba(102, 181, 230, 0.14);
  border-radius: 24px;
  box-shadow: 0 22px 48px rgba(1, 10, 20, 0.42);
}

.cockpit-hero,
.summary-grid,
.command-grid,
.ops-grid,
.module-grid,
.chart-grid {
  display: grid;
  gap: 16px;
  margin-bottom: 16px;
}

.cockpit-hero {
  grid-template-columns: minmax(0, 1.65fr) minmax(320px, 0.95fr);
}

.hero-main,
.signal-card,
.tip-item,
.summary-card,
.panel {
  border: 1px solid rgba(109, 202, 255, 0.14);
  border-radius: 20px;
  background: rgba(8, 27, 44, 0.84);
  box-shadow: 0 18px 40px rgba(1, 10, 20, 0.26);
}

.hero-main {
  padding: 24px 28px;
}

.hero-eyebrow,
.highlight-label,
.signal-label,
.meta-chip span,
.tip-item span,
.source-label,
.summary-label {
  color: #45d9ff;
  font-size: 12px;
  font-weight: 600;
}

.hero-title-row {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 16px;
  margin-top: 12px;
}

.hero-title {
  margin: 0;
  font-size: 34px;
  line-height: 1.1;
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  min-height: 34px;
  padding: 0 14px;
  border-radius: 999px;
  border: 1px solid rgba(90, 215, 255, 0.24);
  background: rgba(69, 217, 255, 0.08);
  color: #dff8ff;
  font-size: 13px;
  font-weight: 600;
}

.hero-desc,
.signal-desc,
.tip-item strong,
.summary-note,
.panel-desc,
.focus-item p,
.workflow-item p,
.quick-card p,
.source-value,
.highlight-title-row + p {
  color: #9eb5ca;
  line-height: 1.75;
}

.hero-meta {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
  margin-top: 20px;
}

.meta-chip,
.tip-item,
.source-item,
.focus-item,
.workflow-item,
.quick-card,
.module-table,
.focus-highlight {
  background: rgba(255, 255, 255, 0.03);
}

.meta-chip {
  display: grid;
  gap: 8px;
  padding: 16px 18px;
  border-radius: 16px;
  border: 1px solid rgba(101, 184, 234, 0.12);
}

.meta-chip strong,
.signal-title,
.highlight-title-row strong,
.focus-item strong,
.workflow-item strong,
.quick-card strong,
.source-value {
  color: #f4f8ff;
}

.hero-side {
  display: grid;
  gap: 14px;
}

.signal-card {
  display: grid;
  gap: 12px;
  padding: 22px;
}

.signal-title {
  font-size: 24px;
}

.signal-foot {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  padding-top: 12px;
  border-top: 1px solid rgba(124, 194, 232, 0.12);
  color: #d6ebff;
  font-size: 13px;
  font-weight: 600;
}

.tip-list {
  display: grid;
  gap: 12px;
}

.tip-item {
  display: grid;
  gap: 8px;
  padding: 16px 18px;
}

.summary-grid {
  grid-template-columns: repeat(4, minmax(0, 1fr));
}

.summary-card {
  padding: 18px 20px;
}

.summary-value {
  margin-top: 12px;
  color: #f7fbff;
  font-size: 30px;
  font-weight: 700;
}

.summary-unit {
  margin-left: 6px;
  color: #9eb5ca;
  font-size: 13px;
}

.command-grid {
  grid-template-columns: minmax(280px, 1fr) minmax(340px, 1.25fr) minmax(280px, 0.9fr);
}

.panel :deep(.el-card__header) {
  padding: 20px 22px 0;
  border-bottom: 0;
}

.panel :deep(.el-card__body) {
  padding: 18px 22px 22px;
}

.panel-head {
  display: flex;
  justify-content: space-between;
  gap: 16px;
}

.panel-title {
  color: #f1f7ff;
  font-size: 16px;
  font-weight: 700;
}

.focus-list,
.workflow-list,
.side-stack {
  display: grid;
  gap: 12px;
}

.focus-item {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto;
  gap: 16px;
  width: 100%;
  padding: 16px;
  border: 1px solid rgba(101, 184, 234, 0.12);
  border-radius: 16px;
  color: inherit;
  text-align: left;
  cursor: pointer;
}

.focus-item.is-active,
.focus-item:hover,
.module-row:hover,
.quick-card:hover {
  border-color: rgba(85, 218, 255, 0.3);
  background: rgba(69, 217, 255, 0.08);
}

.focus-side {
  display: grid;
  align-content: center;
  justify-items: end;
  gap: 8px;
  color: #d6ebff;
  font-size: 13px;
  font-weight: 600;
}

.focus-highlight {
  padding: 16px;
  border: 1px solid rgba(101, 184, 234, 0.12);
  border-radius: 16px;
}

.highlight-title-row {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin-top: 10px;
}

.highlight-count {
  color: #45d9ff;
  font-weight: 700;
}

.source-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
  margin-top: 16px;
}

.source-item {
  padding: 14px 16px;
  border: 1px solid rgba(101, 184, 234, 0.12);
  border-radius: 14px;
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
  padding: 14px 0;
  border-bottom: 1px dashed rgba(124, 194, 232, 0.14);
}

.workflow-item:last-child {
  border-bottom: 0;
}

.workflow-index {
  width: 42px;
  height: 42px;
  border-radius: 50%;
  background: linear-gradient(135deg, #33cff7, #356dff);
  color: #fff;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  flex: none;
}

.tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.readonly-alert {
  margin-bottom: 16px;
}

.ops-grid {
  grid-template-columns: minmax(320px, 0.95fr) minmax(0, 1.2fr);
}

.module-grid {
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

.chart-grid {
  grid-template-columns: repeat(12, minmax(0, 1fr));
}

.chart-panel {
  min-width: 0;
}

.chart-panel--wide {
  grid-column: span 7;
}

.chart-grid > .chart-panel:not(.chart-panel--wide) {
  grid-column: span 5;
}

.chart-head {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 16px;
}

.quick-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.quick-card {
  padding: 16px;
  border: 1px solid rgba(101, 184, 234, 0.12);
  border-radius: 16px;
  color: inherit;
  text-align: left;
  cursor: pointer;
}

.quick-badge {
  display: inline-flex;
  align-items: center;
  min-height: 24px;
  padding: 0 10px;
  border-radius: 999px;
  background: rgba(69, 217, 255, 0.1);
  color: #dcf7ff;
  font-size: 12px;
  font-weight: 600;
}

.module-table {
  border: 1px solid rgba(101, 184, 234, 0.12);
  border-radius: 16px;
  overflow: hidden;
}

.module-row {
  display: grid;
  grid-template-columns: 120px minmax(0, 1fr) minmax(0, 1fr);
  gap: 16px;
  align-items: center;
  width: 100%;
  padding: 14px 16px;
  border: 0;
  border-bottom: 1px solid rgba(101, 184, 234, 0.12);
  background: transparent;
  color: #d6ebff;
  text-align: left;
  cursor: pointer;
}

.module-row:last-child {
  border-bottom: 0;
}

.module-row--head {
  background: rgba(63, 124, 255, 0.1);
  color: #9eb5ca;
  font-size: 12px;
  font-weight: 600;
  cursor: default;
}

.chart-surface {
  width: 100%;
  height: 320px;
  border-radius: 16px;
  background: rgba(6, 20, 32, 0.45);
}

.ygb-cockpit :deep(.portal-explanation-card),
.ygb-cockpit :deep(.el-alert),
.ygb-cockpit :deep(.el-table) {
  background: rgba(8, 27, 44, 0.84);
  color: #eaf4ff;
}

.ygb-cockpit :deep(.portal-explanation-card__title),
.ygb-cockpit :deep(.portal-explanation-item__top strong),
.ygb-cockpit :deep(.portal-explanation-item__summary),
.ygb-cockpit :deep(.el-alert__title),
.ygb-cockpit :deep(.el-alert__description),
.ygb-cockpit :deep(.el-form-item__label),
.ygb-cockpit :deep(.el-table th),
.ygb-cockpit :deep(.el-table td) {
  color: #eaf4ff;
}

.ygb-cockpit :deep(.portal-explanation-card__desc),
.ygb-cockpit :deep(.portal-explanation-item__meta) {
  color: #9eb5ca;
}

.ygb-cockpit :deep(.el-input__wrapper),
.ygb-cockpit :deep(.el-select__wrapper) {
  background: rgba(255, 255, 255, 0.05);
}

@media (max-width: 1400px) {
  .command-grid,
  .ops-grid,
  .module-grid,
  .cockpit-hero {
    grid-template-columns: 1fr;
  }

  .summary-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .chart-panel--wide,
  .chart-grid > .chart-panel:not(.chart-panel--wide) {
    grid-column: span 6;
  }
}

@media (max-width: 992px) {
  .chart-grid {
    grid-template-columns: 1fr;
  }

  .chart-panel--wide,
  .chart-grid > .chart-panel:not(.chart-panel--wide) {
    grid-column: auto;
  }

  .summary-grid,
  .hero-meta,
  .source-grid,
  .quick-grid {
    grid-template-columns: 1fr;
  }

  .module-row,
  .focus-item,
  .hero-title-row,
  .highlight-title-row {
    grid-template-columns: 1fr;
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
