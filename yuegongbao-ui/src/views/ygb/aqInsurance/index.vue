<template>
  <div class="app-container ygb-page ygb-aq-insurance-page">
    <section class="gov-page-header ygb-page__header">
      <div>
        <p class="ygb-page__eyebrow">投保协同</p>
        <h1 class="ygb-page__title">投保续保办理台账</h1>
        <p class="ygb-page__desc">
          面向企业管理员、财务经办和业务协同人员，围绕保单续保、预防费留痕和同步回写形成一套长期可跟踪的办理工作台。
        </p>
      </div>
      <div class="ygb-page__tip">
        当前页面围绕保单同步、续保提醒、预防费留痕和赔付跟踪组织办理动作，按办理顺序推进台账闭环。
      </div>
    </section>

    <div class="ygb-summary-grid">
      <div v-for="item in resolvedSummaryCards" :key="item.key" class="ygb-summary-card" :class="item.cardClass">
        <div class="ygb-summary-card__label">{{ item.label }}</div>
        <div class="ygb-summary-card__value">
          {{ item.value }}
          <span class="ygb-summary-card__unit">{{ item.unit }}</span>
        </div>
        <div class="ygb-summary-card__note">{{ item.note }}</div>
      </div>
    </div>


    <el-card class="search-card ygb-search-card" shadow="never">
      <el-form ref="queryRef" :model="queryParams" :inline="true" v-show="showSearch">
        <el-form-item label="统计月份">
          <el-date-picker v-model="queryParams.statMonth" type="month" value-format="YYYY-MM" format="YYYY-MM" style="width: 170px" />
        </el-form-item>
        <el-form-item label="区域">
          <el-select v-model="queryParams.regionCode" clearable style="width: 180px">
            <el-option v-for="item in regionOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="企业">
          <el-select v-model="queryParams.enterpriseId" clearable filterable style="width: 220px">
            <el-option v-for="item in enterpriseOptions" :key="item.enterpriseId" :label="item.enterpriseName" :value="item.enterpriseId" />
          </el-select>
        </el-form-item>
        <el-form-item label="保单状态">
          <el-select v-model="queryParams.policyStatus" clearable style="width: 160px">
            <el-option v-for="item in policyStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="企业名称">
          <el-input v-model="queryParams.enterpriseName" clearable style="width: 220px" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">查询</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="toolbar-card ygb-toolbar-card" shadow="never">
      <el-row :gutter="10">
        <el-col v-if="!isReadOnlyRole" :span="1.5">
          <el-button type="primary" plain icon="RefreshRight" @click="handleSync" v-hasPermi="['ygb:aqInsurance:sync']">同步保单</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['ygb:aqInsurance:export']">导出</el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>
    </el-card>

    <el-card class="table-card ygb-table-card" shadow="never">
      <el-table v-loading="loading" :data="aqInsuranceList" @row-click="handleRowClick">
        <el-table-column label="保单ID" prop="policyId" width="100" />
        <el-table-column label="统计月份" prop="statMonth" width="110" />
        <el-table-column label="企业名称" prop="enterpriseName" min-width="220" />
        <el-table-column label="区域" width="140">
          <template #default="scope">
            {{ formatRegionName(scope.row.regionCode, '-') }}
          </template>
        </el-table-column>
        <el-table-column label="保险机构" prop="insurerName" min-width="180" />
        <el-table-column label="保单号" prop="policyNo" min-width="180" />
        <el-table-column label="保费" prop="premium" width="110" />
        <el-table-column label="参保人数" prop="insuredPersonCount" width="90" />
        <el-table-column label="起止日期" min-width="220">
          <template #default="scope">
            {{ parseTime(scope.row.startDate, '{y}-{m}-{d}') }} 至 {{ parseTime(scope.row.endDate, '{y}-{m}-{d}') }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="120">
          <template #default="scope">
            <dict-tag :options="policyStatusOptions" :value="scope.row.policyStatus" />
          </template>
        </el-table-column>
        <el-table-column label="预防费计提" prop="preventionFundAmount" width="120" />
        <el-table-column label="已使用" prop="usedFundAmount" width="100" />
        <el-table-column label="余额" prop="remainingFundAmount" width="100" />
        <el-table-column label="剩余天数" prop="expireInDays" width="100" />
        <el-table-column class-name="table-fill-column" min-width="1" />

        <el-table-column label="操作" fixed="right" align="center" width="100">
          <template #default="scope">
            <el-button link type="info" icon="View" @click.stop="openDetail(scope.row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
      <pagination
        v-show="total > 0"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        :total="total"
        @pagination="getList"
      />
    </el-card>

    <page-detail-dialog v-model="detailOpen" title="保单详情" width="760px">
      <template v-if="policyDetail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="保单ID">{{ policyDetail.policyId }}</el-descriptions-item>
          <el-descriptions-item label="统计月份">{{ policyDetail.statMonth }}</el-descriptions-item>
          <el-descriptions-item label="企业名称">{{ policyDetail.enterpriseName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="区域">{{ formatRegionName(policyDetail.regionCode, '-') }}</el-descriptions-item>
          <el-descriptions-item label="保险机构">{{ policyDetail.insurerName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="保单号">{{ policyDetail.policyNo || '-' }}</el-descriptions-item>
          <el-descriptions-item label="保单状态">{{ policyStatusLabel(policyDetail.policyStatus) }}</el-descriptions-item>
          <el-descriptions-item label="参保人数">{{ policyDetail.insuredPersonCount || 0 }}</el-descriptions-item>
          <el-descriptions-item label="起保日期">{{ parseTime(policyDetail.startDate, '{y}-{m}-{d}') }}</el-descriptions-item>
          <el-descriptions-item label="止保日期">{{ parseTime(policyDetail.endDate, '{y}-{m}-{d}') }}</el-descriptions-item>
          <el-descriptions-item label="保费">{{ formatDecimal(policyDetail.premium) }}</el-descriptions-item>
          <el-descriptions-item label="剩余天数">{{ valueWithUnit(policyDetail.expireInDays, '天') }}</el-descriptions-item>
          <el-descriptions-item label="预防费比例">{{ formatDecimal(policyDetail.preventionFundRatio) }}</el-descriptions-item>
          <el-descriptions-item label="预防费计提">{{ formatDecimal(policyDetail.preventionFundAmount) }}</el-descriptions-item>
          <el-descriptions-item label="已使用金额">{{ formatDecimal(policyDetail.usedFundAmount) }}</el-descriptions-item>
          <el-descriptions-item label="可用余额">{{ formatDecimal(policyDetail.remainingFundAmount) }}</el-descriptions-item>
          <el-descriptions-item label="来源流水号" :span="2">{{ policyDetail.sourceSerialNo || '-' }}</el-descriptions-item>
          <el-descriptions-item label="回写状态">{{ policyDetail.sourceStatus || '-' }}</el-descriptions-item>
          <el-descriptions-item label="回调时间">{{ formatDateTime(policyDetail.callbackTime) }}</el-descriptions-item>
          <el-descriptions-item label="来源消息" :span="2">{{ policyDetail.sourceMessage || '-' }}</el-descriptions-item>
        </el-descriptions>

        <div class="ygb-detail-block">
          <h3>办理提示</h3>
          <div class="ygb-tag-list">
            <el-tag v-for="item in resolvedDetailHintTags" :key="item.label" :type="item.type" effect="plain">{{ item.label }}</el-tag>
          </div>
        </div>
      </template>
    </page-detail-dialog>
  </div>
</template>

<script setup name="YgbAqInsurance">
import { computed, getCurrentInstance, watchEffect } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useWorkbenchAssist } from '@/composables/useWorkbenchAssist'
import { decoratePortalExplanationItems, openPortalExplanationAction, resolvePortalExplanationSummary } from '@/utils/portalExplanation'
import {
  buildPolicyHintTags,
  formatDecimal,
  formatRegionName,
  policyStatusLabel,
  policyStatusOptions,
  regionOptions as allRegionOptions,
  useAqInsurancePage,
  valueOrDefault,
  valueWithUnit
} from '@/views/aqInsurance/useAqInsurancePage'
import { useRoleViewMode } from '@/utils/roleView'
import { applyWorkbenchRouteQuery, buildWorkbenchContext, stripWorkbenchRouteQuery } from '@/utils/workbenchLink'
import { useAuthorizedRegionOptions } from '@/utils/regionScope'

const { proxy } = getCurrentInstance()
const regionOptions = useAuthorizedRegionOptions(allRegionOptions)
const route = useRoute()
const { setPageGuide } = useWorkbenchAssist()
const router = useRouter()
const { isReadOnlyRole, readOnlyRoleLabel, readOnlyRoleDescription } = useRoleViewMode()
const aqInsuranceWorkbenchFields = ['enterpriseId', 'regionCode', 'enterpriseName', 'policyStatus', 'focusKey']

const workflowSteps = [
  { label: '同步保单', desc: '按月同步企业保单、保费和预防费计提底数，形成统一投保台账。' },
  { label: '核对续保', desc: '识别即将到期和已过期保单，及时确认续保状态与企业覆盖情况。' },
  { label: '跟踪预防费', desc: '核对计提、已使用和余额，确保资金使用留痕和用途说明完整。' },
  { label: '结果归档', desc: '将保单状态、同步结果和预防费情况纳入月度归档与监管联动结论。' }
]

const {
  aqInsuranceList,
  enterpriseOptions,
  loading,
  showSearch,
  total,
  detailOpen,
  currentPolicy,
  policyDetail,
  summaryData,
  queryParams,
  getList,
  loadEnterpriseOptions,
  handleRowClick,
  handleQuery,
  handleSync,
  handleExport,
  openDetail,
  formatDateTime
} = useAqInsurancePage({
  exportFilePrefix: 'ygb_aq_insurance',
  canMutate: () => !isReadOnlyRole.value,
  onBlockedAction: () => {
    proxy.$modal.msgWarning(`${readOnlyRoleLabel.value}：当前页面仅保留台账查看、详情和导出`)
  }
})

function buildAqInsuranceExplanationQuery(extraQuery = {}) {
  return {
    statMonth: queryParams.value.statMonth,
    regionCode: queryParams.value.regionCode,
    enterpriseId: queryParams.value.enterpriseId,
    enterpriseName: queryParams.value.enterpriseName,
    policyStatus: queryParams.value.policyStatus,
    ...extraQuery
  }
}

const summaryCards = computed(() => ([
  {
    key: 'total',
    label: '保单数量',
    value: valueOrDefault(summaryData.value.totalCount, total.value),
    unit: '单',
    note: '当前筛选范围内的投保保单总量。',
    cardClass: ''
  },
  {
    key: 'valid',
    label: '有效保单',
    value: valueOrDefault(summaryData.value.validCount, 0),
    unit: '单',
    note: '可继续作为当月投保覆盖依据的有效保单数量。',
    cardClass: 'ygb-summary-card--success'
  },
  {
    key: 'risk',
    label: '到期风险',
    value: valueOrDefault(summaryData.value.riskCount, 0),
    unit: '单',
    note: '需要优先催办续保或核查覆盖中断的保单数量。',
    cardClass: 'ygb-summary-card--warning'
  },
  {
    key: 'remaining',
    label: '预防费余额',
    value: formatDecimal(summaryData.value.remainingFundAmountTotal),
    unit: '元',
    note: '用于快速判断当前保单池预防费可用余额。',
    cardClass: 'ygb-summary-card--primary'
  }
]))

const focusItems = computed(() => ([
  { label: '当前区域', value: queryParams.value.regionCode ? formatRegionName(queryParams.value.regionCode, '全部区域') : '全部区域' },
  { label: '即将到期', value: valueWithUnit(valueOrDefault(summaryData.value.soonExpireCount, 0), '单') },
  { label: '已过期', value: valueWithUnit(valueOrDefault(summaryData.value.expiredCount, 0), '单') },
  { label: '保险机构数', value: valueWithUnit(valueOrDefault(summaryData.value.insurerCount, 0), '家') }
]))

const portalExplanations = computed(() => {
  if (Array.isArray(summaryData.value.ygbExplanation) && summaryData.value.ygbExplanation.length) {
    return summaryData.value.ygbExplanation
  }
  return [
    {
      key: 'risk',
      dimensionName: '续保风险',
      currentValue: valueOrDefault(summaryData.value.riskCount, 0),
      targetValue: '0',
      summary: '即将到期或已经过期的保单应优先纳入续保办理链，避免覆盖中断。',
      evidenceModule: 'aqInsurance',
      recommendModule: 'aqInsurance',
      defaultQuery: buildAqInsuranceExplanationQuery({ policyStatus: '2', focusKey: 'risk' }),
      sourceLabel: '530.1 投保续保办理解释',
      sourceDescription: '在当前工作台范围内继续核查续保风险保单。',
      actionText: '优先核查续保'
    },
    {
      key: 'valid',
      dimensionName: '有效覆盖',
      currentValue: valueOrDefault(summaryData.value.validCount, 0),
      targetValue: '持续稳定',
      summary: '有效保单是当前投保覆盖依据，应保持可用于归档和下游复核。',
      evidenceModule: 'aqInsurance',
      recommendModule: 'aqInsurance',
      defaultQuery: buildAqInsuranceExplanationQuery({ policyStatus: '1', focusKey: 'valid' }),
      sourceLabel: '530.1 投保续保办理解释',
      sourceDescription: '在当前工作台范围内继续核查有效保单覆盖情况。',
      actionText: '核查有效覆盖'
    },
    {
      key: 'remaining',
      dimensionName: '预防费余额',
      currentValue: formatDecimal(summaryData.value.remainingFundAmountTotal),
      targetValue: '-',
      summary: '月度归档前应保持预防费余额可追溯，避免使用和留痕脱节。',
      evidenceModule: 'aqInsurance',
      recommendModule: 'aqInsurance',
      defaultQuery: buildAqInsuranceExplanationQuery({ focusKey: 'remaining' }),
      sourceLabel: '530.1 投保续保办理解释',
      sourceDescription: '在当前工作台范围内继续核查预防费余额记录。',
      actionText: '核查预防费余额'
    }
  ]
})

const portalExplanationItems = computed(() => decoratePortalExplanationItems(portalExplanations.value, {
  portalCode: 'ygb',
  panelTitle: '530.1 投保续保办理解释',
  panelDescription: '当前解释项按 530.1 办理链口径展示续保风险、有效覆盖和预防费重点。'
}))

const portalExplanationSummary = computed(() => resolvePortalExplanationSummary(portalExplanationItems.value, ''))
const leadingPortalExplanation = computed(() => portalExplanationItems.value[0] || null)

function resolvePortalExplanationItem(index = 0) {
  return portalExplanationItems.value[index] || leadingPortalExplanation.value || null
}

function buildPortalExplanationLabel(item) {
  if (!item) return ''
  const parts = [item.dimensionName, item.moduleLabel || item.moduleCode].filter(Boolean)
  return Array.from(new Set(parts)).join(' / ')
}

function resolveExplanationFirstText(fallback, explanation) {
  const label = buildPortalExplanationLabel(explanation)
  const summary = explanation?.summary || explanation?.explanationSummary || explanation?.sourceDescription || ''
  if (label && summary) {
    return `${label}: ${summary}`
  }
  if (summary) {
    return summary
  }
  if (label) {
    return `${label} first`
  }
  return fallback
}

function buildExplanationFirstTags(tags = []) {
  const extras = []
  const label = buildPortalExplanationLabel(leadingPortalExplanation.value)
  if (label) {
    extras.push({ label: `530.1 explanation: ${label}`, type: 'warning' })
  }
  if (portalExplanationSummary.value) {
    extras.push({ label: portalExplanationSummary.value, type: 'success' })
  }
  return [...extras, ...tags].slice(0, 4)
}

const workbenchContext = computed(() => buildWorkbenchContext(route.query, {
  fields: aqInsuranceWorkbenchFields,
  sourceLabel: '企业工作台',
  title: '当前投保台账沿用了工作台来源条件',
  description: '当前列表已按企业、区域或企业名称预设筛选，可以直接继续续保回查、同步复核和导出。',
  fieldLabels: {
    enterpriseId: '企业',
    regionCode: '行政区划',
    enterpriseName: '企业名称',
    policyStatus: '保单状态',
    focusKey: '解释焦点'
  },
  fieldFormatters: {
    enterpriseId: value => {
      const matched = enterpriseOptions.value.find(item => String(item.enterpriseId) === String(value))
      return matched ? matched.enterpriseName : value
    },
    regionCode: value => formatRegionName(value, value),
    policyStatus: value => policyStatusLabel(value),
    focusKey: value => aqInsuranceFocusLabel(value)
  }
}))

const resolvedSummaryCards = computed(() => summaryCards.value.map((item, index) => ({
  ...item,
  note: resolveExplanationFirstText(item.note, resolvePortalExplanationItem(index))
})))

const resolvedFocusItems = computed(() => focusItems.value.map((item, index) => ({
  ...item,
  tip: resolveExplanationFirstText('', resolvePortalExplanationItem(index))
})))

const resolvedWorkflowSteps = computed(() => workflowSteps.map((item, index) => ({
  ...item,
  desc: resolveExplanationFirstText(item.desc, resolvePortalExplanationItem(index))
})))

const selectedPolicyOverview = computed(() => {
  if (!currentPolicy.value) {
    return [
      { label: '企业', value: '-' },
      { label: '区域', value: '-' },
      { label: '保单状态', value: '-' },
      { label: '预防费余额', value: '-' }
    ]
  }
  return [
    { label: '企业', value: currentPolicy.value.enterpriseName || '-' },
    { label: '区域', value: formatRegionName(currentPolicy.value.regionCode, '-') },
    { label: '保单状态', value: policyStatusLabel(currentPolicy.value.policyStatus) },
    { label: '预防费余额', value: valueWithUnit(formatDecimal(currentPolicy.value.remainingFundAmount), '元') }
  ]
})

const policyHintTags = computed(() => buildPolicyHintTags(currentPolicy.value))
const detailHintTags = computed(() => buildPolicyHintTags(policyDetail.value || currentPolicy.value))
const resolvedPolicyHintTags = computed(() => buildExplanationFirstTags(policyHintTags.value))
const resolvedDetailHintTags = computed(() => buildExplanationFirstTags(detailHintTags.value))

function resetQuery() {
  proxy.resetForm('queryRef')
  Object.assign(queryParams.value, {
    pageNum: 1,
    pageSize: 10,
    statMonth: undefined,
    regionCode: undefined,
    enterpriseId: undefined,
    policyStatus: undefined,
    enterpriseName: undefined
  })
  applyWorkbenchRouteQuery(route.query, queryParams.value, aqInsuranceWorkbenchFields)
  getList()
}

watchEffect(() => {
  setPageGuide({
    title: '安责险投保监管',
    description: '核查高危企业安责险投保、到期续保和同步状态，支撑保险监管闭环。',
    portalExplanation: portalExplanationItems.value,
    focus: [],
    selection: selectedPolicyOverview.value,
    workflow: resolvedWorkflowSteps.value,
    hints: []
  })
})

function clearWorkbenchContext() {
  Object.assign(queryParams.value, {
    pageNum: 1,
    enterpriseId: undefined,
    regionCode: undefined,
    enterpriseName: undefined,
    policyStatus: undefined
  })
  router.replace({
    path: route.path,
    query: stripWorkbenchRouteQuery(route.query, aqInsuranceWorkbenchFields)
  })
  getList()
}

function applyAqInsuranceWorkbenchQuery(routeQuery = {}) {
  Object.assign(queryParams.value, {
    pageNum: 1,
    pageSize: 10,
    statMonth: undefined,
    regionCode: undefined,
    enterpriseId: undefined,
    enterpriseName: undefined,
    policyStatus: undefined
  })
  applyWorkbenchRouteQuery(routeQuery, queryParams.value, aqInsuranceWorkbenchFields)
  currentPolicy.value = undefined
  getList()
}

function handlePortalExplanationAction(action) {
  if (action?.path === route.path) {
    applyAqInsuranceWorkbenchQuery(action.query || {})
  }
  openPortalExplanationAction(router, action)
}

function aqInsuranceFocusLabel(value) {
  if (value === 'risk') return '续保风险'
  if (value === 'valid') return '有效覆盖'
  if (value === 'remaining') return '预防费余额'
  return value || '-'
}

applyWorkbenchRouteQuery(route.query, queryParams.value, aqInsuranceWorkbenchFields)
loadEnterpriseOptions()
getList()
</script>

<style scoped lang="scss">
.ygb-aq-insurance-page {
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

  .ygb-page__tip {
    max-width: 460px;
    padding: 16px 18px;
    border-radius: 16px;
    background: linear-gradient(135deg, rgba(15, 94, 168, 0.08), rgba(15, 94, 168, 0.02));
    color: #36516d;
    line-height: 1.7;
    font-size: 13px;
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

  .ygb-focus-list,
  .ygb-step-list {
    display: grid;
    gap: 12px;
  }

  .ygb-data-row,
  .ygb-step-item {
    border: 1px solid #dde7f1;
    border-radius: 14px;
    background: #f8fbfd;
  }

  .ygb-data-row {
    padding: 14px 16px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 12px;
    color: #627486;
    font-size: 13px;
  }

  .ygb-data-row strong {
    color: #13243a;
    font-size: 14px;
    text-align: right;
  }

  .ygb-step-item {
    display: grid;
    grid-template-columns: 42px minmax(0, 1fr);
    gap: 14px;
    padding: 16px;
  }

  .ygb-step-item__index {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    height: 34px;
    border-radius: 10px;
    background: #edf5fc;
    color: #0f5ea8;
    font-weight: 700;
  }

  .ygb-step-item__body strong {
    color: #13243a;
  }

  .ygb-step-item__body p {
    margin: 8px 0 0;
    color: #5f6f80;
    font-size: 13px;
    line-height: 1.7;
  }

  .ygb-tag-list {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
  }

  .ygb-readonly-alert,
  .ygb-search-card,
  .ygb-toolbar-card,
  .ygb-table-card {
    margin-bottom: 16px;
  }

  .ygb-detail-block {
    margin-top: 20px;
  }

  .ygb-detail-block h3 {
    margin: 0 0 12px;
    color: #13243a;
    font-size: 16px;
  }

  @media (max-width: 1200px) {
    .ygb-summary-grid,
    .ygb-focus-grid {
      grid-template-columns: repeat(2, minmax(0, 1fr));
    }
  }

  @media (max-width: 768px) {
    .ygb-summary-grid,
    .ygb-focus-grid {
      grid-template-columns: 1fr;
    }
  }
}
</style>
