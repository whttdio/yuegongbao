<template>
  <div class="app-container ygb-page">
    <section class="gov-page-header ygb-page__header">
      <div>
        <p class="ygb-page__eyebrow">用工考勤</p>
        <h1 class="ygb-page__title">设备在线率报表</h1>
        <p class="ygb-page__desc">展示考勤相关设备的在线状态、在线率统计与离线预警，支撑考勤数据质量治理。</p>
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
        <el-form-item label="设备编号" prop="deviceCode">
          <el-input v-model="queryParams.deviceCode" placeholder="请输入设备编号" clearable style="width: 180px" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="企业" prop="enterpriseId">
          <el-select v-model="queryParams.enterpriseId" placeholder="请选择企业" clearable filterable style="width: 200px">
            <el-option v-for="item in enterpriseOptions" :key="item.enterpriseId" :label="item.enterpriseName" :value="item.enterpriseId" />
          </el-select>
        </el-form-item>
        <el-form-item label="在线状态" prop="deviceStatus">
          <el-select v-model="queryParams.deviceStatus" placeholder="请选择在线状态" clearable style="width: 140px">
            <el-option label="在线" value="1" />
            <el-option label="离线" value="0" />
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
            <div class="ygb-card-head__title">设备在线率报表台账</div>
            <div class="ygb-card-head__desc">跟踪考勤设备在线率和数据上报情况，支撑设备巡检和异常补采。</div>
          </div>
          <div class="ygb-card-head__desc">当前总量 {{ total }} 条</div>
        </div>
      </template>
      <el-table v-loading="loading" :data="list" @selection-change="handleSelectionChange">
        <el-table-column label="设备ID" align="center" prop="deviceId" width="80" />
        <el-table-column label="设备编号" align="center" prop="deviceCode" width="160" />
        <el-table-column label="设备名称" align="center" prop="deviceName" min-width="180" />
        <el-table-column label="企业" align="center" prop="enterpriseName" min-width="200" />
        <el-table-column label="设备类型" align="center" prop="deviceType" width="120" />
        <el-table-column label="在线状态" align="center" prop="deviceStatus" width="100">
          <template #default="scope">
            <dict-tag :options="[{ label: '在线', value: '1' }, { label: '离线', value: '0' }]" :value="scope.row.deviceStatus" />
          </template>
        </el-table-column>
        <el-table-column label="最近心跳" align="center" width="170">
          <template #default="scope">
            <span>{{ parseTime(scope.row.lastHeartbeat, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
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

    <page-detail-dialog v-model="detailOpen" title="设备在线率报表详情" width="720px">
      <template v-if="detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="设备编号" :span="1">{{ detail.deviceCode || '-' }}</el-descriptions-item>
          <el-descriptions-item label="设备名称" :span="1">{{ detail.deviceName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="企业" :span="1">{{ detail.enterpriseName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="设备类型" :span="1">{{ detail.deviceType || '-' }}</el-descriptions-item>
          <el-descriptions-item label="在线状态" :span="1">{{ detail.deviceStatus || '-' }}</el-descriptions-item>
          <el-descriptions-item label="最近心跳" :span="1">{{ parseTime(detail.lastHeartbeat, '{y}-{m}-{d} {h}:{i}:{s}') || '-' }}</el-descriptions-item>
          <el-descriptions-item label="安装位置" :span="2">{{ detail.installLocation || '-' }}</el-descriptions-item>
        </el-descriptions>
      </template>
    </page-detail-dialog>
  </div>
</template>

<script setup name="YgbAttendanceDeviceOnline">
import { ref, onMounted, getCurrentInstance } from 'vue'
import { listDevice, getDeviceSummary } from '@/api/ygb/device'
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
  deviceCode: undefined,
  enterpriseId: undefined,
  deviceStatus: undefined
})

const summaryCards = ref([])

async function getList() {
  loading.value = true
  try {
    const res = await listDevice(queryParams.value)
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
    const res = await getDeviceSummary(queryParams.value)
    const data = res.data || res || {}
    const totalCount = valueOrDefault(data.totalCount, 0)
    const onlineCount = valueOrDefault(data.onlineCount, 0)
    summaryCards.value = [
      { key: 'totalDevice', label: '设备总数', value: totalCount, unit: '台', note: '当前范围', cardClass: '' },
      { key: 'onlineCount', label: '在线设备', value: onlineCount, unit: '台', note: '实时在线', cardClass: 'success' },
      { key: 'offlineCount', label: '离线设备', value: Math.max(totalCount - onlineCount, 0), unit: '台', note: '需排查', cardClass: 'danger' },
      { key: 'lockedOrFaultCount', label: '锁定/故障', value: valueOrDefault(data.lockedOrFaultCount, 0), unit: '台', note: '需处置', cardClass: 'warning' }
    ].filter(Boolean)
  } catch (e) {
    console.error(e)
  }
}

function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

function resetQuery() {
  queryParams.value = {
    pageNum: 1,
    pageSize: 10,
    deviceCode: undefined,
    enterpriseId: undefined,
    deviceStatus: undefined
  }
  getList()
}

function handleSelectionChange(selection) {
}

function openDetail(row) {
  detail.value = row
  detailOpen.value = true
}

function handleExport() {
  proxy.download('ygb/attendance/raw/export', { ...queryParams.value }, `attendance_device_online_${Date.now()}.xlsx`)
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
