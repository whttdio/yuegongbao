<template>
  <div class="app-container azb-page">
    <section class="gov-page-header azb-page__header">
      <div>
        <p class="azb-page__eyebrow">参保风险协同</p>
        <h1 class="azb-page__title">漏保清单、督办闭环与高风险对象筛查</h1>
        <p class="azb-page__desc">
          面向应急监管、保险协同和企业现场复核统一查看漏保识别结果、处置状态和预警联动情况。
          页面按“先看高风险缺口，再锁定当前对象，再进入督办处置”的安责保工作流重组，继续复用共享漏保清单接口。
        </p>
      </div>
      <div class="azb-page__tip">
        <div class="azb-page__tip-item">优先关注：未预警、待核查、收入较高但长期未补齐参保的对象。</div>
        <div class="azb-page__tip-item">当前清单仍由税务、社保与收入 Stub 结果碰撞生成，适合做监管联动与保险侧筛查。</div>
        <div class="azb-page__tip-item">生成清单前需先选择统计月份，当前导出仍复用统一后台能力。</div>
      </div>
    </section>

    <div class="azb-summary-grid">
      <div
        v-for="item in resolvedSummaryCards"
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
      <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
        <el-form-item label="统计月份">
          <el-date-picker v-model="queryParams.statMonth" type="month" format="YYYY-MM" value-format="YYYY-MM" style="width: 160px" />
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
        <el-form-item label="人员">
          <el-input v-model="queryParams.personName" clearable style="width: 180px" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="处置状态">
          <el-select v-model="queryParams.disposalStatus" clearable style="width: 150px">
            <el-option v-for="item in disposalStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="预警状态">
          <el-select v-model="queryParams.warningStatus" clearable style="width: 150px">
            <el-option v-for="item in warningStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="toolbar-card azb-toolbar-card" shadow="never">
      <el-row :gutter="10">
        <el-col v-if="!isReadOnlyRole" :span="1.5">
          <el-button type="primary" plain icon="RefreshRight" @click="handleGenerate" v-hasPermi="['ygb:uninsuredList:generate']">生成清单</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['ygb:uninsuredList:export']">导出</el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>
    </el-card>

    <el-card class="table-card azb-table-card" shadow="never">
      <template #header>
        <div class="azb-card-head azb-card-head--between">
          <div>
            <div class="azb-card-head__title">漏保督办台账</div>
            <div class="azb-card-head__desc">{{ focusTableHint }}</div>
          </div>
          <div class="azb-card-head__desc">当前总量 {{ total }} 条</div>
        </div>
      </template>
      <el-table v-loading="loading" :data="visibleUninsuredList" @row-click="handleRowClick">
        <el-table-column label="ID" prop="listId" width="90" />
        <el-table-column label="批次号" prop="batchNo" min-width="180" />
        <el-table-column label="统计月份" prop="statMonth" width="110" />
        <el-table-column label="企业" prop="enterpriseName" min-width="220" />
        <el-table-column label="人员" prop="personName" width="120" />
        <el-table-column label="区域" width="140">
          <template #default="scope">
            <span>{{ formatRegionName(scope.row.regionCode) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="身份证号" prop="idCard" min-width="180" />
        <el-table-column label="工资金额" prop="salaryAmount" width="120" />
        <el-table-column label="识别原因" prop="detectedReason" min-width="240" show-overflow-tooltip />
        <el-table-column label="处置状态" prop="disposalStatus" width="120">
          <template #default="scope">
            <dict-tag :options="disposalStatusOptions" :value="scope.row.disposalStatus" />
          </template>
        </el-table-column>
        <el-table-column label="预警状态" prop="warningStatus" width="120">
          <template #default="scope">
            <dict-tag :options="warningStatusOptions" :value="scope.row.warningStatus" />
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="createTime" width="180" />
        <el-table-column class-name="table-fill-column" min-width="1" />

        <el-table-column label="操作" fixed="right" align="center" width="180" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-button link type="info" icon="View" @click.stop="openDetail(scope.row)">详情</el-button>
            <el-button v-if="!isReadOnlyRole && isUninsuredHandleAllowed(scope.row)" link type="primary" icon="Edit" @click.stop="openHandleDialog(scope.row)" v-hasPermi="['ygb:uninsuredList:handle']">处置</el-button>
          </template>
        </el-table-column>
      </el-table>
      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>

    <el-dialog v-if="!isReadOnlyRole" title="漏保处置" v-model="handleOpen" width="520px" append-to-body>
      <el-form ref="handleRef" :model="handleForm" :rules="handleRules" label-width="100px">
        <el-form-item label="处置状态" prop="disposalStatus">
          <el-select v-model="handleForm.disposalStatus" placeholder="请选择处置状态">
            <el-option v-for="item in availableDisposalStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="处置说明" prop="remark">
          <el-input v-model="handleForm.remark" type="textarea" :rows="4" placeholder="请输入核查说明、督办情况或误报原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitHandle">确定</el-button>
          <el-button @click="handleOpen = false">取消</el-button>
        </div>
      </template>
    </el-dialog>

    <page-detail-dialog v-model="detailOpen" title="漏保记录详情" width="760px">
      <template v-if="detailRow">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="批次号">{{ detailRow.batchNo || '-' }}</el-descriptions-item>
          <el-descriptions-item label="统计月份">{{ detailRow.statMonth || '-' }}</el-descriptions-item>
          <el-descriptions-item label="企业">{{ detailRow.enterpriseName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="人员">{{ detailRow.personName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="区域">{{ formatRegionName(detailRow.regionCode) }}</el-descriptions-item>
          <el-descriptions-item label="工资金额">{{ formatMoney(detailRow.salaryAmount) }}</el-descriptions-item>
          <el-descriptions-item label="处置状态">{{ optionLabel(disposalStatusOptions, detailRow.disposalStatus) }}</el-descriptions-item>
          <el-descriptions-item label="预警状态">{{ optionLabel(warningStatusOptions, detailRow.warningStatus) }}</el-descriptions-item>
          <el-descriptions-item label="身份证号" :span="2">{{ detailRow.idCard || '-' }}</el-descriptions-item>
          <el-descriptions-item label="识别原因" :span="2">{{ detailRow.detectedReason || '-' }}</el-descriptions-item>
          <el-descriptions-item label="处置备注" :span="2">{{ detailRow.remark || '-' }}</el-descriptions-item>
        </el-descriptions>

        <div class="azb-detail-block">
          <h3>安责保侧提示</h3>
          <div class="azb-risk-tags">
            <el-tag v-for="item in resolvedDetailTags" :key="item.label" :type="item.type" effect="plain">{{ item.label }}</el-tag>
          </div>
        </div>
      </template>
    </page-detail-dialog>
  </div>
</template>

<script setup name="AzbUninsuredList">
import { computed, getCurrentInstance, ref, watchEffect } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useWorkbenchAssist } from '@/composables/useWorkbenchAssist'
import { decoratePortalExplanationItems, openPortalExplanationAction, resolvePortalExplanationSummary } from '@/utils/portalExplanation'
import { useRoleViewMode } from '@/utils/roleView'
import { applyWorkbenchRouteQuery, buildWorkbenchContext, stripWorkbenchRouteQuery } from '@/utils/workbenchLink'
import {
  disposalStatusOptions,
  warningStatusOptions,
  isUninsuredHandleAllowed,
  useUninsuredListPage,
  formatRegionName,
  optionLabel,
  formatMoney as formatAmount,
  valueOrDefault
} from '@/views/uninsuredList/useUninsuredListPage'

const { isReadOnlyRole, isBankRole, isInsurerRole, readOnlyRoleLabel, readOnlyRoleDescription } = useRoleViewMode()
const { proxy } = getCurrentInstance()
const route = useRoute()
const { setPageGuide } = useWorkbenchAssist()
const router = useRouter()
const activeFocusKey = ref('')
const uninsuredWorkbenchFields = ['enterpriseId', 'statMonth', 'regionCode', 'focusKey']
const uninsuredInitialQuery = {}
const workbenchClearLabel = '清空来源条件'

applyWorkbenchRouteQuery(route.query, uninsuredInitialQuery, uninsuredWorkbenchFields)

const roleView = computed(() => {
  if (isBankRole.value) {
    return 'bank'
  }
  if (isInsurerRole.value) {
    return 'insurer'
  }
  return 'emergency'
})

function buildUninsuredListExplanationQuery(extraQuery = {}) {
  return {
    enterpriseId: queryParams.value.enterpriseId,
    statMonth: queryParams.value.statMonth,
    regionCode: queryParams.value.regionCode,
    disposalStatus: queryParams.value.disposalStatus,
    warningStatus: queryParams.value.warningStatus,
    ...extraQuery
  }
}

const {
  uninsuredList,
  enterpriseOptions,
  loading,
  showSearch,
  total,
  handleOpen,
  detailOpen,
  currentRow,
  detailRow,
  summaryData,
  availableDisposalStatusOptions,
  queryParams,
  handleForm,
  handleRules,
  regionOptions,
  getList,
  handleQuery,
  handleGenerate,
  handleExport,
  handleRowClick,
  openDetail,
  openHandleDialog,
  submitHandle,
  syncCurrentRow,
  init
} = useUninsuredListPage({
  includeRegion: true,
  exportFilePrefix: 'azb_uninsured_list',
  initialQueryParams: uninsuredInitialQuery,
  handleStatusRequiredMessage: '请选择处置状态',
  canMutate: () => !isReadOnlyRole.value,
  immediate: false,
  getCurrentList: () => visibleUninsuredList.value,
  onBlockedAction(actionLabel) {
    proxy.$modal.msgWarning(`${readOnlyRoleLabel.value}仅保留摘要、详情和导出，不能${actionLabel}`)
  }
})

const summaryCards = computed(() => ([
  {
    key: 'total',
    label: '漏保记录',
    value: valueOrDefault(summaryData.value.totalCount, total.value),
    unit: '条',
    note: '当前筛选条件下的漏保清单总量。',
    cardClass: ''
  },
  {
    key: 'pending',
    label: '待核查/核查中',
    value: valueOrDefault(summaryData.value.pendingCount, 0),
    unit: '条',
    note: '当前筛选条件下仍需持续跟进的处置对象。',
    cardClass: 'azb-summary-card--warning'
  },
  {
    key: 'unwarned',
    label: '未预警',
    value: valueOrDefault(summaryData.value.unwarnedCount, 0),
    unit: '条',
    note: '当前筛选条件下尚未触发联动预警的对象。',
    cardClass: 'azb-summary-card--danger'
  },
  {
    key: 'completed',
    label: '已完成处置',
    value: valueOrDefault(summaryData.value.completedCount, 0),
    unit: '条',
    note: '当前筛选条件下已完成整改回写或核查收口的对象。',
    cardClass: 'azb-summary-card--success'
  }
]))

const resolvedSummaryCards = computed(() => summaryCards.value.map((item, index) => ({
  ...item,
  note: resolveExplanationFirstText(item.note, resolvePortalExplanationItem(index))
})))

const focusQueues = computed(() => {
  if (roleView.value === 'bank') {
    return [
      {
        key: 'highSalary',
        title: '高收入漏保对象',
        desc: '银行协同优先回看高收入且长期漏保的对象，确认是否影响区域筛查和协同报表口径。',
        count: valueOrDefault(summaryData.value.highSalaryCount, countBy(item => Number(item.salaryAmount || 0) >= 10000)),
        unit: '条',
        actionText: '先看高暴露对象'
      },
      {
        key: 'pending',
        title: '待核查对象',
        desc: '持续跟进对象需要确认是否长期积压，避免跨模块统计反复挂起。',
        count: valueOrDefault(summaryData.value.pendingCount, countBy(item => ['0', '1'].includes(item.disposalStatus))),
        unit: '条',
        actionText: '回看积压队列'
      },
      {
        key: 'unwarned',
        title: '未预警对象',
        desc: '优先确认高风险对象是否还未触发联动预警，避免区域筛查滞后。',
        count: valueOrDefault(summaryData.value.unwarnedCount, countBy(item => item.warningStatus === '0')),
        unit: '条',
        actionText: '核对联动状态'
      },
      {
        key: 'completed',
        title: '已完成处置对象',
        desc: '最后回看已完成处置对象，确认区域统计和协同口径是否已恢复稳定。',
        count: valueOrDefault(summaryData.value.completedCount, countBy(item => item.disposalStatus === '3')),
        unit: '条',
        actionText: '抽查闭环结果'
      }
    ]
  }

  if (roleView.value === 'insurer') {
    return [
      {
        key: 'unwarned',
        title: '未预警对象',
        desc: '保险协同先盯住未进预警联动的漏保对象，避免风险暴露晚于业务感知。',
        count: valueOrDefault(summaryData.value.unwarnedCount, countBy(item => item.warningStatus === '0')),
        unit: '条',
        actionText: '先看联动缺口'
      },
      {
        key: 'highSalary',
        title: '高收入漏保对象',
        desc: '高收入对象往往同时具备较高暴露度，应优先进入保险侧筛查。',
        count: valueOrDefault(summaryData.value.highSalaryCount, countBy(item => Number(item.salaryAmount || 0) >= 10000)),
        unit: '条',
        actionText: '复核高暴露对象'
      },
      {
        key: 'pending',
        title: '待核查/核查中',
        desc: '持续处置中的对象需要确认是否真正推进，而不是只停留在台账状态。',
        count: valueOrDefault(summaryData.value.pendingCount, countBy(item => ['0', '1'].includes(item.disposalStatus))),
        unit: '条',
        actionText: '跟踪处置进度'
      },
      {
        key: 'completed',
        title: '已完成处置对象',
        desc: '回看完成处置对象可用于抽查保险协同结果是否真正回写到位。',
        count: valueOrDefault(summaryData.value.completedCount, countBy(item => item.disposalStatus === '3')),
        unit: '条',
        actionText: '抽查闭环质量'
      }
    ]
  }

  return [
    {
      key: 'unwarned',
      title: '未预警对象',
      desc: '应急监管先看未进联动预警的漏保对象，避免高风险对象仍停留在静态台账。',
      count: valueOrDefault(summaryData.value.unwarnedCount, countBy(item => item.warningStatus === '0')),
      unit: '条',
      actionText: '优先补预警'
    },
    {
      key: 'pending',
      title: '待核查/核查中',
      desc: '持续积压对象是督办重点，需要尽快推动企业进入整改或核查收口。',
      count: valueOrDefault(summaryData.value.pendingCount, countBy(item => ['0', '1'].includes(item.disposalStatus))),
      unit: '条',
      actionText: '推进督办闭环'
    },
    {
      key: 'highSalary',
      title: '高收入漏保对象',
      desc: '高收入对象一旦长期漏保，往往意味着更高的风险暴露和更强的治理优先级。',
      count: valueOrDefault(summaryData.value.highSalaryCount, countBy(item => Number(item.salaryAmount || 0) >= 10000)),
      unit: '条',
      actionText: '回看高暴露对象'
    },
    {
      key: 'completed',
      title: '已完成处置对象',
      desc: '抽查已完成处置对象，确认是否完成回写、预警联动和台账收口。',
      count: valueOrDefault(summaryData.value.completedCount, countBy(item => item.disposalStatus === '3')),
      unit: '条',
      actionText: '抽查闭环结果'
    }
  ]
})

const resolvedFocusQueues = computed(() => focusQueues.value.map((item, index) => {
  const explanation = resolvePortalExplanationItem(index)
  return {
    ...item,
    title: explanation?.dimensionName || item.title,
    desc: resolveExplanationFirstText(item.desc, explanation),
    actionText: resolveExplanationFirstActionText(item.actionText, explanation)
  }
}))

const fallbackPortalExplanations = computed(() => focusQueues.value.map(item => ({
  key: item.key,
  dimensionName: item.title,
  currentValue: item.count,
  targetValue: '-',
  summary: item.desc,
  evidenceModule: 'uninsuredList',
  recommendModule: 'uninsuredList',
  defaultQuery: buildUninsuredListExplanationQuery({ focusKey: item.key }),
  sourceLabel: '6.1 漏保治理解释',
  sourceDescription: item.desc,
  actionText: item.actionText
})))

const portalExplanations = computed(() => (Array.isArray(summaryData.value.azbExplanation) && summaryData.value.azbExplanation.length
  ? summaryData.value.azbExplanation
  : fallbackPortalExplanations.value))

const portalExplanationItems = computed(() => decoratePortalExplanationItems(portalExplanations.value, {
  portalCode: 'azb',
  panelTitle: '6.1 漏保治理解释',
  panelDescription: '当前解释项按 6.1 治理链口径展示漏保对象、督办焦点和下步处置方向。'
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
    extras.push({ label: `6.1 ${label}`, type: 'warning' })
  }
  if (portalExplanationSummary.value) {
    extras.push({ label: portalExplanationSummary.value, type: 'success' })
  }
  return [...extras, ...tags].slice(0, 4)
}

const activeFocus = computed(() => focusQueues.value.find(item => item.key === activeFocusKey.value) || focusQueues.value[0] || null)

const visibleUninsuredList = computed(() => sortUninsuredList(uninsuredList.value, activeFocus.value?.key))

const selectedOverview = computed(() => {
  if (!currentRow.value) {
    return [
      { label: '企业', value: '-' },
      { label: '处置状态', value: '-' },
      { label: '预警状态', value: '-' },
      { label: '工资金额', value: '-' },
      { label: '统计月份', value: '-' }
    ]
  }
  return [
    { label: '企业', value: currentRow.value.enterpriseName || '-' },
    { label: '处置状态', value: optionLabel(disposalStatusOptions, currentRow.value.disposalStatus) },
    { label: '预警状态', value: optionLabel(warningStatusOptions, currentRow.value.warningStatus) },
    { label: '工资金额', value: formatMoney(currentRow.value.salaryAmount) },
    { label: '统计月份', value: currentRow.value.statMonth || '-' }
  ]
})

const workflowSteps = computed(() => {
  if (roleView.value === 'bank') {
    return [
      { label: '锁定高暴露对象', desc: '先看高收入漏保和长期积压对象。' },
      { label: '确认是否影响口径', desc: '回看企业、月份和处置状态是否影响区域筛查或统计报表。' },
      { label: '下钻详情复核', desc: '核对识别原因、预警状态和工资金额。' },
      { label: '回到只读协同页', desc: '必要时再联动统计报表和预警总览做交叉判断。' }
    ]
  }
  if (roleView.value === 'insurer') {
    return [
      { label: '锁定未预警对象', desc: '先看尚未进联动预警的漏保对象。' },
      { label: '确认高暴露与积压', desc: '回看高收入和长期待核查对象是否需要优先督办。' },
      { label: '下钻详情复核', desc: '核对识别原因、处置状态和整改进展。' },
      { label: '回到保险协同链', desc: '再联动安责险、预防资金和高危作业页面做只读筛查。' }
    ]
  }
  return [
    { label: '锁定高风险缺口', desc: '先看未预警、待核查和高收入漏保对象。' },
    { label: '确认督办状态', desc: '核对当前记录是待核查、核查中还是已进入整改回写。' },
    { label: '进入处置闭环', desc: '对仍在途对象直接录入处置说明和阶段状态。' },
    { label: '抽查闭环质量', desc: '最后回看已完成处置对象，确认预警和台账都已收口。' }
  ]
})

const currentActionSummary = computed(() => describeAction(currentRow.value, roleView.value))
const currentActionTags = computed(() => buildDetailTags(currentRow.value, roleView.value, activeFocus.value))
const hintTags = computed(() => buildDetailTags(currentRow.value || visibleUninsuredList.value[0], roleView.value, activeFocus.value))
const detailTags = computed(() => buildDetailTags(detailRow.value || currentRow.value, roleView.value, activeFocus.value))
const primaryAction = computed(() => resolvePrimaryAction(currentRow.value, roleView.value))
const resolvedWorkflowSteps = computed(() => workflowSteps.value.map((item, index) => {
  const explanation = resolvePortalExplanationItem(index)
  return {
    ...item,
    label: explanation?.dimensionName || explanation?.moduleLabel || explanation?.moduleCode || item.label,
    desc: resolveExplanationFirstText(item.desc, explanation)
  }
}))
const resolvedCurrentActionSummary = computed(() => resolveExplanationFirstText(
  currentActionSummary.value,
  leadingPortalExplanation.value
))
const resolvedCurrentActionTags = computed(() => buildExplanationFirstTags(currentActionTags.value))
const resolvedHintTags = computed(() => buildExplanationFirstTags(hintTags.value))
const resolvedDetailTags = computed(() => buildExplanationFirstTags(detailTags.value))
const resolvedPrimaryAction = computed(() => ({
  ...primaryAction.value,
  label: resolveExplanationFirstActionText(primaryAction.value.label, leadingPortalExplanation.value)
}))
const focusTableHint = computed(() => activeFocus.value?.desc || '按当前焦点优先暴露最需要督办的漏保对象。')

const readOnlyAlertTitle = computed(() => `${readOnlyRoleLabel.value}：当前仅保留漏保清单查看、详情和导出`)

const readOnlyAlertDescription = computed(() => {
  if (isInsurerRole.value) {
    return `${readOnlyRoleDescription.value} 当前页面聚焦保险侧筛查、高风险对象复核和处置状态跟踪，不展示清单生成和处置动作。`
  }
  if (isBankRole.value) {
    return `${readOnlyRoleDescription.value} 当前页面仅作为跨模块菜单收口兜底，不承担漏保清单生成和督办处置录入。`
  }
  return readOnlyRoleDescription.value
})

const workbenchContext = computed(() => buildWorkbenchContext(route.query, {
  fields: uninsuredWorkbenchFields,
  title: '当前漏保页面沿用了工作台来源条件',
  description: '当前列表保留了上游工作台带入的企业、区域或统计月份范围，便于继续筛查漏保闭环。',
  fieldLabels: {
    enterpriseId: '企业',
    statMonth: '统计月份',
    regionCode: '区域',
    focusKey: '焦点队列'
  },
  fieldFormatters: {
    enterpriseId: value => enterpriseOptions.value.find(item => item.enterpriseId === value)?.enterpriseName || value,
    regionCode: value => formatRegionName(value, value),
    focusKey: value => uninsuredListFocusLabel(value)
  }
}))

init()


watchEffect(() => {
  setPageGuide({
    title: '?????????' || '?????????',
    description: '?????????????????????????????????' || '?????????????????????????????????',
    portalExplanation: portalExplanationItems.value,
    focus: resolvedFocusQueues.value,
    selection: [...selectedOverview.value, { label: '??????', value: currentActionSummary.value }],
    workflow: resolvedWorkflowSteps.value,
    hints: [...currentActionTags.value, ...resolvedHintTags.value].slice(0, 6)
  })
})

function resetQuery() {
  proxy.resetForm('queryRef')
  Object.assign(queryParams.value, {
    pageNum: 1,
    pageSize: 10,
    statMonth: undefined,
    regionCode: undefined,
    enterpriseId: undefined,
    personName: undefined,
    disposalStatus: undefined,
    warningStatus: undefined
  })
  applyWorkbenchRouteQuery(route.query, queryParams.value, uninsuredWorkbenchFields)
  activeFocusKey.value = resolveUninsuredListFocusKey(route.query.focusKey)
  getList()
}

function clearWorkbenchContext() {
  activeFocusKey.value = ''
  Object.assign(queryParams.value, {
    pageNum: 1,
    pageSize: 10,
    statMonth: undefined,
    regionCode: undefined,
    enterpriseId: undefined,
    personName: undefined,
    disposalStatus: undefined,
    warningStatus: undefined
  })
  router.replace({
    path: route.path,
    query: stripWorkbenchRouteQuery(route.query, uninsuredWorkbenchFields)
  })
  currentRow.value = undefined
  syncCurrentRow()
  getList()
}

function applyUninsuredListWorkbenchQuery(routeQuery = {}) {
  Object.assign(queryParams.value, {
    pageNum: 1,
    pageSize: 10,
    statMonth: undefined,
    regionCode: undefined,
    enterpriseId: undefined,
    personName: undefined,
    disposalStatus: undefined,
    warningStatus: undefined
  })
  applyWorkbenchRouteQuery(routeQuery, queryParams.value, uninsuredWorkbenchFields)
  activeFocusKey.value = resolveUninsuredListFocusKey(routeQuery.focusKey)
  currentRow.value = undefined
  syncCurrentRow()
  getList()
}

function handlePortalExplanationAction(action) {
  if (action?.path === route.path) {
    applyUninsuredListWorkbenchQuery(action.query || {})
  }
  openPortalExplanationAction(router, action)
}

function countBy(predicate) {
  return uninsuredList.value.filter(item => predicate(item || {})).length
}

function handleSelectFocus(item) {
  activeFocusKey.value = item.key
  currentRow.value = undefined
  syncCurrentRow()
}

function handlePrimaryAction() {
  if (!currentRow.value) {
    return
  }
  if (primaryAction.value.kind === 'handle') {
    if (isReadOnlyRole.value) {
      proxy.$modal.msgWarning(`${readOnlyRoleLabel.value}仅保留摘要、详情和导出，不能处置漏保对象`)
      return
    }
    openHandleDialog(currentRow.value)
    return
  }
  openDetail(currentRow.value)
}

function sortUninsuredList(list, focusKey) {
  return [...(list || [])].sort((left, right) => scoreUninsured(right, focusKey) - scoreUninsured(left, focusKey))
}

function scoreUninsured(row, focusKey) {
  if (!row) {
    return 0
  }
  let score = 0
  if (row.warningStatus === '0') score += 40
  if (['0', '1'].includes(row.disposalStatus)) score += 36
  if (Number(row.salaryAmount || 0) >= 10000) score += 28
  if (!row.remark) score += 8

  switch (focusKey) {
    case 'unwarned':
      score += row.warningStatus === '0' ? 120 : 0
      break
    case 'pending':
      score += ['0', '1'].includes(row.disposalStatus) ? 120 : 0
      break
    case 'highSalary':
      score += Number(row.salaryAmount || 0) >= 10000 ? 120 : 0
      break
    case 'completed':
      score += row.disposalStatus === '3' ? 120 : 0
      break
    default:
      break
  }

  return score
}

function resolvePrimaryAction(row) {
  if (!row) {
    return { kind: 'detail', label: isReadOnlyRole.value ? '查看详情' : '选择对象' }
  }
  if (isReadOnlyRole.value) {
    return { kind: 'detail', label: '查看详情' }
  }
  if (row.warningStatus === '0' || ['0', '1'].includes(row.disposalStatus)) {
    return { kind: 'handle', label: '进入处置' }
  }
  return { kind: 'detail', label: '查看详情' }
}

function describeAction(row, currentRoleView) {
  if (!row) {
    return '先从左侧焦点队列切入，再选择一条重点记录进入督办或复核。'
  }
  if (currentRoleView === 'bank') {
    return `建议先复核 ${row.personName || '该对象'} 的高收入暴露、处置阶段和所属企业，再确认是否会影响区域筛查与协同报表口径。`
  }
  if (currentRoleView === 'insurer') {
    return `建议先确认 ${row.personName || '该对象'} 是否仍未进入预警联动，再回看高收入暴露和整改进展，供保险侧筛查使用。`
  }
  if (row.warningStatus === '0') {
    return `建议优先把 ${row.personName || '该对象'} 推入预警联动，再继续督办处置闭环。`
  }
  if (['0', '1'].includes(row.disposalStatus)) {
    return `建议尽快更新 ${row.personName || '该对象'} 的处置状态和核查说明，避免长期积压在待办队列。`
  }
  if (Number(row.salaryAmount || 0) >= 10000) {
    return `建议优先复核 ${row.personName || '该对象'} 的高收入漏保原因和整改方案，避免高暴露对象久拖不收口。`
  }
  return `${row.personName || '该对象'} 当前已进入相对稳定状态，可继续抽查识别原因、整改结果和台账回写质量。`
}

function buildDetailTags(row, currentRoleView, focus) {
  if (!row) {
    return [{ label: '未选中记录', type: 'info' }]
  }
  const tags = []
  if (focus?.title) {
    tags.push({ label: `当前焦点：${focus.title}`, type: 'info' })
  }
  if (row.warningStatus === '0') {
    tags.push({ label: '未进入预警联动', type: 'danger' })
  }
  if (['0', '1'].includes(row.disposalStatus)) {
    tags.push({ label: '仍需持续督办', type: 'warning' })
  }
  if (Number(row.salaryAmount || 0) >= 10000) {
    tags.push({ label: '高收入漏保对象', type: 'warning' })
  }
  if (row.disposalStatus === '3') {
    tags.push({ label: '已完成处置', type: 'success' })
  }
  if (currentRoleView === 'insurer' && row.warningStatus === '0') {
    tags.push({ label: '保险协同待介入', type: 'danger' })
  }
  if (currentRoleView === 'bank' && Number(row.salaryAmount || 0) >= 10000) {
    tags.push({ label: '区域筛查重点', type: 'info' })
  }
  if (!tags.length) {
    tags.push({ label: '当前无明显参保风险', type: 'success' })
  }
  return tags
}

function formatMoney(value) {
  return `${formatAmount(value)} 元`
}
activeFocusKey.value = resolveUninsuredListFocusKey(route.query.focusKey)
function resolveUninsuredListFocusKey(value) {
  const normalized = typeof value === 'string' ? value.trim() : ''
  return focusQueues.value.some(item => item.key === normalized) ? normalized : ''
}

function uninsuredListFocusLabel(value) {
  return focusQueues.value.find(item => item.key === value)?.title || value || '-'
}
</script>

<style scoped lang="scss">
.azb-role-alert {
  margin-bottom: 18px;
}

.azb-workbench-alert {
  margin-bottom: 18px;
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
  gap: 8px;
  margin-bottom: 10px;
  color: #4f6478;
  line-height: 1.6;
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
  border: 1px solid #d9e3ee;
  border-radius: 16px;
  background: #fff;
}

.azb-summary-card {
  padding: 18px 20px;
}

.azb-summary-card__label {
  color: #5f7183;
  font-size: 13px;
}

.azb-summary-card__value {
  margin-top: 10px;
  color: #1f2d3d;
  font-size: 28px;
  font-weight: 700;
}

.azb-summary-card__unit {
  margin-left: 4px;
  font-size: 13px;
  font-weight: 500;
  color: #7b8da1;
}

.azb-summary-card__note {
  margin-top: 10px;
  color: #5f6f80;
  font-size: 13px;
  line-height: 1.7;
}

.azb-summary-card--success {
  background: linear-gradient(180deg, #ffffff 0%, #f3fbf5 100%);
}

.azb-summary-card--danger {
  background: linear-gradient(180deg, #ffffff 0%, #fff4f3 100%);
}

.azb-summary-card--warning {
  background: linear-gradient(180deg, #ffffff 0%, #fff9ef 100%);
}

.azb-focus-card :deep(.el-card__header) {
  padding: 18px 20px 0;
  border-bottom: none;
}

.azb-focus-card :deep(.el-card__body) {
  padding: 18px 20px 20px;
}

.azb-card-head__title {
  color: #1f2d3d;
  font-size: 18px;
  font-weight: 700;
}

.azb-card-head__desc {
  margin-top: 4px;
  color: #728395;
  font-size: 13px;
  line-height: 1.7;
}

.azb-focus-list,
.azb-source-list {
  display: grid;
  gap: 12px;
}

.azb-focus-list--single {
  grid-template-columns: 1fr;
}

.azb-focus-item,
.azb-source-item {
  padding: 14px 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  border: 1px solid #dde6ef;
  border-radius: 14px;
  background: #f8fbfd;
}

.azb-focus-item__label,
.azb-source-item__label {
  color: #5f7183;
  font-size: 13px;
}

.azb-focus-item__value,
.azb-source-item__value {
  color: #1f2d3d;
  font-size: 14px;
  font-weight: 600;
  text-align: right;
}

.azb-focus-queue {
  width: 100%;
  padding: 16px 18px;
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto;
  gap: 16px;
  border: 1px solid #dde6ef;
  border-radius: 14px;
  background: #f8fbfd;
  appearance: none;
  text-align: left;
  cursor: pointer;
  transition: border-color 0.2s ease, box-shadow 0.2s ease, transform 0.2s ease;
}

.azb-focus-queue:hover,
.azb-focus-queue.is-active {
  border-color: #9db8d6;
  box-shadow: 0 8px 20px rgba(15, 94, 168, 0.08);
  transform: translateY(-1px);
}

.azb-focus-queue__main strong {
  color: #1f2d3d;
  font-size: 15px;
}

.azb-focus-queue__main p {
  margin: 6px 0 0;
  color: #6f8092;
  font-size: 13px;
  line-height: 1.7;
}

.azb-focus-queue__side {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: flex-end;
  gap: 6px;
}

.azb-focus-queue__count {
  color: #1f2d3d;
  font-size: 22px;
  font-weight: 700;
}

.azb-focus-queue__action {
  color: #0f5ea8;
  font-size: 12px;
  font-weight: 600;
}

.azb-recommend-panel {
  margin-top: 14px;
  padding-top: 14px;
  border-top: 1px dashed #d6e2ee;
}

.azb-recommend-panel__title {
  color: #1f2d3d;
  font-size: 14px;
  font-weight: 700;
}

.azb-recommend-panel__summary {
  margin-top: 8px;
  color: #516273;
  font-size: 13px;
  line-height: 1.8;
}

.azb-focus-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 14px;
}

.azb-pipeline-list {
  display: grid;
  gap: 12px;
}

.azb-pipeline-item {
  display: grid;
  grid-template-columns: 34px minmax(0, 1fr);
  gap: 12px;
  align-items: start;
  padding: 14px 16px;
  border: 1px solid #dde6ef;
  border-radius: 14px;
  background: #f8fbfd;
}

.azb-pipeline-item__index {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  height: 34px;
  border-radius: 10px;
  background: #eaf2fb;
  color: #0f5ea8;
  font-size: 13px;
  font-weight: 700;
}

.azb-pipeline-item__body strong {
  color: #1f2d3d;
  font-size: 14px;
}

.azb-pipeline-item__body p {
  margin: 6px 0 0;
  color: #6f8092;
  font-size: 13px;
  line-height: 1.7;
}

.azb-card-head--between {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: flex-start;
}

.azb-detail-block {
  margin-top: 20px;
}

.azb-detail-block h3 {
  margin: 0 0 12px;
  color: #1f2d3d;
  font-size: 16px;
}

.azb-risk-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

@media (max-width: 1200px) {
  .azb-summary-grid,
  .azb-focus-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 768px) {
  .azb-summary-grid,
  .azb-focus-grid {
    grid-template-columns: 1fr;
  }

  .azb-focus-queue {
    grid-template-columns: 1fr;
  }

  .azb-card-head--between {
    flex-direction: column;
  }

  .azb-focus-queue__side {
    align-items: flex-start;
  }
}
</style>
