<template>
  <div class="app-container ygb-page">
    <section class="gov-page-header ygb-page__header">
      <div>
        <p class="ygb-page__eyebrow">预警中心</p>
        <h1 class="ygb-page__title">{{ roleTitle }}</h1>
        <p class="ygb-page__desc">
          {{ roleDescription }}
          粤工保侧重点不是只看规则数量，而是把规则建账、推送触达、超时升级和预警闭环真正串成一条连续办理链。
        </p>
      </div>
      <div class="ygb-page__tip">
        <div class="ygb-page__tip-item">当前视角：{{ roleBadge }}</div>
        <div class="ygb-page__tip-item">{{ roleTip }}</div>
        <div class="ygb-page__tip-item">规则调整会影响触达对象、超时升级和预警闭环，请在启用前完成复核。</div>
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
      :title="`${readOnlyRoleLabel}：当前页面仅保留列表查看、详情和导出`"
      :description="readOnlyRoleDescription || '规则维护入口已自动隐藏。'"
      type="info"
      :closable="false"
      show-icon
      style="margin-bottom: 18px"
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
          <el-button type="primary" icon="Search" @click="handleQuery">查询</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="toolbar-card ygb-toolbar-card" shadow="never">
      <el-row :gutter="10">
        <el-col v-if="canAdd" :span="1.5">
          <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['ygb:warningRule:add']">新增规则</el-button>
        </el-col>
        <el-col v-if="canEdit" :span="1.5">
          <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['ygb:warningRule:edit']">修改规则</el-button>
        </el-col>
        <el-col v-if="canRemove" :span="1.5">
          <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['ygb:warningRule:remove']">停用规则</el-button>
        </el-col>
        <el-col v-if="canExport" :span="1.5">
          <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['ygb:warningRule:export']">导出</el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>
    </el-card>

    <el-card class="table-card ygb-table-card" shadow="never">
      <template #header>
        <div class="ygb-card-head ygb-card-head--between">
          <div>
            <div class="ygb-card-head__title">联动预警规则台账</div>
            <div class="ygb-card-head__desc">{{ focusTableHint }}</div>
          </div>
          <div class="ygb-card-head__desc">当前总量 {{ total }} 条</div>
        </div>
      </template>
      <el-table v-loading="loading" :data="visibleWarningRuleList" @selection-change="handleSelectionChange" @row-click="handleRowClick">
        <el-table-column v-if="showSelection" type="selection" width="55" align="center" />
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
        <el-table-column label="推送目标" prop="pushTargets" width="180" show-overflow-tooltip />
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

        <el-table-column label="操作" fixed="right" align="center" :width="tableActionWidth" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-button link type="info" icon="View" @click.stop="openDetail(scope.row)">详情</el-button>
            <el-button
              v-if="canEdit"
              link
              type="primary"
              icon="Edit"
              @click.stop="handleUpdate(scope.row)"
              v-hasPermi="['ygb:warningRule:edit']"
            >
              修改
            </el-button>
            <el-button
              v-if="canRemove"
              link
              type="primary"
              icon="Delete"
              @click.stop="handleDelete(scope.row)"
              v-hasPermi="['ygb:warningRule:remove']"
            >
              停用
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <pagination v-show="total > 0" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" :total="total" @pagination="getList" />
    </el-card>

    <el-dialog v-model="open" :title="title" width="860px" append-to-body>
      <el-form ref="ruleRef" :model="form" :rules="rules" label-width="110px">
        <div class="ygb-rule-form-grid">
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
          <el-descriptions-item label="当前视角重点" :span="2">{{ detailFocusText }}</el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ detailRule.remark || '-' }}</el-descriptions-item>
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

<script setup name="YgbWarningRule">
import { computed, getCurrentInstance, ref, watch, watchEffect } from 'vue'
import { useWorkbenchAssist } from '@/composables/useWorkbenchAssist'
import { useRoute, useRouter } from 'vue-router'
import useUserStore from '@/store/modules/user'
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
const { setPageGuide } = useWorkbenchAssist()
const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const { isReadOnlyRole, readOnlyRoleLabel, readOnlyRoleDescription } = useRoleViewMode()
const activeFocusKey = ref('')
const warningRuleWorkbenchFields = ['sourceModule', 'focusKey']

function hasPermissionPrefix(permissions, prefixes) {
  if (!Array.isArray(permissions)) {
    return false
  }
  if (permissions.includes('*:*:*')) {
    return true
  }
  return permissions.some(permission => prefixes.some(prefix => permission === prefix || permission.startsWith(prefix)))
}

function hasPermission(permissions, targets) {
  if (!Array.isArray(permissions)) {
    return false
  }
  if (permissions.includes('*:*:*')) {
    return true
  }
  return targets.some(target => {
    if (permissions.includes(target)) {
      return true
    }
    const segments = target.split(':')
    if (segments.length === 3) {
      return permissions.includes(`${segments[0]}:${segments[1]}:*`)
    }
    return false
  })
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

const permissions = computed(() => userStore.permissions || [])
const canAdd = computed(() => !isReadOnlyRole.value && hasPermission(permissions.value, ['ygb:warningRule:add']))
const canEdit = computed(() => !isReadOnlyRole.value && hasPermission(permissions.value, ['ygb:warningRule:edit']))
const canRemove = computed(() => !isReadOnlyRole.value && hasPermission(permissions.value, ['ygb:warningRule:remove']))
const canExport = computed(() => hasPermission(permissions.value, ['ygb:warningRule:export']))
const showSelection = computed(() => canEdit.value || canRemove.value)
const tableActionWidth = computed(() => (canEdit.value || canRemove.value ? 220 : 90))

function summaryCard(key, label, value, unit, note, cardClass = '') {
  return { key, label, value, unit, note, cardClass }
}

function focusQueue(key, title, count, unit, desc, actionText) {
  return { key, title, count, unit, desc, actionText }
}

function countRows(predicate) {
  return warningRuleList.value.filter(predicate).length
}

function matchRuleFocus(rule, focusKey) {
  if (!rule || !focusKey) {
    return false
  }
  const sourceModule = String(rule.sourceModule || '')
  const timeoutMinutes = Number(rule.timeoutMinutes || 0)
  if (focusKey === 'red') {
    return String(rule.warnLevel || '') === '3'
  }
  if (focusKey === 'disabled') {
    return String(rule.ruleStatus || '') === '0'
  }
  if (focusKey === 'financial') {
    return ['SOCIAL', 'TAX', 'EXPANSION'].includes(sourceModule)
  }
  if (focusKey === 'deviceInjury') {
    return ['DEVICE', 'INJURY', 'SPECIAL'].includes(sourceModule)
  }
  if (focusKey === 'targetMissing') {
    return !String(rule.pushTargets || '').trim()
  }
  if (focusKey === 'timeoutShort') {
    return timeoutMinutes > 0 && timeoutMinutes <= 60
  }
  if (focusKey === 'timeoutLong') {
    return timeoutMinutes >= 240
  }
  if (focusKey === 'upgradeRed') {
    return String(rule.warnLevel || '') !== '3' && String(rule.upgradeLevel || '') === '3'
  }
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

function syncCurrentRuleByFocus() {
  if (!visibleWarningRuleList.value.length) {
    currentRule.value = undefined
    return
  }
  if (!currentRule.value || !visibleWarningRuleList.value.some(item => item.ruleId === currentRule.value.ruleId)) {
    currentRule.value = visibleWarningRuleList.value[0]
    return
  }
  if (activeFocus.value?.key && !matchRuleFocus(currentRule.value, activeFocus.value.key)) {
    const matched = visibleWarningRuleList.value.find(item => matchRuleFocus(item, activeFocus.value.key))
    if (matched) {
      currentRule.value = matched
    }
  }
}

function handleSelectFocus(item) {
  activeFocusKey.value = item.key
  syncCurrentRuleByFocus()
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
  init,
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
} = useWarningRulePage({
  exportFilePrefix: 'ygb_warning_rule',
  getCurrentList: () => visibleWarningRuleList.value,
  afterList: () => {
    syncActiveFocus()
  },
  canMutate: () => !isReadOnlyRole.value,
  immediate: false
})

function buildWarningRuleExplanationQuery(extraQuery = {}) {
  return {
    sourceModule: queryParams.value.sourceModule,
    ruleStatus: queryParams.value.ruleStatus,
    ...extraQuery
  }
}

const roleBadge = computed(() => {
  if (roleView.value === 'hrss') return '人社监管视角'
  if (roleView.value === 'operator') return '企业经办视角'
  if (roleView.value === 'finance') return '财务经办视角'
  if (roleView.value === 'admin') return '企业管理员视角'
  return '综合办理视角'
})

const roleTitle = computed(() => {
  if (roleView.value === 'hrss') return '区域预警规则复核工作台'
  if (roleView.value === 'operator') return '预警规则回写与校对台账'
  if (roleView.value === 'finance') return '财务联动预警规则台账'
  if (roleView.value === 'admin') return '企业预警规则闭环工作台'
  return '联动预警规则办理台账'
})

const roleDescription = computed(() => {
  if (roleView.value === 'hrss') {
    return '面向人社监管经办统一复核红警规则、停用规则和推送缺口，重点是把规则维护持续拉回区域监管、预警闭环和月报归档链路。'
  }
  if (roleView.value === 'operator') {
    return '面向企业经办统一校对推送目标、规则说明和超时参数，重点是先把规则留痕和触达对象补齐，再交由管理员或监管经办推进后续校正。'
  }
  if (roleView.value === 'finance') {
    return '面向财务经办统一查看社保、税务和扩面减损来源规则，重点是把规则触达、升级策略和办理链路真正回落到工资社税整改主链。'
  }
  if (roleView.value === 'admin') {
    return '面向企业管理员统一统筹规则建账、推送触达、升级校正和启停复核，重点是把规则维护直接串回企业办理和预警闭环主链。'
  }
  return '面向企业管理员、财务经办和监管经办统一维护预警等级、来源模块、推送对象和超时升级参数。'
})

const roleTip = computed(() => {
  if (roleView.value === 'hrss') {
    return '先锁定红警、停用和缺推送对象规则，再决定是否进入区域督办或预警闭环复核。'
  }
  if (roleView.value === 'operator') {
    return '优先处理缺推送、短时效和停用规则，避免规则台账只停留在结果层。'
  }
  if (roleView.value === 'finance') {
    return '优先回看社保、税务和扩面减损来源规则，减少财务在规则页和社税台账之间来回切换。'
  }
  if (roleView.value === 'admin') {
    return '优先统筹红警、财务来源和缺推送规则，再决定是补对象、调时效还是停用。'
  }
  return '当前规则用于统一配置社保、税务、扩面减损、用工比例、设备接入和工伤联动预警。'
})

const summaryCards = computed(() => {
  if (roleView.value === 'finance') {
    return [
      summaryCard('financial', '财务来源规则', countRows(row => matchRuleFocus(row, 'financial')), '条', '适合作为社保、税务和扩面减损联动口径复核入口。', 'ygb-summary-card--primary'),
      summaryCard('targetMissing', '缺推送对象', countRows(row => matchRuleFocus(row, 'targetMissing')), '条', '建议优先补齐企业管理员、财务经办和监管触达对象。', 'ygb-summary-card--warning'),
      summaryCard('timeoutLong', '长超时规则', countRows(row => matchRuleFocus(row, 'timeoutLong')), '条', '适合优先复核是否会拖慢财务整改和闭环回写。'),
      summaryCard('enabled', '启用规则', valueOrDefault(summaryData.value.enabledCount, countRows(row => String(row.ruleStatus || '') === '1')), '条', '当前仍在参与联动预警触发的规则数量。', 'ygb-summary-card--success')
    ]
  }
  if (roleView.value === 'hrss') {
    return [
      summaryCard('red', '红警规则', valueOrDefault(summaryData.value.redCount, countRows(row => matchRuleFocus(row, 'red'))), '条', '需要优先承接区域监管和闭环复核的重点规则。', 'ygb-summary-card--warning'),
      summaryCard('disabled', '停用规则', countRows(row => matchRuleFocus(row, 'disabled')), '条', '适合作为规则启停复核和历史依赖排查入口。'),
      summaryCard('targetMissing', '缺推送对象', countRows(row => matchRuleFocus(row, 'targetMissing')), '条', '用于提前发现监管督办链路中的触达缺口。', 'ygb-summary-card--primary'),
      summaryCard('enabledRatio', '启用占比', `${formatRatio(summaryData.value.enabledRatio)}`, '%', '用于判断当前筛选范围内规则启用密度。', 'ygb-summary-card--success')
    ]
  }
  if (roleView.value === 'operator') {
    return [
      summaryCard('targetMissing', '缺推送对象', countRows(row => matchRuleFocus(row, 'targetMissing')), '条', '适合优先补充规则责任人和接收对象。', 'ygb-summary-card--warning'),
      summaryCard('timeoutShort', '短时效规则', countRows(row => matchRuleFocus(row, 'timeoutShort')), '条', '建议先确认企业侧是否具备按时响应能力。', 'ygb-summary-card--primary'),
      summaryCard('disabled', '停用规则', countRows(row => matchRuleFocus(row, 'disabled')), '条', '可作为历史台账说明和后续启停复核入口。'),
      summaryCard('upgradeRed', '红警升级路径', countRows(row => matchRuleFocus(row, 'upgradeRed')), '条', '适合优先补充升级对象和说明材料。', 'ygb-summary-card--success')
    ]
  }
  if (roleView.value === 'admin') {
    return [
      summaryCard('total', '规则数量', valueOrDefault(summaryData.value.totalCount, total.value), '条', '当前筛选条件下的联动预警规则总量。'),
      summaryCard('red', '红警规则', valueOrDefault(summaryData.value.redCount, countRows(row => matchRuleFocus(row, 'red'))), '条', '建议优先回查触发口径、升级对象和闭环时效。', 'ygb-summary-card--warning'),
      summaryCard('financial', '财务来源规则', countRows(row => matchRuleFocus(row, 'financial')), '条', '适合联动工资、社保和税务主链复核口径。', 'ygb-summary-card--primary'),
      summaryCard('targetMissing', '缺推送对象', countRows(row => matchRuleFocus(row, 'targetMissing')), '条', '说明当前规则责任对象仍未完全闭合。', 'ygb-summary-card--success')
    ]
  }
  return [
    summaryCard('total', '规则数量', valueOrDefault(summaryData.value.totalCount, total.value), '条', '当前筛选条件下的联动预警规则总量。'),
    summaryCard('enabled', '启用规则', valueOrDefault(summaryData.value.enabledCount, countRows(row => String(row.ruleStatus || '') === '1')), '条', '仍在生效、直接参与联动预警触发的规则数量。', 'ygb-summary-card--success'),
    summaryCard('red', '红警规则', valueOrDefault(summaryData.value.redCount, countRows(row => matchRuleFocus(row, 'red'))), '条', '最高告警级别规则数量，建议优先复核触发口径和升级时效。', 'ygb-summary-card--warning'),
    summaryCard('targetMissing', '缺推送对象', countRows(row => matchRuleFocus(row, 'targetMissing')), '条', '适合作为规则责任对象补录入口。', 'ygb-summary-card--primary')
  ]
})

const focusQueues = computed(() => {
  if (roleView.value === 'finance') {
    return [
      focusQueue('financial', '财务来源规则', countRows(row => matchRuleFocus(row, 'financial')), '条', '优先锁定社保、税务和扩面减损来源规则，减少财务在多台账间反复切换。', '回看社税扩面链路'),
      focusQueue('targetMissing', '缺推送对象规则', countRows(row => matchRuleFocus(row, 'targetMissing')), '条', '先补齐财务经办、企业管理员和监管触达对象，避免规则只触发不落地。', '补推送对象'),
      focusQueue('timeoutLong', '长超时规则', countRows(row => matchRuleFocus(row, 'timeoutLong')), '条', '优先复核超时分钟是否会拖慢社税整改和闭环回写。', '校正升级时效')
    ]
  }
  if (roleView.value === 'hrss') {
    return [
      focusQueue('red', '红警规则', valueOrDefault(summaryData.value.redCount, countRows(row => matchRuleFocus(row, 'red'))), '条', '优先承接最需要区域监管复核的高等级规则。', '进入区域复核'),
      focusQueue('disabled', '停用规则', countRows(row => matchRuleFocus(row, 'disabled')), '条', '适合持续复核历史依赖、停用原因和是否需要恢复启用。', '复核停用原因'),
      focusQueue('targetMissing', '缺推送对象规则', countRows(row => matchRuleFocus(row, 'targetMissing')), '条', '先补齐监管链路中的责任对象，再继续看闭环时效。', '补齐监管触达')
    ]
  }
  if (roleView.value === 'operator') {
    return [
      focusQueue('targetMissing', '缺推送对象规则', countRows(row => matchRuleFocus(row, 'targetMissing')), '条', '优先补录推送责任人和企业接收对象，避免规则无责任闭环。', '补责任对象'),
      focusQueue('timeoutShort', '短时效规则', countRows(row => matchRuleFocus(row, 'timeoutShort')), '条', '先看超时时间是否过短，再决定是否需要补说明或调整时效。', '补时效说明'),
      focusQueue('disabled', '停用规则', countRows(row => matchRuleFocus(row, 'disabled')), '条', '适合作为停用说明、历史留痕和后续启停复核入口。', '补停用留痕')
    ]
  }
  if (roleView.value === 'admin') {
    return [
      focusQueue('red', '红警规则', valueOrDefault(summaryData.value.redCount, countRows(row => matchRuleFocus(row, 'red'))), '条', '先锁定真正会影响企业预警闭环的高等级规则。', '回看高风险规则'),
      focusQueue('financial', '财务来源规则', countRows(row => matchRuleFocus(row, 'financial')), '条', '优先把工资、社保、税务和扩面减损规则拉回企业办理主链。', '联动财务主链'),
      focusQueue('targetMissing', '缺推送对象规则', countRows(row => matchRuleFocus(row, 'targetMissing')), '条', '先补齐责任人和接收对象，再继续推进升级校正。', '补齐对象口径')
    ]
  }
  return [
    focusQueue('red', '红警规则', valueOrDefault(summaryData.value.redCount, countRows(row => matchRuleFocus(row, 'red'))), '条', '优先回查最高等级规则，确认触发条件和升级对象是否准确。', '查看重点规则'),
    focusQueue('targetMissing', '缺推送对象规则', countRows(row => matchRuleFocus(row, 'targetMissing')), '条', '优先补齐责任对象和接收角色。', '补齐规则对象'),
    focusQueue('disabled', '停用规则', countRows(row => matchRuleFocus(row, 'disabled')), '条', '适合作为停用原因和历史依赖排查入口。', '查看停用原因')
  ]
})

const portalExplanations = computed(() => {
  if (Array.isArray(summaryData.value.ygbExplanation) && summaryData.value.ygbExplanation.length) {
    return summaryData.value.ygbExplanation
  }
  return focusQueues.value.map(item => ({
    key: item.key,
    dimensionName: item.title,
    currentValue: item.count,
    targetValue: '-',
    summary: item.desc,
    evidenceModule: 'warningRule',
    recommendModule: 'warningRule',
    defaultQuery: buildWarningRuleExplanationQuery({ focusKey: item.key }),
    sourceLabel: '530.1 预警规则办理解释',
    sourceDescription: item.desc,
    actionText: item.actionText
  }))
})

const portalExplanationItems = computed(() => decoratePortalExplanationItems(portalExplanations.value, {
  portalCode: 'ygb',
  panelTitle: '530.1 预警规则办理解释',
  panelDescription: '当前解释项按 530.1 办理链口径展示规则来源、承接焦点和下步动作。'
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
  if (explanation?.actionText) {
    return explanation.actionText
  }
  if (explanation?.moduleLabel) {
    return `进入${explanation.moduleLabel}`
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

const activeFocus = computed(() => {
  if (!focusQueues.value.length) return null
  return focusQueues.value.find(item => item.key === activeFocusKey.value) || focusQueues.value[0]
})

const visibleWarningRuleList = computed(() => prioritizeFocusRows(warningRuleList.value, row => matchRuleFocus(row, activeFocus.value?.key)))

const workbenchContext = computed(() => buildWorkbenchContext(route.query, {
  fields: warningRuleWorkbenchFields,
  sourceLabel: '上游工作台',
  title: '当前预警规则页沿用了上游来源条件。',
  description: '当前列表保留了来源模块和解释焦点范围，便于从同一办理语境继续核对规则台账。',
  fieldLabels: {
    sourceModule: '来源模块',
    focusKey: '解释焦点'
  },
  fieldFormatters: {
    sourceModule: value => optionLabel(sourceModuleOptions, value, value),
    focusKey: value => warningRuleFocusLabel(value)
  }
}))

const focusTableHint = computed(() => {
  if (!activeFocus.value) {
    return '按当前筛选条件展示联动预警规则台账。'
  }
  return `当前焦点为“${activeFocus.value.title}”，已把对应重点规则优先排到表格前列。`
})

const selectedRuleOverview = computed(() => {
  if (!currentRule.value) {
    return [
      { label: '当前焦点', value: activeFocus.value?.title || '-' },
      { label: '焦点数量', value: `${activeFocus.value?.count ?? 0}${activeFocus.value?.unit || ''}` },
      { label: '来源筛选', value: optionLabel(sourceModuleOptions, queryParams.value.sourceModule, '全部来源') },
      { label: '状态筛选', value: optionLabel(ruleStatusOptions, queryParams.value.ruleStatus, '全部状态') }
    ]
  }
  return [
    { label: '来源模块', value: optionLabel(sourceModuleOptions, currentRule.value.sourceModule) },
    { label: '预警级别', value: optionLabel(warnLevelOptions, currentRule.value.warnLevel) },
    { label: '规则状态', value: optionLabel(ruleStatusOptions, currentRule.value.ruleStatus) },
    {
      label: '超时/升级',
      value: `${valueOrDefault(currentRule.value.timeoutMinutes, '-')} / ${optionLabel(warnLevelOptions, currentRule.value.upgradeLevel)}`
    }
  ]
})

function resolveModuleAction(rule, focusKey) {
  const sourceModule = String(rule?.sourceModule || '')
  if (sourceModule === 'SOCIAL') {
    return { label: '查看社保缴费监控', path: '/social-insurance/payment' }
  }
  if (sourceModule === 'TAX') {
    return { label: '查看个税比对', path: '/tax-supervision/personalTax' }
  }
  if (sourceModule === 'EXPANSION') {
    return { label: '查看漏保清单', path: '/expansion-reduction/uninsured' }
  }
  if (sourceModule === 'SPECIAL') {
    return { label: '查看用工比例', path: '/special-rectification/employmentRatio' }
  }
  if (sourceModule === 'DEVICE') {
    return { label: '查看设备管理', path: '/device-management/detail' }
  }
  if (sourceModule === 'INJURY') {
    return { label: '查看工伤事件', path: '/injury-supervision/event' }
  }
  if (focusKey === 'financial') {
    return { label: '查看社保缴费监控', path: '/social-insurance/payment' }
  }
  if (focusKey === 'targetMissing' || focusKey === 'timeoutShort' || focusKey === 'timeoutLong') {
    return { label: '查看预警中心', path: '/warning-center/workOrder' }
  }
  if (focusKey === 'disabled') {
    return { label: '查看预警治理月报', path: '/statistical-report/custom' }
  }
  return { label: '查看预警中心', path: '/warning-center/workOrder' }
}

function needsEdit(rule) {
  return matchRuleFocus(rule, 'targetMissing')
    || matchRuleFocus(rule, 'timeoutShort')
    || matchRuleFocus(rule, 'timeoutLong')
    || matchRuleFocus(rule, 'disabled')
    || matchRuleFocus(rule, 'red')
}

const primaryAction = computed(() => {
  if (!currentRule.value) {
    if (canAdd.value) {
      return { label: '新增规则', action: 'add' }
    }
    return { label: activeFocus.value?.actionText || '查看详情', action: 'detail' }
  }
  if (isReadOnlyRole.value) {
    return { label: '查看详情', action: 'detail' }
  }
  if (canEdit.value && needsEdit(currentRule.value)) {
    return {
      label: String(currentRule.value.ruleStatus || '') === '0' ? '修改并复核' : '修改规则',
      action: 'edit'
    }
  }
  if (canEdit.value && roleView.value === 'finance' && matchRuleFocus(currentRule.value, 'financial')) {
    return { label: '校正规则口径', action: 'edit' }
  }
  return { label: '查看详情', action: 'detail' }
})

const secondaryAction = computed(() => resolveModuleAction(currentRule.value, activeFocus.value?.key))

function buildHintTags(rule, focus, role) {
  if (!rule) {
    return [{ label: '未选中规则，可先在列表中选择当前待维护规则。', type: 'info' }]
  }
  const tags = []
  const timeoutMinutes = Number(rule.timeoutMinutes || 0)

  if (focus === 'targetMissing' || !String(rule.pushTargets || '').trim()) {
    tags.push({ label: '当前规则缺少推送对象，建议优先补齐企业、财务和监管责任角色。', type: 'warning' })
  }
  if (String(rule.warnLevel || '') === '3') {
    tags.push({ label: '当前为红警规则，建议优先复核触发条件和升级对象是否准确。', type: 'warning' })
  }
  if (String(rule.ruleStatus || '') === '0') {
    tags.push({ label: '当前规则已停用，建议先确认是否仍有办理链路依赖该规则。', type: 'warning' })
  }
  if (timeoutMinutes > 0 && timeoutMinutes <= 60) {
    tags.push({ label: '当前超时时间较短，建议确认企业侧是否具备及时响应能力。', type: 'info' })
  }
  if (timeoutMinutes >= 240) {
    tags.push({ label: '当前超时时间较长，建议复核是否会影响联动预警闭环时效。', type: 'warning' })
  }
  if (matchRuleFocus(rule, 'financial')) {
    tags.push({ label: '当前服务社保、税务或扩面减损链路，建议同步核对财务整改和企业办理触达口径。', type: 'info' })
  }
  if (matchRuleFocus(rule, 'deviceInjury')) {
    tags.push({ label: '当前服务设备接入、工伤或用工比例链路，建议复核现场闭环对象和监管接收角色。', type: 'info' })
  }
  if (String(rule.warnLevel || '') !== '3' && String(rule.upgradeLevel || '') === '3') {
    tags.push({ label: '当前规则存在红警升级路径，可继续用于高风险事项督办。', type: 'success' })
  }

  if (role === 'finance' && matchRuleFocus(rule, 'financial')) {
    tags.push({ label: '财务视角建议优先看规则是否已覆盖工资、社保、税务和扩面减损责任对象。', type: 'info' })
  }
  if (role === 'hrss' && String(rule.warnLevel || '') === '3') {
    tags.push({ label: '人社监管视角建议优先把红警规则接回区域督办和月报复核。', type: 'success' })
  }
  if (role === 'operator' && !String(rule.pushTargets || '').trim()) {
    tags.push({ label: '企业经办视角更适合先补责任对象和规则说明，再继续推进启停复核。', type: 'success' })
  }
  if (role === 'admin' && matchRuleFocus(rule, 'targetMissing')) {
    tags.push({ label: '企业管理员视角应先补对象口径，再决定是否继续调超时或停用规则。', type: 'success' })
  }

  if (!tags.length) {
    tags.push({ label: '当前规则台账信息较完整，可继续用于联动预警触达和闭环复核。', type: 'success' })
  }
  return tags
}

function buildPageHintTags() {
  if (roleView.value === 'finance') {
    return [
      { label: '优先复核社保、税务和扩面减损来源规则', type: 'warning' },
      { label: '推送对象要覆盖财务经办和企业管理员', type: 'info' },
      { label: '规则口径已纳入预警闭环管理', type: 'success' }
    ]
  }
  if (roleView.value === 'hrss') {
    return [
      { label: '优先锁定红警、停用和缺推送对象规则', type: 'warning' },
      { label: '规则复核结果应回落到区域监管和月报归档', type: 'info' },
      { label: '当前视角重点不是新增规则数量，而是监管触达是否闭合', type: 'success' }
    ]
  }
  if (roleView.value === 'operator') {
    return [
      { label: '优先补推送对象、规则说明和停用留痕', type: 'warning' },
      { label: '企业经办更适合先补材料，再交由管理员做最终校正', type: 'info' },
      { label: '当前为 PC 办理台账，规则调整按统一审批口径留痕', type: 'success' }
    ]
  }
  if (roleView.value === 'admin') {
    return [
      { label: '优先统筹红警、财务来源和缺推送对象规则', type: 'warning' },
      { label: '规则维护应直接串回预警、社税和扩面主链', type: 'info' },
      { label: '当前继续共用统一规则底数和启停逻辑', type: 'success' }
    ]
  }
  return [
    { label: '优先复核红警规则和缺推送对象规则', type: 'warning' },
    { label: '规则配置要与来源模块办理链保持一致', type: 'info' },
    { label: '当前规则已纳入统一预警口径和闭环统计', type: 'success' }
  ]
}

const currentActionSummary = computed(() => {
  if (!currentRule.value) {
    return activeFocus.value
      ? `当前已按“${activeFocus.value.title}”重排台账，建议优先处理表格前列规则，再决定是否进入规则修改、预警联动或闭环复核。`
      : '当前暂无规则对象。'
  }
  if (roleView.value === 'finance') {
    if (matchRuleFocus(currentRule.value, 'financial')) {
      return '该规则服务社保、税务或扩面减损链路，建议优先确认推送对象、超时分钟和升级级别是否已覆盖财务整改主链。'
    }
    if (matchRuleFocus(currentRule.value, 'timeoutLong')) {
      return '该规则超时时间较长，建议优先压缩升级时效，避免财务整改对象长期停留在预警处理中。'
    }
  }
  if (roleView.value === 'hrss') {
    if (String(currentRule.value.warnLevel || '') === '3') {
      return '该规则当前属于红警规则，建议优先复核触发口径、升级对象和区域监管接收角色。'
    }
    if (String(currentRule.value.ruleStatus || '') === '0') {
      return '该规则当前已停用，建议先确认是否仍被历史办理链路依赖，再决定是否恢复或归档停用原因。'
    }
  }
  if (roleView.value === 'operator') {
    if (!String(currentRule.value.pushTargets || '').trim()) {
      return '该规则当前缺少推送对象，建议先补齐责任对象和接收角色，再交由管理员继续校正触发口径。'
    }
    if (matchRuleFocus(currentRule.value, 'timeoutShort')) {
      return '该规则当前超时时间偏短，建议先补说明材料和响应能力判断，再决定是否调整升级时效。'
    }
  }
  if (String(currentRule.value.warnLevel || '') === '3') {
    return '该规则当前属于红警规则，建议优先回看触发条件、升级级别和处理对象是否会阻断企业预警闭环。'
  }
  if (!String(currentRule.value.pushTargets || '').trim()) {
    return '该规则当前仍缺推送对象，建议先补齐企业管理员、财务经办和监管触达角色，再继续推进闭环。'
  }
  if (String(currentRule.value.ruleStatus || '') === '0') {
    return '该规则当前已停用，建议先确认停用原因和历史依赖，再决定是否继续恢复启用或保留停用。'
  }
  if (matchRuleFocus(currentRule.value, 'timeoutShort')) {
    return '该规则超时时间较短，建议确认是否会导致企业侧频繁超时升级。'
  }
  if (matchRuleFocus(currentRule.value, 'timeoutLong')) {
    return '该规则超时时间较长，建议继续复核是否会拖慢联动预警处置和归档。'
  }
  return '该规则当前台账信息较完整，建议先查看详情确认材料和来源模块，再决定是否继续联动其他业务页。'
})

const workflowSteps = computed(() => {
  if (roleView.value === 'finance') {
    return [
      { label: '先锁定财务来源规则', desc: '优先找出社保、税务和扩面减损来源规则，快速判断当前财务整改触达链是否完整。' },
      { label: '再校正规则对象与时效', desc: '把推送对象、超时分钟和升级级别真正回落到工资社税整改主链，而不是只留在规则层。' },
      { label: '最后回到闭环复核', desc: '在规则口径校正后，把处理结果沉淀到预警闭环、月报和后续归档。' }
    ]
  }
  if (roleView.value === 'hrss') {
    return [
      { label: '先锁定红警与停用规则', desc: '优先识别仍会影响区域监管闭环的高等级规则和停用规则。' },
      { label: '再承接监管触达复核', desc: '围绕推送对象、升级路径和停用原因持续复核，避免规则维护停在系统配置层。' },
      { label: '最后完成归档复核', desc: '把规则调整结果回落到月报、区域统计和后续监管底稿。' }
    ]
  }
  if (roleView.value === 'operator') {
    return [
      { label: '先补规则对象和说明', desc: '先把缺推送、停用说明和短时效规则对应的材料、截图和备注补齐。' },
      { label: '再回写触达留痕', desc: '把责任对象、处理链路和阶段结论回写到规则台账，避免规则无责任闭环。' },
      { label: '最后交接规则校正', desc: '在对象和留痕补齐后，再交由管理员或监管经办继续推进规则口径校正。' }
    ]
  }
  if (roleView.value === 'admin') {
    return [
      { label: '先建账并锁定重点规则', desc: '先形成规则台账，再锁定红警、财务来源和缺推送对象规则。' },
      { label: '承接企业规则维护主链', desc: '围绕推送对象、升级级别和超时分钟补口径、补说明和补留痕。' },
      { label: '完成闭环复核', desc: '把规则启停、升级和触达调整结果沉淀到预警闭环和月度归档。' }
    ]
  }
  return [
    { label: '先建规则主台账', desc: '先明确来源模块、预警级别和规则说明，形成联动预警规则主台账。' },
    { label: '维护推送对象', desc: '根据企业办理链和监管链配置推送目标，避免触达对象缺失或职责不清。' },
    { label: '校正升级策略', desc: '结合办理时效持续校正超时分钟和升级级别，防止规则过松或过紧。' },
    { label: '闭环复核归档', desc: '把规则启停和调整结果纳入预警闭环复核，支撑月报和规则复盘。' }
  ]
})

const detailFocusText = computed(() => {
  if (roleView.value === 'finance') {
    return '重点看是否已覆盖社保、税务和扩面减损责任对象，再决定是否进入社税整改链路或继续校正规则时效。'
  }
  if (roleView.value === 'hrss') {
    return '重点看红警等级、停用状态和推送对象是否完整，再决定是否进入区域监管督办或月报归档。'
  }
  if (roleView.value === 'operator') {
    return '重点看是否还缺推送对象、停用说明和时效备注，避免规则只停留在系统配置层。'
  }
  if (roleView.value === 'admin') {
    return '重点看规则是否真正串回企业办理、财务整改和预警闭环主链，再决定是否继续启停或调时效。'
  }
  return '重点看规则等级、来源模块、推送对象和超时升级是否匹配当前办理主链。'
})

const currentActionTags = computed(() => buildHintTags(currentRule.value, activeFocus.value?.key, roleView.value))
const detailHintTags = computed(() => buildHintTags(detailRule.value || currentRule.value, activeFocus.value?.key, roleView.value))
const pageHintTags = computed(() => buildPageHintTags())
const resolvedSummaryCards = computed(() => summaryCards.value.map((item, index) => ({
  ...item,
  note: resolveExplanationFirstText(item.note, resolvePortalExplanationItem(index))
})))
const resolvedFocusQueues = computed(() => focusQueues.value.map((item, index) => {
  const explanation = resolvePortalExplanationItem(index)
  return {
    ...item,
    desc: resolveExplanationFirstText(item.desc, explanation),
    actionText: resolveExplanationFirstActionText(item.actionText, explanation)
  }
}))
const resolvedWorkflowSteps = computed(() => workflowSteps.value.map((item, index) => {
  const explanation = resolvePortalExplanationItem(index)
  return {
    ...item,
    label: explanation?.dimensionName || explanation?.moduleLabel || explanation?.moduleCode || item.label,
    desc: resolveExplanationFirstText(item.desc, explanation)
  }
}))
const resolvedCurrentActionSummary = computed(() => resolveExplanationFirstText(
  currentActionSummary.value,
  leadingPortalExplanation.value
))
const resolvedCurrentActionTags = computed(() => buildExplanationFirstTags(currentActionTags.value))
const resolvedPageHintTags = computed(() => buildExplanationFirstTags(pageHintTags.value))
const resolvedDetailHintTags = computed(() => buildExplanationFirstTags(detailHintTags.value))
watchEffect(() => {
  setPageGuide({
    title: roleTitle.value || '预警规则办理台账',
    description: roleDescription.value || '统一查看当前页的门户解释、焦点对象、当前选中、办理路径与办理提示。',
    portalExplanation: portalExplanationItems.value,
    focus: resolvedFocusQueues.value,
    selection: [
      ...selectedRuleOverview.value,
      { label: '当前办理建议', value: resolvedCurrentActionSummary.value }
    ],
    workflow: resolvedWorkflowSteps.value,
    hints: [...resolvedCurrentActionTags.value, ...resolvedPageHintTags.value].slice(0, 6)
  })
})
const resolvedPrimaryAction = computed(() => ({
  ...primaryAction.value,
  label: resolveExplanationFirstActionText(primaryAction.value.label, leadingPortalExplanation.value)
}))
const resolvedSecondaryAction = computed(() => ({
  ...secondaryAction.value,
  label: resolveExplanationFirstActionText(secondaryAction.value.label, leadingPortalExplanation.value)
}))

function handlePrimaryAction() {
  if (primaryAction.value.action === 'add') {
    handleAdd()
    return
  }
  if (primaryAction.value.action === 'edit' && currentRule.value) {
    handleUpdate(currentRule.value)
    return
  }
  if (currentRule.value) {
    openDetail(currentRule.value)
  }
}

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
}

function clearWorkbenchContext() {
  Object.assign(queryParams.value, {
    pageNum: 1,
    pageSize: 10,
    ruleName: undefined,
    sourceModule: undefined,
    ruleStatus: undefined
  })
  activeFocusKey.value = ''
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

function openModule(path) {
  if (path) {
    router.push(path)
  }
}

function resolveWarningRuleFocusKey(value) {
  const normalized = String(value || '')
  return focusQueues.value.some(item => item.key === normalized) ? normalized : ''
}

function warningRuleFocusLabel(value) {
  return focusQueues.value.find(item => item.key === value)?.title || value || '-'
}

watch(focusQueues, () => {
  syncActiveFocus()
  syncCurrentRuleByFocus()
}, { immediate: true })

watch(visibleWarningRuleList, () => {
  syncCurrentRuleByFocus()
})

applyWorkbenchRouteQuery(route.query, queryParams.value, warningRuleWorkbenchFields)
activeFocusKey.value = resolveWarningRuleFocusKey(route.query.focusKey)
init()
</script>

<style scoped lang="scss">
.ygb-summary-grid,
.ygb-focus-grid {
  display: grid;
  gap: 16px;
  margin-bottom: 16px;
}

.ygb-summary-grid {
  grid-template-columns: repeat(4, minmax(0, 1fr));
}

.ygb-focus-grid {
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

.ygb-summary-card,
.ygb-panel-card {
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
  color: #7b8da1;
  font-size: 13px;
  font-weight: 500;
}

.ygb-summary-card__note {
  margin-top: 10px;
  color: #5f6f80;
  font-size: 13px;
  line-height: 1.7;
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

.ygb-panel-card :deep(.el-card__header) {
  padding: 18px 20px 0;
  border-bottom: 0;
}

.ygb-panel-card :deep(.el-card__body) {
  padding: 18px 20px 20px;
}

.ygb-card-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
}

.ygb-card-head--between {
  align-items: center;
}

.ygb-card-head__title {
  color: #13243a;
  font-size: 18px;
  font-weight: 700;
}

.ygb-card-head__desc {
  margin-top: 6px;
  color: #627486;
  font-size: 13px;
  line-height: 1.7;
}

.ygb-focus-list,
.ygb-pipeline-list {
  display: grid;
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

.ygb-source-list {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
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

.ygb-rule-form-grid {
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
  .ygb-summary-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 900px) {
  .ygb-focus-grid,
  .ygb-rule-form-grid,
  .ygb-source-list {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .ygb-summary-grid {
    grid-template-columns: 1fr;
  }

  .ygb-focus-queue {
    flex-direction: column;
  }

  .ygb-focus-queue__side {
    min-width: 0;
    align-items: flex-start;
  }
}
</style>

