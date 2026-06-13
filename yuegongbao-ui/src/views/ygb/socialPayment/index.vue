<template>
  <div class="app-container ygb-page">
    <section class="gov-page-header ygb-page__header">
      <div>
        <p class="ygb-page__eyebrow">监管联动线</p>
        <h1 class="ygb-page__title">社保缴费整改台账</h1>
        <p class="ygb-page__desc">
          统一查看社保 Stub 同步回写的缴费结果，面向企业管理员、财务和监管经办优先识别未缴费、欠费和来源回写异常对象，
          作为后续社保基数比对、漏保识别和月度整改归档的基础台账。
        </p>
      </div>
      <div class="ygb-table-tip">
        当前版本支持按月份执行模拟同步，后续替换正式接口时不改变页面办理链路；异常对象会继续流向基数比对、漏保和预警中心。
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
        <el-form-item label="状态">
          <el-select v-model="queryParams.paymentStatus" clearable style="width: 140px">
            <el-option v-for="item in paymentStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
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
          <el-button type="primary" plain icon="RefreshRight" @click="handleSync" v-hasPermi="['ygb:socialPayment:sync']">模拟同步</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['ygb:socialPayment:export']">导出</el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>
    </el-card>

    <el-card class="table-card ygb-table-card" shadow="never">
      <template #header>
        <div class="ygb-card-head ygb-card-head--between">
          <div>
            <div class="ygb-card-head__title">社保缴费整改台账</div>
            <div class="ygb-card-head__desc">{{ focusTableHint }}</div>
          </div>
          <div class="ygb-card-head__desc">当前总量 {{ total }} 条</div>
        </div>
      </template>
      <el-table v-loading="loading" :data="visiblePaymentList" @row-click="handleRowClick">
        <el-table-column label="ID" prop="paymentId" width="90" />
        <el-table-column label="统计月份" prop="statMonth" width="110" />
        <el-table-column label="企业" prop="enterpriseName" min-width="220" />
        <el-table-column label="人员" prop="personName" width="120" />
        <el-table-column label="证件号" prop="idCard" min-width="180" />
        <el-table-column label="社保基数" prop="baseAmount" width="120" />
        <el-table-column label="缴费金额" prop="paidAmount" width="120" />
        <el-table-column label="来源状态" prop="sourceStatus" width="120" />
        <el-table-column label="缴费状态" width="120">
          <template #default="scope">
            <dict-tag :options="paymentStatusOptions" :value="scope.row.paymentStatus" />
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

    <page-detail-dialog v-model="detailOpen" title="社保缴费详情" width="760px">
      <template v-if="detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="统计月份">{{ detail.statMonth || '-' }}</el-descriptions-item>
          <el-descriptions-item label="缴费状态">
            <dict-tag :options="paymentStatusOptions" :value="detail.paymentStatus" />
          </el-descriptions-item>
          <el-descriptions-item label="企业">{{ detail.enterpriseName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="人员">{{ detail.personName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="身份证号">{{ maskIdCard(detail.idCard) || '-' }}</el-descriptions-item>
          <el-descriptions-item label="区域编码">{{ detail.regionCode || '-' }}</el-descriptions-item>
          <el-descriptions-item label="社保基数">{{ formatMoney(detail.baseAmount) }}</el-descriptions-item>
          <el-descriptions-item label="缴费金额">{{ formatMoney(detail.paidAmount) }}</el-descriptions-item>
          <el-descriptions-item label="来源状态">{{ detail.sourceStatus || '-' }}</el-descriptions-item>
          <el-descriptions-item label="回写时间">{{ parseTime(detail.callbackTime, '{y}-{m}-{d} {h}:{i}:{s}') || '-' }}</el-descriptions-item>
          <el-descriptions-item label="来源流水号" :span="2">{{ detail.sourceSerialNo || '-' }}</el-descriptions-item>
          <el-descriptions-item label="来源消息" :span="2">{{ detail.sourceMessage || '-' }}</el-descriptions-item>
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

<script setup name="YgbSocialPayment">
import { listSocialPayment, getSocialPaymentSummary, getSocialPayment, syncSocialPayment } from "@/api/ygb/socialPayment"
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
const socialPaymentWorkbenchFields = ["enterpriseId", "statMonth", "paymentStatus"]
const workbenchClearLabel = "清空来源条件"

const paymentStatusOptions = [
  { label: "未缴费", value: "0" },
  { label: "正常", value: "1" },
  { label: "欠费", value: "2" }
]

const socialPaymentList = ref([])
const enterpriseOptions = ref([])
const summaryData = ref({})
const currentPayment = ref(undefined)
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
    paymentStatus: undefined
  }
})

const { queryParams } = toRefs(data)
const portalExplanations = computed(() => summaryData.value.ygbExplanation || [])
const portalExplanationItems = computed(() => decoratePortalExplanationItems(portalExplanations.value, {
  portalCode: 'ygb',
  panelTitle: '530.1 办理链解释',
  panelDescription: '缴费风险、补缴情况和下游整改建议统一来自门户解释聚合接口。'
}))

function handlePortalExplanationAction(action) {
  openPortalExplanationAction(router, action)
}

const workbenchContext = computed(() => buildWorkbenchContext(route.query, {
  fields: socialPaymentWorkbenchFields,
  sourceLabel: "财务工作台",
  title: "已按工作台上下文带入筛选条件",
  description: "当前页面保留了首页工作台带入的企业、统计月份和缴费状态范围，可直接继续核对社保缴费结果。",
  fieldLabels: {
    enterpriseId: "企业",
    statMonth: "统计月份",
    paymentStatus: "缴费状态"
  },
  fieldFormatters: {
    enterpriseId: value => enterpriseName(value) || value,
    paymentStatus: value => paymentStatusLabel(value)
  }
}))

const summaryCards = computed(() => ([
  {
    key: "total",
    label: "缴费记录",
    value: summaryData.value.totalCount != null ? summaryData.value.totalCount : total.value,
    unit: "条",
    note: "当前筛选条件下已进入社保缴费整改台账的记录总量。",
    cardClass: ""
  },
  {
    key: "normal",
    label: "正常缴费",
    value: valueOrDefault(summaryData.value.normalCount, 0),
    unit: "条",
    note: "已完成正常缴费的对象，可直接进入后续基数比对和月度归档。",
    cardClass: "ygb-summary-card--success"
  },
  {
    key: "unpaid",
    label: "未缴费对象",
    value: valueOrDefault(summaryData.value.unpaidCount, 0),
    unit: "条",
    note: "仍未完成缴费的对象，需要优先核对同步结果和补缴安排。",
    cardClass: "ygb-summary-card--warning"
  },
  {
    key: "overdue",
    label: "欠费风险",
    value: valueOrDefault(summaryData.value.overdueCount, 0),
    unit: "条",
    note: "已识别为欠费的对象，需要继续承接至漏保和联动整改链路。",
    cardClass: "ygb-summary-card--primary"
  }
]))

const focusQueues = computed(() => {
  const rows = socialPaymentList.value || []
  const queues = [
    {
      key: "unpaid",
      title: "未缴费对象优先补核",
      desc: "优先处理未缴费对象，先核对同步范围和补缴安排，避免继续向下游扩散异常。",
      count: rows.filter(item => item.paymentStatus === "0").length,
      unit: "条",
      actionText: rows.some(item => item.paymentStatus === "0") ? "立即补核" : "暂无积压"
    },
    {
      key: "overdue",
      title: "欠费对象承接整改",
      desc: "优先承接欠费对象，继续流转到漏保识别和联动整改链路。",
      count: rows.filter(item => item.paymentStatus === "2").length,
      unit: "条",
      actionText: rows.some(item => item.paymentStatus === "2") ? "继续整改" : "风险可控"
    },
    {
      key: "sourceAbnormal",
      title: "来源回写异常回查",
      desc: "优先处理来源状态异常或缺失对象，避免财务依据错误回写继续处置。",
      count: rows.filter(item => !item.sourceStatus || item.sourceStatus !== "SUCCESS").length,
      unit: "条",
      actionText: rows.some(item => !item.sourceStatus || item.sourceStatus !== "SUCCESS") ? "回查来源" : "回写稳定"
    },
    {
      key: "zeroPaid",
      title: "金额异常对象核对",
      desc: "优先核对缴费金额为 0 或缺失对象，确认是否为补缴未回写或口径异常。",
      count: rows.filter(item => Number(item.paidAmount || 0) <= 0).length,
      unit: "条",
      actionText: rows.some(item => Number(item.paidAmount || 0) <= 0) ? "核对金额" : "金额正常"
    }
  ]
  if (!queues.find(item => item.key === activeFocusKey.value)) {
    activeFocusKey.value = queues[0]?.key || ""
  }
  return queues
})

const activeFocus = computed(() => focusQueues.value.find(item => item.key === activeFocusKey.value) || focusQueues.value[0])

const selectedPaymentOverview = computed(() => {
  if (!currentPayment.value) {
    return [
      { label: "企业/人员", value: "-" },
      { label: "月份/状态", value: "-" },
      { label: "基数/缴费", value: "-" },
      { label: "回写状态", value: "-" }
    ]
  }
  return [
    { label: "企业/人员", value: `${currentPayment.value.enterpriseName || "-"} / ${currentPayment.value.personName || "-"}` },
    { label: "月份/状态", value: `${currentPayment.value.statMonth || "-"} / ${paymentStatusLabel(currentPayment.value.paymentStatus)}` },
    { label: "基数/缴费", value: `${formatMoney(currentPayment.value.baseAmount)} / ${formatMoney(currentPayment.value.paidAmount)}` },
    { label: "回写状态", value: currentPayment.value.sourceStatus || "-" }
  ]
})

const visiblePaymentList = computed(() => prioritizeFocusRows(socialPaymentList.value, row => matchPaymentFocus(row, activeFocus.value?.key)))

const focusTableHint = computed(() => {
  if (!activeFocus.value) {
    return "当前按查询条件展示缴费记录。"
  }
  return `${activeFocus.value.title}，系统已将符合焦点条件的对象前置，便于财务与监管经办优先处理高风险缴费记录。`
})

const workflowSteps = computed(() => ([
  {
    label: "先锁定统计月份",
    desc: "先按统计月份和企业范围锁定缴费台账，避免财务在不同月份记录中混查。"
  },
  {
    label: "执行社保同步",
    desc: "通过模拟同步拉取社保缴费结果，统一生成当月缴费底稿和来源回写信息。"
  },
  {
    label: "核对缴费状态与金额",
    desc: "优先核对未缴费、欠费对象的基数和缴费金额，避免异常对象进入后续整改链路后仍无底稿。"
  },
  {
    label: "承接基数与漏保整改",
    desc: "存在风险的对象继续承接到社保基数比对、漏保清单和预警联动模块。"
  },
  {
    label: "完成月度归档复核",
    desc: "正常缴费对象沉淀为月度归档底稿，供后续财务复核和监管查询直接引用。"
  }
]))

const primaryPaymentAction = computed(() => {
  if (!currentPayment.value) {
    return { label: "选择待办对象" }
  }
  if (currentPayment.value.paymentStatus === "0") {
    return { label: "核对未缴费说明" }
  }
  if (currentPayment.value.paymentStatus === "2") {
    return { label: "承接欠费整改" }
  }
  if (!currentPayment.value.sourceStatus || currentPayment.value.sourceStatus !== "SUCCESS") {
    return { label: "回查同步来源" }
  }
  if (Number(currentPayment.value.paidAmount || 0) <= 0) {
    return { label: "核对缴费金额" }
  }
  return { label: "查看归档详情" }
})

const currentPaymentActionSummary = computed(() => {
  if (!currentPayment.value) {
    return "先从待办焦点中选择一条缴费记录，再决定是核对未缴费、承接欠费整改、回查来源还是修正缴费金额。"
  }
  if (currentPayment.value.paymentStatus === "0") {
    return "该对象当前未缴费，应先确认同步范围、补缴安排和企业说明，再决定是否继续流转到后续整改链路。"
  }
  if (currentPayment.value.paymentStatus === "2") {
    return "该对象已识别为欠费，建议直接承接到漏保和联动整改链路，避免长期停留在缴费结果层。"
  }
  if (!currentPayment.value.sourceStatus || currentPayment.value.sourceStatus !== "SUCCESS") {
    return "该对象来源回写状态异常，建议先回查同步日志与原始报文，避免基于错误结果继续处理。"
  }
  if (Number(currentPayment.value.paidAmount || 0) <= 0) {
    return "该对象缴费金额为 0 或缺失，建议优先核对基数、补缴情形与来源回写口径。"
  }
  return "该对象当前缴费信息相对完整，可继续作为基数比对和月度整改归档的基础数据。"
})

const currentPaymentActionTags = computed(() => {
  if (!currentPayment.value) {
    return [{ label: "待选择具体缴费对象", type: "info" }]
  }
  return buildHintTags(currentPayment.value)
})

const paymentHintTags = computed(() => buildHintTags(currentPayment.value))
const detailHintTags = computed(() => buildHintTags(detail.value))
const readOnlyAlertTitle = computed(() => `${readOnlyRoleLabel.value}仅保留社保缺费查看、详情和导出`)
const readOnlyAlertDescription = computed(() => `${readOnlyRoleDescription.value || ''} 当前页面仍会展示缺费摘要、解释和来源条件，但不开放模拟同步动作。`.trim())

function blockReadOnlyAction(actionLabel) {
  proxy?.$modal?.msgWarning?.(`${readOnlyRoleLabel.value}仅保留社保缺费查看、详情和导出，不能${actionLabel}`)
}

function getList() {
  loading.value = true
  Promise.all([
    listSocialPayment(queryParams.value),
    getSocialPaymentSummary(buildSummaryQuery())
  ]).then(([listResponse, summaryResponse]) => {
    socialPaymentList.value = listResponse.rows || []
    total.value = listResponse.total || 0
    summaryData.value = summaryResponse.data || {}
    syncCurrentPayment()
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
    paymentStatus: queryParams.value.paymentStatus
  }
}

function loadEnterpriseOptions() {
  optionselectEnterprise().then(response => {
    enterpriseOptions.value = response.data || []
  })
}

function syncCurrentPayment() {
  if (currentPayment.value) {
    const matched = visiblePaymentList.value.find(item => item.paymentId === currentPayment.value.paymentId)
    if (matched) {
      currentPayment.value = matched
      return
    }
  }
  currentPayment.value = visiblePaymentList.value.length > 0 ? visiblePaymentList.value[0] : undefined
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
    paymentStatus: undefined
  })
  applyWorkbenchRouteQuery(route.query, queryParams.value, socialPaymentWorkbenchFields)
  getList()

watchEffect(() => {
  setPageGuide({
    title: '????????' || '????????',
    description: '?????????????????????????????????' || '?????????????????????????????????',
    portalExplanation: portalExplanationItems.value,
    focus: focusQueues.value,
    selection: [...selectedPaymentOverview.value, { label: '??????', value: currentPaymentActionSummary.value }],
    workflow: workflowSteps.value,
    hints: [...currentPaymentActionTags.value].slice(0, 6)
  })
})

}

function clearWorkbenchContext() {
  Object.assign(queryParams.value, {
    pageNum: 1,
    enterpriseId: undefined,
    statMonth: currentMonth(),
    paymentStatus: undefined
  })
  router.replace({
    path: route.path,
    query: stripWorkbenchRouteQuery(route.query, socialPaymentWorkbenchFields)
  })
  getList()
}

function handleRowClick(row) {
  currentPayment.value = row
}

function handleSelectFocus(item) {
  activeFocusKey.value = item.key
  syncCurrentPayment()
}

function openDetail(row) {
  if (!row) {
    return
  }
  currentPayment.value = row
  getSocialPayment(row.paymentId).then(response => {
    detail.value = response.data
    detailOpen.value = true
  })
}

function handleSync() {
  if (isReadOnlyRole.value) {
    blockReadOnlyAction("执行社保同步")
    return
  }
  if (!queryParams.value.statMonth) {
    proxy.$modal.msgWarning("请先选择统计月份")
    return
  }
  syncSocialPayment({
    statMonth: queryParams.value.statMonth,
    enterpriseId: queryParams.value.enterpriseId
  }).then(response => {
    proxy.$modal.msgSuccess(response.msg || "模拟同步完成")
    getList()
  })
}

function handleExport() {
  proxy.download("ygb/social/payment/export", { ...queryParams.value }, `social_payment_${new Date().getTime()}.xlsx`)
}

function handlePrimaryPaymentAction() {
  if (!currentPayment.value) {
    return
  }
  openDetail(currentPayment.value)
}

function buildHintTags(item) {
  if (!item) {
    return [{ label: "未选中缴费记录，可先在列表中选择待办理对象", type: "info" }]
  }
  const tags = []
  if (item.paymentStatus === "0") {
    tags.push({ label: "当前未缴费，建议先核对同步范围并补充缴费安排", type: "warning" })
  }
  if (item.paymentStatus === "2") {
    tags.push({ label: "当前已识别欠费，建议优先承接到漏保和监管联动整改", type: "danger" })
  }
  if (item.paymentStatus === "1") {
    tags.push({ label: "当前缴费正常，可继续承接基数比对和月度归档", type: "success" })
  }
  if (!item.sourceStatus || item.sourceStatus !== "SUCCESS") {
    tags.push({ label: "来源回写状态需关注，建议先核对来源消息和同步结果", type: "info" })
  }
  if (Number(item.paidAmount || 0) <= 0) {
    tags.push({ label: "缴费金额为 0 或缺失，建议先核对补缴口径与来源报文", type: "warning" })
  }
  if (!tags.length) {
    tags.push({ label: "当前缴费信息完整，可继续沿财务整改链路推进", type: "success" })
  }
  return tags
}

function enterpriseName(enterpriseId) {
  const matched = enterpriseOptions.value.find(item => item.enterpriseId === enterpriseId)
  return matched ? matched.enterpriseName : ""
}

function paymentStatusLabel(value) {
  const matched = paymentStatusOptions.find(item => item.value === value)
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

function matchPaymentFocus(row, key) {
  if (!key) {
    return true
  }
  if (key === "unpaid") {
    return row.paymentStatus === "0"
  }
  if (key === "overdue") {
    return row.paymentStatus === "2"
  }
  if (key === "sourceAbnormal") {
    return !row.sourceStatus || row.sourceStatus !== "SUCCESS"
  }
  if (key === "zeroPaid") {
    return Number(row.paidAmount || 0) <= 0
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

applyWorkbenchRouteQuery(route.query, queryParams.value, socialPaymentWorkbenchFields)
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
