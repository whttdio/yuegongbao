<template>
  <div class="app-container azb-page azb-injury-event-workbench">
    <section class="gov-page-header azb-page__header">
      <div>
        <p class="azb-page__eyebrow">工伤治理联动</p>
        <h1 class="azb-page__title">{{ roleTitle }}</h1>
        <p class="azb-page__desc">
          {{ roleDescription }}
          当前继续复用统一工伤事件台账、状态流转和汇总接口，但安责保前端已按应急监管、企业执行复核、保险机构和银行协同四类高频视角重排焦点事件、处置顺序和详情重点。
        </p>
      </div>
      <div class="azb-page__tip">
        <div class="azb-page__tip-item">当前视角：{{ roleBadge }}</div>
        <div class="azb-page__tip-item">优先关注：已预警、超期、认定中长期未推进和未完结事件。</div>
        <div class="azb-page__tip-item">事件新增、修改和状态流转继续走共享后台，不额外复制安责保独立流程表。</div>
        <div class="azb-page__tip-item">后续可继续接入事故等级、现场材料、赔付进度和隐患回流链路。</div>
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

    <el-card class="toolbar-card azb-toolbar-card" shadow="never">
      <el-row :gutter="10">
        <el-col v-if="!isReadOnlyRole" :span="1.5">
          <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['ygb:injuryEvent:add']">新增事件</el-button>
        </el-col>
        <el-col v-if="!isReadOnlyRole" :span="1.5">
          <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['ygb:injuryEvent:edit']">修改事件</el-button>
        </el-col>
        <el-col v-if="!isReadOnlyRole" :span="1.5">
          <el-button type="warning" plain icon="Promotion" :disabled="single" @click="openStatusDialog()" v-hasPermi="['ygb:injuryEvent:flow']">状态流转</el-button>
        </el-col>
        <el-col v-if="!isReadOnlyRole" :span="1.5">
          <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['ygb:injuryEvent:remove']">删除事件</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['ygb:injuryEvent:export']">导出</el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>
    </el-card>

    <el-card class="table-card azb-table-card" shadow="never">
      <template #header>
        <div class="azb-card-head azb-card-head--between">
          <div>
            <div class="azb-card-head__title">工伤事件治理台账</div>
            <div class="azb-card-head__desc">{{ focusTableHint }}</div>
          </div>
          <div class="azb-card-head__desc">当前总量 {{ total }} 条</div>
        </div>
      </template>
      <el-table v-loading="loading" :data="visibleEventList" @selection-change="handleSelectionChange" @row-click="handleRowClick">
        <el-table-column v-if="!isReadOnlyRole" type="selection" width="55" align="center" />
        <el-table-column label="事件ID" prop="eventId" width="100" />
        <el-table-column label="人员" prop="personName" width="120" />
        <el-table-column label="企业名称" prop="enterpriseName" min-width="220" />
        <el-table-column label="区域" width="140">
          <template #default="scope">
            {{ formatRegionName(scope.row.regionCode) }}
          </template>
        </el-table-column>
        <el-table-column label="事故日期" width="120">
          <template #default="scope">
            {{ parseTime(scope.row.eventDate, '{y}-{m}-{d}') }}
          </template>
        </el-table-column>
        <el-table-column label="事故地点" prop="injuryLocation" min-width="200" show-overflow-tooltip />
        <el-table-column label="受伤部位" prop="injuryPart" width="120" />
        <el-table-column label="事件状态" width="120">
          <template #default="scope">
            <dict-tag :options="injuryStatusOptions" :value="scope.row.injuryStatus" />
          </template>
        </el-table-column>
        <el-table-column label="剩余天数" width="100">
          <template #default="scope">
            {{ remainingDaysText(scope.row.remainingDays) }}
          </template>
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
            <template v-if="!isReadOnlyRole">
              <el-button link type="primary" icon="Edit" @click.stop="handleUpdate(scope.row)" v-hasPermi="['ygb:injuryEvent:edit']">修改</el-button>
              <el-button link type="primary" icon="Promotion" @click.stop="openStatusDialog(scope.row)" v-hasPermi="['ygb:injuryEvent:flow']">流转</el-button>
              <el-button link type="danger" icon="Delete" @click.stop="handleDelete(scope.row)" v-hasPermi="['ygb:injuryEvent:remove']">删除</el-button>
            </template>
          </template>
        </el-table-column>
      </el-table>
      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>

    <el-dialog v-if="!isReadOnlyRole" :title="title" v-model="open" width="860px" append-to-body>
      <el-form ref="injuryRef" :model="form" :rules="rules" label-width="110px">
        <div class="azb-injury-form-grid">
          <el-form-item label="企业" prop="enterpriseId">
            <el-select v-model="form.enterpriseId" filterable placeholder="请选择企业" @change="handleEnterpriseChange">
              <el-option v-for="item in enterpriseOptions" :key="item.enterpriseId" :label="item.enterpriseName" :value="item.enterpriseId" />
            </el-select>
          </el-form-item>
          <el-form-item label="人员" prop="personId">
            <el-select v-model="form.personId" filterable placeholder="请选择人员">
              <el-option v-for="item in formPersonOptions" :key="item.personId" :label="`${item.personName} / ${item.enterpriseName || ''}`" :value="item.personId" />
            </el-select>
          </el-form-item>
          <el-form-item label="事故日期" prop="eventDate">
            <el-date-picker v-model="form.eventDate" type="date" format="YYYY-MM-DD" value-format="YYYY-MM-DD" style="width: 100%" />
          </el-form-item>
          <el-form-item label="上报时间" prop="reportTime">
            <el-date-picker v-model="form.reportTime" type="datetime" format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
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
            <el-date-picker v-model="form.approvalDeadline" type="date" format="YYYY-MM-DD" value-format="YYYY-MM-DD" style="width: 100%" />
          </el-form-item>
        </div>
        <el-form-item label="事故地点" prop="injuryLocation">
          <el-input v-model="form.injuryLocation" placeholder="请输入事故地点" />
        </el-form-item>
        <el-form-item label="诊断证明" prop="diagnosisUrl">
          <el-input v-model="form.diagnosisUrl" placeholder="请输入诊断证明链接" />
        </el-form-item>
        <el-form-item label="审批结果" prop="approvalResult">
          <el-input v-model="form.approvalResult" type="textarea" :rows="3" placeholder="请输入审批结论或事件说明" />
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

    <el-dialog v-if="!isReadOnlyRole" title="工伤状态流转" v-model="statusOpen" width="520px" append-to-body>
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

    <page-detail-dialog v-model="detailOpen" title="事件详情" width="760px">
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
          <el-descriptions-item label="当前视角重点" :span="2">{{ detailFocusText }}</el-descriptions-item>
          <el-descriptions-item label="事故地点" :span="2">{{ detailEvent.injuryLocation || '-' }}</el-descriptions-item>
          <el-descriptions-item label="诊断证明" :span="2">{{ detailEvent.diagnosisUrl || '-' }}</el-descriptions-item>
          <el-descriptions-item label="审批结果" :span="2">{{ detailEvent.approvalResult || '-' }}</el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ detailEvent.remark || '-' }}</el-descriptions-item>
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

<script setup name="AzbInjuryEvent">
import { computed, getCurrentInstance, ref, watch, watchEffect } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useWorkbenchAssist } from '@/composables/useWorkbenchAssist'
import useUserStore from '@/store/modules/user'
import { decoratePortalExplanationItems, openPortalExplanationAction, resolvePortalExplanationSummary } from '@/utils/portalExplanation'
import { useRoleViewMode } from '@/utils/roleView'
import { applyWorkbenchRouteQuery, buildWorkbenchContext, stripWorkbenchRouteQuery } from '@/utils/workbenchLink'
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
const userStore = useUserStore()
const { isReadOnlyRole, isInsurerRole, isBankRole, readOnlyRoleLabel, readOnlyRoleDescription } = useRoleViewMode()
const route = useRoute()
const { setPageGuide } = useWorkbenchAssist()
const router = useRouter()
const activeFocusKey = ref('')
const injuryEventWorkbenchFields = ['regionCode', 'enterpriseId', 'injuryStatus', 'warningStatus', 'focusKey']
const injuryEventInitialQuery = {}

applyWorkbenchRouteQuery(route.query, injuryEventInitialQuery, injuryEventWorkbenchFields)

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
  if (roleView.value === 'insurer') return '保险只读协同'
  if (roleView.value === 'site-enterprise') return '企业执行复核'
  return '应急监管处置'
})

const roleTitle = computed(() => {
  if (roleView.value === 'bank') return '银行工伤协同看板'
  if (roleView.value === 'insurer') return '安责保工伤进度复核台'
  if (roleView.value === 'site-enterprise') return '企业工伤事件执行台账'
  return '工伤事件治理工作台'
})

const roleDescription = computed(() => {
  if (roleView.value === 'bank') {
    return '面向银行协同只读查看超期、预警和未完结事件，重点判断是否需要联动信用及区域报表口径。'
  }
  if (roleView.value === 'insurer') {
    return '面向保险机构聚焦认定进度、待遇申领和超期风险，重点复核哪些事件仍缺少结论、材料或持续滞留。'
  }
  if (roleView.value === 'site-enterprise') {
    return '面向企业管理员、工伤经办和现场负责人统一处理事故上报、认定推进、待遇申领和闭环留痕。'
  }
  return '面向应急监管统一识别区域已预警、超期和长期未闭环事件，形成可连续处置的治理队列。'
})

function buildInjuryEventExplanationQuery(extraQuery = {}) {
  return {
    enterpriseId: queryParams.value.enterpriseId,
    regionCode: queryParams.value.regionCode,
    ...extraQuery
  }
}

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
  syncCurrentEvent,
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
  exportFilePrefix: 'azb_injury_event',
  initialQueryParams: injuryEventInitialQuery,
  resetQueryPatch: () => {
    const patch = {}
    applyWorkbenchRouteQuery(route.query, patch, injuryEventWorkbenchFields)
    return patch
  },
  canMutate: () => !isReadOnlyRole.value,
  onBlockedAction: blockReadOnlyAction,
  getCurrentList: () => visibleEventList.value
})

const portalExplanations = computed(() => {
  const warningItem = {
    key: 'warning',
    dimensionName: '预警事件',
    currentValue: valueOrDefault(summaryData.value.warningCount, 0),
    targetValue: '0',
    summary: '已预警事件应优先进入治理闭环，避免继续在区域侧累积风险暴露。',
    evidenceModule: 'injuryEvent',
    recommendModule: 'injuryEvent',
    defaultQuery: buildInjuryEventExplanationQuery({ warningStatus: '1', focusKey: 'warning' }),
    sourceLabel: '6.1 工伤治理解释',
    sourceDescription: '从工伤事件中继续核查已进入预警口径的风险对象。'
  }
  const overdueItem = {
    key: 'overdue',
    dimensionName: '超期风险',
    currentValue: valueOrDefault(summaryData.value.overdueCount, 0),
    targetValue: '0',
    summary: '超期事件说明认定、申领或办结推进存在停滞，应优先压降时效风险。',
    evidenceModule: 'injuryEvent',
    recommendModule: 'injuryEvent',
    defaultQuery: buildInjuryEventExplanationQuery({ focusKey: 'overdue' }),
    sourceLabel: '6.1 工伤治理解释',
    sourceDescription: '从工伤事件中继续追踪超期未推进的治理对象。'
  }
  const recognizingItem = {
    key: 'recognizing',
    dimensionName: '认定中事件',
    currentValue: valueOrDefault(summaryData.value.recognizingCount, 0),
    targetValue: '0',
    summary: '认定中事件应持续核对认定材料、审批结论和时限，避免长期停留在处理中。',
    evidenceModule: 'injuryEvent',
    recommendModule: 'injuryEvent',
    defaultQuery: buildInjuryEventExplanationQuery({ injuryStatus: '1', focusKey: 'recognizing' }),
    sourceLabel: '6.1 工伤治理解释',
    sourceDescription: '从工伤事件中继续核查认定推进中的重点对象。'
  }
  const claimingItem = {
    key: 'claiming',
    dimensionName: '待遇申领',
    currentValue: valueOrDefault(summaryData.value.claimingCount, 0),
    targetValue: '0',
    summary: '待遇申领阶段更依赖材料回执和处置说明，应尽快补齐申领闭环证据。',
    evidenceModule: 'injuryEvent',
    recommendModule: 'injuryEvent',
    defaultQuery: buildInjuryEventExplanationQuery({ injuryStatus: '3', focusKey: 'claiming' }),
    sourceLabel: '6.1 工伤治理解释',
    sourceDescription: '从工伤事件中继续核查待遇申领和回执闭环。'
  }
  const pendingItem = {
    key: 'pending',
    dimensionName: '未闭环事件',
    currentValue: valueOrDefault(summaryData.value.pendingCount, 0),
    targetValue: '0',
    summary: '未闭环事件直接决定当前区域工伤治理压力，应优先形成连续处置队列。',
    evidenceModule: 'injuryEvent',
    recommendModule: 'injuryEvent',
    defaultQuery: buildInjuryEventExplanationQuery({ focusKey: 'pending' }),
    sourceLabel: '6.1 工伤治理解释',
    sourceDescription: '从工伤事件中继续核查尚未办结的治理对象。'
  }
  const finishedItem = {
    key: 'finished',
    dimensionName: '已办结事件',
    currentValue: valueOrDefault(summaryData.value.finishedCount, 0),
    targetValue: '持续复盘',
    summary: '已办结事件可用于回看闭环沉淀和预防复盘，不再作为优先处置项但应保留追溯能力。',
    evidenceModule: 'injuryEvent',
    recommendModule: 'injuryEvent',
    defaultQuery: buildInjuryEventExplanationQuery({ injuryStatus: '4', focusKey: 'finished' }),
    sourceLabel: '6.1 工伤治理解释',
    sourceDescription: '从工伤事件中继续回看办结对象和闭环结果。'
  }

  if (roleView.value === 'bank') {
    return [warningItem, overdueItem, pendingItem, finishedItem]
  }
  if (roleView.value === 'insurer') {
    return [recognizingItem, claimingItem, overdueItem, pendingItem]
  }
  if (roleView.value === 'site-enterprise') {
    return [pendingItem, recognizingItem, claimingItem, finishedItem]
  }
  return [warningItem, overdueItem, pendingItem, recognizingItem]
})

const portalExplanationItems = computed(() => decoratePortalExplanationItems(portalExplanations.value, {
  portalCode: 'azb',
  panelTitle: '6.1 工伤治理解释',
  panelDescription: '围绕预警事件、超期风险和闭环进度输出治理解释。',
  sourceLabel: '安责保工伤工作台',
  title: '当前工伤治理页沿用了首页来源条件',
  description: '列表已按首页工作台带入的区域或企业条件锁定，适合继续处理当前范围内的工伤风险和闭环事项。',
  fieldLabels: {
    regionCode: '区域',
    enterpriseId: '企业',
    injuryStatus: '事件状态',
    warningStatus: '预警状态',
    focusKey: '焦点队列'
  },
  fieldFormatters: {
    regionCode: value => formatRegionName(value),
    enterpriseId: value => enterpriseName(value) || value,
    injuryStatus: value => injuryStatusLabel(value),
    warningStatus: value => warningStatusLabel(value),
    focusKey: value => injuryEventFocusLabel(value)
  }
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
  const totalCount = valueOrDefault(summaryData.value.totalCount, total.value)
  const pendingCount = valueOrDefault(summaryData.value.pendingCount, 0)
  const warningCount = valueOrDefault(summaryData.value.warningCount, 0)
  const overdueCount = valueOrDefault(summaryData.value.overdueCount, 0)
  const recognizingCount = valueOrDefault(summaryData.value.recognizingCount, 0)
  const claimingCount = valueOrDefault(summaryData.value.claimingCount, 0)
  const finishedCount = valueOrDefault(summaryData.value.finishedCount, 0)

  if (roleView.value === 'bank') {
    return [
      { key: 'warning', label: '预警事件', value: warningCount, unit: '件', note: '协同视角下优先关注的风险事件总量。', cardClass: 'azb-summary-card--danger' },
      { key: 'overdue', label: '超期风险', value: overdueCount, unit: '件', note: '剩余天数已为负值、需要重点联动观察的事件。', cardClass: 'azb-summary-card--warning' },
      { key: 'pending', label: '未完结事件', value: pendingCount, unit: '件', note: '仍未完成闭环、可能影响区域风险口径的事件。', cardClass: 'azb-summary-card--primary' },
      { key: 'finished', label: '已完结事件', value: finishedCount, unit: '件', note: '用于对照当前区域闭环沉淀情况。', cardClass: 'azb-summary-card--success' }
    ]
  }

  if (roleView.value === 'insurer') {
    return [
      { key: 'warning', label: '预警事件', value: warningCount, unit: '件', note: '优先补齐结论和材料的重点对象。', cardClass: 'azb-summary-card--danger' },
      { key: 'recognizing', label: '认定中事件', value: recognizingCount, unit: '件', note: '需要持续复核认定进度和处置时限。', cardClass: 'azb-summary-card--warning' },
      { key: 'claiming', label: '待遇申领中', value: claimingCount, unit: '件', note: '重点看申领回执、支付材料和处置说明。', cardClass: 'azb-summary-card--primary' },
      { key: 'overdue', label: '超期风险', value: overdueCount, unit: '件', note: '超期事件需要尽快推进流转或补结论。', cardClass: 'azb-summary-card--success' }
    ]
  }

  if (roleView.value === 'site-enterprise') {
    return [
      { key: 'pending', label: '未完结事件', value: pendingCount, unit: '件', note: '先处理仍处于认定和申领阶段的事件。', cardClass: 'azb-summary-card--warning' },
      { key: 'overdue', label: '超期风险', value: overdueCount, unit: '件', note: '企业经办优先补录结论并推动状态流转。', cardClass: 'azb-summary-card--danger' },
      { key: 'warning', label: '预警事件', value: warningCount, unit: '件', note: '已进入预警口径的事件需要尽快压降。', cardClass: 'azb-summary-card--primary' },
      { key: 'finished', label: '已完结事件', value: finishedCount, unit: '件', note: '用于对照当前事件闭环和复盘沉淀情况。', cardClass: 'azb-summary-card--success' }
    ]
  }

  return [
    { key: 'total', label: '事件记录', value: totalCount, unit: '件', note: '当前筛选范围内纳入治理的工伤事件总量。', cardClass: '' },
    { key: 'warning', label: '预警事件', value: warningCount, unit: '件', note: '优先纳入监管处置视野的重点风险事件。', cardClass: 'azb-summary-card--danger' },
    { key: 'overdue', label: '超期风险', value: overdueCount, unit: '件', note: '超期事件应优先推动认定、申领或办结。', cardClass: 'azb-summary-card--warning' },
    { key: 'pending', label: '未完结事件', value: pendingCount, unit: '件', note: '用于把握区域事件积压和闭环压力。', cardClass: 'azb-summary-card--success' }
  ]
})

const resolvedSummaryCards = computed(() => summaryCards.value.map((item, index) => ({
  ...item,
  note: resolveExplanationFirstText(item.note, resolvePortalExplanationItem(index))
})))

const focusQueues = computed(() => {
  const pendingCount = valueOrDefault(summaryData.value.pendingCount, 0)
  const warningCount = valueOrDefault(summaryData.value.warningCount, 0)
  const overdueCount = valueOrDefault(summaryData.value.overdueCount, 0)
  const recognizingCount = valueOrDefault(summaryData.value.recognizingCount, 0)
  const claimingCount = valueOrDefault(summaryData.value.claimingCount, 0)

  if (roleView.value === 'bank') {
    return [
      { key: 'warning', title: '预警事件', count: warningCount, unit: '件', desc: '先判断是否进入区域重点风险口径。', actionText: '查看风险摘要' },
      { key: 'overdue', title: '超期事件', count: overdueCount, unit: '件', desc: '重点看长期未推进对象是否影响区域信用。', actionText: '查看超期事件' },
      { key: 'pending', title: '未完结事件', count: pendingCount, unit: '件', desc: '继续保留在跟踪列表中的未闭环对象。', actionText: '查看未完结事件' }
    ]
  }

  if (roleView.value === 'insurer') {
    return [
      { key: 'recognizing', title: '认定中事件', count: recognizingCount, unit: '件', desc: '重点复核认定进度和资料是否完整。', actionText: '查看认定事件' },
      { key: 'claiming', title: '待遇申领中', count: claimingCount, unit: '件', desc: '优先查看仍缺材料、回执或支付说明的对象。', actionText: '查看申领事件' },
      { key: 'overdue', title: '超期风险', count: overdueCount, unit: '件', desc: '超期事件需要尽快补齐处置结论。', actionText: '查看超期事件' }
    ]
  }

  if (roleView.value === 'site-enterprise') {
    return [
      { key: 'overdue', title: '超期风险', count: overdueCount, unit: '件', desc: '企业经办先处理超时未推进事件。', actionText: '立即补录结论' },
      { key: 'recognizing', title: '认定中事件', count: recognizingCount, unit: '件', desc: '集中跟踪认定资料和时限。', actionText: '推进认定' },
      { key: 'claiming', title: '待遇申领中', count: claimingCount, unit: '件', desc: '重点补齐申领材料和回执。', actionText: '推进申领' }
    ]
  }

  return [
    { key: 'warning', title: '预警事件', count: warningCount, unit: '件', desc: '先处理已经进入监管预警口径的事件。', actionText: '进入监管复核' },
    { key: 'overdue', title: '超期风险', count: overdueCount, unit: '件', desc: '优先压降超时未推进事件。', actionText: '推动流转' },
    { key: 'pending', title: '未完结事件', count: pendingCount, unit: '件', desc: '持续跟踪尚未闭环的事件。', actionText: '查看在途事件' }
  ]
})

const resolvedFocusQueues = computed(() => focusQueues.value.map((item, index) => {
  const explanation = resolvePortalExplanationItem(index)
  return {
    ...item,
    desc: resolveExplanationFirstText(item.desc, explanation),
    actionText: resolveExplanationFirstActionText(item.actionText, explanation)
  }
}))

const activeFocus = computed(() => {
  const queues = focusQueues.value
  if (!queues.length) {
    return undefined
  }
  return queues.find(item => item.key === activeFocusKey.value) || queues[0]
})

const visibleEventList = computed(() => prioritizeFocusRows(
  injuryEventList.value,
  row => matchEventFocus(row, activeFocus.value?.key)
))

const focusTableHint = computed(() => {
  if (!activeFocus.value) {
    return '按当前筛选条件展示工伤事件治理台账。'
  }
  return `当前聚焦：${activeFocus.value.title}`
})

const selectedEventOverview = computed(() => {
  const event = currentEvent.value
  if (!event) {
    return [
      { label: '人员', value: '-' },
      { label: '企业', value: '-' },
      { label: '事件状态', value: '-' },
      { label: '剩余天数', value: '-' },
      { label: '预警状态', value: '-' },
      { label: '区域', value: '-' }
    ]
  }
  return [
    { label: '人员', value: event.personName || '-' },
    { label: '企业', value: event.enterpriseName || '-' },
    { label: '事件状态', value: injuryStatusLabel(event.injuryStatus) },
    { label: '剩余天数', value: remainingDaysText(event.remainingDays) },
    { label: '预警状态', value: warningStatusLabel(event.warningStatus) },
    { label: '区域', value: formatRegionName(event.regionCode) }
  ]
})

const primaryEventAction = computed(() => {
  if (isReadOnlyRole.value) {
    return { label: '查看事件详情', action: 'detail' }
  }
  if (activeFocus.value?.key === 'warning' || activeFocus.value?.key === 'overdue') {
    return { label: '推进状态流转', action: 'status' }
  }
  if (String(currentEvent.value?.injuryStatus || '') === '0') {
    return { label: '补录认定信息', action: 'edit' }
  }
  return { label: '维护当前事件', action: 'edit' }
})

const currentEventActionSummary = computed(() => {
  const event = currentEvent.value
  if (!event) {
    return '请选择事件查看治理建议。'
  }
  const eventLabel = `${event.personName || '当前人员'} / ${event.enterpriseName || '当前企业'}`

  if (isReadOnlyRole.value) {
    return `${eventLabel} 当前为只读复核视图。`
  }
  if (String(event.warningStatus) === '1' && isPending(event.injuryStatus)) {
    return `${eventLabel} 已进入预警口径，需优先跟进处置。`
  }
  if (isOverdue(event.remainingDays)) {
    return `${eventLabel} 当前已超期，建议优先补录结论或推进流转。`
  }
  if (String(event.injuryStatus) === '1') {
    return `${eventLabel} 正处于认定推进阶段，建议持续核对资料和时限。`
  }
  if (String(event.injuryStatus) === '3') {
    return `${eventLabel} 正处于待遇申领阶段，建议尽快补齐申领材料和回执。`
  }
  if (String(event.injuryStatus) === '4') {
    return `${eventLabel} 已办结，可继续用于闭环复盘和留痕核对。`
  }
  return `${eventLabel} 仍需继续跟进，建议结合当前焦点推进闭环。`
})

const currentEventActionTags = computed(() => buildAzbHintTags(currentEvent.value, roleView.value, activeFocus.value))

const workflowSteps = computed(() => {
  if (roleView.value === 'bank') {
    return [
      { label: '查看风险摘要', desc: '先看区域和对象是否已预警、超期或长期未完结。' },
      { label: '复核关键进度', desc: '重点确认认定、申领和办结状态是否持续停滞。' },
      { label: '联动信用口径', desc: '必要时将未闭环事件纳入信用与报表观察。' }
    ]
  }
  if (roleView.value === 'insurer') {
    return [
      { label: '筛选认定对象', desc: '先找认定中、待遇申领中和超期事件。' },
      { label: '补齐材料结论', desc: '重点复核审批结果、诊断证明和申领材料。' },
      { label: '推动闭环留痕', desc: '对长期滞留对象持续督促回写并完成闭环留痕。' }
    ]
  }
  if (roleView.value === 'site-enterprise') {
    return [
      { label: '事故上报', desc: '完成事故上报、人员和企业基础信息。' },
      { label: '认定推进', desc: '按认定进度持续回写状态、结论和时效。' },
      { label: '申领闭环', desc: '补齐待遇申领材料并完成闭环留痕。' }
    ]
  }
  return [
    { label: '识别重点事件', desc: '优先识别预警、超期和长期未完结事件。' },
    { label: '推进认定申领', desc: '对卡点对象推动认定、申领或办结流转。' },
    { label: '形成治理闭环', desc: '沉淀区域治理结论并压降持续风险。' }
  ]
})

const hintTags = computed(() => buildAzbHintTags(currentEvent.value, roleView.value, activeFocus.value))
const detailHintTags = computed(() => buildAzbHintTags(detailEvent.value || currentEvent.value, roleView.value, activeFocus.value))
const resolvedWorkflowSteps = computed(() => workflowSteps.value.map((item, index) => {
  const explanation = resolvePortalExplanationItem(index)
  return {
    ...item,
    label: explanation?.dimensionName || explanation?.moduleLabel || explanation?.moduleCode || item.label,
    desc: resolveExplanationFirstText(item.desc, explanation)
  }
}))
const resolvedCurrentEventActionSummary = computed(() => resolveExplanationFirstText(
  currentEventActionSummary.value,
  leadingPortalExplanation.value
))
const resolvedCurrentEventActionTags = computed(() => buildExplanationFirstTags(currentEventActionTags.value))
const resolvedHintTags = computed(() => buildExplanationFirstTags(hintTags.value))
const resolvedDetailHintTags = computed(() => buildExplanationFirstTags(detailHintTags.value))
const resolvedPrimaryEventAction = computed(() => ({
  ...primaryEventAction.value,
  label: resolveExplanationFirstActionText(primaryEventAction.value.label, leadingPortalExplanation.value)
}))

const detailFocusText = computed(() => {
  if (roleView.value === 'bank') {
    return '银行视角重点关注风险摘要、时效和对区域报表口径的影响。'
  }
  if (roleView.value === 'insurer') {
    return '保险视角重点关注认定进度、待遇申领、材料回写和超期风险。'
  }
  if (roleView.value === 'site-enterprise') {
    return '企业视角重点关注认定推进、申领材料和闭环留痕进度。'
  }
  return '应急监管视角重点关注预警状态、剩余时效和是否形成连续处置闭环。'
})

const readOnlyAlertTitle = computed(() => `${readOnlyRoleLabel.value}：当前仅保留工伤摘要、详情和导出`)
const readOnlyAlertDescription = computed(() => `${readOnlyRoleDescription.value} 当前页面仅保留工伤摘要、详情和导出能力。`)

watch(activeFocusKey, () => {
  syncCurrentEvent()
})

watch(focusQueues, queues => {
  if (!queues.length) {
    activeFocusKey.value = ''
    return
  }
  if (!queues.find(item => item.key === activeFocusKey.value)) {
    activeFocusKey.value = queues[0].key
  }
}, { immediate: true })

function handleSelectFocus(item) {
  activeFocusKey.value = item.key
}

function applyInjuryEventWorkbenchQuery(routeQuery = {}) {
  Object.assign(queryParams.value, {
    pageNum: 1,
    regionCode: undefined,
    enterpriseId: undefined,
    injuryStatus: undefined,
    warningStatus: undefined
  })
  applyWorkbenchRouteQuery(routeQuery, queryParams.value, injuryEventWorkbenchFields)
  activeFocusKey.value = resolveInjuryEventFocusKey(routeQuery.focusKey)
  getList()

watchEffect(() => {
  setPageGuide({
    title: roleTitle.value || '?????????',
    description: roleDescription.value || '?????????????????????????????????',
    portalExplanation: portalExplanationItems.value,
    focus: resolvedFocusQueues.value,
    selection: [...selectedEventOverview.value, { label: '??????', value: resolvedCurrentEventActionSummary.value }],
    workflow: resolvedWorkflowSteps.value,
    hints: [...resolvedCurrentEventActionTags.value, ...resolvedHintTags.value].slice(0, 6)
  })
})

}

function enterpriseName(value) {
  return enterpriseOptions.value.find(item => String(item.enterpriseId) === String(value))?.enterpriseName
}

function resetQuery() {
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

function handlePrimaryEventAction() {
  if (!currentEvent.value) {
    return
  }
  if (primaryEventAction.value.action === 'detail') {
    openDetail(currentEvent.value)
    return
  }
  if (primaryEventAction.value.action === 'status') {
    if (isReadOnlyRole.value) {
      blockReadOnlyAction('变更事件状态')
      return
    }
    openStatusDialog(currentEvent.value)
    return
  }
  if (isReadOnlyRole.value) {
    blockReadOnlyAction('修改工伤事件')
    return
  }
  handleUpdate(currentEvent.value)
}

function handlePortalExplanationAction(action) {
  if (action?.path === route.path) {
    applyInjuryEventWorkbenchQuery(action.query || {})
  }
  openPortalExplanationAction(router, action)
}

function blockReadOnlyAction(actionLabel) {
  proxy.$modal.msgWarning(`${readOnlyRoleLabel.value}当前仅保留查看、详情和导出，不能执行“${actionLabel}”`)
}

function buildAzbHintTags(event, role, focus) {
  if (!event) {
    if (focus?.title) {
      return [
        { label: `当前焦点：${focus.title}`, type: 'info' },
        { label: `优先动作：${focus.actionText}`, type: 'warning' }
      ]
    }
    return [{ label: '请选择事件查看治理提示', type: 'info' }]
  }

  const tags = []
  const remainingDays = Number(event.remainingDays)

  if (String(event.warningStatus) === '1') {
    tags.push({ label: '当前事件已进入预警口径，应优先纳入治理队列。', type: 'danger' })
  }
  if (!Number.isNaN(remainingDays) && remainingDays < 0) {
    tags.push({ label: '当前事件已超期，建议优先补录结论或推进流转。', type: 'warning' })
  }
  if (String(event.injuryStatus) === '1') {
    tags.push({ label: '当前事件处于认定中，建议重点复核认定资料和时限。', type: 'warning' })
  }
  if (String(event.injuryStatus) === '3') {
    tags.push({ label: '当前事件处于待遇申领中，建议补齐支付材料和申领回执。', type: 'warning' })
  }
  if (!event.diagnosisUrl) {
    tags.push({ label: '缺少诊断证明链接，建议补齐基础佐证材料。', type: 'warning' })
  }
  if (!event.approvalResult && String(event.injuryStatus) !== '0') {
    tags.push({ label: '当前状态缺少审批结论，建议同步回写处置说明。', type: 'warning' })
  }

  if (role === 'bank') {
    tags.push({ label: '银行视角仅保留风险摘要、时效和区域协同复核。', type: 'info' })
  } else if (role === 'insurer') {
    tags.push({ label: '保险视角优先关注认定进度、申领材料和超期滞留。', type: 'info' })
  } else if (role === 'site-enterprise') {
    tags.push({ label: '企业视角优先推动认定、申领和闭环留痕。', type: 'info' })
  } else {
    tags.push({ label: '应急监管视角优先压降预警、超期和未闭环事件。', type: 'info' })
  }

  if (String(event.injuryStatus) === '4') {
    tags.push({ label: '当前事件已办结，可继续沉淀闭环结论和复盘留痕。', type: 'success' })
  }
  if (!tags.length) {
    tags.push({ label: '当前事件信息完整，可按既定路径继续治理闭环。', type: 'success' })
  }
  return tags.slice(0, 5)
}

function matchEventFocus(event, focusKey) {
  if (!event || !focusKey) {
    return false
  }
  if (focusKey === 'warning') {
    return String(event.warningStatus) === '1'
  }
  if (focusKey === 'overdue') {
    return isOverdue(event.remainingDays)
  }
  if (focusKey === 'recognizing') {
    return String(event.injuryStatus) === '1'
  }
  if (focusKey === 'claiming') {
    return String(event.injuryStatus) === '3'
  }
  if (focusKey === 'finished') {
    return String(event.injuryStatus) === '4'
  }
  if (focusKey === 'pending') {
    return isPending(event.injuryStatus)
  }
  return false
}

function isOverdue(value) {
  const days = Number(value)
  return !Number.isNaN(days) && days < 0
}

function isPending(value) {
  return String(value) !== '4'
}

activeFocusKey.value = resolveInjuryEventFocusKey(route.query.focusKey)

function resolveInjuryEventFocusKey(value) {
  return Array.isArray(value) ? String(value[0] || '') : String(value || '')
}

function injuryEventFocusLabel(value) {
  return ({
    warning: '预警事件',
    overdue: '超期风险',
    recognizing: '认定中事件',
    claiming: '待遇申领',
    finished: '已办结事件',
    pending: '未闭环事件'
  })[String(value || '')] || (value || '-')
}
</script>

<style scoped lang="scss">
.azb-injury-event-workbench {
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

  .azb-card-head--between {
    display: flex;
    justify-content: space-between;
    gap: 16px;
    align-items: center;
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
    margin-bottom: 8px;
    font-size: 12px;
    color: #64748b;
  }

  .azb-source-item__value {
    font-size: 16px;
    font-weight: 600;
    color: #0f172a;
    line-height: 1.5;
  }

  .azb-focus-actions {
    display: flex;
    flex-wrap: wrap;
    gap: 12px;
    margin-top: 16px;
  }

  .azb-recommend-panel {
    margin-top: 16px;
    padding: 14px 16px;
    border: 1px solid #dbe7f3;
    border-radius: 10px;
    background: #f8fbff;
  }

  .azb-recommend-panel__title {
    font-size: 13px;
    font-weight: 600;
    color: #0f172a;
  }

  .azb-recommend-panel__summary {
    margin: 8px 0 0;
    font-size: 13px;
    line-height: 1.7;
    color: #475569;
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
    padding: 14px 16px;
    border-radius: 10px;
    background: #f8fbff;
    border: 1px solid #dbe7f3;
  }

  .azb-pipeline-item__index {
    min-width: 40px;
    height: 40px;
    border-radius: 12px;
    background: #0b6b78;
    color: #fff;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    font-size: 14px;
    font-weight: 700;
  }

  .azb-pipeline-item__body strong {
    display: block;
    color: #0f172a;
    margin-bottom: 4px;
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
    gap: 8px;
    margin-bottom: 10px;
    color: #4b5f73;
    line-height: 1.6;
  }

  .azb-detail-block {
    margin-top: 18px;
    padding: 16px;
    border: 1px solid #dbe7f3;
    border-radius: 10px;
    background: #f8fbff;
  }

  .azb-detail-block h3 {
    margin: 0 0 12px;
    font-size: 15px;
    font-weight: 600;
    color: #0f172a;
  }

  .azb-injury-form-grid {
    display: grid;
    grid-template-columns: repeat(3, minmax(0, 1fr));
    gap: 0 16px;
  }

  @media (max-width: 1200px) {
    .azb-focus-grid {
      grid-template-columns: 1fr;
    }
  }

  @media (max-width: 900px) {
    .azb-injury-form-grid {
      grid-template-columns: 1fr;
    }
  }

  @media (max-width: 768px) {
    .azb-source-list {
      grid-template-columns: 1fr;
    }

    .azb-focus-queue {
      flex-direction: column;
    }

    .azb-focus-queue__side {
      min-width: 0;
      align-items: flex-start;
    }
  }
}
</style>
