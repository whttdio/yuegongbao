import { computed, getCurrentInstance, reactive, ref } from 'vue'
import { generateAiReport, getAiReport, getAiReportDashboard, listAiReport } from '@/api/ygb/aiReport'
import { getCurrentAiReportConfig } from '@/api/ygb/aiReportConfig'
import { authorizedDefaultRegionCode } from '@/utils/regionScope'
import { gdRegionNameMap, gdRegionOptions } from '@/utils/regionName'

export const regionOptions = gdRegionOptions

export const regionNameMap = gdRegionNameMap

export const reportTypeOptions = [
  { label: '日报', value: 'DAILY' },
  { label: '周报', value: 'WEEKLY' },
  { label: '月报', value: 'MONTHLY' },
  { label: '年报', value: 'YEARLY' }
]

export const riskLevelOptions = [
  { label: '低风险', value: 'LOW' },
  { label: '中风险', value: 'MEDIUM' },
  { label: '高风险', value: 'HIGH' }
]

export const enterpriseTypeOptions = [
  { label: '全部企业', value: 'ALL' },
  { label: '派遣企业', value: 'DISPATCH' },
  { label: '用工企业', value: 'EMPLOYER' }
]

export const dimensionOptions = [
  { label: 'A 合同备案合规', value: 'A' },
  { label: 'B 考勤归集合规', value: 'B' },
  { label: 'C 工资发放合规', value: 'C' },
  { label: 'D 设备工伤安全', value: 'D' },
  { label: 'E 预警闭环治理', value: 'E' }
]

export function reportTypeLabel(value) {
  return reportTypeOptions.find(item => item.value === value)?.label || value || '-'
}

export function riskLevelLabel(value) {
  return riskLevelOptions.find(item => item.value === value)?.label || value || '-'
}

export function enterpriseTypeLabel(value) {
  return enterpriseTypeOptions.find(item => item.value === value)?.label || value || '-'
}

export function riskTagType(value) {
  if (value === 'LOW') return 'success'
  if (value === 'MEDIUM') return 'warning'
  if (value === 'HIGH') return 'danger'
  return 'info'
}

export function formatDecimal(value) {
  const number = Number(value || 0)
  return Number.isNaN(number) ? '0.00' : number.toFixed(2)
}

export function formatNativeDate(date) {
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

export function currentMonthRange() {
  const now = new Date()
  const first = new Date(now.getFullYear(), now.getMonth(), 1)
  const last = new Date(now.getFullYear(), now.getMonth() + 1, 0)
  return [formatNativeDate(first), formatNativeDate(last)]
}

export function formatDate(value) {
  if (!value) return ''
  if (typeof value === 'string') return value.slice(0, 10)
  return formatNativeDate(new Date(value))
}

export function parseJson(text) {
  if (!text) return {}
  try {
    return JSON.parse(text)
  } catch (error) {
    return {}
  }
}

export function normalizeReportRow(row = {}) {
  return {
    ...row,
    regionName: row.regionName || regionNameMap[row.regionCode] || row.regionCode || '-'
  }
}

export function normalizeReportRows(rows = []) {
  return rows.map(item => normalizeReportRow(item))
}

export function reportDisplayName(row) {
  if (!row) return '-'
  return `${row.regionName || regionNameMap[row.regionCode] || row.regionCode || '-'} · ${reportTypeLabel(row.reportType)}`
}

export function buildReportSummary(row) {
  if (!row) {
    return '当前暂无可用报告样本。'
  }
  if (row.reportSummary) return row.reportSummary
  if (row.highlight) return row.highlight
  if (row.riskReason) return row.riskReason
  return `综合得分 ${row.totalScore ?? '-'}，风险等级 ${riskLevelLabel(row.riskLevel)}。`
}

export function summaryCard(key, label, value, unit, note, cardClass) {
  return { key, label, value, unit, note, cardClass }
}

export function focusQueue(key, title, count, unit, desc, actionText) {
  return { key, title, count, unit, desc, actionText }
}

export function prioritizeFocusRows(rows = [], predicate = () => false) {
  const matched = []
  const others = []
  rows.forEach(row => {
    if (predicate(row)) {
      matched.push(row)
      return
    }
    others.push(row)
  })
  return [...matched, ...others]
}

export function matchReportFocus(row, focusKey, averageScore = 0) {
  if (!row || !focusKey) return false
  if (focusKey === 'highRisk') {
    return String(row.riskLevel || '') === 'HIGH'
  }
  if (focusKey === 'lowScore') {
    return Number(row.totalScore || 0) < Number(averageScore || 0)
  }
  if (focusKey === 'total') {
    return true
  }
  return false
}

export function buildBaseAiReportHintTags(report, options = {}) {
  const {
    items = [],
    focus,
    detailMode = false,
    averageScore = 0,
    highRiskCount = 0,
    currentConfigVersion = 'DEFAULT-V1'
  } = options

  if (!report) {
    if (focus?.title) {
      return [
        { label: `当前焦点：${focus.title}`, type: 'info' },
        { label: `优先动作：${focus.actionText}`, type: 'warning' }
      ]
    }
    return [{ label: '当前暂无 AI 报告样本，请先筛选或生成报告。', type: 'info' }]
  }

  const tags = []
  const score = Number(report.totalScore || 0)

  if (String(report.riskLevel || '') === 'HIGH' || score < 70) {
    tags.push({ label: '当前报告属于高风险对象，建议优先纳入监管或专项治理链路。', type: 'danger' })
  } else if (String(report.riskLevel || '') === 'MEDIUM' || score < 85) {
    tags.push({ label: '当前报告属于中风险对象，建议复核薄弱维度和近期波动。', type: 'warning' })
  } else {
    tags.push({ label: '当前报告整体稳定，可作为同口径样本的正向对标。', type: 'success' })
  }

  if (Number(averageScore || 0) > 0 && score < Number(averageScore || 0)) {
    tags.push({ label: '当前样本低于本期平均分，适合优先回看业务台账和整改动作。', type: 'warning' })
  }

  if (items.length) {
    tags.push({ label: `已加载 ${items.length} 个维度明细，可直接复核指标与建议动作。`, type: 'info' })
  } else if (!detailMode) {
    tags.push({ label: '当前尚未打开维度明细，建议进入详情复核评分依据。', type: 'info' })
  }

  if (!detailMode && Number(highRiskCount || 0) > 0) {
    tags.push({ label: `同口径下仍有 ${highRiskCount} 个高风险对象待继续跟进。`, type: 'warning' })
  }

  if (!report.reportPdfUrl) {
    tags.push({ label: '当前报告未生成单独下载地址，可通过页面预览和导出列表完成复核归档。', type: 'info' })
  }

  tags.push({
    label: `模型版本 ${report.configVersion || currentConfigVersion || 'DEFAULT-V1'} 已加载，请保持解释口径一致。`,
    type: 'info'
  })

  if (detailMode && !report.reportHtml) {
    tags.push({ label: '当前详情未返回报告 HTML 片段，请以维度明细和导出结果完成复核。', type: 'warning' })
  }

  return tags.slice(0, 5)
}

function createDashboard() {
  return {
    totalCount: 0,
    averageScore: 0,
    highRiskCount: 0,
    activeReport: null,
    activeItems: [],
    scoreCards: [],
    conclusion: {
      summary: '',
      strength: '',
      weakness: '',
      advice: ''
    },
    trendPoints: [],
    topRankingList: [],
    bottomRankingList: [],
    highRiskList: []
  }
}

export function useAiReportPage(options = {}) {
  const {
    exportFilePrefix = 'ai_report',
    canMutate = () => true,
    onBlockedAction,
    afterLoad
  } = options
  const { proxy } = getCurrentInstance()
  const defaultRegionCode = authorizedDefaultRegionCode('440000')

  const loading = ref(false)
  const showSearch = ref(true)
  const total = ref(0)
  const reportList = ref([])
  const reportDetail = ref(null)
  const reportItems = ref([])
  const detailOpen = ref(false)
  const generateOpen = ref(false)
  const queryRange = ref(currentMonthRange())
  const generateRange = ref(currentMonthRange())
  const activeReportId = ref(undefined)
  const currentConfig = ref({
    version: 'DEFAULT-V1',
    dimensionWeights: '{}',
    targetValues: '{}'
  })

  const dashboard = reactive(createDashboard())

  const queryParams = reactive({
    pageNum: 1,
    pageSize: 10,
    regionCode: defaultRegionCode,
    reportType: 'MONTHLY',
    enterpriseType: '',
    riskLevel: undefined
  })

  const generateForm = reactive({
    regionCode: defaultRegionCode,
    reportType: 'MONTHLY',
    enterpriseType: 'ALL',
    dimensions: ['A', 'B', 'C', 'D', 'E']
  })

  const generateRules = {
    regionCode: [{ required: true, message: '请选择区域', trigger: 'change' }],
    reportType: [{ required: true, message: '请选择报告类型', trigger: 'change' }],
    enterpriseType: [{ required: true, message: '请选择企业类型', trigger: 'change' }],
    dimensions: [{ required: true, message: '请选择评分维度', trigger: 'change' }]
  }

  const selectedDimensionItems = computed(() => {
    return (dashboard.activeItems || []).map(item => ({
      ...item,
      detail: parseJson(item.detailJson)
    }))
  })

  const detailDimensionItems = computed(() => {
    return (reportItems.value || []).map(item => ({
      ...item,
      detail: parseJson(item.detailJson)
    }))
  })

  const detailDisplayReport = computed(() => {
    if (!reportDetail.value) return null
    if (dashboard.activeReport?.reportId === reportDetail.value.reportId) {
      return {
        ...reportDetail.value,
        ...dashboard.activeReport
      }
    }
    return reportDetail.value
  })

  const defaultSelectedReport = computed(() => {
    return reportList.value.find(item => item.reportId === activeReportId.value)
      || dashboard.activeReport
      || detailDisplayReport.value
      || reportList.value[0]
      || null
  })

  function blockMutation(actionLabel) {
    if (canMutate()) {
      return false
    }
    if (typeof onBlockedAction === 'function') {
      onBlockedAction(actionLabel)
    }
    return true
  }

  function buildListQuery() {
    return {
      ...queryParams,
      periodStart: queryRange.value?.[0],
      periodEnd: queryRange.value?.[1]
    }
  }

  function syncActiveReportToList() {
    if (!dashboard.activeReport?.reportId) return
    reportList.value = reportList.value.map(item => {
      if (item.reportId !== dashboard.activeReport.reportId) {
        return item
      }
      return normalizeReportRow({
        ...item,
        ...dashboard.activeReport
      })
    })
  }

  function syncDetailReport() {
    if (!reportDetail.value || dashboard.activeReport?.reportId !== reportDetail.value.reportId) {
      return
    }
    reportDetail.value = normalizeReportRow({
      ...reportDetail.value,
      ...dashboard.activeReport
    })
  }

  async function loadDashboard(preferredId) {
    const response = await getAiReportDashboard({
      ...buildListQuery(),
      activeReportId: preferredId || activeReportId.value || reportList.value[0]?.reportId
    })
    const data = response.data || {}
    Object.assign(dashboard, createDashboard(), {
      ...data,
      activeReport: data.activeReport ? normalizeReportRow(data.activeReport) : null,
      topRankingList: normalizeReportRows(data.topRankingList || []),
      bottomRankingList: normalizeReportRows(data.bottomRankingList || []),
      highRiskList: normalizeReportRows(data.highRiskList || [])
    })
    activeReportId.value = dashboard.activeReport?.reportId || reportList.value[0]?.reportId
    syncActiveReportToList()
    syncDetailReport()
  }

  async function loadCurrentConfig(regionCode = queryParams.regionCode, range = queryRange.value) {
    try {
      const response = await getCurrentAiReportConfig({
        regionCode: regionCode || defaultRegionCode,
        effectiveDate: range?.[1] || currentMonthRange()[1]
      })
      currentConfig.value = response.data || {
        version: 'DEFAULT-V1',
        dimensionWeights: '{}',
        targetValues: '{}'
      }
    } catch (error) {
      currentConfig.value = {
        version: 'DEFAULT-V1',
        dimensionWeights: '{}',
        targetValues: '{}'
      }
    }
  }

  async function getList(preferredId) {
    loading.value = true
    try {
      const response = await listAiReport(buildListQuery())
      reportList.value = normalizeReportRows(response.rows || [])
      total.value = response.total || 0
      await Promise.all([
        loadDashboard(preferredId),
        loadCurrentConfig()
      ])
      if (typeof afterLoad === 'function') {
        await afterLoad()
      }
    } finally {
      loading.value = false
    }
  }

  async function loadReportDetail(reportId, openDrawer = false) {
    if (!reportId) return
    const response = await getAiReport(reportId)
    reportDetail.value = normalizeReportRow(response.data || {})
    reportItems.value = response.items || []
    activeReportId.value = reportId
    syncDetailReport()
    if (openDrawer) {
      detailOpen.value = true
    }
  }

  function handleQuery() {
    queryParams.pageNum = 1
    activeReportId.value = undefined
    getList()
  }

  function resetQuery() {
    proxy.resetForm('queryRef')
    queryParams.pageNum = 1
    queryParams.pageSize = 10
    queryParams.regionCode = defaultRegionCode
    queryParams.reportType = 'MONTHLY'
    queryParams.enterpriseType = ''
    queryParams.riskLevel = undefined
    queryRange.value = currentMonthRange()
    activeReportId.value = undefined
    getList()
  }

  function openGenerateDialog(row) {
    if (blockMutation('执行 AI 报告生成')) {
      return
    }
    if (row) {
      generateForm.regionCode = row.regionCode || defaultRegionCode
      generateForm.reportType = row.reportType || 'MONTHLY'
      generateForm.enterpriseType = row.enterpriseType || 'ALL'
      generateRange.value = [formatDate(row.periodStart), formatDate(row.periodEnd)]
    } else {
      generateForm.regionCode = queryParams.regionCode || defaultRegionCode
      generateForm.reportType = queryParams.reportType || 'MONTHLY'
      generateForm.enterpriseType = queryParams.enterpriseType || 'ALL'
      generateRange.value = [...queryRange.value]
    }
    generateForm.dimensions = ['A', 'B', 'C', 'D', 'E']
    loadCurrentConfig(generateForm.regionCode, generateRange.value)
    generateOpen.value = true
  }

  function submitGenerate() {
    if (blockMutation('执行 AI 报告生成')) {
      return
    }
    proxy.$refs.generateRef.validate(async valid => {
      if (!valid) return
      const response = await generateAiReport({
        regionCode: generateForm.regionCode,
        reportType: generateForm.reportType,
        enterpriseType: generateForm.enterpriseType,
        startDate: generateRange.value?.[0],
        endDate: generateRange.value?.[1],
        dimensions: generateForm.dimensions
      })
      proxy.$modal.msgSuccess(response.msg || 'AI 监测报告生成完成')
      generateOpen.value = false
      await getList()
    })
  }

  async function openDetail(row) {
    if (!row?.reportId) return
    activeReportId.value = row.reportId
    await loadDashboard(row.reportId)
    await loadReportDetail(row.reportId, true)
  }

  function handleExport() {
    proxy.download('ygb/aiReport/export', buildListQuery(), `${exportFilePrefix}_${new Date().getTime()}.xlsx`)
  }

  return {
    loading,
    showSearch,
    total,
    reportList,
    reportDetail,
    reportItems,
    detailOpen,
    generateOpen,
    queryRange,
    generateRange,
    activeReportId,
    currentConfig,
    dashboard,
    queryParams,
    generateForm,
    generateRules,
    selectedDimensionItems,
    detailDimensionItems,
    detailDisplayReport,
    defaultSelectedReport,
    buildListQuery,
    getList,
    loadDashboard,
    loadCurrentConfig,
    loadReportDetail,
    handleQuery,
    resetQuery,
    openGenerateDialog,
    submitGenerate,
    openDetail,
    handleExport,
    blockMutation
  }
}
