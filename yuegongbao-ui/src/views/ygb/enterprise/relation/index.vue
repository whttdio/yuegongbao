<template>
  <div class="app-container ygb-page">
    <section class="gov-page-header ygb-page__header">
      <div>
        <p class="ygb-page__eyebrow">单位管理</p>
        <h1 class="ygb-page__title">派遣/用工关联关系</h1>
        <p class="ygb-page__desc">基于企业扩展台账维护派遣、用工关联记录，支撑关系留痕、流程跟进和归档查询。</p>
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
        <el-form-item label="企业" prop="enterpriseId">
          <el-select v-model="queryParams.enterpriseId" placeholder="请选择企业" clearable filterable style="width: 200px">
            <el-option v-for="item in enterpriseOptions" :key="item.enterpriseId" :label="item.enterpriseName" :value="item.enterpriseId" />
          </el-select>
        </el-form-item>
        <el-form-item label="关联名称" prop="recordName">
          <el-input v-model="queryParams.recordName" placeholder="请输入关联名称" clearable style="width: 180px" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="流程状态" prop="workflowStatus">
          <el-select v-model="queryParams.workflowStatus" placeholder="请选择流程状态" clearable style="width: 140px">
            <el-option v-for="item in workflowStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
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
          <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['ygb:enterprise:export']">导出</el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>
    </el-card>

    <el-card class="table-card ygb-table-card" shadow="never">
      <template #header>
        <div class="ygb-card-head ygb-card-head--between">
          <div>
            <div class="ygb-card-head__title">派遣/用工关联关系台账</div>
            <div class="ygb-card-head__desc">按企业扩展台账组织的关联关系记录</div>
          </div>
          <div class="ygb-card-head__desc">当前总量 {{ total }} 条</div>
        </div>
      </template>
      <el-table v-loading="loading" :data="list">
        <el-table-column label="ID" align="center" prop="recordId" width="80" />
        <el-table-column label="关联名称" align="center" prop="recordName" min-width="220" show-overflow-tooltip />
        <el-table-column label="企业" align="center" prop="enterpriseName" min-width="220" show-overflow-tooltip />
        <el-table-column label="人员" align="center" prop="personName" width="120" />
        <el-table-column label="流程状态" align="center" prop="workflowStatus" width="120">
          <template #default="scope">
            <dict-tag :options="workflowStatusOptions" :value="scope.row.workflowStatus" />
          </template>
        </el-table-column>
        <el-table-column label="启停状态" align="center" prop="status" width="100">
          <template #default="scope">
            <dict-tag :options="statusOptions" :value="scope.row.status" />
          </template>
        </el-table-column>
        <el-table-column label="来源" align="center" prop="sourceLabel" width="120" show-overflow-tooltip />
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

    <page-detail-dialog v-model="detailOpen" title="派遣/用工关联关系详情" width="720px">
      <template v-if="detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="关联名称" :span="1">{{ detail.recordName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="企业" :span="1">{{ detail.enterpriseName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="人员" :span="1">{{ detail.personName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="流程状态" :span="1">{{ formatWorkflowStatus(detail.workflowStatus) }}</el-descriptions-item>
          <el-descriptions-item label="启停状态" :span="1">{{ formatStatus(detail.status) }}</el-descriptions-item>
          <el-descriptions-item label="来源" :span="1">{{ detail.sourceLabel || '-' }}</el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ detail.remark || '-' }}</el-descriptions-item>
        </el-descriptions>
      </template>
    </page-detail-dialog>
  </div>
</template>

<script setup name="YgbEnterpriseRelation">
import { ref, onMounted, getCurrentInstance } from 'vue'
import { listEnterpriseSubmodule, getEnterpriseSubmoduleSummary, optionselectEnterprise } from '@/api/ygb/enterprise'

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

const workflowStatusOptions = [
  { label: '草稿', value: 'draft' },
  { label: '待处理', value: 'pending' },
  { label: '处理中', value: 'processing' },
  { label: '已关闭', value: 'closed' },
  { label: '已驳回', value: 'rejected' },
  { label: '已逾期', value: 'overdue' }
]

const statusOptions = [
  { label: '启用', value: '0' },
  { label: '停用', value: '1' }
]

const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  enterpriseId: undefined,
  recordName: undefined,
  workflowStatus: undefined
})

const summaryCards = ref([])

async function getList() {
  loading.value = true
  try {
    const res = await listEnterpriseSubmodule('relation', queryParams.value)
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
    const res = await getEnterpriseSubmoduleSummary('relation', queryParams.value)
    const data = res.data || res || {}
    summaryCards.value = [
      { key: 'totalCount', label: '关联记录', value: valueOrDefault(data.totalCount, 0), unit: '条', note: '当前范围', cardClass: '' },
      { key: 'enabledCount', label: '启用记录', value: valueOrDefault(data.enabledCount, 0), unit: '条', note: '正常可用', cardClass: 'success' },
      { key: 'pendingCount', label: '待处理', value: valueOrDefault(data.pendingCount, 0), unit: '条', note: '需要跟进', cardClass: 'warning' },
      { key: 'closedCount', label: '已关闭', value: valueOrDefault(data.closedCount, 0), unit: '条', note: '已归档', cardClass: '' }
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
    enterpriseId: undefined,
    recordName: undefined,
    workflowStatus: undefined
  }
  getList()
  getSummary()
}

function openDetail(row) {
  detail.value = row
  detailOpen.value = true
}

function handleExport() {
  proxy.download('ygb/enterprise/relation/export', { ...queryParams.value }, `enterprise_relation_${Date.now()}.xlsx`)
}

function formatWorkflowStatus(value) {
  return workflowStatusOptions.find(item => item.value === value)?.label || '-'
}

function formatStatus(value) {
  return statusOptions.find(item => item.value === value)?.label || '-'
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
