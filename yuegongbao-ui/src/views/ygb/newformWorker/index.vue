<template>
  <div class="app-container ygb-page">
    <section class="gov-page-header ygb-page__header">
      <div>
        <p class="ygb-page__eyebrow">新业态监管线</p>
        <h1 class="ygb-page__title">新业态人员监管台账</h1>
        <p class="ygb-page__desc">
          围绕平台企业从业人员、职业伤害参保状态和来源回写结果组织新业态监管台账，
          面向企业管理员、工地负责人、操作员和监管经办优先识别未参保、停保和待联动对象。
        </p>
      </div>
      <div class="ygb-table-tip">
        当前同步固定走 Stub，后续可平滑切换平台正式接口；未参加职业伤害保障的人员会自动联动预警中心。
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

    <div class="ygb-submodule-grid">
      <button
        v-for="item in submoduleEntries"
        :key="item.key"
        type="button"
        class="ygb-submodule-card"
        @click="openSubmodule(item)"
      >
        <div class="ygb-submodule-card__head">
          <strong>{{ item.title }}</strong>
          <span>{{ item.actionText }}</span>
        </div>
        <p>{{ item.desc }}</p>
      </button>
    </div>


    <el-card class="search-card ygb-search-card" shadow="never">
      <el-form ref="queryRef" :model="queryParams" :inline="true" v-show="showSearch">
        <el-form-item label="统计月份">
          <el-date-picker v-model="queryParams.statMonth" type="month" format="YYYY-MM" value-format="YYYY-MM" style="width: 160px" />
        </el-form-item>
        <el-form-item label="企业">
          <el-select v-model="queryParams.enterpriseId" clearable filterable style="width: 220px">
            <el-option v-for="item in enterpriseOptions" :key="item.enterpriseId" :label="item.enterpriseName" :value="item.enterpriseId" />
          </el-select>
        </el-form-item>
        <el-form-item label="人员">
          <el-input v-model="queryParams.personName" clearable style="width: 180px" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="平台企业">
          <el-select v-model="queryParams.platformName" clearable style="width: 180px">
            <el-option v-for="item in platformOptions" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-form-item label="从业类型">
          <el-select v-model="queryParams.employmentType" clearable style="width: 160px">
            <el-option v-for="item in employmentTypeOptions" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-form-item label="职业伤害参保">
          <el-select v-model="queryParams.injuryInsuranceStatus" clearable style="width: 160px">
            <el-option v-for="item in injuryInsuranceOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="预警状态">
          <el-select v-model="queryParams.warningStatus" clearable style="width: 140px">
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
        <el-col v-if="!isReadOnlyRole" :span="1.5">
          <el-button type="primary" plain icon="RefreshRight" @click="handleSync" v-hasPermi="['ygb:newformWorker:sync']">模拟同步</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['ygb:newformWorker:export']">导出</el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>
    </el-card>

    <el-card class="table-card ygb-table-card" shadow="never">
      <template #header>
        <div class="ygb-card-head ygb-card-head--between">
          <div>
            <div class="ygb-card-head__title">新业态人员监管台账</div>
            <div class="ygb-card-head__desc">{{ focusTableHint }}</div>
          </div>
          <div class="ygb-card-head__desc">当前总量 {{ total }} 条</div>
        </div>
      </template>
      <el-table v-loading="loading" :data="visibleWorkerList" @row-click="handleRowClick">
        <el-table-column label="ID" prop="workerRecordId" width="90" />
        <el-table-column label="统计月份" prop="statMonth" width="110" />
        <el-table-column label="企业" prop="enterpriseName" min-width="220" />
        <el-table-column label="人员" prop="personName" width="120" />
        <el-table-column label="平台企业" prop="platformName" min-width="160" />
        <el-table-column label="从业类型" prop="employmentType" width="120" />
        <el-table-column label="区域" min-width="120">
          <template #default="scope">
            {{ scope.row.regionName || scope.row.regionCode }}
          </template>
        </el-table-column>
        <el-table-column label="职业伤害参保" width="120">
          <template #default="scope">
            <dict-tag :options="injuryInsuranceOptions" :value="scope.row.injuryInsuranceStatus" />
          </template>
        </el-table-column>
        <el-table-column label="预警状态" width="110">
          <template #default="scope">
            <dict-tag :options="warningStatusOptions" :value="scope.row.warningStatus" />
          </template>
        </el-table-column>
        <el-table-column label="月收入" width="110">
          <template #default="scope">
            {{ formatMoney(scope.row.monthlyIncome) }}
          </template>
        </el-table-column>
        <el-table-column label="来源消息" prop="sourceMessage" min-width="220" show-overflow-tooltip />
        <el-table-column class-name="table-fill-column" min-width="1" />

        <el-table-column label="操作" fixed="right" align="center" width="100" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-button link type="info" icon="View" @click.stop="openDetail(scope.row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>

    <page-detail-dialog v-model="detailOpen" title="新业态人员详情" width="760px">
      <template v-if="detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="统计月份">{{ detail.statMonth || '-' }}</el-descriptions-item>
          <el-descriptions-item label="预警状态">
            <dict-tag :options="warningStatusOptions" :value="detail.warningStatus" />
          </el-descriptions-item>
          <el-descriptions-item label="企业">{{ detail.enterpriseName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="人员">{{ detail.personName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="身份证号">{{ maskIdCard(detail.idCard) }}</el-descriptions-item>
          <el-descriptions-item label="区域">{{ detail.regionName || detail.regionCode || '-' }}</el-descriptions-item>
          <el-descriptions-item label="平台企业">{{ detail.platformName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="从业类型">{{ detail.employmentType || '-' }}</el-descriptions-item>
          <el-descriptions-item label="社保状态">
            <dict-tag :options="insuranceOptions" :value="detail.insuranceStatus" />
          </el-descriptions-item>
          <el-descriptions-item label="职业伤害参保">
            <dict-tag :options="injuryInsuranceOptions" :value="detail.injuryInsuranceStatus" />
          </el-descriptions-item>
          <el-descriptions-item label="月收入">{{ formatMoney(detail.monthlyIncome) }}</el-descriptions-item>
          <el-descriptions-item label="回写时间">{{ parseTime(detail.callbackTime, '{y}-{m}-{d} {h}:{i}:{s}') || '-' }}</el-descriptions-item>
          <el-descriptions-item label="来源流水号" :span="2">{{ detail.sourceSerialNo || '-' }}</el-descriptions-item>
          <el-descriptions-item label="来源状态" :span="2">{{ detail.sourceStatus || '-' }}</el-descriptions-item>
          <el-descriptions-item label="来源消息" :span="2">{{ detail.sourceMessage || '-' }}</el-descriptions-item>
        </el-descriptions>

        <div class="detail-block">
          <h3>办理提示</h3>
          <div class="ygb-tag-list">
            <el-tag v-for="item in detailHintTags" :key="item.label" :type="item.type" effect="plain">{{ item.label }}</el-tag>
          </div>
        </div>
      </template>
    </page-detail-dialog>
  </div>
</template>

<script setup name="YgbNewformWorker">
import { listNewformWorker, getNewformWorkerSummary, syncNewformWorker } from '@/api/ygb/newformWorker'
import { optionselectEnterprise } from '@/api/ygb/enterprise'
import { useRoute, useRouter } from 'vue-router'
import { useWorkbenchAssist } from '@/composables/useWorkbenchAssist'
import { decoratePortalExplanationItems, openPortalExplanationAction } from '@/utils/portalExplanation'
import { useRoleViewMode } from '@/utils/roleView'
import { applyWorkbenchRouteQuery, buildWorkbenchContext, stripWorkbenchRouteQuery } from '@/utils/workbenchLink'

const { proxy } = getCurrentInstance()
const route = useRoute()
const { setPageGuide } = useWorkbenchAssist()
const router = useRouter()
const { isReadOnlyRole, readOnlyRoleLabel, readOnlyRoleDescription } = useRoleViewMode()
const newformWorkerWorkbenchFields = ['statMonth', 'enterpriseId', 'personName', 'platformName', 'employmentType', 'injuryInsuranceStatus', 'warningStatus', 'focusKey']

const insuranceOptions = [
  { label: '未参保', value: '0' },
  { label: '已参保', value: '1' },
  { label: '停保', value: '2' }
]

const injuryInsuranceOptions = [
  { label: '未参保', value: '0' },
  { label: '已参保', value: '1' },
  { label: '停保', value: '2' }
]

const warningStatusOptions = [
  { label: '未预警', value: '0' },
  { label: '已预警', value: '1' }
]

const platformOptions = ['即时配送平台', '南粤灵工平台', '湾区众包平台']
const employmentTypeOptions = ['配送骑手', '网约车司机', '平台零工']

const workerList = ref([])
const enterpriseOptions = ref([])
const summaryData = ref({})
const currentWorker = ref(undefined)
const detail = ref(undefined)
const loading = ref(false)
const showSearch = ref(true)
const total = ref(0)
const detailOpen = ref(false)
const activeFocusKey = ref('')

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    statMonth: currentMonth(),
    enterpriseId: undefined,
    personName: undefined,
    platformName: undefined,
    employmentType: undefined,
    injuryInsuranceStatus: undefined,
    warningStatus: undefined
  }
})

const { queryParams } = toRefs(data)

applyWorkbenchRouteQuery(route.query, queryParams.value, newformWorkerWorkbenchFields)

const summaryCards = computed(() => ([
  {
    key: 'total',
    label: '监管记录',
    value: summaryData.value.totalCount != null ? summaryData.value.totalCount : total.value,
    unit: '条',
    note: '当前筛选条件下已进入新业态监管台账的人员记录总量。',
    cardClass: ''
  },
  {
    key: 'insured',
    label: '已参职业伤害险',
    value: valueOrDefault(summaryData.value.insuredCount, 0),
    unit: '人',
    note: '职业伤害保障状态正常，可继续用于月度归档和后续监管分析。',
    cardClass: 'ygb-summary-card--success'
  },
  {
    key: 'uninsured',
    label: '未参保对象',
    value: valueOrDefault(summaryData.value.uninsuredCount, 0),
    unit: '人',
    note: '未参加职业伤害保障的对象需要优先核对平台归属和参保说明。',
    cardClass: 'ygb-summary-card--warning'
  },
  {
    key: 'warning',
    label: '已触发预警',
    value: valueOrDefault(summaryData.value.warningCount, 0),
    unit: '人',
    note: '已联动预警中心的对象需要继续承接至后续监管闭环。',
    cardClass: 'ygb-summary-card--primary'
  }
]))

const focusQueues = computed(() => {
  const rows = workerList.value || []
  const queues = [
    {
      key: 'uninsured',
      title: '未参保对象优先核对',
      desc: '优先核对平台归属、从业类型和参保说明，避免未参保对象继续积压。',
      count: rows.filter(item => item.injuryInsuranceStatus === '0').length,
      unit: '人',
      actionText: rows.some(item => item.injuryInsuranceStatus === '0') ? '立即补核' : '暂无积压'
    },
    {
      key: 'stopped',
      title: '停保对象复核',
      desc: '确认停保原因与是否需要恢复保障，减少停保风险滞留。',
      count: rows.filter(item => item.injuryInsuranceStatus === '2').length,
      unit: '人',
      actionText: rows.some(item => item.injuryInsuranceStatus === '2') ? '核对停保' : '状态平稳'
    },
    {
      key: 'warning',
      title: '预警对象继续承接',
      desc: '已联动预警中心的对象继续跟进闭环，避免问题停留在同步结果页。',
      count: rows.filter(item => item.warningStatus === '1').length,
      unit: '人',
      actionText: rows.some(item => item.warningStatus === '1') ? '继续闭环' : '暂无预警'
    },
    {
      key: 'income',
      title: '收入异常回写校正',
      desc: '优先处理收入为 0 或缺失的对象，修正平台回写口径后再归档。',
      count: rows.filter(item => Number(item.monthlyIncome || 0) <= 0).length,
      unit: '人',
      actionText: rows.some(item => Number(item.monthlyIncome || 0) <= 0) ? '修正收入' : '回写正常'
    }
  ]
  if (!queues.find(item => item.key === activeFocusKey.value)) {
    activeFocusKey.value = queues[0]?.key || ''
  }
  return queues
})

function buildNewformWorkerExplanationQuery(extraQuery = {}) {
  return {
    statMonth: queryParams.value.statMonth,
    enterpriseId: queryParams.value.enterpriseId,
    personName: queryParams.value.personName,
    platformName: queryParams.value.platformName,
    employmentType: queryParams.value.employmentType,
    injuryInsuranceStatus: queryParams.value.injuryInsuranceStatus,
    warningStatus: queryParams.value.warningStatus,
    ...extraQuery
  }
}

function buildNewformSubmoduleQuery(extraQuery = {}) {
  return {
    statMonth: queryParams.value.statMonth,
    regionCode: route.query.regionCode,
    enterpriseId: queryParams.value.enterpriseId,
    platformName: queryParams.value.platformName,
    warningStatus: queryParams.value.warningStatus,
    injuryInsuranceStatus: queryParams.value.injuryInsuranceStatus,
    wbSourceTitle: '新业态监管主页面来源条件',
    wbSourceLabel: '新业态监管',
    wbSourceDescription: '沿用主页面筛选条件进入子页，保持同口径聚合、台账和导出。',
    ...extraQuery
  }
}

const fallbackPortalExplanations = computed(() => focusQueues.value.map(item => ({
  key: item.key,
  dimensionName: item.title,
  currentValue: item.count,
  targetValue: item.key === 'income' ? '> 0' : '-',
  summary: item.desc,
  evidenceModule: 'newformWorker',
  recommendModule: 'newformWorker',
  defaultQuery: buildNewformWorkerExplanationQuery({ focusKey: item.key }),
  sourceLabel: '530.1 新业态办理解释',
  sourceDescription: item.desc,
  actionText: item.actionText
})))

const portalExplanations = computed(() => (Array.isArray(summaryData.value.ygbExplanation) && summaryData.value.ygbExplanation.length
  ? summaryData.value.ygbExplanation
  : fallbackPortalExplanations.value))

const portalExplanationItems = computed(() => decoratePortalExplanationItems(portalExplanations.value, {
  portalCode: 'ygb',
  panelTitle: '530.1 新业态办理解释',
  panelDescription: '当前解释项按 530.1 办理链口径展示新业态人员参保、预警和回写重点。'
}))

const activeFocus = computed(() => focusQueues.value.find(item => item.key === activeFocusKey.value) || focusQueues.value[0])

const workbenchContext = computed(() => buildWorkbenchContext(route.query, {
  fields: newformWorkerWorkbenchFields,
  title: '当前新业态人员页沿用了上游来源条件。',
  description: '当前页面复用了上游工作台筛选，可在同一门户语境下继续处理参保、预警和回写问题。',
  fieldLabels: {
    statMonth: '统计月份',
    enterpriseId: '企业',
    personName: '人员',
    platformName: '平台企业',
    employmentType: '从业类型',
    injuryInsuranceStatus: '职业伤害参保',
    warningStatus: '预警状态',
    focusKey: '焦点队列'
  },
  fieldFormatters: {
    enterpriseId: value => enterpriseName(value) || value,
    injuryInsuranceStatus: value => injuryInsuranceLabel(value),
    warningStatus: value => warningStatusLabel(value),
    focusKey: value => newformWorkerFocusLabel(value)
  }
}))

const selectedWorkerOverview = computed(() => {
  if (!currentWorker.value) {
    return [
      { label: '人员/企业', value: '-' },
      { label: '平台/类型', value: '-' },
      { label: '参保/预警', value: '-' },
      { label: '收入/月度', value: '-' }
    ]
  }
  return [
    { label: '人员/企业', value: `${currentWorker.value.personName || '-'} / ${currentWorker.value.enterpriseName || '-'}` },
    { label: '平台/类型', value: `${currentWorker.value.platformName || '-'} / ${currentWorker.value.employmentType || '-'}` },
    {
      label: '参保/预警',
      value: `${injuryInsuranceLabel(currentWorker.value.injuryInsuranceStatus)} / ${warningStatusLabel(currentWorker.value.warningStatus)}`
    },
    {
      label: '收入/月度',
      value: `${formatMoney(currentWorker.value.monthlyIncome, false)} / ${currentWorker.value.statMonth || '-'}`
    }
  ]
})

const visibleWorkerList = computed(() => prioritizeFocusRows(workerList.value, row => matchWorkerFocus(row, activeFocus.value?.key)))

const submoduleEntries = computed(() => ([
  {
    key: 'platform',
    title: '平台企业聚合',
    desc: '复用主表聚合平台维度的人员底数、参保状态、风险数量和平均收入。',
    actionText: '打开聚合页',
    path: '/ygb-newform/newformPlatform',
    query: buildNewformSubmoduleQuery()
  },
  {
    key: 'injuryMonitor',
    title: '职业伤害监测',
    desc: '聚合职业伤害参保、未参保、停保和预警对象，直接承接风险核查。',
    actionText: '打开监测页',
    path: '/ygb-newform/newformInjuryMonitor',
    query: buildNewformSubmoduleQuery({
      warningStatus: queryParams.value.warningStatus,
      injuryInsuranceStatus: queryParams.value.injuryInsuranceStatus
    })
  },
  {
    key: 'training',
    title: '培训管理',
    desc: '进入 typed-record 台账，延续当前月份、区域、企业和平台上下文。',
    actionText: '打开培训台账',
    path: '/ygb-newform/newformTraining',
    query: buildNewformSubmoduleQuery()
  }
]))

const focusTableHint = computed(() => {
  if (!activeFocus.value) {
    return '当前按查询条件展示台账记录。'
  }
  return `${activeFocus.value.title}，系统已将符合焦点条件的对象前置，便于高频用户先处理高风险记录。`
})

const workflowSteps = computed(() => ([
  {
    label: '先锁定统计月份',
    desc: '先按统计月份、企业和平台范围锁定本次监管台账，避免跨月和跨平台对象混查。'
  },
  {
    label: '执行平台同步',
    desc: '按月份重新拉取平台人员记录，统一回写平台企业、从业类型、参保状态和来源消息。'
  },
  {
    label: '核对平台归属与参保状态',
    desc: '优先核对未参保、停保对象的平台归属、人员类型和参保说明，排除口径偏差。'
  },
  {
    label: '承接预警闭环',
    desc: '对已触发预警对象继续承接到预警中心，避免问题停留在同步结果页。'
  },
  {
    label: '完成月度监管归档',
    desc: '状态正常或已完成说明补录的对象沉淀为月度监管底稿，供后续人社与企业复核。'
  }
]))

const primaryWorkerAction = computed(() => {
  if (isReadOnlyRole.value) {
    return { label: '查看归档详情' }
  }
  if (!currentWorker.value) {
    return { label: '选择待办对象' }
  }
  if (currentWorker.value.injuryInsuranceStatus === '0') {
    return { label: '核对未参保说明' }
  }
  if (currentWorker.value.injuryInsuranceStatus === '2') {
    return { label: '复核停保原因' }
  }
  if (currentWorker.value.warningStatus === '1') {
    return { label: '跟进预警闭环' }
  }
  if (Number(currentWorker.value.monthlyIncome || 0) <= 0) {
    return { label: '修正收入回写' }
  }
  return { label: '查看归档详情' }
})

const readOnlyAlertTitle = computed(() => `${readOnlyRoleLabel.value}仅保留新业态人员查看、详情和导出`)
const readOnlyAlertDescription = computed(() => readOnlyRoleDescription.value || `${readOnlyRoleLabel.value}不能执行新业态人员同步等办理动作。`)

const currentWorkerActionSummary = computed(() => {
  if (!currentWorker.value) {
    return '先从待办焦点中选择一条人员记录，再决定是核对参保、复核停保、承接预警，还是修正收入回写。'
  }
  if (currentWorker.value.injuryInsuranceStatus === '0') {
    return '该人员当前未参保，应优先核对平台归属、从业类型与参保说明，明确是否需要立即补录保障。'
  }
  if (currentWorker.value.injuryInsuranceStatus === '2') {
    return '该人员处于停保状态，应先确认停保原因与停保时间，再决定是否恢复保障或补充办理说明。'
  }
  if (currentWorker.value.warningStatus === '1') {
    return '该人员已触发预警，建议继续承接预警中心处置要求，避免问题停留在新业态同步结果层。'
  }
  if (Number(currentWorker.value.monthlyIncome || 0) <= 0) {
    return '该人员收入回写异常，建议先核对平台侧收入口径和回写日志，再继续做月度归档。'
  }
  return '该人员当前状态相对稳定，可作为月度监管底稿沉淀，供后续企业复核和监管抽查使用。'
})

const currentWorkerActionTags = computed(() => {
  if (!currentWorker.value) {
    return [{ label: '待选择具体办理对象', type: 'info' }]
  }
  return buildHintTags(currentWorker.value)
})

const workerHintTags = computed(() => buildHintTags(currentWorker.value))
const detailHintTags = computed(() => buildHintTags(detail.value))

function getList() {
  loading.value = true
  Promise.all([
    listNewformWorker(queryParams.value),
    getNewformWorkerSummary(buildSummaryQuery())
  ]).then(([listResponse, summaryResponse]) => {
    workerList.value = listResponse.rows || []
    total.value = listResponse.total || 0
    summaryData.value = summaryResponse.data || {}
    syncCurrentWorker()
    loading.value = false
  }).catch(() => {
    loading.value = false
  })
}

function buildSummaryQuery() {
  return {
    statMonth: queryParams.value.statMonth,
    enterpriseId: queryParams.value.enterpriseId,
    personName: queryParams.value.personName,
    platformName: queryParams.value.platformName,
    employmentType: queryParams.value.employmentType,
    injuryInsuranceStatus: queryParams.value.injuryInsuranceStatus,
    warningStatus: queryParams.value.warningStatus
  }
}

function loadEnterpriseOptions() {
  optionselectEnterprise().then(response => {
    enterpriseOptions.value = response.data || []
  })
}

function syncCurrentWorker() {
  if (currentWorker.value) {
    const matched = visibleWorkerList.value.find(item => item.workerRecordId === currentWorker.value.workerRecordId)
    if (matched) {
      currentWorker.value = matched
      return
    }
  }
  currentWorker.value = visibleWorkerList.value.length > 0 ? visibleWorkerList.value[0] : undefined
}

function handleQuery() {
  queryParams.value.pageNum = 1
  getList()

watchEffect(() => {
  setPageGuide({
    title: '?????????' || '?????????',
    description: '?????????????????????????????????' || '?????????????????????????????????',
    portalExplanation: portalExplanationItems.value,
    focus: focusQueues.value,
    selection: [...selectedWorkerOverview.value, { label: '??????', value: currentWorkerActionSummary.value }],
    workflow: workflowSteps.value,
    hints: [...currentWorkerActionTags.value].slice(0, 6)
  })
})

}

function resetQuery() {
  proxy.resetForm('queryRef')
  Object.assign(queryParams.value, {
    pageNum: 1,
    pageSize: 10,
    statMonth: currentMonth(),
    enterpriseId: undefined,
    personName: undefined,
    platformName: undefined,
    employmentType: undefined,
    injuryInsuranceStatus: undefined,
    warningStatus: undefined
  })
  applyWorkbenchRouteQuery(route.query, queryParams.value, newformWorkerWorkbenchFields)
  activeFocusKey.value = resolveNewformWorkerFocusKey(route.query.focusKey)
  getList()
}

function clearWorkbenchContext() {
  activeFocusKey.value = ''
  Object.assign(queryParams.value, {
    pageNum: 1,
    pageSize: 10,
    statMonth: currentMonth(),
    enterpriseId: undefined,
    personName: undefined,
    platformName: undefined,
    employmentType: undefined,
    injuryInsuranceStatus: undefined,
    warningStatus: undefined
  })
  router.replace({
    path: route.path,
    query: stripWorkbenchRouteQuery(route.query, newformWorkerWorkbenchFields)
  })
  getList()
}

function applyNewformWorkerWorkbenchQuery(routeQuery = {}) {
  Object.assign(queryParams.value, {
    pageNum: 1,
    pageSize: 10,
    statMonth: currentMonth(),
    enterpriseId: undefined,
    personName: undefined,
    platformName: undefined,
    employmentType: undefined,
    injuryInsuranceStatus: undefined,
    warningStatus: undefined
  })
  applyWorkbenchRouteQuery(routeQuery, queryParams.value, newformWorkerWorkbenchFields)
  activeFocusKey.value = resolveNewformWorkerFocusKey(routeQuery.focusKey)
  syncCurrentWorker()
  getList()
}

function handleRowClick(row) {
  currentWorker.value = row
}

function handleSelectFocus(item) {
  activeFocusKey.value = item.key
  syncCurrentWorker()
}

function openDetail(row) {
  if (!row) {
    return
  }
  currentWorker.value = row
  detail.value = row
  detailOpen.value = true
}

function handleSync() {
  if (isReadOnlyRole.value) {
    blockReadOnlyAction('执行新业态人员同步')
    return
  }
  if (!queryParams.value.statMonth) {
    proxy.$modal.msgWarning('请先选择统计月份')
    return
  }
  syncNewformWorker({
    statMonth: queryParams.value.statMonth,
    enterpriseId: queryParams.value.enterpriseId
  }).then(response => {
    proxy.$modal.msgSuccess(response.msg || '模拟同步完成')
    getList()
  })
}

function handleExport() {
  proxy.download('ygb/newform/worker/export', { ...queryParams.value }, `newform_worker_${new Date().getTime()}.xlsx`)
}

function handlePrimaryWorkerAction() {
  if (!currentWorker.value) {
    return
  }
  openDetail(currentWorker.value)
}

function openSubmodule(item) {
  if (!item?.path) {
    return
  }
  proxy.$tab.openPage(item.title, item.path, item.query || {})
}

function handlePortalExplanationAction(action) {
  if (action?.path === route.path) {
    applyNewformWorkerWorkbenchQuery(action.query || {})
  }
  openPortalExplanationAction(router, action)
}

function blockReadOnlyAction(actionLabel) {
  proxy?.$modal?.msgWarning?.(`${readOnlyRoleLabel.value}仅保留新业态人员查看、详情和导出，不能${actionLabel}`)
}

function buildHintTags(item) {
  if (!item) {
    return [{ label: '未选中监管对象，可先在列表中选择待办理人员', type: 'info' }]
  }
  const tags = []
  if (item.injuryInsuranceStatus === '0') {
    tags.push({ label: '当前未参保，建议优先核对平台归属、从业类型和参保说明', type: 'danger' })
  } else if (item.injuryInsuranceStatus === '2') {
    tags.push({ label: '当前为停保对象，建议核对停保原因并确认是否需要恢复保障', type: 'warning' })
  } else {
    tags.push({ label: '当前参保状态正常，可继续做月度归档和后续监管分析', type: 'success' })
  }
  if (item.warningStatus === '1') {
    tags.push({ label: '当前已触发预警，建议继续承接到预警中心闭环', type: 'warning' })
  }
  if (Number(item.monthlyIncome || 0) <= 0) {
    tags.push({ label: '当前月收入为空或为 0，建议核对平台回写结果和收入口径', type: 'info' })
  }
  if (!item.sourceMessage) {
    tags.push({ label: '当前缺少来源消息，建议复查平台同步日志', type: 'info' })
  }
  return tags
}

function enterpriseName(enterpriseId) {
  const matched = enterpriseOptions.value.find(item => item.enterpriseId === enterpriseId)
  return matched ? matched.enterpriseName : ''
}

function injuryInsuranceLabel(value) {
  const matched = injuryInsuranceOptions.find(item => item.value === value)
  return matched ? matched.label : '-'
}

function warningStatusLabel(value) {
  const matched = warningStatusOptions.find(item => item.value === value)
  return matched ? matched.label : '-'
}

function resolveNewformWorkerFocusKey(value) {
  const normalized = String(value || '')
  return focusQueues.value.some(item => item.key === normalized) ? normalized : ''
}

function newformWorkerFocusLabel(value) {
  return focusQueues.value.find(item => item.key === value)?.title || value || '-'
}

function valueOrDefault(value, fallback = 0) {
  return value != null ? value : fallback
}

function formatMoney(value, withUnit = true) {
  if (value == null || value === '') {
    return withUnit ? '0.00 元' : '0.00'
  }
  const amount = Number(value)
  if (Number.isNaN(amount)) {
    return withUnit ? `${value} 元` : `${value}`
  }
  return withUnit ? `${amount.toFixed(2)} 元` : amount.toFixed(2)
}

function maskIdCard(value) {
  if (!value) {
    return '-'
  }
  if (value.length <= 8) {
    return value
  }
  return `${value.slice(0, 4)}********${value.slice(-4)}`
}

function currentMonth() {
  const date = new Date()
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}`
}

function matchWorkerFocus(row, key) {
  if (!key) {
    return true
  }
  if (key === 'uninsured') {
    return row.injuryInsuranceStatus === '0'
  }
  if (key === 'stopped') {
    return row.injuryInsuranceStatus === '2'
  }
  if (key === 'warning') {
    return row.warningStatus === '1'
  }
  if (key === 'income') {
    return Number(row.monthlyIncome || 0) <= 0
  }
  return false
}

function prioritizeFocusRows(rows, predicate) {
  const matched = []
  const rest = []
  ;(rows || []).forEach(item => {
    if (predicate(item)) {
      matched.push(item)
    } else {
      rest.push(item)
    }
  })
  return [...matched, ...rest]
}

loadEnterpriseOptions()
activeFocusKey.value = resolveNewformWorkerFocusKey(route.query.focusKey)
getList()
</script>

<style scoped lang="scss">
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
  color: #5f6f80;
  line-height: 1.7;
}

.ygb-workbench-alert__desc strong {
  color: #15304b;
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
  background: linear-gradient(180deg, #ffffff 0%, #f2f7ff 100%);
}

.ygb-card-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
}

.ygb-card-head--between {
  justify-content: space-between;
}

.ygb-card-head__title {
  color: #15304b;
  font-size: 16px;
  font-weight: 700;
}

.ygb-card-head__desc {
  margin-top: 6px;
  color: #627486;
  line-height: 1.7;
  font-size: 13px;
}

.ygb-focus-list,
.ygb-source-list {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.ygb-focus-list--single {
  grid-template-columns: minmax(0, 1fr);
}

.ygb-source-item {
  padding: 14px 16px;
  border-radius: 12px;
  background: #f6f9fc;
  border: 1px solid #e2eaf2;
}

.ygb-source-item__label {
  color: #708397;
  font-size: 12px;
}

.ygb-source-item__value {
  margin-top: 8px;
  color: #17324d;
  font-size: 15px;
  font-weight: 600;
  line-height: 1.6;
}

.ygb-focus-queue {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  width: 100%;
  padding: 16px;
  border-radius: 12px;
  border: 1px solid #e2eaf2;
  background: #f6f9fc;
  text-align: left;
  cursor: pointer;
  transition: all 0.2s ease;
}

.ygb-focus-queue:hover,
.ygb-focus-queue.is-active {
  border-color: #1f5aa6;
  background: #eef5ff;
}

.ygb-focus-queue__main strong {
  color: #17324d;
  font-size: 15px;
}

.ygb-focus-queue__main p {
  margin: 6px 0 0;
  color: #627486;
  line-height: 1.7;
  font-size: 13px;
}

.ygb-focus-queue__side {
  display: flex;
  min-width: 120px;
  flex-direction: column;
  align-items: flex-end;
  gap: 6px;
}

.ygb-focus-queue__count {
  color: #15304b;
  font-size: 20px;
  font-weight: 700;
}

.ygb-focus-queue__action {
  color: #1f5aa6;
  font-size: 12px;
}

.ygb-pipeline-list {
  display: grid;
  gap: 12px;
}

.ygb-pipeline-item {
  display: flex;
  gap: 14px;
  padding: 14px 16px;
  border-radius: 12px;
  background: #f7f9fc;
  border: 1px solid #e0e8f0;
}

.ygb-pipeline-item__index {
  width: 34px;
  height: 34px;
  line-height: 34px;
  border-radius: 10px;
  background: #1f5aa6;
  color: #fff;
  text-align: center;
  font-size: 13px;
  font-weight: 700;
  flex-shrink: 0;
}

.ygb-pipeline-item__body strong {
  color: #183552;
  font-size: 14px;
}

.ygb-pipeline-item__body p {
  margin: 6px 0 0;
  color: #627486;
  line-height: 1.7;
  font-size: 13px;
}

.ygb-tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.ygb-recommend-panel {
  margin-top: 16px;
  padding: 14px 16px;
  border-radius: 12px;
  background: #f6f9fc;
  border: 1px solid #e2eaf2;
}

.ygb-recommend-panel__title {
  color: #15304b;
  font-weight: 700;
}

.ygb-recommend-panel__summary {
  margin: 8px 0 0;
  color: #627486;
  line-height: 1.7;
  font-size: 13px;
}

.ygb-focus-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 16px;
}

.detail-block {
  margin-top: 20px;
}

.detail-block h3 {
  margin: 0 0 12px;
  color: #15304b;
  font-size: 15px;
}

@media (max-width: 1200px) {
  .ygb-summary-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 768px) {
  .ygb-summary-grid,
  .ygb-focus-grid,
  .ygb-source-list {
    grid-template-columns: minmax(0, 1fr);
  }

  .ygb-focus-queue {
    flex-direction: column;
    align-items: flex-start;
  }

  .ygb-focus-queue__side {
    min-width: 0;
    align-items: flex-start;
  }
}
</style>
