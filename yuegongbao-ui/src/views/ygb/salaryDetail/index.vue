<template>
  <div class="app-container ygb-page">
    <section class="gov-page-header ygb-page__header">
      <div>
        <p class="ygb-page__eyebrow">工资监管链路</p>
        <h1 class="ygb-page__title">工资明细办理台账</h1>
        <p class="ygb-page__desc">
          以考勤归集结果为基础生成个人工资明细，面向财务经办和企业管理员优先处理核验不通过、待发放、发放失败和银行卡异常对象，
          作为工资代发、个税比对和社保基数比对的明细底账。
        </p>
      </div>
      <div class="ygb-table-tip">
        当前版本支持按批次维护工资明细；已发放批次默认只做查看和留痕，避免事后篡改发薪结果。
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
        <el-form-item label="工资批次" prop="batchId">
          <el-select v-model="queryParams.batchId" placeholder="请选择工资批次" clearable filterable style="width: 240px">
            <el-option v-for="item in batchOptions" :key="item.batchId" :label="`${item.batchNo} / ${item.statMonth}`" :value="item.batchId" />
          </el-select>
        </el-form-item>
        <el-form-item label="劳动者" prop="personName">
          <el-input v-model="queryParams.personName" placeholder="请输入劳动者姓名" clearable style="width: 180px" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="工资核验" prop="attCheck">
          <el-select v-model="queryParams.attCheck" placeholder="请选择核验状态" clearable style="width: 140px">
            <el-option v-for="item in attCheckOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="发放状态" prop="payStatus">
          <el-select v-model="queryParams.payStatus" placeholder="请选择发放状态" clearable style="width: 140px">
            <el-option v-for="item in payStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
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
          <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['ygb:salaryDetail:edit']">修改</el-button>
        </el-col>
        <el-col v-if="!isReadOnlyRole" :span="1.5">
          <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['ygb:salaryDetail:remove']">删除</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['ygb:salaryDetail:export']">导出</el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
      </el-row>
    </el-card>

    <el-card class="table-card ygb-table-card" shadow="never">
      <template #header>
        <div class="ygb-card-head ygb-card-head--between">
          <div>
            <div class="ygb-card-head__title">工资明细台账</div>
            <div class="ygb-card-head__desc">{{ focusTableHint }}</div>
          </div>
          <div class="ygb-card-head__desc">当前总量 {{ total }} 条</div>
        </div>
      </template>
      <el-table v-loading="loading" :data="visibleDetailList" @selection-change="handleSelectionChange" @row-click="handleRowClick">
        <el-table-column v-if="!isReadOnlyRole" type="selection" width="55" align="center" />
        <el-table-column label="ID" align="center" prop="detailId" width="80" />
        <el-table-column label="批次编号" align="center" prop="batchNo" width="180" />
        <el-table-column label="劳动者" align="center" prop="personName" width="110" />
        <el-table-column label="合同编号" align="center" prop="contractNo" width="180" />
        <el-table-column label="出勤天数" align="center" prop="attendanceDays" width="90" />
        <el-table-column label="累计工时" align="center" prop="totalHours" width="90" />
        <el-table-column label="工资核验" align="center" prop="attCheck" width="100">
          <template #default="scope">
            <dict-tag :options="attCheckOptions" :value="scope.row.attCheck" />
          </template>
        </el-table-column>
        <el-table-column label="应发工资" align="center" prop="payableAmount" width="110" />
        <el-table-column label="扣减金额" align="center" prop="deductionAmount" width="110" />
        <el-table-column label="实发工资" align="center" prop="netAmount" width="110" />
        <el-table-column label="发放状态" align="center" prop="payStatus" width="100">
          <template #default="scope">
            <dict-tag :options="payStatusOptions" :value="scope.row.payStatus" />
          </template>
        </el-table-column>
        <el-table-column label="银行卡号" align="center" prop="bankAccountNo" width="180" />
        <el-table-column class-name="table-fill-column" min-width="1" />

        <el-table-column label="操作" fixed="right" align="center" :width="isReadOnlyRole ? 90 : 220" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-button link type="info" icon="View" @click.stop="openDetail(scope.row)">详情</el-button>
            <template v-if="!isReadOnlyRole">
              <el-button link type="primary" icon="Edit" @click.stop="handleUpdate(scope.row)" v-hasPermi="['ygb:salaryDetail:edit']">修改</el-button>
              <el-button link type="danger" icon="Delete" @click.stop="handleDelete(scope.row)" v-hasPermi="['ygb:salaryDetail:remove']">删除</el-button>
            </template>
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

    <el-dialog :title="title" v-model="open" width="860px" append-to-body>
      <el-form ref="salaryDetailRef" :model="form" :rules="rules" label-width="110px">
        <div class="ygb-panel-grid">
          <el-form-item label="工资核验" prop="attCheck">
            <el-select v-model="form.attCheck" placeholder="请选择工资核验">
              <el-option v-for="item in attCheckOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="发放状态" prop="payStatus">
            <el-select v-model="form.payStatus" placeholder="请选择发放状态">
              <el-option v-for="item in payStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="应发工资" prop="payableAmount">
            <el-input-number v-model="form.payableAmount" :min="0" :precision="2" :step="100" style="width: 100%" />
          </el-form-item>
          <el-form-item label="扣减金额" prop="deductionAmount">
            <el-input-number v-model="form.deductionAmount" :min="0" :precision="2" :step="100" style="width: 100%" />
          </el-form-item>
          <el-form-item label="开户名" prop="bankAccountName">
            <el-input v-model="form.bankAccountName" placeholder="请输入开户名" />
          </el-form-item>
          <el-form-item label="银行卡号" prop="bankAccountNo">
            <el-input v-model="form.bankAccountNo" placeholder="请输入银行卡号" />
          </el-form-item>
        </div>
        <el-form-item label="失败原因" prop="failReason">
          <el-input v-model="form.failReason" placeholder="请输入失败原因" />
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

    <page-detail-dialog v-model="detailOpen" title="工资明细详情" width="760px">
      <template v-if="detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="批次编号">{{ detail.batchNo || "-" }}</el-descriptions-item>
          <el-descriptions-item label="统计月份">{{ detail.statMonth || "-" }}</el-descriptions-item>
          <el-descriptions-item label="劳动者">{{ detail.personName || "-" }}</el-descriptions-item>
          <el-descriptions-item label="身份证号">{{ maskIdCard(detail.idCard) || "-" }}</el-descriptions-item>
          <el-descriptions-item label="合同编号">{{ detail.contractNo || "-" }}</el-descriptions-item>
          <el-descriptions-item label="派遣单位">{{ detail.dispatchEnterpriseName || "-" }}</el-descriptions-item>
          <el-descriptions-item label="用工单位">{{ detail.employerEnterpriseName || "-" }}</el-descriptions-item>
          <el-descriptions-item label="工资核验">
            <dict-tag :options="attCheckOptions" :value="detail.attCheck" />
          </el-descriptions-item>
          <el-descriptions-item label="发放状态">
            <dict-tag :options="payStatusOptions" :value="detail.payStatus" />
          </el-descriptions-item>
          <el-descriptions-item label="出勤天数">{{ valueOrDefault(detail.attendanceDays, 0) }} 天</el-descriptions-item>
          <el-descriptions-item label="累计工时">{{ formatMoney(detail.totalHours) }} 小时</el-descriptions-item>
          <el-descriptions-item label="应发工资">{{ formatMoney(detail.payableAmount) }}</el-descriptions-item>
          <el-descriptions-item label="扣减金额">{{ formatMoney(detail.deductionAmount) }}</el-descriptions-item>
          <el-descriptions-item label="实发工资">{{ formatMoney(detail.netAmount) }}</el-descriptions-item>
          <el-descriptions-item label="开户名">{{ detail.bankAccountName || "-" }}</el-descriptions-item>
          <el-descriptions-item label="银行卡号" :span="2">{{ detail.bankAccountNo || "-" }}</el-descriptions-item>
          <el-descriptions-item label="失败原因" :span="2">{{ detail.failReason || "-" }}</el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ detail.remark || "-" }}</el-descriptions-item>
        </el-descriptions>

        <div class="detail-block">
          <h3>办理提示</h3>
          <div class="ygb-tag-list">
            <el-tag v-for="item in detailHintTagsForDetail" :key="item.label" :type="item.type" effect="plain">{{ item.label }}</el-tag>
          </div>
        </div>
      </template>
    </page-detail-dialog>
  </div>
</template>

<script setup name="YgbSalaryDetail">
import { listSalaryDetail, getSalaryDetail, updateSalaryDetail, delSalaryDetail, getSalaryDetailSummary } from "@/api/ygb/salaryDetail"
import { optionselectSalaryBatch } from "@/api/ygb/salaryBatch"
import { decoratePortalExplanationItems, openPortalExplanationAction } from '@/utils/portalExplanation'
import { useRoleViewMode } from "@/utils/roleView"
import { useRoute, useRouter } from "vue-router"
import { applyWorkbenchRouteQuery, buildWorkbenchContext, stripWorkbenchRouteQuery } from "@/utils/workbenchLink"
import { useWorkbenchAssist } from "@/composables/useWorkbenchAssist"

const { proxy } = getCurrentInstance()
const { isReadOnlyRole, readOnlyRoleLabel, readOnlyRoleDescription } = useRoleViewMode()
const route = useRoute()
const { setPageGuide } = useWorkbenchAssist()
const router = useRouter()
const salaryDetailWorkbenchFields = ["batchId", "personName", "payStatus"]

const attCheckOptions = [
  { label: "不通过", value: "0" },
  { label: "通过", value: "1" }
]

const payStatusOptions = [
  { label: "待发放", value: "0" },
  { label: "发放中", value: "1" },
  { label: "已发放", value: "2" },
  { label: "发放失败", value: "3" }
]

const salaryDetailList = ref([])
const batchOptions = ref([])
const summaryData = ref({})
const currentDetail = ref(undefined)
const detail = ref(undefined)
const open = ref(false)
const detailOpen = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")
const activeFocusKey = ref("")

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    batchId: undefined,
    personName: undefined,
    attCheck: undefined,
    payStatus: undefined
  },
  rules: {
    attCheck: [{ required: true, message: "工资核验不能为空", trigger: "change" }],
    payableAmount: [{ required: true, message: "应发工资不能为空", trigger: "blur" }],
    deductionAmount: [{ required: true, message: "扣减金额不能为空", trigger: "blur" }]
  }
})

const { queryParams, form, rules } = toRefs(data)
const portalExplanations = computed(() => summaryData.value.ygbExplanation || [])
const portalExplanationItems = computed(() => decoratePortalExplanationItems(portalExplanations.value, {
  portalCode: 'ygb',
  panelTitle: '530.1 办理链解释',
  panelDescription: '工资核验、发放异常和补录建议统一来自门户解释聚合接口。'
}))

function handlePortalExplanationAction(action) {
  openPortalExplanationAction(router, action)
}

const workbenchContext = computed(() => buildWorkbenchContext(route.query, {
  fields: salaryDetailWorkbenchFields,
  sourceLabel: "财务工作台",
  title: "当前工资明细页沿用了工作台来源条件",
  description: "列表已按首页工作台带入的批次、人员和发放状态锁定，可直接继续核对失败明细与发放结果。",
  fieldLabels: {
    batchId: "工资批次",
    personName: "劳动者",
    payStatus: "发放状态"
  },
  fieldFormatters: {
    batchId: value => selectedBatchLabel(value) || value,
    payStatus: value => payStatusLabel(value)
  }
}))

const summaryCards = computed(() => ([
  {
    key: "total",
    label: "工资明细",
    value: summaryData.value.totalCount != null ? summaryData.value.totalCount : total.value,
    unit: "条",
    note: "当前筛选条件下进入发薪核对链路的工资明细总量。",
    cardClass: ""
  },
  {
    key: "pending",
    label: "待发放",
    value: valueOrDefault(summaryData.value.pendingPayCount, 0),
    unit: "条",
    note: "待进入银行代发或仍需财务复核的工资明细，应优先关注。",
    cardClass: "ygb-summary-card--warning"
  },
  {
    key: "paid",
    label: "已发放",
    value: valueOrDefault(summaryData.value.paidCount, 0),
    unit: "条",
    note: "已完成代发闭环的工资明细，可直接参与对账与税社联动。",
    cardClass: "ygb-summary-card--success"
  },
  {
    key: "risk",
    label: "失败/核验异常",
    value: valueOrDefault(summaryData.value.failedCount, 0) + valueOrDefault(summaryData.value.checkFailedCount, 0),
    unit: "条",
    note: "发放失败与考勤核验异常对象需要优先消化，避免月度闭环被拖住。",
    cardClass: "ygb-summary-card--primary"
  }
]))

const focusQueues = computed(() => {
  const rows = salaryDetailList.value || []
  return [
    {
      key: "checkFailed",
      title: "核验不通过对象",
      desc: "先回查考勤归集、合同口径和工时，避免错误明细继续流向代发。",
      count: summaryData.value.checkFailedCount != null ? summaryData.value.checkFailedCount : rows.filter(item => item.attCheck === "0").length,
      unit: "条",
      actionText: rows.some(item => item.attCheck === "0") ? "优先回查" : "核验平稳"
    },
    {
      key: "pendingPay",
      title: "待发放对象",
      desc: "优先处理金额已定但尚未进入银行代发的工资明细，缩短发薪等待时间。",
      count: summaryData.value.pendingPayCount != null ? summaryData.value.pendingPayCount : rows.filter(item => item.payStatus === "0").length,
      unit: "条",
      actionText: rows.some(item => item.payStatus === "0") ? "复核后发放" : "待发平稳"
    },
    {
      key: "failedPay",
      title: "发放失败对象",
      desc: "重点回看失败原因、账户信息和金额口径，避免失败记录反复积压。",
      count: summaryData.value.failedCount != null ? summaryData.value.failedCount : rows.filter(item => item.payStatus === "3").length,
      unit: "条",
      actionText: rows.some(item => item.payStatus === "3") ? "立即核对失败" : "失败可控"
    },
    {
      key: "missingBankCard",
      title: "银行卡异常对象",
      desc: "优先补齐开户名或银行卡号，避免进入银行代发时集中失败。",
      count: rows.filter(item => !item.bankAccountNo).length,
      unit: "条",
      actionText: rows.some(item => !item.bankAccountNo) ? "补齐银行卡" : "账户齐备"
    }
  ]
})

const activeFocus = computed(() => focusQueues.value.find(item => item.key === activeFocusKey.value) || focusQueues.value[0])
const visibleDetailList = computed(() => prioritizeFocusRows(salaryDetailList.value, row => matchDetailFocus(row, activeFocus.value?.key)))

const focusTableHint = computed(() => {
  if (!activeFocus.value) {
    return "当前按查询条件展示工资明细台账。"
  }
  return `${activeFocus.value.title}，系统已将对应对象前置，便于财务和企业先处理最影响发薪闭环的明细。`
})

const selectedDetailOverview = computed(() => {
  if (!currentDetail.value) {
    return [
      { label: "劳动者", value: "-" },
      { label: "批次/合同", value: "-" },
      { label: "发放状态", value: "-" },
      { label: "实发/核验", value: "-" }
    ]
  }
  return [
    { label: "劳动者", value: currentDetail.value.personName || "-" },
    { label: "批次/合同", value: `${currentDetail.value.batchNo || "-"} / ${currentDetail.value.contractNo || "-"}` },
    { label: "发放状态", value: payStatusLabel(currentDetail.value.payStatus) },
    { label: "实发/核验", value: `${formatMoney(currentDetail.value.netAmount)} / ${attCheckLabel(currentDetail.value.attCheck)}` }
  ]
})

const primaryDetailAction = computed(() => {
  if (!currentDetail.value) {
    return { label: "选择待办对象", action: "detail" }
  }
  if (isReadOnlyRole.value) {
    return { label: "查看详情", action: "detail" }
  }
  if (currentDetail.value.attCheck === "0" || currentDetail.value.payStatus === "3" || !currentDetail.value.bankAccountNo) {
    return { label: "修改明细", action: "edit" }
  }
  return { label: "查看详情", action: "detail" }
})

const currentDetailActionSummary = computed(() => {
  if (!currentDetail.value) {
    return "先从左侧焦点队列选择重点对象，再联动查看当前明细的修正建议。"
  }
  if (currentDetail.value.attCheck === "0") {
    return "该明细考勤核验未通过，建议先回查出勤天数、累计工时和合同口径，再决定是否修正金额。"
  }
  if (currentDetail.value.payStatus === "3") {
    return "该明细发放失败，建议优先核对失败原因、银行卡信息和实发金额，避免批次重复失败。"
  }
  if (!currentDetail.value.bankAccountNo) {
    return "该明细缺少银行卡号，应先补齐开户名和账户信息，再进入发放链路。"
  }
  if (currentDetail.value.payStatus === "0") {
    return "该明细当前待发放，建议复核金额和账户信息后，由批次端统一推进代发。"
  }
  if (currentDetail.value.payStatus === "1") {
    return "该明细已处于发放中，建议重点关注银行回写进度，必要时回到批次端核对代发状态。"
  }
  return "该明细当前信息完整，可继续承接税社比对、对账归档和后续监管查询。"
})

const currentDetailActionTags = computed(() => buildHintTags(currentDetail.value))
const workflowSteps = computed(() => ([
  {
    label: "锁定工资批次",
    desc: "先按工资批次和月份锁定明细范围，避免财务在不同批次间来回切换。"
  },
  {
    label: "核对考勤与工时",
    desc: "先确认出勤天数、累计工时和考勤核验结果，再进入金额修正。"
  },
  {
    label: "修正应发与扣减",
    desc: "在未发放批次中修正应发工资、扣减金额和实发金额，确保发薪口径一致。"
  },
  {
    label: "检查银行卡信息",
    desc: "重点检查开户名和银行卡号，避免银行代发阶段出现失败回写。"
  },
  {
    label: "回看发放结果",
    desc: "对发放中、失败和已发放对象分流处理，形成财务闭环和后续税社比对底稿。"
  }
]))

const detailHintTagsForCurrent = computed(() => buildHintTags(currentDetail.value))
const detailHintTagsForDetail = computed(() => buildHintTags(detail.value))

function getList() {
  loading.value = true
  Promise.all([
    listSalaryDetail(queryParams.value),
    getSalaryDetailSummary(buildSummaryQuery())
  ]).then(([listResponse, summaryResponse]) => {
    salaryDetailList.value = listResponse.rows || []
    total.value = listResponse.total || 0
    summaryData.value = summaryResponse.data || {}
    syncCurrentDetail()
    loading.value = false
  }).catch(() => {
    loading.value = false
  })
}

function buildSummaryQuery() {
  return {
    batchId: queryParams.value.batchId,
    personName: queryParams.value.personName,
    attCheck: queryParams.value.attCheck,
    payStatus: queryParams.value.payStatus
  }
}

function syncCurrentDetail() {
  if (currentDetail.value) {
    const matched = visibleDetailList.value.find(item => item.detailId === currentDetail.value.detailId)
    if (matched) {
      currentDetail.value = matched
      return
    }
  }
  currentDetail.value = visibleDetailList.value.length > 0 ? visibleDetailList.value[0] : undefined
}

function loadBatchOptions() {
  optionselectSalaryBatch().then(response => {
    batchOptions.value = response.data || []
  })
}

function reset() {
  form.value = {
    detailId: undefined,
    attCheck: "1",
    payableAmount: undefined,
    deductionAmount: 0,
    bankAccountName: undefined,
    bankAccountNo: undefined,
    payStatus: "0",
    failReason: undefined,
    remark: undefined
  }
  proxy.resetForm("salaryDetailRef")
}

function cancel() {
  open.value = false
  reset()
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
    batchId: undefined,
    personName: undefined,
    attCheck: undefined,
    payStatus: undefined
  })
  applyWorkbenchRouteQuery(route.query, queryParams.value, salaryDetailWorkbenchFields)
  getList()

watchEffect(() => {
  setPageGuide({
    title: '????????' || '????????',
    description: '?????????????????????????????????' || '?????????????????????????????????',
    portalExplanation: portalExplanationItems.value,
    focus: focusQueues.value,
    selection: [...selectedDetailOverview.value, { label: '??????', value: currentDetailActionSummary.value }],
    workflow: workflowSteps.value,
    hints: [...currentDetailActionTags.value].slice(0, 6)
  })
})

}

function clearWorkbenchContext() {
  Object.assign(queryParams.value, {
    pageNum: 1,
    batchId: undefined,
    personName: undefined,
    payStatus: undefined
  })
  router.replace({
    path: route.path,
    query: stripWorkbenchRouteQuery(route.query, salaryDetailWorkbenchFields)
  })
  getList()
}

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.detailId)
  single.value = selection.length !== 1
  multiple.value = !selection.length
}

function handleRowClick(row) {
  currentDetail.value = row
}

function handleSelectFocus(item) {
  activeFocusKey.value = item.key
  syncCurrentDetail()
}

function handleUpdate(row) {
  if (blockReadOnlyAction("修改工资明细")) {
    return
  }
  reset()
  const detailId = row?.detailId || ids.value[0]
  if (!detailId) {
    return
  }
  getSalaryDetail(detailId).then(response => {
    form.value = response.data
    currentDetail.value = response.data || currentDetail.value
    open.value = true
    title.value = "修改工资明细"
  })
}

function openDetail(row) {
  if (!row?.detailId) {
    return
  }
  currentDetail.value = row
  getSalaryDetail(row.detailId).then(response => {
    detail.value = response.data
    detailOpen.value = true
  })
}

function submitForm() {
  proxy.$refs.salaryDetailRef.validate(valid => {
    if (!valid) {
      return
    }
    updateSalaryDetail(form.value).then(() => {
      proxy.$modal.msgSuccess("修改成功")
      open.value = false
      getList()
      refreshDetailIfMatched(form.value.detailId)
    })
  })
}

function handleDelete(row) {
  if (blockReadOnlyAction("删除工资明细")) {
    return
  }
  const detailIds = row?.detailId || ids.value
  proxy.$modal.confirm("是否确认删除选中的工资明细？").then(function() {
    return delSalaryDetail(detailIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

function handlePrimaryDetailAction() {
  if (!currentDetail.value) {
    return
  }
  if (primaryDetailAction.value.action === "edit") {
    handleUpdate(currentDetail.value)
    return
  }
  openDetail(currentDetail.value)
}

function refreshDetailIfMatched(detailId) {
  if (detailOpen.value && detail.value && detail.value.detailId === detailId) {
    getSalaryDetail(detailId).then(response => {
      detail.value = response.data
    })
  }
}

function handleExport() {
  proxy.download("ygb/salary/detail/export", {
    ...queryParams.value
  }, `salary_detail_${new Date().getTime()}.xlsx`)
}

function buildHintTags(item) {
  if (!item) {
    return [{ label: "未选中工资明细，可先在列表中选择待办理对象", type: "info" }]
  }
  const tags = []
  if (item.attCheck === "0") {
    tags.push({ label: "考勤核验不通过，建议先回查归集工时和合同口径", type: "danger" })
  }
  if (item.payStatus === "0") {
    tags.push({ label: "当前待发放，建议先复核金额和银行卡信息", type: "warning" })
  }
  if (item.payStatus === "1") {
    tags.push({ label: "当前发放中，建议关注银行回写进度", type: "info" })
  }
  if (item.payStatus === "2") {
    tags.push({ label: "当前已发放，可直接承接税社比对与对账归档", type: "success" })
  }
  if (item.payStatus === "3") {
    tags.push({ label: "当前发放失败，建议优先核对失败原因和账户信息", type: "warning" })
  }
  if (!item.bankAccountNo) {
    tags.push({ label: "缺少银行卡号，银行代发前必须补齐", type: "warning" })
  }
  if (!tags.length) {
    tags.push({ label: "当前工资明细信息完整，可继续沿发薪链路推进", type: "success" })
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

function selectedBatchLabel(batchId) {
  const matched = batchOptions.value.find(item => item.batchId === batchId)
  return matched ? `${matched.batchNo} / ${matched.statMonth}` : ""
}

function attCheckLabel(value) {
  const matched = attCheckOptions.find(item => item.value === value)
  return matched ? matched.label : "-"
}

function payStatusLabel(value) {
  const matched = payStatusOptions.find(item => item.value === value)
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

function matchDetailFocus(row, focusKey) {
  if (!row || !focusKey) {
    return false
  }
  if (focusKey === "checkFailed") {
    return row.attCheck === "0"
  }
  if (focusKey === "pendingPay") {
    return row.payStatus === "0"
  }
  if (focusKey === "failedPay") {
    return row.payStatus === "3"
  }
  if (focusKey === "missingBankCard") {
    return !row.bankAccountNo
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

watch(focusQueues, queues => {
  if (!queues.length) {
    activeFocusKey.value = ""
    return
  }
  if (!queues.find(item => item.key === activeFocusKey.value)) {
    activeFocusKey.value = queues[0].key
  }
}, { immediate: true })

loadBatchOptions()
reset()
applyWorkbenchRouteQuery(route.query, queryParams.value, salaryDetailWorkbenchFields)
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

.ygb-panel-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0 16px;
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
  .ygb-source-list,
  .ygb-panel-grid {
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
