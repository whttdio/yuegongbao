<template>
  <div class="app-container ygb-page">
    <section class="gov-page-header ygb-page__header">
      <div>
        <p class="ygb-page__eyebrow">职业病监管线</p>
        <h1 class="ygb-page__title">职业病监测台账</h1>
        <p class="ygb-page__desc">
          围绕区域、行业、发病人数和千人发病率组织职业病监测台账，
          面向企业管理员、工地负责人和监管经办优先识别高风险区域行业组合，承接预警和月度归档。
        </p>
      </div>
      <div class="ygb-table-tip">
        当前阶段仍为 Stub 同步，字段结构已与后续正式接口保持一致；达到阈值的区域行业组合会自动联动预警中心。
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

    <div class="ygb-submodule-grid">
      <button
        v-for="item in submoduleEntries"
        :key="item.key"
        type="button"
        class="ygb-submodule-card"
        @click="openSubmodule(item)"
      >
        <div class="ygb-submodule-card__head">
          <strong>{{ item.title }}</strong>
          <span>{{ item.actionText }}</span>
        </div>
        <p>{{ item.desc }}</p>
      </button>
    </div>


    <el-card class="search-card ygb-search-card" shadow="never">
      <el-form ref="queryRef" :model="queryParams" :inline="true" v-show="showSearch">
        <el-form-item label="统计月份">
          <el-date-picker v-model="queryParams.statMonth" type="month" format="YYYY-MM" value-format="YYYY-MM" style="width: 160px" />
        </el-form-item>
        <el-form-item label="区域">
          <el-select v-model="queryParams.regionCode" clearable style="width: 180px">
            <el-option v-for="item in regionOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="行业">
          <el-select v-model="queryParams.industryType" clearable style="width: 180px">
            <el-option v-for="item in industryOptions" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-form-item label="预警级别">
          <el-select v-model="queryParams.warningLevel" clearable style="width: 140px">
            <el-option v-for="item in warningLevelOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="预警状态">
          <el-select v-model="queryParams.warningStatus" clearable style="width: 140px">
            <el-option v-for="item in warningStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
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
        <el-col v-if="!isReadOnlyRole" :span="1.5">
          <el-button type="primary" plain icon="RefreshRight" @click="handleSync" v-hasPermi="['ygb:occupationMonitor:sync']">模拟同步</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['ygb:occupationMonitor:export']">导出</el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>
    </el-card>

    <el-card class="table-card ygb-table-card" shadow="never">
      <template #header>
        <div class="ygb-card-head ygb-card-head--between">
          <div>
            <div class="ygb-card-head__title">职业病监测台账</div>
            <div class="ygb-card-head__desc">{{ focusTableHint }}</div>
          </div>
          <div class="ygb-card-head__desc">当前总量 {{ total }} 条</div>
        </div>
      </template>
      <el-table v-loading="loading" :data="visibleMonitorList" @row-click="handleRowClick">
        <el-table-column label="ID" prop="monitorId" width="90" />
        <el-table-column label="统计月份" prop="statMonth" width="110" />
        <el-table-column label="区域" min-width="130">
          <template #default="scope">
            {{ scope.row.regionName || scope.row.regionCode }}
          </template>
        </el-table-column>
        <el-table-column label="行业" prop="industryType" min-width="140" />
        <el-table-column label="企业数" prop="enterpriseCount" width="100" />
        <el-table-column label="从业人数" prop="workerCount" width="100" />
        <el-table-column label="发病人数" prop="caseCount" width="100" />
        <el-table-column label="高风险企业" prop="highRiskEnterpriseCount" width="110" />
        <el-table-column label="千人发病率" width="120">
          <template #default="scope">
            {{ formatRate(scope.row.incidenceRate) }}
          </template>
        </el-table-column>
        <el-table-column label="预警级别" width="110">
          <template #default="scope">
            <dict-tag :options="warningLevelOptions" :value="scope.row.warningLevel" />
          </template>
        </el-table-column>
        <el-table-column label="来源消息" prop="sourceMessage" min-width="220" show-overflow-tooltip />
        <el-table-column class-name="table-fill-column" min-width="1" />

        <el-table-column label="操作" fixed="right" align="center" width="100" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-button link type="info" icon="View" @click.stop="openDetail(scope.row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>

    <page-detail-dialog v-model="detailOpen" title="职业病监测详情" width="760px">
      <template v-if="detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="统计月份">{{ detail.statMonth || '-' }}</el-descriptions-item>
          <el-descriptions-item label="预警级别">
            <dict-tag :options="warningLevelOptions" :value="detail.warningLevel" />
          </el-descriptions-item>
          <el-descriptions-item label="区域">{{ detail.regionName || detail.regionCode || '-' }}</el-descriptions-item>
          <el-descriptions-item label="行业">{{ detail.industryType || '-' }}</el-descriptions-item>
          <el-descriptions-item label="企业数">{{ detail.enterpriseCount ?? 0 }}</el-descriptions-item>
          <el-descriptions-item label="从业人数">{{ detail.workerCount ?? 0 }}</el-descriptions-item>
          <el-descriptions-item label="发病人数">{{ detail.caseCount ?? 0 }}</el-descriptions-item>
          <el-descriptions-item label="高风险企业">{{ detail.highRiskEnterpriseCount ?? 0 }}</el-descriptions-item>
          <el-descriptions-item label="千人发病率">{{ formatRate(detail.incidenceRate) }}</el-descriptions-item>
          <el-descriptions-item label="来源渠道">{{ detail.sourceChannel || '-' }}</el-descriptions-item>
          <el-descriptions-item label="回写时间">{{ parseTime(detail.callbackTime, '{y}-{m}-{d} {h}:{i}:{s}') || '-' }}</el-descriptions-item>
          <el-descriptions-item label="来源状态">{{ detail.sourceStatus || '-' }}</el-descriptions-item>
          <el-descriptions-item label="来源流水号" :span="2">{{ detail.sourceSerialNo || '-' }}</el-descriptions-item>
          <el-descriptions-item label="来源消息" :span="2">{{ detail.sourceMessage || '-' }}</el-descriptions-item>
        </el-descriptions>

        <div class="detail-block">
          <h3>办理提示</h3>
          <div class="ygb-tag-list">
            <el-tag v-for="item in detailHintTags" :key="item.label" :type="item.type" effect="plain">{{ item.label }}</el-tag>
          </div>
        </div>
      </template>
    </page-detail-dialog>
  </div>
</template>

<script setup name="YgbOccupationMonitor">
import { listOccupationMonitor, getOccupationMonitorSummary, syncOccupationMonitor } from '@/api/ygb/occupationMonitor'
import { useRoute, useRouter } from 'vue-router'
import { useWorkbenchAssist } from '@/composables/useWorkbenchAssist'
import { decoratePortalExplanationItems, openPortalExplanationAction } from '@/utils/portalExplanation'
import { useRoleViewMode } from '@/utils/roleView'
import { applyWorkbenchRouteQuery, buildWorkbenchContext, stripWorkbenchRouteQuery } from '@/utils/workbenchLink'

const { proxy } = getCurrentInstance()
const route = useRoute()
const { setPageGuide } = useWorkbenchAssist()
const router = useRouter()
const { isReadOnlyRole, readOnlyRoleLabel, readOnlyRoleDescription } = useRoleViewMode()
const occupationMonitorWorkbenchFields = ['statMonth', 'regionCode', 'industryType', 'warningLevel', 'warningStatus', 'focusKey']

const regionOptions = [
  { label: '广东省', value: '440000' },
  { label: '广州市天河区', value: '440106' },
  { label: '深圳市南山区', value: '440305' },
  { label: '佛山市顺德区', value: '440606' }
]

const industryOptions = ['建筑施工', '制造加工', '平台配送']

const warningLevelOptions = [
  { label: '正常', value: '0' },
  { label: '黄色', value: '1' },
  { label: '红色', value: '2' }
]

const warningStatusOptions = [
  { label: '未预警', value: '0' },
  { label: '已预警', value: '1' }
]

const monitorList = ref([])
const summaryData = ref({})
const currentMonitor = ref(undefined)
const detail = ref(undefined)
const loading = ref(false)
const showSearch = ref(true)
const total = ref(0)
const detailOpen = ref(false)
const activeFocusKey = ref('')

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    statMonth: currentMonth(),
    regionCode: undefined,
    industryType: undefined,
    warningLevel: undefined,
    warningStatus: undefined
  }
})

const { queryParams } = toRefs(data)

applyWorkbenchRouteQuery(route.query, queryParams.value, occupationMonitorWorkbenchFields)

const summaryCards = computed(() => ([
  {
    key: 'total',
    label: '监测记录',
    value: summaryData.value.totalCount != null ? summaryData.value.totalCount : total.value,
    unit: '条',
    note: '当前筛选条件下已进入职业病监测台账的区域行业记录总量。',
    cardClass: ''
  },
  {
    key: 'yellow',
    label: '黄警记录',
    value: valueOrDefault(summaryData.value.yellowCount, 0),
    unit: '条',
    note: '达到预警阈值但未进入红警的记录，需要优先复核行业口径和发病趋势。',
    cardClass: 'ygb-summary-card--warning'
  },
  {
    key: 'red',
    label: '红警记录',
    value: valueOrDefault(summaryData.value.redCount, 0),
    unit: '条',
    note: '发病人数或千人发病率已达到高风险阈值，需要直接承接监管和预警闭环。',
    cardClass: 'ygb-summary-card--primary'
  },
  {
    key: 'case',
    label: '发病人数',
    value: valueOrDefault(summaryData.value.totalCaseCount, 0),
    unit: '人',
    note: '当前筛选范围内累计发病人数，用于判断区域行业风险热度。',
    cardClass: 'ygb-summary-card--success'
  }
]))

const focusQueues = computed(() => {
  const rows = monitorList.value || []
  const queues = [
    {
      key: 'red',
      title: '红警组合优先承接',
      desc: '优先承接红警区域行业组合，直接进入预警和监管闭环。',
      count: rows.filter(item => item.warningLevel === '2').length,
      unit: '条',
      actionText: rows.some(item => item.warningLevel === '2') ? '立即承接' : '暂无红警'
    },
    {
      key: 'yellow',
      title: '黄警组合复核口径',
      desc: '优先复核黄警组合的行业归类、样本覆盖和发病统计口径。',
      count: rows.filter(item => item.warningLevel === '1').length,
      unit: '条',
      actionText: rows.some(item => item.warningLevel === '1') ? '先行复核' : '状态平稳'
    },
    {
      key: 'case',
      title: '高发病组合追踪',
      desc: '优先处理发病人数较高的组合，避免风险聚集后仍停留在统计层。',
      count: rows.filter(item => Number(item.caseCount || 0) >= 3).length,
      unit: '条',
      actionText: rows.some(item => Number(item.caseCount || 0) >= 3) ? '继续追踪' : '无集中暴露'
    },
    {
      key: 'highRiskEnterprise',
      title: '高风险企业聚集复核',
      desc: '优先下钻高风险企业较多的组合，判断是否需要转企业侧办理复核。',
      count: rows.filter(item => Number(item.highRiskEnterpriseCount || 0) > 0).length,
      unit: '条',
      actionText: rows.some(item => Number(item.highRiskEnterpriseCount || 0) > 0) ? '下钻企业' : '暂无聚集'
    }
  ]
  if (!queues.find(item => item.key === activeFocusKey.value)) {
    activeFocusKey.value = queues[0]?.key || ''
  }
  return queues
})

function buildOccupationMonitorExplanationQuery(extraQuery = {}) {
  return {
    statMonth: queryParams.value.statMonth,
    regionCode: queryParams.value.regionCode,
    industryType: queryParams.value.industryType,
    warningLevel: queryParams.value.warningLevel,
    warningStatus: queryParams.value.warningStatus,
    ...extraQuery
  }
}

function buildOccupationSubmoduleQuery(extraQuery = {}) {
  return {
    statMonth: queryParams.value.statMonth,
    regionCode: queryParams.value.regionCode,
    industryType: queryParams.value.industryType,
    warningLevel: queryParams.value.warningLevel,
    wbSourceTitle: '职业病监测主页面来源条件',
    wbSourceLabel: '职业病监测',
    wbSourceDescription: '沿用主页面筛选条件进入子页，保持同口径台账、详情和导出。',
    ...extraQuery
  }
}

const fallbackPortalExplanations = computed(() => focusQueues.value.map(item => ({
  key: item.key,
  dimensionName: item.title,
  currentValue: item.count,
  targetValue: item.key === 'case' ? '< 3' : '-',
  summary: item.desc,
  evidenceModule: 'occupationMonitor',
  recommendModule: 'occupationMonitor',
  defaultQuery: buildOccupationMonitorExplanationQuery({ focusKey: item.key }),
  sourceLabel: '530.1 职业监测办理解释',
  sourceDescription: item.desc,
  actionText: item.actionText
})))

const portalExplanations = computed(() => (Array.isArray(summaryData.value.ygbExplanation) && summaryData.value.ygbExplanation.length
  ? summaryData.value.ygbExplanation
  : fallbackPortalExplanations.value))

const portalExplanationItems = computed(() => decoratePortalExplanationItems(portalExplanations.value, {
  portalCode: 'ygb',
  panelTitle: '530.1 职业监测办理解释',
  panelDescription: '当前解释项按 530.1 办理链口径展示职业监测对象、风险和跟进重点。'
}))

const activeFocus = computed(() => focusQueues.value.find(item => item.key === activeFocusKey.value) || focusQueues.value[0])

const workbenchContext = computed(() => buildWorkbenchContext(route.query, {
  fields: occupationMonitorWorkbenchFields,
  title: '当前职业监测页沿用了上游来源条件。',
  description: '当前页面复用了上游工作台筛选，可在同一门户语境下继续处理监测对象和风险问题。',
  fieldLabels: {
    statMonth: 'Month',
    regionCode: 'Region',
    industryType: 'Industry',
    warningLevel: 'Warning level',
    warningStatus: 'Warning',
    focusKey: 'Focus'
  },
  fieldFormatters: {
    warningLevel: value => warningLevelLabel(value),
    focusKey: value => occupationMonitorFocusLabel(value)
  }
}))

const selectedMonitorOverview = computed(() => {
  if (!currentMonitor.value) {
    return [
      { label: '区域/行业', value: '-' },
      { label: '企业/从业人数', value: '-' },
      { label: '发病/预警', value: '-' },
      { label: '高风险企业/来源', value: '-' }
    ]
  }
  return [
    {
      label: '区域/行业',
      value: `${currentMonitor.value.regionName || currentMonitor.value.regionCode || '-'} / ${currentMonitor.value.industryType || '-'}`
    },
    {
      label: '企业/从业人数',
      value: `${currentMonitor.value.enterpriseCount ?? 0} / ${currentMonitor.value.workerCount ?? 0}`
    },
    {
      label: '发病/预警',
      value: `${currentMonitor.value.caseCount ?? 0} / ${warningLevelLabel(currentMonitor.value.warningLevel)}`
    },
    {
      label: '高风险企业/来源',
      value: `${currentMonitor.value.highRiskEnterpriseCount ?? 0} / ${currentMonitor.value.sourceChannel || '-'}`
    }
  ]
})

const visibleMonitorList = computed(() => prioritizeFocusRows(monitorList.value, row => matchMonitorFocus(row, activeFocus.value?.key)))

const submoduleEntries = computed(() => ([
  {
    key: 'prevention',
    title: '职业病预防项目',
    desc: '进入只读预防项目台账，沿用月份、区域、行业和预警级别条件。',
    actionText: '打开预防台账',
    path: '/ygb-occupation/occupationPrevention',
    query: buildOccupationSubmoduleQuery()
  },
  {
    key: 'healthArchive',
    title: '职业健康档案',
    desc: '进入只读健康档案台账，继续按同口径核查月份、区域和行业对象。',
    actionText: '打开档案台账',
    path: '/ygb-occupation/occupationHealthArchive',
    query: buildOccupationSubmoduleQuery()
  }
]))

const focusTableHint = computed(() => {
  if (!activeFocus.value) {
    return '当前按查询条件展示监测记录。'
  }
  return `${activeFocus.value.title}，系统已将符合焦点条件的组合前置，便于监管经办优先处理高风险区域行业记录。`
})

const workflowSteps = computed(() => ([
  {
    label: '先锁定统计月份',
    desc: '先按统计月份、区域和行业范围锁定本次监测台账，避免跨月和跨行业混查。'
  },
  {
    label: '执行卫健 Stub 同步',
    desc: '按月份重新拉取区域行业监测记录，统一回写发病人数、千人发病率和来源消息。'
  },
  {
    label: '复核区域行业口径',
    desc: '优先复核黄警、红警对象的行业归类、企业覆盖范围和发病统计口径，排除统计偏差。'
  },
  {
    label: '承接预警闭环',
    desc: '对已触发预警的区域行业组合继续承接到预警中心，避免问题停留在监测结果页。'
  },
  {
    label: '完成月度监管归档',
    desc: '完成核查和处置的对象沉淀为月度监管底稿，供后续人社和区域监管复核。'
  }
]))

const primaryMonitorAction = computed(() => {
  if (isReadOnlyRole.value) {
    return { label: '鏌ョ湅褰掓。璇︽儏' }
  }
  if (!currentMonitor.value) {
    return { label: '选择待办对象' }
  }
  if (currentMonitor.value.warningLevel === '2') {
    return { label: '承接红警闭环' }
  }
  if (currentMonitor.value.warningLevel === '1') {
    return { label: '复核黄警口径' }
  }
  if (Number(currentMonitor.value.caseCount || 0) >= 3) {
    return { label: '追踪高发病组合' }
  }
  if (Number(currentMonitor.value.highRiskEnterpriseCount || 0) > 0) {
    return { label: '下钻高风险企业' }
  }
  return { label: '查看归档详情' }
})

const readOnlyAlertTitle = computed(() => `${readOnlyRoleLabel.value}仅保留职业病监测查看、详情和导出`)
const readOnlyAlertDescription = computed(() => readOnlyRoleDescription.value || `${readOnlyRoleLabel.value}不能执行职业病监测同步等办理动作。`)

const currentMonitorActionSummary = computed(() => {
  if (!currentMonitor.value) {
    return '先从待办焦点中选择一条监测记录，再决定是承接红警、复核黄警、追踪高发病还是下钻企业风险。'
  }
  if (currentMonitor.value.warningLevel === '2') {
    return '该组合当前为红警，建议直接承接区域监管和预警闭环，避免高风险组合继续停留在监测结果层。'
  }
  if (currentMonitor.value.warningLevel === '1') {
    return '该组合当前为黄警，应优先复核行业归类、覆盖样本和发病统计口径，再决定是否升级处置。'
  }
  if (Number(currentMonitor.value.caseCount || 0) >= 3) {
    return '该组合发病人数已进入重点观察区间，建议继续追踪发病趋势并准备联动企业侧办理复核。'
  }
  if (Number(currentMonitor.value.highRiskEnterpriseCount || 0) > 0) {
    return '该组合存在高风险企业聚集，建议继续下钻企业清单，判断是否进入办理复核或预警承接。'
  }
  return '该组合当前状态相对平稳，可沉淀为月度监测底稿，供后续区域监管抽查。'
})

const currentMonitorActionTags = computed(() => {
  if (!currentMonitor.value) {
    return [{ label: '待选择具体监测对象', type: 'info' }]
  }
  return buildHintTags(currentMonitor.value)
})

const monitorHintTags = computed(() => buildHintTags(currentMonitor.value))
const detailHintTags = computed(() => buildHintTags(detail.value))

function getList() {
  loading.value = true
  Promise.all([
    listOccupationMonitor(queryParams.value),
    getOccupationMonitorSummary(buildSummaryQuery())
  ]).then(([listResponse, summaryResponse]) => {
    monitorList.value = listResponse.rows || []
    total.value = listResponse.total || 0
    summaryData.value = summaryResponse.data || {}
    syncCurrentMonitor()
    loading.value = false
  }).catch(() => {
    loading.value = false
  })
}

function buildSummaryQuery() {
  return {
    statMonth: queryParams.value.statMonth,
    regionCode: queryParams.value.regionCode,
    industryType: queryParams.value.industryType,
    warningLevel: queryParams.value.warningLevel,
    warningStatus: queryParams.value.warningStatus
  }
}

function syncCurrentMonitor() {
  if (currentMonitor.value) {
    const matched = visibleMonitorList.value.find(item => item.monitorId === currentMonitor.value.monitorId)
    if (matched) {
      currentMonitor.value = matched
      return
    }
  }
  currentMonitor.value = visibleMonitorList.value.length > 0 ? visibleMonitorList.value[0] : undefined
}

function handleQuery() {
  queryParams.value.pageNum = 1
  getList()

watchEffect(() => {
  setPageGuide({
    title: '????????' || '????????',
    description: '?????????????????????????????????' || '?????????????????????????????????',
    portalExplanation: portalExplanationItems.value,
    focus: focusQueues.value,
    selection: [...selectedMonitorOverview.value, { label: '??????', value: currentMonitorActionSummary.value }],
    workflow: workflowSteps.value,
    hints: [...currentMonitorActionTags.value].slice(0, 6)
  })
})

}

function resetQuery() {
  proxy.resetForm('queryRef')
  Object.assign(queryParams.value, {
    pageNum: 1,
    pageSize: 10,
    statMonth: currentMonth(),
    regionCode: undefined,
    industryType: undefined,
    warningLevel: undefined,
    warningStatus: undefined
  })
  applyWorkbenchRouteQuery(route.query, queryParams.value, occupationMonitorWorkbenchFields)
  activeFocusKey.value = resolveOccupationMonitorFocusKey(route.query.focusKey)
  getList()
}

function clearWorkbenchContext() {
  activeFocusKey.value = ''
  Object.assign(queryParams.value, {
    pageNum: 1,
    pageSize: 10,
    statMonth: currentMonth(),
    regionCode: undefined,
    industryType: undefined,
    warningLevel: undefined,
    warningStatus: undefined
  })
  router.replace({
    path: route.path,
    query: stripWorkbenchRouteQuery(route.query, occupationMonitorWorkbenchFields)
  })
  getList()
}

function applyOccupationMonitorWorkbenchQuery(routeQuery = {}) {
  Object.assign(queryParams.value, {
    pageNum: 1,
    pageSize: 10,
    statMonth: currentMonth(),
    regionCode: undefined,
    industryType: undefined,
    warningLevel: undefined,
    warningStatus: undefined
  })
  applyWorkbenchRouteQuery(routeQuery, queryParams.value, occupationMonitorWorkbenchFields)
  activeFocusKey.value = resolveOccupationMonitorFocusKey(routeQuery.focusKey)
  syncCurrentMonitor()
  getList()
}

function handleRowClick(row) {
  currentMonitor.value = row
}

function handleSelectFocus(item) {
  activeFocusKey.value = item.key
  syncCurrentMonitor()
}

function openDetail(row) {
  if (!row) {
    return
  }
  currentMonitor.value = row
  detail.value = row
  detailOpen.value = true
}

function handleSync() {
  if (isReadOnlyRole.value) {
    blockReadOnlyAction('执行职业病监测同步')
    return
  }
  if (!queryParams.value.statMonth) {
    proxy.$modal.msgWarning('请先选择统计月份')
    return
  }
  syncOccupationMonitor({
    statMonth: queryParams.value.statMonth
  }).then(response => {
    proxy.$modal.msgSuccess(response.msg || '模拟同步完成')
    getList()
  })
}

function handleExport() {
  proxy.download('ygb/occupation/monitor/export', { ...queryParams.value }, `occupation_monitor_${new Date().getTime()}.xlsx`)
}

function handlePrimaryMonitorAction() {
  if (!currentMonitor.value) {
    return
  }
  openDetail(currentMonitor.value)
}

function openSubmodule(item) {
  if (!item?.path) {
    return
  }
  proxy.$tab.openPage(item.title, item.path, item.query || {})
}

function handlePortalExplanationAction(action) {
  if (action?.path === route.path) {
    applyOccupationMonitorWorkbenchQuery(action.query || {})
  }
  openPortalExplanationAction(router, action)
}

function blockReadOnlyAction(actionLabel) {
  proxy?.$modal?.msgWarning?.(`${readOnlyRoleLabel.value}仅保留职业病监测查看、详情和导出，不能${actionLabel}`)
}

function buildHintTags(item) {
  if (!item) {
    return [{ label: '未选中监测对象，可先在列表中选择待办理记录', type: 'info' }]
  }
  const tags = []
  if (item.warningLevel === '2') {
    tags.push({ label: '当前为红警记录，建议直接承接区域监管和预警闭环', type: 'danger' })
  } else if (item.warningLevel === '1') {
    tags.push({ label: '当前为黄警记录，建议优先复核行业口径和发病趋势', type: 'warning' })
  } else {
    tags.push({ label: '当前监测状态正常，可继续做月度监管归档', type: 'success' })
  }
  if (Number(item.caseCount || 0) >= 5) {
    tags.push({ label: '发病人数已达到高风险阈值，建议直接联动区域监管', type: 'danger' })
  } else if (Number(item.caseCount || 0) >= 3) {
    tags.push({ label: '发病人数已进入重点观察区间，建议继续复核覆盖范围', type: 'warning' })
  }
  if (Number(item.highRiskEnterpriseCount || 0) > 0) {
    tags.push({ label: '当前存在高风险企业，建议继续下钻企业级风险对象', type: 'warning' })
  }
  if (!item.sourceMessage) {
    tags.push({ label: '当前缺少来源消息，建议复查同步日志和卫健回写状态', type: 'info' })
  }
  return tags
}

function warningLevelLabel(value) {
  const matched = warningLevelOptions.find(item => item.value === value)
  return matched ? matched.label : '-'
}

function resolveOccupationMonitorFocusKey(value) {
  const normalized = String(value || '')
  return focusQueues.value.some(item => item.key === normalized) ? normalized : ''
}

function occupationMonitorFocusLabel(value) {
  return focusQueues.value.find(item => item.key === value)?.title || value || '-'
}

function valueOrDefault(value, fallback = 0) {
  return value != null ? value : fallback
}

function formatRate(value) {
  if (value == null || value === '') {
    return '0.00'
  }
  const amount = Number(value)
  if (Number.isNaN(amount)) {
    return `${value}`
  }
  return amount.toFixed(2)
}

function currentMonth() {
  const date = new Date()
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}`
}

function matchMonitorFocus(row, key) {
  if (!key) {
    return true
  }
  if (key === 'red') {
    return row.warningLevel === '2'
  }
  if (key === 'yellow') {
    return row.warningLevel === '1'
  }
  if (key === 'case') {
    return Number(row.caseCount || 0) >= 3
  }
  if (key === 'highRiskEnterprise') {
    return Number(row.highRiskEnterpriseCount || 0) > 0
  }
  return false
}

function prioritizeFocusRows(rows, predicate) {
  const matched = []
  const rest = []
  ;(rows || []).forEach(item => {
    if (predicate(item)) {
      matched.push(item)
    } else {
      rest.push(item)
    }
  })
  return [...matched, ...rest]
}

activeFocusKey.value = resolveOccupationMonitorFocusKey(route.query.focusKey)
getList()
</script>

<style scoped lang="scss">
.ygb-workbench-alert__title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.ygb-workbench-alert__desc {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 10px;
  color: #5f6f80;
  line-height: 1.7;
}

.ygb-workbench-alert__desc strong {
  color: #15304b;
}

.ygb-summary-grid,
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
  background: linear-gradient(180deg, #ffffff 0%, #f2f7ff 100%);
}

.ygb-card-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
}

.ygb-card-head--between {
  justify-content: space-between;
}

.ygb-card-head__title {
  color: #15304b;
  font-size: 16px;
  font-weight: 700;
}

.ygb-card-head__desc {
  margin-top: 6px;
  color: #627486;
  line-height: 1.7;
  font-size: 13px;
}

.ygb-focus-list,
.ygb-source-list {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.ygb-focus-list--single {
  grid-template-columns: minmax(0, 1fr);
}

.ygb-source-item {
  padding: 14px 16px;
  border-radius: 12px;
  background: #f6f9fc;
  border: 1px solid #e2eaf2;
}

.ygb-source-item__label {
  color: #708397;
  font-size: 12px;
}

.ygb-source-item__value {
  margin-top: 8px;
  color: #17324d;
  font-size: 15px;
  font-weight: 600;
  line-height: 1.6;
}

.ygb-focus-queue {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  width: 100%;
  padding: 16px;
  border-radius: 12px;
  border: 1px solid #e2eaf2;
  background: #f6f9fc;
  text-align: left;
  cursor: pointer;
  transition: all 0.2s ease;
}

.ygb-focus-queue:hover,
.ygb-focus-queue.is-active {
  border-color: #1f5aa6;
  background: #eef5ff;
}

.ygb-focus-queue__main strong {
  color: #17324d;
  font-size: 15px;
}

.ygb-focus-queue__main p {
  margin: 6px 0 0;
  color: #627486;
  line-height: 1.7;
  font-size: 13px;
}

.ygb-focus-queue__side {
  display: flex;
  min-width: 120px;
  flex-direction: column;
  align-items: flex-end;
  gap: 6px;
}

.ygb-focus-queue__count {
  color: #15304b;
  font-size: 20px;
  font-weight: 700;
}

.ygb-focus-queue__action {
  color: #1f5aa6;
  font-size: 12px;
}

.ygb-pipeline-list {
  display: grid;
  gap: 12px;
}

.ygb-pipeline-item {
  display: flex;
  gap: 14px;
  padding: 14px 16px;
  border-radius: 12px;
  background: #f7f9fc;
  border: 1px solid #e0e8f0;
}

.ygb-pipeline-item__index {
  width: 34px;
  height: 34px;
  line-height: 34px;
  border-radius: 10px;
  background: #1f5aa6;
  color: #fff;
  text-align: center;
  font-size: 13px;
  font-weight: 700;
  flex-shrink: 0;
}

.ygb-pipeline-item__body strong {
  color: #183552;
  font-size: 14px;
}

.ygb-pipeline-item__body p {
  margin: 6px 0 0;
  color: #627486;
  line-height: 1.7;
  font-size: 13px;
}

.ygb-tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.ygb-recommend-panel {
  margin-top: 16px;
  padding: 14px 16px;
  border-radius: 12px;
  background: #f6f9fc;
  border: 1px solid #e2eaf2;
}

.ygb-recommend-panel__title {
  color: #15304b;
  font-weight: 700;
}

.ygb-recommend-panel__summary {
  margin: 8px 0 0;
  color: #627486;
  line-height: 1.7;
  font-size: 13px;
}

.ygb-focus-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 16px;
}

.detail-block {
  margin-top: 20px;
}

.detail-block h3 {
  margin: 0 0 12px;
  color: #15304b;
  font-size: 15px;
}

@media (max-width: 1200px) {
  .ygb-summary-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 768px) {
  .ygb-summary-grid,
  .ygb-focus-grid,
  .ygb-source-list {
    grid-template-columns: minmax(0, 1fr);
  }

  .ygb-focus-queue {
    flex-direction: column;
    align-items: flex-start;
  }

  .ygb-focus-queue__side {
    min-width: 0;
    align-items: flex-start;
  }
}
</style>
