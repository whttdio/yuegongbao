<template>
  <div class="app-container azb-page azb-stat-report-workbench">
    <section class="gov-page-header azb-page__header">
      <div>
        <p class="azb-page__eyebrow">应急统计报表</p>
        <h1 class="azb-page__title">{{ roleTitle }}</h1>
        <p class="azb-page__desc">
          {{ roleDescription }}
          当前继续复用统一统计报表底座和附件结构，但安责保前端已按应急监管、企业复核、保险协同和银行协同四类高频视角重排焦点对象、治理复盘顺序和详情重点。
        </p>
      </div>
      <div class="azb-page__tip">
        <div class="azb-page__tip-item">当前视角：{{ roleBadge }}</div>
        <div class="azb-page__tip-item">优先关注：预警治理月报、工伤发生率月报、草稿积压和区域联动月报覆盖。</div>
        <div class="azb-page__tip-item">生成报表统一返回附件地址和结构化明细，便于继续治理复盘与区域协同。</div>
        <div class="azb-page__tip-item">后续可直接衔接驾驶舱复盘、PDF 存证和区域治理复核链路。</div>
      </div>
    </section>

    <div class="azb-summary-grid">
      <div
        v-for="item in summaryCards"
        :key="item.key"
        class="azb-summary-card"
        :class="item.cardClass"
      >
        <div class="azb-summary-card__label">{{ item.label }}</div>
        <div class="azb-summary-card__value">
          {{ item.value }}
          <span class="azb-summary-card__unit">{{ item.unit }}</span>
        </div>
        <div class="azb-summary-card__note">{{ item.note }}</div>
      </div>
    </div>



    <el-card class="search-card azb-search-card" shadow="never">
      <el-form ref="queryRef" :model="queryParams" :inline="true" v-show="showSearch">
        <el-form-item label="行政区划">
          <el-select v-model="queryParams.regionCode" clearable style="width: 180px">
            <el-option v-for="item in regionOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="统计月份">
          <el-date-picker v-model="queryParams.statMonth" type="month" value-format="YYYY-MM" format="YYYY-MM" style="width: 180px" />
        </el-form-item>
        <el-form-item label="报表类型">
          <el-select v-model="queryParams.reportCode" clearable style="width: 220px">
            <el-option v-for="item in azbReportTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="报表状态">
          <el-select v-model="queryParams.reportStatus" clearable style="width: 140px">
            <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">查询</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="toolbar-card azb-toolbar-card" shadow="never">
      <el-row :gutter="10">
        <el-col v-if="!isReadOnlyRole" :span="1.5">
          <el-button type="primary" plain icon="DocumentAdd" @click="openGenerateDialog()" v-hasPermi="['ygb:statReport:generate']">生成报表</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['ygb:statReport:export']">导出</el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>
    </el-card>

    <el-card class="table-card azb-table-card" shadow="never">
      <template #header>
        <div class="azb-card-head azb-card-head--between">
          <div>
            <div class="azb-card-head__title">统计报表台账</div>
            <div class="azb-card-head__desc">{{ focusTableHint }}</div>
          </div>
          <div class="azb-card-head__desc">当前总量 {{ total }} 份</div>
        </div>
      </template>
      <el-table v-loading="loading" :data="visibleReportList" @row-click="handleRowClick">
        <el-table-column label="报表ID" prop="reportId" width="100" />
        <el-table-column label="报表名称" min-width="180">
          <template #default="scope">
            {{ resolveAzbReportName(scope.row) }}
          </template>
        </el-table-column>
        <el-table-column label="统计月份" prop="statMonth" width="110" />
        <el-table-column label="区域" width="140">
          <template #default="scope">
            {{ regionNameMap[scope.row.regionCode] || scope.row.regionCode }}
          </template>
        </el-table-column>
        <el-table-column label="状态" prop="reportStatus" width="110">
          <template #default="scope">
            <dict-tag :options="statusOptions" :value="scope.row.reportStatus" />
          </template>
        </el-table-column>
        <el-table-column label="核心数量" prop="metricCount" width="100" />
        <el-table-column label="核心数值" prop="metricAmount" width="120" />
        <el-table-column label="核心比率" prop="metricRate" width="110">
          <template #default="scope">
            <span>{{ formatMetricRate(scope.row.metricRate, scope.row.reportCode) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="摘要" prop="reportSummary" min-width="300" show-overflow-tooltip />
        <el-table-column label="生成时间" width="180">
          <template #default="scope">
            <span>{{ parseTime(scope.row.generatedTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
          </template>
        </el-table-column>
        <el-table-column class-name="table-fill-column" min-width="1" />

        <el-table-column label="操作" fixed="right" align="center" width="200" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-button link type="info" icon="View" @click.stop="openDetail(scope.row)">详情</el-button>
            <el-button v-if="!isReadOnlyRole" link type="primary" icon="RefreshRight" @click.stop="openGenerateDialog(scope.row)" v-hasPermi="['ygb:statReport:generate']">重生成</el-button>
          </template>
        </el-table-column>
      </el-table>
      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>

    <el-dialog v-if="!isReadOnlyRole" title="生成统计报表" v-model="generateOpen" width="520px" append-to-body>
      <el-form ref="generateRef" :model="generateForm" :rules="generateRules" label-width="96px">
        <el-form-item label="区域" prop="regionCode">
          <el-select v-model="generateForm.regionCode" style="width: 100%">
            <el-option v-for="item in regionOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="统计月份" prop="statMonth">
          <el-date-picker v-model="generateForm.statMonth" type="month" value-format="YYYY-MM" format="YYYY-MM" style="width: 100%" />
        </el-form-item>
        <el-form-item label="报表类型" prop="reportCode">
          <el-select v-model="generateForm.reportCode" style="width: 100%">
            <el-option v-for="item in azbReportTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitGenerate">生成</el-button>
          <el-button @click="generateOpen = false">取消</el-button>
        </div>
      </template>
    </el-dialog>

    <page-detail-dialog v-model="detailOpen" title="报表详情" width="760px">
      <template v-if="reportDetail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="报表名称">{{ resolveAzbReportName(reportDetail) }}</el-descriptions-item>
          <el-descriptions-item label="统计月份">{{ reportDetail.statMonth }}</el-descriptions-item>
          <el-descriptions-item label="区域">{{ regionNameMap[reportDetail.regionCode] || reportDetail.regionCode }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <dict-tag :options="statusOptions" :value="reportDetail.reportStatus" />
          </el-descriptions-item>
          <el-descriptions-item label="核心数量">{{ reportDetail.metricCount }}</el-descriptions-item>
          <el-descriptions-item label="核心数值">{{ reportDetail.metricAmount }}</el-descriptions-item>
          <el-descriptions-item label="核心比率">{{ formatMetricRate(reportDetail.metricRate, reportDetail.reportCode) }}</el-descriptions-item>
          <el-descriptions-item label="生成时间">{{ parseTime(reportDetail.generatedTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</el-descriptions-item>
          <el-descriptions-item label="当前视角重点" :span="2">{{ detailFocusText }}</el-descriptions-item>
          <el-descriptions-item label="报表附件" :span="2">{{ reportDetail.attachmentUrl || '-' }}</el-descriptions-item>
          <el-descriptions-item label="摘要" :span="2">{{ reportDetail.reportSummary }}</el-descriptions-item>
        </el-descriptions>

        <div class="azb-detail-block">
          <h3>治理提示</h3>
          <div class="azb-tag-list">
            <el-tag v-for="item in detailHintTags" :key="item.label" :type="item.type" effect="plain">{{ item.label }}</el-tag>
          </div>
        </div>

        <div class="azb-detail-block">
          <h3>明细项</h3>
          <el-table :data="reportItems" size="small">
            <el-table-column label="分类" prop="itemCategory" width="150" />
            <el-table-column label="名称" prop="itemName" min-width="160" />
            <el-table-column label="维度编码" prop="itemDimension" width="160" />
            <el-table-column label="数量" prop="metricCount" width="90" />
            <el-table-column label="数值" prop="metricValue" width="120" />
            <el-table-column label="比率" prop="metricRate" width="100" />
          </el-table>
        </div>
      </template>
    </page-detail-dialog>
  </div>
</template>

<script setup name="AzbStatReport">
import { computed, ref, watchEffect } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useWorkbenchAssist } from '@/composables/useWorkbenchAssist'
import useUserStore from '@/store/modules/user'
import {
  decoratePortalExplanationItems,
  openPortalExplanationAction,
  resolvePortalExplanationSummary
} from '@/utils/portalExplanation'
import { useRoleViewMode } from '@/utils/roleView'
import { applyWorkbenchRouteQuery, buildWorkbenchContext, stripWorkbenchRouteQuery } from '@/utils/workbenchLink'
import { useAuthorizedRegionOptions } from '@/utils/regionScope'
import {
  formatMetricRate,
  statReportRegionNameMap as regionNameMap,
  statReportRegionOptions as allRegionOptions,
  statReportStatusOptions as statusOptions,
  statReportTypeOptions as rawReportTypeOptions,
  statusLabel,
  useStatReportPage,
  valueOrDefault
} from '@/views/statReport/useStatReportPage'

const route = useRoute()
const { setPageGuide } = useWorkbenchAssist()
const regionOptions = useAuthorizedRegionOptions(allRegionOptions)
const router = useRouter()
const userStore = useUserStore()
const { isReadOnlyRole, isBankRole, isInsurerRole, readOnlyRoleLabel, readOnlyRoleDescription } = useRoleViewMode()
const activeFocusKey = ref('')
const statReportWorkbenchFields = ['regionCode']

const roleView = computed(() => {
  if (isBankRole.value) return 'bank'
  if (isInsurerRole.value) return 'insurer'
  const roles = userStore.roles || []
  if (roles.includes('ygb_enterprise_admin') || roles.includes('ygb_enterprise_operator')) {
    return 'site-enterprise'
  }
  return 'emergency'
})

const azbReportTypeOptions = computed(() => rawReportTypeOptions.map(item => (
  item.value === 'SOCIAL_TAX'
    ? { ...item, label: '联动类月报' }
    : item
)))

const {
  loading,
  showSearch,
  total,
  reportList,
  currentReport,
  reportDetail,
  reportItems,
  summaryData,
  detailOpen,
  generateOpen,
  queryParams,
  generateForm,
  generateRules,
  getList,
  handleRowClick,
  handleQuery,
  resetQuery: pageResetQuery,
  handleExport,
  openGenerateDialog,
  submitGenerate,
  openDetail,
  syncCurrentReport
} = useStatReportPage({
  exportFilePrefix: 'azb_stat_report',
  defaultReportCode: 'WARNING_OVERVIEW',
  canMutate: () => !isReadOnlyRole.value,
  getCurrentList: () => visibleReportList.value,
  afterList: () => {
    syncActiveFocus()
  },
  immediate: false
})

const portalExplanations = computed(() => summaryData.value.azbExplanation || [])
const portalExplanationItems = computed(() => decoratePortalExplanationItems(portalExplanations.value, {
  portalCode: 'azb',
  panelTitle: '6.1 治理解释',
  panelDescription: '治理月报、区域态势和压降结果统一来自门户解释聚合接口。'
}))

const portalExplanationSummary = computed(() => resolvePortalExplanationSummary(portalExplanationItems.value, ''))

function handlePortalExplanationAction(action) {
  openPortalExplanationAction(router, action)
}

function buildAggregateWorkflowSteps(items = []) {
  return (items || [])
    .filter(item => item?.summary || item?.explanationSummary || item?.sourceDescription)
    .slice(0, 3)
    .map(item => ({
      label: item.dimensionName || item.moduleLabel || item.moduleCode || 'Explanation',
      desc: item.summary || item.explanationSummary || item.sourceDescription || ''
    }))
}

function buildAggregateHintTags(items = [], fallback = '') {
  const tags = []
  const leadingItem = (items || [])[0]
  if (leadingItem?.dimensionName) {
    tags.push({ label: leadingItem.dimensionName, type: 'info' })
  }
  if (leadingItem?.moduleLabel) {
    tags.push({ label: leadingItem.moduleLabel, type: 'success' })
  }
  if (!leadingItem?.dimensionName && fallback) {
    tags.push({ label: fallback, type: 'info' })
  }
  return tags
}

const roleBadge = computed(() => {
  if (roleView.value === 'bank') return '银行只读协同'
  if (roleView.value === 'insurer') return '保险只读协同'
  if (roleView.value === 'site-enterprise') return '企业报表复核'
  return '应急监管处置'
})

const roleTitle = computed(() => {
  if (roleView.value === 'bank') return '区域报表与信用协同复核看板'
  if (roleView.value === 'insurer') return '治理月报与风险复核协同台账'
  if (roleView.value === 'site-enterprise') return '企业风险复核与治理跟踪工作台'
  return '治理月报与区域处置工作台'
})

const roleDescription = computed(() => {
  if (roleView.value === 'bank') {
    return '围绕区域报表覆盖、重点风险月报和生成稳定度做只读复核，用于辅助联合风控和信用判断。'
  }
  if (roleView.value === 'insurer') {
    return '围绕预警治理月报、工伤发生率月报和草稿积压做协同复核，优先识别需要继续跟进的风险对象。'
  }
  if (roleView.value === 'site-enterprise') {
    return '围绕草稿补齐、月报重生成和治理留痕组织企业管理层持续跟踪，重点避免风险对象和协同月报脱节。'
  }
  return '围绕预警治理、工伤发生率、联动月报和草稿积压组织应急监管工作流，优先压降区域报表治理风险。'
})

function summaryCard(key, label, value, unit, note, cardClass) {
  return { key, label, value, unit, note, cardClass }
}

function focusQueue(key, title, count, unit, desc, actionText) {
  return { key, title, count, unit, desc, actionText }
}

const summaryCards = computed(() => {
  if (roleView.value === 'bank') {
    return [
      summaryCard('generated', '已生成', valueOrDefault(summaryData.value.generatedCount, 0), '份', '用于辅助区域报表完整度和联合风控判断。', 'azb-summary-card--success'),
      summaryCard('warning', '预警类月报', valueOrDefault(summaryData.value.warningOverviewCount, 0), '份', '最值得优先联动复核的区域风险月报。', 'azb-summary-card--danger'),
      summaryCard('injury', '工伤类月报', valueOrDefault(summaryData.value.injuryRateCount, 0), '份', '用于辅助判断事故风险和信用变化。', 'azb-summary-card--warning'),
      summaryCard('draft', '待补齐草稿', valueOrDefault(summaryData.value.draftCount, 0), '份', '草稿越多，越需要警惕区域统计链路不稳定。', '')
    ]
  }
  if (roleView.value === 'insurer') {
    return [
      summaryCard('warning', '预警类月报', valueOrDefault(summaryData.value.warningOverviewCount, 0), '份', '优先纳入保险协同和事故预防复核。', 'azb-summary-card--danger'),
      summaryCard('injury', '工伤类月报', valueOrDefault(summaryData.value.injuryRateCount, 0), '份', '工伤月报更适合作为服务策略跟踪依据。', 'azb-summary-card--warning'),
      summaryCard('generated', '已生成', valueOrDefault(summaryData.value.generatedCount, 0), '份', '当前已可复核的月报数量。', 'azb-summary-card--success'),
      summaryCard('draft', '草稿待补齐', valueOrDefault(summaryData.value.draftCount, 0), '份', '说明当前仍有报表对象未完成治理复核。', '')
    ]
  }
  if (roleView.value === 'site-enterprise') {
    return [
      summaryCard('draft', '草稿待补齐', valueOrDefault(summaryData.value.draftCount, 0), '份', '企业侧优先处理仍未成稿的月报对象。', 'azb-summary-card--warning'),
      summaryCard('generated', '已生成', valueOrDefault(summaryData.value.generatedCount, 0), '份', '已可查看和导出的月报数量。', 'azb-summary-card--success'),
      summaryCard('socialTax', '联动类月报', valueOrDefault(summaryData.value.socialTaxCount, 0), '份', '用于复核跨模块风险来源和协同链路是否完整。', ''),
      summaryCard('injury', '工伤类月报', valueOrDefault(summaryData.value.injuryRateCount, 0), '份', '帮助企业回看事故风险和作业闭环。', 'azb-summary-card--danger')
    ]
  }
  return [
    summaryCard('total', '报表数量', valueOrDefault(summaryData.value.totalCount, total.value), '份', '当前筛选条件下的报表总量。', ''),
    summaryCard('draft', '草稿待补齐', valueOrDefault(summaryData.value.draftCount, 0), '份', '仍需补生成或复核的报表对象。', 'azb-summary-card--warning'),
    summaryCard('warning', '预警类月报', valueOrDefault(summaryData.value.warningOverviewCount, 0), '份', '应急侧优先查看的治理月报。', 'azb-summary-card--danger'),
    summaryCard('generated', '已生成', valueOrDefault(summaryData.value.generatedCount, 0), '份', '当前已可复核和纳入治理复盘的报表数量。', 'azb-summary-card--success')
  ]
})

const focusQueues = computed(() => {
  if (roleView.value === 'bank') {
    return [
      focusQueue('warning', '预警治理月报', valueOrDefault(summaryData.value.warningOverviewCount, 0), '份', '先看会影响区域风控和联合风控判断的预警治理月报。', '查看风险月报'),
      focusQueue('injury', '工伤发生率月报', valueOrDefault(summaryData.value.injuryRateCount, 0), '份', '工伤类报表更适合作为区域信用和风险变化参考。', '查看工伤月报'),
      focusQueue('draft', '草稿积压', valueOrDefault(summaryData.value.draftCount, 0), '份', '草稿积压越高，越需要警惕统计链路断点。', '查看补齐对象')
    ]
  }
  if (roleView.value === 'insurer') {
    return [
      focusQueue('warning', '预警治理月报', valueOrDefault(summaryData.value.warningOverviewCount, 0), '份', '优先找出需要继续事故预防服务协同的治理月报。', '查看治理月报'),
      focusQueue('injury', '工伤发生率月报', valueOrDefault(summaryData.value.injuryRateCount, 0), '份', '工伤月报更值得持续跟踪服务成效。', '查看工伤对象'),
      focusQueue('generated', '已生成月报', valueOrDefault(summaryData.value.generatedCount, 0), '份', '先复核已出稿对象，再决定后续协同。', '查看已出稿报表')
    ]
  }
  if (roleView.value === 'site-enterprise') {
    return [
      focusQueue('draft', '待补齐草稿', valueOrDefault(summaryData.value.draftCount, 0), '份', '先把仍未成稿的企业月报对象找出来。', '进入补齐链路'),
      focusQueue('socialTax', '联动月报', valueOrDefault(summaryData.value.socialTaxCount, 0), '份', '联动类月报更容易暴露跨模块风险协同断点。', '查看联动月报'),
      focusQueue('generated', '已生成月报', valueOrDefault(summaryData.value.generatedCount, 0), '份', '补齐后及时回到已出稿对象做治理复核确认。', '查看已出稿对象')
    ]
  }
  return [
    focusQueue('warning', '预警治理月报', valueOrDefault(summaryData.value.warningOverviewCount, 0), '份', '应急侧先锁定当前区域最值得优先复核的治理月报对象。', '进入治理复核'),
    focusQueue('draft', '草稿待补齐', valueOrDefault(summaryData.value.draftCount, 0), '份', '草稿越多，越需要优先压实报表补齐责任。', '查看草稿对象'),
    focusQueue('injury', '工伤发生率月报', valueOrDefault(summaryData.value.injuryRateCount, 0), '份', '工伤类月报更适合作为区域监管压力研判入口。', '查看工伤月报'),
    focusQueue('socialTax', '联动类月报', valueOrDefault(summaryData.value.socialTaxCount, 0), '份', '联动类月报可辅助判断跨模块风险协同的稳定度。', '查看联动月报')
  ]
})

const activeFocus = computed(() => {
  if (!focusQueues.value.length) return null
  return focusQueues.value.find(item => item.key === activeFocusKey.value) || focusQueues.value[0]
})

function matchReportFocus(row, focusKey) {
  if (!row || !focusKey) {
    return false
  }
  if (focusKey === 'warning') return row.reportCode === 'WARNING_OVERVIEW'
  if (focusKey === 'injury') return row.reportCode === 'INJURY_RATE'
  if (focusKey === 'draft') return String(row.reportStatus || '') === '0'
  if (focusKey === 'generated') return ['1', '2'].includes(String(row.reportStatus || ''))
  if (focusKey === 'socialTax') return row.reportCode === 'SOCIAL_TAX'
  return false
}

function prioritizeFocusRows(rows, predicate) {
  const matched = []
  const others = []
  rows.forEach(row => {
    if (predicate(row)) {
      matched.push(row)
    } else {
      others.push(row)
    }
  })
  return [...matched, ...others]
}

const visibleReportList = computed(() => prioritizeFocusRows(reportList.value, row => matchReportFocus(row, activeFocus.value?.key)))

const workbenchContext = computed(() => buildWorkbenchContext(route.query, {
  fields: statReportWorkbenchFields,
  sourceLabel: '统计工作台',
  title: '当前统计台账沿用了工作台来源条件',
  description: '已按行政区划范围锁定报表结果，适合继续处理区域月报、草稿积压和治理复核。',
  fieldLabels: {
    regionCode: '行政区划'
  },
  fieldFormatters: {
    regionCode: value => regionNameMap[value] || value
  }
}))

function syncActiveFocus() {
  if (!activeFocusKey.value || !focusQueues.value.some(item => item.key === activeFocusKey.value)) {
    activeFocusKey.value = focusQueues.value[0]?.key || ''
  }
}

function handleSelectFocus(item) {
  activeFocusKey.value = item.key
  syncCurrentReport()
}

const focusTableHint = computed(() => {
  if (!activeFocus.value) {
    return '按当前筛选条件展示统计报表台账。'
  }
  return `当前焦点为“${activeFocus.value.title}”，已把对应重点对象优先排到表格前列。`
})

const selectedReportOverview = computed(() => {
  if (!currentReport.value) {
    return [
      { label: '当前焦点', value: activeFocus.value?.title || '-' },
      { label: '焦点数量', value: `${activeFocus.value?.count ?? 0}${activeFocus.value?.unit || ''}` },
      { label: '当前区域', value: queryParams.value.regionCode ? (regionNameMap[queryParams.value.regionCode] || queryParams.value.regionCode) : '全部区域' },
      { label: '下一步', value: activeFocus.value?.actionText || '-' }
    ]
  }
  return [
    { label: '报表名称', value: resolveAzbReportName(currentReport.value) },
    { label: '区域', value: regionNameMap[currentReport.value.regionCode] || currentReport.value.regionCode || '-' },
    { label: '报表状态', value: statusLabel(currentReport.value.reportStatus) },
    { label: '核心产出', value: `${currentReport.value.metricAmount || 0} / ${formatMetricRate(currentReport.value.metricRate, currentReport.value.reportCode)}` }
  ]
})

function resolveAzbReportName(report) {
  if (!report) {
    return '-'
  }
  if (report.reportCode === 'SOCIAL_TAX') {
    return '联动类月报'
  }
  return report.reportName || '-'
}

const primaryReportAction = computed(() => {
  if (!currentReport.value) {
    return { label: activeFocus.value?.actionText || '查看详情', action: 'detail' }
  }
  if (isReadOnlyRole.value) {
    return { label: '查看详情', action: 'detail' }
  }
  if (String(currentReport.value.reportStatus || '') === '0') {
    return { label: '按当前报表重生成', action: 'generate' }
  }
  return { label: '查看详情', action: 'detail' }
})

function buildReportHintTags(report, focus) {
  const tags = []
  if (!report) {
    if (focus?.title) {
      tags.push({ label: `当前焦点：${focus.title}`, type: 'info' })
      tags.push({ label: `优先动作：${focus.actionText}`, type: 'warning' })
    }
    return tags
  }
  if (String(report.reportStatus || '') === '0') {
    tags.push({ label: '当前报表仍为草稿，建议优先补齐生成', type: 'warning' })
  }
  if (report.reportCode === 'WARNING_OVERVIEW') {
    tags.push({ label: '预警治理月报，适合优先联动治理闭环', type: 'danger' })
  }
  if (report.reportCode === 'INJURY_RATE') {
    tags.push({ label: '工伤发生率月报，适合继续跟踪事故风险', type: 'info' })
  }
  if (report.reportCode === 'SOCIAL_TAX') {
    tags.push({ label: '联动类月报，适合继续复核跨模块风险协同链路', type: 'success' })
  }
  if (String(report.reportStatus || '') === '1') {
    tags.push({ label: '当前报表已生成，可直接查看明细和导出', type: 'success' })
  }
  return tags
}
const currentReportActionSummary = computed(() => {
  return resolvePortalExplanationSummary(
    portalExplanationItems.value,
    currentReport.value
      ? '当前统计报表的治理月报、区域态势和压降结果已按 6.1 治理口径展示。'
      : '当前暂无统计报表，可继续通过 6.1 治理解释查看本期重点治理方向。'
  )
})

const currentReportActionTags = computed(() => ([
  ...buildAggregateHintTags(portalExplanationItems.value, portalExplanationSummary.value),
  ...buildReportHintTags(currentReport.value || undefined, activeFocus.value)
]).slice(0, 5))

const workflowSteps = computed(() => {
  const aggregateSteps = buildAggregateWorkflowSteps(portalExplanationItems.value)
  if (aggregateSteps.length) {
    return aggregateSteps
  }

  if (roleView.value === 'bank') {
    return [
      { label: '先看预警和工伤月报', desc: '优先判断当前区域是否存在会影响信用和联合风控判断的高风险月报。' },
      { label: '再看草稿稳定度', desc: '草稿积压可辅助判断统计链路和数据回写是否稳定。' },
      { label: '最后回到信用与企业协同', desc: '必要时再进入企业、信用和驾驶舱页做联合复核。' }
    ]
  }
  if (roleView.value === 'insurer') {
    return [
      { label: '先锁定预警治理月报', desc: '优先找出需要继续事故预防服务协同的治理月报。' },
      { label: '再看工伤与已出稿对象', desc: '通过工伤月报和已生成月报判断服务复核优先级。' },
      { label: '最后回到详情复核', desc: '在详情里查看摘要、明细项和 Stub 附件，再决定是否继续协同。' }
    ]
  }
  if (roleView.value === 'site-enterprise') {
    return [
      { label: '先锁定待补齐草稿', desc: '优先处理仍未成稿的企业月报对象。' },
      { label: '再补联动和工伤月报', desc: '重点确认联动风险来源和工伤链路是否已完整生成。' },
      { label: '最后回到治理复核', desc: '确认可导出、可查看后再完成企业侧治理留痕。' }
    ]
  }
  return [
    { label: '先锁定预警治理月报', desc: '应急侧优先处理当前区域最关键的治理月报对象。' },
    { label: '再看草稿与工伤月报', desc: '通过草稿积压和工伤类月报判断区域报表治理压力。' },
    { label: '最后回到详情与治理复盘', desc: '通过详情、明细项和导出结果核实治理结果是否真正闭环。' }
  ]
})

const hintTags = computed(() => {
  const tags = [...buildAggregateHintTags(portalExplanationItems.value, portalExplanationSummary.value)]
  if (valueOrDefault(summaryData.value.draftCount, 0) > 0) {
    tags.push({ label: `当前有 ${valueOrDefault(summaryData.value.draftCount, 0)} 份草稿待补齐`, type: 'warning' })
  }
  if (valueOrDefault(summaryData.value.warningOverviewCount, 0) > 0) {
    tags.push({ label: `预警治理月报 ${valueOrDefault(summaryData.value.warningOverviewCount, 0)} 份，建议优先复核治理压力`, type: 'danger' })
  }
  if (valueOrDefault(summaryData.value.injuryRateCount, 0) > 0) {
    tags.push({ label: `工伤发生率月报 ${valueOrDefault(summaryData.value.injuryRateCount, 0)} 份，可继续跟踪事故风险`, type: 'info' })
  }
  if (roleView.value === 'site-enterprise' && valueOrDefault(summaryData.value.socialTaxCount, 0) > 0) {
    tags.push({ label: `联动类月报 ${valueOrDefault(summaryData.value.socialTaxCount, 0)} 份，建议核对风险协同链路完整度`, type: 'success' })
  }
  return tags.slice(0, 5)
})

const detailHintTags = computed(() => buildReportHintTags(reportDetail.value || currentReport.value, activeFocus.value))

const detailFocusText = computed(() => {
  if (roleView.value === 'bank') {
    return '重点看预警治理、工伤月报和草稿积压，用于辅助区域信用与联合风控复核。'
  }
  if (roleView.value === 'insurer') {
    return '重点看预警治理、工伤发生率和结构化明细，用于判断事故预防协同重点。'
  }
  if (roleView.value === 'site-enterprise') {
    return '重点看哪些报表仍需补齐、哪些联动类月报已形成治理结果，以及当前附件和明细是否完整。'
  }
  return '重点看预警治理、草稿状态、工伤月报和结构化明细，必要时优先升级区域复核。'
})

const readOnlyAlertTitle = computed(() => `${readOnlyRoleLabel.value}：当前仅保留报表查看、详情和导出`)
const readOnlyAlertDescription = computed(() => {
  if (isBankRole.value) {
    return `${readOnlyRoleDescription.value} 当前页面聚焦区域报表、重点月报和详情复核，不展示生成和重生成动作。`
  }
  if (isInsurerRole.value) {
    return `${readOnlyRoleDescription.value} 当前页面聚焦治理月报、工伤月报和详情复核，不展示生成和重生成动作。`
  }
  return `${readOnlyRoleDescription.value} 当前页面仅作为菜单收口兜底，不承担生成和重生成动作。`
})

function handlePrimaryReportAction() {
  if (!currentReport.value) {
    return
  }
  if (primaryReportAction.value.action === 'generate') {
    if (isReadOnlyRole.value) {
      proxy.$modal.msgWarning(`${readOnlyRoleLabel.value}仅保留报表查看、详情和导出，不能生成报表`)
      return
    }
    openGenerateDialog(currentReport.value)
    return
  }
  openDetail(currentReport.value)
}

function resetQuery() {
  pageResetQuery()
  applyWorkbenchRouteQuery(route.query, queryParams.value, statReportWorkbenchFields)
  getList()

watchEffect(() => {
  setPageGuide({
    title: roleTitle.value || '????????????',
    description: roleDescription.value || '?????????????????????????????????',
    portalExplanation: portalExplanationItems.value,
    focus: focusQueues.value,
    selection: [...selectedReportOverview.value, { label: '??????', value: currentReportActionSummary.value }],
    workflow: workflowSteps.value,
    hints: [...currentReportActionTags.value, ...hintTags.value].slice(0, 6)
  })
})

}

function clearWorkbenchContext() {
  Object.assign(queryParams.value, {
    pageNum: 1,
    regionCode: undefined
  })
  router.replace({
    path: route.path,
    query: stripWorkbenchRouteQuery(route.query, statReportWorkbenchFields)
  })
  getList()
}

applyWorkbenchRouteQuery(route.query, queryParams.value, statReportWorkbenchFields)
getList()
</script>

<style scoped lang="scss">
.azb-stat-report-workbench {
  .azb-workbench-alert {
    margin-bottom: 16px;
  }

  .azb-workbench-alert__title {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 12px;
  }

  .azb-workbench-alert__desc {
    display: flex;
    flex-wrap: wrap;
    gap: 8px 12px;
    margin-bottom: 12px;
    color: #47636d;
    line-height: 1.7;
    font-size: 13px;
  }

  .azb-workbench-alert__desc strong {
    color: #0b6b78;
  }

  .azb-focus-grid {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 16px;
    margin-bottom: 16px;
  }

  .azb-card-head__title {
    font-size: 16px;
    font-weight: 600;
    color: #0f172a;
  }

  .azb-card-head__desc {
    margin-top: 4px;
    font-size: 13px;
    line-height: 1.6;
    color: #64748b;
  }

  .azb-focus-list--single {
    display: flex;
    flex-direction: column;
    gap: 12px;
  }

  .azb-focus-queue {
    width: 100%;
    display: flex;
    justify-content: space-between;
    gap: 16px;
    padding: 14px 16px;
    border: 1px solid #dbe7f3;
    border-radius: 10px;
    background: #f8fbff;
    text-align: left;
    cursor: pointer;
    transition: border-color 0.2s ease, box-shadow 0.2s ease;
  }

  .azb-focus-queue:hover,
  .azb-focus-queue.is-active {
    border-color: #0b6b78;
    box-shadow: 0 10px 24px rgba(11, 107, 120, 0.08);
  }

  .azb-focus-queue__main strong {
    display: block;
    margin-bottom: 6px;
    color: #0f172a;
  }

  .azb-focus-queue__main p {
    margin: 0;
    font-size: 13px;
    line-height: 1.7;
    color: #64748b;
  }

  .azb-focus-queue__side {
    min-width: 120px;
    display: flex;
    flex-direction: column;
    align-items: flex-end;
    justify-content: center;
    gap: 8px;
  }

  .azb-focus-queue__count {
    font-size: 20px;
    font-weight: 700;
    color: #0b6b78;
  }

  .azb-focus-queue__action {
    font-size: 12px;
    color: #1d4ed8;
  }

  .azb-source-list {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 12px;
  }

  .azb-source-item {
    padding: 14px 16px;
    border: 1px solid #dbe7f3;
    border-radius: 10px;
    background: #f8fbff;
  }

  .azb-source-item__label {
    font-size: 12px;
    color: #64748b;
    margin-bottom: 8px;
  }

  .azb-source-item__value {
    font-size: 16px;
    font-weight: 600;
    color: #0f172a;
  }

  .azb-recommend-panel {
    margin-top: 16px;
    padding: 16px;
    border-radius: 12px;
    background: linear-gradient(135deg, rgba(11, 107, 120, 0.08), rgba(29, 78, 216, 0.05));
    border: 1px solid rgba(11, 107, 120, 0.16);
  }

  .azb-recommend-panel__title {
    font-size: 13px;
    font-weight: 600;
    color: #0b6b78;
  }

  .azb-recommend-panel__summary {
    margin: 8px 0 12px;
    font-size: 14px;
    line-height: 1.7;
    color: #1f2937;
  }

  .azb-focus-actions {
    display: flex;
    flex-wrap: wrap;
    gap: 12px;
    margin-top: 16px;
  }

  .azb-pipeline-list {
    display: flex;
    flex-direction: column;
    gap: 14px;
  }

  .azb-pipeline-item {
    display: flex;
    gap: 14px;
    align-items: flex-start;
    padding: 14px 0;
    border-bottom: 1px dashed #d9e3ef;
  }

  .azb-pipeline-item:last-child {
    border-bottom: 0;
    padding-bottom: 0;
  }

  .azb-pipeline-item__index {
    width: 42px;
    height: 42px;
    border-radius: 50%;
    background: #0b6b78;
    color: #fff;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    font-weight: 700;
  }

  .azb-pipeline-item__body strong {
    display: block;
    margin-bottom: 6px;
    color: #0f172a;
  }

  .azb-pipeline-item__body p {
    margin: 0;
    font-size: 13px;
    line-height: 1.7;
    color: #64748b;
  }

  .azb-tag-list {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
  }

  .azb-detail-block {
    margin-top: 20px;
  }

  .azb-detail-block h3 {
    margin: 0 0 12px;
    font-size: 16px;
    color: #1f2d3d;
  }

  @media (max-width: 1200px) {
    .azb-focus-grid,
    .azb-source-list {
      grid-template-columns: 1fr;
    }
  }
}
</style>
