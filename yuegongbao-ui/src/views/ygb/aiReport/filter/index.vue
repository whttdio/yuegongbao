<template>
  <div class="app-container ygb-page">
    <section class="gov-page-header ygb-page__header">
      <div>
        <p class="ygb-page__eyebrow">AI 监测报告</p>
        <h1 class="ygb-page__title">多维筛选与穿透</h1>
        <p class="ygb-page__desc">按区域、周期、企业类型、风险等级和评分维度筛选报告，快速进入报告、任务和订阅办理链路。</p>
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
        <el-form-item label="报告类型" prop="reportType">
          <el-select v-model="queryParams.reportType" clearable style="width: 150px">
            <el-option v-for="item in reportTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="统计区间">
          <el-date-picker
            v-model="queryRange"
            type="daterange"
            value-format="YYYY-MM-DD"
            format="YYYY-MM-DD"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            style="width: 280px"
          />
        </el-form-item>
        <el-form-item label="行政区划" prop="regionCode">
          <el-select v-model="queryParams.regionCode" clearable filterable style="width: 180px">
            <el-option v-for="item in regionOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="企业类型" prop="enterpriseType">
          <el-select v-model="queryParams.enterpriseType" clearable style="width: 150px">
            <el-option v-for="item in enterpriseTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="评分维度">
          <el-select v-model="selectedDimension" clearable style="width: 190px">
            <el-option v-for="item in dimensionOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="风险等级" prop="riskLevel">
          <el-select v-model="queryParams.riskLevel" clearable style="width: 150px">
            <el-option v-for="item in riskLevelOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleDimensionQuery">搜索</el-button>
          <el-button icon="Refresh" @click="resetDimensionQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="toolbar-card ygb-toolbar-card" shadow="never">
      <el-row :gutter="10">
        <el-col :span="1.5">
          <el-button type="primary" plain icon="Document" :disabled="!currentRow" @click="openReportWorkbench()">报告穿透</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button plain icon="Tickets" :disabled="!currentRow" @click="openTaskPage()">生成任务</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button plain icon="Bell" @click="openSubscriptionPage()" v-hasPermi="['ygb:aiReportSubscription:list']">订阅导出</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['ygb:aiReport:export']">导出</el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>
    </el-card>

    <el-card class="table-card ygb-table-card" shadow="never">
      <template #header>
        <div class="ygb-card-head ygb-card-head--between">
          <div>
            <div class="ygb-card-head__title">多维筛选与穿透台账</div>
            <div class="ygb-card-head__desc">按真实报告维度和风险标签筛选，选中一行后可穿透到报告、任务和订阅管理。</div>
          </div>
          <div class="ygb-card-head__desc">当前展示 {{ displayTotal }} 条</div>
        </div>
      </template>
      <el-table
        v-loading="loading"
        :data="filteredReportList"
        highlight-current-row
        @current-change="handleCurrentChange"
        @row-click="handleRowClick"
      >
        <el-table-column label="报告ID" align="center" prop="reportId" width="90" />
        <el-table-column label="报告类型" align="center" width="110">
          <template #default="scope">{{ reportTypeLabel(scope.row.reportType) }}</template>
        </el-table-column>
        <el-table-column label="区域" align="center" prop="regionName" min-width="150" />
        <el-table-column label="统计区间" align="center" min-width="210">
          <template #default="scope">
            {{ parseTime(scope.row.periodStart, '{y}-{m}-{d}') }} 至 {{ parseTime(scope.row.periodEnd, '{y}-{m}-{d}') }}
          </template>
        </el-table-column>
        <el-table-column label="评分维度" align="left" min-width="250">
          <template #default="scope">
            <el-tag v-for="item in dimensionTags(scope.row.selectedDimensions)" :key="item" size="small" effect="plain" style="margin: 2px 4px 2px 0">
              {{ item }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="综合得分" align="center" prop="totalScore" width="105" />
        <el-table-column label="风险等级" align="center" width="110">
          <template #default="scope">
            <el-tag :type="riskTagType(scope.row.riskLevel)">{{ riskLevelLabel(scope.row.riskLevel) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="区域排名" align="center" prop="rankingNo" width="95" />
        <el-table-column label="结论摘要" align="left" prop="reportSummary" min-width="260" show-overflow-tooltip />
        <el-table-column class-name="table-fill-column" min-width="1" />
        <el-table-column label="操作" fixed="right" align="center" width="300" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-button link type="info" icon="View" @click.stop="openReportDetail(scope.row)">详情</el-button>
            <el-button link type="primary" icon="Document" @click.stop="openReportWorkbench(scope.row)">报告</el-button>
            <el-button link type="primary" icon="Tickets" @click.stop="openTaskPage(scope.row)">任务</el-button>
            <el-button link type="primary" icon="Bell" @click.stop="openSubscriptionPage(scope.row)">订阅</el-button>
          </template>
        </el-table-column>
      </el-table>
      <pagination
        v-show="total > 0 && !selectedDimension"
        :total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
      />
    </el-card>

    <page-detail-dialog v-model="detailOpen" title="多维筛选与穿透详情" width="820px">
      <template v-if="detailRow">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="报告ID">{{ detailRow.reportId || '-' }}</el-descriptions-item>
          <el-descriptions-item label="报告类型">{{ reportTypeLabel(detailRow.reportType) }}</el-descriptions-item>
          <el-descriptions-item label="区域">{{ detailRow.regionName || detailRow.regionCode || '-' }}</el-descriptions-item>
          <el-descriptions-item label="企业类型">{{ enterpriseTypeLabel(detailRow.enterpriseType) }}</el-descriptions-item>
          <el-descriptions-item label="统计区间">
            {{ parseTime(detailRow.periodStart, '{y}-{m}-{d}') || '-' }} 至 {{ parseTime(detailRow.periodEnd, '{y}-{m}-{d}') || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="综合得分">{{ detailRow.totalScore || '-' }}</el-descriptions-item>
          <el-descriptions-item label="风险等级">{{ riskLevelLabel(detailRow.riskLevel) }}</el-descriptions-item>
          <el-descriptions-item label="区域排名">{{ detailRow.rankingNo || '-' }}</el-descriptions-item>
          <el-descriptions-item label="模型版本">{{ detailRow.configVersion || '-' }}</el-descriptions-item>
          <el-descriptions-item label="生成时间">{{ parseTime(detailRow.generatedTime, '{y}-{m}-{d} {h}:{i}:{s}') || '-' }}</el-descriptions-item>
          <el-descriptions-item label="评分维度" :span="2">{{ formatDimensions(detailRow.selectedDimensions) }}</el-descriptions-item>
          <el-descriptions-item label="结论摘要" :span="2">{{ detailRow.reportSummary || '-' }}</el-descriptions-item>
        </el-descriptions>

        <el-table v-if="reportItems.length" :data="visibleReportItems" size="small" style="margin-top: 16px">
          <el-table-column label="维度" min-width="150">
            <template #default="scope">{{ scope.row.dimensionCode }} {{ scope.row.dimensionName }}</template>
          </el-table-column>
          <el-table-column label="指标" prop="metricLabel" min-width="160" show-overflow-tooltip />
          <el-table-column label="指标值" prop="metricValue" width="110" />
          <el-table-column label="目标值" prop="targetValue" width="110" />
          <el-table-column label="得分" prop="dimensionScore" width="90" />
          <el-table-column label="风险等级" width="100">
            <template #default="scope">
              <el-tag :type="riskTagType(scope.row.riskLevel)">{{ riskLevelLabel(scope.row.riskLevel) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="建议" prop="suggestionText" min-width="220" show-overflow-tooltip />
        </el-table>
      </template>
    </page-detail-dialog>
  </div>
</template>

<script setup name="YgbAiReportFilter">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { parseTime } from '@/utils/yuegongbao'
import {
  dimensionOptions,
  enterpriseTypeLabel,
  enterpriseTypeOptions,
  reportTypeLabel,
  reportTypeOptions,
  riskLevelLabel,
  riskLevelOptions,
  riskTagType,
  regionOptions,
  summaryCard,
  useAiReportPage
} from '@/views/aiReport/useAiReportPage'

const router = useRouter()
const currentRow = ref(null)
const selectedDimension = ref(undefined)

const {
  loading,
  showSearch,
  total,
  reportList,
  reportDetail,
  reportItems,
  detailOpen,
  dashboard,
  queryParams,
  queryRange,
  getList,
  handleQuery,
  resetQuery,
  openDetail,
  handleExport
} = useAiReportPage({ exportFilePrefix: 'ai_report_filter' })

const detailRow = computed(() => reportDetail.value || currentRow.value)

const filteredReportList = computed(() => {
  if (!selectedDimension.value) return reportList.value
  return reportList.value.filter(row => selectedDimensionValues(row.selectedDimensions).includes(selectedDimension.value))
})

const visibleReportItems = computed(() => {
  if (!selectedDimension.value) return reportItems.value
  return reportItems.value.filter(item => item.dimensionCode === selectedDimension.value)
})

const displayTotal = computed(() => selectedDimension.value ? filteredReportList.value.length : total.value)

const summaryCards = computed(() => [
  summaryCard('totalCount', '报告总数', dashboard.totalCount || 0, '份', '当前筛选范围', ''),
  summaryCard('dimensionCount', '覆盖维度', dashboard.scoreCards?.length || 0, '个', '模型评分维度', ''),
  summaryCard('highRiskCount', '高风险报告', dashboard.highRiskCount || 0, '份', '需穿透复核', 'danger'),
  summaryCard('activeItems', '明细指标', dashboard.activeItems?.length || 0, '项', '当前活动报告', 'primary')
])

function selectedDimensionValues(value) {
  if (!value) return []
  return String(value).split(',').map(item => item.trim()).filter(Boolean)
}

function dimensionTags(value) {
  const values = selectedDimensionValues(value)
  if (!values.length) return ['未配置']
  return values.map(item => {
    const label = dimensionOptions.find(option => option.value === item)?.label || item
    return label.replace(/^[A-Z]\s*/, '')
  })
}

function formatDimensions(value) {
  return dimensionTags(value).join('、')
}

function handleCurrentChange(row) {
  currentRow.value = row || currentRow.value
}

function handleRowClick(row) {
  currentRow.value = row
}

function handleDimensionQuery() {
  handleQuery()
}

function resetDimensionQuery() {
  selectedDimension.value = undefined
  resetQuery()
}

async function openReportDetail(row) {
  currentRow.value = row
  await openDetail(row)
}

function openReportWorkbench(row) {
  const target = row || currentRow.value
  router.push({
    path: '/ai-report/report',
    query: target?.reportId ? { reportId: String(target.reportId) } : {}
  })
}

function openTaskPage(row) {
  const target = row || currentRow.value
  router.push({
    path: '/ai-report/task',
    query: target?.reportId ? { reportId: String(target.reportId) } : {}
  })
}

function openSubscriptionPage(row) {
  const target = row || currentRow.value
  router.push({
    path: '/ai-report/subscription',
    query: target?.reportId ? { reportId: String(target.reportId) } : {}
  })
}

onMounted(() => {
  getList()
})
</script>
