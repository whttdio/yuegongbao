<template>
  <div class="app-container ygb-page ygb-stat-report-page">
    <section class="gov-page-header ygb-page__header">
      <div>
        <p class="ygb-page__eyebrow">统计报表</p>
        <h1 class="ygb-page__title">{{ roleTitle }}</h1>
        <p class="ygb-page__desc">
          {{ roleDescription }}
          粤工保侧重点不是单独查看报表结果，而是把月报生成、联动核查、归档复核和闭环留痕串成连续办理台账。
        </p>
      </div>
      <div class="ygb-page__tip">
        当前视角：{{ roleBadge }}。{{ roleTip }}
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

    <el-alert
      v-if="isReadOnlyRole"
      class="ygb-readonly-alert"
      type="warning"
      :closable="false"
      :title="readOnlyAlertTitle"
      :description="readOnlyAlertDescription"
      show-icon
      style="margin-bottom: 18px;"
    />

    <el-alert
      v-if="workbenchContext"
      class="ygb-workbench-alert"
      type="info"
      :closable="false"
      show-icon
    >
      <template #title>
        <div class="ygb-workbench-alert__title">
          <span>{{ workbenchContext.title }}</span>
          <el-button link type="primary" @click="clearWorkbenchContext">清空来源条件</el-button>
        </div>
      </template>
      <div class="ygb-workbench-alert__desc">
        <strong>{{ workbenchContext.sourceLabel }}</strong>
        <span>{{ workbenchContext.description }}</span>
      </div>
      <div class="ygb-tag-list">
        <el-tag v-for="item in workbenchContext.tags" :key="item.key" effect="plain">{{ item.label }}：{{ item.value }}</el-tag>
      </div>
    </el-alert>

    <el-card class="search-card ygb-search-card" shadow="never">
      <el-form ref="queryRef" :model="queryParams" :inline="true" v-show="showSearch">
        <el-form-item label="区域">
          <el-select v-model="queryParams.regionCode" style="width: 180px">
            <el-option v-for="item in regionOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="统计月份">
          <el-date-picker v-model="queryParams.statMonth" type="month" value-format="YYYY-MM" format="YYYY-MM" style="width: 180px" />
        </el-form-item>
        <el-form-item label="报表类型">
          <el-select v-model="queryParams.reportCode" style="width: 220px">
            <el-option v-for="item in authorizedQueryReportTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
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

    <el-card class="toolbar-card ygb-toolbar-card" shadow="never">
      <el-row :gutter="10">
        <el-col v-if="canGenerate" :span="1.5">
          <el-button type="primary" plain icon="DocumentAdd" @click="guardedOpenGenerateDialog()">生成报表</el-button>
        </el-col>
        <el-col v-if="canExportCurrentReport" :span="1.5">
          <el-button type="warning" plain icon="Download" @click="handleExport">导出</el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="guardedGetList" />
      </el-row>
    </el-card>

    <el-card class="table-card ygb-table-card" shadow="never">
      <template #header>
        <div class="ygb-card-head ygb-card-head--between">
          <div>
            <div class="ygb-card-head__title">统计报表办理台账</div>
            <div class="ygb-card-head__desc">{{ focusTableHint }}</div>
          </div>
          <div class="ygb-card-head__desc">当前总量 {{ total }} 份</div>
        </div>
      </template>
      <el-table v-loading="loading" :data="visibleReportList" @row-click="handleRowClick">
        <el-table-column label="报表ID" prop="reportId" width="100" />
        <el-table-column label="报表名称" prop="reportName" min-width="180" />
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
            <el-button
              v-if="canGenerateReport(scope.row.reportCode)"
              link
              type="primary"
              icon="RefreshRight"
              @click.stop="guardedOpenGenerateDialog(scope.row)"
            >
              重生成
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="guardedGetList" />
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
            <el-option v-for="item in authorizedGenerateReportTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="guardedSubmitGenerate">生成</el-button>
          <el-button @click="generateOpen = false">取消</el-button>
        </div>
      </template>
    </el-dialog>

    <page-detail-dialog v-model="detailOpen" title="报表详情" width="760px">
      <template v-if="reportDetail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="报表名称">{{ reportDetail.reportName }}</el-descriptions-item>
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
          <el-descriptions-item label="报表下载地址" :span="2">{{ reportDetail.attachmentUrl || '-' }}</el-descriptions-item>
          <el-descriptions-item label="摘要" :span="2">{{ reportDetail.reportSummary }}</el-descriptions-item>
        </el-descriptions>

        <div class="ygb-detail-block">
          <h3>办理提示</h3>
          <div class="ygb-tag-list">
            <el-tag v-for="item in detailHintTags" :key="item.label" :type="item.type" effect="plain">{{ item.label }}</el-tag>
          </div>
        </div>

        <div class="ygb-report-items">
          <h3>明细项</h3>
          <el-table :data="reportItems" size="small">
            <el-table-column label="分类" width="150">
              <template #default="scope">
                {{ formatStatReportItemCategory(scope.row.itemCategory) }}
              </template>
            </el-table-column>
            <el-table-column label="名称" prop="itemName" min-width="160" />
            <el-table-column label="维度" width="160">
              <template #default="scope">
                {{ formatStatReportItemDimension(scope.row.itemDimension, scope.row) }}
              </template>
            </el-table-column>
            <el-table-column label="数量" prop="metricCount" width="90" />
            <el-table-column label="数值" prop="metricValue" width="120" />
            <el-table-column label="比率" width="100">
              <template #default="scope">
                {{ formatMetricRate(scope.row.metricRate, reportDetail.reportCode) }}
              </template>
            </el-table-column>
          </el-table>
        </div>
      </template>
    </page-detail-dialog>
  </div>
</template>

<script setup name="YgbStatReport">
import { computed, getCurrentInstance, ref, watchEffect } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useWorkbenchAssist } from '@/composables/useWorkbenchAssist'
import useUserStore from '@/store/modules/user'
import { useAuthorizedRegionOptions } from '@/utils/regionScope'
import {
  decoratePortalExplanationItems,
  openPortalExplanationAction,
  resolvePortalExplanationSummary
} from '@/utils/portalExplanation'
import { useRoleViewMode } from '@/utils/roleView'
import { applyWorkbenchRouteQuery, buildWorkbenchContext, stripWorkbenchRouteQuery } from '@/utils/workbenchLink'
import {
  formatMetricRate,
  formatStatReportItemCategory,
  formatStatReportItemDimension,
  statReportRegionNameMap as regionNameMap,
  statReportRegionOptions as allRegionOptions,
  statReportStatusOptions as statusOptions,
  statReportTypeOptions as reportTypeOptions,
  statusLabel,
  useStatReportPage,
  valueOrDefault
} from '@/views/statReport/useStatReportPage'
import { resolveStatReportTypeKeyByReportCode } from '@/views/statReport/reportConfigs'

const { proxy } = getCurrentInstance()
const { setPageGuide } = useWorkbenchAssist()
const userStore = useUserStore()
const regionOptions = useAuthorizedRegionOptions(allRegionOptions)
const route = useRoute()
const router = useRouter()
const { isReadOnlyRole, readOnlyRoleLabel, readOnlyRoleDescription } = useRoleViewMode()
const activeFocusKey = ref('')
const statReportWorkbenchFields = ['regionCode']

function hasPermissionPrefix(permissions, prefixes) {
  return Array.isArray(permissions) && permissions.some(permission => permission === '*:*:*' || prefixes.some(prefix => permission.startsWith(prefix)))
}

function hasExactPermission(permissions, permission) {
  return Array.isArray(permissions) && permissions.some(item => item === '*:*:*' || item === permission)
}

function isFinanceView(roles, permissions) {
  if (roles.includes('ygb_hrss_supervisor') || roles.includes('ygb_enterprise_operator')) {
    return false
  }
  const financePrefixes = ['ygb:salaryBatch', 'ygb:salaryDetail', 'ygb:socialPayment', 'ygb:socialBaseCompare', 'ygb:taxCompare']
  return hasPermissionPrefix(permissions, financePrefixes)
}

function summaryCard(key, label, value, unit, note, cardClass = '') {
  return { key, label, value, unit, note, cardClass }
}

function focusQueue(key, title, count, unit, desc, actionText) {
  return { key, title, count, unit, desc, actionText }
}

function recommendationCard(code, label, tag, desc, hint) {
  return { code, label, tag, desc, hint }
}

function countRows(predicate) {
  return reportList.value.filter(predicate).length
}

function reportTypeLabel(reportCode) {
  return reportTypeOptions.find(item => item.value === reportCode)?.label || reportCode || '-'
}

function resolveKnownStatReportTypeKey(reportCode) {
  if (!reportTypeOptions.some(item => item.value === reportCode)) {
    return ''
  }
  return resolveStatReportTypeKeyByReportCode(reportCode)
}

function hasStatReportPermission(reportCode, action) {
  const typeKey = resolveKnownStatReportTypeKey(reportCode)
  if (!typeKey) {
    return false
  }
  return hasExactPermission(userStore.permissions || [], `ygb:statReport:${typeKey}:${action}`)
}

function matchReportFocus(row, focusKey) {
  if (!row || !focusKey) {
    return false
  }
  if (focusKey === 'draft') return String(row.reportStatus || '') === '0'
  if (focusKey === 'generated') return ['1', '2'].includes(String(row.reportStatus || ''))
  if (focusKey === 'warning') return row.reportCode === 'WARNING_OVERVIEW'
  if (focusKey === 'injury') return row.reportCode === 'INJURY_RATE'
  if (focusKey === 'salary') return row.reportCode === 'SALARY_PAYMENT'
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

const roleView = computed(() => {
  const roles = userStore.roles || []
  const permissions = userStore.permissions || []
  if (roles.includes('ygb_hrss_supervisor')) {
    return 'hrss'
  }
  if (roles.includes('ygb_enterprise_operator')) {
    return 'operator'
  }
  if (isFinanceView(roles, permissions)) {
    return 'finance'
  }
  if (roles.includes('ygb_enterprise_admin')) {
    return 'admin'
  }
  return 'default'
})

const authorizedQueryReportTypeOptions = computed(() => reportTypeOptions.filter(item => hasStatReportPermission(item.value, 'query')))
const authorizedGenerateReportTypeOptions = computed(() => reportTypeOptions.filter(item => hasStatReportPermission(item.value, 'generate')))
const authorizedExportReportTypeOptions = computed(() => reportTypeOptions.filter(item => hasStatReportPermission(item.value, 'export')))
const canGenerate = computed(() => !isReadOnlyRole.value && authorizedGenerateReportTypeOptions.value.length > 0)
const canExportCurrentReport = computed(() => hasStatReportPermission(queryParams.value.reportCode, 'export'))

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
  getList: pageGetList,
  handleRowClick,
  handleQuery: pageHandleQuery,
  handleExport: pageHandleExport,
  openGenerateDialog,
  submitGenerate,
  openDetail,
  syncCurrentReport
} = useStatReportPage({
  exportFilePrefix: 'ygb_stat_report',
  defaultReportCode: 'SALARY_PAYMENT',
  canMutate: () => !isReadOnlyRole.value,
  onBlockedAction: actionLabel => {
    proxy?.$modal?.msgWarning?.(`${readOnlyRoleLabel.value}仅保留报表查看、详情和导出，不能${actionLabel}`)
  },
  getCurrentList: () => visibleReportList.value,
  afterList: () => {
    syncActiveFocus()
  },
  immediate: false
})

function firstAuthorizedReportCode(action) {
  const options = action === 'generate'
    ? authorizedGenerateReportTypeOptions.value
    : action === 'export'
      ? authorizedExportReportTypeOptions.value
      : authorizedQueryReportTypeOptions.value
  return options[0]?.value || ''
}

function resolveAuthorizedReportCode(action, reportCode = queryParams.value.reportCode) {
  if (reportCode && hasStatReportPermission(reportCode, action)) {
    return reportCode
  }
  return firstAuthorizedReportCode(action)
}

function ensureAuthorizedQueryReportCode() {
  const nextReportCode = resolveAuthorizedReportCode('query')
  if (!nextReportCode) {
    proxy?.$modal?.msgWarning?.('当前账号没有统计报表查询权限')
    reportList.value = []
    total.value = 0
    currentReport.value = undefined
    return false
  }
  if (queryParams.value.reportCode !== nextReportCode) {
    queryParams.value.reportCode = nextReportCode
  }
  return true
}

function guardedGetList() {
  if (!ensureAuthorizedQueryReportCode()) {
    return Promise.resolve()
  }
  return pageGetList()
}

function handleQuery() {
  if (!ensureAuthorizedQueryReportCode()) {
    return
  }
  pageHandleQuery()
}

function reportActionLabel(reportCode, action) {
  return `${reportTypeLabel(reportCode)}${action === 'generate' ? '生成' : action === 'export' ? '导出' : '查询'}`
}

function canGenerateReport(reportCode) {
  return !isReadOnlyRole.value && hasStatReportPermission(reportCode, 'generate')
}

function guardedOpenGenerateDialog(payload) {
  const nextReportCode = payload?.reportCode
    ? payload.reportCode
    : resolveAuthorizedReportCode('generate', queryParams.value.reportCode)
  if (!nextReportCode || !canGenerateReport(nextReportCode)) {
    proxy?.$modal?.msgWarning?.(`当前账号没有${reportActionLabel(nextReportCode, 'generate')}权限`)
    return
  }
  openGenerateDialog({
    ...(payload || {}),
    reportCode: nextReportCode
  })
}

function guardedSubmitGenerate() {
  if (!canGenerateReport(generateForm.value.reportCode)) {
    proxy?.$modal?.msgWarning?.(`当前账号没有${reportActionLabel(generateForm.value.reportCode, 'generate')}权限`)
    return
  }
  submitGenerate()
}

function handleExport() {
  if (!hasStatReportPermission(queryParams.value.reportCode, 'export')) {
    proxy?.$modal?.msgWarning?.(`当前账号没有${reportActionLabel(queryParams.value.reportCode, 'export')}权限`)
    return
  }
  pageHandleExport()
}

const portalExplanations = computed(() => summaryData.value.ygbExplanation || [])
const portalExplanationItems = computed(() => decoratePortalExplanationItems(portalExplanations.value, {
  portalCode: 'ygb',
  panelTitle: '530.1 办理链解释',
  panelDescription: '首页摘要、办理月报和归档解释统一来自门户解释聚合接口。'
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

function syncActiveFocus() {
  if (!activeFocusKey.value || !focusQueues.value.some(item => item.key === activeFocusKey.value)) {
    activeFocusKey.value = focusQueues.value[0]?.key || ''
  }
}

function handleSelectFocus(item) {
  activeFocusKey.value = item.key
  syncCurrentReport()
}

const workbenchContext = computed(() => buildWorkbenchContext(route.query, {
  fields: statReportWorkbenchFields,
  sourceLabel: '企业工作台',
  title: '当前统计报表台账沿用了工作台来源条件',
  description: '当前列表已按行政区划预设筛选，可以直接继续月报生成、回查和归档复核。',
  fieldLabels: {
    regionCode: '行政区划'
  },
  fieldFormatters: {
    regionCode: value => regionNameMap[value] || value
  }
}))

function buildReportHintTags(report, focus, view) {
  if (!report) {
    if (focus?.title) {
      return [
        { label: `当前焦点：${focus.title}`, type: 'info' },
        { label: `建议动作：${focus.actionText}`, type: 'warning' }
      ]
    }
    return [{ label: '请选择报表查看归档建议', type: 'info' }]
  }

  const tags = []
  if (String(report.reportStatus || '') === '0') {
    tags.push({ label: '当前报表仍为草稿，建议优先补齐生成', type: 'warning' })
  }
  if (String(report.reportStatus || '') === '1') {
    tags.push({ label: '当前报表已生成，可优先查看明细和导出归档', type: 'success' })
  }
  if (String(report.reportStatus || '') === '2') {
    tags.push({ label: '当前报表已归档，建议继续复核摘要、附件和明细完整度', type: 'success' })
  }
  if (report.reportCode === 'SALARY_PAYMENT') {
    tags.push({ label: '工资发放月报，适合优先承接财务闭环和到账复核', type: 'info' })
  }
  if (report.reportCode === 'SOCIAL_TAX') {
    tags.push({ label: '社保税务联动月报，适合优先回查社保、税务和合规异常', type: 'warning' })
  }
  if (report.reportCode === 'WARNING_OVERVIEW') {
    tags.push({ label: '预警闭环月报，适合优先承接异常闭环和工单复核', type: 'danger' })
  }
  if (report.reportCode === 'INJURY_RATE') {
    tags.push({ label: '工伤发生率月报，适合继续跟踪参保样本和事故风险', type: 'info' })
  }
  if (view === 'finance') {
    tags.push({ label: '当前视角优先承接工资与社保税务月报，再决定是否归档', type: 'info' })
  }
  if (view === 'hrss') {
    tags.push({ label: '当前视角优先承接预警闭环、工伤月报和月报归档复核', type: 'info' })
  }
  if (view === 'operator') {
    tags.push({ label: '当前视角优先补草稿、补说明和补留痕，再交由管理员归档', type: 'info' })
  }
  return tags
}

const roleBadge = computed(() => {
  if (roleView.value === 'hrss') return '人社经办视角'
  if (roleView.value === 'operator') return '企业经办视角'
  if (roleView.value === 'finance') return '财务经办视角'
  if (roleView.value === 'admin') return '企业管理员视角'
  return '综合办理视角'
})

const roleTitle = computed(() => {
  if (roleView.value === 'hrss') return '区域月报复核与归档工作台'
  if (roleView.value === 'operator') return '企业月报补齐与回写台账'
  if (roleView.value === 'finance') return '财务月报回查与归档台账'
  if (roleView.value === 'admin') return '企业月报闭环与归档工作台'
  return '办理闭环月报与归档工作台'
})

const roleDescription = computed(() => {
  if (roleView.value === 'hrss') {
    return '面向人社经办统一查看月度统计报表结果，重点是把预警闭环、工伤发生率和草稿积压对象串回区域复核、月报归档和办理闭环。'
  }
  if (roleView.value === 'operator') {
    return '面向企业经办统一跟进月报结果，重点是先识别需要补生成、补说明和补留痕的对象，再承接后续归档动作。'
  }
  if (roleView.value === 'finance') {
    return '面向财务经办统一查看月度统计报表结果，重点是把工资发放、社保税务联动月报直接回落到财务对账和整改链路。'
  }
  if (roleView.value === 'admin') {
    return '面向企业管理员统一查看月度统计报表结果，重点是把草稿、联动异常和预警工伤类月报直接串回企业办理与归档闭环。'
  }
  return '面向企业管理员、财务和经办角色统一查看月度统计报表结果。'
})

const roleTip = computed(() => {
  if (roleView.value === 'hrss') {
    return '先锁定预警闭环、工伤发生率和草稿积压对象，再决定是否进入月报归档和区域统计。'
  }
  if (roleView.value === 'operator') {
    return '优先补齐草稿、补异常说明和补业务留痕，不让月报页停留在只读结果。'
  }
  if (roleView.value === 'finance') {
    return '优先回查工资和社保税务联动月报，减少财务在月报、工资和社税台账之间来回切换。'
  }
  if (roleView.value === 'admin') {
    return '优先统筹会阻断企业归档闭环的关键月报对象，再决定是否重生成和归档。'
  }
  return '生成结果会返回报表附件和结构化明细，可直接衔接 PDF、OSS 和归档链路。'
})

const summaryCards = computed(() => {
  if (roleView.value === 'finance') {
    return [
      summaryCard('salary', '工资月报', countRows(row => matchReportFocus(row, 'salary')), '份', '优先承接工资发放、到账状态和财务闭环。', 'ygb-summary-card--primary'),
      summaryCard('socialTax', '联动月报', countRows(row => matchReportFocus(row, 'socialTax')), '份', '适合作为社保、税务和合规异常整改入口。', 'ygb-summary-card--warning'),
      summaryCard('draft', '草稿待补齐', valueOrDefault(summaryData.value.draftCount, 0), '份', '仍需补生成或补说明的月报数量。', 'ygb-summary-card--warning'),
      summaryCard('generated', '已生成', valueOrDefault(summaryData.value.generatedCount, 0), '份', '当前已可查看和导出的财务相关月报。', 'ygb-summary-card--success')
    ]
  }
  if (roleView.value === 'hrss') {
    return [
      summaryCard('warning', '预警月报', valueOrDefault(summaryData.value.warningOverviewCount, 0), '份', '需要优先承接异常闭环和工单复核。', 'ygb-summary-card--warning'),
      summaryCard('injury', '工伤月报', valueOrDefault(summaryData.value.injuryRateCount, 0), '份', '适合作为区域尾部风险与月报归档入口。'),
      summaryCard('draft', '草稿待补齐', valueOrDefault(summaryData.value.draftCount, 0), '份', '越多越说明区域归档链路存在断点。', 'ygb-summary-card--warning'),
      summaryCard('generated', '已生成', valueOrDefault(summaryData.value.generatedCount, 0), '份', '用于对照当前区域月报归档完成度。', 'ygb-summary-card--primary')
    ]
  }
  if (roleView.value === 'operator') {
    return [
      summaryCard('draft', '草稿待补齐', valueOrDefault(summaryData.value.draftCount, 0), '份', '企业经办应优先处理仍未成稿的月报对象。', 'ygb-summary-card--warning'),
      summaryCard('generated', '已生成', valueOrDefault(summaryData.value.generatedCount, 0), '份', '已可回写和归档确认的月报数量。', 'ygb-summary-card--success'),
      summaryCard('salary', '工资月报', countRows(row => matchReportFocus(row, 'salary')), '份', '适合作为补录后确认财务链路是否恢复的样本。'),
      summaryCard('socialTax', '联动月报', countRows(row => matchReportFocus(row, 'socialTax')), '份', '便于核对社保、税务和联动异常是否已回写。', 'ygb-summary-card--primary')
    ]
  }
  if (roleView.value === 'admin') {
    return [
      summaryCard('total', '报表总量', valueOrDefault(summaryData.value.totalCount, total.value), '份', '当前筛选条件下可见的统计报表总量。'),
      summaryCard('draft', '草稿待补齐', valueOrDefault(summaryData.value.draftCount, 0), '份', '建议优先处理会阻断企业归档闭环的草稿对象。', 'ygb-summary-card--warning'),
      summaryCard('warning', '预警月报', valueOrDefault(summaryData.value.warningOverviewCount, 0), '份', '适合作为企业联动整改和归档复核入口。'),
      summaryCard('generated', '已生成', valueOrDefault(summaryData.value.generatedCount, 0), '份', '可作为月报归档和企业规范样本的候选对象。', 'ygb-summary-card--success')
    ]
  }
  return [
    summaryCard('total', '报表总量', valueOrDefault(summaryData.value.totalCount, total.value), '份', '当前筛选条件下可见的统计报表总量。'),
    summaryCard('generated', '已生成', valueOrDefault(summaryData.value.generatedCount, 0), '份', '已生成且可查看详情的报表数量。', 'ygb-summary-card--success'),
    summaryCard('draft', '草稿待补齐', valueOrDefault(summaryData.value.draftCount, 0), '份', '仍需补生成或复核的报表数量。', 'ygb-summary-card--warning'),
    summaryCard('socialTax', '联动月报', valueOrDefault(summaryData.value.socialTaxCount, 0), '份', '社保、税务、用工联动类月报数量。', 'ygb-summary-card--primary')
  ]
})

const focusQueues = computed(() => {
  if (roleView.value === 'finance') {
    return [
      focusQueue('salary', '工资发放月报', countRows(row => matchReportFocus(row, 'salary')), '份', '先回查工资批次、发放结果和到账状态，再决定是否归档。', '回看财务月报'),
      focusQueue('socialTax', '社保税务联动月报', countRows(row => matchReportFocus(row, 'socialTax')), '份', '优先承接社保、税务和合规异常整改对象。', '回看联动月报'),
      focusQueue('draft', '草稿待补齐', valueOrDefault(summaryData.value.draftCount, 0), '份', '适合先补结构化产出和说明材料。', '补齐草稿')
    ]
  }
  if (roleView.value === 'hrss') {
    return [
      focusQueue('warning', '预警闭环月报', valueOrDefault(summaryData.value.warningOverviewCount, 0), '份', '优先承接最需要复核闭环的预警月报。', '进入闭环复核'),
      focusQueue('injury', '工伤发生率月报', valueOrDefault(summaryData.value.injuryRateCount, 0), '份', '适合逐项复核参保样本和事故风险。', '查看工伤月报'),
      focusQueue('draft', '草稿待补齐', valueOrDefault(summaryData.value.draftCount, 0), '份', '先补齐草稿，再决定是否进入区域统计归档。', '查看草稿对象')
    ]
  }
  if (roleView.value === 'operator') {
    return [
      focusQueue('draft', '草稿待补齐', valueOrDefault(summaryData.value.draftCount, 0), '份', '优先补生成、补说明和补留痕。', '补草稿对象'),
      focusQueue('generated', '已生成月报', valueOrDefault(summaryData.value.generatedCount, 0), '份', '适合补回写记录后做归档确认。', '查看已出稿对象'),
      focusQueue('salary', '工资发放月报', countRows(row => matchReportFocus(row, 'salary')), '份', '可作为补录完成后的财务链路确认样本。', '查看工资月报')
    ]
  }
  if (roleView.value === 'admin') {
    return [
      focusQueue('draft', '草稿待补齐', valueOrDefault(summaryData.value.draftCount, 0), '份', '先锁定真正拖慢企业归档闭环的草稿对象。', '进入补齐链路'),
      focusQueue('warning', '预警闭环月报', valueOrDefault(summaryData.value.warningOverviewCount, 0), '份', '优先统筹企业联动整改和月报归档的关键月报。', '回看闭环月报'),
      focusQueue('salary', '工资发放月报', countRows(row => matchReportFocus(row, 'salary')), '份', '适合作为企业财务闭环和月报归档入口。', '复核财务月报')
    ]
  }
  return [
    focusQueue('draft', '草稿待补齐', valueOrDefault(summaryData.value.draftCount, 0), '份', '优先补齐最影响归档链路的月报对象。', '补齐草稿'),
    focusQueue('warning', '预警闭环月报', valueOrDefault(summaryData.value.warningOverviewCount, 0), '份', '适合作为异常闭环和工单复核入口。', '查看闭环月报'),
    focusQueue('socialTax', '社保税务联动月报', valueOrDefault(summaryData.value.socialTaxCount, 0), '份', '提前处理联动异常对象，避免继续堆积到下月。', '查看联动月报')
  ]
})

const activeFocus = computed(() => {
  if (!focusQueues.value.length) return null
  return focusQueues.value.find(item => item.key === activeFocusKey.value) || focusQueues.value[0]
})

const visibleReportList = computed(() => prioritizeFocusRows(reportList.value, row => matchReportFocus(row, activeFocus.value?.key)))

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
      { label: '当前月份', value: queryParams.value.statMonth || '全部月份' },
      { label: '当前区域', value: regionNameMap[queryParams.value.regionCode] || queryParams.value.regionCode || '广东省' }
    ]
  }
  return [
    { label: '报表名称', value: currentReport.value.reportName || '-' },
    { label: '统计月份', value: currentReport.value.statMonth || '-' },
    { label: '报表状态', value: statusLabel(currentReport.value.reportStatus) },
    { label: '核心产出', value: `${currentReport.value.metricAmount || 0} / ${formatMetricRate(currentReport.value.metricRate, currentReport.value.reportCode)}` }
  ]
})

const primaryReportAction = computed(() => {
  if (!currentReport.value) {
    return { label: activeFocus.value?.actionText || '查看详情', action: 'detail' }
  }
  if (canGenerateReport(currentReport.value.reportCode) && String(currentReport.value.reportStatus || '') === '0') {
    return { label: '按当前报表重生成', action: 'generate' }
  }
  return { label: '查看详情', action: 'detail' }
})

const currentReportActionSummary = computed(() => {
  return resolvePortalExplanationSummary(
    portalExplanationItems.value,
    currentReport.value
      ? '当前统计报表的办理月报、归档进度和整改闭环结果已按 530.1 办理链口径展示。'
      : '当前暂无统计报表，可继续通过 530.1 办理链解释查看本月主办理方向。'
  )
})

const currentReportActionTags = computed(() => ([
  ...buildAggregateHintTags(portalExplanationItems.value, portalExplanationSummary.value),
  ...buildReportHintTags(currentReport.value, activeFocus.value, roleView.value)
]).slice(0, 5))

const workflowSteps = computed(() => {
  const aggregateSteps = buildAggregateWorkflowSteps(portalExplanationItems.value)
  if (aggregateSteps.length) {
    return aggregateSteps
  }

  if (roleView.value === 'finance') {
    return [
      { label: '先看工资月报', desc: '优先锁定工资发放月报、草稿和联动类月报，快速判断财务链路卡点。' },
      { label: '再承接联动整改', desc: '把社保、税务和合规异常对象接回财务整改台账，避免月报结果停留在汇总层。' },
      { label: '最后回到归档复核', desc: '在整改留痕补齐后复核月报，并把结果沉淀到企业月报归档。' }
    ]
  }
  if (roleView.value === 'hrss') {
    return [
      { label: '先锁定重点月报', desc: '先看预警闭环、工伤发生率和草稿待补齐对象，快速判断当前区域办理压力。' },
      { label: '再回看明细与闭环', desc: '围绕结构化明细、附件和风险摘要判断是实际风险还是数据缺口。' },
      { label: '最后完成归档复核', desc: '把月报结果回落到区域统计、经办底稿和后续联动处置。' }
    ]
  }
  if (roleView.value === 'operator') {
    return [
      { label: '先补草稿生成', desc: '先把草稿对象对应的生成、截图和说明材料补齐。' },
      { label: '再回写业务留痕', desc: '把回查结论回写到企业台账，避免月报和原业务链路脱节。' },
      { label: '最后配合归档', desc: '在说明和留痕补齐后，再配合管理员或财务执行归档确认。' }
    ]
  }
  if (roleView.value === 'admin') {
    return [
      { label: '先判断关键卡点', desc: '先通过草稿、联动月报和预警闭环月报判断卡点在工资、社税、预警还是工伤链路。' },
      { label: '承接企业整改主链', desc: '优先回到工资、社保税务、预警和工伤主链处理关键短板。' },
      { label: '执行月报复核', desc: '在整改留痕补齐后重审月报结果，判断企业归档是否真正闭环。' },
      { label: '完成月度归档', desc: '把月报结果纳入企业归档、经办底稿和后续联动。' }
    ]
  }
  return [
    { label: '汇聚底数', desc: '先汇聚当月工资、社保税务、预警闭环和工伤底数，确保月报口径与办理链路一致。' },
    { label: '生成月报', desc: '按统一口径生成结构化结果、附件和明细，形成企业月度报表台账。' },
    { label: '核查异常', desc: '对草稿、联动异常和预警工伤类月报回查原始业务数据，确认是实际风险还是数据缺口。' },
    { label: '归档联动', desc: '将月报结果用于企业归档、异常联动和区域统计，并沉淀为可复核台账。' }
  ]
})

const reportRecommendations = computed(() => {
  if (roleView.value === 'finance') {
    return [
      recommendationCard('SALARY_PAYMENT', '工资发放月报', '财务闭环', '先看工资批次、发放结果和到账状态，适合财务经办优先复核。', `当前 ${countRows(row => matchReportFocus(row, 'salary'))} 份`),
      recommendationCard('SOCIAL_TAX', '社保税务联动月报', '联动异常', '集中汇总社保、税务和合规异常对象，适合财务整改承接。', `当前 ${summaryData.value.socialTaxCount ?? 0} 份`),
      recommendationCard('WARNING_OVERVIEW', '预警闭环月报', '联动复核', '适合作为财务与异常闭环联动复核的补充入口。', `当前 ${summaryData.value.warningOverviewCount ?? 0} 份`),
      recommendationCard('INJURY_RATE', '工伤发生率月报', '归档参考', '用于补充查看工伤风险对月度归档的影响。', `当前 ${summaryData.value.injuryRateCount ?? 0} 份`)
    ]
  }
  if (roleView.value === 'hrss') {
    return [
      recommendationCard('WARNING_OVERVIEW', '预警闭环月报', '闭环复核', '适合经办侧复核本月预警处理、闭环时效和重点风险来源。', `当前 ${summaryData.value.warningOverviewCount ?? 0} 份`),
      recommendationCard('INJURY_RATE', '工伤发生率月报', '工伤归档', '用于经办侧复核参保样本、工伤发生率和区划对比结果。', `当前 ${summaryData.value.injuryRateCount ?? 0} 份`),
      recommendationCard('SOCIAL_TAX', '社保税务联动月报', '联动异常', '集中汇总社保、税务和合规异常对象，便于跨模块联动复核。', `当前 ${summaryData.value.socialTaxCount ?? 0} 份`),
      recommendationCard('SALARY_PAYMENT', '工资发放月报', '财务参考', '作为区域财务闭环和归档稳定度的参考样本。', `当前 ${countRows(row => matchReportFocus(row, 'salary'))} 份`)
    ]
  }
  if (roleView.value === 'operator') {
    return [
      recommendationCard('SALARY_PAYMENT', '工资发放月报', '先补草稿', '适合先补工资月报结构化产出，再回写业务留痕。', `当前 ${countRows(row => matchReportFocus(row, 'salary'))} 份`),
      recommendationCard('SOCIAL_TAX', '社保税务联动月报', '补说明', '便于集中补齐联动异常说明和回查截图。', `当前 ${summaryData.value.socialTaxCount ?? 0} 份`),
      recommendationCard('WARNING_OVERVIEW', '预警闭环月报', '补留痕', '适合作为预警处置留痕和工单说明补录入口。', `当前 ${summaryData.value.warningOverviewCount ?? 0} 份`),
      recommendationCard('INJURY_RATE', '工伤发生率月报', '归档配合', '用于补足工伤侧说明材料，配合后续归档。', `当前 ${summaryData.value.injuryRateCount ?? 0} 份`)
    ]
  }
  return [
    recommendationCard('SALARY_PAYMENT', '工资发放月报', '财务闭环', '先看工资批次、发放结果和到账状态，适合企业管理层和财务经办归档。', `当前 ${countRows(row => matchReportFocus(row, 'salary'))} 份`),
    recommendationCard('SOCIAL_TAX', '社保税务联动月报', '联动异常', '集中汇总社保、税务和合规异常对象，适合作为企业联动整改入口。', `当前 ${summaryData.value.socialTaxCount ?? 0} 份`),
    recommendationCard('WARNING_OVERVIEW', '预警闭环月报', '闭环复核', '适合管理层复核本月预警处理、闭环时效和重点风险来源。', `当前 ${summaryData.value.warningOverviewCount ?? 0} 份`),
    recommendationCard('INJURY_RATE', '工伤发生率月报', '工伤归档', '用于复核参保样本、工伤发生率和区划对比结果。', `当前 ${summaryData.value.injuryRateCount ?? 0} 份`)
  ]
})
watchEffect(() => {
  setPageGuide({
    title: roleTitle.value || '统计报表办理台账',
    description: roleDescription.value || '统一查看当前页的门户解释、焦点对象、当前选中、办理路径与推荐入口。',
    portalExplanation: portalExplanationItems.value,
    focus: focusQueues.value,
    selection: [
      ...selectedReportOverview.value,
      { label: '当前办理建议', value: currentReportActionSummary.value }
    ],
    workflow: workflowSteps.value,
    hints: currentReportActionTags.value
  })
})

const detailHintTags = computed(() => buildReportHintTags(reportDetail.value || currentReport.value, activeFocus.value, roleView.value))

const detailFocusText = computed(() => {
  if (roleView.value === 'finance') {
    return '重点看工资、社保税务和附件结构是否支撑财务归档，再回到整改链路处理。'
  }
  if (roleView.value === 'hrss') {
    return '重点看预警闭环、工伤月报和草稿补齐是否集中，再决定是否进入区域复核和统计归档。'
  }
  if (roleView.value === 'operator') {
    return '重点看需要补生成、补说明和补留痕的对象，避免月报结果与企业台账脱节。'
  }
  if (roleView.value === 'admin') {
    return '重点看哪些月报对象仍阻断企业归档闭环，再把整改动作回落到工资、社税、预警和工伤台账。'
  }
  return '重点看草稿状态、月报类型、摘要和附件是否需要继续回查业务主链。'
})

function handlePrimaryReportAction() {
  if (!currentReport.value) {
    return
  }
  if (primaryReportAction.value.action === 'generate') {
    guardedOpenGenerateDialog(currentReport.value)
    return
  }
  openDetail(currentReport.value)
}

const readOnlyAlertTitle = computed(() => `${readOnlyRoleLabel.value}：当前仅保留报表查看、详情和导出`)
const readOnlyAlertDescription = computed(() => `${readOnlyRoleDescription.value || ''} 当前页面聚焦月报摘要、详情复核和导出，不展示生成与重生成动作。`.trim())

function resetQuery() {
  Object.assign(queryParams.value, {
    pageNum: 1,
    pageSize: 10,
    regionCode: undefined,
    statMonth: undefined,
    reportCode: undefined,
    reportStatus: undefined
  })
  applyWorkbenchRouteQuery(route.query, queryParams.value, statReportWorkbenchFields)
  guardedGetList()
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
  guardedGetList()
}

applyWorkbenchRouteQuery(route.query, queryParams.value, statReportWorkbenchFields)
guardedGetList()
</script>

<style scoped lang="scss">
.ygb-workbench-alert {
  margin-bottom: 16px;
}

.ygb-workbench-alert__title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.ygb-workbench-alert__desc {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 10px;
  color: #4f6478;
  line-height: 1.6;
}

.ygb-summary-grid,
.ygb-focus-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
  margin-bottom: 16px;
}

.ygb-summary-grid {
  grid-template-columns: repeat(4, minmax(0, 1fr));
}

.ygb-summary-card,
.ygb-focus-card {
  border: 1px solid #dbe5f0;
  border-radius: 16px;
  background: #fff;
}

.ygb-summary-card {
  padding: 18px 20px;
}

.ygb-summary-card__label {
  color: #627486;
  font-size: 13px;
}

.ygb-summary-card__value {
  margin-top: 10px;
  color: #13243a;
  font-size: 28px;
  font-weight: 700;
}

.ygb-summary-card__unit {
  margin-left: 4px;
  font-size: 13px;
  font-weight: 500;
  color: #7b8da1;
}

.ygb-summary-card__note {
  margin-top: 10px;
  color: #5f6f80;
  line-height: 1.7;
  font-size: 13px;
}

.ygb-summary-card--success {
  background: linear-gradient(180deg, #ffffff 0%, #f3fbf5 100%);
}

.ygb-summary-card--warning {
  background: linear-gradient(180deg, #ffffff 0%, #fff9ef 100%);
}

.ygb-summary-card--primary {
  background: linear-gradient(180deg, #ffffff 0%, #f2f7fd 100%);
}

.ygb-focus-card :deep(.el-card__header) {
  padding: 18px 20px 0;
  border-bottom: none;
}

.ygb-focus-card :deep(.el-card__body) {
  padding: 18px 20px 20px;
}

.ygb-card-head__title {
  color: #13243a;
  font-size: 18px;
  font-weight: 700;
}

.ygb-card-head__desc {
  margin-top: 4px;
  color: #7b8da1;
  font-size: 13px;
  line-height: 1.7;
}

.ygb-card-head--between {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: flex-start;
}

.ygb-focus-list--single,
.ygb-pipeline-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.ygb-focus-queue {
  width: 100%;
  display: flex;
  justify-content: space-between;
  gap: 16px;
  padding: 14px 16px;
  border: 1px solid #dde7f1;
  border-radius: 14px;
  background: #f8fbfd;
  text-align: left;
  cursor: pointer;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.ygb-focus-queue:hover,
.ygb-focus-queue.is-active {
  border-color: #0f5ea8;
  box-shadow: 0 10px 24px rgba(15, 94, 168, 0.08);
}

.ygb-focus-queue__main strong {
  display: block;
  margin-bottom: 6px;
  color: #13243a;
}

.ygb-focus-queue__main p {
  margin: 0;
  font-size: 13px;
  line-height: 1.7;
  color: #5f6f80;
}

.ygb-focus-queue__side {
  min-width: 120px;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  justify-content: center;
  gap: 8px;
}

.ygb-focus-queue__count {
  font-size: 20px;
  font-weight: 700;
  color: #0f5ea8;
}

.ygb-focus-queue__action {
  font-size: 12px;
  color: #1d4ed8;
}

.ygb-source-list {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.ygb-source-item,
.ygb-pipeline-item {
  border: 1px solid #dde7f1;
  border-radius: 14px;
  background: #f8fbfd;
}

.ygb-source-item {
  padding: 14px 16px;
}

.ygb-source-item__label {
  font-size: 12px;
  color: #7b8da1;
}

.ygb-source-item__value {
  margin-top: 8px;
  color: #13243a;
  font-size: 16px;
  font-weight: 600;
}

.ygb-recommend-panel {
  margin-top: 16px;
  padding: 16px;
  border-radius: 14px;
  background: linear-gradient(135deg, rgba(15, 94, 168, 0.08), rgba(29, 78, 216, 0.05));
  border: 1px solid rgba(15, 94, 168, 0.15);
}

.ygb-recommend-panel__title {
  color: #0f5ea8;
  font-size: 13px;
  font-weight: 600;
}

.ygb-recommend-panel__summary {
  margin: 8px 0 12px;
  color: #1f2937;
  font-size: 14px;
  line-height: 1.7;
}

.ygb-focus-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 16px;
}

.ygb-recommend-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.ygb-recommend-card {
  padding: 16px;
  border: 1px solid #dde7f1;
  border-radius: 14px;
  background: #f8fbfd;
  text-align: left;
  cursor: pointer;
  transition: border-color 0.2s ease, box-shadow 0.2s ease, transform 0.2s ease;
}

.ygb-recommend-card:hover {
  border-color: #0f5ea8;
  box-shadow: 0 8px 18px rgba(15, 35, 58, 0.06);
  transform: translateY(-1px);
}

.ygb-recommend-card__tag {
  display: inline-flex;
  align-items: center;
  min-height: 24px;
  padding: 0 8px;
  border-radius: 999px;
  background: #edf5fc;
  color: #0f5ea8;
  font-size: 12px;
  font-weight: 600;
}

.ygb-recommend-card__title {
  display: block;
  margin-top: 12px;
  color: #13243a;
}

.ygb-recommend-card__desc,
.ygb-recommend-card__foot {
  display: block;
  margin: 8px 0 0;
  color: #5f6f80;
  font-size: 13px;
  line-height: 1.7;
}

.ygb-pipeline-item {
  display: flex;
  gap: 14px;
  align-items: flex-start;
  padding: 16px;
}

.ygb-pipeline-item__index {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: #0f5ea8;
  color: #fff;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  font-weight: 700;
}

.ygb-pipeline-item__body strong {
  display: block;
  color: #13243a;
}

.ygb-pipeline-item__body p {
  margin: 6px 0 0;
  color: #5f6f80;
  font-size: 13px;
  line-height: 1.7;
}

.ygb-tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.ygb-detail-block,
.ygb-report-items {
  margin-top: 18px;
}

.ygb-detail-block h3,
.ygb-report-items h3 {
  margin: 0 0 12px;
  color: #13243a;
  font-size: 16px;
}

@media (max-width: 1200px) {
  .ygb-summary-grid,
  .ygb-focus-grid,
  .ygb-source-list,
  .ygb-recommend-grid {
    grid-template-columns: 1fr;
  }

  .ygb-card-head--between {
    flex-direction: column;
  }
}
</style>

