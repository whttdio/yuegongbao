import { getCurrentInstance, reactive, ref, toRefs } from 'vue'
import { generateStatReport, getStatReport, getStatReportSummary, listStatReport } from '@/api/ygb/statReport'
import { authorizedDefaultRegionCode } from '@/utils/regionScope'
import { gdRegionNameMap, gdRegionOptions } from '@/utils/regionName'

export const statReportRegionOptions = gdRegionOptions

export const statReportRegionNameMap = gdRegionNameMap

export const statReportTypeOptions = [
  { label: '工伤发生率月报', value: 'INJURY_RATE' },
  { label: '预警治理月报', value: 'WARNING_OVERVIEW' },
  { label: '工资发放月报', value: 'SALARY_PAYMENT' },
  { label: '社保税务联动月报', value: 'SOCIAL_TAX' },
  { label: '用工月报', value: 'EMPLOYMENT' },
  { label: '考勤月报', value: 'ATTENDANCE' },
  { label: '社保月报', value: 'SOCIAL' },
  { label: '税务月报', value: 'TAX' },
  { label: '安责险月报', value: 'AQ_INSURANCE' },
  { label: '新业态月报', value: 'NEWFORM' },
  { label: '职业病月报', value: 'OCCUPATION' },
  { label: '工会监督月报', value: 'UNION_SUPERVISION' },
  { label: '专项整治月报', value: 'SPECIAL_RECTIFICATION' },
  { label: '自定义月报', value: 'CUSTOM' }
]

export const statReportItemCategoryOptions = [
  ...statReportTypeOptions,
  { label: '设备月报', value: 'DEVICE_STATS' },
  { label: '扩面减损月报', value: 'EXPANSION_REDUCTION' },
  { label: '工资发放月报', value: 'SALARY' }
]

export const statReportItemDimensionOptions = [
  { label: '社保基数异常', value: 'SOCIAL_ABNORMAL' },
  { label: '个税比对异常', value: 'TAX_ABNORMAL' },
  { label: '漏保清单', value: 'UNINSURED' },
  { label: '用工比例黄警', value: 'EMPLOYMENT_YELLOW' },
  { label: '用工比例红警', value: 'EMPLOYMENT_RED' },
  { label: '假外包疑似', value: 'FAKE_OUTSOURCING' },
  { label: '安责险理赔监控', value: 'AQ_CLAIM' },
  { label: '新业态职业伤害', value: 'NEWFORM_INJURY' },
  { label: '职业病预警', value: 'OCCUPATION_WARNING' },
  { label: '工会投诉协同', value: 'UNION_CASE' },
  { label: '社保监管', value: 'SOCIAL' },
  { label: '税务监管', value: 'TAX' },
  { label: '扩面减损', value: 'EXPANSION' },
  { label: '专项治理', value: 'SPECIAL' },
  { label: '设备预警', value: 'DEVICE' },
  { label: '工伤监管', value: 'INJURY' },
  { label: '安责险', value: 'AQINS' },
  { label: '新业态', value: 'NEWFORM' },
  { label: '职业病', value: 'OCCUPATION' },
  { label: '工会监督', value: 'UNION' }
]

export const statReportStatusOptions = [
  { label: '草稿', value: '0' },
  { label: '已生成', value: '1' },
  { label: '已归档', value: '2' }
]

export function currentMonth() {
  const date = new Date()
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}`
}

export function valueOrDefault(value, fallback = 0) {
  return value ?? fallback
}

export function statusLabel(value, options = statReportStatusOptions) {
  const matched = options.find(item => item.value === value)
  return matched ? matched.label : '-'
}

function optionLabel(value, options) {
  const matched = options.find(item => String(item.value) === String(value))
  return matched ? matched.label : ''
}

export function formatStatReportItemCategory(value) {
  return optionLabel(value, statReportItemCategoryOptions) || value || '-'
}

export function formatStatReportItemDimension(value, row = {}) {
  if (statReportRegionNameMap[value]) {
    return statReportRegionNameMap[value]
  }
  const label = optionLabel(value, statReportItemDimensionOptions)
  if (label) {
    return label
  }
  if (/^\d+$/.test(String(value || '')) && row.itemName) {
    return row.itemName
  }
  return value || row.itemName || '-'
}

function normalizeMetricRate(value, reportCode) {
  const safeValue = Number(value ?? 0)
  if (reportCode === 'INJURY_RATE' && safeValue > 100) {
    return Number((safeValue / 10).toFixed(2))
  }
  return value ?? 0
}

export function formatMetricRate(value, reportCode) {
  const safeValue = normalizeMetricRate(value, reportCode)
  const suffix = '%'
  return `${safeValue}${suffix}`
}

function buildBaseQueryParams(defaultRegionCode, fixedReportCode) {
  return {
    pageNum: 1,
    pageSize: 10,
    regionCode: defaultRegionCode,
    statMonth: currentMonth(),
    reportCode: fixedReportCode || undefined,
    reportStatus: undefined,
    keyword: undefined,
    itemDimension: undefined,
    sourceModule: undefined,
    minMetricCount: undefined,
    maxMetricCount: undefined,
    minMetricRate: undefined,
    maxMetricRate: undefined,
    riskCategory: undefined,
    batchNo: undefined
  }
}

function createDefaultQueryParams(defaultRegionCode, fixedReportCode, defaultExtraQueryParams = {}) {
  return {
    ...buildBaseQueryParams(defaultRegionCode, fixedReportCode),
    ...(defaultExtraQueryParams || {}),
    reportCode: fixedReportCode || defaultExtraQueryParams?.reportCode || undefined
  }
}

function createDefaultGenerateForm(defaultRegionCode, reportCode) {
  return {
    regionCode: defaultRegionCode,
    statMonth: currentMonth(),
    reportCode
  }
}

function normalizeParams(params = {}) {
  return Object.fromEntries(
    Object.entries(params).filter(([, value]) => value !== undefined && value !== null && value !== '')
  )
}

export function useStatReportPage(options = {}) {
  const {
    exportFilePrefix = 'stat_report',
    defaultRegionCode = '440000',
    defaultReportCode = 'SALARY_PAYMENT',
    fixedReportCode = undefined,
    defaultExtraQueryParams = {},
    canMutate = () => true,
    onBlockedAction,
    getCurrentList,
    afterList,
    immediate = true
  } = options

  const { proxy } = getCurrentInstance()
  const reportCode = fixedReportCode || defaultReportCode
  const authorizedRegionCode = authorizedDefaultRegionCode(defaultRegionCode)

  const loading = ref(false)
  const showSearch = ref(true)
  const total = ref(0)
  const reportList = ref([])
  const currentReport = ref(undefined)
  const reportDetail = ref(undefined)
  const reportItems = ref([])
  const summaryData = ref({})
  const detailOpen = ref(false)
  const generateOpen = ref(false)

  const data = reactive({
    queryParams: createDefaultQueryParams(authorizedRegionCode, fixedReportCode, defaultExtraQueryParams),
    generateForm: createDefaultGenerateForm(authorizedRegionCode, reportCode),
    generateRules: {
      regionCode: [{ required: true, message: '请选择区域', trigger: 'change' }],
      statMonth: [{ required: true, message: '请选择统计月份', trigger: 'change' }],
      reportCode: [{ required: true, message: '请选择月报类型', trigger: 'change' }]
    }
  })

  const { queryParams, generateForm, generateRules } = toRefs(data)

  function resolveCurrentList() {
    if (typeof getCurrentList === 'function') {
      const list = getCurrentList()
      if (Array.isArray(list)) {
        return list
      }
    }
    return reportList.value
  }

  function guardMutation(actionLabel) {
    if (canMutate()) {
      return true
    }
    if (typeof onBlockedAction === 'function') {
      onBlockedAction(actionLabel)
    } else {
      proxy?.$modal?.msgWarning?.(`当前角色仅支持查询、导出和打印，不能${actionLabel}`)
    }
    return false
  }

  function buildRequestQuery() {
    return normalizeParams({
      ...queryParams.value,
      reportCode: fixedReportCode || queryParams.value.reportCode
    })
  }

  function buildSummaryQuery() {
    const { pageNum, pageSize, ...rest } = queryParams.value
    return normalizeParams({
      ...rest,
      reportCode: fixedReportCode || queryParams.value.reportCode
    })
  }

  function syncCurrentReport() {
    const currentList = resolveCurrentList()
    if (currentReport.value) {
      const matched = currentList.find(item => item.reportId === currentReport.value.reportId)
      if (matched) {
        currentReport.value = matched
        return
      }
    }
    currentReport.value = currentList.length > 0 ? currentList[0] : undefined
  }

  function getList() {
    loading.value = true
    const listQuery = buildRequestQuery()
    const summaryQuery = buildSummaryQuery()
    return listStatReport(listQuery)
      .then(listResponse => {
        reportList.value = listResponse.rows || []
        total.value = listResponse.total || 0
        return getStatReportSummary(summaryQuery).catch(() => ({ data: {} }))
      })
      .then(summaryResponse => {
        summaryData.value = summaryResponse?.data || {}
        if (typeof afterList === 'function') {
          afterList({
            reportList: reportList.value,
            summaryData: summaryData.value
          })
        }
        syncCurrentReport()
      })
      .finally(() => {
        loading.value = false
      })
  }

  function handleRowClick(row) {
    currentReport.value = row
  }

  function handleQuery() {
    queryParams.value.pageNum = 1
    if (fixedReportCode) {
      queryParams.value.reportCode = fixedReportCode
    }
    getList()
  }

  function resetQuery() {
    proxy?.resetForm?.('queryRef')
    queryParams.value = createDefaultQueryParams(authorizedRegionCode, fixedReportCode, defaultExtraQueryParams)
    getList()
  }

  function handleExport() {
    proxy.download('ygb/report/export', buildSummaryQuery(), `${exportFilePrefix}_${Date.now()}.xlsx`)
  }

  function openGenerateDialog(payload) {
    if (!guardMutation('生成月报')) {
      return
    }
    const nextReportCode = fixedReportCode || payload?.reportCode || queryParams.value.reportCode || reportCode
    generateForm.value = {
      regionCode: payload?.regionCode || queryParams.value.regionCode || defaultRegionCode,
      statMonth: payload?.statMonth || queryParams.value.statMonth || currentMonth(),
      reportCode: nextReportCode
    }
    generateOpen.value = true
  }

  function submitGenerate() {
    if (!guardMutation('生成月报')) {
      return
    }
    if (fixedReportCode) {
      generateForm.value.reportCode = fixedReportCode
    }
    proxy.$refs.generateRef.validate(valid => {
      if (!valid) {
        return
      }
      generateStatReport(normalizeParams(generateForm.value)).then(response => {
        proxy.$modal.msgSuccess(response.msg || '统计月报生成完成')
        generateOpen.value = false
        getList()
      })
    })
  }

  function openDetail(row) {
    if (!row?.reportId) {
      return Promise.resolve()
    }
    currentReport.value = row
    return getStatReport(row.reportId).then(response => {
      reportDetail.value = response.data || {}
      reportItems.value = response.items || []
      detailOpen.value = true
    })
  }

  if (immediate) {
    getList()
  }

  return {
    loading,
    showSearch,
    total,
    reportList,
    currentReport,
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
    resetQuery,
    handleExport,
    openGenerateDialog,
    submitGenerate,
    openDetail,
    syncCurrentReport
  }
}
