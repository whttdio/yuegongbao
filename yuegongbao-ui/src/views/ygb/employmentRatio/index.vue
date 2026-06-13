<template>
  <div class="app-container ygb-page">
    <section class="gov-page-header ygb-page__header">
      <div>
        <p class="ygb-page__eyebrow">用工比例线</p>
        <h1 class="ygb-page__title">用工比例合规整改台账</h1>
        <p class="ygb-page__desc">
          基于合同备案和在岗人员结构计算派遣比例，面向企业管理员、财务和监管经办优先识别超比例用工对象，
          作为用工比例整改、预警联动和月度合规复核的重要办理台账。
        </p>
      </div>
      <div class="ygb-table-tip">
        当前版本按统计月份重算用工比例，大于 10% 触发黄警，大于 15% 触发红警，并自动写入预警中心。
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
        <el-form-item label="用工单位">
          <el-select v-model="queryParams.employerEnterpriseId" clearable filterable style="width: 220px">
            <el-option v-for="item in enterpriseOptions" :key="item.enterpriseId" :label="item.enterpriseName" :value="item.enterpriseId" />
          </el-select>
        </el-form-item>
        <el-form-item label="预警级别">
          <el-select v-model="queryParams.warningLevel" clearable style="width: 140px">
            <el-option v-for="item in warningLevelOptions" :key="item.value" :label="item.label" :value="item.value" />
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
          <el-button type="primary" plain icon="Operation" @click="handleCalculate" v-hasPermi="['ygb:employmentRatio:calculate']">执行计算</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['ygb:employmentRatio:export']">导出</el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>
    </el-card>

    <el-card class="table-card ygb-table-card" shadow="never">
      <template #header>
        <div class="ygb-card-head ygb-card-head--between">
          <div>
            <div class="ygb-card-head__title">用工比例合规台账</div>
            <div class="ygb-card-head__desc">{{ focusTableHint }}</div>
          </div>
          <div class="ygb-card-head__desc">当前总量 {{ total }} 条</div>
        </div>
      </template>
      <el-table v-loading="loading" :data="visibleRatioList" @row-click="handleRowClick">
        <el-table-column label="ID" prop="recordId" width="90" />
        <el-table-column label="统计月份" prop="statMonth" width="110" />
        <el-table-column label="用工单位" prop="employerEnterpriseName" min-width="240" />
        <el-table-column label="派遣人数" prop="dispatchCount" width="120" />
        <el-table-column label="正式工人数" prop="formalCount" width="120" />
        <el-table-column label="派遣比例(%)" prop="ratioValue" width="130">
          <template #default="scope">
            {{ formatRatio(scope.row.ratioValue) }}
          </template>
        </el-table-column>
        <el-table-column label="预警级别" prop="warningLevel" width="120">
          <template #default="scope">
            <dict-tag :options="warningLevelOptions" :value="scope.row.warningLevel" />
          </template>
        </el-table-column>
        <el-table-column label="预警状态" prop="warningStatus" width="120">
          <template #default="scope">
            <dict-tag :options="warningStatusOptions" :value="scope.row.warningStatus" />
          </template>
        </el-table-column>
        <el-table-column label="区域编码" prop="regionCode" width="140" />
        <el-table-column label="创建时间" prop="createTime" width="180" />
        <el-table-column class-name="table-fill-column" min-width="1" />

        <el-table-column label="操作" fixed="right" align="center" width="100" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-button link type="info" icon="View" @click.stop="openDetail(scope.row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>

    <page-detail-dialog v-model="detailOpen" title="用工比例详情" width="760px">
      <template v-if="detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="统计月份">{{ detail.statMonth || '-' }}</el-descriptions-item>
          <el-descriptions-item label="预警级别">
            <dict-tag :options="warningLevelOptions" :value="detail.warningLevel" />
          </el-descriptions-item>
          <el-descriptions-item label="用工单位">{{ detail.employerEnterpriseName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="区域编码">{{ detail.regionCode || '-' }}</el-descriptions-item>
          <el-descriptions-item label="派遣人数">{{ detail.dispatchCount ?? 0 }}</el-descriptions-item>
          <el-descriptions-item label="正式工人数">{{ detail.formalCount ?? 0 }}</el-descriptions-item>
          <el-descriptions-item label="派遣比例">{{ formatRatio(detail.ratioValue) }}</el-descriptions-item>
          <el-descriptions-item label="预警状态">
            <dict-tag :options="warningStatusOptions" :value="detail.warningStatus" />
          </el-descriptions-item>
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

<script setup name="YgbEmploymentRatio">
import { computed, getCurrentInstance, reactive, ref, toRefs, watch, watchEffect } from 'vue'
import { listEmploymentRatio, getEmploymentRatioSummary, calculateEmploymentRatio } from "@/api/ygb/employmentRatio"
import { optionselectEnterprise } from "@/api/ygb/enterprise"
import { useRouter } from 'vue-router'
import { useWorkbenchAssist } from '@/composables/useWorkbenchAssist'
import { decoratePortalExplanationItems, openPortalExplanationAction } from '@/utils/portalExplanation'
import { useRoleViewMode } from '@/utils/roleView'

const { proxy } = getCurrentInstance()
const router = useRouter()
const { setPageGuide } = useWorkbenchAssist()
const { isReadOnlyRole, readOnlyRoleLabel, readOnlyRoleDescription } = useRoleViewMode()

const warningLevelOptions = [
  { label: "正常", value: "0" },
  { label: "黄警", value: "1" },
  { label: "红警", value: "2" }
]

const warningStatusOptions = [
  { label: "未预警", value: "0" },
  { label: "已预警", value: "1" }
]

const employmentRatioList = ref([])
const enterpriseOptions = ref([])
const summaryData = ref({})
const currentRatio = ref(undefined)
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
    employerEnterpriseId: undefined,
    warningLevel: undefined,
    warningStatus: undefined
  }
})

const { queryParams } = toRefs(data)
const portalExplanations = computed(() => summaryData.value.ygbExplanation || [])
const portalExplanationItems = computed(() => decoratePortalExplanationItems(portalExplanations.value, {
  portalCode: 'ygb',
  panelTitle: '530.1 办理链解释',
  panelDescription: '比例预警、整改建议和闭环提示统一来自门户解释聚合接口。'
}))

function handlePortalExplanationAction(action) {
  openPortalExplanationAction(router, action)
}

const summaryCards = computed(() => ([
  {
    key: "total",
    label: "比例记录",
    value: summaryData.value.totalCount != null ? summaryData.value.totalCount : total.value,
    unit: "条",
    note: "当前筛选条件下已进入用工比例合规台账的记录总量。",
    cardClass: ""
  },
  {
    key: "normal",
    label: "比例正常",
    value: valueOrDefault(summaryData.value.normalCount, 0),
    unit: "条",
    note: "派遣比例未超过预警阈值的对象，可继续月度合规归档。",
    cardClass: "ygb-summary-card--success"
  },
  {
    key: "yellow",
    label: "黄警对象",
    value: valueOrDefault(summaryData.value.yellowCount, 0),
    unit: "条",
    note: "派遣比例超过 10% 的对象需要优先核对用工结构和派遣规模。",
    cardClass: "ygb-summary-card--warning"
  },
  {
    key: "red",
    label: "红警对象",
    value: valueOrDefault(summaryData.value.redCount, 0),
    unit: "条",
    note: "派遣比例超过 15% 的对象需要直接承接比例整改和预警闭环。",
    cardClass: "ygb-summary-card--primary"
  }
]))

const activeFocusKey = ref("")

const focusQueues = computed(() => ([
  {
    key: "red",
    title: "红警整改对象",
    desc: "派遣比例超过 15% 的对象应直接进入比例整改和预警闭环。",
    count: valueOrDefault(summaryData.value.redCount, 0),
    unit: "条",
    actionText: "优先承接红警"
  },
  {
    key: "yellow",
    title: "黄警整改对象",
    desc: "派遣比例超过 10% 的对象应先核对合同结构和人员类型。",
    count: valueOrDefault(summaryData.value.yellowCount, 0),
    unit: "条",
    actionText: "先核对结构"
  },
  {
    key: "warned",
    title: "已预警对象",
    desc: "已进入预警中心的对象应继续承接后续办理闭环。",
    count: valueOrDefault(summaryData.value.warnedCount, 0),
    unit: "条",
    actionText: "承接预警闭环"
  },
  {
    key: "all",
    title: "当前纳管总量",
    desc: "用于统看当前月份和单位范围内的用工比例办理盘子。",
    count: valueOrDefault(summaryData.value.totalCount, total.value),
    unit: "条",
    actionText: "查看全部对象"
  }
]))

const activeFocus = computed(() => focusQueues.value.find(item => item.key === activeFocusKey.value) || focusQueues.value[0])

const visibleRatioList = computed(() => prioritizeFocusRows(employmentRatioList.value, row => matchRatioFocus(row, activeFocus.value?.key)))

const focusTableHint = computed(() => {
  if (!activeFocus.value) {
    return "当前按默认顺序展示用工比例台账。"
  }
  return `${activeFocus.value.title}优先置顶，便于先处理最需要比例整改、结构核查或预警承接的对象。`
})

const selectedRatioOverview = computed(() => {
  if (!currentRatio.value) {
    return [
      { label: "用工单位", value: "-" },
      { label: "月份/级别", value: "-" },
      { label: "派遣/正式工", value: "-" },
      { label: "比例/预警", value: "-" }
    ]
  }
  return [
    { label: "用工单位", value: currentRatio.value.employerEnterpriseName || "-" },
    { label: "月份/级别", value: `${currentRatio.value.statMonth || "-"} / ${warningLevelLabel(currentRatio.value.warningLevel)}` },
    { label: "派遣/正式工", value: `${currentRatio.value.dispatchCount ?? 0} / ${currentRatio.value.formalCount ?? 0}` },
    { label: "比例/预警", value: `${formatRatio(currentRatio.value.ratioValue)} / ${warningStatusLabel(currentRatio.value.warningStatus)}` }
  ]
})

const primaryRatioAction = computed(() => {
  if (isReadOnlyRole.value) {
    return { label: "鏌ョ湅璇︽儏", action: "detail" }
  }
  if (!currentRatio.value) {
    return { label: "查看详情", action: "detail" }
  }
  if (currentRatio.value.warningLevel === "2" || currentRatio.value.warningLevel === "1") {
    return { label: "执行计算", action: "calculate" }
  }
  return { label: "查看详情", action: "detail" }
})

const readOnlyAlertTitle = computed(() => `${readOnlyRoleLabel.value}仅保留用工比例查看、详情和导出`)
const readOnlyAlertDescription = computed(() => readOnlyRoleDescription.value || `${readOnlyRoleLabel.value}不能执行用工比例计算等办理动作。`)

const currentRatioActionSummary = computed(() => {
  if (!currentRatio.value) {
    return "先从左侧焦点队列选择重点办理对象，再在台账中联动查看当前办理建议。"
  }
  if (primaryRatioAction.value.action === "calculate") {
    return "当前对象更适合先重跑用工比例计算，确认派遣人数、正式工人数和预警级别是否仍然准确。"
  }
  return "当前对象以详情复核为主，先看人数结构、比例值和预警状态，再决定是否继续比例整改。"
})

const currentRatioActionTags = computed(() => buildHintTags(currentRatio.value).slice(0, 3))

const workflowSteps = computed(() => ([
  {
    label: "先锁定统计月份",
    desc: "先按统计月份和用工单位范围锁定合规台账，避免跨月合同和人员结构混查。"
  },
  {
    label: "执行比例计算",
    desc: "按用工单位重算派遣工和正式工结构，统一生成比例值、预警级别和状态。"
  },
  {
    label: "回查合同与人员结构",
    desc: "优先回查超比例对象的合同结构、人员类型和在岗范围，确认是否存在口径偏差。"
  },
  {
    label: "承接整改与预警",
    desc: "黄警、红警对象继续承接到用工比例整改和预警中心，避免问题停留在计算结果页。"
  },
  {
    label: "完成月度合规归档",
    desc: "比例正常或已完成整改的对象沉淀为月度合规底稿，供监管和企业后续复核。"
  }
]))

const ratioHintTags = computed(() => buildHintTags(currentRatio.value))
const detailHintTags = computed(() => buildHintTags(detail.value))

function getList() {
  loading.value = true
  Promise.all([
    listEmploymentRatio(queryParams.value),
    getEmploymentRatioSummary(buildSummaryQuery())
  ]).then(([listResponse, summaryResponse]) => {
    employmentRatioList.value = listResponse.rows || []
    total.value = listResponse.total || 0
    summaryData.value = summaryResponse.data || {}
    syncCurrentRatio()
    loading.value = false
  }).catch(() => {
    loading.value = false
  })
}

function buildSummaryQuery() {
  return {
    statMonth: queryParams.value.statMonth,
    employerEnterpriseId: queryParams.value.employerEnterpriseId,
    warningLevel: queryParams.value.warningLevel,
    warningStatus: queryParams.value.warningStatus
  }
}

function loadEnterpriseOptions() {
  optionselectEnterprise().then(response => {
    enterpriseOptions.value = response.data || []
  })
}

function syncCurrentRatio() {
  if (currentRatio.value) {
    const matched = visibleRatioList.value.find(item => item.recordId === currentRatio.value.recordId)
    if (matched) {
      currentRatio.value = matched
      return
    }
  }
  currentRatio.value = visibleRatioList.value.length > 0 ? visibleRatioList.value[0] : undefined
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
    employerEnterpriseId: undefined,
    warningLevel: undefined,
    warningStatus: undefined
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
  currentRatio.value = row
}

function handleSelectFocus(item) {
  activeFocusKey.value = item.key
  syncCurrentRatio()
}

function openDetail(row) {
  currentRatio.value = row
  detail.value = row
  detailOpen.value = true
}

function handleCalculate() {
  if (isReadOnlyRole.value) {
    blockReadOnlyAction("执行用工比例计算")
    return
  }
  if (!ensureMonth()) {
    return
  }
  calculateEmploymentRatio({
    statMonth: queryParams.value.statMonth,
    employerEnterpriseId: queryParams.value.employerEnterpriseId
  }).then(response => {
    proxy.$modal.msgSuccess(response.msg || "用工比例计算完成")
    getList()
  })
}

function handlePrimaryRatioAction() {
  if (!currentRatio.value) {
    return
  }
  if (primaryRatioAction.value.action === "calculate") {
    handleCalculate()
    return
  }
  openDetail(currentRatio.value)
}

function handleExport() {
  proxy.download("ygb/special/employmentRatio/export", { ...queryParams.value }, `employment_ratio_${new Date().getTime()}.xlsx`)
}

function blockReadOnlyAction(actionLabel) {
  proxy?.$modal?.msgWarning?.(`${readOnlyRoleLabel.value}仅保留用工比例查看、详情和导出，不能${actionLabel}`)
}

function buildHintTags(item) {
  if (!item) {
    return [{ label: "未选中比例记录，可先在列表中选择待办理对象", type: "info" }]
  }
  const tags = []
  if (item.warningLevel === "2") {
    tags.push({ label: "当前为红警对象，建议直接承接比例整改并核对用工结构整改计划", type: "danger" })
  } else if (item.warningLevel === "1") {
    tags.push({ label: "当前为黄警对象，建议优先核对派遣规模和正式工结构", type: "warning" })
  } else {
    tags.push({ label: "当前比例正常，可继续月度合规复核和归档", type: "success" })
  }
  if (item.warningStatus === "1") {
    tags.push({ label: "当前已触发预警，建议继续承接到预警中心闭环处置", type: "warning" })
  }
  if (Number(item.ratioValue || 0) > 15) {
    tags.push({ label: "派遣比例超过 15%，建议优先核对合同归属、人员类型和用工模式", type: "danger" })
  } else if (Number(item.ratioValue || 0) > 10) {
    tags.push({ label: "派遣比例超过 10%，建议控制派遣规模并补充正式工结构说明", type: "warning" })
  }
  if (Number(item.dispatchCount || 0) <= 0 && Number(item.formalCount || 0) <= 0) {
    tags.push({ label: "当前人数结构为空，建议先核对合同范围和人员类型同步结果", type: "info" })
  }
  return tags
}

function matchRatioFocus(item, focusKey) {
  if (!focusKey || focusKey === "all") {
    return true
  }
  if (focusKey === "red") {
    return item.warningLevel === "2" || Number(item.ratioValue || 0) > 15
  }
  if (focusKey === "yellow") {
    return item.warningLevel === "1" || (Number(item.ratioValue || 0) > 10 && Number(item.ratioValue || 0) <= 15)
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

function warningLevelLabel(value) {
  const matched = warningLevelOptions.find(item => item.value === value)
  return matched ? matched.label : "-"
}

function warningStatusLabel(value) {
  const matched = warningStatusOptions.find(item => item.value === value)
  return matched ? matched.label : "-"
}

function valueOrDefault(value, fallback = 0) {
  return value != null ? value : fallback
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

watch(focusQueues, queues => {
  if (!queues.length) {
    activeFocusKey.value = ""
    return
  }
  if (!queues.find(item => item.key === activeFocusKey.value)) {
    activeFocusKey.value = queues[0].key
  }
}, { immediate: true })

watchEffect(() => {
  setPageGuide({
    title: '??????????',
    description: '?????????????????????????????????',
    portalExplanation: portalExplanationItems.value,
    focus: focusQueues.value,
    selection: [...selectedRatioOverview.value, { label: '??????', value: currentRatioActionSummary.value }],
    workflow: workflowSteps.value,
    hints: [...currentRatioActionTags.value, ...ratioHintTags.value].slice(0, 6)
  })
})

loadEnterpriseOptions()
getList()
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
