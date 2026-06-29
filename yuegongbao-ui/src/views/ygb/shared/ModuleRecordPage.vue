<template>
  <div class="app-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>{{ config.title }}</span>
          <span class="card-header__tip">{{ config.description || '围绕当前业务台账提供列表、汇总、详情、导出和权限动作。' }}</span>
        </div>
      </template>

      <div class="summary-grid">
        <div v-for="item in summaryCards" :key="item.key" class="summary-card">
          <div class="summary-card__label">{{ item.label }}</div>
          <div class="summary-card__value">{{ item.value }}</div>
        </div>
      </div>

      <el-form ref="queryRef" :model="queryParams" :inline="true" v-show="showSearch" label-width="88px">
        <el-form-item v-if="showFilter('statMonth')" label="统计月份">
          <el-date-picker v-model="queryParams.statMonth" type="month" value-format="YYYY-MM" format="YYYY-MM" style="width: 160px" />
        </el-form-item>
        <el-form-item v-if="showFilter('regionCode')" label="区域">
          <el-select v-model="queryParams.regionCode" clearable style="width: 180px">
            <el-option v-for="item in regionOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="showFilter('enterpriseId')" label="企业">
          <el-select
            v-model="queryParams.enterpriseId"
            :clearable="!enterpriseFilterLocked"
            :disabled="enterpriseFilterLocked"
            filterable
            style="width: 220px"
          >
            <el-option v-for="item in enterpriseOptions" :key="item.enterpriseId" :label="item.enterpriseName" :value="item.enterpriseId" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="showFilter('platformName')" label="平台企业">
          <el-input v-model="queryParams.platformName" clearable placeholder="请输入平台企业" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item v-if="showFilter('industryType')" label="行业">
          <el-input v-model="queryParams.industryType" clearable placeholder="请输入行业" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item v-if="showFilter('warningLevel')" label="预警级别">
          <el-select v-model="queryParams.warningLevel" clearable style="width: 160px">
            <el-option v-for="item in warningLevelOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="showFilter('recordName')" :label="config.recordNameLabel || '记录名称'">
          <el-input v-model="queryParams.recordName" clearable :placeholder="config.recordNamePlaceholder || '请输入记录名称'" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item v-if="showFilter('enterpriseName')" label="企业名称">
          <el-input v-model="queryParams.enterpriseName" clearable placeholder="请输入企业名称" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item v-if="showFilter('personName')" label="人员姓名">
          <el-input v-model="queryParams.personName" clearable placeholder="请输入人员姓名" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item v-if="showFilter('workflowStatus')" label="流程状态">
          <el-select v-model="queryParams.workflowStatus" clearable style="width: 160px">
            <el-option v-for="item in workflowStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="showFilter('status')" label="启停状态">
          <el-select v-model="queryParams.status" clearable style="width: 140px">
            <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">查询</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <el-row :gutter="10" class="mb8">
        <el-col :span="1.5" v-if="!config.readOnly">
          <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="[`${config.permPrefix}:add`]">新增</el-button>
        </el-col>
        <el-col :span="1.5" v-if="!config.readOnly">
          <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="[`${config.permPrefix}:edit`]">修改</el-button>
        </el-col>
        <el-col :span="1.5" v-if="!config.readOnly">
          <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="[`${config.permPrefix}:remove`]">删除</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="[`${config.permPrefix}:export`]">导出</el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>

      <el-table v-loading="loading" :data="recordList" @selection-change="handleSelectionChange">
        <el-table-column v-if="!config.readOnly" type="selection" width="55" align="center" />
        <el-table-column label="ID" prop="recordId" width="90" />
        <el-table-column :label="config.recordNameLabel || '记录名称'" prop="recordName" min-width="180" show-overflow-tooltip />
        <el-table-column label="分类编码" prop="categoryCode" width="140" />
        <el-table-column label="统计月份" prop="statMonth" width="110" />
        <el-table-column label="区域" width="120">
          <template #default="scope">
            <span>{{ formatRegionName(scope.row.regionCode, scope.row.regionCode || '-') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="企业名称" prop="enterpriseName" min-width="180" show-overflow-tooltip />
        <el-table-column label="流程状态" width="120">
          <template #default="scope">
            <dict-tag :options="workflowStatusOptions" :value="scope.row.workflowStatus" />
          </template>
        </el-table-column>
        <el-table-column label="启停状态" width="100">
          <template #default="scope">
            <dict-tag :options="statusOptions" :value="scope.row.status" />
          </template>
        </el-table-column>
        <el-table-column label="更新时间" width="170">
          <template #default="scope">
            <span>{{ parseTime(scope.row.updateTime || scope.row.createTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="180">
          <template #default="scope">
            <el-button link type="primary" @click="handleView(scope.row)" v-hasPermi="[`${config.permPrefix}:query`]">详情</el-button>
            <el-button
              v-if="!config.readOnly"
              link
              type="primary"
              @click="handleUpdate(scope.row)"
              v-hasPermi="[`${config.permPrefix}:edit`]"
            >
              修改
            </el-button>
            <el-button
              v-if="!config.readOnly"
              link
              type="danger"
              @click="handleDelete(scope.row)"
              v-hasPermi="[`${config.permPrefix}:remove`]"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <pagination
        v-show="total > 0"
        :total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
      />
    </el-card>

    <el-dialog v-if="!config.readOnly" :title="title" v-model="open" width="760px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item :label="config.recordNameLabel || '记录名称'" prop="recordName">
              <el-input v-model="form.recordName" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="分类编码" prop="categoryCode">
              <el-input v-model="form.categoryCode" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="统计月份" prop="statMonth">
              <el-date-picker v-model="form.statMonth" type="month" value-format="YYYY-MM" format="YYYY-MM" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="区域编码" prop="regionCode">
              <el-input v-model="form.regionCode" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="企业" prop="enterpriseId">
              <el-select
                v-model="form.enterpriseId"
                :clearable="!enterpriseFilterLocked"
                :disabled="enterpriseFilterLocked"
                filterable
                style="width: 100%"
                @change="handleFormEnterpriseChange"
              >
                <el-option
                  v-for="item in enterpriseOptions"
                  :key="item.enterpriseId"
                  :label="item.enterpriseName"
                  :value="item.enterpriseId"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="企业名称" prop="enterpriseName">
              <el-input v-model="form.enterpriseName" :disabled="enterpriseFilterLocked" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="人员" prop="personId">
              <el-select
                v-model="form.personId"
                clearable
                filterable
                placeholder="请选择人员"
                style="width: 100%"
                @change="handleFormPersonChange"
                @clear="handleFormPersonClear"
              >
                <el-option
                  v-for="item in personOptions"
                  :key="item.personId"
                  :label="formatPersonOptionLabel(item)"
                  :value="item.personId"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="人员姓名" prop="personName">
              <el-input v-model="form.personName" disabled placeholder="选择人员后自动回填" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="流程状态" prop="workflowStatus">
              <el-select v-model="form.workflowStatus" style="width: 100%">
                <el-option v-for="item in workflowStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="启停状态" prop="status">
              <el-select v-model="form.status" style="width: 100%">
                <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="来源标签" prop="sourceLabel">
              <el-input v-model="form.sourceLabel" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="排序" prop="sortOrder">
              <el-input-number v-model="form.sortOrder" :min="0" style="width: 100%" controls-position="right" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="关联编码" prop="relatedCode">
              <el-input v-model="form.relatedCode" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="扩展JSON" prop="payloadJson">
              <el-input v-model="form.payloadJson" type="textarea" :rows="5" placeholder='如 {"channel":"internal"}' />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" type="textarea" :rows="3" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitForm">确定</el-button>
        <el-button @click="cancel">取消</el-button>
      </template>
    </el-dialog>

    <el-dialog title="台账详情" v-model="detailOpen" width="760px">
      <el-descriptions v-if="detail" :column="2" border>
        <el-descriptions-item label="记录ID">{{ detail.recordId }}</el-descriptions-item>
        <el-descriptions-item :label="config.recordNameLabel || '记录名称'">{{ detail.recordName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="分类编码">{{ detail.categoryCode || '-' }}</el-descriptions-item>
        <el-descriptions-item label="统计月份">{{ detail.statMonth || '-' }}</el-descriptions-item>
        <el-descriptions-item label="区域">{{ formatRegionName(detail.regionCode, detail.regionCode || '-') }}</el-descriptions-item>
        <el-descriptions-item label="企业名称">{{ detail.enterpriseName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="人员姓名">{{ detail.personName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="来源标签">{{ detail.sourceLabel || '-' }}</el-descriptions-item>
        <el-descriptions-item label="流程状态">
          <dict-tag :options="workflowStatusOptions" :value="detail.workflowStatus" />
        </el-descriptions-item>
        <el-descriptions-item label="启停状态">
          <dict-tag :options="statusOptions" :value="detail.status" />
        </el-descriptions-item>
        <el-descriptions-item label="关联编码" :span="2">{{ detail.relatedCode || '-' }}</el-descriptions-item>
        <el-descriptions-item label="扩展JSON" :span="2">
          <pre class="payload-json">{{ formattedPayload(detail.payloadJson) }}</pre>
        </el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detail.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, getCurrentInstance, reactive, ref, toRefs } from 'vue'
import { useRoute } from 'vue-router'
import { optionselectEnterprise } from '@/api/ygb/enterprise'
import { optionselectPerson } from '@/api/ygb/person'
import {
  applyLockedEnterpriseQuery,
  filterAuthorizedEnterpriseOptions,
  isEnterpriseFilterLocked,
  lockedEnterpriseId
} from '@/utils/enterpriseScope'
import { authorizedDefaultRegionCode, useAuthorizedRegionOptions } from '@/utils/regionScope'
import { formatRegionName } from '@/utils/regionName'
import { statReportRegionOptions } from '@/views/statReport/useStatReportPage'

const props = defineProps({
  config: {
    type: Object,
    required: true
  }
})

const route = useRoute()
const { proxy } = getCurrentInstance()
const regionOptions = useAuthorizedRegionOptions(statReportRegionOptions)
const enterpriseOptions = ref([])
const personOptions = ref([])
const loading = ref(false)
const showSearch = ref(true)
const recordList = ref([])
const total = ref(0)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const open = ref(false)
const detailOpen = ref(false)
const title = ref('')
const detail = ref(null)
const summaryData = ref({})

const workflowStatusOptions = [
  { label: '草稿', value: 'draft' },
  { label: '待处理', value: 'pending' },
  { label: '处理中', value: 'processing' },
  { label: '已关闭', value: 'closed' },
  { label: '已驳回', value: 'rejected' },
  { label: '已逾期', value: 'overdue' }
]

const statusOptions = [
  { label: '启用', value: '0' },
  { label: '停用', value: '1' }
]

const warningLevelOptions = [
  { label: '正常', value: '0' },
  { label: '黄色', value: '1' },
  { label: '红色', value: '2' }
]

const defaultVisibleFilters = ['statMonth', 'regionCode', 'recordName', 'enterpriseName', 'workflowStatus', 'status']

const enterpriseFilterLocked = computed(() => isEnterpriseFilterLocked())

const formRules = computed(() => ({
  recordName: [{
    required: true,
    message: `${props.config.recordNameLabel || '记录名称'}不能为空`,
    trigger: 'blur'
  }]
}))

const data = reactive({
  queryParams: applyLockedEnterpriseQuery({
    pageNum: 1,
    pageSize: 10,
    portalCode: props.config.portalCode || 'ygb',
    statMonth: undefined,
    regionCode: authorizedDefaultRegionCode(props.config.defaultRegionCode || '440000'),
    enterpriseId: undefined,
    platformName: undefined,
    industryType: undefined,
    warningLevel: undefined,
    recordName: undefined,
    enterpriseName: undefined,
    personName: undefined,
    workflowStatus: undefined,
    status: undefined
  }),
  form: {}
})

const { queryParams, form } = toRefs(data)
const rules = formRules

const summaryCards = computed(() => ([
  { key: 'total', label: '总量', value: summaryData.value.totalCount ?? 0 },
  { key: 'enabled', label: '启用中', value: summaryData.value.enabledCount ?? 0 },
  { key: 'pending', label: '待处理', value: summaryData.value.pendingCount ?? 0 },
  { key: 'processing', label: '处理中', value: summaryData.value.processingCount ?? 0 },
  { key: 'closed', label: '已关闭', value: summaryData.value.closedCount ?? 0 },
  { key: 'overdue', label: '已逾期', value: summaryData.value.overdueCount ?? 0 }
]))

function normalizedParams(source) {
  return Object.fromEntries(Object.entries(source).filter(([, value]) => value !== undefined && value !== null && value !== ''))
}

function visibleFilters() {
  return Array.isArray(props.config.filters) && props.config.filters.length ? props.config.filters : defaultVisibleFilters
}

function showFilter(field) {
  return visibleFilters().includes(field)
}

function applyRouteQuery() {
  const routeFields = props.config.routeQueryFields || [
    'statMonth',
    'regionCode',
    'enterpriseId',
    'platformName',
    'industryType',
    'warningLevel',
    'workflowStatus',
    'status',
    'recordName',
    'enterpriseName',
    'personName'
  ]
  routeFields.forEach(field => {
    if (route.query[field] !== undefined) {
      queryParams.value[field] = route.query[field]
    }
  })
}

function resolveEnterpriseName(enterpriseId) {
  if (enterpriseId == null) {
    return ''
  }
  const matched = enterpriseOptions.value.find(item => Number(item.enterpriseId) === Number(enterpriseId))
  return matched?.enterpriseName || ''
}

function normalizeId(value) {
  return value === undefined || value === null || value === '' ? '' : String(value)
}

function formatPersonOptionLabel(item) {
  if (!item) {
    return ''
  }
  const name = item.personName || `ID:${item.personId}`
  const suffix = item.enterpriseName || ''
  return suffix ? `${name} / ${suffix}` : name
}

function findPersonOption(personId) {
  const targetId = normalizeId(personId)
  return personOptions.value.find(item => normalizeId(item.personId) === targetId)
}

function applyEnterpriseScopeToForm(formData) {
  const enterpriseId = lockedEnterpriseId() ?? formData.enterpriseId
  return {
    ...formData,
    enterpriseId,
    enterpriseName: formData.enterpriseName || resolveEnterpriseName(enterpriseId)
  }
}

function handleFormEnterpriseChange(enterpriseId) {
  form.value.enterpriseName = resolveEnterpriseName(enterpriseId)
  form.value.personId = undefined
  form.value.personName = ''
  loadPersonOptions({ enterpriseId })
}

function handleFormPersonClear() {
  form.value.personId = undefined
  form.value.personName = ''
}

function handleFormPersonChange(personId) {
  if (!personId) {
    handleFormPersonClear()
    return
  }
  const selectedPerson = findPersonOption(personId)
  applySelectedPersonToForm(selectedPerson)
}

function resetFormData() {
  form.value = applyEnterpriseScopeToForm({
    recordId: undefined,
    portalCode: props.config.portalCode || 'ygb',
    recordName: '',
    categoryCode: props.config.defaultCategoryCode || '',
    statMonth: queryParams.value.statMonth,
    workflowStatus: props.config.defaultWorkflowStatus || 'draft',
    status: '0',
    regionCode: queryParams.value.regionCode,
    enterpriseId: queryParams.value.enterpriseId,
    enterpriseName: '',
    personId: undefined,
    personName: '',
    relatedCode: '',
    sortOrder: 0,
    sourceLabel: props.config.defaultSourceLabel || '',
    payloadJson: '',
    remark: ''
  })
  proxy.resetForm('formRef')
}

function scopedQueryParams() {
  return applyLockedEnterpriseQuery(queryParams.value)
}

function getList() {
  loading.value = true
  const scopedParams = scopedQueryParams()
  return Promise.all([
    props.config.listApi(normalizedParams(scopedParams)),
    props.config.summaryApi(normalizedParams({
      ...scopedParams,
      pageNum: undefined,
      pageSize: undefined
    }))
  ]).then(([listResponse, summaryResponse]) => {
    recordList.value = listResponse.rows || []
    total.value = listResponse.total || 0
    summaryData.value = summaryResponse.data || {}
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
    }
  })
}

function loadPersonOptions(query = {}) {
  const params = normalizedParams(query)
  return optionselectPerson(params).then(response => {
    personOptions.value = response.data || []
  })
}

function loadFormPersonOptions() {
  return loadPersonOptions({ enterpriseId: form.value.enterpriseId }).then(() => {
    if (form.value.personId && !findPersonOption(form.value.personId)) {
      return loadPersonOptions()
    }
  }).then(() => {
    const selectedPerson = findPersonOption(form.value.personId)
    if (selectedPerson) {
      applySelectedPersonToForm(selectedPerson, { preserveEnterprise: true })
    }
  })
}

function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

function resetQuery() {
  proxy.resetForm('queryRef')
  queryParams.value = applyLockedEnterpriseQuery({
    pageNum: 1,
    pageSize: 10,
    portalCode: props.config.portalCode || 'ygb',
    statMonth: undefined,
    regionCode: authorizedDefaultRegionCode(props.config.defaultRegionCode || '440000'),
    enterpriseId: undefined,
    platformName: undefined,
    industryType: undefined,
    warningLevel: undefined,
    recordName: undefined,
    enterpriseName: undefined,
    personName: undefined,
    workflowStatus: undefined,
    status: undefined
  })
  applyRouteQuery()
  getList()
}

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.recordId)
  single.value = selection.length !== 1
  multiple.value = !selection.length
}

function handleAdd() {
  resetFormData()
  loadFormPersonOptions()
  title.value = `新增${props.config.title}`
  open.value = true
}

function handleUpdate(row) {
  resetFormData()
  const recordId = row?.recordId || ids.value[0]
  props.config.detailApi(recordId).then(response => {
    form.value = applyEnterpriseScopeToForm(response.data || {})
    loadFormPersonOptions()
    open.value = true
    title.value = `修改${props.config.title}`
  })
}

function handleView(row) {
  props.config.detailApi(row.recordId).then(response => {
    detail.value = response.data || null
    detailOpen.value = true
  })
}

function submitForm() {
  proxy.$refs.formRef.validate(valid => {
    if (!valid) return
    const payload = buildSubmitPayload()
    if (!payload) return
    const request = payload.recordId ? props.config.updateApi(payload) : props.config.addApi(payload)
    request.then(() => {
      proxy.$modal.msgSuccess(form.value.recordId ? '修改成功' : '新增成功')
      open.value = false
      getList()
    })
  })
}

function applySelectedPersonToForm(selectedPerson, options = {}) {
  if (!selectedPerson) {
    return
  }
  form.value.personId = selectedPerson.personId
  form.value.personName = selectedPerson.personName || ''
  if (!options.preserveEnterprise || !form.value.enterpriseId) {
    form.value.enterpriseId = selectedPerson.enterpriseId ?? form.value.enterpriseId
    form.value.enterpriseName = selectedPerson.enterpriseName || form.value.enterpriseName
    form.value.regionCode = selectedPerson.regionCode || form.value.regionCode
  }
}

function buildSubmitPayload() {
  const selectedPerson = findPersonOption(form.value.personId)
  if (form.value.personId && !selectedPerson) {
    proxy.$modal.msgWarning('请选择有效人员')
    return null
  }
  if (selectedPerson) {
    const formEnterpriseId = normalizeId(form.value.enterpriseId)
    const personEnterpriseId = normalizeId(selectedPerson.enterpriseId)
    if (formEnterpriseId && personEnterpriseId && formEnterpriseId !== personEnterpriseId) {
      proxy.$modal.msgWarning('所选人员与当前企业不一致，请重新选择人员')
      return null
    }
  }
  const payload = applyEnterpriseScopeToForm({ ...form.value })
  if (selectedPerson) {
    payload.personName = selectedPerson.personName || payload.personName
    payload.enterpriseId = payload.enterpriseId ?? selectedPerson.enterpriseId
    payload.enterpriseName = payload.enterpriseName || selectedPerson.enterpriseName
    payload.regionCode = payload.regionCode || selectedPerson.regionCode
  }
  return payload
}

function handleDelete(row) {
  const recordIds = row?.recordId ? [row.recordId] : ids.value
  proxy.$modal.confirm(`是否确认删除选中的 ${props.config.title} 记录？`).then(() => {
    return props.config.deleteApi(recordIds)
  }).then(() => {
    proxy.$modal.msgSuccess('删除成功')
    getList()
  }).catch(() => {})
}

function handleExport() {
  proxy.download(props.config.exportUrl, normalizedParams({
    ...scopedQueryParams(),
    pageNum: undefined,
    pageSize: undefined
  }), `${props.config.filePrefix || 'module_record'}_${Date.now()}.xlsx`)
}

function cancel() {
  open.value = false
  resetFormData()
}

function formattedPayload(payload) {
  if (!payload) {
    return '-'
  }
  try {
    return JSON.stringify(JSON.parse(payload), null, 2)
  } catch (error) {
    return payload
  }
}

applyRouteQuery()
resetFormData()
loadEnterpriseOptions().finally(() => {
  getList()
})
</script>

<style scoped>
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.card-header__tip {
  color: var(--el-text-color-secondary);
  font-size: 13px;
}

.summary-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(140px, 1fr));
  gap: 12px;
  margin-bottom: 16px;
}

.summary-card {
  padding: 14px 16px;
  border-radius: 10px;
  background: linear-gradient(135deg, #f6f9fc 0%, #eef4fb 100%);
}

.summary-card__label {
  color: var(--el-text-color-secondary);
  font-size: 13px;
}

.summary-card__value {
  margin-top: 8px;
  font-size: 24px;
  font-weight: 600;
  color: #123b67;
}

.payload-json {
  margin: 0;
  white-space: pre-wrap;
  word-break: break-all;
  font-size: 12px;
}
</style>
