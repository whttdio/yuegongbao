<template>
  <div class="app-container ygb-page">
    <section class="gov-page-header ygb-page__header">
      <div>
        <p class="ygb-page__eyebrow">工资监管</p>
        <h1 class="ygb-page__title">监管账户监控</h1>
        <p class="ygb-page__desc">监控工资监管专用账户的到账情况，识别未到账、部分到账等异常状态。</p>
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
        <el-col :span="1.5">
          <el-button type="primary" plain icon="Wallet" :disabled="!canConfirmAccount(currentRow)" @click="openAccountDialog()">确认到账</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button plain icon="Tickets" :disabled="!currentRow" @click="openBatchPage()">批次办理</el-button>
        </el-col>
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
            <div class="ygb-card-head__title">监管账户监控台账</div>
            <div class="ygb-card-head__desc">监控工资监管账户到账金额、流水和到账状态，可直接确认到账并进入批次办理。</div>
          </div>
          <div class="ygb-card-head__desc">当前总量 {{ total }} 条</div>
        </div>
      </template>
      <el-table v-loading="loading" :data="list" highlight-current-row @current-change="handleCurrentChange" @row-click="handleRowClick">
        <el-table-column label="ID" align="center" prop="batchId" width="80" />
        <el-table-column label="批次编号" align="center" prop="batchNo" width="180" />
        <el-table-column label="统计月份" align="center" prop="statMonth" width="100" />
        <el-table-column label="派遣单位" align="center" prop="dispatchEnterpriseName" min-width="200" />
        <el-table-column label="监管账户名" align="center" prop="regulatorAccountName" min-width="160" />
        <el-table-column label="到账金额" align="center" width="120">
          <template #default="scope">
            <span>{{ formatMoney(scope.row.accountReceivedAmount) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="到账状态" align="center" prop="accountStatus" width="100">
          <template #default="scope">
            <dict-tag :options="accountStatusOptions" :value="scope.row.accountStatus" />
          </template>
        </el-table-column>
        <el-table-column label="银行流水号" align="center" prop="bankSerialNo" width="180" />
        <el-table-column class-name="table-fill-column" min-width="1" />
        <el-table-column label="操作" fixed="right" align="center" width="220" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-button link type="info" icon="View" @click.stop="openDetail(scope.row)">详情</el-button>
            <el-button link type="primary" icon="Wallet" :disabled="!canConfirmAccount(scope.row)" @click.stop="openAccountDialog(scope.row)">到账</el-button>
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

    <page-detail-dialog v-model="detailOpen" title="监管账户监控详情" width="720px">
      <template v-if="detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="批次编号" :span="1">{{ detail.batchNo || '-' }}</el-descriptions-item>
          <el-descriptions-item label="统计月份" :span="1">{{ detail.statMonth || '-' }}</el-descriptions-item>
          <el-descriptions-item label="派遣单位" :span="1">{{ detail.dispatchEnterpriseName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="监管账户名" :span="1">{{ detail.regulatorAccountName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="监管账号" :span="1">{{ detail.regulatorAccountNo || '-' }}</el-descriptions-item>
          <el-descriptions-item label="到账金额" :span="1">{{ formatMoney(detail.accountReceivedAmount) }}</el-descriptions-item>
          <el-descriptions-item label="到账状态" :span="1">{{ formatAccountStatus(detail.accountStatus) }}</el-descriptions-item>
          <el-descriptions-item label="银行流水号" :span="1">{{ detail.bankSerialNo || '-' }}</el-descriptions-item>
        </el-descriptions>
      </template>
    </page-detail-dialog>

    <el-dialog title="确认监管账户到账" v-model="accountOpen" width="520px" append-to-body>
      <el-form ref="accountRef" :model="accountForm" :rules="accountRules" label-width="110px">
        <el-form-item label="批次编号">
          <el-input :model-value="currentRow?.batchNo || '-'" disabled />
        </el-form-item>
        <el-form-item label="到账金额" prop="accountReceivedAmount">
          <el-input-number v-model="accountForm.accountReceivedAmount" :min="0.01" :precision="2" :step="1000" style="width: 100%" />
        </el-form-item>
        <el-form-item label="银行流水号" prop="bankSerialNo">
          <el-input v-model="accountForm.bankSerialNo" placeholder="请输入监管账户入账流水号" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="accountOpen = false">取消</el-button>
        <el-button type="primary" @click="submitAccount">确认到账</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="YgbSalaryAccountMonitor">
import { ref, onMounted, getCurrentInstance, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { listSalaryAccount, getSalaryAccountSummary } from '@/api/ygb/salaryAccount'
import { confirmSalaryAccount } from '@/api/ygb/salaryBatch'
import { optionselectEnterprise } from '@/api/ygb/enterprise'
import { resolveRouteAliasPath } from '@/utils/routeAlias'

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
const router = useRouter()
const currentRow = ref(null)
const accountOpen = ref(false)
const accountForm = ref({
  accountReceivedAmount: undefined,
  bankSerialNo: undefined
})
const accountRules = {
  accountReceivedAmount: [{ required: true, message: '请输入到账金额', trigger: 'blur' }],
  bankSerialNo: [{ required: true, message: '请输入银行流水号', trigger: 'blur' }]
}
const accountStatusOptions = [
  { label: '未到账', value: '0' },
  { label: '已到账', value: '1' }
]

const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  batchNo: undefined,
  statMonth: undefined,
  dispatchEnterpriseId: undefined,
  accountStatus: undefined
})

const summaryCards = ref([])

async function getList() {
  loading.value = true
  try {
    const res = await listSalaryAccount(queryParams.value)
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
    const res = await getSalaryAccountSummary(queryParams.value)
    const data = res.data || res || {}
    summaryCards.value = [
      { key: 'totalCount', label: '批次总数', value: valueOrDefault(data.totalCount, 0), unit: '笔', note: '当前范围', cardClass: '' },
      { key: 'awaitingAccountCount', label: '待到账', value: valueOrDefault(data.awaitingAccountCount, 0), unit: '笔', note: '需跟进', cardClass: 'warning' },
      { key: 'regulatorAccountBalance', label: '监管余额', value: valueOrDefault(data.regulatorAccountBalance, 0), unit: '元', note: '当前汇总', cardClass: 'success' },
      { key: 'accountGapAmount', label: '到账缺口', value: valueOrDefault(data.accountGapAmount, 0), unit: '元', note: '需核对', cardClass: 'danger' }
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
    accountStatus: undefined
  }
  getList()
  getSummary()
}

function handleSelectionChange(selection) {
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

function openAccountDialog(row) {
  currentRow.value = row || currentRow.value
  if (!currentRow.value?.batchId) {
    proxy.$modal.msgWarning('请先选择需要确认到账的工资批次')
    return
  }
  if (!canConfirmAccount(currentRow.value)) {
    proxy.$modal.msgWarning('已发放或代发中的批次不能再次确认到账')
    return
  }
  accountForm.value = {
    accountReceivedAmount: currentRow.value.accountReceivedAmount || undefined,
    bankSerialNo: currentRow.value.bankSerialNo || undefined
  }
  accountOpen.value = true
  nextTick(() => proxy.resetForm('accountRef'))
}

function submitAccount() {
  proxy.$refs.accountRef.validate(valid => {
    if (!valid || !currentRow.value?.batchId) {
      return
    }
    confirmSalaryAccount(currentRow.value.batchId, accountForm.value).then(() => {
      proxy.$modal.msgSuccess('到账确认成功')
      accountOpen.value = false
      getList()
      getSummary()
    })
  })
}

function openBatchPage(row) {
  const target = row || currentRow.value
  router.push({
    path: resolveRouteAliasPath('/salary-supervision/payment'),
    query: {
      batchNo: target?.batchNo || queryParams.value.batchNo,
      statMonth: target?.statMonth || queryParams.value.statMonth,
      dispatchEnterpriseId: target?.dispatchEnterpriseId || queryParams.value.dispatchEnterpriseId
    }
  })
}

function handleExport() {
  proxy.download('ygb/salary/account/export', { ...queryParams.value }, `salary_account_monitor_${Date.now()}.xlsx`)
}

function formatAccountStatus(value) {
  return accountStatusOptions.find(item => item.value === value)?.label || '-'
}

function canConfirmAccount(row) {
  return !!row && row.batchStatus !== '5' && row.batchStatus !== '6'
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
