<template>
  <div class="app-container azb-page azb-prevention-fund-workbench">
    <section class="gov-page-header azb-page__header">
      <div>
        <p class="azb-page__eyebrow">事故预防资金治理</p>
        <h1 class="azb-page__title">{{ roleTitle }}</h1>
        <p class="azb-page__desc">
          {{ roleDescription }}
          当前继续复用统一预防资金台账与回写接口，但安责保前端已按应急监管、企业复核、保险协同和银行协同四类视角重排焦点对象、结算顺序和详情重点。
        </p>
      </div>
      <div class="azb-page__tip">
        <div class="azb-page__tip-item">当前视角：{{ roleBadge }}</div>
        <div class="azb-page__tip-item">优先关注：低余额、使用中未结算、凭证缺失、长期人工维护对象。</div>
        <div class="azb-page__tip-item">资金维护仍走统一后台校验，已使用金额不能大于计提金额。</div>
        <div class="azb-page__tip-item">当前来源仍以 Stub 为主，后续可平滑切换到正式机构回写链路。</div>
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
        <el-form-item label="统计月份">
          <el-date-picker v-model="queryParams.statMonth" type="month" value-format="YYYY-MM" format="YYYY-MM" style="width: 170px" />
        </el-form-item>
        <el-form-item label="行政区划">
          <el-select v-model="queryParams.regionCode" clearable style="width: 180px">
            <el-option v-for="item in regionOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="企业">
          <el-select v-model="queryParams.enterpriseId" clearable filterable style="width: 220px">
            <el-option v-for="item in enterpriseOptions" :key="item.enterpriseId" :label="item.enterpriseName" :value="item.enterpriseId" />
          </el-select>
        </el-form-item>
        <el-form-item label="资金状态">
          <el-select v-model="queryParams.fundStatus" clearable style="width: 160px">
            <el-option v-for="item in fundStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="企业名称">
          <el-input v-model="queryParams.enterpriseName" clearable style="width: 220px" @keyup.enter="handleQuery" />
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
          <el-button
            type="success"
            plain
            icon="Edit"
            :disabled="!currentFund"
            @click="openEditDialog()"
            v-hasPermi="['ygb:preventionFund:edit']"
          >
            维护资金
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['ygb:preventionFund:export']">导出</el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
      </el-row>
    </el-card>

    <el-card class="table-card azb-table-card" shadow="never">
      <template #header>
        <div class="azb-card-head azb-card-head--between">
          <div>
            <div class="azb-card-head__title">资金治理台账</div>
            <div class="azb-card-head__desc">{{ focusTableHint }}</div>
          </div>
          <div class="azb-card-head__desc">当前总量 {{ total }} 条</div>
        </div>
      </template>
      <el-table v-loading="loading" :data="visibleFundList" @row-click="handleRowClick">
        <el-table-column label="资金池ID" prop="fundId" width="100" />
        <el-table-column label="统计月份" prop="statMonth" width="110" />
        <el-table-column label="企业名称" prop="enterpriseName" min-width="220" />
        <el-table-column label="行政区划" width="140">
          <template #default="scope">
            {{ formatRegionName(scope.row.regionCode, '-') }}
          </template>
        </el-table-column>
        <el-table-column label="计提金额" width="120">
          <template #default="scope">
            {{ formatMoney(scope.row.accruedAmount) }}
          </template>
        </el-table-column>
        <el-table-column label="已使用金额" width="120">
          <template #default="scope">
            {{ formatMoney(scope.row.usedAmount) }}
          </template>
        </el-table-column>
        <el-table-column label="可用余额" width="120">
          <template #default="scope">
            {{ formatMoney(scope.row.remainingAmount) }}
          </template>
        </el-table-column>
        <el-table-column label="资金状态" width="110">
          <template #default="scope">
            <dict-tag :options="fundStatusOptions" :value="scope.row.fundStatus" />
          </template>
        </el-table-column>
        <el-table-column label="使用用途" prop="usagePurpose" min-width="220" show-overflow-tooltip />
        <el-table-column label="来源模式" width="110">
          <template #default="scope">
            {{ sourceModeLabel(scope.row.sourceMode) }}
          </template>
        </el-table-column>
        <el-table-column label="最近结算时间" width="180">
          <template #default="scope">
            {{ formatDateTime(scope.row.lastSettleTime) }}
          </template>
        </el-table-column>
        <el-table-column class-name="table-fill-column" min-width="1" />

        <el-table-column label="操作" fixed="right" align="center" width="140">
          <template #default="scope">
            <el-button link type="info" icon="View" @click.stop="openDetail(scope.row)">详情</el-button>
            <el-button
              v-if="!isReadOnlyRole"
              link
              type="primary"
              icon="Edit"
              @click.stop="openEditDialog(scope.row)"
              v-hasPermi="['ygb:preventionFund:edit']"
            >
              维护
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>

    <el-dialog v-if="!isReadOnlyRole" :title="title" v-model="editOpen" width="640px" append-to-body>
      <el-form ref="fundRef" :model="form" :rules="rules" label-width="110px">
        <el-form-item label="企业名称">
          <el-input v-model="form.enterpriseName" disabled />
        </el-form-item>
        <el-form-item label="统计月份">
          <el-input v-model="form.statMonth" disabled />
        </el-form-item>
        <el-form-item label="计提金额">
          <el-input :model-value="formatMoney(form.accruedAmount)" disabled />
        </el-form-item>
        <el-form-item label="已使用金额" prop="usedAmount">
          <el-input-number v-model="form.usedAmount" :min="0" :step="100" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="预计余额">
          <el-input :model-value="remainingPreview" disabled />
        </el-form-item>
        <el-form-item label="使用用途" prop="usagePurpose">
          <el-input v-model="form.usagePurpose" type="textarea" :rows="3" placeholder="请输入本次资金使用用途说明" />
        </el-form-item>
        <el-form-item label="凭证地址" prop="evidenceUrl">
          <el-input v-model="form.evidenceUrl" placeholder="例如：stub://aqins/fund/evidence" />
        </el-form-item>
        <el-form-item label="结算时间" prop="lastSettleTime">
          <el-date-picker
            v-model="form.lastSettleTime"
            type="datetime"
            value-format="YYYY-MM-DD HH:mm:ss"
            format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确定</el-button>
          <el-button @click="editOpen = false">取消</el-button>
        </div>
      </template>
    </el-dialog>

    <page-detail-dialog v-model="detailOpen" title="资金详情" width="760px">
      <template v-if="detailFund">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="资金池ID">{{ detailFund.fundId }}</el-descriptions-item>
          <el-descriptions-item label="鍏宠仈淇濆崟ID">{{ detailFund.policyId || '-' }}</el-descriptions-item>
          <el-descriptions-item label="企业名称">{{ detailFund.enterpriseName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="统计月份">{{ detailFund.statMonth || '-' }}</el-descriptions-item>
          <el-descriptions-item label="行政区划">{{ formatRegionName(detailFund.regionCode, '-') }}</el-descriptions-item>
          <el-descriptions-item label="资金状态">
            <dict-tag :options="fundStatusOptions" :value="detailFund.fundStatus" />
          </el-descriptions-item>
          <el-descriptions-item label="计提金额">{{ formatMoney(detailFund.accruedAmount) }}</el-descriptions-item>
          <el-descriptions-item label="已使用金额">{{ formatMoney(detailFund.usedAmount) }}</el-descriptions-item>
          <el-descriptions-item label="可用余额">{{ formatMoney(detailFund.remainingAmount) }}</el-descriptions-item>
          <el-descriptions-item label="来源模式">{{ sourceModeLabel(detailFund.sourceMode) }}</el-descriptions-item>
          <el-descriptions-item label="最近结算时间">{{ formatDateTime(detailFund.lastSettleTime) }}</el-descriptions-item>
          <el-descriptions-item label="更新人">{{ detailFund.updateBy || '-' }}</el-descriptions-item>
          <el-descriptions-item label="当前视角重点" :span="2">{{ detailFocusText }}</el-descriptions-item>
          <el-descriptions-item label="使用用途" :span="2">{{ detailFund.usagePurpose || '-' }}</el-descriptions-item>
          <el-descriptions-item label="凭证地址" :span="2">{{ detailFund.evidenceUrl || '-' }}</el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ detailFund.remark || '-' }}</el-descriptions-item>
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

<script setup name="AzbPreventionFund">
import { computed, getCurrentInstance, ref, watch, watchEffect } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useWorkbenchAssist } from '@/composables/useWorkbenchAssist'
import useUserStore from '@/store/modules/user'
import { decoratePortalExplanationItems, openPortalExplanationAction } from '@/utils/portalExplanation'
import { useRoleViewMode } from '@/utils/roleView'
import { applyWorkbenchRouteQuery, buildWorkbenchContext, stripWorkbenchRouteQuery } from '@/utils/workbenchLink'
import { useAuthorizedRegionOptions } from '@/utils/regionScope'
import {
  focusQueue,
  formatDateTime,
  formatMoney,
  formatRegionName,
  fundStatusOptions,
  isLowBalance,
  matchFundFocus,
  prioritizeFocusRows,
  regionOptions as allRegionOptions,
  sourceModeLabel,
  summaryCard,
  usePreventionFundPage,
  valueOrDefault
} from '@/views/preventionFund/usePreventionFundPage'

const { proxy } = getCurrentInstance()
const regionOptions = useAuthorizedRegionOptions(allRegionOptions)
const route = useRoute()
const { setPageGuide } = useWorkbenchAssist()
const router = useRouter()
const userStore = useUserStore()
const { isReadOnlyRole, isInsurerRole, isBankRole, readOnlyRoleLabel, readOnlyRoleDescription } = useRoleViewMode()
const activeFocusKey = ref('')
const preventionFundWorkbenchFields = ['enterpriseId', 'regionCode', 'statMonth', 'fundStatus', 'focusKey']
const workbenchClearLabel = '清空来源条件'
const preventionFundInitialQuery = {}

applyWorkbenchRouteQuery(route.query, preventionFundInitialQuery, preventionFundWorkbenchFields)

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
  if (roleView.value === 'bank') return '银行预防资金协同看板'
  if (roleView.value === 'insurer') return '安责保预防资金回写复核台'
  if (roleView.value === 'site-enterprise') return '企业事故预防资金执行台账'
  return '事故预防资金风险处置工作台'
})

const roleDescription = computed(() => {
  if (roleView.value === 'bank') {
    return '面向银行协同只读查看资金余额、信用联动和区域报表口径，重点判断是否存在持续低余额或长期未回写对象。'
  }
  if (roleView.value === 'insurer') {
    return '面向保险机构聚焦预防资金余额、结算凭证和回写留痕，重点复核哪些资金对象仍处于 Stub 或人工维护状态。'
  }
  if (roleView.value === 'site-enterprise') {
    return '面向企业管理员、现场负责人和财务经办统一处理事故预防资金的用途登记、凭证补录和回写留痕。'
  }
  return '面向应急监管统一识别区域低余额、长期未结算和回写缺失的资金对象，形成可连续处置的治理队列。'
})

const workbenchContext = computed(() => buildWorkbenchContext(route.query, {
  fields: preventionFundWorkbenchFields,
  sourceLabel: '工作台',
  title: '当前资金台账沿用了工作台来源条件',
  description: '已按企业、区域、统计月份和资金状态锁定数据范围，适合继续处理低余额、使用中和凭证缺失对象。',
  fieldLabels: {
    enterpriseId: '企业',
    regionCode: '行政区划',
    statMonth: '统计月份',
    fundStatus: '资金状态',
    focusKey: '焦点类型'
  },
  fieldFormatters: {
    enterpriseId: value => enterpriseName(value) || value,
    regionCode: value => formatRegionName(value, value),
    fundStatus: value => fundStatusLabel(value),
    focusKey: value => preventionFundFocusLabel(value)
  }
}))

const {
  loading,
  showSearch,
  total,
  fundList,
  enterpriseOptions,
  currentFund,
  detailFund,
  detailOpen,
  editOpen,
  title,
  summaryData,
  form,
  queryParams,
  rules,
  remainingPreview,
  getList,
  syncCurrentFund,
  handleRowClick,
  handleQuery,
  resetQuery: pageResetQuery,
  openEditDialog,
  submitForm,
  openDetail,
  handleExport
} = usePreventionFundPage({
  exportFilePrefix: 'azb_prevention_fund',
  canMutate: () => !isReadOnlyRole.value,
  onBlockedAction: blockReadOnlyAction,
  getCurrentList: () => visibleFundList.value,
  requireUsagePurpose: true,
  initialQueryParams: preventionFundInitialQuery
})

function buildPreventionFundExplanationQuery(extraQuery = {}) {
  return {
    enterpriseId: queryParams.value.enterpriseId,
    regionCode: queryParams.value.regionCode,
    statMonth: queryParams.value.statMonth,
    ...extraQuery
  }
}

const portalExplanations = computed(() => {
  const lowBalanceItem = {
    key: 'lowBalance',
    dimensionName: '低余额对象',
    currentValue: valueOrDefault(summaryData.value.lowBalanceCount, 0),
    targetValue: '余额充足',
    summary: '低余额对象说明后续投入或核销安排可能不足，应优先核定资金续投和回写计划。',
    evidenceModule: 'preventionFund',
    recommendModule: 'preventionFund',
    defaultQuery: buildPreventionFundExplanationQuery({ focusKey: 'lowBalance' }),
    sourceLabel: '6.1 预防资金解释',
    sourceDescription: '从预防资金维度继续核查低余额治理对象。'
  }
  const inUseItem = {
    key: 'inUse',
    dimensionName: '使用中资金',
    currentValue: valueOrDefault(summaryData.value.inUseCount, 0),
    targetValue: '及时结算',
    summary: '使用中资金对象应持续补齐用途、结算时间和回写留痕，避免月度复核卡点。',
    evidenceModule: 'preventionFund',
    recommendModule: 'preventionFund',
    defaultQuery: buildPreventionFundExplanationQuery({ fundStatus: '2', focusKey: 'inUse' }),
    sourceLabel: '6.1 预防资金解释',
    sourceDescription: '从预防资金维度继续核查使用中待结算对象。'
  }
  const missingEvidenceItem = {
    key: 'missingEvidence',
    dimensionName: '缺少凭证',
    currentValue: valueOrDefault(summaryData.value.missingEvidenceCount, 0),
    targetValue: '0',
    summary: '缺少凭证会直接削弱治理闭环和后续核查证据，应优先补齐凭证链路。',
    evidenceModule: 'preventionFund',
    recommendModule: 'preventionFund',
    defaultQuery: buildPreventionFundExplanationQuery({ focusKey: 'missingEvidence' }),
    sourceLabel: '6.1 预防资金解释',
    sourceDescription: '从预防资金维度继续核查凭证缺失对象。'
  }
  const nonStubItem = {
    key: 'nonStub',
    dimensionName: '正式回写来源',
    currentValue: valueOrDefault(summaryData.value.nonStubCount, 0),
    targetValue: '持续提升',
    summary: '正式回写覆盖度越高，越有利于沉淀真实治理证据并减少人工维护噪声。',
    evidenceModule: 'preventionFund',
    recommendModule: 'preventionFund',
    defaultQuery: buildPreventionFundExplanationQuery({ focusKey: 'nonStub' }),
    sourceLabel: '6.1 预防资金解释',
    sourceDescription: '从预防资金维度继续核查正式回写覆盖情况。'
  }

  if (roleView.value === 'bank') {
    return [lowBalanceItem, missingEvidenceItem, nonStubItem]
  }
  if (roleView.value === 'insurer') {
    return [missingEvidenceItem, inUseItem, lowBalanceItem, nonStubItem]
  }
  if (roleView.value === 'site-enterprise') {
    return [inUseItem, missingEvidenceItem, lowBalanceItem]
  }
  return [lowBalanceItem, inUseItem, missingEvidenceItem]
})

const portalExplanationItems = computed(() => decoratePortalExplanationItems(portalExplanations.value, {
  portalCode: 'azb',
  panelTitle: '6.1 预防资金解释',
  panelDescription: '围绕资金余额、回写状态和凭证留痕输出治理解释。'
}))

const summaryCards = computed(() => {
  const totalCount = valueOrDefault(summaryData.value.totalCount, total.value)
  const remainingAmountTotal = valueOrDefault(summaryData.value.remainingAmountTotal, 0)
  const lowBalanceCount = valueOrDefault(summaryData.value.lowBalanceCount, 0)
  const inUseCount = valueOrDefault(summaryData.value.inUseCount, 0)
  const missingEvidenceCount = valueOrDefault(summaryData.value.missingEvidenceCount, 0)
  const nonStubCount = valueOrDefault(summaryData.value.nonStubCount, 0)
  const summaryCard = (key, label, value, unit, note, cardClass = '') => ({ key, label, value, unit, note, cardClass })

  if (roleView.value === 'bank') {
    return [
      summaryCard('remaining', '可用余额', remainingAmountTotal, '元', '当前范围内可用资金余额。', 'azb-summary-card--success'),
      summaryCard('lowBalance', '低余额对象', lowBalanceCount, '条', '可能需要追加跟进的资金对象。', 'azb-summary-card--danger'),
      summaryCard('nonStub', '正式来源', nonStubCount, '条', '已脱离 Stub 来源的记录数。', ''),
      summaryCard('missingEvidence', '缺少凭证', missingEvidenceCount, '条', '仍缺结算凭证的对象。', 'azb-summary-card--warning')
    ]
  }

  if (roleView.value === 'insurer') {
    return [
      summaryCard('lowBalance', '低余额对象', lowBalanceCount, '条', '优先复核低余额对象。', 'azb-summary-card--danger'),
      summaryCard('inUse', '使用中资金', inUseCount, '条', '仍处于使用中的资金对象。', 'azb-summary-card--warning'),
      summaryCard('missingEvidence', '缺少凭证', missingEvidenceCount, '条', '凭证链路仍不完整。', 'azb-summary-card--primary'),
      summaryCard('remaining', '可用余额', remainingAmountTotal, '元', '当前范围内可用资金余额。', 'azb-summary-card--success')
    ]
  }

  if (roleView.value === 'site-enterprise') {
    return [
      summaryCard('inUse', '使用中资金', inUseCount, '条', '仍需继续结算跟进的资金对象。', 'azb-summary-card--warning'),
      summaryCard('missingEvidence', '缺少凭证', missingEvidenceCount, '条', '仍需补齐凭证材料。', 'azb-summary-card--primary'),
      summaryCard('lowBalance', '低余额对象', lowBalanceCount, '条', '需重点关注的低余额对象。', 'azb-summary-card--danger'),
      summaryCard('remaining', '可用余额', remainingAmountTotal, '元', '当前查询范围内的可用余额。', 'azb-summary-card--success')
    ]
  }

  return [
    summaryCard('total', '监管总量', totalCount, '条', '当前纳入监管的资金记录总数。', ''),
    summaryCard('lowBalance', '低余额对象', lowBalanceCount, '条', '低余额风险对象。', 'azb-summary-card--danger'),
    summaryCard('inUse', '使用中资金', inUseCount, '条', '仍在使用中的资金对象。', 'azb-summary-card--warning'),
    summaryCard('remaining', '可用余额', remainingAmountTotal, '元', '当前范围内可用资金余额。', 'azb-summary-card--success')
  ]
})

const focusQueues = computed(() => {
  const lowBalanceCount = valueOrDefault(summaryData.value.lowBalanceCount, 0)
  const inUseCount = valueOrDefault(summaryData.value.inUseCount, 0)
  const missingEvidenceCount = valueOrDefault(summaryData.value.missingEvidenceCount, 0)
  const nonStubCount = valueOrDefault(summaryData.value.nonStubCount, 0)
  const totalCount = valueOrDefault(summaryData.value.totalCount, total.value)

  if (roleView.value === 'bank') {
    return [
      focusQueue('lowBalance', '低余额协同对象', lowBalanceCount, '条', '优先识别余额偏低且可能影响后续资金安排的对象。', '联动风险分层'),
      focusQueue('missingEvidence', '缺少凭证对象', missingEvidenceCount, '条', '查看哪些资金对象仍未形成可核对的结算凭证。', '提示业务补录'),
      focusQueue('nonStub', '正式回写覆盖', nonStubCount, '条', '判断当前区域正式回写替代 Stub 的推进情况。', '检查回写质量')
    ]
  }

  if (roleView.value === 'insurer') {
    return [
      focusQueue('missingEvidence', '结算凭证缺失', missingEvidenceCount, '条', '优先补齐能证明资金使用和回写留痕的关键材料。', '优先回写复核'),
      focusQueue('inUse', '使用中待回写', inUseCount, '条', '持续跟进使用中的资金对象，补齐用途和结算时间。', '补录用途时间'),
      focusQueue('lowBalance', '低余额预警', lowBalanceCount, '条', '尽快确认低余额对象是否需要后续核销或追加投入。', '核对后续安排')
    ]
  }

  if (roleView.value === 'site-enterprise') {
    return [
      focusQueue('inUse', '当前待补回写对象', inUseCount, '条', '先找出处于使用中且最容易卡住回写闭环的资金对象。', '先补用途结算'),
      focusQueue('missingEvidence', '凭证待补对象', missingEvidenceCount, '条', '财务经办优先补齐回写凭证或佐证链接。', '补齐凭证链接'),
      focusQueue('lowBalance', '低余额执行对象', lowBalanceCount, '条', '现场和财务同步确认是否需要安排后续投入或核销。', '核对余额计划')
    ]
  }

  return [
    focusQueue('lowBalance', '区域低余额对象', lowBalanceCount, '条', '优先处置可能引发后续投入风险的资金池记录。', '先看余额风险'),
    focusQueue('inUse', '长期使用中对象', inUseCount, '条', '持续使用但未闭环的对象通常伴随回写和凭证问题。', '跟进结算闭环'),
    focusQueue('missingEvidence', '凭证缺失对象', missingEvidenceCount, '条', '没有结算凭证或佐证链接，会直接影响后续监管核查。', '督促补齐材料'),
    focusQueue('all', '当前监管总量', totalCount, '条', '用于统看当前区域已纳入监管的资金池对象规模。', '查看整体盘子')
  ]
})

const activeFocus = computed(() => {
  const queues = focusQueues.value
  if (!queues.length) {
    return undefined
  }
  return queues.find(item => item.key === activeFocusKey.value) || queues[0]
})

const visibleFundList = computed(() => prioritizeFocusRows(fundList.value, row => matchFundFocus(row, activeFocus.value?.key)))

const focusTableHint = computed(() => {
  if (!activeFocus.value) {
    return '按当前筛选条件展示资金治理台账。'
  }
  return `当前优先展示“${activeFocus.value.title}”相关对象。`
})

const selectedFundOverview = computed(() => {
  if (!currentFund.value) {
    return [
      { label: '企业名称', value: '-' },
      { label: '区域', value: '-' },
      { label: activeFocus.value?.title || '当前焦点', value: '-' },
      { label: roleView.value === 'site-enterprise' ? '最近结算' : '来源模式', value: '-' }
    ]
  }
  return [
    { label: '企业名称', value: currentFund.value.enterpriseName || '-' },
    { label: '区域', value: formatRegionName(currentFund.value.regionCode, '-') },
    { label: activeFocus.value?.title || '当前焦点', value: activeFocusValue(currentFund.value) },
    {
      label: roleView.value === 'site-enterprise' ? '最近结算' : '来源模式',
      value: roleView.value === 'site-enterprise'
        ? formatDateTime(currentFund.value.lastSettleTime)
        : sourceModeLabel(currentFund.value.sourceMode)
    }
  ]
})

const primaryFundAction = computed(() => {
  if (!currentFund.value) {
    return { label: activeFocus.value?.actionText || '查看详情', action: 'detail' }
  }
  if (isReadOnlyRole.value) {
    return { label: '查看详情', action: 'detail' }
  }
  if (
    isLowBalance(currentFund.value.accruedAmount, currentFund.value.remainingAmount) ||
    String(currentFund.value.fundStatus) === '2' ||
    !currentFund.value.evidenceUrl ||
    !currentFund.value.usagePurpose
  ) {
    return { label: '维护资金', action: 'edit' }
  }
  return { label: '查看详情', action: 'detail' }
})

const currentFundActionSummary = computed(() => {
  if (!currentFund.value) {
    return activeFocus.value
      ? `当前聚焦“${activeFocus.value.title}”队列，可先从列表中选中一条资金对象。`
      : '请先选择一条资金记录查看当前处置建议。'
  }
  if (isReadOnlyRole.value) {
    return roleView.value === 'bank'
      ? '银行视角仅保留摘要、详情和导出能力。'
      : '当前资金页面为只读视角。'
  }
  if (!currentFund.value.evidenceUrl || !currentFund.value.usagePurpose) {
    return '当前资金记录仍缺少凭证或用途说明，应优先补齐回写材料。'
  }
  if (String(currentFund.value.fundStatus) === '2') {
    return '当前资金记录仍处于使用中，应持续跟进结算回写。'
  }
  if (isLowBalance(currentFund.value.accruedAmount, currentFund.value.remainingAmount)) {
    return '当前资金记录已进入低余额状态，应尽快确认后续安排。'
  }
  return '当前资金记录信息相对完整，可转入详情复核。'
})

const currentFundActionTags = computed(() => buildFundHintTags(currentFund.value || undefined, roleView.value, activeFocus.value))

const workflowSteps = computed(() => {
  if (roleView.value === 'bank') {
    return [
      { label: '识别低余额对象', desc: '先从余额偏低对象里判断哪些需要联动风险分层和区域风险口径。' },
      { label: '查看回写留痕', desc: '确认是否已有正式回写来源、凭证地址和最近结算时间。' },
      { label: '形成协同提示', desc: '将缺凭证、低余额和长期未结算对象回传业务侧继续跟进。' },
      { label: '留存区域口径', desc: '把当前筛选结果沉淀为协同报表和区域风险观察依据。' }
    ]
  }
  if (roleView.value === 'insurer') {
    return [
      { label: '筛出待复核资金', desc: '先锁定缺凭证、使用中和低余额对象，形成当天回写清单。' },
      { label: '补齐用途与凭证', desc: '重点确认资金去向、用途说明和结算证明材料。' },
      { label: '复核回写时间', desc: '确保最近结算时间和来源模式可用于后续核查留痕。' },
      { label: '完成回写闭环', desc: '将可核销对象推进到凭证齐全、状态可复核。' }
    ]
  }
  if (roleView.value === 'site-enterprise') {
    return [
      { label: '确认当月资金盘子', desc: '先看当前月哪些资金对象仍在使用中或余额偏低。' },
      { label: '登记使用用途', desc: '将项目、培训或服务支出的用途说明补齐到统一资金台账。' },
      { label: '补录结算凭证', desc: '财务经办补齐凭证地址和最近结算时间，避免复核卡点。' },
      { label: '完成月度留痕', desc: '将余额、用途和回写痕迹沉淀为月度复核依据。' }
    ]
  }
  return [
    { label: '锁定区域风险对象', desc: '先找出低余额、长期使用中和凭证缺失的重点对象。' },
    { label: '下钻查看资金详情', desc: '联动企业、来源模式和最近结算时间，判断风险原因。' },
    { label: '督促业务闭环', desc: '推动企业或保险机构补齐用途、凭证和回写痕迹。' },
    { label: '形成监管留痕', desc: '将当前筛选和关注对象沉淀为区域治理依据。' }
  ]
})

const hintTags = computed(() => buildFundHintTags(currentFund.value, roleView.value, activeFocus.value))
const detailHintTags = computed(() => buildFundHintTags(detailFund.value || currentFund.value, roleView.value, activeFocus.value))

const detailFocusText = computed(() => {
  const fund = detailFund.value || currentFund.value
  if (!fund) {
    return activeFocus.value?.desc || '请选择资金池查看当前视角重点。'
  }
  if (roleView.value === 'bank') {
    return '银行协同重点查看该对象是否存在低余额、缺凭证以及正式回写覆盖不足的问题，便于与信用和区域报表联动观察。'
  }
  if (roleView.value === 'insurer') {
    return '保险机构重点复核该对象的用途说明、凭证地址和最近结算时间，确保资金回写留痕连续可核对。'
  }
  if (roleView.value === 'site-enterprise') {
    return '企业侧应优先补齐该对象的用途说明、结算凭证和时间留痕，避免月度复核和后续核查被卡住。'
  }
  return '应急监管重点关注该对象是否属于低余额、长期使用中或回写缺失记录，并判断是否需要纳入区域重点督办。'
})

const readOnlyAlertTitle = computed(() => `${readOnlyRoleLabel.value}：当前仅保留资金台账摘要、详情和导出`)

const readOnlyAlertDescription = computed(() => {
  if (isInsurerRole.value) {
    return `${readOnlyRoleDescription.value} 当前保险机构视角仅支持查看，不开放直接维护。`
  }
  return `${readOnlyRoleDescription.value} 当前页面为只读视角。`
})

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
  syncCurrentFund()
}

function applyPreventionFundWorkbenchQuery(routeQuery = {}) {
  Object.assign(queryParams.value, {
    pageNum: 1,
    enterpriseId: undefined,
    regionCode: undefined,
    statMonth: currentMonth(),
    fundStatus: undefined
  })
  applyWorkbenchRouteQuery(routeQuery, queryParams.value, preventionFundWorkbenchFields)
  activeFocusKey.value = resolvePreventionFundFocusKey(routeQuery.focusKey)
  getList()

watchEffect(() => {
  setPageGuide({
    title: roleTitle.value || '?????????',
    description: roleDescription.value || '?????????????????????????????????',
    portalExplanation: portalExplanationItems.value,
    focus: focusQueues.value,
    selection: [...selectedFundOverview.value, { label: '??????', value: currentFundActionSummary.value }],
    workflow: workflowSteps.value,
    hints: [...currentFundActionTags.value, ...hintTags.value].slice(0, 6)
  })
})

}

function handlePrimaryFundAction() {
  if (!currentFund.value) {
    return
  }
  if (primaryFundAction.value.action === 'edit') {
    if (blockReadOnlyAction('修改资金记录')) {
      return
    }
    openEditDialog(currentFund.value)
    return
  }
  openDetail(currentFund.value)
}

function resetQuery() {
  pageResetQuery()
  applyWorkbenchRouteQuery(route.query, queryParams.value, preventionFundWorkbenchFields)
  getList()
}

function clearWorkbenchContext() {
  activeFocusKey.value = ''
  Object.assign(queryParams.value, {
    pageNum: 1,
    enterpriseId: undefined,
    regionCode: undefined,
    statMonth: currentMonth(),
    fundStatus: undefined,
    focusKey: undefined
  })
  router.replace({
    path: route.path,
    query: stripWorkbenchRouteQuery(route.query, preventionFundWorkbenchFields)
  })
  getList()
}

function blockReadOnlyAction(actionLabel) {
  if (!isReadOnlyRole.value) {
    return false
  }
  proxy.$modal.msgWarning(`${readOnlyRoleLabel.value}仅保留摘要、详情和导出，不能${actionLabel}`)
  return true
}

function activeFocusValue(fund) {
  if (!fund) {
    return '-'
  }
  if (activeFocus.value?.key === 'lowBalance') {
    return isLowBalance(fund.accruedAmount, fund.remainingAmount) ? '低余额' : '正常'
  }
  if (activeFocus.value?.key === 'inUse') {
    return String(fund.fundStatus) === '2' ? '使用中' : '非使用中'
  }
  if (activeFocus.value?.key === 'missingEvidence') {
    return fund.evidenceUrl ? '凭证齐全' : '缺少凭证'
  }
  if (activeFocus.value?.key === 'nonStub') {
    return sourceModeLabel(fund.sourceMode)
  }
  if (activeFocus.value?.key === 'all') {
    return `${formatMoney(fund.remainingAmount)} 可用余额`
  }
  return sourceModeLabel(fund.sourceMode)
}

function enterpriseName(enterpriseId) {
  const matched = enterpriseOptions.value.find(item => String(item.enterpriseId) === String(enterpriseId))
  return matched ? matched.enterpriseName : ''
}

function fundStatusLabel(value) {
  const matched = fundStatusOptions.find(item => String(item.value) === String(value))
  return matched ? matched.label : '-'
}

function resolvePreventionFundFocusKey(value) {
  return Array.isArray(value) ? String(value[0] || '') : String(value || '')
}

activeFocusKey.value = resolvePreventionFundFocusKey(route.query.focusKey)

function preventionFundFocusLabel(value) {
  return ({
    lowBalance: '低余额对象',
    inUse: '使用中资金',
    missingEvidence: '缺少凭证',
    nonStub: '正式回写来源',
    all: '全部对象'
  })[String(value || '')] || (value || '-')
}

function currentMonth() {
  const date = new Date()
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}`
}

function buildFundHintTags(fund, role, focus) {
  if (!fund) {
    const tags = [{ label: '请选择资金池查看治理提示', type: 'info' }]
    if (focus?.title) {
      tags.push({ label: `当前焦点：${focus.title}`, type: 'warning' })
      tags.push({ label: `优先动作：${focus.actionText}`, type: 'info' })
    }
    return tags
  }
  const tags = []

  if (isLowBalance(fund.accruedAmount, fund.remainingAmount)) {
    tags.push({
      label: role === 'site-enterprise' ? '当前余额偏低，建议尽快确认后续投入或核销安排。' : '当前余额偏低，建议优先纳入低余额风险复核。',
      type: 'warning'
    })
  }
  if (String(fund.fundStatus) === '2') {
    tags.push({
      label: role === 'emergency' ? '资金仍在使用中，建议持续跟踪是否按时完成结算回写。' : '资金处于使用中，建议及时补录用途说明和结算时间。',
      type: 'warning'
    })
  }
  if (!fund.usagePurpose) {
    tags.push({ label: '缺少使用用途，建议补齐费用去向说明。', type: 'warning' })
  }
  if (!fund.evidenceUrl) {
    tags.push({
      label: role === 'bank' ? '缺少凭证地址，协同侧可提示业务方补齐结算佐证材料。' : '缺少凭证地址，建议补齐结算凭证或佐证材料链接。',
      type: 'warning'
    })
  }
  if (!fund.lastSettleTime) {
    tags.push({ label: '缺少最近结算时间，建议补录回写时间留痕。', type: 'info' })
  }
  if (sourceModeLabel(fund.sourceMode) === '人工维护') {
    tags.push({
      label: role === 'insurer' ? '当前为人工维护记录，建议复核是否已完成正式回写。' : '当前为人工维护记录，建议复核金额、用途和同步状态。',
      type: 'info'
    })
  }
  if (sourceModeLabel(fund.sourceMode) === 'Stub') {
    tags.push({ label: '当前仍为 Stub 来源，后续可平滑切换为正式机构回写链路。', type: 'info' })
  }
  if (String(fund.fundStatus) === '3' && fund.evidenceUrl) {
    tags.push({ label: '资金已核销且凭证齐全，可继续用于复核留痕或监管核查。', type: 'success' })
  }
  if (!tags.length) {
    tags.push({ label: '当前资金池信息较完整，可继续用于回写复核和后续核查。', type: 'success' })
  }
  return tags
}
</script>

<style scoped lang="scss">
.azb-prevention-fund-workbench {
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

  .azb-focus-grid {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 16px;
    margin-bottom: 16px;
  }

  .azb-card-head--between {
    display: flex;
    justify-content: space-between;
    gap: 16px;
    align-items: flex-start;
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

  @media (max-width: 1200px) {
    .azb-focus-grid {
      grid-template-columns: 1fr;
    }
  }

  @media (max-width: 768px) {
    .azb-card-head--between,
    .azb-source-list {
      grid-template-columns: 1fr;
      display: grid;
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


