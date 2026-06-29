import { getCurrentInstance, reactive, ref, toRefs } from 'vue'
import { getAqInsurance, getAqInsuranceSummary, listAqInsurance, syncAqInsurance } from '@/api/ygb/aqInsurance'
import { optionselectEnterprise } from '@/api/ygb/enterprise'
import { gdRegionNameMap, gdRegionOptions } from '@/utils/regionName'

export const regionOptions = gdRegionOptions

export const regionNameMap = gdRegionNameMap

export const policyStatusOptions = [
  { label: '未生效', value: '0' },
  { label: '有效', value: '1' },
  { label: '即将到期', value: '2' },
  { label: '已过期', value: '3' }
]

function createDefaultQueryParams() {
  return {
    pageNum: 1,
    pageSize: 10,
    statMonth: currentMonth(),
    regionCode: undefined,
    enterpriseId: undefined,
    enterpriseName: undefined,
    policyStatus: undefined
  }
}

export function summaryCard(key, label, value, unit, note, cardClass) {
  return { key, label, value, unit, note, cardClass }
}

export function focusQueue(key, title, count, unit, desc, actionText) {
  return { key, title, count, unit, desc, actionText }
}

export function buildPolicyHintTags(policy, focus) {
  if (!policy) {
    if (focus?.title) {
      return [
        { label: `当前焦点：${focus.title}`, type: 'info' },
        { label: `优先动作：${focus.actionText}`, type: 'warning' }
      ]
    }
    return [{ label: '请选择保单查看治理提示', type: 'info' }]
  }

  const tags = []
  const remainingAmount = Number(policy.remainingFundAmount || 0)
  const expireInDays = Number(policy.expireInDays)

  if (policy.policyStatus === '2') {
    tags.push({ label: '保单即将到期，建议尽快确认续保和覆盖衔接', type: 'warning' })
  }
  if (policy.policyStatus === '3') {
    tags.push({ label: '保单已过期，建议优先核查覆盖中断和企业补保计划', type: 'danger' })
  }
  if (!Number.isNaN(expireInDays) && expireInDays >= 0 && expireInDays <= 15) {
    tags.push({ label: '剩余天数较短，建议提前准备续保材料和付款安排', type: 'warning' })
  }
  if (remainingAmount > 0 && remainingAmount < 10000) {
    tags.push({ label: '预防费余额偏低，建议核对已使用记录和后续投入计划', type: 'warning' })
  }
  if (policy.sourceStatus && policy.sourceStatus !== 'SUCCESS' && policy.sourceStatus !== '1') {
    tags.push({ label: '同步回写异常，建议复核保单同步结果和来源消息', type: 'warning' })
  }
  if (!policy.sourceStatus) {
    tags.push({ label: '缺少回写状态展示，建议确认本月同步是否已执行', type: 'info' })
  }
  if (policy.policyStatus === '1' && remainingAmount >= 10000) {
    tags.push({ label: '保单状态稳定，可继续用于月度归档和投保覆盖校验', type: 'success' })
  }
  if (!tags.length) {
    tags.push({ label: '当前保单信息完整，可继续跟踪续保和预防费留痕', type: 'success' })
  }
  return tags
}

export function matchPolicyFocus(row, focusKey) {
  if (!row || !focusKey) {
    return false
  }
  if (focusKey === 'soon') {
    return String(row.policyStatus || '') === '2'
  }
  if (focusKey === 'risk') {
    return ['2', '3'].includes(String(row.policyStatus || '')) || Number(row.expireInDays) <= 15
  }
  if (focusKey === 'remaining') {
    const remainingAmount = Number(row.remainingFundAmount || 0)
    return remainingAmount > 0 && remainingAmount < 10000
  }
  if (focusKey === 'coverage') {
    return String(row.policyStatus || '') === '1'
  }
  if (focusKey === 'insurer') {
    return !!row.insurerName
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

export function policyStatusLabel(value) {
  const matched = policyStatusOptions.find(item => item.value === value)
  return matched ? matched.label : '-'
}

export function formatDecimal(value) {
  const number = Number(value || 0)
  return Number.isNaN(number) ? '0.00' : number.toFixed(2)
}

export function formatRate(value) {
  const number = Number(value || 0)
  return Number.isNaN(number) ? '0.0' : number.toFixed(1)
}

export function valueOrDefault(value, fallback) {
  return value === undefined || value === null ? fallback : value
}

export function valueWithUnit(value, unit) {
  if (value === undefined || value === null || value === '') {
    return '-'
  }
  return `${value} ${unit}`
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

export function useAqInsurancePage(options = {}) {
  const {
    exportFilePrefix = 'ygb_aq_insurance',
    canMutate = () => true,
    onBlockedAction,
    getCurrentList,
    afterList
  } = options
  const { proxy } = getCurrentInstance()

  const aqInsuranceList = ref([])
  const enterpriseOptions = ref([])
  const loading = ref(false)
  const showSearch = ref(true)
  const total = ref(0)
  const detailOpen = ref(false)
  const currentPolicy = ref(undefined)
  const policyDetail = ref(undefined)
  const summaryData = ref({})

  const data = reactive({
    queryParams: createDefaultQueryParams()
  })

  const { queryParams } = toRefs(data)

  function resolveCurrentList() {
    if (typeof getCurrentList === 'function') {
      const list = getCurrentList()
      if (Array.isArray(list)) {
        return list
      }
    }
    return aqInsuranceList.value
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
      policyStatus: queryParams.value.policyStatus
    }
  }

  function syncCurrentPolicy() {
    const currentList = resolveCurrentList()
    if (currentPolicy.value) {
      const matched = currentList.find(item => item.policyId === currentPolicy.value.policyId)
      if (matched) {
        currentPolicy.value = matched
        return
      }
    }
    currentPolicy.value = currentList.length > 0 ? currentList[0] : undefined
  }

  function getList() {
    loading.value = true
    Promise.all([
      listAqInsurance(queryParams.value),
      getAqInsuranceSummary(buildSummaryQuery())
    ]).then(([listResponse, summaryResponse]) => {
      aqInsuranceList.value = listResponse.rows || []
      total.value = listResponse.total || 0
      summaryData.value = summaryResponse.data || {}
      if (typeof afterList === 'function') {
        afterList()
      }
      syncCurrentPolicy()
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
    currentPolicy.value = row
  }

  function handleQuery() {
    queryParams.value.pageNum = 1
    getList()
  }

  function resetQuery() {
    proxy.resetForm('queryRef')
    Object.assign(queryParams.value, createDefaultQueryParams())
    getList()
  }

  function syncPolicyContext(policy) {
    if (!guardMutation('执行保单同步')) {
      return
    }
    const statMonth = policy?.statMonth || queryParams.value.statMonth
    if (!statMonth) {
      proxy.$modal.msgWarning('请先选择统计月份')
      return
    }
    syncAqInsurance({
      statMonth,
      enterpriseId: policy?.enterpriseId || queryParams.value.enterpriseId
    }).then(response => {
      proxy.$modal.msgSuccess(response.msg || '保单同步完成')
      getList()
    })
  }

  function handleSync() {
    syncPolicyContext()
  }

  function handleExport() {
    proxy.download('ygb/aqInsurance/export', { ...queryParams.value }, `${exportFilePrefix}_${new Date().getTime()}.xlsx`)
  }

  function openDetail(row) {
    if (!row?.policyId) {
      return
    }
    currentPolicy.value = row
    getAqInsurance(row.policyId).then(response => {
      policyDetail.value = response.data || {}
      detailOpen.value = true
    })
  }

  function formatDateTime(value) {
    if (!value) {
      return '-'
    }
    return proxy.parseTime(value, '{y}-{m}-{d} {h}:{i}:{s}')
  }

  return {
    aqInsuranceList,
    enterpriseOptions,
    loading,
    showSearch,
    total,
    detailOpen,
    currentPolicy,
    policyDetail,
    summaryData,
    queryParams,
    buildSummaryQuery,
    syncCurrentPolicy,
    getList,
    loadEnterpriseOptions,
    handleRowClick,
    handleQuery,
    resetQuery,
    handleSync,
    handleExport,
    syncPolicyContext,
    openDetail,
    formatDateTime
  }
}
