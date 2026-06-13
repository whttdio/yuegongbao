<template>
  <div class="app-container ygb-page">
    <section class="gov-page-header ygb-page__header">
      <div>
        <p class="ygb-page__eyebrow">监管联动线</p>
        <h1 class="ygb-page__title">社保基数比对整改台账</h1>
        <p class="ygb-page__desc">
          对比工资实发与社保基数差异，面向企业管理员、财务和监管经办优先识别差异异常和已触发预警对象，
          作为社保缴费整改、漏保识别和联动预警处置的关键中间台账。
        </p>
      </div>
      <div class="ygb-table-tip">
        执行比对前请先完成社保缴费同步和工资明细生成；当前版本按差异超过 20% 自动写入预警中心。
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
          <el-button type="primary" icon="Search" @click="handleQuery">查询</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="toolbar-card ygb-toolbar-card" shadow="never">
      <el-row :gutter="10">
        <el-col v-if="!isReadOnlyRole" :span="1.5">
          <el-button type="primary" plain icon="Histogram" @click="handleCompare" v-hasPermi="['ygb:socialBaseCompare:compare']">执行比对</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['ygb:socialBaseCompare:export']">导出</el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>
    </el-card>

    <el-card class="table-card ygb-table-card" shadow="never">
      <template #header>
        <div class="ygb-card-head ygb-card-head--between">
          <div>
            <div class="ygb-card-head__title">社保基数比对整改台账</div>
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
        <el-table-column label="工资实发" prop="salaryAmount" width="120" />
        <el-table-column label="社保基数" prop="socialBaseAmount" width="120" />
        <el-table-column label="差异率(%)" width="120">
          <template #default="scope">
            {{ formatRatio(scope.row.diffRatio) }}
          </template>
        </el-table-column>
        <el-table-column label="比对结果" width="120">
          <template #default="scope">
            <dict-tag :options="compareResultOptions" :value="scope.row.compareResult" />
          </template>
        </el-table-column>
        <el-table-column label="预警状态" width="120">
          <template #default="scope">
            <dict-tag :options="warningStatusOptions" :value="scope.row.warningStatus" />
          </template>
        </el-table-column>
        <el-table-column label="备注" prop="remark" min-width="220" show-overflow-tooltip />
        <el-table-column class-name="table-fill-column" min-width="1" />

        <el-table-column label="操作" fixed="right" align="center" width="100" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-button link type="info" icon="View" @click.stop="openDetail(scope.row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>

    <page-detail-dialog v-model="detailOpen" title="社保基数比对详情" width="760px">
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
          <el-descriptions-item label="社保基数">{{ formatMoney(detail.socialBaseAmount) }}</el-descriptions-item>
          <el-descriptions-item label="差异率">{{ formatRatio(detail.diffRatio) }}</el-descriptions-item>
          <el-descriptions-item label="预警状态">
            <dict-tag :options="warningStatusOptions" :value="detail.warningStatus" />
          </el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ detail.remark || '-' }}</el-descriptions-item>
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

<script setup name="YgbSocialBaseCompare">
import { listSocialBaseCompare, getSocialBaseCompareSummary, executeSocialBaseCompare } from "@/api/ygb/socialBaseCompare"
import { optionselectEnterprise } from "@/api/ygb/enterprise"
import { decoratePortalExplanationItems, openPortalExplanationAction } from '@/utils/portalExplanation'
import { useRoute, useRouter } from "vue-router"
import { applyWorkbenchRouteQuery, buildWorkbenchContext, stripWorkbenchRouteQuery } from "@/utils/workbenchLink"
import { useRoleViewMode } from "@/utils/roleView"

const { proxy } = getCurrentInstance()
const route = useRoute()
const { setPageGuide } = useWorkbenchAssist()
const router = useRouter()
const { isReadOnlyRole, readOnlyRoleLabel, readOnlyRoleDescription } = useRoleViewMode()
const socialBaseWorkbenchFields = ["enterpriseId", "statMonth", "compareResult"]
const workbenchClearLabel = "清空来源条件"

const compareResultOptions = [
  { label: "正常", value: "1" },
  { label: "异常", value: "2" }
]

const warningStatusOptions = [
  { label: "未预警", value: "0" },
  { label: "已预警", value: "1" }
]

const compareList = ref([])
const enterpriseOptions = ref([])
const summaryData = ref({})
const currentCompare = ref(undefined)
const detail = ref(undefined)
const loading = ref(false)
const showSearch = ref(true)
const total = ref(0)
const detailOpen = ref(false)
const activeFocusKey = ref("")

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    statMonth: currentMonth(),
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
  panelDescription: '基数差异、预警承接和漏保整改建议统一来自门户解释聚合接口。'
}))

function handlePortalExplanationAction(action) {
  openPortalExplanationAction(router, action)
}

const workbenchContext = computed(() => buildWorkbenchContext(route.query, {
  fields: socialBaseWorkbenchFields,
  sourceLabel: "财务工作台",
  title: "已按工作台上下文带入筛选条件",
  description: "当前页面保留了首页工作台带入的企业、统计月份和比对结果范围，可直接继续核对社保基数异常对象。",
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
    note: "当前筛选条件下已进入社保基数比对台账的记录总量。",
    cardClass: ""
  },
  {
    key: "normal",
    label: "比对正常",
    value: valueOrDefault(summaryData.value.normalCount, 0),
    unit: "条",
    note: "工资实发与社保基数差异在安全范围内的对象，可继续月度归档。",
    cardClass: "ygb-summary-card--success"
  },
  {
    key: "abnormal",
    label: "比对异常",
    value: valueOrDefault(summaryData.value.abnormalCount, 0),
    unit: "条",
    note: "差异超过阈值的对象需要优先回查工资口径和社保基数来源。",
    cardClass: "ygb-summary-card--warning"
  },
  {
    key: "warned",
    label: "已触发预警",
    value: valueOrDefault(summaryData.value.warnedCount, 0),
    unit: "条",
    note: "已写入预警中心的对象需要继续承接联动处置和漏保整改。",
    cardClass: "ygb-summary-card--primary"
  }
]))

const focusQueues = computed(() => {
  const rows = compareList.value || []
  const queues = [
    {
      key: "abnormal",
      title: "异常差异对象优先回查",
      desc: "优先处理比对异常对象，回查工资实发与社保基数口径差异。",
      count: rows.filter(item => item.compareResult === "2").length,
      unit: "条",
      actionText: rows.some(item => item.compareResult === "2") ? "立即回查" : "暂无异常"
    },
    {
      key: "warned",
      title: "预警对象继续承接",
      desc: "优先承接已预警对象，继续流转到预警中心和漏保整改链路。",
      count: rows.filter(item => item.warningStatus === "1").length,
      unit: "条",
      actionText: rows.some(item => item.warningStatus === "1") ? "继续闭环" : "暂无预警"
    },
    {
      key: "highDiff",
      title: "高差异率对象复核",
      desc: "优先处理差异率超过 20% 的对象，避免高风险差异继续沉淀。",
      count: rows.filter(item => Number(item.diffRatio || 0) > 20).length,
      unit: "条",
      actionText: rows.some(item => Number(item.diffRatio || 0) > 20) ? "重点复核" : "差异平稳"
    },
    {
      key: "salaryHigher",
      title: "工资高于基数对象核对",
      desc: "优先核对工资实发明显高于社保基数对象，判断是否存在低基数或漏保风险。",
      count: rows.filter(item => Number(item.salaryAmount || 0) > Number(item.socialBaseAmount || 0)).length,
      unit: "条",
      actionText: rows.some(item => Number(item.salaryAmount || 0) > Number(item.socialBaseAmount || 0)) ? "核对口径" : "暂无积压"
    }
  ]
  if (!queues.find(item => item.key === activeFocusKey.value)) {
    activeFocusKey.value = queues[0]?.key || ""
  }
  return queues
})

const activeFocus = computed(() => focusQueues.value.find(item => item.key === activeFocusKey.value) || focusQueues.value[0])

const selectedCompareOverview = computed(() => {
  if (!currentCompare.value) {
    return [
      { label: "企业/人员", value: "-" },
      { label: "月份/结果", value: "-" },
      { label: "工资/基数", value: "-" },
      { label: "差异/预警", value: "-" }
    ]
  }
  return [
    { label: "企业/人员", value: `${currentCompare.value.enterpriseName || "-"} / ${currentCompare.value.personName || "-"}` },
    { label: "月份/结果", value: `${currentCompare.value.statMonth || "-"} / ${compareResultLabel(currentCompare.value.compareResult)}` },
    { label: "工资/基数", value: `${formatMoney(currentCompare.value.salaryAmount)} / ${formatMoney(currentCompare.value.socialBaseAmount)}` },
    { label: "差异/预警", value: `${formatRatio(currentCompare.value.diffRatio)} / ${warningStatusLabel(currentCompare.value.warningStatus)}` }
  ]
})

const visibleCompareList = computed(() => prioritizeFocusRows(compareList.value, row => matchCompareFocus(row, activeFocus.value?.key)))

const focusTableHint = computed(() => {
  if (!activeFocus.value) {
    return "当前按查询条件展示比对记录。"
  }
  return `${activeFocus.value.title}，系统已将符合焦点条件的对象前置，便于财务和监管经办优先处理高风险差异记录。`
})

const workflowSteps = computed(() => ([
  {
    label: "先锁定统计月份",
    desc: "先按统计月份和企业范围锁定比对台账，避免跨月工资与社保数据混合判断。"
  },
  {
    label: "执行基数比对",
    desc: "将工资实发与社保基数进行自动比对，统一生成差异率、结果和预警状态。"
  },
  {
    label: "回查工资与缴费口径",
    desc: "优先回查异常对象的工资实发、社保基数和缴费同步来源，确认差异是否合理。"
  },
  {
    label: "承接预警与漏保整改",
    desc: "已触发预警对象继续承接到预警中心和漏保整改链路，避免问题停留在比对页。"
  },
  {
    label: "完成月度复核归档",
    desc: "正常对象和已整改对象沉淀为月度复核底稿，供财务和监管直接查询。"
  }
]))

const primaryCompareAction = computed(() => {
  if (isReadOnlyRole.value) {
    return { label: "鏌ョ湅褰掓。璇︽儏" }
  }
  if (!currentCompare.value) {
    return { label: "选择待办对象" }
  }
  if (currentCompare.value.compareResult === "2") {
    return { label: "回查异常口径" }
  }
  if (currentCompare.value.warningStatus === "1") {
    return { label: "承接预警闭环" }
  }
  if (Number(currentCompare.value.diffRatio || 0) > 20) {
    return { label: "复核高差异率" }
  }
  if (Number(currentCompare.value.salaryAmount || 0) > Number(currentCompare.value.socialBaseAmount || 0)) {
    return { label: "核对低基数风险" }
  }
  return { label: "查看归档详情" }
})

const readOnlyAlertTitle = computed(() => `${readOnlyRoleLabel.value}仅保留社保基数比对查看、详情和导出`)
const readOnlyAlertDescription = computed(() => readOnlyRoleDescription.value || `${readOnlyRoleLabel.value}不能执行社保基数比对等办理动作。`)

const currentCompareActionSummary = computed(() => {
  if (!currentCompare.value) {
    return "先从待办焦点中选择一条比对记录，再决定是回查异常、承接预警、复核高差异还是核对低基数风险。"
  }
  if (currentCompare.value.compareResult === "2") {
    return "该对象当前比对异常，建议优先回查工资实发、社保基数和缴费同步口径，确认差异来源。"
  }
  if (currentCompare.value.warningStatus === "1") {
    return "该对象已触发预警，建议继续承接到预警中心和漏保整改链路，避免风险停留在比对层。"
  }
  if (Number(currentCompare.value.diffRatio || 0) > 20) {
    return "该对象差异率已超过 20%，建议重点复核工资口径、社保基数来源和企业申报说明。"
  }
  if (Number(currentCompare.value.salaryAmount || 0) > Number(currentCompare.value.socialBaseAmount || 0)) {
    return "该对象工资实发高于社保基数，建议进一步核对是否存在低基数申报或漏保风险。"
  }
  return "该对象当前比对结果相对稳定，可继续作为月度复核和归档底稿使用。"
})

const currentCompareActionTags = computed(() => {
  if (!currentCompare.value) {
    return [{ label: "待选择具体比对对象", type: "info" }]
  }
  return buildHintTags(currentCompare.value)
})

const compareHintTags = computed(() => buildHintTags(currentCompare.value))
const detailHintTags = computed(() => buildHintTags(detail.value))

function getList() {
  loading.value = true
  Promise.all([
    listSocialBaseCompare(queryParams.value),
    getSocialBaseCompareSummary(buildSummaryQuery())
  ]).then(([listResponse, summaryResponse]) => {
    compareList.value = listResponse.rows || []
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
    statMonth: currentMonth(),
    enterpriseId: undefined,
    personName: undefined,
    compareResult: undefined,
    warningStatus: undefined
  })
  applyWorkbenchRouteQuery(route.query, queryParams.value, socialBaseWorkbenchFields)
  getList()

watchEffect(() => {
  setPageGuide({
    title: '????????' || '????????',
    description: '?????????????????????????????????' || '?????????????????????????????????',
    portalExplanation: portalExplanationItems.value,
    focus: focusQueues.value,
    selection: [...selectedCompareOverview.value, { label: '??????', value: currentCompareActionSummary.value }],
    workflow: workflowSteps.value,
    hints: [...currentCompareActionTags.value].slice(0, 6)
  })
})

}

function clearWorkbenchContext() {
  Object.assign(queryParams.value, {
    pageNum: 1,
    enterpriseId: undefined,
    statMonth: currentMonth(),
    compareResult: undefined
  })
  router.replace({
    path: route.path,
    query: stripWorkbenchRouteQuery(route.query, socialBaseWorkbenchFields)
  })
  getList()
}

function handleRowClick(row) {
  currentCompare.value = row
}

function handleSelectFocus(item) {
  activeFocusKey.value = item.key
  syncCurrentCompare()
}

function openDetail(row) {
  if (!row) {
    return
  }
  currentCompare.value = row
  detail.value = row
  detailOpen.value = true
}

function handleCompare() {
  if (isReadOnlyRole.value) {
    blockReadOnlyAction("执行社保基数比对")
    return
  }
  if (!queryParams.value.statMonth) {
    proxy.$modal.msgWarning("请先选择统计月份")
    return
  }
  executeSocialBaseCompare({
    statMonth: queryParams.value.statMonth,
    enterpriseId: queryParams.value.enterpriseId
  }).then(response => {
    proxy.$modal.msgSuccess(response.msg || "执行比对完成")
    getList()
  })
}

function handleExport() {
  proxy.download("ygb/social/compare/export", { ...queryParams.value }, `social_compare_${new Date().getTime()}.xlsx`)
}

function handlePrimaryCompareAction() {
  if (!currentCompare.value) {
    return
  }
  openDetail(currentCompare.value)
}

function blockReadOnlyAction(actionLabel) {
  proxy?.$modal?.msgWarning?.(`${readOnlyRoleLabel.value}仅保留社保基数比对查看、详情和导出，不能${actionLabel}`)
}

function buildHintTags(item) {
  if (!item) {
    return [{ label: "未选中比对记录，可先在列表中选择待办理对象", type: "info" }]
  }
  const tags = []
  if (item.compareResult === "2") {
    tags.push({ label: "当前比对异常，建议优先回查工资实发与社保基数口径", type: "danger" })
  }
  if (item.warningStatus === "1") {
    tags.push({ label: "当前已触发预警，建议继续承接到预警中心闭环处置", type: "warning" })
  }
  if (item.compareResult === "1") {
    tags.push({ label: "当前比对正常，可继续月度复核和归档", type: "success" })
  }
  if (Number(item.diffRatio || 0) > 20) {
    tags.push({ label: "差异率超过 20%，建议同步核对缴费同步和工资明细来源", type: "warning" })
  }
  if (Number(item.salaryAmount || 0) > Number(item.socialBaseAmount || 0)) {
    tags.push({ label: "工资实发高于社保基数，建议核对是否存在低基数或漏保风险", type: "warning" })
  }
  if (!tags.length) {
    tags.push({ label: "当前比对结果完整，可继续沿财务整改链路推进", type: "success" })
  }
  return tags
}

function enterpriseName(enterpriseId) {
  const matched = enterpriseOptions.value.find(item => item.enterpriseId === enterpriseId)
  return matched ? matched.enterpriseName : ""
}

function compareResultLabel(value) {
  const matched = compareResultOptions.find(item => item.value === value)
  return matched ? matched.label : "-"
}

function warningStatusLabel(value) {
  const matched = warningStatusOptions.find(item => item.value === value)
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

function currentMonth() {
  const date = new Date()
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, "0")}`
}

function matchCompareFocus(row, key) {
  if (!key) {
    return true
  }
  if (key === "abnormal") {
    return row.compareResult === "2"
  }
  if (key === "warned") {
    return row.warningStatus === "1"
  }
  if (key === "highDiff") {
    return Number(row.diffRatio || 0) > 20
  }
  if (key === "salaryHigher") {
    return Number(row.salaryAmount || 0) > Number(row.socialBaseAmount || 0)
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

applyWorkbenchRouteQuery(route.query, queryParams.value, socialBaseWorkbenchFields)
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
