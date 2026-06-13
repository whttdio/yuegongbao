<template>
  <div class="app-container azb-page azb-aq-insurance-page">
    <section class="gov-page-header azb-page__header">
      <div>
        <p class="azb-page__eyebrow">安责险协同</p>
        <h1 class="azb-page__title">{{ roleTitle }}</h1>
        <p class="azb-page__desc">{{ roleDescription }}</p>
      </div>
      <div class="azb-page__tip">
        <div class="azb-page__tip-item">当前视角：{{ roleBadge }}</div>
        <div class="azb-page__tip-item">聚焦安责险覆盖风险、续保到期、保险协同和预防费余额，不新增第二套保单业务表。</div>
        <div class="azb-page__tip-item">首页、驾驶舱和解释面板下钻后，保留来源条件继续查看常规列表。</div>
      </div>
    </section>

    <div class="azb-summary-grid">
      <div v-for="item in resolvedSummaryCards" :key="item.key" class="azb-summary-card" :class="item.cardClass">
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

    <el-card class="toolbar-card azb-toolbar-card" shadow="never">
      <el-row :gutter="10">
        <el-col v-if="!isReadOnlyRole" :span="1.5">
          <el-button type="primary" plain icon="RefreshRight" @click="handleSync" v-hasPermi="['ygb:aqInsurance:sync']">执行同步</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['ygb:aqInsurance:export']">导出</el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>
    </el-card>

    <el-card class="table-card azb-table-card" shadow="never">
      <template #header>
        <div class="azb-card-head azb-card-head--between">
          <div>
            <div class="azb-card-head__title">安责险协同列表</div>
            <div class="azb-card-head__desc">{{ focusTableHint }}</div>
          </div>
          <div class="azb-card-head__desc">当前共 {{ total }} 条</div>
        </div>
      </template>
      <el-table v-loading="loading" :data="visiblePolicyList" @row-click="handleRowClick">
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
        <el-table-column label="状态" width="120">
          <template #default="scope">
            <dict-tag :options="policyStatusOptions" :value="scope.row.policyStatus" />
          </template>
        </el-table-column>
        <el-table-column label="预防费计提" prop="preventionFundAmount" width="120" />
        <el-table-column label="已使用" prop="usedFundAmount" width="100" />
        <el-table-column label="余额" prop="remainingFundAmount" width="100" />
        <el-table-column label="剩余天数" prop="expireInDays" width="100" />
        <el-table-column label="回写状态" prop="sourceStatus" width="120" />
        <el-table-column label="回调时间" width="180">
          <template #default="scope">
            {{ formatDateTime(scope.row.callbackTime) }}
          </template>
        </el-table-column>
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
          <el-descriptions-item label="起保日期">{{ formatDate(policyDetail.startDate) }}</el-descriptions-item>
          <el-descriptions-item label="止保日期">{{ formatDate(policyDetail.endDate) }}</el-descriptions-item>
          <el-descriptions-item label="保费">{{ formatDecimal(policyDetail.premium) }}</el-descriptions-item>
          <el-descriptions-item label="剩余天数">{{ valueWithUnit(policyDetail.expireInDays, '天') }}</el-descriptions-item>
          <el-descriptions-item label="预防费比例">{{ formatDecimal(policyDetail.preventionFundRatio) }}</el-descriptions-item>
          <el-descriptions-item label="预防费计提">{{ formatDecimal(policyDetail.preventionFundAmount) }}</el-descriptions-item>
          <el-descriptions-item label="已使用金额">{{ formatDecimal(policyDetail.usedFundAmount) }}</el-descriptions-item>
          <el-descriptions-item label="可用余额">{{ formatDecimal(policyDetail.remainingFundAmount) }}</el-descriptions-item>
          <el-descriptions-item label="当前治理焦点" :span="2">{{ detailFocusText }}</el-descriptions-item>
          <el-descriptions-item label="来源流水号" :span="2">{{ policyDetail.sourceSerialNo || '-' }}</el-descriptions-item>
          <el-descriptions-item label="回写状态">{{ policyDetail.sourceStatus || '-' }}</el-descriptions-item>
          <el-descriptions-item label="回调时间">{{ formatDateTime(policyDetail.callbackTime) }}</el-descriptions-item>
          <el-descriptions-item label="来源消息" :span="2">{{ policyDetail.sourceMessage || '-' }}</el-descriptions-item>
          <el-descriptions-item label="原始报文" :span="2">
            <pre class="azb-policy-payload">{{ policyDetail.rawPayload || '-' }}</pre>
          </el-descriptions-item>
        </el-descriptions>

        <div class="azb-detail-block">
          <h3>治理提示</h3>
          <div class="azb-tag-list">
            <el-tag v-for="item in resolvedDetailHintTags" :key="item.label" :type="item.type" effect="plain">{{ item.label }}</el-tag>
          </div>
        </div>
      </template>
    </page-detail-dialog>
  </div>
</template>

<script setup name="AzbAqInsurance">
import { computed, getCurrentInstance, ref, watchEffect } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useWorkbenchAssist } from '@/composables/useWorkbenchAssist'
import useUserStore from '@/store/modules/user'
import { decoratePortalExplanationItems, openPortalExplanationAction, resolvePortalExplanationSummary } from '@/utils/portalExplanation'
import {
  buildPolicyHintTags,
  currentMonth,
  focusQueue,
  formatDecimal,
  formatRate,
  formatRegionName,
  matchPolicyFocus,
  policyStatusLabel,
  policyStatusOptions,
  prioritizeFocusRows,
  regionOptions as allRegionOptions,
  summaryCard,
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
const userStore = useUserStore()
const { isReadOnlyRole, isInsurerRole, isBankRole, readOnlyRoleLabel, readOnlyRoleDescription } = useRoleViewMode()

const workflowSteps = [
  { label: '核验覆盖', desc: '先确认当前区域安责险覆盖范围和有效保单底数，识别覆盖缺口。' },
  { label: '识别续保风险', desc: '优先查看即将到期和已过期保单，判断是否存在覆盖中断。' },
  { label: '复核预防费', desc: '核对计提、已使用和余额，确认是否具备继续投入和留痕条件。' },
  { label: '保留协同痕迹', desc: '将回写状态、来源消息和处置建议统一沉淀为监管协同记录。' }
]

const aqInsuranceWorkbenchFields = ['enterpriseId', 'regionCode', 'enterpriseName', 'policyStatus', 'focusKey']
const activeFocusKey = ref('')
let resolveVisiblePolicyList = () => []

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
  syncCurrentPolicy,
  getList,
  loadEnterpriseOptions,
  handleRowClick,
  handleQuery,
  handleSync,
  handleExport,
  syncPolicyContext,
  openDetail,
  formatDateTime
} = useAqInsurancePage({
  exportFilePrefix: 'azb_aq_insurance',
  canMutate: () => !isReadOnlyRole.value,
  onBlockedAction: actionLabel => {
    proxy.$modal.msgWarning(`${readOnlyRoleLabel.value}当前仅保留摘要、详情和导出，不能${actionLabel}`)
  },
  getCurrentList: () => resolveVisiblePolicyList(),
  afterList: () => {
    syncActiveFocus()
  }
})

const roleView = computed(() => {
  if (isBankRole.value) return 'bank'
  if (isInsurerRole.value) return 'insurer'
  const roles = userStore.roles || []
  if (roles.includes('ygb_enterprise_admin') || roles.includes('ygb_enterprise_operator')) {
    return 'site-enterprise'
  }
  return 'emergency'
})

const roleBadge = computed(() => {
  if (roleView.value === 'bank') return '银行只读协同'
  if (roleView.value === 'insurer') return '保险协同复核'
  if (roleView.value === 'site-enterprise') return '企业现场补核'
  return '应急监管处置'
})

const roleTitle = computed(() => {
  if (roleView.value === 'bank') return '安责险协同复核台账'
  if (roleView.value === 'insurer') return '安责险风险与续保复核台账'
  if (roleView.value === 'site-enterprise') return '现场安责险覆盖复核台账'
  return '安责险治理协同台账'
})

const roleDescription = computed(() => {
  if (roleView.value === 'bank') {
    return '面向银行协同角色查看安责险覆盖、到期风险和回写结果，只做风险复核，不承担续保录入和治理动作。'
  }
  if (roleView.value === 'insurer') {
    return '面向保险协同角色重点识别续保风险、余额异常和来源回写状态，便于继续推进保险侧复核。'
  }
  if (roleView.value === 'site-enterprise') {
    return '面向企业现场角色继续核对保单覆盖、续保状态和预防费余额，作为现场补核和资料回传入口。'
  }
  return '面向应急监管角色统一查看覆盖风险、续保提醒、预防费余额和回写状态，保持治理链解释口径一致。'
})

const portalExplanations = computed(() => {
  if (Array.isArray(summaryData.value.azbExplanation) && summaryData.value.azbExplanation.length) {
    return summaryData.value.azbExplanation
  }
  return [
    {
      key: 'risk',
      dimensionName: '续保风险',
      currentValue: valueOrDefault(summaryData.value.riskCount, 0),
      targetValue: '0',
      summary: '即将到期或已过期的保单应优先进入安责险风险复核队列，避免覆盖中断。',
      evidenceModule: 'aqInsurance',
      recommendModule: 'aqInsurance',
      defaultQuery: buildAqInsuranceExplanationQuery({ focusKey: 'risk' }),
      sourceLabel: '6.1 治理解释',
      sourceDescription: '继续在当前筛选范围内查看续保风险保单。',
      actionText: '查看续保风险'
    },
    {
      key: 'coverage',
      dimensionName: '有效覆盖',
      currentValue: formatRate(summaryData.value.coverageRate),
      targetValue: '100',
      summary: '有效保单覆盖率是当前区域治理底数，应保持稳定并可回溯。',
      evidenceModule: 'aqInsurance',
      recommendModule: 'aqInsurance',
      defaultQuery: buildAqInsuranceExplanationQuery({ policyStatus: '1', focusKey: 'coverage' }),
      sourceLabel: '6.1 治理解释',
      sourceDescription: '继续在当前筛选范围内查看有效覆盖保单。',
      actionText: '查看有效覆盖'
    },
    {
      key: 'remaining',
      dimensionName: '预防费余额',
      currentValue: formatDecimal(summaryData.value.remainingFundAmountTotal),
      targetValue: '-',
      summary: '预防费余额需要与保单状态、回写记录一起复核，避免治理投入和留痕脱节。',
      evidenceModule: 'aqInsurance',
      recommendModule: 'aqInsurance',
      defaultQuery: buildAqInsuranceExplanationQuery({ focusKey: 'remaining' }),
      sourceLabel: '6.1 治理解释',
      sourceDescription: '继续在当前筛选范围内查看预防费余额异常对象。',
      actionText: '查看余额风险'
    }
  ]
})

const portalExplanationItems = computed(() => decoratePortalExplanationItems(portalExplanations.value, {
  portalCode: 'azb',
  panelTitle: '6.1 治理解释',
  panelDescription: '安责险覆盖、续保风险和预防费核查建议统一来自门户解释聚合接口。'
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
    return `${label}：${summary}`
  }
  if (summary) {
    return summary
  }
  if (label) {
    return `${label}优先`
  }
  return fallback
}

function resolveExplanationFirstActionText(fallback, explanation) {
  if (!explanation) {
    return fallback
  }
  return explanation.actionLabel || explanation.actionText || explanation.targetLabel || explanation.moduleLabel || fallback
}

function buildExplanationFirstTags(tags = []) {
  const extras = []
  const label = buildPortalExplanationLabel(leadingPortalExplanation.value)
  if (label) {
    extras.push({ label: `6.1主解释：${label}`, type: 'warning' })
  }
  if (portalExplanationSummary.value) {
    extras.push({ label: portalExplanationSummary.value, type: 'success' })
  }
  return [...extras, ...tags].slice(0, 4)
}

const summaryCards = computed(() => {
  if (roleView.value === 'bank') {
    return [
      summaryCard('coverage', '覆盖率', formatRate(summaryData.value.coverageRate), '%', '用于核对区域安责险有效覆盖水平。', 'azb-summary-card--success'),
      summaryCard('risk', '风险保单', valueOrDefault(summaryData.value.riskCount, 0), '单', '优先查看即将到期或已过期的保单。', 'azb-summary-card--danger'),
      summaryCard('insurer', '保险机构', valueOrDefault(summaryData.value.insurerCount, 0), '家', '用于复核当前保单分布的协同主体。', ''),
      summaryCard('remaining', '预防费余额', formatDecimal(summaryData.value.remainingFundAmountTotal), '元', '辅助判断余额是否需要继续核查。', 'azb-summary-card--warning')
    ]
  }
  if (roleView.value === 'insurer') {
    return [
      summaryCard('risk', '风险保单', valueOrDefault(summaryData.value.riskCount, 0), '单', '保险侧优先处理续保风险和来源异常对象。', 'azb-summary-card--danger'),
      summaryCard('soon', '即将到期', valueOrDefault(summaryData.value.soonExpireCount, 0), '单', '需提前准备续保确认和资料回传。', 'azb-summary-card--warning'),
      summaryCard('remaining', '预防费余额', formatDecimal(summaryData.value.remainingFundAmountTotal), '元', '用于复核后续可投入资源和留痕完整性。', ''),
      summaryCard('valid', '有效保单', valueOrDefault(summaryData.value.validCount, 0), '单', '当前仍处于有效覆盖状态的保单。', 'azb-summary-card--success')
    ]
  }
  return [
    summaryCard('total', '保单总量', valueOrDefault(summaryData.value.totalCount, total.value), '单', '当前筛选范围内的安责险保单总量。', ''),
    summaryCard('risk', '风险保单', valueOrDefault(summaryData.value.riskCount, 0), '单', '用于优先识别续保中断和治理缺口。', 'azb-summary-card--danger'),
    summaryCard('coverage', '有效覆盖率', formatRate(summaryData.value.coverageRate), '%', '反映当前区域安责险覆盖稳定性。', 'azb-summary-card--success'),
    summaryCard('remaining', '预防费余额', formatDecimal(summaryData.value.remainingFundAmountTotal), '元', '用于快速判断当前保单池预防费余额。', 'azb-summary-card--warning')
  ]
})
const resolvedSummaryCards = computed(() => summaryCards.value.map((item, index) => ({
  ...item,
  note: resolveExplanationFirstText(item.note, resolvePortalExplanationItem(index))
})))

const focusQueues = computed(() => {
  const queues = [
    focusQueue('risk', '续保风险', valueOrDefault(summaryData.value.riskCount, 0), '单', '优先查看即将到期、已过期和可能中断覆盖的保单。', '优先复核'),
    focusQueue('coverage', '有效覆盖', formatRate(summaryData.value.coverageRate), '%', '查看当前范围内仍处于有效覆盖的保单对象。', '核对覆盖'),
    focusQueue('insurer', '保险协同', valueOrDefault(summaryData.value.insurerCount, 0), '家', '按保险机构维度回看当前保单分布。', '查看机构分布'),
    focusQueue('soon', '到期提醒', valueOrDefault(summaryData.value.soonExpireCount, 0), '单', '聚焦即将到期的保单，提前推进续保。', '跟进到期'),
    focusQueue('remaining', '余额复核', formatDecimal(summaryData.value.remainingFundAmountTotal), '元', '查看预防费余额和留痕是否匹配。', '核对余额')
  ]
  if (roleView.value === 'bank') {
    return queues.filter(item => ['coverage', 'risk', 'insurer'].includes(item.key))
  }
  if (roleView.value === 'insurer') {
    return queues.filter(item => ['risk', 'soon', 'remaining'].includes(item.key))
  }
  return queues
})
const resolvedFocusQueues = computed(() => focusQueues.value.map((item, index) => {
  const explanation = resolvePortalExplanationItem(index)
  return {
    ...item,
    desc: resolveExplanationFirstText(item.desc, explanation),
    actionText: resolveExplanationFirstActionText(item.actionText, explanation)
  }
}))

const activeFocus = computed(() => focusQueues.value.find(item => item.key === activeFocusKey.value) || focusQueues.value[0] || null)
const visiblePolicyList = computed(() => prioritizeFocusRows(aqInsuranceList.value, row => matchPolicyFocus(row, activeFocus.value?.key)))
resolveVisiblePolicyList = () => visiblePolicyList.value

const workbenchContext = computed(() => buildWorkbenchContext(route.query, {
  fields: aqInsuranceWorkbenchFields,
  sourceLabel: '安责保工作台',
  title: '当前保单台账沿用了工作台来源条件',
  description: '当前列表保留了上游带入的企业、区域、状态或解释焦点，便于继续核查覆盖风险和预防费余额。',
  fieldLabels: {
    enterpriseId: '企业',
    regionCode: '行政区划',
    enterpriseName: '企业名称',
    policyStatus: '保单状态',
    focusKey: '焦点队列'
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

const focusTableHint = computed(() => activeFocus.value?.desc || '按当前焦点优先暴露最需要复核的安责险对象。')

const selectedPolicyOverview = computed(() => {
  if (!currentPolicy.value) {
    return [
      { label: '焦点队列', value: activeFocus.value?.title || '-' },
      { label: '当前数量', value: `${activeFocus.value?.count ?? 0}${activeFocus.value?.unit || ''}` },
      { label: '当前区域', value: queryParams.value.regionCode ? formatRegionName(queryParams.value.regionCode, '全部区域') : '全部区域' },
      { label: '主动作', value: activeFocus.value?.actionText || '-' }
    ]
  }
  return [
    { label: '企业', value: currentPolicy.value.enterpriseName || '-' },
    { label: '区域', value: formatRegionName(currentPolicy.value.regionCode, '-') },
    { label: '保单状态', value: policyStatusLabel(currentPolicy.value.policyStatus) },
    { label: '余额 / 回写', value: `${formatDecimal(currentPolicy.value.remainingFundAmount)} 元 / ${currentPolicy.value.sourceStatus || '-'}` }
  ]
})

const primaryPolicyAction = computed(() => {
  if (!currentPolicy.value) {
    return { label: activeFocus.value?.actionText || '查看详情', action: 'detail' }
  }
  if (isReadOnlyRole.value) {
    return { label: '查看详情', action: 'detail' }
  }
  if (['2', '3'].includes(String(currentPolicy.value.policyStatus || ''))) {
    return { label: '执行同步', action: 'sync' }
  }
  if (Number(currentPolicy.value.remainingFundAmount || 0) > 0 && Number(currentPolicy.value.remainingFundAmount || 0) < 10000) {
    return { label: '执行同步', action: 'sync' }
  }
  if (currentPolicy.value.sourceStatus && !['SUCCESS', '1'].includes(String(currentPolicy.value.sourceStatus))) {
    return { label: '执行同步', action: 'sync' }
  }
  return { label: '查看详情', action: 'detail' }
})

const currentPolicyActionSummary = computed(() => {
  if (!currentPolicy.value) {
    return activeFocus.value
      ? `当前已按“${activeFocus.value.title}”重排列表，可继续查看摘要、详情或按来源条件导出。`
      : '请选择一条保单查看当前治理建议。'
  }
  if (isReadOnlyRole.value) {
    return '当前角色仅保留协同复核和详情查看，不执行续保录入或处置动作。'
  }
  if (['2', '3'].includes(String(currentPolicy.value.policyStatus || ''))) {
    return '当前保单存在明显续保风险，建议先核对来源回写，再推进续保或覆盖衔接。'
  }
  if (Number(currentPolicy.value.remainingFundAmount || 0) > 0 && Number(currentPolicy.value.remainingFundAmount || 0) < 10000) {
    return '当前保单预防费余额偏低，建议继续核查余额使用和后续治理投入。'
  }
  if (currentPolicy.value.sourceStatus && !['SUCCESS', '1'].includes(String(currentPolicy.value.sourceStatus))) {
    return '当前保单回写状态异常，建议先复核来源消息与同步结果。'
  }
  return '当前保单状态相对稳定，可继续回看详情、导出或保留为后续协同底数。'
})

const currentPolicyActionTags = computed(() => buildPolicyHintTags(currentPolicy.value || undefined, activeFocus.value))
const detailHintTags = computed(() => buildPolicyHintTags(policyDetail.value || currentPolicy.value, activeFocus.value))
const resolvedCurrentPolicyActionSummary = computed(() => resolveExplanationFirstText(
  currentPolicyActionSummary.value,
  leadingPortalExplanation.value
))
const resolvedCurrentPolicyActionTags = computed(() => buildExplanationFirstTags(currentPolicyActionTags.value))
const resolvedPrimaryPolicyAction = computed(() => ({
  ...primaryPolicyAction.value,
  label: resolveExplanationFirstActionText(primaryPolicyAction.value.label, leadingPortalExplanation.value)
}))

const hintTags = computed(() => {
  const tags = []
  if (valueOrDefault(summaryData.value.riskCount, 0) > 0) {
    tags.push({ label: `当前仍有 ${valueOrDefault(summaryData.value.riskCount, 0)} 单风险保单，建议优先处理续保风险。`, type: 'danger' })
  }
  if (valueOrDefault(summaryData.value.soonExpireCount, 0) > 0) {
    tags.push({ label: `当前有 ${valueOrDefault(summaryData.value.soonExpireCount, 0)} 单即将到期保单，需要提前续保提醒。`, type: 'warning' })
  }
  if (Number(summaryData.value.remainingFundAmountTotal || 0) > 0) {
    tags.push({ label: `当前预防费余额 ${formatDecimal(summaryData.value.remainingFundAmountTotal)} 元，建议与保单状态一并核对。`, type: 'info' })
  }
  if (!tags.length) {
    tags.push({ label: '当前范围内安责险态势平稳，可继续通过解释面板或来源条件下钻复核。', type: 'success' })
  }
  return tags
})
const resolvedWorkflowSteps = computed(() => workflowSteps.map((item, index) => ({
  ...item,
  desc: resolveExplanationFirstText(item.desc, resolvePortalExplanationItem(index))
})))
const resolvedHintTags = computed(() => buildExplanationFirstTags(hintTags.value))
const resolvedDetailHintTags = computed(() => buildExplanationFirstTags(detailHintTags.value))

const detailFocusText = computed(() => {
  if (roleView.value === 'bank') {
    return '当前详情主要用于银行侧复核保单覆盖、风险分布和回写状态，不承担治理录入。'
  }
  if (roleView.value === 'insurer') {
    return '当前详情主要用于保险侧核查续保风险、余额状态和来源回写结果。'
  }
  if (roleView.value === 'site-enterprise') {
    return '当前详情主要用于现场补核保单覆盖和余额情况，便于补充资料与回传结果。'
  }
  return '当前详情主要用于应急监管复核安责险覆盖、续保风险、预防费余额和来源留痕。'
})

const readOnlyAlertTitle = computed(() => `${readOnlyRoleLabel.value}：当前仅保留安责险摘要、详情和导出`)
const readOnlyAlertDescription = computed(() => {
  if (isInsurerRole.value) {
    return `${readOnlyRoleDescription.value} 当前页面聚焦保险协同复核，不展示同步执行和治理录入。`
  }
  if (isBankRole.value) {
    return `${readOnlyRoleDescription.value} 当前页面仅作为跨模块协同查看入口，不承担续保推动和治理动作。`
  }
  return readOnlyRoleDescription.value
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

function resolveAqInsuranceFocusKey(value) {
  const normalized = String(value || '')
  return ['risk', 'coverage', 'insurer', 'soon', 'remaining'].includes(normalized) ? normalized : ''
}

function aqInsuranceFocusLabel(value) {
  if (value === 'risk') return '续保风险'
  if (value === 'coverage') return '有效覆盖'
  if (value === 'insurer') return '保险协同'
  if (value === 'soon') return '到期提醒'
  if (value === 'remaining') return '余额复核'
  return value || '-'
}

function formatDate(value) {
  if (!value) {
    return '-'
  }
  return proxy.parseTime(value, '{y}-{m}-{d}')
}

function syncActiveFocus() {
  if (!activeFocusKey.value || !focusQueues.value.some(item => item.key === activeFocusKey.value)) {
    activeFocusKey.value = focusQueues.value[0]?.key || ''
  }
}

activeFocusKey.value = resolveAqInsuranceFocusKey(route.query.focusKey)

function handleSelectFocus(item) {
  activeFocusKey.value = item.key
  syncCurrentPolicy()
}

function handlePrimaryPolicyAction() {
  if (!currentPolicy.value) {
    return
  }
  if (primaryPolicyAction.value.action === 'sync') {
    if (isReadOnlyRole.value) {
      proxy.$modal.msgWarning(`${readOnlyRoleLabel.value}当前仅保留摘要、详情和导出，不能执行同步`)
      return
    }
    syncPolicyContext(currentPolicy.value)
    return
  }
  openDetail(currentPolicy.value)
}

function resetQuery() {
  proxy.resetForm('queryRef')
  Object.assign(queryParams.value, {
    pageNum: 1,
    pageSize: 10,
    statMonth: currentMonth(),
    regionCode: undefined,
    enterpriseId: undefined,
    enterpriseName: undefined,
    policyStatus: undefined
  })
  applyWorkbenchRouteQuery(route.query, queryParams.value, aqInsuranceWorkbenchFields)
  activeFocusKey.value = resolveAqInsuranceFocusKey(route.query.focusKey)
  getList()

watchEffect(() => {
  setPageGuide({
    title: roleTitle.value || '????????',
    description: roleDescription.value || '?????????????????????????????????',
    portalExplanation: portalExplanationItems.value,
    focus: resolvedFocusQueues.value,
    selection: [...selectedPolicyOverview.value, { label: '??????', value: resolvedCurrentPolicyActionSummary.value }],
    workflow: resolvedWorkflowSteps.value,
    hints: [...resolvedCurrentPolicyActionTags.value, ...resolvedHintTags.value].slice(0, 6)
  })
})

}

function clearWorkbenchContext() {
  activeFocusKey.value = ''
  Object.assign(queryParams.value, {
    pageNum: 1,
    pageSize: 10,
    statMonth: currentMonth(),
    regionCode: undefined,
    enterpriseId: undefined,
    enterpriseName: undefined,
    policyStatus: undefined
  })
  router.replace({
    path: route.path,
    query: stripWorkbenchRouteQuery(route.query, aqInsuranceWorkbenchFields)
  })
  currentPolicy.value = undefined
  getList()
}

function applyAqInsuranceWorkbenchQuery(routeQuery = {}) {
  Object.assign(queryParams.value, {
    pageNum: 1,
    pageSize: 10,
    statMonth: currentMonth(),
    regionCode: undefined,
    enterpriseId: undefined,
    enterpriseName: undefined,
    policyStatus: undefined
  })
  applyWorkbenchRouteQuery(routeQuery, queryParams.value, aqInsuranceWorkbenchFields)
  activeFocusKey.value = resolveAqInsuranceFocusKey(routeQuery.focusKey)
  currentPolicy.value = undefined
  getList()
}

function handlePortalExplanationAction(action) {
  if (action?.path === route.path) {
    applyAqInsuranceWorkbenchQuery(action.query || {})
  }
  openPortalExplanationAction(router, action)
}

applyWorkbenchRouteQuery(route.query, queryParams.value, aqInsuranceWorkbenchFields)
loadEnterpriseOptions()
getList()
</script>

<style scoped lang="scss">
.azb-aq-insurance-page {
  .azb-workbench-alert,
  .azb-readonly-alert {
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

  .azb-page__tip {
    display: grid;
    gap: 10px;
    min-width: min(420px, 100%);
    padding: 18px 20px;
    border-radius: 18px;
    background: linear-gradient(135deg, rgba(11, 107, 120, 0.12), rgba(11, 107, 120, 0.03));
  }

  .azb-page__tip-item {
    color: #325f69;
    line-height: 1.7;
    font-size: 13px;
  }

  .azb-summary-grid,
  .azb-focus-grid {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 16px;
    margin-bottom: 16px;
  }

  .azb-summary-grid {
    grid-template-columns: repeat(4, minmax(0, 1fr));
  }

  .azb-summary-card,
  .azb-focus-card {
    border: 1px solid #d7e3e8;
    border-radius: 16px;
    background: #fff;
  }

  .azb-summary-card {
    padding: 18px 20px;
  }

  .azb-summary-card__label {
    color: #5f7580;
    font-size: 13px;
  }

  .azb-summary-card__value {
    margin-top: 10px;
    color: #10353b;
    font-size: 28px;
    font-weight: 700;
  }

  .azb-summary-card__unit {
    margin-left: 4px;
    font-size: 13px;
    font-weight: 500;
    color: #6f8790;
  }

  .azb-summary-card__note {
    margin-top: 10px;
    color: #5a717a;
    line-height: 1.7;
    font-size: 13px;
  }

  .azb-summary-card--success {
    background: linear-gradient(180deg, #ffffff 0%, #f3fbf8 100%);
  }

  .azb-summary-card--warning {
    background: linear-gradient(180deg, #ffffff 0%, #fff9ef 100%);
  }

  .azb-summary-card--danger {
    background: linear-gradient(180deg, #ffffff 0%, #fff5f4 100%);
  }

  .azb-focus-card :deep(.el-card__header) {
    padding: 18px 20px 10px;
    border-bottom: none;
  }

  .azb-focus-card :deep(.el-card__body) {
    padding: 0 20px 20px;
  }

  .azb-card-head {
    display: flex;
    flex-direction: column;
    gap: 6px;
  }

  .azb-card-head--between {
    flex-direction: row;
    align-items: flex-end;
    justify-content: space-between;
    gap: 20px;
  }

  .azb-card-head__title {
    color: #163840;
    font-size: 16px;
    font-weight: 700;
  }

  .azb-card-head__desc {
    color: #5e747d;
    line-height: 1.7;
    font-size: 13px;
  }

  .azb-focus-queue-list,
  .azb-step-list {
    display: grid;
    gap: 12px;
  }

  .azb-focus-queue {
    display: flex;
    align-items: center;
    justify-content: space-between;
    width: 100%;
    padding: 14px 16px;
    border: 1px solid #d7e3e8;
    border-radius: 14px;
    background: #fff;
    text-align: left;
    cursor: pointer;
    transition: border-color 0.2s ease, box-shadow 0.2s ease, transform 0.2s ease;
  }

  .azb-focus-queue:hover,
  .azb-focus-queue.is-active {
    border-color: #0b6b78;
    box-shadow: 0 10px 24px rgba(11, 107, 120, 0.08);
    transform: translateY(-1px);
  }

  .azb-focus-queue__main {
    display: grid;
    gap: 6px;
  }

  .azb-focus-queue__main strong {
    color: #163840;
    font-size: 15px;
  }

  .azb-focus-queue__main p {
    margin: 0;
    color: #627981;
    line-height: 1.7;
    font-size: 13px;
  }

  .azb-focus-queue__side {
    display: grid;
    gap: 8px;
    justify-items: end;
    margin-left: 20px;
  }

  .azb-focus-queue__count {
    color: #0b6b78;
    font-size: 22px;
    font-weight: 700;
  }

  .azb-focus-queue__action {
    color: #6a8189;
    font-size: 12px;
  }

  .azb-overview-list,
  .azb-tag-list {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
  }

  .azb-data-row {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 16px;
    padding: 10px 0;
    border-bottom: 1px dashed #e2ecef;
    color: #4b636c;
  }

  .azb-data-row:last-child {
    border-bottom: none;
  }

  .azb-data-row strong {
    color: #163840;
  }

  .azb-recommend-panel {
    margin-top: 14px;
    padding: 14px 16px;
    border-radius: 14px;
    background: linear-gradient(180deg, #f5fbfc 0%, #ffffff 100%);
  }

  .azb-recommend-panel__title {
    color: #0b6b78;
    font-size: 13px;
    font-weight: 700;
  }

  .azb-recommend-panel__summary {
    margin: 8px 0 12px;
    color: #4e656d;
    line-height: 1.8;
    font-size: 13px;
  }

  .azb-focus-actions {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
    margin-top: 14px;
  }

  .azb-step-item {
    display: flex;
    gap: 12px;
    padding: 12px 0;
    border-bottom: 1px dashed #e0eaed;
  }

  .azb-step-item:last-child {
    border-bottom: none;
  }

  .azb-step-item__index {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    width: 36px;
    height: 36px;
    border-radius: 12px;
    background: rgba(11, 107, 120, 0.08);
    color: #0b6b78;
    font-weight: 700;
  }

  .azb-step-item__body strong {
    display: block;
    margin-bottom: 6px;
    color: #163840;
  }

  .azb-step-item__body p {
    margin: 0;
    color: #60767e;
    line-height: 1.7;
    font-size: 13px;
  }

  .azb-detail-block {
    margin-top: 16px;
  }

  .azb-detail-block h3 {
    margin: 0 0 12px;
    color: #163840;
    font-size: 16px;
  }

  .azb-policy-payload {
    margin: 0;
    white-space: pre-wrap;
    word-break: break-all;
    color: #516871;
    line-height: 1.7;
    font-size: 12px;
  }

  @media (max-width: 1200px) {
    .azb-summary-grid {
      grid-template-columns: repeat(2, minmax(0, 1fr));
    }
  }

  @media (max-width: 768px) {
    .azb-summary-grid,
    .azb-focus-grid {
      grid-template-columns: minmax(0, 1fr);
    }

    .azb-card-head--between {
      flex-direction: column;
      align-items: flex-start;
    }

    .azb-focus-queue {
      flex-direction: column;
      align-items: flex-start;
      gap: 12px;
    }

    .azb-focus-queue__side {
      justify-items: start;
      margin-left: 0;
    }
  }
}
</style>
