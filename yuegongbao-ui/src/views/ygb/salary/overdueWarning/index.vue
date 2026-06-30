<template>
  <div class="app-container ygb-page">
    <section class="gov-page-header ygb-page__header">
      <div>
        <p class="ygb-page__eyebrow">工资监管</p>
        <h1 class="ygb-page__title">拖欠工资预警</h1>
        <p class="ygb-page__desc">识别存在拖欠工资风险的批次与企业，支持处置跟踪与闭环管理。</p>
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
        <el-form-item label="企业" prop="dispatchEnterpriseId">
          <el-select v-model="queryParams.dispatchEnterpriseId" placeholder="请选择企业" clearable filterable style="width: 200px">
            <el-option v-for="item in enterpriseOptions" :key="item.enterpriseId" :label="item.enterpriseName" :value="item.enterpriseId" />
          </el-select>
        </el-form-item>
        <el-form-item label="处置状态" prop="handleStatus">
          <el-select v-model="queryParams.handleStatus" placeholder="请选择处置状态" clearable style="width: 140px">
            <el-option v-for="item in handleStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
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
          <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['ygb:salaryBatchArrears:export']">导出</el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>
    </el-card>

    <el-card class="table-card ygb-table-card" shadow="never">
      <template #header>
        <div class="ygb-card-head ygb-card-head--between">
          <div>
            <div class="ygb-card-head__title">拖欠工资预警台账</div>
            <div class="ygb-card-head__desc">识别逾期未发、部分发放和疑似拖欠工资风险，联动预警工单处置。</div>
          </div>
          <div class="ygb-card-head__desc">当前总量 {{ total }} 条</div>
        </div>
      </template>
      <el-table v-loading="loading" :data="list" @selection-change="handleSelectionChange">
        <el-table-column label="ID" align="center" prop="arrearsId" width="80" />
        <el-table-column label="批次编号" align="center" prop="batchNo" width="180" />
        <el-table-column label="企业" align="center" prop="dispatchEnterpriseName" min-width="200" />
        <el-table-column label="拖欠金额" align="center" width="120">
          <template #default="scope">
            <span>{{ formatMoney(scope.row.arrearsAmount) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="拖欠人数" align="center" prop="arrearsPersonCount" width="100" />
        <el-table-column label="处置状态" align="center" prop="handleStatus" width="100">
          <template #default="scope">
            <dict-tag :options="handleStatusOptions" :value="scope.row.handleStatus" />
          </template>
        </el-table-column>
        <el-table-column label="发现时间" align="center" width="170">
          <template #default="scope">
            <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
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

    <page-detail-dialog v-model="detailOpen" title="拖欠工资预警详情" width="720px">
      <template v-if="detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="批次编号" :span="1">{{ detail.batchNo || '-' }}</el-descriptions-item>
          <el-descriptions-item label="企业" :span="1">{{ detail.dispatchEnterpriseName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="拖欠金额" :span="1">{{ formatMoney(detail.arrearsAmount) }}</el-descriptions-item>
          <el-descriptions-item label="应发人数" :span="1">{{ detail.totalPersonCount || '-' }}</el-descriptions-item>
          <el-descriptions-item label="处置状态" :span="1">
            <dict-tag :options="handleStatusOptions" :value="detail.handleStatus" />
          </el-descriptions-item>
          <el-descriptions-item label="发现时间" :span="1">{{ parseTime(detail.createTime, '{y}-{m}-{d} {h}:{i}:{s}') || '-' }}</el-descriptions-item>
          <el-descriptions-item label="处置说明" :span="2">{{ detail.handleResult || '-' }}</el-descriptions-item>
        </el-descriptions>
      </template>
    </page-detail-dialog>
  </div>
</template>

<script setup name="YgbSalaryOverdueWarning">
import { ref, onMounted, getCurrentInstance } from 'vue'
import { listSalaryArrears, getSalaryArrearsSummary } from '@/api/ygb/salaryArrears'
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

const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  batchNo: undefined,
  dispatchEnterpriseId: undefined,
  handleStatus: undefined
})

const handleStatusOptions = [
  { label: '未处置', value: 'pending' },
  { label: '处置中', value: 'processing' },
  { label: '已闭环', value: 'closed' },
  { label: '已驳回', value: 'rejected' },
  { label: '已逾期', value: 'overdue' }
]

const summaryCards = ref([])

async function getList() {
  loading.value = true
  try {
    const res = await listSalaryArrears(queryParams.value)
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
    const res = await getSalaryArrearsSummary(queryParams.value)
    const data = res.data || res || {}
    summaryCards.value = [
      { key: 'totalArrears', label: '拖欠笔数', value: valueOrDefault(data.totalArrears, 0), unit: '笔', note: '当前范围', cardClass: 'danger' },
      { key: 'totalArrearsAmount', label: '拖欠总额', value: valueOrDefault(data.totalArrearsAmount, 0), unit: '元', note: '累计', cardClass: '' },
      { key: 'unhandledCount', label: '未处置', value: valueOrDefault(data.unhandledCount, 0), unit: '笔', note: '需跟进', cardClass: 'warning' },
      { key: 'handledCount', label: '已处置', value: valueOrDefault(data.handledCount, 0), unit: '笔', note: '闭环', cardClass: 'success' }
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
    dispatchEnterpriseId: undefined,
    handleStatus: undefined
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
  proxy.download('ygb/salary/batch/arrears/export', { ...queryParams.value }, `salary_overdue_warning_${Date.now()}.xlsx`)
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
