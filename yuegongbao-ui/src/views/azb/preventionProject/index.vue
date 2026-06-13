<template>
  <div class="app-container azb-page azb-prevention-project-workbench">
    <section class="gov-page-header azb-page__header">
      <div>
        <p class="azb-page__eyebrow">事故预防治理</p>
        <h1 class="azb-page__title">{{ roleTitle }}</h1>
        <p class="azb-page__desc">
          {{ roleDescription }}
          当前继续复用统一预防项目台账和汇总接口，但安责保前端已按应急监管、企业执行复核、保险机构和银行协同四类高频视角重排焦点项目、验收顺序和详情重点。
        </p>
      </div>
      <div class="azb-page__tip">
        <div class="azb-page__tip-item">当前视角：{{ roleBadge }}</div>
        <div class="azb-page__tip-item">优先关注：高预算项目、验收滞留、低评分对象和培训/AI 建设项目。</div>
        <div class="azb-page__tip-item">新增、修改、删除继续走共享后台台账，不额外复制安责保独立业务表。</div>
        <div class="azb-page__tip-item">后续可继续接入经费拨付、验收材料、服务机构评价和减量成效佐证。</div>
      </div>
    </section>

    <div class="azb-summary-grid">
      <div
        v-for="item in summaryCards"
        :key="item.key"
        class="azb-summary-card"
        :class="item.cardClass"
      >
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
        <el-form-item label="项目名称">
          <el-input v-model="queryParams.projectName" clearable style="width: 220px" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="区域">
          <el-select v-model="queryParams.regionCode" clearable style="width: 180px">
            <el-option v-for="item in regionOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
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

    <el-card class="toolbar-card azb-toolbar-card" shadow="never">
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

    <el-card class="table-card azb-table-card" shadow="never">
      <template #header>
        <div class="azb-card-head azb-card-head--between">
          <div>
            <div class="azb-card-head__title">项目治理台账</div>
            <div class="azb-card-head__desc">{{ focusTableHint }}</div>
          </div>
          <div class="azb-card-head__desc">当前总量 {{ total }} 条</div>
        </div>
      </template>
      <el-table v-loading="loading" :data="visibleProjectList" @selection-change="handleSelectionChange" @row-click="handleRowClick">
        <el-table-column v-if="!isReadOnlyRole" type="selection" width="55" align="center" />
        <el-table-column label="项目ID" prop="projectId" width="100" />
        <el-table-column label="项目名称" prop="projectName" min-width="220" />
        <el-table-column label="项目类型" width="120">
          <template #default="scope">
            <dict-tag :options="projectTypeOptions" :value="scope.row.projectType" />
          </template>
        </el-table-column>
        <el-table-column label="企业名称" prop="enterpriseName" min-width="220" />
        <el-table-column label="区域" width="140">
          <template #default="scope">
            {{ formatRegionName(scope.row.regionCode, '-') }}
          </template>
        </el-table-column>
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
        <el-table-column label="评分" width="100">
          <template #default="scope">
            {{ scoreText(scope.row.evaluationScore) }}
          </template>
        </el-table-column>
        <el-table-column label="实施周期" min-width="220">
          <template #default="scope">
            {{ formatDate(scope.row.startDate) }} 至 {{ formatDate(scope.row.endDate) }}
          </template>
        </el-table-column>
        <el-table-column class-name="table-fill-column" min-width="1" />

        <el-table-column label="操作" fixed="right" align="center" width="220">
          <template #default="scope">
            <el-button link type="info" icon="View" @click.stop="openDetail(scope.row)">详情</el-button>
            <el-button
              v-if="!isReadOnlyRole"
              link
              type="primary"
              icon="Edit"
              @click.stop="handleUpdate(scope.row)"
              v-hasPermi="['ygb:preventionProject:edit']"
            >
              修改
            </el-button>
            <el-button
              v-if="!isReadOnlyRole"
              link
              type="primary"
              icon="Delete"
              @click.stop="handleDelete(scope.row)"
              v-hasPermi="['ygb:preventionProject:remove']"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>

    <el-dialog v-if="!isReadOnlyRole" :title="title" v-model="open" width="860px" append-to-body>
      <el-form ref="projectRef" :model="form" :rules="rules" label-width="110px">
        <div class="azb-project-form-grid">
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
          <el-form-item label="评分" prop="evaluationScore">
            <el-input-number v-model="form.evaluationScore" :min="0" :max="100" style="width: 100%" />
          </el-form-item>
        </div>
        <el-form-item label="评估报告" prop="evaluationReport">
          <el-input v-model="form.evaluationReport" placeholder="请输入评估报告链接或摘要" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入项目备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确定</el-button>
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
          <el-descriptions-item label="区域">{{ formatRegionName(detailProject.regionCode, '-') }}</el-descriptions-item>
          <el-descriptions-item label="预算金额">{{ formatMoney(detailProject.budgetAmount) }}</el-descriptions-item>
          <el-descriptions-item label="实际金额">{{ formatMoney(detailProject.actualAmount) }}</el-descriptions-item>
          <el-descriptions-item label="项目状态">{{ projectStatusLabel(detailProject.projectStatus) }}</el-descriptions-item>
          <el-descriptions-item label="评分">{{ scoreText(detailProject.evaluationScore) }}</el-descriptions-item>
          <el-descriptions-item label="实施周期" :span="2">{{ formatDate(detailProject.startDate) }} 至 {{ formatDate(detailProject.endDate) }}</el-descriptions-item>
          <el-descriptions-item label="当前视角重点" :span="2">{{ detailFocusText }}</el-descriptions-item>
          <el-descriptions-item label="评估报告" :span="2">{{ detailProject.evaluationReport || '-' }}</el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ detailProject.remark || '-' }}</el-descriptions-item>
        </el-descriptions>

        <div class="azb-detail-block">
          <h3>治理提示</h3>
          <div class="azb-tag-list">
            <el-tag v-for="item in detailHintTags" :key="item.label" :type="item.type" effect="plain">{{ item.label }}</el-tag>
          </div>
        </div>
      </template>
    </page-detail-dialog>
  </div>
</template>

<script setup name="AzbPreventionProject">
import { computed, getCurrentInstance, ref, watch, watchEffect } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useWorkbenchAssist } from '@/composables/useWorkbenchAssist'
import useUserStore from '@/store/modules/user'
import { decoratePortalExplanationItems, openPortalExplanationAction } from '@/utils/portalExplanation'
import { useRoleViewMode } from '@/utils/roleView'
import { applyWorkbenchRouteQuery, buildWorkbenchContext, stripWorkbenchRouteQuery } from '@/utils/workbenchLink'
import { useAuthorizedRegionOptions } from '@/utils/regionScope'
import {
  formatMoney,
  formatRegionName,
  isActiveProject,
  isHighBudget,
  isLowScore,
  isTrainingAiProject,
  matchProjectFocus,
  prioritizeFocusRows,
  projectStatusLabel,
  projectStatusOptions,
  projectTypeLabel,
  projectTypeOptions,
  regionOptions as allRegionOptions,
  scoreText,
  usePreventionProjectPage,
  valueOrDefault
} from '@/views/preventionProject/usePreventionProjectPage'

const { proxy } = getCurrentInstance()
const regionOptions = useAuthorizedRegionOptions(allRegionOptions)
const route = useRoute()
const { setPageGuide } = useWorkbenchAssist()
const router = useRouter()
const userStore = useUserStore()
const { isReadOnlyRole, isInsurerRole, isBankRole, readOnlyRoleLabel, readOnlyRoleDescription } = useRoleViewMode()
const activeFocusKey = ref('')
const preventionProjectWorkbenchFields = ['enterpriseId', 'regionCode', 'projectType', 'projectStatus', 'focusKey']
const preventionProjectInitialQuery = {}

applyWorkbenchRouteQuery(route.query, preventionProjectInitialQuery, preventionProjectWorkbenchFields)

const roleView = computed(() => {
  if (isBankRole.value) return 'bank'
  if (isInsurerRole.value) return 'insurer'
  const roles = userStore.roles || []
  if (roles.includes('ygb_enterprise_admin') || roles.includes('ygb_enterprise_operator')) {
    return 'site-enterprise'
  }
  return 'emergency'
})

const roleBadge = computed(() => {
  if (roleView.value === 'bank') return '银行只读协同'
  if (roleView.value === 'insurer') return '保险只读协同'
  if (roleView.value === 'site-enterprise') return '企业执行复核'
  return '应急监管处置'
})

const roleTitle = computed(() => {
  if (roleView.value === 'bank') return '银行预防项目协同看板'
  if (roleView.value === 'insurer') return '安责保预防项目验收复核台'
  if (roleView.value === 'site-enterprise') return '企业预防项目执行台账'
  return '事故预防项目治理工作台'
})

const roleDescription = computed(() => {
  if (roleView.value === 'bank') {
    return '面向银行协同只读查看项目预算体量、验收滞留和低评分对象，重点判断是否需要联动信用及区域报表口径。'
  }
  if (roleView.value === 'insurer') {
    return '面向保险机构聚焦项目执行、验收评价和成效留痕，重点复核哪些项目仍处于高预算、低评分或待验收状态。'
  }
  if (roleView.value === 'site-enterprise') {
    return '面向企业管理员、现场负责人和经办人员统一处理项目建账、执行回写、验收评价和结项留痕。'
  }
  return '面向应急监管统一识别区域高预算、验收滞留和低成效项目，形成可连续处置的治理队列。'
})

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
  syncCurrentProject,
  cancel,
  handleQuery,
  resetQuery: pageResetQuery,
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
  exportFilePrefix: 'azb_prevention_project',
  initialQueryParams: preventionProjectInitialQuery,
  canMutate: () => !isReadOnlyRole.value,
  onBlockedAction: blockReadOnlyAction,
  getCurrentList: () => visibleProjectList.value
})

function buildPreventionProjectExplanationQuery(extraQuery = {}) {
  return {
    enterpriseId: queryParams.value.enterpriseId,
    regionCode: queryParams.value.regionCode,
    ...extraQuery
  }
}

const portalExplanations = computed(() => {
  const highBudgetItem = {
    key: 'highBudget',
    dimensionName: '高预算项目',
    currentValue: valueOrDefault(summaryData.value.highBudgetCount, 0),
    targetValue: '预算受控',
    summary: '高预算项目对区域治理口径影响更大，应优先核定预算执行偏差和后续投入安排。',
    evidenceModule: 'preventionProject',
    recommendModule: 'preventionProject',
    defaultQuery: buildPreventionProjectExplanationQuery({ focusKey: 'highBudget' }),
    sourceLabel: '6.1 预防项目解释',
    sourceDescription: '从预防项目维度继续核查高预算治理对象。'
  }
  const acceptanceItem = {
    key: 'acceptance',
    dimensionName: '待验收项目',
    currentValue: valueOrDefault(summaryData.value.acceptancePendingCount, 0),
    targetValue: '0',
    summary: '待验收项目更容易形成治理停滞，应优先补齐验收材料、评估报告和闭环说明。',
    evidenceModule: 'preventionProject',
    recommendModule: 'preventionProject',
    defaultQuery: buildPreventionProjectExplanationQuery({ projectStatus: '3', focusKey: 'acceptance' }),
    sourceLabel: '6.1 预防项目解释',
    sourceDescription: '从预防项目维度继续核查待验收重点对象。'
  }
  const lowScoreItem = {
    key: 'lowScore',
    dimensionName: '低评分项目',
    currentValue: valueOrDefault(summaryData.value.lowScoreCount, 0),
    targetValue: '>=80',
    summary: '低评分项目说明成效解释偏弱，应优先补强整改说明和治理成效证据。',
    evidenceModule: 'preventionProject',
    recommendModule: 'preventionProject',
    defaultQuery: buildPreventionProjectExplanationQuery({ focusKey: 'lowScore' }),
    sourceLabel: '6.1 预防项目解释',
    sourceDescription: '从预防项目维度继续核查低评分治理对象。'
  }
  const trainingAiItem = {
    key: 'trainingAi',
    dimensionName: '培训/AI项目',
    currentValue: valueOrDefault(summaryData.value.trainingAiProjectCount, 0),
    targetValue: '量化成效',
    summary: '培训和 AI 项目更依赖过程材料与量化成效，应持续核定服务结果和复用价值。',
    evidenceModule: 'preventionProject',
    recommendModule: 'preventionProject',
    defaultQuery: buildPreventionProjectExplanationQuery({ focusKey: 'trainingAi' }),
    sourceLabel: '6.1 预防项目解释',
    sourceDescription: '从预防项目维度继续核查培训和 AI 治理对象。'
  }
  const activeItem = {
    key: 'active',
    dimensionName: '执行中项目',
    currentValue: valueOrDefault(summaryData.value.activeProjectCount, 0),
    targetValue: '持续回写',
    summary: '执行中项目决定当前治理推进节奏，应优先补齐执行过程和阶段成果回写。',
    evidenceModule: 'preventionProject',
    recommendModule: 'preventionProject',
    defaultQuery: buildPreventionProjectExplanationQuery({ focusKey: 'active' }),
    sourceLabel: '6.1 预防项目解释',
    sourceDescription: '从预防项目维度继续核查执行中的重点对象。'
  }

  if (roleView.value === 'bank') {
    return [highBudgetItem, acceptanceItem, lowScoreItem]
  }
  if (roleView.value === 'insurer') {
    return [acceptanceItem, lowScoreItem, trainingAiItem, highBudgetItem]
  }
  if (roleView.value === 'site-enterprise') {
    return [activeItem, acceptanceItem, highBudgetItem, trainingAiItem]
  }
  return [highBudgetItem, acceptanceItem, lowScoreItem, activeItem]
})

const portalExplanationItems = computed(() => decoratePortalExplanationItems(portalExplanations.value, {
  portalCode: 'azb',
  panelTitle: '6.1 预防项目解释',
  panelDescription: '围绕预算体量、验收状态和项目成效输出治理解释。'
}))

const summaryCards = computed(() => {
  const totalCount = valueOrDefault(summaryData.value.totalCount, total.value)
  const totalBudget = valueOrDefault(summaryData.value.totalBudget, 0)
  const highBudgetCount = valueOrDefault(summaryData.value.highBudgetCount, 0)
  const acceptancePendingCount = valueOrDefault(summaryData.value.acceptancePendingCount, 0)
  const lowScoreCount = valueOrDefault(summaryData.value.lowScoreCount, 0)
  const trainingAiProjectCount = valueOrDefault(summaryData.value.trainingAiProjectCount, 0)
  const activeProjectCount = valueOrDefault(summaryData.value.activeProjectCount, 0)
  const summaryCard = (key, label, value, unit, note, cardClass = '') => ({ key, label, value, unit, note, cardClass })

  if (roleView.value === 'bank') {
    return [
      summaryCard('budget', '预算总额', totalBudget, '元', '当前范围内的项目预算体量。', 'azb-summary-card--success'),
      summaryCard('highBudget', '高预算项目', highBudgetCount, '项', '预算规模较高的项目对象。', 'azb-summary-card--danger'),
      summaryCard('acceptance', '待验收项目', acceptancePendingCount, '项', '仍在等待验收闭环的项目。', 'azb-summary-card--warning'),
      summaryCard('lowScore', '低评分项目', lowScoreCount, '项', '成效评价偏低的项目。', 'azb-summary-card--primary')
    ]
  }

  if (roleView.value === 'insurer') {
    return [
      summaryCard('acceptance', '待验收项目', acceptancePendingCount, '项', '验收跟进仍待推进。', 'azb-summary-card--warning'),
      summaryCard('lowScore', '低评分项目', lowScoreCount, '项', '评价分偏低的项目对象。', 'azb-summary-card--danger'),
      summaryCard('trainingAi', '培训/AI项目', trainingAiProjectCount, '项', '培训和 AI 相关项目。', 'azb-summary-card--primary'),
      summaryCard('budget', '预算总额', totalBudget, '元', '当前范围内的项目预算体量。', 'azb-summary-card--success')
    ]
  }

  if (roleView.value === 'site-enterprise') {
    return [
      summaryCard('active', '执行中项目', activeProjectCount, '项', '仍在实施推进中的项目。', 'azb-summary-card--warning'),
      summaryCard('acceptance', '待验收项目', acceptancePendingCount, '项', '等待验收的项目对象。', 'azb-summary-card--primary'),
      summaryCard('highBudget', '高预算项目', highBudgetCount, '项', '预算规模较高的项目。', 'azb-summary-card--danger'),
      summaryCard('budget', '预算总额', totalBudget, '元', '当前范围内的项目预算体量。', 'azb-summary-card--success')
    ]
  }

  return [
    summaryCard('total', '项目总量', totalCount, '项', '当前纳入治理的项目记录总数。', ''),
    summaryCard('highBudget', '高预算项目', highBudgetCount, '项', '预算规模较高的项目。', 'azb-summary-card--danger'),
    summaryCard('acceptance', '待验收项目', acceptancePendingCount, '项', '等待验收闭环的项目。', 'azb-summary-card--warning'),
    summaryCard('budget', '预算总额', totalBudget, '元', '当前范围内的项目预算体量。', 'azb-summary-card--success')
  ]
})

const focusQueues = computed(() => {
  const activeProjectCount = valueOrDefault(summaryData.value.activeProjectCount, 0)
  const acceptancePendingCount = valueOrDefault(summaryData.value.acceptancePendingCount, 0)
  const lowScoreCount = valueOrDefault(summaryData.value.lowScoreCount, 0)
  const trainingAiProjectCount = valueOrDefault(summaryData.value.trainingAiProjectCount, 0)
  const highBudgetCount = valueOrDefault(summaryData.value.highBudgetCount, 0)
  const totalCount = valueOrDefault(summaryData.value.totalCount, total.value)

  if (roleView.value === 'bank') {
    return [
      { key: 'highBudget', title: '高预算协同项目', desc: '优先识别预算体量大且可能影响区域风险口径的项目对象。', count: highBudgetCount, unit: '项', actionText: '联动区域口径' },
      { key: 'lowScore', title: '低评分协同项目', desc: '查看哪些项目成效偏弱，需要与信用或报表口径联动观察。', count: lowScoreCount, unit: '项', actionText: '关注成效风险' },
      { key: 'acceptance', title: '验收滞留项目', desc: '尚未完成验收留痕的项目更适合纳入协同提示。', count: acceptancePendingCount, unit: '项', actionText: '提示业务补录' }
    ]
  }

  if (roleView.value === 'insurer') {
    return [
      { key: 'acceptance', title: '待验收重点项目', desc: '优先补齐评估报告、验收说明和成效留痕。', count: acceptancePendingCount, unit: '项', actionText: '优先验收复核' },
      { key: 'lowScore', title: '低评分项目', desc: '重点复核成效不足或闭环偏弱的项目对象。', count: lowScoreCount, unit: '项', actionText: '跟踪整改说明' },
      { key: 'trainingAi', title: '培训/AI项目', desc: '这两类项目通常更依赖可量化成果和阶段性回写。', count: trainingAiProjectCount, unit: '项', actionText: '核对成果材料' }
    ]
  }

  if (roleView.value === 'site-enterprise') {
    return [
      { key: 'active', title: '当前待补回写项目', desc: '先找出仍在执行中的项目，避免集中在结项前补录。', count: activeProjectCount, unit: '项', actionText: '先补执行回写' },
      { key: 'acceptance', title: '待验收项目', desc: '财务和经办优先补齐验收评估报告与成效说明。', count: acceptancePendingCount, unit: '项', actionText: '补验收材料' },
      { key: 'highBudget', title: '高预算项目', desc: '同步确认预算与实际执行偏差，避免后续结算口径失真。', count: highBudgetCount, unit: '项', actionText: '核对预算执行' }
    ]
  }

  return [
    { key: 'highBudget', title: '区域高预算项目', desc: '优先处置预算体量大、影响面更广的项目对象。', count: highBudgetCount, unit: '项', actionText: '先看预算压力' },
    { key: 'acceptance', title: '验收滞留项目', desc: '验收长期未闭环的项目通常伴随成效与材料缺口。', count: acceptancePendingCount, unit: '项', actionText: '跟进验收闭环' },
    { key: 'lowScore', title: '低成效项目', desc: '评价分偏低对象需要尽快判断是否纳入重点督办。', count: lowScoreCount, unit: '项', actionText: '核查成效不足' },
    { key: 'all', title: '当前监管总量', desc: '用于统看当前区域已纳入治理的项目盘子规模。', count: totalCount, unit: '项', actionText: '查看整体盘子' }
  ]
})

const activeFocus = computed(() => {
  const queues = focusQueues.value
  if (!queues.length) {
    return undefined
  }
  return queues.find(item => item.key === activeFocusKey.value) || queues[0]
})

const visibleProjectList = computed(() => prioritizeFocusRows(projectList.value, row => matchProjectFocus(row, activeFocus.value?.key)))

const focusTableHint = computed(() => {
  if (!activeFocus.value) {
    return '当前按默认顺序展示项目台账。'
  }
  return `当前优先展示“${activeFocus.value.title}”相关项目。`
})

const selectedProjectOverview = computed(() => {
  if (!currentProject.value) {
    return [
      { label: '项目名称', value: '-' },
      { label: '项目类型', value: '-' },
      { label: activeFocus.value?.title || '当前焦点', value: '-' },
      { label: roleView.value === 'site-enterprise' ? '预算/评分' : '项目状态', value: '-' }
    ]
  }
  return [
    { label: '项目名称', value: currentProject.value.projectName || '-' },
    { label: '项目类型', value: projectTypeLabel(currentProject.value.projectType) },
    { label: activeFocus.value?.title || '当前焦点', value: activeFocusValue(currentProject.value) },
    {
      label: roleView.value === 'site-enterprise' ? '预算/评分' : '项目状态',
      value: roleView.value === 'site-enterprise'
        ? `${formatMoney(currentProject.value.budgetAmount)} / ${scoreText(currentProject.value.evaluationScore)}`
        : projectStatusLabel(currentProject.value.projectStatus)
    }
  ]
})

const primaryProjectAction = computed(() => {
  if (!currentProject.value || isReadOnlyRole.value) {
    return { label: '查看详情', action: 'detail' }
  }
  if (String(currentProject.value.projectStatus) === '3') {
    return { label: '维护项目', action: 'edit' }
  }
  if (isHighBudget(currentProject.value) || isLowScore(currentProject.value.evaluationScore) || isActiveProject(currentProject.value.projectStatus)) {
    return { label: '维护项目', action: 'edit' }
  }
  return { label: '查看详情', action: 'detail' }
})

const currentProjectActionSummary = computed(() => {
  if (!currentProject.value) {
    return '先从左侧焦点队列选择一类重点项目，再在台账里联动查看当前建议动作。'
  }
  if (primaryProjectAction.value.action === 'edit') {
    return '当前项目更适合直接进入维护流程，优先补齐执行进度、验收材料、评价结果或预算偏差信息。'
  }
  return '当前项目以详情复核为主，先看项目状态、预算执行和成效说明，再决定是否进入维护。'
})

const currentProjectActionTags = computed(() => {
  if (!currentProject.value) {
    return [{ label: '未选中项目', type: 'info' }]
  }
  return buildProjectHintTags(currentProject.value, roleView.value).slice(0, 3)
})

const workflowSteps = computed(() => {
  if (roleView.value === 'bank') {
    return [
      { label: '识别高预算项目', desc: '先从预算体量大或影响面广的项目里判断哪些需要联动区域风险口径。' },
      { label: '查看验收与评分', desc: '确认是否已完成验收留痕，以及是否存在低评分或长期滞留。' },
      { label: '形成协同提示', desc: '将高预算、低成效和验收缺口项目回传业务侧跟进。' },
      { label: '留存区域口径', desc: '将当前筛选结果沉淀为协同报表和区域观察依据。' }
    ]
  }
  if (roleView.value === 'insurer') {
    return [
      { label: '筛出待复核项目', desc: '先锁定待验收、低评分和培训/AI 项目，形成当天复核清单。' },
      { label: '补齐评价与成效', desc: '重点确认评价分、验收报告和过程材料是否完整。' },
      { label: '复核预算执行', desc: '核对预算与实际投入偏差，避免成效结论失真。' },
      { label: '完成验收闭环', desc: '将可结项对象推进到评价齐全、留痕可核对状态。' }
    ]
  }
  if (roleView.value === 'site-enterprise') {
    return [
      { label: '确认当月项目盘子', desc: '先看当前哪些项目仍在执行中、待验收或预算偏大。' },
      { label: '持续回写执行', desc: '把预算、实际投入和阶段成果补齐到统一项目台账。' },
      { label: '补录验收材料', desc: '企业经办补齐评价分、验收报告和成效说明。' },
      { label: '完成结项留痕', desc: '将项目结果沉淀为资金复盘、月报和后续监管核查依据。' }
    ]
  }
  return [
    { label: '锁定区域重点项目', desc: '先找出高预算、验收滞留和低评分的区域重点对象。' },
    { label: '下钻查看项目详情', desc: '联动企业、类型、预算和项目状态，判断治理原因。' },
    { label: '督促业务闭环', desc: '推动企业或保险机构补齐执行、验收和成效留痕。' },
    { label: '形成监管留痕', desc: '把当前筛选和处置关注项目沉淀为区域核查依据。' }
  ]
})

const hintTags = computed(() => buildProjectHintTags(currentProject.value, roleView.value))
const detailHintTags = computed(() => buildProjectHintTags(detailProject.value || currentProject.value, roleView.value))

const detailFocusText = computed(() => {
  const project = detailProject.value || currentProject.value
  if (!project) {
    return activeFocus.value?.desc || '请选择项目查看当前视角重点。'
  }
  if (roleView.value === 'bank') {
    return '银行协同重点查看该项目是否存在高预算、低评分和验收留痕不足的问题，便于与区域报表和信用口径联动观察。'
  }
  if (roleView.value === 'insurer') {
    return '保险机构重点复核该项目的执行进度、验收报告、评价分和成效说明，确保项目留痕连续可核对。'
  }
  if (roleView.value === 'site-enterprise') {
    return '企业侧应优先补齐该项目的执行回写、评价分和验收报告，避免结项留痕和后续复核被卡住。'
  }
  return '应急监管重点关注该项目是否属于高预算、验收滞留或低成效对象，并判断是否需要纳入区域重点督办。'
})

const readOnlyAlertTitle = computed(() => `${readOnlyRoleLabel.value}：当前仅保留项目摘要、详情和导出`)

const readOnlyAlertDescription = computed(() => {
  if (isInsurerRole.value) {
    return `${readOnlyRoleDescription.value} 当前保险机构视角仅支持查看，不开放直接维护。`
  }
  return `${readOnlyRoleDescription.value} 当前页面为只读视角。`
})

const workbenchContext = computed(() => buildWorkbenchContext(route.query, {
  fields: preventionProjectWorkbenchFields,
  title: '当前项目台账沿用了工作台来源条件',
  description: '已按上游工作台带入的企业和区域范围筛选数据，适合继续处理重点项目和验收闭环。',
  fieldLabels: {
    enterpriseId: '企业',
    regionCode: '区域',
    projectType: '项目类型',
    projectStatus: '项目状态',
    focusKey: '焦点类型'
  },
  fieldFormatters: {
    enterpriseId: value => enterpriseOptions.value.find(item => String(item.enterpriseId) === String(value))?.enterpriseName || value,
    regionCode: value => formatRegionName(value, value),
    projectType: value => projectTypeLabel(value),
    projectStatus: value => projectStatusLabel(value),
    focusKey: value => preventionProjectFocusLabel(value)
  }
}))

watch(focusQueues, queues => {
  if (!queues.length) {
    activeFocusKey.value = ''
    return
  }
  if (!queues.find(item => item.key === activeFocusKey.value)) {
    activeFocusKey.value = queues[0].key
  }
}, { immediate: true })

function handleSelectFocus(item) {
  activeFocusKey.value = item.key
  syncCurrentProject()
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
  activeFocusKey.value = resolvePreventionProjectFocusKey(routeQuery.focusKey)
  getList()

watchEffect(() => {
  setPageGuide({
    title: roleTitle.value || '?????????',
    description: roleDescription.value || '?????????????????????????????????',
    portalExplanation: portalExplanationItems.value,
    focus: focusQueues.value,
    selection: [...selectedProjectOverview.value, { label: '??????', value: currentProjectActionSummary.value }],
    workflow: workflowSteps.value,
    hints: [...currentProjectActionTags.value, ...hintTags.value].slice(0, 6)
  })
})

}

function handlePrimaryProjectAction() {
  if (!currentProject.value) {
    return
  }
  if (primaryProjectAction.value.action === 'edit') {
    if (blockReadOnlyAction('修改项目')) {
      return
    }
    handleUpdate(currentProject.value)
    return
  }
  openDetail(currentProject.value)
}

function blockReadOnlyAction(actionLabel) {
  if (!isReadOnlyRole.value) {
    return false
  }
  proxy.$modal.msgWarning(`${readOnlyRoleLabel.value}仅保留摘要、详情和导出，不能${actionLabel}`)
  return true
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
}

function clearWorkbenchContext() {
  activeFocusKey.value = ''
  Object.assign(queryParams.value, {
    pageNum: 1,
    pageSize: 10,
    projectName: undefined,
    regionCode: undefined,
    enterpriseId: undefined,
    projectType: undefined,
    projectStatus: undefined,
    focusKey: undefined
  })
  router.replace({
    path: route.path,
    query: stripWorkbenchRouteQuery(route.query, preventionProjectWorkbenchFields)
  })
  getList()
}

function activeFocusValue(project) {
  if (!project) {
    return '-'
  }
  if (activeFocus.value?.key === 'highBudget') {
    return isHighBudget(project) ? '高预算' : '常规预算'
  }
  if (activeFocus.value?.key === 'acceptance') {
    return String(project.projectStatus) === '3' ? '待验收' : '非待验收'
  }
  if (activeFocus.value?.key === 'lowScore') {
    return isLowScore(project.evaluationScore) ? '低评分' : '评分正常'
  }
  if (activeFocus.value?.key === 'trainingAi') {
    return isTrainingAiProject(project.projectType) ? '培训/AI项目' : '其他项目'
  }
  if (activeFocus.value?.key === 'active') {
    return isActiveProject(project.projectStatus) ? '执行中' : '非执行中'
  }
  if (activeFocus.value?.key === 'all') {
    return `${formatMoney(project.budgetAmount)} 预算`
  }
  return projectStatusLabel(project.projectStatus)
}

function resolvePreventionProjectFocusKey(value) {
  return Array.isArray(value) ? String(value[0] || '') : String(value || '')
}

activeFocusKey.value = resolvePreventionProjectFocusKey(route.query.focusKey)

function preventionProjectFocusLabel(value) {
  return ({
    highBudget: '高预算项目',
    acceptance: '待验收项目',
    lowScore: '低评分项目',
    trainingAi: '培训/AI项目',
    active: '执行中项目',
    all: '全部对象'
  })[String(value || '')] || (value || '-')
}

function buildProjectHintTags(project, role) {
  if (!project) {
    return [{ label: '请选择项目查看治理提示', type: 'info' }]
  }
  const tags = []
  const budgetAmount = Number(project.budgetAmount || 0)
  const actualAmount = Number(project.actualAmount || 0)
  const score = Number(project.evaluationScore || 0)

  if (String(project.projectStatus) === '0') {
    tags.push({ label: '项目仍在申报阶段，建议尽快补齐预算、周期和执行安排后转入立项。', type: 'warning' })
  }
  if (String(project.projectStatus) === '2') {
    tags.push({
      label: role === 'emergency' ? '项目处于实施阶段，建议持续跟踪阶段成果和验收准备情况。' : '项目处于实施阶段，建议持续回写实际金额和阶段成果。',
      type: 'warning'
    })
  }
  if (String(project.projectStatus) === '3') {
    tags.push({
      label: role === 'bank' ? '项目已进入验收阶段，协同侧可提示业务方尽快补齐验收和评价材料。' : '项目已进入验收阶段，应优先补录评价报告和成效说明。',
      type: 'warning'
    })
  }
  if (String(project.projectStatus) === '4') {
    tags.push({ label: '项目已结项，可继续用于资金复盘、专项月报和监管复核留痕。', type: 'success' })
  }
  if (isHighBudget(project)) {
    tags.push({
      label: role === 'site-enterprise' ? '当前为高预算项目，建议同步核对预算执行偏差和后续结算口径。' : '当前为高预算项目，建议优先纳入重点复核或督办视野。',
      type: 'danger'
    })
  }
  if (actualAmount > budgetAmount && budgetAmount > 0) {
    tags.push({ label: '实际金额已超过预算金额，建议复核执行范围和后续结算口径。', type: 'danger' })
  }
  if (score > 0 && score < 80) {
    tags.push({
      label: role === 'emergency' ? '评价分偏低，建议判断是否纳入区域重点项目督办。' : '评价分偏低，建议重点复核项目成效、过程材料和整改闭环。',
      type: 'warning'
    })
  }
  if (isTrainingAiProject(project.projectType) && String(project.projectStatus) !== '4') {
    tags.push({ label: '培训或 AI 建设项目尚未结项，建议同步核对执行进度和可量化成果。', type: 'info' })
  }
  if (!project.evaluationReport && ['3', '4'].includes(String(project.projectStatus))) {
    tags.push({ label: '当前阶段缺少评价报告，建议补齐验收材料或报告链接。', type: 'warning' })
  }
  if (!tags.length) {
    tags.push({ label: '当前项目信息较完整，可继续用于验收、复盘和留痕复核。', type: 'success' })
  }
  return tags
}
</script>

<style scoped lang="scss">
.azb-prevention-project-workbench {
  .azb-focus-grid {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 16px;
    margin-bottom: 16px;
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
    margin-bottom: 12px;
    color: #516273;
    font-size: 13px;
    line-height: 1.7;
  }

  .azb-workbench-alert__desc strong {
    color: #0f5ea8;
  }

  .azb-card-head__title {
    font-size: 16px;
    font-weight: 600;
    color: #0f172a;
  }

  .azb-card-head__desc {
    margin-top: 4px;
    font-size: 13px;
    line-height: 1.6;
    color: #64748b;
  }

  .azb-focus-list--single {
    display: flex;
    flex-direction: column;
    gap: 12px;
  }

  .azb-focus-queue {
    width: 100%;
    display: flex;
    justify-content: space-between;
    gap: 16px;
    padding: 14px 16px;
    border: 1px solid #dbe7f3;
    border-radius: 10px;
    background: #f8fbff;
    text-align: left;
    cursor: pointer;
    transition: border-color 0.2s ease, box-shadow 0.2s ease;
  }

  .azb-focus-queue:hover,
  .azb-focus-queue.is-active {
    border-color: #0b6b78;
    box-shadow: 0 10px 24px rgba(11, 107, 120, 0.08);
  }

  .azb-focus-queue__main strong {
    display: block;
    margin-bottom: 6px;
    color: #0f172a;
  }

  .azb-focus-queue__main p {
    margin: 0;
    font-size: 13px;
    line-height: 1.7;
    color: #64748b;
  }

  .azb-focus-queue__side {
    min-width: 120px;
    display: flex;
    flex-direction: column;
    align-items: flex-end;
    justify-content: center;
    gap: 8px;
  }

  .azb-focus-queue__count {
    font-size: 20px;
    font-weight: 700;
    color: #0b6b78;
  }

  .azb-focus-queue__action {
    font-size: 12px;
    color: #1d4ed8;
  }

  .azb-source-list {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 12px;
  }

  .azb-source-item {
    padding: 14px 16px;
    border: 1px solid #dbe7f3;
    border-radius: 10px;
    background: #f8fbff;
  }

  .azb-source-item__label {
    margin-bottom: 8px;
    font-size: 12px;
    color: #64748b;
  }

  .azb-source-item__value {
    font-size: 16px;
    font-weight: 600;
    color: #0f172a;
    line-height: 1.5;
  }

  .azb-focus-actions {
    display: flex;
    flex-wrap: wrap;
    gap: 12px;
    margin-top: 16px;
  }

  .azb-pipeline-list {
    display: flex;
    flex-direction: column;
    gap: 14px;
  }

  .azb-pipeline-item {
    display: flex;
    gap: 14px;
    align-items: flex-start;
    padding: 14px 16px;
    border-radius: 10px;
    background: #f8fbff;
    border: 1px solid #dbe7f3;
  }

  .azb-pipeline-item__index {
    min-width: 40px;
    height: 40px;
    border-radius: 12px;
    background: #0b6b78;
    color: #fff;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    font-size: 14px;
    font-weight: 700;
  }

  .azb-pipeline-item__body strong {
    display: block;
    color: #0f172a;
    margin-bottom: 4px;
  }

  .azb-pipeline-item__body p {
    margin: 0;
    font-size: 13px;
    line-height: 1.7;
    color: #64748b;
  }

  .azb-tag-list {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
  }

  .azb-detail-block {
    margin-top: 18px;
    padding: 16px;
    border: 1px solid #dbe7f3;
    border-radius: 10px;
    background: #f8fbff;
  }

  .azb-detail-block h3 {
    margin: 0 0 12px;
    font-size: 15px;
    font-weight: 600;
    color: #0f172a;
  }

  .azb-project-form-grid {
    display: grid;
    grid-template-columns: repeat(3, minmax(0, 1fr));
    gap: 0 16px;
  }

  @media (max-width: 1200px) {
    .azb-focus-grid {
      grid-template-columns: 1fr;
    }
  }

  @media (max-width: 900px) {
    .azb-project-form-grid {
      grid-template-columns: 1fr;
    }
  }

  @media (max-width: 768px) {
    .azb-source-list {
      grid-template-columns: 1fr;
    }

    .azb-focus-queue {
      flex-direction: column;
    }

    .azb-focus-queue__side {
      min-width: 0;
      align-items: flex-start;
    }
  }
}
</style>


