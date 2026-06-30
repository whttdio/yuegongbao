<template>
  <div class="app-container ygb-page">
    <section class="gov-page-header ygb-page__header">
      <div>
        <p class="ygb-page__eyebrow">用工考勤</p>
        <h1 class="ygb-page__title">考勤总览</h1>
        <p class="ygb-page__desc">汇总展示考勤上报情况、出勤率、异常率等核心指标，支持按企业与月份下钻。</p>
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
          <el-select v-model="queryParams.attendanceStatus" placeholder="请选择考勤状态" clearable style="width: 140px">
            <el-option label="正常" value="0" />
            <el-option label="异常" value="1" />
            <el-option label="缺卡" value="2" />
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
            <div class="ygb-card-head__title">考勤总览台账</div>
            <div class="ygb-card-head__desc">汇总考勤归集、核验状态和异常趋势，支撑监管端按区域快速研判。</div>
          </div>
          <div class="ygb-card-head__desc">当前总量 {{ total }} 条</div>
        </div>
      </template>
      <el-table v-loading="loading" :data="list" @selection-change="handleSelectionChange">
        <el-table-column label="ID" align="center" prop="attendanceId" width="80" />
        <el-table-column label="派遣单位" align="center" prop="dispatchEnterpriseName" min-width="200" />
        <el-table-column label="用工单位" align="center" prop="employerEnterpriseName" min-width="200" />
        <el-table-column label="人员" align="center" prop="personName" width="120" />
        <el-table-column label="考勤日期" align="center" width="120">
          <template #default="scope">
            <span>{{ parseTime(scope.row.attendanceDate, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="出勤状态" align="center" prop="attendanceStatus" width="100">
          <template #default="scope">
            <dict-tag :options="[{ label: '正常', value: '0' }, { label: '异常', value: '1' }, { label: '缺卡', value: '2' }]" :value="scope.row.attendanceStatus" />
          </template>
        </el-table-column>
        <el-table-column label="上班打卡" align="center" width="170">
          <template #default="scope">
            <span>{{ parseTime(scope.row.clockInTime, '{y}-{m}-{d} {h}:{i}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="下班打卡" align="center" width="170">
          <template #default="scope">
            <span>{{ parseTime(scope.row.clockOutTime, '{y}-{m}-{d} {h}:{i}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="出勤工时" align="center" prop="attendanceHours" width="110" />
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

    <page-detail-dialog v-model="detailOpen" title="考勤总览详情" width="720px">
      <template v-if="detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="派遣单位" :span="1">{{ detail.dispatchEnterpriseName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="用工单位" :span="1">{{ detail.employerEnterpriseName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="人员" :span="1">{{ detail.personName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="考勤日期" :span="1">{{ parseTime(detail.attendanceDate, '{y}-{m}-{d}') || '-' }}</el-descriptions-item>
          <el-descriptions-item label="出勤状态" :span="1">{{ detail.attendanceStatus || '-' }}</el-descriptions-item>
          <el-descriptions-item label="出勤工时" :span="1">{{ detail.attendanceHours || '-' }}</el-descriptions-item>
          <el-descriptions-item label="加班工时" :span="1">{{ detail.overtimeHours || '-' }}</el-descriptions-item>
          <el-descriptions-item label="创建时间" :span="1">{{ parseTime(detail.createTime, '{y}-{m}-{d} {h}:{i}:{s}') || '-' }}</el-descriptions-item>
        </el-descriptions>
      </template>
    </page-detail-dialog>
  </div>
</template>

<script setup name="YgbAttendanceOverview">
import { ref, onMounted, getCurrentInstance } from 'vue'
import { listAttendanceRaw, getAttendanceRawOverview } from '@/api/ygb/attendanceRaw'
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
  statMonth: undefined,
  dispatchEnterpriseId: undefined,
  attendanceStatus: undefined
})

const summaryCards = ref([])

async function getList() {
  loading.value = true
  try {
    const res = await listAttendanceRaw(queryParams.value)
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
    const res = await getAttendanceRawOverview(queryParams.value)
    const data = res.data || res || {}
    summaryCards.value = [
      { key: 'totalRecord', label: '考勤记录', value: valueOrDefault(data.totalCount, 0), unit: '条', note: '当前范围', cardClass: '' },
      { key: 'collectedCount', label: '已归集', value: valueOrDefault(data.collectedCount, 0), unit: '条', note: '可进入工资校验', cardClass: 'success' },
      { key: 'abnormalCount', label: '异常记录', value: valueOrDefault(data.abnormalCount, 0), unit: '条', note: '需关注', cardClass: 'warning' },
      { key: 'deviceOnlineRate', label: '设备在线率', value: valueOrDefault(data.deviceOnlineRate, 0), unit: '%', note: '实时均值', cardClass: '' }
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
  proxy.download('ygb/attendance/raw/export', { ...queryParams.value }, `attendance_overview_${Date.now()}.xlsx`)
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
