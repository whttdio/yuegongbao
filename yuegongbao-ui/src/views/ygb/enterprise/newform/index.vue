<template>
  <div class="app-container ygb-page">
    <section class="gov-page-header ygb-page__header">
      <div>
        <p class="ygb-page__eyebrow">单位管理</p>
        <h1 class="ygb-page__title">新业态平台企业管理</h1>
        <p class="ygb-page__desc">管理网约车、外卖、快递等新业态平台企业信息，支撑新业态监管。</p>
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
        <el-form-item label="平台名称" prop="platformName">
          <el-input v-model="queryParams.platformName" placeholder="请输入平台名称" clearable style="width: 200px" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="企业" prop="enterpriseId">
          <el-select v-model="queryParams.enterpriseId" placeholder="请选择企业" clearable filterable style="width: 200px">
            <el-option v-for="item in enterpriseOptions" :key="item.enterpriseId" :label="item.enterpriseName" :value="item.enterpriseId" />
          </el-select>
        </el-form-item>
        <el-form-item label="平台类型" prop="platformType">
          <el-select v-model="queryParams.platformType" placeholder="请选择平台类型" clearable style="width: 140px">
            <el-option label="网约车" value="taxi" />
            <el-option label="外卖" value="food" />
            <el-option label="快递" value="express" />
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
          <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['ygb:newformPlatform:export']">导出</el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>
    </el-card>

    <el-card class="table-card ygb-table-card" shadow="never">
      <template #header>
        <div class="ygb-card-head ygb-card-head--between">
          <div>
            <div class="ygb-card-head__title">新业态平台企业管理台账</div>
            <div class="ygb-card-head__desc">跟踪新业态企业备案、经营形态、用工规模和监管画像。</div>
          </div>
          <div class="ygb-card-head__desc">当前总量 {{ total }} 条</div>
        </div>
      </template>
      <el-table v-loading="loading" :data="list" @selection-change="handleSelectionChange">
        <el-table-column label="ID" align="center" prop="platformId" width="80" />
        <el-table-column label="平台名称" align="center" prop="platformName" min-width="200" />
        <el-table-column label="企业" align="center" prop="enterpriseName" min-width="200" />
        <el-table-column label="平台类型" align="center" prop="platformType" width="120" />
        <el-table-column label="注册人员" align="center" prop="registerCount" width="100" />
        <el-table-column label="活跃人员" align="center" prop="activeCount" width="100" />
        <el-table-column label="更新时间" align="center" width="170">
          <template #default="scope">
            <span>{{ parseTime(scope.row.updateTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
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

    <page-detail-dialog v-model="detailOpen" title="新业态平台企业管理详情" width="720px">
      <template v-if="detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="平台名称" :span="1">{{ detail.platformName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="企业" :span="1">{{ detail.enterpriseName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="平台类型" :span="1">{{ detail.platformType || '-' }}</el-descriptions-item>
          <el-descriptions-item label="注册人员" :span="1">{{ detail.registerCount || '-' }}</el-descriptions-item>
          <el-descriptions-item label="活跃人员" :span="1">{{ detail.activeCount || '-' }}</el-descriptions-item>
          <el-descriptions-item label="更新时间" :span="1">{{ parseTime(detail.updateTime, '{y}-{m}-{d} {h}:{i}:{s}') || '-' }}</el-descriptions-item>
          <el-descriptions-item label="平台说明" :span="2">{{ detail.platformDesc || '-' }}</el-descriptions-item>
        </el-descriptions>
      </template>
    </page-detail-dialog>
  </div>
</template>

<script setup name="YgbEnterpriseNewform">
import { ref, onMounted, getCurrentInstance } from 'vue'
import { listEnterpriseSubmodule, getEnterpriseSubmoduleSummary } from '@/api/ygb/enterprise'
import { listNewformPlatform, getNewformPlatformSummary } from '@/api/ygb/newformWorker'
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
  platformName: undefined,
  enterpriseId: undefined,
  platformType: undefined
})

const summaryCards = ref([])

async function getList() {
  loading.value = true
  try {
    const res = await listNewformPlatform(queryParams.value)
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
    const res = await getNewformPlatformSummary(queryParams.value)
    const data = res.data || res || {}
    summaryCards.value = [
      { key: 'totalPlatform', label: '平台总数', value: valueOrDefault(data.totalPlatform, 0), unit: '家', note: '当前范围', cardClass: '' },
      { key: 'totalRegister', label: '注册人员', value: valueOrDefault(data.totalRegister, 0), unit: '人', note: '累计', cardClass: '' },
      { key: 'totalActive', label: '活跃人员', value: valueOrDefault(data.totalActive, 0), unit: '人', note: '累计', cardClass: 'primary' },
      { key: 'monitoredCount', label: '已监测', value: valueOrDefault(data.monitoredCount, 0), unit: '家', note: '接入监管', cardClass: 'success' }
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
  platformName: undefined,
  enterpriseId: undefined,
  platformType: undefined
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
  proxy.download('ygb/newform/platform/export', { ...queryParams.value }, `enterprise_newform_platform_${Date.now()}.xlsx`)
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
