<template>
  <div class="app-container typed-stat-report-page">
    <section class="report-hero" :style="heroStyle">
      <div>
        <p class="report-hero__eyebrow">{{ config.eyebrow }}</p>
        <h1 class="report-hero__title">{{ config.title }}</h1>
        <p class="report-hero__desc">{{ config.portalDescription }} {{ config.description }}</p>
      </div>
      <div class="report-hero__meta">
        <div class="report-hero__meta-label">{{ config.surfaceTitle }}</div>
        <div class="report-hero__meta-value">{{ total }} 份</div>
        <div class="report-hero__meta-note">固定月报入口，不再混入类型切换。</div>
      </div>
    </section>

    <el-alert
      v-if="workbenchContext"
      type="info"
      :closable="false"
      show-icon
      class="report-workbench-alert"
    >
      <template #title>
        <div class="report-workbench-alert__title">
          <span>{{ workbenchContext.title }}</span>
          <el-button link type="primary" @click="clearWorkbenchContext">清空来源条件</el-button>
        </div>
      </template>
      <div class="report-workbench-alert__desc">
        <strong>{{ workbenchContext.sourceLabel }}</strong>
        <span>{{ workbenchContext.description }}</span>
      </div>
    </el-alert>

    <div class="report-summary-grid">
      <div v-for="item in summaryCards" :key="item.key" class="report-summary-card">
        <div class="report-summary-card__label">{{ item.label }}</div>
        <div class="report-summary-card__value">
          {{ item.value }}
          <span v-if="item.unit" class="report-summary-card__unit">{{ item.unit }}</span>
        </div>
        <div class="report-summary-card__note">{{ item.note }}</div>
      </div>
    </div>

    <el-card class="report-search-card" shadow="never">
      <el-form ref="queryRef" :model="queryParams" :inline="true" v-show="showSearch">
        <el-form-item label="区域">
          <el-select v-model="queryParams.regionCode" clearable style="width: 180px">
            <el-option v-for="item in regionOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="统计月份">
          <el-date-picker v-model="queryParams.statMonth" type="month" value-format="YYYY-MM" format="YYYY-MM" style="width: 180px" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.reportStatus" clearable style="width: 150px">
            <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <template v-for="field in config.queryFields" :key="`${config.typeKey}-${field.label}`">
          <el-form-item v-if="field.kind === 'text'" :label="field.label">
            <el-input v-model="queryParams[field.key]" :placeholder="field.placeholder || `请输入${field.label}`" clearable style="width: 200px" />
          </el-form-item>
          <el-form-item v-else-if="field.kind === 'select'" :label="field.label">
            <el-select v-model="queryParams[field.key]" clearable style="width: 180px">
              <el-option v-for="item in field.options || []" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item v-else-if="field.kind === 'range'" :label="field.label">
            <div class="report-range-field">
              <el-input-number v-model="queryParams[field.minKey]" :min="0" :controls="false" placeholder="最小" />
              <span class="report-range-field__sep">-</span>
              <el-input-number v-model="queryParams[field.maxKey]" :min="0" :controls="false" placeholder="最大" />
              <span v-if="field.suffix" class="report-range-field__suffix">{{ field.suffix }}</span>
            </div>
          </el-form-item>
        </template>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">查询</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="report-toolbar-card" shadow="never">
      <el-row :gutter="10">
        <el-col v-if="canGenerate" :span="1.5">
          <el-button type="primary" plain icon="DocumentAdd" @click="openGenerateDialog()" v-hasPermi="[generatePermission]">生成月报</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="[exportPermission]">导出</el-button>
        </el-col>
        <el-col v-if="canPrint" :span="1.5">
          <el-button plain icon="Printer" @click="handlePrintList" v-hasPermi="[printPermission]">打印列表</el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>
    </el-card>

    <el-card class="report-table-card" shadow="never">
      <template #header>
        <div class="report-table-head">
          <div>
            <div class="report-table-head__title">{{ config.title }}列表</div>
            <div class="report-table-head__desc">当前列表只返回固定类型月报，可直接打印列表或打印单份详情。</div>
          </div>
          <div class="report-table-head__count">共 {{ total }} 份</div>
        </div>
      </template>
      <el-table v-loading="loading" :data="reportList" @row-click="handleRowClick">
        <el-table-column label="报表ID" prop="reportId" width="90" />
        <el-table-column v-for="column in config.tableColumns" :key="column.key" :label="column.label" :prop="column.prop" :width="column.width" :min-width="column.minWidth">
          <template #default="scope">
            <dict-tag v-if="column.type === 'status'" :options="statusOptions" :value="scope.row.reportStatus" />
            <span v-else>{{ resolveColumnText(column, scope.row) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="摘要" prop="reportSummary" min-width="240" show-overflow-tooltip />
        <el-table-column label="操作" fixed="right" align="center" width="220">
          <template #default="scope">
            <el-button link type="primary" icon="View" @click.stop="openDetail(scope.row)">详情</el-button>
            <el-button v-if="canPrint" link type="info" icon="Printer" @click.stop="handlePrintDetail(scope.row)" v-hasPermi="[printPermission]">打印</el-button>
            <el-button v-if="canGenerate" link type="success" icon="RefreshRight" @click.stop="openGenerateDialog(scope.row)" v-hasPermi="[generatePermission]">重生成</el-button>
          </template>
        </el-table-column>
      </el-table>
      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>

    <el-dialog title="生成月报" v-model="generateOpen" width="520px" append-to-body>
      <el-form ref="generateRef" :model="generateForm" :rules="generateRules" label-width="96px">
        <el-form-item label="月报类型">
          <el-input :model-value="config.title" disabled />
        </el-form-item>
        <el-form-item label="区域" prop="regionCode">
          <el-select v-model="generateForm.regionCode" style="width: 100%">
            <el-option v-for="item in regionOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="统计月份" prop="statMonth">
          <el-date-picker v-model="generateForm.statMonth" type="month" value-format="YYYY-MM" format="YYYY-MM" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitGenerate">生成</el-button>
          <el-button @click="generateOpen = false">取消</el-button>
        </div>
      </template>
    </el-dialog>

    <page-detail-dialog v-model="detailOpen" :title="`${config.title}详情`" width="840px">
      <template v-if="reportDetail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="报表名称">{{ reportDetail.reportName }}</el-descriptions-item>
          <el-descriptions-item label="统计月份">{{ reportDetail.statMonth }}</el-descriptions-item>
          <el-descriptions-item label="区域">{{ regionNameMap[reportDetail.regionCode] || reportDetail.regionCode }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <dict-tag :options="statusOptions" :value="reportDetail.reportStatus" />
          </el-descriptions-item>
          <el-descriptions-item label="核心数量">{{ reportDetail.metricCount }}</el-descriptions-item>
          <el-descriptions-item label="核心数值">{{ reportDetail.metricAmount }}</el-descriptions-item>
          <el-descriptions-item label="核心比率">{{ formatMetricRate(reportDetail.metricRate, reportDetail.reportCode) }}</el-descriptions-item>
          <el-descriptions-item label="生成时间">{{ parseTime(reportDetail.generatedTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</el-descriptions-item>
          <el-descriptions-item label="摘要" :span="2">{{ reportDetail.reportSummary || '-' }}</el-descriptions-item>
        </el-descriptions>

        <div class="report-detail-actions">
          <el-button v-if="canPrint" plain icon="Printer" @click="handlePrintDetail(reportDetail)" v-hasPermi="[printPermission]">打印详情</el-button>
        </div>

        <div class="report-detail-table">
          <div class="report-detail-table__title">明细项</div>
          <el-table :data="reportItems" size="small">
            <el-table-column label="分类" prop="itemCategory" width="150" />
            <el-table-column label="名称" prop="itemName" min-width="160" />
            <el-table-column label="维度" prop="itemDimension" width="160" />
            <el-table-column label="数量" prop="metricCount" width="90" />
            <el-table-column label="数值" prop="metricValue" width="120" />
            <el-table-column label="比率" width="100">
              <template #default="scope">
                {{ formatMetricRate(scope.row.metricRate, reportDetail.reportCode) }}
              </template>
            </el-table-column>
          </el-table>
        </div>
      </template>
    </page-detail-dialog>
  </div>
</template>

<script setup name="TypedStatReportPage">
import { computed, getCurrentInstance } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { parseTime } from '@/utils/yuegongbao'
import { buildWorkbenchContext, applyWorkbenchRouteQuery, stripWorkbenchRouteQuery } from '@/utils/workbenchLink'
import { printStatReportDetail, printStatReportList } from '@/views/statReport/print'
import { useAuthorizedRegionOptions } from '@/utils/regionScope'
import {
  formatMetricRate,
  statReportRegionNameMap as regionNameMap,
  statReportRegionOptions as allRegionOptions,
  statReportStatusOptions as statusOptions,
  statusLabel,
  useStatReportPage
} from '@/views/statReport/useStatReportPage'

const props = defineProps({
  config: {
    type: Object,
    required: true
  }
})

const { proxy } = getCurrentInstance()
const regionOptions = useAuthorizedRegionOptions(allRegionOptions)
const route = useRoute()
const router = useRouter()
const statReportWorkbenchFields = ['regionCode', 'statMonth']

const {
  loading,
  showSearch,
  total,
  reportList,
  reportDetail,
  reportItems,
  summaryData,
  detailOpen,
  generateOpen,
  queryParams,
  generateForm,
  generateRules,
  getList,
  handleRowClick,
  handleQuery,
  resetQuery: pageResetQuery,
  handleExport,
  openGenerateDialog,
  submitGenerate,
  openDetail
} = useStatReportPage({
  exportFilePrefix: `${props.config.portalCode}_${props.config.typeKey}_stat_report`,
  defaultReportCode: props.config.reportCode,
  fixedReportCode: props.config.reportCode,
  immediate: false
})

const generatePermission = computed(() => `${props.config.permissionPrefix}:generate`)
const exportPermission = computed(() => `${props.config.permissionPrefix}:export`)
const printPermission = computed(() => `${props.config.permissionPrefix}:print`)
const canGenerate = computed(() => Boolean(proxy?.$auth?.hasPermi?.(generatePermission.value)))
const canPrint = computed(() => Boolean(proxy?.$auth?.hasPermi?.(printPermission.value)))

const heroStyle = computed(() => ({
  '--report-primary': props.config.primaryText,
  '--report-accent': props.config.accent,
  '--report-soft': props.config.soft
}))

const workbenchContext = computed(() => buildWorkbenchContext(route.query, {
  fields: statReportWorkbenchFields,
  sourceLabel: '月报工作台',
  title: '当前月报沿用了来源筛选条件',
  description: '保留来源区域和月份，便于继续做月报复核与打印。',
  fieldLabels: {
    regionCode: '区域',
    statMonth: '统计月份'
  },
  fieldFormatters: {
    regionCode: value => regionNameMap[value] || value
  }
}))

const summaryCards = computed(() => {
  const rows = reportList.value || []
  const generatedCount = rows.filter(item => item.reportStatus === '1').length
  const draftCount = rows.filter(item => item.reportStatus === '0').length
  const cards = [
    { key: 'total', label: '当前列表', value: rows.length, unit: '份', note: '固定类型列表，不混入其他月报。' },
    { key: 'generated', label: '已生成', value: generatedCount, unit: '份', note: '已形成正式结果，可打印详情。' },
    { key: 'draft', label: '草稿', value: draftCount, unit: '份', note: '待补齐后再生成或归档。' }
  ]
  props.config.summaryMetrics.forEach(metric => {
    const values = rows.map(item => Number(item[metric.key] || 0))
    const totalValue = values.reduce((sum, value) => sum + value, 0)
    const displayValue = metric.isRate
      ? (rows.length ? (totalValue / rows.length).toFixed(2) : '0.00')
      : totalValue
    cards.push({
      key: metric.key,
      label: metric.label,
      value: displayValue,
      unit: metric.unit,
      note: '按当前查询结果实时汇总。'
    })
  })
  return cards.slice(0, 5)
})

const printListMetrics = computed(() => props.config.summaryMetrics.map(metric => {
  const values = reportList.value.map(item => Number(item[metric.key] || 0))
  const totalValue = values.reduce((sum, value) => sum + value, 0)
  const displayValue = metric.isRate
    ? (reportList.value.length ? (totalValue / reportList.value.length).toFixed(2) : '0.00')
    : totalValue
  return {
    label: metric.label,
    value: displayValue,
    unit: metric.unit
  }
}))

const printQuerySummary = computed(() => {
  const summary = [
    { label: '区域', value: regionNameMap[queryParams.value.regionCode] || queryParams.value.regionCode || '全部' },
    { label: '统计月份', value: queryParams.value.statMonth || '全部' },
    { label: '状态', value: statusLabel(queryParams.value.reportStatus) }
  ]
  props.config.queryFields.forEach(field => {
    if (field.kind === 'text' && queryParams.value[field.key]) {
      summary.push({ label: field.label, value: queryParams.value[field.key] })
    }
    if (field.kind === 'select' && queryParams.value[field.key]) {
      const matched = (field.options || []).find(item => item.value === queryParams.value[field.key])
      summary.push({ label: field.label, value: matched?.label || queryParams.value[field.key] })
    }
    if (field.kind === 'range' && (queryParams.value[field.minKey] !== undefined || queryParams.value[field.maxKey] !== undefined)) {
      summary.push({
        label: field.label,
        value: `${queryParams.value[field.minKey] ?? '-'} - ${queryParams.value[field.maxKey] ?? '-'}${field.suffix || ''}`
      })
    }
  })
  return summary.filter(item => item.value && item.value !== '-')
})

function resolveColumnText(column, row) {
  if (typeof column.formatter === 'function') {
    return column.formatter(row, { parseTime })
  }
  return row[column.prop] ?? '-'
}

function buildPrintRows() {
  return reportList.value.map(row => {
    const record = { reportId: row.reportId }
    props.config.tableColumns.forEach(column => {
      record[column.key] = column.type === 'status'
        ? statusLabel(row.reportStatus)
        : resolveColumnText(column, row)
    })
    record.reportSummary = row.reportSummary || '-'
    return record
  })
}

function handlePrintList() {
  printStatReportList({
    config: props.config,
    querySummary: printQuerySummary.value,
    metrics: printListMetrics.value,
    columns: [
      { key: 'reportId', label: '报表ID' },
      ...props.config.tableColumns.map(column => ({ key: column.key, label: column.label })),
      { key: 'reportSummary', label: '摘要' }
    ],
    rows: buildPrintRows(),
    printTime: parseTime(new Date(), '{y}-{m}-{d} {h}:{i}:{s}')
  })
}

async function handlePrintDetail(row) {
  if (!row?.reportId) {
    return
  }
  if (!reportDetail.value || reportDetail.value.reportId !== row.reportId) {
    await openDetail(row)
  }
  printStatReportDetail({
    config: props.config,
    report: reportDetail.value || row,
    items: reportItems.value || [],
    printTime: parseTime(new Date(), '{y}-{m}-{d} {h}:{i}:{s}')
  })
}

function resetQuery() {
  pageResetQuery()
  applyWorkbenchRouteQuery(route.query, queryParams.value, statReportWorkbenchFields)
  getList()
}

function clearWorkbenchContext() {
  Object.assign(queryParams.value, {
    pageNum: 1,
    regionCode: undefined,
    statMonth: queryParams.value.statMonth
  })
  router.replace({
    path: route.path,
    query: stripWorkbenchRouteQuery(route.query, statReportWorkbenchFields)
  })
  getList()
}

applyWorkbenchRouteQuery(route.query, queryParams.value, statReportWorkbenchFields)
getList()
</script>

<style scoped lang="scss">
.typed-stat-report-page {
  .report-hero {
    display: grid;
    grid-template-columns: minmax(0, 1fr) 240px;
    gap: 20px;
    padding: 24px 28px;
    border: 1px solid #dbe5f0;
    border-radius: 12px;
    background:
      linear-gradient(135deg, var(--report-accent) 0%, rgba(255, 255, 255, 0.96) 56%),
      #fff;
    margin-bottom: 16px;
  }

  .report-hero__eyebrow {
    margin: 0;
    color: var(--report-primary);
    font-size: 12px;
    font-weight: 600;
  }

  .report-hero__title {
    margin: 8px 0 0;
    color: #13243a;
    font-size: 30px;
    line-height: 1.2;
  }

  .report-hero__desc {
    margin: 12px 0 0;
    max-width: 70ch;
    color: #526377;
    font-size: 14px;
    line-height: 1.8;
  }

  .report-hero__meta {
    padding: 18px 20px;
    border-radius: 12px;
    background: var(--report-soft);
    border: 1px solid rgba(148, 163, 184, 0.25);
  }

  .report-hero__meta-label {
    color: #64748b;
    font-size: 12px;
  }

  .report-hero__meta-value {
    margin-top: 10px;
    color: var(--report-primary);
    font-size: 30px;
    font-weight: 700;
  }

  .report-hero__meta-note {
    margin-top: 8px;
    color: #64748b;
    font-size: 13px;
    line-height: 1.7;
  }

  .report-workbench-alert {
    margin-bottom: 16px;
  }

  .report-workbench-alert__title {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 12px;
  }

  .report-workbench-alert__desc {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
    line-height: 1.7;
  }

  .report-summary-grid {
    display: grid;
    grid-template-columns: repeat(5, minmax(0, 1fr));
    gap: 14px;
    margin-bottom: 16px;
  }

  .report-summary-card {
    padding: 16px 18px;
    border: 1px solid #dbe5f0;
    border-radius: 10px;
    background: #fff;
  }

  .report-summary-card__label {
    color: #64748b;
    font-size: 12px;
  }

  .report-summary-card__value {
    margin-top: 10px;
    color: #0f172a;
    font-size: 24px;
    font-weight: 700;
  }

  .report-summary-card__unit {
    margin-left: 4px;
    color: #64748b;
    font-size: 12px;
  }

  .report-summary-card__note {
    margin-top: 8px;
    color: #64748b;
    font-size: 12px;
    line-height: 1.7;
  }

  .report-search-card,
  .report-toolbar-card,
  .report-table-card {
    margin-bottom: 16px;
    border-color: #dbe5f0;
    border-radius: 10px;
  }

  .report-search-card :deep(.el-card__body),
  .report-toolbar-card :deep(.el-card__body),
  .report-table-card :deep(.el-card__body) {
    padding: 18px 20px;
  }

  .report-range-field {
    display: flex;
    align-items: center;
    gap: 8px;
  }

  .report-range-field :deep(.el-input-number) {
    width: 120px;
  }

  .report-range-field__sep,
  .report-range-field__suffix {
    color: #64748b;
    font-size: 12px;
  }

  .report-table-head {
    display: flex;
    align-items: flex-start;
    justify-content: space-between;
    gap: 16px;
  }

  .report-table-head__title {
    color: #0f172a;
    font-size: 18px;
    font-weight: 700;
  }

  .report-table-head__desc,
  .report-table-head__count {
    margin-top: 4px;
    color: #64748b;
    font-size: 13px;
    line-height: 1.7;
  }

  .report-detail-actions {
    margin-top: 16px;
  }

  .report-detail-table {
    margin-top: 18px;
  }

  .report-detail-table__title {
    margin-bottom: 12px;
    color: #0f172a;
    font-size: 16px;
    font-weight: 700;
  }

  @media (max-width: 1280px) {
    .report-summary-grid {
      grid-template-columns: repeat(2, minmax(0, 1fr));
    }
  }

  @media (max-width: 960px) {
    .report-hero,
    .report-summary-grid {
      grid-template-columns: 1fr;
    }

    .report-table-head {
      flex-direction: column;
    }
  }
}
</style>
