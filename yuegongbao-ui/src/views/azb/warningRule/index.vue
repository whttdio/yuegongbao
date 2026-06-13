<template>
  <div class="app-container azb-page">
    <section class="gov-page-header azb-page__header">
      <div>
        <p class="azb-page__eyebrow">预警规则治理</p>
        <h1 class="azb-page__title">规则强度、处置时效与模块来源配置</h1>
        <p class="azb-page__desc">
          面向应急监管、保险协同和平台运营统一维护预警等级、来源模块、推送目标和超时升级参数，
          页面按“先看规则结构，再锁定当前规则，再做维护调整”的安责保视角重组。
        </p>
      </div>
      <div class="azb-page__tip">
        <div class="azb-page__tip-item">优先关注：红警规则、设备/工伤来源规则、已停用但仍被链路依赖的规则。</div>
        <div class="azb-page__tip-item">规则覆盖设备、现场隐患、高危作业、参保风险等统一预警入口。</div>
        <div class="azb-page__tip-item">当前规则表达式仍为结构化说明文本，后续可继续扩展更细粒度的条件编排。</div>
      </div>
    </section>

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
      <el-form v-show="showSearch" ref="queryRef" :model="queryParams" :inline="true">
        <el-form-item label="规则名称">
          <el-input v-model="queryParams.ruleName" clearable style="width: 220px" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="来源模块">
          <el-select v-model="queryParams.sourceModule" clearable style="width: 180px">
            <el-option v-for="item in sourceModuleOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="规则状态">
          <el-select v-model="queryParams.ruleStatus" clearable style="width: 160px">
            <el-option v-for="item in ruleStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
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
          <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['ygb:warningRule:add']">新增</el-button>
        </el-col>
        <el-col v-if="!isReadOnlyRole" :span="1.5">
          <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['ygb:warningRule:edit']">修改</el-button>
        </el-col>
        <el-col v-if="!isReadOnlyRole" :span="1.5">
          <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['ygb:warningRule:remove']">停用</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['ygb:warningRule:export']">导出</el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>
    </el-card>

    <el-card class="table-card azb-table-card" shadow="never">
      <template #header>
        <div class="azb-card-head azb-card-head--between">
          <div>
            <div class="azb-card-head__title">预警规则治理台账</div>
            <div class="azb-card-head__desc">{{ focusTableHint }}</div>
          </div>
          <div class="azb-card-head__desc">当前总量 {{ total }} 条</div>
        </div>
      </template>
      <el-table v-loading="loading" :data="visibleWarningRuleList" @selection-change="handleSelectionChange" @row-click="handleRowClick">
        <el-table-column v-if="!isReadOnlyRole" type="selection" width="55" align="center" />
        <el-table-column label="ID" prop="ruleId" width="90" />
        <el-table-column label="规则名称" prop="ruleName" min-width="200" />
        <el-table-column label="预警级别" width="100">
          <template #default="scope">
            <dict-tag :options="warnLevelOptions" :value="scope.row.warnLevel" />
          </template>
        </el-table-column>
        <el-table-column label="来源模块" width="120">
          <template #default="scope">
            <dict-tag :options="sourceModuleOptions" :value="scope.row.sourceModule" />
          </template>
        </el-table-column>
        <el-table-column label="规则表达式" prop="conditionText" min-width="260" show-overflow-tooltip />
        <el-table-column label="推送目标" prop="pushTargets" width="160" show-overflow-tooltip />
        <el-table-column label="超时分钟" prop="timeoutMinutes" width="110" />
        <el-table-column label="升级级别" width="100">
          <template #default="scope">
            <dict-tag :options="warnLevelOptions" :value="scope.row.upgradeLevel" />
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="scope">
            <dict-tag :options="ruleStatusOptions" :value="scope.row.ruleStatus" />
          </template>
        </el-table-column>
        <el-table-column class-name="table-fill-column" min-width="1" />

        <el-table-column label="操作" fixed="right" align="center" :width="isReadOnlyRole ? 90 : 220" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-button link type="info" icon="View" @click.stop="openDetail(scope.row)">详情</el-button>
            <template v-if="!isReadOnlyRole">
              <el-button link type="primary" icon="Edit" @click.stop="handleUpdate(scope.row)" v-hasPermi="['ygb:warningRule:edit']">修改</el-button>
              <el-button link type="danger" icon="Delete" @click.stop="handleDelete(scope.row)" v-hasPermi="['ygb:warningRule:remove']">停用</el-button>
            </template>
          </template>
        </el-table-column>
      </el-table>
      <pagination v-show="total > 0" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" :total="total" @pagination="getList" />
    </el-card>

    <el-dialog v-model="open" :title="title" width="860px" append-to-body>
      <el-form ref="ruleRef" :model="form" :rules="rules" label-width="110px">
        <div class="azb-rule-form-grid">
          <el-form-item label="规则名称" prop="ruleName">
            <el-input v-model="form.ruleName" placeholder="请输入规则名称" />
          </el-form-item>
          <el-form-item label="预警级别" prop="warnLevel">
            <el-select v-model="form.warnLevel" placeholder="请选择预警级别">
              <el-option v-for="item in warnLevelOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="来源模块" prop="sourceModule">
            <el-select v-model="form.sourceModule" placeholder="请选择来源模块">
              <el-option v-for="item in sourceModuleOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="规则状态" prop="ruleStatus">
            <el-select v-model="form.ruleStatus" placeholder="请选择规则状态">
              <el-option v-for="item in ruleStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="超时分钟" prop="timeoutMinutes">
            <el-input-number v-model="form.timeoutMinutes" :min="1" :step="10" style="width: 100%" />
          </el-form-item>
          <el-form-item label="升级级别" prop="upgradeLevel">
            <el-select v-model="form.upgradeLevel" placeholder="请选择升级级别">
              <el-option v-for="item in warnLevelOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
        </div>
        <el-form-item label="规则表达式" prop="conditionText">
          <el-input v-model="form.conditionText" type="textarea" :rows="3" placeholder="请输入规则表达式或说明文本" />
        </el-form-item>
        <el-form-item label="推送目标" prop="pushTargets">
          <el-input v-model="form.pushTargets" placeholder="例如：监管员,企业管理员,财务经办" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入规则备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确定</el-button>
          <el-button @click="cancel">取消</el-button>
        </div>
      </template>
    </el-dialog>

    <page-detail-dialog v-model="detailOpen" title="预警规则详情" width="760px">
      <template v-if="detailRule">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="规则名称">{{ detailRule.ruleName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="预警级别">{{ optionLabel(warnLevelOptions, detailRule.warnLevel) }}</el-descriptions-item>
          <el-descriptions-item label="来源模块">{{ optionLabel(sourceModuleOptions, detailRule.sourceModule) }}</el-descriptions-item>
          <el-descriptions-item label="规则状态">{{ optionLabel(ruleStatusOptions, detailRule.ruleStatus) }}</el-descriptions-item>
          <el-descriptions-item label="超时分钟">{{ detailRule.timeoutMinutes || '-' }}</el-descriptions-item>
          <el-descriptions-item label="升级级别">{{ optionLabel(warnLevelOptions, detailRule.upgradeLevel) }}</el-descriptions-item>
          <el-descriptions-item label="推送目标" :span="2">{{ detailRule.pushTargets || '-' }}</el-descriptions-item>
          <el-descriptions-item label="规则表达式" :span="2">{{ detailRule.conditionText || '-' }}</el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ detailRule.remark || '-' }}</el-descriptions-item>
        </el-descriptions>

        <div class="azb-detail-block">
          <h3>安责保侧提示</h3>
          <div class="azb-tag-list">
            <el-tag v-for="item in resolvedDetailTags" :key="item.label" :type="item.type" effect="plain">{{ item.label }}</el-tag>
          </div>
        </div>
      </template>
    </page-detail-dialog>
  </div>
</template>

<script setup name="AzbWarningRule">
import { computed, getCurrentInstance, ref, watch, watchEffect } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useWorkbenchAssist } from '@/composables/useWorkbenchAssist'
import { decoratePortalExplanationItems, openPortalExplanationAction, resolvePortalExplanationSummary } from '@/utils/portalExplanation'
import { useRoleViewMode } from '@/utils/roleView'
import { applyWorkbenchRouteQuery, buildWorkbenchContext, stripWorkbenchRouteQuery } from '@/utils/workbenchLink'
import {
  formatRatio,
  optionLabel,
  ruleStatusOptions,
  sourceModuleOptions,
  useWarningRulePage,
  valueOrDefault,
  warnLevelOptions
} from '@/views/warningRule/useWarningRulePage'

const { proxy } = getCurrentInstance()
const route = useRoute()
const { setPageGuide } = useWorkbenchAssist()
const router = useRouter()
const { isReadOnlyRole, isBankRole, isInsurerRole, readOnlyRoleLabel, readOnlyRoleDescription } = useRoleViewMode()
const activeFocusKey = ref('')
const warningRuleWorkbenchFields = ['sourceModule', 'focusKey']
const warningRuleInitialQuery = {}

applyWorkbenchRouteQuery(route.query, warningRuleInitialQuery, warningRuleWorkbenchFields)

const roleView = computed(() => {
  if (isBankRole.value) {
    return 'bank'
  }
  if (isInsurerRole.value) {
    return 'insurer'
  }
  return 'emergency'
})

function buildWarningRuleExplanationQuery(extraQuery = {}) {
  return {
    sourceModule: queryParams.value.sourceModule,
    ruleStatus: queryParams.value.ruleStatus,
    ...extraQuery
  }
}

function blockReadOnlyAction(actionLabel) {
  proxy.$modal.msgWarning(`${readOnlyRoleLabel.value}仅保留摘要、详情和导出，不能${actionLabel}`)
}

const {
  warningRuleList,
  open,
  detailOpen,
  loading,
  showSearch,
  single,
  multiple,
  total,
  title,
  currentRule,
  detailRule,
  summaryData,
  form,
  queryParams,
  rules,
  getList,
  init,
  cancel,
  handleQuery,
  resetQuery: pageResetQuery,
  handleSelectionChange,
  handleRowClick,
  handleAdd,
  handleUpdate,
  openDetail,
  submitForm,
  handleDelete,
  handleExport
} = useWarningRulePage({
  exportFilePrefix: 'azb_warning_rule',
  canMutate: () => !isReadOnlyRole.value,
  onBlockedAction: blockReadOnlyAction,
  immediate: false
})

Object.assign(queryParams.value, warningRuleInitialQuery)

const summaryCards = computed(() => ([
  {
    key: 'total',
    label: '规则数量',
    value: valueOrDefault(summaryData.value.totalCount, total.value),
    unit: '条',
    note: '当前筛选条件下的预警规则总量。',
    cardClass: ''
  },
  {
    key: 'enabled',
    label: '启用规则',
    value: valueOrDefault(summaryData.value.enabledCount, 0),
    unit: '条',
    note: '当前仍直接参与安责保预警链路的规则数量。',
    cardClass: 'azb-summary-card--success'
  },
  {
    key: 'red',
    label: '红警规则',
    value: valueOrDefault(summaryData.value.redCount, 0),
    unit: '条',
    note: '最高告警级别规则数量，决定应急督办的触发强度。',
    cardClass: 'azb-summary-card--danger'
  },
  {
    key: 'device',
    label: '设备/工伤来源',
    value: valueOrDefault(summaryData.value.deviceInjuryCount, 0),
    unit: '条',
    note: '面向现场治理链路的核心规则数量。',
    cardClass: 'azb-summary-card--warning'
  }
]))

const resolvedSummaryCards = computed(() => summaryCards.value.map((item, index) => ({
  ...item,
  note: resolveExplanationFirstText(item.note, resolvePortalExplanationItem(index))
})))

const focusQueues = computed(() => {
  if (roleView.value === 'bank') {
    return [
      {
        key: 'expansion',
        title: '参保风险来源规则',
        desc: '银行协同优先确认参保风险规则是否会影响区域筛查、协同报表和对象分层口径。',
        count: valueOrDefault(summaryData.value.expansionCount, countBy(rule => rule.sourceModule === 'EXPANSION')),
        unit: '条',
        actionText: '先看风险口径'
      },
      {
        key: 'inactive',
        title: '停用规则',
        desc: '需要确认停用规则是否仍被历史链路、报表或协同模块依赖。',
        count: countBy(rule => rule.ruleStatus === '0'),
        unit: '条',
        actionText: '核对停用影响'
      },
      {
        key: 'missingTargets',
        title: '推送目标缺失',
        desc: '推送目标缺失会让联动链路只剩规则、不落对象，优先回看配置完整性。',
        count: countBy(rule => !rule.pushTargets),
        unit: '条',
        actionText: '补齐协同对象'
      },
      {
        key: 'highTimeout',
        title: '超时偏长规则',
        desc: '超时过长的规则会让处置链路滞后，需确认是否仍符合当前治理时效。',
        count: countBy(rule => Number(rule.timeoutMinutes || 0) >= 180),
        unit: '条',
        actionText: '复核时效设置'
      }
    ]
  }

  if (roleView.value === 'insurer') {
    return [
      {
        key: 'deviceInjury',
        title: '设备/工伤来源规则',
        desc: '保险协同先看设备和工伤来源规则，确认风险事件如何进入后续协同链路。',
        count: valueOrDefault(summaryData.value.deviceInjuryCount, countBy(rule => ['DEVICE', 'INJURY'].includes(rule.sourceModule))),
        unit: '条',
        actionText: '先看核心触发链'
      },
      {
        key: 'red',
        title: '红警规则',
        desc: '红警规则决定高风险事件的前置触发强度，需优先确认是否过严或过松。',
        count: valueOrDefault(summaryData.value.redCount, countBy(rule => rule.warnLevel === '3')),
        unit: '条',
        actionText: '复核红警强度'
      },
      {
        key: 'missingTargets',
        title: '推送目标缺失',
        desc: '协同对象缺失会导致预警无法真正落到保险侧承接人。',
        count: countBy(rule => !rule.pushTargets),
        unit: '条',
        actionText: '补齐协同触达'
      },
      {
        key: 'inactive',
        title: '停用规则',
        desc: '停用规则需要确认是否仍被事故预防、高危作业或安责险链路依赖。',
        count: countBy(rule => rule.ruleStatus === '0'),
        unit: '条',
        actionText: '回看历史依赖'
      }
    ]
  }

  return [
    {
      key: 'red',
      title: '红警规则',
      desc: '应急监管先看红警规则，确保高风险事件能被优先触发、催办和升级。',
      count: valueOrDefault(summaryData.value.redCount, countBy(rule => rule.warnLevel === '3')),
      unit: '条',
      actionText: '优先复核强度'
    },
    {
      key: 'deviceInjury',
      title: '设备/工伤来源规则',
      desc: '现场治理链条先依赖设备和工伤规则，需确认来源模块和升级链路是否正确。',
      count: valueOrDefault(summaryData.value.deviceInjuryCount, countBy(rule => ['DEVICE', 'INJURY'].includes(rule.sourceModule))),
      unit: '条',
      actionText: '先排现场规则'
    },
    {
      key: 'inactive',
      title: '停用规则',
      desc: '停用规则可能造成治理断点，需要优先确认是否存在残留依赖。',
      count: countBy(rule => rule.ruleStatus === '0'),
      unit: '条',
      actionText: '回看停用影响'
    },
    {
      key: 'fastTimeout',
      title: '高时效规则',
      desc: '时效过短的规则会直接增加催办压力，需确认是否符合现场实际。',
      count: countBy(rule => Number(rule.timeoutMinutes || 0) > 0 && Number(rule.timeoutMinutes || 0) <= 60),
      unit: '条',
      actionText: '复核时效口径'
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

const fallbackPortalExplanations = computed(() => focusQueues.value.map(item => ({
  key: item.key,
  dimensionName: item.title,
  currentValue: item.count,
  targetValue: '-',
  summary: item.desc,
  evidenceModule: 'warningRule',
  recommendModule: 'warningRule',
  defaultQuery: buildWarningRuleExplanationQuery({ focusKey: item.key }),
  sourceLabel: '6.1 预警规则治理解释',
  sourceDescription: item.desc,
  actionText: item.actionText
})))

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
  panelTitle: '6.1 预警规则治理解释',
  panelDescription: '规则解释统一按当前安责保门户来源条件输出。'
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
  return explanation.actionLabel || explanation.actionText || explanation.targetLabel || explanation.moduleLabel || fallback
}

const activeFocus = computed(() => focusQueues.value.find(item => item.key === activeFocusKey.value) || focusQueues.value[0] || null)
const visibleWarningRuleList = computed(() => sortWarningRuleList(warningRuleList.value, activeFocus.value?.key))

watch([warningRuleList, activeFocusKey], () => {
  currentRule.value = visibleWarningRuleList.value[0]
}, { immediate: true })

const selectedOverview = computed(() => {
  if (!currentRule.value) {
    return [
      { label: '来源模块', value: '-' },
      { label: '预警级别', value: '-' },
      { label: '规则状态', value: '-' },
      { label: '超时/升级', value: '-' },
      { label: '推送目标', value: '-' }
    ]
  }
  return [
    { label: '来源模块', value: optionLabel(sourceModuleOptions, currentRule.value.sourceModule) },
    { label: '预警级别', value: optionLabel(warnLevelOptions, currentRule.value.warnLevel) },
    { label: '规则状态', value: optionLabel(ruleStatusOptions, currentRule.value.ruleStatus) },
    {
      label: '超时/升级',
      value: `${valueOrDefault(currentRule.value.timeoutMinutes, '-')} / ${optionLabel(warnLevelOptions, currentRule.value.upgradeLevel)}`
    },
    { label: '推送目标', value: currentRule.value.pushTargets || '-' }
  ]
})

const workflowSteps = computed(() => {
  if (roleView.value === 'bank') {
    return [
      { label: '锁定参保风险与停用规则', desc: '先看参保风险来源、停用规则和推送对象缺失规则。' },
      { label: '确认口径影响', desc: '回看这些规则是否会影响区域筛查、协同报表和对象分层链路。' },
      { label: '只读下钻详情', desc: '核对来源模块、升级时效、推送对象和表达式说明。' },
      { label: '联动协同模块', desc: '必要时再回到统计报表、预警中心和漏保页做只读交叉判断。' }
    ]
  }
  if (roleView.value === 'insurer') {
    return [
      { label: '锁定设备/工伤规则', desc: '先看设备、工伤和红警规则的触发结构。' },
      { label: '确认协同对象', desc: '检查推送目标是否能真正落到保险承接和事故预防链路。' },
      { label: '下钻详情复核', desc: '回看规则表达式、超时设置和升级级别。' },
      { label: '回到风险协同页', desc: '再联动安责险、高处作业和工伤页继续只读筛查。' }
    ]
  }
  return [
    { label: '锁定高强度规则', desc: '先看红警规则、设备/工伤来源规则和停用规则。' },
    { label: '核对触发与升级', desc: '确认来源模块、超时分钟和升级级别是否符合治理时效。' },
    { label: '必要时维护规则', desc: '对失效规则、推送缺口或升级不当规则直接修改或停用。' },
    { label: '回到预警链路', desc: '再回到预警中心、设备和工伤治理页承接后续闭环。' }
  ]
})

const currentRuleActionSummary = computed(() => describeRuleAction(currentRule.value, roleView.value))
const currentRuleActionTags = computed(() => buildDetailTags(currentRule.value, roleView.value, activeFocus.value))
const ruleHintTags = computed(() => buildDetailTags(currentRule.value || visibleWarningRuleList.value[0], roleView.value, activeFocus.value))
const detailTags = computed(() => buildDetailTags(detailRule.value || currentRule.value, roleView.value, activeFocus.value))
const primaryRuleAction = computed(() => resolvePrimaryRuleAction(currentRule.value))
const resolvedWorkflowSteps = computed(() => workflowSteps.value.map((item, index) => {
  const explanation = resolvePortalExplanationItem(index)
  return {
    ...item,
    label: explanation?.dimensionName || explanation?.moduleLabel || explanation?.moduleCode || item.label,
    desc: resolveExplanationFirstText(item.desc, explanation)
  }
}))
const resolvedCurrentRuleActionSummary = computed(() => resolveExplanationFirstText(
  currentRuleActionSummary.value,
  leadingPortalExplanation.value
))
const resolvedCurrentRuleActionTags = computed(() => {
  const tags = [...currentRuleActionTags.value]
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
const resolvedRuleHintTags = computed(() => {
  const tags = [...ruleHintTags.value]
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
const resolvedDetailTags = computed(() => {
  const tags = [...detailTags.value]
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
const resolvedPrimaryRuleAction = computed(() => ({
  ...primaryRuleAction.value,
  label: resolveExplanationFirstActionText(primaryRuleAction.value.label, leadingPortalExplanation.value)
}))
const focusTableHint = computed(() => activeFocus.value?.desc || '按当前焦点优先暴露最需要复核的预警规则。')

const readOnlyAlertTitle = computed(() => `${readOnlyRoleLabel.value}：当前仅保留规则摘要、详情和导出`)
const readOnlyAlertDescription = computed(() => {
  if (isInsurerRole.value) {
    return `${readOnlyRoleDescription.value} 当前页面聚焦规则来源、红警强度和保险协同触达对象复核，不展示规则维护动作。`
  }
  if (isBankRole.value) {
    return `${readOnlyRoleDescription.value} 当前页面仅作为预警口径只读入口，用于核对规则是否影响区域筛查和协同链路。`
  }
  return `${readOnlyRoleDescription.value} 当前页面聚焦规则结构、模块来源和状态复核，不展示规则维护与停用动作。`
})

function countBy(predicate) {
  return warningRuleList.value.filter(item => predicate(item || {})).length
}

function handleSelectFocus(item) {
  activeFocusKey.value = item.key
}

function handlePrimaryRuleAction() {
  if (!currentRule.value) {
    return
  }
  if (primaryRuleAction.value.kind === 'edit') {
    if (blockReadOnlyAction('修改规则')) {
      return
    }
    handleUpdate(currentRule.value)
    return
  }
  openDetail(currentRule.value)
}

const workbenchContext = computed(() => buildWorkbenchContext(route.query, {
  fields: warningRuleWorkbenchFields,
  title: '当前规则页沿用了工作台来源条件',
  description: '已按上游工作台带入的来源模块筛选规则台账，适合继续核对预警链路和停用影响。',
  fieldLabels: {
    sourceModule: '来源模块',
    focusKey: '焦点队列'
  },
  fieldFormatters: {
    sourceModule: value => optionLabel(sourceModuleOptions, value, value),
    focusKey: value => warningRuleFocusLabel(value)
  }
}))

function resetQuery() {
  proxy.resetForm('queryRef')
  Object.assign(queryParams.value, {
    pageNum: 1,
    pageSize: 10,
    ruleName: undefined,
    sourceModule: undefined,
    ruleStatus: undefined
  })
  applyWorkbenchRouteQuery(route.query, queryParams.value, warningRuleWorkbenchFields)
  activeFocusKey.value = resolveWarningRuleFocusKey(route.query.focusKey)
  getList()

watchEffect(() => {
  setPageGuide({
    title: '????????' || '????????',
    description: '?????????????????????????????????' || '?????????????????????????????????',
    portalExplanation: portalExplanationItems.value,
    focus: resolvedFocusQueues.value,
    selection: [...selectedOverview.value, { label: '??????', value: resolvedCurrentRuleActionSummary.value }],
    workflow: resolvedWorkflowSteps.value,
    hints: [...resolvedCurrentRuleActionTags.value, ...resolvedRuleHintTags.value].slice(0, 6)
  })
})

}

function clearWorkbenchContext() {
  activeFocusKey.value = ''
  Object.assign(queryParams.value, {
    pageNum: 1,
    pageSize: 10,
    ruleName: undefined,
    sourceModule: undefined,
    ruleStatus: undefined
  })
  router.replace({
    path: route.path,
    query: stripWorkbenchRouteQuery(route.query, warningRuleWorkbenchFields)
  })
  getList()
}

function applyWarningRuleWorkbenchQuery(routeQuery = {}) {
  Object.assign(queryParams.value, {
    pageNum: 1,
    pageSize: 10,
    ruleName: undefined,
    sourceModule: undefined,
    ruleStatus: undefined
  })
  applyWorkbenchRouteQuery(routeQuery, queryParams.value, warningRuleWorkbenchFields)
  activeFocusKey.value = resolveWarningRuleFocusKey(routeQuery.focusKey)
  getList()
}

function handlePortalExplanationAction(action) {
  if (action?.path === route.path) {
    applyWarningRuleWorkbenchQuery(action.query || {})
  }
  openPortalExplanationAction(router, action)
}

function sortWarningRuleList(list, focusKey) {
  return [...(list || [])].sort((left, right) => scoreRule(right, focusKey) - scoreRule(left, focusKey))
}

function scoreRule(rule, focusKey) {
  if (!rule) {
    return 0
  }
  let score = 0
  if (rule.warnLevel === '3') score += 42
  if (rule.ruleStatus === '0') score += 28
  if (['DEVICE', 'INJURY'].includes(rule.sourceModule)) score += 24
  if (!rule.pushTargets) score += 18
  if (Number(rule.timeoutMinutes || 0) > 0 && Number(rule.timeoutMinutes || 0) <= 60) score += 12
  if (Number(rule.timeoutMinutes || 0) >= 180) score += 10

  switch (focusKey) {
    case 'red':
      score += rule.warnLevel === '3' ? 120 : 0
      break
    case 'deviceInjury':
      score += ['DEVICE', 'INJURY'].includes(rule.sourceModule) ? 120 : 0
      break
    case 'inactive':
      score += rule.ruleStatus === '0' ? 120 : 0
      break
    case 'fastTimeout':
      score += Number(rule.timeoutMinutes || 0) > 0 && Number(rule.timeoutMinutes || 0) <= 60 ? 120 : 0
      break
    case 'highTimeout':
      score += Number(rule.timeoutMinutes || 0) >= 180 ? 120 : 0
      break
    case 'missingTargets':
      score += !rule.pushTargets ? 120 : 0
      break
    case 'expansion':
      score += rule.sourceModule === 'EXPANSION' ? 120 : 0
      break
    default:
      break
  }

  return score
}

function resolvePrimaryRuleAction(rule) {
  if (!rule) {
    return { kind: 'detail', label: isReadOnlyRole.value ? '查看详情' : '选择规则' }
  }
  if (isReadOnlyRole.value) {
    return { kind: 'detail', label: '查看详情' }
  }
  if (
    rule.ruleStatus === '0' ||
    !rule.pushTargets ||
    rule.warnLevel === '3' ||
    Number(rule.timeoutMinutes || 0) <= 60
  ) {
    return { kind: 'edit', label: '修改规则' }
  }
  return { kind: 'detail', label: '查看详情' }
}

function describeRuleAction(rule, currentRoleView) {
  if (!rule) {
    return '先从左侧焦点队列切入，再选择一条重点规则进入复核或维护。'
  }
  if (currentRoleView === 'bank') {
    return `建议先确认 ${rule.ruleName || '该规则'} 是否会影响区域筛查、协同报表和对象分层口径，再回看推送对象和停用影响。`
  }
  if (currentRoleView === 'insurer') {
    return `建议先确认 ${rule.ruleName || '该规则'} 是否覆盖保险协同承接对象，再回看设备/工伤来源和红警升级强度。`
  }
  if (rule.ruleStatus === '0') {
    return `建议优先确认 ${rule.ruleName || '该规则'} 停用后是否仍有预警链路残留依赖，必要时恢复或替换。`
  }
  if (!rule.pushTargets) {
    return `建议先补齐 ${rule.ruleName || '该规则'} 的推送对象，避免规则触发后没有明确承接人。`
  }
  if (rule.warnLevel === '3') {
    return `建议优先复核 ${rule.ruleName || '该规则'} 的红警强度和升级链路，避免高风险事件误报或漏报。`
  }
  if (Number(rule.timeoutMinutes || 0) <= 60) {
    return `建议确认 ${rule.ruleName || '该规则'} 的处置时效是否过紧，避免现场端持续被高频催办。`
  }
  return `${rule.ruleName || '该规则'} 当前结构相对稳定，可继续回看表达式、升级级别和历史依赖是否匹配现场治理链路。`
}

function buildDetailTags(rule, currentRoleView, focus) {
  if (!rule) {
    return [{ label: '未选中规则', type: 'info' }]
  }
  const tags = []
  if (focus?.title) {
    tags.push({ label: `当前焦点：${focus.title}`, type: 'info' })
  }
  if (rule.warnLevel === '3') {
    tags.push({ label: '红警规则', type: 'danger' })
  }
  if (rule.ruleStatus === '0') {
    tags.push({ label: '当前已停用', type: 'warning' })
  }
  if (rule.sourceModule === 'DEVICE' || rule.sourceModule === 'INJURY') {
    tags.push({ label: '现场治理核心来源', type: 'success' })
  }
  if (rule.sourceModule === 'EXPANSION') {
    tags.push({ label: '参保风险协同来源', type: 'warning' })
  }
  if (Number(rule.timeoutMinutes || 0) <= 60) {
    tags.push({ label: '时效要求较高', type: 'warning' })
  }
  if (Number(rule.timeoutMinutes || 0) >= 180) {
    tags.push({ label: '超时设置偏长', type: 'info' })
  }
  if (!rule.pushTargets) {
    tags.push({ label: '推送目标待补齐', type: 'warning' })
  }
  if (currentRoleView === 'insurer' && ['DEVICE', 'INJURY'].includes(rule.sourceModule)) {
    tags.push({ label: '保险协同重点', type: 'danger' })
  }
  if (currentRoleView === 'bank' && rule.sourceModule === 'EXPANSION') {
    tags.push({ label: '区域筛查重点', type: 'info' })
  }
  if (!tags.length) {
    tags.push({ label: '当前暂无明显配置风险', type: 'success' })
  }
  return tags
}

activeFocusKey.value = resolveWarningRuleFocusKey(route.query.focusKey)
init()
function resolveWarningRuleFocusKey(value) {
  const normalized = typeof value === 'string' ? value.trim() : ''
  return focusQueues.value.some(item => item.key === normalized) ? normalized : ''
}

function warningRuleFocusLabel(value) {
  return focusQueues.value.find(item => item.key === value)?.title || value || '-'
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
  margin-bottom: 12px;
  color: #516273;
  font-size: 13px;
  line-height: 1.7;
}

.azb-workbench-alert__desc strong {
  color: #0f5ea8;
}

.azb-summary-grid,
.azb-focus-grid {
  display: grid;
  gap: 16px;
  margin-bottom: 16px;
}

.azb-summary-grid {
  grid-template-columns: repeat(4, minmax(0, 1fr));
}

.azb-focus-grid {
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

.azb-summary-card,
.azb-focus-card {
  border: 1px solid #dbe7f3;
  border-radius: 16px;
  background: #fff;
}

.azb-summary-card {
  padding: 18px 20px;
}

.azb-summary-card__label {
  color: #64748b;
  font-size: 13px;
}

.azb-summary-card__value {
  margin-top: 10px;
  color: #0f172a;
  font-size: 28px;
  font-weight: 700;
}

.azb-summary-card__unit {
  margin-left: 4px;
  color: #64748b;
  font-size: 13px;
  font-weight: 500;
}

.azb-summary-card__note {
  margin-top: 10px;
  color: #64748b;
  font-size: 13px;
  line-height: 1.7;
}

.azb-summary-card--success {
  background: linear-gradient(180deg, #ffffff 0%, #f4fbf8 100%);
}

.azb-summary-card--danger {
  background: linear-gradient(180deg, #ffffff 0%, #fff4f4 100%);
}

.azb-summary-card--warning {
  background: linear-gradient(180deg, #ffffff 0%, #fff8ef 100%);
}

.azb-focus-card :deep(.el-card__header) {
  padding: 18px 20px 0;
  border-bottom: 0;
}

.azb-focus-card :deep(.el-card__body) {
  padding: 18px 20px 20px;
}

.azb-card-head__title {
  color: #0f172a;
  font-size: 18px;
  font-weight: 700;
}

.azb-card-head__desc {
  margin-top: 6px;
  color: #64748b;
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

.azb-source-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 14px 16px;
  border: 1px solid #dbe7f3;
  border-radius: 14px;
  background: #f8fbff;
}

.azb-source-item__label {
  color: #64748b;
  font-size: 13px;
}

.azb-source-item__value {
  color: #0f172a;
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
  border: 1px solid #dbe7f3;
  border-radius: 14px;
  background: #f8fbff;
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
  color: #0f172a;
  font-size: 15px;
}

.azb-focus-queue__main p {
  margin: 6px 0 0;
  color: #64748b;
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
  color: #0f172a;
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
  color: #0f172a;
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
  border: 1px solid #dbe7f3;
  border-radius: 14px;
  background: #f8fbff;
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
  color: #0f172a;
  font-size: 14px;
}

.azb-pipeline-item__body p {
  margin: 6px 0 0;
  color: #64748b;
  font-size: 13px;
  line-height: 1.7;
}

.azb-card-head--between {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: flex-start;
}

.azb-rule-form-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 0 16px;
}

.azb-detail-block {
  margin-top: 20px;
}

.azb-detail-block h3 {
  margin: 0 0 12px;
  color: #0f172a;
  font-size: 16px;
}

.azb-tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

@media (max-width: 1200px) {
  .azb-summary-grid,
  .azb-focus-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 900px) {
  .azb-focus-grid,
  .azb-rule-form-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .azb-summary-grid {
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
