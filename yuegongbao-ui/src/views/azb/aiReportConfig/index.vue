<template>
  <div class="app-container azb-page azb-ai-config-workbench">
    <section class="gov-page-header azb-page__header">
      <div>
        <p class="azb-page__eyebrow">AI模型治理</p>
        <h1 class="azb-page__title">{{ roleTitle }}</h1>
        <p class="azb-page__desc">
          {{ roleDescription }}
          当前继续复用统一评分配置中心，但安责保前端已按应急监管、企业风险复核、保险机构和银行协同重排版本治理焦点、启用顺序和详情重点。
        </p>
      </div>
      <div class="azb-page__tip">
        <div class="azb-page__tip-item">当前视角：{{ roleBadge }}</div>
        <div class="azb-page__tip-item">优先关注：当前启用版本、待生效版本、闭环目标值和异常权重分布。</div>
        <div class="azb-page__tip-item">启用版本会联动当前区域 AI 报告中心，不额外维护第二套模型表。</div>
        <div class="azb-page__tip-item">后续如拆安责保独立评分服务，仍以本页作为版本台账承接入口。</div>
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

    <el-alert
      v-if="isReadOnlyRole"
      :title="readOnlyAlertTitle"
      :description="readOnlyAlertDescription"
      type="info"
      :closable="false"
      show-icon
      style="margin-bottom: 18px;"
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

    <el-card class="search-card azb-search-card" shadow="never">
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

    <el-card class="toolbar-card azb-toolbar-card" shadow="never">
      <el-row :gutter="10">
        <el-col v-if="!isReadOnlyRole" :span="1.5">
          <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['ygb:aiReportConfig:add']">新增版本</el-button>
        </el-col>
        <el-col v-if="!isReadOnlyRole" :span="1.5">
          <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['ygb:aiReportConfig:edit']">修改版本</el-button>
        </el-col>
        <el-col v-if="!isReadOnlyRole" :span="1.5">
          <el-button type="warning" plain icon="Select" :disabled="single" @click="handleActivate()" v-hasPermi="['ygb:aiReportConfig:activate']">启用版本</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="info" plain icon="Download" @click="handleExport" v-hasPermi="['ygb:aiReportConfig:export']">导出</el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>
    </el-card>

    <el-card class="table-card azb-table-card" shadow="never">
      <template #header>
        <div class="azb-card-head azb-card-head--between">
          <div>
            <div class="azb-card-head__title">模型版本台账</div>
            <div class="azb-card-head__desc">{{ focusTableHint }}</div>
          </div>
          <div class="azb-card-head__desc">当前总量 {{ total }} 条</div>
        </div>
      </template>
      <el-table v-loading="loading" :data="visibleConfigList" @selection-change="handleSelectionChange" @row-click="handleRowClick">
        <el-table-column v-if="!isReadOnlyRole" type="selection" width="55" align="center" />
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

        <el-table-column label="操作" fixed="right" align="center" width="220" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-button link type="info" icon="View" @click.stop="openDetail(scope.row)">详情</el-button>
            <template v-if="!isReadOnlyRole">
              <el-button link type="primary" icon="Edit" @click.stop="handleUpdate(scope.row)" v-hasPermi="['ygb:aiReportConfig:edit']">修改</el-button>
              <el-button link type="primary" icon="Select" @click.stop="handleActivate(scope.row)" v-hasPermi="['ygb:aiReportConfig:activate']">启用</el-button>
            </template>
          </template>
        </el-table-column>
      </el-table>
      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>

    <el-dialog :title="title" v-model="open" width="920px" append-to-body>
      <el-form ref="configRef" :model="form" :rules="rules" label-width="110px">
        <div class="azb-config-form-grid">
          <el-form-item label="区域" prop="regionCode">
            <el-select v-model="form.regionCode" style="width: 100%">
              <el-option v-for="item in regionOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="版本" prop="version">
            <el-input v-model="form.version" placeholder="例如：AZB-V2026.06-GD" />
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

        <div class="azb-config-section">
          <h3>维度权重</h3>
          <div class="azb-config-form-grid">
            <el-form-item label="A 安责险覆盖" prop="weightA">
              <el-input-number v-model="form.weightA" :min="0" :max="100" :step="1" style="width: 100%" />
            </el-form-item>
            <el-form-item label="B 作业准入" prop="weightB">
              <el-input-number v-model="form.weightB" :min="0" :max="100" :step="1" style="width: 100%" />
            </el-form-item>
            <el-form-item label="C 技防联动" prop="weightC">
              <el-input-number v-model="form.weightC" :min="0" :max="100" :step="1" style="width: 100%" />
            </el-form-item>
            <el-form-item label="D 设备工伤" prop="weightD">
              <el-input-number v-model="form.weightD" :min="0" :max="100" :step="1" style="width: 100%" />
            </el-form-item>
            <el-form-item label="E 预警处置" prop="weightE">
              <el-input-number v-model="form.weightE" :min="0" :max="100" :step="1" style="width: 100%" />
            </el-form-item>
          </div>
        </div>

        <div class="azb-config-section">
          <h3>目标值</h3>
          <div class="azb-config-form-grid">
            <el-form-item label="安责险覆盖率%" prop="contractRate">
              <el-input-number v-model="form.contractRate" :min="0" :max="100" :step="1" style="width: 100%" />
            </el-form-item>
            <el-form-item label="作业准入率%" prop="attendanceRate">
              <el-input-number v-model="form.attendanceRate" :min="0" :max="100" :step="1" style="width: 100%" />
            </el-form-item>
            <el-form-item label="技防联动率%" prop="paySuccessRate">
              <el-input-number v-model="form.paySuccessRate" :min="0" :max="100" :step="1" style="width: 100%" />
            </el-form-item>
            <el-form-item label="设备在线率%" prop="onlineRate">
              <el-input-number v-model="form.onlineRate" :min="0" :max="100" :step="1" style="width: 100%" />
            </el-form-item>
            <el-form-item label="工伤发生率‰" prop="injuryRate">
              <el-input-number v-model="form.injuryRate" :min="0" :max="100" :step="0.1" :precision="1" style="width: 100%" />
            </el-form-item>
            <el-form-item label="预警处置率%" prop="warningCloseRate">
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
          <el-descriptions-item label="当前视角重点" :span="2">{{ detailFocusText }}</el-descriptions-item>
          <el-descriptions-item label="权重概览" :span="2">{{ weightSummary(detailConfig.dimensionWeights) }}</el-descriptions-item>
          <el-descriptions-item label="目标概览" :span="2">{{ targetSummary(detailConfig.targetValues) }}</el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ detailConfig.remark || '-' }}</el-descriptions-item>
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

<script setup name="AzbAiReportConfig">
import { computed, getCurrentInstance, ref, watchEffect } from 'vue'
import { useWorkbenchAssist } from '@/composables/useWorkbenchAssist'
import { useRoute, useRouter } from 'vue-router'
import useUserStore from '@/store/modules/user'
import { decoratePortalExplanationItems, openPortalExplanationAction } from '@/utils/portalExplanation'
import { applyWorkbenchRouteQuery, buildWorkbenchContext, stripWorkbenchRouteQuery } from '@/utils/workbenchLink'
import {
  buildHintTags,
  focusQueue,
  formatPercent,
  matchConfigFocus,
  parseJson,
  prioritizeFocusRows,
  regionNameMap,
  summaryCard,
  targetSummary,
  useAiReportConfigPage,
  valueOrDefault,
  weightSummary
} from '@/views/aiReportConfig/useAiReportConfigPage'
import { useRoleViewMode } from '@/utils/roleView'

const { proxy } = getCurrentInstance()
const { setPageGuide } = useWorkbenchAssist()
const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const { isReadOnlyRole, isBankRole, isInsurerRole, readOnlyRoleLabel, readOnlyRoleDescription } = useRoleViewMode()
const activeFocusKey = ref('')
const aiReportConfigWorkbenchFields = ['regionCode', 'focusKey']
const aiReportConfigInitialQuery = {}

applyWorkbenchRouteQuery(route.query, aiReportConfigInitialQuery, aiReportConfigWorkbenchFields)

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
  if (roleView.value === 'site-enterprise') return '企业风险复核'
  return '应急监管处置'
})

const roleTitle = computed(() => {
  if (roleView.value === 'bank') return 'AI模型版本与信用口径复核看板'
  if (roleView.value === 'insurer') return '模型版本与风险口径协同复核台账'
  if (roleView.value === 'site-enterprise') return '企业评分口径与整改阈值复核工作台'
  return 'AI评分模型版本治理工作台'
})

const roleDescription = computed(() => {
  if (roleView.value === 'bank') {
    return '围绕启用版本、闭环目标值和区域覆盖做只读复核，用于辅助联合风控和信用解释口径判断。'
  }
  if (roleView.value === 'insurer') {
    return '围绕启用版本、目标值和异常权重分布做协同复核，优先判断当前风险研判口径是否稳定。'
  }
  if (roleView.value === 'site-enterprise') {
    return '围绕企业当前适用版本、闭环目标和维度权重做复核，重点是判断评分口径是否影响当前整改优先级。'
  }
  return '围绕启用版本、待生效版本、闭环目标值和权重总和组织应急监管工作流，优先压降评分口径偏移风险。'
})

function buildAiReportConfigExplanationQuery(extraQuery = {}) {
  return {
    regionCode: queryParams.value.regionCode,
    configStatus: queryParams.value.configStatus,
    ...extraQuery
  }
}

const {
  regionOptions,
  statusOptions,
  configList,
  loading,
  showSearch,
  open,
  detailOpen,
  total,
  ids,
  single,
  title,
  currentConfigRow,
  detailConfig,
  summaryData,
  queryParams,
  form,
  rules,
  syncCurrentConfig,
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
  init
} = useAiReportConfigPage({
  exportFilePrefix: 'azb_ai_report_config',
  canMutate: () => !isReadOnlyRole.value,
  onBlockedAction: actionLabel => {
    proxy.$modal.msgWarning(`${readOnlyRoleLabel.value}仅保留摘要、详情和导出，不能${actionLabel}`)
  },
  getCurrentList: () => visibleConfigList.value,
  afterList: () => {
    syncActiveFocus()
  },
  immediate: false
})

Object.assign(queryParams.value, aiReportConfigInitialQuery)

const summaryCards = computed(() => {
  if (roleView.value === 'bank') {
    return [
      summaryCard('active', '启用版本', valueOrDefault(summaryData.value.activeCount, 0), '个', '辅助联合风控时优先确认当前生效口径。', 'azb-summary-card--success'),
      summaryCard('covered', '覆盖区域', valueOrDefault(summaryData.value.coveredRegionCount, 0), '个', '用于判断模型口径覆盖面是否完整。', ''),
      summaryCard('closeRate', '闭环目标', formatPercent(summaryData.value.activeWarningCloseRate), '', '辅助判断当前区域治理阈值是否偏松。', 'azb-summary-card--warning'),
      summaryCard('weight', '平均权重和', valueOrDefault(summaryData.value.averageWeightSum, 0), '', '快速识别是否存在明显权重异常。', 'azb-summary-card--danger')
    ]
  }
  if (roleView.value === 'insurer') {
    return [
      summaryCard('active', '启用版本', valueOrDefault(summaryData.value.activeCount, 0), '个', '优先确认当前风险研判所用版本。', 'azb-summary-card--success'),
      summaryCard('upcoming', '待生效版本', valueOrDefault(summaryData.value.upcomingCount, 0), '个', '帮助识别即将切换的风险研判口径。', 'azb-summary-card--warning'),
      summaryCard('closeRate', '闭环目标', formatPercent(summaryData.value.activeWarningCloseRate), '', '用于判断事故预防协同阈值是否合理。', ''),
      summaryCard('covered', '覆盖区域', valueOrDefault(summaryData.value.coveredRegionCount, 0), '个', '反映当前模型配置的覆盖范围。', 'azb-summary-card--danger')
    ]
  }
  if (roleView.value === 'site-enterprise') {
    return [
      summaryCard('active', '启用版本', valueOrDefault(summaryData.value.activeCount, 0), '个', '企业当前最应确认的评分口径版本数量。', 'azb-summary-card--success'),
      summaryCard('closeRate', '闭环目标', formatPercent(summaryData.value.activeWarningCloseRate), '', '帮助判断当前整改阈值和预警要求。', 'azb-summary-card--warning'),
      summaryCard('weight', '平均权重和', valueOrDefault(summaryData.value.averageWeightSum, 0), '', '用于判断是否存在权重配置偏移。', 'azb-summary-card--danger'),
      summaryCard('upcoming', '待生效版本', valueOrDefault(summaryData.value.upcomingCount, 0), '个', '提前识别即将影响企业评分解释口径的版本。', '')
    ]
  }
  return [
    summaryCard('total', '配置版本', valueOrDefault(summaryData.value.totalCount, total.value), '个', '当前筛选条件下的模型版本总量。', ''),
    summaryCard('active', '启用版本', valueOrDefault(summaryData.value.activeCount, 0), '个', '当前已启用、直接服务 AI 报告中心的版本数量。', 'azb-summary-card--success'),
    summaryCard('upcoming', '待生效版本', valueOrDefault(summaryData.value.upcomingCount, 0), '个', '可作为下阶段切换储备的版本数量。', 'azb-summary-card--warning'),
    summaryCard('covered', '覆盖区域', valueOrDefault(summaryData.value.coveredRegionCount, 0), '个', '当前已配置模型的区域数量。', 'azb-summary-card--danger')
  ]
})

const focusQueues = computed(() => {
  if (roleView.value === 'bank') {
    return [
      focusQueue('active', '当前启用版本', valueOrDefault(summaryData.value.activeCount, 0), '个', '先确认当前区域信用解释口径是否稳定。', '查看当前版本'),
      focusQueue('closeRate', '闭环目标复核', formatPercent(summaryData.value.activeWarningCloseRate), '', '闭环目标偏低时更值得联合复核。', '查看目标值'),
      focusQueue('covered', '区域覆盖复核', valueOrDefault(summaryData.value.coveredRegionCount, 0), '个', '覆盖不足时更适合回到区域做联合判断。', '查看覆盖区域')
    ]
  }
  if (roleView.value === 'insurer') {
    return [
      focusQueue('active', '当前启用版本', valueOrDefault(summaryData.value.activeCount, 0), '个', '优先确认当前风险研判口径。', '查看启用版本'),
      focusQueue('upcoming', '待生效版本', valueOrDefault(summaryData.value.upcomingCount, 0), '个', '提前判断即将切换的风险服务口径。', '查看待生效版本'),
      focusQueue('weight', '权重总和', valueOrDefault(summaryData.value.averageWeightSum, 0), '', '权重异常更值得优先进入详情复核。', '查看权重分布')
    ]
  }
  if (roleView.value === 'site-enterprise') {
    return [
      focusQueue('closeRate', '闭环目标复核', formatPercent(summaryData.value.activeWarningCloseRate), '', '先确认当前整改目标是否与企业实际治理压力匹配。', '查看闭环目标'),
      focusQueue('active', '启用版本', valueOrDefault(summaryData.value.activeCount, 0), '个', '企业优先确认当前评分解释口径。', '查看当前版本'),
      focusQueue('upcoming', '待生效版本', valueOrDefault(summaryData.value.upcomingCount, 0), '个', '提前识别即将影响企业评分解释的版本。', '查看待生效版本')
    ]
  }
  return [
    focusQueue('active', '当前启用版本', valueOrDefault(summaryData.value.activeCount, 0), '个', '应急侧先锁定当前实际生效的评分口径。', '进入启用版本'),
    focusQueue('upcoming', '待生效版本', valueOrDefault(summaryData.value.upcomingCount, 0), '个', '待生效版本越多，越值得提前复核切换风险。', '查看待生效版本'),
    focusQueue('weight', '权重总和', valueOrDefault(summaryData.value.averageWeightSum, 0), '', '权重总和异常说明评分口径可能存在偏移。', '查看权重异常')
  ]
})

const fallbackPortalExplanations = computed(() => focusQueues.value.map(item => ({
  key: item.key,
  dimensionName: item.title,
  currentValue: item.count,
  targetValue: '-',
  summary: item.desc,
  evidenceModule: 'aiReportConfig',
  recommendModule: 'aiReportConfig',
  defaultQuery: buildAiReportConfigExplanationQuery({ focusKey: item.key }),
  sourceLabel: '6.1 AI 配置治理解释',
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
  panelTitle: '6.1 AI 配置治理解释',
  panelDescription: '当前解释项按 6.1 治理链口径展示 AI 配置风险、分层和联动方向。'
}))

const activeFocus = computed(() => {
  if (!focusQueues.value.length) return null
  return focusQueues.value.find(item => item.key === activeFocusKey.value) || focusQueues.value[0]
})

const visibleConfigList = computed(() => prioritizeFocusRows(configList.value, row => matchConfigFocus(row, activeFocus.value?.key)))

const focusTableHint = computed(() => {
  if (!activeFocus.value) {
    return '当前按默认顺序展示模型版本台账。'
  }
  return `${activeFocus.value.title}优先置顶，便于先复核最可能影响当前风险研判和评分口径的配置版本。`
})

const selectedConfigOverview = computed(() => {
  if (!currentConfigRow.value) {
    return [
      { label: '当前焦点', value: activeFocus.value?.title || '-' },
      { label: '焦点数量', value: `${activeFocus.value?.count ?? 0}${activeFocus.value?.unit || ''}` },
      { label: '当前区域', value: regionNameMap[queryParams.value.regionCode] || queryParams.value.regionCode || '全部区域' },
      { label: '下一步', value: activeFocus.value?.actionText || '-' }
    ]
  }
  const targets = parseJson(currentConfigRow.value.targetValues)
  return [
    { label: '区域', value: currentConfigRow.value.regionName || regionNameMap[currentConfigRow.value.regionCode] || currentConfigRow.value.regionCode || '-' },
    { label: '版本', value: currentConfigRow.value.version || '-' },
    { label: '状态', value: currentConfigRow.value.configStatus === '1' ? '启用' : '停用' },
    { label: '闭环目标', value: `${targets.warningCloseRate || '-'} %` }
  ]
})

const primaryConfigAction = computed(() => {
  if (!currentConfigRow.value || isReadOnlyRole.value) {
    return { label: '查看详情', action: 'detail' }
  }
  const weights = parseJson(currentConfigRow.value.dimensionWeights)
  const weightSum = Number(weights.A || 0) + Number(weights.B || 0) + Number(weights.C || 0) + Number(weights.D || 0) + Number(weights.E || 0)
  if (currentConfigRow.value.configStatus !== '1') {
    return { label: '启用版本', action: 'activate' }
  }
  if (weightSum !== 100 || activeFocus.value?.key === 'upcoming' || activeFocus.value?.key === 'weight') {
    return { label: '修改版本', action: 'edit' }
  }
  return { label: '查看详情', action: 'detail' }
})

const currentConfigActionSummary = computed(() => {
  if (!currentConfigRow.value) {
    return '先从左侧焦点队列选择重点版本，再在列表中联动查看当前建议动作。'
  }
  if (primaryConfigAction.value.action === 'activate') {
    return '当前版本更适合优先进入启用流程，先确认目标值、权重和版本说明后再切换为生效口径。'
  }
  if (primaryConfigAction.value.action === 'edit') {
    return '当前版本更适合先做配置修正，优先检查权重总和、闭环目标和设备在线率等关键阈值。'
  }
  return '当前版本以详情复核为主，先看权重分布、目标值和区域覆盖，再决定是否进入修改或启用。'
})

const currentConfigActionTags = computed(() => {
  if (!currentConfigRow.value) {
    return [{ label: '未选中配置', type: 'info' }]
  }
  return buildHintTags(currentConfigRow.value).slice(0, 3)
})

const workflowSteps = computed(() => {
  if (roleView.value === 'bank') {
    return [
      { label: '先看启用版本', desc: '优先确认当前区域信用解释口径是否稳定。' },
      { label: '再看目标值与覆盖范围', desc: '通过闭环目标和区域覆盖判断当前模型是否存在解释偏差。' },
      { label: '最后回到信用与报表协同', desc: '必要时再进入信用、报表和 AI 页面做联合复核。' }
    ]
  }
  if (roleView.value === 'insurer') {
    return [
      { label: '先看启用与待生效版本', desc: '优先判断当前风险研判口径和即将切换口径是否稳定。' },
      { label: '再看权重与目标值', desc: '确认事故预防协同最关注的闭环目标和权重是否合理。' },
      { label: '最后回到详情复核', desc: '在详情里查看版本说明、权重和目标值，再决定是否继续协同。' }
    ]
  }
  if (roleView.value === 'site-enterprise') {
    return [
      { label: '先确认当前启用版本', desc: '优先判断当前企业评分解释口径是否稳定。' },
      { label: '再看闭环目标和权重', desc: '通过目标值和权重判断整改优先级是否需要调整。' },
      { label: '最后跟踪待生效版本', desc: '提前识别即将影响企业评分解释的模型版本。' }
    ]
  }
  return [
    { label: '先锁定启用版本', desc: '应急侧优先处理当前真正生效的评分口径。' },
    { label: '再看待生效和权重异常', desc: '待生效版本和权重异常更适合优先进入详情复核。' },
    { label: '最后回到启用切换', desc: '在确认目标值和权重合理后再执行启用切换。' }
  ]
})

const hintTags = computed(() => {
  const tags = []
  if (valueOrDefault(summaryData.value.activeCount, 0) > 0) {
    tags.push({ label: `当前启用版本 ${valueOrDefault(summaryData.value.activeCount, 0)} 个，建议先确认是否仍匹配当前治理口径`, type: 'success' })
  }
  if (valueOrDefault(summaryData.value.upcomingCount, 0) > 0) {
    tags.push({ label: `待生效版本 ${valueOrDefault(summaryData.value.upcomingCount, 0)} 个，建议提前复核切换风险`, type: 'warning' })
  }
  if (valueOrDefault(summaryData.value.averageWeightSum, 0) !== 100 && valueOrDefault(summaryData.value.averageWeightSum, 0) !== 0) {
    tags.push({ label: `平均权重总和为 ${valueOrDefault(summaryData.value.averageWeightSum, 0)}，建议优先检查权重配置`, type: 'danger' })
  }
  if (summaryData.value.activeWarningCloseRate !== undefined && summaryData.value.activeWarningCloseRate !== null) {
    tags.push({ label: `当前闭环目标 ${formatPercent(summaryData.value.activeWarningCloseRate)}，建议结合预警闭环要求继续复核`, type: 'info' })
  }
  return tags
})

const detailHintTags = computed(() => buildHintTags(detailConfig.value || currentConfigRow.value))

watchEffect(() => {
  setPageGuide({
    title: roleTitle.value || 'AI 模型治理工作台',
    description: roleDescription.value || '统一查看当前页的门户解释、焦点对象、当前选中、治理路径与风险提示。',
    portalExplanation: portalExplanationItems.value,
    focus: focusQueues.value,
    selection: [
      ...selectedConfigOverview.value,
      { label: '当前处置建议', value: currentConfigActionSummary.value }
    ],
    workflow: workflowSteps.value,
    hints: [...currentConfigActionTags.value, ...hintTags.value].slice(0, 6)
  })
})

const detailFocusText = computed(() => {
  if (roleView.value === 'bank') {
    return '重点看启用版本、闭环目标值和区域覆盖是否稳定，用于辅助信用解释口径和联合风控判断。'
  }
  if (roleView.value === 'insurer') {
    return '重点看启用版本、权重分布和闭环目标值，用于判断当前风险研判和事故预防协同口径是否合理。'
  }
  if (roleView.value === 'site-enterprise') {
    return '重点看当前企业适用版本、闭环目标和维度权重，判断是否会影响当前整改和评分解释优先级。'
  }
  return '重点看启用版本、待生效版本和权重目标是否存在偏移，必要时优先执行版本复核和启用切换。'
})

const readOnlyAlertTitle = computed(() => `${readOnlyRoleLabel.value}：当前仅保留模型摘要、详情和导出`)
const readOnlyAlertDescription = computed(() => `${readOnlyRoleDescription.value} 当前页面聚焦版本状态、区域覆盖、权重和目标值复核，不展示模型维护与启用动作。`)

const workbenchContext = computed(() => buildWorkbenchContext(route.query, {
  fields: aiReportConfigWorkbenchFields,
  title: '当前模型配置页沿用了工作台来源条件',
  description: '已按上游工作台带入的区域范围筛选版本台账，适合继续核对启用版本和目标口径。',
  fieldLabels: {
    regionCode: '行政区划',
    focusKey: '焦点队列'
  },
  fieldFormatters: {
    regionCode: value => regionNameMap[value] || value,
    focusKey: value => aiReportConfigFocusLabel(value)
  }
}))

function syncActiveFocus() {
  if (!activeFocusKey.value || !focusQueues.value.some(item => item.key === activeFocusKey.value)) {
    activeFocusKey.value = focusQueues.value[0]?.key || ''
  }
}

function handleSelectFocus(item) {
  activeFocusKey.value = item.key
  syncCurrentConfig()
}

function handlePortalExplanationAction(action) {
  if (action?.path === route.path) {
    applyAiReportConfigWorkbenchQuery(action.query || {})
  }
  openPortalExplanationAction(router, action)
}

function handlePrimaryConfigAction() {
  if (!currentConfigRow.value) {
    return
  }
  if (primaryConfigAction.value.action === 'activate') {
    if (isReadOnlyRole.value) {
      proxy.$modal.msgWarning(`${readOnlyRoleLabel.value}仅保留摘要、详情和导出，不能启用评分配置`)
      return
    }
    handleActivate(currentConfigRow.value)
    return
  }
  if (primaryConfigAction.value.action === 'edit') {
    if (isReadOnlyRole.value) {
      proxy.$modal.msgWarning(`${readOnlyRoleLabel.value}仅保留摘要、详情和导出，不能修改评分配置`)
      return
    }
    handleUpdate(currentConfigRow.value)
    return
  }
  openDetail(currentConfigRow.value)
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

function resolveAiReportConfigFocusKey(value) {
  const normalized = typeof value === 'string' ? value.trim() : ''
  return focusQueues.value.some(item => item.key === normalized) ? normalized : ''
}

function aiReportConfigFocusLabel(value) {
  return focusQueues.value.find(item => item.key === value)?.title || value || '-'
}

activeFocusKey.value = resolveAiReportConfigFocusKey(route.query.focusKey)

init()
</script>

<style scoped lang="scss">
.azb-ai-config-workbench {
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
    color: #0b6b78;
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
    font-size: 12px;
    color: #64748b;
    margin-bottom: 8px;
  }

  .azb-source-item__value {
    font-size: 16px;
    font-weight: 600;
    color: #0f172a;
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
    padding: 14px 0;
    border-bottom: 1px dashed #d9e3ef;
  }

  .azb-pipeline-item:last-child {
    border-bottom: 0;
    padding-bottom: 0;
  }

  .azb-pipeline-item__index {
    width: 42px;
    height: 42px;
    border-radius: 50%;
    background: #0b6b78;
    color: #fff;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    font-weight: 700;
  }

  .azb-pipeline-item__body strong {
    display: block;
    margin-bottom: 6px;
    color: #0f172a;
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

  .azb-config-form-grid {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 0 16px;
  }

  .azb-config-section {
    margin-bottom: 12px;
  }

  .azb-config-section h3 {
    margin: 0 0 12px;
    font-size: 15px;
    color: #2f4057;
  }

  .azb-detail-block {
    margin-top: 20px;
  }

  .azb-detail-block h3 {
    margin: 0 0 12px;
    font-size: 16px;
    color: #1f2d3d;
  }

  @media (max-width: 1200px) {
    .azb-focus-grid,
    .azb-source-list {
      grid-template-columns: 1fr;
    }
  }

  @media (max-width: 900px) {
    .azb-config-form-grid {
      grid-template-columns: 1fr;
    }
  }
}
</style>

