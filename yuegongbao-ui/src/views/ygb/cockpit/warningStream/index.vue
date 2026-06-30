<template>
  <div class="app-container ygb-page">
    <section class="gov-page-header ygb-page__header">
      <div>
        <p class="ygb-page__eyebrow">驾驶舱</p>
        <h1 class="ygb-page__title">实时预警流</h1>
        <p class="ygb-page__desc">滚动展示平台实时产生的预警工单，支持按级别、来源、状态快速筛选。</p>
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
        <el-form-item label="预警级别" prop="warnLevel">
          <el-select v-model="queryParams.warnLevel" placeholder="请选择预警级别" clearable style="width: 140px">
            <el-option v-for="item in warnLevelOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="来源模块" prop="sourceModule">
          <el-select v-model="queryParams.sourceModule" placeholder="请选择来源模块" clearable style="width: 150px">
            <el-option v-for="item in sourceModuleOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="工单状态" prop="warnStatus">
          <el-select v-model="queryParams.warnStatus" placeholder="请选择工单状态" clearable style="width: 150px">
            <el-option v-for="item in warnStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
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
          <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['ygb:cockpit:export']">导出</el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>
    </el-card>

    <el-card class="table-card ygb-table-card" shadow="never">
      <template #header>
        <div class="ygb-card-head ygb-card-head--between">
          <div>
            <div class="ygb-card-head__title">实时预警流台账</div>
            <div class="ygb-card-head__desc">滚动展示预警事件来源、等级、状态和处置进度，支撑驾驶舱实时研判。</div>
          </div>
          <div class="ygb-card-head__desc">当前总量 {{ total }} 条</div>
        </div>
      </template>
      <el-table v-loading="loading" :data="list" @selection-change="handleSelectionChange">
        <el-table-column label="预警ID" align="center" prop="warnId" width="90" />
        <el-table-column label="级别" align="center" prop="warnLevel" width="90">
          <template #default="scope">
            <dict-tag :options="warnLevelOptions" :value="scope.row.warnLevel" />
          </template>
        </el-table-column>
        <el-table-column label="类型" align="center" prop="warnType" min-width="160" />
        <el-table-column label="来源" align="center" prop="sourceModule" width="110">
          <template #default="scope">{{ sourceModuleLabel(scope.row.sourceModule) }}</template>
        </el-table-column>
        <el-table-column label="企业" align="center" prop="enterpriseName" min-width="200" />
        <el-table-column label="内容" align="center" prop="content" min-width="260" />
        <el-table-column label="状态" align="center" prop="warnStatus" width="100">
          <template #default="scope">
            <dict-tag :options="warnStatusOptions" :value="scope.row.warnStatus" />
          </template>
        </el-table-column>
        <el-table-column label="创建时间" align="center" width="170">
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

    <page-detail-dialog v-model="detailOpen" title="实时预警流详情" width="720px">
      <template v-if="detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="预警ID" :span="1">{{ detail.warnId || '-' }}</el-descriptions-item>
          <el-descriptions-item label="级别" :span="1">{{ warnLevelLabel(detail.warnLevel) }}</el-descriptions-item>
          <el-descriptions-item label="类型" :span="1">{{ detail.warnType || '-' }}</el-descriptions-item>
          <el-descriptions-item label="来源模块" :span="1">{{ sourceModuleLabel(detail.sourceModule) }}</el-descriptions-item>
          <el-descriptions-item label="企业" :span="1">{{ detail.enterpriseName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="区域" :span="1">{{ detail.regionName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="状态" :span="1">{{ warnStatusLabel(detail.warnStatus) }}</el-descriptions-item>
          <el-descriptions-item label="创建时间" :span="1">{{ parseTime(detail.createTime, '{y}-{m}-{d} {h}:{i}:{s}') || '-' }}</el-descriptions-item>
          <el-descriptions-item label="预警内容" :span="2">{{ detail.content || '-' }}</el-descriptions-item>
        </el-descriptions>
      </template>
    </page-detail-dialog>
  </div>
</template>

<script setup name="YgbCockpitWarningStream">
import { ref, onMounted, getCurrentInstance } from 'vue'
import { listWarning, getWarningSummary } from '@/api/ygb/warning'
import { parseTime } from '@/utils/yuegongbao'
import { optionselectEnterprise } from '@/api/ygb/enterprise'
import {
  normalizeSourceModule,
  optionLabel,
  sourceModuleOptions,
  warnLevelOptions,
  warnStatusOptions
} from '@/views/warning/useWarningPage'

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
  warnLevel: undefined,
  sourceModule: undefined,
  warnStatus: undefined
})

const summaryCards = ref([])

async function getList() {
  loading.value = true
  try {
    normalizeQueryParams()
    const res = await listWarning(queryParams.value)
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
    normalizeQueryParams()
    const res = await getWarningSummary(queryParams.value)
    const data = res.data || res || {}
    summaryCards.value = [
      { key: 'totalWarning', label: '预警总数', value: valueOrDefault(data.totalWarning, 0), unit: '条', note: '累计产生', cardClass: '' },
      { key: 'pendingWarning', label: '待处置', value: valueOrDefault(data.pendingWarning, 0), unit: '条', note: '需尽快闭环', cardClass: 'warning' },
      { key: 'highRiskWarning', label: '红码预警', value: valueOrDefault(data.highRiskWarning, 0), unit: '条', note: '高风险信号', cardClass: 'danger' },
      { key: 'resolvedWarning', label: '已办结', value: valueOrDefault(data.resolvedWarning, 0), unit: '条', note: '本周办结', cardClass: 'success' }
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
  warnLevel: undefined,
  sourceModule: undefined,
  warnStatus: undefined
  }
  getList()
  getSummary()
}

function handleSelectionChange(selection) {
}

function normalizeQueryParams() {
  queryParams.value.sourceModule = normalizeSourceModule(queryParams.value.sourceModule)
}

function warnLevelLabel(value) {
  return optionLabel(warnLevelOptions, value, value || '-')
}

function warnStatusLabel(value) {
  return optionLabel(warnStatusOptions, value, value || '-')
}

function sourceModuleLabel(value) {
  return optionLabel(sourceModuleOptions, normalizeSourceModule(value), value || '-')
}

function openDetail(row) {
  detail.value = row
  detailOpen.value = true
}

function handleExport() {
  proxy.download('ygb/cockpit/export', { ...queryParams.value }, `cockpit_warning_stream_${Date.now()}.xlsx`)
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
