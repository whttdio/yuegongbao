<template>
  <div class="app-container ygb-page">
    <section class="gov-page-header ygb-page__header">
      <div>
        <p class="ygb-page__eyebrow">用工复核线</p>
        <h1 class="ygb-page__title">假外包识别与复核台账</h1>
        <p class="ygb-page__desc">
          按考勤管理、排班管理、奖惩管理、培训管理四个维度固化评分，面向企业管理员和监管经办优先识别疑似假外包对象，
          作为假外包复核、预警联动和月度合规归档的重要台账。
        </p>
      </div>
      <div class="ygb-table-tip">
        当前版本使用人工评分录入完成分析闭环；后续可直接替换为 OCR、文档和设备数据自动采分，不改变当前台账结构。
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
          <el-date-picker v-model="queryParams.statMonth" type="month" format="YYYY-MM" value-format="YYYY-MM" style="width: 160px" />
        </el-form-item>
        <el-form-item label="企业">
          <el-select v-model="queryParams.enterpriseId" clearable filterable style="width: 220px">
            <el-option v-for="item in enterpriseOptions" :key="item.enterpriseId" :label="item.enterpriseName" :value="item.enterpriseId" />
          </el-select>
        </el-form-item>
        <el-form-item label="疑似标志">
          <el-select v-model="queryParams.suspectedFlag" clearable style="width: 140px">
            <el-option v-for="item in suspectedFlagOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="预警状态">
          <el-select v-model="queryParams.warningStatus" clearable style="width: 140px">
            <el-option v-for="item in warningStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="toolbar-card ygb-toolbar-card" shadow="never">
      <el-row :gutter="10">
        <el-col v-if="!isReadOnlyRole" :span="1.5">
          <el-button type="primary" plain icon="Operation" @click="openAnalyzeDialog" v-hasPermi="['ygb:fakeOutsourcing:analyze']">执行识别</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['ygb:fakeOutsourcing:export']">导出</el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>
    </el-card>

    <el-card class="table-card ygb-table-card" shadow="never">
      <template #header>
        <div class="ygb-card-head ygb-card-head--between">
          <div>
            <div class="ygb-card-head__title">假外包识别复核台账</div>
            <div class="ygb-card-head__desc">{{ focusTableHint }}</div>
          </div>
          <div class="ygb-card-head__desc">当前总量 {{ total }} 条</div>
        </div>
      </template>
      <el-table v-loading="loading" :data="visibleRecordList" @row-click="handleRowClick">
        <el-table-column label="ID" prop="recordId" width="90" />
        <el-table-column label="统计月份" prop="statMonth" width="110" />
        <el-table-column label="企业" prop="enterpriseName" min-width="240" />
        <el-table-column label="考勤分" prop="attendanceScore" width="90" />
        <el-table-column label="排班分" prop="scheduleScore" width="90" />
        <el-table-column label="奖惩分" prop="rewardScore" width="90" />
        <el-table-column label="培训分" prop="trainingScore" width="90" />
        <el-table-column label="综合分" prop="totalScore" width="90" />
        <el-table-column label="疑似标志" prop="suspectedFlag" width="110">
          <template #default="scope">
            <dict-tag :options="suspectedFlagOptions" :value="scope.row.suspectedFlag" />
          </template>
        </el-table-column>
        <el-table-column label="预警状态" prop="warningStatus" width="110">
          <template #default="scope">
            <dict-tag :options="warningStatusOptions" :value="scope.row.warningStatus" />
          </template>
        </el-table-column>
        <el-table-column label="证据摘要" prop="evidenceSummary" min-width="260" show-overflow-tooltip />
        <el-table-column class-name="table-fill-column" min-width="1" />

        <el-table-column label="操作" fixed="right" align="center" width="100" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-button link type="info" icon="View" @click.stop="openDetail(scope.row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>

    <el-dialog title="假外包识别" v-model="analyzeOpen" width="760px" append-to-body>
      <el-form ref="analyzeRef" :model="analyzeForm" :rules="analyzeRules" label-width="110px">
        <div class="ygb-panel-grid">
          <el-form-item label="统计月份" prop="statMonth">
            <el-date-picker v-model="analyzeForm.statMonth" type="month" format="YYYY-MM" value-format="YYYY-MM" style="width: 100%" />
          </el-form-item>
          <el-form-item label="企业" prop="enterpriseId">
            <el-select v-model="analyzeForm.enterpriseId" filterable placeholder="请选择企业">
              <el-option v-for="item in enterpriseOptions" :key="item.enterpriseId" :label="item.enterpriseName" :value="item.enterpriseId" />
            </el-select>
          </el-form-item>
          <el-form-item label="考勤管理分" prop="attendanceScore">
            <el-input-number v-model="analyzeForm.attendanceScore" :min="0" :max="100" style="width: 100%" />
          </el-form-item>
          <el-form-item label="排班管理分" prop="scheduleScore">
            <el-input-number v-model="analyzeForm.scheduleScore" :min="0" :max="100" style="width: 100%" />
          </el-form-item>
          <el-form-item label="奖惩管理分" prop="rewardScore">
            <el-input-number v-model="analyzeForm.rewardScore" :min="0" :max="100" style="width: 100%" />
          </el-form-item>
          <el-form-item label="培训管理分" prop="trainingScore">
            <el-input-number v-model="analyzeForm.trainingScore" :min="0" :max="100" style="width: 100%" />
          </el-form-item>
        </div>
        <el-form-item label="证据摘要" prop="evidenceSummary">
          <el-input v-model="analyzeForm.evidenceSummary" type="textarea" :rows="4" placeholder="请输入人员管理、排班控制、培训签到等佐证摘要" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitAnalyze">确定</el-button>
          <el-button @click="analyzeOpen = false">取消</el-button>
        </div>
      </template>
    </el-dialog>

    <page-detail-dialog v-model="detailOpen" title="假外包识别详情" width="760px">
      <template v-if="detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="统计月份">{{ detail.statMonth || '-' }}</el-descriptions-item>
          <el-descriptions-item label="疑似标志">
            <dict-tag :options="suspectedFlagOptions" :value="detail.suspectedFlag" />
          </el-descriptions-item>
          <el-descriptions-item label="企业">{{ detail.enterpriseName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="区域编码">{{ detail.regionCode || '-' }}</el-descriptions-item>
          <el-descriptions-item label="考勤管理分">{{ detail.attendanceScore ?? 0 }}</el-descriptions-item>
          <el-descriptions-item label="排班管理分">{{ detail.scheduleScore ?? 0 }}</el-descriptions-item>
          <el-descriptions-item label="奖惩管理分">{{ detail.rewardScore ?? 0 }}</el-descriptions-item>
          <el-descriptions-item label="培训管理分">{{ detail.trainingScore ?? 0 }}</el-descriptions-item>
          <el-descriptions-item label="综合分">{{ detail.totalScore ?? 0 }}</el-descriptions-item>
          <el-descriptions-item label="预警状态">
            <dict-tag :options="warningStatusOptions" :value="detail.warningStatus" />
          </el-descriptions-item>
          <el-descriptions-item label="证据摘要" :span="2">{{ detail.evidenceSummary || '-' }}</el-descriptions-item>
          <el-descriptions-item label="办理备注" :span="2">{{ detail.remark || '-' }}</el-descriptions-item>
          <el-descriptions-item label="创建时间" :span="2">{{ parseTime(detail.createTime, '{y}-{m}-{d} {h}:{i}:{s}') || '-' }}</el-descriptions-item>
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

<script setup name="YgbFakeOutsourcing">
import { listFakeOutsourcing, getFakeOutsourcingSummary, analyzeFakeOutsourcing } from "@/api/ygb/fakeOutsourcing"
import { optionselectEnterprise } from "@/api/ygb/enterprise"
import { useRoute, useRouter } from "vue-router"
import { decoratePortalExplanationItems, openPortalExplanationAction } from "@/utils/portalExplanation"
import { useRoleViewMode } from "@/utils/roleView"
import { applyWorkbenchRouteQuery, buildWorkbenchContext, stripWorkbenchRouteQuery } from "@/utils/workbenchLink"
import { useWorkbenchAssist } from "@/composables/useWorkbenchAssist"

const { proxy } = getCurrentInstance()
const route = useRoute()
const { setPageGuide } = useWorkbenchAssist()
const router = useRouter()
const { isReadOnlyRole, readOnlyRoleLabel, readOnlyRoleDescription } = useRoleViewMode()
const fakeOutsourcingWorkbenchFields = ["statMonth", "enterpriseId", "suspectedFlag", "warningStatus", "focusKey"]

const suspectedFlagOptions = [
  { label: "否", value: "0" },
  { label: "是", value: "1" }
]

const warningStatusOptions = [
  { label: "未预警", value: "0" },
  { label: "已预警", value: "1" }
]

const fakeOutsourcingList = ref([])
const enterpriseOptions = ref([])
const summaryData = ref({})
const currentRecord = ref(undefined)
const detail = ref(undefined)
const loading = ref(false)
const showSearch = ref(true)
const total = ref(0)
const analyzeOpen = ref(false)
const detailOpen = ref(false)
const activeFocusKey = ref("")

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    statMonth: undefined,
    enterpriseId: undefined,
    suspectedFlag: undefined,
    warningStatus: undefined
  },
  analyzeForm: {
    statMonth: undefined,
    enterpriseId: undefined,
    attendanceScore: 18,
    scheduleScore: 16,
    rewardScore: 16,
    trainingScore: 15,
    evidenceSummary: undefined
  },
  analyzeRules: {
    statMonth: [{ required: true, message: "统计月份不能为空", trigger: "change" }],
    enterpriseId: [{ required: true, message: "企业不能为空", trigger: "change" }]
  }
})

const { queryParams, analyzeForm, analyzeRules } = toRefs(data)

applyWorkbenchRouteQuery(route.query, queryParams.value, fakeOutsourcingWorkbenchFields)

const summaryCards = computed(() => ([
  {
    key: "total",
    label: "识别记录",
    value: summaryData.value.totalCount != null ? summaryData.value.totalCount : total.value,
    unit: "条",
    note: "当前筛选条件下已进入假外包识别复核台账的记录总量。",
    cardClass: ""
  },
  {
    key: "normal",
    label: "非疑似对象",
    value: valueOrDefault(summaryData.value.normalCount, 0),
    unit: "条",
    note: "综合评分正常的对象可继续作为月度复核归档底稿。",
    cardClass: "ygb-summary-card--success"
  },
  {
    key: "suspected",
    label: "疑似对象",
    value: valueOrDefault(summaryData.value.suspectedCount, 0),
    unit: "条",
    note: "综合评分低于 60 的对象需要优先补充佐证、复核用工模式和整改计划。",
    cardClass: "ygb-summary-card--warning"
  },
  {
    key: "warned",
    label: "已触发预警",
    value: valueOrDefault(summaryData.value.warnedCount, 0),
    unit: "条",
    note: "已写入预警中心的对象需要继续承接假外包复核和预警闭环。",
    cardClass: "ygb-summary-card--primary"
  }
]))

const focusQueues = computed(() => ([
  {
    key: "suspected",
    title: "疑似假外包对象",
    desc: "综合分偏低且已命中疑似标志的对象应优先进入复核办理。",
    count: valueOrDefault(summaryData.value.suspectedCount, 0),
    unit: "条",
    actionText: "先补证据复核"
  },
  {
    key: "lowScore",
    title: "低分对象",
    desc: "综合分较低但未必已触发疑似标志的对象也应优先复核。",
    count: valueOrDefault(summaryData.value.lowScoreCount, 0),
    unit: "条",
    actionText: "先查四维短板"
  },
  {
    key: "warned",
    title: "已预警对象",
    desc: "已写入预警中心的对象更适合继续承接假外包复核和闭环办理。",
    count: valueOrDefault(summaryData.value.warnedCount, 0),
    unit: "条",
    actionText: "承接预警闭环"
  },
  {
    key: "all",
    title: "当前纳管总量",
    desc: "用于统看当前月份和企业范围内的识别复核台账盘子。",
    count: valueOrDefault(summaryData.value.totalCount, total.value),
    unit: "条",
    actionText: "查看全部对象"
  }
]))

function buildFakeOutsourcingExplanationQuery(extraQuery = {}) {
  return {
    statMonth: queryParams.value.statMonth,
    enterpriseId: queryParams.value.enterpriseId,
    suspectedFlag: queryParams.value.suspectedFlag,
    warningStatus: queryParams.value.warningStatus,
    ...extraQuery
  }
}

const fallbackPortalExplanations = computed(() => focusQueues.value.map(item => ({
  key: item.key,
  dimensionName: item.title,
  currentValue: item.count,
  targetValue: item.key === "lowScore" ? ">= 60" : "-",
  summary: item.desc,
  evidenceModule: "fakeOutsourcing",
  recommendModule: "fakeOutsourcing",
  defaultQuery: buildFakeOutsourcingExplanationQuery({ focusKey: item.key }),
  sourceLabel: "530.1 假外包治理解释",
  sourceDescription: item.desc,
  actionText: item.actionText
})))

const portalExplanations = computed(() => (Array.isArray(summaryData.value.ygbExplanation) && summaryData.value.ygbExplanation.length
  ? summaryData.value.ygbExplanation
  : fallbackPortalExplanations.value))

const portalExplanationItems = computed(() => decoratePortalExplanationItems(portalExplanations.value, {
  portalCode: "ygb",
  panelTitle: "530.1 假外包治理解释",
  panelDescription: "假外包解释统一按当前粤工保门户来源条件输出。"
}))

const activeFocus = computed(() => focusQueues.value.find(item => item.key === activeFocusKey.value) || focusQueues.value[0])

const visibleRecordList = computed(() => prioritizeFocusRows(fakeOutsourcingList.value, row => matchRecordFocus(row, activeFocus.value?.key)))

const workbenchContext = computed(() => buildWorkbenchContext(route.query, {
  fields: fakeOutsourcingWorkbenchFields,
  title: "当前假外包页面沿用了工作台来源条件",
  description: "当前页面已带入工作台筛选条件，便于直接承接当前门户的治理语境。",
  fieldLabels: {
    statMonth: "Month",
    enterpriseId: "Enterprise",
    suspectedFlag: "Suspected",
    warningStatus: "Warning",
    focusKey: "Focus"
  },
  fieldFormatters: {
    enterpriseId: value => enterpriseName(value) || value,
    focusKey: value => fakeOutsourcingFocusLabel(value),
    suspectedFlag: value => suspectedFlagLabel(value),
    warningStatus: value => warningStatusLabel(value)
  }
}))

const focusTableHint = computed(() => {
  if (!activeFocus.value) {
    return "当前按默认顺序展示假外包识别台账。"
  }
  return `${activeFocus.value.title}优先置顶，便于先处理最需要补证据、重评分或承接预警的复核对象。`
})

const selectedRecordOverview = computed(() => {
  if (!currentRecord.value) {
    return [
      { label: "企业", value: "-" },
      { label: "月份/疑似", value: "-" },
      { label: "四维评分", value: "-" },
      { label: "综合分/预警", value: "-" }
    ]
  }
  return [
    { label: "企业", value: currentRecord.value.enterpriseName || "-" },
    { label: "月份/疑似", value: `${currentRecord.value.statMonth || "-"} / ${suspectedFlagLabel(currentRecord.value.suspectedFlag)}` },
    { label: "四维评分", value: `${currentRecord.value.attendanceScore ?? 0} / ${currentRecord.value.scheduleScore ?? 0} / ${currentRecord.value.rewardScore ?? 0} / ${currentRecord.value.trainingScore ?? 0}` },
    { label: "综合分/预警", value: `${currentRecord.value.totalScore ?? 0} / ${warningStatusLabel(currentRecord.value.warningStatus)}` }
  ]
})

const primaryRecordAction = computed(() => {
  if (isReadOnlyRole.value) {
    return { label: "鏌ョ湅璇︽儏", action: "detail" }
  }
  if (!currentRecord.value) {
    return { label: "查看详情", action: "detail" }
  }
  if (currentRecord.value.suspectedFlag === "1" || Number(currentRecord.value.totalScore || 0) < 60 || !currentRecord.value.evidenceSummary) {
    return { label: "执行识别", action: "analyze" }
  }
  return { label: "查看详情", action: "detail" }
})

const readOnlyAlertTitle = computed(() => `${readOnlyRoleLabel.value}仅保留假外包识别查看、详情和导出`)
const readOnlyAlertDescription = computed(() => readOnlyRoleDescription.value || `${readOnlyRoleLabel.value}不能执行假外包识别等办理动作。`)

const currentRecordActionSummary = computed(() => {
  if (!currentRecord.value) {
    return "先从左侧焦点队列选择重点复核对象，再在台账中联动查看当前办理建议。"
  }
  if (primaryRecordAction.value.action === "analyze") {
    return "当前记录更适合优先重做识别或补充证据，先核对四维评分、证据摘要和疑似标志，再决定是否进入后续复核闭环。"
  }
  return "当前记录以详情复核为主，先看四维评分、综合分、证据摘要和预警状态，再决定是否继续复核。"
})

const currentRecordActionTags = computed(() => buildHintTags(currentRecord.value).slice(0, 3))

const workflowSteps = computed(() => ([
  {
    label: "先锁定统计月份",
    desc: "先按统计月份和企业范围锁定识别台账，避免跨月评分和证据混用。"
  },
  {
    label: "执行四维识别",
    desc: "基于考勤、排班、奖惩和培训四个维度录入评分，统一生成综合分、疑似标志和预警状态。"
  },
  {
    label: "补充证据与复核",
    desc: "对低分和疑似对象优先补充证据摘要，回查人员管理、排班控制和培训签到等复核佐证。"
  },
  {
    label: "承接预警处置",
    desc: "已预警对象继续承接到预警中心和假外包复核链路，避免识别结果停留在台账页。"
  },
  {
    label: "完成月度归档",
    desc: "正常或已复核完成的对象沉淀为月度复核底稿，供监管和企业后续复核。"
  }
]))

const recordHintTags = computed(() => buildHintTags(currentRecord.value))
const detailHintTags = computed(() => buildHintTags(detail.value))

function getList() {
  loading.value = true
  Promise.all([
    listFakeOutsourcing(queryParams.value),
    getFakeOutsourcingSummary(buildSummaryQuery())
  ]).then(([listResponse, summaryResponse]) => {
    fakeOutsourcingList.value = listResponse.rows || []
    total.value = listResponse.total || 0
    summaryData.value = summaryResponse.data || {}
    syncCurrentRecord()
    loading.value = false
  }).catch(() => {
    loading.value = false
  })
}

function buildSummaryQuery() {
  return {
    statMonth: queryParams.value.statMonth,
    enterpriseId: queryParams.value.enterpriseId,
    suspectedFlag: queryParams.value.suspectedFlag,
    warningStatus: queryParams.value.warningStatus
  }
}

function loadEnterpriseOptions() {
  optionselectEnterprise().then(response => {
    enterpriseOptions.value = response.data || []
  })
}

function syncCurrentRecord() {
  if (currentRecord.value) {
    const matched = visibleRecordList.value.find(item => item.recordId === currentRecord.value.recordId)
    if (matched) {
      currentRecord.value = matched
      return
    }
  }
  currentRecord.value = visibleRecordList.value.length > 0 ? visibleRecordList.value[0] : undefined
}

function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

watchEffect(() => {
  setPageGuide({
    title: '假外包识别',
    description: '结合合同、人员管理和作业控制线索识别疑似假外包，支撑合规复核和整改。',
    portalExplanation: portalExplanationItems.value,
    focus: focusQueues.value,
    selection: [...selectedRecordOverview.value, { label: '当前处置建议', value: currentRecordActionSummary.value }],
    workflow: workflowSteps.value,
    hints: [...currentRecordActionTags.value].slice(0, 6)
  })
})

function resetQuery() {
  proxy.resetForm("queryRef")
  Object.assign(queryParams.value, {
    pageNum: 1,
    pageSize: 10,
    statMonth: undefined,
    enterpriseId: undefined,
    suspectedFlag: undefined,
    warningStatus: undefined
  })
  applyWorkbenchRouteQuery(route.query, queryParams.value, fakeOutsourcingWorkbenchFields)
  activeFocusKey.value = resolveFakeOutsourcingFocusKey(route.query.focusKey)
  getList()
}

function clearWorkbenchContext() {
  activeFocusKey.value = ""
  Object.assign(queryParams.value, {
    pageNum: 1,
    pageSize: 10,
    statMonth: undefined,
    enterpriseId: undefined,
    suspectedFlag: undefined,
    warningStatus: undefined
  })
  router.replace({
    path: route.path,
    query: stripWorkbenchRouteQuery(route.query, fakeOutsourcingWorkbenchFields)
  })
  getList()
}

function applyFakeOutsourcingWorkbenchQuery(routeQuery = {}) {
  Object.assign(queryParams.value, {
    pageNum: 1,
    pageSize: 10,
    statMonth: undefined,
    enterpriseId: undefined,
    suspectedFlag: undefined,
    warningStatus: undefined
  })
  applyWorkbenchRouteQuery(routeQuery, queryParams.value, fakeOutsourcingWorkbenchFields)
  activeFocusKey.value = resolveFakeOutsourcingFocusKey(routeQuery.focusKey)
  syncCurrentRecord()
  getList()
}

function handleExport() {
  proxy.download("ygb/special/fakeOutsourcing/export", { ...queryParams.value }, `fake_outsourcing_${new Date().getTime()}.xlsx`)
}

function handleRowClick(row) {
  currentRecord.value = row
}

function handleSelectFocus(item) {
  activeFocusKey.value = item.key
  syncCurrentRecord()
}

function openDetail(row) {
  currentRecord.value = row
  detail.value = row
  detailOpen.value = true
}

function openAnalyzeDialog(row) {
  if (isReadOnlyRole.value) {
    blockReadOnlyAction("执行假外包识别")
    return
  }
  const target = row || currentRecord.value
  analyzeForm.value = {
    statMonth: target?.statMonth || queryParams.value.statMonth,
    enterpriseId: target?.enterpriseId || queryParams.value.enterpriseId,
    attendanceScore: target?.attendanceScore ?? 18,
    scheduleScore: target?.scheduleScore ?? 16,
    rewardScore: target?.rewardScore ?? 16,
    trainingScore: target?.trainingScore ?? 15,
    evidenceSummary: target?.evidenceSummary
  }
  analyzeOpen.value = true
}

function handlePrimaryRecordAction() {
  if (!currentRecord.value) {
    return
  }
  if (primaryRecordAction.value.action === "analyze") {
    openAnalyzeDialog(currentRecord.value)
    return
  }
  openDetail(currentRecord.value)
}

function handlePortalExplanationAction(action) {
  if (action?.path === route.path) {
    applyFakeOutsourcingWorkbenchQuery(action.query || {})
  }
  openPortalExplanationAction(router, action)
}

function submitAnalyze() {
  if (isReadOnlyRole.value) {
    blockReadOnlyAction("提交假外包识别")
    return
  }
  proxy.$refs["analyzeRef"].validate(valid => {
    if (!valid) {
      return
    }
    analyzeFakeOutsourcing(analyzeForm.value).then(response => {
      proxy.$modal.msgSuccess(response.msg || "假外包识别完成")
      analyzeOpen.value = false
      getList()
    })
  })
}

function blockReadOnlyAction(actionLabel) {
  proxy?.$modal?.msgWarning?.(`${readOnlyRoleLabel.value}仅保留假外包识别查看、详情和导出，不能${actionLabel}`)
}

function buildHintTags(item) {
  if (!item) {
    return [{ label: "未选中识别记录，可先在列表中选择待复核对象", type: "info" }]
  }
  const tags = []
  if (item.suspectedFlag === "1") {
    tags.push({ label: "当前为疑似假外包对象，建议优先补充复核佐证并复核用工模式", type: "danger" })
  } else {
    tags.push({ label: "当前识别结果正常，可继续月度复核归档", type: "success" })
  }
  if (item.warningStatus === "1") {
    tags.push({ label: "当前已触发预警，建议继续承接到预警中心闭环处置", type: "warning" })
  }
  if (Number(item.totalScore || 0) < 50) {
    tags.push({ label: "综合分低于 50，建议优先核查排班控制、奖惩记录和培训佐证", type: "warning" })
  }
  if (!item.evidenceSummary) {
    tags.push({ label: "证据摘要缺失，建议先补充人员管理、排班和培训佐证", type: "info" })
  }
  return tags
}

function matchRecordFocus(item, focusKey) {
  if (!focusKey || focusKey === "all") {
    return true
  }
  if (focusKey === "suspected") {
    return item.suspectedFlag === "1"
  }
  if (focusKey === "lowScore") {
    return Number(item.totalScore || 0) < 60
  }
  if (focusKey === "warned") {
    return item.warningStatus === "1"
  }
  return false
}

function prioritizeFocusRows(rows, predicate) {
  const matched = []
  const others = []
  rows.forEach(row => {
    if (predicate(row)) {
      matched.push(row)
      return
    }
    others.push(row)
  })
  return [...matched, ...others]
}

function enterpriseName(enterpriseId) {
  const matched = enterpriseOptions.value.find(item => item.enterpriseId === enterpriseId)
  return matched ? matched.enterpriseName : ""
}

function suspectedFlagLabel(value) {
  const matched = suspectedFlagOptions.find(item => item.value === value)
  return matched ? matched.label : "-"
}

function warningStatusLabel(value) {
  const matched = warningStatusOptions.find(item => item.value === value)
  return matched ? matched.label : "-"
}

function resolveFakeOutsourcingFocusKey(value) {
  const normalized = String(value || "")
  return focusQueues.value.some(item => item.key === normalized) ? normalized : ""
}

function fakeOutsourcingFocusLabel(value) {
  return focusQueues.value.find(item => item.key === value)?.title || value || "-"
}

function valueOrDefault(value, fallback = 0) {
  return value != null ? value : fallback
}

watch(focusQueues, queues => {
  if (!queues.length) {
    activeFocusKey.value = ""
    return
  }
  if (!queues.find(item => item.key === activeFocusKey.value)) {
    activeFocusKey.value = queues[0].key
  }
  syncCurrentRecord()
}, { immediate: true })

loadEnterpriseOptions()
activeFocusKey.value = resolveFakeOutsourcingFocusKey(route.query.focusKey)
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
  display: flex;
  flex-direction: column;
}

.ygb-focus-queue {
  width: 100%;
  display: flex;
  justify-content: space-between;
  gap: 16px;
  padding: 14px 16px;
  border-radius: 12px;
  border: 1px solid #e2eaf2;
  background: #f6f9fc;
  text-align: left;
  cursor: pointer;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.ygb-focus-queue:hover,
.ygb-focus-queue.is-active {
  border-color: #1768ac;
  box-shadow: 0 10px 24px rgba(23, 104, 172, 0.1);
}

.ygb-focus-queue__main strong {
  display: block;
  margin-bottom: 6px;
  color: #15304b;
}

.ygb-focus-queue__main p {
  margin: 0;
  color: #627486;
  line-height: 1.7;
  font-size: 13px;
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
  color: #1768ac;
}

.ygb-focus-queue__action {
  color: #1768ac;
  font-size: 12px;
}

.ygb-focus-item,
.ygb-source-item {
  padding: 14px 16px;
  border-radius: 12px;
  background: #f6f9fc;
  border: 1px solid #e2eaf2;
}

.ygb-focus-item__label,
.ygb-source-item__label {
  color: #708397;
  font-size: 12px;
}

.ygb-focus-item__value,
.ygb-source-item__value {
  margin-top: 8px;
  color: #17324d;
  font-size: 15px;
  font-weight: 600;
  line-height: 1.6;
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

.ygb-card-head--between {
  justify-content: space-between;
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
  .ygb-focus-list,
  .ygb-source-list {
    grid-template-columns: minmax(0, 1fr);
  }
}
</style>
