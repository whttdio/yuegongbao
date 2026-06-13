<template>
  <div class="app-container azb-page">
    <section class="gov-page-header azb-page__header">
      <div>
        <p class="azb-page__eyebrow">人员底座台账</p>
        <h1 class="azb-page__title">人员准入、持证状态与现场授权准备</h1>
        <p class="azb-page__desc">
          面向应急监管、设备管理员和企业现场负责人统一查看从业人员归属、持证状态、参保状态与在岗情况。
          页面按“先看风险人群，再锁定当前人员，再进入台账复核”的安责保视角重组，继续复用共享人员主数据接口。
        </p>
      </div>
      <div class="azb-page__tip">
        <div class="azb-page__tip-item">优先关注：证书临期或过期、未参保、已离岗但仍在现场名单中的人员。</div>
        <div class="azb-page__tip-item">现场联动：设备授权、高处作业报备和 AI 识别结果都依赖这里的人员主数据。</div>
        <div class="azb-page__tip-item">当前证书校验与培训记录仍复用共享底座，后续可补安责保专属风险标签。</div>
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

    <el-card class="azb-focus-card azb-link-card" shadow="never">
      <template #header>
        <div class="azb-card-head">
          <div>
            <div class="azb-card-head__title">协同入口</div>
            <div class="azb-card-head__desc">围绕当前人员直接进入下一步协同模块，减少在设备、安责险和作业台账之间来回筛选。</div>
          </div>
        </div>
      </template>
      <div class="azb-link-grid">
        <button
          v-for="link in workbenchLinks"
          :key="link.key"
          type="button"
          class="azb-link-item"
          :disabled="link.disabled"
          @click="handleOpenWorkbenchLink(link)"
        >
          <div class="azb-link-item__title">{{ link.title }}</div>
          <div class="azb-link-item__desc">{{ link.desc }}</div>
          <div class="azb-link-item__action">{{ link.disabled ? '请先选择人员' : '进入模块' }}</div>
        </button>
      </div>
    </el-card>

    <el-card class="search-card azb-search-card" shadow="never">
      <el-form ref="queryRef" :model="queryParams" :inline="true" v-show="showSearch">
        <el-form-item label="区域">
          <el-select v-model="queryParams.regionCode" clearable style="width: 180px">
            <el-option v-for="item in regionOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属企业">
          <el-select v-model="queryParams.enterpriseId" clearable filterable style="width: 220px">
            <el-option
              v-for="item in enterpriseOptions"
              :key="item.enterpriseId"
              :label="item.enterpriseName"
              :value="item.enterpriseId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="queryParams.personName" clearable style="width: 160px" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="身份证号">
          <el-input v-model="queryParams.idCard" clearable style="width: 200px" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="用工类型">
          <el-select v-model="queryParams.workerType" clearable style="width: 150px">
            <el-option v-for="item in workerTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="持证状态">
          <el-select v-model="queryParams.certStatus" clearable style="width: 150px">
            <el-option v-for="item in certStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="参保状态">
          <el-select v-model="queryParams.insuranceStatus" clearable style="width: 150px">
            <el-option v-for="item in insuranceStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="在岗状态">
          <el-select v-model="queryParams.employmentStatus" clearable style="width: 150px">
            <el-option v-for="item in employmentStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
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
          <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['ygb:person:add']">新增</el-button>
        </el-col>
        <el-col v-if="!isReadOnlyRole" :span="1.5">
          <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['ygb:person:edit']">修改</el-button>
        </el-col>
        <el-col v-if="!isReadOnlyRole" :span="1.5">
          <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['ygb:person:remove']">删除</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['ygb:person:export']">导出</el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>
    </el-card>

    <el-card class="table-card azb-table-card" shadow="never">
      <template #header>
        <div class="azb-card-head azb-card-head--between">
          <div>
            <div class="azb-card-head__title">人员准入复核台账</div>
            <div class="azb-card-head__desc">{{ focusTableHint }}</div>
          </div>
          <div class="azb-card-head__desc">当前总量 {{ total }} 人</div>
        </div>
      </template>
      <el-table
        v-loading="loading"
        :data="visiblePersonList"
        @selection-change="handleSelectionChange"
        @row-click="handleRowClick"
      >
        <el-table-column v-if="!isReadOnlyRole" type="selection" width="55" align="center" />
        <el-table-column label="人员ID" prop="personId" width="90" />
        <el-table-column label="姓名" prop="personName" width="120" />
        <el-table-column label="身份证号" min-width="180">
          <template #default="scope">
            <span>{{ maskIdCard(scope.row.idCard) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="所属企业" prop="enterpriseName" min-width="220" />
        <el-table-column label="区域" width="140">
          <template #default="scope">
            <span>{{ formatRegionName(scope.row.regionCode) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="用工类型" prop="workerType" width="110">
          <template #default="scope">
            <dict-tag :options="workerTypeOptions" :value="scope.row.workerType" />
          </template>
        </el-table-column>
        <el-table-column label="工种" prop="jobType" width="140" />
        <el-table-column label="持证状态" prop="certStatus" width="110">
          <template #default="scope">
            <dict-tag :options="certStatusOptions" :value="scope.row.certStatus" />
          </template>
        </el-table-column>
        <el-table-column label="参保状态" prop="insuranceStatus" width="110">
          <template #default="scope">
            <dict-tag :options="insuranceStatusOptions" :value="scope.row.insuranceStatus" />
          </template>
        </el-table-column>
        <el-table-column label="在岗状态" prop="employmentStatus" width="110">
          <template #default="scope">
            <dict-tag :options="employmentStatusOptions" :value="scope.row.employmentStatus" />
          </template>
        </el-table-column>
        <el-table-column label="联系电话" prop="mobile" width="130" />
        <el-table-column label="入场日期" width="120">
          <template #default="scope">
            <span>{{ parseTime(scope.row.entryDate, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column class-name="table-fill-column" min-width="1" />

        <el-table-column label="操作" fixed="right" align="center" :width="isReadOnlyRole ? 90 : 220" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-button link type="info" icon="View" @click.stop="openDetail(scope.row)">详情</el-button>
            <el-button v-if="!isReadOnlyRole" link type="primary" icon="Edit" @click.stop="handleUpdate(scope.row)" v-hasPermi="['ygb:person:edit']">修改</el-button>
            <el-button v-if="!isReadOnlyRole" link type="danger" icon="Delete" @click.stop="handleDelete(scope.row)" v-hasPermi="['ygb:person:remove']">删除</el-button>
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
      <el-form ref="personRef" :model="form" :rules="rules" label-width="110px">
        <div class="azb-panel-grid">
          <el-form-item label="所属企业" prop="enterpriseId">
            <el-select v-model="form.enterpriseId" filterable placeholder="请选择所属企业" style="width: 100%" @change="handleEnterpriseChange">
              <el-option
                v-for="item in enterpriseOptions"
                :key="item.enterpriseId"
                :label="item.enterpriseName"
                :value="item.enterpriseId"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="区域编码" prop="regionCode">
            <el-input v-model="form.regionCode" placeholder="随所属企业带出" />
          </el-form-item>
          <el-form-item label="姓名" prop="personName">
            <el-input v-model="form.personName" placeholder="请输入姓名" />
          </el-form-item>
          <el-form-item label="身份证号" prop="idCard">
            <el-input v-model="form.idCard" placeholder="请输入身份证号" />
          </el-form-item>
          <el-form-item label="联系电话" prop="mobile">
            <el-input v-model="form.mobile" placeholder="请输入联系电话" />
          </el-form-item>
          <el-form-item label="用工类型" prop="workerType">
            <el-select v-model="form.workerType" placeholder="请选择用工类型">
              <el-option v-for="item in workerTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="工种" prop="jobType">
            <el-input v-model="form.jobType" placeholder="请输入工种" />
          </el-form-item>
          <el-form-item label="持证状态" prop="certStatus">
            <el-radio-group v-model="form.certStatus">
              <el-radio v-for="item in certStatusOptions" :key="item.value" :label="item.value">{{ item.label }}</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="参保状态" prop="insuranceStatus">
            <el-radio-group v-model="form.insuranceStatus">
              <el-radio v-for="item in insuranceStatusOptions" :key="item.value" :label="item.value">{{ item.label }}</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="在岗状态" prop="employmentStatus">
            <el-radio-group v-model="form.employmentStatus">
              <el-radio v-for="item in employmentStatusOptions" :key="item.value" :label="item.value">{{ item.label }}</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="入场日期" prop="entryDate">
            <el-date-picker
              v-model="form.entryDate"
              type="date"
              placeholder="请选择入场日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              style="width: 100%"
            />
          </el-form-item>
          <el-form-item label="离场日期" prop="leaveDate">
            <el-date-picker
              v-model="form.leaveDate"
              type="date"
              placeholder="请选择离场日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              style="width: 100%"
            />
          </el-form-item>
        </div>
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

    <page-detail-dialog v-model="detailOpen" title="人员详情" width="720px">
      <template v-if="detailPerson">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="姓名">{{ detailPerson.personName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="所属企业">{{ detailPerson.enterpriseName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="区域">{{ formatRegionName(detailPerson.regionCode) }}</el-descriptions-item>
          <el-descriptions-item label="用工类型">{{ optionLabel(workerTypeOptions, detailPerson.workerType) }}</el-descriptions-item>
          <el-descriptions-item label="持证状态">{{ optionLabel(certStatusOptions, detailPerson.certStatus) }}</el-descriptions-item>
          <el-descriptions-item label="参保状态">{{ optionLabel(insuranceStatusOptions, detailPerson.insuranceStatus) }}</el-descriptions-item>
          <el-descriptions-item label="在岗状态">{{ optionLabel(employmentStatusOptions, detailPerson.employmentStatus) }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ detailPerson.mobile || '-' }}</el-descriptions-item>
          <el-descriptions-item label="身份证号" :span="2">{{ detailPerson.idCard || '-' }}</el-descriptions-item>
          <el-descriptions-item label="工种">{{ detailPerson.jobType || '-' }}</el-descriptions-item>
          <el-descriptions-item label="入场日期">{{ parseTime(detailPerson.entryDate, '{y}-{m}-{d}') || '-' }}</el-descriptions-item>
          <el-descriptions-item label="离场日期">{{ parseTime(detailPerson.leaveDate, '{y}-{m}-{d}') || '-' }}</el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ detailPerson.remark || '-' }}</el-descriptions-item>
        </el-descriptions>

        <div class="azb-detail-block">
          <h3>安责保侧风险提示</h3>
          <div class="azb-risk-tags">
            <el-tag v-for="item in resolvedDetailRiskTags" :key="item.label" :type="item.type" effect="plain">{{ item.label }}</el-tag>
          </div>
        </div>
      </template>
    </page-detail-dialog>
  </div>
</template>

<script setup name="AzbPerson">
  import { computed, getCurrentInstance, ref, watch, watchEffect } from 'vue'
  import { useRoute, useRouter } from 'vue-router'
  import { useWorkbenchAssist } from '@/composables/useWorkbenchAssist'
import { useRoleViewMode } from '@/utils/roleView'
import { decoratePortalExplanationItems, openPortalExplanationAction, resolvePortalExplanationSummary } from '@/utils/portalExplanation'
import { applyWorkbenchRouteQuery, buildPersonWorkbenchLinks, buildWorkbenchContext, openWorkbenchLink, stripWorkbenchRouteQuery } from '@/utils/workbenchLink'
import {
  certStatusOptions,
  employmentStatusOptions,
  formatRegionName,
  insuranceStatusOptions,
  maskIdCard,
  optionLabel,
  usePersonPage,
  workerTypeOptions
} from '@/views/person/usePersonPage'

  const { proxy } = getCurrentInstance()
  const route = useRoute()
  const router = useRouter()
  const { setPageGuide } = useWorkbenchAssist()
  const { isReadOnlyRole, isBankRole, isInsurerRole, readOnlyRoleLabel, readOnlyRoleDescription } = useRoleViewMode()
const personWorkbenchFields = ['enterpriseId', 'regionCode', 'workerType', 'certStatus', 'insuranceStatus', 'employmentStatus']
const personInitialQuery = {}
applyWorkbenchRouteQuery(route.query, personInitialQuery, personWorkbenchFields)

const {
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
  single,
  multiple,
  queryParams,
  form,
  rules,
  regionOptions,
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
  handleExport,
  handleEnterpriseChange
} = usePersonPage({
  exportFilePrefix: 'azb_person',
  initialQueryParams: personInitialQuery,
  canMutate: () => !isReadOnlyRole.value,
  onBlockedAction: blockReadOnlyAction
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

function buildPersonExplanationQuery(extraQuery = {}) {
  return {
    enterpriseId: queryParams.value.enterpriseId,
    regionCode: queryParams.value.regionCode,
    ...extraQuery
  }
}

const fallbackPortalExplanations = computed(() => {
  const baseItems = [
    {
      key: 'certRisk',
      dimensionName: '证书风险',
      currentValue: valueOrDefault(summaryData.value.certRiskCount, 0),
      targetValue: '0',
      summary: '治理链优先识别证书临期和过期人员，避免现场授权和高风险作业对象失真。',
      evidenceModule: 'person',
      recommendModule: 'person',
      defaultQuery: buildPersonExplanationQuery({ certStatus: '3' }),
      sourceLabel: '6.1 人员对象解释',
      sourceDescription: '从人员对象继续核查证书风险人群。'
    },
    {
      key: 'uninsured',
      dimensionName: '未参保对象',
      currentValue: valueOrDefault(summaryData.value.uninsuredCount, 0),
      targetValue: '0',
      summary: '未参保人员会放大治理风险，应优先作为现场核查和协同压降对象。',
      evidenceModule: 'person',
      recommendModule: 'person',
      defaultQuery: buildPersonExplanationQuery({ insuranceStatus: '0' }),
      sourceLabel: '6.1 人员对象解释',
      sourceDescription: '从人员对象继续核查未参保人群。'
    },
    {
      key: 'onPost',
      dimensionName: '在岗范围',
      currentValue: valueOrDefault(summaryData.value.onPostCount, 0),
      targetValue: '持续核实',
      summary: '先固定在岗人员范围，再下钻设备授权、现场作业和预警对象，保持治理链对象一致。',
      evidenceModule: 'person',
      recommendModule: 'person',
      defaultQuery: buildPersonExplanationQuery({ employmentStatus: '0' }),
      sourceLabel: '6.1 人员对象解释',
      sourceDescription: '从人员对象继续核定在岗治理范围。'
    }
  ]

  if (roleView.value === 'bank') {
    return [
      ...baseItems,
      {
        key: 'dispatch',
        dimensionName: '派遣结构',
        currentValue: valueOrDefault(summaryData.value.dispatchWorkerCount, 0),
        targetValue: '持续核实',
        summary: '银行协同优先看派遣结构，便于对照区域用工风险和后续协同对象范围。',
        evidenceModule: 'person',
        recommendModule: 'person',
        defaultQuery: buildPersonExplanationQuery({ workerType: '1' }),
        sourceLabel: '6.1 人员对象解释',
        sourceDescription: '从人员对象继续核对派遣结构。'
      }
    ]
  }

  return [
    ...baseItems,
    {
      key: 'outsourcing',
      dimensionName: '外包人员结构',
      currentValue: valueOrDefault(summaryData.value.outsourcingWorkerCount, 0),
      targetValue: '持续核实',
      summary: '外包人员结构会影响现场治理对象边界，应优先固定人员类型口径。',
      evidenceModule: 'person',
      recommendModule: 'person',
      defaultQuery: buildPersonExplanationQuery({ workerType: '3' }),
      sourceLabel: '6.1 人员对象解释',
      sourceDescription: '从人员对象继续核对外包人员结构。'
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
  panelDescription: '人员对象页面按治理口径解释证书风险、未参保、在岗范围和重点人员结构。'
}))

function handlePortalExplanationAction(action) {
  openPortalExplanationAction(router, action)
}

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
    return `${label}：${summary}`
  }
  if (summary) {
    return summary
  }
  if (label) {
    return `${label}优先`
  }
  return fallback
}

function resolveExplanationFirstActionText(fallback, explanation) {
  if (!explanation) {
    return fallback
  }
  return explanation.actionLabel || explanation.targetLabel || explanation.moduleLabel || fallback
}

const summaryCards = computed(() => ([
  {
    key: 'total',
    label: '人员记录',
    value: valueOrDefault(summaryData.value.totalCount, total.value),
    unit: '人',
    note: '当前筛选条件下的人员台账总量。',
    cardClass: ''
  },
  {
    key: 'onPost',
    label: '在岗人员',
    value: valueOrDefault(summaryData.value.onPostCount, 0),
    unit: '人',
    note: '当前筛选条件下可直接用于现场授权和作业筛查的在岗人员。',
    cardClass: 'azb-summary-card--success'
  },
  {
    key: 'certRisk',
    label: '证书异常/临期',
    value: valueOrDefault(summaryData.value.certRiskCount, 0),
    unit: '人',
    note: '当前筛选条件下持证状态为临期或过期的人员。',
    cardClass: 'azb-summary-card--danger'
  },
  {
    key: 'uninsured',
    label: '未参保人员',
    value: valueOrDefault(summaryData.value.uninsuredCount, 0),
    unit: '人',
    note: '当前筛选条件下工伤参保状态为空缺的人员，需重点跟进。',
    cardClass: 'azb-summary-card--warning'
  }
]))

const resolvedSummaryCards = computed(() => summaryCards.value.map((item, index) => ({
  ...item,
  note: resolveExplanationFirstText(item.note, resolvePortalExplanationItem(index))
})))

const workbenchContext = computed(() => buildWorkbenchContext(route.query, {
  fields: personWorkbenchFields,
  sourceLabel: '对象工作台',
  title: '当前人员台账页沿用了工作台来源条件',
  description: '列表已按工作台带入的所属企业与行政区划锁定，可直接继续处理该对象范围内的准入、持证、参保和现场治理联动。',
  fieldLabels: {
    enterpriseId: '所属企业',
    regionCode: '行政区划',
    workerType: '用工类型',
    certStatus: '持证状态',
    insuranceStatus: '参保状态',
    employmentStatus: '在岗状态'
  },
  fieldFormatters: {
    enterpriseId: value => {
      const matched = enterpriseOptions.value.find(item => String(item.enterpriseId) === String(value))
      return matched ? matched.enterpriseName : value
    },
    regionCode: value => formatRegionName(value, value),
    workerType: value => optionLabel(workerTypeOptions, value, value),
    certStatus: value => optionLabel(certStatusOptions, value, value),
    insuranceStatus: value => optionLabel(insuranceStatusOptions, value, value),
    employmentStatus: value => optionLabel(employmentStatusOptions, value, value)
  }
}))

const focusQueues = computed(() => {
  if (roleView.value === 'bank') {
    return [
      {
        key: 'uninsured',
        title: '参保空缺人员',
        desc: '先确认参保空缺对象是否影响区域统计、协同报表和对象分层口径。',
        count: valueOrDefault(summaryData.value.uninsuredCount, countBy(item => item.insuranceStatus === '0')),
        unit: '人',
        actionText: '先看参保缺口'
      },
      {
        key: 'certRisk',
        title: '证书异常人员',
        desc: '持证异常会影响高危作业和现场治理闭环，也会影响跨模块底册可信度。',
        count: valueOrDefault(summaryData.value.certRiskCount, countBy(item => ['2', '3'].includes(item.certStatus))),
        unit: '人',
        actionText: '核对准入底册'
      },
      {
        key: 'offPost',
        title: '已离岗但仍在册',
        desc: '需要确认是否仍被统计到现场名单，避免误入后续分析和报表。',
        count: countBy(item => item.employmentStatus === '1'),
        unit: '人',
        actionText: '回看现场残留'
      },
      {
        key: 'dispatch',
        title: '派遣人员',
        desc: '优先确认派遣归属与现场主体映射是否稳定，避免跨企业数据挂接错误。',
        count: valueOrDefault(summaryData.value.dispatchWorkerCount, countBy(item => item.workerType === '1')),
        unit: '人',
        actionText: '核对归属关系'
      }
    ]
  }

  if (roleView.value === 'insurer') {
    return [
      {
        key: 'uninsured',
        title: '未参保人员',
        desc: '安责险协同先盯住未参保对象，避免事故预防和承保视图脱节。',
        count: valueOrDefault(summaryData.value.uninsuredCount, countBy(item => item.insuranceStatus === '0')),
        unit: '人',
        actionText: '先看参保空缺'
      },
      {
        key: 'certRisk',
        title: '证书临期或过期',
        desc: '持证异常人群会直接影响高危作业和事故预防协同判断。',
        count: valueOrDefault(summaryData.value.certRiskCount, countBy(item => ['2', '3'].includes(item.certStatus))),
        unit: '人',
        actionText: '复核持证风险'
      },
      {
        key: 'outsourcing',
        title: '外包作业人群',
        desc: '外包对象需要额外确认归属主体和保险覆盖边界，避免责任口径混淆。',
        count: valueOrDefault(summaryData.value.outsourcingWorkerCount, countBy(item => item.workerType === '3')),
        unit: '人',
        actionText: '核对协同边界'
      },
      {
        key: 'offPost',
        title: '已离岗对象',
        desc: '核对离岗后是否仍挂在现场名单或高危作业链路中，避免误判风险暴露。',
        count: countBy(item => item.employmentStatus === '1'),
        unit: '人',
        actionText: '确认残留名单'
      }
    ]
  }

  return [
    {
      key: 'certRisk',
      title: '证书异常人员',
      desc: '应急监管先看证书临期、过期和未校验对象，避免高危作业和设备授权误放行。',
      count: valueOrDefault(summaryData.value.certRiskCount, countBy(item => ['2', '3'].includes(item.certStatus))),
      unit: '人',
      actionText: '优先复核准入'
    },
    {
      key: 'offPost',
      title: '已离岗仍在册',
      desc: '需要优先确认是否应从现场授权、高处作业和设备名单中移除。',
      count: countBy(item => item.employmentStatus === '1'),
      unit: '人',
      actionText: '先排现场残留'
    },
    {
      key: 'uninsured',
      title: '未参保对象',
      desc: '参保空缺会影响事故风险暴露和后续治理闭环，应尽快确认真实原因。',
      count: valueOrDefault(summaryData.value.uninsuredCount, countBy(item => item.insuranceStatus === '0')),
      unit: '人',
      actionText: '核对参保状态'
    },
    {
      key: 'dispatch',
      title: '派遣作业人群',
      desc: '派遣对象更容易出现归属错挂，需同步回看企业、设备和作业链路。',
      count: valueOrDefault(summaryData.value.dispatchWorkerCount, countBy(item => item.workerType === '1')),
      unit: '人',
      actionText: '回看归属链路'
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

const visiblePersonList = computed(() => sortPersonList(personList.value, activeFocus.value?.key))

watch([personList, activeFocusKey], () => {
  currentPerson.value = visiblePersonList.value[0]
}, { immediate: true })

const selectedPersonOverview = computed(() => {
  if (!currentPerson.value) {
    return [
      { label: '所属企业', value: '-' },
      { label: '所在区域', value: '-' },
      { label: '持证/参保', value: '-' },
      { label: '在岗/工种', value: '-' },
      { label: '联系电话', value: '-' }
    ]
  }
  return [
    { label: '所属企业', value: currentPerson.value.enterpriseName || '-' },
    { label: '所在区域', value: formatRegionName(currentPerson.value.regionCode, currentPerson.value.regionCode || '-') },
    {
      label: '持证/参保',
      value: `${optionLabel(certStatusOptions, currentPerson.value.certStatus)} / ${optionLabel(insuranceStatusOptions, currentPerson.value.insuranceStatus)}`
    },
    {
      label: '在岗/工种',
      value: `${optionLabel(employmentStatusOptions, currentPerson.value.employmentStatus)} / ${currentPerson.value.jobType || '-'}`
    },
    { label: '联系电话', value: currentPerson.value.mobile || '-' }
  ]
})

const workflowSteps = computed(() => {
  if (roleView.value === 'bank') {
    return [
      { label: '锁定参保空缺', desc: '先筛出未参保、离岗残留和归属不稳的人群。' },
      { label: '回看所属主体', desc: '确认人员是否会影响区域报表、协同名单和对象分层。' },
      { label: '只读复核详情', desc: '通过详情回看身份证、联系方式、工种和现场状态。' },
      { label: '联动其他模块', desc: '必要时回到统计报表和预警总览页做只读协同。' }
    ]
  }
  if (roleView.value === 'insurer') {
    return [
      { label: '锁定未参保与持证异常', desc: '先看参保空缺和证书异常，确定事故预防重点。' },
      { label: '确认企业与工种归属', desc: '回看人员属于哪家企业、哪类工种和当前在岗状态。' },
      { label: '下钻人员详情', desc: '核对身份证、联系方式和离岗时间，判断是否存在残留风险。' },
      { label: '回到保险协同链', desc: '再联动安责险、高处作业和预防项目页面做只读筛查。' }
    ]
  }
  return [
    { label: '锁定高风险人群', desc: '先看证书异常、离岗残留和参保空缺对象。' },
    { label: '核对现场准入底册', desc: '确认所属企业、工种、联系方式和在岗状态是否完整。' },
    { label: '必要时修正台账', desc: '对证书、参保、联系方式或离岗信息直接回写整改。' },
    { label: '联动现场链路', desc: '再回到设备授权、高处作业和 AI 风险页承接后续动作。' }
  ]
})

const currentPersonActionSummary = computed(() => describePersonAction(currentPerson.value, roleView.value))
const currentPersonActionTags = computed(() => buildRiskTags(currentPerson.value, roleView.value, activeFocus.value))
const personHintTags = computed(() => buildRiskTags(currentPerson.value || visiblePersonList.value[0], roleView.value, activeFocus.value))
const detailRiskTags = computed(() => buildRiskTags(detailPerson.value || currentPerson.value, roleView.value, activeFocus.value))
const primaryPersonAction = computed(() => resolvePrimaryPersonAction(currentPerson.value, roleView.value))
const resolvedWorkflowSteps = computed(() => workflowSteps.value.map((item, index) => {
  const explanation = resolvePortalExplanationItem(index)
  return {
    ...item,
    label: explanation?.dimensionName || explanation?.moduleLabel || explanation?.moduleCode || item.label,
    desc: resolveExplanationFirstText(item.desc, explanation)
  }
}))
const resolvedCurrentPersonActionSummary = computed(() => resolveExplanationFirstText(
  currentPersonActionSummary.value,
  leadingPortalExplanation.value
))
const resolvedCurrentPersonActionTags = computed(() => {
  const tags = [...currentPersonActionTags.value]
  const extras = []
  const label = buildPortalExplanationLabel(leadingPortalExplanation.value)
  if (label) {
    extras.push({ label: `6.1主解释：${label}`, type: 'warning' })
  }
  if (portalExplanationSummary.value) {
    extras.push({ label: portalExplanationSummary.value, type: 'success' })
  }
  return [...extras, ...tags].slice(0, 4)
})
const resolvedPersonHintTags = computed(() => {
  const tags = [...personHintTags.value]
  const extras = []
  const label = buildPortalExplanationLabel(leadingPortalExplanation.value)
  if (label) {
    extras.push({ label: `6.1主解释：${label}`, type: 'warning' })
  }
  if (portalExplanationSummary.value) {
    extras.push({ label: portalExplanationSummary.value, type: 'success' })
  }
  return [...extras, ...tags].slice(0, 4)
})
  const resolvedDetailRiskTags = computed(() => {
  const tags = [...detailRiskTags.value]
  const extras = []
  const label = buildPortalExplanationLabel(leadingPortalExplanation.value)
  if (label) {
    extras.push({ label: `6.1主解释：${label}`, type: 'warning' })
  }
  if (portalExplanationSummary.value) {
    extras.push({ label: portalExplanationSummary.value, type: 'success' })
  }
    return [...extras, ...tags].slice(0, 4)
  })
  watchEffect(() => {
    setPageGuide({
      title: '人员准入、持证状态与现场授权准备',
      description: '统一查看当前页的角色焦点、当前选中、治理路径与治理提示。',
      portalExplanation: portalExplanationItems.value,
      focus: resolvedFocusQueues.value,
      selection: [
        ...selectedPersonOverview.value,
        { label: '当前处置建议', value: resolvedCurrentPersonActionSummary.value }
      ],
      workflow: resolvedWorkflowSteps.value,
      hints: [...resolvedCurrentPersonActionTags.value, ...resolvedPersonHintTags.value].slice(0, 6)
    })
  })
  const resolvedPrimaryPersonAction = computed(() => ({
  ...primaryPersonAction.value,
  label: resolveExplanationFirstActionText(primaryPersonAction.value.label, leadingPortalExplanation.value)
}))
const focusTableHint = computed(() => activeFocus.value?.desc || '按当前焦点优先暴露最需要复核的人员对象。')
const readOnlyAlertTitle = computed(() => `${readOnlyRoleLabel.value}：当前仅保留人员摘要、详情和导出`)
const workbenchLinks = computed(() => buildPersonWorkbenchLinks({
  proxy,
  person: currentPerson.value,
  portalCode: 'azb',
  roleView: roleView.value
}))

const readOnlyAlertDescription = computed(() => {
  if (isInsurerRole.value) {
    return `${readOnlyRoleDescription.value} 当前页面聚焦安责险协同前的人员准入、参保空缺和持证风险复核。`
  }
  if (isBankRole.value) {
    return `${readOnlyRoleDescription.value} 当前页面仅作为跨模块人员底册只读入口，不承担人员台账维护动作。`
  }
  return `${readOnlyRoleDescription.value} 当前页面聚焦人员准入、持证状态和参保空缺复核。`
})

function resetQuery() {
  proxy.resetForm('queryRef')
  Object.assign(queryParams.value, {
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
  })
  applyWorkbenchRouteQuery(route.query, queryParams.value, personWorkbenchFields)
  getList()
}

function clearWorkbenchContext() {
  Object.assign(queryParams.value, {
    pageNum: 1,
    enterpriseId: undefined,
    regionCode: undefined,
    workerType: undefined,
    certStatus: undefined,
    insuranceStatus: undefined,
    employmentStatus: undefined
  })
  router.replace({
    path: route.path,
    query: stripWorkbenchRouteQuery(route.query, personWorkbenchFields)
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
  return personList.value.filter(item => predicate(item || {})).length
}

function handleSelectFocus(item) {
  activeFocusKey.value = item.key
}

function handlePrimaryPersonAction() {
  if (!currentPerson.value) {
    return
  }
  if (primaryPersonAction.value.kind === 'edit') {
    if (blockReadOnlyAction('修改人员')) {
      return
    }
    handleUpdate(currentPerson.value)
    return
  }
  openDetail(currentPerson.value)
}

function handleOpenWorkbenchLink(link) {
  if (openWorkbenchLink(proxy, link)) {
    return
  }
  proxy.$modal.msgWarning('请先选择人员，再进入协同模块')
}

function sortPersonList(list, focusKey) {
  return [...(list || [])].sort((left, right) => scorePerson(right, focusKey) - scorePerson(left, focusKey))
}

function scorePerson(person, focusKey) {
  if (!person) {
    return 0
  }
  let score = 0
  if (['2', '3'].includes(person.certStatus)) score += 42
  if (person.insuranceStatus === '0') score += 36
  if (person.employmentStatus === '1') score += 24
  if (!person.mobile) score += 12

  switch (focusKey) {
    case 'certRisk':
      score += ['2', '3'].includes(person.certStatus) ? 120 : 0
      break
    case 'uninsured':
      score += person.insuranceStatus === '0' ? 120 : 0
      break
    case 'offPost':
      score += person.employmentStatus === '1' ? 120 : 0
      break
    case 'dispatch':
      score += person.workerType === '1' ? 120 : 0
      break
    case 'outsourcing':
      score += person.workerType === '3' ? 120 : 0
      break
    default:
      break
  }

  return score
}

function resolvePrimaryPersonAction(person, currentRoleView) {
  if (!person) {
    return {
      kind: 'detail',
      label: currentRoleView === 'emergency' ? '选择人员' : '查看详情'
    }
  }
  if (isReadOnlyRole.value) {
    return { kind: 'detail', label: '查看详情' }
  }
  if (
    ['2', '3'].includes(person.certStatus) ||
    person.insuranceStatus === '0' ||
    person.employmentStatus === '1' ||
    !person.mobile
  ) {
    return { kind: 'edit', label: '修改台账' }
  }
  return { kind: 'detail', label: '查看详情' }
}

function describePersonAction(person, currentRoleView) {
  if (!person) {
    return '先从左侧焦点队列切入，再选择一个重点人员进入准入复核。'
  }
  if (currentRoleView === 'bank') {
    return `建议先核对 ${person.personName || '该人员'} 的参保、在岗和归属状态，再回到统计报表或对象分层页面确认是否影响协同口径。`
  }
  if (currentRoleView === 'insurer') {
    return `建议先复核 ${person.personName || '该人员'} 的参保和持证状态，再联动安责险、高处作业或事故预防链路做只读筛查。`
  }
  if (['2', '3'].includes(person.certStatus)) {
    return `建议优先修正 ${person.personName || '该人员'} 的证书状态或培训信息，避免高危作业和设备授权误放行。`
  }
  if (person.employmentStatus === '1') {
    return `建议先确认 ${person.personName || '该人员'} 是否应从现场授权、高处作业和 AI 点名名单中移除。`
  }
  if (person.insuranceStatus === '0') {
    return `建议先补齐 ${person.personName || '该人员'} 的工伤参保状态，或明确空缺原因供后续治理闭环使用。`
  }
  if (!person.mobile) {
    return `建议先补全 ${person.personName || '该人员'} 的联系方式，避免现场通知和整改回执无法落地。`
  }
  return `${person.personName || '该人员'} 当前底册较完整，可继续联动设备授权、高处作业报备和 AI 风险识别链路。`
}

function buildRiskTags(person, currentRoleView, focus) {
  const tags = []
  if (!person) {
    return [{ label: '未选中人员', type: 'info' }]
  }
  if (focus?.title) {
    tags.push({ label: `当前焦点：${focus.title}`, type: 'info' })
  }
  if (['2', '3'].includes(person.certStatus)) {
    tags.push({ label: '证书临期或过期', type: 'danger' })
  }
  if (person.insuranceStatus === '0') {
    tags.push({ label: '工伤参保待补齐', type: 'warning' })
  }
  if (person.employmentStatus === '1') {
    tags.push({ label: '已离岗，需核实现场权限', type: 'info' })
  }
  if (!person.mobile) {
    tags.push({ label: '联系方式缺失', type: 'warning' })
  }
  if (currentRoleView === 'insurer' && person.workerType === '3') {
    tags.push({ label: '外包协同重点', type: 'warning' })
  }
  if (currentRoleView === 'bank' && person.workerType === '1') {
    tags.push({ label: '派遣归属复核', type: 'info' })
  }
  if (!tags.length) {
    tags.push({ label: '当前无明显准入风险', type: 'success' })
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
