<template>
  <div class="app-container azb-page">
    <section class="gov-page-header azb-page__header">
      <div>
        <p class="azb-page__eyebrow">企业主数据底座</p>
        <h1 class="azb-page__title">企业归属、同步状态与治理对象台账</h1>
        <p class="azb-page__desc">
          面向应急监管、保险协同和企业现场治理统一查看企业主数据、区域归属、同步状态和联系人信息。
          页面按“先看治理对象范围，再锁定当前企业，再维护台账”的安责保视角重组，继续复用共享企业主数据接口。
        </p>
      </div>
      <div class="azb-page__tip">
        <div class="azb-page__tip-item">优先关注：同步异常企业、停用企业、重点区域企业和高频治理对象。</div>
        <div class="azb-page__tip-item">企业主数据会影响设备、安责险、高处作业和预警工单的归属关系。</div>
        <div class="azb-page__tip-item">当前行业标签仍复用共享底座，后续可扩展安责保专属风险分层。</div>
      </div>
    </section>

    <el-alert
      v-if="isReadOnlyRole"
      class="azb-role-alert"
      type="info"
      :closable="false"
      :title="readOnlyAlertTitle"
      :description="readOnlyAlertDescription"
    />

    <el-alert
      v-if="workbenchContext"
      class="azb-workbench-alert"
      type="info"
      :closable="false"
      show-icon
    >
      <template #title>
        <div class="azb-workbench-alert__title">
          <span>{{ workbenchContext.title }}</span>
          <el-button link type="primary" @click="clearWorkbenchContext">清空来源条件</el-button>
        </div>
      </template>
      <div class="azb-workbench-alert__desc">
        <strong>{{ workbenchContext.sourceLabel }}</strong>
        <span>{{ workbenchContext.description }}</span>
      </div>
      <div class="azb-risk-tags">
        <el-tag v-for="item in workbenchContext.tags" :key="item.key" effect="plain">{{ item.label }}：{{ item.value }}</el-tag>
      </div>
    </el-alert>

    <div class="azb-summary-grid">
      <div v-for="item in resolvedSummaryCards" :key="item.key" class="azb-summary-card" :class="item.cardClass">
        <div class="azb-summary-card__label">{{ item.label }}</div>
        <div class="azb-summary-card__value">
          {{ item.value }}
          <span class="azb-summary-card__unit">{{ item.unit }}</span>
        </div>
        <div class="azb-summary-card__note">{{ item.note }}</div>
      </div>
    </div>


    <el-card class="search-card azb-search-card" shadow="never">
      <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
        <el-form-item label="区域">
          <el-select v-model="queryParams.regionCode" clearable style="width: 180px">
            <el-option v-for="item in regionOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="企业名称">
          <el-input v-model="queryParams.enterpriseName" placeholder="请输入企业名称" clearable style="width: 220px" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="统一代码">
          <el-input v-model="queryParams.enterpriseCode" placeholder="请输入统一社会信用代码" clearable style="width: 220px" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="企业类型">
          <el-select v-model="queryParams.enterpriseType" clearable style="width: 160px">
            <el-option v-for="item in enterpriseTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="同步状态">
          <el-select v-model="queryParams.syncStatus" clearable style="width: 160px">
            <el-option v-for="item in syncStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" clearable style="width: 140px">
            <el-option v-for="dict in sys_normal_disable" :key="dict.value" :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="toolbar-card azb-toolbar-card" shadow="never">
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

    <el-card class="table-card azb-table-card" shadow="never">
      <template #header>
        <div class="azb-card-head azb-card-head--between">
          <div>
            <div class="azb-card-head__title">企业归属治理台账</div>
            <div class="azb-card-head__desc">{{ focusTableHint }}</div>
          </div>
          <div class="azb-card-head__desc">当前总量 {{ total }} 家</div>
        </div>
      </template>
      <el-table
        v-loading="loading"
        :data="visibleEnterpriseList"
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
        <div class="azb-panel-grid">
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

        <div class="azb-detail-block">
          <h3>安责保治理提示</h3>
          <div class="azb-risk-tags">
            <el-tag v-for="item in resolvedDetailRiskTags" :key="item.label" :type="item.type" effect="plain">{{ item.label }}</el-tag>
          </div>
        </div>
      </template>
    </page-detail-dialog>
  </div>
</template>

<script setup name="AzbEnterprise">
import { computed, getCurrentInstance, ref, watchEffect } from 'vue'
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
const regionOptions = useAuthorizedRegionOptions(allRegionOptions)
const route = useRoute()
const { setPageGuide } = useWorkbenchAssist()
const router = useRouter()
const { sys_normal_disable } = useDict('sys_normal_disable')
const { isReadOnlyRole, isBankRole, isInsurerRole, readOnlyRoleLabel, readOnlyRoleDescription } = useRoleViewMode()
const ENTERPRISE_FOCUS_KEYS = ['syncError', 'employer', 'disabled', 'normal', 'serviceOrg', 'dispatch']
const enterpriseWorkbenchFields = ['regionCode', 'enterpriseType', 'syncStatus', 'status', 'focusKey']
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
  exportFilePrefix: 'azb_enterprise',
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

const activeFocusKey = ref('')

const roleView = computed(() => {
  if (isBankRole.value) {
    return 'bank'
  }
  if (isInsurerRole.value) {
    return 'insurer'
  }
  return 'emergency'
})

function buildEnterpriseExplanationQuery(extraQuery = {}) {
  return {
    regionCode: queryParams.value.regionCode,
    ...extraQuery
  }
}

const fallbackPortalExplanations = computed(() => {
  if (roleView.value === 'bank') {
    return [
      {
        key: 'syncError',
        dimensionName: '同步异常对象',
        currentValue: summaryData.value.syncErrorCount ?? 0,
        targetValue: '0',
        summary: '银行协同先锁定同步异常企业，避免区域治理对象和信用协同口径失配。',
        evidenceModule: 'enterprise',
        recommendModule: 'enterprise',
        defaultQuery: buildEnterpriseExplanationQuery({ syncStatus: '2' }),
        sourceLabel: '6.1 企业对象解释',
        sourceDescription: '从企业对象回看同步异常企业。'
      },
      {
        key: 'employer',
        dimensionName: '重点用工主体',
        currentValue: summaryData.value.employerCount ?? 0,
        targetValue: '持续核实',
        summary: '重点用工主体决定后续设备、安责险和预警对象归属，应先核定治理对象范围。',
        evidenceModule: 'enterprise',
        recommendModule: 'enterprise',
        defaultQuery: buildEnterpriseExplanationQuery({ enterpriseType: '2' }),
        sourceLabel: '6.1 企业对象解释',
        sourceDescription: '从企业对象继续核对重点用工主体。'
      },
      {
        key: 'disabled',
        dimensionName: '停用对象',
        currentValue: summaryData.value.disabledCount ?? 0,
        targetValue: '0',
        summary: '停用企业若仍留在治理链，会影响设备、预警和保单归属，应优先核清。',
        evidenceModule: 'enterprise',
        recommendModule: 'enterprise',
        defaultQuery: buildEnterpriseExplanationQuery({ status: '1' }),
        sourceLabel: '6.1 企业对象解释',
        sourceDescription: '从企业对象继续核查停用主体。'
      }
    ]
  }

  return [
    {
      key: 'syncError',
      dimensionName: '同步异常对象',
      currentValue: summaryData.value.syncErrorCount ?? 0,
      targetValue: '0',
      summary: '治理链优先压降同步异常企业，避免后续设备、安责险和预警对象来源失真。',
      evidenceModule: 'enterprise',
      recommendModule: 'enterprise',
      defaultQuery: buildEnterpriseExplanationQuery({ syncStatus: '2' }),
      sourceLabel: '6.1 企业对象解释',
      sourceDescription: '从企业对象回看同步异常企业。'
    },
    {
      key: 'employer',
      dimensionName: '治理对象范围',
      currentValue: summaryData.value.employerCount ?? 0,
      targetValue: '持续核实',
      summary: '用工主体范围决定区域治理对象边界，应优先固定企业归属后再下钻到其他模块。',
      evidenceModule: 'enterprise',
      recommendModule: 'enterprise',
      defaultQuery: buildEnterpriseExplanationQuery({ enterpriseType: '2' }),
      sourceLabel: '6.1 企业对象解释',
      sourceDescription: '从企业对象继续核定治理主体范围。'
    },
    {
      key: 'dispatch',
      dimensionName: '派遣协同主体',
      currentValue: summaryData.value.dispatchCount ?? 0,
      targetValue: '持续核实',
      summary: '派遣主体会影响现场用工、人员归属和设备授权，应与治理对象一并核定。',
      evidenceModule: 'enterprise',
      recommendModule: 'enterprise',
      defaultQuery: buildEnterpriseExplanationQuery({ enterpriseType: '1' }),
      sourceLabel: '6.1 企业对象解释',
      sourceDescription: '从企业对象继续核查派遣协同主体。'
    },
    {
      key: 'disabled',
      dimensionName: '停用对象',
      currentValue: summaryData.value.disabledCount ?? 0,
      targetValue: '0',
      summary: '停用对象仍留在治理台账，会持续污染区域态势，应优先清理。',
      evidenceModule: 'enterprise',
      recommendModule: 'enterprise',
      defaultQuery: buildEnterpriseExplanationQuery({ status: '1' }),
      sourceLabel: '6.1 企业对象解释',
      sourceDescription: '从企业对象继续核查停用主体。'
    }
  ]
})

const portalExplanations = computed(() => {
  const aggregated = Array.isArray(summaryData.value.azbExplanation) ? summaryData.value.azbExplanation : []
  if (aggregated.length) {
    const focusKeys = new Set(focusQueues.value.map(item => item.key))
    const filtered = aggregated.filter(item => focusKeys.has(item.key))
    return filtered.length ? filtered : aggregated
  }
  return fallbackPortalExplanations.value
})

const portalExplanationItems = computed(() => decoratePortalExplanationItems(portalExplanations.value, {
  portalCode: 'azb',
  panelTitle: '6.1 治理说明',
  panelDescription: '企业对象页面按治理口径解释同步异常、重点用工主体、停用对象和区域承接范围。'
}))

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

function resolveExplanationFirstActionText(fallback, explanation) {
  if (!explanation) {
    return fallback
  }
  return explanation.actionLabel || explanation.actionText || explanation.targetLabel || explanation.moduleLabel || fallback
}

function buildExplanationFirstTags(tags = []) {
  const extras = []
  const label = buildPortalExplanationLabel(leadingPortalExplanation.value)
  if (label) {
    extras.push({ label: `6.1 first: ${label}`, type: 'warning' })
  }
  if (portalExplanationSummary.value) {
    extras.push({ label: portalExplanationSummary.value, type: 'success' })
  }
  return [...extras, ...tags].slice(0, 4)
}

function handlePortalExplanationAction(action) {
  if (action?.path === route.path) {
    applyEnterpriseWorkbenchQuery(action.query || {})
  }
  openPortalExplanationAction(router, action)
}

const summaryCards = computed(() => ([
  {
    key: 'total',
    label: '企业记录',
    value: summaryData.value.totalCount ?? total.value,
    unit: '家',
    note: '当前筛选范围内的企业主数据总量。',
    cardClass: ''
  },
  {
    key: 'normal',
    label: '正常企业',
    value: summaryData.value.normalCount ?? 0,
    unit: '家',
    note: '当前筛选范围内状态正常、可继续参与治理联动的企业。',
    cardClass: 'azb-summary-card--success'
  },
  {
    key: 'syncError',
    label: '同步异常',
    value: summaryData.value.syncErrorCount ?? 0,
    unit: '家',
    note: '当前筛选范围内对接状态异常的企业，需优先排查同步链路。',
    cardClass: 'azb-summary-card--danger'
  },
  {
    key: 'employer',
    label: '用工单位',
    value: summaryData.value.employerCount ?? 0,
    unit: '家',
    note: '当前筛选范围内的用工单位数量，可作为现场治理对象基数。',
    cardClass: 'azb-summary-card--warning'
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
  description: '列表已按工作台带入的行政区划锁定，可直接继续查看该区域治理对象并下钻设备、预警、高处作业和保险协同链路。',
  fieldLabels: {
    regionCode: '行政区划',
    enterpriseType: '企业类型',
    syncStatus: '同步状态',
    status: '启停状态',
    focusKey: '焦点队列'
  },
  fieldFormatters: {
    regionCode: value => formatRegionName(value, value),
    enterpriseType: value => optionLabel(enterpriseTypeOptions, value, value),
    syncStatus: value => optionLabel(syncStatusOptions, value, value),
    status: value => optionLabel(sys_normal_disable.value, value, value),
    focusKey: value => enterpriseFocusLabel(value)
  }
}))

const focusQueues = computed(() => {
  if (roleView.value === 'bank') {
    return [
      {
        key: 'syncError',
        title: '同步异常企业',
        desc: '银行协同先识别归属和同步链路异常对象，避免信用或报表关联到错误主体。',
        count: valueOrDefault(summaryData.value.syncErrorCount, countBy(item => item.syncStatus === '2')),
        unit: '家',
        actionText: '先看同步链路'
      },
      {
        key: 'employer',
        title: '重点用工单位',
        desc: '优先回看当前筛选范围内的用工单位，判断是否需要继续联动信用和区域报表。',
        count: valueOrDefault(summaryData.value.employerCount, countBy(item => item.enterpriseType === '2')),
        unit: '家',
        actionText: '复核治理对象'
      },
      {
        key: 'disabled',
        title: '停用企业',
        desc: '停用企业仍可能残留历史关联，需要先确认是否影响报表和联合风控判断。',
        count: valueOrDefault(summaryData.value.disabledCount, countBy(item => item.status === '1')),
        unit: '家',
        actionText: '回看停用原因'
      },
      {
        key: 'normal',
        title: '正常企业底座',
        desc: '最后回看可继续联动的正常企业盘子，判断当前区域治理基数是否稳定。',
        count: valueOrDefault(summaryData.value.normalCount, countBy(item => item.status === '0')),
        unit: '家',
        actionText: '查看稳定盘子'
      }
    ]
  }

  if (roleView.value === 'insurer') {
    return [
      {
        key: 'syncError',
        title: '同步异常企业',
        desc: '保险协同先处理归属和同步异常企业，避免保单、预防资金和高危作业挂错主体。',
        count: valueOrDefault(summaryData.value.syncErrorCount, countBy(item => item.syncStatus === '2')),
        unit: '家',
        actionText: '先看异常链路'
      },
      {
        key: 'serviceOrg',
        title: '服务机构企业',
        desc: '优先查看服务机构类主体，便于判断事故预防服务和协同关系是否完整。',
        count: valueOrDefault(summaryData.value.serviceOrgCount, countBy(item => item.enterpriseType === '3')),
        unit: '家',
        actionText: '复核服务关系'
      },
      {
        key: 'employer',
        title: '重点用工单位',
        desc: '用工单位直接影响保单覆盖和治理对象归属，应作为第二层协同复核入口。',
        count: valueOrDefault(summaryData.value.employerCount, countBy(item => item.enterpriseType === '2')),
        unit: '家',
        actionText: '回看治理主体'
      },
      {
        key: 'disabled',
        title: '停用企业',
        desc: '停用主体要优先确认是否仍有保单、设备或预警工单挂载。',
        count: valueOrDefault(summaryData.value.disabledCount, countBy(item => item.status === '1')),
        unit: '家',
        actionText: '核对历史挂载'
      }
    ]
  }

  return [
    {
      key: 'syncError',
      title: '同步异常企业',
      desc: '应急侧先排查主数据同步异常，避免设备、预警、高危作业和工伤事件归属失真。',
      count: valueOrDefault(summaryData.value.syncErrorCount, countBy(item => item.syncStatus === '2')),
      unit: '家',
      actionText: '先排查归属'
    },
    {
      key: 'employer',
      title: '重点用工单位',
      desc: '用工单位是现场治理主对象，应优先锁定并联动设备、作业和预警台账。',
      count: valueOrDefault(summaryData.value.employerCount, countBy(item => item.enterpriseType === '2')),
      unit: '家',
      actionText: '进入治理对象'
    },
    {
      key: 'dispatch',
      title: '派遣单位',
      desc: '派遣单位会影响人员归属和现场准入链路，需要持续核对跨主体关联是否稳定。',
      count: valueOrDefault(summaryData.value.dispatchCount, countBy(item => item.enterpriseType === '1')),
      unit: '家',
      actionText: '核对人员归属'
    },
    {
      key: 'disabled',
      title: '停用企业',
      desc: '停用企业需要优先确认是否仍有现场治理对象未清退。',
      count: valueOrDefault(summaryData.value.disabledCount, countBy(item => item.status === '1')),
      unit: '家',
      actionText: '回看现场残留'
    }
  ]
})

const resolvedFocusQueues = computed(() => focusQueues.value.map((item, index) => {
  const explanation = resolvePortalExplanationItem(index)
  return {
    ...item,
    desc: resolveExplanationFirstText(item.desc, explanation),
    actionText: resolveExplanationFirstActionText(item.actionText, explanation)
  }
}))

const activeFocus = computed(() => focusQueues.value.find(item => item.key === activeFocusKey.value) || focusQueues.value[0] || null)

const visibleEnterpriseList = computed(() => {
  const focusKey = activeFocus.value?.key
  return [...enterpriseList.value].sort((left, right) => scoreEnterprise(right, focusKey) - scoreEnterprise(left, focusKey))
})

const selectedEnterpriseOverview = computed(() => {
  if (!currentEnterprise.value) {
    return [
      { label: '所属区域', value: '-' },
      { label: '企业类型', value: '-' },
      { label: '同步状态', value: '-' },
      { label: '状态', value: '-' },
      { label: '联系人', value: '-' }
    ]
  }
  return [
    { label: '所属区域', value: formatRegionName(currentEnterprise.value.regionCode, currentEnterprise.value.regionCode || '-') },
    { label: '企业类型', value: optionLabel(enterpriseTypeOptions, currentEnterprise.value.enterpriseType) },
    { label: '同步状态', value: optionLabel(syncStatusOptions, currentEnterprise.value.syncStatus) },
    { label: '状态', value: optionLabel(sys_normal_disable.value, currentEnterprise.value.status) },
    { label: '联系人', value: currentEnterprise.value.contactPerson || '-' }
  ]
})

const workflowSteps = computed(() => {
  if (roleView.value === 'bank') {
    return [
      { label: '锁定异常主体', desc: '先看同步异常、停用企业和重点用工单位。' },
      { label: '复核归属关系', desc: '确认企业主数据是否会影响风险分层和区域报表。' },
      { label: '下钻详情', desc: '通过企业详情回看联系人、归属区域和历史状态。' },
      { label: '联动复核', desc: '必要时再回到信用评价和统计报表做只读协同。' }
    ]
  }
  if (roleView.value === 'insurer') {
    return [
      { label: '锁定异常主体', desc: '先识别同步异常、服务机构和重点用工单位。' },
      { label: '确认服务关系', desc: '回看保单、资金、项目等协同链路是否挂到正确主体。' },
      { label: '下钻企业详情', desc: '核对联系人、状态和区域归属。' },
      { label: '回到协同模块', desc: '再联动安责险、预防资金和高危作业台账。' }
    ]
  }
  return [
    { label: '锁定治理主体', desc: '先看同步异常、重点用工单位和派遣单位。' },
    { label: '核对归属底座', desc: '确认企业状态、区域归属和联系人是否完整。' },
    { label: '下钻对象详情', desc: '结合当前企业详情判断是否继续进入设备、作业和预警链路。' },
    { label: '推进现场闭环', desc: '再回到高危作业、设备和预警页面承接治理动作。' }
  ]
})

const resolvedWorkflowSteps = computed(() => workflowSteps.value.map((item, index) => {
  const explanation = resolvePortalExplanationItem(index)
  return {
    ...item,
    label: explanation?.dimensionName || explanation?.moduleLabel || explanation?.moduleCode || item.label,
    desc: resolveExplanationFirstText(item.desc, explanation)
  }
}))

const currentEnterpriseActionSummary = computed(() => describeEnterpriseAction(currentEnterprise.value, roleView.value, activeFocus.value))
const currentEnterpriseActionTags = computed(() => buildRiskTags(currentEnterprise.value, roleView.value, activeFocus.value))
const enterpriseHintTags = computed(() => buildRiskTags(currentEnterprise.value, roleView.value, activeFocus.value))
const detailRiskTags = computed(() => buildRiskTags(detailEnterprise.value || currentEnterprise.value, roleView.value, activeFocus.value))
const primaryEnterpriseAction = computed(() => resolvePrimaryEnterpriseAction(currentEnterprise.value, roleView.value, activeFocus.value))
const resolvedCurrentEnterpriseActionSummary = computed(() => resolveExplanationFirstText(
  currentEnterpriseActionSummary.value,
  leadingPortalExplanation.value
))
const resolvedCurrentEnterpriseActionTags = computed(() => buildExplanationFirstTags(currentEnterpriseActionTags.value))
const resolvedEnterpriseHintTags = computed(() => buildExplanationFirstTags(enterpriseHintTags.value))
const resolvedDetailRiskTags = computed(() => buildExplanationFirstTags(detailRiskTags.value))
const resolvedPrimaryEnterpriseAction = computed(() => ({
  ...primaryEnterpriseAction.value,
  label: resolveExplanationFirstActionText(primaryEnterpriseAction.value.label, leadingPortalExplanation.value)
}))
const focusTableHint = computed(() => activeFocus.value?.desc || '按当前焦点优先暴露最需要复核的企业主体。')
const readOnlyAlertTitle = computed(() => `${readOnlyRoleLabel.value}：当前仅保留企业摘要、详情和导出`)
const readOnlyAlertDescription = computed(() => `${readOnlyRoleDescription.value} 当前页面聚焦企业归属、同步状态和联系人信息复核，不承接企业维护动作。`)
const workbenchLinks = computed(() => buildEnterpriseWorkbenchLinks({
  proxy,
  enterprise: currentEnterprise.value,
  portalCode: 'azb',
  roleView: roleView.value
}))

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
  activeFocusKey.value = resolveEnterpriseFocusKey(route.query.focusKey)
  getList()

watchEffect(() => {
  setPageGuide({
    title: '????????' || '????????',
    description: '?????????????????????????????????' || '?????????????????????????????????',
    portalExplanation: portalExplanationItems.value,
    focus: resolvedFocusQueues.value,
    selection: [...selectedEnterpriseOverview.value, { label: '??????', value: resolvedCurrentEnterpriseActionSummary.value }],
    workflow: resolvedWorkflowSteps.value,
    hints: [...resolvedCurrentEnterpriseActionTags.value].slice(0, 6)
  })
})

}

function clearWorkbenchContext() {
  activeFocusKey.value = ''
  Object.assign(queryParams.value, {
    pageNum: 1,
    regionCode: undefined,
    enterpriseName: undefined,
    enterpriseCode: undefined,
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

function blockReadOnlyAction(actionLabel) {
  if (!isReadOnlyRole.value) {
    return false
  }
  proxy.$modal.msgWarning(`${readOnlyRoleLabel.value}仅保留摘要、详情和导出，不能${actionLabel}`)
  return true
}

function valueOrDefault(value, fallback = 0) {
  return value == null ? fallback : value
}

function countBy(predicate) {
  return enterpriseList.value.filter(item => predicate(item || {})).length
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
  activeFocusKey.value = resolveEnterpriseFocusKey(routeQuery.focusKey)
  getList()
}

function resolveEnterpriseFocusKey(value) {
  const normalized = String(value || '')
  return ENTERPRISE_FOCUS_KEYS.includes(normalized) ? normalized : ''
}

activeFocusKey.value = resolveEnterpriseFocusKey(route.query.focusKey)

function enterpriseFocusLabel(value) {
  return focusQueues.value.find(item => item.key === value)?.title || value || '-'
}

function handleSelectFocus(item) {
  activeFocusKey.value = item.key
}

function handlePrimaryEnterpriseAction() {
  if (!currentEnterprise.value) {
    return
  }
  if (primaryEnterpriseAction.value.kind === 'edit') {
    if (blockReadOnlyAction('修改企业')) {
      return
    }
    handleUpdate(currentEnterprise.value)
    return
  }
  openDetail(currentEnterprise.value)
}

function handleOpenWorkbenchLink(link) {
  if (openWorkbenchLink(proxy, link)) {
    return
  }
  proxy.$modal.msgWarning('请先选择企业，再进入协同模块')
}

function scoreEnterprise(enterprise, focusKey) {
  if (!enterprise) {
    return 0
  }
  let score = 0
  if (enterprise.syncStatus === '2') score += 40
  if (enterprise.status === '1') score += 24
  if (!enterprise.contactPhone || !enterprise.contactPerson) score += 12

  switch (focusKey) {
    case 'syncError':
      score += enterprise.syncStatus === '2' ? 120 : 0
      break
    case 'employer':
      score += enterprise.enterpriseType === '2' ? 120 : 0
      break
    case 'dispatch':
      score += enterprise.enterpriseType === '1' ? 120 : 0
      break
    case 'serviceOrg':
      score += enterprise.enterpriseType === '3' ? 120 : 0
      break
    case 'disabled':
      score += enterprise.status === '1' ? 120 : 0
      break
    case 'normal':
      score += enterprise.status === '0' ? 60 : 0
      break
    default:
      break
  }

  return score
}

function resolvePrimaryEnterpriseAction(enterprise, currentRoleView, focus) {
  if (!enterprise) {
    return { label: '查看详情', kind: 'detail' }
  }
  if (isReadOnlyRole.value) {
    return { label: currentRoleView === 'bank' ? '查看归属详情' : '查看企业详情', kind: 'detail' }
  }
  if (enterprise.syncStatus === '2' || focus?.key === 'syncError') {
    return { label: '修正同步信息', kind: 'edit' }
  }
  if (!enterprise.contactPhone || !enterprise.contactPerson) {
    return { label: '补齐联系人', kind: 'edit' }
  }
  if (enterprise.status === '1') {
    return { label: '查看停用详情', kind: 'detail' }
  }
  return { label: '修正企业信息', kind: 'edit' }
}

function describeEnterpriseAction(enterprise, currentRoleView, focus) {
  if (!enterprise) {
    return '先从左侧焦点队列选择一类重点企业，再在表格中联动查看当前企业的归属、联系人和同步状态。'
  }
  if (isReadOnlyRole.value) {
    if (currentRoleView === 'bank') {
      return `当前企业以${optionLabel(enterpriseTypeOptions, enterprise.enterpriseType, '未知类型')}身份参与当前区域治理链路，建议先复核同步状态和区域归属，再决定是否回到风险分层或区域报表继续只读协同。`
    }
    return `当前企业会影响保单、资金和高危作业归属，建议先复核同步状态、企业状态和联系人，再决定是否回到安责险或事故预防链路继续协同。`
  }
  if (enterprise.syncStatus === '2') {
    return '当前企业存在主数据同步异常，应优先修正归属和同步结果，避免设备、预警、高危作业或保单对象挂载错误。'
  }
  if (!enterprise.contactPhone || !enterprise.contactPerson) {
    return '当前企业联系人信息不完整，建议先补齐联系人和联系电话，降低后续现场联动和督办沟通成本。'
  }
  if (enterprise.status === '1') {
    return '当前企业已停用，建议先确认是否仍有设备、作业或预警工单残留，再决定是否继续治理或清退。'
  }
  if (focus?.key === 'employer') {
    return '当前企业属于重点用工单位，建议结合人员、设备和高危作业台账继续下钻复核。'
  }
  if (focus?.key === 'dispatch') {
    return '当前企业属于派遣单位，建议优先核对人员归属和跨主体治理关系是否稳定。'
  }
  if (focus?.key === 'serviceOrg') {
    return '当前企业属于服务机构，建议先复核协同服务关系，再回到保单或事故预防链路查看落点。'
  }
  return '当前企业主数据整体稳定，可继续通过详情查看归属、状态和联系人，再按需要进入关联治理模块。'
}

function buildRiskTags(enterprise, currentRoleView, focus) {
  if (!enterprise) {
    return [{ label: '未选中企业', type: 'info' }]
  }
  const tags = []
  if (focus?.title) {
    tags.push({ label: `当前焦点：${focus.title}`, type: 'info' })
  }
  if (enterprise.syncStatus === '2') {
    tags.push({ label: '同步异常待排查', type: 'danger' })
  }
  if (enterprise.status === '1') {
    tags.push({ label: '企业已停用', type: 'warning' })
  }
  if (!enterprise.contactPhone) {
    tags.push({ label: '联系电话缺失', type: 'warning' })
  }
  if (enterprise.enterpriseType === '2') {
    tags.push({ label: '重点用工治理对象', type: 'success' })
  }
  if (enterprise.enterpriseType === '1') {
    tags.push({ label: '派遣单位链路主体', type: 'info' })
  }
  if (enterprise.enterpriseType === '3') {
    tags.push({ label: '服务机构协同主体', type: 'info' })
  }
  if (currentRoleView === 'bank') {
    tags.push({ label: '银行只读协同', type: 'info' })
  } else if (currentRoleView === 'insurer') {
    tags.push({ label: '保险只读协同', type: 'info' })
  }
  if (!tags.length) {
    tags.push({ label: '当前无明显治理异常', type: 'success' })
  }
  return tags
}
</script>

<style scoped lang="scss">
.azb-role-alert {
  margin-bottom: 18px;
}

.azb-workbench-alert {
  margin-bottom: 18px;
}

.azb-workbench-alert__title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.azb-workbench-alert__desc {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 10px;
  color: #5f6f80;
  line-height: 1.7;
}

.azb-workbench-alert__desc strong {
  color: #15304b;
}

.azb-summary-grid,
.azb-focus-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
  margin-bottom: 16px;
}

.azb-summary-grid {
  grid-template-columns: repeat(4, minmax(0, 1fr));
}

.azb-summary-card,
.azb-focus-card {
  border: 1px solid #d9e3ee;
  border-radius: 16px;
  background: #fff;
}

.azb-summary-card {
  padding: 18px 20px;
}

.azb-summary-card__label {
  color: #5f7183;
  font-size: 13px;
}

.azb-summary-card__value {
  margin-top: 10px;
  color: #1f2d3d;
  font-size: 28px;
  font-weight: 700;
}

.azb-summary-card__unit {
  margin-left: 4px;
  font-size: 13px;
  font-weight: 500;
  color: #7b8da1;
}

.azb-summary-card__note {
  margin-top: 10px;
  color: #5f6f80;
  font-size: 13px;
  line-height: 1.7;
}

.azb-summary-card--success {
  background: linear-gradient(180deg, #ffffff 0%, #f3fbf5 100%);
}

.azb-summary-card--danger {
  background: linear-gradient(180deg, #ffffff 0%, #fff4f3 100%);
}

.azb-summary-card--warning {
  background: linear-gradient(180deg, #ffffff 0%, #fff9ef 100%);
}

.azb-focus-card :deep(.el-card__header) {
  padding: 18px 20px 0;
  border-bottom: none;
}

.azb-focus-card :deep(.el-card__body) {
  padding: 18px 20px 20px;
}

.azb-card-head__title {
  color: #1f2d3d;
  font-size: 18px;
  font-weight: 700;
}

.azb-card-head__desc {
  margin-top: 4px;
  color: #728395;
  font-size: 13px;
  line-height: 1.7;
}

.azb-focus-list,
.azb-source-list {
  display: grid;
  gap: 12px;
}

.azb-focus-list--single {
  grid-template-columns: 1fr;
}

.azb-focus-item,
.azb-source-item {
  padding: 14px 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  border: 1px solid #dde6ef;
  border-radius: 14px;
  background: #f8fbfd;
}

.azb-focus-item__label,
.azb-source-item__label {
  color: #5f7183;
  font-size: 13px;
}

.azb-focus-item__value,
.azb-source-item__value {
  color: #1f2d3d;
  font-size: 14px;
  font-weight: 600;
  text-align: right;
}

.azb-focus-queue {
  width: 100%;
  padding: 16px 18px;
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto;
  gap: 16px;
  border: 1px solid #dde6ef;
  border-radius: 14px;
  background: #f8fbfd;
  appearance: none;
  text-align: left;
  cursor: pointer;
  transition: border-color 0.2s ease, box-shadow 0.2s ease, transform 0.2s ease;
}

.azb-focus-queue:hover,
.azb-focus-queue.is-active {
  border-color: #9db8d6;
  box-shadow: 0 8px 20px rgba(15, 94, 168, 0.08);
  transform: translateY(-1px);
}

.azb-focus-queue__main strong {
  color: #1f2d3d;
  font-size: 15px;
}

.azb-focus-queue__main p {
  margin: 6px 0 0;
  color: #6f8092;
  font-size: 13px;
  line-height: 1.7;
}

.azb-focus-queue__side {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: flex-end;
  gap: 6px;
}

.azb-focus-queue__count {
  color: #1f2d3d;
  font-size: 22px;
  font-weight: 700;
}

.azb-focus-queue__action {
  color: #0f5ea8;
  font-size: 12px;
  font-weight: 600;
}

.azb-recommend-panel {
  margin-top: 14px;
  padding-top: 14px;
  border-top: 1px dashed #d6e2ee;
}

.azb-recommend-panel__title {
  color: #1f2d3d;
  font-size: 14px;
  font-weight: 700;
}

.azb-recommend-panel__summary {
  margin-top: 8px;
  color: #516273;
  font-size: 13px;
  line-height: 1.8;
}

.azb-focus-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 14px;
}

.azb-pipeline-list {
  display: grid;
  gap: 12px;
}

.azb-pipeline-item {
  display: grid;
  grid-template-columns: 34px minmax(0, 1fr);
  gap: 12px;
  align-items: start;
  padding: 14px 16px;
  border: 1px solid #dde6ef;
  border-radius: 14px;
  background: #f8fbfd;
}

.azb-pipeline-item__index {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  height: 34px;
  border-radius: 10px;
  background: #eaf2fb;
  color: #0f5ea8;
  font-size: 13px;
  font-weight: 700;
}

.azb-pipeline-item__body strong {
  color: #1f2d3d;
  font-size: 14px;
}

.azb-pipeline-item__body p {
  margin: 6px 0 0;
  color: #6f8092;
  font-size: 13px;
  line-height: 1.7;
}

.azb-card-head--between {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: flex-start;
}

.azb-panel-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0 16px;
}

.azb-detail-block {
  margin-top: 20px;
}

.azb-detail-block h3 {
  margin: 0 0 12px;
  color: #1f2d3d;
  font-size: 16px;
}

.azb-risk-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.azb-link-card {
  margin-bottom: 16px;
}

.azb-link-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
}

.azb-link-item {
  padding: 16px;
  border: 1px solid #dde6ef;
  border-radius: 14px;
  background: #f8fbfd;
  appearance: none;
  text-align: left;
  cursor: pointer;
  transition: border-color 0.2s ease, box-shadow 0.2s ease, transform 0.2s ease;
}

.azb-link-item:hover:not(:disabled) {
  border-color: #9db8d6;
  box-shadow: 0 8px 20px rgba(15, 94, 168, 0.08);
  transform: translateY(-1px);
}

.azb-link-item:disabled {
  cursor: not-allowed;
  opacity: 0.62;
}

.azb-link-item__title {
  color: #1f2d3d;
  font-size: 14px;
  font-weight: 700;
}

.azb-link-item__desc {
  margin-top: 8px;
  color: #5f6f80;
  font-size: 13px;
  line-height: 1.7;
}

.azb-link-item__action {
  margin-top: 12px;
  color: #0f5ea8;
  font-size: 12px;
  font-weight: 600;
}

@media (max-width: 1200px) {
  .azb-summary-grid,
  .azb-focus-grid,
  .azb-link-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 768px) {
  .azb-summary-grid,
  .azb-focus-grid,
  .azb-panel-grid,
  .azb-link-grid {
    grid-template-columns: 1fr;
  }

  .azb-focus-queue {
    grid-template-columns: 1fr;
  }

  .azb-card-head--between {
    flex-direction: column;
  }

  .azb-focus-queue__side {
    align-items: flex-start;
  }
}
</style>
