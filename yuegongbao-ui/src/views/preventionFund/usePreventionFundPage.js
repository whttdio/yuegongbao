import { computed, getCurrentInstance, reactive, ref, toRefs } from 'vue'
import { getPreventionFund, getPreventionFundSummary, listPreventionFund, updatePreventionFund } from '@/api/ygb/preventionFund'
import { optionselectEnterprise } from '@/api/ygb/enterprise'
import { gdRegionNameMap, gdRegionOptions } from '@/utils/regionName'

export const regionOptions = gdRegionOptions

export const regionNameMap = gdRegionNameMap

export const fundStatusOptions = [
  { label: '待计提', value: '0' },
  { label: '可使用', value: '1' },
  { label: '使用中', value: '2' },
  { label: '已核销', value: '3' }
]

function createDefaultQueryParams() {
  return {
    pageNum: 1,
    pageSize: 10,
    statMonth: currentMonth(),
    regionCode: undefined,
    enterpriseId: undefined,
    enterpriseName: undefined,
    fundStatus: undefined
  }
}

function createDefaultForm() {
  return {
    fundId: undefined,
    statMonth: undefined,
    enterpriseName: undefined,
    accruedAmount: 0,
    usedAmount: 0,
    usagePurpose: undefined,
    evidenceUrl: undefined,
    lastSettleTime: undefined
  }
}

function createRules(requireUsagePurpose = false) {
  return {
    usedAmount: [{ required: true, message: '请输入已使用金额', trigger: 'blur' }],
    ...(requireUsagePurpose
      ? {
          usagePurpose: [{ required: true, message: '请输入资金使用用途', trigger: 'blur' }]
        }
      : {})
  }
}

export function summaryCard(key, label, value, unit, note, cardClass) {
  return { key, label, value, unit, note, cardClass }
}

export function focusQueue(key, title, count, unit, desc, actionText) {
  return { key, title, count, unit, desc, actionText }
}

export function sourceModeLabel(value) {
  const sourceMode = String(value || '').toLowerCase()
  if (!sourceMode) return '-'
  if (sourceMode.includes('stub')) return '接口同步'
  if (sourceMode.includes('manual')) return '人工维护'
  if (sourceMode.includes('sync')) return '同步回写'
  if (sourceMode.includes('system')) return '系统生成'
  return value
}

export function isManualSourceMode(value) {
  return String(value || '').toLowerCase().includes('manual')
}

export function isFormalSourceMode(value) {
  const sourceMode = String(value || '').toLowerCase()
  return Boolean(sourceMode) && !sourceMode.includes('manual')
}

export function isLowBalance(accruedAmount, remainingAmount) {
  const accrued = Number(accruedAmount || 0)
  const remaining = Number(remainingAmount || 0)
  if (accrued <= 0) {
    return false
  }
  return remaining / accrued <= 0.1
}

export function matchFundFocus(fund, focusKey) {
  if (!fund || !focusKey) {
    return false
  }
  if (focusKey === 'lowBalance') {
    return isLowBalance(fund.accruedAmount, fund.remainingAmount)
  }
  if (focusKey === 'inUse') {
    return String(fund.fundStatus) === '2'
  }
  if (focusKey === 'missingEvidence') {
    return !fund.evidenceUrl
  }
  if (focusKey === 'nonStub') {
    return isFormalSourceMode(fund.sourceMode)
  }
  if (focusKey === 'all') {
    return true
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

export function formatMoney(value) {
  const number = Number(value || 0)
  if (Number.isNaN(number)) {
    return '0.00'
  }
  return number.toFixed(2)
}

export function valueOrDefault(value, fallback) {
  return value === undefined || value === null ? fallback : value
}

export function formatRegionName(code, fallback = '-') {
  if (!code) {
    return fallback
  }
  return regionNameMap[code] || code
}

export function currentMonth() {
  const date = new Date()
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}`
}

export function formatDateTime(value, proxy) {
  if (!value) {
    return '-'
  }
  if (proxy?.parseTime) {
    return proxy.parseTime(value, '{y}-{m}-{d} {h}:{i}:{s}')
  }
  return value
}

export function usePreventionFundPage(options = {}) {
  const {
    exportFilePrefix = 'ygb_prevention_fund',
    canMutate = () => true,
    onBlockedAction,
    getCurrentList,
    requireUsagePurpose = false,
    initialQueryParams = {}
  } = options
  const { proxy } = getCurrentInstance()

  const loading = ref(false)
  const showSearch = ref(true)
  const total = ref(0)
  const fundList = ref([])
  const enterpriseOptions = ref([])
  const currentFund = ref(undefined)
  const detailFund = ref(undefined)
  const detailOpen = ref(false)
  const open = ref(false)
  const title = ref('')
  const summaryData = ref({})

  const data = reactive({
    form: createDefaultForm(),
    queryParams: createDefaultQueryParams(),
    rules: createRules(requireUsagePurpose)
  })

  const { form, queryParams, rules } = toRefs(data)
  Object.assign(queryParams.value, initialQueryParams || {})

  function resolveCurrentList() {
    if (typeof getCurrentList === 'function') {
      const list = getCurrentList()
      if (Array.isArray(list)) {
        return list
      }
    }
    return fundList.value
  }

  function guardMutation(actionLabel) {
    if (canMutate()) {
      return true
    }
    if (typeof onBlockedAction === 'function') {
      onBlockedAction(actionLabel)
    }
    return false
  }

  function buildSummaryQuery() {
    return {
      statMonth: queryParams.value.statMonth,
      regionCode: queryParams.value.regionCode,
      enterpriseId: queryParams.value.enterpriseId,
      enterpriseName: queryParams.value.enterpriseName,
      fundStatus: queryParams.value.fundStatus
    }
  }

  function syncCurrentFund() {
    const currentList = resolveCurrentList()
    if (currentFund.value) {
      const matched = currentList.find(item => item.fundId === currentFund.value.fundId)
      if (matched) {
        currentFund.value = matched
        return
      }
    }
    currentFund.value = currentList.length > 0 ? currentList[0] : undefined
  }

  function getList() {
    loading.value = true
    Promise.all([
      listPreventionFund(queryParams.value),
      getPreventionFundSummary(buildSummaryQuery())
    ]).then(([listResponse, summaryResponse]) => {
      fundList.value = listResponse.rows || []
      total.value = listResponse.total || 0
      summaryData.value = summaryResponse.data || {}
      syncCurrentFund()
    }).finally(() => {
      loading.value = false
    })
  }

  function loadEnterpriseOptions() {
    optionselectEnterprise().then(response => {
      enterpriseOptions.value = response.data || []
    })
  }

  function handleRowClick(row) {
    currentFund.value = row
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

  function normalizeEditForm(detail = {}) {
    return {
      fundId: detail.fundId,
      statMonth: detail.statMonth,
      enterpriseName: detail.enterpriseName,
      accruedAmount: Number(detail.accruedAmount || 0),
      usedAmount: Number(detail.usedAmount || 0),
      usagePurpose: detail.usagePurpose,
      evidenceUrl: detail.evidenceUrl,
      lastSettleTime: detail.lastSettleTime
        ? proxy.parseTime(detail.lastSettleTime, '{y}-{m}-{d} {h}:{i}:{s}')
        : proxy.parseTime(new Date(), '{y}-{m}-{d} {h}:{i}:{s}')
    }
  }

  function openEditDialog(row) {
    if (!guardMutation('执行资金维护')) {
      return
    }
    const target = row || currentFund.value
    if (!target?.fundId) {
      proxy.$modal.msgWarning('请选择一条资金池记录')
      return
    }
    getPreventionFund(target.fundId).then(response => {
      const detail = response.data || {}
      currentFund.value = detail
      form.value = normalizeEditForm(detail)
      title.value = '维护事故预防资金'
      open.value = true
    })
  }

  function submitForm() {
    if (!guardMutation('执行资金维护')) {
      return
    }
    const accruedAmount = Number(form.value.accruedAmount || 0)
    const usedAmount = Number(form.value.usedAmount || 0)
    if (usedAmount < 0) {
      proxy.$modal.msgWarning('已使用金额不能小于 0')
      return
    }
    if (usedAmount > accruedAmount) {
      proxy.$modal.msgWarning('已使用金额不能大于计提金额')
      return
    }
    if (usedAmount > 0 && (!form.value.usagePurpose || !form.value.evidenceUrl)) {
      proxy.$modal.msgWarning('使用预防资金时请填写用途和凭证地址')
      return
    }
    proxy.$refs.fundRef.validate(valid => {
      if (!valid) {
        return
      }
      updatePreventionFund(form.value).then(() => {
        proxy.$modal.msgSuccess('维护成功')
        open.value = false
        getList()
      })
    })
  }

  function openDetail(row) {
    const target = row || currentFund.value
    if (!target?.fundId) {
      proxy.$modal.msgWarning('请选择一条资金池记录')
      return
    }
    currentFund.value = target
    getPreventionFund(target.fundId).then(response => {
      detailFund.value = response.data || {}
      detailOpen.value = true
    })
  }

  function handleExport() {
    proxy.download('ygb/preventionFund/export', { ...queryParams.value }, `${exportFilePrefix}_${new Date().getTime()}.xlsx`)
  }

  const remainingPreview = computed(() => {
    const accruedAmount = Number(form.value.accruedAmount || 0)
    const usedAmount = Number(form.value.usedAmount || 0)
    return formatMoney(Math.max(accruedAmount - usedAmount, 0))
  })

  loadEnterpriseOptions()
  getList()

  return {
    loading,
    showSearch,
    total,
    fundList,
    enterpriseOptions,
    currentFund,
    detailFund,
    detailOpen,
    open,
    editOpen: open,
    title,
    summaryData,
    form,
    queryParams,
    rules,
    remainingPreview,
    getList,
    syncCurrentFund,
    handleRowClick,
    handleQuery,
    resetQuery,
    openEditDialog,
    submitForm,
    openDetail,
    handleExport,
    formatDateTime: value => formatDateTime(value, proxy)
  }
}
