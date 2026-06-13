<template>
  <div class="app-container ygb-page ygb-warning-page">
    <section class="gov-page-header ygb-page__header">
      <div>
        <p class="ygb-page__eyebrow">预警中心</p>
        <h1 class="ygb-page__title">联动预警办理闭环</h1>
        <p class="ygb-page__desc">
          面向企业办理和监管协同统一承接社保、个税、扩面、设备和工伤来源预警，按粤工保办理链持续核实、处置和归档。
        </p>
      </div>
      <div class="ygb-page__tip">
        当前页面保留解释钻取和工作台来源条件透传，可直接从首页或驾驶舱进入常规列表处理。
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
          <el-button link type="primary" @click="clearWorkbenchContext">清空来源条件</el-button>
        </div>
      </template>
      <div class="ygb-workbench-alert__desc">
        <strong>{{ workbenchContext.sourceLabel }}</strong>
        <span>{{ workbenchContext.description }}</span>
      </div>
      <div class="ygb-tag-list">
        <el-tag v-for="item in workbenchContext.tags" :key="item.key" effect="plain">{{ item.label }}: {{ item.value }}</el-tag>
      </div>
    </el-alert>

    <el-alert
      v-if="isReadOnlyRole"
      class="ygb-workbench-alert"
      type="warning"
      :closable="false"
      :title="readOnlyAlertTitle"
      :description="readOnlyAlertDescription"
      show-icon
    />

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

    <div class="ygb-analysis-grid">
      <div v-for="item in analysisOverviewCards" :key="item.key" class="ygb-summary-card ygb-analysis-card" :class="item.cardClass">
        <div class="ygb-summary-card__label">{{ item.label }}</div>
        <div class="ygb-summary-card__value">
          {{ item.value }}
          <span class="ygb-summary-card__unit">{{ item.unit }}</span>
        </div>
        <div class="ygb-summary-card__note">{{ item.note }}</div>
      </div>
    </div>

    <div class="ygb-analysis-grid">
      <el-card v-for="section in analysisSections" :key="section.key" class="ygb-focus-card ygb-analysis-panel" shadow="never">
        <template #header>
          <div class="ygb-card-head">
            <div class="ygb-card-head__title">{{ section.title }}</div>
            <div class="ygb-card-head__desc">{{ section.desc }}</div>
          </div>
        </template>
        <div v-if="section.items.length" class="ygb-analysis-list">
          <button
            v-for="item in section.items"
            :key="`${section.key}-${item.dimensionKey}`"
            type="button"
            class="ygb-analysis-item"
            @click="applyAnalysisFilter(section.filterField, item.dimensionKey)"
          >
            <span class="ygb-analysis-item__label">{{ item.dimensionLabel }}</span>
            <span class="ygb-analysis-item__value">{{ item.dimensionCount }}</span>
          </button>
        </div>
        <el-empty v-else description="暂无统计数据" :image-size="56" />
      </el-card>
    </div>

    <el-card class="search-card ygb-search-card" shadow="never">
      <el-form ref="queryRef" :model="queryParams" :inline="true" v-show="showSearch">
        <el-form-item label="区域">
          <el-select v-model="queryParams.regionCode" clearable style="width: 180px">
            <el-option v-for="item in regionOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="企业">
          <el-select v-model="queryParams.enterpriseId" clearable filterable style="width: 220px">
            <el-option v-for="item in enterpriseOptions" :key="item.enterpriseId" :label="item.enterpriseName" :value="item.enterpriseId" />
          </el-select>
        </el-form-item>
        <el-form-item label="预警级别">
          <el-select v-model="queryParams.warnLevel" clearable style="width: 160px">
            <el-option v-for="item in warnLevelOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="来源模块">
          <el-select v-model="queryParams.sourceModule" clearable style="width: 160px">
            <el-option v-for="item in sourceModuleOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="工单状态">
          <el-select v-model="queryParams.warnStatus" clearable style="width: 160px">
            <el-option v-for="item in warnStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="预警内容">
          <el-input v-model="queryParams.content" clearable style="width: 220px" @keyup.enter="handleQuery" />
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
          <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['ygb:warning:export']">导出</el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="loadAll" />
      </el-row>
    </el-card>

    <el-card class="table-card ygb-table-card" shadow="never">
      <el-table v-loading="loading" :data="warningList" @row-click="handleRowClick">
        <el-table-column label="ID" prop="warnId" width="90" />
        <el-table-column label="预警级别" prop="warnLevel" width="100">
          <template #default="scope">
            <dict-tag :options="warnLevelOptions" :value="scope.row.warnLevel" />
          </template>
        </el-table-column>
        <el-table-column label="预警类型" prop="warnType" min-width="180" show-overflow-tooltip />
        <el-table-column label="来源模块" prop="sourceModule" width="120">
          <template #default="scope">
            <dict-tag :options="sourceModuleOptions" :value="scope.row.sourceModule" />
          </template>
        </el-table-column>
        <el-table-column label="企业" prop="enterpriseName" min-width="200" show-overflow-tooltip />
        <el-table-column label="区域" width="140">
          <template #default="scope">
            <span>{{ formatRegionName(scope.row.regionCode) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="预警内容" prop="content" min-width="260" show-overflow-tooltip />
        <el-table-column label="工单状态" prop="warnStatus" width="110">
          <template #default="scope">
            <dict-tag :options="warnStatusOptions" :value="scope.row.warnStatus" />
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="180">
          <template #default="scope">
            <span>{{ formatDateTime(scope.row.createTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="办结时间" width="180">
          <template #default="scope">
            <span>{{ formatDateTime(scope.row.resolveTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column class-name="table-fill-column" min-width="1" />
        <el-table-column label="操作" fixed="right" align="center" width="180" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-button link type="info" icon="View" @click.stop="openDetail(scope.row)">详情</el-button>
            <el-button
              v-if="!isReadOnlyRole"
              link
              type="primary"
              icon="Operation"
              @click.stop="openHandleDialog(scope.row)"
              v-hasPermi="['ygb:warning:handle']"
            >
              处置
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="loadAll" />
    </el-card>

    <page-detail-dialog v-model="detailOpen" title="预警详情" width="720px">
      <template v-if="warningDetail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="预警ID">{{ warningDetail.warnId }}</el-descriptions-item>
          <el-descriptions-item label="预警级别">
            <dict-tag :options="warnLevelOptions" :value="warningDetail.warnLevel" />
          </el-descriptions-item>
          <el-descriptions-item label="预警类型">{{ warningDetail.warnType || '-' }}</el-descriptions-item>
          <el-descriptions-item label="来源模块">
            <dict-tag :options="sourceModuleOptions" :value="warningDetail.sourceModule" />
          </el-descriptions-item>
          <el-descriptions-item label="企业">{{ warningDetail.enterpriseName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="区域">{{ formatRegionName(warningDetail.regionCode) }}</el-descriptions-item>
          <el-descriptions-item label="工单状态">
            <dict-tag :options="warnStatusOptions" :value="warningDetail.warnStatus" />
          </el-descriptions-item>
          <el-descriptions-item label="处置人">{{ warningDetail.assignName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ formatDateTime(warningDetail.createTime) }}</el-descriptions-item>
          <el-descriptions-item label="办结时间">{{ formatDateTime(warningDetail.resolveTime) }}</el-descriptions-item>
          <el-descriptions-item label="预警内容" :span="2">{{ warningDetail.content || '-' }}</el-descriptions-item>
          <el-descriptions-item label="证据链接" :span="2">{{ warningDetail.evidenceUrl || '-' }}</el-descriptions-item>
        </el-descriptions>

        <div class="ygb-detail-block">
          <h3>办理提示</h3>
          <div class="ygb-tag-list">
            <el-tag v-for="item in detailHintTags" :key="item.label" :type="item.type" effect="plain">{{ item.label }}</el-tag>
          </div>
        </div>

        <div class="ygb-log-block">
          <h3>处置日志</h3>
          <el-table :data="warningLogs" empty-text="暂无处置日志">
            <el-table-column label="日志ID" prop="logId" width="90" />
            <el-table-column label="动作" prop="actionType" width="100" />
            <el-table-column label="前置状态" prop="beforeStatus" width="100" />
            <el-table-column label="后置状态" prop="afterStatus" width="100" />
            <el-table-column label="处置意见" prop="opinion" min-width="200" show-overflow-tooltip />
            <el-table-column label="处理人" prop="handlerName" width="100" />
            <el-table-column label="处理时间" width="180">
              <template #default="scope">
                <span>{{ formatDateTime(scope.row.handleTime) }}</span>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </template>
    </page-detail-dialog>

    <el-dialog title="预警处置" v-model="handleOpen" width="560px" append-to-body>
      <el-form ref="handleRef" :model="handleForm" :rules="handleRules" label-width="100px">
        <el-form-item label="处置动作" prop="action">
          <el-select v-model="handleForm.action" placeholder="请选择处置动作">
            <el-option v-for="item in actionOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="处置意见" prop="opinion">
          <el-input v-model="handleForm.opinion" type="textarea" :rows="4" placeholder="请输入处置意见" />
        </el-form-item>
        <el-form-item label="附件链接" prop="attachmentUrls">
          <el-input v-model="handleForm.attachmentUrls" placeholder="可选，多个附件请用英文逗号分隔" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button v-if="!isReadOnlyRole" type="primary" @click="submitHandle">确定</el-button>
          <el-button @click="handleOpen = false">取消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="YgbWarning">
import { computed, getCurrentInstance, watchEffect } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useWorkbenchAssist } from '@/composables/useWorkbenchAssist'
import { decoratePortalExplanationItems, openPortalExplanationAction } from '@/utils/portalExplanation'
import { applyWorkbenchRouteQuery, buildWorkbenchContext, stripWorkbenchRouteQuery } from '@/utils/workbenchLink'
import { useRoleViewMode } from '@/utils/roleView'
import {
  actionOptions,
  formatRegionName,
  optionLabel,
  sourceModuleOptions,
  useWarningPage,
  warnLevelOptions,
  warnStatusOptions
} from '@/views/warning/useWarningPage'

const { proxy } = getCurrentInstance()
const { setPageGuide } = useWorkbenchAssist()
const route = useRoute()
const router = useRouter()
const { isReadOnlyRole, readOnlyRoleLabel, readOnlyRoleDescription } = useRoleViewMode()

const {
  warningList,
  enterpriseOptions,
  warningDetail,
  warningLogs,
  warningSummary,
  warningAnalysis,
  currentWarning,
  loading,
  showSearch,
  total,
  detailOpen,
  handleOpen,
  queryParams,
  handleForm,
  handleRules,
  regionOptions,
  loadAll,
  loadEnterpriseOptions,
  handleQuery,
  handleExport,
  handleRowClick,
  openDetail,
  openHandleDialog,
  submitHandle
} = useWarningPage({
  exportFilePrefix: 'ygb_warning',
  canHandle: () => !isReadOnlyRole.value,
  onBlockedAction: actionLabel => {
    proxy.$modal.msgWarning(`${readOnlyRoleLabel.value}仅保留预警查看、详情和导出，不能${actionLabel}`)
  },
  immediate: false
})

const warningWorkbenchFields = ['enterpriseId', 'regionCode', 'warnLevel', 'sourceModule', 'warnStatus', 'content', 'focusKey']

const portalExplanations = computed(() => warningSummary.value.ygbExplanation || [])
const portalExplanationItems = computed(() => decoratePortalExplanationItems(portalExplanations.value, {
  portalCode: 'ygb',
  panelTitle: '530.1 办理链解释',
  panelDescription: '预警来源、整改优先级和推荐下钻统一来自粤工保解释聚合接口。'
}))

const workbenchContext = computed(() => buildWorkbenchContext(route.query, {
  fields: warningWorkbenchFields,
  sourceLabel: '粤工保工作台',
  title: '当前预警列表沿用了工作台来源条件',
  description: '当前列表保留了上游带入的企业、区域、级别或解释焦点，便于继续办理和回查。',
  fieldLabels: {
    enterpriseId: '企业',
    regionCode: '区域',
    warnLevel: '预警级别',
    sourceModule: '来源模块',
    warnStatus: '工单状态',
    content: '预警内容',
    focusKey: '解释焦点'
  },
  fieldFormatters: {
    enterpriseId: value => {
      const matched = enterpriseOptions.value.find(item => String(item.enterpriseId) === String(value))
      return matched ? matched.enterpriseName : value
    },
    regionCode: value => formatRegionName(value, value),
    warnLevel: value => optionLabel(warnLevelOptions, value),
    sourceModule: value => optionLabel(sourceModuleOptions, value),
    warnStatus: value => optionLabel(warnStatusOptions, value),
    focusKey: value => warningFocusLabel(value)
  }
}))

const summaryCards = computed(() => ([
  {
    key: 'pendingCount',
    label: '待处理预警',
    value: warningSummary.value.pendingCount ?? 0,
    unit: '条',
    note: '需要继续核实和推动整改的预警工单数量。',
    cardClass: 'ygb-summary-card--warning'
  },
  {
    key: 'processingCount',
    label: '处理中预警',
    value: warningSummary.value.processingCount ?? 0,
    unit: '条',
    note: '已经进入整改或核实阶段的预警工单数量。',
    cardClass: 'ygb-summary-card--primary'
  },
  {
    key: 'socialTax',
    label: '社保个税联动',
    value: Number(warningSummary.value.socialCount || 0) + Number(warningSummary.value.taxCount || 0),
    unit: '条',
    note: '优先影响企业办理和合规闭环的联动预警总量。',
    cardClass: 'ygb-summary-card--success'
  },
  {
    key: 'closedCount',
    label: '已办结预警',
    value: warningSummary.value.closedCount ?? 0,
    unit: '条',
    note: '已经形成整改结论并完成归档的预警工单数量。',
    cardClass: ''
  }
]))

const analysisOverviewCards = computed(() => ([
  {
    key: 'overduePendingCount',
    label: '超48小时待办',
    value: warningAnalysis.value.overduePendingCount ?? 0,
    unit: '条',
    note: '超过48小时仍未闭环的待处理或处理中预警数量。',
    cardClass: 'ygb-summary-card--warning'
  },
  {
    key: 'closedWithin72hCount',
    label: '72小时内办结',
    value: warningAnalysis.value.closedWithin72hCount ?? 0,
    unit: '条',
    note: '从预警创建到闭环在72小时内完成的办结预警数量。',
    cardClass: 'ygb-summary-card--success'
  }
]))

const analysisSections = computed(() => ([
  {
    key: 'level',
    title: '按预警等级',
    desc: '快速识别红警、黄警、提示类预警的结构分布。',
    filterField: 'warnLevel',
    items: normalizeAnalysisItems(warningAnalysis.value.levelStats, 'warnLevel')
  },
  {
    key: 'source',
    title: '按来源模块',
    desc: '识别社保、个税、扩面、设备等预警来源的集中点。',
    filterField: 'sourceModule',
    items: normalizeAnalysisItems(warningAnalysis.value.sourceStats, 'sourceModule')
  },
  {
    key: 'region',
    title: '按区域分布',
    desc: '查看当前筛选范围内预警最集中的区域。',
    filterField: 'regionCode',
    items: normalizeAnalysisItems(warningAnalysis.value.regionStats, 'regionCode')
  },
  {
    key: 'status',
    title: '按处理状态',
    desc: '评估待处理、处理中、已办结和升级预警的结构。',
    filterField: 'warnStatus',
    items: normalizeAnalysisItems(warningAnalysis.value.statusStats, 'warnStatus')
  }
]))

const selectedWarningOverview = computed(() => {
  if (!currentWarning.value) {
    return [
      { label: '所属企业', value: '-' },
      { label: '预警级别', value: '-' },
      { label: '来源模块', value: '-' },
      { label: '工单状态', value: '-' }
    ]
  }
  return [
    { label: '所属企业', value: currentWarning.value.enterpriseName || '-' },
    { label: '预警级别', value: optionLabel(warnLevelOptions, currentWarning.value.warnLevel) },
    { label: '来源模块', value: optionLabel(sourceModuleOptions, currentWarning.value.sourceModule) },
    { label: '工单状态', value: optionLabel(warnStatusOptions, currentWarning.value.warnStatus) }
  ]
})

const warningHintTags = computed(() => buildHintTags(currentWarning.value))
const detailHintTags = computed(() => buildHintTags(warningDetail.value || currentWarning.value))
watchEffect(() => {
  setPageGuide({
    title: '联动预警办理闭环',
    description: '统一查看当前页的门户解释、当前选中预警与办理提示。',
    portalExplanation: portalExplanationItems.value,
    selection: selectedWarningOverview.value,
    hints: warningHintTags.value
  })
})
const readOnlyAlertTitle = computed(() => `${readOnlyRoleLabel.value}仅保留预警查看、详情和导出`)
const readOnlyAlertDescription = computed(() => `${readOnlyRoleDescription.value || ''} 当前页面仍展示摘要、解释和来源条件，但不开放工单处置动作。`.trim())

function warningFocusLabel(value) {
  if (value === 'pending') return '待处理工单'
  if (value === 'processing') return '处理中工单'
  if (value === 'financial') return '社保个税联动'
  if (value === 'expansion') return '扩面减损联动'
  if (value === 'red') return '红警工单'
  return value || '-'
}

function normalizeAnalysisItems(items = [], field) {
  return (Array.isArray(items) ? items : []).map(item => ({
    ...item,
    dimensionLabel: resolveAnalysisLabel(field, item.dimensionKey, item.dimensionLabel),
    dimensionCount: Number(item.dimensionCount || 0)
  }))
}

function resolveAnalysisLabel(field, value, fallback) {
  if (field === 'warnLevel') {
    return optionLabel(warnLevelOptions, value, fallback || value || '-')
  }
  if (field === 'sourceModule') {
    return optionLabel(sourceModuleOptions, value, fallback || value || '-')
  }
  if (field === 'warnStatus') {
    return optionLabel(warnStatusOptions, value, fallback || value || '-')
  }
  if (field === 'regionCode') {
    return formatRegionName(value, fallback || value || '-')
  }
  return fallback || value || '-'
}

function applyAnalysisFilter(field, value) {
  queryParams.value.pageNum = 1
  queryParams.value[field] = value || undefined
  loadAll()
}

function buildHintTags(warning) {
  if (!warning) {
    return [{ label: '未选中工单，可先在列表中选择待处理预警。', type: 'info' }]
  }
  const tags = []
  if (warning.warnLevel === '3') {
    tags.push({ label: '红警工单，建议优先督办并跟踪时效。', type: 'danger' })
  }
  if (warning.warnStatus === '0') {
    tags.push({ label: '当前待处理，建议先核实企业并转处理中。', type: 'warning' })
  }
  if (warning.warnStatus === '1') {
    tags.push({ label: '当前处理中，建议补齐整改意见和附件。', type: 'info' })
  }
  if (warning.warnStatus === '4') {
    tags.push({ label: '当前已升级，需要继续跟踪上级处置结果。', type: 'danger' })
  }
  if (warning.sourceModule === 'SOCIAL') {
    tags.push({ label: '社保联动来源，适合财务和人社监管优先核查。', type: 'success' })
  }
  if (warning.sourceModule === 'TAX') {
    tags.push({ label: '个税联动来源，建议联动工资和税务比对结果复核。', type: 'success' })
  }
  if (warning.sourceModule === 'EXPANSION') {
    tags.push({ label: '扩面减损来源，建议核对漏保对象和整改责任。', type: 'warning' })
  }
  if (warning.sourceModule === 'DEVICE') {
    tags.push({ label: '设备联动来源，必要时同步查看设备状态和处置留痕。', type: 'info' })
  }
  if (warning.sourceModule === 'INJURY') {
    tags.push({ label: '工伤事件来源，建议关注办结时限和证据材料。', type: 'warning' })
  }
  if (!tags.length) {
    tags.push({ label: '当前工单信息完整，可按既定办理流程继续闭环。', type: 'success' })
  }
  return tags
}

function formatDateTime(value) {
  if (!value) {
    return '-'
  }
  return proxy.parseTime(value, '{y}-{m}-{d} {h}:{i}:{s}')
}

function resetQuery() {
  proxy.resetForm('queryRef')
  Object.assign(queryParams.value, {
    pageNum: 1,
    pageSize: 10,
    regionCode: undefined,
    enterpriseId: undefined,
    warnLevel: undefined,
    sourceModule: undefined,
    warnStatus: undefined,
    content: undefined
  })
  applyWorkbenchRouteQuery(route.query, queryParams.value, warningWorkbenchFields)
  loadAll()
}

function clearWorkbenchContext() {
  Object.assign(queryParams.value, {
    pageNum: 1,
    pageSize: 10,
    enterpriseId: undefined,
    regionCode: undefined,
    warnLevel: undefined,
    sourceModule: undefined,
    warnStatus: undefined,
    content: undefined,
    focusKey: undefined
  })
  router.replace({
    path: route.path,
    query: stripWorkbenchRouteQuery(route.query, warningWorkbenchFields)
  })
  loadAll()
}

function applyWarningWorkbenchQuery(routeQuery = {}) {
  Object.assign(queryParams.value, {
    pageNum: 1,
    pageSize: 10,
    regionCode: undefined,
    enterpriseId: undefined,
    warnLevel: undefined,
    sourceModule: undefined,
    warnStatus: undefined,
    content: undefined,
    focusKey: undefined
  })
  applyWorkbenchRouteQuery(routeQuery, queryParams.value, warningWorkbenchFields)
  loadAll()
}

function handlePortalExplanationAction(action) {
  if (action?.path === route.path) {
    applyWarningWorkbenchQuery(action.query || {})
  }
  openPortalExplanationAction(router, action)
}

applyWorkbenchRouteQuery(route.query, queryParams.value, warningWorkbenchFields)
loadEnterpriseOptions()
loadAll()
</script>

<style scoped lang="scss">
.ygb-warning-page {
  .ygb-workbench-alert {
    margin-bottom: 16px;
  }

  .ygb-workbench-alert__title {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 12px;
  }

  .ygb-workbench-alert__desc {
    display: flex;
    flex-wrap: wrap;
    gap: 8px 12px;
    margin-bottom: 12px;
    color: #5f6f80;
    line-height: 1.7;
    font-size: 13px;
  }

  .ygb-workbench-alert__desc strong {
    color: #0f5ea8;
  }

  .ygb-page__tip {
    max-width: 460px;
    padding: 16px 18px;
    border-radius: 16px;
    background: linear-gradient(135deg, rgba(15, 94, 168, 0.08), rgba(15, 94, 168, 0.02));
    color: #36516d;
    line-height: 1.7;
    font-size: 13px;
  }

  .ygb-summary-grid,
  .ygb-analysis-grid,
  .ygb-focus-grid {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 16px;
    margin-bottom: 16px;
  }

  .ygb-summary-grid {
    grid-template-columns: repeat(4, minmax(0, 1fr));
  }

  .ygb-summary-card,
  .ygb-focus-card {
    border: 1px solid #dbe5f0;
    border-radius: 16px;
    background: #fff;
  }

  .ygb-summary-card {
    padding: 18px 20px;
  }

  .ygb-analysis-card {
    min-height: 154px;
  }

  .ygb-summary-card__label {
    color: #627486;
    font-size: 13px;
  }

  .ygb-summary-card__value {
    margin-top: 10px;
    color: #13243a;
    font-size: 28px;
    font-weight: 700;
  }

  .ygb-summary-card__unit {
    margin-left: 4px;
    font-size: 13px;
    font-weight: 500;
    color: #7b8da1;
  }

  .ygb-summary-card__note {
    margin-top: 10px;
    color: #5f6f80;
    line-height: 1.7;
    font-size: 13px;
  }

  .ygb-summary-card--success {
    background: linear-gradient(180deg, #ffffff 0%, #f3fbf5 100%);
  }

  .ygb-summary-card--warning {
    background: linear-gradient(180deg, #ffffff 0%, #fff9ef 100%);
  }

  .ygb-summary-card--primary {
    background: linear-gradient(180deg, #ffffff 0%, #f2f7fd 100%);
  }

  .ygb-focus-card :deep(.el-card__header),
  .ygb-analysis-panel :deep(.el-card__header) {
    padding: 18px 20px 10px;
    border-bottom: none;
  }

  .ygb-focus-card :deep(.el-card__body),
  .ygb-analysis-panel :deep(.el-card__body) {
    padding: 0 20px 20px;
  }

  .ygb-card-head {
    display: grid;
    gap: 6px;
  }

  .ygb-card-head__title {
    color: #13243a;
    font-size: 16px;
    font-weight: 700;
  }

  .ygb-card-head__desc {
    color: #627486;
    line-height: 1.7;
    font-size: 13px;
  }

  .ygb-focus-list {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 12px;
  }

  .ygb-focus-item {
    padding: 14px 16px;
    border-radius: 14px;
    background: #f7fafc;
  }

  .ygb-focus-item__label {
    color: #627486;
    font-size: 13px;
  }

  .ygb-focus-item__value {
    margin-top: 8px;
    color: #13243a;
    font-weight: 700;
  }

  .ygb-tag-list {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
  }

  .ygb-analysis-list {
    display: grid;
    gap: 10px;
  }

  .ygb-analysis-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 16px;
    width: 100%;
    padding: 12px 14px;
    border: 1px solid #e0e8f0;
    border-radius: 12px;
    background: #f7fafc;
    color: #13243a;
    cursor: pointer;
    transition: all 0.2s ease;
    text-align: left;
  }

  .ygb-analysis-item:hover {
    border-color: #0f5ea8;
    background: #eef5ff;
  }

  .ygb-analysis-item__label {
    color: #425568;
    line-height: 1.6;
  }

  .ygb-analysis-item__value {
    color: #13243a;
    font-size: 18px;
    font-weight: 700;
  }

  .ygb-detail-block,
  .ygb-log-block {
    margin-top: 16px;
  }

  .ygb-detail-block h3,
  .ygb-log-block h3 {
    margin: 0 0 12px;
    color: #13243a;
    font-size: 16px;
  }

  @media (max-width: 1200px) {
    .ygb-summary-grid,
    .ygb-analysis-grid {
      grid-template-columns: repeat(2, minmax(0, 1fr));
    }
  }

  @media (max-width: 768px) {
    .ygb-summary-grid,
    .ygb-analysis-grid,
    .ygb-focus-grid,
    .ygb-focus-list {
      grid-template-columns: minmax(0, 1fr);
    }
  }
}
</style>

