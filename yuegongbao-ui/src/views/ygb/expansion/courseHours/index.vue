<template>
  <div class="app-container ygb-page">
    <section class="gov-page-header ygb-page__header">
      <div>
        <p class="ygb-page__eyebrow">扩面减损</p>
        <h1 class="ygb-page__title">培训课程与学时管理</h1>
        <p class="ygb-page__desc">按培训类预防项目归集课程、周期、投入和完成评价，支撑课程学时管控。</p>
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
      <el-form ref="queryRef" :model="queryParams" :inline="true" v-show="showSearch">
        <el-form-item label="课程/项目" prop="projectName">
          <el-input v-model="queryParams.projectName" placeholder="请输入课程或项目名称" clearable style="width: 220px" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="企业" prop="enterpriseId">
          <el-select v-model="queryParams.enterpriseId" placeholder="请选择企业" clearable filterable style="width: 220px">
            <el-option v-for="item in enterpriseOptions" :key="item.enterpriseId" :label="item.enterpriseName" :value="item.enterpriseId" />
          </el-select>
        </el-form-item>
        <el-form-item label="课程状态" prop="projectStatus">
          <el-select v-model="queryParams.projectStatus" placeholder="请选择状态" clearable style="width: 160px">
            <el-option v-for="item in projectStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
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
          <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['ygb:preventionProject:export']">导出</el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>
    </el-card>

    <el-card class="table-card ygb-table-card" shadow="never">
      <template #header>
        <div class="ygb-card-head ygb-card-head--between">
          <div>
            <div class="ygb-card-head__title">课程与学时台账</div>
            <div class="ygb-card-head__desc">用项目周期、预算投入和验收评价承载课程计划、学时安排、完成核验等三级功能。</div>
          </div>
          <div class="ygb-card-head__desc">当前总量 {{ total }} 条</div>
        </div>
      </template>
      <el-table v-loading="loading" :data="list" @row-click="openDetail">
        <el-table-column label="课程ID" align="center" prop="projectId" width="90" />
        <el-table-column label="课程/项目名称" prop="projectName" min-width="240" show-overflow-tooltip />
        <el-table-column label="企业" prop="enterpriseName" min-width="220" show-overflow-tooltip />
        <el-table-column label="课程状态" align="center" width="110">
          <template #default="scope">
            <dict-tag :options="projectStatusOptions" :value="scope.row.projectStatus" />
          </template>
        </el-table-column>
        <el-table-column label="预计学时" align="center" width="100">
          <template #default="scope">{{ estimateHours(scope.row) }}</template>
        </el-table-column>
        <el-table-column label="预算投入" align="right" width="120">
          <template #default="scope">{{ formatMoney(scope.row.budgetAmount) }}</template>
        </el-table-column>
        <el-table-column label="实际投入" align="right" width="120">
          <template #default="scope">{{ formatMoney(scope.row.actualAmount) }}</template>
        </el-table-column>
        <el-table-column label="评价分" align="center" width="90">
          <template #default="scope">{{ scoreText(scope.row.evaluationScore) }}</template>
        </el-table-column>
        <el-table-column label="课程周期" min-width="190">
          <template #default="scope">{{ formatDate(scope.row.startDate) }} 至 {{ formatDate(scope.row.endDate) }}</template>
        </el-table-column>
        <el-table-column class-name="table-fill-column" min-width="1" />
        <el-table-column label="操作" fixed="right" align="center" width="100">
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

    <page-detail-dialog v-model="detailOpen" title="培训课程与学时详情" width="760px">
      <template v-if="detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="课程/项目名称">{{ detail.projectName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="企业">{{ detail.enterpriseName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="预计学时">{{ estimateHours(detail) }}</el-descriptions-item>
          <el-descriptions-item label="课程状态">{{ projectStatusLabel(detail.projectStatus) }}</el-descriptions-item>
          <el-descriptions-item label="预算投入">{{ formatMoney(detail.budgetAmount) }}</el-descriptions-item>
          <el-descriptions-item label="实际投入">{{ formatMoney(detail.actualAmount) }}</el-descriptions-item>
          <el-descriptions-item label="评价分">{{ scoreText(detail.evaluationScore) }}</el-descriptions-item>
          <el-descriptions-item label="课程周期">{{ formatDate(detail.startDate) }} 至 {{ formatDate(detail.endDate) }}</el-descriptions-item>
          <el-descriptions-item label="评价报告" :span="2">{{ detail.evaluationReport || '-' }}</el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ detail.remark || '-' }}</el-descriptions-item>
        </el-descriptions>
      </template>
    </page-detail-dialog>
  </div>
</template>

<script setup name="YgbExpansionCourseHours">
import { getCurrentInstance, onMounted, ref } from 'vue'
import { listPreventionProject, getPreventionProjectSummary } from '@/api/ygb/preventionProject'
import { optionselectEnterprise } from '@/api/ygb/enterprise'
import {
  formatMoney,
  projectStatusLabel,
  projectStatusOptions,
  scoreText,
  valueOrDefault
} from '@/views/preventionProject/usePreventionProjectPage'

const { proxy } = getCurrentInstance()
const loading = ref(false)
const list = ref([])
const total = ref(0)
const showSearch = ref(true)
const detailOpen = ref(false)
const detail = ref(null)
const enterpriseOptions = ref([])
const summaryCards = ref([])

const defaultQuery = {
  pageNum: 1,
  pageSize: 10,
  projectName: undefined,
  enterpriseId: undefined,
  projectStatus: undefined,
  projectType: '2'
}

const queryParams = ref({ ...defaultQuery })

function summaryQuery() {
  return {
    projectName: queryParams.value.projectName,
    enterpriseId: queryParams.value.enterpriseId,
    projectStatus: queryParams.value.projectStatus,
    projectType: '2'
  }
}

function buildSummaryCards(data = {}) {
  summaryCards.value = [
    { key: 'totalCount', label: '课程项目', value: valueOrDefault(data.totalCount, 0), unit: '门', note: '当前范围', cardClass: '' },
    { key: 'activeProjectCount', label: '执行中', value: valueOrDefault(data.activeProjectCount, 0), unit: '门', note: '立项/实施/验收', cardClass: '' },
    { key: 'totalBudget', label: '预算投入', value: formatMoney(data.totalBudget), unit: '元', note: '课程预算合计', cardClass: '' },
    { key: 'acceptancePendingCount', label: '待验收', value: valueOrDefault(data.acceptancePendingCount, 0), unit: '门', note: '需要核验学时', cardClass: 'warning' }
  ]
}

async function getList() {
  loading.value = true
  try {
    const [listRes, summaryRes] = await Promise.all([
      listPreventionProject({ ...queryParams.value, projectType: '2' }),
      getPreventionProjectSummary(summaryQuery())
    ])
    list.value = listRes.rows || []
    total.value = listRes.total || 0
    buildSummaryCards(summaryRes.data || {})
  } finally {
    loading.value = false
  }
}

function estimateHours(row) {
  if (!row?.startDate || !row?.endDate) {
    return '-'
  }
  const start = new Date(row.startDate)
  const end = new Date(row.endDate)
  if (Number.isNaN(start.getTime()) || Number.isNaN(end.getTime()) || end < start) {
    return '-'
  }
  const days = Math.max(1, Math.ceil((end - start) / 86400000) + 1)
  return `${days * 4} 小时`
}

function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

function resetQuery() {
  proxy.resetForm('queryRef')
  queryParams.value = { ...defaultQuery }
  getList()
}

function openDetail(row) {
  detail.value = row
  detailOpen.value = true
}

function handleExport() {
  proxy.download('ygb/injury/prevention/export', { ...queryParams.value, projectType: '2' }, `expansion_course_hours_${Date.now()}.xlsx`)
}

function formatDate(value) {
  return value ? proxy.parseTime(value, '{y}-{m}-{d}') : '-'
}

async function loadEnterpriseOptions() {
  const res = await optionselectEnterprise()
  enterpriseOptions.value = res.data || []
}

onMounted(() => {
  loadEnterpriseOptions()
  getList()
})
</script>
