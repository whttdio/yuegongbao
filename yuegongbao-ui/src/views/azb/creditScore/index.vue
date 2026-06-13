<template>
  <div class="app-container azb-page azb-credit-score-workbench">
    <section class="gov-page-header azb-page__header">
      <div>
        <p class="azb-page__eyebrow">信用评价</p>
        <h1 class="azb-page__title">{{ roleTitle }}</h1>
        <p class="azb-page__desc">
          {{ roleDescription }}
          当前继续复用统一信用评分底座，但安责保前端已按应急监管、企业复核、保险协同和银行协同四类高频视角重排焦点对象、复核顺序和详情重点。
        </p>
      </div>
      <div class="azb-page__tip">
        <div class="azb-page__tip-item">当前视角：{{ roleBadge }}</div>
        <div class="azb-page__tip-item">优先关注：红码企业、D级企业、黄码边缘对象和低分维度。</div>
        <div class="azb-page__tip-item">生成评分继续基于已落库监管数据，不依赖外部评分引擎。</div>
        <div class="azb-page__tip-item">当前评分解释已按 6.1 风险分层口径输出，并保持角色化复核顺序。</div>
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
      <el-form ref="queryRef" :model="queryParams" :inline="true" v-show="showSearch">
        <el-form-item label="统计月份">
          <el-date-picker v-model="queryParams.statMonth" type="month" value-format="YYYY-MM" format="YYYY-MM" style="width: 170px" />
        </el-form-item>
        <el-form-item label="企业">
          <el-select v-model="queryParams.enterpriseId" clearable filterable style="width: 220px">
            <el-option v-for="item in enterpriseOptions" :key="item.enterpriseId" :label="item.enterpriseName" :value="item.enterpriseId" />
          </el-select>
        </el-form-item>
        <el-form-item label="行政区划">
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

    <el-card class="toolbar-card azb-toolbar-card" shadow="never">
      <el-row :gutter="10">
        <el-col v-if="!isReadOnlyRole" :span="1.5">
          <el-button type="primary" plain icon="MagicStick" @click="openGenerateDialog()" v-hasPermi="['ygb:creditScore:generate']">生成评分</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['ygb:creditScore:export']">导出</el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>
    </el-card>

    <el-card class="table-card azb-table-card" shadow="never">
      <template #header>
        <div class="azb-card-head azb-card-head--between">
          <div>
            <div class="azb-card-head__title">信用评分台账</div>
            <div class="azb-card-head__desc">{{ focusTableHint }}</div>
          </div>
          <div class="azb-card-head__desc">当前总量 {{ total }} 条</div>
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
        <el-table-column label="安全" prop="safetyScore" width="90" />
        <el-table-column label="治理" prop="governanceScore" width="90" />
        <el-table-column label="合同" prop="contractScore" width="90" />
        <el-table-column label="考勤" prop="attendanceScore" width="90" />
        <el-table-column label="收入稳定" prop="salaryScore" width="90" />
        <el-table-column label="联动底数" prop="socialTaxScore" width="110" />
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
          <el-button type="primary" @click="submitGenerate">生成</el-button>
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

        <div class="azb-detail-block">
          <h3>治理提示</h3>
          <div class="azb-tag-list">
            <el-tag v-for="item in detailHintTags" :key="item.label" :type="item.type" effect="plain">{{ item.label }}</el-tag>
          </div>
        </div>

        <div class="azb-detail-block">
          <h3>评分因子</h3>
          <el-table :data="displayFactorRows" size="small">
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

<script setup name="AzbCreditScore">
import { computed, ref, watchEffect } from 'vue'
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
import { useAuthorizedRegionOptions } from '@/utils/regionScope'
import {
  colorLabel,
  colorTagType,
  creditColorOptions as colorOptions,
  creditLevelOptions as levelOptions,
  creditScoreRegionNameMap as regionNameMap,
  creditScoreRegionOptions as allRegionOptions,
  formatDecimal,
  levelTagType,
  useCreditScorePage,
  valueOrDefault
} from '@/views/creditScore/useCreditScorePage'

const route = useRoute()
const { setPageGuide } = useWorkbenchAssist()
const regionOptions = useAuthorizedRegionOptions(allRegionOptions)
const router = useRouter()
const userStore = useUserStore()
const { isReadOnlyRole, isBankRole, isInsurerRole, readOnlyRoleLabel, readOnlyRoleDescription } = useRoleViewMode()
const activeFocusKey = ref('')
const creditScoreWorkbenchFields = ['enterpriseId', 'regionCode']
const AZB_CREDIT_DIMENSION_LABEL_MAP = Object.freeze({
  salaryScore: '现场活跃',
  socialTaxScore: '协同覆盖',
  工资: '现场活跃',
  '\u5de5\u8d44\u53d1\u653e': '现场活跃',
  '\u5de5\u8d44\u53d1\u653e\u5408\u89c4': '现场活跃',
  工资维度: '现场活跃维度',
  '\u793e\u4fdd\u7a0e\u52a1': '协同覆盖',
  社保个税: '协同覆盖',
  '\u793e\u4fdd\u7a0e\u52a1\u7ef4\u5ea6': '协同覆盖维度',
  社保个税维度: '协同覆盖维度'
})

const roleView = computed(() => {
  if (isBankRole.value) return 'bank'
  if (isInsurerRole.value) return 'insurer'
  const roles = userStore.roles || []
  if (roles.includes('ygb_enterprise_admin') || roles.includes('ygb_enterprise_operator')) {
    return 'site-enterprise'
  }
  return 'emergency'
})

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
  resetQuery: pageResetQuery,
  openGenerateDialog,
  submitGenerate,
  handleExport,
  openDetail,
  syncCurrentScore
} = useCreditScorePage({
  exportFilePrefix: 'azb_credit_score',
  canMutate: () => !isReadOnlyRole.value,
  getCurrentList: () => visibleScoreList.value,
  afterList: () => {
    syncActiveFocus()
  },
  immediate: false
})

const portalExplanations = computed(() => summaryData.value.azbExplanation || [])
const portalExplanationItems = computed(() => decoratePortalExplanationItems(portalExplanations.value, {
  portalCode: 'azb',
  panelTitle: '6.1 风险分层解释',
  panelDescription: '风险等级、尾部对象和分层摘要统一来自信用解释聚合接口。'
}))
const portalExplanationSummary = computed(() => resolvePortalExplanationSummary(portalExplanationItems.value, ''))

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

function matchScoreFocus(row, focusKey) {
  if (!row || !focusKey) {
    return false
  }
  if (focusKey === 'red') return row.colorCode === 'RED'
  if (focusKey === 'yellow') return row.colorCode === 'YELLOW'
  if (focusKey === 'd') return row.creditLevel === 'D'
  if (focusKey === 'green') return row.colorCode === 'GREEN' && ['A', 'B'].includes(row.creditLevel)
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
  syncCurrentScore()
}

function normalizeAzbCreditLabel(text) {
  if (!text) return text
  return Object.entries(AZB_CREDIT_DIMENSION_LABEL_MAP).reduce((current, [source, target]) => {
    return current.replaceAll(source, target)
  }, String(text))
}

function buildDetailHintTags(score, focus) {
  if (!score) {
    if (focus?.title) {
      return [
        { label: `当前焦点：${focus.title}`, type: 'info' },
        { label: `建议动作：${focus.actionText}`, type: 'warning' }
      ]
    }
    return [{ label: '请选择企业查看治理提示', type: 'info' }]
  }

  const tags = []
  const totalScore = Number(score.totalScore || 0)
  const salaryScore = Number(score.salaryScore || 0)
  const governanceScore = Number(score.governanceScore || 0)
  const socialTaxScore = Number(score.socialTaxScore || 0)

  if (score.colorCode === 'RED') {
    tags.push({ label: '当前为红码企业，建议优先进入风险治理链路', type: 'danger' })
  }
  if (score.creditLevel === 'D') {
    tags.push({ label: '当前为 D 级企业，适合优先逐项复核低分维度', type: 'warning' })
  }
  if (score.colorCode === 'YELLOW') {
    tags.push({ label: '当前为黄码边缘对象，建议先看异常说明和整改进度', type: 'warning' })
  }
  if (salaryScore > 0 && salaryScore < 80) {
    tags.push({ label: '收入稳定维度偏弱，建议回看波动来源、预警联动和现场协同处置结果', type: 'info' })
  }
  if (socialTaxScore > 0 && socialTaxScore < 80) {
    tags.push({ label: '联动底数维度偏弱，建议核对社保、税务来源和协同回写是否一致', type: 'info' })
  }
  if (governanceScore > 0 && governanceScore < 80) {
    tags.push({ label: '治理维度偏弱，建议先核实预警处置闭环和现场整改留痕', type: 'warning' })
  }
  if (score.colorCode === 'GREEN' && ['A', 'B'].includes(score.creditLevel) && totalScore >= 85) {
    tags.push({ label: '当前为稳定绿码对象，可作为区域样本或企业整改对照', type: 'success' })
  }
  if (!tags.length) {
    tags.push({ label: '当前评分结构完整，建议优先查看详情后再决定后续动作', type: 'success' })
  }
  return tags
}

const roleBadge = computed(() => {
  if (roleView.value === 'bank') return '银行只读协同'
  if (roleView.value === 'insurer') return '保险只读协同'
  if (roleView.value === 'site-enterprise') return '企业风险复核'
  return '应急监管处置'
})

const roleTitle = computed(() => {
  if (roleView.value === 'bank') return '区域风险分层与联合风控复核看板'
  if (roleView.value === 'insurer') return '风险企业信用分层协同台账'
  if (roleView.value === 'site-enterprise') return '企业信用短板整改与重评分工作台'
  return '风险分层对象治理与压降工作台'
})

const roleDescription = computed(() => {
  if (roleView.value === 'bank') {
    return '围绕红码企业、低分对象和区域分层结果做只读复核，用于辅助信用判断和联合风控。'
  }
  if (roleView.value === 'insurer') {
    return '围绕红码企业、D级企业和低分维度做协同复核，优先识别适合纳入事故预防服务的风险企业。'
  }
  if (roleView.value === 'site-enterprise') {
    return '围绕本单位综合得分、短板维度和重评分结果组织企业管理层持续跟踪，重点是把风险分层结果真正转成整改动作。'
  }
  return '围绕红码企业、D级企业、区域尾部对象和短板维度组织应急监管工作流，优先压降区域风险。'
})

const summaryCards = computed(() => {
  if (roleView.value === 'bank') {
    return [
      summaryCard('average', '平均得分', formatDecimal(summaryData.value.averageScore), '分', '用于判断当前样本范围整体信用水平。', 'azb-summary-card--success'),
      summaryCard('red', '红码企业', valueOrDefault(summaryData.value.redCount, 0), '家', '优先影响联合风控判断的高风险对象。', 'azb-summary-card--danger'),
      summaryCard('d', 'D级企业', valueOrDefault(summaryData.value.dCount, 0), '家', '需要重点关注的尾部企业。'),
      summaryCard('highGrade', 'A/B级企业', valueOrDefault(summaryData.value.highGradeCount, 0), '家', '可作为区域稳定样本对照。', 'azb-summary-card--primary')
    ]
  }
  if (roleView.value === 'insurer') {
    return [
      summaryCard('red', '红码企业', valueOrDefault(summaryData.value.redCount, 0), '家', '更适合优先纳入事故预防协同。', 'azb-summary-card--danger'),
      summaryCard('d', 'D级企业', valueOrDefault(summaryData.value.dCount, 0), '家', '需要逐维度复核低分原因。'),
      summaryCard('yellow', '黄码企业', valueOrDefault(summaryData.value.yellowCount, 0), '家', '适合提前干预避免继续下滑。', 'azb-summary-card--warning'),
      summaryCard('average', '平均得分', formatDecimal(summaryData.value.averageScore), '分', '当前样本范围的综合得分水平。', 'azb-summary-card--success')
    ]
  }
  if (roleView.value === 'site-enterprise') {
    return [
      summaryCard('total', '评分记录', valueOrDefault(summaryData.value.totalCount, total.value), '条', '当前筛选条件下的企业评分总量。'),
      summaryCard('average', '平均得分', formatDecimal(summaryData.value.averageScore), '分', '用于判断本单位评分整体水平。', 'azb-summary-card--success'),
      summaryCard('yellow', '黄码企业', valueOrDefault(summaryData.value.yellowCount, 0), '家', '这些对象最适合优先补说明和整改。', 'azb-summary-card--warning'),
      summaryCard('highGrade', 'A/B级企业', valueOrDefault(summaryData.value.highGradeCount, 0), '家', '可作为本单位整改对照样本。', 'azb-summary-card--primary')
    ]
  }
  return [
    summaryCard('total', '评分记录', valueOrDefault(summaryData.value.totalCount, total.value), '条', '当前筛选条件下的信用评分总量。'),
    summaryCard('red', '红码企业', valueOrDefault(summaryData.value.redCount, 0), '家', '应急侧优先压降的风险对象。', 'azb-summary-card--danger'),
    summaryCard('d', 'D级企业', valueOrDefault(summaryData.value.dCount, 0), '家', '需要重点关注的尾部样本。', 'azb-summary-card--warning'),
    summaryCard('average', '平均得分', formatDecimal(summaryData.value.averageScore), '分', '用于对照当前区域信用整体水平。', 'azb-summary-card--success')
  ]
})

const resolvedSummaryCards = computed(() => summaryCards.value.map((item, index) => ({
  ...item,
  note: resolveExplanationFirstText(item.note, resolvePortalExplanationItem(index))
})))

const focusQueues = computed(() => {
  if (roleView.value === 'bank') {
    return [
      focusQueue('red', '红码企业', valueOrDefault(summaryData.value.redCount, 0), '家', '先看会影响联合风控判断的高风险对象。', '查看重点对象'),
      focusQueue('d', 'D级企业', valueOrDefault(summaryData.value.dCount, 0), '家', '尾部企业更适合作为信用复核入口。', '查看尾部企业'),
      focusQueue('yellow', '黄码企业', valueOrDefault(summaryData.value.yellowCount, 0), '家', '提前识别可能继续下滑的边缘对象。', '查看边缘对象')
    ]
  }
  if (roleView.value === 'insurer') {
    return [
      focusQueue('red', '红码企业', valueOrDefault(summaryData.value.redCount, 0), '家', '优先识别需要继续事故预防服务协同的对象。', '查看高风险对象'),
      focusQueue('d', 'D级企业', valueOrDefault(summaryData.value.dCount, 0), '家', '适合优先回看低分维度和治理缺口。', '查看低分对象'),
      focusQueue('yellow', '黄码企业', valueOrDefault(summaryData.value.yellowCount, 0), '家', '提前干预可避免风险继续外溢。', '查看预警对象')
    ]
  }
  if (roleView.value === 'site-enterprise') {
    return [
      focusQueue('d', 'D级企业', valueOrDefault(summaryData.value.dCount, 0), '家', '先锁定真正拖低企业风险分层结果的尾部对象。', '进入整改链路'),
      focusQueue('yellow', '黄码企业', valueOrDefault(summaryData.value.yellowCount, 0), '家', '黄码更适合通过补说明和重评分快速改善。', '补异常说明'),
      focusQueue('green', '稳定绿码样本', valueOrDefault(summaryData.value.highGradeCount, 0), '家', '可作为本单位整改动作的对照样本。', '查看对照样本')
    ]
  }
  return [
    focusQueue('red', '红码企业', valueOrDefault(summaryData.value.redCount, 0), '家', '应急侧先锁定最需要压降的高风险对象。', '进入治理复核'),
    focusQueue('d', 'D级企业', valueOrDefault(summaryData.value.dCount, 0), '家', '适合逐项核查低分维度和治理短板。', '查看尾部企业'),
    focusQueue('yellow', '黄码企业', valueOrDefault(summaryData.value.yellowCount, 0), '家', '这类对象更适合提前介入避免继续下滑。', '查看边缘对象')
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

const workbenchContext = computed(() => buildWorkbenchContext(route.query, {
  fields: creditScoreWorkbenchFields,
  sourceLabel: '信用工作台',
  title: '当前信用台账沿用了工作台来源条件',
  description: '已按企业或行政区划范围锁定评分结果，适合继续处理红黄码、尾部企业和复核动作。',
  fieldLabels: {
    enterpriseId: '企业',
    regionCode: '行政区划'
  },
  fieldFormatters: {
    enterpriseId: value => {
      const matched = enterpriseOptions.value.find(item => String(item.enterpriseId) === String(value))
      return matched ? matched.enterpriseName : value
    },
    regionCode: value => regionNameMap[value] || value
  }
}))

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
      { label: '当前区域', value: queryParams.value.regionCode ? (regionNameMap[queryParams.value.regionCode] || queryParams.value.regionCode) : '全部区域' },
      { label: '下一步', value: activeFocus.value?.actionText || '-' }
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
  if (isReadOnlyRole.value) {
    return { label: '查看详情', action: 'detail' }
  }
  if (['RED', 'YELLOW'].includes(currentScore.value.colorCode) || currentScore.value.creditLevel === 'D') {
    return { label: '按当前企业重生成', action: 'generate' }
  }
  return { label: '查看详情', action: 'detail' }
})

const currentScoreActionSummary = computed(() => {
  return resolvePortalExplanationSummary(
    portalExplanationItems.value,
    currentScore.value
      ? '当前信用评分对象的风险等级、尾部原因和推荐复核动作已按 6.1 风险分层口径展示。'
      : '当前暂无信用评分对象，可继续从 6.1 风险分层解释面板进入主下钻模块。'
  )
})

const currentScoreActionTags = computed(() => buildDetailHintTags(currentScore.value || undefined, activeFocus.value))

const displayFactorRows = computed(() => factorRows.value.map(item => ({
  ...item,
  name: normalizeAzbCreditLabel(item.name),
  metricLabel: normalizeAzbCreditLabel(item.metricLabel),
  description: normalizeAzbCreditLabel(item.description)
})))

const workflowSteps = computed(() => {
  const aggregateSteps = portalExplanationItems.value
    .filter(item => item.summary || item.explanationSummary || item.sourceDescription)
    .slice(0, 3)
    .map(item => ({
      label: item.dimensionName || item.moduleLabel || item.moduleCode || '6.1 Explanation',
      desc: item.summary || item.explanationSummary || item.sourceDescription || ''
    }))

  if (aggregateSteps.length) {
    return aggregateSteps
  }

  if (roleView.value === 'bank') {
    return [
      { label: '先看红码与 D 级对象', desc: '优先判断当前区域是否存在需要信用侧重点复核的尾部企业。' },
      { label: '再看黄码与稳定度', desc: '通过黄码数量和平均分判断区域风险是否有继续扩散迹象。' },
      { label: '最后回到信用与报表协同', desc: '必要时再进入企业、统计和驾驶舱做联合判断。' }
    ]
  }
  if (roleView.value === 'insurer') {
    return [
      { label: '先锁定红码企业', desc: '优先确认最需要纳入事故预防服务的高风险对象。' },
      { label: '再看 D 级维度短板', desc: '通过低分维度判断是预警、设备还是高处作业等治理链路问题更突出。' },
      { label: '最后回到详情复核', desc: '在详情里查看维度得分明细，再决定是否继续协同。' }
    ]
  }
  if (roleView.value === 'site-enterprise') {
    return [
      { label: '先锁定低分对象', desc: '优先处理拖低企业画像和综合评分的尾部对象。' },
      { label: '再看短板维度', desc: '通过维度得分和说明回到具体治理链路整改。' },
      { label: '最后跟踪重评分结果', desc: '在整改后重新生成评分，确认等级和色码是否改善。' }
    ]
  }
  return [
    { label: '先锁定红码企业', desc: '应急侧优先处理最需要压降的红码和 D 级对象。' },
    { label: '再看短板维度与尾部对象', desc: '通过低分维度确认治理重点是否集中在某类区域或企业。' },
    { label: '最后回到详情与重评分', desc: '通过维度明细和重评分结果验证治理是否产生效果。' }
  ]
})

const hintTags = computed(() => {
  const tags = []
  const leadingExplanation = portalExplanationItems.value[0]
  if (leadingExplanation?.dimensionName) {
    tags.push({ label: `6.1主解释：${leadingExplanation.dimensionName}`, type: 'info' })
  }
  if (leadingExplanation?.moduleLabel) {
    tags.push({ label: `主下钻模块：${leadingExplanation.moduleLabel}`, type: 'success' })
  }
  if (!leadingExplanation?.dimensionName && portalExplanationSummary.value) {
    tags.push({ label: portalExplanationSummary.value, type: 'info' })
  }
  if (Number(summaryData.value.redCount || 0) > 0) {
    tags.push({ label: `当前有 ${summaryData.value.redCount || 0} 家红码企业，建议优先联动治理`, type: 'danger' })
  }
  if (Number(summaryData.value.dCount || 0) > 0) {
    tags.push({ label: `D级企业 ${summaryData.value.dCount || 0} 家，适合回到风险对象台账逐项压实整改`, type: 'warning' })
  }
  if (Number(summaryData.value.yellowCount || 0) > 0) {
    tags.push({ label: `黄码企业 ${summaryData.value.yellowCount || 0} 家，建议提前复核避免继续下滑`, type: 'info' })
  }
  if (roleView.value === 'site-enterprise' && Number(summaryData.value.highGradeCount || 0) > 0) {
    tags.push({ label: `当前已有 ${summaryData.value.highGradeCount || 0} 家 A/B级对象，可作为整改对照样本`, type: 'success' })
  }
  return tags.slice(0, 4)
})

const detailHintTags = computed(() => buildDetailHintTags(scoreDetail.value || currentScore.value, activeFocus.value))

const detailFocusText = computed(() => {
  if (roleView.value === 'bank') {
    return '重点看红码企业、D级对象和区域分层是否稳定，用于辅助信用与联合风控判断。'
  }
  if (roleView.value === 'insurer') {
    return '重点看红码等级、收入稳定/联动底数短板和指标偏差，用于判断事故预防协同和风险服务优先级。'
  }
  if (roleView.value === 'site-enterprise') {
    return '重点看哪些维度拖低了企业得分，再把整改动作回落到预警、设备、高处作业等具体治理台账。'
  }
  return '重点看红码等级、尾部维度和联动底数偏差是否集中，必要时优先进入区域或对象治理链路。'
})

const readOnlyAlertTitle = computed(() => `${readOnlyRoleLabel.value}：当前仅保留风险分层查看、详情和导出`)
const readOnlyAlertDescription = computed(() => {
  if (isBankRole.value) {
    return `${readOnlyRoleDescription.value} 当前页面聚焦信用分层、区域对比和重点企业详情，不展示评分生成动作。`
  }
  if (isInsurerRole.value) {
    return `${readOnlyRoleDescription.value} 当前页面聚焦风险分层、红黄绿码和详情复核，不展示评分生成动作。`
  }
  return `${readOnlyRoleDescription.value} 当前页面仅作为菜单收口兜底，不承担评分生成动作。`
})

function handlePrimaryScoreAction() {
  if (!currentScore.value) {
    return
  }
  if (primaryScoreAction.value.action === 'generate') {
    if (isReadOnlyRole.value) {
      proxy.$modal.msgWarning(`${readOnlyRoleLabel.value}仅保留风险分层查看、详情和导出，不能生成评分`)
      return
    }
    openGenerateDialog(currentScore.value)
    return
  }
  openDetail(currentScore.value)
}

function resetQuery() {
  pageResetQuery()
  applyWorkbenchRouteQuery(route.query, queryParams.value, creditScoreWorkbenchFields)
  getList()

watchEffect(() => {
  setPageGuide({
    title: roleTitle.value || '???????????',
    description: roleDescription.value || '?????????????????????????????????',
    portalExplanation: portalExplanationItems.value,
    focus: resolvedFocusQueues.value,
    selection: [...selectedScoreOverview.value, { label: '??????', value: currentScoreActionSummary.value }],
    workflow: workflowSteps.value,
    hints: [...currentScoreActionTags.value, ...hintTags.value].slice(0, 6)
  })
})

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
  border: 1px solid #dbe7f3;
  border-radius: 14px;
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
  background: linear-gradient(180deg, #ffffff 0%, #f3fbf7 100%);
}

.azb-summary-card--warning {
  background: linear-gradient(180deg, #ffffff 0%, #fff9ef 100%);
}

.azb-summary-card--danger {
  background: linear-gradient(180deg, #ffffff 0%, #fff4f4 100%);
}

.azb-summary-card--primary {
  background: linear-gradient(180deg, #ffffff 0%, #f4f8ff 100%);
}

.azb-focus-card :deep(.el-card__header) {
  padding: 18px 20px 0;
  border-bottom: none;
}

.azb-focus-card :deep(.el-card__body) {
  padding: 18px 20px 20px;
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

.azb-card-head--between {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: flex-start;
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

.azb-recommend-panel {
  margin-top: 16px;
  padding: 16px;
  border-radius: 12px;
  background: linear-gradient(135deg, rgba(11, 107, 120, 0.08), rgba(29, 78, 216, 0.05));
  border: 1px solid rgba(11, 107, 120, 0.16);
}

.azb-recommend-panel__title {
  font-size: 13px;
  font-weight: 600;
  color: #0b6b78;
}

.azb-recommend-panel__summary {
  margin: 8px 0 12px;
  font-size: 14px;
  line-height: 1.7;
  color: #1f2937;
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

.azb-detail-block {
  margin-top: 20px;
}

.azb-detail-block h3 {
  margin: 0 0 12px;
  font-size: 16px;
  color: #1f2d3d;
}

@media (max-width: 1200px) {
  .azb-summary-grid,
  .azb-focus-grid,
  .azb-source-list {
    grid-template-columns: 1fr;
  }

  .azb-card-head--between {
    flex-direction: column;
  }
}
</style>
