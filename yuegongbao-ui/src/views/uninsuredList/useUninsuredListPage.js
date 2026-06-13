import { computed, getCurrentInstance, reactive, ref, toRefs } from 'vue'
import {
  generateUninsuredList,
  getUninsuredListSummary,
  handleUninsured,
  listUninsuredList
} from '@/api/ygb/uninsuredList'
import { optionselectEnterprise } from '@/api/ygb/enterprise'
import useUserStore from '@/store/modules/user'
import { filterAuthorizedRegionOptions } from '@/utils/regionScope'

export const disposalStatusOptions = [
  { label: '待核查', value: '0' },
  { label: '核查中', value: '1' },
  { label: '已催缴', value: '2' },
  { label: '已补缴', value: '3' },
  { label: '强制执行', value: '4' },
  { label: '误报', value: '5' }
]

export const warningStatusOptions = [
  { label: '未预警', value: '0' },
  { label: '已预警', value: '1' }
]

export const uninsuredRegionNameMap = {
  '440000': '广东省',
  '440100': '广州市',
  '440106': '广州市天河区',
  '440300': '深圳市',
  '440305': '深圳市南山区',
  '440600': '佛山市',
  '440606': '佛山市顺德区'
}

export const uninsuredBaseRegionOptions = [
  { label: '广东省', value: '440000' },
  { label: '广州市', value: '440100' },
  { label: '广州市天河区', value: '440106' },
  { label: '深圳市', value: '440300' },
  { label: '深圳市南山区', value: '440305' },
  { label: '佛山市', value: '440600' },
  { label: '佛山市顺德区', value: '440606' }
]

function createDefaultQueryParams(includeRegion = false) {
  return {
    pageNum: 1,
    pageSize: 10,
    statMonth: undefined,
    ...(includeRegion ? { regionCode: undefined } : {}),
    enterpriseId: undefined,
    personName: undefined,
    disposalStatus: undefined,
    warningStatus: undefined
  }
}

function createDefaultHandleForm() {
  return {
    disposalStatus: '1',
    remark: undefined
  }
}

function createHandleRules(message = '处置状态不能为空') {
  return {
    disposalStatus: [{ required: true, message, trigger: 'change' }]
  }
}

export function optionLabel(options, value, fallback = '-') {
  const normalizedOptions = Array.isArray(options) ? options : []
  const matched = normalizedOptions.find(item => item.value === value)
  return matched ? matched.label : fallback
}

export function formatMoney(value) {
  const amount = Number(value || 0)
  if (Number.isNaN(amount)) {
    return '0.00'
  }
  return amount.toFixed(2)
}

export function valueOrDefault(value, fallback = 0) {
  return value === undefined || value === null ? fallback : value
}

export function formatRegionName(code, fallback = '全部区域') {
  if (!code) {
    return fallback
  }
  return uninsuredRegionNameMap[code] || code
}

export function useUninsuredListPage(options = {}) {
  const {
    includeRegion = false,
    exportFilePrefix = 'ygb_uninsured_list',
    initialQueryParams = {},
    handleStatusRequiredMessage = '处置状态不能为空',
    canMutate = () => true,
    onBlockedAction,
    getCurrentList,
    afterList,
    immediate = true
  } = options

  const { proxy } = getCurrentInstance()
  const userStore = useUserStore()

  const uninsuredList = ref([])
  const enterpriseOptions = ref([])
  const loading = ref(false)
  const showSearch = ref(true)
  const total = ref(0)
  const handleOpen = ref(false)
  const detailOpen = ref(false)
  const currentListId = ref(undefined)
  const currentRow = ref(undefined)
  const detailRow = ref(undefined)
  const summaryData = ref({})

  const data = reactive({
    queryParams: {
      ...createDefaultQueryParams(includeRegion),
      ...(initialQueryParams || {})
    },
    handleForm: createDefaultHandleForm(),
    handleRules: createHandleRules(handleStatusRequiredMessage)
  })

  const { queryParams, handleForm, handleRules } = toRefs(data)

  const regionOptions = computed(() => {
    if (!includeRegion) {
      return []
    }
    const optionMap = new Map(uninsuredBaseRegionOptions.map(item => [item.value, item]))
    enterpriseOptions.value.forEach(item => {
      if (item.regionCode && !optionMap.has(item.regionCode)) {
        optionMap.set(item.regionCode, {
          value: item.regionCode,
          label: formatRegionName(item.regionCode)
        })
      }
    })
    uninsuredList.value.forEach(item => {
      if (item.regionCode && !optionMap.has(item.regionCode)) {
        optionMap.set(item.regionCode, {
          value: item.regionCode,
          label: formatRegionName(item.regionCode)
        })
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
    return uninsuredList.value
  }

  function guardMutation(actionLabel) {
    if (canMutate()) {
      return true
    }
    if (typeof onBlockedAction === 'function') {
      onBlockedAction(actionLabel)
    } else {
      proxy.$modal.msgWarning(`当前角色仅支持查看和导出，不能${actionLabel}`)
    }
    return false
  }

  function buildSummaryQuery() {
    return {
      statMonth: queryParams.value.statMonth,
      ...(includeRegion ? { regionCode: queryParams.value.regionCode } : {}),
      enterpriseId: queryParams.value.enterpriseId,
      personName: queryParams.value.personName,
      disposalStatus: queryParams.value.disposalStatus,
      warningStatus: queryParams.value.warningStatus
    }
  }

  function syncCurrentRow() {
    const currentList = resolveCurrentList()
    if (currentRow.value) {
      const matched = currentList.find(item => item.listId === currentRow.value.listId)
      if (matched) {
        currentRow.value = matched
        return
      }
    }
    currentRow.value = currentList.length > 0 ? currentList[0] : undefined
  }

  function getList() {
    loading.value = true
    return Promise.all([
      listUninsuredList(queryParams.value),
      getUninsuredListSummary(buildSummaryQuery())
    ]).then(([listResponse, summaryResponse]) => {
      uninsuredList.value = listResponse.rows || []
      total.value = listResponse.total || 0
      summaryData.value = summaryResponse.data || {}
      if (typeof afterList === 'function') {
        afterList({
          uninsuredList: uninsuredList.value,
          summaryData: summaryData.value
        })
      }
      syncCurrentRow()
    }).finally(() => {
      loading.value = false
    })
  }

  function loadEnterpriseOptions() {
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
    Object.assign(queryParams.value, createDefaultQueryParams(includeRegion))
    getList()
  }

  function ensureMonth() {
    if (!queryParams.value.statMonth) {
      proxy.$modal.msgWarning('请先选择统计月份')
      return false
    }
    return true
  }

  function handleGenerate() {
    if (!guardMutation('执行漏保清单生成')) {
      return
    }
    if (!ensureMonth()) {
      return
    }
    generateUninsuredList({
      statMonth: queryParams.value.statMonth,
      enterpriseId: queryParams.value.enterpriseId
    }).then(response => {
      proxy.$modal.msgSuccess(response.msg || '漏保清单生成完成')
      getList()
    })
  }

  function handleExport() {
    proxy.download('ygb/expansion/uninsured/export', { ...queryParams.value }, `${exportFilePrefix}_${Date.now()}.xlsx`)
  }

  function handleRowClick(row) {
    currentRow.value = row
  }

  function openDetail(row) {
    currentRow.value = row
    detailRow.value = row
    detailOpen.value = true
  }

  function openHandleDialog(row) {
    if (!guardMutation('执行漏保处置')) {
      return
    }
    currentListId.value = row.listId
    currentRow.value = row
    handleForm.value = {
      disposalStatus: row.disposalStatus || '1',
      remark: row.remark
    }
    handleOpen.value = true
  }

  function submitHandle() {
    if (!guardMutation('执行漏保处置')) {
      return
    }
    proxy.$refs.handleRef.validate(valid => {
      if (!valid) {
        return
      }
      handleUninsured(currentListId.value, handleForm.value).then(response => {
        proxy.$modal.msgSuccess(response.msg || '处置完成')
        handleOpen.value = false
        getList()
      })
    })
  }

  function init() {
    loadEnterpriseOptions()
    getList()
  }

  if (immediate) {
    init()
  }

  return {
    uninsuredList,
    enterpriseOptions,
    loading,
    showSearch,
    total,
    handleOpen,
    detailOpen,
    currentListId,
    currentRow,
    detailRow,
    summaryData,
    queryParams,
    handleForm,
    handleRules,
    regionOptions,
    buildSummaryQuery,
    getList,
    loadEnterpriseOptions,
    syncCurrentRow,
    handleQuery,
    resetQuery,
    ensureMonth,
    handleGenerate,
    handleExport,
    handleRowClick,
    openDetail,
    openHandleDialog,
    submitHandle,
    init
  }
}
