<template>
  <div class="app-container ygb-page">
    <section class="gov-page-header ygb-page__header">
      <div>
        <p class="ygb-page__eyebrow">财务整改线</p>
        <h1 class="ygb-page__title">个税比对整改台账</h1>
        <p class="ygb-page__desc">
          对比工资实发与个税申报收入差异，面向企业管理员、财务和监管经办优先识别差异异常与已触发预警对象，
          作为工资发放复核、税务口径回查和联动预警处置的关键整改台账。
        </p>
      </div>
      <div class="ygb-table-tip">
        当前页面按月份同步税务申报数据；差异超过 10% 会自动写入预警中心，页面已按粤工保财务整改链路重组。
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
        <el-form-item label="人员">
          <el-input v-model="queryParams.personName" clearable style="width: 180px" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="结果">
          <el-select v-model="queryParams.compareResult" clearable style="width: 140px">
            <el-option v-for="item in compareResultOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="预警">
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
          <el-button type="primary" plain icon="RefreshRight" @click="handleSync" v-hasPermi="['ygb:taxCompare:sync']">同步税务数据</el-button>
        </el-col>
        <el-col v-if="!isReadOnlyRole" :span="1.5">
          <el-button type="success" plain icon="Operation" @click="handleCompare" v-hasPermi="['ygb:taxCompare:compare']">执行比对</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['ygb:taxCompare:export']">导出</el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>
    </el-card>

    <el-card class="table-card ygb-table-card" shadow="never">
      <template #header>
        <div class="ygb-card-head ygb-card-head--between">
          <div>
            <div class="ygb-card-head__title">个税比对整改台账</div>
            <div class="ygb-card-head__desc">{{ focusTableHint }}</div>
          </div>
          <div class="ygb-card-head__desc">当前总量 {{ total }} 条</div>
        </div>
      </template>
      <el-table v-loading="loading" :data="visibleCompareList" @row-click="handleRowClick">
        <el-table-column label="ID" prop="compareId" width="90" />
        <el-table-column label="统计月份" prop="statMonth" width="110" />
        <el-table-column label="企业" prop="enterpriseName" min-width="220" />
        <el-table-column label="人员" prop="personName" width="120" />
        <el-table-column label="工资实发" prop="salaryAmount" width="120">
          <template #default="scope">
            {{ formatMoney(scope.row.salaryAmount) }}
          </template>
        </el-table-column>
        <el-table-column label="申报收入" prop="declaredAmount" width="120">
          <template #default="scope">
            {{ formatMoney(scope.row.declaredAmount) }}
          </template>
        </el-table-column>
        <el-table-column label="差异率(%)" prop="diffRatio" width="120">
          <template #default="scope">
            {{ formatRatio(scope.row.diffRatio) }}
          </template>
        </el-table-column>
        <el-table-column label="比对结果" prop="compareResult" width="120">
          <template #default="scope">
            <dict-tag :options="compareResultOptions" :value="scope.row.compareResult" />
          </template>
        </el-table-column>
        <el-table-column label="预警状态" prop="warningStatus" width="120">
          <template #default="scope">
            <dict-tag :options="warningStatusOptions" :value="scope.row.warningStatus" />
          </template>
        </el-table-column>
        <el-table-column label="来源状态" prop="sourceStatus" width="120" />
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

    <page-detail-dialog v-model="detailOpen" title="个税比对详情" width="760px">
      <template v-if="detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="统计月份">{{ detail.statMonth || '-' }}</el-descriptions-item>
          <el-descriptions-item label="比对结果">
            <dict-tag :options="compareResultOptions" :value="detail.compareResult" />
          </el-descriptions-item>
          <el-descriptions-item label="企业">{{ detail.enterpriseName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="人员">{{ detail.personName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="身份证号">{{ maskIdCard(detail.idCard) || '-' }}</el-descriptions-item>
          <el-descriptions-item label="区域编码">{{ detail.regionCode || '-' }}</el-descriptions-item>
          <el-descriptions-item label="工资实发">{{ formatMoney(detail.salaryAmount) }}</el-descriptions-item>
          <el-descriptions-item label="申报收入">{{ formatMoney(detail.declaredAmount) }}</el-descriptions-item>
          <el-descriptions-item label="差异率">{{ formatRatio(detail.diffRatio) }}</el-descriptions-item>
          <el-descriptions-item label="预警状态">
            <dict-tag :options="warningStatusOptions" :value="detail.warningStatus" />
          </el-descriptions-item>
          <el-descriptions-item label="来源状态">{{ detail.sourceStatus || '-' }}</el-descriptions-item>
          <el-descriptions-item label="回写时间">{{ parseTime(detail.callbackTime, '{y}-{m}-{d} {h}:{i}:{s}') || '-' }}</el-descriptions-item>
          <el-descriptions-item label="来源流水号" :span="2">{{ detail.sourceSerialNo || '-' }}</el-descriptions-item>
          <el-descriptions-item label="来源消息" :span="2">{{ detail.sourceMessage || '-' }}</el-descriptions-item>
          <el-descriptions-item label="办理备注" :span="2">{{ detail.remark || '-' }}</el-descriptions-item>
          <el-descriptions-item label="原始报文" :span="2">{{ detail.rawPayload || '-' }}</el-descriptions-item>
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

<script setup name="YgbTaxCompare">
import { listTaxCompare, getTaxCompareSummary, syncTaxCompare, executeTaxCompare } from "@/api/ygb/taxCompare"
import { optionselectEnterprise } from "@/api/ygb/enterprise"
import { decoratePortalExplanationItems, openPortalExplanationAction } from '@/utils/portalExplanation'
import { useRoute, useRouter } from "vue-router"
import { applyWorkbenchRouteQuery, buildWorkbenchContext, stripWorkbenchRouteQuery } from "@/utils/workbenchLink"
import { useRoleViewMode } from "@/utils/roleView"
import { useWorkbenchAssist } from "@/composables/useWorkbenchAssist"

const { proxy } = getCurrentInstance()
const route = useRoute()
const { setPageGuide } = useWorkbenchAssist()
const router = useRouter()
const { isReadOnlyRole, readOnlyRoleLabel, readOnlyRoleDescription } = useRoleViewMode()
const taxCompareWorkbenchFields = ["enterpriseId", "statMonth", "compareResult"]
const workbenchClearLabel = "清空来源条件"

const compareResultOptions = [
  { label: "正常", value: "1" },
  { label: "异常", value: "2" },
  { label: "待复核", value: "3", elTagType: "warning" }
]

const warningStatusOptions = [
  { label: "未预警", value: "0" },
  { label: "已预警", value: "1" }
]

const taxCompareList = ref([])
const enterpriseOptions = ref([])
const summaryData = ref({})
const currentCompare = ref(undefined)
const detail = ref(undefined)
const loading = ref(false)
const showSearch = ref(true)
const total = ref(0)
const detailOpen = ref(false)

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    statMonth: undefined,
    enterpriseId: undefined,
    personName: undefined,
    compareResult: undefined,
    warningStatus: undefined
  }
})

const { queryParams } = toRefs(data)
const portalExplanations = computed(() => summaryData.value.ygbExplanation || [])
const portalExplanationItems = computed(() => decoratePortalExplanationItems(portalExplanations.value, {
  portalCode: 'ygb',
  panelTitle: '530.1 办理链解释',
  panelDescription: '个税差异、来源异常和联动处置建议统一来自门户解释聚合接口。'
}))

function handlePortalExplanationAction(action) {
  openPortalExplanationAction(router, action)
}

const workbenchContext = computed(() => buildWorkbenchContext(route.query, {
  fields: taxCompareWorkbenchFields,
  sourceLabel: "财务工作台",
  title: "已按工作台上下文带入筛选条件",
  description: "当前页面保留了首页工作台带入的企业、统计月份和比对结果范围，可直接继续核对个税异常对象。",
  fieldLabels: {
    enterpriseId: "企业",
    statMonth: "统计月份",
    compareResult: "比对结果"
  },
  fieldFormatters: {
    enterpriseId: value => enterpriseName(value) || value,
    compareResult: value => compareResultLabel(value)
  }
}))

const summaryCards = computed(() => ([
  {
    key: "total",
    label: "比对记录",
    value: summaryData.value.totalCount != null ? summaryData.value.totalCount : total.value,
    unit: "条",
    note: "当前筛选条件下已进入个税比对整改台账的记录总量。",
    cardClass: ""
  },
  {
    key: "normal",
    label: "比对正常",
    value: valueOrDefault(summaryData.value.normalCount, 0),
    unit: "条",
    note: "工资实发与个税申报收入口径一致或处于安全范围内的对象，可继续月度归档。",
    cardClass: "ygb-summary-card--success"
  },
  {
    key: "abnormal",
    label: "比对异常",
    value: valueOrDefault(summaryData.value.abnormalCount, 0),
    unit: "条",
    note: "差异超过阈值的对象需要优先回查工资发放口径、申报收入和来源报文。",
    cardClass: "ygb-summary-card--warning"
  },
  {
    key: "warned",
    label: "已触发预警",
    value: valueOrDefault(summaryData.value.warnedCount, 0),
    unit: "条",
    note: "已写入预警中心的对象需要继续承接联动处置和月度整改闭环。",
    cardClass: "ygb-summary-card--primary"
  }
]))

const activeFocusKey = ref("")

const focusQueues = computed(() => ([
  {
    key: "abnormal",
    title: "差异异常对象",
    desc: "优先回查工资实发与个税申报收入口径不一致的对象。",
    count: valueOrDefault(summaryData.value.abnormalCount, 0),
    unit: "条",
    actionText: "先核对差异"
  },
  {
    key: "warned",
    title: "已预警对象",
    desc: "已进入预警中心的对象更适合继续承接联动闭环。",
    count: valueOrDefault(summaryData.value.warnedCount, 0),
    unit: "条",
    actionText: "承接预警闭环"
  },
  {
    key: "source",
    title: "来源异常对象",
    desc: "来源回写未成功或原始报文异常的对象应先排查同步结果。",
    count: valueOrDefault(summaryData.value.sourceAbnormalCount, 0),
    unit: "条",
    actionText: "先查来源状态"
  },
  {
    key: "all",
    title: "当前纳管总量",
    desc: "用于统看当前月份和企业范围内的比对台账盘子。",
    count: valueOrDefault(summaryData.value.totalCount, total.value),
    unit: "条",
    actionText: "查看全部对象"
  }
]))

const activeFocus = computed(() => focusQueues.value.find(item => item.key === activeFocusKey.value) || focusQueues.value[0])

const visibleCompareList = computed(() => prioritizeFocusRows(taxCompareList.value, row => matchCompareFocus(row, activeFocus.value?.key)))

const focusTableHint = computed(() => {
  if (!activeFocus.value) {
    return "当前按默认顺序展示个税比对台账。"
  }
  return `${activeFocus.value.title}优先置顶，便于先处理最需要回查、同步或承接预警的财务整改对象。`
})

const selectedCompareOverview = computed(() => {
  if (!currentCompare.value) {
    return [
      { label: "企业/人员", value: "-" },
      { label: "月份/结果", value: "-" },
      { label: "工资/申报", value: "-" },
      { label: "差异/来源", value: "-" }
    ]
  }
  return [
    { label: "企业/人员", value: `${currentCompare.value.enterpriseName || "-"} / ${currentCompare.value.personName || "-"}` },
    { label: "月份/结果", value: `${currentCompare.value.statMonth || "-"} / ${compareResultLabel(currentCompare.value.compareResult)}` },
    { label: "工资/申报", value: `${formatMoney(currentCompare.value.salaryAmount)} / ${formatMoney(currentCompare.value.declaredAmount)}` },
    { label: "差异/来源", value: `${formatRatio(currentCompare.value.diffRatio)} / ${currentCompare.value.sourceStatus || "-"}` }
  ]
})

const primaryCompareAction = computed(() => {
  if (!currentCompare.value) {
    return { label: "查看详情", action: "detail" }
  }
  if (["2", "3"].includes(currentCompare.value.compareResult) || currentCompare.value.warningStatus === "1" || currentCompare.value.sourceStatus !== "SUCCESS") {
    return { label: "执行比对", action: "compare" }
  }
  return { label: "查看详情", action: "detail" }
})

const currentCompareActionSummary = computed(() => {
  if (!currentCompare.value) {
    return "先从左侧焦点队列选择一类重点对象，再在台账中联动查看当前办理建议。"
  }
  if (primaryCompareAction.value.action === "compare") {
    return "当前记录更适合优先重跑同步或个税比对，先确认来源状态、差异率和预警状态，再决定是否进入后续闭环。"
  }
  return "当前记录以详情复核为主，先看工资实发、申报收入、来源报文和预警状态，再决定后续动作。"
})

const currentCompareActionTags = computed(() => buildHintTags(currentCompare.value).slice(0, 3))

const workflowSteps = computed(() => ([
  {
    label: "先锁定统计月份",
    desc: "先按统计月份和企业范围锁定个税比对台账，避免跨月工资明细和税务申报口径混查。"
  },
  {
    label: "执行税务同步",
    desc: "同步税务申报记录，统一生成本月申报收入和来源信息。"
  },
  {
    label: "发起个税比对",
    desc: "将工资实发与个税申报收入自动比对，统一形成差异率、结果和预警状态。"
  },
  {
    label: "回查异常口径",
    desc: "优先回查异常对象的工资明细、申报收入和来源报文，确认是申报偏差还是工资口径异常。"
  },
  {
    label: "承接预警与归档",
    desc: "已预警对象继续承接到预警中心闭环处置，正常对象沉淀为月度财务复核底稿。"
  }
]))

const compareHintTags = computed(() => buildHintTags(currentCompare.value))
const detailHintTags = computed(() => buildHintTags(detail.value))
const readOnlyAlertTitle = computed(() => `${readOnlyRoleLabel.value}仅保留个税比对查看、详情和导出`)
const readOnlyAlertDescription = computed(() => `${readOnlyRoleDescription.value || ''} 当前页面仍会展示比对摘要、解释和来源条件，但不开放同步和执行比对动作。`.trim())

function blockReadOnlyAction(actionLabel) {
  proxy?.$modal?.msgWarning?.(`${readOnlyRoleLabel.value}仅保留个税比对查看、详情和导出，不能${actionLabel}`)
}

function getList() {
  loading.value = true
  Promise.all([
    listTaxCompare(queryParams.value),
    getTaxCompareSummary(buildSummaryQuery())
  ]).then(([listResponse, summaryResponse]) => {
    taxCompareList.value = listResponse.rows || []
    total.value = listResponse.total || 0
    summaryData.value = summaryResponse.data || {}
    syncCurrentCompare()
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
    compareResult: queryParams.value.compareResult,
    warningStatus: queryParams.value.warningStatus
  }
}

function loadEnterpriseOptions() {
  optionselectEnterprise().then(response => {
    enterpriseOptions.value = response.data || []
  })
}

function syncCurrentCompare() {
  if (currentCompare.value) {
    const matched = visibleCompareList.value.find(item => item.compareId === currentCompare.value.compareId)
    if (matched) {
      currentCompare.value = matched
      return
    }
  }
  currentCompare.value = visibleCompareList.value.length > 0 ? visibleCompareList.value[0] : undefined
}

function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

function resetQuery() {
  proxy.resetForm("queryRef")
  Object.assign(queryParams.value, {
    pageNum: 1,
    pageSize: 10,
    statMonth: undefined,
    enterpriseId: undefined,
    personName: undefined,
    compareResult: undefined,
    warningStatus: undefined
  })
  applyWorkbenchRouteQuery(route.query, queryParams.value, taxCompareWorkbenchFields)
  getList()
}

watchEffect(() => {
  setPageGuide({
    title: '个税比对',
    description: '比对工资发放与个税申报差异，识别异常申报、漏报和追缴线索。',
    portalExplanation: portalExplanationItems.value,
    focus: focusQueues.value,
    selection: [...selectedCompareOverview.value, { label: '当前处置建议', value: currentCompareActionSummary.value }],
    workflow: workflowSteps.value,
    hints: [...currentCompareActionTags.value].slice(0, 6)
  })
})

function clearWorkbenchContext() {
  Object.assign(queryParams.value, {
    pageNum: 1,
    enterpriseId: undefined,
    statMonth: undefined,
    compareResult: undefined
  })
  router.replace({
    path: route.path,
    query: stripWorkbenchRouteQuery(route.query, taxCompareWorkbenchFields)
  })
  getList()
}

function ensureMonth() {
  if (!queryParams.value.statMonth) {
    proxy.$modal.msgWarning("请先选择统计月份")
    return false
  }
  return true
}

function handleRowClick(row) {
  currentCompare.value = row
}

function handleSelectFocus(item) {
  activeFocusKey.value = item.key
  syncCurrentCompare()
}

function openDetail(row) {
  currentCompare.value = row
  detail.value = row
  detailOpen.value = true
}

function handleSync() {
  if (isReadOnlyRole.value) {
    blockReadOnlyAction("执行税务同步")
    return
  }
  if (!ensureMonth()) {
    return
  }
  syncTaxCompare({
    statMonth: queryParams.value.statMonth,
    enterpriseId: queryParams.value.enterpriseId
  }).then(response => {
    proxy.$modal.msgSuccess(response.msg || "税务数据同步完成")
    getList()
  })
}

function handleCompare() {
  if (isReadOnlyRole.value) {
    blockReadOnlyAction("执行个税比对")
    return
  }
  if (!ensureMonth()) {
    return
  }
  executeTaxCompare({
    statMonth: queryParams.value.statMonth,
    enterpriseId: queryParams.value.enterpriseId
  }).then(response => {
    proxy.$modal.msgSuccess(response.msg || "个税比对完成")
    getList()
  })
}

function handlePrimaryCompareAction() {
  if (!currentCompare.value) {
    return
  }
  if (primaryCompareAction.value.action === "compare") {
    handleCompare()
    return
  }
  openDetail(currentCompare.value)
}

function handleExport() {
  proxy.download("ygb/tax/compare/export", { ...queryParams.value }, `tax_compare_${new Date().getTime()}.xlsx`)
}

function buildHintTags(item) {
  if (!item) {
    return [{ label: "未选中比对记录，可先在列表中选择待办理对象", type: "info" }]
  }
  const tags = []
  if (item.compareResult === "2") {
    tags.push({ label: "当前比对异常，建议优先回查工资实发与个税申报收入口径", type: "danger" })
  }
  if (item.compareResult === "3") {
    tags.push({ label: "当前比对结果待复核，建议重新执行个税比对并核对来源报文", type: "warning" })
  }
  if (item.warningStatus === "1") {
    tags.push({ label: "当前已触发预警，建议继续承接到预警中心闭环处置", type: "warning" })
  }
  if (item.compareResult === "1") {
    tags.push({ label: "当前比对正常，可继续月度复核和归档", type: "success" })
  }
  if (!item.sourceStatus || item.sourceStatus !== "SUCCESS") {
    tags.push({ label: "来源回写状态需关注，建议先核对同步结果和原始报文", type: "info" })
  }
  if (Number(item.diffRatio || 0) > 10) {
    tags.push({ label: "差异率超过 10%，建议同步核对工资明细、发放结果和申报时间窗口", type: "warning" })
  }
  if (Number(item.salaryAmount || 0) <= 0 || Number(item.declaredAmount || 0) <= 0) {
    tags.push({ label: "工资或申报金额存在零值/缺失，建议先核对明细来源和税务回写完整性", type: "warning" })
  }
  if (!tags.length) {
    tags.push({ label: "当前比对信息完整，可继续沿财务整改链路推进", type: "success" })
  }
  return tags
}

function matchCompareFocus(item, focusKey) {
  if (!focusKey || focusKey === "all") {
    return true
  }
  if (focusKey === "abnormal") {
    return ["2", "3"].includes(item.compareResult) || Number(item.diffRatio || 0) > 10
  }
  if (focusKey === "warned") {
    return item.warningStatus === "1"
  }
  if (focusKey === "source") {
    return !item.sourceStatus || item.sourceStatus !== "SUCCESS"
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

function compareResultLabel(value) {
  const matched = compareResultOptions.find(item => item.value === value)
  return matched ? matched.label : "-"
}

function valueOrDefault(value, fallback = 0) {
  return value != null ? value : fallback
}

function formatMoney(value) {
  if (value == null || value === "") {
    return "0.00"
  }
  const amount = Number(value)
  if (Number.isNaN(amount)) {
    return String(value)
  }
  return amount.toFixed(2)
}

function formatRatio(value) {
  if (value == null || value === "") {
    return "0.00%"
  }
  const amount = Number(value)
  if (Number.isNaN(amount)) {
    return `${value}%`
  }
  return `${amount.toFixed(2)}%`
}

function maskIdCard(idCard) {
  if (!idCard || idCard.length < 8) {
    return idCard
  }
  return `${idCard.slice(0, 4)}********${idCard.slice(-4)}`
}

watch(focusQueues, queues => {
  if (!queues.length) {
    activeFocusKey.value = ""
    return
  }
  if (!queues.find(item => item.key === activeFocusKey.value)) {
    activeFocusKey.value = queues[0].key
  }
}, { immediate: true })

applyWorkbenchRouteQuery(route.query, queryParams.value, taxCompareWorkbenchFields)
loadEnterpriseOptions()
getList()
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
