<template>
  <div class="app-container ygb-page">
    <section class="gov-page-header ygb-page__header">
      <div>
        <p class="ygb-page__eyebrow">预防资金</p>
        <h1 class="ygb-page__title">事故预防资金结算台账</h1>
        <p class="ygb-page__desc">
          面向企业管理员、财务经办和监管人员统一跟踪事故预防费计提、使用、余额和结算凭证。
          粤工保侧重点不是余额风险看板，而是把资金使用、凭证回写和结算时间纳入连续办理台账，支撑月度归档和后续核查。
        </p>
      </div>
      <div class="ygb-table-tip">
        当资金池余额低于计提金额 10% 时仍会进入预警口径。请优先核对低余额、缺凭证和未结算记录。
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


    <el-card class="search-card ygb-search-card" shadow="never">
      <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
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
        <el-form-item label="资金状态">
          <el-select v-model="queryParams.fundStatus" clearable style="width: 160px">
            <el-option v-for="item in fundStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
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
          <el-button type="success" plain icon="Edit" :disabled="!currentFund" @click="openEditDialog()" v-hasPermi="['ygb:preventionFund:edit']">维护资金</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['ygb:preventionFund:export']">导出</el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>
    </el-card>

    <el-card class="table-card ygb-table-card" shadow="never">
      <el-table v-loading="loading" :data="fundList" @row-click="handleRowClick">
        <el-table-column label="资金池ID" prop="fundId" width="100" />
        <el-table-column label="统计月份" prop="statMonth" width="110" />
        <el-table-column label="企业名称" prop="enterpriseName" min-width="220" />
        <el-table-column label="区域" width="140">
          <template #default="scope">
            {{ formatRegionName(scope.row.regionCode, '-') }}
          </template>
        </el-table-column>
        <el-table-column label="计提金额" width="120">
          <template #default="scope">
            {{ formatMoney(scope.row.accruedAmount) }}
          </template>
        </el-table-column>
        <el-table-column label="已使用" width="120">
          <template #default="scope">
            {{ formatMoney(scope.row.usedAmount) }}
          </template>
        </el-table-column>
        <el-table-column label="可用余额" width="120">
          <template #default="scope">
            {{ formatMoney(scope.row.remainingAmount) }}
          </template>
        </el-table-column>
        <el-table-column label="资金状态" width="110">
          <template #default="scope">
            <dict-tag :options="fundStatusOptions" :value="scope.row.fundStatus" />
          </template>
        </el-table-column>
        <el-table-column label="使用用途" prop="usagePurpose" min-width="220" show-overflow-tooltip />
        <el-table-column label="最近结算时间" width="180">
          <template #default="scope">
            {{ parseTime(scope.row.lastSettleTime, '{y}-{m}-{d} {h}:{i}:{s}') }}
          </template>
        </el-table-column>
        <el-table-column class-name="table-fill-column" min-width="1" />

        <el-table-column label="操作" fixed="right" align="center" width="140">
          <template #default="scope">
            <el-button link type="info" icon="View" @click.stop="openDetail(scope.row)">详情</el-button>
            <el-button v-if="!isReadOnlyRole" link type="primary" icon="Edit" @click.stop="openEditDialog(scope.row)" v-hasPermi="['ygb:preventionFund:edit']">维护</el-button>
          </template>
        </el-table-column>
      </el-table>
      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>

    <el-dialog :title="title" v-model="open" width="640px" append-to-body>
      <el-form ref="fundRef" :model="form" :rules="rules" label-width="110px">
        <el-form-item label="企业名称">
          <el-input v-model="form.enterpriseName" disabled />
        </el-form-item>
        <el-form-item label="统计月份">
          <el-input v-model="form.statMonth" disabled />
        </el-form-item>
        <el-form-item label="计提金额">
          <el-input :model-value="formatMoney(form.accruedAmount)" disabled />
        </el-form-item>
        <el-form-item label="已使用金额" prop="usedAmount">
          <el-input-number v-model="form.usedAmount" :min="0" :step="100" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="预计余额">
          <el-input :model-value="remainingPreview" disabled />
        </el-form-item>
        <el-form-item label="使用用途" prop="usagePurpose">
          <el-input v-model="form.usagePurpose" type="textarea" :rows="3" placeholder="请输入资金用途" />
        </el-form-item>
        <el-form-item label="凭证地址" prop="evidenceUrl">
          <el-input v-model="form.evidenceUrl" placeholder="例如：https://oss.example.com/aqins/fund/evidence.pdf" />
        </el-form-item>
        <el-form-item label="结算时间" prop="lastSettleTime">
          <el-date-picker v-model="form.lastSettleTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button v-if="!isReadOnlyRole" type="primary" @click="submitForm">确定</el-button>
          <el-button @click="open = false">取消</el-button>
        </div>
      </template>
    </el-dialog>

    <page-detail-dialog v-model="detailOpen" title="资金池详情" width="760px">
      <template v-if="detailFund">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="资金池ID">{{ detailFund.fundId }}</el-descriptions-item>
          <el-descriptions-item label="关联保单ID">{{ detailFund.policyId || '-' }}</el-descriptions-item>
          <el-descriptions-item label="企业名称">{{ detailFund.enterpriseName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="统计月份">{{ detailFund.statMonth || '-' }}</el-descriptions-item>
          <el-descriptions-item label="区域">{{ formatRegionName(detailFund.regionCode, '-') }}</el-descriptions-item>
          <el-descriptions-item label="资金状态">
            <dict-tag :options="fundStatusOptions" :value="detailFund.fundStatus" />
          </el-descriptions-item>
          <el-descriptions-item label="计提金额">{{ formatMoney(detailFund.accruedAmount) }}</el-descriptions-item>
          <el-descriptions-item label="已使用金额">{{ formatMoney(detailFund.usedAmount) }}</el-descriptions-item>
          <el-descriptions-item label="可用余额">{{ formatMoney(detailFund.remainingAmount) }}</el-descriptions-item>
          <el-descriptions-item label="来源模式">{{ sourceModeLabel(detailFund.sourceMode) }}</el-descriptions-item>
          <el-descriptions-item label="最近结算时间">{{ formatDateTime(detailFund.lastSettleTime) }}</el-descriptions-item>
          <el-descriptions-item label="更新人">{{ detailFund.updateBy || '-' }}</el-descriptions-item>
          <el-descriptions-item label="使用用途" :span="2">{{ detailFund.usagePurpose || '-' }}</el-descriptions-item>
          <el-descriptions-item label="凭证地址" :span="2">{{ detailFund.evidenceUrl || '-' }}</el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ detailFund.remark || '-' }}</el-descriptions-item>
        </el-descriptions>

        <div class="ygb-detail-block">
          <h3>办理提示</h3>
          <div class="ygb-tag-list">
            <el-tag v-for="item in detailHintTags" :key="item.label" :type="item.type" effect="plain">{{ item.label }}</el-tag>
          </div>
        </div>
      </template>
    </page-detail-dialog>
  </div>
</template>

<script setup name="YgbPreventionFund">
import { computed, getCurrentInstance, watchEffect } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useWorkbenchAssist } from '@/composables/useWorkbenchAssist'
import { decoratePortalExplanationItems, openPortalExplanationAction } from '@/utils/portalExplanation'
import { useRoleViewMode } from '@/utils/roleView'
import { applyWorkbenchRouteQuery, buildWorkbenchContext, stripWorkbenchRouteQuery } from '@/utils/workbenchLink'
import { useAuthorizedRegionOptions } from '@/utils/regionScope'
import {
  currentMonth,
  formatDateTime,
  formatMoney,
  formatRegionName,
  fundStatusOptions,
  isLowBalance,
  isManualSourceMode,
  regionOptions as allRegionOptions,
  sourceModeLabel,
  usePreventionFundPage,
  valueOrDefault
} from '@/views/preventionFund/usePreventionFundPage'

const { proxy } = getCurrentInstance()
const regionOptions = useAuthorizedRegionOptions(allRegionOptions)
const route = useRoute()
const { setPageGuide } = useWorkbenchAssist()
const router = useRouter()
const { isReadOnlyRole, readOnlyRoleLabel, readOnlyRoleDescription } = useRoleViewMode()
const preventionFundWorkbenchFields = ['enterpriseId', 'regionCode', 'statMonth', 'fundStatus', 'focusKey']
const preventionFundInitialQuery = {}
applyWorkbenchRouteQuery(route.query, preventionFundInitialQuery, preventionFundWorkbenchFields)

const workflowSteps = [
  { label: '计提入账', desc: '先确认当月计提金额和关联企业，形成事故预防费基础入账台账。' },
  { label: '用途登记', desc: '按项目、培训或服务用途登记已使用金额和文字说明，保留资金去向。' },
  { label: '结算回写', desc: '补齐凭证地址和结算时间，确保资金使用和回写留痕连续。' },
  { label: '结果归档', desc: '将余额、用途和结算情况纳入月度归档与后续监管核查依据。' }
]

const {
  loading,
  showSearch,
  total,
  fundList,
  enterpriseOptions,
  currentFund,
  detailFund,
  detailOpen,
  open,
  title,
  summaryData,
  form,
  queryParams,
  rules,
  remainingPreview,
  getList,
  handleRowClick,
  handleQuery,
  openEditDialog,
  submitForm,
  openDetail,
  handleExport
} = usePreventionFundPage({
  exportFilePrefix: 'ygb_prevention_fund',
  canMutate: () => !isReadOnlyRole.value,
  onBlockedAction: blockReadOnlyAction,
  initialQueryParams: preventionFundInitialQuery
})

function buildPreventionFundExplanationQuery(extraQuery = {}) {
  return {
    enterpriseId: queryParams.value.enterpriseId,
    regionCode: queryParams.value.regionCode,
    statMonth: queryParams.value.statMonth,
    fundStatus: queryParams.value.fundStatus,
    ...extraQuery
  }
}

const summaryCards = computed(() => ([
  {
    key: 'total',
    label: '资金池记录',
    value: valueOrDefault(summaryData.value.totalCount, total.value),
    unit: '条',
    note: '当前筛选范围内的资金池记录总量。',
    cardClass: ''
  },
  {
    key: 'remaining',
    label: '可用余额汇总',
    value: formatMoney(summaryData.value.remainingAmountTotal),
    unit: '元',
    note: '用于快速判断当前月份事故预防费总体可用余额。',
    cardClass: 'ygb-summary-card--success'
  },
  {
    key: 'lowBalance',
    label: '低余额风险',
    value: valueOrDefault(summaryData.value.lowBalanceCount, 0),
    unit: '条',
    note: '建议优先核查余额占比过低的资金池记录。',
    cardClass: 'ygb-summary-card--warning'
  },
  {
    key: 'inUse',
    label: '使用中记录',
    value: valueOrDefault(summaryData.value.inUseCount, 0),
    unit: '条',
    note: '仍在持续使用或待补结算留痕的记录数量。',
    cardClass: 'ygb-summary-card--primary'
  }
]))

const focusItems = computed(() => ([
  { label: '当前月份', value: queryParams.value.statMonth || '全部月份' },
  { label: '当前区域', value: queryParams.value.regionCode ? formatRegionName(queryParams.value.regionCode, '全部区域') : '全部区域' },
  { label: '协同来源', value: `${valueOrDefault(summaryData.value.nonStubCount, 0)} 条` },
  { label: '缺少凭证地址', value: `${valueOrDefault(summaryData.value.missingEvidenceCount, 0)} 条` }
]))

const portalExplanations = computed(() => {
  if (Array.isArray(summaryData.value.ygbExplanation) && summaryData.value.ygbExplanation.length) {
    return summaryData.value.ygbExplanation
  }
  return [
    {
      key: 'lowBalance',
      dimensionName: 'Low balance funds',
      currentValue: valueOrDefault(summaryData.value.lowBalanceCount, 0),
      targetValue: '0',
      summary: 'Low-balance funds should be reviewed before the monthly settlement chain continues.',
      evidenceModule: 'preventionFund',
      recommendModule: 'preventionFund',
      defaultQuery: buildPreventionFundExplanationQuery({ focusKey: 'lowBalance' }),
      sourceLabel: '530.1 预防资金办理解释',
      sourceDescription: '在当前工作台范围内继续核查低余额资金对象。',
      actionText: 'Review low balance'
    },
    {
      key: 'inUse',
      dimensionName: '使用中资金',
      currentValue: valueOrDefault(summaryData.value.inUseCount, 0),
      targetValue: 'Keep traceable',
      summary: '使用中资金在归档前应同步补齐用途说明、结算时间和凭证材料。',
      evidenceModule: 'preventionFund',
      recommendModule: 'preventionFund',
      defaultQuery: buildPreventionFundExplanationQuery({ fundStatus: '2', focusKey: 'inUse' }),
      sourceLabel: '530.1 预防资金办理解释',
      sourceDescription: '在当前工作台范围内继续核查使用中的资金对象。',
      actionText: '核查使用中资金'
    },
    {
      key: 'missingEvidence',
      dimensionName: '缺少凭证',
      currentValue: valueOrDefault(summaryData.value.missingEvidenceCount, 0),
      targetValue: '0',
      summary: '缺少凭证的资金应在结算和月度归档前完成补正。',
      evidenceModule: 'preventionFund',
      recommendModule: 'preventionFund',
      defaultQuery: buildPreventionFundExplanationQuery({ focusKey: 'missingEvidence' }),
      sourceLabel: '530.1 预防资金办理解释',
      sourceDescription: '在当前工作台范围内继续核查凭证缺失资金对象。',
      actionText: '核查缺失凭证'
    },
    {
      key: 'nonStub',
      dimensionName: '协同来源记录',
      currentValue: valueOrDefault(summaryData.value.nonStubCount, 0),
      targetValue: '持续提升',
      summary: '协同来源记录有助于提升月度归档可追溯性，减少重复维护。',
      evidenceModule: 'preventionFund',
      recommendModule: 'preventionFund',
      defaultQuery: buildPreventionFundExplanationQuery({ focusKey: 'nonStub' }),
      sourceLabel: '530.1 预防资金办理解释',
      sourceDescription: '在当前工作台范围内继续核查正式来源资金记录。',
      actionText: '核查协同来源'
    }
  ]
})

const portalExplanationItems = computed(() => decoratePortalExplanationItems(portalExplanations.value, {
  portalCode: 'ygb',
  panelTitle: '530.1 预防资金办理解释',
  panelDescription: '当前解释项按 530.1 办理链口径展示预防资金余额、使用和凭证重点。'
}))

const selectedFundOverview = computed(() => {
  if (!currentFund.value) {
    return [
      { label: '企业名称', value: '-' },
      { label: '区域', value: '-' },
      { label: '余额风险', value: '-' },
      { label: '最近结算', value: '-' }
    ]
  }
  return [
    { label: '企业名称', value: currentFund.value.enterpriseName || '-' },
    { label: '区域', value: formatRegionName(currentFund.value.regionCode, '-') },
    { label: '余额风险', value: isLowBalance(currentFund.value.accruedAmount, currentFund.value.remainingAmount) ? '低余额' : '正常' },
    { label: '最近结算', value: formatDateTime(currentFund.value.lastSettleTime) }
  ]
})

const workbenchContext = computed(() => buildWorkbenchContext(route.query, {
  fields: preventionFundWorkbenchFields,
  sourceLabel: '上游工作台',
  title: '当前预防资金页沿用了上游来源条件。',
  description: '当前列表保留了企业、区域、月份和状态范围，便于沿同一办理语境继续处理资金链路。',
  fieldLabels: {
    enterpriseId: 'Enterprise',
    regionCode: 'Region',
    statMonth: 'Month',
    fundStatus: '资金状态',
    focusKey: '解释焦点'
  },
  fieldFormatters: {
    enterpriseId: value => {
      const matched = enterpriseOptions.value.find(item => String(item.enterpriseId) === String(value))
      return matched ? matched.enterpriseName : value
    },
    regionCode: value => formatRegionName(value, value),
    fundStatus: value => fundStatusLabel(value),
    focusKey: value => preventionFundFocusLabel(value)
  }
}))

const fundHintTags = computed(() => buildYgbFundHintTags(currentFund.value))
const detailHintTags = computed(() => buildYgbFundHintTags(detailFund.value || currentFund.value))
const readOnlyAlertTitle = computed(() => `${readOnlyRoleLabel.value}仅保留资金台账摘要、详情和导出`)
const readOnlyAlertDescription = computed(() => `${readOnlyRoleDescription.value || ''} 当前页面仍会展示资金摘要和详情，但不开放资金维护动作。`.trim())

function resetQuery() {
  proxy.resetForm('queryRef')
  Object.assign(queryParams.value, {
    pageNum: 1,
    pageSize: 10,
    statMonth: currentMonth(),
    regionCode: undefined,
    enterpriseId: undefined,
    enterpriseName: undefined,
    fundStatus: undefined
  })
  applyWorkbenchRouteQuery(route.query, queryParams.value, preventionFundWorkbenchFields)
  getList()
}

watchEffect(() => {
  setPageGuide({
    title: '事故预防资金池',
    description: '管理事故预防资金池预算、使用、余额和预警，支撑资金监管闭环。',
    portalExplanation: portalExplanationItems.value,
    focus: [],
    selection: selectedFundOverview.value,
    workflow: workflowSteps,
    hints: []
  })
})

function clearWorkbenchContext() {
  Object.assign(queryParams.value, {
    pageNum: 1,
    pageSize: 10,
    statMonth: currentMonth(),
    regionCode: undefined,
    enterpriseId: undefined,
    enterpriseName: undefined,
    fundStatus: undefined
  })
  router.replace({
    path: route.path,
    query: stripWorkbenchRouteQuery(route.query, preventionFundWorkbenchFields)
  })
  getList()
}

function applyPreventionFundWorkbenchQuery(routeQuery = {}) {
  Object.assign(queryParams.value, {
    pageNum: 1,
    pageSize: 10,
    statMonth: currentMonth(),
    regionCode: undefined,
    enterpriseId: undefined,
    enterpriseName: undefined,
    fundStatus: undefined
  })
  applyWorkbenchRouteQuery(routeQuery, queryParams.value, preventionFundWorkbenchFields)
  currentFund.value = undefined
  getList()
}

function handlePortalExplanationAction(action) {
  if (action?.path === route.path) {
    applyPreventionFundWorkbenchQuery(action.query || {})
  }
  openPortalExplanationAction(router, action)
}

function blockReadOnlyAction(actionLabel) {
  proxy?.$modal?.msgWarning?.(`${readOnlyRoleLabel.value}仅保留摘要、详情和导出，不能${actionLabel}`)
}

function fundStatusLabel(value) {
  return fundStatusOptions.find(item => item.value === String(value))?.label || value || '-'
}

function preventionFundFocusLabel(value) {
  if (value === 'lowBalance') return '低余额资金'
  if (value === 'inUse') return '使用中资金'
  if (value === 'missingEvidence') return '缺少凭证'
  if (value === 'nonStub') return '协同来源记录'
  return value || '-'
}

function buildYgbFundHintTags(fund) {
  if (!fund) {
    return [{ label: '请选择资金池查看办理提示', type: 'info' }]
  }
  const tags = []
  if (isLowBalance(fund.accruedAmount, fund.remainingAmount)) {
    tags.push({ label: '当前余额偏低，建议优先核对后续投入和核销计划。', type: 'warning' })
  }
  if (String(fund.fundStatus) === '2') {
    tags.push({ label: '资金处于使用中，建议及时补录用途说明和结算时间。', type: 'warning' })
  }
  if (!fund.usagePurpose) {
    tags.push({ label: '缺少使用用途，建议补齐费用去向说明。', type: 'warning' })
  }
  if (!fund.evidenceUrl) {
    tags.push({ label: '缺少凭证地址，建议补齐结算凭证或佐证材料链接。', type: 'warning' })
  }
  if (!fund.lastSettleTime) {
    tags.push({ label: '缺少最近结算时间，建议补录回写时间留痕。', type: 'info' })
  }
  if (isManualSourceMode(fund.sourceMode)) {
    tags.push({ label: '当前为人工维护记录，建议复核金额与用途是否已同步。', type: 'info' })
  }
  if (String(fund.fundStatus) === '3' && fund.evidenceUrl) {
    tags.push({ label: '资金已核销且凭证齐全，可继续用于月度归档。', type: 'success' })
  }
  if (!tags.length) {
    tags.push({ label: '当前资金池信息完整，可继续用于结算归档和后续核查。', type: 'success' })
  }
  return tags
}

</script>

<style scoped lang="scss">
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
.ygb-source-list,
.ygb-pipeline-list {
  display: grid;
  gap: 12px;
}

.ygb-focus-item,
.ygb-source-item,
.ygb-pipeline-item {
  border: 1px solid #dde7f1;
  border-radius: 14px;
  background: #f8fbfd;
}

.ygb-focus-item,
.ygb-source-item {
  padding: 14px 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.ygb-focus-item__label,
.ygb-source-item__label {
  color: #627486;
  font-size: 13px;
}

.ygb-focus-item__value,
.ygb-source-item__value {
  color: #13243a;
  font-size: 14px;
  font-weight: 600;
  text-align: right;
}

.ygb-pipeline-item {
  display: grid;
  grid-template-columns: 42px minmax(0, 1fr);
  gap: 14px;
  padding: 16px;
}

.ygb-pipeline-item__index {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  height: 34px;
  border-radius: 10px;
  background: #edf5fc;
  color: #0f5ea8;
  font-weight: 700;
}

.ygb-pipeline-item__body strong {
  color: #13243a;
}

.ygb-pipeline-item__body p {
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

.ygb-detail-block {
  margin-top: 20px;
}

.ygb-detail-block h3 {
  margin: 0 0 12px;
  color: #13243a;
  font-size: 16px;
}

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
</style>

