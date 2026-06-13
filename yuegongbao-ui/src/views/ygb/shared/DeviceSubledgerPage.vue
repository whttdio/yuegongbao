<template>
  <div class="app-container ygb-page">
    <section class="gov-page-header ygb-page__header">
      <div>
        <p class="ygb-page__eyebrow">{{ config.eyebrow || config.title }}</p>
        <h1 class="ygb-page__title">{{ config.title }}</h1>
        <p class="ygb-page__desc">{{ config.description }}</p>
      </div>
      <div v-if="config.tip" class="ygb-table-tip">
        {{ config.tip }}
      </div>
    </section>

    <div class="ygb-summary-grid">
      <div v-for="item in summaryCards" :key="item.key" class="ygb-summary-card" :class="item.cardClass">
        <div class="ygb-summary-card__label">{{ item.label }}</div>
        <div class="ygb-summary-card__value">
          {{ item.value }}
          <span v-if="item.unit" class="ygb-summary-card__unit">{{ item.unit }}</span>
        </div>
      </div>
    </div>

    <el-card class="search-card ygb-search-card" shadow="never">
      <el-form ref="queryRef" :model="queryParams" :inline="true" v-show="showSearch">
        <el-form-item v-if="showFilter('regionCode')" label="区域">
          <el-select v-model="queryParams.regionCode" clearable style="width: 180px">
            <el-option v-for="item in regionOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="showFilter('enterpriseId')" label="企业">
          <el-select v-model="queryParams.enterpriseId" clearable filterable style="width: 220px">
            <el-option v-for="item in enterpriseOptions" :key="item.enterpriseId" :label="item.enterpriseName" :value="item.enterpriseId" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="showFilter('deviceCode')" label="设备编码">
          <el-input v-model="queryParams.deviceCode" clearable style="width: 180px" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item v-if="showFilter('deviceName')" label="设备名称">
          <el-input v-model="queryParams.deviceName" clearable style="width: 180px" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item v-if="showFilter('deviceStatus')" label="设备状态">
          <el-select v-model="queryParams.deviceStatus" clearable style="width: 160px">
            <el-option v-for="item in deviceStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="showFilter('authStatus')" label="授权状态">
          <el-select v-model="queryParams.authStatus" clearable style="width: 160px">
            <el-option v-for="item in authStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">查询</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="toolbar-card ygb-toolbar-card" shadow="never">
      <el-row :gutter="10">
        <el-col :span="1.5">
          <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="[`${config.permPrefix}:export`]">导出</el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>
    </el-card>

    <el-card class="table-card ygb-table-card" shadow="never">
      <el-table v-loading="loading" :data="deviceList">
        <el-table-column v-for="column in config.columns" :key="column.prop || column.label" v-bind="resolveColumnProps(column)">
          <template v-if="column.type" #default="{ row }">
            <dict-tag v-if="column.type === 'deviceStatus'" :options="deviceStatusOptions" :value="row[column.prop]" />
            <dict-tag v-else-if="column.type === 'authStatus'" :options="authStatusOptions" :value="row[column.prop]" />
            <dict-tag v-else-if="column.type === 'deviceType'" :options="deviceTypeOptions" :value="row[column.prop]" />
            <span v-else-if="column.type === 'region'">{{ formatRegionName(row[column.prop], row[column.prop]) }}</span>
            <span v-else-if="column.type === 'time'">{{ parseTime(row[column.prop], '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
            <span v-else>{{ row[column.prop] ?? '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column v-if="config.detailApi" label="操作" fixed="right" width="100">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)" v-hasPermi="[`${config.permPrefix}:query`]">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>

    <el-dialog title="设备详情" v-model="detailOpen" width="760px">
      <el-descriptions v-if="detail" :column="2" border>
        <el-descriptions-item label="设备编码">{{ detail.deviceCode || '-' }}</el-descriptions-item>
        <el-descriptions-item label="设备名称">{{ detail.deviceName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="设备类型"><dict-tag :options="deviceTypeOptions" :value="detail.deviceType" /></el-descriptions-item>
        <el-descriptions-item label="所属企业">{{ detail.enterpriseName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="区域">{{ formatRegionName(detail.regionCode, detail.regionCode) }}</el-descriptions-item>
        <el-descriptions-item label="芯片ID">{{ detail.chipId || '-' }}</el-descriptions-item>
        <el-descriptions-item label="物联卡号">{{ detail.simCardNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="设备状态"><dict-tag :options="deviceStatusOptions" :value="detail.deviceStatus" /></el-descriptions-item>
        <el-descriptions-item label="授权状态"><dict-tag :options="authStatusOptions" :value="detail.authStatus" /></el-descriptions-item>
        <el-descriptions-item label="最后心跳">{{ parseTime(detail.lastHeartbeat, '{y}-{m}-{d} {h}:{i}:{s}') || '-' }}</el-descriptions-item>
        <el-descriptions-item label="安装位置" :span="2">{{ detail.installLocation || '-' }}</el-descriptions-item>
        <el-descriptions-item label="固件版本">{{ detail.firmwareVersion || '-' }}</el-descriptions-item>
        <el-descriptions-item label="备注">{{ detail.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, getCurrentInstance, reactive, ref, toRefs } from 'vue'
import { useRoute } from 'vue-router'
import { optionselectEnterprise } from '@/api/ygb/enterprise'
import { authorizedDefaultRegionCode, useAuthorizedRegionOptions } from '@/utils/regionScope'
import { statReportRegionOptions } from '@/views/statReport/useStatReportPage'
import { authStatusOptions, deviceStatusOptions, deviceTypeOptions, formatRegionName } from '@/views/device/useDevicePage'

const props = defineProps({
  config: {
    type: Object,
    required: true
  }
})

const route = useRoute()
const { proxy } = getCurrentInstance()
const loading = ref(false)
const showSearch = ref(true)
const total = ref(0)
const deviceList = ref([])
const summaryData = ref({})
const detail = ref(null)
const detailOpen = ref(false)
const enterpriseOptions = ref([])
const regionOptions = useAuthorizedRegionOptions(statReportRegionOptions)

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    regionCode: authorizedDefaultRegionCode(props.config.defaultRegionCode || '440000'),
    enterpriseId: undefined,
    deviceCode: undefined,
    deviceName: undefined,
    deviceStatus: undefined,
    authStatus: undefined,
    ...(props.config.defaultQueryParams || {})
  }
})

const { queryParams } = toRefs(data)

const summaryCards = computed(() => {
  if (typeof props.config.summaryCards === 'function') {
    return props.config.summaryCards(summaryData.value)
  }
  return [
    { key: 'totalCount', label: '总量', value: summaryData.value.totalCount ?? 0, unit: '台' },
    { key: 'onlineCount', label: '在线设备', value: summaryData.value.onlineCount ?? 0, unit: '台', cardClass: 'ygb-summary-card--success' },
    { key: 'authDeniedCount', label: '授权异常', value: summaryData.value.authDeniedCount ?? 0, unit: '台', cardClass: 'ygb-summary-card--warning' },
    { key: 'lockedOrFaultCount', label: '锁定/故障', value: summaryData.value.lockedOrFaultCount ?? 0, unit: '台', cardClass: 'ygb-summary-card--primary' }
  ]
})

function visibleFilters() {
  return props.config.filters || ['regionCode', 'enterpriseId', 'deviceCode', 'deviceName', 'deviceStatus', 'authStatus']
}

function showFilter(field) {
  return visibleFilters().includes(field)
}

function applyRouteQuery() {
  const fields = props.config.routeQueryFields || ['regionCode', 'enterpriseId', 'deviceCode', 'deviceName', 'deviceStatus', 'authStatus']
  fields.forEach(field => {
    if (route.query[field] !== undefined) {
      queryParams.value[field] = route.query[field]
    }
  })
}

function normalizedParams(source) {
  return Object.fromEntries(Object.entries(source).filter(([, value]) => value !== undefined && value !== null && value !== ''))
}

function resolveColumnProps(column) {
  const { type, ...rest } = column
  return rest
}

function getList() {
  loading.value = true
  return Promise.all([
    props.config.listApi(normalizedParams(queryParams.value)),
    props.config.summaryApi(normalizedParams({
      ...queryParams.value,
      pageNum: undefined,
      pageSize: undefined
    }))
  ]).then(([listResponse, summaryResponse]) => {
    deviceList.value = listResponse.rows || []
    total.value = listResponse.total || 0
    summaryData.value = summaryResponse.data || {}
  }).finally(() => {
    loading.value = false
  })
}

function loadEnterpriseOptions() {
  return optionselectEnterprise().then(response => {
    enterpriseOptions.value = response.data || []
  })
}

function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

function resetQuery() {
  proxy.resetForm('queryRef')
  queryParams.value = {
    pageNum: 1,
    pageSize: 10,
    regionCode: authorizedDefaultRegionCode(props.config.defaultRegionCode || '440000'),
    enterpriseId: undefined,
    deviceCode: undefined,
    deviceName: undefined,
    deviceStatus: undefined,
    authStatus: undefined,
    ...(props.config.defaultQueryParams || {})
  }
  applyRouteQuery()
  getList()
}

function handleView(row) {
  if (!props.config.detailApi) {
    return
  }
  props.config.detailApi(row.deviceId).then(response => {
    detail.value = response.data || null
    detailOpen.value = true
  })
}

function handleExport() {
  proxy.download(props.config.exportUrl, normalizedParams({
    ...queryParams.value,
    pageNum: undefined,
    pageSize: undefined
  }), `${props.config.filePrefix || 'device_subledger'}_${Date.now()}.xlsx`)
}

applyRouteQuery()
loadEnterpriseOptions().finally(() => {
  getList()
})
</script>
