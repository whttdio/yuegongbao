<template>
  <div class="app-container ygb-page">
    <section class="gov-page-header ygb-page__header">
      <div>
        <p class="ygb-page__eyebrow">AI 监测报告</p>
        <h1 class="ygb-page__title">风险评分与排名</h1>
        <p class="ygb-page__desc">基于已生成 AI 监测报告，展示区域、周期、企业类型下的综合评分、风险等级和排名结果。</p>
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
        <el-form-item label="风险等级" prop="riskLevel">
          <el-select v-model="queryParams.riskLevel" clearable style="width: 150px">
            <el-option v-for="item in riskLevelOptions" :key="item.value" :label="item.label" :value="item.value" />
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
          <el-button type="primary" plain icon="Document" :disabled="!currentRow" @click="openReportWorkbench()">报告工作台</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button plain icon="Tickets" :disabled="!currentRow" @click="openTaskPage()">创建监测任务</el-button>
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
            <div class="ygb-card-head__title">风险评分与排名台账</div>
            <div class="ygb-card-head__desc">按 AI 报告真实评分字段展示监管对象排名，支持进入详情、工作台和任务闭环。</div>
          </div>
          <div class="ygb-card-head__desc">当前总量 {{ total }} 条</div>
        </div>
      </template>
      <el-table
        v-loading="loading"
        :data="reportList"
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
        <el-table-column label="企业类型" align="center" width="120">
          <template #default="scope">{{ enterpriseTypeLabel(scope.row.enterpriseType) }}</template>
        </el-table-column>
        <el-table-column label="综合得分" align="center" prop="totalScore" width="105" />
        <el-table-column label="风险等级" align="center" width="110">
          <template #default="scope">
            <el-tag :type="riskTagType(scope.row.riskLevel)">{{ riskLevelLabel(scope.row.riskLevel) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="区域排名" align="center" prop="rankingNo" width="95" />
        <el-table-column label="生成时间" align="center" width="170">
          <template #default="scope">
            <span>{{ parseTime(scope.row.generatedTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="结论摘要" align="left" prop="reportSummary" min-width="240" show-overflow-tooltip />
        <el-table-column class-name="table-fill-column" min-width="1" />
        <el-table-column label="操作" fixed="right" align="center" width="260" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-button link type="info" icon="View" @click.stop="openReportDetail(scope.row)">详情</el-button>
            <el-button link type="primary" icon="Document" @click.stop="openReportWorkbench(scope.row)">工作台</el-button>
            <el-button link type="primary" icon="Tickets" @click.stop="openTaskPage(scope.row)">任务</el-button>
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

    <page-detail-dialog v-model="detailOpen" title="风险评分与排名详情" width="820px">
      <template v-if="detailRow">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="报告ID">{{ detailRow.reportId || '-' }}</el-descriptions-item>
          <el-descriptions-item label="报告类型">{{ reportTypeLabel(detailRow.reportType) }}</el-descriptions-item>
          <el-descriptions-item label="区域">{{ detailRow.regionName || detailRow.regionCode || '-' }}</el-descriptions-item>
          <el-descriptions-item label="企业类型">{{ enterpriseTypeLabel(detailRow.enterpriseType) }}</el-descriptions-item>
          <el-descriptions-item label="统计区间">
            {{ parseTime(detailRow.periodStart, '{y}-{m}-{d}') || '-' }} 至 {{ parseTime(detailRow.periodEnd, '{y}-{m}-{d}') || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="生成时间">{{ parseTime(detailRow.generatedTime, '{y}-{m}-{d} {h}:{i}:{s}') || '-' }}</el-descriptions-item>
          <el-descriptions-item label="综合得分">{{ detailRow.totalScore || '-' }}</el-descriptions-item>
          <el-descriptions-item label="风险等级">{{ riskLevelLabel(detailRow.riskLevel) }}</el-descriptions-item>
          <el-descriptions-item label="区域排名">{{ detailRow.rankingNo || '-' }}</el-descriptions-item>
          <el-descriptions-item label="模型版本">{{ detailRow.configVersion || '-' }}</el-descriptions-item>
          <el-descriptions-item label="评分维度" :span="2">{{ formatDimensions(detailRow.selectedDimensions) }}</el-descriptions-item>
          <el-descriptions-item label="结论摘要" :span="2">{{ detailRow.reportSummary || '-' }}</el-descriptions-item>
        </el-descriptions>

        <el-table v-if="reportItems.length" :data="reportItems" size="small" style="margin-top: 16px">
          <el-table-column label="维度" min-width="150">
            <template #default="scope">{{ scope.row.dimensionCode }} {{ scope.row.dimensionName }}</template>
          </el-table-column>
          <el-table-column label="指标" prop="metricLabel" min-width="160" show-overflow-tooltip />
          <el-table-column label="指标值" prop="metricValue" width="110" />
          <el-table-column label="目标值" prop="targetValue" width="110" />
          <el-table-column label="得分" prop="dimensionScore" width="90" />
          <el-table-column label="建议" prop="suggestionText" min-width="220" show-overflow-tooltip />
        </el-table>
      </template>
    </page-detail-dialog>
  </div>
</template>

<script setup name="YgbAiReportRanking">
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
} = useAiReportPage({ exportFilePrefix: 'ai_report_ranking' })

const detailRow = computed(() => reportDetail.value || currentRow.value)

const summaryCards = computed(() => [
  summaryCard('totalCount', '报告总数', dashboard.totalCount || 0, '份', '当前筛选范围', ''),
  summaryCard('highRiskCount', '高风险报告', dashboard.highRiskCount || 0, '份', '需重点研判', 'danger'),
  summaryCard('averageScore', '平均得分', dashboard.averageScore || 0, '分', '同口径均值', ''),
  summaryCard('topRanking', '前位样本', dashboard.topRankingList?.length || 0, '个', '用于正向对标', 'success')
])

function handleCurrentChange(row) {
  currentRow.value = row || currentRow.value
}

function handleRowClick(row) {
  currentRow.value = row
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

function formatDimensions(value) {
  if (!value) return '-'
  return String(value)
    .split(',')
    .map(item => item.trim())
    .filter(Boolean)
    .map(item => dimensionOptions.find(option => option.value === item)?.label || item)
    .join('、')
}

onMounted(() => {
  getList()
})
</script>
