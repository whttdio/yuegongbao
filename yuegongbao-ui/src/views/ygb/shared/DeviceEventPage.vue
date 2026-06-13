<template>
  <div class="app-container ygb-page">
    <section class="gov-page-header ygb-page__header">
      <div>
        <p class="ygb-page__eyebrow">{{ config.eyebrow || config.title }}</p>
        <h1 class="ygb-page__title">{{ config.title }}</h1>
        <p class="ygb-page__desc">{{ config.description }}</p>
      </div>
    </section>

    <el-card class="search-card ygb-search-card" shadow="never">
      <el-form ref="queryRef" :model="queryParams" :inline="true" v-show="showSearch">
        <el-form-item label="区域">
          <el-select v-model="queryParams.regionCode" clearable style="width: 180px">
            <el-option v-for="item in regionOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="企业">
          <el-select v-model="queryParams.enterpriseId" clearable filterable style="width: 220px">
            <el-option v-for="item in enterpriseOptions" :key="item.enterpriseId" :label="item.enterpriseName" :value="item.enterpriseId" />
          </el-select>
        </el-form-item>
        <el-form-item label="设备编码">
          <el-input v-model="queryParams.deviceCode" clearable style="width: 180px" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="处置状态">
          <el-select v-model="queryParams.eventStatus" clearable style="width: 160px">
            <el-option label="未处理" value="0" />
            <el-option label="已处理" value="1" />
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
      <el-table v-loading="loading" :data="eventList">
        <el-table-column label="事件ID" prop="eventId" width="90" />
        <el-table-column label="设备编码" prop="deviceCode" width="150" />
        <el-table-column label="企业名称" prop="enterpriseName" min-width="180" show-overflow-tooltip />
        <el-table-column label="区域" min-width="140">
          <template #default="{ row }">{{ formatRegionName(row.regionCode, row.regionCode) }}</template>
        </el-table-column>
        <el-table-column label="事件编码" prop="eventCode" width="150" />
        <el-table-column label="事件内容" prop="eventContent" min-width="220" show-overflow-tooltip />
        <el-table-column label="处置状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.eventStatus === '1' ? 'success' : 'warning'">{{ row.eventStatus === '1' ? '已处理' : '未处理' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="事件时间" width="170">
          <template #default="{ row }">{{ parseTime(row.eventTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</template>
        </el-table-column>
      </el-table>
      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>
  </div>
</template>

<script setup>
import { getCurrentInstance, reactive, ref, toRefs } from 'vue'
import { useRoute } from 'vue-router'
import { optionselectEnterprise } from '@/api/ygb/enterprise'
import { authorizedDefaultRegionCode, useAuthorizedRegionOptions } from '@/utils/regionScope'
import { statReportRegionOptions } from '@/views/statReport/useStatReportPage'
import { formatRegionName } from '@/views/device/useDevicePage'

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
const eventList = ref([])
const enterpriseOptions = ref([])
const regionOptions = useAuthorizedRegionOptions(statReportRegionOptions)

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    regionCode: authorizedDefaultRegionCode('440000'),
    enterpriseId: undefined,
    deviceCode: undefined,
    eventStatus: undefined
  }
})

const { queryParams } = toRefs(data)

function applyRouteQuery() {
  ;['regionCode', 'enterpriseId', 'deviceCode', 'eventStatus'].forEach(field => {
    if (route.query[field] !== undefined) {
      queryParams.value[field] = route.query[field]
    }
  })
}

function normalizedParams(source) {
  return Object.fromEntries(Object.entries(source).filter(([, value]) => value !== undefined && value !== null && value !== ''))
}

function getList() {
  loading.value = true
  return props.config.listApi(normalizedParams(queryParams.value)).then(response => {
    eventList.value = response.rows || []
    total.value = response.total || 0
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
    regionCode: authorizedDefaultRegionCode('440000'),
    enterpriseId: undefined,
    deviceCode: undefined,
    eventStatus: undefined
  }
  applyRouteQuery()
  getList()
}

function handleExport() {
  proxy.download(props.config.exportUrl, normalizedParams({
    ...queryParams.value,
    pageNum: undefined,
    pageSize: undefined
  }), `${props.config.filePrefix || 'device_event'}_${Date.now()}.xlsx`)
}

applyRouteQuery()
loadEnterpriseOptions().finally(() => {
  getList()
})
</script>
