import { getCurrentInstance, reactive, ref, toRefs } from 'vue'
import { addEnterprise, delEnterprise, getEnterprise, getEnterpriseSummary, listEnterprise, updateEnterprise } from '@/api/ygb/enterprise'
import { formatRegionName, gdRegionNameMap, gdRegionOptions } from '@/utils/regionName'

export const enterpriseTypeOptions = [
  { label: '派遣单位', value: '1' },
  { label: '用工单位', value: '2' },
  { label: '服务机构', value: '3' },
  { label: '监管单位', value: '4' }
]

export const syncStatusOptions = [
  { label: '未同步', value: '0' },
  { label: '已同步', value: '1' },
  { label: '同步异常', value: '2' }
]

export const regionNameMap = gdRegionNameMap

export const regionOptions = gdRegionOptions

function createDefaultQueryParams() {
  return {
    pageNum: 1,
    pageSize: 10,
    regionCode: undefined,
    enterpriseName: undefined,
    enterpriseCode: undefined,
    enterpriseType: undefined,
    syncStatus: undefined,
    status: undefined
  }
}

function createDefaultForm() {
  return {
    enterpriseId: undefined,
    enterpriseName: undefined,
    enterpriseCode: undefined,
    regionCode: undefined,
    enterpriseType: '1',
    legalPerson: undefined,
    contactPerson: undefined,
    contactPhone: undefined,
    address: undefined,
    establishedDate: undefined,
    syncStatus: '0',
    status: '0',
    remark: undefined
  }
}

function createRules() {
  return {
    enterpriseName: [{ required: true, message: '请输入企业名称', trigger: 'blur' }],
    enterpriseCode: [{ required: true, message: '请输入统一社会信用代码', trigger: 'blur' }],
    enterpriseType: [{ required: true, message: '请选择企业类型', trigger: 'change' }],
    regionCode: [{ required: true, message: '请输入区域编码', trigger: 'blur' }]
  }
}

export function optionLabel(options, value, fallback = '-') {
  const normalizedOptions = Array.isArray(options) ? options : []
  const matched = normalizedOptions.find(item => item.value === value)
  return matched ? matched.label : fallback
}

export { formatRegionName } from '@/utils/regionName'

export function useEnterprisePage(options = {}) {
  const {
    exportFilePrefix = 'ygb_enterprise',
    initialQueryParams = {},
    canMutate = () => true,
    onBlockedAction,
    actionText = {
      add: '新增企业',
      edit: '修改企业',
      delete: '删除企业'
    },
    dialogTitle = {
      add: '新增企业',
      edit: '修改企业'
    },
    messages = {
      addSuccess: '新增成功',
      editSuccess: '修改成功',
      deleteSuccess: '删除成功',
      deleteConfirm: enterpriseNames => `是否确认删除企业“${enterpriseNames}”的数据项？`
    }
  } = options

  const { proxy } = getCurrentInstance()

  const showSearch = ref(true)
  const loading = ref(false)
  const total = ref(0)
  const open = ref(false)
  const detailOpen = ref(false)
  const title = ref('')
  const enterpriseList = ref([])
  const currentEnterprise = ref(undefined)
  const detailEnterprise = ref(undefined)
  const summaryData = ref({})
  const ids = ref([])
  const names = ref([])
  const single = ref(true)
  const multiple = ref(true)

  const data = reactive({
    form: createDefaultForm(),
    queryParams: {
      ...createDefaultQueryParams(),
      ...(initialQueryParams || {})
    },
    rules: createRules()
  })

  const { queryParams, form, rules } = toRefs(data)

  function guardMutation(label) {
    if (canMutate()) {
      return true
    }
    if (typeof onBlockedAction === 'function') {
      onBlockedAction(label)
    }
    return false
  }

  function buildSummaryQuery() {
    return {
      regionCode: queryParams.value.regionCode,
      enterpriseName: queryParams.value.enterpriseName,
      enterpriseCode: queryParams.value.enterpriseCode,
      enterpriseType: queryParams.value.enterpriseType,
      syncStatus: queryParams.value.syncStatus,
      status: queryParams.value.status
    }
  }

  function syncCurrentEnterprise() {
    if (currentEnterprise.value) {
      const matched = enterpriseList.value.find(item => item.enterpriseId === currentEnterprise.value.enterpriseId)
      if (matched) {
        currentEnterprise.value = matched
        return
      }
    }
    currentEnterprise.value = enterpriseList.value.length > 0 ? enterpriseList.value[0] : undefined
  }

  function getList() {
    loading.value = true
    Promise.all([
      listEnterprise(queryParams.value),
      getEnterpriseSummary(buildSummaryQuery())
    ]).then(([listResponse, summaryResponse]) => {
      enterpriseList.value = listResponse.rows || []
      total.value = listResponse.total || 0
      summaryData.value = summaryResponse.data || {}
      syncCurrentEnterprise()
    }).finally(() => {
      loading.value = false
    })
  }

  function reset() {
    form.value = createDefaultForm()
    proxy.resetForm('enterpriseRef')
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
    ids.value = selection.map(item => item.enterpriseId)
    names.value = selection.map(item => item.enterpriseName)
    single.value = selection.length !== 1
    multiple.value = !selection.length
  }

  function handleRowClick(row) {
    currentEnterprise.value = row
  }

  function handleAdd() {
    if (!guardMutation(actionText.add)) {
      return
    }
    reset()
    title.value = dialogTitle.add
    open.value = true
  }

  function handleUpdate(row) {
    if (!guardMutation(actionText.edit)) {
      return
    }
    reset()
    const enterpriseId = row?.enterpriseId || ids.value[0]
    if (!enterpriseId) {
      return
    }
    getEnterprise(enterpriseId).then(response => {
      const source = response.data || {}
      form.value = {
        ...createDefaultForm(),
        ...source,
        enterpriseType: source.enterpriseType || '1',
        syncStatus: source.syncStatus || '0',
        status: source.status || '0'
      }
      title.value = dialogTitle.edit
      open.value = true
    })
  }

  function openDetail(row) {
    const target = row || currentEnterprise.value
    if (!target?.enterpriseId) {
      return
    }
    currentEnterprise.value = target
    getEnterprise(target.enterpriseId).then(response => {
      detailEnterprise.value = response.data || {}
      detailOpen.value = true
    })
  }

  function submitForm() {
    const isEdit = form.value.enterpriseId !== undefined
    if (!guardMutation(isEdit ? actionText.edit : actionText.add)) {
      return
    }
    proxy.$refs.enterpriseRef.validate(valid => {
      if (!valid) {
        return
      }
      const payload = { ...form.value }
      if (isEdit) {
        updateEnterprise(payload).then(() => {
          proxy.$modal.msgSuccess(messages.editSuccess)
          open.value = false
          getList()
        })
        return
      }
      addEnterprise(payload).then(() => {
        proxy.$modal.msgSuccess(messages.addSuccess)
        open.value = false
        getList()
      })
    })
  }

  function handleDelete(row) {
    if (!guardMutation(actionText.delete)) {
      return
    }
    const enterpriseIds = row?.enterpriseId || ids.value
    const enterpriseNames = row?.enterpriseName || names.value.join('、')
    if (!enterpriseIds || (Array.isArray(enterpriseIds) && !enterpriseIds.length)) {
      return
    }
    proxy.$modal.confirm(messages.deleteConfirm(enterpriseNames)).then(() => {
      return delEnterprise(enterpriseIds)
    }).then(() => {
      proxy.$modal.msgSuccess(messages.deleteSuccess)
      getList()
    }).catch(() => {})
  }

  function handleExport() {
    proxy.download('ygb/enterprise/export', { ...queryParams.value }, `${exportFilePrefix}_${new Date().getTime()}.xlsx`)
  }

  reset()
  getList()

  return {
    showSearch,
    loading,
    total,
    open,
    detailOpen,
    title,
    enterpriseList,
    currentEnterprise,
    detailEnterprise,
    summaryData,
    ids,
    names,
    single,
    multiple,
    queryParams,
    form,
    rules,
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
    syncCurrentEnterprise
  }
}
