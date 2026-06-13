<template>
  <div class="app-container ygb-page">
    <section class="gov-page-header ygb-page__header">
      <div>
        <p class="ygb-page__eyebrow">工资核验链路</p>
        <h1 class="ygb-page__title">考勤归集办理台账</h1>
        <p class="ygb-page__desc">
          按统计月份将实名考勤原始记录归集为劳动者月度工时台账，面向企业管理员、经办和财务优先解决待归集、核验异常和未发薪对象，
          为工资明细生成、异常预警和监管月报提供统一口径。
        </p>
      </div>
      <div class="ygb-table-tip">
        当前版本支持按月份和派遣单位手动归集，后续可对接定时任务、消息队列和第三方设备实时流水。
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

    <div class="ygb-analysis-grid">
      <div v-for="item in analysisOverviewCards" :key="item.key" class="ygb-summary-card ygb-analysis-card" :class="item.cardClass">
        <div class="ygb-summary-card__label">{{ item.label }}</div>
        <div class="ygb-summary-card__value">
          {{ item.value }}
          <span class="ygb-summary-card__unit">{{ item.unit }}</span>
        </div>
        <div class="ygb-summary-card__note">{{ item.note }}</div>
      </div>
    </div>

    <div class="ygb-analysis-grid">
      <el-card class="ygb-focus-card ygb-analysis-panel" shadow="never">
        <template #header>
          <div class="ygb-card-head">
            <div class="ygb-card-head__title">区域总览</div>
            <div class="ygb-card-head__desc">按月度归集对象分布查看区域集中度，点击后保留当前筛选口径并切入区域。</div>
          </div>
        </template>
        <div v-if="regionOverviewItems.length" class="ygb-analysis-list">
          <button
            v-for="item in regionOverviewItems"
            :key="item.dimensionKey"
            type="button"
            class="ygb-analysis-item"
            @click="applyAnalysisFilter('regionCode', item.dimensionKey)"
          >
            <span class="ygb-analysis-item__label">{{ item.dimensionLabel }}</span>
            <span class="ygb-analysis-item__value">{{ item.dimensionCount }}</span>
          </button>
        </div>
        <el-empty v-else description="暂无区域统计" :image-size="56" />
      </el-card>

      <el-card class="ygb-focus-card ygb-analysis-panel" shadow="never">
        <template #header>
          <div class="ygb-card-head">
            <div class="ygb-card-head__title">归集状态分布</div>
            <div class="ygb-card-head__desc">按待归集、已归集、已核验、已发薪查看对象结构。</div>
          </div>
        </template>
        <div v-if="summaryStatusOverviewItems.length" class="ygb-analysis-list">
          <button
            v-for="item in summaryStatusOverviewItems"
            :key="item.dimensionKey"
            type="button"
            class="ygb-analysis-item"
            @click="applyAnalysisFilter('summaryStatus', item.dimensionKey)"
          >
            <span class="ygb-analysis-item__label">{{ item.dimensionLabel }}</span>
            <span class="ygb-analysis-item__value">{{ item.dimensionCount }}</span>
          </button>
        </div>
        <el-empty v-else description="暂无状态统计" :image-size="56" />
      </el-card>
    </div>


    <el-card class="search-card ygb-search-card" shadow="never">
      <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
        <el-form-item label="统计月份" prop="statMonth">
          <el-date-picker v-model="queryParams.statMonth" type="month" placeholder="请选择统计月份" format="YYYY-MM" value-format="YYYY-MM" style="width: 160px" />
        </el-form-item>
        <el-form-item label="派遣单位" prop="dispatchEnterpriseId">
          <el-select v-model="queryParams.dispatchEnterpriseId" placeholder="请选择派遣单位" clearable filterable style="width: 220px">
            <el-option v-for="item in enterpriseOptions" :key="item.enterpriseId" :label="item.enterpriseName" :value="item.enterpriseId" />
          </el-select>
        </el-form-item>
        <el-form-item label="归集状态" prop="summaryStatus">
          <el-select v-model="queryParams.summaryStatus" placeholder="请选择归集状态" clearable style="width: 150px">
            <el-option v-for="item in summaryStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="工资核验" prop="attCheck">
          <el-select v-model="queryParams.attCheck" placeholder="请选择核验状态" clearable style="width: 140px">
            <el-option v-for="item in attCheckOptions" :key="item.value" :label="item.label" :value="item.value" />
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
          <el-button
            type="primary"
            plain
            icon="Operation"
            @click="handleAggregate"
            v-hasPermi="['ygb:attendanceMonthly:aggregate']"
          >
            执行归集
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="warning"
            plain
            icon="Download"
            @click="handleExport"
            v-hasPermi="['ygb:attendanceMonthly:export']"
          >
            导出
          </el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
      </el-row>
    </el-card>

    <el-card class="table-card ygb-table-card" shadow="never">
      <template #header>
        <div class="ygb-card-head ygb-card-head--between">
          <div>
            <div class="ygb-card-head__title">考勤归集台账</div>
            <div class="ygb-card-head__desc">{{ focusTableHint }}</div>
          </div>
          <div class="ygb-card-head__desc">当前总量 {{ total }} 条</div>
        </div>
      </template>
      <el-table v-loading="loading" :data="visibleMonthlyList" @row-click="handleRowClick">
        <el-table-column label="ID" align="center" prop="monthlyId" width="80" />
        <el-table-column label="统计月份" align="center" prop="statMonth" width="100" />
        <el-table-column label="劳动者" align="center" prop="personName" width="110" />
        <el-table-column label="合同编号" align="center" prop="contractNo" width="180" />
        <el-table-column label="派遣单位" align="center" prop="dispatchEnterpriseName" min-width="220" />
        <el-table-column label="出勤天数" align="center" prop="attendanceDays" width="100" />
        <el-table-column label="缺勤天数" align="center" prop="absenceDays" width="100" />
        <el-table-column label="迟到天数" align="center" prop="lateDays" width="100" />
        <el-table-column label="早退天数" align="center" prop="earlyLeaveDays" width="100" />
        <el-table-column label="累计工时" align="center" prop="totalHours" width="100" />
        <el-table-column label="加班工时" align="center" prop="overtimeHours" width="100" />
        <el-table-column label="工资核验" align="center" prop="attCheck" width="100">
          <template #default="scope">
            <dict-tag :options="attCheckOptions" :value="scope.row.attCheck" />
          </template>
        </el-table-column>
        <el-table-column label="归集状态" align="center" prop="summaryStatus" width="100">
          <template #default="scope">
            <dict-tag :options="summaryStatusOptions" :value="scope.row.summaryStatus" />
          </template>
        </el-table-column>
        <el-table-column label="最后考勤日" align="center" prop="lastAttendanceDate" width="120">
          <template #default="scope">
            <span>{{ parseTime(scope.row.lastAttendanceDate, "{y}-{m}-{d}") }}</span>
          </template>
        </el-table-column>
        <el-table-column class-name="table-fill-column" min-width="1" />

        <el-table-column label="操作" fixed="right" align="center" width="100" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-button link type="info" icon="View" @click.stop="openDetail(scope.row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <pagination
        v-show="total > 0"
        :total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
      />
    </el-card>

    <el-dialog title="执行考勤归集" v-model="aggregateOpen" width="520px" append-to-body>
      <el-form ref="aggregateRef" :model="aggregateForm" :rules="aggregateRules" label-width="110px">
        <el-form-item label="统计月份" prop="statMonth">
          <el-date-picker v-model="aggregateForm.statMonth" type="month" placeholder="请选择统计月份" format="YYYY-MM" value-format="YYYY-MM" style="width: 100%" />
        </el-form-item>
        <el-form-item label="派遣单位" prop="dispatchEnterpriseId">
          <el-select v-model="aggregateForm.dispatchEnterpriseId" placeholder="不选则归集全量派遣单位" clearable filterable style="width: 100%">
            <el-option v-for="item in enterpriseOptions" :key="item.enterpriseId" :label="item.enterpriseName" :value="item.enterpriseId" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitAggregate">确定</el-button>
          <el-button @click="aggregateOpen = false">取消</el-button>
        </div>
      </template>
    </el-dialog>

    <page-detail-dialog v-model="detailOpen" title="考勤归集详情" width="760px">
      <template v-if="detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="统计月份">{{ detail.statMonth }}</el-descriptions-item>
          <el-descriptions-item label="劳动者">{{ detail.personName || "-" }}</el-descriptions-item>
          <el-descriptions-item label="合同编号">{{ detail.contractNo || "-" }}</el-descriptions-item>
          <el-descriptions-item label="身份证号">{{ maskIdCard(detail.idCard) || "-" }}</el-descriptions-item>
          <el-descriptions-item label="派遣单位">{{ detail.dispatchEnterpriseName || "-" }}</el-descriptions-item>
          <el-descriptions-item label="用工单位">{{ detail.employerEnterpriseName || "-" }}</el-descriptions-item>
          <el-descriptions-item label="归集状态">
            <dict-tag :options="summaryStatusOptions" :value="detail.summaryStatus" />
          </el-descriptions-item>
          <el-descriptions-item label="工资核验">
            <dict-tag :options="attCheckOptions" :value="detail.attCheck" />
          </el-descriptions-item>
          <el-descriptions-item label="出勤天数">{{ valueOrDefault(detail.attendanceDays, 0) }} 天</el-descriptions-item>
          <el-descriptions-item label="缺勤天数">{{ valueOrDefault(detail.absenceDays, 0) }} 天</el-descriptions-item>
          <el-descriptions-item label="迟到天数">{{ valueOrDefault(detail.lateDays, 0) }} 天</el-descriptions-item>
          <el-descriptions-item label="早退天数">{{ valueOrDefault(detail.earlyLeaveDays, 0) }} 天</el-descriptions-item>
          <el-descriptions-item label="累计工时">{{ formatDecimal(detail.totalHours) }} 小时</el-descriptions-item>
          <el-descriptions-item label="加班工时">{{ formatDecimal(detail.overtimeHours) }} 小时</el-descriptions-item>
          <el-descriptions-item label="最后考勤日" :span="2">{{ parseTime(detail.lastAttendanceDate, "{y}-{m}-{d}") || "-" }}</el-descriptions-item>
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

<script setup name="YgbAttendanceMonthly">
import { computed, getCurrentInstance, nextTick, reactive, ref, toRefs, watch } from "vue"
import { listAttendanceMonthly, aggregateAttendanceMonthly, getAttendanceMonthlySummary } from "@/api/ygb/attendanceMonthly"
import { optionselectEnterprise } from "@/api/ygb/enterprise"
import { decoratePortalExplanationItems, openPortalExplanationAction } from "@/utils/portalExplanation"
import { useRoleViewMode } from "@/utils/roleView"
import { parseTime } from "@/utils/yuegongbao"
import { useRoute, useRouter } from "vue-router"
import { applyWorkbenchRouteQuery, buildWorkbenchContext, stripWorkbenchRouteQuery } from "@/utils/workbenchLink"
import { formatRegionName } from "@/views/warning/useWarningPage"
import { useWorkbenchAssist } from "@/composables/useWorkbenchAssist"

const { proxy } = getCurrentInstance()
const { isReadOnlyRole, readOnlyRoleLabel, readOnlyRoleDescription } = useRoleViewMode()
const route = useRoute()
const { setPageGuide } = useWorkbenchAssist()
const router = useRouter()
const attendanceMonthlyWorkbenchFields = ["dispatchEnterpriseId", "statMonth", "focusKey"]

const summaryStatusOptions = [
  { label: "待归集", value: "1" },
  { label: "已归集", value: "2" },
  { label: "已核验", value: "3" },
  { label: "已发薪", value: "4" }
]

const attCheckOptions = [
  { label: "不通过", value: "0" },
  { label: "通过", value: "1" }
]

const attendanceMonthlyList = ref([])
const enterpriseOptions = ref([])
const summaryData = ref({})
const currentMonthly = ref(undefined)
const detail = ref(undefined)
const loading = ref(true)
const showSearch = ref(true)
const aggregateOpen = ref(false)
const detailOpen = ref(false)
const total = ref(0)
const activeFocusKey = ref("")

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    statMonth: undefined,
    dispatchEnterpriseId: undefined,
    summaryStatus: undefined,
    attCheck: undefined
  },
  aggregateForm: {
    statMonth: undefined,
    dispatchEnterpriseId: undefined
  },
  aggregateRules: {
    statMonth: [{ required: true, message: "统计月份不能为空", trigger: "change" }]
  }
})

const { queryParams, aggregateForm, aggregateRules } = toRefs(data)

function buildAttendanceMonthlyExplanationQuery(extraQuery = {}) {
  return {
    dispatchEnterpriseId: queryParams.value.dispatchEnterpriseId,
    statMonth: queryParams.value.statMonth,
    summaryStatus: queryParams.value.summaryStatus,
    attCheck: queryParams.value.attCheck,
    ...extraQuery
  }
}

const workbenchContext = computed(() => buildWorkbenchContext(route.query, {
  fields: attendanceMonthlyWorkbenchFields,
  sourceLabel: "企业工作台",
  title: "当前月度归集页沿用了工作台来源条件",
  description: "列表已按首页工作台带入的派遣单位和统计月份锁定数据范围，适合继续处理待归集和待核对对象。",
  fieldLabels: {
    dispatchEnterpriseId: "派遣单位",
    statMonth: "统计月份"
  },
  fieldFormatters: {
    dispatchEnterpriseId: value => enterpriseName(value) || value
  }
}))

const summaryCards = computed(() => ([
  {
    key: "total",
    label: "归集对象",
    value: summaryData.value.totalCount != null ? summaryData.value.totalCount : total.value,
    unit: "人",
    note: "当前筛选条件下已进入月度归集台账的人数规模。",
    cardClass: ""
  },
  {
    key: "pending",
    label: "待归集",
    value: valueOrDefault(summaryData.value.pendingAggregateCount, 0),
    unit: "人",
    note: "仍待执行归集或待生成月度台账的对象，应优先补跑当前月份。",
    cardClass: "ygb-summary-card--warning"
  },
  {
    key: "verified",
    label: "已核验",
    value: valueOrDefault(summaryData.value.verifiedCount, 0),
    unit: "人",
    note: "已完成工时核对并可进入工资明细环节的对象数量。",
    cardClass: "ygb-summary-card--primary"
  },
  {
    key: "paid",
    label: "已发薪",
    value: valueOrDefault(summaryData.value.paidCount, 0),
    unit: "人",
    note: "已沿工资链路完成发薪闭环的归集对象，可直接沉淀报表。",
    cardClass: "ygb-summary-card--success"
  }
]))

const analysisOverviewCards = computed(() => ([
  {
    key: "workHourAbnormalCount",
    label: "异常工时对象",
    value: valueOrDefault(summaryData.value.workHourAbnormalCount, 0),
    unit: "人",
    note: "缺勤、迟到、早退等异常应优先处理，避免把异常工时继续传导到工资链路。",
    cardClass: "ygb-summary-card--warning"
  },
  {
    key: "checkFailedCount",
    label: "校验失败对象",
    value: valueOrDefault(summaryData.value.checkFailedCount, 0),
    unit: "人",
    note: "核验失败对象应优先回查原始考勤和合同口径。",
    cardClass: "ygb-summary-card--primary"
  }
]))

const regionOverviewItems = computed(() => normalizeAnalysisItems(summaryData.value.regionStats, "regionCode"))
const summaryStatusOverviewItems = computed(() => normalizeAnalysisItems(summaryData.value.summaryStatusStats, "summaryStatus"))

const focusQueues = computed(() => {
  const rows = attendanceMonthlyList.value || []
  return [
    {
      key: "pendingAggregate",
      title: "待归集对象先补跑",
      desc: "优先处理尚未形成月度工时台账的对象，避免月底集中缺口。",
      count: summaryData.value.pendingAggregateCount != null ? summaryData.value.pendingAggregateCount : rows.filter(item => item.summaryStatus === "1").length,
      unit: "人",
      actionText: rows.some(item => item.summaryStatus === "1") ? "立即补归集" : "归集平稳"
    },
    {
      key: "checkFailed",
      title: "核验失败对象回查",
      desc: "优先回查工时、缺勤和合同口径，避免错误归集继续传导到工资核验。",
      count: summaryData.value.checkFailedCount != null ? summaryData.value.checkFailedCount : rows.filter(item => item.attCheck === "0").length,
      unit: "人",
      actionText: rows.some(item => item.attCheck === "0") ? "优先回查" : "核验平稳"
    },
    {
      key: "aggregatedNotPaid",
      title: "已归集未发薪对象",
      desc: "已归集或已核验但尚未进入发薪闭环的对象，应尽快承接工资链路。",
      count: summaryData.value.aggregatedCount != null ? summaryData.value.aggregatedCount : rows.filter(item => item.summaryStatus === "2" || item.summaryStatus === "3").length,
      unit: "人",
      actionText: rows.some(item => item.summaryStatus === "2" || item.summaryStatus === "3") ? "推进发薪" : "链路顺畅"
    },
    {
      key: "workHourAbnormal",
      title: "异常工时对象",
      desc: "存在缺勤、迟到、早退或工时异常对象更适合优先核对考勤口径。",
      count: rows.filter(item => hasWorkHourAbnormal(item)).length,
      unit: "人",
      actionText: rows.some(item => hasWorkHourAbnormal(item)) ? "先核对工时" : "工时平稳"
    }
  ]
})

const fallbackPortalExplanations = computed(() => focusQueues.value.map(item => ({
  key: item.key,
  dimensionName: item.title,
  currentValue: item.count,
  targetValue: "-",
  summary: item.desc,
  evidenceModule: "attendanceMonthly",
  recommendModule: "attendanceMonthly",
  defaultQuery: buildAttendanceMonthlyExplanationQuery({ focusKey: item.key }),
  sourceLabel: "530.1 月考勤办理解释",
  sourceDescription: item.desc,
  actionText: item.actionText
})))

const portalExplanations = computed(() => (Array.isArray(summaryData.value.ygbExplanation) && summaryData.value.ygbExplanation.length
  ? summaryData.value.ygbExplanation
  : fallbackPortalExplanations.value))

const portalExplanationItems = computed(() => decoratePortalExplanationItems(portalExplanations.value, {
  portalCode: "ygb",
  panelTitle: "530.1 月考勤办理解释",
  panelDescription: "当前解释项按 530.1 办理链口径展示月考勤归集、核对和回写重点。"
}))

const activeFocus = computed(() => focusQueues.value.find(item => item.key === activeFocusKey.value) || focusQueues.value[0])
const visibleMonthlyList = computed(() => prioritizeFocusRows(attendanceMonthlyList.value, row => matchMonthlyFocus(row, activeFocus.value?.key)))

const focusTableHint = computed(() => {
  if (!activeFocus.value) {
    return "当前按查询条件展示考勤归集台账。"
  }
  return `${activeFocus.value.title}，系统已将对应对象前置，便于优先处理最影响工资核验链路的归集对象。`
})

const selectedMonthlyOverview = computed(() => {
  if (!currentMonthly.value) {
    return [
      { label: "劳动者", value: "-" },
      { label: "合同/月份", value: "-" },
      { label: "归集状态", value: "-" },
      { label: "工时/核验", value: "-" }
    ]
  }
  return [
    { label: "劳动者", value: currentMonthly.value.personName || "-" },
    { label: "合同/月份", value: `${currentMonthly.value.contractNo || "-"} / ${currentMonthly.value.statMonth || "-"}` },
    { label: "归集状态", value: summaryStatusLabel(currentMonthly.value.summaryStatus) },
    { label: "工时/核验", value: `${formatDecimal(currentMonthly.value.totalHours)} 小时 / ${attCheckLabel(currentMonthly.value.attCheck)}` }
  ]
})

const primaryMonthlyAction = computed(() => {
  if (!currentMonthly.value) {
    return { label: "选择待办对象", action: "detail" }
  }
  if (isReadOnlyRole.value) {
    return { label: "查看详情", action: "detail" }
  }
  if (currentMonthly.value.summaryStatus === "1") {
    return { label: "执行归集", action: "aggregate" }
  }
  return { label: "查看详情", action: "detail" }
})

const currentMonthlyActionSummary = computed(() => {
  if (!currentMonthly.value) {
    return "先从左侧焦点队列选择重点归集对象，再联动查看当前办理建议。"
  }
  if (currentMonthly.value.summaryStatus === "1") {
    return "该对象当前仍待归集，建议先补跑当前月份归集，避免工时台账缺失影响工资生成。"
  }
  if (currentMonthly.value.attCheck === "0") {
    return "该对象工资核验未通过，建议优先回查原始考勤、缺勤天数和合同口径，再决定是否重新归集。"
  }
  if (hasWorkHourAbnormal(currentMonthly.value)) {
    return "该对象存在缺勤、迟到、早退或工时异常，建议先核对考勤口径，再承接后续工资链路。"
  }
  if (currentMonthly.value.summaryStatus === "2" || currentMonthly.value.summaryStatus === "3") {
    return "该对象已完成归集或核验，可直接承接工资明细和批次发薪链路。"
  }
  return "该对象已完成发薪闭环，可继续用于月报沉淀和监管对账。"
})

const currentMonthlyActionTags = computed(() => buildHintTags(currentMonthly.value))
const workflowSteps = computed(() => ([
  {
    label: "先锁定月份范围",
    desc: "先按统计月份和派遣单位锁定归集范围，避免跨月或跨主体混合归集。"
  },
  {
    label: "执行月度归集",
    desc: "将原始考勤按合同和人员汇总为月度工时台账，形成工资链路统一起点。"
  },
  {
    label: "核对工时与异常天数",
    desc: "优先核对缺勤、迟到、早退和加班口径，避免问题直接传导到工资计算。"
  },
  {
    label: "处理工资核验结果",
    desc: "核验不通过对象要先回查考勤或合同，再决定是否进入工资明细。"
  },
  {
    label: "承接工资发薪",
    desc: "已核验对象应直接承接到工资明细和批次发薪，不再重复手工核对。"
  }
]))

const monthlyHintTags = computed(() => buildHintTags(currentMonthly.value))
const detailHintTags = computed(() => buildHintTags(detail.value))

function getList() {
  loading.value = true
  Promise.all([
    listAttendanceMonthly(queryParams.value),
    getAttendanceMonthlySummary(buildSummaryQuery())
  ]).then(([listResponse, summaryResponse]) => {
    attendanceMonthlyList.value = listResponse.rows || []
    total.value = listResponse.total || 0
    summaryData.value = summaryResponse.data || {}
    syncCurrentMonthly()
    loading.value = false
  }).catch(() => {
    loading.value = false
  })
}

function buildSummaryQuery() {
  return {
    statMonth: queryParams.value.statMonth,
    dispatchEnterpriseId: queryParams.value.dispatchEnterpriseId,
    summaryStatus: queryParams.value.summaryStatus,
    attCheck: queryParams.value.attCheck
  }
}

function syncCurrentMonthly() {
  if (currentMonthly.value) {
    const matched = visibleMonthlyList.value.find(item => item.monthlyId === currentMonthly.value.monthlyId)
    if (matched) {
      currentMonthly.value = matched
      return
    }
  }
  currentMonthly.value = visibleMonthlyList.value.length > 0 ? visibleMonthlyList.value[0] : undefined
}

function loadEnterpriseOptions() {
  optionselectEnterprise().then(response => {
    enterpriseOptions.value = response.data || []
  })
}

function enterpriseName(value) {
  return enterpriseOptions.value.find(item => String(item.enterpriseId) === String(value))?.enterpriseName
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
    dispatchEnterpriseId: undefined,
    summaryStatus: undefined,
    attCheck: undefined
  })
  applyWorkbenchRouteQuery(route.query, queryParams.value, attendanceMonthlyWorkbenchFields)
  activeFocusKey.value = resolveAttendanceMonthlyFocusKey(route.query.focusKey)
  getList()

watchEffect(() => {
  setPageGuide({
    title: '????????' || '????????',
    description: '?????????????????????????????????' || '?????????????????????????????????',
    portalExplanation: portalExplanationItems.value,
    focus: focusQueues.value,
    selection: [...selectedMonthlyOverview.value, { label: '??????', value: currentMonthlyActionSummary.value }],
    workflow: workflowSteps.value,
    hints: [...currentMonthlyActionTags.value].slice(0, 6)
  })
})

}

function clearWorkbenchContext() {
  proxy.resetForm("queryRef")
  activeFocusKey.value = ""
  Object.assign(queryParams.value, {
    pageNum: 1,
    pageSize: 10,
    statMonth: undefined,
    dispatchEnterpriseId: undefined,
    summaryStatus: undefined,
    attCheck: undefined
  })
  router.replace({
    path: route.path,
    query: stripWorkbenchRouteQuery(route.query, attendanceMonthlyWorkbenchFields)
  })
  getList()
}

function applyAnalysisFilter(field, value) {
  queryParams.value.pageNum = 1
  queryParams.value[field] = value || undefined
  getList()
}

function applyAttendanceMonthlyWorkbenchQuery(routeQuery = {}) {
  Object.assign(queryParams.value, {
    pageNum: 1,
    pageSize: 10,
    statMonth: undefined,
    dispatchEnterpriseId: undefined,
    summaryStatus: undefined,
    attCheck: undefined
  })
  applyWorkbenchRouteQuery(routeQuery, queryParams.value, attendanceMonthlyWorkbenchFields)
  activeFocusKey.value = resolveAttendanceMonthlyFocusKey(routeQuery.focusKey)
  currentMonthly.value = undefined
  syncCurrentMonthly()
  getList()
}

function handleRowClick(row) {
  currentMonthly.value = row
}

function handleSelectFocus(item) {
  activeFocusKey.value = item.key
  syncCurrentMonthly()
}

function handlePortalExplanationAction(action) {
  if (action?.path === route.path) {
    applyAttendanceMonthlyWorkbenchQuery(action.query || {})
  }
  openPortalExplanationAction(router, action)
}

function openDetail(row) {
  if (!row) {
    return
  }
  currentMonthly.value = row
  detail.value = row
  detailOpen.value = true
}

function handleAggregate() {
  if (blockReadOnlyAction("执行考勤归集")) {
    return
  }
  aggregateOpen.value = true
  nextTick(() => {
    proxy.resetForm("aggregateRef")
    aggregateForm.value.statMonth = queryParams.value.statMonth
    aggregateForm.value.dispatchEnterpriseId = queryParams.value.dispatchEnterpriseId
  })
}

function submitAggregate() {
  proxy.$refs.aggregateRef.validate(valid => {
    if (!valid) {
      return
    }
    aggregateAttendanceMonthly(aggregateForm.value).then(response => {
      proxy.$modal.msgSuccess(response.msg || "归集成功")
      aggregateOpen.value = false
      queryParams.value.statMonth = aggregateForm.value.statMonth
      queryParams.value.dispatchEnterpriseId = aggregateForm.value.dispatchEnterpriseId
      getList()
    })
  })
}

function handlePrimaryMonthlyAction() {
  if (!currentMonthly.value) {
    return
  }
  if (primaryMonthlyAction.value.action === "aggregate") {
    handleAggregate()
    return
  }
  openDetail(currentMonthly.value)
}

function handleExport() {
  proxy.download("ygb/attendance/monthly/export", {
    ...queryParams.value
  }, `attendance_monthly_${new Date().getTime()}.xlsx`)
}

function buildHintTags(monthly) {
  if (!monthly) {
    return [{ label: "未选中归集对象，可先在列表中选择待办理人员", type: "info" }]
  }
  const tags = []
  if (monthly.summaryStatus === "1") {
    tags.push({ label: "当前待归集，建议先执行当前月份归集操作", type: "warning" })
  }
  if (monthly.summaryStatus === "2") {
    tags.push({ label: "当前已归集，建议继续核对工时和异常天数", type: "info" })
  }
  if (monthly.summaryStatus === "3") {
    tags.push({ label: "当前已核验，可直接承接到工资明细生成", type: "primary" })
  }
  if (monthly.summaryStatus === "4") {
    tags.push({ label: "当前已发薪，可直接进入月报和对账归档", type: "success" })
  }
  if (monthly.attCheck === "0") {
    tags.push({ label: "工资核验不通过，建议优先回查原始考勤和合同信息", type: "danger" })
  }
  if (hasWorkHourAbnormal(monthly)) {
    tags.push({ label: "存在缺勤、迟到、早退或工时异常，建议先核对异常工时口径", type: "warning" })
  }
  if (!tags.length) {
    tags.push({ label: "当前归集信息完整，可继续沿工资核验链路推进", type: "success" })
  }
  return tags
}

function blockReadOnlyAction(actionLabel) {
  if (!isReadOnlyRole.value) {
    return false
  }
  proxy.$modal.msgWarning(`${readOnlyRoleLabel.value}仅保留查看和导出能力，不能${actionLabel}`)
  return true
}

function hasWorkHourAbnormal(item) {
  return valueOrDefault(item?.absenceDays, 0) > 0 ||
    valueOrDefault(item?.lateDays, 0) > 0 ||
    valueOrDefault(item?.earlyLeaveDays, 0) > 0
}

function matchMonthlyFocus(row, focusKey) {
  if (!row || !focusKey) {
    return false
  }
  if (focusKey === "pendingAggregate") {
    return row.summaryStatus === "1"
  }
  if (focusKey === "checkFailed") {
    return row.attCheck === "0"
  }
  if (focusKey === "aggregatedNotPaid") {
    return row.summaryStatus === "2" || row.summaryStatus === "3"
  }
  if (focusKey === "workHourAbnormal") {
    return hasWorkHourAbnormal(row)
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

function summaryStatusLabel(value) {
  const matched = summaryStatusOptions.find(item => item.value === value)
  return matched ? matched.label : "-"
}

function attCheckLabel(value) {
  const matched = attCheckOptions.find(item => item.value === value)
  return matched ? matched.label : "-"
}

function normalizeAnalysisItems(items = [], field) {
  return (Array.isArray(items) ? items : []).map(item => ({
    ...item,
    dimensionLabel: resolveAnalysisLabel(field, item.dimensionKey, item.dimensionLabel),
    dimensionCount: Number(item.dimensionCount || 0)
  }))
}

function resolveAnalysisLabel(field, value, fallback) {
  if (field === "regionCode") {
    return formatRegionName(value, fallback || value || "-")
  }
  if (field === "summaryStatus") {
    return summaryStatusLabel(value || fallback)
  }
  return fallback || value || "-"
}

function valueOrDefault(value, fallback = 0) {
  return value != null ? value : fallback
}

function formatDecimal(value) {
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

function resolveAttendanceMonthlyFocusKey(value) {
  const normalized = typeof value === "string" ? value.trim() : ""
  return focusQueues.value.some(item => item.key === normalized) ? normalized : ""
}

function attendanceMonthlyFocusLabel(value) {
  return focusQueues.value.find(item => item.key === value)?.title || value || "-"
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

loadEnterpriseOptions()
applyWorkbenchRouteQuery(route.query, queryParams.value, attendanceMonthlyWorkbenchFields)
activeFocusKey.value = resolveAttendanceMonthlyFocusKey(route.query.focusKey)
getList()
</script>

<style scoped lang="scss">
.ygb-summary-grid,
.ygb-analysis-grid,
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

.ygb-analysis-card {
  min-height: 154px;
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

.ygb-analysis-list {
  display: grid;
  gap: 10px;
}

.ygb-analysis-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  width: 100%;
  padding: 12px 14px;
  border: 1px solid #e0e8f0;
  border-radius: 12px;
  background: #f7fafc;
  color: #13243a;
  cursor: pointer;
  transition: all 0.2s ease;
  text-align: left;
}

.ygb-analysis-item:hover {
  border-color: #0f5ea8;
  background: #eef5ff;
}

.ygb-analysis-item__label {
  color: #425568;
  line-height: 1.6;
}

.ygb-analysis-item__value {
  color: #13243a;
  font-size: 18px;
  font-weight: 700;
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
  .ygb-analysis-grid,
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
