<template>
  <div class="app-container ygb-page ygb-credit-score-page">
    <section class="gov-page-header ygb-page__header">
      <div>
        <p class="ygb-page__eyebrow">信用评价</p>
        <h1 class="ygb-page__title">{{ roleTitle }}</h1>
        <p class="ygb-page__desc">
          {{ roleDescription }}
          粤工保侧重点不是单独展示风险，而是把评分结果与合同、考勤、工资、社保税务、预警闭环串成一条可复核、可追踪、可归档的业务台账。
        </p>
      </div>
      <div class="ygb-page__tip">
        当前视角：{{ roleBadge }}。{{ roleTip }}
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

    <el-alert
      v-if="isReadOnlyRole"
      class="ygb-readonly-alert"
      type="warning"
      :closable="false"
      :title="readOnlyAlertTitle"
      :description="readOnlyAlertDescription"
      show-icon
      style="margin-bottom: 18px;"
    />

    <el-alert
      v-if="workbenchContext"
      class="ygb-workbench-alert"
      type="info"
      :closable="false"
      show-icon
    >
      <template #title>
        <div class="ygb-workbench-alert__title">
          <span>{{ workbenchContext.title }}</span>
          <el-button link type="primary" @click="clearWorkbenchContext">清空来源条件</el-button>
        </div>
      </template>
      <div class="ygb-workbench-alert__desc">
        <strong>{{ workbenchContext.sourceLabel }}</strong>
        <span>{{ workbenchContext.description }}</span>
      </div>
      <div class="ygb-tag-list">
        <el-tag v-for="item in workbenchContext.tags" :key="item.key" effect="plain">{{ item.label }}：{{ item.value }}</el-tag>
      </div>
    </el-alert>

    <el-card class="search-card ygb-search-card" shadow="never">
      <el-form ref="queryRef" :model="queryParams" :inline="true" v-show="showSearch">
        <el-form-item label="统计月份">
          <el-date-picker v-model="queryParams.statMonth" type="month" value-format="YYYY-MM" format="YYYY-MM" style="width: 170px" />
        </el-form-item>
        <el-form-item label="企业">
          <el-select v-model="queryParams.enterpriseId" clearable filterable style="width: 220px">
            <el-option v-for="item in enterpriseOptions" :key="item.enterpriseId" :label="item.enterpriseName" :value="item.enterpriseId" />
          </el-select>
        </el-form-item>
        <el-form-item label="区域">
          <el-select v-model="queryParams.regionCode" clearable style="width: 180px">
            <el-option v-for="item in regionOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="信用等级">
          <el-select v-model="queryParams.creditLevel" clearable style="width: 140px">
            <el-option v-for="item in levelOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="红黄绿码">
          <el-select v-model="queryParams.colorCode" clearable style="width: 140px">
            <el-option v-for="item in colorOptions" :key="item.value" :label="item.label" :value="item.value" />
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
        <el-col v-if="canGenerate" :span="1.5">
          <el-button type="primary" plain icon="MagicStick" @click="guardedOpenGenerateDialog()" v-hasPermi="['ygb:creditScore:generate']">生成评分</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['ygb:creditScore:export']">导出</el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>
    </el-card>

    <el-card class="table-card ygb-table-card" shadow="never">
      <template #header>
        <div class="ygb-card-head ygb-card-head--between">
          <div>
            <div class="ygb-card-head__title">企业信用评分办理台账</div>
            <div class="ygb-card-head__desc">{{ focusTableHint }}</div>
          </div>
          <div class="ygb-card-head__desc">当前总量 {{ total }} 条</div>
        </div>
      </template>
      <el-table v-loading="loading" :data="visibleScoreList" @row-click="handleRowClick">
        <el-table-column label="评分ID" prop="scoreId" width="90" />
        <el-table-column label="统计月份" prop="statMonth" width="110" />
        <el-table-column label="企业名称" prop="enterpriseName" min-width="220" />
        <el-table-column label="区域" width="140">
          <template #default="scope">
            {{ scope.row.regionName || regionNameMap[scope.row.regionCode] || scope.row.regionCode }}
          </template>
        </el-table-column>
        <el-table-column label="合同" prop="contractScore" width="90" />
        <el-table-column label="考勤" prop="attendanceScore" width="90" />
        <el-table-column label="工资" prop="salaryScore" width="90" />
        <el-table-column label="社保税务" prop="socialTaxScore" width="110" />
        <el-table-column label="安全" prop="safetyScore" width="90" />
        <el-table-column label="闭环" prop="governanceScore" width="90" />
        <el-table-column label="总分" prop="totalScore" width="100" />
        <el-table-column label="等级" width="90">
          <template #default="scope">
            <el-tag :type="levelTagType(scope.row.creditLevel)">{{ scope.row.creditLevel }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="红黄绿码" width="100">
          <template #default="scope">
            <el-tag :type="colorTagType(scope.row.colorCode)">{{ colorLabel(scope.row.colorCode) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="排名" prop="rankNo" width="80" />
        <el-table-column label="摘要" prop="summaryText" min-width="260" show-overflow-tooltip />
        <el-table-column label="评分时间" width="180">
          <template #default="scope">
            {{ parseTime(scope.row.evaluateTime, '{y}-{m}-{d} {h}:{i}:{s}') }}
          </template>
        </el-table-column>
        <el-table-column class-name="table-fill-column" min-width="1" />

        <el-table-column label="操作" fixed="right" align="center" width="120">
          <template #default="scope">
            <el-button link type="info" icon="View" @click.stop="openDetail(scope.row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>

    <el-dialog v-if="!isReadOnlyRole" title="生成企业信用评分" v-model="generateOpen" width="520px" append-to-body>
      <el-form ref="generateRef" :model="generateForm" :rules="generateRules" label-width="96px">
        <el-form-item label="统计月份" prop="statMonth">
          <el-date-picker v-model="generateForm.statMonth" type="month" value-format="YYYY-MM" format="YYYY-MM" style="width: 100%" />
        </el-form-item>
        <el-form-item label="区域">
          <el-select v-model="generateForm.regionCode" clearable style="width: 100%">
            <el-option v-for="item in regionOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="企业">
          <el-select v-model="generateForm.enterpriseId" clearable filterable style="width: 100%">
            <el-option v-for="item in enterpriseOptions" :key="item.enterpriseId" :label="item.enterpriseName" :value="item.enterpriseId" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="guardedSubmitGenerate">生成</el-button>
          <el-button @click="generateOpen = false">取消</el-button>
        </div>
      </template>
    </el-dialog>

    <page-detail-dialog v-model="detailOpen" title="信用评分详情" width="760px">
      <template v-if="scoreDetail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="企业">{{ scoreDetail.enterpriseName }}</el-descriptions-item>
          <el-descriptions-item label="统计月份">{{ scoreDetail.statMonth }}</el-descriptions-item>
          <el-descriptions-item label="信用等级">
            <el-tag :type="levelTagType(scoreDetail.creditLevel)">{{ scoreDetail.creditLevel }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="红黄绿码">
            <el-tag :type="colorTagType(scoreDetail.colorCode)">{{ colorLabel(scoreDetail.colorCode) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="总分">{{ scoreDetail.totalScore }}</el-descriptions-item>
          <el-descriptions-item label="排名">第 {{ scoreDetail.rankNo || '-' }} 名</el-descriptions-item>
          <el-descriptions-item label="当前视角重点" :span="2">{{ detailFocusText }}</el-descriptions-item>
          <el-descriptions-item label="摘要" :span="2">{{ scoreDetail.summaryText || '-' }}</el-descriptions-item>
        </el-descriptions>

        <div class="ygb-detail-block">
          <h3>办理提示</h3>
          <div class="ygb-tag-list">
            <el-tag v-for="item in resolvedDetailHintTags" :key="item.label" :type="item.type" effect="plain">{{ item.label }}</el-tag>
          </div>
        </div>

        <div class="ygb-detail-block">
          <h3>评分因子</h3>
          <el-table :data="factorRows" size="small">
            <el-table-column label="维度" prop="name" width="140" />
            <el-table-column label="得分" prop="score" width="90" />
            <el-table-column label="指标" prop="metricLabel" width="120" />
            <el-table-column label="指标值" prop="metricValue" width="100" />
            <el-table-column label="目标值" prop="targetValue" width="100" />
            <el-table-column label="说明" prop="description" min-width="260" show-overflow-tooltip />
          </el-table>
        </div>
      </template>
    </page-detail-dialog>
  </div>
</template>

<script setup name="YgbCreditScore">
import { computed, getCurrentInstance, ref, watchEffect } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import useUserStore from '@/store/modules/user'
import { useAuthorizedRegionOptions } from '@/utils/regionScope'
import { useRoleViewMode } from '@/utils/roleView'
import { applyWorkbenchRouteQuery, buildWorkbenchContext, stripWorkbenchRouteQuery } from '@/utils/workbenchLink'
import { useWorkbenchAssist } from '@/composables/useWorkbenchAssist'
import { decoratePortalExplanationItems, openPortalExplanationAction, resolvePortalExplanationSummary } from '@/utils/portalExplanation'
import {
  colorLabel,
  colorTagType,
  creditColorOptions as colorOptions,
  creditLevelOptions as levelOptions,
  creditScoreRegionNameMap as regionNameMap,
  creditScoreRegionOptions as allRegionOptions,
  formatDecimal,
  formatRegionName,
  levelTagType,
  useCreditScorePage,
  valueOrDefault
} from '@/views/creditScore/useCreditScorePage'

const { proxy } = getCurrentInstance()
const { setPageGuide } = useWorkbenchAssist()
const userStore = useUserStore()
const regionOptions = useAuthorizedRegionOptions(allRegionOptions)
const route = useRoute()
const router = useRouter()
const { isReadOnlyRole, readOnlyRoleLabel, readOnlyRoleDescription } = useRoleViewMode()
const activeFocusKey = ref('')
const creditScoreWorkbenchFields = ['enterpriseId', 'regionCode']

function hasPermissionPrefix(permissions, prefixes) {
  return Array.isArray(permissions) && permissions.some(permission => prefixes.some(prefix => permission.startsWith(prefix)))
}

function isFinanceView(roles, permissions) {
  if (roles.includes('ygb_hrss_supervisor') || roles.includes('ygb_enterprise_operator')) {
    return false
  }
  const financePrefixes = ['ygb:salaryBatch', 'ygb:salaryDetail', 'ygb:socialPayment', 'ygb:socialBaseCompare', 'ygb:taxCompare']
  return hasPermissionPrefix(permissions, financePrefixes)
}

const roleView = computed(() => {
  const roles = userStore.roles || []
  const permissions = userStore.permissions || []
  if (roles.includes('ygb_hrss_supervisor')) {
    return 'hrss'
  }
  if (roles.includes('ygb_enterprise_operator')) {
    return 'operator'
  }
  if (isFinanceView(roles, permissions)) {
    return 'finance'
  }
  if (roles.includes('ygb_enterprise_admin')) {
    return 'admin'
  }
  return 'default'
})

const canGenerate = computed(() => !isReadOnlyRole.value && hasPermissionPrefix(userStore.permissions || [], ['ygb:creditScore:generate']))

const {
  loading,
  showSearch,
  total,
  enterpriseOptions,
  creditScoreList,
  currentScore,
  scoreDetail,
  factorRows,
  detailOpen,
  generateOpen,
  summaryData,
  queryParams,
  generateForm,
  generateRules,
  getList,
  handleRowClick,
  handleQuery,
  openGenerateDialog,
  submitGenerate,
  handleExport,
  openDetail,
  syncCurrentScore
} = useCreditScorePage({
  exportFilePrefix: 'ygb_credit_score',
  canMutate: () => !isReadOnlyRole.value,
  onBlockedAction: actionLabel => {
    proxy?.$modal?.msgWarning?.(`${readOnlyRoleLabel.value}仅保留信用结果查看、详情和导出，不能${actionLabel}`)
  },
  getCurrentList: () => visibleScoreList.value,
  afterList: () => {
    syncActiveFocus()
  },
  immediate: false
})

function guardedOpenGenerateDialog(payload) {
  openGenerateDialog(payload)
}

function guardedSubmitGenerate() {
  submitGenerate()
}

const portalExplanations = computed(() => summaryData.value.ygbExplanation || [])
const portalExplanationItems = computed(() => decoratePortalExplanationItems(portalExplanations.value, {
  portalCode: 'ygb',
  panelTitle: '530.1 办理链解释',
  panelDescription: '信用评分页面的办理链说明、证据来源和主下钻入口统一来自 530.1 门户解释聚合。'
}))

function handlePortalExplanationAction(action) {
  openPortalExplanationAction(router, action)
}

function summaryCard(key, label, value, unit, note, cardClass = '') {
  return { key, label, value, unit, note, cardClass }
}

function focusQueue(key, title, count, unit, desc, actionText) {
  return { key, title, count, unit, desc, actionText }
}

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
  if (explanation?.actionText) {
    return explanation.actionText
  }
  if (explanation?.moduleLabel) {
    return `进入${explanation.moduleLabel}`
  }
  return fallback
}

function countRows(predicate) {
  return creditScoreList.value.filter(predicate).length
}

function matchScoreFocus(row, focusKey) {
  if (!row || !focusKey) {
    return false
  }
  if (focusKey === 'red') return row.colorCode === 'RED'
  if (focusKey === 'yellow') return row.colorCode === 'YELLOW'
  if (focusKey === 'd') return row.creditLevel === 'D'
  if (focusKey === 'lowSalary') return Number(row.salaryScore || 0) > 0 && Number(row.salaryScore || 0) < 80
  if (focusKey === 'lowSocialTax') return Number(row.socialTaxScore || 0) > 0 && Number(row.socialTaxScore || 0) < 80
  if (focusKey === 'lowGovernance') return Number(row.governanceScore || 0) > 0 && Number(row.governanceScore || 0) < 80
  if (focusKey === 'stable') return row.colorCode === 'GREEN' && ['A', 'B'].includes(row.creditLevel) && Number(row.totalScore || 0) >= 85
  return false
}

function prioritizeFocusRows(rows, predicate) {
  const matched = []
  const others = []
  rows.forEach(row => {
    if (predicate(row)) {
      matched.push(row)
    } else {
      others.push(row)
    }
  })
  return [...matched, ...others]
}

function syncActiveFocus() {
  if (!activeFocusKey.value || !focusQueues.value.some(item => item.key === activeFocusKey.value)) {
    activeFocusKey.value = focusQueues.value[0]?.key || ''
  }
}

const workbenchContext = computed(() => buildWorkbenchContext(route.query, {
  fields: creditScoreWorkbenchFields,
  sourceLabel: '企业工作台',
  title: '当前信用评分台账沿用了工作台来源条件',
  description: '当前列表已按企业或行政区划锁定，可以直接继续回查信用结果、短板维度和重评分。',
  fieldLabels: {
    enterpriseId: '企业',
    regionCode: '行政区划'
  },
  fieldFormatters: {
    enterpriseId: value => {
      const matched = enterpriseOptions.value.find(item => String(item.enterpriseId) === String(value))
      return matched ? matched.enterpriseName : value
    },
    regionCode: value => formatRegionName(value, value)
  }
}))

function handleSelectFocus(item) {
  activeFocusKey.value = item.key
  syncCurrentScore()
}

function buildScoreHintTags(score, focus, view) {
  if (!score) {
    if (focus?.title) {
      return [
        { label: `当前焦点：${focus.title}`, type: 'info' },
        { label: `建议动作：${focus.actionText}`, type: 'warning' }
      ]
    }
    return [{ label: '请选择企业查看评分承接建议', type: 'info' }]
  }

  const tags = []
  const totalScore = Number(score.totalScore || 0)
  const governanceScore = Number(score.governanceScore || 0)
  const salaryScore = Number(score.salaryScore || 0)
  const socialTaxScore = Number(score.socialTaxScore || 0)

  if (score.colorCode === 'RED') {
    tags.push({ label: '当前为红码企业，建议优先回查工资、社保税务和预警闭环链路', type: 'danger' })
  }
  if (score.creditLevel === 'D') {
    tags.push({ label: '信用等级 D，建议暂停直接归档，先补核查说明和整改记录', type: 'warning' })
  }
  if (score.colorCode === 'YELLOW') {
    tags.push({ label: '当前为黄码企业，建议补齐当月异常说明后再输出月报结论', type: 'warning' })
  }
  if (governanceScore > 0 && governanceScore < 80) {
    tags.push({ label: '闭环维度偏弱，建议优先复核预警处置闭环和工单办结情况', type: 'warning' })
  }
  if (salaryScore > 0 && salaryScore < 80) {
    tags.push({ label: '工资维度偏弱，建议复核批次发放、失败记录和到账时效', type: 'warning' })
  }
  if (socialTaxScore > 0 && socialTaxScore < 80) {
    tags.push({ label: '社保税务维度偏弱，建议联动检查社保个税比对异常', type: 'warning' })
  }
  if (view === 'finance') {
    tags.push({ label: '当前视角优先承接工资、社保和个税差异，再决定是否进入归档', type: 'info' })
  }
  if (view === 'hrss') {
    tags.push({ label: '当前视角优先承接区域异常、闭环短板和月度归档复核', type: 'info' })
  }
  if (['A', 'B'].includes(score.creditLevel) && score.colorCode === 'GREEN' && totalScore >= 85) {
    tags.push({ label: '评分稳定，可作为月报归档样本和企业规范办理参照', type: 'success' })
  }
  if (!tags.length) {
    tags.push({ label: '当前评分信息完整，可继续用于月报、监管联动和归档输出', type: 'success' })
  }
  return tags
}

const roleBadge = computed(() => {
  if (roleView.value === 'hrss') return '人社监管视角'
  if (roleView.value === 'operator') return '企业经办视角'
  if (roleView.value === 'finance') return '财务经办视角'
  if (roleView.value === 'admin') return '企业管理员视角'
  return '综合办理视角'
})

const roleTitle = computed(() => {
  if (roleView.value === 'hrss') return '区域信用复核与归档工作台'
  if (roleView.value === 'operator') return '企业信用结果回写与跟进台账'
  if (roleView.value === 'finance') return '信用评分回查与财务整改台账'
  if (roleView.value === 'admin') return '企业信用评分闭环工作台'
  return '企业信用评分办理台账'
})

const roleDescription = computed(() => {
  if (roleView.value === 'hrss') {
    return '面向人社监管经办统一查看月度信用评分结果，重点是把红黄码、低等级和闭环短板企业串回区域监管、预警闭环和月报归档链路。'
  }
  if (roleView.value === 'operator') {
    return '面向企业经办统一跟进评分结果，重点是先识别需要补说明、补数据和回写整改记录的企业，再承接后续重评分动作。'
  }
  if (roleView.value === 'finance') {
    return '面向财务经办统一查看月度信用评分结果，重点是把低分维度直接回落到工资、社保和个税整改链路，缩短对账和回查路径。'
  }
  if (roleView.value === 'admin') {
    return '面向企业管理员统一查看月度信用评分结果，重点是把红黄码、低等级和闭环短板企业直接串回企业办理、现场和监管闭环链路。'
  }
  return '面向企业管理员、财务和监管经办统一查看月度信用评分结果。'
})

const roleTip = computed(() => {
  if (roleView.value === 'hrss') {
    return '先锁定红码、D级和闭环维度偏弱企业，再决定是进入预警、扩面减损还是统计归档。'
  }
  if (roleView.value === 'operator') {
    return '优先处理需要补说明、补留痕和回写整改记录的对象，不让评分页停留在只读结果。'
  }
  if (roleView.value === 'finance') {
    return '优先回查工资、社保税务低分维度，减少财务在评分、工资和税社台账之间来回切换。'
  }
  if (roleView.value === 'admin') {
    return '优先统筹会阻断企业信用结果改善的关键对象，再决定是否进入重评分和月度归档。'
  }
  return '当前“生成评分”继续复用统一评分底座，不引入外部评分引擎。'
})

const summaryCards = computed(() => {
  if (roleView.value === 'finance') {
    return [
      summaryCard('average', '平均分', formatDecimal(summaryData.value.averageScore), '分', '用于判断当前月份财务相关企业信用整体水平。', 'ygb-summary-card--primary'),
      summaryCard('lowSalary', '工资偏弱', countRows(row => matchScoreFocus(row, 'lowSalary')), '家', '建议优先回查批次发放、失败原因和到账时效。', 'ygb-summary-card--warning'),
      summaryCard('lowSocialTax', '社税偏弱', countRows(row => matchScoreFocus(row, 'lowSocialTax')), '家', '适合作为社保欠费和个税差异整改入口。', 'ygb-summary-card--warning'),
      summaryCard('yellow', '黄码企业', valueOrDefault(summaryData.value.yellowCount, 0), '家', '适合先补异常说明和财务整改留痕。', 'ygb-summary-card--success')
    ]
  }
  if (roleView.value === 'hrss') {
    return [
      summaryCard('red', '红码企业', valueOrDefault(summaryData.value.redCount, 0), '家', '需要优先承接区域监管和预警闭环。', 'ygb-summary-card--warning'),
      summaryCard('d', 'D级企业', valueOrDefault(summaryData.value.dCount, 0), '家', '适合作为区域尾部企业复核入口。', 'ygb-summary-card--warning'),
      summaryCard('yellow', '黄码企业', valueOrDefault(summaryData.value.yellowCount, 0), '家', '便于提前承接闭环和说明核查。'),
      summaryCard('average', '平均分', formatDecimal(summaryData.value.averageScore), '分', '用于对照当前区域信用整体状态。', 'ygb-summary-card--primary')
    ]
  }
  if (roleView.value === 'operator') {
    return [
      summaryCard('total', '评分记录', valueOrDefault(summaryData.value.totalCount, total.value), '条', '当前筛选范围内的月度评分记录总量。'),
      summaryCard('yellow', '黄码企业', valueOrDefault(summaryData.value.yellowCount, 0), '家', '适合优先补录异常说明和回写记录。', 'ygb-summary-card--warning'),
      summaryCard('stable', '稳定样本', valueOrDefault(summaryData.value.highGradeCount, 0), '家', '可作为补录和回写后的对照样本。', 'ygb-summary-card--success'),
      summaryCard('average', '平均分', formatDecimal(summaryData.value.averageScore), '分', '用于快速判断当前结果回写后的稳定程度。', 'ygb-summary-card--primary')
    ]
  }
  if (roleView.value === 'admin') {
    return [
      summaryCard('total', '评分记录', valueOrDefault(summaryData.value.totalCount, total.value), '条', '当前筛选范围内的月度评分记录总量。'),
      summaryCard('red', '红码企业', valueOrDefault(summaryData.value.redCount, 0), '家', '建议优先回查工资、社保税务和预警闭环是否存在明显短板。', 'ygb-summary-card--warning'),
      summaryCard('d', 'D级企业', valueOrDefault(summaryData.value.dCount, 0), '家', '适合作为企业低分整改和核查说明入口。'),
      summaryCard('highGrade', 'A/B级企业', valueOrDefault(summaryData.value.highGradeCount, 0), '家', '可作为月报归档和企业规范样本的候选对象。', 'ygb-summary-card--success')
    ]
  }
  return [
    summaryCard('total', '评分记录', valueOrDefault(summaryData.value.totalCount, total.value), '条', '当前筛选范围内的月度评分记录总量。'),
    summaryCard('average', '平均分', formatDecimal(summaryData.value.averageScore), '分', '用于快速判断当前月份企业信用整体水平。', 'ygb-summary-card--primary'),
    summaryCard('red', '红码企业', valueOrDefault(summaryData.value.redCount, 0), '家', '建议优先回查工资、社保税务和预警闭环是否存在明显短板。', 'ygb-summary-card--warning'),
    summaryCard('highGrade', 'A/B级企业', valueOrDefault(summaryData.value.highGradeCount, 0), '家', '可作为月报归档和企业规范样本的候选对象。', 'ygb-summary-card--success')
  ]
})

const resolvedSummaryCards = computed(() => summaryCards.value.map((item, index) => ({
  ...item,
  note: resolveExplanationFirstText(item.note, resolvePortalExplanationItem(index))
})))

const focusQueues = computed(() => {
  if (roleView.value === 'finance') {
    return [
      focusQueue('lowSalary', '工资维度偏弱企业', countRows(row => matchScoreFocus(row, 'lowSalary')), '家', '先回查工资批次、失败明细和到账状态，再决定是否进入重评分。', '回看工资链路'),
      focusQueue('lowSocialTax', '社税维度偏弱企业', countRows(row => matchScoreFocus(row, 'lowSocialTax')), '家', '优先承接社保欠费、社保基数和个税差异整改对象。', '回看社税链路'),
      focusQueue('yellow', '黄码企业', valueOrDefault(summaryData.value.yellowCount, 0), '家', '适合先补异常说明和财务整改留痕后再观察分层变化。', '补异常说明')
    ]
  }
  if (roleView.value === 'hrss') {
    return [
      focusQueue('red', '红码企业', valueOrDefault(summaryData.value.redCount, 0), '家', '优先承接最需要区域监管复核的高风险企业。', '进入区域复核'),
      focusQueue('d', 'D级企业', valueOrDefault(summaryData.value.dCount, 0), '家', '适合逐项复核低分维度和监管说明。', '查看尾部企业'),
      focusQueue('lowGovernance', '闭环维度偏弱企业', countRows(row => matchScoreFocus(row, 'lowGovernance')), '家', '先看预警处置闭环和工单办结情况，再决定是否进入月报归档。', '回看闭环链路')
    ]
  }
  if (roleView.value === 'operator') {
    return [
      focusQueue('yellow', '黄码企业', valueOrDefault(summaryData.value.yellowCount, 0), '家', '优先补充异常说明、整改记录和结果回写。', '补回写记录'),
      focusQueue('d', 'D级企业', valueOrDefault(summaryData.value.dCount, 0), '家', '适合先补台账说明，再协助管理员进入重评分。', '补核查说明'),
      focusQueue('stable', '稳定样本企业', countRows(row => matchScoreFocus(row, 'stable')), '家', '可作为补录完成后的对照结果，便于确认回写动作是否生效。', '查看对照样本')
    ]
  }
  if (roleView.value === 'admin') {
    return [
      focusQueue('d', 'D级企业', valueOrDefault(summaryData.value.dCount, 0), '家', '先锁定真正拖低企业信用结果的尾部对象。', '进入整改链路'),
      focusQueue('red', '红码企业', valueOrDefault(summaryData.value.redCount, 0), '家', '优先统筹工资、社税和预警主链的关键风险对象。', '回看高风险对象'),
      focusQueue('lowGovernance', '闭环维度偏弱企业', countRows(row => matchScoreFocus(row, 'lowGovernance')), '家', '适合优先核对预警处置闭环和现场留痕是否完整。', '复核闭环短板')
    ]
  }
  return [
    focusQueue('red', '红码企业', valueOrDefault(summaryData.value.redCount, 0), '家', '优先回查最影响信用结果的高风险对象。', '回查高风险对象'),
    focusQueue('d', 'D级企业', valueOrDefault(summaryData.value.dCount, 0), '家', '适合作为低分维度和整改说明核查入口。', '查看低分对象'),
    focusQueue('yellow', '黄码企业', valueOrDefault(summaryData.value.yellowCount, 0), '家', '提前处理边缘对象，避免继续下滑到红码或 D 级。', '补说明留痕')
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

const activeFocus = computed(() => {
  if (!focusQueues.value.length) return null
  return focusQueues.value.find(item => item.key === activeFocusKey.value) || focusQueues.value[0]
})

const visibleScoreList = computed(() => prioritizeFocusRows(creditScoreList.value, row => matchScoreFocus(row, activeFocus.value?.key)))

const focusTableHint = computed(() => {
  if (!activeFocus.value) {
    return '按当前筛选条件展示信用评分台账。'
  }
  return `当前焦点为“${activeFocus.value.title}”，已把对应重点对象优先排到表格前列。`
})

const selectedScoreOverview = computed(() => {
  if (!currentScore.value) {
    return [
      { label: '当前焦点', value: activeFocus.value?.title || '-' },
      { label: '焦点数量', value: `${activeFocus.value?.count ?? 0}${activeFocus.value?.unit || ''}` },
      { label: '当前月份', value: queryParams.value.statMonth || '全部月份' },
      { label: '当前区域', value: formatRegionName(queryParams.value.regionCode) }
    ]
  }
  return [
    { label: '企业名称', value: currentScore.value.enterpriseName || '-' },
    { label: '区域', value: currentScore.value.regionName || regionNameMap[currentScore.value.regionCode] || currentScore.value.regionCode || '-' },
    { label: '总分', value: `${formatDecimal(currentScore.value.totalScore)} 分` },
    { label: '等级/色码', value: `${currentScore.value.creditLevel || '-'} / ${colorLabel(currentScore.value.colorCode)}` }
  ]
})

const primaryScoreAction = computed(() => {
  if (!currentScore.value) {
    return { label: activeFocus.value?.actionText || '查看详情', action: 'detail' }
  }
  const risky = currentScore.value.colorCode === 'RED'
    || currentScore.value.colorCode === 'YELLOW'
    || currentScore.value.creditLevel === 'D'
    || Number(currentScore.value.salaryScore || 0) < 80
    || Number(currentScore.value.socialTaxScore || 0) < 80
    || Number(currentScore.value.governanceScore || 0) < 80

  if (canGenerate.value && risky) {
    return { label: '按当前企业重生成', action: 'generate' }
  }
  return { label: '查看详情', action: 'detail' }
})

const currentScoreActionSummary = computed(() => {
  const portalSummary = resolvePortalExplanationSummary(portalExplanationItems.value, '')
  if (portalSummary) {
    return portalSummary
  }
  if (!currentScore.value) {
    return activeFocus.value
      ? `当前已按“${activeFocus.value.title}”重排台账，建议优先处理表格前列企业，再决定是否进入重评分或月报归档。`
      : '当前暂无评分对象。'
  }

  if (roleView.value === 'finance') {
    if (matchScoreFocus(currentScore.value, 'lowSalary')) {
      return '该企业工资维度偏弱，建议优先回查工资批次、失败明细和到账状态，再决定是否重生成评分。'
    }
    if (matchScoreFocus(currentScore.value, 'lowSocialTax')) {
      return '该企业社保税务维度偏弱，建议先完成社保、个税和基数差异整改留痕，再回到信用评分复核。'
    }
  }

  if (roleView.value === 'hrss') {
    if (currentScore.value.colorCode === 'RED' || currentScore.value.creditLevel === 'D') {
      return '该企业已进入区域监管重点范围，建议先复核低分维度和闭环短板，再决定是否进入预警或月报归档链路。'
    }
    return '该企业当前风险可控，建议先查看详情确认维度得分，再决定是否继续承接区域监管动作。'
  }

  if (roleView.value === 'operator') {
    if (currentScore.value.colorCode === 'YELLOW' || currentScore.value.creditLevel === 'D') {
      return '该企业当前更适合先补异常说明、整改记录和结果回写，再交由管理员或财务执行重评分。'
    }
    return '该企业当前评分结果较稳定，建议先查看详情确认是否还需要补充说明或归档材料。'
  }

  if (currentScore.value.colorCode === 'RED' || currentScore.value.creditLevel === 'D') {
    return '该企业仍处于红码或 D 级风险，建议先锁定低分维度回查主链路，再决定是否立即重生成评分。'
  }
  if (currentScore.value.colorCode === 'YELLOW') {
    return '该企业仍处于黄码边缘，建议在完成整改留痕后优先重生成评分，确认是否已回到稳定分层。'
  }
  return '该企业当前分层相对稳定，建议先查看详情确认维度得分和归档链路是否完整。'
})

const currentScoreActionTags = computed(() => buildScoreHintTags(currentScore.value, activeFocus.value, roleView.value))
const resolvedCurrentScoreActionTags = computed(() => {
  const tags = [...currentScoreActionTags.value]
  const explanation = leadingPortalExplanation.value
  const label = buildPortalExplanationLabel(explanation)
  if (label) {
    tags.unshift({ label: `530.1主解释：${label}`, type: 'info' })
  } else if (explanation?.summary || explanation?.explanationSummary) {
    tags.unshift({ label: explanation.summary || explanation.explanationSummary, type: 'info' })
  }
  return tags.slice(0, 4)
})

const workflowSteps = computed(() => {
  if (roleView.value === 'finance') {
    return [
      { label: '先看工资短板', desc: '优先锁定工资维度偏弱、黄码和尾部企业，快速判断财务链路卡点。' },
      { label: '再承接社税整改', desc: '把社保欠费、社保基数和个税差异对象接回财务整改台账，避免评分结果停留在汇总层。' },
      { label: '最后回到重评分与归档', desc: '在整改留痕补齐后重生成评分，并把结果沉淀到月报和信用档案。' }
    ]
  }
  if (roleView.value === 'hrss') {
    return [
      { label: '先锁定区域尾部企业', desc: '先看红码、D级和闭环维度偏弱企业，快速判断当前区域监管压力。' },
      { label: '再回看闭环链路', desc: '围绕预警闭环、整改留痕和说明材料判断是实际风险还是数据缺口。' },
      { label: '最后完成归档复核', desc: '把信用结果回落到月报、区域统计和企业监管底稿。' }
    ]
  }
  if (roleView.value === 'operator') {
    return [
      { label: '先补异常说明', desc: '先把黄码、低等级对象对应的说明、截图和整改记录补齐。' },
      { label: '再回写业务留痕', desc: '把回查结论回写到企业台账，避免评分与原业务链路脱节。' },
      { label: '最后配合重评分', desc: '在留痕和说明补齐后，再配合管理员或财务执行重评分。' }
    ]
  }
  if (roleView.value === 'admin') {
    return [
      { label: '先判断关键卡点', desc: '先通过红黄码、D级和闭环短板企业判断卡点在工资、社税、预警还是现场闭环。' },
      { label: '承接企业整改主链', desc: '优先回到合同、考勤、工资、社保税务和预警主链处理关键短板。' },
      { label: '执行重评分复核', desc: '在整改留痕补齐后重生成评分，判断企业信用结果是否真正改善。' },
      { label: '完成月度归档', desc: '把评分结果纳入月报、企业办理底稿和后续监管联动。' }
    ]
  }
  return [
    { label: '汇聚底数', desc: '先汇聚当月合同、考勤、工资、社保税务、工伤预防和预警闭环底数，确保评分口径与办理链路一致。' },
    { label: '生成评分', desc: '按统一评分模型生成等级、色码和摘要，形成企业月度信用结果。' },
    { label: '核查异常', desc: '对红黄码、低等级和低分维度企业回查原始业务数据，确认是实际风险还是数据缺口。' },
    { label: '归档联动', desc: '将评分结果用于月报、企业核查和联动监管，并沉淀为可复核的信用归档台账。' }
  ]
})

const resolvedWorkflowSteps = computed(() => {
  const aggregateSteps = portalExplanationItems.value
    .filter(item => item?.summary || item?.explanationSummary || item?.sourceDescription)
    .slice(0, 3)
    .map(item => ({
      label: item.dimensionName || item.moduleLabel || item.moduleCode || '530.1 Explanation',
      desc: item.summary || item.explanationSummary || item.sourceDescription || ''
    }))

  if (aggregateSteps.length) {
    return aggregateSteps
  }
  return workflowSteps.value
})

const scoreHintTags = computed(() => buildScoreHintTags(currentScore.value, activeFocus.value, roleView.value))
const detailHintTags = computed(() => buildScoreHintTags(scoreDetail.value || currentScore.value, activeFocus.value, roleView.value))
const resolvedScoreHintTags = computed(() => {
  const tags = [...scoreHintTags.value]
  const explanation = leadingPortalExplanation.value
  const label = buildPortalExplanationLabel(explanation)
  if (label) {
    tags.unshift({ label: `530.1主解释：${label}`, type: 'info' })
  } else if (explanation?.summary || explanation?.explanationSummary) {
    tags.unshift({ label: explanation.summary || explanation.explanationSummary, type: 'info' })
  }
  return tags.slice(0, 4)
})
watchEffect(() => {
  setPageGuide({
    title: roleTitle.value || '信用评价办理台账',
    description: roleDescription.value || '统一查看当前页的门户解释、焦点对象、当前选中、办理路径与办理提示。',
    portalExplanation: portalExplanationItems.value,
    focus: resolvedFocusQueues.value,
    selection: [
      ...selectedScoreOverview.value,
      { label: '当前办理建议', value: currentScoreActionSummary.value }
    ],
    workflow: resolvedWorkflowSteps.value,
    hints: [...resolvedCurrentScoreActionTags.value, ...resolvedScoreHintTags.value].slice(0, 6)
  })
})
const resolvedDetailHintTags = computed(() => {
  const tags = [...detailHintTags.value]
  const explanation = leadingPortalExplanation.value
  const label = buildPortalExplanationLabel(explanation)
  if (label) {
    tags.unshift({ label: `530.1主解释：${label}`, type: 'info' })
  } else if (explanation?.summary || explanation?.explanationSummary) {
    tags.unshift({ label: explanation.summary || explanation.explanationSummary, type: 'info' })
  }
  return tags.slice(0, 4)
})

const detailFocusText = computed(() => {
  if (roleView.value === 'finance') {
    return '重点看工资、社保税务和闭环维度是否拖低了企业信用结果，再回到财务整改链路处理。'
  }
  if (roleView.value === 'hrss') {
    return '重点看红黄码、低等级和闭环短板是否集中，再决定是否进入区域监管、预警闭环和统计归档。'
  }
  if (roleView.value === 'operator') {
    return '重点看需要补说明、补留痕和回写结果的对象，避免评分结果与企业台账脱节。'
  }
  if (roleView.value === 'admin') {
    return '重点看哪些维度拖低了企业综合得分，再把整改动作回落到工资、社税、预警和现场闭环台账。'
  }
  return '重点看低分维度、红黄绿码和摘要是否需要继续回查业务主链。'
})

function handlePrimaryScoreAction() {
  if (!currentScore.value) {
    return
  }
  if (primaryScoreAction.value.action === 'generate') {
    guardedOpenGenerateDialog(currentScore.value)
    return
  }
  openDetail(currentScore.value)
}

const readOnlyAlertTitle = computed(() => `${readOnlyRoleLabel.value}仅保留信用结果查看、详情和导出`)
const readOnlyAlertDescription = computed(() => `${readOnlyRoleDescription.value || ''} 当前页面仍会展示重点队列和评分解释，但不开放生成类动作。`.trim())

function resetQuery() {
  Object.assign(queryParams.value, {
    pageNum: 1,
    pageSize: 10,
    statMonth: undefined,
    enterpriseId: undefined,
    regionCode: undefined,
    creditLevel: undefined,
    colorCode: undefined
  })
  applyWorkbenchRouteQuery(route.query, queryParams.value, creditScoreWorkbenchFields)
  getList()
}

function clearWorkbenchContext() {
  Object.assign(queryParams.value, {
    pageNum: 1,
    enterpriseId: undefined,
    regionCode: undefined
  })
  router.replace({
    path: route.path,
    query: stripWorkbenchRouteQuery(route.query, creditScoreWorkbenchFields)
  })
  getList()
}

applyWorkbenchRouteQuery(route.query, queryParams.value, creditScoreWorkbenchFields)
getList()
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
  color: #4f6478;
  line-height: 1.6;
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

.ygb-card-head--between {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: flex-start;
}

.ygb-focus-list--single,
.ygb-pipeline-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.ygb-source-list {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.ygb-focus-queue {
  width: 100%;
  display: flex;
  justify-content: space-between;
  gap: 16px;
  padding: 14px 16px;
  border: 1px solid #dde7f1;
  border-radius: 14px;
  background: #f8fbfd;
  text-align: left;
  cursor: pointer;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.ygb-focus-queue:hover,
.ygb-focus-queue.is-active {
  border-color: #0f5ea8;
  box-shadow: 0 10px 24px rgba(15, 94, 168, 0.08);
}

.ygb-focus-queue__main strong {
  display: block;
  margin-bottom: 6px;
  color: #13243a;
}

.ygb-focus-queue__main p {
  margin: 0;
  font-size: 13px;
  line-height: 1.7;
  color: #5f6f80;
}

.ygb-focus-queue__side {
  min-width: 120px;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  justify-content: center;
  gap: 8px;
}

.ygb-focus-queue__count {
  font-size: 20px;
  font-weight: 700;
  color: #0f5ea8;
}

.ygb-focus-queue__action {
  font-size: 12px;
  color: #1d4ed8;
}

.ygb-source-item,
.ygb-pipeline-item {
  border: 1px solid #dde7f1;
  border-radius: 14px;
  background: #f8fbfd;
}

.ygb-source-item {
  padding: 14px 16px;
}

.ygb-source-item__label {
  font-size: 12px;
  color: #7b8da1;
}

.ygb-source-item__value {
  margin-top: 8px;
  color: #13243a;
  font-size: 16px;
  font-weight: 600;
}

.ygb-recommend-panel {
  margin-top: 16px;
  padding: 16px;
  border-radius: 14px;
  background: linear-gradient(135deg, rgba(15, 94, 168, 0.08), rgba(29, 78, 216, 0.05));
  border: 1px solid rgba(15, 94, 168, 0.15);
}

.ygb-recommend-panel__title {
  color: #0f5ea8;
  font-size: 13px;
  font-weight: 600;
}

.ygb-recommend-panel__summary {
  margin: 8px 0 12px;
  color: #1f2937;
  font-size: 14px;
  line-height: 1.7;
}

.ygb-focus-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 16px;
}

.ygb-pipeline-item {
  display: flex;
  gap: 14px;
  align-items: flex-start;
  padding: 16px;
}

.ygb-pipeline-item__index {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: #0f5ea8;
  color: #fff;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  font-weight: 700;
}

.ygb-pipeline-item__body strong {
  display: block;
  color: #13243a;
}

.ygb-pipeline-item__body p {
  margin: 6px 0 0;
  color: #5f6f80;
  font-size: 13px;
  line-height: 1.7;
}

.ygb-tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
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
  .ygb-source-list {
    grid-template-columns: 1fr;
  }

  .ygb-card-head--between {
    flex-direction: column;
  }
}
</style>

