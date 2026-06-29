<template>
  <div class="app-container ygb-page">
    <section class="gov-page-header ygb-page__header">
      <div>
        <p class="ygb-page__eyebrow">AI监测报告</p>
        <h1 class="ygb-page__title">{{ roleTitle }}</h1>
        <p class="ygb-page__desc">
          {{ roleDescription }}
          粤工保侧重点不是只看模型版本数量，而是把评分口径建账、启用切换、目标校准和闭环复核串成一条连续办理链。
        </p>
      </div>
      <div class="ygb-page__tip">
        <div class="ygb-page__tip-item">当前视角：{{ roleBadge }}</div>
        <div class="ygb-page__tip-item">{{ roleTip }}</div>
        <div class="ygb-page__tip-item">当前配置将同步影响 AI 报告、信用评分和预警解释口径，请在启用前完成校准复核。</div>
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

    <el-alert
      v-if="isReadOnlyRole"
      :title="`${readOnlyRoleLabel}：当前页面仅保留列表查看、详情和导出`"
      :description="readOnlyRoleDescription || '模型维护入口已自动隐藏。'"
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
        <el-tag v-for="item in workbenchContext.tags" :key="item.key" effect="plain">{{ item.label }}: {{ item.value }}</el-tag>
      </div>
    </el-alert>

    <el-card class="search-card ygb-search-card" shadow="never">
      <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
        <el-form-item label="区域">
          <el-select v-model="queryParams.regionCode" clearable style="width: 180px">
            <el-option v-for="item in regionOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.configStatus" clearable style="width: 160px">
            <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
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
          <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['ygb:aiReportConfig:add']">新增版本</el-button>
        </el-col>
        <el-col v-if="canEdit" :span="1.5">
          <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['ygb:aiReportConfig:edit']">修改版本</el-button>
        </el-col>
        <el-col v-if="canActivate" :span="1.5">
          <el-button type="warning" plain icon="Select" :disabled="!canActivateRow(currentConfigRow)" @click="handleActivate()" v-hasPermi="['ygb:aiReportConfig:activate']">启用版本</el-button>
        </el-col>
        <el-col v-if="canExport" :span="1.5">
          <el-button type="info" plain icon="Download" @click="handleExport" v-hasPermi="['ygb:aiReportConfig:export']">导出</el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>
    </el-card>

    <el-card class="table-card ygb-table-card" shadow="never">
      <template #header>
        <div class="ygb-card-head ygb-card-head--between">
          <div>
            <div class="ygb-card-head__title">AI 评分模型配置台账</div>
            <div class="ygb-card-head__desc">{{ focusTableHint }}</div>
          </div>
          <div class="ygb-card-head__desc">当前总量 {{ total }} 条</div>
        </div>
      </template>
      <el-table v-loading="loading" :data="visibleConfigList" @selection-change="handleSelectionChange" @row-click="handleRowClick">
        <el-table-column v-if="showSelection" type="selection" width="55" align="center" />
        <el-table-column label="配置ID" prop="configId" width="100" />
        <el-table-column label="区域" min-width="140">
          <template #default="scope">
            {{ scope.row.regionName || regionNameMap[scope.row.regionCode] || scope.row.regionCode }}
          </template>
        </el-table-column>
        <el-table-column label="版本" prop="version" width="150" />
        <el-table-column label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.configStatus === '1' ? 'success' : 'info'">{{ scope.row.configStatus === '1' ? '启用' : '停用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="生效日期" width="120">
          <template #default="scope">
            {{ parseTime(scope.row.effectiveDate, '{y}-{m}-{d}') }}
          </template>
        </el-table-column>
        <el-table-column label="权重概览" min-width="220" show-overflow-tooltip>
          <template #default="scope">
            {{ weightSummary(scope.row.dimensionWeights) }}
          </template>
        </el-table-column>
        <el-table-column label="目标概览" min-width="320" show-overflow-tooltip>
          <template #default="scope">
            {{ targetSummary(scope.row.targetValues) }}
          </template>
        </el-table-column>
        <el-table-column label="更新时间" width="180">
          <template #default="scope">
            {{ parseTime(scope.row.updateTime, '{y}-{m}-{d} {h}:{i}:{s}') }}
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
              v-hasPermi="['ygb:aiReportConfig:edit']"
            >
              修改
            </el-button>
            <el-button
              v-if="canActivate"
              link
              type="primary"
              icon="Select"
              :disabled="!canActivateRow(scope.row)"
              @click.stop="handleActivate(scope.row)"
              v-hasPermi="['ygb:aiReportConfig:activate']"
            >
              启用
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>

    <el-dialog :title="title" v-model="open" width="920px" append-to-body>
      <el-form ref="configRef" :model="form" :rules="rules" label-width="110px">
        <div class="ygb-config-form-grid">
          <el-form-item label="区域" prop="regionCode">
            <el-select v-model="form.regionCode" style="width: 100%">
              <el-option v-for="item in regionOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="版本" prop="version">
            <el-input v-model="form.version" placeholder="例如：M2026.06-GD" />
          </el-form-item>
          <el-form-item label="生效日期" prop="effectiveDate">
            <el-date-picker v-model="form.effectiveDate" type="date" value-format="YYYY-MM-DD" format="YYYY-MM-DD" style="width: 100%" />
          </el-form-item>
          <el-form-item label="状态" prop="configStatus">
            <el-select v-model="form.configStatus" style="width: 100%">
              <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
        </div>

        <div class="ygb-config-section">
          <h3>维度权重</h3>
          <div class="ygb-config-form-grid">
            <el-form-item label="A 合同备案" prop="weightA">
              <el-input-number v-model="form.weightA" :min="0" :max="100" :step="1" style="width: 100%" />
            </el-form-item>
            <el-form-item label="B 考勤归集" prop="weightB">
              <el-input-number v-model="form.weightB" :min="0" :max="100" :step="1" style="width: 100%" />
            </el-form-item>
            <el-form-item label="C 工资发放" prop="weightC">
              <el-input-number v-model="form.weightC" :min="0" :max="100" :step="1" style="width: 100%" />
            </el-form-item>
            <el-form-item label="D 设备工伤" prop="weightD">
              <el-input-number v-model="form.weightD" :min="0" :max="100" :step="1" style="width: 100%" />
            </el-form-item>
            <el-form-item label="E 预警闭环" prop="weightE">
              <el-input-number v-model="form.weightE" :min="0" :max="100" :step="1" style="width: 100%" />
            </el-form-item>
          </div>
        </div>

        <div class="ygb-config-section">
          <h3>目标值</h3>
          <div class="ygb-config-form-grid">
            <el-form-item label="合同备案率" prop="contractRate">
              <el-input-number v-model="form.contractRate" :min="0" :max="100" :step="1" style="width: 100%" />
            </el-form-item>
            <el-form-item label="考勤通过率" prop="attendanceRate">
              <el-input-number v-model="form.attendanceRate" :min="0" :max="100" :step="1" style="width: 100%" />
            </el-form-item>
            <el-form-item label="工资成功率" prop="paySuccessRate">
              <el-input-number v-model="form.paySuccessRate" :min="0" :max="100" :step="1" style="width: 100%" />
            </el-form-item>
            <el-form-item label="设备在线率" prop="onlineRate">
              <el-input-number v-model="form.onlineRate" :min="0" :max="100" :step="1" style="width: 100%" />
            </el-form-item>
            <el-form-item label="工伤发生率" prop="injuryRate">
              <el-input-number v-model="form.injuryRate" :min="0" :max="100" :step="0.1" :precision="1" style="width: 100%" />
            </el-form-item>
            <el-form-item label="预警闭环率" prop="warningCloseRate">
              <el-input-number v-model="form.warningCloseRate" :min="0" :max="100" :step="1" style="width: 100%" />
            </el-form-item>
          </div>
        </div>

        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入模型说明" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确定</el-button>
          <el-button @click="cancel">取消</el-button>
        </div>
      </template>
    </el-dialog>

    <page-detail-dialog v-model="detailOpen" title="模型配置详情" width="760px">
      <template v-if="detailConfig">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="区域">{{ detailConfig.regionName || regionNameMap[detailConfig.regionCode] || detailConfig.regionCode }}</el-descriptions-item>
          <el-descriptions-item label="版本">{{ detailConfig.version || '-' }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="detailConfig.configStatus === '1' ? 'success' : 'info'">{{ detailConfig.configStatus === '1' ? '启用' : '停用' }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="生效日期">{{ parseTime(detailConfig.effectiveDate, '{y}-{m}-{d}') }}</el-descriptions-item>
          <el-descriptions-item label="权重概览" :span="2">{{ weightSummary(detailConfig.dimensionWeights) }}</el-descriptions-item>
          <el-descriptions-item label="目标概览" :span="2">{{ targetSummary(detailConfig.targetValues) }}</el-descriptions-item>
          <el-descriptions-item label="当前视角重点" :span="2">{{ detailFocusText }}</el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ detailConfig.remark || '-' }}</el-descriptions-item>
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

<script setup name="YgbAiReportConfig">
import { computed, getCurrentInstance, ref, watch, watchEffect } from 'vue'
import { useWorkbenchAssist } from '@/composables/useWorkbenchAssist'
import { useRoute, useRouter } from 'vue-router'
import useUserStore from '@/store/modules/user'
import { decoratePortalExplanationItems, openPortalExplanationAction } from '@/utils/portalExplanation'
import { useRoleViewMode } from '@/utils/roleView'
import { applyWorkbenchRouteQuery, buildWorkbenchContext, stripWorkbenchRouteQuery } from '@/utils/workbenchLink'
import {
  buildHintTags,
  formatPercent,
  matchConfigFocus,
  parseJson,
  prioritizeFocusRows,
  regionNameMap,
  useAiReportConfigPage,
  valueOrDefault,
  weightSummary,
  targetSummary
} from '@/views/aiReportConfig/useAiReportConfigPage'

const route = useRoute()
const router = useRouter()
const { setPageGuide } = useWorkbenchAssist()
const userStore = useUserStore()
const { isReadOnlyRole, readOnlyRoleLabel, readOnlyRoleDescription } = useRoleViewMode()
const activeFocusKey = ref('')
const { proxy } = getCurrentInstance()
const aiReportConfigWorkbenchFields = ['regionCode', 'focusKey']
const aiReportConfigInitialQuery = {}

applyWorkbenchRouteQuery(route.query, aiReportConfigInitialQuery, aiReportConfigWorkbenchFields)

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

function summaryCard(key, label, value, unit, note, cardClass = '') {
  return { key, label, value, unit, note, cardClass }
}

function focusQueue(key, title, count, unit, desc, actionText) {
  return { key, title, count, unit, desc, actionText }
}

function targetValues(config) {
  return parseJson(config?.targetValues)
}

function weightValues(config) {
  return parseJson(config?.dimensionWeights)
}

function weightTotal(config) {
  const weights = weightValues(config)
  return Number(weights.A || 0) + Number(weights.B || 0) + Number(weights.C || 0) + Number(weights.D || 0) + Number(weights.E || 0)
}

function canActivateRow(config) {
  return Boolean(config?.configId) && config.configStatus !== '1' && weightTotal(config) === 100
}

function countRows(predicate) {
  return configList.value.filter(predicate).length
}

function matchConfigRow(config, focusKey) {
  if (!config || !focusKey) {
    return false
  }
  if (matchConfigFocus(config, focusKey)) {
    return true
  }
  const targets = targetValues(config)
  if (focusKey === 'payTarget') {
    return Number(targets.paySuccessRate || 0) > 0 && Number(targets.paySuccessRate || 0) < 98
  }
  if (focusKey === 'attendanceTarget') {
    return Number(targets.attendanceRate || 0) > 0 && Number(targets.attendanceRate || 0) < 95
  }
  if (focusKey === 'onlineTarget') {
    return Number(targets.onlineRate || 0) > 0 && Number(targets.onlineRate || 0) < 95
  }
  if (focusKey === 'injuryTarget') {
    return Number(targets.injuryRate || 0) > 2.5
  }
  return false
}

function uniqueTags(tags = []) {
  const deduped = new Map()
  tags.forEach(tag => {
    if (tag?.label && !deduped.has(tag.label)) {
      deduped.set(tag.label, tag)
    }
  })
  return [...deduped.values()]
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
const canAdd = computed(() => !isReadOnlyRole.value && hasPermission(permissions.value, ['ygb:aiReportConfig:add']))
const canEdit = computed(() => !isReadOnlyRole.value && hasPermission(permissions.value, ['ygb:aiReportConfig:edit']))
const canActivate = computed(() => !isReadOnlyRole.value && hasPermission(permissions.value, ['ygb:aiReportConfig:activate']))
const canExport = computed(() => hasPermission(permissions.value, ['ygb:aiReportConfig:export']))
const showSelection = computed(() => canEdit.value || canActivate.value)
const tableActionWidth = computed(() => (canEdit.value || canActivate.value ? 220 : 90))

const {
  regionOptions,
  statusOptions,
  configList,
  loading,
  showSearch,
  open,
  detailOpen,
  total,
  single,
  title,
  currentConfigRow,
  detailConfig,
  summaryData,
  queryParams,
  form,
  rules,
  getList,
  handleQuery,
  resetQuery: pageResetQuery,
  handleSelectionChange,
  handleRowClick,
  cancel,
  handleAdd,
  handleUpdate,
  openDetail,
  handleActivate,
  submitForm,
  handleExport,
  syncCurrentConfig
} = useAiReportConfigPage({
  exportFilePrefix: 'ygb_ai_report_config',
  canMutate: () => !isReadOnlyRole.value,
  onBlockedAction(actionLabel) {
    const roleLabel = readOnlyRoleLabel.value || '当前角色'
    proxy?.$modal?.msgWarning?.(`${roleLabel}不能${actionLabel}`)
  },
  getCurrentList: () => visibleConfigList.value,
  afterList: () => {
    syncActiveFocus()
  },
  immediate: false
})

Object.assign(queryParams.value, aiReportConfigInitialQuery)

function syncActiveFocus() {
  if (!activeFocusKey.value || !focusQueues.value.some(item => item.key === activeFocusKey.value)) {
    activeFocusKey.value = focusQueues.value[0]?.key || ''
  }
}

function handleSelectFocus(item) {
  activeFocusKey.value = item.key
  syncCurrentConfig()
}

function buildConfigHintTags(config, focus, view) {
  const tags = [...buildHintTags(config)]
  if (focus?.title) {
    tags.unshift({ label: `当前焦点：${focus.title}`, type: 'info' })
  }
  if (view === 'finance') {
    tags.push({ label: '财务视角优先核对工资成功率、考勤通过率与联动月报是否同口径。', type: 'info' })
  }
  if (view === 'hrss') {
    tags.push({ label: '人社视角优先核对闭环率、工伤发生率和区域覆盖是否支撑监管解释。', type: 'info' })
  }
  if (view === 'operator') {
    tags.push({ label: '企业经办视角优先补齐版本命名、备注说明和待启用版本留痕。', type: 'info' })
  }
  if (view === 'admin') {
    tags.push({ label: '企业管理员视角优先保证权重总和、目标阈值和启用版本不阻断信用与报告主链。', type: 'info' })
  }
  return uniqueTags(tags)
}

const roleBadge = computed(() => {
  if (roleView.value === 'hrss') return '人社监管视角'
  if (roleView.value === 'operator') return '企业经办视角'
  if (roleView.value === 'finance') return '财务经办视角'
  if (roleView.value === 'admin') return '企业管理员视角'
  return '综合办理视角'
})

const roleTitle = computed(() => {
  if (roleView.value === 'hrss') return '区域评分口径复核与监管台账'
  if (roleView.value === 'operator') return '模型版本补齐与启用准备台账'
  if (roleView.value === 'finance') return '财务评分口径回查与联动台账'
  if (roleView.value === 'admin') return 'AI 评分模型闭环工作台'
  return 'AI 评分模型配置办理台账'
})

const roleDescription = computed(() => {
  if (roleView.value === 'hrss') {
    return '面向人社监管经办统一查看区域评分模型配置结果，重点是把闭环率、工伤发生率和区域覆盖口径回落到监管复核、区域对比和月报解释链路。'
  }
  if (roleView.value === 'operator') {
    return '面向企业经办统一跟进模型配置结果，重点是先补齐待启用版本、版本命名和备注留痕，再交由管理员完成启用切换。'
  }
  if (roleView.value === 'finance') {
    return '面向财务经办统一查看模型目标值，重点是把工资成功率、考勤通过率和联动月报目标直接串回工资、社保税务和信用评分链路。'
  }
  if (roleView.value === 'admin') {
    return '面向企业管理员统一查看模型配置结果，重点是把版本建账、目标校准、启用切换和后续 AI 报告、信用评分闭环串成一条主链。'
  }
  return '面向企业管理员、财务和监管经办统一查看 AI 评分模型配置结果。'
})

const roleTip = computed(() => {
  if (roleView.value === 'hrss') {
    return '优先锁定闭环率偏低、工伤发生率阈值偏松和区域覆盖不足的版本，再决定是否进入监管复核。'
  }
  if (roleView.value === 'operator') {
    return '优先补齐待启用版本和模型说明，不让模型配置页停留在只有表格的静态维护。'
  }
  if (roleView.value === 'finance') {
    return '优先回看工资成功率、考勤通过率和联动月报目标，减少财务在工资、个税、社保和 AI 报告之间来回切换。'
  }
  if (roleView.value === 'admin') {
    return '优先统筹会影响信用评分和 AI 报告解释口径的关键版本，再决定是修改还是启用切换。'
  }
  return '后续如拆分独立模型服务，仍以本台账作为统一配置入口。'
})

const summaryCards = computed(() => {
  if (roleView.value === 'finance') {
    return [
      summaryCard('active', '启用版本', valueOrDefault(summaryData.value.activeCount, 0), '个', '当前直接服务工资、社保税务和信用评分链路的版本数量。', 'ygb-summary-card--success'),
      summaryCard('pay', '工资目标偏低', countRows(row => matchConfigRow(row, 'payTarget')), '个', '工资成功率目标低于 98% 的版本，建议优先回查口径。', 'ygb-summary-card--warning'),
      summaryCard('attendance', '考勤目标偏低', countRows(row => matchConfigRow(row, 'attendanceTarget')), '个', '考勤通过率目标低于 95% 的版本，易影响月报解释。'),
      summaryCard('upcoming', '待切换版本', valueOrDefault(summaryData.value.upcomingCount, 0), '个', '适合作为财务联动月报切换前的储备版本。', 'ygb-summary-card--primary')
    ]
  }
  if (roleView.value === 'hrss') {
    return [
      summaryCard('covered', '覆盖区域', valueOrDefault(summaryData.value.coveredRegionCount, 0), '个', '已配置评分模型的区域数量，应与监管覆盖范围一致。', 'ygb-summary-card--primary'),
      summaryCard('closeRate', '闭环目标偏低', countRows(row => matchConfigRow(row, 'closeRate')), '个', '闭环率目标低于 90% 的版本，建议优先复核。', 'ygb-summary-card--warning'),
      summaryCard('injury', '工伤阈值偏松', countRows(row => matchConfigRow(row, 'injuryTarget')), '个', '工伤发生率目标高于 2.5 的版本，影响监管解释口径。'),
      summaryCard('active', '启用版本', valueOrDefault(summaryData.value.activeCount, 0), '个', '当前实际用于区域监管解释的版本数量。', 'ygb-summary-card--success')
    ]
  }
  if (roleView.value === 'operator') {
    return [
      summaryCard('upcoming', '待启用版本', valueOrDefault(summaryData.value.upcomingCount, 0), '个', '企业经办应优先补齐说明和切换准备的版本。', 'ygb-summary-card--warning'),
      summaryCard('weight', '权重待校准', countRows(row => matchConfigRow(row, 'weight')), '个', '权重总和不为 100 的版本，不能直接进入正式启用。'),
      summaryCard('active', '启用版本', valueOrDefault(summaryData.value.activeCount, 0), '个', '当前仍在服务 AI 报告与信用评分的版本。', 'ygb-summary-card--success'),
      summaryCard('covered', '覆盖区域', valueOrDefault(summaryData.value.coveredRegionCount, 0), '个', '经办补录时需确认是否已覆盖当前业务区域。', 'ygb-summary-card--primary')
    ]
  }
  if (roleView.value === 'admin') {
    return [
      summaryCard('total', '配置版本', valueOrDefault(summaryData.value.totalCount, total.value), '个', '当前筛选条件下可见的评分模型配置版本总量。'),
      summaryCard('active', '启用版本', valueOrDefault(summaryData.value.activeCount, 0), '个', '当前直接作用于 AI 报告和信用评分的版本。', 'ygb-summary-card--success'),
      summaryCard('closeRate', '闭环目标偏低', countRows(row => matchConfigRow(row, 'closeRate')), '个', '会拖慢预警闭环解释口径的版本。', 'ygb-summary-card--warning'),
      summaryCard('weight', '权重待校准', countRows(row => matchConfigRow(row, 'weight')), '个', '权重总和不为 100 的版本，建议优先调整。', 'ygb-summary-card--primary')
    ]
  }
  return [
    summaryCard('total', '配置版本', valueOrDefault(summaryData.value.totalCount, total.value), '个', '当前筛选条件下可见的评分模型配置版本总量。'),
    summaryCard('active', '启用版本', valueOrDefault(summaryData.value.activeCount, 0), '个', '当前实际生效的模型配置版本数量。', 'ygb-summary-card--success'),
    summaryCard('upcoming', '待启用版本', valueOrDefault(summaryData.value.upcomingCount, 0), '个', '后续切换前可继续补齐说明和备注的版本。', 'ygb-summary-card--warning'),
    summaryCard('covered', '覆盖区域', valueOrDefault(summaryData.value.coveredRegionCount, 0), '个', '已建立评分模型配置的区域数量。', 'ygb-summary-card--primary')
  ]
})

const focusQueues = computed(() => {
  if (roleView.value === 'finance') {
    return [
      focusQueue('payTarget', '工资成功率目标偏低', countRows(row => matchConfigRow(row, 'payTarget')), '个', '优先回查工资发放目标是否与工资台账、月报和信用评分保持一致。', '回看工资口径'),
      focusQueue('attendanceTarget', '考勤通过率目标偏低', countRows(row => matchConfigRow(row, 'attendanceTarget')), '个', '适合优先对照考勤归集和个税比对结果复核模型目标。', '回看考勤口径'),
      focusQueue('upcoming', '待启用版本', valueOrDefault(summaryData.value.upcomingCount, 0), '个', '在财务联动月报切换前先补齐目标和备注说明。', '准备启用版本')
    ]
  }
  if (roleView.value === 'hrss') {
    return [
      focusQueue('closeRate', '闭环率目标偏低', countRows(row => matchConfigRow(row, 'closeRate')), '个', '优先回查闭环率阈值是否过松，避免弱化监管解释。', '进入监管复核'),
      focusQueue('injuryTarget', '工伤发生率阈值偏松', countRows(row => matchConfigRow(row, 'injuryTarget')), '个', '适合联动工伤事件、AI 报告和统计月报一起复核。', '回看工伤口径'),
      focusQueue('covered', '区域覆盖待补齐', Math.max(0, regionOptions.value.length - valueOrDefault(summaryData.value.coveredRegionCount, 0)), '个', '先补齐区域口径，再进入监管月报解释链路。', '补齐区域覆盖')
    ]
  }
  if (roleView.value === 'operator') {
    return [
      focusQueue('upcoming', '待启用版本', valueOrDefault(summaryData.value.upcomingCount, 0), '个', '优先补齐版本说明、生效日期和备注留痕。', '补齐待启用版本'),
      focusQueue('weight', '权重待校准', countRows(row => matchConfigRow(row, 'weight')), '个', '权重总和不为 100 的版本应先回写修正。', '校准权重'),
      focusQueue('active', '当前启用版本', valueOrDefault(summaryData.value.activeCount, 0), '个', '用于比对当前模型口径与正在生成的 AI 报告是否一致。', '回看启用版本')
    ]
  }
  if (roleView.value === 'admin') {
    return [
      focusQueue('weight', '权重待校准', countRows(row => matchConfigRow(row, 'weight')), '个', '优先处理会直接扭曲评分口径的版本。', '校准模型权重'),
      focusQueue('closeRate', '闭环目标偏低', countRows(row => matchConfigRow(row, 'closeRate')), '个', '适合作为预警闭环、AI 报告和信用评分联动复核入口。', '回看闭环目标'),
      focusQueue('upcoming', '待启用版本', valueOrDefault(summaryData.value.upcomingCount, 0), '个', '在模型切换前先完成版本核对和启用准备。', '准备启用切换')
    ]
  }
  return [
    focusQueue('active', '当前启用版本', valueOrDefault(summaryData.value.activeCount, 0), '个', '优先确认正在服务业务的模型口径是否稳定。', '查看启用版本'),
    focusQueue('upcoming', '待启用版本', valueOrDefault(summaryData.value.upcomingCount, 0), '个', '适合先补齐说明和启用准备。', '查看待启用版本'),
    focusQueue('closeRate', '闭环目标偏低', countRows(row => matchConfigRow(row, 'closeRate')), '个', '适合作为联动预警闭环复核入口。', '回看闭环目标')
  ]
})

function buildAiReportConfigExplanationQuery(extraQuery = {}) {
  return {
    regionCode: queryParams.value.regionCode,
    configStatus: queryParams.value.configStatus,
    ...extraQuery
  }
}

const fallbackPortalExplanations = computed(() => focusQueues.value.map(item => ({
  key: item.key,
  dimensionName: item.title,
  currentValue: item.count,
  targetValue: '-',
  summary: item.desc,
  evidenceModule: 'aiReportConfig',
  recommendModule: 'aiReportConfig',
  defaultQuery: buildAiReportConfigExplanationQuery({ focusKey: item.key }),
  sourceLabel: '530.1 AI 配置办理解释',
  sourceDescription: item.desc,
  actionText: item.actionText
})))

const portalExplanations = computed(() => {
  const aggregated = Array.isArray(summaryData.value.ygbExplanation) ? summaryData.value.ygbExplanation : []
  if (aggregated.length) {
    const focusKeys = new Set(focusQueues.value.map(item => item.key))
    const filtered = aggregated.filter(item => focusKeys.has(item.key))
    return filtered.length ? filtered : aggregated
  }
  return fallbackPortalExplanations.value
})

const portalExplanationItems = computed(() => decoratePortalExplanationItems(portalExplanations.value, {
  portalCode: 'ygb',
  panelTitle: '530.1 AI 配置办理解释',
  panelDescription: '当前解释项按 530.1 办理链口径展示 AI 报告配置、生效范围和校准重点。'
}))

const activeFocus = computed(() => {
  if (!focusQueues.value.length) {
    return null
  }
  return focusQueues.value.find(item => item.key === activeFocusKey.value) || focusQueues.value[0]
})

const visibleConfigList = computed(() => prioritizeFocusRows(configList.value, row => matchConfigRow(row, activeFocus.value?.key)))

const workbenchContext = computed(() => buildWorkbenchContext(route.query, {
  fields: aiReportConfigWorkbenchFields,
  title: '当前 AI 配置页沿用了上游来源条件。',
  description: '当前页面已带入工作台筛选条件，可继续处理配置校准和生效范围。',
  fieldLabels: {
    regionCode: 'Region',
    focusKey: 'Focus'
  },
  fieldFormatters: {
    regionCode: value => regionNameMap[value] || value,
    focusKey: value => aiReportConfigFocusLabel(value)
  }
}))

watch(focusQueues, () => {
  syncActiveFocus()
  syncCurrentConfig()
}, { immediate: true })

const focusTableHint = computed(() => {
  if (!activeFocus.value) {
    return '按当前筛选条件展示 AI 评分模型配置台账。'
  }
  return `当前焦点为“${activeFocus.value.title}”，已把对应重点版本优先排到表格前列。`
})

const selectedConfigOverview = computed(() => {
  if (!currentConfigRow.value) {
    return [
      { label: '当前焦点', value: activeFocus.value?.title || '-' },
      { label: '焦点数量', value: `${activeFocus.value?.count ?? 0}${activeFocus.value?.unit || ''}` },
      { label: '当前区域', value: regionNameMap[queryParams.value.regionCode] || queryParams.value.regionCode || '全部区域' },
      { label: '启用版本号', value: summaryData.value.activeVersion || '-' }
    ]
  }
  const targets = targetValues(currentConfigRow.value)
  const focusMetric = roleView.value === 'finance'
    ? `${targets.paySuccessRate ?? '-'} %`
    : roleView.value === 'hrss'
      ? `${targets.warningCloseRate ?? '-'} %`
      : `${weightTotal(currentConfigRow.value)}`
  const focusMetricLabel = roleView.value === 'finance'
    ? '工资成功率'
    : roleView.value === 'hrss'
      ? '预警闭环率'
      : '权重总和'
  return [
    { label: '区域', value: currentConfigRow.value.regionName || regionNameMap[currentConfigRow.value.regionCode] || currentConfigRow.value.regionCode || '-' },
    { label: '版本', value: currentConfigRow.value.version || '-' },
    { label: '状态', value: currentConfigRow.value.configStatus === '1' ? '启用' : '停用' },
    { label: focusMetricLabel, value: focusMetric }
  ]
})

const primaryConfigAction = computed(() => {
  if (!currentConfigRow.value) {
    if (canAdd.value) {
      return { label: '新增版本', action: 'add' }
    }
    return { label: '查看详情', action: 'detail' }
  }
  if (canActivate.value && canActivateRow(currentConfigRow.value)) {
    return { label: '启用版本', action: 'activate' }
  }
  if (canEdit.value && (roleView.value === 'finance' || roleView.value === 'hrss')) {
    return { label: '修改并复核', action: 'edit' }
  }
  if (canEdit.value) {
    return { label: '修改版本', action: 'edit' }
  }
  return { label: '查看详情', action: 'detail' }
})

const secondaryAction = computed(() => {
  if (activeFocus.value?.key === 'closeRate' || roleView.value === 'hrss') {
    return { label: '进入预警中心', path: '/warning-center/workOrder' }
  }
  if (activeFocus.value?.key === 'payTarget' || activeFocus.value?.key === 'attendanceTarget' || roleView.value === 'finance') {
    return { label: '进入个税比对', path: '/tax-supervision/personalTax' }
  }
  if (activeFocus.value?.key === 'weight') {
    return { label: '进入信用评价', path: '/credit-evaluation/overview' }
  }
  return { label: '进入 AI 报告', path: '/ai-report/report' }
})

const currentConfigActionSummary = computed(() => {
  if (!currentConfigRow.value) {
    return activeFocus.value
      ? `当前已按“${activeFocus.value.title}”重排台账，建议先处理表格前列版本，再决定是修改、启用还是联动其他模块。`
      : '当前暂无模型配置对象。'
  }

  const targets = targetValues(currentConfigRow.value)
  const totalWeight = weightTotal(currentConfigRow.value)

  if (roleView.value === 'finance') {
    if (matchConfigRow(currentConfigRow.value, 'payTarget') || matchConfigRow(currentConfigRow.value, 'attendanceTarget')) {
      return '该版本更适合先回查工资成功率、考勤通过率和个税、社保联动口径，再决定是否继续启用。'
    }
    return '该版本财务目标相对稳定，建议先核对联动月报和信用评分解释是否仍沿用同一套口径。'
  }
  if (roleView.value === 'hrss') {
    if (matchConfigRow(currentConfigRow.value, 'closeRate') || matchConfigRow(currentConfigRow.value, 'injuryTarget')) {
      return '该版本已进入监管复核重点范围，建议先核对闭环率和工伤发生率目标，再决定是否继续沿用区域解释口径。'
    }
    return '该版本监管口径相对稳定，建议先查看详情确认区域覆盖和备注说明是否完整。'
  }
  if (roleView.value === 'operator') {
    if (currentConfigRow.value.configStatus !== '1') {
      return '该版本当前更适合先补齐版本说明、生效日期和备注留痕，再交由管理员启用切换。'
    }
    return '该版本已经启用，建议先核对是否仍需补充备注和业务留痕，再回看 AI 报告结果。'
  }
  if (totalWeight !== 100) {
    return `该版本当前权重总和为 ${totalWeight}，建议先校准到 100，再决定是否继续用于信用评分和 AI 报告。`
  }
  if (currentConfigRow.value.configStatus !== '1') {
    return '该版本尚未启用，建议先核对目标阈值和适用范围，再执行启用切换。'
  }
  return '该版本当前已处于启用状态，建议先回看 AI 报告、信用评分和预警闭环解释口径是否仍保持一致。'
})

const currentConfigActionTags = computed(() => buildConfigHintTags(currentConfigRow.value, activeFocus.value, roleView.value))

const workflowSteps = computed(() => {
  if (roleView.value === 'finance') {
    return [
      { label: '先看财务口径', desc: '优先锁定工资成功率、考勤通过率和联动月报目标，快速判断财务链路是否需要调整。' },
      { label: '再核对联动模块', desc: '把目标值回落到工资、个税、社保和信用评分台账，避免模型配置和业务解释脱节。' },
      { label: '最后确认启用', desc: '在目标校准和留痕补齐后，再决定是否切换启用版本。' }
    ]
  }
  if (roleView.value === 'hrss') {
    return [
      { label: '先看监管阈值', desc: '先看闭环率、工伤发生率和区域覆盖，快速判断监管解释是否存在短板。' },
      { label: '再联动报告与月报', desc: '围绕 AI 报告、预警闭环和统计月报确认阈值是否真实支撑监管链路。' },
      { label: '最后完成复核留档', desc: '把复核结论沉淀到区域监管和月报解释口径中。' }
    ]
  }
  if (roleView.value === 'operator') {
    return [
      { label: '先补待启用版本', desc: '先把待启用版本的命名、生效日期和备注说明补齐。' },
      { label: '再校准权重目标', desc: '核对权重总和和目标阈值，避免管理员启用后再返工。' },
      { label: '最后交接启用', desc: '在说明和留痕完整后，再交由管理员完成启用切换。' }
    ]
  }
  if (roleView.value === 'admin') {
    return [
      { label: '先统筹关键版本', desc: '先判断哪些版本会直接影响信用评分、AI 报告和预警闭环解释。' },
      { label: '再校准权重目标', desc: '围绕权重总和、工资成功率和闭环率阈值处理关键短板。' },
      { label: '执行启用切换', desc: '在版本说明、目标阈值和适用区域确认后完成启用切换。' },
      { label: '回看联动结果', desc: '最后回到 AI 报告和信用评分页面确认口径已同步生效。' }
    ]
  }
  return [
    { label: '建立版本台账', desc: '先明确区域、版本号、生效日期和状态，形成可追溯的模型配置主台账。' },
    { label: '维护权重目标', desc: '按合同、考勤、工资、设备工伤和预警闭环维护权重与目标值。' },
    { label: '切换启用版本', desc: '在确认配置完整后切换启用版本，避免 AI 报告沿用过期口径。' },
    { label: '复核归档', desc: '把启用结论和备注说明纳入月度复核与后续解释链路。' }
  ]
})

const pageHintTags = computed(() => buildConfigHintTags(currentConfigRow.value, activeFocus.value, roleView.value))
const detailHintTags = computed(() => buildConfigHintTags(detailConfig.value || currentConfigRow.value, activeFocus.value, roleView.value))

watchEffect(() => {
  setPageGuide({
    title: roleTitle.value || 'AI 评分模型配置台账',
    description: roleDescription.value || '统一查看当前页的门户解释、焦点对象、当前选中、办理路径与办理提示。',
    portalExplanation: portalExplanationItems.value,
    focus: focusQueues.value,
    selection: [
      ...selectedConfigOverview.value,
      { label: '当前办理建议', value: currentConfigActionSummary.value }
    ],
    workflow: workflowSteps.value,
    hints: [...currentConfigActionTags.value, ...pageHintTags.value].slice(0, 6)
  })
})

const detailFocusText = computed(() => {
  if (roleView.value === 'finance') {
    return '重点看工资成功率、考勤通过率和联动月报目标是否足以支撑财务回查与信用解释。'
  }
  if (roleView.value === 'hrss') {
    return '重点看闭环率、工伤发生率和区域覆盖是否支撑监管复核与区域月报解释。'
  }
  if (roleView.value === 'operator') {
    return '重点看待启用版本的命名、备注和生效日期是否已经补齐，避免管理员启用后返工。'
  }
  if (roleView.value === 'admin') {
    return '重点看哪些版本仍会阻断 AI 报告、信用评分和预警闭环的统一口径，再把整改动作回落到主链。'
  }
  return '重点看版本状态、目标阈值和权重总和是否仍需继续回查业务主链。'
})

function handlePrimaryConfigAction() {
  if (primaryConfigAction.value.action === 'add') {
    handleAdd()
    return
  }
  if (!currentConfigRow.value) {
    return
  }
  if (primaryConfigAction.value.action === 'activate') {
    if (!canActivateRow(currentConfigRow.value)) {
      proxy.$modal.msgWarning('当前版本需停用状态且权重总和为100后才能启用')
      return
    }
    handleActivate(currentConfigRow.value)
    return
  }
  if (primaryConfigAction.value.action === 'edit') {
    handleUpdate(currentConfigRow.value)
    return
  }
  openDetail(currentConfigRow.value)
}

function handlePortalExplanationAction(action) {
  if (action?.path === route.path) {
    applyAiReportConfigWorkbenchQuery(action.query || {})
  }
  openPortalExplanationAction(router, action)
}

function resetQuery() {
  proxy.resetForm('queryRef')
  Object.assign(queryParams.value, {
    pageNum: 1,
    pageSize: 10,
    regionCode: undefined,
    configStatus: undefined
  })
  applyWorkbenchRouteQuery(route.query, queryParams.value, aiReportConfigWorkbenchFields)
  activeFocusKey.value = resolveAiReportConfigFocusKey(route.query.focusKey)
  getList()
}

function clearWorkbenchContext() {
  activeFocusKey.value = ''
  Object.assign(queryParams.value, {
    pageNum: 1,
    pageSize: 10,
    regionCode: undefined,
    configStatus: undefined
  })
  router.replace({
    path: route.path,
    query: stripWorkbenchRouteQuery(route.query, aiReportConfigWorkbenchFields)
  })
  getList()
}

function applyAiReportConfigWorkbenchQuery(routeQuery = {}) {
  Object.assign(queryParams.value, {
    pageNum: 1,
    pageSize: 10,
    regionCode: undefined,
    configStatus: undefined
  })
  applyWorkbenchRouteQuery(routeQuery, queryParams.value, aiReportConfigWorkbenchFields)
  activeFocusKey.value = resolveAiReportConfigFocusKey(routeQuery.focusKey)
  syncCurrentConfig()
  getList()
}

function openModule(path) {
  if (!path) {
    return
  }
  router.push(path)
}

function resolveAiReportConfigFocusKey(value) {
  const normalized = String(value || '')
  return focusQueues.value.some(item => item.key === normalized) ? normalized : ''
}

function aiReportConfigFocusLabel(value) {
  return focusQueues.value.find(item => item.key === value)?.title || value || '-'
}

activeFocusKey.value = resolveAiReportConfigFocusKey(route.query.focusKey)
getList()
</script>

<style scoped lang="scss">
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

.ygb-page__tip {
  display: grid;
  gap: 10px;
  padding: 18px 20px;
  min-width: 320px;
  max-width: 420px;
  border-radius: 18px;
  background: linear-gradient(180deg, #f8fbff 0%, #eef5fb 100%);
  border: 1px solid #d7e5f5;
}

.ygb-page__tip-item {
  color: #41556d;
  font-size: 13px;
  line-height: 1.7;
}

.ygb-focus-card :deep(.el-card__header) {
  padding: 18px 20px 0;
  border-bottom: none;
}

.ygb-focus-card :deep(.el-card__body) {
  padding: 18px 20px 20px;
}

.ygb-card-head--between {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
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

.ygb-focus-queue {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  width: 100%;
  padding: 16px;
  border: 1px solid #dbe5f0;
  border-radius: 14px;
  background: #f8fbfd;
  text-align: left;
  cursor: pointer;
  transition: all 0.2s ease;
}

.ygb-focus-queue:hover,
.ygb-focus-queue.is-active {
  border-color: #93b7df;
  background: #eef5fb;
}

.ygb-focus-queue__main strong {
  color: #13243a;
  font-size: 15px;
}

.ygb-focus-queue__main p {
  margin: 8px 0 0;
  color: #5f6f80;
  font-size: 13px;
  line-height: 1.7;
}

.ygb-focus-queue__side {
  display: grid;
  gap: 6px;
  justify-items: end;
  min-width: 120px;
}

.ygb-focus-queue__count {
  color: #0f5ea8;
  font-size: 18px;
  font-weight: 700;
}

.ygb-focus-queue__action {
  color: #6c8298;
  font-size: 12px;
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

.ygb-recommend-panel {
  margin-top: 14px;
  padding: 14px 16px;
  border-radius: 14px;
  background: #f5f9fd;
  border: 1px solid #dbe7f4;
}

.ygb-recommend-panel__title {
  color: #2f4057;
  font-size: 14px;
  font-weight: 700;
}

.ygb-recommend-panel__summary {
  margin-top: 8px;
  color: #53677c;
  font-size: 13px;
  line-height: 1.8;
}

.ygb-focus-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 14px;
}

.ygb-tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.ygb-config-form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0 16px;
}

.ygb-config-section {
  margin-bottom: 12px;
}

.ygb-config-section h3,
.ygb-detail-block h3 {
  margin: 0 0 12px;
  font-size: 15px;
  color: #2f4057;
}

.ygb-detail-block {
  margin-top: 20px;
}

@media (max-width: 1200px) {
  .ygb-summary-grid,
  .ygb-focus-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 900px) {
  .ygb-config-form-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .ygb-summary-grid,
  .ygb-focus-grid {
    grid-template-columns: 1fr;
  }

  .ygb-focus-queue {
    align-items: flex-start;
  }

  .ygb-focus-queue__side {
    justify-items: start;
  }

  .ygb-card-head--between {
    display: block;
  }
}
</style>

