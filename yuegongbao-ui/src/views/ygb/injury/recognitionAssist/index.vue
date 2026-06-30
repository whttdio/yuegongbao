<template>
  <div class="app-container ygb-page">
    <section class="gov-page-header ygb-page__header">
      <div>
        <p class="ygb-page__eyebrow">工伤监管</p>
        <h1 class="ygb-page__title">工伤认定辅助</h1>
        <p class="ygb-page__desc">围绕工伤事件认定进度、材料完整性和办理状态提供辅助核查台账。</p>
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
        <el-form-item label="人员姓名" prop="personName">
          <el-input v-model="queryParams.personName" placeholder="请输入人员姓名" clearable style="width: 180px" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="企业" prop="enterpriseId">
          <el-select v-model="queryParams.enterpriseId" placeholder="请选择企业" clearable filterable style="width: 200px">
            <el-option v-for="item in enterpriseOptions" :key="item.enterpriseId" :label="item.enterpriseName" :value="item.enterpriseId" />
          </el-select>
        </el-form-item>
        <el-form-item label="事件状态" prop="injuryStatus">
          <el-select v-model="queryParams.injuryStatus" placeholder="请选择事件状态" clearable style="width: 150px">
            <el-option v-for="item in injuryStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
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
          <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['ygb:injuryEvent:export']">导出</el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>
    </el-card>

    <el-card class="table-card ygb-table-card" shadow="never">
      <template #header>
        <div class="ygb-card-head ygb-card-head--between">
          <div>
            <div class="ygb-card-head__title">工伤认定辅助台账</div>
            <div class="ygb-card-head__desc">按工伤事件生成认定辅助记录，辅助核查材料与办理进度</div>
          </div>
          <div class="ygb-card-head__desc">当前总量 {{ total }} 条</div>
        </div>
      </template>
      <el-table v-loading="loading" :data="list">
        <el-table-column label="记录ID" align="center" prop="recordId" width="90" />
        <el-table-column label="记录名称" align="center" prop="recordName" min-width="150" show-overflow-tooltip />
        <el-table-column label="企业" align="center" prop="enterpriseName" min-width="200" show-overflow-tooltip />
        <el-table-column label="人员" align="center" prop="personName" width="120" />
        <el-table-column label="事件状态" align="center" prop="categoryCode" width="120">
          <template #default="scope">
            <dict-tag :options="injuryStatusOptions" :value="scope.row.categoryCode" />
          </template>
        </el-table-column>
        <el-table-column label="办理状态" align="center" prop="workflowStatus" width="120">
          <template #default="scope">
            <dict-tag :options="workflowStatusOptions" :value="scope.row.workflowStatus" />
          </template>
        </el-table-column>
        <el-table-column label="来源" align="center" prop="sourceLabel" width="120" />
        <el-table-column label="辅助提示" align="left" prop="remark" min-width="240" show-overflow-tooltip />
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

    <page-detail-dialog v-model="detailOpen" title="工伤认定辅助详情" width="720px">
      <template v-if="detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="记录ID" :span="1">{{ detail.recordId || '-' }}</el-descriptions-item>
          <el-descriptions-item label="记录名称" :span="1">{{ detail.recordName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="企业" :span="1">{{ detail.enterpriseName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="人员" :span="1">{{ detail.personName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="事件ID" :span="1">{{ detail.relatedId || '-' }}</el-descriptions-item>
          <el-descriptions-item label="事件状态" :span="1">{{ formatInjuryStatus(detail.categoryCode) }}</el-descriptions-item>
          <el-descriptions-item label="办理状态" :span="1">{{ formatWorkflowStatus(detail.workflowStatus) }}</el-descriptions-item>
          <el-descriptions-item label="来源" :span="1">{{ detail.sourceLabel || '-' }}</el-descriptions-item>
          <el-descriptions-item label="辅助提示" :span="2">{{ detail.remark || '-' }}</el-descriptions-item>
        </el-descriptions>
      </template>
    </page-detail-dialog>
  </div>
</template>

<script setup name="YgbInjuryRecognitionAssist">
import { ref, onMounted, getCurrentInstance } from 'vue'
import { listRecognitionAssist, getRecognitionAssist, getRecognitionAssistSummary } from '@/api/ygb/injuryEvent'
import { optionselectEnterprise } from '@/api/ygb/enterprise'

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

const injuryStatusOptions = [
  { label: '已报告', value: '0' },
  { label: '认定中', value: '1' },
  { label: '已认定', value: '2' },
  { label: '待遇申领中', value: '3' },
  { label: '已完结', value: '4' }
]

const workflowStatusOptions = [
  { label: '待处理', value: 'pending' },
  { label: '办理中', value: 'processing' },
  { label: '已逾期', value: 'overdue' },
  { label: '已关闭', value: 'closed' }
]

const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  personName: undefined,
  enterpriseId: undefined,
  injuryStatus: undefined
})

const summaryCards = ref([])

async function getList() {
  loading.value = true
  try {
    const res = await listRecognitionAssist(queryParams.value)
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
    const res = await getRecognitionAssistSummary(queryParams.value)
    const data = res.data || res || {}
    summaryCards.value = [
      { key: 'totalEvent', label: '认定事件', value: valueOrDefault(data.totalCount, 0), unit: '件', note: '当前范围', cardClass: '' },
      { key: 'pendingCount', label: '待补充材料', value: valueOrDefault(data.pendingCount, 0), unit: '件', note: '待报告或待处理', cardClass: 'warning' },
      { key: 'recognizingCount', label: '认定中', value: valueOrDefault(data.recognizingCount, 0), unit: '件', note: '正在办理', cardClass: '' },
      { key: 'finishedCount', label: '已办结', value: valueOrDefault(data.finishedCount, 0), unit: '件', note: '流程闭环', cardClass: 'success' }
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
    personName: undefined,
    enterpriseId: undefined,
    injuryStatus: undefined
  }
  getList()
  getSummary()
}

async function openDetail(row) {
  detailOpen.value = true
  detail.value = row
  try {
    const eventId = row.relatedId || row.recordId
    if (!eventId) return
    const res = await getRecognitionAssist(eventId)
    detail.value = res.data || row
  } catch (e) {
    console.error(e)
  }
}

function handleExport() {
  proxy.download('ygb/injury/event/recognitionAssist/export', { ...queryParams.value }, `injury_recognition_assist_${Date.now()}.xlsx`)
}

function formatInjuryStatus(value) {
  return injuryStatusOptions.find(item => item.value === value)?.label || '-'
}

function formatWorkflowStatus(value) {
  return workflowStatusOptions.find(item => item.value === value)?.label || '-'
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
