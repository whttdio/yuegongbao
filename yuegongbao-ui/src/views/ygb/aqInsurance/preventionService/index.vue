<template>
  <div class="app-container ygb-page">
    <section class="gov-page-header ygb-page__header">
      <div>
        <p class="ygb-page__eyebrow">安责险管理</p>
        <h1 class="ygb-page__title">事故预防服务</h1>
        <p class="ygb-page__desc">展示安责险事故预防服务项目与服务记录，跟踪服务落地情况。</p>
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
        <el-form-item label="项目名称" prop="projectName">
          <el-input v-model="queryParams.projectName" placeholder="请输入项目名称" clearable style="width: 200px" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="企业" prop="enterpriseId">
          <el-select v-model="queryParams.enterpriseId" placeholder="请选择企业" clearable filterable style="width: 200px">
            <el-option v-for="item in enterpriseOptions" :key="item.enterpriseId" :label="item.enterpriseName" :value="item.enterpriseId" />
          </el-select>
        </el-form-item>
        <el-form-item label="服务状态" prop="serviceStatus">
          <el-select v-model="queryParams.serviceStatus" placeholder="请选择服务状态" clearable style="width: 140px">
            <el-option label="未开始" value="0" />
            <el-option label="进行中" value="1" />
            <el-option label="已完成" value="2" />
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
          <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['ygb:preventionProject:export']">导出</el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>
    </el-card>

    <el-card class="table-card ygb-table-card" shadow="never">
      <template #header>
        <div class="ygb-card-head ygb-card-head--between">
          <div>
            <div class="ygb-card-head__title">事故预防服务台账</div>
            <div class="ygb-card-head__desc">按项目、企业、状态跟踪服务执行情况</div>
          </div>
          <div class="ygb-card-head__desc">当前总量 {{ total }} 条</div>
        </div>
      </template>
      <el-table v-loading="loading" :data="list" @selection-change="handleSelectionChange">
        <el-table-column label="ID" align="center" prop="projectId" width="80" />
        <el-table-column label="项目名称" align="center" prop="projectName" min-width="200" />
        <el-table-column label="企业" align="center" prop="enterpriseName" min-width="200" />
        <el-table-column label="服务类型" align="center" prop="serviceType" width="120" />
        <el-table-column label="服务状态" align="center" prop="serviceStatus" width="100">
          <template #default="scope">
            <dict-tag :options="serviceStatusOptions" :value="scope.row.serviceStatus" />
          </template>
        </el-table-column>
        <el-table-column label="计划时间" align="center" width="120">
          <template #default="scope">
            <span>{{ parseTime(scope.row.planDate, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="完成时间" align="center" width="120">
          <template #default="scope">
            <span>{{ parseTime(scope.row.finishDate, '{y}-{m}-{d}') }}</span>
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

    <page-detail-dialog v-model="detailOpen" title="事故预防服务详情" width="720px">
      <template v-if="detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="项目名称" :span="1">{{ detail.projectName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="企业" :span="1">{{ detail.enterpriseName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="服务类型" :span="1">{{ detail.serviceType || '-' }}</el-descriptions-item>
          <el-descriptions-item label="服务状态" :span="1">{{ formatServiceStatus(detail.serviceStatus) }}</el-descriptions-item>
          <el-descriptions-item label="计划时间" :span="1">{{ parseTime(detail.planDate, '{y}-{m}-{d}') || '-' }}</el-descriptions-item>
          <el-descriptions-item label="完成时间" :span="1">{{ parseTime(detail.finishDate, '{y}-{m}-{d}') || '-' }}</el-descriptions-item>
          <el-descriptions-item label="服务内容" :span="2">{{ detail.serviceContent || '-' }}</el-descriptions-item>
        </el-descriptions>
      </template>
    </page-detail-dialog>
  </div>
</template>

<script setup name="YgbAqInsurancePreventionService">
import { ref, onMounted, getCurrentInstance } from 'vue'
import { listPreventionProject, getPreventionProjectSummary } from '@/api/ygb/preventionProject'
import { parseTime } from '@/utils/yuegongbao'
import { optionselectEnterprise } from '@/api/ygb/enterprise'

const serviceStatusOptions = [
  { label: '未开始', value: '0' },
  { label: '进行中', value: '1' },
  { label: '已完成', value: '2' }
]

function valueOrDefault(value, fallback = 0) {
  return value === undefined || value === null ? fallback : value
}

function formatServiceStatus(value) {
  return serviceStatusOptions.find(item => item.value === String(value))?.label || value || '-'
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
  projectName: undefined,
  enterpriseId: undefined,
  serviceStatus: undefined
})

const summaryCards = ref([])

async function getList() {
  loading.value = true
  try {
    const res = await listPreventionProject(queryParams.value)
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
    const res = await getPreventionProjectSummary(queryParams.value)
    const data = res.data || res || {}
    summaryCards.value = [
      { key: 'totalProject', label: '项目总数', value: valueOrDefault(data.totalProject, 0), unit: '个', note: '当前范围', cardClass: '' },
      { key: 'ongoingCount', label: '进行中', value: valueOrDefault(data.ongoingCount, 0), unit: '个', note: '需跟踪', cardClass: 'warning' },
      { key: 'finishedCount', label: '已完成', value: valueOrDefault(data.finishedCount, 0), unit: '个', note: '服务落地', cardClass: 'success' },
      { key: 'coverageRate', label: '覆盖率', value: valueOrDefault(data.coverageRate, 0), unit: '%', note: '投保企业', cardClass: '' }
    ]
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
    projectName: undefined,
    enterpriseId: undefined,
    serviceStatus: undefined
  }
  getList()
  getSummary()
}

function handleSelectionChange() {}

function openDetail(row) {
  detail.value = row
  detailOpen.value = true
}

function handleExport() {
  proxy.download('ygb/injury/prevention/export', { ...queryParams.value }, `aq_insurance_prevention_service_${Date.now()}.xlsx`)
}

async function loadEnterpriseOptions() {
  try {
    const res = await optionselectEnterprise()
    enterpriseOptions.value = res.data || []
  } catch (e) {
    console.error(e)
  }
}

onMounted(() => {
  loadEnterpriseOptions()
  getList()
  getSummary()
})
</script>
