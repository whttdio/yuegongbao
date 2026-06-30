<template>
  <div class="app-container ygb-page">
    <section class="gov-page-header ygb-page__header">
      <div>
        <p class="ygb-page__eyebrow">运营后台</p>
        <h1 class="ygb-page__title">数据统计与分析</h1>
        <p class="ygb-page__desc">基于运营招聘统计记录查看台账、流程状态和归档情况。</p>
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
        <el-form-item label="记录名称" prop="recordName">
          <el-input v-model="queryParams.recordName" placeholder="请输入记录名称" clearable style="width: 180px" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="统计月份" prop="statMonth">
          <el-date-picker v-model="queryParams.statMonth" type="month" value-format="YYYY-MM" format="YYYY-MM" placeholder="请选择月份" style="width: 150px" />
        </el-form-item>
        <el-form-item label="流程状态" prop="workflowStatus">
          <el-select v-model="queryParams.workflowStatus" placeholder="请选择流程状态" clearable style="width: 150px">
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
          <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['ygb:operationRecruitStats:export']">导出</el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="refreshAll" />
      </el-row>
    </el-card>

    <el-card class="table-card ygb-table-card" shadow="never">
      <template #header>
        <div class="ygb-card-head ygb-card-head--between">
          <div>
            <div class="ygb-card-head__title">招聘统计分析台账</div>
            <div class="ygb-card-head__desc">汇总招聘发布、投递转化和运营趋势</div>
          </div>
          <div class="ygb-card-head__desc">当前总量 {{ total }} 条</div>
        </div>
      </template>
      <el-table v-loading="loading" :data="list">
        <el-table-column label="ID" align="center" prop="recordId" width="80" />
        <el-table-column label="记录名称" align="center" prop="recordName" min-width="180" show-overflow-tooltip />
        <el-table-column label="统计月份" align="center" prop="statMonth" width="110" />
        <el-table-column label="分类编码" align="center" prop="categoryCode" width="120" />
        <el-table-column label="流程状态" align="center" prop="workflowStatus" width="120">
          <template #default="scope">
            <dict-tag :options="workflowStatusOptions" :value="scope.row.workflowStatus" />
          </template>
        </el-table-column>
        <el-table-column label="启停状态" align="center" prop="status" width="100" />
        <el-table-column label="来源" align="center" prop="sourceLabel" width="140" show-overflow-tooltip />
        <el-table-column label="备注" align="left" prop="remark" min-width="220" show-overflow-tooltip />
        <el-table-column label="更新时间" align="center" width="170">
          <template #default="scope">
            <span>{{ parseTime(scope.row.updateTime || scope.row.createTime, '{y}-{m}-{d} {h}:{i}:{s}') || '-' }}</span>
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

    <page-detail-dialog v-model="detailOpen" title="数据统计与分析详情" width="760px">
      <template v-if="detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="记录名称">{{ detail.recordName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="统计月份">{{ detail.statMonth || '-' }}</el-descriptions-item>
          <el-descriptions-item label="分类编码">{{ detail.categoryCode || '-' }}</el-descriptions-item>
          <el-descriptions-item label="流程状态">{{ formatWorkflowStatus(detail.workflowStatus) }}</el-descriptions-item>
          <el-descriptions-item label="启停状态">{{ detail.status || '-' }}</el-descriptions-item>
          <el-descriptions-item label="来源">{{ detail.sourceLabel || '-' }}</el-descriptions-item>
          <el-descriptions-item label="关联编码">{{ detail.relatedCode || '-' }}</el-descriptions-item>
          <el-descriptions-item label="更新时间">{{ parseTime(detail.updateTime || detail.createTime, '{y}-{m}-{d} {h}:{i}:{s}') || '-' }}</el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ detail.remark || '-' }}</el-descriptions-item>
          <el-descriptions-item label="扩展数据" :span="2">{{ detail.payloadJson || '-' }}</el-descriptions-item>
        </el-descriptions>
      </template>
    </page-detail-dialog>
  </div>
</template>

<script setup name="YgbOperationDataAnalysis">
import { ref, onMounted, getCurrentInstance } from 'vue'
import { listOperationModule, getOperationModuleSummary } from '@/api/ygb/operation'
import { parseTime } from '@/utils/yuegongbao'

function valueOrDefault(value, fallback = 0) {
  return value === undefined || value === null ? fallback : value
}

const loading = ref(false)
const list = ref([])
const total = ref(0)
const showSearch = ref(true)
const detailOpen = ref(false)
const detail = ref(null)
const { proxy } = getCurrentInstance()

const workflowStatusOptions = [
  { label: '草稿', value: 'draft' },
  { label: '待处理', value: 'pending' },
  { label: '处理中', value: 'processing' },
  { label: '已关闭', value: 'closed' },
  { label: '已驳回', value: 'rejected' },
  { label: '已逾期', value: 'overdue' }
]

const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  recordName: undefined,
  statMonth: undefined,
  workflowStatus: undefined
})

const summaryCards = ref([])

async function getList() {
  loading.value = true
  try {
    const res = await listOperationModule('recruitStats', queryParams.value)
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
    const res = await getOperationModuleSummary('recruitStats', queryParams.value)
    const data = res.data || res || {}
    summaryCards.value = [
      { key: 'totalCount', label: '统计记录', value: valueOrDefault(data.totalCount, 0), unit: '条', note: '当前范围', cardClass: '' },
      { key: 'enabledCount', label: '启用记录', value: valueOrDefault(data.enabledCount, 0), unit: '条', note: '正常可用', cardClass: 'success' },
      { key: 'processingCount', label: '处理中', value: valueOrDefault(data.processingCount, 0), unit: '条', note: '正在跟进', cardClass: 'warning' },
      { key: 'closedCount', label: '已关闭', value: valueOrDefault(data.closedCount, 0), unit: '条', note: '已归档', cardClass: '' }
    ]
  } catch (e) {
    console.error(e)
  }
}

function refreshAll() {
  getList()
  getSummary()
}

function handleQuery() {
  queryParams.value.pageNum = 1
  refreshAll()
}

function resetQuery() {
  queryParams.value = {
    pageNum: 1,
    pageSize: 10,
    recordName: undefined,
    statMonth: undefined,
    workflowStatus: undefined
  }
  refreshAll()
}

function openDetail(row) {
  detail.value = row
  detailOpen.value = true
}

function handleExport() {
  proxy.download('ygb/operation/recruitStats/export', { ...queryParams.value }, `operation_data_analysis_${Date.now()}.xlsx`)
}

function formatWorkflowStatus(value) {
  return workflowStatusOptions.find(item => item.value === value)?.label || '-'
}

onMounted(() => {
  refreshAll()
})
</script>
