<template>
  <div class="app-container ygb-page">
    <section class="gov-page-header ygb-page__header">
      <div>
        <p class="ygb-page__eyebrow">工资监管</p>
        <h1 class="ygb-page__title">工资发放监控</h1>
        <p class="ygb-page__desc">监控工资批次从创建、到账确认到银行代发的全流程状态，确保工资按时足额发放。</p>
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
        <el-form-item label="批次编号" prop="batchNo">
          <el-input v-model="queryParams.batchNo" placeholder="请输入批次编号" clearable style="width: 200px" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="统计月份" prop="statMonth">
          <el-date-picker v-model="queryParams.statMonth" type="month" placeholder="请选择统计月份" format="YYYY-MM" value-format="YYYY-MM" style="width: 160px" />
        </el-form-item>
        <el-form-item label="企业" prop="dispatchEnterpriseId">
          <el-select v-model="queryParams.dispatchEnterpriseId" placeholder="请选择企业" clearable filterable style="width: 200px">
            <el-option v-for="item in enterpriseOptions" :key="item.enterpriseId" :label="item.enterpriseName" :value="item.enterpriseId" />
          </el-select>
        </el-form-item>
        <el-form-item label="批次状态" prop="batchStatus">
          <el-select v-model="queryParams.batchStatus" placeholder="请选择批次状态" clearable style="width: 140px">
            <el-option v-for="item in batchStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
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
          <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['ygb:salaryBatch:export']">导出</el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>
    </el-card>

    <el-card class="table-card ygb-table-card" shadow="never">
      <template #header>
        <div class="ygb-card-head ygb-card-head--between">
          <div>
            <div class="ygb-card-head__title">工资发放监控台账</div>
            <div class="ygb-card-head__desc">跟踪工资批次、发放状态、支付金额和未完成清单，支撑工资监管闭环。</div>
          </div>
          <div class="ygb-card-head__desc">当前总量 {{ total }} 条</div>
        </div>
      </template>
      <el-table v-loading="loading" :data="list" @selection-change="handleSelectionChange">
        <el-table-column label="ID" align="center" prop="batchId" width="80" />
        <el-table-column label="批次编号" align="center" prop="batchNo" width="180" />
        <el-table-column label="统计月份" align="center" prop="statMonth" width="100" />
        <el-table-column label="派遣单位" align="center" prop="dispatchEnterpriseName" min-width="200" />
        <el-table-column label="人数" align="center" prop="totalPersonCount" width="80" />
        <el-table-column label="应发合计" align="center" width="120">
          <template #default="scope">
            <span>{{ formatMoney(scope.row.totalPayableAmount) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="实发合计" align="center" width="120">
          <template #default="scope">
            <span>{{ formatMoney(scope.row.totalPaidAmount) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="批次状态" align="center" prop="batchStatus" width="110">
          <template #default="scope">
            <dict-tag :options="batchStatusOptions" :value="scope.row.batchStatus" />
          </template>
        </el-table-column>
        <el-table-column class-name="table-fill-column" min-width="1" />
        <el-table-column label="操作" fixed="right" align="center" width="120" class-name="small-padding fixed-width">
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

    <page-detail-dialog v-model="detailOpen" title="工资发放监控详情" width="720px">
      <template v-if="detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="批次编号" :span="1">{{ detail.batchNo || '-' }}</el-descriptions-item>
          <el-descriptions-item label="统计月份" :span="1">{{ detail.statMonth || '-' }}</el-descriptions-item>
          <el-descriptions-item label="派遣单位" :span="1">{{ detail.dispatchEnterpriseName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="人数" :span="1">{{ detail.totalPersonCount || '-' }}</el-descriptions-item>
          <el-descriptions-item label="应发合计" :span="1">{{ formatMoney(detail.totalPayableAmount) }}</el-descriptions-item>
          <el-descriptions-item label="实发合计" :span="1">{{ formatMoney(detail.totalPaidAmount) }}</el-descriptions-item>
          <el-descriptions-item label="批次状态" :span="1">{{ formatBatchStatus(detail.batchStatus) }}</el-descriptions-item>
          <el-descriptions-item label="发放时间" :span="1">{{ parseTime(detail.paidTime, '{y}-{m}-{d} {h}:{i}:{s}') || '-' }}</el-descriptions-item>
        </el-descriptions>
      </template>
    </page-detail-dialog>
  </div>
</template>

<script setup name="YgbSalaryPaymentMonitor">
import { ref, onMounted, getCurrentInstance } from 'vue'
import { listSalaryBatch, getSalaryBatchSummary } from '@/api/ygb/salaryBatch'
import { parseTime } from '@/utils/yuegongbao'
import { optionselectEnterprise } from '@/api/ygb/enterprise'

function formatMoney(value) {
  if (value === undefined || value === null || value === '') return '-'
  return '¥ ' + Number(value).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}
function valueOrDefault(value, fallback = 0) {
  return value === undefined || value === null ? fallback : value
}

const loading = ref(false)
const list = ref([])
const total = ref(0)
const showSearch = ref(true)
const detailOpen = ref(false)
const detail = ref(null)
const enterpriseOptions = ref([])
const { proxy } = getCurrentInstance()
const batchStatusOptions = [
  { label: '草稿', value: '0' },
  { label: '待到账确认', value: '1' },
  { label: '待入账', value: '2' },
  { label: '已到账待生成明细', value: '3' },
  { label: '明细已生成待代发', value: '4' },
  { label: '已提交银行', value: '5' },
  { label: '发放成功', value: '6' },
  { label: '发放失败', value: '7' }
]

const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  batchNo: undefined,
  statMonth: undefined,
  dispatchEnterpriseId: undefined,
  batchStatus: undefined
})

const summaryCards = ref([])

async function getList() {
  loading.value = true
  try {
    const res = await listSalaryBatch(queryParams.value)
    list.value = res.rows || res.data || []
    total.value = res.total || 0
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}



async function getSummary() {
  try {
    const res = await getSalaryBatchSummary(queryParams.value)
    const data = res.data || res || {}
    summaryCards.value = [
      { key: 'totalCount', label: '批次总数', value: valueOrDefault(data.totalCount, 0), unit: '笔', note: '当前范围', cardClass: '' },
      { key: 'totalPersonCount', label: '涉及人数', value: valueOrDefault(data.totalPersonCount, 0), unit: '人', note: '工资对象', cardClass: '' },
      { key: 'payingCount', label: '代发中', value: valueOrDefault(data.payingCount, 0), unit: '笔', note: '等待回调', cardClass: 'warning' },
      { key: 'failedCount', label: '发放失败', value: valueOrDefault(data.failedCount, 0), unit: '笔', note: '需处理', cardClass: 'danger' }
    ].filter(Boolean)
  } catch (e) {
    console.error(e)
  }
}

function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
  getSummary()
}

function resetQuery() {
  queryParams.value = {
    pageNum: 1,
    pageSize: 10,
    batchNo: undefined,
    statMonth: undefined,
    dispatchEnterpriseId: undefined,
    batchStatus: undefined
  }
  getList()
  getSummary()
}

function handleSelectionChange(selection) {
}

function openDetail(row) {
  detail.value = row
  detailOpen.value = true
}

function handleExport() {
  proxy.download('ygb/salary/batch/export', { ...queryParams.value }, `salary_payment_monitor_${Date.now()}.xlsx`)
}

function formatBatchStatus(value) {
  return batchStatusOptions.find(item => item.value === value)?.label || '-'
}

async function loadEnterpriseOptions() {
  try {
    const res = await optionselectEnterprise()
    enterpriseOptions.value = res.data || []
  } catch (e) {}
}

onMounted(() => {
  loadEnterpriseOptions()
  getList()
  getSummary()
})
</script>
