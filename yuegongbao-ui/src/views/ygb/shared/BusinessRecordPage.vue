<template>
  <div class="app-container business-record-page">
    <el-card shadow="never">
      <template #header>
        <div class="page-head">
          <div>
            <div class="page-title">{{ config.title }}</div>
            <div class="page-desc">{{ config.description || '按业务对象维护记录、状态、企业、人员、金额和处置结果。' }}</div>
          </div>
        </div>
      </template>

      <div class="summary-grid">
        <div v-for="item in summaryCards" :key="item.key" class="summary-card">
          <span>{{ item.label }}</span>
          <strong>{{ item.value }}</strong>
        </div>
      </div>

      <div v-if="coreFeatures.length" class="core-feature-panel">
        <div class="core-feature-title">核心功能</div>
        <div class="core-feature-list">
          <div v-for="item in coreFeatures" :key="item" class="core-feature-item">
            <el-icon><Check /></el-icon>
            <span>{{ item }}</span>
          </div>
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
          <el-select v-model="queryParams.enterpriseId" :clearable="!enterpriseFilterLocked" :disabled="enterpriseFilterLocked" filterable style="width: 220px">
            <el-option v-for="item in enterpriseOptions" :key="item.enterpriseId" :label="item.enterpriseName" :value="item.enterpriseId" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="showFilter('businessName')" :label="config.businessNameLabel || '业务名称'">
          <el-input v-model="queryParams.businessName" clearable :placeholder="config.businessNamePlaceholder || '请输入业务名称'" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item v-if="showFilter('enterpriseName')" label="企业名称">
          <el-input v-model="queryParams.enterpriseName" clearable placeholder="请输入企业名称" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item v-if="showFilter('personName')" label="人员姓名">
          <el-input v-model="queryParams.personName" clearable placeholder="请输入人员姓名" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item v-if="showFilter('riskLevel')" label="风险等级">
          <el-select v-model="queryParams.riskLevel" clearable style="width: 140px">
            <el-option v-for="item in riskLevelOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="showFilter('workflowStatus')" label="流程状态">
          <el-select v-model="queryParams.workflowStatus" clearable style="width: 150px">
            <el-option v-for="item in workflowStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="showFilter('status')" label="启停状态">
          <el-select v-model="queryParams.status" clearable style="width: 130px">
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
        <el-table-column label="业务编号" prop="businessNo" width="170" show-overflow-tooltip />
        <el-table-column :label="config.businessNameLabel || '业务名称'" prop="businessName" min-width="180" show-overflow-tooltip />
        <el-table-column label="统计月份" prop="statMonth" width="110" />
        <el-table-column label="区域" width="130">
          <template #default="scope">
            <span>{{ formatRegionName(scope.row.regionCode, scope.row.regionCode || '-') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="企业名称" prop="enterpriseName" min-width="180" show-overflow-tooltip />
        <el-table-column label="人员姓名" prop="personName" width="110" show-overflow-tooltip />
        <el-table-column label="风险" width="90">
          <template #default="scope">
            <dict-tag :options="riskLevelOptions" :value="scope.row.riskLevel" />
          </template>
        </el-table-column>
        <el-table-column :label="fieldLabel('amount', '金额')" prop="amount" width="120" />
        <el-table-column label="流程状态" width="110">
          <template #default="scope">
            <dict-tag :options="workflowStatusOptions" :value="scope.row.workflowStatus" />
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
            <el-button v-if="!config.readOnly" link type="primary" @click="handleUpdate(scope.row)" v-hasPermi="[`${config.permPrefix}:edit`]">修改</el-button>
            <el-button v-if="!config.readOnly" link type="danger" @click="handleDelete(scope.row)" v-hasPermi="[`${config.permPrefix}:remove`]">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>

    <el-dialog v-if="!config.readOnly" :title="title" v-model="open" width="820px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="128px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="业务编号" prop="businessNo">
              <el-input v-model="form.businessNo" placeholder="留空自动生成" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="config.businessNameLabel || '业务名称'" prop="businessName">
              <el-input v-model="form.businessName" :placeholder="config.businessNamePlaceholder || '请输入业务名称'" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="统计月份" prop="statMonth">
              <el-date-picker v-model="form.statMonth" type="month" value-format="YYYY-MM" format="YYYY-MM" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="区域编码" prop="regionCode">
              <el-input v-model="form.regionCode" maxlength="12" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="企业" prop="enterpriseId">
              <el-select v-model="form.enterpriseId" :clearable="!enterpriseFilterLocked" :disabled="enterpriseFilterLocked" filterable style="width: 100%" @change="handleFormEnterpriseChange">
                <el-option v-for="item in enterpriseOptions" :key="item.enterpriseId" :label="item.enterpriseName" :value="item.enterpriseId" />
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
              <el-select v-model="form.personId" clearable filterable style="width: 100%" @change="handleFormPersonChange" @clear="handleFormPersonClear">
                <el-option v-for="item in personOptions" :key="item.personId" :label="formatPersonOptionLabel(item)" :value="item.personId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="人员姓名" prop="personName">
              <el-input v-model="form.personName" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="风险等级" prop="riskLevel">
              <el-select v-model="form.riskLevel" clearable style="width: 100%">
                <el-option v-for="item in riskLevelOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
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
            <el-form-item :label="fieldLabel('amount', '金额')" prop="amount">
              <el-input-number v-model="form.amount" :min="0" :precision="2" controls-position="right" :placeholder="fieldPlaceholder('amount', '请输入金额')" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="fieldLabel('quantity', '数量')" prop="quantity">
              <el-input-number v-model="form.quantity" :min="0" :precision="2" controls-position="right" :placeholder="fieldPlaceholder('quantity', '请输入数量')" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="fieldLabel('ownerName', '责任人')" prop="ownerName">
              <el-input v-model="form.ownerName" :placeholder="fieldPlaceholder('ownerName', '请输入责任人')" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="fieldLabel('contactPhone', '联系电话')" prop="contactPhone">
              <el-input v-model="form.contactPhone" :placeholder="fieldPlaceholder('contactPhone', '请输入联系电话')" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="fieldLabel('eventTime', '事件时间')" prop="eventTime">
              <el-date-picker v-model="form.eventTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="fieldLabel('sourceLabel', '来源')" prop="sourceLabel">
              <el-input v-model="form.sourceLabel" :placeholder="fieldPlaceholder('sourceLabel', '请输入来源')" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item :label="fieldLabel('contentText', '业务内容')" prop="contentText">
              <el-input v-model="form.contentText" type="textarea" :rows="4" :placeholder="fieldPlaceholder('contentText', '请输入业务内容')" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item :label="fieldLabel('handleResult', '处置结果')" prop="handleResult">
              <el-input v-model="form.handleResult" type="textarea" :rows="3" :placeholder="fieldPlaceholder('handleResult', '请输入处置结果')" />
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

    <el-drawer v-model="detailOpen" title="业务详情" size="720px">
      <el-descriptions v-if="detail" :column="2" border>
        <el-descriptions-item label="业务编号">{{ detail.businessNo || '-' }}</el-descriptions-item>
        <el-descriptions-item :label="config.businessNameLabel || '业务名称'">{{ detail.businessName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="统计月份">{{ detail.statMonth || '-' }}</el-descriptions-item>
        <el-descriptions-item label="区域">{{ formatRegionName(detail.regionCode, detail.regionCode || '-') }}</el-descriptions-item>
        <el-descriptions-item label="企业名称">{{ detail.enterpriseName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="人员姓名">{{ detail.personName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="风险等级"><dict-tag :options="riskLevelOptions" :value="detail.riskLevel" /></el-descriptions-item>
        <el-descriptions-item label="流程状态"><dict-tag :options="workflowStatusOptions" :value="detail.workflowStatus" /></el-descriptions-item>
        <el-descriptions-item :label="fieldLabel('amount', '金额')">{{ detail.amount ?? '-' }}</el-descriptions-item>
        <el-descriptions-item :label="fieldLabel('quantity', '数量')">{{ detail.quantity ?? '-' }}</el-descriptions-item>
        <el-descriptions-item :label="fieldLabel('ownerName', '责任人')">{{ detail.ownerName || '-' }}</el-descriptions-item>
        <el-descriptions-item :label="fieldLabel('contactPhone', '联系电话')">{{ detail.contactPhone || '-' }}</el-descriptions-item>
        <el-descriptions-item :label="fieldLabel('eventTime', '事件时间')">{{ detail.eventTime || '-' }}</el-descriptions-item>
        <el-descriptions-item :label="fieldLabel('sourceLabel', '来源')">{{ detail.sourceLabel || '-' }}</el-descriptions-item>
        <el-descriptions-item :label="fieldLabel('contentText', '业务内容')" :span="2">{{ detail.contentText || '-' }}</el-descriptions-item>
        <el-descriptions-item :label="fieldLabel('handleResult', '处置结果')" :span="2">{{ detail.handleResult || '-' }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detail.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-drawer>
  </div>
</template>

<script setup>
import { Check } from '@element-plus/icons-vue'
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

const riskLevelOptions = [
  { label: '正常', value: '0' },
  { label: '低风险', value: '1' },
  { label: '中风险', value: '2' },
  { label: '高风险', value: '3' }
]

const defaultVisibleFilters = ['statMonth', 'regionCode', 'businessName', 'enterpriseName', 'workflowStatus', 'status']
const enterpriseFilterLocked = computed(() => isEnterpriseFilterLocked())
const formRules = computed(() => ({
  businessName: [{ required: true, message: `${props.config.businessNameLabel || '业务名称'}不能为空`, trigger: 'blur' }]
}))

const data = reactive({
  queryParams: applyLockedEnterpriseQuery({
    pageNum: 1,
    pageSize: 10,
    portalCode: props.config.portalCode || 'ygb',
    statMonth: undefined,
    regionCode: authorizedDefaultRegionCode(props.config.defaultRegionCode || '440000'),
    enterpriseId: undefined,
    businessName: undefined,
    enterpriseName: undefined,
    personName: undefined,
    riskLevel: undefined,
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
const coreFeatures = computed(() => Array.isArray(props.config.coreFeatures) ? props.config.coreFeatures.filter(Boolean) : [])

function normalizedParams(source) {
  return Object.fromEntries(Object.entries(source).filter(([, value]) => value !== undefined && value !== null && value !== ''))
}

function visibleFilters() {
  return Array.isArray(props.config.filters) && props.config.filters.length ? props.config.filters : defaultVisibleFilters
}

function showFilter(field) {
  return visibleFilters().includes(field)
}

function fieldLabel(field, fallback) {
  const label = props.config.fieldLabels?.[field]
  return label || fallback
}

function fieldPlaceholder(field, fallback) {
  const placeholder = props.config.fieldPlaceholders?.[field]
  return placeholder || fallback
}

function applyRouteQuery() {
  const routeFields = props.config.routeQueryFields || visibleFilters()
  routeFields.forEach(field => {
    if (route.query[field] !== undefined) {
      queryParams.value[field] = route.query[field]
    }
  })
}

function resolveEnterpriseName(enterpriseId) {
  if (enterpriseId == null) return ''
  const matched = enterpriseOptions.value.find(item => Number(item.enterpriseId) === Number(enterpriseId))
  return matched?.enterpriseName || ''
}

function normalizeId(value) {
  return value === undefined || value === null || value === '' ? '' : String(value)
}

function formatPersonOptionLabel(item) {
  if (!item) return ''
  const name = item.personName || `ID:${item.personId}`
  return item.enterpriseName ? `${name} / ${item.enterpriseName}` : name
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
  if (selectedPerson) {
    form.value.personId = selectedPerson.personId
    form.value.personName = selectedPerson.personName || ''
    form.value.enterpriseId = selectedPerson.enterpriseId ?? form.value.enterpriseId
    form.value.enterpriseName = selectedPerson.enterpriseName || form.value.enterpriseName
    form.value.regionCode = selectedPerson.regionCode || form.value.regionCode
  }
}

function resetFormData() {
  form.value = applyEnterpriseScopeToForm({
    businessId: undefined,
    moduleCode: props.config.module,
    businessNo: '',
    businessName: '',
    businessType: props.config.defaultBusinessType || props.config.module,
    portalCode: props.config.portalCode || 'ygb',
    statMonth: queryParams.value.statMonth,
    workflowStatus: 'draft',
    status: '0',
    regionCode: queryParams.value.regionCode,
    enterpriseId: queryParams.value.enterpriseId,
    enterpriseName: '',
    personId: undefined,
    personName: '',
    relatedCode: '',
    sourceLabel: props.config.defaultSourceLabel || '',
    riskLevel: '0',
    handleResult: '',
    ownerName: '',
    contactPhone: '',
    amount: 0,
    quantity: 0,
    eventTime: undefined,
    contentText: '',
    sortOrder: 0,
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
    props.config.summaryApi(normalizedParams({ ...scopedParams, pageNum: undefined, pageSize: undefined }))
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
  return optionselectPerson(normalizedParams(query)).then(response => {
    personOptions.value = response.data || []
  })
}

function loadFormPersonOptions() {
  return loadPersonOptions({ enterpriseId: form.value.enterpriseId })
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
    businessName: undefined,
    enterpriseName: undefined,
    personName: undefined,
    riskLevel: undefined,
    workflowStatus: undefined,
    status: undefined
  })
  applyRouteQuery()
  getList()
}

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.businessId)
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
  const businessId = row?.businessId || ids.value[0]
  props.config.detailApi(businessId).then(response => {
    form.value = applyEnterpriseScopeToForm(response.data || {})
    loadFormPersonOptions()
    open.value = true
    title.value = `修改${props.config.title}`
  })
}

function handleView(row) {
  props.config.detailApi(row.businessId).then(response => {
    detail.value = response.data || null
    detailOpen.value = true
  })
}

function submitForm() {
  proxy.$refs.formRef.validate(valid => {
    if (!valid) return
    const payload = buildSubmitPayload()
    if (!payload) return
    const request = payload.businessId ? props.config.updateApi(payload) : props.config.addApi(payload)
    request.then(() => {
      proxy.$modal.msgSuccess(payload.businessId ? '修改成功' : '新增成功')
      open.value = false
      getList()
    })
  })
}

function buildSubmitPayload() {
  const selectedPerson = findPersonOption(form.value.personId)
  if (form.value.personId && !selectedPerson && !form.value.personName) {
    proxy.$modal.msgWarning('请选择有效人员或填写人员姓名')
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
  return applyEnterpriseScopeToForm({ ...form.value, moduleCode: props.config.module })
}

function handleDelete(row) {
  const businessIds = row?.businessId ? [row.businessId] : ids.value
  proxy.$modal.confirm(`是否确认删除选中的${props.config.title}记录？`).then(() => {
    return props.config.deleteApi(businessIds)
  }).then(() => {
    proxy.$modal.msgSuccess('删除成功')
    getList()
  }).catch(() => {})
}

function handleExport() {
  proxy.download(props.config.exportUrl, normalizedParams({ ...scopedQueryParams(), pageNum: undefined, pageSize: undefined }), `${props.config.filePrefix || 'business_record'}_${Date.now()}.xlsx`)
}

function cancel() {
  open.value = false
  resetFormData()
}

applyRouteQuery()
resetFormData()
loadEnterpriseOptions().finally(() => {
  getList()
})
</script>

<style scoped>
.page-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.page-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.page-desc {
  margin-top: 4px;
  color: var(--el-text-color-secondary);
  font-size: 13px;
}

.summary-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(140px, 1fr));
  gap: 12px;
  margin-bottom: 16px;
}

.core-feature-panel {
  margin-bottom: 16px;
  padding: 12px 14px;
  border: 1px solid #e3ebf3;
  border-radius: 6px;
  background: #fbfdff;
}

.core-feature-title {
  margin-bottom: 10px;
  color: var(--el-text-color-primary);
  font-size: 14px;
  font-weight: 600;
}

.core-feature-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 8px 14px;
}

.core-feature-item {
  display: flex;
  align-items: center;
  gap: 6px;
  min-width: 0;
  color: var(--el-text-color-regular);
  font-size: 13px;
  line-height: 20px;
}

.core-feature-item .el-icon {
  flex: 0 0 auto;
  color: #1f7a4d;
}

.core-feature-item span {
  min-width: 0;
  overflow-wrap: anywhere;
}

.summary-card {
  display: flex;
  flex-direction: column;
  gap: 6px;
  padding: 14px 16px;
  border: 1px solid #e7edf3;
  border-radius: 6px;
  background: #f8fafc;
}

.summary-card span {
  color: var(--el-text-color-secondary);
  font-size: 13px;
}

.summary-card strong {
  color: #123b67;
  font-size: 24px;
  font-weight: 600;
}
</style>
