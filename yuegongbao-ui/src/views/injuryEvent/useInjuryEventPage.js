import { getCurrentInstance, reactive, ref, toRefs } from 'vue'
import {
  addInjuryEvent,
  delInjuryEvent,
  getInjuryEvent,
  getInjuryEventSummary,
  listInjuryEvent,
  updateInjuryEvent,
  updateInjuryStatus
} from '@/api/ygb/injuryEvent'
import { optionselectEnterprise } from '@/api/ygb/enterprise'
import { optionselectPerson } from '@/api/ygb/person'
import { useAuthorizedRegionOptions } from '@/utils/regionScope'
import { gdRegionNameMap, gdRegionOptions } from '@/utils/regionName'

export const injuryStatusOptions = [
  { label: '已报告', value: '0' },
  { label: '认定中', value: '1' },
  { label: '已认定', value: '2' },
  { label: '待遇申领中', value: '3' },
  { label: '已完结', value: '4' }
]

export const warningStatusOptions = [
  { label: '未预警', value: '0' },
  { label: '已预警', value: '1' }
]

export const regionOptions = gdRegionOptions

export const regionNameMap = gdRegionNameMap

function createDefaultQueryParams() {
  return {
    pageNum: 1,
    pageSize: 10,
    regionCode: undefined,
    enterpriseId: undefined,
    personName: undefined,
    injuryStatus: undefined,
    warningStatus: undefined
  }
}

function createDefaultForm() {
  return {
    eventId: undefined,
    personId: undefined,
    personName: undefined,
    enterpriseId: undefined,
    enterpriseName: undefined,
    regionCode: undefined,
    eventDate: undefined,
    reportTime: undefined,
    injuryLocation: undefined,
    injuryPart: undefined,
    diagnosisUrl: undefined,
    injuryStatus: '0',
    approvalDeadline: undefined,
    approvalResult: undefined,
    warningStatus: '0',
    remark: undefined
  }
}

function createStatusForm() {
  return {
    injuryStatus: undefined,
    approvalResult: undefined
  }
}

function createRules() {
  return {
    personId: [{ required: true, message: '人员不能为空', trigger: 'change' }],
    enterpriseId: [{ required: true, message: '企业不能为空', trigger: 'change' }]
  }
}

function createStatusRules() {
  return {
    injuryStatus: [{ required: true, message: '事件状态不能为空', trigger: 'change' }]
  }
}

function normalizeEventForm(source = {}) {
  return {
    ...createDefaultForm(),
    ...source,
    eventId: source.eventId ?? undefined,
    injuryStatus: String(source.injuryStatus ?? '0'),
    warningStatus: String(source.warningStatus ?? '0')
  }
}

export function injuryStatusLabel(value) {
  return injuryStatusOptions.find(item => item.value === String(value))?.label || '-'
}

function injuryStatusOrder(value) {
  const normalized = String(value ?? '0')
  const index = injuryStatusOptions.findIndex(item => item.value === normalized)
  return index >= 0 ? index : -1
}

function validateInjuryStatusTransition(currentStatus, targetStatus, approvalResult) {
  const current = injuryStatusOrder(currentStatus)
  const target = injuryStatusOrder(targetStatus)
  if (target < 0) {
    return '事件状态不合法'
  }
  if (target < current) {
    return '工伤事件状态不能回退'
  }
  if (target - current > 1) {
    return '工伤事件状态只能按办理链路逐步流转'
  }
  if (target >= 2 && !String(approvalResult || '').trim()) {
    return '进入已认定、待遇申领或已完结状态时，审批结果不能为空'
  }
  return ''
}

function resolveStatusTransitionOptions(currentStatus) {
  const current = injuryStatusOrder(currentStatus)
  if (current < 0) {
    return injuryStatusOptions.slice(0, 1)
  }
  return injuryStatusOptions.filter((_, index) => index === current || index === current + 1)
}

export function warningStatusLabel(value) {
  return warningStatusOptions.find(item => item.value === String(value))?.label || '-'
}

export function formatRegionName(code) {
  if (!code) {
    return '-'
  }
  return regionNameMap[code] || code
}

export function remainingDaysText(value) {
  if (value === undefined || value === null || value === '') {
    return '-'
  }
  return `${value} 天`
}

export function valueOrDefault(value, fallback) {
  return value === undefined || value === null ? fallback : value
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

export function useInjuryEventPage(options = {}) {
  const {
    exportFilePrefix = 'ygb_injury_event',
    initialQueryParams = {},
    resetQueryPatch = () => initialQueryParams,
    canMutate = () => true,
    onBlockedAction,
    getCurrentList,
    onAfterLoad
  } = options
  const { proxy } = getCurrentInstance()
  const authorizedRegionOptions = useAuthorizedRegionOptions(regionOptions)

  const injuryEventList = ref([])
  const enterpriseOptions = ref([])
  const personOptions = ref([])
  const formPersonOptions = ref([])
  const open = ref(false)
  const statusOpen = ref(false)
  const detailOpen = ref(false)
  const loading = ref(false)
  const showSearch = ref(true)
  const ids = ref([])
  const single = ref(true)
  const multiple = ref(true)
  const total = ref(0)
  const title = ref('')
  const currentEvent = ref(undefined)
  const detailEvent = ref(undefined)
  const summaryData = ref({})
  const statusTransitionOptions = ref(resolveStatusTransitionOptions('0'))

  const data = reactive({
    queryParams: {
      ...createDefaultQueryParams(),
      ...initialQueryParams
    },
    form: createDefaultForm(),
    statusForm: createStatusForm(),
    rules: createRules(),
    statusRules: createStatusRules()
  })

  const { queryParams, form, statusForm, rules, statusRules } = toRefs(data)

  function resolveCurrentList() {
    if (typeof getCurrentList === 'function') {
      const list = getCurrentList()
      if (Array.isArray(list)) {
        return list
      }
    }
    return injuryEventList.value
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
      regionCode: queryParams.value.regionCode,
      enterpriseId: queryParams.value.enterpriseId,
      personName: queryParams.value.personName,
      injuryStatus: queryParams.value.injuryStatus,
      warningStatus: queryParams.value.warningStatus
    }
  }

  function resolveResetQueryPatch() {
    const patch = typeof resetQueryPatch === 'function' ? resetQueryPatch() : resetQueryPatch
    return patch && typeof patch === 'object' ? patch : {}
  }

  function syncCurrentEvent() {
    const currentList = resolveCurrentList()
    if (currentEvent.value) {
      const matched = currentList.find(item => item.eventId === currentEvent.value.eventId)
      if (matched) {
        currentEvent.value = matched
        return
      }
    }
    currentEvent.value = currentList.length > 0 ? currentList[0] : undefined
  }

  function getList() {
    loading.value = true
    return Promise.all([
      listInjuryEvent(queryParams.value),
      getInjuryEventSummary(buildSummaryQuery())
    ]).then(([listResponse, summaryResponse]) => {
      injuryEventList.value = listResponse.rows || []
      total.value = listResponse.total || 0
      summaryData.value = summaryResponse.data || {}
      syncCurrentEvent()
      if (typeof onAfterLoad === 'function') {
        return Promise.resolve(onAfterLoad({
          list: injuryEventList.value,
          total: total.value,
          summary: summaryData.value,
          query: buildSummaryQuery()
        }))
      }
      return undefined
    }).finally(() => {
      loading.value = false
    })
  }

  function loadEnterpriseOptions() {
    optionselectEnterprise().then(response => {
      enterpriseOptions.value = response.data || []
    })
  }

  function loadPersonOptions(query = {}) {
    optionselectPerson(query).then(response => {
      const list = response.data || []
      if (query.enterpriseId) {
        formPersonOptions.value = list
        return
      }
      personOptions.value = list
      formPersonOptions.value = form.value.enterpriseId ? formPersonOptions.value : list
    })
  }

  function reset() {
    form.value = createDefaultForm()
    statusForm.value = createStatusForm()
    formPersonOptions.value = personOptions.value.slice()
  }

  function cancel() {
    open.value = false
    reset()
    proxy.resetForm('injuryRef')
  }

  function handleQuery() {
    queryParams.value.pageNum = 1
    getList()
  }

  function resetQuery() {
    proxy.resetForm('queryRef')
    Object.assign(queryParams.value, createDefaultQueryParams(), resolveResetQueryPatch())
    getList()
  }

  function handleSelectionChange(selection) {
    ids.value = selection.map(item => item.eventId)
    single.value = selection.length !== 1
    multiple.value = selection.length === 0
  }

  function handleRowClick(row) {
    currentEvent.value = row
  }

  function handleAdd() {
    if (!guardMutation('新增工伤事件')) {
      return
    }
    reset()
    title.value = '新增工伤事件'
    open.value = true
  }

  function handleUpdate(row) {
    if (!guardMutation('修改工伤事件')) {
      return
    }
    const eventId = row?.eventId || ids.value[0]
    if (!eventId) {
      return
    }
    currentEvent.value = row || resolveCurrentList().find(item => item.eventId === eventId) || currentEvent.value
    reset()
    getInjuryEvent(eventId).then(response => {
      form.value = normalizeEventForm(response.data || {})
      if (form.value.enterpriseId) {
        loadPersonOptions({ enterpriseId: form.value.enterpriseId })
      }
      title.value = '修改工伤事件'
      open.value = true
    })
  }

  function openDetail(row) {
    const target = row || currentEvent.value
    if (!target?.eventId) {
      return
    }
    getInjuryEvent(target.eventId).then(response => {
      detailEvent.value = response.data || target
      detailOpen.value = true
    })
  }

  function handleEnterpriseChange(value) {
    form.value.enterpriseId = value
    form.value.personId = undefined
    form.value.personName = undefined
    const enterprise = enterpriseOptions.value.find(item => item.enterpriseId === value)
    if (enterprise) {
      form.value.enterpriseName = enterprise.enterpriseName
      form.value.regionCode = enterprise.regionCode || enterprise.areaCode || form.value.regionCode
    }
    if (value) {
      loadPersonOptions({ enterpriseId: value })
      return
    }
    formPersonOptions.value = personOptions.value.slice()
  }

  function submitForm() {
    proxy.$refs.injuryRef.validate(valid => {
      if (!valid) {
        return
      }
      const selectedPerson = formPersonOptions.value.find(item => item.personId === form.value.personId)
      if (selectedPerson) {
        form.value.personName = selectedPerson.personName
        form.value.enterpriseName = form.value.enterpriseName || selectedPerson.enterpriseName
      }
      const currentStatus = form.value.eventId ? (currentEvent.value?.injuryStatus ?? '0') : '0'
      const transitionError = form.value.eventId
        ? validateInjuryStatusTransition(currentStatus, form.value.injuryStatus, form.value.approvalResult)
        : form.value.injuryStatus === '0' ? '' : '新增工伤事件只能从已报告状态开始'
      if (transitionError) {
        proxy.$modal.msgWarning(transitionError)
        return
      }
      const request = form.value.eventId ? updateInjuryEvent(form.value) : addInjuryEvent(form.value)
      request.then(() => {
        proxy.$modal.msgSuccess(form.value.eventId ? '修改成功' : '新增成功')
        open.value = false
        getList()
      })
    })
  }

  function openStatusDialog(row) {
    if (!guardMutation('流转工伤事件')) {
      return
    }
    const target = row || currentEvent.value
    if (!target?.eventId) {
      return
    }
    currentEvent.value = target
    statusTransitionOptions.value = resolveStatusTransitionOptions(target.injuryStatus)
    statusForm.value = {
      injuryStatus: String(target.injuryStatus ?? '0'),
      approvalResult: target.approvalResult
    }
    statusOpen.value = true
  }

  function submitStatus() {
    proxy.$refs.statusRef.validate(valid => {
      if (!valid || !currentEvent.value?.eventId) {
        return
      }
      const transitionError = validateInjuryStatusTransition(
        currentEvent.value.injuryStatus,
        statusForm.value.injuryStatus,
        statusForm.value.approvalResult
      )
      if (transitionError) {
        proxy.$modal.msgWarning(transitionError)
        return
      }
      updateInjuryStatus(currentEvent.value.eventId, statusForm.value).then(() => {
        proxy.$modal.msgSuccess('流转成功')
        statusOpen.value = false
        getList()
      })
    })
  }

  function handleDelete(row) {
    if (!guardMutation('删除工伤事件')) {
      return
    }
    const targetIds = row?.eventId ? [row.eventId] : ids.value.slice()
    if (!targetIds.length) {
      return
    }
    proxy.$modal.confirm(`是否确认删除工伤事件编号为“${targetIds.join(',')}”的数据项？`).then(() => {
      return Promise.all(targetIds.map(eventId => delInjuryEvent(eventId)))
    }).then(() => {
      proxy.$modal.msgSuccess('删除成功')
      getList()
    }).catch(() => {})
  }

  function handleExport() {
    proxy.download('ygb/injury/event/export', { ...queryParams.value }, `${exportFilePrefix}_${new Date().getTime()}.xlsx`)
  }

  loadEnterpriseOptions()
  loadPersonOptions()
  reset()
  getList()

  return {
    injuryEventList,
    enterpriseOptions,
    formPersonOptions,
    open,
    statusOpen,
    detailOpen,
    loading,
    showSearch,
    ids,
    single,
    multiple,
    total,
    title,
    currentEvent,
    detailEvent,
    summaryData,
    queryParams,
    form,
    statusForm,
    statusTransitionOptions,
    rules,
    statusRules,
    regionOptions: authorizedRegionOptions,
    getList,
    syncCurrentEvent,
    cancel,
    handleQuery,
    resetQuery,
    handleSelectionChange,
    handleRowClick,
    handleAdd,
    handleUpdate,
    openDetail,
    handleEnterpriseChange,
    submitForm,
    openStatusDialog,
    submitStatus,
    handleDelete,
    handleExport
  }
}
