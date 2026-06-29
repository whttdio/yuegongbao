<template>
  <div class="app-container azb-page azb-warning-page">
    <section class="gov-page-header azb-page__header">
      <div>
        <p class="azb-page__eyebrow">监管预警</p>
        <h1 class="azb-page__title">{{ roleTitle }}</h1>
        <p class="azb-page__desc">{{ roleDescription }}</p>
      </div>
      <div class="azb-page__tip">
        <div class="azb-page__tip-item">当前视角：{{ roleBadge }}</div>
        <div class="azb-page__tip-item">聚焦红警、设备联动、升级处置和工伤来源预警，保持安责保 6.1 治理解释口径一致。</div>
        <div class="azb-page__tip-item">从首页、驾驶舱或解释面板下钻后，会保留来源条件继续查看常规列表。</div>
      </div>
    </section>

    <el-alert
      v-if="isReadOnlyRole"
      :title="readOnlyAlertTitle"
      :description="readOnlyAlertDescription"
      type="info"
      :closable="false"
      show-icon
      class="azb-role-alert"
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
      <div class="azb-tag-list">
        <el-tag v-for="item in workbenchContext.tags" :key="item.key" effect="plain">{{ item.label }}：{{ item.value }}</el-tag>
      </div>
    </el-alert>

    <div class="azb-summary-grid">
      <div v-for="item in summaryCards" :key="item.key" class="azb-summary-card" :class="item.cardClass">
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
        <el-form-item label="企业">
          <el-select v-model="queryParams.enterpriseId" clearable filterable style="width: 220px">
            <el-option v-for="item in enterpriseOptions" :key="item.enterpriseId" :label="item.enterpriseName" :value="item.enterpriseId" />
          </el-select>
        </el-form-item>
        <el-form-item label="预警级别">
          <el-select v-model="queryParams.warnLevel" clearable style="width: 160px">
            <el-option v-for="item in warnLevelOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="来源模块">
          <el-select v-model="queryParams.sourceModule" clearable style="width: 160px">
            <el-option v-for="item in sourceModuleOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="工单状态">
          <el-select v-model="queryParams.warnStatus" clearable style="width: 160px">
            <el-option v-for="item in warnStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="预警内容">
          <el-input v-model="queryParams.content" clearable style="width: 220px" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">查询</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="toolbar-card azb-toolbar-card" shadow="never">
      <el-row :gutter="10">
        <el-col :span="1.5">
          <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['ygb:warning:export']">导出</el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="loadAll" />
      </el-row>
    </el-card>

    <el-card class="table-card azb-table-card" shadow="never">
      <template #header>
        <div class="azb-card-head azb-card-head--between">
          <div>
            <div class="azb-card-head__title">监管预警列表</div>
            <div class="azb-card-head__desc">{{ focusTableHint }}</div>
          </div>
          <div class="azb-card-head__desc">当前共 {{ total }} 条</div>
        </div>
      </template>
      <el-table v-loading="loading" :data="visibleWarningList" @row-click="handleRowClick">
        <el-table-column label="ID" prop="warnId" width="90" />
        <el-table-column label="预警级别" prop="warnLevel" width="100">
          <template #default="scope">
            <dict-tag :options="warnLevelOptions" :value="scope.row.warnLevel" />
          </template>
        </el-table-column>
        <el-table-column label="预警类型" prop="warnType" min-width="180" show-overflow-tooltip />
        <el-table-column label="来源模块" prop="sourceModule" width="120">
          <template #default="scope">
            <dict-tag :options="sourceModuleOptions" :value="scope.row.sourceModule" />
          </template>
        </el-table-column>
        <el-table-column label="企业" prop="enterpriseName" min-width="180" show-overflow-tooltip />
        <el-table-column label="区域" width="130">
          <template #default="scope">
            <span>{{ formatRegionName(scope.row.regionCode) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="预警内容" prop="content" min-width="240" show-overflow-tooltip />
        <el-table-column label="工单状态" prop="warnStatus" width="110">
          <template #default="scope">
            <dict-tag :options="warnStatusOptions" :value="scope.row.warnStatus" />
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="180">
          <template #default="scope">
            <span>{{ formatDateTime(scope.row.createTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="办结时间" width="180">
          <template #default="scope">
            <span>{{ formatDateTime(scope.row.resolveTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column class-name="table-fill-column" min-width="1" />

        <el-table-column label="操作" fixed="right" align="center" width="180" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-button link type="info" icon="View" @click.stop="openDetail(scope.row)">详情</el-button>
            <el-button
              v-if="!isReadOnlyRole"
              link
              type="primary"
              icon="Operation"
              :disabled="!canHandleWarningRow(scope.row)"
              @click.stop="openHandleDialog(scope.row)"
              v-hasPermi="['ygb:warning:handle']"
            >
              处置
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="loadAll" />
    </el-card>

    <page-detail-dialog v-model="detailOpen" title="预警详情" width="760px">
      <template v-if="warningDetail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="预警ID">{{ warningDetail.warnId }}</el-descriptions-item>
          <el-descriptions-item label="预警级别">
            <dict-tag :options="warnLevelOptions" :value="warningDetail.warnLevel" />
          </el-descriptions-item>
          <el-descriptions-item label="预警类型">{{ warningDetail.warnType || '-' }}</el-descriptions-item>
          <el-descriptions-item label="来源模块">
            <dict-tag :options="sourceModuleOptions" :value="warningDetail.sourceModule" />
          </el-descriptions-item>
          <el-descriptions-item label="企业">{{ warningDetail.enterpriseName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="区域">{{ formatRegionName(warningDetail.regionCode, '-') }}</el-descriptions-item>
          <el-descriptions-item label="工单状态">
            <dict-tag :options="warnStatusOptions" :value="warningDetail.warnStatus" />
          </el-descriptions-item>
          <el-descriptions-item label="处置人">{{ warningDetail.assignName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ formatDateTime(warningDetail.createTime) }}</el-descriptions-item>
          <el-descriptions-item label="办结时间">{{ formatDateTime(warningDetail.resolveTime) }}</el-descriptions-item>
          <el-descriptions-item label="预警内容" :span="2">{{ warningDetail.content || '-' }}</el-descriptions-item>
          <el-descriptions-item label="证据链接" :span="2">{{ warningDetail.evidenceUrl || '-' }}</el-descriptions-item>
          <el-descriptions-item label="当前治理焦点" :span="2">{{ detailFocusText }}</el-descriptions-item>
        </el-descriptions>

        <div class="azb-detail-block">
          <h3>治理提示</h3>
          <div class="azb-tag-list">
            <el-tag v-for="item in detailHintTags" :key="item.label" :type="item.type" effect="plain">{{ item.label }}</el-tag>
          </div>
        </div>

        <div style="margin-top: 20px;">
          <h3 style="margin: 0 0 12px;">处置日志</h3>
          <el-table :data="warningLogs" empty-text="暂无处置日志">
            <el-table-column label="日志ID" prop="logId" width="90" />
            <el-table-column label="动作" prop="actionType" width="100" />
            <el-table-column label="前置状态" prop="beforeStatus" width="100" />
            <el-table-column label="后置状态" prop="afterStatus" width="100" />
            <el-table-column label="处置意见" prop="opinion" min-width="200" show-overflow-tooltip />
            <el-table-column label="处理人" prop="handlerName" width="100" />
            <el-table-column label="处理时间" width="180">
              <template #default="scope">
                <span>{{ formatDateTime(scope.row.handleTime) }}</span>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </template>
    </page-detail-dialog>

    <el-dialog title="预警处置" v-model="handleOpen" width="560px" append-to-body>
      <el-form ref="handleRef" :model="handleForm" :rules="handleRules" label-width="100px">
        <el-form-item label="处置动作" prop="action">
          <el-select v-model="handleForm.action" placeholder="请选择处置动作">
            <el-option v-for="item in handleActionOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="处置意见" prop="opinion">
          <el-input v-model="handleForm.opinion" type="textarea" :rows="4" placeholder="请输入处置意见" />
        </el-form-item>
        <el-form-item label="附件链接" prop="attachmentUrls">
          <el-input v-model="handleForm.attachmentUrls" placeholder="可选，多个附件请用英文逗号分隔" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitHandle">确定</el-button>
          <el-button @click="handleOpen = false">取消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="AzbWarning">
import { computed, getCurrentInstance, ref, watchEffect } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useWorkbenchAssist } from '@/composables/useWorkbenchAssist'
import { decoratePortalExplanationItems, openPortalExplanationAction } from '@/utils/portalExplanation'
import { useRoleViewMode } from '@/utils/roleView'
import { applyWorkbenchRouteQuery, buildWorkbenchContext, stripWorkbenchRouteQuery } from '@/utils/workbenchLink'
import {
  focusQueue,
  formatRegionName,
  matchWarningFocus,
  optionLabel,
  prioritizeFocusRows,
  sourceModuleOptions,
  summaryCard,
  useWarningPage,
  warnLevelOptions,
  warnStatusOptions
} from '@/views/warning/useWarningPage'

const { isReadOnlyRole, isInsurerRole, isBankRole, readOnlyRoleLabel, readOnlyRoleDescription } = useRoleViewMode()
const { proxy } = getCurrentInstance()
const route = useRoute()
const { setPageGuide } = useWorkbenchAssist()
const router = useRouter()

const activeFocusKey = ref('')

const {
  warningList,
  enterpriseOptions,
  warningDetail,
  warningLogs,
  warningSummary,
  currentWarning,
  loading,
  showSearch,
  total,
  detailOpen,
  handleOpen,
  queryParams,
  handleForm,
  handleRules,
  regionOptions,
  getWarningActionOptions: resolveWarningActionOptions,
  canHandleWarning: resolveCanHandleWarning,
  loadAll,
  loadEnterpriseOptions,
  handleQuery,
  handleExport,
  handleRowClick,
  openDetail,
  openHandleDialog,
  submitHandle
} = useWarningPage({
  exportFilePrefix: 'azb_warning',
  canHandle: () => !isReadOnlyRole.value,
  onBlockedAction: blockReadOnlyAction,
  getCurrentList: () => visibleWarningList.value,
  afterList: syncActiveFocus,
  immediate: false
})

const portalExplanations = computed(() => warningSummary.value.azbExplanation || [])
const portalExplanationItems = computed(() => decoratePortalExplanationItems(portalExplanations.value, {
  portalCode: 'azb',
  panelTitle: '6.1 治理解释',
  panelDescription: '风险排序、治理建议和推荐下钻统一来自安责保解释聚合接口。'
}))

const roleView = computed(() => {
  if (isBankRole.value) return 'bank'
  if (isInsurerRole.value) return 'insurer'
  return 'emergency'
})

const roleBadge = computed(() => {
  if (roleView.value === 'bank') return '银行只读协同'
  if (roleView.value === 'insurer') return '保险只读协同'
  return '应急监管处置'
})

const roleTitle = computed(() => {
  if (roleView.value === 'bank') return '跨部门风险复核台账'
  if (roleView.value === 'insurer') return '设备与工伤联动风险台账'
  return '监管预警治理台账'
})

const roleDescription = computed(() => {
  if (roleView.value === 'bank') {
    return '面向银行协同查看区域风险分布、升级态势和办结结果，只做只读复核，不承担现场治理动作。'
  }
  if (roleView.value === 'insurer') {
    return '面向保险协同重点识别设备联动、工伤事件和升级处置风险，便于继续跨模块复核。'
  }
  return '面向应急监管统一查看红警、设备联动、工伤事件和升级处置预警，保持 6.1 治理解释和处置链一致。'
})

const summaryCards = computed(() => {
  if (roleView.value === 'bank') {
    return [
      summaryCard('redCount', '红警工单', warningSummary.value.redCount, '条', '优先复核高等级风险分布。', 'azb-summary-card--danger'),
      summaryCard('injuryCount', '工伤来源', warningSummary.value.injuryCount, '条', '关注跨部门协同和办结结果。', 'azb-summary-card--warning'),
      summaryCard('closedCount', '已办结', warningSummary.value.closedCount, '条', '用于回看治理结果沉淀。', 'azb-summary-card--success'),
      summaryCard('totalCount', '预警总量', warningSummary.value.totalCount, '条', '当前范围内的监管预警总量。', '')
    ]
  }
  if (roleView.value === 'insurer') {
    return [
      summaryCard('deviceCount', '设备 / AI 联动', warningSummary.value.deviceCount, '条', '优先查看设备侧高风险预警。', ''),
      summaryCard('upgradedCount', '升级处置', warningSummary.value.upgradedCount, '条', '重点关注仍在升级链上的工单。', 'azb-summary-card--danger'),
      summaryCard('injuryCount', '工伤来源', warningSummary.value.injuryCount, '条', '继续跟进事故类来源预警。', 'azb-summary-card--warning'),
      summaryCard('closedCount', '已办结', warningSummary.value.closedCount, '条', '用于回看处置结果和留痕。', 'azb-summary-card--success')
    ]
  }
  return [
    summaryCard('pendingCount', '待处理', warningSummary.value.pendingCount, '条', '仍需继续核实和分派的监管预警。', 'azb-summary-card--warning'),
    summaryCard('redCount', '红警工单', warningSummary.value.redCount, '条', '优先识别需要立即跟进的高等级工单。', 'azb-summary-card--danger'),
    summaryCard('deviceCount', '设备 / AI 联动', warningSummary.value.deviceCount, '条', '反映设备和智能感知侧风险总量。', ''),
    summaryCard('upgradedCount', '升级处置', warningSummary.value.upgradedCount, '条', '用于跟踪升级链上的重点对象。', 'azb-summary-card--success')
  ]
})

const focusQueues = computed(() => {
  if (roleView.value === 'bank') {
    return [
      focusQueue('red', '红警工单', warningSummary.value.redCount, '条', '优先复核高等级风险分布和办结结果。', '优先复核'),
      focusQueue('injury', '工伤来源', warningSummary.value.injuryCount, '条', '查看事故类来源预警的后续状态。', '查看事故来源'),
      focusQueue('closed', '已办结', warningSummary.value.closedCount, '条', '回看治理结果与留痕完整性。', '回看结果')
    ]
  }
  if (roleView.value === 'insurer') {
    return [
      focusQueue('device', '设备 / AI 联动', warningSummary.value.deviceCount, '条', '优先查看设备感知和 AI 识别来源风险。', '查看设备风险'),
      focusQueue('upgrade', '升级处置', warningSummary.value.upgradedCount, '条', '跟进仍在升级链上的重点工单。', '跟进升级'),
      focusQueue('injury', '工伤来源', warningSummary.value.injuryCount, '条', '查看事故类来源预警后续状态。', '查看事故来源')
    ]
  }
  return [
    focusQueue('red', '红警工单', warningSummary.value.redCount, '条', '优先识别需要立即跟进的高等级工单。', '优先处置'),
    focusQueue('pending', '待处理', warningSummary.value.pendingCount, '条', '优先分派和核实仍未进入处置链的工单。', '推进分派'),
    focusQueue('device', '设备 / AI 联动', warningSummary.value.deviceCount, '条', '继续核查设备和智能感知来源风险。', '查看设备风险'),
    focusQueue('upgrade', '升级处置', warningSummary.value.upgradedCount, '条', '跟踪升级链上的重点对象。', '跟进升级')
  ]
})

const activeFocus = computed(() => {
  if (!focusQueues.value.length) {
    return null
  }
  return focusQueues.value.find(item => item.key === activeFocusKey.value) || focusQueues.value[0]
})

const visibleWarningList = computed(() => prioritizeFocusRows(warningList.value, row => matchWarningFocus(row, activeFocus.value?.key)))
const warningWorkbenchFields = ['enterpriseId', 'regionCode', 'warnLevel', 'sourceModule', 'warnStatus', 'content', 'focusKey']

const workbenchContext = computed(() => buildWorkbenchContext(route.query, {
  fields: warningWorkbenchFields,
  sourceLabel: '安责保工作台',
  title: '当前预警列表沿用了工作台来源条件',
  description: '当前列表保留了上游带入的企业、区域、级别、状态或解释焦点，便于继续治理和复核。',
  fieldLabels: {
    enterpriseId: '企业',
    regionCode: '区域',
    warnLevel: '预警级别',
    sourceModule: '来源模块',
    warnStatus: '工单状态',
    content: '预警内容',
    focusKey: '焦点队列'
  },
  fieldFormatters: {
    enterpriseId: value => {
      const matched = enterpriseOptions.value.find(item => String(item.enterpriseId) === String(value))
      return matched ? matched.enterpriseName : value
    },
    regionCode: value => formatRegionName(value, value),
    warnLevel: value => optionLabel(warnLevelOptions, value),
    sourceModule: value => optionLabel(sourceModuleOptions, value),
    warnStatus: value => optionLabel(warnStatusOptions, value),
    focusKey: value => warningFocusLabel(value)
  }
}))

const focusTableHint = computed(() => activeFocus.value?.desc || '按当前焦点优先暴露最需要复核的监管预警对象。')

const selectedWarningOverview = computed(() => {
  if (currentWarning.value) {
    return [
      { label: '预警类型', value: currentWarning.value.warnType || currentWarning.value.content || '-' },
      { label: '企业 / 区域', value: `${currentWarning.value.enterpriseName || '-'} / ${formatRegionName(currentWarning.value.regionCode, '-')}` },
      { label: '级别 / 状态', value: `${optionLabel(warnLevelOptions, currentWarning.value.warnLevel)} / ${optionLabel(warnStatusOptions, currentWarning.value.warnStatus)}` },
      { label: '来源模块', value: optionLabel(sourceModuleOptions, currentWarning.value.sourceModule) }
    ]
  }
  return [
    { label: '当前焦点', value: activeFocus.value?.title || '-' },
    { label: '当前数量', value: `${activeFocus.value?.count ?? 0}${activeFocus.value?.unit || ''}` },
    { label: '当前区域', value: formatRegionName(queryParams.value.regionCode) },
    { label: '主动作', value: activeFocus.value?.actionText || '-' }
  ]
})

const primaryWarningAction = computed(() => {
  if (!currentWarning.value) {
    return { label: activeFocus.value?.actionText || '查看详情', action: 'detail' }
  }
  if (isReadOnlyRole.value) {
    return { label: '查看详情', action: 'detail' }
  }
  if (resolveCanHandleWarning(currentWarning.value)) {
    return { label: '处置工单', action: 'handle' }
  }
  return { label: '查看详情', action: 'detail' }
})

const currentWarningActionSummary = computed(() => {
  if (!currentWarning.value) {
    return activeFocus.value
      ? `当前已按“${activeFocus.value.title}”重排列表，可继续查看摘要、详情或导出当前条件。`
      : '请选择一条预警查看当前治理建议。'
  }
  if (isReadOnlyRole.value) {
    if (roleView.value === 'bank') {
      return '当前角色仅保留风险复核、详情查看和导出，不执行现场治理动作。'
    }
    return '当前角色仅保留协同复核、详情查看和导出，不执行预警处置动作。'
  }
  if (String(currentWarning.value.warnStatus || '') === '0') {
    return '当前工单仍待处理，建议先核实来源和责任对象，再进入处置链。'
  }
  if (String(currentWarning.value.warnStatus || '') === '1') {
    return '当前工单已进入处理中，建议补齐处置意见、附件和办结目标。'
  }
  if (String(currentWarning.value.warnStatus || '') === '4') {
    return '当前工单已升级，需要持续跟踪上级处置结果和回写留痕。'
  }
  return '当前工单状态较稳定，可继续回看详情、日志和来源证据。'
})

const currentWarningActionTags = computed(() => buildWarningHintTags(currentWarning.value || undefined, roleView.value, activeFocus.value))
const detailHintTags = computed(() => buildWarningHintTags(warningDetail.value || currentWarning.value, roleView.value, activeFocus.value))
const handleActionOptions = computed(() => resolveWarningActionOptions(currentWarning.value))

function canHandleWarningRow(row) {
  return resolveCanHandleWarning(row)
}

const workflowSteps = computed(() => {
  if (roleView.value === 'bank') {
    return [
      { label: '先看高风险分布', desc: '先确认红警和事故来源预警数量，再判断是否需要继续下钻复核。' },
      { label: '回看办结结果', desc: '查看已办结对象是否形成完整留痕和稳定结论。' },
      { label: '保留协同记录', desc: '将当前查看结果作为跨部门协同复核底数。' }
    ]
  }
  if (roleView.value === 'insurer') {
    return [
      { label: '先看设备与工伤来源', desc: '优先识别设备联动和工伤事件来源预警。' },
      { label: '跟进升级对象', desc: '查看升级处置链是否持续推进并有回写。' },
      { label: '沉淀协同意见', desc: '保留当前风险复核和协同结论，便于后续跟进。' }
    ]
  }
  return [
    { label: '识别重点风险', desc: '优先分辨红警、待处理和设备来源预警。' },
    { label: '推进处置分派', desc: '尽快让高风险工单进入处理或升级链。' },
    { label: '沉淀办结留痕', desc: '最后保留处置意见、日志和证据链接，形成治理闭环。' }
  ]
})

const hintTags = computed(() => {
  const tags = []
  if (Number(warningSummary.value.redCount || 0) > 0) {
    tags.push({ label: `当前仍有 ${warningSummary.value.redCount || 0} 条红警工单，建议优先跟进。`, type: 'danger' })
  }
  if (Number(warningSummary.value.upgradedCount || 0) > 0) {
    tags.push({ label: `当前有 ${warningSummary.value.upgradedCount || 0} 条升级工单，需要持续查看升级链结果。`, type: 'warning' })
  }
  if (Number(warningSummary.value.deviceCount || 0) > 0 && roleView.value !== 'bank') {
    tags.push({ label: `设备 / AI 联动预警 ${warningSummary.value.deviceCount || 0} 条，建议联动设备模块继续复核。`, type: 'info' })
  }
  if (Number(warningSummary.value.injuryCount || 0) > 0) {
    tags.push({ label: `工伤来源预警 ${warningSummary.value.injuryCount || 0} 条，建议关注办结时效和证据材料。`, type: 'warning' })
  }
  if (!tags.length) {
    tags.push({ label: '当前范围内监管预警态势平稳，可继续通过解释面板或来源条件下钻复核。', type: 'success' })
  }
  return tags
})

const readOnlyAlertTitle = computed(() => `${readOnlyRoleLabel.value}：当前仅保留摘要、详情和导出`)
const readOnlyAlertDescription = computed(() => {
  if (isBankRole.value) {
    return `${readOnlyRoleDescription.value} 当前页面仅承担跨部门风险复核，不展示现场治理动作。`
  }
  if (isInsurerRole.value) {
    return `${readOnlyRoleDescription.value} 当前页面仅承担保险协同复核，不展示预警处置动作。`
  }
  return `${readOnlyRoleDescription.value} 当前页面仅保留只读复核能力。`
})

const detailFocusText = computed(() => {
  if (roleView.value === 'bank') return '当前详情主要用于跨部门复核风险分布、办结结果和证据留痕。'
  if (roleView.value === 'insurer') return '当前详情主要用于保险侧复核设备联动、工伤来源和升级状态。'
  return '当前详情主要用于监管侧复核红警、升级处置、来源模块和证据留痕。'
})

function resolveWarningFocusKey(value) {
  const normalized = String(value || '')
  return ['red', 'pending', 'device', 'upgrade', 'injury', 'closed'].includes(normalized) ? normalized : ''
}

function warningFocusLabel(value) {
  if (value === 'red') return '红警工单'
  if (value === 'pending') return '待处理'
  if (value === 'device') return '设备 / AI 联动'
  if (value === 'upgrade') return '升级处置'
  if (value === 'injury') return '工伤来源'
  if (value === 'closed') return '已办结'
  return value || '-'
}

function syncActiveFocus() {
  if (!activeFocusKey.value || !focusQueues.value.some(item => item.key === activeFocusKey.value)) {
    activeFocusKey.value = focusQueues.value[0]?.key || ''
  }
}

function handleSelectFocus(item) {
  activeFocusKey.value = item.key
  if (visibleWarningList.value.length > 0) {
    handleRowClick(visibleWarningList.value[0])
  }
}

function blockReadOnlyAction(actionLabel) {
  if (!isReadOnlyRole.value) {
    return false
  }
  proxy.$modal.msgWarning(`${readOnlyRoleLabel.value}当前仅保留摘要、详情和导出，不能${actionLabel}`)
  return true
}

function handlePrimaryWarningAction() {
  if (!currentWarning.value) {
    return
  }
  if (primaryWarningAction.value.action === 'handle') {
    if (blockReadOnlyAction('处置工单')) {
      return
    }
    openHandleDialog(currentWarning.value)
    return
  }
  openDetail(currentWarning.value)
}

function buildWarningHintTags(warning, role, focus) {
  const tags = []
  if (!warning) {
    if (focus?.title) {
      tags.push({ label: `当前焦点：${focus.title}`, type: 'info' })
      tags.push({ label: `优先动作：${focus.actionText}`, type: 'warning' })
    }
    if (!tags.length) {
      tags.push({ label: '请选择一条预警查看治理提示。', type: 'info' })
    }
    return tags
  }
  if (warning.warnLevel === '3') {
    tags.push({ label: '红警工单，建议优先跟进和督办。', type: 'danger' })
  }
  if (warning.warnStatus === '0') {
    tags.push({ label: '当前待处理，建议先核实来源和责任对象。', type: 'warning' })
  }
  if (warning.warnStatus === '1') {
    tags.push({ label: '当前处理中，建议补齐处置意见和附件。', type: 'info' })
  }
  if (warning.warnStatus === '4') {
    tags.push({ label: '当前已升级，需要继续跟踪升级链结果。', type: 'danger' })
  }
  if (warning.sourceModule === 'DEVICE') {
    tags.push({ label: '设备联动来源，建议联动设备模块继续复核。', type: 'info' })
  }
  if (warning.sourceModule === 'INJURY') {
    tags.push({ label: '工伤来源，建议关注办结时效和证据材料。', type: 'warning' })
  }
  if (warning.sourceModule === 'SPECIAL') {
    tags.push({ label: '专项治理来源，建议回看专项治理链路结果。', type: 'warning' })
  }
  if (role === 'bank') {
    tags.push({ label: '当前角色为只读协同，不执行现场处置动作。', type: 'info' })
  }
  if (role === 'insurer') {
    tags.push({ label: '当前角色为保险协同，重点关注设备和工伤来源。', type: 'success' })
  }
  if (!tags.length) {
    tags.push({ label: '当前预警信息完整，可继续查看详情和日志。', type: 'success' })
  }
  return tags
}

function formatDateTime(value) {
  if (!value) {
    return '-'
  }
  return proxy.parseTime(value, '{y}-{m}-{d} {h}:{i}:{s}')
}

function resetQuery() {
  proxy.resetForm('queryRef')
  Object.assign(queryParams.value, {
    pageNum: 1,
    pageSize: 10,
    regionCode: undefined,
    enterpriseId: undefined,
    warnLevel: undefined,
    sourceModule: undefined,
    warnStatus: undefined,
    content: undefined
  })
  applyWorkbenchRouteQuery(route.query, queryParams.value, warningWorkbenchFields)
  activeFocusKey.value = resolveWarningFocusKey(route.query.focusKey)
  loadAll()

watchEffect(() => {
  setPageGuide({
    title: roleTitle.value || '????????',
    description: roleDescription.value || '?????????????????????????????????',
    portalExplanation: portalExplanationItems.value,
    focus: focusQueues.value,
    selection: [...selectedWarningOverview.value, { label: '??????', value: currentWarningActionSummary.value }],
    workflow: workflowSteps.value,
    hints: [...currentWarningActionTags.value, ...hintTags.value].slice(0, 6)
  })
})

}

activeFocusKey.value = resolveWarningFocusKey(route.query.focusKey)

function clearWorkbenchContext() {
  activeFocusKey.value = ''
  Object.assign(queryParams.value, {
    pageNum: 1,
    pageSize: 10,
    enterpriseId: undefined,
    regionCode: undefined,
    warnLevel: undefined,
    sourceModule: undefined,
    warnStatus: undefined,
    content: undefined,
    focusKey: undefined
  })
  router.replace({
    path: route.path,
    query: stripWorkbenchRouteQuery(route.query, warningWorkbenchFields)
  })
  loadAll()
}

function applyWarningWorkbenchQuery(routeQuery = {}) {
  Object.assign(queryParams.value, {
    pageNum: 1,
    pageSize: 10,
    regionCode: undefined,
    enterpriseId: undefined,
    warnLevel: undefined,
    sourceModule: undefined,
    warnStatus: undefined,
    content: undefined,
    focusKey: undefined
  })
  applyWorkbenchRouteQuery(routeQuery, queryParams.value, warningWorkbenchFields)
  activeFocusKey.value = resolveWarningFocusKey(routeQuery.focusKey)
  loadAll()
}

function handlePortalExplanationAction(action) {
  if (action?.path === route.path) {
    applyWarningWorkbenchQuery(action.query || {})
  }
  openPortalExplanationAction(router, action)
}

applyWorkbenchRouteQuery(route.query, queryParams.value, warningWorkbenchFields)
loadEnterpriseOptions()
loadAll()
</script>

<style scoped lang="scss">
.azb-warning-page {
  .azb-role-alert,
  .azb-workbench-alert {
    margin-bottom: 16px;
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
    gap: 8px 12px;
    margin-bottom: 12px;
    color: #47636d;
    line-height: 1.7;
    font-size: 13px;
  }

  .azb-workbench-alert__desc strong {
    color: #0b6b78;
  }

  .azb-page__tip {
    display: grid;
    gap: 10px;
    min-width: min(420px, 100%);
    padding: 18px 20px;
    border-radius: 18px;
    background: linear-gradient(135deg, rgba(11, 107, 120, 0.12), rgba(11, 107, 120, 0.03));
  }

  .azb-page__tip-item {
    color: #325f69;
    line-height: 1.7;
    font-size: 13px;
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
    border: 1px solid #d7e3e8;
    border-radius: 16px;
    background: #fff;
  }

  .azb-summary-card {
    padding: 18px 20px;
  }

  .azb-summary-card__label {
    color: #5f7580;
    font-size: 13px;
  }

  .azb-summary-card__value {
    margin-top: 10px;
    color: #10353b;
    font-size: 28px;
    font-weight: 700;
  }

  .azb-summary-card__unit {
    margin-left: 4px;
    font-size: 13px;
    font-weight: 500;
    color: #6f8790;
  }

  .azb-summary-card__note {
    margin-top: 10px;
    color: #5a717a;
    line-height: 1.7;
    font-size: 13px;
  }

  .azb-summary-card--success {
    background: linear-gradient(180deg, #ffffff 0%, #f3fbf8 100%);
  }

  .azb-summary-card--warning {
    background: linear-gradient(180deg, #ffffff 0%, #fff9ef 100%);
  }

  .azb-summary-card--danger {
    background: linear-gradient(180deg, #ffffff 0%, #fff5f4 100%);
  }

  .azb-focus-card :deep(.el-card__header) {
    padding: 18px 20px 10px;
    border-bottom: none;
  }

  .azb-focus-card :deep(.el-card__body) {
    padding: 0 20px 20px;
  }

  .azb-card-head {
    display: flex;
    flex-direction: column;
    gap: 6px;
  }

  .azb-card-head--between {
    flex-direction: row;
    align-items: flex-end;
    justify-content: space-between;
    gap: 20px;
  }

  .azb-card-head__title {
    color: #163840;
    font-size: 16px;
    font-weight: 700;
  }

  .azb-card-head__desc {
    color: #5e747d;
    line-height: 1.7;
    font-size: 13px;
  }

  .azb-focus-queue-list,
  .azb-pipeline-list {
    display: grid;
    gap: 12px;
  }

  .azb-focus-queue {
    display: flex;
    align-items: center;
    justify-content: space-between;
    width: 100%;
    padding: 14px 16px;
    border: 1px solid #d7e3e8;
    border-radius: 14px;
    background: #fff;
    text-align: left;
    cursor: pointer;
    transition: border-color 0.2s ease, box-shadow 0.2s ease, transform 0.2s ease;
  }

  .azb-focus-queue:hover,
  .azb-focus-queue.is-active {
    border-color: #0b6b78;
    box-shadow: 0 10px 24px rgba(11, 107, 120, 0.08);
    transform: translateY(-1px);
  }

  .azb-focus-queue__main {
    display: grid;
    gap: 6px;
  }

  .azb-focus-queue__main strong {
    color: #163840;
    font-size: 15px;
  }

  .azb-focus-queue__main p {
    margin: 0;
    color: #627981;
    line-height: 1.7;
    font-size: 13px;
  }

  .azb-focus-queue__side {
    display: grid;
    gap: 8px;
    justify-items: end;
    margin-left: 20px;
  }

  .azb-focus-queue__count {
    color: #0b6b78;
    font-size: 22px;
    font-weight: 700;
  }

  .azb-focus-queue__action {
    color: #6a8189;
    font-size: 12px;
  }

  .azb-source-list {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 12px;
  }

  .azb-source-item {
    padding: 14px 16px;
    border-radius: 14px;
    background: #f6fafb;
  }

  .azb-source-item__label {
    color: #5e747d;
    font-size: 13px;
  }

  .azb-source-item__value {
    margin-top: 8px;
    color: #163840;
    font-weight: 700;
  }

  .azb-recommend-panel {
    margin-top: 14px;
    padding: 14px 16px;
    border-radius: 14px;
    background: linear-gradient(180deg, #f5fbfc 0%, #ffffff 100%);
  }

  .azb-recommend-panel__title {
    color: #0b6b78;
    font-size: 13px;
    font-weight: 700;
  }

  .azb-recommend-panel__summary {
    margin: 8px 0 12px;
    color: #4e656d;
    line-height: 1.8;
    font-size: 13px;
  }

  .azb-tag-list {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
  }

  .azb-focus-actions {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
    margin-top: 14px;
  }

  .azb-pipeline-item {
    display: flex;
    gap: 12px;
    padding: 12px 0;
    border-bottom: 1px dashed #e0eaed;
  }

  .azb-pipeline-item:last-child {
    border-bottom: none;
  }

  .azb-pipeline-item__index {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    width: 36px;
    height: 36px;
    border-radius: 12px;
    background: rgba(11, 107, 120, 0.08);
    color: #0b6b78;
    font-weight: 700;
  }

  .azb-pipeline-item__body strong {
    display: block;
    margin-bottom: 6px;
    color: #163840;
  }

  .azb-pipeline-item__body p {
    margin: 0;
    color: #60767e;
    line-height: 1.7;
    font-size: 13px;
  }

  .azb-detail-block h3 {
    margin: 16px 0 12px;
    color: #163840;
    font-size: 16px;
  }

  @media (max-width: 1200px) {
    .azb-summary-grid {
      grid-template-columns: repeat(2, minmax(0, 1fr));
    }
  }

  @media (max-width: 768px) {
    .azb-summary-grid,
    .azb-focus-grid,
    .azb-source-list {
      grid-template-columns: minmax(0, 1fr);
    }

    .azb-card-head--between {
      flex-direction: column;
      align-items: flex-start;
    }

    .azb-focus-queue {
      flex-direction: column;
      align-items: flex-start;
      gap: 12px;
    }

    .azb-focus-queue__side {
      justify-items: start;
      margin-left: 0;
    }
  }
}
</style>
