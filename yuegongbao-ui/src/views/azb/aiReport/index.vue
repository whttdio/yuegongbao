<template>
  <div class="app-container azb-page azb-ai-report-workbench">
    <section class="gov-page-header azb-page__header">
      <div>
        <p class="azb-page__eyebrow">AI 治理研判</p>
        <h1 class="azb-page__title">{{ roleTitle }}</h1>
        <p class="azb-page__desc">
          {{ roleDescription }}
          当前继续复用统一 AI 报告底座和评分配置中心，但安责保前端已按应急监管、保险机构、银行协同和企业管理四类高频使用视角重排焦点对象、摘要语义和操作顺序。
        </p>
      </div>
      <div class="azb-page__tip">
        <div class="azb-page__tip-item">当前视角：{{ roleBadge }}</div>
        <div class="azb-page__tip-item">优先关注：高风险样本、低分对象、区域尾部对象和建议动作集中度。</div>
        <div class="azb-page__tip-item">当前模型版本：{{ currentConfig.version || '未配置' }}，仍来自统一评分配置中心。</div>
        <div class="azb-page__tip-item">报告生成统一返回结构化内容和预览片段，便于继续下钻风险对象与治理建议。</div>
      </div>
    </section>

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
      <el-form ref="queryRef" :model="queryParams" :inline="true" v-show="showSearch">
        <el-form-item label="监测周期">
          <el-select v-model="queryParams.reportType" style="width: 160px">
            <el-option v-for="item in reportTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="统计区间">
          <el-date-picker
            v-model="queryRange"
            type="daterange"
            value-format="YYYY-MM-DD"
            format="YYYY-MM-DD"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            style="width: 300px"
          />
        </el-form-item>
        <el-form-item label="行政区划">
          <el-select v-model="queryParams.regionCode" style="width: 180px">
            <el-option v-for="item in regionOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="企业类型">
          <el-select v-model="queryParams.enterpriseType" clearable style="width: 160px">
            <el-option v-for="item in enterpriseTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="风险等级">
          <el-select v-model="queryParams.riskLevel" clearable style="width: 160px">
            <el-option v-for="item in riskLevelOptions" :key="item.value" :label="item.label" :value="item.value" />
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
          <el-button type="primary" plain icon="MagicStick" @click="openGenerateDialog()" v-hasPermi="['ygb:aiReport:generate']">生成报告</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['ygb:aiReport:export']">导出</el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>
    </el-card>

    <div class="azb-chart-grid">
      <el-card class="azb-chart-card" shadow="never">
        <template #header>
          <div class="azb-chart-card__head">
            <div>
              <div class="azb-chart-card__title">风险排名（前 5）</div>
              <div class="azb-chart-card__desc">当前筛选范围内的优良样本，可作为对照基线和区域参考面。</div>
            </div>
          </div>
        </template>
        <el-table :data="dashboard.topRankingList || []" size="small" empty-text="暂无样本">
          <el-table-column label="序位" width="74">
            <template #default="scope">
              <span class="azb-ranking-no azb-ranking-no--good">#{{ scope.row.rankingNo || scope.$index + 1 }}</span>
            </template>
          </el-table-column>
          <el-table-column label="区域" prop="regionName" min-width="140" />
          <el-table-column label="企业类型" min-width="110">
            <template #default="scope">
              {{ enterpriseTypeLabel(scope.row.enterpriseType) }}
            </template>
          </el-table-column>
          <el-table-column label="得分" prop="totalScore" width="90" />
          <el-table-column label="亮点" prop="highlight" min-width="180" show-overflow-tooltip />
        </el-table>
      </el-card>

      <el-card class="azb-chart-card" shadow="never">
        <template #header>
          <div class="azb-chart-card__head">
            <div>
              <div class="azb-chart-card__title">高风险对象</div>
              <div class="azb-chart-card__desc">重点展示高风险样本的风险原因和建议动作。</div>
            </div>
          </div>
        </template>
        <el-table :data="dashboard.highRiskList || []" size="small" empty-text="暂无高风险对象">
          <el-table-column label="序位" width="74">
            <template #default="scope">
              <span class="azb-ranking-no azb-ranking-no--risk">#{{ scope.row.rankingNo || scope.$index + 1 }}</span>
            </template>
          </el-table-column>
          <el-table-column label="区域" prop="regionName" min-width="140" />
          <el-table-column label="等级" width="100">
            <template #default="scope">
              <el-tag :type="riskTagType(scope.row.riskLevel)">{{ riskLevelLabel(scope.row.riskLevel) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="风险原因" prop="riskReason" min-width="180" show-overflow-tooltip />
          <el-table-column label="建议动作" prop="advice" min-width="180" show-overflow-tooltip />
        </el-table>
      </el-card>
    </div>

    <el-card class="table-card azb-table-card" shadow="never">
      <template #header>
        <div class="azb-card-head azb-card-head--between">
          <div>
            <div class="azb-card-head__title">AI 报告台账</div>
            <div class="azb-card-head__desc">{{ focusTableHint }}</div>
          </div>
          <div class="azb-card-head__desc">当前总量 {{ total }} 份</div>
        </div>
      </template>
      <div class="azb-report-conclusion" v-if="dashboard.conclusion">
        <div class="azb-report-conclusion__title">AI 监测结论</div>
        <div class="azb-report-conclusion__body">
          <p><strong>综合判断：</strong>{{ dashboard.conclusion.summary || '-' }}</p>
          <p><strong>主要优势：</strong>{{ dashboard.conclusion.strength || '-' }}</p>
          <p><strong>突出问题：</strong>{{ dashboard.conclusion.weakness || '-' }}</p>
          <p><strong>建议动作：</strong>{{ dashboard.conclusion.advice || '-' }}</p>
        </div>
      </div>

      <el-table
        v-loading="loading"
        :data="visibleReportList"
        row-key="reportId"
        @row-click="handleSelectReport"
      >
        <el-table-column label="报告ID" prop="reportId" width="96" />
        <el-table-column label="报告类型" width="110">
          <template #default="scope">
            {{ reportTypeLabel(scope.row.reportType) }}
          </template>
        </el-table-column>
        <el-table-column label="区域" min-width="140" prop="regionName" />
        <el-table-column label="统计区间" min-width="220">
          <template #default="scope">
            {{ parseTime(scope.row.periodStart, '{y}-{m}-{d}') }} 至 {{ parseTime(scope.row.periodEnd, '{y}-{m}-{d}') }}
          </template>
        </el-table-column>
        <el-table-column label="企业类型" width="110">
          <template #default="scope">
            {{ enterpriseTypeLabel(scope.row.enterpriseType) }}
          </template>
        </el-table-column>
        <el-table-column label="综合得分" prop="totalScore" width="100" />
        <el-table-column label="风险等级" width="100">
          <template #default="scope">
            <el-tag :type="riskTagType(scope.row.riskLevel)">{{ riskLevelLabel(scope.row.riskLevel) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="排名" prop="rankingNo" width="86" />
        <el-table-column label="摘要" prop="reportSummary" min-width="280" show-overflow-tooltip />
        <el-table-column label="生成时间" min-width="170">
          <template #default="scope">
            {{ parseTime(scope.row.generatedTime, '{y}-{m}-{d} {h}:{i}:{s}') }}
          </template>
        </el-table-column>
        <el-table-column class-name="table-fill-column" min-width="1" />

        <el-table-column label="操作" fixed="right" width="220" align="center">
          <template #default="scope">
            <el-button link type="info" icon="View" @click.stop="openDetail(scope.row)">详情</el-button>
            <el-button
              v-if="!isReadOnlyRole"
              link
              type="primary"
              icon="RefreshRight"
              @click.stop="openGenerateDialog(scope.row)"
              v-hasPermi="['ygb:aiReport:generate']"
            >
              重生成
            </el-button>
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

    <el-dialog v-model="generateOpen" title="生成 AI 监测报告" width="620px">
      <el-form ref="generateRef" :model="generateForm" :rules="generateRules" label-width="96px">
        <el-form-item label="区域" prop="regionCode">
          <el-select v-model="generateForm.regionCode" style="width: 100%">
            <el-option v-for="item in regionOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="报告类型" prop="reportType">
          <el-select v-model="generateForm.reportType" style="width: 100%">
            <el-option v-for="item in reportTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="企业类型" prop="enterpriseType">
          <el-select v-model="generateForm.enterpriseType" style="width: 100%">
            <el-option v-for="item in enterpriseTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="统计区间">
          <el-date-picker
            v-model="generateRange"
            type="daterange"
            value-format="YYYY-MM-DD"
            format="YYYY-MM-DD"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="评分维度" prop="dimensions">
          <el-checkbox-group v-model="generateForm.dimensions">
            <el-checkbox v-for="item in azbDimensionOptions" :key="item.value" :value="item.value">{{ item.label }}</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="generateOpen = false">取消</el-button>
        <el-button type="primary" @click="submitGenerate">确认生成</el-button>
      </template>
    </el-dialog>

    <page-detail-dialog v-model="detailOpen" title="AI 报告详情" width="760px">
      <template v-if="detailDisplayReport">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="报告ID">{{ detailDisplayReport.reportId }}</el-descriptions-item>
          <el-descriptions-item label="模型版本">{{ detailDisplayReport.configVersion || currentConfig.version || '未配置' }}</el-descriptions-item>
          <el-descriptions-item label="区域">{{ detailDisplayReport.regionName || regionNameMap[detailDisplayReport.regionCode] || detailDisplayReport.regionCode }}</el-descriptions-item>
          <el-descriptions-item label="报告类型">{{ reportTypeLabel(detailDisplayReport.reportType) }}</el-descriptions-item>
          <el-descriptions-item label="企业类型">{{ enterpriseTypeLabel(detailDisplayReport.enterpriseType) }}</el-descriptions-item>
          <el-descriptions-item label="风险等级">
            <el-tag :type="riskTagType(detailDisplayReport.riskLevel)">{{ riskLevelLabel(detailDisplayReport.riskLevel) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="综合得分">{{ detailDisplayReport.totalScore }}</el-descriptions-item>
          <el-descriptions-item label="区域排名">{{ detailDisplayReport.rankingNo || '-' }}</el-descriptions-item>
          <el-descriptions-item label="统计区间" :span="2">
            {{ parseTime(detailDisplayReport.periodStart, '{y}-{m}-{d}') }} 至 {{ parseTime(detailDisplayReport.periodEnd, '{y}-{m}-{d}') }}
          </el-descriptions-item>
          <el-descriptions-item label="摘要" :span="2">{{ detailDisplayReport.reportSummary || '-' }}</el-descriptions-item>
          <el-descriptions-item label="当前视角重点" :span="2">{{ detailFocusText }}</el-descriptions-item>
        </el-descriptions>

        <div class="azb-detail-block">
          <h3>治理提示</h3>
          <div class="azb-tag-list">
            <el-tag v-for="item in detailHintTags" :key="item.label" :type="item.type" effect="plain">{{ item.label }}</el-tag>
          </div>
        </div>

        <div class="azb-detail-block">
          <h3>维度明细</h3>
          <el-table :data="displayDetailDimensionItems" size="small" empty-text="暂无维度明细">
            <el-table-column label="维度" min-width="180">
              <template #default="scope">
                {{ scope.row.dimensionCode }} {{ scope.row.dimensionName }}
              </template>
            </el-table-column>
            <el-table-column label="指标" prop="metricLabel" min-width="120" />
            <el-table-column label="指标值" prop="metricValue" width="110" />
            <el-table-column label="目标值" prop="targetValue" width="110" />
            <el-table-column label="得分" prop="dimensionScore" width="90" />
            <el-table-column label="建议" prop="suggestionText" min-width="180" show-overflow-tooltip />
          </el-table>
        </div>

        <div class="azb-detail-block" v-if="detailDisplayReport.reportHtml">
          <h3>报告片段</h3>
          <div class="azb-report-html" v-html="detailDisplayReport.reportHtml" />
        </div>
      </template>
    </page-detail-dialog>
  </div>
</template>

<script setup name="AzbAiReport">
import { computed, getCurrentInstance, ref, watchEffect } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useWorkbenchAssist } from '@/composables/useWorkbenchAssist'
import useUserStore from '@/store/modules/user'
import {
  decoratePortalExplanationItems,
  openPortalExplanationAction,
  resolvePortalExplanationSummary
} from '@/utils/portalExplanation'
import { useRoleViewMode } from '@/utils/roleView'
import { applyWorkbenchRouteQuery, buildWorkbenchContext, stripWorkbenchRouteQuery } from '@/utils/workbenchLink'
import { authorizedDefaultRegionCode, useAuthorizedRegionOptions } from '@/utils/regionScope'
import {
  buildBaseAiReportHintTags,
  currentMonthRange,
  dimensionOptions,
  enterpriseTypeLabel,
  enterpriseTypeOptions,
  focusQueue,
  formatDecimal,
  matchReportFocus,
  prioritizeFocusRows,
  regionNameMap,
  regionOptions as allRegionOptions,
  reportTypeLabel,
  reportTypeOptions,
  riskLevelLabel,
  riskLevelOptions,
  riskTagType,
  summaryCard,
  useAiReportPage
} from '@/views/aiReport/useAiReportPage'

const { proxy } = getCurrentInstance()
const { setPageGuide } = useWorkbenchAssist()
const regionOptions = useAuthorizedRegionOptions(allRegionOptions)
const defaultRegionCode = authorizedDefaultRegionCode('440000')
const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const { isReadOnlyRole, isBankRole, isInsurerRole, readOnlyRoleLabel, readOnlyRoleDescription } = useRoleViewMode()

const activeFocusKey = ref('')
const aiReportWorkbenchFields = ['regionCode']
const aiReportInitialQuery = {}
const AZB_AI_DIMENSION_LABEL_MAP = Object.freeze({
  A: '主体台账合规',
  B: '在岗留痕合规',
  C: '收入联动合规',
  D: '设备作业安全',
  E: '风险闭环处置'
})

applyWorkbenchRouteQuery(route.query, aiReportInitialQuery, aiReportWorkbenchFields)

const {
  loading,
  showSearch,
  total,
  reportList,
  detailOpen,
  generateOpen,
  queryRange,
  generateRange,
  activeReportId,
  currentConfig,
  dashboard,
  queryParams,
  generateForm,
  generateRules,
  detailDimensionItems,
  detailDisplayReport,
  getList,
  loadDashboard,
  handleQuery,
  resetQuery: pageResetQuery,
  openGenerateDialog,
  submitGenerate,
  openDetail,
  handleExport
} = useAiReportPage({
  exportFilePrefix: 'azb_ai_report',
  canMutate: () => !isReadOnlyRole.value,
  onBlockedAction: () => {
    proxy.$modal.msgWarning(`${readOnlyRoleLabel.value}仅保留摘要、详情和导出，不能执行该操作`)
  },
  afterLoad: () => {
    syncActiveFocus()
    syncCurrentReport()
  }
})

Object.assign(queryParams, aiReportInitialQuery)

const portalExplanations = computed(() => dashboard.azbExplanation || [])
const portalExplanationItems = computed(() => decoratePortalExplanationItems(portalExplanations.value, {
  portalCode: 'azb',
  panelTitle: '6.1 隐患压降解释',
  panelDescription: '高风险对象、重复隐患和压降建议统一来自门户解释聚合接口。'
}))

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
  if (roleView.value === 'bank') return '区域信用与 AI 风险研判看板'
  if (roleView.value === 'insurer') return 'AI 高风险对象协同复核台账'
  if (roleView.value === 'site-enterprise') return '企业风险画像与整改跟踪工作台'
  return 'AI 监测高风险对象治理工作台'
})

const roleDescription = computed(() => {
  if (roleView.value === 'bank') {
    return '围绕区域尾部样本、高风险对象和模型版本做只读复核，用于辅助信用判断和联合风控。'
  }
  if (roleView.value === 'insurer') {
    return '围绕高风险样本、低分对象和建议动作做协同复核，优先识别适合纳入事故预防服务的风险企业。'
  }
  if (roleView.value === 'site-enterprise') {
    return '围绕本单位综合得分、低分维度和整改建议组织企业管理员持续跟踪，重点是把报告真正转成整改动作。'
  }
  return '围绕高风险样本、低分区域、治理时效和建议动作组织应急监管工作流，优先压降风险对象。'
})

function resolveAzbDimensionLabel(code, fallback = '') {
  return AZB_AI_DIMENSION_LABEL_MAP[String(code || '').toUpperCase()] || fallback || code || '-'
}

function decorateAzbAiReportTags(tags = []) {
  return tags.map(item => ({
    ...item,
    label: String(item.label || '').replace('业务台账', '治理台账')
  }))
}

const azbDimensionOptions = computed(() => dimensionOptions.map(item => ({
  ...item,
  label: `${item.value} ${resolveAzbDimensionLabel(item.value, item.label.replace(/^[A-Z]\s*/, ''))}`
})))

const displayDetailDimensionItems = computed(() => detailDimensionItems.value.map(item => ({
  ...item,
  dimensionName: resolveAzbDimensionLabel(item.dimensionCode, item.dimensionName)
})))

const focusQueues = computed(() => {
  const averageScore = Number(dashboard.averageScore || 0)
  const highRiskCount = Number(dashboard.highRiskList?.length || dashboard.highRiskCount || 0)
  const lowScoreCount = prioritizeFocusRows(reportList.value, row => matchReportFocus(row, 'lowScore', averageScore))
    .filter(row => matchReportFocus(row, 'lowScore', averageScore))
    .length

  if (roleView.value === 'bank') {
    return [
      focusQueue('highRisk', '高风险对象', highRiskCount, '个', '先确认当前区域是否存在较多红色风险对象。', '查看高风险对象'),
      focusQueue('lowScore', '低分尾部样本', lowScoreCount, '份', '尾部样本更适合联动信用和报表做联合复核。', '查看低分样本'),
      focusQueue('total', '本期报告样本', dashboard.totalCount || total.value, '份', '先判断本期样本量是否具备区域比较价值。', '查看样本规模')
    ]
  }
  if (roleView.value === 'insurer') {
    return [
      focusQueue('highRisk', '高风险样本', highRiskCount, '份', '高风险样本更值得优先纳入事故预防服务。', '查看高风险样本'),
      focusQueue('lowScore', '低分整改样本', lowScoreCount, '份', '低分样本适合跟踪建议动作和后续闭环。', '查看低分对象'),
      focusQueue('total', '本期报告样本', dashboard.totalCount || total.value, '份', '用于判断当前区域和周期的风险覆盖面。', '查看样本规模')
    ]
  }
  if (roleView.value === 'site-enterprise') {
    return [
      focusQueue('lowScore', '待整改低分样本', lowScoreCount, '份', '先把分数最低的样本找出来，回到风险对象台账做整改。', '进入低分整改'),
      focusQueue('highRisk', '高风险样本', highRiskCount, '份', '高风险样本优先对应到预警、设备和高危作业链路。', '查看高风险项'),
      focusQueue('total', '本期报告样本', dashboard.totalCount || total.value, '份', '确认本期可用于企业内部复盘的 AI 样本规模。', '查看报告台账')
    ]
  }
  return [
    focusQueue('highRisk', '高风险对象', highRiskCount, '个', '应急侧先锁定最需要处置的红色风险样本。', '进入高风险处置'),
    focusQueue('lowScore', '低分尾部样本', lowScoreCount, '份', '低分样本适合回到区域和企业做针对性治理。', '查看尾部样本'),
    focusQueue('total', '本期报告样本', dashboard.totalCount || total.value, '份', '确认当前周期样本覆盖面，再决定是否重生成。', '查看样本规模')
  ]
})

const activeFocus = computed(() => {
  if (!focusQueues.value.length) return null
  return focusQueues.value.find(item => item.key === activeFocusKey.value) || focusQueues.value[0]
})

const visibleReportList = computed(() => {
  return prioritizeFocusRows(reportList.value, row => matchReportFocus(row, activeFocus.value?.key, dashboard.averageScore))
})

const selectedReport = computed(() => {
  return visibleReportList.value.find(item => item.reportId === activeReportId.value)
    || dashboard.activeReport
    || detailDisplayReport.value
    || visibleReportList.value[0]
    || null
})

const summaryCards = computed(() => {
  const highRiskCount = Number(dashboard.highRiskList?.length || dashboard.highRiskCount || 0)
  const lowScoreCount = visibleReportList.value.filter(row => matchReportFocus(row, 'lowScore', dashboard.averageScore)).length

  if (roleView.value === 'bank') {
    return [
      summaryCard('total', '报告样本', dashboard.totalCount || total.value, '份', '当前筛选范围内已生成的 AI 报告样本总量。', ''),
      summaryCard('highRisk', '高风险对象', highRiskCount, '个', '优先用于识别需要信用侧联合关注的风险对象。', 'azb-summary-card--danger'),
      summaryCard('lowScore', '低分样本', lowScoreCount, '份', '尾部样本越多，越值得回到区域和企业做联合复核。', 'azb-summary-card--warning'),
      summaryCard('model', '模型版本', currentConfig.value.version || 'DEFAULT-STUB', '', '当前区域生效的 AI 评分配置版本。', '')
    ]
  }
  if (roleView.value === 'insurer') {
    return [
      summaryCard('highRisk', '高风险样本', highRiskCount, '份', '优先纳入事故预防服务和风险协同复核。', 'azb-summary-card--danger'),
      summaryCard('average', '平均得分', formatDecimal(dashboard.averageScore), '分', '当前样本范围的综合平均得分。', 'azb-summary-card--success'),
      summaryCard('lowScore', '低分样本', lowScoreCount, '份', '低分样本越多，越适合跟踪治理动作是否落地。', 'azb-summary-card--warning'),
      summaryCard('total', '报告样本', dashboard.totalCount || total.value, '份', '当前条件下的 AI 报告规模。', '')
    ]
  }
  if (roleView.value === 'site-enterprise') {
    return [
      summaryCard('average', '综合得分', formatDecimal(dashboard.averageScore), '分', '企业当前样本范围的综合得分水平。', 'azb-summary-card--success'),
      summaryCard('highRisk', '高风险样本', highRiskCount, '份', '说明哪些周期或区域需要优先回到风险对象台账整改。', 'azb-summary-card--danger'),
      summaryCard('lowScore', '低分样本', lowScoreCount, '份', '低分样本越多，越需要细看维度得分和建议动作。', 'azb-summary-card--warning'),
      summaryCard('total', '报告样本', dashboard.totalCount || total.value, '份', '当前筛选条件下可追踪的 AI 报告数量。', '')
    ]
  }
  return [
    summaryCard('total', '报告样本', dashboard.totalCount || total.value, '份', '当前筛选范围内已生成的 AI 报告样本数量。', ''),
    summaryCard('average', '平均得分', formatDecimal(dashboard.averageScore), '分', '当前样本范围的综合平均得分。', 'azb-summary-card--success'),
    summaryCard('highRisk', '高风险样本', highRiskCount, '份', '当前筛选范围内高风险等级的样本数量。', 'azb-summary-card--danger'),
    summaryCard('lowScore', '低分样本', lowScoreCount, '份', '优先用于锁定区域尾部对象和重复问题样本。', 'azb-summary-card--warning')
  ]
})

function handlePortalExplanationAction(action) {
  openPortalExplanationAction(router, action)
}

const portalExplanationSummary = computed(() => resolvePortalExplanationSummary(portalExplanationItems.value, ''))

function buildAggregateWorkflowSteps(items = []) {
  return (items || [])
    .filter(item => item?.summary || item?.explanationSummary || item?.sourceDescription)
    .slice(0, 3)
    .map(item => ({
      label: item.dimensionName || item.moduleLabel || item.moduleCode || 'Explanation',
      desc: item.summary || item.explanationSummary || item.sourceDescription || ''
    }))
}

function buildAggregateHintTags(items = [], fallback = '') {
  const tags = []
  const leadingItem = (items || [])[0]
  if (leadingItem?.dimensionName) {
    tags.push({ label: leadingItem.dimensionName, type: 'info' })
  }
  if (leadingItem?.moduleLabel) {
    tags.push({ label: leadingItem.moduleLabel, type: 'success' })
  }
  if (!leadingItem?.dimensionName && fallback) {
    tags.push({ label: fallback, type: 'info' })
  }
  return tags
}

const focusTableHint = computed(() => {
  if (!activeFocus.value) {
    return '按当前筛选条件展示 AI 报告台账。'
  }
  return `当前焦点为“${activeFocus.value.title}”，已把对应重点对象优先排到表格前列。`
})

const selectedReportOverview = computed(() => {
  if (!selectedReport.value) {
    return [
      { label: '当前焦点', value: activeFocus.value?.title || '-' },
      { label: '焦点数量', value: `${activeFocus.value?.count ?? 0}${activeFocus.value?.unit || ''}` },
      { label: '当前区域', value: regionNameMap[queryParams.regionCode] || queryParams.regionCode || '全部区域' },
      { label: '下一步', value: activeFocus.value?.actionText || '-' }
    ]
  }
  return [
    { label: '区域', value: selectedReport.value.regionName || regionNameMap[selectedReport.value.regionCode] || selectedReport.value.regionCode || '-' },
    { label: '风险等级', value: riskLevelLabel(selectedReport.value.riskLevel) },
    { label: '综合得分', value: `${formatDecimal(selectedReport.value.totalScore)} 分` },
    { label: '统计区间', value: `${parseTime(selectedReport.value.periodStart, '{m}-{d}') || '-'} 至 ${parseTime(selectedReport.value.periodEnd, '{m}-{d}') || '-'}` }
  ]
})

const primaryReportAction = computed(() => {
  if (!selectedReport.value) {
    return { label: activeFocus.value?.actionText || '查看详情', action: 'detail' }
  }
  if (isReadOnlyRole.value) {
    return { label: '查看详情', action: 'detail' }
  }
  if (String(selectedReport.value.riskLevel || '') === 'HIGH') {
    return { label: '按当前报告重生成', action: 'generate' }
  }
  if (activeFocus.value?.key === 'lowScore' && Number(selectedReport.value.totalScore || 0) < Number(dashboard.averageScore || 0)) {
    return { label: '按当前报告重生成', action: 'generate' }
  }
  return { label: '查看详情', action: 'detail' }
})

const currentReportActionSummary = computed(() => {
  return resolvePortalExplanationSummary(
    portalExplanationItems.value,
    selectedReport.value
      ? '当前 AI 报告的高风险对象、重复隐患和压降建议已按 6.1 治理口径展示。'
      : '当前暂无 AI 报告，可继续通过 6.1 隐患压降解释查看主下钻方向。'
  )
})

const currentReportActionTags = computed(() => decorateAzbAiReportTags([
  ...buildAggregateHintTags(portalExplanationItems.value, portalExplanationSummary.value),
  ...buildBaseAiReportHintTags(selectedReport.value, {
    focus: activeFocus.value,
    averageScore: dashboard.averageScore,
    highRiskCount: dashboard.highRiskCount,
    currentConfigVersion: currentConfig.value.version
  })
]))

const workflowSteps = computed(() => {
  const aggregateSteps = buildAggregateWorkflowSteps(portalExplanationItems.value)
  if (aggregateSteps.length) {
    return aggregateSteps
  }
  if (roleView.value === 'bank') {
    return [
      { label: '先看高风险与低分样本', desc: '优先判断当前区域是否出现需要信用侧重点复核的对象。' },
      { label: '再看模型与样本覆盖', desc: '确认模型版本和样本规模是否支撑当前结论。' },
      { label: '最后回到信用与报表协同', desc: '必要时再进入企业、信用和统计页做联合判断。' }
    ]
  }
  if (roleView.value === 'insurer') {
    return [
      { label: '先看高风险样本', desc: '优先确认最需要纳入事故预防服务的风险对象。' },
      { label: '再看低分维度与建议动作', desc: '通过建议动作判断是设备、预警还是高危作业问题更突出。' },
      { label: '最后回到详情复核', desc: '在详情里查看维度得分明细，再决定是否继续协同。' }
    ]
  }
  if (roleView.value === 'site-enterprise') {
    return [
      { label: '先锁定低分样本', desc: '优先处理拖低企业画像和评分的样本对象。' },
      { label: '再看维度短板', desc: '通过维度得分和建议动作回到具体治理链路整改。' },
      { label: '最后跟踪重生成结果', desc: '在整改后重新生成报告，确认分值和风险等级是否下降。' }
    ]
  }
  return [
    { label: '先锁定高风险对象', desc: '应急侧优先处理最需要压降的红色风险样本。' },
    { label: '再看低分尾部和建议动作', desc: '通过低分对象确认治理重点是否集中在某类区域或企业。' },
    { label: '最后回到详情与重生成', desc: '通过维度明细和重生成动作验证治理是否产生效果。' }
  ]
})

const hintTags = computed(() => {
  const tags = [...buildAggregateHintTags(portalExplanationItems.value, portalExplanationSummary.value)]
  const highRiskCount = Number(dashboard.highRiskList?.length || dashboard.highRiskCount || 0)
  const lowScoreCount = visibleReportList.value.filter(row => matchReportFocus(row, 'lowScore', dashboard.averageScore)).length
  if (highRiskCount > 0) {
    tags.push({ label: `当前有 ${highRiskCount} 个高风险对象，建议优先查看原因与建议动作`, type: 'danger' })
  }
  if (lowScoreCount > 0) {
    tags.push({ label: `尾部低分样本 ${lowScoreCount} 份，适合回到风险对象台账逐项整改`, type: 'warning' })
  }
  if (currentConfig.value.version) {
    tags.push({ label: `当前模型版本 ${currentConfig.value.version} 正在生效，可作为本期解释口径`, type: 'info' })
  }
  if (Number(dashboard.totalCount || total.value || 0) > 0 && roleView.value === 'site-enterprise') {
    tags.push({ label: `当前样本 ${dashboard.totalCount || total.value || 0} 份，建议结合预警、设备、高处作业做交叉治理复核`, type: 'success' })
  }
  if (!tags.length) {
    tags.push({ label: '当前筛选条件下暂无明显风险提示，可继续查看台账详情。', type: 'info' })
  }
  return decorateAzbAiReportTags(tags)
})

watchEffect(() => {
  setPageGuide({
    title: roleTitle.value || 'AI 治理研判工作台',
    description: roleDescription.value || '统一查看当前页的门户解释、焦点对象、当前选中、治理路径与风险提示。',
    portalExplanation: portalExplanationItems.value,
    focus: focusQueues.value,
    selection: [
      ...selectedReportOverview.value,
      { label: '当前处置建议', value: currentReportActionSummary.value }
    ],
    workflow: workflowSteps.value,
    hints: [...currentReportActionTags.value, ...hintTags.value].slice(0, 6)
  })
})

const detailHintTags = computed(() => decorateAzbAiReportTags([
  ...buildAggregateHintTags(portalExplanationItems.value, portalExplanationSummary.value),
  ...buildBaseAiReportHintTags(detailDisplayReport.value || selectedReport.value, {
    focus: activeFocus.value,
    items: detailDimensionItems.value,
    detailMode: true,
    averageScore: dashboard.averageScore,
    currentConfigVersion: currentConfig.value.version
  })
]))

const detailFocusText = computed(() => {
  if (roleView.value === 'bank') {
    return '重点看高风险对象、低分样本和模型版本是否稳定，用于辅助区域信用与联合风控判断。'
  }
  if (roleView.value === 'insurer') {
    return '重点看高风险等级、低分维度和建议动作，用于判断事故预防服务和风险协同优先级。'
  }
  if (roleView.value === 'site-enterprise') {
    return '重点看哪些维度拖低了企业得分，再把建议动作回落到预警、设备、高危作业等具体整改台账。'
  }
  return '重点看高风险等级、尾部样本和建议动作是否集中，必要时优先进入区域或对象治理链路。'
})

const readOnlyAlertTitle = computed(() => `${readOnlyRoleLabel.value}：当前仅保留摘要、详情和导出`)
const readOnlyAlertDescription = computed(() => {
  if (isInsurerRole.value) {
    return `${readOnlyRoleDescription.value} 当前页面聚焦高风险样本、低分对象和报告详情复核，不展示报告生成和重生成动作。`
  }
  if (isBankRole.value) {
    return `${readOnlyRoleDescription.value} 当前页面仅承担区域信用与风险研判协同，不承担 AI 报告生成类操作。`
  }
  return readOnlyRoleDescription.value
})

const workbenchContext = computed(() => buildWorkbenchContext(route.query, {
  fields: aiReportWorkbenchFields,
  title: '当前 AI 报告页沿用了工作台来源条件',
  description: '已按上游工作台带入的区域范围筛选数据，适合继续查看区域高风险对象和报告结论。',
  fieldLabels: {
    regionCode: '行政区划'
  },
  fieldFormatters: {
    regionCode: value => regionNameMap[value] || value
  }
}))

function syncActiveFocus() {
  if (!activeFocusKey.value || !focusQueues.value.some(item => item.key === activeFocusKey.value)) {
    activeFocusKey.value = focusQueues.value[0]?.key || ''
  }
}

function syncCurrentReport() {
  activeReportId.value = selectedReport.value?.reportId || visibleReportList.value[0]?.reportId
}

function handleSelectReport(row) {
  activeReportId.value = row.reportId
  loadDashboard(row.reportId)
}

function handleSelectFocus(item) {
  activeFocusKey.value = item.key
  syncCurrentReport()
  loadDashboard(activeReportId.value)
}

function handlePrimaryReportAction() {
  if (!selectedReport.value) return
  if (primaryReportAction.value.action === 'generate') {
    if (isReadOnlyRole.value) {
      proxy.$modal.msgWarning(`${readOnlyRoleLabel.value}仅保留摘要、详情和导出，不能生成报告`)
      return
    }
    openGenerateDialog(selectedReport.value)
    return
  }
  openDetail(selectedReport.value)
}

function resetQuery() {
  proxy.resetForm('queryRef')
  Object.assign(queryParams, {
    pageNum: 1,
    pageSize: 10,
    regionCode: defaultRegionCode,
    reportType: 'MONTHLY',
    enterpriseType: '',
    riskLevel: undefined
  })
  queryRange.value = currentMonthRange()
  activeReportId.value = undefined
  applyWorkbenchRouteQuery(route.query, queryParams, aiReportWorkbenchFields)
  getList()
}

function clearWorkbenchContext() {
  Object.assign(queryParams, {
    pageNum: 1,
    pageSize: 10,
    regionCode: defaultRegionCode,
    reportType: 'MONTHLY',
    enterpriseType: '',
    riskLevel: undefined
  })
  queryRange.value = currentMonthRange()
  activeReportId.value = undefined
  router.replace({
    path: route.path,
    query: stripWorkbenchRouteQuery(route.query, aiReportWorkbenchFields)
  })
  getList()
}

getList()
</script>

<style scoped lang="scss">
.azb-ai-report-workbench {
  .azb-focus-grid,
  .azb-chart-grid {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 16px;
    margin-bottom: 16px;
  }

  .azb-summary-grid {
    display: grid;
    grid-template-columns: repeat(4, minmax(0, 1fr));
    gap: 16px;
    margin-bottom: 16px;
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
    color: #48656e;
    font-size: 13px;
    line-height: 1.7;
  }

  .azb-workbench-alert__desc strong {
    color: #0b6b78;
  }

  .azb-summary-card,
  .azb-focus-card,
  .azb-chart-card,
  .azb-table-card {
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
    color: #12333c;
    font-size: 28px;
    font-weight: 700;
  }

  .azb-summary-card__unit {
    margin-left: 4px;
    font-size: 13px;
    color: #708894;
  }

  .azb-summary-card__note {
    margin-top: 10px;
    color: #58707b;
    line-height: 1.7;
    font-size: 13px;
  }

  .azb-summary-card--success {
    background: linear-gradient(180deg, #ffffff 0%, #f1fbf7 100%);
  }

  .azb-summary-card--warning {
    background: linear-gradient(180deg, #ffffff 0%, #fff8ef 100%);
  }

  .azb-summary-card--danger {
    background: linear-gradient(180deg, #ffffff 0%, #fff3f2 100%);
  }

  .azb-focus-card :deep(.el-card__header),
  .azb-chart-card :deep(.el-card__header) {
    padding: 18px 20px 0;
    border-bottom: none;
  }

  .azb-focus-card :deep(.el-card__body),
  .azb-chart-card :deep(.el-card__body) {
    padding: 18px 20px 20px;
  }

  .azb-card-head__title,
  .azb-chart-card__title {
    font-size: 16px;
    font-weight: 700;
    color: #12333c;
  }

  .azb-card-head__desc,
  .azb-chart-card__desc {
    margin-top: 4px;
    font-size: 13px;
    line-height: 1.7;
    color: #64808a;
  }

  .azb-card-head--between {
    display: flex;
    align-items: flex-start;
    justify-content: space-between;
    gap: 12px;
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
    border-radius: 14px;
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
    color: #12333c;
    font-size: 14px;
  }

  .azb-focus-queue__main p {
    margin: 6px 0 0;
    color: #667f89;
    line-height: 1.7;
    font-size: 13px;
  }

  .azb-focus-queue__side {
    min-width: 116px;
    display: grid;
    justify-items: end;
    gap: 6px;
  }

  .azb-focus-queue__count {
    color: #0b6b78;
    font-weight: 700;
  }

  .azb-focus-queue__action {
    color: #6b7d86;
    font-size: 12px;
  }

  .azb-source-list,
  .azb-pipeline-list {
    display: grid;
    gap: 12px;
  }

  .azb-source-item,
  .azb-pipeline-item {
    border: 1px solid #dde7ef;
    border-radius: 14px;
    background: #f7fafc;
  }

  .azb-source-item {
    padding: 14px 16px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 12px;
  }

  .azb-source-item__label {
    color: #66808a;
    font-size: 13px;
  }

  .azb-source-item__value {
    color: #12333c;
    text-align: right;
    font-weight: 600;
  }

  .azb-recommend-panel {
    margin-top: 16px;
    padding: 16px;
    border-radius: 14px;
    background: linear-gradient(135deg, rgba(11, 107, 120, 0.08), rgba(11, 107, 120, 0.02));
  }

  .azb-recommend-panel__title {
    color: #0b6b78;
    font-size: 13px;
    font-weight: 700;
  }

  .azb-recommend-panel__summary {
    margin-top: 8px;
    color: #335963;
    line-height: 1.7;
    font-size: 13px;
  }

  .azb-focus-actions {
    margin-top: 16px;
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
  }

  .azb-pipeline-item {
    display: grid;
    grid-template-columns: 42px minmax(0, 1fr);
    gap: 14px;
    padding: 16px;
  }

  .azb-pipeline-item__index {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    height: 34px;
    border-radius: 10px;
    background: #e6f4f5;
    color: #0b6b78;
    font-weight: 700;
  }

  .azb-pipeline-item__body strong {
    color: #12333c;
  }

  .azb-pipeline-item__body p {
    margin: 8px 0 0;
    color: #607882;
    line-height: 1.7;
    font-size: 13px;
  }

  .azb-tag-list {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
  }

  .azb-report-conclusion {
    margin-bottom: 16px;
    padding: 16px 18px;
    border-left: 6px solid #0b6b78;
    border-radius: 14px;
    background: #f2faf9;
  }

  .azb-report-conclusion__title {
    color: #0b6b78;
    font-weight: 700;
    margin-bottom: 8px;
  }

  .azb-report-conclusion__body {
    color: #48656e;
    line-height: 1.8;
    font-size: 13px;
  }

  .azb-report-conclusion__body p {
    margin: 0 0 6px;
  }

  .azb-ranking-no {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    min-width: 42px;
    padding: 3px 8px;
    border-radius: 999px;
    font-size: 12px;
    font-weight: 700;
  }

  .azb-ranking-no--good {
    background: #edf6f1;
    color: #157347;
  }

  .azb-ranking-no--risk {
    background: #feeceb;
    color: #b42318;
  }

  .azb-detail-block {
    margin-top: 20px;
  }

  .azb-detail-block h3 {
    margin: 0 0 12px;
    color: #12333c;
    font-size: 16px;
  }

  .azb-report-html {
    padding: 16px 18px;
    border-radius: 14px;
    border: 1px solid #e0e9ee;
    background: #f8fbfd;
  }

  @media (max-width: 1200px) {
    .azb-summary-grid,
    .azb-focus-grid,
    .azb-chart-grid {
      grid-template-columns: repeat(2, minmax(0, 1fr));
    }
  }

  @media (max-width: 768px) {
    .azb-summary-grid,
    .azb-focus-grid,
    .azb-chart-grid {
      grid-template-columns: 1fr;
    }

    .azb-card-head--between,
    .azb-focus-queue {
      display: block;
    }

    .azb-focus-queue__side {
      margin-top: 10px;
      justify-items: start;
    }
  }
}
</style>

