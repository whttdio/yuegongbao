<template>
  <div class="app-container ygb-page">
    <section class="gov-page-header ygb-page__header">
      <div>
        <p class="ygb-page__eyebrow">监管联动</p>
        <h1 class="ygb-page__title">{{ roleTitle }}</h1>
        <p class="ygb-page__desc">
          {{ roleDescription }}
          粤工保侧重点不是只看漏保风险数量，而是把名单生成、对象核查、催缴补缴、预警联动和月度归档串成一条连续办理链。
        </p>
      </div>
      <div class="ygb-page__tip">
        <div class="ygb-page__tip-item">当前视角：{{ roleBadge }}</div>
        <div class="ygb-page__tip-item">{{ roleTip }}</div>
        <div class="ygb-page__tip-item">生成名单前需先完成社保缴费同步和个税比对，当前仍复用统一漏保清单接口。</div>
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
      <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
        <el-form-item label="统计月份">
          <el-date-picker v-model="queryParams.statMonth" type="month" format="YYYY-MM" value-format="YYYY-MM" style="width: 160px" />
        </el-form-item>
        <el-form-item label="企业">
          <el-select v-model="queryParams.enterpriseId" clearable filterable style="width: 220px">
            <el-option v-for="item in enterpriseOptions" :key="item.enterpriseId" :label="item.enterpriseName" :value="item.enterpriseId" />
          </el-select>
        </el-form-item>
        <el-form-item label="人员">
          <el-input v-model="queryParams.personName" clearable style="width: 180px" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="处置状态">
          <el-select v-model="queryParams.disposalStatus" clearable style="width: 150px">
            <el-option v-for="item in disposalStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="预警状态">
          <el-select v-model="queryParams.warningStatus" clearable style="width: 150px">
            <el-option v-for="item in warningStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
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
          <el-button type="primary" plain icon="RefreshRight" @click="handleGenerate" v-hasPermi="['ygb:uninsuredList:generate']">生成名单</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['ygb:uninsuredList:export']">导出</el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>
    </el-card>

    <el-card class="table-card ygb-table-card" shadow="never">
      <template #header>
        <div class="ygb-card-head ygb-card-head--between">
          <div>
            <div class="ygb-card-head__title">漏保整改台账</div>
            <div class="ygb-card-head__desc">{{ focusTableHint }}</div>
          </div>
          <div class="ygb-card-head__desc">当前总量 {{ total }} 条</div>
        </div>
      </template>
      <el-table v-loading="loading" :data="visibleUninsuredList" @row-click="handleRowClick">
        <el-table-column label="ID" prop="listId" width="90" />
        <el-table-column label="批次号" prop="batchNo" min-width="180" />
        <el-table-column label="统计月份" prop="statMonth" width="110" />
        <el-table-column label="企业" prop="enterpriseName" min-width="220" />
        <el-table-column label="人员" prop="personName" width="120" />
        <el-table-column label="身份证号" prop="idCard" min-width="180" />
        <el-table-column label="工资金额" width="120">
          <template #default="scope">
            {{ formatMoney(scope.row.salaryAmount) }}
          </template>
        </el-table-column>
        <el-table-column label="识别原因" prop="detectedReason" min-width="240" show-overflow-tooltip />
        <el-table-column label="处置状态" width="120">
          <template #default="scope">
            <dict-tag :options="disposalStatusOptions" :value="scope.row.disposalStatus" />
          </template>
        </el-table-column>
        <el-table-column label="预警状态" width="120">
          <template #default="scope">
            <dict-tag :options="warningStatusOptions" :value="scope.row.warningStatus" />
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="createTime" width="180" />
        <el-table-column class-name="table-fill-column" min-width="1" />

        <el-table-column label="操作" fixed="right" align="center" :width="isReadOnlyRole ? 90 : 180" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-button link type="info" icon="View" @click.stop="openDetail(scope.row)">详情</el-button>
            <el-button
              v-if="!isReadOnlyRole"
              link
              type="primary"
              icon="Edit"
              @click.stop="openHandleDialog(scope.row)"
              v-hasPermi="['ygb:uninsuredList:handle']"
            >
              处置
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>

    <el-dialog title="漏保处置" v-model="handleOpen" width="520px" append-to-body>
      <el-form ref="handleRef" :model="handleForm" :rules="handleRules" label-width="100px">
        <el-form-item label="处置状态" prop="disposalStatus">
          <el-select v-model="handleForm.disposalStatus" placeholder="请选择处置状态">
            <el-option v-for="item in disposalStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="处置说明" prop="remark">
          <el-input v-model="handleForm.remark" type="textarea" :rows="4" placeholder="请输入核查说明、催缴情况或误报原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitHandle">确定</el-button>
          <el-button @click="handleOpen = false">取消</el-button>
        </div>
      </template>
    </el-dialog>

    <page-detail-dialog v-model="detailOpen" title="漏保记录详情" width="760px">
      <template v-if="detailRow">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="批次号">{{ detailRow.batchNo || '-' }}</el-descriptions-item>
          <el-descriptions-item label="统计月份">{{ detailRow.statMonth || '-' }}</el-descriptions-item>
          <el-descriptions-item label="企业">{{ detailRow.enterpriseName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="人员">{{ detailRow.personName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="处置状态">{{ optionLabel(disposalStatusOptions, detailRow.disposalStatus) }}</el-descriptions-item>
          <el-descriptions-item label="预警状态">{{ optionLabel(warningStatusOptions, detailRow.warningStatus) }}</el-descriptions-item>
          <el-descriptions-item label="工资金额">{{ formatMoney(detailRow.salaryAmount) }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ detailRow.createTime || '-' }}</el-descriptions-item>
          <el-descriptions-item label="当前视角重点" :span="2">{{ detailFocusText }}</el-descriptions-item>
          <el-descriptions-item label="身份证号" :span="2">{{ detailRow.idCard || '-' }}</el-descriptions-item>
          <el-descriptions-item label="识别原因" :span="2">{{ detailRow.detectedReason || '-' }}</el-descriptions-item>
          <el-descriptions-item label="处置备注" :span="2">{{ detailRow.remark || '-' }}</el-descriptions-item>
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

<script setup name="YgbUninsuredList">
import { computed, ref, watch, watchEffect } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useWorkbenchAssist } from '@/composables/useWorkbenchAssist'
import useUserStore from '@/store/modules/user'
import { decoratePortalExplanationItems, openPortalExplanationAction, resolvePortalExplanationSummary } from '@/utils/portalExplanation'
import { useRoleViewMode } from '@/utils/roleView'
import { applyWorkbenchRouteQuery, buildWorkbenchContext, stripWorkbenchRouteQuery } from '@/utils/workbenchLink'
import {
  disposalStatusOptions,
  warningStatusOptions,
  useUninsuredListPage,
  optionLabel,
  formatMoney,
  valueOrDefault
} from '@/views/uninsuredList/useUninsuredListPage'

const route = useRoute()
const { setPageGuide } = useWorkbenchAssist()
const router = useRouter()
const userStore = useUserStore()
const { isReadOnlyRole, readOnlyRoleLabel, readOnlyRoleDescription } = useRoleViewMode()
const activeFocusKey = ref('')
const uninsuredWorkbenchFields = ['enterpriseId', 'statMonth']
const workbenchClearLabel = '\u6e05\u7a7a\u6765\u6e90\u6761\u4ef6'

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

const workbenchContext = computed(() => buildWorkbenchContext(route.query, {
  fields: uninsuredWorkbenchFields,
  sourceLabel: '\u6269\u9762\u51cf\u635f\u5de5\u4f5c\u53f0',
  title: '\u5df2\u6309\u5de5\u4f5c\u53f0\u4e0a\u4e0b\u6587\u5e26\u5165\u7b5b\u9009\u6761\u4ef6',
  description: '\u5f53\u524d\u9875\u9762\u4fdd\u7559\u4e86\u9996\u9875\u5de5\u4f5c\u53f0\u5e26\u5165\u7684\u4f01\u4e1a\u4e0e\u7edf\u8ba1\u6708\u4efd\u8303\u56f4\uff0c\u53ef\u76f4\u63a5\u7ee7\u7eed\u6838\u67e5\u4e0e\u5904\u7f6e\u3002',
  fieldLabels: {
    enterpriseId: '\u4f01\u4e1a',
    statMonth: '\u7edf\u8ba1\u6708\u4efd'
  },
  fieldFormatters: {
    enterpriseId: value => {
      const matched = enterpriseOptions.value.find(item => String(item.enterpriseId) === String(value))
      return matched ? matched.enterpriseName : value
    }
  }
}))

const {
  uninsuredList,
  enterpriseOptions,
  loading,
  showSearch,
  total,
  handleOpen,
  detailOpen,
  currentRow,
  detailRow,
  summaryData,
  queryParams,
  handleForm,
  handleRules,
  getList,
  handleQuery,
  resetQuery: pageResetQuery,
  handleGenerate,
  handleExport,
  handleRowClick,
  openDetail,
  openHandleDialog,
  submitHandle,
  syncCurrentRow,
  init
} = useUninsuredListPage({
  getCurrentList: () => visibleUninsuredList.value,
  afterList: () => {
    syncActiveFocus()
  },
  immediate: false
})
const portalExplanations = computed(() => summaryData.value.ygbExplanation || [])
const portalExplanationItems = computed(() => decoratePortalExplanationItems(portalExplanations.value, {
  portalCode: 'ygb',
  panelTitle: '530.1 办理链解释',
  panelDescription: '漏保识别、催缴补缴和联动处置建议统一来自门户解释聚合接口。'
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

function summaryCard(key, label, value, unit, note, cardClass = '') {
  return { key, label, value, unit, note, cardClass }
}

function focusQueue(key, title, count, unit, desc, actionText) {
  return { key, title, count, unit, desc, actionText }
}

function countRows(predicate) {
  return uninsuredList.value.filter(predicate).length
}

function matchFocus(row, focusKey) {
  if (!row || !focusKey) {
    return false
  }
  if (focusKey === 'pending') {
    return ['0', '1'].includes(String(row.disposalStatus || ''))
  }
  if (focusKey === 'unwarned') {
    return String(row.warningStatus || '') === '0'
  }
  if (focusKey === 'highSalary') {
    return Number(row.salaryAmount || 0) >= 10000
  }
  if (focusKey === 'pushed') {
    return String(row.disposalStatus || '') === '2'
  }
  if (focusKey === 'completed') {
    return String(row.disposalStatus || '') === '3'
  }
  if (focusKey === 'enforced') {
    return String(row.disposalStatus || '') === '4'
  }
  if (focusKey === 'misreport') {
    return String(row.disposalStatus || '') === '5'
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

function handleSelectFocus(item) {
  activeFocusKey.value = item.key
  syncCurrentRow()
}

const roleBadge = computed(() => {
  if (roleView.value === 'hrss') return '人社监管视角'
  if (roleView.value === 'operator') return '企业经办视角'
  if (roleView.value === 'finance') return '财务经办视角'
  if (roleView.value === 'admin') return '企业管理员视角'
  return '综合办理视角'
})

const roleTitle = computed(() => {
  if (roleView.value === 'hrss') return '区域扩面减损督办工作台'
  if (roleView.value === 'operator') return '漏保名单核查与回写台账'
  if (roleView.value === 'finance') return '财务补缴与扩面减损台账'
  if (roleView.value === 'admin') return '企业漏保整改闭环工作台'
  return '漏保整改办理台账'
})

const roleDescription = computed(() => {
  if (roleView.value === 'hrss') {
    return '面向人社监管经办统一查看漏保识别、未预警对象、催缴进度和企业覆盖情况，重点是把扩面减损对象持续拉回督办和归档链路。'
  }
  if (roleView.value === 'operator') {
    return '面向企业经办统一承接名单核查、说明补录和误报回写，重点是先把对象说明和办理留痕补齐，再交由管理员或财务推进补缴。'
  }
  if (roleView.value === 'finance') {
    return '面向财务经办统一承接高工资漏保、已催缴对象和补缴情况回写，重点是把工资、社保和个税差异真正转成补缴闭环。'
  }
  if (roleView.value === 'admin') {
    return '面向企业管理员统一统筹漏保识别、对象核查、催缴补缴和预警联动，重点是把扩面减损主链从名单生成一直压实到月度归档。'
  }
  return '面向企业管理员、财务经办和监管人员统一查看漏保识别结果、催缴进度、补缴状态和联动预警情况。'
})

const roleTip = computed(() => {
  if (roleView.value === 'hrss') {
    return '先锁定未预警、待核查和强制执行对象，再决定是否进入预警中心或月度归档复核。'
  }
  if (roleView.value === 'operator') {
    return '优先处理待核查、未预警和误报对象，避免名单长期挂起。'
  }
  if (roleView.value === 'finance') {
    return '优先回看高工资漏保、已催缴和已补缴对象，缩短补缴和归档链路。'
  }
  if (roleView.value === 'admin') {
    return '优先统筹待核查、高工资和未预警对象，再决定是催缴补缴还是进入监管联动。'
  }
  return '页面继续复用统一漏保清单接口，不引入第二套扩面业务表。'
})

const summaryCards = computed(() => {
  if (roleView.value === 'finance') {
    return [
      summaryCard('highSalary', '高工资对象', valueOrDefault(summaryData.value.highSalaryCount, countRows(row => matchFocus(row, 'highSalary'))), '条', '适合作为社保缴费、基数和个税差异整改入口。', 'ygb-summary-card--warning'),
      summaryCard('pushed', '已催缴对象', countRows(row => matchFocus(row, 'pushed')), '条', '建议持续回写催缴进度，避免已催缴长期停滞。', 'ygb-summary-card--primary'),
      summaryCard('completed', '已补缴', valueOrDefault(summaryData.value.completedCount, countRows(row => matchFocus(row, 'completed'))), '条', '已完成补缴情况，可继续参与月度归档和监管复核。', 'ygb-summary-card--success'),
      summaryCard('unwarned', '未预警对象', valueOrDefault(summaryData.value.unwarnedCount, countRows(row => matchFocus(row, 'unwarned'))), '条', '需同步补齐预警联动和财务整改留痕。', '')
    ]
  }
  if (roleView.value === 'hrss') {
    return [
      summaryCard('pending', '待核查/核查中', valueOrDefault(summaryData.value.pendingCount, countRows(row => matchFocus(row, 'pending'))), '条', '需要持续督办核查和催缴动作的重点对象。', 'ygb-summary-card--warning'),
      summaryCard('unwarned', '未预警对象', valueOrDefault(summaryData.value.unwarnedCount, countRows(row => matchFocus(row, 'unwarned'))), '条', '尚未进入监管联动的对象，建议优先补齐。', 'ygb-summary-card--primary'),
      summaryCard('enterprise', '涉及企业', valueOrDefault(summaryData.value.enterpriseCount, 0), '家', '用于判断当前区域扩面减损覆盖范围。', ''),
      summaryCard('completed', '已补缴', valueOrDefault(summaryData.value.completedCount, countRows(row => matchFocus(row, 'completed'))), '条', '可直接纳入月报和区域监管复核。', 'ygb-summary-card--success')
    ]
  }
  if (roleView.value === 'operator') {
    return [
      summaryCard('pending', '待核查/核查中', valueOrDefault(summaryData.value.pendingCount, countRows(row => matchFocus(row, 'pending'))), '条', '企业经办最需要优先补说明和回写的对象。', 'ygb-summary-card--warning'),
      summaryCard('unwarned', '未预警对象', valueOrDefault(summaryData.value.unwarnedCount, countRows(row => matchFocus(row, 'unwarned'))), '条', '需要优先补齐联动留痕，避免对象游离在预警外。', 'ygb-summary-card--primary'),
      summaryCard('misreport', '误报对象', countRows(row => matchFocus(row, 'misreport')), '条', '适合先补误报说明和规则校正材料。', ''),
      summaryCard('completed', '已补缴', valueOrDefault(summaryData.value.completedCount, countRows(row => matchFocus(row, 'completed'))), '条', '可作为本月经办闭环完成结果。', 'ygb-summary-card--success')
    ]
  }
  if (roleView.value === 'admin') {
    return [
      summaryCard('total', '漏保记录', valueOrDefault(summaryData.value.totalCount, total.value), '条', '当前筛选条件下的漏保整改台账总量。', ''),
      summaryCard('pending', '待核查/核查中', valueOrDefault(summaryData.value.pendingCount, countRows(row => matchFocus(row, 'pending'))), '条', '仍需持续跟进核查和催缴动作的对象数量。', 'ygb-summary-card--warning'),
      summaryCard('highSalary', '高工资对象', valueOrDefault(summaryData.value.highSalaryCount, countRows(row => matchFocus(row, 'highSalary'))), '条', '建议优先联动工资、社保和个税台账复核。', 'ygb-summary-card--primary'),
      summaryCard('completed', '已补缴', valueOrDefault(summaryData.value.completedCount, countRows(row => matchFocus(row, 'completed'))), '条', '已完成补缴情况，可直接进入归档复核。', 'ygb-summary-card--success')
    ]
  }
  return [
    summaryCard('total', '漏保记录', valueOrDefault(summaryData.value.totalCount, total.value), '条', '当前筛选条件下的漏保整改台账总量。', ''),
    summaryCard('pending', '待核查/核查中', valueOrDefault(summaryData.value.pendingCount, countRows(row => matchFocus(row, 'pending'))), '条', '仍需持续跟进核查和催缴动作的对象数量。', 'ygb-summary-card--warning'),
    summaryCard('completed', '已补缴', valueOrDefault(summaryData.value.completedCount, countRows(row => matchFocus(row, 'completed'))), '条', '已完成补缴情况、可直接进入归档复核。', 'ygb-summary-card--success'),
    summaryCard('unwarned', '未预警对象', valueOrDefault(summaryData.value.unwarnedCount, countRows(row => matchFocus(row, 'unwarned'))), '条', '尚未进入预警联动的漏保对象，应优先补齐联动处置。', 'ygb-summary-card--primary')
  ]
})

const resolvedSummaryCards = computed(() => summaryCards.value.map((item, index) => ({
  ...item,
  note: resolveExplanationFirstText(item.note, resolvePortalExplanationItem(index))
})))

const focusQueues = computed(() => {
  if (roleView.value === 'finance') {
    return [
      focusQueue('highSalary', '高工资漏保对象', valueOrDefault(summaryData.value.highSalaryCount, countRows(row => matchFocus(row, 'highSalary'))), '条', '先回查工资金额、社保缴纳基数和个税差异，再决定补缴情况。', '回看工资社税链路'),
      focusQueue('pushed', '已催缴待回写对象', countRows(row => matchFocus(row, 'pushed')), '条', '优先补催缴结果、补缴时间和后续留痕，避免对象长期停在催缴状态。', '回写催缴进度'),
      focusQueue('completed', '已补缴对象', valueOrDefault(summaryData.value.completedCount, countRows(row => matchFocus(row, 'completed'))), '条', '适合优先复核已补缴情况和月度归档结果。', '查看补缴结果')
    ]
  }
  if (roleView.value === 'hrss') {
    return [
      focusQueue('unwarned', '未预警对象', valueOrDefault(summaryData.value.unwarnedCount, countRows(row => matchFocus(row, 'unwarned'))), '条', '优先补齐监管联动和预警留痕，避免对象脱离督办链。', '进入预警联动'),
      focusQueue('pending', '待核查对象', valueOrDefault(summaryData.value.pendingCount, countRows(row => matchFocus(row, 'pending'))), '条', '适合持续督办企业核查说明和催缴情况。', '推进区域督办'),
      focusQueue('enforced', '强制执行对象', countRows(row => matchFocus(row, 'enforced')), '条', '重点回看历史积压和强制执行结果回写。', '复核执行结果')
    ]
  }
  if (roleView.value === 'operator') {
    return [
      focusQueue('pending', '待核查对象', valueOrDefault(summaryData.value.pendingCount, countRows(row => matchFocus(row, 'pending'))), '条', '优先补充身份、工资来源和参保情况说明，避免台账长期挂起。', '补核查说明'),
      focusQueue('unwarned', '未预警对象', valueOrDefault(summaryData.value.unwarnedCount, countRows(row => matchFocus(row, 'unwarned'))), '条', '先补齐预警侧留痕和催办记录，减少后续监管反复追问。', '补联动留痕'),
      focusQueue('misreport', '误报对象', countRows(row => matchFocus(row, 'misreport')), '条', '适合作为误报说明和规则校正材料整理入口。', '补误报说明')
    ]
  }
  if (roleView.value === 'admin') {
    return [
      focusQueue('pending', '待核查对象', valueOrDefault(summaryData.value.pendingCount, countRows(row => matchFocus(row, 'pending'))), '条', '先锁定真正拖住扩面减损闭环的待核查和核查中对象。', '进入核查链路'),
      focusQueue('highSalary', '高工资漏保对象', valueOrDefault(summaryData.value.highSalaryCount, countRows(row => matchFocus(row, 'highSalary'))), '条', '优先联动工资、社保和个税链路复核高风险漏保主体。', '回看工资社税链路'),
      focusQueue('unwarned', '未预警对象', valueOrDefault(summaryData.value.unwarnedCount, countRows(row => matchFocus(row, 'unwarned'))), '条', '先补齐联动预警，再继续推进催缴和归档。', '补齐预警联动')
    ]
  }
  return [
    focusQueue('pending', '待核查对象', valueOrDefault(summaryData.value.pendingCount, countRows(row => matchFocus(row, 'pending'))), '条', '优先处理仍待核查和核查中的漏保对象。', '查看重点对象'),
    focusQueue('unwarned', '未预警对象', valueOrDefault(summaryData.value.unwarnedCount, countRows(row => matchFocus(row, 'unwarned'))), '条', '优先补齐联动预警和催办记录。', '补联动留痕'),
    focusQueue('completed', '已补缴对象', valueOrDefault(summaryData.value.completedCount, countRows(row => matchFocus(row, 'completed'))), '条', '适合优先回看补缴情况和月度归档。', '查看闭环结果')
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

const visibleUninsuredList = computed(() => prioritizeFocusRows(uninsuredList.value, row => matchFocus(row, activeFocus.value?.key)))

const focusTableHint = computed(() => {
  if (!activeFocus.value) {
    return '按当前筛选条件展示漏保整改台账。'
  }
  return `当前焦点为“${activeFocus.value.title}”，已把对应重点对象优先排到表格前列。`
})

const selectedOverview = computed(() => {
  if (!currentRow.value) {
    return [
      { label: '当前焦点', value: activeFocus.value?.title || '-' },
      { label: '焦点数量', value: `${activeFocus.value?.count ?? 0}${activeFocus.value?.unit || ''}` },
      { label: '当前月份', value: queryParams.value.statMonth || '全部月份' },
      { label: '当前企业', value: currentEnterpriseName() }
    ]
  }
  return [
    { label: '企业', value: currentRow.value.enterpriseName || '-' },
    { label: '处置状态', value: optionLabel(disposalStatusOptions, currentRow.value.disposalStatus) },
    { label: '预警状态', value: optionLabel(warningStatusOptions, currentRow.value.warningStatus) },
    { label: '工资金额', value: formatMoney(currentRow.value.salaryAmount) }
  ]
})

const primaryAction = computed(() => {
  if (!currentRow.value) {
    return { label: activeFocus.value?.actionText || '查看详情', action: 'detail' }
  }
  if (isReadOnlyRole.value) {
    return { label: '查看详情', action: 'detail' }
  }
  if (String(currentRow.value.warningStatus || '') === '0' || ['0', '1', '2'].includes(String(currentRow.value.disposalStatus || ''))) {
    return { label: '处置对象', action: 'handle' }
  }
  return { label: '查看详情', action: 'detail' }
})

const secondaryAction = computed(() => {
  if (activeFocus.value?.key === 'highSalary') {
    return { label: '查看社保基数比对', path: '/ygb/socialBaseCompare' }
  }
  if (activeFocus.value?.key === 'unwarned' || activeFocus.value?.key === 'enforced') {
    return { label: '查看预警中心', path: '/ygb/warning' }
  }
  if (activeFocus.value?.key === 'completed') {
    return { label: '查看社保税务联动月报', path: '/ygb-report/statReport/socialTax' }
  }
  return { label: '查看社保缴费监控', path: '/ygb/socialPayment' }
})

const currentActionSummary = computed(() => {
  if (!currentRow.value) {
    return activeFocus.value
      ? `当前已按“${activeFocus.value.title}”重排台账，建议优先处理表格前列对象，再决定是否进入催缴、补缴或归档链路。`
      : '当前暂无漏保对象。'
  }
  if (roleView.value === 'finance') {
    if (matchFocus(currentRow.value, 'highSalary')) {
      return '该对象工资金额较高，建议优先联动社保缴费、社保基数和个税比对台账，确认补缴情况和基数口径。'
    }
    if (matchFocus(currentRow.value, 'pushed')) {
      return '该对象已进入催缴阶段，建议持续回写催缴进度、补缴时间和结果，避免财务整改链断开。'
    }
  }
  if (roleView.value === 'hrss') {
    if (String(currentRow.value.warningStatus || '') === '0') {
      return '该对象尚未进入预警联动，建议优先补齐监管预警留痕，再继续督办企业核查和补缴。'
    }
    if (String(currentRow.value.disposalStatus || '') === '4') {
      return '该对象已进入强制执行，建议重点复核执行结果回写和月度监管归档。'
    }
  }
  if (roleView.value === 'operator') {
    if (String(currentRow.value.disposalStatus || '') === '5') {
      return '该对象已标记为误报，建议补齐误报原因和佐证材料，便于后续规则校正。'
    }
    return '当前更适合先补充核查说明、催缴记录和预警留痕，再交由管理员或财务推进后续闭环。'
  }
  if (String(currentRow.value.warningStatus || '') === '0') {
    return '该对象尚未进入预警联动，建议优先补齐预警侧留痕和催办记录，避免扩面减损对象游离在监管外。'
  }
  if (String(currentRow.value.disposalStatus || '') === '0') {
    return '该对象当前仍待核查，建议先确认人员身份、工资来源和实际参保情况，再决定是否继续催缴。'
  }
  if (String(currentRow.value.disposalStatus || '') === '1') {
    return '该对象当前处于核查中，建议尽快补录核查结论和处置说明，避免名单长期挂起。'
  }
  if (String(currentRow.value.disposalStatus || '') === '2') {
    return '该对象当前已催缴，建议持续跟进补缴进度并回写完成时间或阶段结果。'
  }
  if (String(currentRow.value.disposalStatus || '') === '3') {
    return '该对象当前已补缴，可继续纳入月度扩面减损归档和后续监管复核。'
  }
  return '该对象当前台账信息较完整，建议先查看详情确认材料，再决定是否继续联动其他台账。'
})

const currentActionTags = computed(() => buildHintTags(currentRow.value, activeFocus.value, roleView.value))
const detailHintTags = computed(() => buildHintTags(detailRow.value || currentRow.value, activeFocus.value, roleView.value))
const pageHintTags = computed(() => buildPageHintTags())

const workflowSteps = computed(() => {
  if (roleView.value === 'finance') {
    return [
      { label: '先锁定高工资漏保对象', desc: '优先找出高工资、已催缴和已补缴对象，快速判断补缴情况和基数风险。' },
      { label: '再联动社税整改', desc: '把社保缴费、社保基数和个税差异对象接回财务整改台账，避免漏保整改停在汇总层。' },
      { label: '最后回到补缴归档', desc: '在补缴情况回写完成后，把结果沉淀到月报和扩面减损归档。' }
    ]
  }
  if (roleView.value === 'hrss') {
    return [
      { label: '先锁定未预警和待核查对象', desc: '优先识别仍未进入监管联动或长期核查中的对象。' },
      { label: '再承接区域督办', desc: '围绕企业覆盖、强制执行和补缴情况持续督办，避免重点企业长期脱离扩面链路。' },
      { label: '最后完成归档复核', desc: '把漏保结果回落到月报、区域监管和后续监管底稿。' }
    ]
  }
  if (roleView.value === 'operator') {
    return [
      { label: '先补对象核查说明', desc: '先把待核查、核查中和误报对象的说明、截图和结论补齐。' },
      { label: '再回写催缴留痕', desc: '把催缴情况、预警状态和阶段结果回写到对象台账。' },
      { label: '最后交接补缴闭环', desc: '在说明和留痕补齐后，再交由管理员或财务继续推进补缴和归档。' }
    ]
  }
  if (roleView.value === 'admin') {
    return [
      { label: '先生成名单与锁定重点对象', desc: '先生成当月名单，再锁定待核查、高工资和未预警对象。' },
      { label: '承接企业整改主链', desc: '围绕工资、社保、个税和预警链路补核查说明、催缴情况和补缴结果。' },
      { label: '完成归档复核', desc: '把已补缴、误报和强制执行结果沉淀到月度扩面减损归档。' }
    ]
  }
  return [
    { label: '先生成名单', desc: '按统计月份生成漏保对象清单，形成企业、人员、工资和识别原因可追溯的扩面台账。' },
    { label: '核查对象原因', desc: '逐条确认人员身份、工资来源和参保情况，补录核查说明，避免误报与漏催并存。' },
    { label: '催缴与补缴回写', desc: '对待处理对象持续回写催缴进度、补缴结果和预警状态，保留办理留痕。' },
    { label: '闭环归档复核', desc: '将已补缴、误报或强制执行结果纳入月度扩面减损归档和后续监管复核。' }
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
const resolvedCurrentActionSummary = computed(() => resolveExplanationFirstText(
  currentActionSummary.value,
  leadingPortalExplanation.value
))
const resolvedCurrentActionTags = computed(() => buildExplanationFirstTags(currentActionTags.value))
const resolvedPageHintTags = computed(() => buildExplanationFirstTags(pageHintTags.value))
const resolvedDetailHintTags = computed(() => buildExplanationFirstTags(detailHintTags.value))
const resolvedPrimaryAction = computed(() => ({
  ...primaryAction.value,
  label: resolveExplanationFirstActionText(primaryAction.value.label, leadingPortalExplanation.value)
}))

const detailFocusText = computed(() => {
  if (roleView.value === 'finance') {
    return '重点看工资金额、催缴情况和补缴情况是否已回写，再决定是否进入社保缴费、基数或个税比对链路。'
  }
  if (roleView.value === 'hrss') {
    return '重点看未预警、核查进度、企业覆盖和强制执行结果是否完整，再决定是否进入监管督办或月报归档。'
  }
  if (roleView.value === 'operator') {
    return '重点看是否还缺核查说明、误报材料和预警留痕，避免对象只停留在名单层。'
  }
  if (roleView.value === 'admin') {
    return '重点看哪些对象仍阻断企业扩面减损闭环，再把动作回落到工资、社保、税务和预警主链。'
  }
  return '重点看当前对象的核查、催缴、补缴和预警状态是否完整。'
})

function buildHintTags(row, focus, view) {
  if (!row) {
    if (focus?.title) {
      return [
        { label: `当前焦点：${focus.title}`, type: 'info' },
        { label: `建议动作：${focus.actionText}`, type: 'warning' }
      ]
    }
    return [{ label: '请选择漏保对象查看办理提示。', type: 'info' }]
  }

  const tags = []
  const salaryAmount = Number(row.salaryAmount || 0)

  if (String(row.warningStatus || '') === '0') {
    tags.push({ label: '当前尚未进入预警联动，建议优先补齐预警侧留痕和催办记录。', type: 'warning' })
  }
  if (String(row.disposalStatus || '') === '0') {
    tags.push({ label: '当前仍待核查，建议先确认人员身份、工资来源和实际参保情况。', type: 'warning' })
  }
  if (String(row.disposalStatus || '') === '1') {
    tags.push({ label: '当前处于核查中，建议及时回写核查结论，避免名单长期挂起。', type: 'warning' })
  }
  if (String(row.disposalStatus || '') === '2') {
    tags.push({ label: '当前已催缴，建议持续跟进补缴进度并回写完成时间或结果。', type: 'info' })
  }
  if (String(row.disposalStatus || '') === '3') {
    tags.push({ label: '当前已补缴，可继续纳入月度扩面减损归档和监管复核。', type: 'success' })
  }
  if (String(row.disposalStatus || '') === '4') {
    tags.push({ label: '当前已进入强制执行，建议重点复核执行结果回写和后续归档。', type: 'primary' })
  }
  if (String(row.disposalStatus || '') === '5') {
    tags.push({ label: '当前标记为误报，建议补充误报原因，便于后续模型和规则校正。', type: 'info' })
  }
  if (salaryAmount >= 10000) {
    tags.push({ label: '当前对象工资金额较高，建议优先复核社保缴纳基数和连续参保情况。', type: 'warning' })
  }
  if (!row.remark && String(row.disposalStatus || '') !== '0') {
    tags.push({ label: '当前已进入处置流程但缺少备注，建议补录核查说明或催缴结果。', type: 'warning' })
  }
  if (view === 'finance' && salaryAmount >= 10000) {
    tags.push({ label: '财务视角建议继续联动社保基数和个税比对，确认补缴情况是否闭环。', type: 'primary' })
  }
  if (view === 'hrss' && String(row.warningStatus || '') === '0') {
    tags.push({ label: '监管视角建议把该对象尽快纳入预警督办链，避免区域漏管。', type: 'danger' })
  }
  if (view === 'operator' && ['0', '1'].includes(String(row.disposalStatus || ''))) {
    tags.push({ label: '经办视角建议优先补核查说明和佐证材料，再继续催缴。', type: 'info' })
  }
  if (view === 'admin' && ['2', '3'].includes(String(row.disposalStatus || ''))) {
    tags.push({ label: '管理员视角建议同步回看补缴情况与月度归档，避免闭环只停留在催缴阶段。', type: 'success' })
  }
  if (!tags.length) {
    tags.push({ label: '当前漏保对象台账信息较完整，可继续用于后续补缴闭环和归档。', type: 'success' })
  }
  return tags
}

function buildPageHintTags() {
  const tags = []
  if (valueOrDefault(summaryData.value.pendingCount, 0) > 0) {
    tags.push({ label: `当前仍有 ${valueOrDefault(summaryData.value.pendingCount, 0)} 条待核查对象，建议优先推进核查说明和催缴情况回写。`, type: 'warning' })
  }
  if (valueOrDefault(summaryData.value.unwarnedCount, 0) > 0) {
    tags.push({ label: `当前仍有 ${valueOrDefault(summaryData.value.unwarnedCount, 0)} 条未预警对象，建议同步补齐监管联动。`, type: 'danger' })
  }
  if (roleView.value === 'finance' && valueOrDefault(summaryData.value.highSalaryCount, 0) > 0) {
    tags.push({ label: `高工资漏保对象 ${valueOrDefault(summaryData.value.highSalaryCount, 0)} 条，建议继续联动工资、社保和个税链路复核。`, type: 'primary' })
  }
  if (valueOrDefault(summaryData.value.completedCount, 0) > 0) {
    tags.push({ label: `当前已有 ${valueOrDefault(summaryData.value.completedCount, 0)} 条已补缴对象，可作为月度归档和监管复核底稿。`, type: 'success' })
  }
  if (!tags.length) {
    tags.push({ label: '当前漏保整改链路相对平稳，可继续回看统计归档和区域覆盖情况。', type: 'success' })
  }
  return tags
}

function currentEnterpriseName() {
  if (!queryParams.value.enterpriseId) {
    return '全部企业'
  }
  const current = enterpriseOptions.value.find(item => item.enterpriseId === queryParams.value.enterpriseId)
  return current ? current.enterpriseName : queryParams.value.enterpriseId
}

function openModule(path) {
  if (!path) {
    return
  }
  router.push(path)
}

function handlePrimaryAction() {
  if (!currentRow.value) {
    return
  }
  if (primaryAction.value.action === 'handle') {
    openHandleDialog(currentRow.value)
    return
  }
  openDetail(currentRow.value)
}

function applyUninsuredWorkbenchQuery(routeQuery = {}) {
  Object.assign(queryParams.value, {
    pageNum: 1,
    pageSize: 10,
    statMonth: undefined,
    enterpriseId: undefined,
    personName: undefined,
    disposalStatus: undefined,
    warningStatus: undefined
  })
  applyWorkbenchRouteQuery(routeQuery, queryParams.value, uninsuredWorkbenchFields)
  getList()
}

function handlePortalExplanationAction(action) {
  if (action?.path === route.path) {
    applyUninsuredWorkbenchQuery(action.query || {})
  }
  openPortalExplanationAction(router, action)
}

function resetQuery() {
  pageResetQuery()
  applyWorkbenchRouteQuery(route.query, queryParams.value, uninsuredWorkbenchFields)
  getList()

watchEffect(() => {
  setPageGuide({
    title: roleTitle.value || '????????',
    description: roleDescription.value || '?????????????????????????????????',
    portalExplanation: portalExplanationItems.value,
    focus: resolvedFocusQueues.value,
    selection: [...selectedOverview.value, { label: '??????', value: currentActionSummary.value }],
    workflow: resolvedWorkflowSteps.value,
    hints: [...currentActionTags.value].slice(0, 6)
  })
})

}

function clearWorkbenchContext() {
  Object.assign(queryParams.value, {
    pageNum: 1,
    enterpriseId: undefined,
    statMonth: undefined
  })
  router.replace({
    path: route.path,
    query: stripWorkbenchRouteQuery(route.query, uninsuredWorkbenchFields)
  })
  getList()
}

watch(focusQueues, queues => {
  if (!queues.length) {
    activeFocusKey.value = ''
    return
  }
  if (!queues.some(item => item.key === activeFocusKey.value)) {
    activeFocusKey.value = queues[0].key
  }
}, { immediate: true })

applyWorkbenchRouteQuery(route.query, queryParams.value, uninsuredWorkbenchFields)
init()
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
  background: linear-gradient(180deg, #ffffff 0%, #f2f7ff 100%);
}

.ygb-card-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
}

.ygb-card-head--between {
  justify-content: space-between;
}

.ygb-card-head__title {
  color: #15304b;
  font-size: 16px;
  font-weight: 700;
}

.ygb-card-head__desc {
  margin-top: 6px;
  color: #627486;
  line-height: 1.7;
  font-size: 13px;
}

.ygb-focus-list,
.ygb-source-list {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.ygb-focus-list--single {
  grid-template-columns: minmax(0, 1fr);
}

.ygb-focus-queue {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  width: 100%;
  padding: 16px;
  border-radius: 12px;
  border: 1px solid #e2eaf2;
  background: #f6f9fc;
  text-align: left;
  cursor: pointer;
  transition: all 0.2s ease;
}

.ygb-focus-queue:hover,
.ygb-focus-queue.is-active {
  border-color: #1f5aa6;
  background: #eef5ff;
}

.ygb-focus-queue__main strong {
  color: #17324d;
  font-size: 15px;
}

.ygb-focus-queue__main p {
  margin: 6px 0 0;
  color: #627486;
  line-height: 1.7;
  font-size: 13px;
}

.ygb-focus-queue__side {
  display: flex;
  min-width: 120px;
  flex-direction: column;
  align-items: flex-end;
  gap: 6px;
}

.ygb-focus-queue__count {
  color: #15304b;
  font-size: 20px;
  font-weight: 700;
}

.ygb-focus-queue__action {
  color: #1f5aa6;
  font-size: 12px;
}

.ygb-source-item {
  padding: 14px 16px;
  border-radius: 12px;
  background: #f6f9fc;
  border: 1px solid #e2eaf2;
}

.ygb-source-item__label {
  color: #708397;
  font-size: 12px;
}

.ygb-source-item__value {
  margin-top: 8px;
  color: #17324d;
  font-size: 15px;
  font-weight: 600;
  line-height: 1.6;
}

.ygb-pipeline-list {
  display: grid;
  gap: 12px;
}

.ygb-pipeline-item {
  display: flex;
  gap: 14px;
  padding: 14px 16px;
  border-radius: 12px;
  background: #f7f9fc;
  border: 1px solid #e0e8f0;
}

.ygb-pipeline-item__index {
  width: 34px;
  height: 34px;
  line-height: 34px;
  border-radius: 10px;
  background: #1f5aa6;
  color: #fff;
  text-align: center;
  font-size: 13px;
  font-weight: 700;
  flex-shrink: 0;
}

.ygb-pipeline-item__body strong {
  color: #183552;
  font-size: 14px;
}

.ygb-pipeline-item__body p {
  margin: 6px 0 0;
  color: #627486;
  line-height: 1.7;
  font-size: 13px;
}

.ygb-tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.ygb-recommend-panel {
  margin-top: 16px;
  padding: 14px 16px;
  border-radius: 12px;
  background: #f6f9fc;
  border: 1px solid #e2eaf2;
}

.ygb-recommend-panel__title {
  color: #15304b;
  font-weight: 700;
}

.ygb-recommend-panel__summary {
  margin: 8px 0 0;
  color: #627486;
  line-height: 1.7;
  font-size: 13px;
}

.ygb-focus-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 16px;
}

.ygb-detail-block {
  margin-top: 20px;
}

.ygb-detail-block h3 {
  margin: 0 0 12px;
  color: #15304b;
  font-size: 15px;
}

@media (max-width: 1200px) {
  .ygb-summary-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 768px) {
  .ygb-summary-grid,
  .ygb-focus-grid,
  .ygb-source-list {
    grid-template-columns: minmax(0, 1fr);
  }

  .ygb-focus-queue {
    flex-direction: column;
    align-items: flex-start;
  }

  .ygb-focus-queue__side {
    min-width: 0;
    align-items: flex-start;
  }
}
</style>
