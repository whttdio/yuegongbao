<template>
  <div class="app-container ygb-page">
    <section class="gov-page-header ygb-page__header">
      <div>
        <p class="ygb-page__eyebrow">工资监管链路</p>
        <h1 class="ygb-page__title">工资批次办理台账</h1>
        <p class="ygb-page__desc">
          面向企业管理员、财务经办和监管协同角色，统一承接工资月度批次创建、监管账户到账确认、工资明细生成、银行代发提交和 Stub 回调闭环。
          页面不再停留在批量增删改查，而是按“先补到账、再生成明细、后推进代发”的真实办理顺序组织任务。
        </p>
      </div>
      <div class="ygb-table-tip">
        当前版本已打通人工建批、到账确认、明细生成、代发提交和 Stub 回调链路，后续替换真实银行接口时不改办理台账结构。
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
      <div v-for="item in salaryOverviewCards" :key="item.key" class="ygb-summary-card ygb-analysis-card" :class="item.cardClass">
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
        <el-form-item label="批次编号" prop="batchNo">
          <el-input v-model="queryParams.batchNo" placeholder="请输入批次编号" clearable style="width: 220px" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="统计月份" prop="statMonth">
          <el-date-picker v-model="queryParams.statMonth" type="month" placeholder="请选择统计月份" format="YYYY-MM" value-format="YYYY-MM" style="width: 160px" />
        </el-form-item>
        <el-form-item label="派遣单位" prop="dispatchEnterpriseId">
          <el-select v-model="queryParams.dispatchEnterpriseId" placeholder="请选择派遣单位" clearable filterable style="width: 220px">
            <el-option v-for="item in enterpriseOptions" :key="item.enterpriseId" :label="item.enterpriseName" :value="item.enterpriseId" />
          </el-select>
        </el-form-item>
        <el-form-item label="批次状态" prop="batchStatus">
          <el-select v-model="queryParams.batchStatus" placeholder="请选择批次状态" clearable style="width: 150px">
            <el-option v-for="item in batchStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="到账状态" prop="accountStatus">
          <el-select v-model="queryParams.accountStatus" placeholder="请选择到账状态" clearable style="width: 140px">
            <el-option v-for="item in accountStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
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
          <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['ygb:salaryBatch:add']">新增</el-button>
        </el-col>
        <el-col v-if="!isReadOnlyRole" :span="1.5">
          <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['ygb:salaryBatch:edit']">修改</el-button>
        </el-col>
        <el-col v-if="!isReadOnlyRole" :span="1.5">
          <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['ygb:salaryBatch:remove']">删除</el-button>
        </el-col>
        <el-col v-if="!isReadOnlyRole" :span="1.5">
          <el-button type="warning" plain icon="DocumentAdd" :disabled="single" @click="handleGenerate()" v-hasPermi="['ygb:salaryBatch:generate']">生成明细</el-button>
        </el-col>
        <el-col v-if="!isReadOnlyRole" :span="1.5">
          <el-button type="info" plain icon="Wallet" :disabled="single" @click="handleAccount()" v-hasPermi="['ygb:salaryBatch:account']">确认到账</el-button>
        </el-col>
        <el-col v-if="!isReadOnlyRole" :span="1.5">
          <el-button type="success" plain icon="Promotion" :disabled="single" @click="handleSubmitBatch()" v-hasPermi="['ygb:salaryBatch:submit']">提交代发</el-button>
        </el-col>
        <el-col v-if="!isReadOnlyRole" :span="1.5">
          <el-button type="primary" plain icon="RefreshRight" :disabled="single" @click="handleMockCallback()" v-hasPermi="['ygb:salaryBatch:submit']">模拟回调</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['ygb:salaryBatch:export']">导出</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button plain icon="Bell" @click="openSalaryArrears()" v-hasPermi="['ygb:salaryBatchArrears:list']">拖欠预警</el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>
    </el-card>

    <el-card class="table-card ygb-table-card" shadow="never">
      <template #header>
        <div class="ygb-card-head ygb-card-head--between">
          <div>
            <div class="ygb-card-head__title">工资批次台账</div>
            <div class="ygb-card-head__desc">{{ focusTableHint }}</div>
          </div>
          <div class="ygb-card-head__desc">当前总量 {{ total }} 条</div>
        </div>
      </template>
      <el-table v-loading="loading" :data="visibleBatchList" @selection-change="handleSelectionChange" @row-click="handleRowClick">
        <el-table-column v-if="!isReadOnlyRole" type="selection" width="55" align="center" />
        <el-table-column label="ID" align="center" prop="batchId" width="80" />
        <el-table-column label="批次编号" align="center" prop="batchNo" width="180" />
        <el-table-column label="统计月份" align="center" prop="statMonth" width="100" />
        <el-table-column label="派遣单位" align="center" prop="dispatchEnterpriseName" min-width="220" />
        <el-table-column label="人数" align="center" prop="totalPersonCount" width="80" />
        <el-table-column label="应发合计" align="center" prop="totalPayableAmount" width="120" />
        <el-table-column label="实发合计" align="center" prop="totalPaidAmount" width="120" />
        <el-table-column label="到账金额" align="center" prop="accountReceivedAmount" width="120" />
        <el-table-column label="到账状态" align="center" prop="accountStatus" width="100">
          <template #default="scope">
            <dict-tag :options="accountStatusOptions" :value="scope.row.accountStatus" />
          </template>
        </el-table-column>
        <el-table-column label="批次状态" align="center" prop="batchStatus" width="110">
          <template #default="scope">
            <dict-tag :options="batchStatusOptions" :value="scope.row.batchStatus" />
          </template>
        </el-table-column>
        <el-table-column label="银行流水号" align="center" prop="bankSerialNo" width="180" />
        <el-table-column class-name="table-fill-column" min-width="1" />

        <el-table-column label="操作" fixed="right" align="center" :width="isReadOnlyRole ? 90 : 320" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-button link type="info" icon="View" @click.stop="openDetail(scope.row)">详情</el-button>
            <template v-if="!isReadOnlyRole">
              <el-button link type="primary" icon="Edit" @click.stop="handleUpdate(scope.row)" v-hasPermi="['ygb:salaryBatch:edit']">修改</el-button>
              <el-button link type="primary" icon="DocumentAdd" @click.stop="handleGenerate(scope.row)" v-hasPermi="['ygb:salaryBatch:generate']">生成明细</el-button>
              <el-button link type="primary" icon="Promotion" @click.stop="handleSubmitBatch(scope.row)" v-hasPermi="['ygb:salaryBatch:submit']">代发</el-button>
              <el-button link type="primary" icon="RefreshRight" @click.stop="handleMockCallback(scope.row)" v-hasPermi="['ygb:salaryBatch:submit']">回调</el-button>
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

    <el-dialog :title="title" v-model="open" width="760px" append-to-body>
      <el-form ref="salaryBatchRef" :model="form" :rules="rules" label-width="110px">
        <div class="ygb-panel-grid">
          <el-form-item label="批次编号" prop="batchNo">
            <el-input v-model="form.batchNo" placeholder="请输入批次编号" />
          </el-form-item>
          <el-form-item label="统计月份" prop="statMonth">
            <el-date-picker v-model="form.statMonth" type="month" placeholder="请选择统计月份" format="YYYY-MM" value-format="YYYY-MM" style="width: 100%" />
          </el-form-item>
          <el-form-item label="派遣单位" prop="dispatchEnterpriseId">
            <el-select v-model="form.dispatchEnterpriseId" placeholder="请选择派遣单位" filterable>
              <el-option v-for="item in enterpriseOptions" :key="item.enterpriseId" :label="item.enterpriseName" :value="item.enterpriseId" />
            </el-select>
          </el-form-item>
          <el-form-item label="批次状态" prop="batchStatus">
            <el-select v-model="form.batchStatus" placeholder="请选择批次状态">
              <el-option v-for="item in batchStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="监管账户名" prop="regulatorAccountName">
            <el-input v-model="form.regulatorAccountName" placeholder="请输入监管账户名" />
          </el-form-item>
          <el-form-item label="监管账号" prop="regulatorAccountNo">
            <el-input v-model="form.regulatorAccountNo" placeholder="请输入监管账号" />
          </el-form-item>
        </div>
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

    <el-dialog title="监管账户到账确认" v-model="accountOpen" width="520px" append-to-body>
      <el-form ref="accountRef" :model="accountForm" :rules="accountRules" label-width="110px">
        <el-form-item label="到账金额" prop="accountReceivedAmount">
          <el-input-number v-model="accountForm.accountReceivedAmount" :min="0" :precision="2" :step="1000" style="width: 100%" />
        </el-form-item>
        <el-form-item label="银行流水号" prop="bankSerialNo">
          <el-input v-model="accountForm.bankSerialNo" placeholder="请输入银行流水号" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitAccount">确定</el-button>
          <el-button @click="accountOpen = false">取消</el-button>
        </div>
      </template>
    </el-dialog>

    <page-detail-dialog v-model="detailOpen" title="工资批次详情" width="760px">
      <template v-if="detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="批次编号">{{ detail.batchNo }}</el-descriptions-item>
          <el-descriptions-item label="统计月份">{{ detail.statMonth }}</el-descriptions-item>
          <el-descriptions-item label="派遣单位">{{ detail.dispatchEnterpriseName || "-" }}</el-descriptions-item>
          <el-descriptions-item label="人数">{{ valueOrDefault(detail.totalPersonCount, 0) }} 人</el-descriptions-item>
          <el-descriptions-item label="批次状态">
            <dict-tag :options="batchStatusOptions" :value="detail.batchStatus" />
          </el-descriptions-item>
          <el-descriptions-item label="到账状态">
            <dict-tag :options="accountStatusOptions" :value="detail.accountStatus" />
          </el-descriptions-item>
          <el-descriptions-item label="应发合计">{{ formatMoney(detail.totalPayableAmount) }}</el-descriptions-item>
          <el-descriptions-item label="实发合计">{{ formatMoney(detail.totalPaidAmount) }}</el-descriptions-item>
          <el-descriptions-item label="到账金额">{{ formatMoney(detail.accountReceivedAmount) }}</el-descriptions-item>
          <el-descriptions-item label="监管账户名">{{ detail.regulatorAccountName || "-" }}</el-descriptions-item>
          <el-descriptions-item label="监管账号" :span="2">{{ detail.regulatorAccountNo || "-" }}</el-descriptions-item>
          <el-descriptions-item label="银行流水号" :span="2">{{ detail.bankSerialNo || "-" }}</el-descriptions-item>
          <el-descriptions-item label="提交时间">{{ parseTime(detail.submitTime, "{y}-{m}-{d} {h}:{i}:{s}") || "-" }}</el-descriptions-item>
          <el-descriptions-item label="发放时间">{{ parseTime(detail.paidTime, "{y}-{m}-{d} {h}:{i}:{s}") || "-" }}</el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ detail.remark || "-" }}</el-descriptions-item>
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

<script setup name="YgbSalaryBatch">
import { useRoute, useRouter } from 'vue-router'
import { useWorkbenchAssist } from '@/composables/useWorkbenchAssist'
import {
  listSalaryBatch,
  getSalaryBatch,
  addSalaryBatch,
  updateSalaryBatch,
  delSalaryBatch,
  generateSalaryDetail,
  confirmSalaryAccount,
  submitSalaryBatch,
  simulateSalaryBatchCallback,
  getSalaryBatchSummary
} from "@/api/ygb/salaryBatch"
import { optionselectEnterprise } from "@/api/ygb/enterprise"
import { decoratePortalExplanationItems, openPortalExplanationAction } from '@/utils/portalExplanation'
import { useRoleViewMode } from "@/utils/roleView"
import { applyWorkbenchRouteQuery, buildWorkbenchContext, stripWorkbenchRouteQuery } from '@/utils/workbenchLink'

const { proxy } = getCurrentInstance()
const route = useRoute()
const { setPageGuide } = useWorkbenchAssist()
const router = useRouter()
const { isReadOnlyRole, readOnlyRoleLabel, readOnlyRoleDescription } = useRoleViewMode()
const salaryBatchWorkbenchFields = ['dispatchEnterpriseId']

const batchStatusOptions = [
  { label: "草稿", value: "0" },
  { label: "待确认", value: "1" },
  { label: "待入账", value: "2" },
  { label: "待生成明细", value: "3" },
  { label: "待代发", value: "4" },
  { label: "代发中", value: "5" },
  { label: "已发放", value: "6" },
  { label: "发放失败", value: "7" }
]

const accountStatusOptions = [
  { label: "未到账", value: "0" },
  { label: "已到账", value: "1" }
]

const salaryBatchList = ref([])
const enterpriseOptions = ref([])
const summaryData = ref({})
const currentBatch = ref(undefined)
const detail = ref(undefined)
const open = ref(false)
const accountOpen = ref(false)
const detailOpen = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const batchNos = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")
const currentBatchId = ref(undefined)
const activeFocusKey = ref("")

const data = reactive({
  form: {},
  accountForm: {
    accountReceivedAmount: undefined,
    bankSerialNo: undefined
  },
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    batchNo: undefined,
    statMonth: undefined,
    dispatchEnterpriseId: undefined,
    batchStatus: undefined,
    accountStatus: undefined
  },
  rules: {
    batchNo: [{ required: true, message: "批次编号不能为空", trigger: "blur" }],
    statMonth: [{ required: true, message: "统计月份不能为空", trigger: "change" }],
    dispatchEnterpriseId: [{ required: true, message: "派遣单位不能为空", trigger: "change" }]
  },
  accountRules: {
    accountReceivedAmount: [{ required: true, message: "到账金额不能为空", trigger: "blur" }],
    bankSerialNo: [{ required: true, message: "银行流水号不能为空", trigger: "blur" }]
  }
})

const { queryParams, form, rules, accountForm, accountRules } = toRefs(data)
const portalExplanations = computed(() => summaryData.value.ygbExplanation || [])
const portalExplanationItems = computed(() => decoratePortalExplanationItems(portalExplanations.value, {
  portalCode: 'ygb',
  panelTitle: '530.1 办理链解释',
  panelDescription: '批次办理堵点、到账状态和代发建议统一来自门户解释聚合接口。'
}))

function handlePortalExplanationAction(action) {
  openPortalExplanationAction(router, action)
}

const workbenchContext = computed(() => buildWorkbenchContext(route.query, {
  fields: salaryBatchWorkbenchFields,
  sourceLabel: '企业工作台',
  title: '当前工资批次台账沿用了工作台来源条件',
  description: '当前列表已按派遣单位预设筛选，可以直接继续到账确认、明细生成和代发推进。',
  fieldLabels: {
    dispatchEnterpriseId: '娲鹃仯鍗曚綅'
  },
  fieldFormatters: {
    dispatchEnterpriseId: value => {
      const matched = enterpriseOptions.value.find(item => String(item.enterpriseId) === String(value))
      return matched ? matched.enterpriseName : value
    }
  }
}))

const summaryCards = computed(() => ([
  {
    key: "total",
    label: "工资批次",
    value: summaryData.value.totalCount != null ? summaryData.value.totalCount : total.value,
    unit: "批",
    note: "当前筛选条件下可继续推进的工资月份批次数量。",
    cardClass: ""
  },
  {
    key: "generate",
    label: "待生成明细",
    value: valueOrDefault(summaryData.value.pendingGenerateCount, 0),
    unit: "批",
    note: "已具备发薪前提但尚未生成工资明细的批次，应优先补齐。",
    cardClass: "ygb-summary-card--warning"
  },
  {
    key: "submit",
    label: "待提交代发",
    value: valueOrDefault(summaryData.value.pendingSubmitCount, 0),
    unit: "批",
    note: "明细已生成但尚未提交银行代发的批次，可直接进入发放动作。",
    cardClass: "ygb-summary-card--primary"
  },
  {
    key: "people",
    label: "覆盖人数",
    value: valueOrDefault(summaryData.value.totalPersonCount, 0),
    unit: "人",
    note: "当前筛选条件下工资批次累计覆盖人数，用于快速判断影响面。",
    cardClass: "ygb-summary-card--success"
  }
]))

const salaryOverviewCards = computed(() => ([
  {
    key: "regulatorAccountBalance",
    label: "监管账户余额",
    value: formatMoney(summaryData.value.regulatorAccountBalance),
    unit: "元",
    note: "按当前筛选口径汇总已到账与已实发差额。",
    cardClass: "ygb-summary-card--primary"
  },
  {
    key: "accountGapAmount",
    label: "到账缺口",
    value: formatMoney(summaryData.value.accountGapAmount),
    unit: "元",
    note: "应发与到账之间仍存在缺口的金额总量。",
    cardClass: "ygb-summary-card--warning"
  },
  {
    key: "failedBatchCount",
    label: "代发失败批次",
    value: valueOrDefault(summaryData.value.failedBatchCount, 0),
    unit: "批",
    note: "需优先回看银行回调与失败原因。",
    cardClass: "ygb-summary-card--warning"
  },
  {
    key: "arrearsEnterpriseCount",
    label: "拖欠企业数",
    value: valueOrDefault(summaryData.value.arrearsEnterpriseCount, 0),
    unit: "家",
    note: "点击入口可进入拖欠预警台账继续处置。",
    cardClass: "ygb-summary-card--success"
  }
]))

const focusQueues = computed(() => {
  const rows = salaryBatchList.value || []
  return [
    {
      key: "awaitingAccount",
      title: "未到账批次优先补齐",
      desc: "先确认监管账户到账金额和银行流水，避免未到账批次继续向明细和代发环节推进。",
      count: summaryData.value.awaitingAccountCount != null ? summaryData.value.awaitingAccountCount : rows.filter(item => item.accountStatus !== "1").length,
      unit: "批",
      actionText: rows.some(item => item.accountStatus !== "1") ? "立即补到账" : "到账稳定"
    },
    {
      key: "pendingGenerate",
      title: "待生成明细批次",
      desc: "到账已确认但明细未生成的批次应优先处理，避免月底集中堆积。",
      count: summaryData.value.pendingGenerateCount != null ? summaryData.value.pendingGenerateCount : rows.filter(item => item.batchStatus === "3").length,
      unit: "批",
      actionText: rows.some(item => item.batchStatus === "3") ? "生成明细" : "明细齐备"
    },
    {
      key: "pendingSubmit",
      title: "待代发提交批次",
      desc: "明细已齐但尚未提交银行代发的批次，适合财务集中推进发薪。",
      count: summaryData.value.pendingSubmitCount != null ? summaryData.value.pendingSubmitCount : rows.filter(item => item.batchStatus === "4").length,
      unit: "批",
      actionText: rows.some(item => item.batchStatus === "4") ? "提交代发" : "提交平稳"
    },
    {
      key: "payingOrFailed",
      title: "代发中与失败批次",
      desc: "重点关注银行回写结果，及时识别长时间代发中或已失败的批次。",
      count: valueOrDefault(summaryData.value.payingCount, 0) + valueOrDefault(summaryData.value.failedCount, 0),
      unit: "批",
      actionText: rows.some(item => item.batchStatus === "5" || item.batchStatus === "7") ? "回看回调" : "回调稳定"
    }
  ]
})

const activeFocus = computed(() => focusQueues.value.find(item => item.key === activeFocusKey.value) || focusQueues.value[0])

const visibleBatchList = computed(() => prioritizeFocusRows(salaryBatchList.value, row => matchBatchFocus(row, activeFocus.value?.key)))

const focusTableHint = computed(() => {
  if (!activeFocus.value) {
    return "当前按查询条件展示工资批次台账。"
  }
  return `${activeFocus.value.title}，系统已将对应批次前置，便于优先处理当前办理堵点。`
})

const selectedBatchOverview = computed(() => {
  if (!currentBatch.value) {
    return [
      { label: "批次编号", value: "-" },
      { label: "批次状态", value: "-" },
      { label: "到账状态", value: "-" },
      { label: "人数/金额", value: "-" }
    ]
  }
  return [
    { label: "批次编号", value: currentBatch.value.batchNo || "-" },
    { label: "批次状态", value: batchStatusLabel(currentBatch.value.batchStatus) },
    { label: "到账状态", value: accountStatusLabel(currentBatch.value.accountStatus) },
    {
      label: "人数/金额",
      value: `${valueOrDefault(currentBatch.value.totalPersonCount, 0)} 人 / ${formatMoney(currentBatch.value.totalPayableAmount)}`
    }
  ]
})

const primaryBatchAction = computed(() => {
  if (!currentBatch.value) {
    return { label: "选择待办对象", action: "detail" }
  }
  if (isReadOnlyRole.value) {
    return { label: "查看详情", action: "detail" }
  }
  if (currentBatch.value.accountStatus !== "1") {
    return { label: "确认到账", action: "account" }
  }
  if (currentBatch.value.batchStatus === "3") {
    return { label: "生成明细", action: "generate" }
  }
  if (currentBatch.value.batchStatus === "4") {
    return { label: "提交代发", action: "submit" }
  }
  if (currentBatch.value.batchStatus === "5" || currentBatch.value.batchStatus === "7") {
    return { label: "模拟回调", action: "callback" }
  }
  return { label: "查看详情", action: "detail" }
})

const currentBatchActionSummary = computed(() => {
  if (!currentBatch.value) {
    return "先从左侧焦点队列选择重点批次，再联动查看当前办理建议。"
  }
  if (currentBatch.value.accountStatus !== "1") {
    return "该批次尚未确认到账，应先补录到账金额和银行流水，避免后续明细和代发口径失真。"
  }
  if (currentBatch.value.batchStatus === "3") {
    return "该批次已具备发薪前提，但尚未生成工资明细，建议优先按当前考勤归集结果生成明细。"
  }
  if (currentBatch.value.batchStatus === "4") {
    return "该批次明细已准备完成，可由财务直接提交银行代发，缩短发薪闭环时间。"
  }
  if (currentBatch.value.batchStatus === "5") {
    return "该批次已处于代发中，建议持续关注银行回调结果和失败明细。"
  }
  if (currentBatch.value.batchStatus === "7") {
    return "该批次代发失败，建议优先核对失败原因、流水号和批次明细后重新推进闭环。"
  }
  if (currentBatch.value.batchStatus === "6") {
    return "该批次已完成发放，可继续用于财务对账、月报沉淀和工资监管归档。"
  }
  return "该批次基础信息已齐备，可按监管到账、明细生成和代发流程逐步推进。"
})

const currentBatchActionTags = computed(() => buildHintTags(currentBatch.value))
const workflowSteps = computed(() => ([
  {
    label: "创建月度批次",
    desc: "先确认统计月份、派遣单位和监管账户信息，确保工资批次具备完整承接主体。"
  },
  {
    label: "确认监管到账",
    desc: "确认到账金额和银行流水，避免未到账或到账不足情况下直接推进工资明细和代发。"
  },
  {
    label: "生成工资明细",
    desc: "以月度考勤归集为基础生成工资明细，明确人数、应发和实发金额。"
  },
  {
    label: "提交银行代发",
    desc: "批次达到待代发状态后提交银行接口或 Stub 通道，进入统一回写链路。"
  },
  {
    label: "回调闭环归档",
    desc: "回写成功、失败或部分失败结果后，及时留存流水和状态，支撑财务与监管后续对账。"
  }
]))

const batchHintTags = computed(() => buildHintTags(currentBatch.value))
const detailHintTags = computed(() => buildHintTags(detail.value))

function getList() {
  loading.value = true
  Promise.all([
    listSalaryBatch(queryParams.value),
    getSalaryBatchSummary(buildSummaryQuery())
  ]).then(([listResponse, summaryResponse]) => {
    salaryBatchList.value = listResponse.rows || []
    total.value = listResponse.total || 0
    summaryData.value = summaryResponse.data || {}
    syncCurrentBatch()
    loading.value = false
  }).catch(() => {
    loading.value = false
  })
}

function buildSummaryQuery() {
  return {
    batchNo: queryParams.value.batchNo,
    statMonth: queryParams.value.statMonth,
    dispatchEnterpriseId: queryParams.value.dispatchEnterpriseId,
    batchStatus: queryParams.value.batchStatus,
    accountStatus: queryParams.value.accountStatus
  }
}

function syncCurrentBatch() {
  if (currentBatch.value) {
    const matched = visibleBatchList.value.find(item => item.batchId === currentBatch.value.batchId)
    if (matched) {
      currentBatch.value = matched
      return
    }
  }
  currentBatch.value = visibleBatchList.value.length > 0 ? visibleBatchList.value[0] : undefined
}

function loadEnterpriseOptions() {
  optionselectEnterprise().then(response => {
    enterpriseOptions.value = response.data || []
  })
}

function reset() {
  form.value = {
    batchId: undefined,
    batchNo: undefined,
    statMonth: undefined,
    dispatchEnterpriseId: undefined,
    batchStatus: "1",
    accountStatus: "0",
    regulatorAccountName: "粤工保监管账户",
    regulatorAccountNo: undefined,
    remark: undefined
  }
  proxy.resetForm("salaryBatchRef")
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
    batchNo: undefined,
    statMonth: undefined,
    dispatchEnterpriseId: undefined,
    batchStatus: undefined,
    accountStatus: undefined
  })
  applyWorkbenchRouteQuery(route.query, queryParams.value, salaryBatchWorkbenchFields)
  getList()

watchEffect(() => {
  setPageGuide({
    title: '????????' || '????????',
    description: '?????????????????????????????????' || '?????????????????????????????????',
    portalExplanation: portalExplanationItems.value,
    focus: focusQueues.value,
    selection: [...selectedBatchOverview.value, { label: '??????', value: currentBatchActionSummary.value }],
    workflow: workflowSteps.value,
    hints: [...currentBatchActionTags.value].slice(0, 6)
  })
})

}

function clearWorkbenchContext() {
  Object.assign(queryParams.value, {
    pageNum: 1,
    dispatchEnterpriseId: undefined
  })
  router.replace({
    path: route.path,
    query: stripWorkbenchRouteQuery(route.query, salaryBatchWorkbenchFields)
  })
  getList()
}

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.batchId)
  batchNos.value = selection.map(item => item.batchNo)
  single.value = selection.length !== 1
  multiple.value = !selection.length
}

function handleRowClick(row) {
  currentBatch.value = row
}

function handleSelectFocus(item) {
  activeFocusKey.value = item.key
  syncCurrentBatch()
}

function handleAdd() {
  if (blockReadOnlyAction("新增工资批次")) {
    return
  }
  reset()
  open.value = true
  title.value = "新增工资批次"
}

function handleUpdate(row) {
  if (blockReadOnlyAction("修改工资批次")) {
    return
  }
  reset()
  const batchId = row?.batchId || ids.value[0]
  if (!batchId) {
    return
  }
  getSalaryBatch(batchId).then(response => {
    form.value = response.data
    currentBatch.value = response.data || currentBatch.value
    open.value = true
    title.value = "修改工资批次"
  })
}

function openDetail(row) {
  if (!row?.batchId) {
    return
  }
  currentBatch.value = row
  getSalaryBatch(row.batchId).then(response => {
    detail.value = response.data
    detailOpen.value = true
  })
}

function submitForm() {
  proxy.$refs.salaryBatchRef.validate(valid => {
    if (!valid) {
      return
    }
    if (form.value.batchId != undefined) {
      updateSalaryBatch(form.value).then(() => {
        proxy.$modal.msgSuccess("修改成功")
        open.value = false
        getList()
      })
      return
    }
    addSalaryBatch(form.value).then(() => {
      proxy.$modal.msgSuccess("新增成功")
      open.value = false
      getList()
    })
  })
}

function handleDelete(row) {
  if (blockReadOnlyAction("删除工资批次")) {
    return
  }
  const batchIds = row?.batchId || ids.value
  const names = row?.batchNo || batchNos.value.join("、")
  proxy.$modal.confirm(`是否确认删除工资批次“${names}”的数据项？`).then(function() {
    return delSalaryBatch(batchIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

function handleGenerate(row) {
  if (blockReadOnlyAction("生成工资明细")) {
    return
  }
  const batchId = row?.batchId || ids.value[0]
  if (!batchId) {
    return
  }
  currentBatch.value = row || currentBatch.value
  proxy.$modal.confirm("是否确认根据考勤归集生成工资明细？").then(function() {
    return generateSalaryDetail(batchId)
  }).then(response => {
    proxy.$modal.msgSuccess(response.msg || "生成成功")
    getList()
    refreshDetailIfMatched(batchId)
  }).catch(() => {})
}

function handleAccount(row) {
  if (blockReadOnlyAction("确认监管到账")) {
    return
  }
  currentBatchId.value = row?.batchId || ids.value[0]
  if (!currentBatchId.value) {
    return
  }
  currentBatch.value = row || currentBatch.value
  accountForm.value = {
    accountReceivedAmount: undefined,
    bankSerialNo: undefined
  }
  accountOpen.value = true
  nextTick(() => proxy.resetForm("accountRef"))
}

function submitAccount() {
  proxy.$refs.accountRef.validate(valid => {
    if (!valid) {
      return
    }
    confirmSalaryAccount(currentBatchId.value, accountForm.value).then(() => {
      proxy.$modal.msgSuccess("到账确认成功")
      accountOpen.value = false
      getList()
      refreshDetailIfMatched(currentBatchId.value)
    })
  })
}

function handleSubmitBatch(row) {
  if (blockReadOnlyAction("提交银行代发")) {
    return
  }
  const batchId = row?.batchId || ids.value[0]
  if (!batchId) {
    return
  }
  currentBatch.value = row || currentBatch.value
  proxy.$modal.confirm("是否确认提交银行代发？").then(function() {
    return submitSalaryBatch(batchId)
  }).then(response => {
    proxy.$modal.msgSuccess(response.msg || "提交成功")
    getList()
    refreshDetailIfMatched(batchId)
  }).catch(() => {})
}

function handleMockCallback(row) {
  if (blockReadOnlyAction("触发模拟回调")) {
    return
  }
  const batchId = row?.batchId || currentBatch.value?.batchId
  const batchNo = row?.batchNo || batchNos.value[0] || currentBatch.value?.batchNo
  if (!batchNo) {
    return
  }
  currentBatch.value = row || currentBatch.value
  proxy.$modal.confirm("是否按当前批次生成 Stub 银行回调？").then(function() {
    return simulateSalaryBatchCallback({ batchNo })
  }).then(response => {
    proxy.$modal.msgSuccess(response.msg || "银行回调处理成功")
    getList()
    if (batchId) {
      refreshDetailIfMatched(batchId)
    }
  }).catch(() => {})
}

function handlePrimaryBatchAction() {
  if (!currentBatch.value) {
    return
  }
  if (primaryBatchAction.value.action === "account") {
    handleAccount(currentBatch.value)
    return
  }
  if (primaryBatchAction.value.action === "generate") {
    handleGenerate(currentBatch.value)
    return
  }
  if (primaryBatchAction.value.action === "submit") {
    handleSubmitBatch(currentBatch.value)
    return
  }
  if (primaryBatchAction.value.action === "callback") {
    handleMockCallback(currentBatch.value)
    return
  }
  openDetail(currentBatch.value)
}

function openSalaryArrears(batch) {
  const target = batch || currentBatch.value
  const query = {
    statMonth: target?.statMonth || queryParams.value.statMonth,
    dispatchEnterpriseId: target?.dispatchEnterpriseId || queryParams.value.dispatchEnterpriseId,
    regionCode: target?.regionCode || undefined
  }
  if (target?.batchStatus === "7") {
    query.handleStatus = "processing"
  }
  router.push({
    path: "/ygb/salaryArrears",
    query
  })
}

function refreshDetailIfMatched(batchId) {
  if (detailOpen.value && detail.value && detail.value.batchId === batchId) {
    getSalaryBatch(batchId).then(response => {
      detail.value = response.data
    })
  }
}

function handleExport() {
  proxy.download("ygb/salary/batch/export", {
    ...queryParams.value
  }, `salary_batch_${new Date().getTime()}.xlsx`)
}

function buildHintTags(batch) {
  if (!batch) {
    return [{ label: "未选中工资批次，可先在列表中选择待办理对象", type: "info" }]
  }
  const tags = []
  if (batch.accountStatus !== "1") {
    tags.push({ label: "当前未到账，建议先确认监管账户到账和银行流水", type: "warning" })
  }
  if (batch.batchStatus === "3") {
    tags.push({ label: "当前待生成明细，可直接根据考勤归集生成工资明细", type: "warning" })
  }
  if (batch.batchStatus === "4") {
    tags.push({ label: "当前待代发，建议复核金额后立即提交银行发放", type: "primary" })
  }
  if (batch.batchStatus === "5") {
    tags.push({ label: "当前代发中，建议持续关注银行回写结果和失败明细", type: "info" })
  }
  if (batch.batchStatus === "6") {
    tags.push({ label: "当前已发放，可继续做对账归档和月报沉淀", type: "success" })
  }
  if (batch.batchStatus === "7") {
    tags.push({ label: "当前发放失败，建议优先核对银行回调和失败原因", type: "danger" })
  }
  if (!batch.bankSerialNo && (batch.batchStatus === "5" || batch.batchStatus === "6" || batch.batchStatus === "7")) {
    tags.push({ label: "代发链路缺少银行流水号，建议补齐留痕", type: "warning" })
  }
  if (!tags.length) {
    tags.push({ label: "当前批次基础信息完整，可继续沿工资监管流程推进", type: "success" })
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

function matchBatchFocus(row, focusKey) {
  if (!row || !focusKey) {
    return false
  }
  if (focusKey === "awaitingAccount") {
    return row.accountStatus !== "1"
  }
  if (focusKey === "pendingGenerate") {
    return row.batchStatus === "3"
  }
  if (focusKey === "pendingSubmit") {
    return row.batchStatus === "4"
  }
  if (focusKey === "payingOrFailed") {
    return row.batchStatus === "5" || row.batchStatus === "7"
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

function batchStatusLabel(value) {
  const matched = batchStatusOptions.find(item => item.value === value)
  return matched ? matched.label : "-"
}

function accountStatusLabel(value) {
  const matched = accountStatusOptions.find(item => item.value === value)
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

watch(focusQueues, queues => {
  if (!queues.length) {
    activeFocusKey.value = ""
    return
  }
  if (!queues.find(item => item.key === activeFocusKey.value)) {
    activeFocusKey.value = queues[0].key
  }
}, { immediate: true })

applyWorkbenchRouteQuery(route.query, queryParams.value, salaryBatchWorkbenchFields)
loadEnterpriseOptions()
reset()
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
  .ygb-analysis-grid,
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
