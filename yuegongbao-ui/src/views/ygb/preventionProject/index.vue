<template>
  <div class="app-container ygb-page">
    <section class="gov-page-header ygb-page__header">
      <div>
        <p class="ygb-page__eyebrow">工伤预防</p>
        <h1 class="ygb-page__title">预防项目执行办理台账</h1>
        <p class="ygb-page__desc">
          面向企业管理员、经办人员和监管人员统一管理工伤预防宣传、培训、AI建设和风险排查项目。
          粤工保侧重点不是现场处置总览，而是把项目申报、预算执行、验收评价和后续归档串成连续办理台账，
          便于后续资金结算、月报归档和成效复核。
        </p>
      </div>
      <div class="ygb-table-tip">
        当前继续复用统一预防项目后台接口，不拆第二套项目业务表。后续如接预算拨付、验收材料和预防报告，仍在本台账链路内扩展。
      </div>
    </section>

    <div class="ygb-summary-grid">
      <div v-for="item in summaryCards" :key="item.key" class="ygb-summary-card" :class="item.cardClass">
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
        <el-form-item label="项目名称">
          <el-input v-model="queryParams.projectName" clearable style="width: 220px" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="企业">
          <el-select v-model="queryParams.enterpriseId" clearable filterable style="width: 220px">
            <el-option v-for="item in enterpriseOptions" :key="item.enterpriseId" :label="item.enterpriseName" :value="item.enterpriseId" />
          </el-select>
        </el-form-item>
        <el-form-item label="项目类型">
          <el-select v-model="queryParams.projectType" clearable style="width: 160px">
            <el-option v-for="item in projectTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="项目状态">
          <el-select v-model="queryParams.projectStatus" clearable style="width: 160px">
            <el-option v-for="item in projectStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
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
          <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['ygb:preventionProject:add']">新增项目</el-button>
        </el-col>
        <el-col v-if="!isReadOnlyRole" :span="1.5">
          <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['ygb:preventionProject:edit']">修改项目</el-button>
        </el-col>
        <el-col v-if="!isReadOnlyRole" :span="1.5">
          <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['ygb:preventionProject:remove']">删除项目</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['ygb:preventionProject:export']">导出</el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>
    </el-card>

    <el-card class="table-card ygb-table-card" shadow="never">
      <el-table v-loading="loading" :data="projectList" @selection-change="handleSelectionChange" @row-click="handleRowClick">
        <el-table-column v-if="!isReadOnlyRole" type="selection" width="55" align="center" />
        <el-table-column label="项目ID" prop="projectId" width="100" />
        <el-table-column label="项目名称" prop="projectName" min-width="220" />
        <el-table-column label="项目类型" width="120">
          <template #default="scope">
            <dict-tag :options="projectTypeOptions" :value="scope.row.projectType" />
          </template>
        </el-table-column>
        <el-table-column label="企业名称" prop="enterpriseName" min-width="220" />
        <el-table-column label="预算金额" width="120">
          <template #default="scope">
            {{ formatMoney(scope.row.budgetAmount) }}
          </template>
        </el-table-column>
        <el-table-column label="实际金额" width="120">
          <template #default="scope">
            {{ formatMoney(scope.row.actualAmount) }}
          </template>
        </el-table-column>
        <el-table-column label="项目状态" width="120">
          <template #default="scope">
            <dict-tag :options="projectStatusOptions" :value="scope.row.projectStatus" />
          </template>
        </el-table-column>
        <el-table-column label="评价分" prop="evaluationScore" width="100" />
        <el-table-column label="实施周期" min-width="220">
          <template #default="scope">
            {{ formatDate(scope.row.startDate) }} 至 {{ formatDate(scope.row.endDate) }}
          </template>
        </el-table-column>
        <el-table-column class-name="table-fill-column" min-width="1" />

        <el-table-column label="操作" fixed="right" align="center" width="220">
          <template #default="scope">
            <el-button link type="info" icon="View" @click.stop="openDetail(scope.row)">详情</el-button>
            <el-button v-if="!isReadOnlyRole" link type="primary" icon="Edit" @click.stop="handleUpdate(scope.row)" v-hasPermi="['ygb:preventionProject:edit']">修改</el-button>
            <el-button v-if="!isReadOnlyRole" link type="danger" icon="Delete" @click.stop="handleDelete(scope.row)" v-hasPermi="['ygb:preventionProject:remove']">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>

    <el-dialog :title="title" v-model="open" width="860px" append-to-body>
      <el-form ref="projectRef" :model="form" :rules="rules" label-width="110px">
        <div class="ygb-project-form-grid">
          <el-form-item label="项目名称" prop="projectName">
            <el-input v-model="form.projectName" placeholder="请输入项目名称" />
          </el-form-item>
          <el-form-item label="项目类型" prop="projectType">
            <el-select v-model="form.projectType" placeholder="请选择项目类型">
              <el-option v-for="item in projectTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="企业" prop="enterpriseId">
            <el-select v-model="form.enterpriseId" filterable placeholder="请选择企业" @change="handleEnterpriseChange">
              <el-option v-for="item in enterpriseOptions" :key="item.enterpriseId" :label="item.enterpriseName" :value="item.enterpriseId" />
            </el-select>
          </el-form-item>
          <el-form-item label="项目状态" prop="projectStatus">
            <el-select v-model="form.projectStatus" placeholder="请选择项目状态">
              <el-option v-for="item in projectStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="预算金额" prop="budgetAmount">
            <el-input-number v-model="form.budgetAmount" :min="0" :precision="2" :step="1000" style="width: 100%" />
          </el-form-item>
          <el-form-item label="实际金额" prop="actualAmount">
            <el-input-number v-model="form.actualAmount" :min="0" :precision="2" :step="1000" style="width: 100%" />
          </el-form-item>
          <el-form-item label="开始日期" prop="startDate">
            <el-date-picker v-model="form.startDate" type="date" format="YYYY-MM-DD" value-format="YYYY-MM-DD" style="width: 100%" />
          </el-form-item>
          <el-form-item label="结束日期" prop="endDate">
            <el-date-picker v-model="form.endDate" type="date" format="YYYY-MM-DD" value-format="YYYY-MM-DD" style="width: 100%" />
          </el-form-item>
          <el-form-item label="评价分" prop="evaluationScore">
            <el-input-number v-model="form.evaluationScore" :min="0" :max="100" style="width: 100%" />
          </el-form-item>
        </div>
        <el-form-item label="评价报告" prop="evaluationReport">
          <el-input v-model="form.evaluationReport" placeholder="请输入评价报告链接或摘要" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button v-if="!isReadOnlyRole" type="primary" @click="submitForm">确定</el-button>
          <el-button @click="cancel">取消</el-button>
        </div>
      </template>
    </el-dialog>

    <page-detail-dialog v-model="detailOpen" title="项目详情" width="760px">
      <template v-if="detailProject">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="项目名称">{{ detailProject.projectName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="项目类型">{{ projectTypeLabel(detailProject.projectType) }}</el-descriptions-item>
          <el-descriptions-item label="企业名称">{{ detailProject.enterpriseName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="项目状态">{{ projectStatusLabel(detailProject.projectStatus) }}</el-descriptions-item>
          <el-descriptions-item label="预算金额">{{ formatMoney(detailProject.budgetAmount) }}</el-descriptions-item>
          <el-descriptions-item label="实际金额">{{ formatMoney(detailProject.actualAmount) }}</el-descriptions-item>
          <el-descriptions-item label="评价分">{{ scoreText(detailProject.evaluationScore) }}</el-descriptions-item>
          <el-descriptions-item label="实施周期">{{ formatDate(detailProject.startDate) }} 至 {{ formatDate(detailProject.endDate) }}</el-descriptions-item>
          <el-descriptions-item label="评价报告" :span="2">{{ detailProject.evaluationReport || '-' }}</el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ detailProject.remark || '-' }}</el-descriptions-item>
        </el-descriptions>

        <div class="ygb-detail-block">
          <h3>办理提示</h3>
          <div class="ygb-tag-list">
            <el-tag v-for="item in detailHintTags" :key="item.label" :type="item.type" effect="plain">{{ item.label }}</el-tag>
          </div>
        </div>
      </template>
    </page-detail-dialog>
  </div>
</template>

<script setup name="YgbPreventionProject">
import { computed, getCurrentInstance, watchEffect } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useWorkbenchAssist } from '@/composables/useWorkbenchAssist'
import { decoratePortalExplanationItems, openPortalExplanationAction } from '@/utils/portalExplanation'
import { useRoleViewMode } from '@/utils/roleView'
import { applyWorkbenchRouteQuery, buildWorkbenchContext, stripWorkbenchRouteQuery } from '@/utils/workbenchLink'
import {
  formatMoney,
  formatRegionName,
  projectStatusLabel,
  projectStatusOptions,
  projectTypeLabel,
  projectTypeOptions,
  scoreText,
  usePreventionProjectPage,
  valueOrDefault
} from '@/views/preventionProject/usePreventionProjectPage'

const { proxy } = getCurrentInstance()
const route = useRoute()
const { setPageGuide } = useWorkbenchAssist()
const router = useRouter()
const { isReadOnlyRole, readOnlyRoleLabel, readOnlyRoleDescription } = useRoleViewMode()
const preventionProjectWorkbenchFields = ['enterpriseId', 'regionCode', 'projectType', 'projectStatus', 'focusKey']
const preventionProjectInitialQuery = {}
applyWorkbenchRouteQuery(route.query, preventionProjectInitialQuery, preventionProjectWorkbenchFields)

const workflowSteps = [
  { label: '项目申报建账', desc: '先明确项目名称、企业主体、类型、预算和计划周期，形成可追溯的预防项目主台账。' },
  { label: '执行推进回写', desc: '项目进入立项或实施后，持续回写实际投入和进度状态，避免预算与执行脱节。' },
  { label: '验收评价留痕', desc: '到达验收阶段后补录评价分和评价报告，确保后续结算与成效分析有依据。' },
  { label: '结项归档关联', desc: '项目结项后将结果纳入资金结算、预防月报和监管复核，不再停留在单次维护。' }
]

const {
  projectList,
  enterpriseOptions,
  open,
  detailOpen,
  loading,
  showSearch,
  single,
  multiple,
  total,
  title,
  currentProject,
  detailProject,
  summaryData,
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
  handleEnterpriseChange,
  submitForm,
  handleDelete,
  handleExport,
  formatDate
} = usePreventionProjectPage({
  exportFilePrefix: 'ygb_prevention_project',
  canMutate: () => !isReadOnlyRole.value,
  onBlockedAction: blockReadOnlyAction,
  initialQueryParams: preventionProjectInitialQuery
})

function buildPreventionProjectExplanationQuery(extraQuery = {}) {
  return {
    projectName: queryParams.value.projectName,
    regionCode: queryParams.value.regionCode,
    enterpriseId: queryParams.value.enterpriseId,
    projectType: queryParams.value.projectType,
    projectStatus: queryParams.value.projectStatus,
    ...extraQuery
  }
}

const summaryCards = computed(() => ([
  {
    key: 'total',
    label: '项目记录',
    value: valueOrDefault(summaryData.value.totalCount, total.value),
    unit: '项',
    note: '当前筛选条件下的工伤预防项目台账总量。',
    cardClass: ''
  },
  {
    key: 'budget',
    label: '预算金额汇总',
    value: formatMoney(summaryData.value.totalBudget),
    unit: '元',
    note: '用于快速判断当前项目库预算体量和后续资金承接压力。',
    cardClass: 'ygb-summary-card--success'
  },
  {
    key: 'active',
    label: '执行中项目',
    value: valueOrDefault(summaryData.value.activeProjectCount, 0),
    unit: '项',
    note: '处于立项、实施和验收阶段的项目数量，应作为持续回写重点。',
    cardClass: 'ygb-summary-card--warning'
  },
  {
    key: 'lowScore',
    label: '低评分项目',
    value: valueOrDefault(summaryData.value.lowScoreCount, 0),
    unit: '项',
    note: '评价分低于 80 分的项目数量，建议优先复核成效材料。',
    cardClass: 'ygb-summary-card--primary'
  }
]))

const focusItems = computed(() => ([
  { label: '当前企业', value: currentEnterpriseName() },
  { label: '待验收项目', value: `${valueOrDefault(summaryData.value.acceptancePendingCount, 0)} 项` },
  { label: '培训/AI类项目', value: `${valueOrDefault(summaryData.value.trainingAiProjectCount, 0)} 项` },
  { label: '高预算项目', value: `${valueOrDefault(summaryData.value.highBudgetCount, 0)} 项` }
]))

const portalExplanations = computed(() => {
  if (Array.isArray(summaryData.value.ygbExplanation) && summaryData.value.ygbExplanation.length) {
    return summaryData.value.ygbExplanation
  }
  return [
    {
      key: 'active',
      dimensionName: 'Active projects',
      currentValue: valueOrDefault(summaryData.value.activeProjectCount, 0),
      targetValue: 'Keep traceable',
      summary: 'Active projects should keep execution records aligned before acceptance and archive close.',
      evidenceModule: 'preventionProject',
      recommendModule: 'preventionProject',
      defaultQuery: buildPreventionProjectExplanationQuery({ focusKey: 'active' }),
      sourceLabel: '530.1 预防项目办理解释',
      sourceDescription: '在当前工作台范围内继续核查推进中的预防项目。',
      actionText: 'Review active projects'
    },
    {
      key: 'acceptance',
      dimensionName: 'Acceptance pending',
      currentValue: valueOrDefault(summaryData.value.acceptancePendingCount, 0),
      targetValue: '0',
      summary: 'Acceptance-pending projects should complete reports and evidence before monthly archive review.',
      evidenceModule: 'preventionProject',
      recommendModule: 'preventionProject',
      defaultQuery: buildPreventionProjectExplanationQuery({ projectStatus: '3', focusKey: 'acceptance' }),
      sourceLabel: '530.1 预防项目办理解释',
      sourceDescription: '在当前工作台范围内继续核查待验收项目。',
      actionText: 'Review acceptance pending'
    },
    {
      key: 'lowScore',
      dimensionName: 'Low-score projects',
      currentValue: valueOrDefault(summaryData.value.lowScoreCount, 0),
      targetValue: '>=80',
      summary: 'Low-score projects should strengthen evidence and follow-up before they are closed.',
      evidenceModule: 'preventionProject',
      recommendModule: 'preventionProject',
      defaultQuery: buildPreventionProjectExplanationQuery({ focusKey: 'lowScore' }),
      sourceLabel: '530.1 预防项目办理解释',
      sourceDescription: '在当前工作台范围内继续核查评分偏低项目。',
      actionText: 'Review low scores'
    },
    {
      key: 'trainingAi',
      dimensionName: 'Training and AI projects',
      currentValue: valueOrDefault(summaryData.value.trainingAiProjectCount, 0),
      targetValue: 'Keep measurable',
      summary: 'Training and AI projects should keep measurable outputs and reports before settlement and archive close.',
      evidenceModule: 'preventionProject',
      recommendModule: 'preventionProject',
      defaultQuery: buildPreventionProjectExplanationQuery({ focusKey: 'trainingAi' }),
      sourceLabel: '530.1 预防项目办理解释',
      sourceDescription: '在当前工作台范围内继续核查培训与 AI 类项目。',
      actionText: 'Review training and AI'
    }
  ]
})

const portalExplanationItems = computed(() => decoratePortalExplanationItems(portalExplanations.value, {
  portalCode: 'ygb',
  panelTitle: '530.1 预防项目办理解释',
  panelDescription: '当前解释项按 530.1 办理链口径展示预防项目推进、验收和培训重点。'
}))

const selectedProjectOverview = computed(() => {
  if (!currentProject.value) {
    return [
      { label: '项目名称', value: '-' },
      { label: '项目类型', value: '-' },
      { label: '项目状态', value: '-' },
      { label: '预算/评分', value: '-' }
    ]
  }
  return [
    { label: '项目名称', value: currentProject.value.projectName || '-' },
    { label: '项目类型', value: projectTypeLabel(currentProject.value.projectType) },
    { label: '项目状态', value: projectStatusLabel(currentProject.value.projectStatus) },
    {
      label: '预算/评分',
      value: `${formatMoney(currentProject.value.budgetAmount)} / ${scoreText(currentProject.value.evaluationScore)}`
    }
  ]
})

const workbenchContext = computed(() => buildWorkbenchContext(route.query, {
  fields: preventionProjectWorkbenchFields,
  sourceLabel: '上游工作台',
  title: '当前预防项目页沿用了上游来源条件。',
  description: '当前列表保留了企业、区域、项目类型和状态范围，便于沿同一办理语境继续推进项目链路。',
  fieldLabels: {
    enterpriseId: 'Enterprise',
    regionCode: 'Region',
    projectType: 'Project type',
    projectStatus: 'Project status',
    focusKey: '解释焦点'
  },
  fieldFormatters: {
    enterpriseId: value => {
      const matched = enterpriseOptions.value.find(item => String(item.enterpriseId) === String(value))
      return matched ? matched.enterpriseName : value
    },
    regionCode: value => formatRegionName(value, value),
    projectType: value => projectTypeLabel(value),
    projectStatus: value => projectStatusLabel(value),
    focusKey: value => preventionProjectFocusLabel(value)
  }
}))

const projectHintTags = computed(() => buildYgbHintTags(currentProject.value))
const detailHintTags = computed(() => buildYgbHintTags(detailProject.value || currentProject.value))
const readOnlyAlertTitle = computed(() => `${readOnlyRoleLabel.value}仅保留项目摘要、详情和导出`)
const readOnlyAlertDescription = computed(() => `${readOnlyRoleDescription.value || ''} 当前页面仍会展示项目摘要和详情，但不开放新增、修改和删除动作。`.trim())

function currentEnterpriseName() {
  if (!queryParams.value.enterpriseId) {
    return '全部企业'
  }
  const current = enterpriseOptions.value.find(item => item.enterpriseId === queryParams.value.enterpriseId)
  return current ? current.enterpriseName : queryParams.value.enterpriseId
}

function resetQuery() {
  proxy.resetForm('queryRef')
  Object.assign(queryParams.value, {
    pageNum: 1,
    pageSize: 10,
    projectName: undefined,
    regionCode: undefined,
    enterpriseId: undefined,
    projectType: undefined,
    projectStatus: undefined
  })
  applyWorkbenchRouteQuery(route.query, queryParams.value, preventionProjectWorkbenchFields)
  getList()

watchEffect(() => {
  setPageGuide({
    title: '????????' || '????????',
    description: '????????????????????????????' || '????????????????????????????',
    portalExplanation: portalExplanationItems.value,
    focus: [],
    selection: selectedProjectOverview.value,
    workflow: workflowSteps,
    hints: []
  })
})

}

function clearWorkbenchContext() {
  Object.assign(queryParams.value, {
    pageNum: 1,
    pageSize: 10,
    projectName: undefined,
    regionCode: undefined,
    enterpriseId: undefined,
    projectType: undefined,
    projectStatus: undefined
  })
  router.replace({
    path: route.path,
    query: stripWorkbenchRouteQuery(route.query, preventionProjectWorkbenchFields)
  })
  getList()
}

function applyPreventionProjectWorkbenchQuery(routeQuery = {}) {
  Object.assign(queryParams.value, {
    pageNum: 1,
    pageSize: 10,
    projectName: undefined,
    regionCode: undefined,
    enterpriseId: undefined,
    projectType: undefined,
    projectStatus: undefined
  })
  applyWorkbenchRouteQuery(routeQuery, queryParams.value, preventionProjectWorkbenchFields)
  currentProject.value = undefined
  getList()
}

function handlePortalExplanationAction(action) {
  if (action?.path === route.path) {
    applyPreventionProjectWorkbenchQuery(action.query || {})
  }
  openPortalExplanationAction(router, action)
}

function blockReadOnlyAction(actionLabel) {
  proxy?.$modal?.msgWarning?.(`${readOnlyRoleLabel.value}仅保留摘要、详情和导出，不能${actionLabel}`)
}

function preventionProjectFocusLabel(value) {
  if (value === 'active') return 'Active projects'
  if (value === 'acceptance') return 'Acceptance pending'
  if (value === 'lowScore') return 'Low-score projects'
  if (value === 'trainingAi') return 'Training and AI projects'
  return value || '-'
}

function buildYgbHintTags(project) {
  if (!project) {
    return [{ label: '请选择项目查看办理提示', type: 'info' }]
  }

  const tags = []
  const budgetAmount = Number(project.budgetAmount || 0)
  const actualAmount = Number(project.actualAmount || 0)
  const score = Number(project.evaluationScore || 0)

  if (String(project.projectStatus) === '0') {
    tags.push({ label: '项目仍在申报阶段，建议尽快补齐预算、周期和执行安排后转入立项。', type: 'warning' })
  }
  if (String(project.projectStatus) === '2') {
    tags.push({ label: '项目处于实施阶段，建议持续回写实际金额和阶段成果，避免结项前集中补录。', type: 'warning' })
  }
  if (String(project.projectStatus) === '3') {
    tags.push({ label: '项目已进入验收阶段，应优先补录评价报告和成效说明，避免长期滞留。', type: 'warning' })
  }
  if (String(project.projectStatus) === '4') {
    tags.push({ label: '项目已结项，可继续用于资金结算、预防月报和监管复核归档。', type: 'success' })
  }
  if (actualAmount > budgetAmount && budgetAmount > 0) {
    tags.push({ label: '实际金额已超过预算金额，建议复核执行范围和后续结算口径。', type: 'danger' })
  }
  if (score > 0 && score < 80) {
    tags.push({ label: '评价分偏低，建议重点复核项目成效、过程材料和整改闭环。', type: 'warning' })
  }
  if ((String(project.projectType) === '2' || String(project.projectType) === '3') && String(project.projectStatus) !== '4') {
    tags.push({ label: '培训或 AI 建设项目仍未结项，建议同步核对执行进度和可量化成果。', type: 'info' })
  }
  if (!project.evaluationReport && ['3', '4'].includes(String(project.projectStatus))) {
    tags.push({ label: '当前阶段缺少评价报告，建议补齐验收材料或报告链接。', type: 'warning' })
  }
  if (!tags.length) {
    tags.push({ label: '当前项目台账信息较完整，可继续用于后续验收、结算和归档。', type: 'success' })
  }
  return tags
}

</script>

<style scoped lang="scss">
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

.ygb-project-form-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 0 16px;
}

.ygb-detail-block {
  margin-top: 20px;
}

.ygb-detail-block h3 {
  margin: 0 0 12px;
  color: #13243a;
  font-size: 16px;
}

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
  color: #4f6478;
  line-height: 1.6;
}

@media (max-width: 1200px) {
  .ygb-summary-grid,
  .ygb-focus-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 900px) {
  .ygb-project-form-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .ygb-summary-grid,
  .ygb-focus-grid {
    grid-template-columns: 1fr;
  }
}
</style>

