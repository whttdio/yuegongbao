<template>
  <div class="app-container ygb-page">
    <section class="gov-page-header ygb-page__header">
      <div>
        <p class="ygb-page__eyebrow">主数据底座</p>
        <h1 class="ygb-page__title">企业主数据办理台账</h1>
        <p class="ygb-page__desc">
          统一维护派遣单位、用工单位和服务机构主数据，作为人员归属、合同备案、考勤汇聚、工资监管和社保税务联动的企业底座。
          当前页面按“先看承接范围，再看同步状态，再进入维护动作”的方式重组，减少企业管理员在多个模块之间来回跳转。
        </p>
      </div>
      <div class="ygb-table-tip">
        当前页面聚焦 PC 办理链路。后续对接市场监管、银行、社保等外部系统时，继续复用同一套企业主数据和同步状态，不拆后台表。
      </div>
    </section>

    <div class="ygb-summary-grid">
      <div v-for="item in resolvedSummaryCards" :key="item.key" class="ygb-summary-card" :class="item.cardClass">
        <div class="ygb-summary-card__label">{{ item.label }}</div>
        <div class="ygb-summary-card__value">
          {{ item.value }}
          <span class="ygb-summary-card__unit">{{ item.unit }}</span>
        </div>
        <div class="ygb-summary-card__note">{{ item.note }}</div>
      </div>
    </div>


    <el-card class="search-card ygb-search-card" shadow="never">
      <el-form ref="queryRef" :model="queryParams" :inline="true" v-show="showSearch">
        <el-form-item label="区域">
          <el-select v-model="queryParams.regionCode" clearable style="width: 180px">
            <el-option v-for="item in regionOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="企业名称" prop="enterpriseName">
          <el-input
            v-model="queryParams.enterpriseName"
            placeholder="请输入企业名称"
            clearable
            style="width: 220px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="统一代码" prop="enterpriseCode">
          <el-input
            v-model="queryParams.enterpriseCode"
            placeholder="请输入统一社会信用代码"
            clearable
            style="width: 220px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="企业类型" prop="enterpriseType">
          <el-select v-model="queryParams.enterpriseType" placeholder="请选择企业类型" clearable style="width: 160px">
            <el-option v-for="item in enterpriseTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="同步状态" prop="syncStatus">
          <el-select v-model="queryParams.syncStatus" placeholder="请选择同步状态" clearable style="width: 160px">
            <el-option v-for="item in syncStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 140px">
            <el-option v-for="dict in sys_normal_disable" :key="dict.value" :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">查询</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="toolbar-card ygb-toolbar-card" shadow="never">
      <el-row :gutter="10">
        <el-col v-if="!isReadOnlyRole" :span="1.5">
          <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['ygb:enterprise:add']">新增</el-button>
        </el-col>
        <el-col v-if="!isReadOnlyRole" :span="1.5">
          <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['ygb:enterprise:edit']">修改</el-button>
        </el-col>
        <el-col v-if="!isReadOnlyRole" :span="1.5">
          <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['ygb:enterprise:remove']">删除</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['ygb:enterprise:export']">导出</el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>
    </el-card>

    <el-card class="table-card ygb-table-card" shadow="never">
      <el-table
        v-loading="loading"
        :data="enterpriseList"
        @selection-change="handleSelectionChange"
        @row-click="handleRowClick"
      >
        <el-table-column v-if="!isReadOnlyRole" type="selection" width="55" align="center" />
        <el-table-column label="企业ID" prop="enterpriseId" width="96" />
        <el-table-column label="企业名称" prop="enterpriseName" min-width="220" />
        <el-table-column label="统一社会信用代码" prop="enterpriseCode" width="190" />
        <el-table-column label="企业类型" prop="enterpriseType" width="120">
          <template #default="scope">
            <dict-tag :options="enterpriseTypeOptions" :value="scope.row.enterpriseType" />
          </template>
        </el-table-column>
        <el-table-column label="区域" width="140">
          <template #default="scope">
            <span>{{ formatRegionName(scope.row.regionCode) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="联系人" prop="contactPerson" width="120" />
        <el-table-column label="联系电话" prop="contactPhone" width="130" />
        <el-table-column label="同步状态" prop="syncStatus" width="110">
          <template #default="scope">
            <dict-tag :options="syncStatusOptions" :value="scope.row.syncStatus" />
          </template>
        </el-table-column>
        <el-table-column label="状态" prop="status" width="90">
          <template #default="scope">
            <dict-tag :options="sys_normal_disable" :value="scope.row.status" />
          </template>
        </el-table-column>
        <el-table-column label="成立日期" width="120">
          <template #default="scope">
            <span>{{ parseTime(scope.row.establishedDate, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column class-name="table-fill-column" min-width="1" />

        <el-table-column label="操作" fixed="right" align="center" :width="isReadOnlyRole ? 90 : 220" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-button link type="info" icon="View" @click.stop="openDetail(scope.row)">详情</el-button>
            <el-button v-if="!isReadOnlyRole" link type="primary" icon="Edit" @click.stop="handleUpdate(scope.row)" v-hasPermi="['ygb:enterprise:edit']">修改</el-button>
            <el-button v-if="!isReadOnlyRole" link type="danger" icon="Delete" @click.stop="handleDelete(scope.row)" v-hasPermi="['ygb:enterprise:remove']">删除</el-button>
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

    <el-dialog :title="title" v-model="open" width="820px" append-to-body>
      <el-form ref="enterpriseRef" :model="form" :rules="rules" label-width="120px">
        <div class="ygb-panel-grid">
          <el-form-item label="企业名称" prop="enterpriseName">
            <el-input v-model="form.enterpriseName" placeholder="请输入企业名称" />
          </el-form-item>
          <el-form-item label="统一社会信用代码" prop="enterpriseCode">
            <el-input v-model="form.enterpriseCode" placeholder="请输入统一社会信用代码" />
          </el-form-item>
          <el-form-item label="企业类型" prop="enterpriseType">
            <el-select v-model="form.enterpriseType" placeholder="请选择企业类型">
              <el-option v-for="item in enterpriseTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="区域编码" prop="regionCode">
            <el-input v-model="form.regionCode" placeholder="请输入 6 位区域编码" maxlength="6" />
          </el-form-item>
          <el-form-item label="法定代表人" prop="legalPerson">
            <el-input v-model="form.legalPerson" placeholder="请输入法定代表人" />
          </el-form-item>
          <el-form-item label="联系人" prop="contactPerson">
            <el-input v-model="form.contactPerson" placeholder="请输入联系人" />
          </el-form-item>
          <el-form-item label="联系电话" prop="contactPhone">
            <el-input v-model="form.contactPhone" placeholder="请输入联系电话" />
          </el-form-item>
          <el-form-item label="成立日期" prop="establishedDate">
            <el-date-picker
              v-model="form.establishedDate"
              type="date"
              placeholder="请选择成立日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              style="width: 100%"
            />
          </el-form-item>
          <el-form-item label="同步状态" prop="syncStatus">
            <el-radio-group v-model="form.syncStatus">
              <el-radio v-for="item in syncStatusOptions" :key="item.value" :value="item.value">{{ item.label }}</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <el-radio-group v-model="form.status">
              <el-radio v-for="dict in sys_normal_disable" :key="dict.value" :value="dict.value">{{ dict.label }}</el-radio>
            </el-radio-group>
          </el-form-item>
        </div>
        <el-form-item label="企业地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入企业地址" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确定</el-button>
          <el-button @click="cancel">取消</el-button>
        </div>
      </template>
    </el-dialog>

    <page-detail-dialog v-model="detailOpen" title="企业详情" width="760px">
      <template v-if="detailEnterprise">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="企业名称">{{ detailEnterprise.enterpriseName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="企业类型">{{ optionLabel(enterpriseTypeOptions, detailEnterprise.enterpriseType) }}</el-descriptions-item>
          <el-descriptions-item label="统一社会信用代码" :span="2">{{ detailEnterprise.enterpriseCode || '-' }}</el-descriptions-item>
          <el-descriptions-item label="区域">{{ formatRegionName(detailEnterprise.regionCode) }}</el-descriptions-item>
          <el-descriptions-item label="同步状态">{{ optionLabel(syncStatusOptions, detailEnterprise.syncStatus) }}</el-descriptions-item>
          <el-descriptions-item label="状态">{{ optionLabel(sys_normal_disable.value, detailEnterprise.status) }}</el-descriptions-item>
          <el-descriptions-item label="成立日期">{{ parseTime(detailEnterprise.establishedDate, '{y}-{m}-{d}') || '-' }}</el-descriptions-item>
          <el-descriptions-item label="法定代表人">{{ detailEnterprise.legalPerson || '-' }}</el-descriptions-item>
          <el-descriptions-item label="联系人">{{ detailEnterprise.contactPerson || '-' }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ detailEnterprise.contactPhone || '-' }}</el-descriptions-item>
          <el-descriptions-item label="企业地址" :span="2">{{ detailEnterprise.address || '-' }}</el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ detailEnterprise.remark || '-' }}</el-descriptions-item>
        </el-descriptions>

        <div class="ygb-detail-block">
          <h3>办理提示</h3>
          <div class="ygb-tag-list">
            <el-tag v-for="item in resolvedDetailHintTags" :key="item.label" :type="item.type" effect="plain">{{ item.label }}</el-tag>
          </div>
        </div>
      </template>
    </page-detail-dialog>
  </div>
</template>

<script setup name="YgbEnterprise">
import { computed, getCurrentInstance, watchEffect } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useWorkbenchAssist } from '@/composables/useWorkbenchAssist'
import { useRoleViewMode } from '@/utils/roleView'
import { decoratePortalExplanationItems, openPortalExplanationAction, resolvePortalExplanationSummary } from '@/utils/portalExplanation'
import { applyWorkbenchRouteQuery, buildEnterpriseWorkbenchLinks, buildWorkbenchContext, openWorkbenchLink, stripWorkbenchRouteQuery } from '@/utils/workbenchLink'
import { useAuthorizedRegionOptions } from '@/utils/regionScope'
import {
  enterpriseTypeOptions,
  formatRegionName,
  optionLabel,
  regionOptions as allRegionOptions,
  syncStatusOptions,
  useEnterprisePage
} from '@/views/enterprise/useEnterprisePage'

const { proxy } = getCurrentInstance()
const route = useRoute()
const { setPageGuide } = useWorkbenchAssist()
const router = useRouter()
const { sys_normal_disable } = useDict('sys_normal_disable')
const { isReadOnlyRole, readOnlyRoleLabel, readOnlyRoleDescription } = useRoleViewMode()
const regionOptions = useAuthorizedRegionOptions(allRegionOptions)
const enterpriseWorkbenchFields = ['regionCode', 'enterpriseType', 'syncStatus', 'status']
const enterpriseInitialQuery = {}
applyWorkbenchRouteQuery(route.query, enterpriseInitialQuery, enterpriseWorkbenchFields)

const {
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
  single,
  multiple,
  queryParams,
  form,
  rules,
  getList,
  cancel,
  handleQuery,
  handleSelectionChange,
  handleRowClick,
  handleAdd,
  handleUpdate,
  openDetail,
  submitForm,
  handleDelete,
  handleExport
} = useEnterprisePage({
  exportFilePrefix: 'ygb_enterprise',
  initialQueryParams: enterpriseInitialQuery,
  canMutate: () => !isReadOnlyRole.value,
  onBlockedAction: blockReadOnlyAction,
  actionText: {
    add: '新增企业',
    edit: '修改企业',
    delete: '删除企业'
  },
  dialogTitle: {
    add: '新增企业',
    edit: '修改企业'
  },
  messages: {
    addSuccess: '新增成功',
    editSuccess: '修改成功',
    deleteSuccess: '删除成功',
    deleteConfirm: enterpriseNames => `是否确认删除企业“${enterpriseNames}”的数据项？`
  }
})

function buildEnterpriseExplanationQuery(extraQuery = {}) {
  return {
    regionCode: queryParams.value.regionCode,
    ...extraQuery
  }
}

const fallbackPortalExplanations = computed(() => ([
  {
    key: 'dispatch',
    dimensionName: '派遣单位覆盖',
    currentValue: summaryData.value.dispatchCount != null ? summaryData.value.dispatchCount : 0,
    targetValue: '持续完整',
    summary: '先核对派遣单位底座，再进入人员归属、合同备案和考勤归集链路，避免办理口径失真。',
    evidenceModule: 'enterprise',
    recommendModule: 'enterprise',
    defaultQuery: buildEnterpriseExplanationQuery({ enterpriseType: '1' }),
    sourceLabel: '530.1 企业主数据解释',
    sourceDescription: '从企业主数据继续核对派遣单位承接范围。'
  },
  {
    key: 'employer',
    dimensionName: '用工单位覆盖',
    currentValue: summaryData.value.employerCount != null ? summaryData.value.employerCount : 0,
    targetValue: '持续完整',
    summary: '用工单位底座会直接影响人员在岗、工资发放和社保税务比对，需优先保证口径稳定。',
    evidenceModule: 'enterprise',
    recommendModule: 'enterprise',
    defaultQuery: buildEnterpriseExplanationQuery({ enterpriseType: '2' }),
    sourceLabel: '530.1 企业主数据解释',
    sourceDescription: '从企业主数据继续核对用工单位覆盖情况。'
  },
  {
    key: 'syncError',
    dimensionName: '同步异常',
    currentValue: summaryData.value.syncErrorCount != null ? summaryData.value.syncErrorCount : 0,
    targetValue: '0',
    summary: '同步异常会阻断企业底座向后续办理模块传递，应优先回到企业台账补齐同步状态。',
    evidenceModule: 'enterprise',
    recommendModule: 'enterprise',
    defaultQuery: buildEnterpriseExplanationQuery({ syncStatus: '2' }),
    sourceLabel: '530.1 企业主数据解释',
    sourceDescription: '从企业主数据直接回看同步异常对象。'
  },
  {
    key: 'disabled',
    dimensionName: '停用企业',
    currentValue: summaryData.value.disabledCount != null ? summaryData.value.disabledCount : 0,
    targetValue: '0',
    summary: '停用企业应先核对是否仍在承接人员、合同或考勤链路，避免后续办理继续落到失效主体。',
    evidenceModule: 'enterprise',
    recommendModule: 'enterprise',
    defaultQuery: buildEnterpriseExplanationQuery({ status: '1' }),
    sourceLabel: '530.1 企业主数据解释',
    sourceDescription: '从企业主数据继续核查停用主体是否已完全退出办理链。'
  }
]))

const portalExplanations = computed(() => {
  const aggregated = Array.isArray(summaryData.value.ygbExplanation) ? summaryData.value.ygbExplanation : []
  return aggregated.length ? aggregated : fallbackPortalExplanations.value
})

const portalExplanationSummary = computed(() => resolvePortalExplanationSummary(portalExplanationItems.value, ''))
const leadingPortalExplanation = computed(() => portalExplanationItems.value[0] || null)

function resolvePortalExplanationItem(index = 0) {
  return portalExplanationItems.value[index] || leadingPortalExplanation.value || null
}

function buildPortalExplanationLabel(item) {
  if (!item) return ''
  const parts = [item.dimensionName, item.moduleLabel || item.moduleCode].filter(Boolean)
  return Array.from(new Set(parts)).join(' / ')
}

function resolveExplanationFirstText(fallback, explanation) {
  const label = buildPortalExplanationLabel(explanation)
  const summary = explanation?.summary || explanation?.explanationSummary || explanation?.sourceDescription || ''
  if (label && summary) {
    return `${label}: ${summary}`
  }
  if (summary) {
    return summary
  }
  if (label) {
    return `${label} first`
  }
  return fallback
}

function buildExplanationFirstTags(tags = []) {
  const extras = []
  const label = buildPortalExplanationLabel(leadingPortalExplanation.value)
  if (label) {
    extras.push({ label: `530.1主解释：${label}`, type: 'warning' })
  }
  if (portalExplanationSummary.value) {
    extras.push({ label: portalExplanationSummary.value, type: 'success' })
  }
  return [...extras, ...tags].slice(0, 4)
}

const portalExplanationItems = computed(() => decoratePortalExplanationItems(portalExplanations.value, {
  portalCode: 'ygb',
  panelTitle: '530.1 办理链说明',
  panelDescription: '企业主数据页面按办理链顺序解释派遣覆盖、用工覆盖、同步异常和停用风险。'
}))

function handlePortalExplanationAction(action) {
  if (action?.path === route.path) {
    applyEnterpriseWorkbenchQuery(action.query || {})
  }
  openPortalExplanationAction(router, action)
}

const summaryCards = computed(() => ([
  {
    key: 'total',
    label: '企业台账总量',
    value: summaryData.value.totalCount != null ? summaryData.value.totalCount : total.value,
    unit: '家',
    note: '当前筛选范围内可继续承接人员、合同、考勤和工资监管的企业主数据总量。',
    cardClass: ''
  },
  {
    key: 'dispatch',
    label: '派遣单位',
    value: summaryData.value.dispatchCount != null ? summaryData.value.dispatchCount : 0,
    unit: '家',
    note: '优先用于承接人员归属、派遣协议、合同备案和后续派遣链路办理。',
    cardClass: 'ygb-summary-card--primary'
  },
  {
    key: 'employer',
    label: '用工单位',
    value: summaryData.value.employerCount != null ? summaryData.value.employerCount : 0,
    unit: '家',
    note: '用于承接考勤汇聚、工资发放、社保税务联动和企业侧闭环办理。',
    cardClass: 'ygb-summary-card--success'
  },
  {
    key: 'syncError',
    label: '同步异常',
    value: summaryData.value.syncErrorCount != null ? summaryData.value.syncErrorCount : 0,
    unit: '家',
    note: '进入后续办理前应优先修复同步异常，避免人员、合同和监管数据链断裂。',
    cardClass: 'ygb-summary-card--warning'
  }
]))

const resolvedSummaryCards = computed(() => summaryCards.value.map((item, index) => ({
  ...item,
  note: resolveExplanationFirstText(item.note, resolvePortalExplanationItem(index))
})))

const workbenchContext = computed(() => buildWorkbenchContext(route.query, {
  fields: enterpriseWorkbenchFields,
  sourceLabel: '对象工作台',
  title: '当前企业台账页沿用了工作台来源条件',
  description: '列表已按工作台带入的行政区划锁定，可直接继续查看该区域企业对象并下钻后续业务。',
  fieldLabels: {
    regionCode: '行政区划',
    enterpriseType: '企业类型',
    syncStatus: '同步状态',
    status: '启停状态'
  },
  fieldFormatters: {
    regionCode: value => formatRegionName(value, value),
    enterpriseType: value => optionLabel(enterpriseTypeOptions, value, value),
    syncStatus: value => optionLabel(syncStatusOptions, value, value),
    status: value => optionLabel(sys_normal_disable.value, value, value)
  }
}))

const focusItems = computed(() => ([
  { label: '当前区域', value: formatRegionName(queryParams.value.regionCode) },
  { label: '正常企业', value: `${summaryData.value.normalCount != null ? summaryData.value.normalCount : 0} 家` },
  { label: '服务机构', value: `${summaryData.value.serviceOrgCount != null ? summaryData.value.serviceOrgCount : 0} 家` },
  { label: '停用企业', value: `${summaryData.value.disabledCount != null ? summaryData.value.disabledCount : 0} 家` }
]))

const resolvedFocusItems = computed(() => focusItems.value.map((item, index) => ({
  ...item,
  label: resolvePortalExplanationItem(index)?.dimensionName || item.label
})))

const selectedEnterpriseOverview = computed(() => {
  if (!currentEnterprise.value) {
    return [
      { label: '企业类型', value: '-' },
      { label: '同步状态', value: '-' },
      { label: '状态', value: '-' },
      { label: '联系人', value: '-' }
    ]
  }
  return [
    { label: '企业类型', value: optionLabel(enterpriseTypeOptions, currentEnterprise.value.enterpriseType) },
    { label: '同步状态', value: optionLabel(syncStatusOptions, currentEnterprise.value.syncStatus) },
    { label: '状态', value: optionLabel(sys_normal_disable.value, currentEnterprise.value.status) },
    { label: '联系人', value: currentEnterprise.value.contactPerson || '-' }
  ]
})

const businessSteps = [
  {
    label: '先补企业主数据',
    desc: '先确认企业归属、区域、联系人和同步状态，避免后续办理入口挂错主体。'
  },
  {
    label: '再承接人员归属',
    desc: '企业基础信息稳定后，再进入人员管理，补齐在岗人员、证书和参保关联。'
  },
  {
    label: '进入合同与考勤',
    desc: '派遣单位和用工单位关系明确后，再做合同备案、考勤上报和月度汇聚。'
  },
  {
    label: '联动工资与监管',
    desc: '最后承接工资批次、社保税务比对和预警闭环，形成企业办理台账。'
  }
]

const resolvedBusinessSteps = computed(() => businessSteps.map((item, index) => ({
  ...item,
  label: resolvePortalExplanationItem(index)?.dimensionName || item.label,
  desc: resolveExplanationFirstText(item.desc, resolvePortalExplanationItem(index))
})))

const enterpriseHintTags = computed(() => buildHintTags(currentEnterprise.value))
const detailHintTags = computed(() => buildHintTags(detailEnterprise.value || currentEnterprise.value))
const resolvedEnterpriseHintTags = computed(() => buildExplanationFirstTags(enterpriseHintTags.value))
const resolvedDetailHintTags = computed(() => buildExplanationFirstTags(detailHintTags.value))
const workbenchLinks = computed(() => buildEnterpriseWorkbenchLinks({
  proxy,
  enterprise: currentEnterprise.value,
  portalCode: 'ygb'
}))
const submoduleEntries = [
  { key: 'regulator', title: '监管单位', desc: '从企业主表筛出监管单位并继续维护主数据。', path: '/ygb-foundation/enterpriseRegulator', kind: 'category', enterpriseType: '4' },
  { key: 'dispatch', title: '劳务派遣公司', desc: '聚焦派遣主体，继续承接人员归属和合同链路。', path: '/ygb-foundation/enterpriseDispatch', kind: 'category', enterpriseType: '1' },
  { key: 'employer', title: '用工单位', desc: '聚焦用工主体，继续承接考勤、工资和联动治理。', path: '/ygb-foundation/enterpriseEmployer', kind: 'category', enterpriseType: '2' },
  { key: 'highRisk', title: '高危企业库', desc: '维护高危企业标识、风险来源和闭环状态。', path: '/ygb-foundation/enterpriseHighRisk', kind: 'record' },
  { key: 'relation', title: '派遣用工关联', desc: '维护派遣单位与用工单位关系及回写状态。', path: '/ygb-foundation/enterpriseRelation', kind: 'record' },
  { key: 'union', title: '工会管理', desc: '维护企业工会组织和协同信息。', path: '/ygb-foundation/enterpriseUnion', kind: 'record' }
]

function resetQuery() {
  proxy.resetForm('queryRef')
  Object.assign(queryParams.value, {
    pageNum: 1,
    pageSize: 10,
    regionCode: undefined,
    enterpriseName: undefined,
    enterpriseCode: undefined,
    enterpriseType: undefined,
    syncStatus: undefined,
    status: undefined
  })
  applyWorkbenchRouteQuery(route.query, queryParams.value, enterpriseWorkbenchFields)
  getList()

watchEffect(() => {
  setPageGuide({
    title: '?????????' || '?????????',
    description: '???????????????????????' || '???????????????????????',
    portalExplanation: portalExplanationItems.value,
    focus: [],
    selection: selectedEnterpriseOverview.value,
    workflow: [],
    hints: [...resolvedDetailHintTags.value].slice(0, 6)
  })
})

}

function clearWorkbenchContext() {
  Object.assign(queryParams.value, {
    pageNum: 1,
    regionCode: undefined,
    enterpriseType: undefined,
    syncStatus: undefined,
    status: undefined
  })
  router.replace({
    path: route.path,
    query: stripWorkbenchRouteQuery(route.query, enterpriseWorkbenchFields)
  })
  getList()
}

function applyEnterpriseWorkbenchQuery(routeQuery = {}) {
  Object.assign(queryParams.value, {
    pageNum: 1,
    pageSize: 10,
    regionCode: undefined,
    enterpriseName: undefined,
    enterpriseCode: undefined,
    enterpriseType: undefined,
    syncStatus: undefined,
    status: undefined
  })
  applyWorkbenchRouteQuery(routeQuery, queryParams.value, enterpriseWorkbenchFields)
  getList()
}

function blockReadOnlyAction(actionLabel) {
  if (!isReadOnlyRole.value) {
    return false
  }
  proxy.$modal.msgWarning(`${readOnlyRoleLabel.value}当前仅保留查看、详情和导出，不能${actionLabel}`)
  return true
}

function handleOpenWorkbenchLink(link) {
  if (openWorkbenchLink(proxy, link)) {
    return
  }
  proxy.$modal.msgWarning('请先选择企业，再进入关联办理模块')
}

function buildSubmoduleQuery(entry) {
  const query = {
    regionCode: queryParams.value.regionCode,
    syncStatus: queryParams.value.syncStatus,
    status: queryParams.value.status
  }
  if (entry.kind === 'record' && currentEnterprise.value?.enterpriseId) {
    query.enterpriseId = currentEnterprise.value.enterpriseId
    query.enterpriseName = currentEnterprise.value.enterpriseName
  }
  if (entry.kind === 'category' && currentEnterprise.value?.enterpriseType === entry.enterpriseType) {
    query.enterpriseName = currentEnterprise.value.enterpriseName
  }
  return Object.fromEntries(Object.entries(query).filter(([, value]) => value !== undefined && value !== null && value !== ''))
}

function openSubmodule(entry) {
  router.push({
    path: entry.path,
    query: buildSubmoduleQuery(entry)
  })
}

function buildHintTags(enterprise) {
  if (!enterprise) {
    return [{ label: '未选中企业，可先在列表中选择办理主体。', type: 'info' }]
  }
  const tags = []
  if (enterprise.syncStatus === '2') {
    tags.push({ label: '同步异常，建议先修复后再进入人员和合同办理。', type: 'danger' })
  }
  if (enterprise.status === '1') {
    tags.push({ label: '当前为停用状态，不建议继续新增办理数据。', type: 'warning' })
  }
  if (!enterprise.contactPhone) {
    tags.push({ label: '缺少联系电话，可能影响企业侧闭环联络。', type: 'warning' })
  }
  if (enterprise.enterpriseType === '1') {
    tags.push({ label: '可优先承接人员归属和派遣协议链路。', type: 'success' })
  }
  if (enterprise.enterpriseType === '2') {
    tags.push({ label: '可继续进入考勤、工资和社保税务联动办理。', type: 'success' })
  }
  if (enterprise.enterpriseType === '3') {
    tags.push({ label: '适合作为服务机构主体承接培训与外部协同。', type: 'info' })
  }
  if (!tags.length) {
    tags.push({ label: '主数据完整，可继续进入后续办理模块。', type: 'success' })
  }
  return tags
}
</script>

<style scoped lang="scss">
.ygb-workbench-alert {
  margin-bottom: 16px;
}

.ygb-workbench-alert__title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.ygb-workbench-alert__desc {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 10px;
  color: #5f6f80;
  line-height: 1.7;
}

.ygb-workbench-alert__desc strong {
  color: #15304b;
}

.ygb-summary-grid,
.ygb-focus-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
  margin-bottom: 16px;
}

.ygb-summary-grid {
  grid-template-columns: repeat(4, minmax(0, 1fr));
}

.ygb-summary-card,
.ygb-focus-card {
  border: 1px solid #dbe5f0;
  border-radius: 16px;
  background: #fff;
}

.ygb-summary-card {
  padding: 18px 20px;
}

.ygb-summary-card__label {
  color: #627486;
  font-size: 13px;
}

.ygb-summary-card__value {
  margin-top: 10px;
  color: #13243a;
  font-size: 28px;
  font-weight: 700;
}

.ygb-summary-card__unit {
  margin-left: 4px;
  font-size: 13px;
  font-weight: 500;
  color: #7b8da1;
}

.ygb-summary-card__note {
  margin-top: 10px;
  color: #5f6f80;
  line-height: 1.7;
  font-size: 13px;
}

.ygb-summary-card--success {
  background: linear-gradient(180deg, #ffffff 0%, #f3fbf5 100%);
}

.ygb-summary-card--warning {
  background: linear-gradient(180deg, #ffffff 0%, #fff9ef 100%);
}

.ygb-summary-card--primary {
  background: linear-gradient(180deg, #ffffff 0%, #f2f7fd 100%);
}

.ygb-focus-card :deep(.el-card__header) {
  padding: 18px 20px 0;
  border-bottom: none;
}

.ygb-focus-card :deep(.el-card__body) {
  padding: 18px 20px 20px;
}

.ygb-card-head__title {
  color: #13243a;
  font-size: 18px;
  font-weight: 700;
}

.ygb-card-head__desc {
  margin-top: 4px;
  color: #7b8da1;
  font-size: 13px;
  line-height: 1.7;
}

.ygb-focus-list,
.ygb-source-list,
.ygb-pipeline-list {
  display: grid;
  gap: 12px;
}

.ygb-focus-item,
.ygb-source-item,
.ygb-pipeline-item {
  border: 1px solid #dde7f1;
  border-radius: 14px;
  background: #f8fbfd;
}

.ygb-focus-item,
.ygb-source-item {
  padding: 14px 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.ygb-focus-item__label,
.ygb-source-item__label {
  color: #627486;
  font-size: 13px;
}

.ygb-focus-item__value,
.ygb-source-item__value {
  color: #13243a;
  font-size: 14px;
  font-weight: 600;
  text-align: right;
}

.ygb-pipeline-item {
  display: grid;
  grid-template-columns: 42px minmax(0, 1fr);
  gap: 14px;
  padding: 16px;
}

.ygb-pipeline-item__index {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  height: 34px;
  border-radius: 10px;
  background: #edf5fc;
  color: #0f5ea8;
  font-weight: 700;
}

.ygb-pipeline-item__body strong {
  color: #13243a;
}

.ygb-pipeline-item__body p {
  margin: 8px 0 0;
  color: #5f6f80;
  font-size: 13px;
  line-height: 1.7;
}

.ygb-tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.ygb-link-card {
  margin-bottom: 16px;
}

.ygb-link-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
}

.ygb-link-item {
  padding: 16px;
  border: 1px solid #dde7f1;
  border-radius: 14px;
  background: #f8fbfd;
  text-align: left;
  cursor: pointer;
  transition: border-color 0.2s ease, box-shadow 0.2s ease, transform 0.2s ease;
}

.ygb-link-item:hover:not(:disabled) {
  border-color: #9db8d6;
  box-shadow: 0 8px 20px rgba(15, 94, 168, 0.08);
  transform: translateY(-1px);
}

.ygb-link-item:disabled {
  cursor: not-allowed;
  opacity: 0.62;
}

.ygb-link-item__title {
  color: #13243a;
  font-size: 14px;
  font-weight: 700;
}

.ygb-link-item__desc {
  margin-top: 8px;
  color: #5f6f80;
  font-size: 13px;
  line-height: 1.7;
}

.ygb-link-item__action {
  margin-top: 12px;
  color: #0f5ea8;
  font-size: 12px;
  font-weight: 600;
}

.ygb-detail-block {
  margin-top: 20px;
}

.ygb-detail-block h3 {
  margin: 0 0 12px;
  color: #13243a;
  font-size: 16px;
}

@media (max-width: 1200px) {
  .ygb-summary-grid,
  .ygb-focus-grid,
  .ygb-link-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 768px) {
  .ygb-summary-grid,
  .ygb-focus-grid {
    grid-template-columns: 1fr;
  }
}
</style>
