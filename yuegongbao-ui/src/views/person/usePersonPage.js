import { computed, getCurrentInstance, reactive, ref, toRefs } from 'vue'
import {
  addPerson,
  delPerson,
  getPerson,
  getPersonSummary,
  listPerson,
  updatePerson
} from '@/api/ygb/person'
import { optionselectEnterprise } from '@/api/ygb/enterprise'
import useUserStore from '@/store/modules/user'
import { filterAuthorizedRegionOptions } from '@/utils/regionScope'

export const workerTypeOptions = [
  { label: '派遣工', value: '1' },
  { label: '正式工', value: '2' },
  { label: '外包工', value: '3' },
  { label: '新业态人员', value: '4' }
]

export const certStatusOptions = [
  { label: '未校验', value: '0' },
  { label: '有效', value: '1' },
  { label: '临期', value: '2' },
  { label: '过期', value: '3' }
]

export const insuranceStatusOptions = [
  { label: '未参保', value: '0' },
  { label: '已参保', value: '1' },
  { label: '停保', value: '2' }
]

export const employmentStatusOptions = [
  { label: '在岗', value: '0' },
  { label: '离岗', value: '1' }
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

export const baseRegionOptions = [
  { label: '广东省', value: '440000' },
  { label: '广州市', value: '440100' },
  { label: '广州市天河区', value: '440106' },
  { label: '深圳市', value: '440300' },
  { label: '深圳市南山区', value: '440305' },
  { label: '佛山市', value: '440600' },
  { label: '佛山市顺德区', value: '440606' }
]

function createDefaultQueryParams() {
  return {
    pageNum: 1,
    pageSize: 10,
    regionCode: undefined,
    enterpriseId: undefined,
    personName: undefined,
    idCard: undefined,
    workerType: undefined,
    certStatus: undefined,
    insuranceStatus: undefined,
    employmentStatus: undefined
  }
}

function createDefaultForm() {
  return {
    personId: undefined,
    enterpriseId: undefined,
    regionCode: undefined,
    personName: undefined,
    idCard: undefined,
    mobile: undefined,
    workerType: '1',
    jobType: undefined,
    certStatus: '0',
    insuranceStatus: '0',
    employmentStatus: '0',
    entryDate: undefined,
    leaveDate: undefined,
    remark: undefined
  }
}

function createRules() {
  return {
    enterpriseId: [{ required: true, message: '请选择所属企业', trigger: 'change' }],
    personName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
    idCard: [{ required: true, message: '请输入身份证号', trigger: 'blur' }],
    workerType: [{ required: true, message: '请选择用工类型', trigger: 'change' }]
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

export function maskIdCard(idCard) {
  if (!idCard || idCard.length < 8) {
    return idCard || '-'
  }
  return `${idCard.slice(0, 4)}********${idCard.slice(-4)}`
}

export function usePersonPage(options = {}) {
  const {
    exportFilePrefix = 'ygb_person',
    initialQueryParams = {},
    canMutate = () => true,
    onBlockedAction
  } = options

  const { proxy } = getCurrentInstance()
  const userStore = useUserStore()

  const showSearch = ref(true)
  const loading = ref(false)
  const total = ref(0)
  const open = ref(false)
  const detailOpen = ref(false)
  const title = ref('')
  const personList = ref([])
  const enterpriseOptions = ref([])
  const currentPerson = ref(undefined)
  const detailPerson = ref(undefined)
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
    personList.value.forEach(item => {
      if (item.regionCode && !optionMap.has(item.regionCode)) {
        optionMap.set(item.regionCode, {
          value: item.regionCode,
          label: formatRegionName(item.regionCode, item.regionCode)
        })
      }
    })
    return filterAuthorizedRegionOptions(Array.from(optionMap.values()), userStore.allowedRegionCodes)
  })

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
      idCard: queryParams.value.idCard,
      workerType: queryParams.value.workerType,
      certStatus: queryParams.value.certStatus,
      insuranceStatus: queryParams.value.insuranceStatus,
      employmentStatus: queryParams.value.employmentStatus
    }
  }

  function syncCurrentPerson() {
    if (currentPerson.value) {
      const matched = personList.value.find(item => item.personId === currentPerson.value.personId)
      if (matched) {
        currentPerson.value = matched
        return
      }
    }
    currentPerson.value = personList.value.length > 0 ? personList.value[0] : undefined
  }

  function getList() {
    loading.value = true
    Promise.all([
      listPerson(queryParams.value),
      getPersonSummary(buildSummaryQuery())
    ]).then(([listResponse, summaryResponse]) => {
      personList.value = listResponse.rows || []
      total.value = listResponse.total || 0
      summaryData.value = summaryResponse.data || {}
      syncCurrentPerson()
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
    proxy.resetForm('personRef')
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
    ids.value = selection.map(item => item.personId)
    names.value = selection.map(item => item.personName)
    single.value = selection.length !== 1
    multiple.value = !selection.length
  }

  function handleRowClick(row) {
    currentPerson.value = row
  }

  function handleAdd() {
    if (!guardMutation('新增人员')) {
      return
    }
    reset()
    title.value = '新增人员'
    open.value = true
  }

  function normalizeForm(source = {}) {
    return {
      ...createDefaultForm(),
      ...source,
      workerType: source.workerType || '1',
      certStatus: source.certStatus || '0',
      insuranceStatus: source.insuranceStatus || '0',
      employmentStatus: source.employmentStatus || '0'
    }
  }

  function handleUpdate(row) {
    if (!guardMutation('修改人员')) {
      return
    }
    reset()
    const personId = row?.personId || ids.value[0]
    if (!personId) {
      return
    }
    getPerson(personId).then(response => {
      form.value = normalizeForm(response.data || {})
      title.value = '修改人员'
      open.value = true
    })
  }

  function openDetail(row) {
    const target = row || currentPerson.value
    if (!target?.personId) {
      return
    }
    currentPerson.value = target
    getPerson(target.personId).then(response => {
      detailPerson.value = response.data || {}
      detailOpen.value = true
    })
  }

  function submitForm() {
    if (!guardMutation(form.value.personId ? '修改人员' : '新增人员')) {
      return
    }
    proxy.$refs.personRef.validate(valid => {
      if (!valid) {
        return
      }
      const payload = { ...form.value }
      const request = payload.personId ? updatePerson(payload) : addPerson(payload)
      request.then(() => {
        proxy.$modal.msgSuccess(payload.personId ? '修改成功' : '新增成功')
        open.value = false
        getList()
      })
    })
  }

  function handleDelete(row) {
    if (!guardMutation('删除人员')) {
      return
    }
    const personIds = row?.personId || ids.value
    const personNames = row?.personName || names.value.join('、')
    if (!personIds || (Array.isArray(personIds) && !personIds.length)) {
      return
    }
    proxy.$modal.confirm(`是否确认删除人员“${personNames}”的数据项？`).then(() => {
      return delPerson(personIds)
    }).then(() => {
      proxy.$modal.msgSuccess('删除成功')
      getList()
    }).catch(() => {})
  }

  function handleExport() {
    proxy.download('ygb/person/export', { ...queryParams.value }, `${exportFilePrefix}_${Date.now()}.xlsx`)
  }

  function handleEnterpriseChange(value) {
    const current = enterpriseOptions.value.find(item => item.enterpriseId === value)
    form.value.regionCode = current?.regionCode || undefined
  }

  loadEnterpriseOptions()
  getList()

  return {
    showSearch,
    loading,
    total,
    open,
    detailOpen,
    title,
    personList,
    enterpriseOptions,
    currentPerson,
    detailPerson,
    summaryData,
    ids,
    single,
    multiple,
    queryParams,
    form,
    rules,
    regionOptions,
    getList,
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
    handleEnterpriseChange
  }
}
