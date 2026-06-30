<template>
  <div class="app-container ygb-page">
    <section class="gov-page-header ygb-page__header">
      <div>
        <p class="ygb-page__eyebrow">工资监管</p>
        <h1 class="ygb-page__title">银行代发结果监控</h1>
        <p class="ygb-page__desc">
          监控工资银行代发结果与回调状态，跟踪每笔代发交易的最终到账结果。
        </p>
      </div>
    </section>

    <div class="ygb-summary-grid">
      <div
        v-for="item in summaryCards"
        :key="item.key"
        class="ygb-summary-card"
        :class="item.cardClass"
      >
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
        <el-form-item label="批次编号" prop="batchNo">
          <el-input
            v-model="queryParams.batchNo"
            placeholder="请输入批次编号"
            clearable
            style="width: 200px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="统计月份" prop="statMonth">
          <el-date-picker
            v-model="queryParams.statMonth"
            type="month"
            placeholder="请选择统计月份"
            format="YYYY-MM"
            value-format="YYYY-MM"
            style="width: 160px"
          />
        </el-form-item>
        <el-form-item label="企业" prop="dispatchEnterpriseId">
          <el-select
            v-model="queryParams.dispatchEnterpriseId"
            placeholder="请选择企业"
            clearable
            filterable
            style="width: 200px"
          >
            <el-option
              v-for="item in enterpriseOptions"
              :key="item.enterpriseId"
              :label="item.enterpriseName"
              :value="item.enterpriseId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="代发状态" prop="batchStatus">
          <el-select
            v-model="queryParams.batchStatus"
            placeholder="请选择代发状态"
            clearable
            style="width: 160px"
          >
            <el-option
              v-for="item in distributionStatusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
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
        <el-col :span="1.5">
          <el-button type="success" plain icon="Promotion" :disabled="!currentRow" @click="openBatchPage()">
            批次办理
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="primary"
            plain
            icon="RefreshRight"
            :disabled="!canCallbackBatch(currentRow)"
            @click="handleBankCallback()"
            v-hasPermi="['ygb:salaryBatch:submit']"
          >
            回写结果
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['ygb:salaryBatch:export']">
            导出
          </el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>
    </el-card>

    <el-card class="table-card ygb-table-card" shadow="never">
      <template #header>
        <div class="ygb-card-head ygb-card-head--between">
          <div>
            <div class="ygb-card-head__title">银行代发结果监控台账</div>
            <div class="ygb-card-head__desc">
              跟踪银行代发回写结果、失败原因和批次状态，支撑发放结果核查。
            </div>
          </div>
          <div class="ygb-card-head__desc">当前总量 {{ total }} 条</div>
        </div>
      </template>
      <el-table
        v-loading="loading"
        :data="list"
        highlight-current-row
        @current-change="handleCurrentChange"
        @row-click="handleRowClick"
      >
        <el-table-column label="ID" align="center" prop="batchId" width="80" />
        <el-table-column label="批次编号" align="center" prop="batchNo" width="180" />
        <el-table-column label="统计月份" align="center" prop="statMonth" width="100" />
        <el-table-column label="派遣单位" align="center" prop="dispatchEnterpriseName" min-width="200" />
        <el-table-column label="实发合计" align="center" width="120">
          <template #default="scope">
            <span>{{ formatMoney(scope.row.totalPaidAmount) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="银行流水号" align="center" prop="bankSerialNo" width="180">
          <template #default="scope">
            <span>{{ scope.row.bankSerialNo || "-" }}</span>
          </template>
        </el-table-column>
        <el-table-column label="代发状态" align="center" prop="batchStatus" width="140">
          <template #default="scope">
            <dict-tag :options="distributionStatusOptions" :value="normalizeStatusValue(scope.row.batchStatus)" />
          </template>
        </el-table-column>
        <el-table-column label="发放时间" align="center" width="170">
          <template #default="scope">
            <span>{{ parseTime(scope.row.paidTime, "{y}-{m}-{d} {h}:{i}:{s}") }}</span>
          </template>
        </el-table-column>
        <el-table-column class-name="table-fill-column" min-width="1" />
        <el-table-column label="操作" fixed="right" align="center" width="220" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-button link type="info" icon="View" @click.stop="openDetail(scope.row)">详情</el-button>
            <el-button
              link
              type="primary"
              icon="RefreshRight"
              :disabled="!canCallbackBatch(scope.row)"
              @click.stop="handleBankCallback(scope.row)"
              v-hasPermi="['ygb:salaryBatch:submit']"
            >
              回写
            </el-button>
            <el-button link type="primary" icon="Tickets" @click.stop="openBatchPage(scope.row)">批次</el-button>
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

    <page-detail-dialog v-model="detailOpen" title="银行代发结果监控详情" width="720px">
      <template v-if="detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="批次编号" :span="1">{{ detail.batchNo || "-" }}</el-descriptions-item>
          <el-descriptions-item label="统计月份" :span="1">{{ detail.statMonth || "-" }}</el-descriptions-item>
          <el-descriptions-item label="派遣单位" :span="1">{{ detail.dispatchEnterpriseName || "-" }}</el-descriptions-item>
          <el-descriptions-item label="实发合计" :span="1">{{ formatMoney(detail.totalPaidAmount) }}</el-descriptions-item>
          <el-descriptions-item label="银行流水号" :span="1">{{ detail.bankSerialNo || "-" }}</el-descriptions-item>
          <el-descriptions-item label="代发状态" :span="1">{{ formatDistributionStatus(detail.batchStatus) }}</el-descriptions-item>
          <el-descriptions-item label="发放时间" :span="1">
            {{ parseTime(detail.paidTime, "{y}-{m}-{d} {h}:{i}:{s}") || "-" }}
          </el-descriptions-item>
          <el-descriptions-item label="到账状态" :span="1">{{ formatAccountStatus(detail.accountStatus) }}</el-descriptions-item>
        </el-descriptions>
      </template>
    </page-detail-dialog>
  </div>
</template>

<script setup name="YgbSalaryBankDistribution">
import { getCurrentInstance, onMounted, ref } from "vue"
import { useRouter } from "vue-router"
import { optionselectEnterprise } from "@/api/ygb/enterprise"
import { listSalaryBank, getSalaryBankSummary } from "@/api/ygb/salaryBank"
import { handleSalaryBatchCallback } from "@/api/ygb/salaryBatch"
import { parseTime } from "@/utils/yuegongbao"
import { resolveRouteAliasPath } from "@/utils/routeAlias"

const queryRef = ref()
const loading = ref(false)
const list = ref([])
const total = ref(0)
const showSearch = ref(true)
const detailOpen = ref(false)
const detail = ref(null)
const enterpriseOptions = ref([])
const currentRow = ref(null)
const summaryCards = ref([])

const { proxy } = getCurrentInstance()
const router = useRouter()

const distributionStatusOptions = [
  { label: "草稿", value: "0" },
  { label: "待到账确认", value: "1" },
  { label: "待入账", value: "2" },
  { label: "已到账待生成明细", value: "3" },
  { label: "明细已生成待代发", value: "4" },
  { label: "已提交银行", value: "5" },
  { label: "发放成功", value: "6" },
  { label: "发放失败", value: "7" },
]

const accountStatusOptions = [
  { label: "未到账", value: "0" },
  { label: "已到账", value: "1" },
]

const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  batchNo: undefined,
  statMonth: undefined,
  dispatchEnterpriseId: undefined,
  batchStatus: undefined,
})

function formatMoney(value) {
  if (value === undefined || value === null || value === "") return "-"
  return "¥ " + Number(value).toLocaleString("zh-CN", { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

function valueOrDefault(value, fallback = 0) {
  return value === undefined || value === null ? fallback : value
}

function normalizeStatusValue(value) {
  return value === undefined || value === null || value === "" ? "" : String(value)
}

function formatDistributionStatus(value) {
  const normalizedValue = normalizeStatusValue(value)
  return distributionStatusOptions.find((item) => item.value === normalizedValue)?.label || normalizedValue || "-"
}

function formatAccountStatus(value) {
  const normalizedValue = normalizeStatusValue(value)
  return accountStatusOptions.find((item) => item.value === normalizedValue)?.label || normalizedValue || "-"
}

function canCallbackBatch(row) {
  const batchStatus = normalizeStatusValue(row?.batchStatus)
  return !!row && (batchStatus === "5" || batchStatus === "7")
}

async function getList() {
  loading.value = true
  try {
    const res = await listSalaryBank(queryParams.value)
    list.value = res.rows || res.data || []
    total.value = res.total || 0
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

async function getSummary() {
  try {
    const res = await getSalaryBankSummary(queryParams.value)
    const data = res.data || res || {}
    summaryCards.value = [
      { key: "totalCount", label: "代发批次", value: valueOrDefault(data.totalCount, 0), unit: "笔", note: "当前范围", cardClass: "" },
      { key: "paidCount", label: "成功", value: valueOrDefault(data.paidCount, 0), unit: "笔", note: "已发放", cardClass: "success" },
      { key: "failedCount", label: "失败", value: valueOrDefault(data.failedCount, 0), unit: "笔", note: "需处理", cardClass: "danger" },
      { key: "payingCount", label: "处理中", value: valueOrDefault(data.payingCount, 0), unit: "笔", note: "等待回调", cardClass: "warning" },
    ]
  } catch (error) {
    console.error(error)
  }
}

function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
  getSummary()
}

function resetQuery() {
  queryRef.value?.resetFields?.()
  queryParams.value.pageNum = 1
  queryParams.value.pageSize = 10
  getList()
  getSummary()
}

function handleCurrentChange(row) {
  currentRow.value = row || currentRow.value
}

function handleRowClick(row) {
  currentRow.value = row
}

function openDetail(row) {
  currentRow.value = row
  detail.value = row
  detailOpen.value = true
}

function handleBankCallback(row) {
  currentRow.value = row || currentRow.value
  if (!currentRow.value?.batchNo) {
    proxy.$modal.msgWarning("请先选择需要回写代发结果的工资批次")
    return
  }
  if (!canCallbackBatch(currentRow.value)) {
    proxy.$modal.msgWarning("只有“已提交银行”或“发放失败”的批次才能回写银行代发结果")
    return
  }
  proxy.$modal
    .confirm(`是否回写工资批次“${currentRow.value.batchNo}”的银行代发结果？`)
    .then(() => handleSalaryBatchCallback({ batchNo: currentRow.value.batchNo }))
    .then((response) => {
      proxy.$modal.msgSuccess(response.msg || "代发结果回写成功")
      getList()
      getSummary()
    })
    .catch(() => {})
}

function openBatchPage(row) {
  const target = row || currentRow.value
  router.push({
    path: resolveRouteAliasPath("/salary-supervision/payment"),
    query: {
      batchNo: target?.batchNo ?? queryParams.value.batchNo,
      statMonth: target?.statMonth ?? queryParams.value.statMonth,
      dispatchEnterpriseId: target?.dispatchEnterpriseId ?? queryParams.value.dispatchEnterpriseId,
      batchStatus: normalizeStatusValue(target?.batchStatus) || queryParams.value.batchStatus,
    },
  })
}

function handleExport() {
  proxy.download("ygb/salary/bank/export", { ...queryParams.value }, `salary_bank_distribution_${Date.now()}.xlsx`)
}

async function loadEnterpriseOptions() {
  try {
    const res = await optionselectEnterprise()
    enterpriseOptions.value = res.data || []
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  loadEnterpriseOptions()
  getList()
  getSummary()
})
</script>
