import { computed, getCurrentInstance, reactive, ref, toRefs } from 'vue'
import { getWarning, getWarningAnalysis, getWarningSummary, handleWarning, listWarning } from '@/api/ygb/warning'
import { optionselectEnterprise } from '@/api/ygb/enterprise'
import useUserStore from '@/store/modules/user'
import { filterAuthorizedRegionOptions } from '@/utils/regionScope'
import { formatRegionName, gdRegionNameMap, gdRegionOptions, normalizeRegionCode } from '@/utils/regionName'

export const baseRegionOptions = gdRegionOptions

export const regionNameMap = gdRegionNameMap

export const warnLevelOptions = [
  { label: '提示', value: '1' },
  { label: '黄警', value: '2' },
  { label: '红警', value: '3' }
]

export const warnStatusOptions = [
  { label: '待处理', value: '0' },
  { label: '处理中', value: '1' },
  { label: '已办结', value: '2' },
  { label: '误报', value: '3' },
  { label: '已升级', value: '4' }
]

export const sourceModuleOptions = [
  { label: '社保联动', value: 'SOCIAL' },
  { label: '个税联动', value: 'TAX' },
  { label: '扩面联动', value: 'EXPANSION' },
  { label: '专项治理', value: 'SPECIAL' },
  { label: '设备联动', value: 'DEVICE' },
  { label: '工伤事件', value: 'INJURY' }
]

const sourceModuleAliasMap = {
  social: 'SOCIAL',
  tax: 'TAX',
  expansion: 'EXPANSION',
  special: 'SPECIAL',
  device: 'DEVICE',
  injury: 'INJURY'
}

export const actionOptions = [
  { label: '转处理中', value: 'PROCESS' },
  { label: '办结关闭', value: 'CLOSE' },
  { label: '标记误报', value: 'MISREPORT' },
  { label: '升级处置', value: 'UPGRADE' }
]

const warningActionStatusMap = {
  PROCESS: ['0'],
  CLOSE: ['0', '1', '4'],
  MISREPORT: ['0', '1'],
  UPGRADE: ['0', '1']
}

function createDefaultQueryParams() {
  return {
    pageNum: 1,
    pageSize: 10,
    regionCode: undefined,
    enterpriseId: undefined,
    warnLevel: undefined,
    sourceModule: undefined,
    warnStatus: undefined,
    content: undefined
  }
}

function createDefaultHandleForm() {
  return {
    action: undefined,
    opinion: undefined,
    attachmentUrls: undefined
  }
}

function createHandleRules() {
  return {
    action: [{ required: true, message: '处置动作不能为空', trigger: 'change' }],
    opinion: [{ required: true, message: '处置意见不能为空', trigger: 'blur' }]
  }
}

export function optionLabel(options, value, fallback = '-') {
  const matched = Array.isArray(options) ? options.find(item => item.value === value) : undefined
  return matched ? matched.label : fallback
}

export function normalizeSourceModule(value) {
  if (value === null || typeof value === 'undefined' || value === '') {
    return undefined
  }
  const rawValue = String(value)
  const normalizedValue = sourceModuleAliasMap[rawValue.toLowerCase()] || rawValue.toUpperCase()
  return sourceModuleOptions.some(item => item.value === normalizedValue) ? normalizedValue : rawValue
}

export function getWarningActionOptions(warning) {
  const warnStatus = String(warning?.warnStatus || '')
  return actionOptions.filter(item => warningActionStatusMap[item.value]?.includes(warnStatus))
}

export function canHandleWarning(warning) {
  return getWarningActionOptions(warning).length > 0
}

export { formatRegionName } from '@/utils/regionName'

export function summaryCard(key, label, value, unit, note, cardClass) {
  return { key, label, value: value ?? 0, unit, note, cardClass }
}

export function focusQueue(key, title, count, unit, desc, actionText) {
  return { key, title, count: count ?? 0, unit, desc, actionText }
}

export function matchWarningFocus(row, focusKey) {
  if (!row || !focusKey) {
    return false
  }
  if (focusKey === 'red') {
    return String(row.warnLevel || '') === '3'
  }
  if (focusKey === 'pending') {
    return String(row.warnStatus || '') === '0'
  }
  if (focusKey === 'device') {
    return String(row.sourceModule || '') === 'DEVICE'
  }
  if (focusKey === 'upgrade') {
    return String(row.warnStatus || '') === '4'
  }
  if (focusKey === 'injury') {
    return String(row.sourceModule || '') === 'INJURY'
  }
  if (focusKey === 'closed') {
    return ['2', '3'].includes(String(row.warnStatus || ''))
  }
  return false
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

export function useWarningPage(options = {}) {
  const {
    exportFilePrefix = 'ygb_warning',
    canHandle = () => true,
    onBlockedAction,
    getCurrentList,
    afterList,
    immediate = true
  } = options

  const { proxy } = getCurrentInstance()
  const userStore = useUserStore()

  const warningList = ref([])
  const enterpriseOptions = ref([])
  const warningDetail = ref(undefined)
  const warningLogs = ref([])
  const warningSummary = ref({})
  const warningAnalysis = ref({})
  const currentWarning = ref(undefined)
  const loading = ref(false)
  const showSearch = ref(true)
  const total = ref(0)
  const detailOpen = ref(false)
  const handleOpen = ref(false)
  const currentWarnId = ref(undefined)

  const data = reactive({
    queryParams: createDefaultQueryParams(),
    handleForm: createDefaultHandleForm(),
    handleRules: createHandleRules()
  })

  const { queryParams, handleForm, handleRules } = toRefs(data)

  const regionOptions = computed(() => {
    const optionMap = new Map(baseRegionOptions.map(item => [item.value, item]))
    enterpriseOptions.value.forEach(item => {
      const regionCode = normalizeRegionCode(item.regionCode)
      if (regionCode && !optionMap.has(regionCode)) {
        optionMap.set(regionCode, { value: regionCode, label: formatRegionName(regionCode, regionCode) })
      }
    })
    warningList.value.forEach(item => {
      const regionCode = normalizeRegionCode(item.regionCode)
      if (regionCode && !optionMap.has(regionCode)) {
        optionMap.set(regionCode, { value: regionCode, label: formatRegionName(regionCode, regionCode) })
      }
    })
    return filterAuthorizedRegionOptions(Array.from(optionMap.values()), userStore.allowedRegionCodes)
  })

  function resolveCurrentList() {
    if (typeof getCurrentList === 'function') {
      const list = getCurrentList()
      if (Array.isArray(list)) {
        return list
      }
    }
    return warningList.value
  }

  function guardHandle(actionLabel) {
    if (canHandle()) {
      return true
    }
    if (typeof onBlockedAction === 'function') {
      onBlockedAction(actionLabel)
    }
    return false
  }

  function buildSummaryQuery() {
    normalizeQueryParams()
    return {
      regionCode: queryParams.value.regionCode,
      enterpriseId: queryParams.value.enterpriseId,
      warnLevel: queryParams.value.warnLevel,
      sourceModule: queryParams.value.sourceModule,
      warnStatus: queryParams.value.warnStatus,
      content: queryParams.value.content
    }
  }

  function normalizeQueryParams() {
    queryParams.value.sourceModule = normalizeSourceModule(queryParams.value.sourceModule)
  }

  function syncCurrentWarning() {
    const currentList = resolveCurrentList()
    if (currentWarning.value) {
      const matched = currentList.find(item => item.warnId === currentWarning.value.warnId)
      if (matched) {
        currentWarning.value = matched
        return
      }
    }
    currentWarning.value = currentList.length > 0 ? currentList[0] : undefined
  }

  function loadAll() {
    normalizeQueryParams()
    loading.value = true
    Promise.all([
      listWarning(queryParams.value),
      getWarningSummary(buildSummaryQuery()),
      getWarningAnalysis(buildSummaryQuery())
    ]).then(([listResponse, summaryResponse, analysisResponse]) => {
      warningList.value = listResponse.rows || []
      total.value = listResponse.total || 0
      warningSummary.value = summaryResponse.data || {}
      warningAnalysis.value = analysisResponse.data || {}
      if (typeof afterList === 'function') {
        afterList()
      }
      syncCurrentWarning()
    }).finally(() => {
      loading.value = false
    })
  }

  function loadEnterpriseOptions() {
    optionselectEnterprise().then(response => {
      enterpriseOptions.value = response.data || []
    })
  }

  function loadWarningDetail(warnId, showDrawer = false) {
    getWarning(warnId).then(response => {
      warningDetail.value = response.data || {}
      warningLogs.value = response.logs || []
      currentWarnId.value = warnId
      currentWarning.value = response.data || currentWarning.value
      if (showDrawer) {
        detailOpen.value = true
      }
    })
  }

  function handleQuery() {
    queryParams.value.pageNum = 1
    loadAll()
  }

  function resetQuery() {
    proxy.resetForm('queryRef')
    Object.assign(queryParams.value, createDefaultQueryParams())
    loadAll()
  }

  function handleExport() {
    proxy.download('ygb/warning/export', { ...buildSummaryQuery() }, `${exportFilePrefix}_${Date.now()}.xlsx`)
  }

  function handleRowClick(row) {
    if (!row?.warnId) {
      return
    }
    currentWarnId.value = row.warnId
    currentWarning.value = row
  }

  function openDetail(row) {
    const target = row || currentWarning.value
    if (!target?.warnId) {
      return
    }
    handleRowClick(target)
    loadWarningDetail(target.warnId, true)
  }

  function openHandleDialog(row) {
    const target = row || currentWarning.value
    if (!target?.warnId) {
      return
    }
    if (!guardHandle('处置预警')) {
      return
    }
    handleRowClick(target)
    const availableActions = getWarningActionOptions(target)
    if (!availableActions.length) {
      proxy.$modal.msgWarning('当前预警状态不允许继续处置')
      return
    }
    handleForm.value = {
      action: availableActions[0].value,
      opinion: undefined,
      attachmentUrls: undefined
    }
    handleOpen.value = true
  }

  function submitHandle() {
    if (!guardHandle('提交处置')) {
      return
    }
    proxy.$refs.handleRef.validate(valid => {
      if (!valid) {
        return
      }
      if (!canHandleWarning(currentWarning.value)) {
        proxy.$modal.msgWarning('当前预警状态不允许继续处置')
        return
      }
      const availableActions = getWarningActionOptions(currentWarning.value).map(item => item.value)
      if (!availableActions.includes(handleForm.value.action)) {
        proxy.$modal.msgWarning('当前预警状态不支持该处置动作')
        return
      }
      handleWarning(currentWarnId.value, handleForm.value).then(response => {
        proxy.$modal.msgSuccess(response.msg || '预警处置完成')
        handleOpen.value = false
        loadAll()
        if (detailOpen.value && currentWarnId.value) {
          loadWarningDetail(currentWarnId.value, false)
        }
      })
    })
  }

  if (immediate) {
    loadEnterpriseOptions()
    loadAll()
  }

  return {
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
    currentWarnId,
    queryParams,
    handleForm,
    handleRules,
    regionOptions,
    getWarningActionOptions,
    canHandleWarning,
    buildSummaryQuery,
    loadAll,
    loadEnterpriseOptions,
    loadWarningDetail,
    handleQuery,
    resetQuery,
    handleExport,
    handleRowClick,
    openDetail,
    openHandleDialog,
    submitHandle
  }
}
