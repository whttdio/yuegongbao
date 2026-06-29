import { getCurrentInstance, reactive, ref, toRefs } from 'vue'
import {
  addPreventionProject,
  delPreventionProject,
  getPreventionProject,
  getPreventionProjectSummary,
  listPreventionProject,
  updatePreventionProject
} from '@/api/ygb/preventionProject'
import { optionselectEnterprise } from '@/api/ygb/enterprise'
import { gdRegionNameMap, gdRegionOptions } from '@/utils/regionName'

export const projectTypeOptions = [
  { label: '宣传', value: '1' },
  { label: '培训', value: '2' },
  { label: 'AI建设', value: '3' },
  { label: '隐患排查', value: '4' }
]

export const projectStatusOptions = [
  { label: '申报', value: '0' },
  { label: '立项', value: '1' },
  { label: '实施', value: '2' },
  { label: '验收', value: '3' },
  { label: '结项', value: '4' }
]

export const regionOptions = gdRegionOptions

export const regionNameMap = gdRegionNameMap

function createDefaultQueryParams() {
  return {
    pageNum: 1,
    pageSize: 10,
    projectName: undefined,
    regionCode: undefined,
    enterpriseId: undefined,
    projectType: undefined,
    projectStatus: undefined
  }
}

function createDefaultForm() {
  return {
    projectId: undefined,
    projectName: undefined,
    projectType: '1',
    enterpriseId: undefined,
    enterpriseName: undefined,
    regionCode: undefined,
    budgetAmount: 0,
    actualAmount: 0,
    startDate: undefined,
    endDate: undefined,
    projectStatus: '0',
    evaluationScore: 80,
    evaluationReport: undefined,
    remark: undefined
  }
}

function createRules() {
  return {
    projectName: [{ required: true, message: '项目名称不能为空', trigger: 'blur' }],
    projectType: [{ required: true, message: '项目类型不能为空', trigger: 'change' }],
    enterpriseId: [{ required: true, message: '企业不能为空', trigger: 'change' }]
  }
}

export function projectTypeLabel(value) {
  return projectTypeOptions.find(item => item.value === String(value))?.label || '-'
}

export function projectStatusLabel(value) {
  return projectStatusOptions.find(item => item.value === String(value))?.label || '-'
}

export function formatRegionName(code, fallback = '-') {
  if (!code) {
    return fallback
  }
  return regionNameMap[code] || code
}

export function formatMoney(value) {
  const number = Number(value || 0)
  if (Number.isNaN(number)) {
    return '0.00'
  }
  return number.toFixed(2)
}

export function scoreText(value) {
  if (value === undefined || value === null || value === '') {
    return '-'
  }
  return String(value)
}

export function valueOrDefault(value, fallback) {
  return value === undefined || value === null ? fallback : value
}

export function isHighBudget(project) {
  return Number(project?.budgetAmount || 0) >= 50000
}

export function isLowScore(value) {
  const score = Number(value || 0)
  return score > 0 && score < 80
}

export function isTrainingAiProject(value) {
  return String(value) === '2' || String(value) === '3'
}

export function isActiveProject(value) {
  return ['1', '2', '3'].includes(String(value))
}

export function isProjectDeleteAllowed(project) {
  return String(project?.projectStatus ?? '0') === '0'
}

export function nextProjectStatusOptions(project) {
  const status = String(project?.projectStatus ?? '0')
  const allowedMap = {
    0: ['0', '1'],
    1: ['1', '2'],
    2: ['2', '3'],
    3: ['3', '4'],
    4: ['4']
  }
  const allowed = allowedMap[status] || ['0']
  return projectStatusOptions.filter(item => allowed.includes(item.value))
}

export function matchProjectFocus(project, focusKey) {
  if (!focusKey || focusKey === 'all') {
    return true
  }
  if (focusKey === 'highBudget') {
    return isHighBudget(project)
  }
  if (focusKey === 'acceptance') {
    return String(project?.projectStatus) === '3'
  }
  if (focusKey === 'lowScore') {
    return isLowScore(project?.evaluationScore)
  }
  if (focusKey === 'trainingAi') {
    return isTrainingAiProject(project?.projectType)
  }
  if (focusKey === 'active') {
    return isActiveProject(project?.projectStatus)
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

export function usePreventionProjectPage(options = {}) {
  const {
    exportFilePrefix = 'ygb_prevention_project',
    initialQueryParams = {},
    canMutate = () => true,
    onBlockedAction,
    getCurrentList,
    afterList
  } = options
  const { proxy } = getCurrentInstance()

  const projectList = ref([])
  const enterpriseOptions = ref([])
  const open = ref(false)
  const detailOpen = ref(false)
  const loading = ref(false)
  const showSearch = ref(true)
  const ids = ref([])
  const single = ref(true)
  const multiple = ref(true)
  const total = ref(0)
  const title = ref('')
  const currentProject = ref(undefined)
  const detailProject = ref(undefined)
  const summaryData = ref({})
  const editableProjectStatusOptions = ref(nextProjectStatusOptions())

  const data = reactive({
    form: createDefaultForm(),
    queryParams: {
      ...createDefaultQueryParams(),
      ...(initialQueryParams || {})
    },
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
    return projectList.value
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
      projectName: queryParams.value.projectName,
      regionCode: queryParams.value.regionCode,
      enterpriseId: queryParams.value.enterpriseId,
      projectType: queryParams.value.projectType,
      projectStatus: queryParams.value.projectStatus
    }
  }

  function syncCurrentProject() {
    const currentList = resolveCurrentList()
    if (currentProject.value) {
      const matched = currentList.find(item => item.projectId === currentProject.value.projectId)
      if (matched) {
        currentProject.value = matched
        return
      }
    }
    currentProject.value = currentList.length > 0 ? currentList[0] : undefined
  }

  function getList() {
    loading.value = true
    Promise.all([
      listPreventionProject(queryParams.value),
      getPreventionProjectSummary(buildSummaryQuery())
    ]).then(([listResponse, summaryResponse]) => {
      projectList.value = listResponse.rows || []
      total.value = listResponse.total || 0
      summaryData.value = summaryResponse.data || {}
      if (typeof afterList === 'function') {
        afterList()
      }
      syncCurrentProject()
    }).finally(() => {
      loading.value = false
    })
  }

  function loadEnterpriseOptions() {
    optionselectEnterprise().then(response => {
      enterpriseOptions.value = response.data || []
    })
  }

  function reset() {
    form.value = createDefaultForm()
    editableProjectStatusOptions.value = nextProjectStatusOptions()
    proxy.resetForm('projectRef')
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
    ids.value = selection.map(item => item.projectId)
    single.value = selection.length !== 1
    multiple.value = !selection.length
  }

  function handleRowClick(row) {
    currentProject.value = row
  }

  function handleAdd() {
    if (!guardMutation('新增项目')) {
      return
    }
    reset()
    editableProjectStatusOptions.value = nextProjectStatusOptions()
    open.value = true
    title.value = '新增预防项目'
  }

  function handleUpdate(row) {
    if (!guardMutation('修改项目')) {
      return
    }
    const projectId = row?.projectId || ids.value[0]
    if (!projectId) {
      proxy.$modal.msgWarning('请选择一个项目')
      return
    }
    reset()
    getPreventionProject(projectId).then(response => {
      form.value = {
        ...createDefaultForm(),
        ...(response.data || {})
      }
      editableProjectStatusOptions.value = nextProjectStatusOptions(response.data || {})
      currentProject.value = response.data || currentProject.value
      open.value = true
      title.value = '修改预防项目'
    })
  }

  function openDetail(row) {
    const target = row || currentProject.value
    if (!target?.projectId) {
      proxy.$modal.msgWarning('请选择一个项目')
      return
    }
    currentProject.value = target
    getPreventionProject(target.projectId).then(response => {
      detailProject.value = response.data || {}
      detailOpen.value = true
    })
  }

  function handleEnterpriseChange(value) {
    const current = enterpriseOptions.value.find(item => item.enterpriseId === value)
    form.value.enterpriseName = current ? current.enterpriseName : undefined
    form.value.regionCode = current ? current.regionCode : undefined
  }

  function submitForm() {
    if (!guardMutation('维护项目')) {
      return
    }
    proxy.$refs.projectRef.validate(valid => {
      if (!valid) {
        return
      }
      const current = enterpriseOptions.value.find(item => item.enterpriseId === form.value.enterpriseId)
      if (current) {
        form.value.enterpriseName = current.enterpriseName
        form.value.regionCode = current.regionCode
      }
      if (['2', '3', '4'].includes(String(form.value.projectStatus || '')) && (!form.value.startDate || !form.value.endDate)) {
        proxy.$modal.msgWarning('进入实施、验收或结项阶段前必须填写计划起止日期')
        return
      }
      if (String(form.value.projectStatus || '') === '4' && (!form.value.evaluationScore || !form.value.evaluationReport)) {
        proxy.$modal.msgWarning('结项前必须填写评价分和评价报告')
        return
      }
      const request = form.value.projectId ? updatePreventionProject(form.value) : addPreventionProject(form.value)
      request.then(() => {
        proxy.$modal.msgSuccess(form.value.projectId ? '修改成功' : '新增成功')
        open.value = false
        getList()
      })
    })
  }

  function handleDelete(row) {
    if (!guardMutation('删除项目')) {
      return
    }
    const projectIds = row?.projectId || ids.value
    if (row && !isProjectDeleteAllowed(row)) {
      proxy.$modal.msgWarning('已立项、实施、验收或结项的预防项目不允许删除')
      return
    }
    if (!row) {
      const currentList = resolveCurrentList()
      const selectedRows = currentList.filter(item => ids.value.includes(item.projectId))
      if (selectedRows.some(item => !isProjectDeleteAllowed(item))) {
        proxy.$modal.msgWarning('已选择项目中包含已推进项目，请仅删除申报状态项目')
        return
      }
    }
    if (!projectIds || (Array.isArray(projectIds) && projectIds.length === 0)) {
      proxy.$modal.msgWarning('请选择要删除的项目')
      return
    }
    const idText = Array.isArray(projectIds) ? projectIds.join(',') : projectIds
    proxy.$modal.confirm(`是否确认删除预防项目“${idText}”？`).then(function() {
      return delPreventionProject(projectIds)
    }).then(() => {
      getList()
      proxy.$modal.msgSuccess('删除成功')
    }).catch(() => {})
  }

  function handleExport() {
    proxy.download('ygb/injury/prevention/export', { ...queryParams.value }, `${exportFilePrefix}_${new Date().getTime()}.xlsx`)
  }

  function formatDate(value) {
    if (!value) {
      return '-'
    }
    return proxy.parseTime(value, '{y}-{m}-{d}')
  }

  loadEnterpriseOptions()
  reset()
  getList()

  return {
    projectList,
    enterpriseOptions,
    open,
    detailOpen,
    loading,
    showSearch,
    ids,
    single,
    multiple,
    total,
    title,
    currentProject,
    detailProject,
    summaryData,
    editableProjectStatusOptions,
    queryParams,
    form,
    rules,
    getList,
    syncCurrentProject,
    reset,
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
    handleDelete,
    handleExport,
    formatDate
  }
}
