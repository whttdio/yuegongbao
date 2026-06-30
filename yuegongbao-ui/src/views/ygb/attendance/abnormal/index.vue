<template>
  <div class="app-container ygb-page">
    <section class="gov-page-header ygb-page__header">
      <div>
        <p class="ygb-page__eyebrow">用工考勤</p>
        <h1 class="ygb-page__title">异常考勤统计</h1>
        <p class="ygb-page__desc">统计并展示异常考勤记录，支持按异常类型、企业、月份筛选。</p>
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
        <el-form-item label="统计月份" prop="statMonth">
          <el-date-picker v-model="queryParams.statMonth" type="month" placeholder="请选择统计月份" format="YYYY-MM" value-format="YYYY-MM" style="width: 160px" />
        </el-form-item>
        <el-form-item label="企业" prop="dispatchEnterpriseId">
          <el-select v-model="queryParams.dispatchEnterpriseId" placeholder="请选择企业" clearable filterable style="width: 200px">
            <el-option v-for="item in enterpriseOptions" :key="item.enterpriseId" :label="item.enterpriseName" :value="item.enterpriseId" />
          </el-select>
        </el-form-item>
        <el-form-item label="考勤状态" prop="attendanceStatus">
          <el-select v-model="queryParams.attendanceStatus" placeholder="请选择考勤状态" clearable style="width: 150px">
            <el-option label="缺勤" value="0" />
            <el-option label="迟到" value="2" />
            <el-option label="早退" value="3" />
            <el-option label="异常" value="5" />
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
          <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['ygb:attendanceRaw:export']">导出</el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>
    </el-card>

    <el-card class="table-card ygb-table-card" shadow="never">
      <template #header>
        <div class="ygb-card-head ygb-card-head--between">
          <div>
            <div class="ygb-card-head__title">异常考勤统计台账</div>
            <div class="ygb-card-head__desc">聚合迟到、缺卡、离岗等异常考勤记录，便于跟踪企业整改和预警处置。</div>
          </div>
          <div class="ygb-card-head__desc">当前总量 {{ total }} 条</div>
        </div>
      </template>
      <el-table v-loading="loading" :data="list" @selection-change="handleSelectionChange">
        <el-table-column label="ID" align="center" prop="attendanceId" width="80" />
        <el-table-column label="派遣单位" align="center" prop="dispatchEnterpriseName" min-width="180" />
        <el-table-column label="用工单位" align="center" prop="employerEnterpriseName" min-width="180" />
        <el-table-column label="人员" align="center" prop="personName" width="120" />
        <el-table-column label="考勤日期" align="center" width="120">
          <template #default="scope">
            <span>{{ parseTime(scope.row.attendanceDate, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="考勤状态" align="center" prop="attendanceStatus" width="110">
          <template #default="scope">
            <dict-tag :options="attendanceStatusOptions" :value="scope.row.attendanceStatus" />
          </template>
        </el-table-column>
        <el-table-column label="异常说明" align="center" prop="anomalyRemark" min-width="200" />
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

    <page-detail-dialog v-model="detailOpen" title="异常考勤统计详情" width="720px">
      <template v-if="detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="派遣单位" :span="1">{{ detail.dispatchEnterpriseName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="用工单位" :span="1">{{ detail.employerEnterpriseName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="人员" :span="1">{{ detail.personName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="考勤日期" :span="1">{{ parseTime(detail.attendanceDate, '{y}-{m}-{d}') || '-' }}</el-descriptions-item>
          <el-descriptions-item label="考勤状态" :span="1">{{ formatAttendanceStatus(detail.attendanceStatus) }}</el-descriptions-item>
          <el-descriptions-item label="异常说明" :span="1">{{ detail.anomalyRemark || '-' }}</el-descriptions-item>
          <el-descriptions-item label="创建时间" :span="1">{{ parseTime(detail.createTime, '{y}-{m}-{d} {h}:{i}:{s}') || '-' }}</el-descriptions-item>
        </el-descriptions>
      </template>
    </page-detail-dialog>
  </div>
</template>

<script setup name="YgbAttendanceAbnormal">
import { ref, onMounted, getCurrentInstance } from 'vue'
import { listAttendanceRaw, getAttendanceRawSummary } from '@/api/ygb/attendanceRaw'
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
const attendanceStatusOptions = [
  { label: '缺勤', value: '0' },
  { label: '正常', value: '1' },
  { label: '迟到', value: '2' },
  { label: '早退', value: '3' },
  { label: '加班', value: '4' },
  { label: '异常', value: '5' }
]

const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  statMonth: undefined,
  dispatchEnterpriseId: undefined,
  attendanceStatus: undefined
})

const summaryCards = ref([])

async function getList() {
  loading.value = true
  try {
    const res = await listAttendanceRaw(buildQueryParams())
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
    const res = await getAttendanceRawSummary(buildQueryParams())
    const data = res.data || res || {}
    summaryCards.value = [
      { key: 'abnormalCount', label: '异常次数', value: valueOrDefault(data.abnormalCount, 0), unit: '次', note: '当前范围', cardClass: 'warning' },
      { key: 'checkFailedCount', label: '校验失败', value: valueOrDefault(data.checkFailedCount, 0), unit: '次', note: '需复核', cardClass: 'danger' },
      { key: 'manualFillCount', label: '补录记录', value: valueOrDefault(data.manualFillCount, 0), unit: '次', note: '人工补录', cardClass: '' },
      { key: 'uncollectedCount', label: '未归集', value: valueOrDefault(data.uncollectedCount, 0), unit: '次', note: '待归集', cardClass: 'danger' }
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
    statMonth: undefined,
    dispatchEnterpriseId: undefined,
    attendanceStatus: undefined
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
  proxy.download('ygb/attendance/raw/export', buildQueryParams(), `attendance_abnormal_${Date.now()}.xlsx`)
}

function buildQueryParams() {
  const query = { ...queryParams.value }
  if (query.statMonth) {
    const [year, month] = query.statMonth.split('-').map(Number)
    const start = new Date(year, month - 1, 1)
    const end = new Date(year, month, 1)
    query.params = {
      ...(query.params || {}),
      beginTime: parseTime(start, '{y}-{m}-{d}'),
      endTime: parseTime(end, '{y}-{m}-{d}')
    }
  }
  delete query.statMonth
  return query
}

function formatAttendanceStatus(value) {
  return attendanceStatusOptions.find(item => item.value === value)?.label || '-'
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
