import { getCurrentInstance, reactive, ref, toRefs } from 'vue'
import {
  addWarningRule,
  delWarningRule,
  getWarningRule,
  getWarningRuleSummary,
  listWarningRule,
  updateWarningRule
} from '@/api/ygb/warningRule'

export const warnLevelOptions = [
  { label: '提示', value: '1' },
  { label: '黄警', value: '2' },
  { label: '红警', value: '3' }
]

export const ruleStatusOptions = [
  { label: '停用', value: '0' },
  { label: '启用', value: '1' }
]

export const sourceModuleOptions = [
  { label: '社保', value: 'SOCIAL' },
  { label: '税务', value: 'TAX' },
  { label: '扩面减损', value: 'EXPANSION' },
  { label: '专项治理', value: 'SPECIAL' },
  { label: '设备', value: 'DEVICE' },
  { label: '工伤', value: 'INJURY' }
]

function createDefaultQueryParams() {
  return {
    pageNum: 1,
    pageSize: 10,
    ruleName: undefined,
    sourceModule: undefined,
    ruleStatus: undefined
  }
}

function createDefaultForm() {
  return {
    ruleId: undefined,
    ruleName: undefined,
    warnLevel: '2',
    sourceModule: 'SOCIAL',
    conditionText: undefined,
    pushTargets: '监管员,企业管理员',
    timeoutMinutes: 120,
    upgradeLevel: '3',
    ruleStatus: '1',
    remark: undefined
  }
}

function createRules() {
  return {
    ruleName: [{ required: true, message: '规则名称不能为空', trigger: 'blur' }],
    sourceModule: [{ required: true, message: '来源模块不能为空', trigger: 'change' }]
  }
}

function buildDeleteLabel(ruleIds) {
  return Array.isArray(ruleIds) ? ruleIds.join(',') : ruleIds
}

export function optionLabel(options, value, fallback = '-') {
  const matched = Array.isArray(options) ? options.find(item => item.value === value) : undefined
  return matched ? matched.label : fallback
}

export function formatRatio(value) {
  const number = Number(value || 0)
  return Number.isNaN(number) ? '0.0' : number.toFixed(1)
}

export function valueOrDefault(value, fallback = 0) {
  return value === undefined || value === null ? fallback : value
}

export function useWarningRulePage(options = {}) {
  const {
    exportFilePrefix = 'ygb_warning_rule',
    canMutate = () => true,
    onBlockedAction,
    getCurrentList,
    afterList,
    immediate = true
  } = options

  const { proxy } = getCurrentInstance()

  const warningRuleList = ref([])
  const open = ref(false)
  const detailOpen = ref(false)
  const loading = ref(false)
  const showSearch = ref(true)
  const ids = ref([])
  const single = ref(true)
  const multiple = ref(true)
  const total = ref(0)
  const title = ref('')
  const currentRule = ref(undefined)
  const detailRule = ref(undefined)
  const summaryData = ref({})

  const data = reactive({
    form: createDefaultForm(),
    queryParams: createDefaultQueryParams(),
    rules: createRules()
  })

  const { form, queryParams, rules } = toRefs(data)

  function resolveCurrentList() {
    if (typeof getCurrentList === 'function') {
      const list = getCurrentList()
      if (Array.isArray(list)) {
        return list
      }
    }
    return warningRuleList.value
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
      ruleName: queryParams.value.ruleName,
      sourceModule: queryParams.value.sourceModule,
      ruleStatus: queryParams.value.ruleStatus
    }
  }

  function syncCurrentRule() {
    const currentList = resolveCurrentList()
    if (currentRule.value) {
      const matched = currentList.find(item => item.ruleId === currentRule.value.ruleId)
      if (matched) {
        currentRule.value = matched
        return
      }
    }
    currentRule.value = currentList.length > 0 ? currentList[0] : undefined
  }

  function getList() {
    loading.value = true
    Promise.all([
      listWarningRule(queryParams.value),
      getWarningRuleSummary(buildSummaryQuery())
    ]).then(([listResponse, summaryResponse]) => {
      warningRuleList.value = listResponse.rows || []
      total.value = listResponse.total || 0
      summaryData.value = summaryResponse.data || {}
      if (typeof afterList === 'function') {
        afterList(warningRuleList.value)
      }
      syncCurrentRule()
    }).finally(() => {
      loading.value = false
    })
  }

  function reset() {
    form.value = createDefaultForm()
    proxy.resetForm('ruleRef')
  }

  function cancel() {
    open.value = false
    reset()
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
    ids.value = selection.map(item => item.ruleId)
    single.value = selection.length !== 1
    multiple.value = !selection.length
    if (selection.length === 1) {
      currentRule.value = selection[0]
    }
  }

  function handleRowClick(row) {
    currentRule.value = row
  }

  function handleAdd() {
    if (!guardMutation('新增规则')) {
      return
    }
    reset()
    open.value = true
    title.value = '新增预警规则'
  }

  function handleUpdate(row) {
    if (!guardMutation('修改规则')) {
      return
    }
    const ruleId = row?.ruleId || ids.value[0]
    if (!ruleId) {
      proxy.$modal.msgWarning('请选择一条规则')
      return
    }
    reset()
    getWarningRule(ruleId).then(response => {
      form.value = {
        ...createDefaultForm(),
        ...(response.data || {})
      }
      currentRule.value = response.data || currentRule.value
      open.value = true
      title.value = '修改预警规则'
    })
  }

  function openDetail(row) {
    const target = row || currentRule.value
    if (!target?.ruleId) {
      return
    }
    currentRule.value = target
    getWarningRule(target.ruleId).then(response => {
      detailRule.value = response.data || {}
      detailOpen.value = true
    })
  }

  function submitForm() {
    if (!guardMutation(form.value.ruleId ? '修改规则' : '新增规则')) {
      return
    }
    proxy.$refs.ruleRef.validate(valid => {
      if (!valid) {
        return
      }
      const request = form.value.ruleId ? updateWarningRule(form.value) : addWarningRule(form.value)
      request.then(() => {
        proxy.$modal.msgSuccess(form.value.ruleId ? '修改成功' : '新增成功')
        open.value = false
        getList()
      })
    })
  }

  function handleDelete(row) {
    if (!guardMutation('停用规则')) {
      return
    }
    const ruleIds = row?.ruleId || ids.value
    if (!ruleIds || (Array.isArray(ruleIds) && !ruleIds.length)) {
      proxy.$modal.msgWarning('请选择要停用的规则')
      return
    }
    proxy.$modal.confirm(`是否确认停用预警规则“${buildDeleteLabel(ruleIds)}”？`).then(() => {
      return delWarningRule(ruleIds)
    }).then(() => {
      proxy.$modal.msgSuccess('停用成功')
      getList()
    }).catch(() => {})
  }

  function handleExport() {
    proxy.download('ygb/warning/rule/export', { ...queryParams.value }, `${exportFilePrefix}_${Date.now()}.xlsx`)
  }

  function init() {
    getList()
  }

  if (immediate) {
    init()
  }

  return {
    warningRuleList,
    open,
    detailOpen,
    loading,
    showSearch,
    ids,
    single,
    multiple,
    total,
    title,
    currentRule,
    detailRule,
    summaryData,
    form,
    queryParams,
    rules,
    buildSummaryQuery,
    getList,
    reset,
    cancel,
    handleQuery,
    resetQuery,
    handleSelectionChange,
    handleRowClick,
    handleAdd,
    handleUpdate,
    openDetail,
    submitForm,
    handleDelete,
    handleExport,
    init
  }
}
