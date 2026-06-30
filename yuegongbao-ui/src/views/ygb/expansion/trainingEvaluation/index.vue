<template>
  <div class="app-container ygb-page">
    <section class="gov-page-header ygb-page__header">
      <div>
        <p class="ygb-page__eyebrow">扩面减损</p>
        <h1 class="ygb-page__title">培训效果评估</h1>
        <p class="ygb-page__desc">聚焦培训类预防项目的验收评分、低分预警和评估报告沉淀，支撑培训前后风险数据评估。</p>
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
        <el-form-item label="项目名称" prop="projectName">
          <el-input v-model="queryParams.projectName" placeholder="请输入项目名称" clearable style="width: 220px" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="企业" prop="enterpriseId">
          <el-select v-model="queryParams.enterpriseId" placeholder="请选择企业" clearable filterable style="width: 220px">
            <el-option v-for="item in enterpriseOptions" :key="item.enterpriseId" :label="item.enterpriseName" :value="item.enterpriseId" />
          </el-select>
        </el-form-item>
        <el-form-item label="评估状态" prop="evaluationState">
          <el-select v-model="queryParams.evaluationState" placeholder="请选择评估状态" clearable style="width: 160px">
            <el-option label="待评估" value="pending" />
            <el-option label="已评估" value="done" />
            <el-option label="低分整改" value="lowScore" />
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
            <div class="ygb-card-head__title">培训效果评估台账</div>
            <div class="ygb-card-head__desc">把评分、报告、验收阶段和低分整改合并在二级菜单内完成。</div>
          </div>
          <div class="ygb-card-head__desc">当前总量 {{ total }} 条</div>
        </div>
      </template>
      <el-table v-loading="loading" :data="filteredList" @row-click="openDetail">
        <el-table-column label="项目ID" align="center" prop="projectId" width="90" />
        <el-table-column label="项目名称" prop="projectName" min-width="240" show-overflow-tooltip />
        <el-table-column label="企业" prop="enterpriseName" min-width="220" show-overflow-tooltip />
        <el-table-column label="评估状态" align="center" width="110">
          <template #default="scope">
            <el-tag :type="evaluationTagType(scope.row)" effect="plain">{{ evaluationStateLabel(scope.row) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="项目状态" align="center" width="110">
          <template #default="scope">
            <dict-tag :options="projectStatusOptions" :value="scope.row.projectStatus" />
          </template>
        </el-table-column>
        <el-table-column label="评价分" align="center" width="90">
          <template #default="scope">{{ scoreText(scope.row.evaluationScore) }}</template>
        </el-table-column>
        <el-table-column label="实际投入" align="right" width="120">
          <template #default="scope">{{ formatMoney(scope.row.actualAmount) }}</template>
        </el-table-column>
        <el-table-column label="评价报告" min-width="220" show-overflow-tooltip>
          <template #default="scope">{{ scope.row.evaluationReport || '-' }}</template>
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

    <page-detail-dialog v-model="detailOpen" title="培训效果评估详情" width="760px">
      <template v-if="detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="项目名称">{{ detail.projectName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="企业">{{ detail.enterpriseName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="评估状态">{{ evaluationStateLabel(detail) }}</el-descriptions-item>
          <el-descriptions-item label="项目状态">{{ projectStatusLabel(detail.projectStatus) }}</el-descriptions-item>
          <el-descriptions-item label="评价分">{{ scoreText(detail.evaluationScore) }}</el-descriptions-item>
          <el-descriptions-item label="实际投入">{{ formatMoney(detail.actualAmount) }}</el-descriptions-item>
          <el-descriptions-item label="实施周期">{{ formatDate(detail.startDate) }} 至 {{ formatDate(detail.endDate) }}</el-descriptions-item>
          <el-descriptions-item label="评价报告" :span="2">{{ detail.evaluationReport || '-' }}</el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ detail.remark || '-' }}</el-descriptions-item>
        </el-descriptions>
      </template>
    </page-detail-dialog>
  </div>
</template>

<script setup name="YgbExpansionTrainingEvaluation">
import { computed, getCurrentInstance, onMounted, ref } from 'vue'
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
  evaluationState: undefined,
  projectType: '2'
}

const queryParams = ref({ ...defaultQuery })

const filteredList = computed(() => {
  if (!queryParams.value.evaluationState) {
    return list.value
  }
  return list.value.filter(row => rowMatchesEvaluationState(row, queryParams.value.evaluationState))
})

function summaryQuery() {
  return {
    projectName: queryParams.value.projectName,
    enterpriseId: queryParams.value.enterpriseId,
    projectType: '2'
  }
}

function buildSummaryCards(data = {}) {
  summaryCards.value = [
    { key: 'totalCount', label: '评估项目', value: valueOrDefault(data.totalCount, 0), unit: '个', note: '当前范围', cardClass: '' },
    { key: 'acceptancePendingCount', label: '待验收', value: valueOrDefault(data.acceptancePendingCount, 0), unit: '个', note: '验收阶段', cardClass: 'warning' },
    { key: 'lowScoreCount', label: '低分整改', value: valueOrDefault(data.lowScoreCount, 0), unit: '个', note: '评价分低于 80', cardClass: 'danger' },
    { key: 'totalBudget', label: '预算投入', value: formatMoney(data.totalBudget), unit: '元', note: '培训预算合计', cardClass: '' }
  ]
}

async function getList() {
  loading.value = true
  try {
    const [listRes, summaryRes] = await Promise.all([
      listPreventionProject({ ...queryParams.value, evaluationState: undefined, projectType: '2' }),
      getPreventionProjectSummary(summaryQuery())
    ])
    list.value = listRes.rows || []
    total.value = listRes.total || 0
    buildSummaryCards(summaryRes.data || {})
  } finally {
    loading.value = false
  }
}

function hasEvaluation(row) {
  return row?.evaluationScore !== undefined && row?.evaluationScore !== null && row?.evaluationScore !== ''
}

function isLowScore(row) {
  const score = Number(row?.evaluationScore || 0)
  return score > 0 && score < 80
}

function rowMatchesEvaluationState(row, state) {
  if (state === 'pending') {
    return !hasEvaluation(row) || String(row.projectStatus) === '3'
  }
  if (state === 'done') {
    return hasEvaluation(row) && String(row.projectStatus) === '4'
  }
  if (state === 'lowScore') {
    return isLowScore(row)
  }
  return true
}

function evaluationStateLabel(row) {
  if (isLowScore(row)) {
    return '低分整改'
  }
  if (hasEvaluation(row) && String(row.projectStatus) === '4') {
    return '已评估'
  }
  return '待评估'
}

function evaluationTagType(row) {
  if (isLowScore(row)) {
    return 'danger'
  }
  if (hasEvaluation(row) && String(row.projectStatus) === '4') {
    return 'success'
  }
  return 'warning'
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
  proxy.download('ygb/injury/prevention/export', { ...queryParams.value, evaluationState: undefined, projectType: '2' }, `expansion_training_evaluation_${Date.now()}.xlsx`)
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
