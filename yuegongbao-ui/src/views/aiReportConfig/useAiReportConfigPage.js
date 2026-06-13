import { getCurrentInstance, reactive, ref, toRefs } from 'vue'
import {
  activateAiReportConfig,
  addAiReportConfig,
  getAiReportConfig,
  getAiReportConfigSummary,
  listAiReportConfig,
  updateAiReportConfig
} from '@/api/ygb/aiReportConfig'
import { authorizedDefaultRegionCode, useAuthorizedRegionOptions } from '@/utils/regionScope'

export const regionOptions = [
  { label: '广东省', value: '440000' },
  { label: '广州市天河区', value: '440106' },
  { label: '深圳市南山区', value: '440305' },
  { label: '佛山市顺德区', value: '440606' }
]

export const regionNameMap = {
  '440000': '广东省',
  '440106': '广州市天河区',
  '440305': '深圳市南山区',
  '440606': '佛山市顺德区'
}

export const statusOptions = [
  { label: '停用', value: '0' },
  { label: '启用', value: '1' }
]

function createDefaultQueryParams() {
  return {
    pageNum: 1,
    pageSize: 10,
    regionCode: undefined,
    configStatus: undefined
  }
}

function createDefaultForm(defaultRegionCode = '440000') {
  return {
    configId: undefined,
    regionCode: defaultRegionCode,
    version: '',
    configStatus: '1',
    effectiveDate: currentDate(),
    weightA: 25,
    weightB: 20,
    weightC: 20,
    weightD: 20,
    weightE: 15,
    contractRate: 100,
    attendanceRate: 95,
    paySuccessRate: 98,
    onlineRate: 95,
    injuryRate: 2.5,
    warningCloseRate: 90,
    remark: undefined
  }
}

function createRules() {
  return {
    regionCode: [{ required: true, message: '请选择区域', trigger: 'change' }],
    version: [{ required: true, message: '版本不能为空', trigger: 'blur' }],
    effectiveDate: [{ required: true, message: '请选择生效日期', trigger: 'change' }]
  }
}

function normalizeConfig(row = {}) {
  return {
    ...row,
    regionName: row.regionName || regionNameMap[row.regionCode] || row.regionCode
  }
}

export function summaryCard(key, label, value, unit, note, cardClass) {
  return { key, label, value, unit, note, cardClass }
}

export function focusQueue(key, title, count, unit, desc, actionText) {
  return { key, title, count, unit, desc, actionText }
}

export function valueOrDefault(value, fallback = 0) {
  return value === undefined || value === null ? fallback : value
}

export function parseJson(text) {
  if (!text) {
    return {}
  }
  try {
    return JSON.parse(text)
  } catch (e) {
    return {}
  }
}

export function weightSummary(text) {
  const weights = parseJson(text)
  return ['A', 'B', 'C', 'D', 'E']
    .map(code => `${code}:${weights[code] || 0}`)
    .join(' / ')
}

export function targetSummary(text) {
  const targets = parseJson(text)
  return `合同${targets.contractRate || '-'}% / 考勤${targets.attendanceRate || '-'}% / 工资${targets.paySuccessRate || '-'}% / 在线${targets.onlineRate || '-'}% / 工伤${targets.injuryRate || '-'}‰ / 闭环${targets.warningCloseRate || '-'}%`
}

export function formatPercent(value) {
  if (value === undefined || value === null || value === '') {
    return '-'
  }
  const number = Number(value)
  if (Number.isNaN(number)) {
    return '-'
  }
  return `${number.toFixed(1)} %`
}

export function matchConfigFocus(config, focusKey) {
  if (!focusKey) {
    return true
  }
  const weights = parseJson(config.dimensionWeights)
  const targets = parseJson(config.targetValues)
  const weightSum = Number(weights.A || 0) + Number(weights.B || 0) + Number(weights.C || 0) + Number(weights.D || 0) + Number(weights.E || 0)
  if (focusKey === 'active') {
    return config.configStatus === '1'
  }
  if (focusKey === 'upcoming') {
    return config.configStatus !== '1'
  }
  if (focusKey === 'weight') {
    return weightSum !== 100
  }
  if (focusKey === 'closeRate') {
    return Number(targets.warningCloseRate || 0) > 0 && Number(targets.warningCloseRate || 0) < 90
  }
  if (focusKey === 'covered') {
    return Boolean(config.regionCode)
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

export function buildHintTags(config) {
  if (!config) {
    return [{ label: '未选中配置，可先在列表中选择当前待复核模型版本。', type: 'info' }]
  }
  const weights = parseJson(config.dimensionWeights)
  const targets = parseJson(config.targetValues)
  const weightSum = Number(weights.A || 0) + Number(weights.B || 0) + Number(weights.C || 0) + Number(weights.D || 0) + Number(weights.E || 0)
  const warningCloseRate = Number(targets.warningCloseRate || 0)
  const onlineRate = Number(targets.onlineRate || 0)
  const tags = []

  if (config.configStatus === '0') {
    tags.push({ label: '当前版本处于停用状态，建议确认是否仍有 AI 报告链路依赖该口径。', type: 'warning' })
  }
  if (config.configStatus === '1') {
    tags.push({ label: '当前版本已启用，修改前建议先核对是否影响正在使用的评分口径。', type: 'success' })
  }
  if (weightSum !== 100) {
    tags.push({ label: `当前权重总和为 ${weightSum}，建议校正到 100 以避免评分口径偏移。`, type: 'warning' })
  }
  if (warningCloseRate > 0 && warningCloseRate < 90) {
    tags.push({ label: '当前闭环目标值偏低，建议结合预警办理要求复核目标是否过松。', type: 'warning' })
  }
  if (onlineRate > 0 && onlineRate < 90) {
    tags.push({ label: '当前设备在线率目标偏低，建议复核设备工伤链路的目标口径。', type: 'info' })
  }
  if (!config.version) {
    tags.push({ label: '当前缺少版本号，建议补齐可识别的区域版本命名。', type: 'warning' })
  }
  if (!config.remark) {
    tags.push({ label: '当前缺少模型说明，建议补录版本用途、调整原因或适用范围。', type: 'info' })
  }
  if (!tags.length) {
    tags.push({ label: '当前模型配置台账信息较完整，可继续用于版本启用和后续复核归档。', type: 'success' })
  }
  return tags
}

export function useAiReportConfigPage(options = {}) {
  const {
    exportFilePrefix = 'ygb_ai_report_config',
    canMutate = () => true,
    onBlockedAction,
    getCurrentList,
    afterList,
    immediate = true
  } = options

  const { proxy } = getCurrentInstance()
  const defaultRegionCode = authorizedDefaultRegionCode('440000')
  const authorizedRegionOptions = useAuthorizedRegionOptions(regionOptions)

  const configList = ref([])
  const loading = ref(false)
  const showSearch = ref(true)
  const open = ref(false)
  const detailOpen = ref(false)
  const total = ref(0)
  const ids = ref([])
  const single = ref(true)
  const title = ref('')
  const currentConfigRow = ref(undefined)
  const detailConfig = ref(undefined)
  const summaryData = ref({})

  const data = reactive({
    form: createDefaultForm(defaultRegionCode),
    queryParams: createDefaultQueryParams(),
    rules: createRules()
  })

  const { queryParams, form, rules } = toRefs(data)

  function resolveCurrentList() {
    if (typeof getCurrentList === 'function') {
      const list = getCurrentList()
      if (Array.isArray(list)) {
        return list
      }
    }
    return configList.value
  }

  function guardMutation(actionLabel) {
    if (canMutate()) {
      return true
    }
    if (typeof onBlockedAction === 'function') {
      onBlockedAction(actionLabel)
    } else {
      proxy.$modal.msgWarning(`当前角色仅支持查看，不能${actionLabel}`)
    }
    return false
  }

  function buildSummaryQuery() {
    return {
      regionCode: queryParams.value.regionCode,
      configStatus: queryParams.value.configStatus
    }
  }

  function syncCurrentConfig() {
    const currentList = resolveCurrentList()
    if (currentConfigRow.value) {
      const matched = currentList.find(item => item.configId === currentConfigRow.value.configId)
      if (matched) {
        currentConfigRow.value = matched
        return
      }
    }
    currentConfigRow.value = currentList.length > 0 ? currentList[0] : undefined
  }

  function getList() {
    loading.value = true
    Promise.all([
      listAiReportConfig(queryParams.value),
      getAiReportConfigSummary(buildSummaryQuery())
    ]).then(([listResponse, summaryResponse]) => {
      configList.value = (listResponse.rows || []).map(item => normalizeConfig(item))
      total.value = listResponse.total || 0
      summaryData.value = summaryResponse.data || {}
      if (typeof afterList === 'function') {
        afterList(configList.value)
      }
      syncCurrentConfig()
    }).finally(() => {
      loading.value = false
    })
  }

  function handleQuery() {
    queryParams.value.pageNum = 1
    getList()
  }

  function resetQuery() {
    proxy.resetForm('queryRef')
    queryParams.value = createDefaultQueryParams()
    getList()
  }

  function handleSelectionChange(selection) {
    ids.value = selection.map(item => item.configId)
    single.value = selection.length !== 1
    if (selection.length === 1) {
      currentConfigRow.value = selection[0]
    }
  }

  function handleRowClick(row) {
    currentConfigRow.value = row
  }

  function reset() {
    form.value = createDefaultForm(defaultRegionCode)
    proxy.resetForm('configRef')
  }

  function cancel() {
    open.value = false
    reset()
  }

  function handleAdd() {
    if (!guardMutation('新增模型版本')) {
      return
    }
    reset()
    open.value = true
    title.value = '新增评分模型'
  }

  function fillForm(row = {}) {
    const weights = parseJson(row.dimensionWeights)
    const targets = parseJson(row.targetValues)
    form.value = {
      configId: row.configId,
      regionCode: row.regionCode || defaultRegionCode,
      version: row.version || '',
      configStatus: row.configStatus || '1',
      effectiveDate: row.effectiveDate ? formatDate(row.effectiveDate, proxy) : currentDate(),
      weightA: Number(weights.A || 25),
      weightB: Number(weights.B || 20),
      weightC: Number(weights.C || 20),
      weightD: Number(weights.D || 20),
      weightE: Number(weights.E || 15),
      contractRate: Number(targets.contractRate || 100),
      attendanceRate: Number(targets.attendanceRate || 95),
      paySuccessRate: Number(targets.paySuccessRate || 98),
      onlineRate: Number(targets.onlineRate || 95),
      injuryRate: Number(targets.injuryRate || 2.5),
      warningCloseRate: Number(targets.warningCloseRate || 90),
      remark: row.remark
    }
  }

  function handleUpdate(row) {
    if (!guardMutation('修改模型版本')) {
      return
    }
    const configId = row?.configId || ids.value[0]
    if (!configId) {
      proxy.$modal.msgWarning('请选择一个模型配置')
      return
    }
    reset()
    getAiReportConfig(configId).then(response => {
      const target = normalizeConfig(response.data || {})
      fillForm(target)
      currentConfigRow.value = target
      open.value = true
      title.value = '修改评分模型'
    })
  }

  function openDetail(row) {
    const target = row || currentConfigRow.value
    if (!target?.configId) {
      return
    }
    currentConfigRow.value = target
    getAiReportConfig(target.configId).then(response => {
      detailConfig.value = normalizeConfig(response.data || {})
      detailOpen.value = true
    })
  }

  function handleActivate(row) {
    if (!guardMutation('启用模型版本')) {
      return
    }
    const configId = row?.configId || ids.value[0]
    if (!configId) {
      proxy.$modal.msgWarning('请选择一个模型配置')
      return
    }
    proxy.$modal.confirm(`是否确认启用评分模型 ${configId}？`).then(() => {
      return activateAiReportConfig(configId)
    }).then(() => {
      proxy.$modal.msgSuccess('启用成功')
      getList()
    }).catch(() => {})
  }

  function buildPayload() {
    return {
      configId: form.value.configId,
      regionCode: form.value.regionCode,
      version: form.value.version,
      configStatus: form.value.configStatus,
      effectiveDate: form.value.effectiveDate,
      dimensionWeights: JSON.stringify({
        A: form.value.weightA,
        B: form.value.weightB,
        C: form.value.weightC,
        D: form.value.weightD,
        E: form.value.weightE
      }),
      targetValues: JSON.stringify({
        contractRate: form.value.contractRate,
        attendanceRate: form.value.attendanceRate,
        paySuccessRate: form.value.paySuccessRate,
        onlineRate: form.value.onlineRate,
        injuryRate: form.value.injuryRate,
        warningCloseRate: form.value.warningCloseRate
      }),
      remark: form.value.remark
    }
  }

  function submitForm() {
    if (!guardMutation(form.value.configId ? '修改模型配置' : '提交模型配置')) {
      return
    }
    proxy.$refs.configRef.validate(valid => {
      if (!valid) {
        return
      }
      const request = form.value.configId ? updateAiReportConfig(buildPayload()) : addAiReportConfig(buildPayload())
      request.then(() => {
        proxy.$modal.msgSuccess(form.value.configId ? '修改成功' : '新增成功')
        open.value = false
        getList()
      })
    })
  }

  function handleExport() {
    proxy.download('ygb/aiReport/config/export', { ...queryParams.value }, `${exportFilePrefix}_${Date.now()}.xlsx`)
  }

  function init() {
    reset()
    getList()
  }

  if (immediate) {
    init()
  }

  return {
    regionOptions: authorizedRegionOptions,
    regionNameMap,
    statusOptions,
    configList,
    loading,
    showSearch,
    open,
    detailOpen,
    total,
    ids,
    single,
    title,
    currentConfigRow,
    detailConfig,
    summaryData,
    queryParams,
    form,
    rules,
    buildSummaryQuery,
    syncCurrentConfig,
    getList,
    handleQuery,
    resetQuery,
    handleSelectionChange,
    handleRowClick,
    reset,
    cancel,
    handleAdd,
    fillForm,
    handleUpdate,
    openDetail,
    handleActivate,
    buildPayload,
    submitForm,
    handleExport,
    init
  }
}

function formatDate(value, proxy) {
  if (!value) {
    return ''
  }
  if (typeof value === 'string') {
    return value.slice(0, 10)
  }
  return proxy.parseTime(value, '{y}-{m}-{d}')
}

function currentDate() {
  return formatNativeDate(new Date())
}

function formatNativeDate(date) {
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}
