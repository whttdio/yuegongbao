<template>
  <div class="app-container ygb-aggregation-page">
    <section class="ygb-aggregation-hero">
      <div class="ygb-aggregation-hero__main">
        <p class="ygb-aggregation-hero__eyebrow">{{ config.eyebrow || config.title }}</p>
        <h1 class="ygb-aggregation-hero__title">{{ config.title }}</h1>
        <p class="ygb-aggregation-hero__desc">{{ config.description }}</p>
      </div>
      <div class="ygb-aggregation-hero__side">
        <div class="ygb-aggregation-signal">
          <span class="ygb-aggregation-signal__label">本页口径</span>
          <strong class="ygb-aggregation-signal__value">{{ config.signalTitle || '平台聚合台账' }}</strong>
          <p class="ygb-aggregation-signal__desc">{{ config.tip || defaultTip }}</p>
        </div>
      </div>
    </section>

    <el-alert
      v-if="workbenchContext"
      class="ygb-workbench-alert"
      type="info"
      :closable="false"
      show-icon
    >
      <template #title>
        <div class="ygb-workbench-alert__title">
          <span>{{ workbenchContext.title }}</span>
        </div>
      </template>
      <div class="ygb-workbench-alert__desc">
        <strong>{{ workbenchContext.sourceLabel }}</strong>
        <span>{{ workbenchContext.description }}</span>
      </div>
      <div class="ygb-tag-list">
        <el-tag v-for="item in workbenchContext.tags" :key="item.key" effect="plain">{{ item.label }}：{{ item.value }}</el-tag>
      </div>
    </el-alert>

    <section class="ygb-aggregation-strip">
      <div class="ygb-summary-grid">
        <article v-for="item in summaryCards" :key="item.key" class="ygb-summary-card" :class="item.cardClass">
          <div class="ygb-summary-card__topline">{{ item.topline || '聚合指标' }}</div>
          <div class="ygb-summary-card__label">{{ item.label }}</div>
          <div class="ygb-summary-card__value">
            {{ item.value }}
            <span v-if="item.unit" class="ygb-summary-card__unit">{{ item.unit }}</span>
          </div>
          <div v-if="item.note" class="ygb-summary-card__note">{{ item.note }}</div>
        </article>
      </div>
    </section>

    <section class="ygb-aggregation-panels">
      <el-card class="ygb-aggregation-panel ygb-aggregation-panel--filters" shadow="never">
        <template #header>
          <div class="ygb-aggregation-panel__head">
            <div>
              <div class="ygb-aggregation-panel__title">筛选条件</div>
              <div class="ygb-aggregation-panel__desc">按月份、区域、企业和平台企业收敛聚合口径。</div>
            </div>
          </div>
        </template>

        <el-form ref="queryRef" :model="queryParams" class="ygb-aggregation-form" label-position="top">
          <el-form-item v-if="hasFilter('statMonth')" label="统计月份">
            <el-date-picker v-model="queryParams.statMonth" type="month" format="YYYY-MM" value-format="YYYY-MM" style="width: 100%" />
          </el-form-item>
          <el-form-item v-if="hasFilter('regionCode')" label="区域">
            <el-select v-model="queryParams.regionCode" clearable style="width: 100%">
              <el-option v-for="item in regionOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item v-if="hasFilter('enterpriseId')" label="企业">
            <el-select v-model="queryParams.enterpriseId" clearable filterable style="width: 100%">
              <el-option v-for="item in enterpriseOptions" :key="item.enterpriseId" :label="item.enterpriseName" :value="item.enterpriseId" />
            </el-select>
          </el-form-item>
          <el-form-item v-if="hasFilter('platformName')" label="平台企业">
            <el-input v-model="queryParams.platformName" clearable @keyup.enter="handleQuery" />
          </el-form-item>
        </el-form>

        <div class="ygb-aggregation-actions">
          <el-button type="primary" icon="Search" @click="handleQuery">查询结果</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置条件</el-button>
        </div>
      </el-card>

      <div class="ygb-aggregation-stack">
        <el-card class="ygb-aggregation-panel ygb-aggregation-panel--focus" shadow="never">
          <template #header>
            <div class="ygb-aggregation-panel__head">
              <div>
                <div class="ygb-aggregation-panel__title">{{ config.focusTitle || '聚合说明' }}</div>
                <div class="ygb-aggregation-panel__desc">{{ config.focusDescription || '当前页面按业务维度汇总重点对象、风险数量和办理进度。' }}</div>
              </div>
            </div>
          </template>

          <div class="ygb-aggregation-focus-list">
            <div v-for="item in focusBullets" :key="item.label" class="ygb-aggregation-focus-item">
              <span class="ygb-aggregation-focus-item__dot" />
              <div>
                <strong>{{ item.label }}</strong>
                <p>{{ item.desc }}</p>
              </div>
            </div>
          </div>
        </el-card>

        <el-card class="ygb-aggregation-panel ygb-aggregation-panel--export" shadow="never">
          <template #header>
            <div class="ygb-aggregation-panel__head">
              <div>
                <div class="ygb-aggregation-panel__title">导出与展示</div>
                <div class="ygb-aggregation-panel__desc">导出沿用当前筛选口径，列表直接展示平台维度结果。</div>
              </div>
            </div>
          </template>

          <div class="ygb-aggregation-export">
            <div class="ygb-aggregation-export__meta">
              <span>当前列表</span>
              <strong>{{ total }} 条</strong>
            </div>
            <div class="ygb-aggregation-export__actions">
              <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="[`${config.permPrefix}:export`]">导出数据</el-button>
              <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
            </div>
          </div>
        </el-card>
      </div>
    </section>

    <el-card class="ygb-aggregation-table" shadow="never">
      <template #header>
        <div class="ygb-aggregation-panel__head">
          <div>
            <div class="ygb-aggregation-panel__title">{{ config.tableTitle || '聚合明细' }}</div>
            <div class="ygb-aggregation-panel__desc">{{ config.tableDescription || '按平台企业展示当前筛选条件下的聚合结果。' }}</div>
          </div>
        </div>
      </template>

      <el-table v-loading="loading" :data="recordList">
        <el-table-column v-for="column in config.columns" :key="column.prop" v-bind="resolveColumnProps(column)">
          <template v-if="column.type" #default="{ row }">
            <dict-tag v-if="column.type === 'dict'" :options="column.options" :value="row[column.prop]" />
            <span v-else-if="column.type === 'region'">{{ row.regionName || formatRegion(row[column.prop]) }}</span>
            <span v-else-if="column.type === 'money'">{{ formatMoney(row[column.prop]) }}</span>
            <span v-else>{{ row[column.prop] ?? '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column v-if="config.drilldown" label="操作" fixed="right" width="120">
          <template #default="{ row }">
            <el-button link type="primary" icon="Search" @click="handleDrilldown(row)">查看明细</el-button>
          </template>
        </el-table-column>
      </el-table>
      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>
  </div>
</template>

<script setup>
import { computed, getCurrentInstance, reactive, ref, toRefs } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { optionselectEnterprise } from '@/api/ygb/enterprise'
import { authorizedDefaultRegionCode, useAuthorizedRegionOptions } from '@/utils/regionScope'
import { buildWorkbenchContext } from '@/utils/workbenchLink'
import { statReportRegionOptions } from '@/views/statReport/useStatReportPage'

const props = defineProps({
  config: {
    type: Object,
    required: true
  }
})

const route = useRoute()
const router = useRouter()
const { proxy } = getCurrentInstance()
const loading = ref(false)
const showSearch = ref(true)
const total = ref(0)
const recordList = ref([])
const summaryData = ref({})
const enterpriseOptions = ref([])
const regionOptions = useAuthorizedRegionOptions(statReportRegionOptions)

const defaultTip = '当前聚合页面按平台维度展示人员底数、风险分布、办理进度和导出结果。'

const defaultQueryParams = () => ({
  pageNum: 1,
  pageSize: 10,
  statMonth: currentMonth(),
  regionCode: authorizedDefaultRegionCode(props.config.defaultRegionCode || '440000'),
  enterpriseId: undefined,
  platformName: undefined,
  ...(props.config.defaultQueryParams || {})
})

const data = reactive({
  queryParams: defaultQueryParams()
})

const { queryParams } = toRefs(data)

const summaryCards = computed(() => {
  if (typeof props.config.summaryCards === 'function') {
    return props.config.summaryCards(summaryData.value)
  }
  return []
})

const focusBullets = computed(() => props.config.focusBullets || [])

const workbenchContext = computed(() => {
  if (!props.config.workbenchFields?.length) {
    return null
  }
  return buildWorkbenchContext(route.query, {
    fields: props.config.workbenchFields,
    title: props.config.workbenchTitle,
    description: props.config.workbenchDescription,
    fieldLabels: props.config.workbenchLabels || {},
    fieldFormatters: props.config.workbenchFormatters || {}
  })
})

function hasFilter(field) {
  return (props.config.filters || []).includes(field)
}

function applyRouteQuery() {
  const routeFields = props.config.routeQueryFields || []
  routeFields.forEach(field => {
    if (route.query[field] !== undefined) {
      queryParams.value[field] = route.query[field]
    }
  })
}

function normalizeParams(source) {
  return Object.fromEntries(Object.entries(source).filter(([, value]) => value !== undefined && value !== null && value !== ''))
}

function resolveColumnProps(column) {
  const { type, options, formatter, ...rest } = column
  return rest
}

function getList() {
  loading.value = true
  return Promise.all([
    props.config.listApi(normalizeParams(queryParams.value)),
    props.config.summaryApi(normalizeParams({
      ...queryParams.value,
      pageNum: undefined,
      pageSize: undefined
    }))
  ]).then(([listResponse, summaryResponse]) => {
    recordList.value = listResponse.rows || []
    total.value = listResponse.total || 0
    summaryData.value = summaryResponse.data || {}
  }).finally(() => {
    loading.value = false
  })
}

function loadEnterpriseOptions() {
  if (!hasFilter('enterpriseId')) {
    return Promise.resolve()
  }
  return optionselectEnterprise().then(response => {
    enterpriseOptions.value = response.data || []
  })
}

function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

function resetQuery() {
  proxy.resetForm('queryRef')
  queryParams.value = defaultQueryParams()
  applyRouteQuery()
  getList()
}

function handleExport() {
  proxy.download(props.config.exportUrl, normalizeParams({
    ...queryParams.value,
    pageNum: undefined,
    pageSize: undefined
  }), `${props.config.filePrefix || 'grouped_aggregation'}_${Date.now()}.xlsx`)
}

function handleDrilldown(row) {
  const drilldown = props.config.drilldown
  if (!drilldown?.path) {
    return
  }
  const query = {
    ...normalizeParams(queryParams.value),
    ...(typeof drilldown.query === 'function' ? drilldown.query(row, queryParams.value) : {})
  }
  delete query.pageNum
  delete query.pageSize
  router.push({ path: drilldown.path, query })
}

function formatMoney(value) {
  if (value === undefined || value === null || value === '') {
    return '-'
  }
  return Number(value).toFixed(2)
}

function formatRegion(value) {
  const matched = (regionOptions.value || []).find(item => item.value === value)
  return matched?.label || value || '-'
}

function currentMonth() {
  const now = new Date()
  const year = now.getFullYear()
  const month = String(now.getMonth() + 1).padStart(2, '0')
  return `${year}-${month}`
}

applyRouteQuery()
loadEnterpriseOptions().finally(() => {
  getList()
})
</script>

<style scoped lang="scss">
.ygb-aggregation-page {
  padding: 24px;
  background:
    radial-gradient(circle at top left, rgba(18, 102, 204, 0.08), transparent 28%),
    linear-gradient(180deg, #f4f7fb 0%, #eef3f8 100%);
}

.ygb-aggregation-hero,
.ygb-aggregation-panels {
  display: grid;
  gap: 18px;
  margin-bottom: 18px;
}

.ygb-aggregation-hero {
  grid-template-columns: minmax(0, 1.5fr) minmax(300px, 0.8fr);
  align-items: stretch;
}

.ygb-aggregation-hero__main,
.ygb-aggregation-signal,
.ygb-summary-card,
.ygb-aggregation-panel,
.ygb-aggregation-table {
  border: 1px solid rgba(174, 198, 230, 0.75);
  border-radius: 22px;
  background: rgba(255, 255, 255, 0.92);
  box-shadow: 0 16px 40px rgba(31, 78, 121, 0.06);
}

.ygb-aggregation-hero__main {
  padding: 28px 30px;
}

.ygb-aggregation-hero__eyebrow {
  margin: 0 0 10px;
  color: #1d5fa8;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.08em;
}

.ygb-aggregation-hero__title {
  margin: 0;
  color: #10233d;
  font-size: 38px;
  line-height: 1.08;
}

.ygb-aggregation-hero__desc {
  max-width: 62ch;
  margin: 14px 0 0;
  color: #52657f;
  font-size: 15px;
  line-height: 1.9;
}

.ygb-aggregation-signal {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 24px 24px 22px;
  background:
    linear-gradient(145deg, rgba(230, 240, 252, 0.92), rgba(255, 255, 255, 0.94)),
    #fff;
}

.ygb-aggregation-signal__label {
  color: #4b6b96;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.06em;
}

.ygb-aggregation-signal__value {
  display: block;
  margin-top: 12px;
  color: #0f2546;
  font-size: 24px;
  line-height: 1.25;
}

.ygb-aggregation-signal__desc {
  margin: 14px 0 0;
  color: #5c708d;
  line-height: 1.8;
}

.ygb-workbench-alert {
  margin-bottom: 18px;
  border-radius: 18px;
}

.ygb-workbench-alert__title {
  display: flex;
  align-items: center;
  gap: 8px;
}

.ygb-workbench-alert__desc {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  line-height: 1.8;
}

.ygb-aggregation-strip {
  margin-bottom: 18px;
}

.ygb-summary-grid {
  display: grid;
  grid-template-columns: repeat(6, minmax(0, 1fr));
  gap: 16px;
}

.ygb-summary-card {
  position: relative;
  overflow: hidden;
  padding: 20px 18px 18px;
}

.ygb-summary-card::after {
  content: '';
  position: absolute;
  inset: auto 0 0 0;
  height: 4px;
  background: linear-gradient(90deg, rgba(29, 95, 168, 0.16), rgba(29, 95, 168, 0.6));
}

.ygb-summary-card__topline {
  margin-bottom: 8px;
  color: #6b809d;
  font-size: 12px;
}

.ygb-summary-card__label {
  color: #20344e;
  font-size: 14px;
  font-weight: 600;
}

.ygb-summary-card__value {
  margin-top: 10px;
  color: #10233d;
  font-size: 34px;
  font-weight: 700;
  line-height: 1.1;
}

.ygb-summary-card__unit {
  margin-left: 6px;
  color: #6f86a4;
  font-size: 14px;
  font-weight: 500;
}

.ygb-summary-card__note {
  margin-top: 12px;
  color: #62768f;
  font-size: 13px;
  line-height: 1.7;
}

.ygb-summary-card--success::after {
  background: linear-gradient(90deg, rgba(28, 136, 96, 0.2), rgba(28, 136, 96, 0.72));
}

.ygb-summary-card--warning::after {
  background: linear-gradient(90deg, rgba(223, 139, 42, 0.2), rgba(223, 139, 42, 0.72));
}

.ygb-summary-card--primary::after {
  background: linear-gradient(90deg, rgba(75, 101, 230, 0.2), rgba(75, 101, 230, 0.72));
}

.ygb-aggregation-panels {
  grid-template-columns: minmax(0, 1.25fr) minmax(340px, 0.95fr);
}

.ygb-aggregation-stack {
  display: grid;
  gap: 18px;
}

.ygb-aggregation-panel,
.ygb-aggregation-table {
  border-radius: 22px;
}

.ygb-aggregation-panel__head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
}

.ygb-aggregation-panel__title {
  color: #122844;
  font-size: 18px;
  font-weight: 700;
}

.ygb-aggregation-panel__desc {
  margin-top: 6px;
  color: #647892;
  line-height: 1.7;
}

.ygb-aggregation-form {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 6px 16px;
}

.ygb-aggregation-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 8px;
}

.ygb-aggregation-focus-list {
  display: grid;
  gap: 14px;
}

.ygb-aggregation-focus-item {
  display: grid;
  grid-template-columns: 14px minmax(0, 1fr);
  gap: 12px;
  align-items: start;
}

.ygb-aggregation-focus-item__dot {
  width: 10px;
  height: 10px;
  margin-top: 7px;
  border-radius: 999px;
  background: linear-gradient(135deg, #2a7de1, #67a2ee);
  box-shadow: 0 0 0 6px rgba(42, 125, 225, 0.1);
}

.ygb-aggregation-focus-item strong {
  color: #18304d;
}

.ygb-aggregation-focus-item p {
  margin: 6px 0 0;
  color: #667b94;
  line-height: 1.75;
}

.ygb-aggregation-export {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18px;
}

.ygb-aggregation-export__meta span {
  display: block;
  color: #7387a0;
  font-size: 12px;
}

.ygb-aggregation-export__meta strong {
  display: block;
  margin-top: 8px;
  color: #0f2744;
  font-size: 28px;
}

.ygb-aggregation-export__actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

:deep(.el-card__header) {
  padding-bottom: 12px;
  border-bottom-color: rgba(222, 231, 242, 0.88);
}

:deep(.el-card__body) {
  padding-top: 16px;
}

:deep(.el-form-item__label) {
  color: #28405f;
  font-weight: 600;
}

:deep(.el-input__wrapper),
:deep(.el-select__wrapper),
:deep(.el-date-editor.el-input__wrapper) {
  border-radius: 14px;
  box-shadow: 0 0 0 1px rgba(195, 210, 230, 0.92) inset;
}

:deep(.el-table) {
  --el-table-header-bg-color: #f5f8fc;
  --el-table-row-hover-bg-color: #f7fbff;
  border-radius: 16px;
  overflow: hidden;
}

@media (max-width: 1280px) {
  .ygb-summary-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}

@media (max-width: 1024px) {
  .ygb-aggregation-hero,
  .ygb-aggregation-panels {
    grid-template-columns: 1fr;
  }

  .ygb-aggregation-form {
    grid-template-columns: 1fr;
  }

  .ygb-aggregation-export {
    flex-direction: column;
    align-items: flex-start;
  }
}

@media (max-width: 768px) {
  .ygb-aggregation-page {
    padding: 16px;
  }

  .ygb-aggregation-hero__main,
  .ygb-aggregation-signal {
    padding: 22px 20px;
  }

  .ygb-aggregation-hero__title {
    font-size: 30px;
  }

  .ygb-summary-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 560px) {
  .ygb-summary-grid {
    grid-template-columns: 1fr;
  }

  .ygb-aggregation-export__actions {
    width: 100%;
    flex-wrap: wrap;
  }
}
</style>
