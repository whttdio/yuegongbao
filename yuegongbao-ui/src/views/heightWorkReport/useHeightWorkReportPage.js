import { getCurrentInstance, reactive, ref, toRefs } from 'vue'
import {
  addHeightWorkReport,
  finishHeightWorkReport,
  getHeightWorkReport,
  getHeightWorkReportSummary,
  getHeightWorkVoucher,
  listHeightWorkReport,
  updateHeightWorkReport
} from '@/api/ygb/heightWorkReport'
import { optionselectEnterprise } from '@/api/ygb/enterprise'
import { useAuthorizedRegionOptions } from '@/utils/regionScope'
import { gdRegionNameMap, gdRegionOptions } from '@/utils/regionName'

export const reporterTypeOptions = [
  { label: '企业', value: '1' },
  { label: '个人', value: '2' },
  { label: '第三方导入', value: '3' }
]

export const reportStatusOptions = [
  { label: '已报备', value: '0' },
  { label: '已结束', value: '1' }
]

export const certStatusOptions = [
  { label: '全部有效', value: '0' },
  { label: '部分失效', value: '1' },
  { label: '全部失效', value: '2' }
]

export const regionOptions = gdRegionOptions

export const regionNameMap = gdRegionNameMap

export const safetyMeasureOptions = ['已佩戴安全带', '已设置安全网', '已配置监护人', '已进行安全交底', '天气条件允许']

function createDefaultQueryParams() {
  return {
    pageNum: 1,
    pageSize: 10,
    reportNo: undefined,
    regionCode: undefined,
    enterpriseId: undefined,
    reportStatus: undefined,
    certValidStatus: undefined,
    startTimeBegin: undefined,
    startTimeEnd: undefined
  }
}

function createDefaultWorker() {
  return {
    workerName: undefined,
    idCard: undefined,
    certNo: undefined,
    certPhotoUrl: undefined
  }
}

function createDefaultForm() {
  return {
    reportId: undefined,
    reportNo: undefined,
    enterpriseId: undefined,
    enterpriseName: undefined,
    reporterType: '1',
    applicantName: undefined,
    applicantPhone: undefined,
    workLocation: undefined,
    longitude: undefined,
    latitude: undefined,
    startTime: undefined,
    endTime: undefined,
    workHeightM: 2,
    workerCount: 1,
    guardianName: undefined,
    guardianPhone: undefined,
    safetyMeasures: ['已配置监护人'],
    workerList: [createDefaultWorker()],
    remark: undefined
  }
}

function createRules() {
  return {
    reporterType: [{ required: true, message: '请选择报备主体', trigger: 'change' }],
    enterpriseId: [{ required: true, message: '请选择作业单位', trigger: 'change' }],
    enterpriseName: [{ required: true, message: '请输入作业单位或个人', trigger: 'blur' }],
    applicantName: [{ required: true, message: '请输入填报人姓名', trigger: 'blur' }],
    applicantPhone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }],
    workLocation: [{ required: true, message: '请输入作业地点', trigger: 'blur' }],
    longitude: [{ required: true, message: '请输入经度', trigger: 'blur' }],
    latitude: [{ required: true, message: '请输入纬度', trigger: 'blur' }],
    startTime: [{ required: true, message: '请选择计划开始时间', trigger: 'change' }],
    endTime: [{ required: true, message: '请选择计划结束时间', trigger: 'change' }],
    workHeightM: [{ required: true, message: '请输入作业高度', trigger: 'blur' }],
    guardianName: [{ required: true, message: '请输入监护人姓名', trigger: 'blur' }],
    guardianPhone: [{ required: true, message: '请输入监护人电话', trigger: 'blur' }],
    safetyMeasures: [{ required: true, message: '请至少选择一项安全措施', trigger: 'change' }]
  }
}

function createFinishRules() {
  return {
    actualEndTime: [{ required: true, message: '请选择实际结束时间', trigger: 'change' }]
  }
}

export function useHeightWorkReportPage(options = {}) {
  const {
    exportFilePrefix = 'ygb_height_work_report',
    canMutate = () => true,
    onBlockedAction,
    getCurrentList,
    afterList
  } = options
  const { proxy } = getCurrentInstance()
  const authorizedRegionOptions = useAuthorizedRegionOptions(regionOptions)

  const showSearch = ref(true)
  const loading = ref(false)
  const total = ref(0)
  const open = ref(false)
  const finishOpen = ref(false)
  const detailOpen = ref(false)
  const title = ref('')
  const reportList = ref([])
  const enterpriseOptions = ref([])
  const summaryData = ref({})
  const detail = ref(undefined)
  const voucher = ref(undefined)
  const currentReportId = ref(undefined)
  const currentReport = ref(undefined)

  const data = reactive({
    queryParams: createDefaultQueryParams(),
    form: createDefaultForm(),
    finishForm: {
      actualEndTime: undefined,
      endPhotoUrl: undefined
    },
    rules: createRules(),
    finishRules: createFinishRules()
  })

  const { queryParams, form, rules, finishForm, finishRules } = toRefs(data)

  function resolveCurrentList() {
    if (typeof getCurrentList === 'function') {
      const list = getCurrentList()
      if (Array.isArray(list)) {
        return list
      }
    }
    return reportList.value
  }

  function guardAction(actionLabel) {
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
      reportNo: queryParams.value.reportNo,
      regionCode: queryParams.value.regionCode,
      enterpriseId: queryParams.value.enterpriseId,
      reportStatus: queryParams.value.reportStatus,
      certValidStatus: queryParams.value.certValidStatus,
      startTimeBegin: queryParams.value.startTimeBegin,
      startTimeEnd: queryParams.value.startTimeEnd
    }
  }

  function syncCurrentReport() {
    const currentList = resolveCurrentList()
    if (currentReport.value) {
      const matched = currentList.find(item => item.reportId === currentReport.value.reportId)
      if (matched) {
        currentReport.value = matched
        return
      }
    }
    currentReport.value = currentList.length > 0 ? currentList[0] : undefined
  }

  function getList() {
    loading.value = true
    Promise.all([
      listHeightWorkReport(queryParams.value),
      getHeightWorkReportSummary(buildSummaryQuery())
    ]).then(([listResponse, summaryResponse]) => {
      reportList.value = listResponse.rows || []
      total.value = listResponse.total || 0
      summaryData.value = summaryResponse.data || {}
      if (typeof afterList === 'function') {
        afterList()
      }
      syncCurrentReport()
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
    voucher.value = undefined
  }

  function defaultWorker() {
    return createDefaultWorker()
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

  function handleRowClick(row) {
    currentReport.value = row
  }

  function handleAdd() {
    if (!guardAction('新增报备')) {
      return
    }
    reset()
    title.value = '新增高处作业报备'
    open.value = true
  }

  function normalizeForm(source = {}) {
    return {
      reportId: source.reportId,
      reportNo: source.reportNo,
      enterpriseId: source.enterpriseId && source.enterpriseId !== 0 ? source.enterpriseId : undefined,
      enterpriseName: source.enterpriseName,
      reporterType: source.reporterType || '1',
      applicantName: source.applicantName,
      applicantPhone: source.applicantPhone,
      workLocation: source.workLocation,
      longitude: source.longitude,
      latitude: source.latitude,
      startTime: source.startTime,
      endTime: source.endTime,
      workHeightM: source.workHeightM,
      workerCount: source.workerCount,
      guardianName: source.guardianName,
      guardianPhone: source.guardianPhone,
      safetyMeasures: source.safetyMeasures || [],
      workerList: (source.workerList && source.workerList.length ? source.workerList : [createDefaultWorker()]).map(item => ({
        rowId: item.rowId,
        workerName: item.workerName,
        idCard: item.idCard,
        certNo: item.certNo,
        certPhotoUrl: item.certPhotoUrl
      })),
      remark: source.remark
    }
  }

  function handleUpdate(row) {
    if (!guardAction('修改报备')) {
      return
    }
    reset()
    getHeightWorkReport(row.reportId).then(response => {
      form.value = normalizeForm(response.data)
      currentReport.value = response.data || currentReport.value
      title.value = '修改高处作业报备'
      open.value = true
    })
  }

  function loadDetail(reportId, showDrawer = false, loadVoucher = false) {
    getHeightWorkReport(reportId).then(response => {
      detail.value = response.data
      if (loadVoucher) {
        getHeightWorkVoucher(reportId).then(voucherResponse => {
          voucher.value = voucherResponse.data
        })
      } else {
        voucher.value = undefined
      }
      if (showDrawer) {
        detailOpen.value = true
      }
    })
  }

  function openDetail(row) {
    currentReport.value = row
    loadDetail(row.reportId, true, false)
  }

  function openVoucher(row) {
    currentReport.value = row
    loadDetail(row.reportId, true, true)
  }

  function handleReporterTypeChange(value) {
    if (value === '1') {
      form.value.enterpriseName = undefined
      return
    }
    form.value.enterpriseId = undefined
  }

  function handleEnterpriseChange(value) {
    const current = enterpriseOptions.value.find(item => item.enterpriseId === value)
    form.value.enterpriseName = current ? current.enterpriseName : undefined
  }

  function addWorker() {
    if (!guardAction('新增作业人员')) {
      return
    }
    form.value.workerList.push(createDefaultWorker())
  }

  function removeWorker(index) {
    if (!guardAction('删除作业人员')) {
      return
    }
    if (form.value.workerList.length === 1) {
      proxy.$modal.msgWarning('至少保留一名作业人员')
      return
    }
    form.value.workerList.splice(index, 1)
  }

  function validateWorkers() {
    const invalid = form.value.workerList.some(item => !item.workerName || !item.idCard || !item.certNo)
    if (invalid) {
      proxy.$modal.msgWarning('请完整填写作业人员姓名、身份证号和证书编号')
      return false
    }
    return true
  }

  function buildPayload() {
    const selectedEnterprise = enterpriseOptions.value.find(item => item.enterpriseId === form.value.enterpriseId)
    return {
      ...form.value,
      enterpriseId: form.value.reporterType === '1' ? form.value.enterpriseId : 0,
      enterpriseName: form.value.reporterType === '1'
        ? ((selectedEnterprise && selectedEnterprise.enterpriseName) || form.value.enterpriseName)
        : form.value.enterpriseName,
      workerCount: form.value.workerList.length,
      workerList: form.value.workerList.map((item, index) => ({
        ...item,
        sortOrder: index + 1
      }))
    }
  }

  function submitForm() {
    if (!guardAction('提交报备')) {
      return
    }
    proxy.$refs.reportRef.validate(valid => {
      if (!valid || !validateWorkers()) {
        return
      }
      const payload = buildPayload()
      const request = payload.reportId ? updateHeightWorkReport(payload) : addHeightWorkReport(payload)
      request.then(response => {
        proxy.$modal.msgSuccess(response.msg || (payload.reportId ? '修改成功' : '新增成功'))
        open.value = false
        getList()
      })
    })
  }

  function handleFinish(row) {
    if (!guardAction('结束作业')) {
      return
    }
    currentReport.value = row
    currentReportId.value = row.reportId
    finishForm.value = {
      actualEndTime: row.endTime,
      endPhotoUrl: undefined
    }
    finishOpen.value = true
  }

  function submitFinish() {
    if (!guardAction('提交结束登记')) {
      return
    }
    proxy.$refs.finishRef.validate(valid => {
      if (!valid) {
        return
      }
      finishHeightWorkReport(currentReportId.value, finishForm.value).then(response => {
        proxy.$modal.msgSuccess(response.msg || '作业结束已登记')
        finishOpen.value = false
        getList()
        if (detailOpen.value && detail.value?.reportId === currentReportId.value) {
          loadDetail(currentReportId.value, false, Boolean(voucher.value))
        }
      })
    })
  }

  function handleExport() {
    proxy.download('ygb/heightWork/report/export', { ...queryParams.value }, `${exportFilePrefix}_${new Date().getTime()}.xlsx`)
  }

  function initialize() {
    loadEnterpriseOptions()
    reset()
    getList()
  }

  return {
    proxy,
    showSearch,
    loading,
    total,
    open,
    finishOpen,
    detailOpen,
    title,
    reportList,
    enterpriseOptions,
    summaryData,
    detail,
    voucher,
    currentReportId,
    currentReport,
    reporterTypeOptions,
    reportStatusOptions,
    certStatusOptions,
    regionOptions: authorizedRegionOptions,
    regionNameMap,
    safetyMeasureOptions,
    queryParams,
    form,
    rules,
    finishForm,
    finishRules,
    buildSummaryQuery,
    syncCurrentReport,
    getList,
    loadEnterpriseOptions,
    reset,
    defaultWorker,
    handleQuery,
    resetQuery,
    handleRowClick,
    handleAdd,
    handleUpdate,
    openDetail,
    openVoucher,
    loadDetail,
    normalizeForm,
    handleReporterTypeChange,
    handleEnterpriseChange,
    addWorker,
    removeWorker,
    validateWorkers,
    buildPayload,
    submitForm,
    handleFinish,
    submitFinish,
    handleExport,
    initialize
  }
}

export function reportStatusLabel(value) {
  return reportStatusOptions.find(item => item.value === value)?.label || '-'
}

export function certStatusLabel(value) {
  return certStatusOptions.find(item => item.value === value)?.label || '-'
}

export function sourceModeLabel(value) {
  const upper = String(value || '').toUpperCase()
  if (!upper) return '-'
  if (upper === 'PC') return 'PC录入'
  if (upper.includes('SYSTEM')) return '系统生成'
  if (upper.includes('STUB')) return '接口同步'
  if (upper.includes('IMPORT')) return '导入'
  if (upper.includes('THIRD')) return '第三方同步'
  return value
}

export function buildBaseHintTags(report) {
  if (!report) {
    return [{ label: '未选中报备，可先在列表中选择待办理对象', type: 'info' }]
  }
  const tags = []
  if (String(report.reportStatus || '') === '0') {
    tags.push({ label: '当前未结束，建议作业完成后及时回写结束留痕', type: 'warning' })
  }
  if (String(report.reportStatus || '') === '1') {
    tags.push({ label: '当前已结束，可作为归档和统计口径直接使用', type: 'success' })
  }
  if (String(report.certValidStatus || '') === '2') {
    tags.push({ label: '全部证书异常，建议优先保留核查说明和现场处置结果', type: 'danger' })
  }
  if (String(report.certValidStatus || '') === '1') {
    tags.push({ label: '部分证书异常，建议核对异常人员和补证留痕', type: 'warning' })
  }
  if (isImportedSource(report.sourceMode)) {
    tags.push({ label: '外部导入来源，建议复核字段完整性和来源平台', type: 'info' })
  }
  if (String(report.reporterType || '') === '2') {
    tags.push({ label: '个人报备主体，建议重点核对联系人与作业地点', type: 'info' })
  }
  if (!report.actualEndTime && String(report.reportStatus || '') === '1') {
    tags.push({ label: '结束状态缺少结束时间展示，建议复核结束留痕', type: 'warning' })
  }
  if (!tags.length) {
    tags.push({ label: '当前报备信息完整，可继续生成凭证或归档闭环', type: 'success' })
  }
  return tags
}

export function buildAzbDetailHintTags(report, currentVoucher) {
  if (!report) {
    return []
  }
  const tags = []
  if (String(report.reportStatus || '') === '0') {
    tags.push({ label: '当前仍在进行中，需优先确认结束条件', type: 'warning' })
  } else {
    tags.push({ label: '当前已结束，可复核照片与凭证归档', type: 'success' })
  }
  if (String(report.certValidStatus || '') === '1') {
    tags.push({ label: '存在部分人员证书异常，建议逐人复核', type: 'warning' })
  }
  if (String(report.certValidStatus || '') === '2') {
    tags.push({ label: '全部证书失效，建议优先升级核查', type: 'danger' })
  }
  if (isImportedSource(report.sourceMode)) {
    tags.push({ label: '来源为导入或第三方，需核对责任链路', type: 'info' })
  }
  if (currentVoucher?.voucherToken) {
    tags.push({ label: '已生成电子凭证，可继续复核二维码与凭证地址', type: 'success' })
  } else {
    tags.push({ label: '当前未加载凭证，必要时切换到凭证视角复核', type: 'info' })
  }
  return tags
}

export function summaryCard(key, label, value, unit, note, cardClass) {
  return {
    key,
    label,
    value: value ?? 0,
    unit,
    note,
    cardClass
  }
}

export function focusQueue(key, title, count, unit, desc, actionText) {
  return {
    key,
    title,
    count: count ?? 0,
    unit,
    desc,
    actionText
  }
}

export function isImportedSource(sourceMode) {
  const value = String(sourceMode || '').toUpperCase()
  return value.includes('IMPORT') || value.includes('STUB') || value.includes('THIRD')
}

export function matchReportFocus(row, focusKey) {
  if (!row || !focusKey) {
    return false
  }
  switch (focusKey) {
    case 'active':
      return String(row.reportStatus || '') === '0'
    case 'invalid':
      return ['1', '2'].includes(String(row.certValidStatus || ''))
    case 'partial':
      return String(row.certValidStatus || '') === '1'
    case 'allInvalid':
      return String(row.certValidStatus || '') === '2'
    case 'imported':
      return isImportedSource(row.sourceMode)
    default:
      return false
  }
}

export function prioritizeFocusRows(rows, matcher) {
  if (!Array.isArray(rows) || rows.length <= 1 || typeof matcher !== 'function') {
    return rows || []
  }
  const matchedRows = []
  const otherRows = []
  rows.forEach(row => {
    if (matcher(row)) {
      matchedRows.push(row)
    } else {
      otherRows.push(row)
    }
  })
  return [...matchedRows, ...otherRows]
}
