import { getCurrentInstance, reactive, ref, toRefs } from 'vue'
import { generateCreditScore, getCreditScore, getCreditScoreSummary, listCreditScore } from '@/api/ygb/creditScore'
import { optionselectEnterprise } from '@/api/ygb/enterprise'
import { authorizedDefaultRegionCode } from '@/utils/regionScope'
import { formatRegionName, gdRegionNameMap, gdRegionOptions } from '@/utils/regionName'
import {
  applyLockedEnterpriseQuery,
  filterAuthorizedEnterpriseOptions,
  isEnterpriseFilterLocked,
  lockedEnterpriseId
} from '@/utils/enterpriseScope'

export const creditScoreRegionOptions = gdRegionOptions

export const creditScoreRegionNameMap = gdRegionNameMap

export const creditLevelOptions = [
  { label: 'A级', value: 'A' },
  { label: 'B级', value: 'B' },
  { label: 'C级', value: 'C' },
  { label: 'D级', value: 'D' }
]

export const creditColorOptions = [
  { label: '绿码', value: 'GREEN' },
  { label: '黄码', value: 'YELLOW' },
  { label: '红码', value: 'RED' }
]

export function currentMonth() {
  const date = new Date()
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}`
}

export function valueOrDefault(value, fallback = 0) {
  return value ?? fallback
}

export function formatDecimal(value) {
  const number = Number(value || 0)
  if (Number.isNaN(number)) {
    return '0.00'
  }
  return number.toFixed(2)
}

export function levelTagType(level) {
  if (level === 'A') return 'success'
  if (level === 'B') return ''
  if (level === 'C') return 'warning'
  return 'danger'
}

export function colorTagType(colorCode) {
  if (colorCode === 'GREEN') return 'success'
  if (colorCode === 'YELLOW') return 'warning'
  return 'danger'
}

export function colorLabel(colorCode) {
  if (colorCode === 'GREEN') return '绿码'
  if (colorCode === 'YELLOW') return '黄码'
  if (colorCode === 'RED') return '红码'
  return '-'
}

export { formatRegionName } from '@/utils/regionName'

function createDefaultQueryParams() {
  return applyLockedEnterpriseQuery({
    pageNum: 1,
    pageSize: 10,
    statMonth: currentMonth(),
    enterpriseId: undefined,
    regionCode: undefined,
    creditLevel: undefined,
    colorCode: undefined
  })
}

function createDefaultGenerateForm(defaultRegionCode = '440000') {
  return applyLockedEnterpriseQuery({
    statMonth: currentMonth(),
    regionCode: defaultRegionCode,
    enterpriseId: undefined
  })
}

export function useCreditScorePage(options = {}) {
  const {
    exportFilePrefix = 'credit_score',
    defaultRegionCode = '440000',
    canMutate = () => true,
    onBlockedAction,
    getCurrentList,
    afterList,
    immediate = true
  } = options

  const { proxy } = getCurrentInstance()
  const authorizedRegionCode = authorizedDefaultRegionCode(defaultRegionCode)

  const loading = ref(false)
  const showSearch = ref(true)
  const total = ref(0)
  const enterpriseOptions = ref([])
  const creditScoreList = ref([])
  const currentScore = ref(undefined)
  const scoreDetail = ref(undefined)
  const factorRows = ref([])
  const detailOpen = ref(false)
  const generateOpen = ref(false)
  const summaryData = ref({})

  const data = reactive({
    queryParams: createDefaultQueryParams(),
    generateForm: createDefaultGenerateForm(authorizedRegionCode),
    generateRules: {
      statMonth: [{ required: true, message: '请选择统计月份', trigger: 'change' }]
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
    return creditScoreList.value
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
    return applyLockedEnterpriseQuery({
      statMonth: queryParams.value.statMonth,
      enterpriseId: queryParams.value.enterpriseId,
      regionCode: queryParams.value.regionCode,
      creditLevel: queryParams.value.creditLevel,
      colorCode: queryParams.value.colorCode
    })
  }

  function syncCurrentScore() {
    const rows = resolveCurrentList()
    if (currentScore.value) {
      const matched = rows.find(item => item.scoreId === currentScore.value.scoreId)
      if (matched) {
        currentScore.value = matched
        return
      }
    }
    currentScore.value = rows.length > 0 ? rows[0] : undefined
  }

  function getList() {
    loading.value = true
    const scopedQuery = applyLockedEnterpriseQuery(queryParams.value)
    return Promise.all([
      listCreditScore(scopedQuery),
      getCreditScoreSummary(buildSummaryQuery())
    ]).then(([listResponse, summaryResponse]) => {
      creditScoreList.value = (listResponse.rows || []).map(item => ({
        ...item,
        regionName: item.regionName || creditScoreRegionNameMap[item.regionCode] || item.regionCode
      }))
      total.value = listResponse.total || 0
      summaryData.value = summaryResponse.data || {}
      if (typeof afterList === 'function') {
        afterList({
          creditScoreList: creditScoreList.value,
          summaryData: summaryData.value
        })
      }
      syncCurrentScore()
    }).finally(() => {
      loading.value = false
    })
  }

  function loadEnterpriseOptions() {
    return optionselectEnterprise().then(response => {
      enterpriseOptions.value = filterAuthorizedEnterpriseOptions(response.data || [])
      const enterpriseId = lockedEnterpriseId()
      if (enterpriseId != null) {
        queryParams.value.enterpriseId = enterpriseId
        generateForm.value.enterpriseId = enterpriseId
      }
    })
  }

  function handleRowClick(row) {
    currentScore.value = row
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

  function openGenerateDialog(row) {
    if (!guardMutation('执行评分生成')) {
      return
    }
    generateForm.value = applyLockedEnterpriseQuery({
      statMonth: row?.statMonth || queryParams.value.statMonth || currentMonth(),
      regionCode: row?.regionCode || queryParams.value.regionCode || defaultRegionCode,
      enterpriseId: row?.enterpriseId || queryParams.value.enterpriseId
    })
    generateOpen.value = true
  }

  function submitGenerate() {
    if (!guardMutation('执行评分生成')) {
      return
    }
    proxy.$refs.generateRef.validate(valid => {
      if (!valid) {
        return
      }
      generateCreditScore(generateForm.value).then(response => {
        proxy.$modal.msgSuccess(response.msg || '生成成功')
        generateOpen.value = false
        getList()
      })
    })
  }

  function handleExport() {
    proxy.download('ygb/credit/score/export', { ...queryParams.value }, `${exportFilePrefix}_${Date.now()}.xlsx`)
  }

  function openDetail(row) {
    if (!row?.scoreId) {
      return
    }
    currentScore.value = row
    getCreditScore(row.scoreId).then(response => {
      scoreDetail.value = response.data || {}
      factorRows.value = Object.values(response.factors || {})
      detailOpen.value = true
    })
  }

  loadEnterpriseOptions()

  if (immediate) {
    getList()
  }

  return {
    loading,
    showSearch,
    total,
    enterpriseOptions,
    creditScoreList,
    currentScore,
    scoreDetail,
    factorRows,
    detailOpen,
    generateOpen,
    summaryData,
    queryParams,
    generateForm,
    generateRules,
    getList,
    handleRowClick,
    handleQuery,
    resetQuery,
    openGenerateDialog,
    submitGenerate,
    handleExport,
    openDetail,
    syncCurrentScore,
    isEnterpriseFilterLocked
  }
}
