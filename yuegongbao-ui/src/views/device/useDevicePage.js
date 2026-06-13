import { computed, getCurrentInstance, reactive, ref, toRefs } from 'vue'
import {
  addDevice,
  aiEventDevice,
  authorizeDevice,
  delDevice,
  getDevice,
  getDeviceSummary,
  heartbeatDevice,
  listDevice,
  listDeviceCommandLog,
  listDeviceEvent,
  lockDevice,
  unlockDevice,
  updateDevice
} from '@/api/ygb/device'
import { optionselectEnterprise } from '@/api/ygb/enterprise'
import { optionselectPerson } from '@/api/ygb/person'
import useUserStore from '@/store/modules/user'
import { filterAuthorizedRegionOptions } from '@/utils/regionScope'

export const baseRegionOptions = [
  { label: '广东省', value: '440000' },
  { label: '广州市', value: '440100' },
  { label: '广州市天河区', value: '440106' },
  { label: '深圳市', value: '440300' },
  { label: '深圳市南山区', value: '440305' },
  { label: '佛山市', value: '440600' },
  { label: '佛山市顺德区', value: '440606' }
]

export const regionNameMap = {
  '440000': '广东省',
  '440100': '广州市',
  '440106': '广州市天河区',
  '440300': '深圳市',
  '440305': '深圳市南山区',
  '440600': '佛山市',
  '440606': '佛山市顺德区'
}

export const deviceTypeOptions = [
  { label: '考勤机', value: '1' },
  { label: '芯片设备', value: '2' },
  { label: 'AI摄像头', value: '3' }
]

export const deviceStatusOptions = [
  { label: '离线', value: '0' },
  { label: '在线', value: '1' },
  { label: '锁定', value: '2' },
  { label: '故障', value: '3' }
]

export const authStatusOptions = [
  { label: '未授权', value: '0' },
  { label: '授权通过', value: '1' },
  { label: '授权拒绝', value: '2' }
]

export const commandTypeOptions = [
  { label: '锁机', value: '1' },
  { label: '解锁', value: '2' },
  { label: '授权', value: '3' }
]

export const commandResultOptions = [
  { label: '待处理', value: '0' },
  { label: '成功', value: '1' },
  { label: '失败', value: '2' }
]

export const eventTypeOptions = [
  { label: '心跳', value: '1' },
  { label: 'AI事件', value: '2' },
  { label: '授权事件', value: '3' }
]

export const eventStatusOptions = [
  { label: '未处理', value: '0' },
  { label: '已处理', value: '1' }
]

export const heartbeatStatusOptions = [
  { label: '在线', value: '1' },
  { label: '故障', value: '3' }
]

function createDefaultQueryParams() {
  return {
    pageNum: 1,
    pageSize: 10,
    regionCode: undefined,
    deviceCode: undefined,
    deviceName: undefined,
    enterpriseId: undefined,
    deviceType: undefined,
    deviceStatus: undefined,
    authStatus: undefined
  }
}

function createDefaultForm() {
  return {
    deviceId: undefined,
    deviceCode: undefined,
    deviceName: undefined,
    deviceType: '1',
    enterpriseId: undefined,
    chipId: undefined,
    simCardNo: undefined,
    deviceStatus: '0',
    authStatus: '0',
    installLocation: undefined,
    firmwareVersion: undefined,
    remark: undefined
  }
}

function createAuthorizeForm(deviceId = undefined) {
  return {
    deviceId,
    personId: undefined,
    certNo: undefined
  }
}

function createHeartbeatForm(deviceId = undefined, heartbeatStatus = '1') {
  return {
    deviceId,
    heartbeatStatus
  }
}

function createAiForm(deviceId = undefined) {
  return {
    deviceId,
    eventCode: 'AI_ALERT',
    eventContent: 'AI识别到疑似异常作业行为',
    evidenceUrl: undefined
  }
}

function createRules() {
  return {
    deviceCode: [{ required: true, message: '设备编码不能为空', trigger: 'blur' }],
    enterpriseId: [{ required: true, message: '所属企业不能为空', trigger: 'change' }]
  }
}

function createAuthorizeRules() {
  return {
    personId: [{ required: true, message: '授权人员不能为空', trigger: 'change' }]
  }
}

export function optionLabel(options, value, fallback = '-') {
  const matched = Array.isArray(options) ? options.find(item => item.value === value) : undefined
  return matched ? matched.label : fallback
}

export function formatRegionName(code, fallback = '全部区域') {
  if (!code) {
    return fallback
  }
  return regionNameMap[code] || code
}

export function valueOrDefault(value, fallback) {
  return value === undefined || value === null ? fallback : value
}

function buildDeleteLabel(deviceIds) {
  if (Array.isArray(deviceIds)) {
    return deviceIds.join(',')
  }
  return deviceIds
}

export function useDevicePage(options = {}) {
  const {
    exportFilePrefix = 'ygb_device',
    canMutate = () => true,
    onBlockedAction,
    getCurrentList,
    afterList,
    immediate = true
  } = options

  const { proxy } = getCurrentInstance()
  const userStore = useUserStore()

  const deviceList = ref([])
  const enterpriseOptions = ref([])
  const personOptions = ref([])
  const commandLogList = ref([])
  const deviceEventList = ref([])
  const summaryData = ref({})
  const open = ref(false)
  const authorizeOpen = ref(false)
  const heartbeatOpen = ref(false)
  const aiOpen = ref(false)
  const loading = ref(false)
  const showSearch = ref(true)
  const ids = ref([])
  const single = ref(true)
  const multiple = ref(true)
  const total = ref(0)
  const title = ref('')
  const currentDevice = ref(undefined)
  const activeTraceTab = ref('command')

  const data = reactive({
    form: createDefaultForm(),
    queryParams: createDefaultQueryParams(),
    authorizeForm: createAuthorizeForm(),
    heartbeatForm: createHeartbeatForm(),
    aiForm: createAiForm(),
    rules: createRules(),
    authorizeRules: createAuthorizeRules()
  })

  const { queryParams, form, rules, authorizeForm, heartbeatForm, aiForm, authorizeRules } = toRefs(data)

  const regionOptions = computed(() => {
    const optionMap = new Map(baseRegionOptions.map(item => [item.value, item]))
    enterpriseOptions.value.forEach(item => {
      if (item.regionCode && !optionMap.has(item.regionCode)) {
        optionMap.set(item.regionCode, {
          value: item.regionCode,
          label: formatRegionName(item.regionCode, item.regionCode)
        })
      }
    })
    deviceList.value.forEach(item => {
      if (item.regionCode && !optionMap.has(item.regionCode)) {
        optionMap.set(item.regionCode, {
          value: item.regionCode,
          label: formatRegionName(item.regionCode, item.regionCode)
        })
      }
    })
    return filterAuthorizedRegionOptions(Array.from(optionMap.values()), userStore.allowedRegionCodes)
  })

  const authorizeDeviceName = computed(() => {
    if (!authorizeForm.value.deviceId) {
      return ''
    }
    const row = deviceList.value.find(item => item.deviceId === authorizeForm.value.deviceId) || currentDevice.value
    return row ? `${row.deviceCode} / ${row.deviceName}` : ''
  })

  const traceDeviceName = computed(() => {
    if (!currentDevice.value) {
      return ''
    }
    return `${currentDevice.value.deviceCode} / ${currentDevice.value.deviceName}`
  })

  function resolveCurrentList() {
    if (typeof getCurrentList === 'function') {
      const list = getCurrentList()
      if (Array.isArray(list)) {
        return list
      }
    }
    return deviceList.value
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
      deviceCode: queryParams.value.deviceCode,
      deviceName: queryParams.value.deviceName,
      enterpriseId: queryParams.value.enterpriseId,
      deviceType: queryParams.value.deviceType,
      deviceStatus: queryParams.value.deviceStatus,
      authStatus: queryParams.value.authStatus
    }
  }

  function loadEnterpriseOptions() {
    optionselectEnterprise().then(response => {
      enterpriseOptions.value = response.data || []
    })
  }

  function loadPersonOptions(query = {}) {
    optionselectPerson(query).then(response => {
      personOptions.value = response.data || []
    })
  }

  function loadTraceTables(deviceId) {
    if (!deviceId) {
      commandLogList.value = []
      deviceEventList.value = []
      return
    }
    listDeviceCommandLog({
      pageNum: 1,
      pageSize: 5,
      deviceId
    }).then(response => {
      commandLogList.value = response.rows || []
    })
    listDeviceEvent({
      pageNum: 1,
      pageSize: 5,
      deviceId
    }).then(response => {
      deviceEventList.value = response.rows || []
    })
  }

  function syncCurrentDevice() {
    const currentList = resolveCurrentList()
    if (currentDevice.value) {
      const matched = currentList.find(item => item.deviceId === currentDevice.value.deviceId)
      if (matched) {
        currentDevice.value = matched
        loadTraceTables(matched.deviceId)
        return
      }
    }
    if (currentList.length > 0) {
      currentDevice.value = currentList[0]
      loadTraceTables(currentDevice.value.deviceId)
      return
    }
    currentDevice.value = undefined
    commandLogList.value = []
    deviceEventList.value = []
  }

  function getList() {
    loading.value = true
    Promise.all([
      listDevice(queryParams.value),
      getDeviceSummary(buildSummaryQuery())
    ]).then(([listResponse, summaryResponse]) => {
      deviceList.value = listResponse.rows || []
      total.value = listResponse.total || 0
      summaryData.value = summaryResponse.data || {}
      if (typeof afterList === 'function') {
        afterList()
      }
      syncCurrentDevice()
    }).finally(() => {
      loading.value = false
    })
  }

  function reset() {
    form.value = createDefaultForm()
    proxy.resetForm('deviceRef')
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
    ids.value = selection.map(item => item.deviceId)
    single.value = selection.length !== 1
    multiple.value = !selection.length
    if (selection.length === 1) {
      currentDevice.value = selection[0]
      loadTraceTables(selection[0].deviceId)
    }
  }

  function handleRowClick(row) {
    currentDevice.value = row
    loadTraceTables(row.deviceId)
  }

  function handleAdd() {
    if (!guardMutation('新增设备')) {
      return
    }
    reset()
    open.value = true
    title.value = '新增设备'
  }

  function handleUpdate(row) {
    if (!guardMutation('修改设备')) {
      return
    }
    const deviceId = row?.deviceId || ids.value[0]
    if (!deviceId) {
      proxy.$modal.msgWarning('请选择一台设备')
      return
    }
    reset()
    getDevice(deviceId).then(response => {
      form.value = {
        ...createDefaultForm(),
        ...(response.data || {})
      }
      open.value = true
      title.value = '修改设备'
    })
  }

  function submitForm() {
    if (!guardMutation(form.value.deviceId ? '修改设备' : '新增设备')) {
      return
    }
    proxy.$refs.deviceRef.validate(valid => {
      if (!valid) {
        return
      }
      const request = form.value.deviceId ? updateDevice(form.value) : addDevice(form.value)
      request.then(() => {
        proxy.$modal.msgSuccess(form.value.deviceId ? '修改成功' : '新增成功')
        open.value = false
        getList()
      })
    })
  }

  function handleDelete(row) {
    if (!guardMutation('删除设备')) {
      return
    }
    const deviceIds = row?.deviceId || ids.value
    if (!deviceIds || (Array.isArray(deviceIds) && !deviceIds.length)) {
      proxy.$modal.msgWarning('请选择要删除的设备')
      return
    }
    proxy.$modal.confirm(`是否确认删除设备"${buildDeleteLabel(deviceIds)}"？`).then(() => {
      return delDevice(deviceIds)
    }).then(() => {
      proxy.$modal.msgSuccess('删除成功')
      getList()
    }).catch(() => {})
  }

  function handleExport() {
    proxy.download('ygb/device/export', { ...queryParams.value }, `${exportFilePrefix}_${Date.now()}.xlsx`)
  }

  function handleLock(row) {
    if (!guardMutation('锁定设备')) {
      return
    }
    currentDevice.value = row
    proxy.$modal.confirm(`是否确认对设备"${row.deviceCode}"下发锁机指令？`).then(() => {
      return lockDevice(row.deviceId)
    }).then(response => {
      proxy.$modal.msgSuccess(response.msg || '锁机指令已记录')
      getList()
    }).catch(() => {})
  }

  function handleUnlock(row) {
    if (!guardMutation('解锁设备')) {
      return
    }
    currentDevice.value = row
    proxy.$modal.confirm(`是否确认对设备"${row.deviceCode}"下发解锁指令？`).then(() => {
      return unlockDevice(row.deviceId)
    }).then(response => {
      proxy.$modal.msgSuccess(response.msg || '解锁指令已记录')
      getList()
    }).catch(() => {})
  }

  function openAuthorizeDialog(row) {
    if (!row?.deviceId) {
      return
    }
    if (!guardMutation('发起设备授权')) {
      return
    }
    currentDevice.value = row
    authorizeForm.value = createAuthorizeForm(row.deviceId)
    loadPersonOptions(row.enterpriseId ? { enterpriseId: row.enterpriseId } : {})
    authorizeOpen.value = true
  }

  function submitAuthorize() {
    if (!guardMutation('提交设备授权')) {
      return
    }
    proxy.$refs.authorizeRef.validate(valid => {
      if (!valid) {
        return
      }
      authorizeDevice(authorizeForm.value).then(response => {
        proxy.$modal.msgSuccess(response.msg || '设备授权处理完成')
        authorizeOpen.value = false
        getList()
      })
    })
  }

  function openHeartbeatDialog(row) {
    if (!row?.deviceId) {
      return
    }
    if (!guardMutation('模拟心跳')) {
      return
    }
    currentDevice.value = row
    heartbeatForm.value = createHeartbeatForm(row.deviceId, row.deviceStatus === '3' ? '3' : '1')
    heartbeatOpen.value = true
  }

  function submitHeartbeat() {
    if (!guardMutation('提交心跳回写')) {
      return
    }
    heartbeatDevice(heartbeatForm.value).then(response => {
      proxy.$modal.msgSuccess(response.msg || '模拟心跳完成')
      heartbeatOpen.value = false
      getList()
    })
  }

  function openAiDialog(row) {
    if (!row?.deviceId) {
      return
    }
    if (!guardMutation('上报AI事件')) {
      return
    }
    currentDevice.value = row
    aiForm.value = createAiForm(row.deviceId)
    aiOpen.value = true
  }

  function submitAiEvent() {
    if (!guardMutation('提交AI事件')) {
      return
    }
    aiEventDevice(aiForm.value).then(response => {
      proxy.$modal.msgSuccess(response.msg || '模拟AI事件完成')
      aiOpen.value = false
      getList()
    })
  }

  function formatDateTime(value) {
    if (!value) {
      return '-'
    }
    return proxy.parseTime(value, '{y}-{m}-{d} {h}:{i}:{s}')
  }

  function init() {
    loadEnterpriseOptions()
    loadPersonOptions()
    getList()
  }

  if (immediate) {
    init()
  }

  return {
    deviceList,
    enterpriseOptions,
    personOptions,
    commandLogList,
    deviceEventList,
    summaryData,
    open,
    authorizeOpen,
    heartbeatOpen,
    aiOpen,
    loading,
    showSearch,
    ids,
    single,
    multiple,
    total,
    title,
    currentDevice,
    activeTraceTab,
    queryParams,
    form,
    rules,
    authorizeForm,
    authorizeRules,
    heartbeatForm,
    aiForm,
    regionOptions,
    authorizeDeviceName,
    traceDeviceName,
    buildSummaryQuery,
    loadEnterpriseOptions,
    loadPersonOptions,
    loadTraceTables,
    syncCurrentDevice,
    getList,
    cancel,
    handleQuery,
    resetQuery,
    handleSelectionChange,
    handleRowClick,
    handleAdd,
    handleUpdate,
    submitForm,
    handleDelete,
    handleExport,
    handleLock,
    handleUnlock,
    openAuthorizeDialog,
    submitAuthorize,
    openHeartbeatDialog,
    submitHeartbeat,
    openAiDialog,
    submitAiEvent,
    formatDateTime,
    init
  }
}
