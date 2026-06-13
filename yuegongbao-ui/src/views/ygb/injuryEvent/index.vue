<template>
  <div class="app-container ygb-page">
    <section class="gov-page-header ygb-page__header">
      <div>
        <p class="ygb-page__eyebrow">工伤监管链路</p>
        <h1 class="ygb-page__title">工伤事件办理闭环台账</h1>
        <p class="ygb-page__desc">
          面向企业管理、经办审核和监管协同统一查看事故上报、工伤认定、待遇申领和办结归档，
          在同一筛选口径下联动统计分析、监测专题和认定辅助。
        </p>
      </div>
      <div class="ygb-table-tip">
        审批截止日持续反算剩余天数，超期或长期未推进对象自动进入预警口径。
        本页保留原有办理链路，同时补齐统计分析、工伤监测和认定辅助闭环。
      </div>
    </section>

    <el-alert
      v-if="isReadOnlyRole"
      class="ygb-workbench-alert"
      type="warning"
      :closable="false"
      show-icon
      :title="`${readOnlyRoleLabel} 当前以只读视图访问`"
      :description="readOnlyRoleDescription"
    />

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
        <el-form-item label="区域">
          <el-select v-model="queryParams.regionCode" clearable style="width: 180px">
            <el-option v-for="item in regionOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="企业">
          <el-select v-model="queryParams.enterpriseId" clearable filterable style="width: 220px">
            <el-option
              v-for="item in enterpriseOptions"
              :key="item.enterpriseId"
              :label="item.enterpriseName"
              :value="item.enterpriseId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="人员">
          <el-input v-model="queryParams.personName" clearable style="width: 180px" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="事件状态">
          <el-select v-model="queryParams.injuryStatus" clearable style="width: 160px">
            <el-option v-for="item in injuryStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="预警状态">
          <el-select v-model="queryParams.warningStatus" clearable style="width: 160px">
            <el-option v-for="item in warningStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
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
        <el-col :span="1.5">
          <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['ygb:injuryEvent:add']">????</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['ygb:injuryEvent:edit']">????</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="warning" plain icon="Promotion" :disabled="single" @click="openStatusDialog()" v-hasPermi="['ygb:injuryEvent:flow']">????</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['ygb:injuryEvent:remove']">????</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['ygb:injuryEvent:export']">????</el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>
    </el-card>

    <el-card class="table-card ygb-table-card" shadow="never">
      <el-table v-loading="loading" :data="visibleEventList" @selection-change="handleSelectionChange" @row-click="handleRowClick">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="事件ID" prop="eventId" width="100" />
        <el-table-column label="人员" prop="personName" width="120" />
        <el-table-column label="企业名称" prop="enterpriseName" min-width="220" />
        <el-table-column label="区域" width="140">
          <template #default="scope">{{ formatRegionName(scope.row.regionCode) }}</template>
        </el-table-column>
        <el-table-column label="事故日期" width="120">
          <template #default="scope">{{ parseTime(scope.row.eventDate, '{y}-{m}-{d}') }}</template>
        </el-table-column>
        <el-table-column label="事故地点" prop="injuryLocation" min-width="200" show-overflow-tooltip />
        <el-table-column label="受伤部位" prop="injuryPart" width="120" />
        <el-table-column label="事件状态" width="120">
          <template #default="scope">
            <dict-tag :options="injuryStatusOptions" :value="scope.row.injuryStatus" />
          </template>
        </el-table-column>
        <el-table-column label="剩余天数" width="100">
          <template #default="scope">{{ remainingDaysText(scope.row.remainingDays) }}</template>
        </el-table-column>
        <el-table-column label="预警状态" width="120">
          <template #default="scope">
            <dict-tag :options="warningStatusOptions" :value="scope.row.warningStatus" />
          </template>
        </el-table-column>
        <el-table-column class-name="table-fill-column" min-width="1" />
        <el-table-column label="操作" fixed="right" align="center" width="260">
          <template #default="scope">
            <el-button link type="info" icon="View" @click.stop="openDetail(scope.row)">详情</el-button>
            <el-button link type="primary" icon="Edit" @click.stop="handleUpdate(scope.row)" v-hasPermi="['ygb:injuryEvent:edit']">修改</el-button>
            <el-button link type="primary" icon="Promotion" @click.stop="openStatusDialog(scope.row)" v-hasPermi="['ygb:injuryEvent:flow']">流转</el-button>
            <el-button link type="danger" icon="Delete" @click.stop="handleDelete(scope.row)" v-hasPermi="['ygb:injuryEvent:remove']">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>

    <el-dialog :title="title" v-model="open" width="860px" append-to-body>
      <el-form ref="injuryRef" :model="form" :rules="rules" label-width="110px">
        <div class="ygb-form-grid">
          <el-form-item label="企业" prop="enterpriseId">
            <el-select v-model="form.enterpriseId" filterable placeholder="请选择企业" @change="handleEnterpriseChange">
              <el-option
                v-for="item in enterpriseOptions"
                :key="item.enterpriseId"
                :label="item.enterpriseName"
                :value="item.enterpriseId"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="人员" prop="personId">
            <el-select v-model="form.personId" filterable placeholder="请选择人员">
              <el-option
                v-for="item in formPersonOptions"
                :key="item.personId"
                :label="`${item.personName} / ${item.enterpriseName || ''}`"
                :value="item.personId"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="事故日期" prop="eventDate">
            <el-date-picker v-model="form.eventDate" type="date" format="YYYY-MM-DD" value-format="YYYY-MM-DD" style="width: 100%" />
          </el-form-item>
          <el-form-item label="上报时间" prop="reportTime">
            <el-date-picker
              v-model="form.reportTime"
              type="datetime"
              format="YYYY-MM-DD HH:mm:ss"
              value-format="YYYY-MM-DD HH:mm:ss"
              style="width: 100%"
            />
          </el-form-item>
          <el-form-item label="受伤部位" prop="injuryPart">
            <el-input v-model="form.injuryPart" placeholder="请输入受伤部位" />
          </el-form-item>
          <el-form-item label="事件状态" prop="injuryStatus">
            <el-select v-model="form.injuryStatus" placeholder="请选择事件状态">
              <el-option v-for="item in injuryStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="审批截止日" prop="approvalDeadline">
            <el-date-picker
              v-model="form.approvalDeadline"
              type="date"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              style="width: 100%"
            />
          </el-form-item>
        </div>
        <el-form-item label="事故地点" prop="injuryLocation">
          <el-input v-model="form.injuryLocation" placeholder="请输入事故地点" />
        </el-form-item>
        <el-form-item label="诊断证明" prop="diagnosisUrl">
          <el-input v-model="form.diagnosisUrl" placeholder="请输入诊断证明链接" />
        </el-form-item>
        <el-form-item label="审批结果" prop="approvalResult">
          <el-input v-model="form.approvalResult" type="textarea" :rows="3" placeholder="请输入审批结果或办理说明" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确定</el-button>
          <el-button @click="cancel">取消</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog title="工伤状态流转" v-model="statusOpen" width="520px" append-to-body>
      <el-form ref="statusRef" :model="statusForm" :rules="statusRules" label-width="100px">
        <el-form-item label="事件状态" prop="injuryStatus">
          <el-select v-model="statusForm.injuryStatus" placeholder="请选择事件状态">
            <el-option v-for="item in injuryStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="审批结果" prop="approvalResult">
          <el-input v-model="statusForm.approvalResult" type="textarea" :rows="4" placeholder="请输入当前流转结论" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitStatus">确定</el-button>
          <el-button @click="statusOpen = false">取消</el-button>
        </div>
      </template>
    </el-dialog>

    <page-detail-dialog v-model="detailOpen" title="工伤事件详情" width="760px">
      <template v-if="detailEvent">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="人员">{{ detailEvent.personName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="企业">{{ detailEvent.enterpriseName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="区域">{{ formatRegionName(detailEvent.regionCode) }}</el-descriptions-item>
          <el-descriptions-item label="事件状态">{{ injuryStatusLabel(detailEvent.injuryStatus) }}</el-descriptions-item>
          <el-descriptions-item label="事故日期">{{ parseTime(detailEvent.eventDate, '{y}-{m}-{d}') }}</el-descriptions-item>
          <el-descriptions-item label="上报时间">{{ parseTime(detailEvent.reportTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</el-descriptions-item>
          <el-descriptions-item label="审批截止日">{{ parseTime(detailEvent.approvalDeadline, '{y}-{m}-{d}') }}</el-descriptions-item>
          <el-descriptions-item label="剩余天数">{{ remainingDaysText(detailEvent.remainingDays) }}</el-descriptions-item>
          <el-descriptions-item label="预警状态">{{ warningStatusLabel(detailEvent.warningStatus) }}</el-descriptions-item>
          <el-descriptions-item label="受伤部位">{{ detailEvent.injuryPart || '-' }}</el-descriptions-item>
          <el-descriptions-item label="事故地点" :span="2">{{ detailEvent.injuryLocation || '-' }}</el-descriptions-item>
          <el-descriptions-item label="诊断证明" :span="2">{{ detailEvent.diagnosisUrl || '-' }}</el-descriptions-item>
          <el-descriptions-item label="审批结果" :span="2">{{ detailEvent.approvalResult || '-' }}</el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ detailEvent.remark || '-' }}</el-descriptions-item>
        </el-descriptions>

        <div class="ygb-detail-block">
          <h3>办理提示</h3>
          <div class="ygb-tag-list">
            <el-tag v-for="item in resolvedDetailHintTags" :key="item.label" :type="item.type" effect="plain">
              {{ item.label }}
            </el-tag>
          </div>
        </div>
      </template>
    </page-detail-dialog>

    <page-detail-dialog v-model="recognitionAssistDetailOpen" title="工伤认定辅助详情" width="720px">
      <template v-if="recognitionAssistDetail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="事件ID">{{ recognitionAssistDetail.recordId || '-' }}</el-descriptions-item>
          <el-descriptions-item label="人员">{{ recognitionAssistDetail.personName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="企业">{{ recognitionAssistDetail.enterpriseName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="区域">{{ formatRegionName(recognitionAssistDetail.regionCode) }}</el-descriptions-item>
          <el-descriptions-item label="流程状态">
            <el-tag :type="workflowStatusTagType(recognitionAssistDetail.workflowStatus)" effect="plain">
              {{ workflowStatusLabel(recognitionAssistDetail.workflowStatus) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="来源">{{ recognitionAssistDetail.sourceLabel || '-' }}</el-descriptions-item>
          <el-descriptions-item label="说明" :span="2">{{ recognitionAssistDetail.remark || '-' }}</el-descriptions-item>
        </el-descriptions>
      </template>
    </page-detail-dialog>
  </div>
</template>

<script setup name="YgbInjuryEvent">
import { computed, getCurrentInstance, ref, watchEffect } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useWorkbenchAssist } from '@/composables/useWorkbenchAssist'
import {
  getInjuryEvent,
  getInjuryEventAnalysis,
  getInjuryEventMonitor,
  getRecognitionAssist,
  listRecognitionAssist
} from '@/api/ygb/injuryEvent'
import { decoratePortalExplanationItems, openPortalExplanationAction, resolvePortalExplanationSummary } from '@/utils/portalExplanation'
import { applyWorkbenchRouteQuery, buildWorkbenchContext, stripWorkbenchRouteQuery } from '@/utils/workbenchLink'
import { useRoleViewMode } from '@/utils/roleView'
import { useAuthorizedRegionOptions } from '@/utils/regionScope'
import {
  formatRegionName,
  injuryStatusLabel,
  injuryStatusOptions,
  prioritizeFocusRows,
  remainingDaysText,
  regionOptions as allRegionOptions,
  useInjuryEventPage,
  valueOrDefault,
  warningStatusLabel,
  warningStatusOptions
} from '@/views/injuryEvent/useInjuryEventPage'

const { proxy } = getCurrentInstance()
const regionOptions = useAuthorizedRegionOptions(allRegionOptions)
const route = useRoute()
const { setPageGuide } = useWorkbenchAssist()
const router = useRouter()
const { isReadOnlyRole, readOnlyRoleLabel, readOnlyRoleDescription } = useRoleViewMode()

const injuryEventWorkbenchFields = ['regionCode', 'enterpriseId', 'injuryStatus', 'warningStatus', 'focusKey']
const injuryEventInitialQuery = {}
applyWorkbenchRouteQuery(route.query, injuryEventInitialQuery, injuryEventWorkbenchFields)

const activeFocusKey = ref('')
const injuryEventAnalysis = ref({})
const injuryEventMonitor = ref({})
const recognitionAssistRows = ref([])
const recognitionAssistDetailOpen = ref(false)
const recognitionAssistDetail = ref(null)

const workflowSteps = [
  { label: '事故上报', desc: '先完成事故时间、地点、受伤部位和基础材料上报，形成后续认定办理底稿。' },
  { label: '工伤认定', desc: '按认定进度持续回写状态和审批结论，确保超期风险能被及时识别。' },
  { label: '待遇申领', desc: '认定后进入待遇申领阶段，补齐材料、时限和办理说明，减少来回退件。' },
  { label: '办结归档', desc: '事件办结后沉淀审批结果、预警处置和归档记录，形成可复核台账。' }
]

const {
  injuryEventList,
  enterpriseOptions,
  formPersonOptions,
  open,
  statusOpen,
  detailOpen,
  loading,
  showSearch,
  single,
  multiple,
  total,
  title,
  currentEvent,
  detailEvent,
  summaryData,
  queryParams,
  form,
  statusForm,
  rules,
  statusRules,
  getList,
  cancel,
  handleQuery,
  resetQuery: baseResetQuery,
  handleSelectionChange,
  handleRowClick,
  handleAdd,
  handleUpdate,
  openDetail,
  handleEnterpriseChange,
  submitForm,
  openStatusDialog,
  submitStatus,
  handleDelete,
  handleExport
} = useInjuryEventPage({
  exportFilePrefix: 'ygb_injury_event',
  initialQueryParams: injuryEventInitialQuery,
  resetQueryPatch: () => {
    const patch = {}
    applyWorkbenchRouteQuery(route.query, patch, injuryEventWorkbenchFields)
    return patch
  },
  canMutate: () => !isReadOnlyRole.value,
  onBlockedAction: actionLabel => {
    proxy.$modal.msgWarning(`${readOnlyRoleLabel.value} read-only: ${actionLabel}`)
    return `${readOnlyRoleLabel.value} read-only: ${actionLabel}`
  },
  getCurrentList: () => visibleEventList.value,
  onAfterLoad: () => loadInjuryAggregateBlocks()
})

function buildInjuryAggregateQuery() {
  return {
    regionCode: queryParams.value.regionCode,
    enterpriseId: queryParams.value.enterpriseId,
    personName: queryParams.value.personName,
    injuryStatus: queryParams.value.injuryStatus,
    warningStatus: queryParams.value.warningStatus
  }
}

function buildInjuryEventExplanationQuery(extraQuery = {}) {
  return {
    ...buildInjuryAggregateQuery(),
    ...extraQuery
  }
}

const workbenchContext = computed(() => buildWorkbenchContext(route.query, {
  fields: injuryEventWorkbenchFields,
  sourceLabel: '粤工保工作台',
  title: '当前工伤事件页沿用了首页来源条件',
  description: '列表已按首页工作台带入的区域或企业条件锁定，适合继续处理当前来源场景下的工伤事件。',
  fieldLabels: {
    injuryStatus: '事件状态',
    warningStatus: '预警状态',
    focusKey: '解释焦点',
    regionCode: '区域',
    enterpriseId: '企业'
  },
  fieldFormatters: {
    injuryStatus: value => injuryStatusLabel(value),
    warningStatus: value => warningStatusLabel(value),
    focusKey: value => injuryEventFocusLabel(value),
    regionCode: value => formatRegionName(value),
    enterpriseId: value => enterpriseName(value) || value
  }
}))

const summaryCards = computed(() => ([
  {
    key: 'total',
    label: '事件记录',
    value: valueOrDefault(summaryData.value.totalCount, total.value),
    unit: '件',
    note: '当前筛选范围内纳入办理台账的工伤事件总量。',
    cardClass: ''
  },
  {
    key: 'pending',
    label: '未办结事件',
    value: valueOrDefault(summaryData.value.pendingCount, 0),
    unit: '件',
    note: '仍处于上报、认定或待遇申领阶段，需要持续推进的事件数量。',
    cardClass: 'ygb-summary-card--warning'
  },
  {
    key: 'warning',
    label: '已预警事件',
    value: valueOrDefault(summaryData.value.warningCount, 0),
    unit: '件',
    note: '已进入预警口径或存在明显办理风险的事件数量。',
    cardClass: 'ygb-summary-card--primary'
  },
  {
    key: 'overdue',
    label: '超期风险',
    value: valueOrDefault(summaryData.value.overdueCount, 0),
    unit: '件',
    note: '剩余天数已为负值的事件，建议优先核查当前承办环节。',
    cardClass: 'ygb-summary-card--success'
  }
]))

const focusItems = computed(() => ([
  { label: '当前区域', value: queryParams.value.regionCode ? formatRegionName(queryParams.value.regionCode) : '全部区域' },
  { label: '认定处理中', value: `${valueOrDefault(summaryData.value.recognizingCount, 0)} 件` },
  { label: '待遇申领中', value: `${valueOrDefault(summaryData.value.claimingCount, 0)} 件` },
  { label: '已办结事件', value: `${valueOrDefault(summaryData.value.finishedCount, 0)} 件` }
]))

const focusQueues = computed(() => ([
  {
    key: 'warning',
    title: '预警事件',
    count: valueOrDefault(summaryData.value.warningCount, 0),
    desc: '预警事件应优先回看认定、申领和材料补充进度，避免进一步延误。'
  },
  {
    key: 'overdue',
    title: '超期事件',
    count: valueOrDefault(summaryData.value.overdueCount, 0),
    desc: '超期事件需要尽快推进回写结论或变更流转状态。'
  },
  {
    key: 'recognizing',
    title: '认定处理中事件',
    count: valueOrDefault(summaryData.value.recognizingCount, 0),
    desc: '认定阶段事件适合作为工伤办理主链的跟踪入口。'
  },
  {
    key: 'claiming',
    title: '待遇申领中事件',
    count: valueOrDefault(summaryData.value.claimingCount, 0),
    desc: '待遇申领对象需要持续关注补材料和回执状态。'
  }
]))

const portalExplanations = computed(() => {
  if (Array.isArray(summaryData.value.ygbExplanation) && summaryData.value.ygbExplanation.length) {
    return summaryData.value.ygbExplanation
  }
  return focusQueues.value.map(item => ({
    key: item.key,
    dimensionName: item.title,
    currentValue: item.count,
    targetValue: '0',
    summary: item.desc,
    evidenceModule: 'injuryEvent',
    recommendModule: 'injuryEvent',
    defaultQuery: buildInjuryEventExplanationQuery(buildInjuryEventFocusQuery(item.key)),
    sourceLabel: '530.1 工伤事件办理解释',
    sourceDescription: item.desc,
    actionText: '应用筛选'
  }))
})

const portalExplanationItems = computed(() => decoratePortalExplanationItems(portalExplanations.value, {
  portalCode: 'ygb',
  panelTitle: '530.1 工伤事件办理解释',
  panelDescription: '当前解释项按工伤办理链路展示承接、认定和闭环重点。'
}))
const portalExplanationSummary = computed(() => resolvePortalExplanationSummary(portalExplanationItems.value, ''))
const leadingPortalExplanation = computed(() => portalExplanationItems.value[0] || null)

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

const activeFocus = computed(() => focusQueues.value.find(item => item.key === activeFocusKey.value) || focusQueues.value[0] || null)
const visibleEventList = computed(() => prioritizeFocusRows(
  injuryEventList.value,
  row => matchInjuryEventFocus(row, activeFocus.value?.key)
))

const analysisCards = computed(() => ([
  { key: 'total', label: '统计对象', value: valueOrDefault(injuryEventAnalysis.value.totalCount, total.value), unit: '件', note: '纳入统计分析的事件总量。' },
  { key: 'overdue', label: '超期对象', value: valueOrDefault(injuryEventAnalysis.value.overdueCount, 0), unit: '件', note: '审批截止日已超期的重点对象。' },
  { key: 'recognizing', label: '认定处理中', value: valueOrDefault(injuryEventAnalysis.value.recognizingCount, 0), unit: '件', note: '仍处于认定链路中的对象。' },
  { key: 'claiming', label: '待遇申领中', value: valueOrDefault(injuryEventAnalysis.value.claimingCount, 0), unit: '件', note: '需要继续补材料和回写结果的对象。' }
]))

const analysisStatusStats = computed(() => normalizeDimensionStats(
  injuryEventAnalysis.value.statusStats,
  item => injuryStatusLabel(item.dimension)
))
const analysisWarningStats = computed(() => normalizeDimensionStats(
  injuryEventAnalysis.value.warningStats,
  item => String(item.dimension) === 'warning' ? '预警对象' : '正常对象'
))
const analysisRegionStats = computed(() => normalizeDimensionStats(
  injuryEventAnalysis.value.regionStats,
  item => formatRegionName(item.dimension)
))
const analysisEnterpriseStats = computed(() => normalizeDimensionStats(
  injuryEventAnalysis.value.enterpriseStats,
  item => item.dimension || '-'
))
const highRiskRegionStats = computed(() => normalizeDimensionStats(
  injuryEventMonitor.value.highRiskRegions,
  item => formatRegionName(item.dimension)
))
const highRiskEnterpriseStats = computed(() => normalizeDimensionStats(
  injuryEventMonitor.value.highRiskEnterprises,
  item => item.dimension || '-'
))
const monitorRows = computed(() => Array.isArray(injuryEventMonitor.value.overdueRows) ? injuryEventMonitor.value.overdueRows : [])
const monitorSummaryCount = computed(() => valueOrDefault(injuryEventMonitor.value.monitorCount, monitorRows.value.length))

const selectedEventOverview = computed(() => {
  const event = currentEvent.value
  if (!event) {
    return [
      { label: '人员', value: '-' },
      { label: '企业', value: '-' },
      { label: '事件状态', value: '-' },
      { label: '剩余天数', value: '-' }
    ]
  }
  return [
    { label: '人员', value: event.personName || '-' },
    { label: '企业', value: event.enterpriseName || '-' },
    { label: '事件状态', value: injuryStatusLabel(event.injuryStatus) },
    { label: '剩余天数', value: remainingDaysText(event.remainingDays) }
  ]
})

const eventHintTags = computed(() => buildYgbHintTags(currentEvent.value))
const detailHintTags = computed(() => buildYgbHintTags(detailEvent.value || currentEvent.value))
const resolvedEventHintTags = computed(() => buildExplanationFirstTags(eventHintTags.value))
const resolvedDetailHintTags = computed(() => buildExplanationFirstTags(detailHintTags.value))

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
    return `${label} 优先`
  }
  return fallback
}

function buildExplanationFirstTags(tags = []) {
  const extras = []
  const label = buildPortalExplanationLabel(leadingPortalExplanation.value)
  if (label) {
    extras.push({ label: `530.1 解释焦点：${label}`, type: 'warning' })
  }
  if (portalExplanationSummary.value) {
    extras.push({ label: portalExplanationSummary.value, type: 'success' })
  }
  return [...extras, ...tags].slice(0, 4)
}

function normalizeDimensionStats(list = [], labelResolver = item => item.dimension || '-') {
  if (!Array.isArray(list)) {
    return []
  }
  return list.map(item => ({
    ...item,
    label: labelResolver(item),
    count: Number(item.count || 0)
  }))
}

function buildInjuryEventFocusQuery(focusKey) {
  if (focusKey === 'warning') {
    return { warningStatus: '1', focusKey }
  }
  if (focusKey === 'recognizing') {
    return { injuryStatus: '1', focusKey }
  }
  if (focusKey === 'claiming') {
    return { injuryStatus: '3', focusKey }
  }
  return { focusKey }
}

function enterpriseName(value) {
  return enterpriseOptions.value.find(item => String(item.enterpriseId) === String(value))?.enterpriseName
}

function workflowStatusLabel(value) {
  if (value === 'draft') return '草稿'
  if (value === 'pending') return '待处理'
  if (value === 'processing') return '处理中'
  if (value === 'closed') return '已关闭'
  if (value === 'rejected') return '已驳回'
  if (value === 'overdue') return '已逾期'
  return value || '-'
}

function workflowStatusTagType(value) {
  if (value === 'closed') return 'success'
  if (value === 'processing') return 'warning'
  if (value === 'overdue' || value === 'rejected') return 'danger'
  return 'info'
}

function resetQuery() {
  activeFocusKey.value = resolveInjuryEventFocusKey(route.query.focusKey)
  baseResetQuery()
}

async function clearWorkbenchContext() {
  activeFocusKey.value = ''
  await router.replace({
    path: route.path,
    query: stripWorkbenchRouteQuery(route.query, injuryEventWorkbenchFields)
  })
  baseResetQuery()
}

function applyInjuryEventWorkbenchQuery(routeQuery = {}) {
  Object.assign(queryParams.value, {
    pageNum: 1,
    pageSize: 10,
    regionCode: undefined,
    enterpriseId: undefined,
    personName: undefined,
    injuryStatus: undefined,
    warningStatus: undefined
  })
  applyWorkbenchRouteQuery(routeQuery, queryParams.value, injuryEventWorkbenchFields)
  activeFocusKey.value = resolveInjuryEventFocusKey(routeQuery.focusKey)
  currentEvent.value = undefined
  getList()

}


function applyAnalysisFilter(type, item) {
  queryParams.value.pageNum = 1
  if (type === 'status') {
    queryParams.value.injuryStatus = item.dimension === 'UNKNOWN' ? undefined : item.dimension
  }
  if (type === 'warning') {
    queryParams.value.warningStatus = item.dimension === 'warning' ? '1' : '0'
  }
  if (type === 'region') {
    queryParams.value.regionCode = item.dimension === '-' ? undefined : item.dimension
  }
  if (type === 'enterprise') {
    const matched = enterpriseOptions.value.find(option => option.enterpriseName === item.dimension)
    queryParams.value.enterpriseId = matched?.enterpriseId
  }
  getList()
}

function applyMonitorFilter(type, item) {
  queryParams.value.pageNum = 1
  queryParams.value.warningStatus = '1'
  if (type === 'region') {
    queryParams.value.regionCode = item.dimension === '-' ? undefined : item.dimension
  }
  if (type === 'enterprise') {
    const matched = enterpriseOptions.value.find(option => option.enterpriseName === item.dimension)
    queryParams.value.enterpriseId = matched?.enterpriseId
  }
  getList()
}

function handlePortalExplanationAction(action) {
  if (action?.path === route.path) {
    applyInjuryEventWorkbenchQuery(action.query || {})
    return
  }
  openPortalExplanationAction(router, action)
}

function injuryEventFocusLabel(value) {
  if (value === 'warning') return '预警事件'
  if (value === 'overdue') return '超期事件'
  if (value === 'recognizing') return '认定处理中'
  if (value === 'claiming') return '待遇申领中'
  return value || '-'
}

function matchInjuryEventFocus(row, focusKey) {
  if (!row || !focusKey) {
    return false
  }
  if (focusKey === 'warning') {
    return String(row.warningStatus || '') === '1'
  }
  if (focusKey === 'overdue') {
    return Number(row.remainingDays) < 0
  }
  if (focusKey === 'recognizing') {
    return String(row.injuryStatus || '') === '1'
  }
  if (focusKey === 'claiming') {
    return String(row.injuryStatus || '') === '3'
  }
  return false
}

watchEffect(() => {
  setPageGuide({
    title: '??????????',
    description: '?????????????????????????????????',
    portalExplanation: portalExplanationItems.value,
    focus: focusQueues.value,
    selection: selectedEventOverview.value,
    workflow: resolvedWorkflowSteps.value,
    hints: resolvedEventHintTags.value
  })
})

activeFocusKey.value = resolveInjuryEventFocusKey(route.query.focusKey)

function resolveInjuryEventFocusKey(value) {
  const normalized = typeof value === 'string' ? value.trim() : ''
  return ['warning', 'overdue', 'recognizing', 'claiming'].includes(normalized) ? normalized : ''
}

function focusMonitorEvent(row) {
  if (!row) {
    return
  }
  queryParams.value.pageNum = 1
  queryParams.value.regionCode = row.regionCode || undefined
  queryParams.value.warningStatus = row.warningStatus || '1'
  queryParams.value.injuryStatus = row.injuryStatus || undefined
  const matchedEnterprise = enterpriseOptions.value.find(option => option.enterpriseName === row.enterpriseName)
  queryParams.value.enterpriseId = matchedEnterprise?.enterpriseId
  activeFocusKey.value = 'overdue'
  getList().then(() => {
    const matched = injuryEventList.value.find(item => String(item.eventId) === String(row.eventId))
    if (matched) {
      handleRowClick(matched)
    }
  })
}

function focusRecognitionAssist(row) {
  const eventId = row?.relatedId || row?.recordId
  if (!eventId) {
    return
  }
  getInjuryEvent(eventId).then(response => {
    const event = response.data || {}
    queryParams.value.pageNum = 1
    queryParams.value.regionCode = event.regionCode || queryParams.value.regionCode
    queryParams.value.enterpriseId = event.enterpriseId || queryParams.value.enterpriseId
    queryParams.value.warningStatus = event.warningStatus || queryParams.value.warningStatus
    queryParams.value.injuryStatus = event.injuryStatus || queryParams.value.injuryStatus
    getList().then(() => {
      const matched = injuryEventList.value.find(item => String(item.eventId) === String(eventId))
      if (matched) {
        handleRowClick(matched)
      }
    })
  })
}

function showRecognitionAssistDetail(row) {
  const eventId = row?.relatedId || row?.recordId
  if (!eventId) {
    return
  }
  getRecognitionAssist(eventId).then(response => {
    recognitionAssistDetail.value = response.data || null
    recognitionAssistDetailOpen.value = true
  })
}

function handleRecognitionAssistExport() {
  proxy.download(
    'ygb/injury/event/recognitionAssist/export',
    { ...buildInjuryAggregateQuery() },
    `injury_recognition_assist_${Date.now()}.xlsx`
  )
}

function loadInjuryAggregateBlocks() {
  const query = buildInjuryAggregateQuery()
  return Promise.all([
    getInjuryEventAnalysis(query),
    getInjuryEventMonitor(query),
    listRecognitionAssist({
      ...query,
      pageNum: 1,
      pageSize: 10
    })
  ]).then(([analysisResponse, monitorResponse, recognitionResponse]) => {
    injuryEventAnalysis.value = analysisResponse.data || {}
    injuryEventMonitor.value = monitorResponse.data || {}
    recognitionAssistRows.value = recognitionResponse.rows || []
  }).catch(() => {
    injuryEventAnalysis.value = {}
    injuryEventMonitor.value = {}
    recognitionAssistRows.value = []
  })
}

function buildYgbHintTags(event) {
  if (!event) {
    return [{ label: '请选择事件查看办理提示', type: 'info' }]
  }

  const tags = []
  const remainingDays = Number(event.remainingDays)

  if (String(event.warningStatus) === '1') {
    tags.push({ label: '当前事件已进入预警口径，建议优先核查承办进度和责任人。', type: 'danger' })
  }
  if (!Number.isNaN(remainingDays) && remainingDays < 0) {
    tags.push({ label: '当前事件已超期，建议立即补录审批结论或推进状态流转。', type: 'warning' })
  }
  if (String(event.injuryStatus) === '1') {
    tags.push({ label: '事件处于认定处理中，建议重点核查认定材料和办理时限。', type: 'warning' })
  }
  if (String(event.injuryStatus) === '3') {
    tags.push({ label: '事件处于待遇申领中，建议关注支付材料和申领回执是否齐全。', type: 'warning' })
  }
  if (!event.diagnosisUrl) {
    tags.push({ label: '缺少诊断证明链接，建议补齐基础佐证材料。', type: 'warning' })
  }
  if (!event.approvalResult && String(event.injuryStatus) !== '0') {
    tags.push({ label: '当前状态缺少审批结论，建议同步回写办理说明。', type: 'warning' })
  }
  if (String(event.injuryStatus) === '4') {
    tags.push({ label: '事件已办结，可继续沉淀归档结论和复盘材料。', type: 'success' })
  }
  if (!tags.length) {
    tags.push({ label: '当前事件信息完整，可按既定路径继续办理闭环。', type: 'success' })
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

.ygb-summary-grid--inner {
  margin-bottom: 16px;
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

.ygb-summary-card--inner {
  padding: 16px 18px;
  border-radius: 14px;
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

.ygb-card-head__title--small {
  font-size: 16px;
}

.ygb-card-head__desc {
  margin-top: 4px;
  color: #7b8da1;
  font-size: 13px;
  line-height: 1.7;
}

.ygb-card-head--inline {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.ygb-card-head--split {
  margin-top: 18px;
}

.ygb-focus-list,
.ygb-source-list,
.ygb-pipeline-list,
.ygb-analysis-list {
  display: grid;
  gap: 12px;
}

.ygb-analysis-columns,
.ygb-monitor-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.ygb-analysis-panel {
  border: 1px solid #dde7f1;
  border-radius: 14px;
  background: #f8fbfd;
  padding: 16px;
}

.ygb-analysis-panel__title {
  color: #13243a;
  font-size: 15px;
  font-weight: 700;
  margin-bottom: 12px;
}

.ygb-stat-item {
  width: 100%;
  border: 1px solid #d7e4f0;
  border-radius: 12px;
  background: #fff;
  padding: 12px 14px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  cursor: pointer;
  text-align: left;
  transition: all 0.2s ease;
}

.ygb-stat-item:hover {
  border-color: #8fb6da;
  box-shadow: 0 8px 20px rgba(15, 94, 168, 0.08);
}

.ygb-stat-item__label {
  color: #55687b;
  font-size: 13px;
}

.ygb-stat-item__value {
  color: #13243a;
  font-size: 16px;
  font-weight: 700;
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
  color: #4b5f73;
  line-height: 1.6;
}

.ygb-form-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 0 16px;
}

.ygb-mini-table {
  margin-top: 16px;
}

.ygb-empty-hint {
  padding-top: 12px;
  color: #7b8da1;
  font-size: 13px;
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
  .ygb-focus-grid,
  .ygb-analysis-columns,
  .ygb-monitor-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 900px) {
  .ygb-form-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .ygb-summary-grid,
  .ygb-focus-grid,
  .ygb-analysis-columns,
  .ygb-monitor-grid {
    grid-template-columns: 1fr;
  }

  .ygb-card-head--inline,
  .ygb-workbench-alert__title {
    align-items: flex-start;
    flex-direction: column;
  }
}
</style>
