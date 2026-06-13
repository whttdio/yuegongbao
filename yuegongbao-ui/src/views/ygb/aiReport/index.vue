<template>
  <div class="app-container ygb-page ai-report-page">
        <section class="gov-page-header ygb-page__header">
      <div>
        <p class="ygb-page__eyebrow">AI 监测报告</p>
        <h1 class="ygb-page__title">{{ roleTitle }}</h1>
        <p class="ygb-page__desc">
          {{ roleDescription }}
          当前继续复用统一 AI 监测报告底座，按粤工保高频角色重排焦点队列、办理建议、详情提示和跳转动作，减少在列表、详情、预览之间反复切换。
        </p>
      </div>
      <div class="ygb-page__tip">
        <div class="ygb-page__tip-item">当前视角：{{ roleBadge }}</div>
        <div class="ygb-page__tip-item">{{ roleTip }}</div>
        <div class="ygb-page__tip-item">当前模型版本：{{ currentConfig.version || '未配置' }}，继续复用统一评分配置中心与门户解释聚合口径，不拆第二套模型接口。</div>
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
      :title="`${readOnlyRoleLabel}：当前页面仅保留摘要、详情、预览和导出`"
      :description="readOnlyRoleDescription || '生成、重生成等办理动作已自动收口。'"
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

    <div class="ai-report-layout">
      <aside class="section-nav">
        <div class="section-nav__title">AI 监测</div>
        <button
          v-for="item in navItems"
          :key="item.key"
          type="button"
          class="section-nav__item"
          :class="{ 'is-active': item.key === activeSection }"
          @click="scrollToSection(item.key)"
        >
          <span class="section-nav__index">{{ item.icon }}</span>
          <span>{{ item.label }}</span>
        </button>
      </aside>

      <main class="report-main">
        <section ref="filterSectionRef" class="report-card report-card--filter">
          <div class="report-card__head">
            <div>
              <h3>监测筛选</h3>
              <p>按周期、行政区划、企业类型和风险等级快速刷新当前 AI 监测视图。</p>
            </div>
            <div class="report-actions">
              <el-button type="primary" icon="Search" @click="handleQuery">查询刷新</el-button>
              <el-button type="danger" plain @click="standardOpen = true">AI 评分标准</el-button>
              <el-button type="success" plain @click="handlePreviewReport">预览报告</el-button>
              <el-button plain @click="goAiTaskPage" v-hasPermi="['ygb:aiReportTask:list']">建议任务</el-button>
              <el-button plain @click="goAiSubscriptionPage" v-hasPermi="['ygb:aiReportSubscription:list']">订阅管理</el-button>
            </div>
          </div>

          <el-form ref="queryRef" :model="queryParams" :inline="true" class="report-filter-form">
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
              <el-select v-model="queryParams.regionCode" style="width: 180px" @change="handleConfigRefresh">
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
              <el-button icon="Refresh" @click="resetQuery">重置</el-button>
              <el-button v-if="canGenerate" type="primary" plain icon="MagicStick" @click="openGenerateDialog()">
                生成报告
              </el-button>
              <el-button v-if="canExport" type="warning" plain icon="Download" @click="handleExport">
                导出列表
              </el-button>
            </el-form-item>
          </el-form>
        </section>

        <section ref="overviewSectionRef" class="report-card">
          <div class="report-section-title">
            <span class="report-section-title__bar" />
            <div>
              <h3>综合评分概览</h3>
              <p>{{ selectedReportTitle }}</p>
            </div>
          </div>

          <div class="overview-hero">
            <div class="overview-hero__main">
              <div class="overview-hero__eyebrow">业务摘要</div>
              <div class="overview-hero__scoreline">
                <div class="overview-hero__score">{{ selectedReport?.totalScore || averageScore }}</div>
                <div class="overview-hero__meta">
                  <div class="overview-hero__risk">
                    <el-tag :type="riskTagType(selectedReport?.riskLevel)">{{ riskLevelLabel(selectedReport?.riskLevel) }}</el-tag>
                    <span>{{ activeReportSummary }}</span>
                  </div>
                  <div class="overview-hero__submeta">
                    <span>当前排名 {{ selectedReport?.rankingNo || '-' }}</span>
                    <span>样本总量 {{ dashboard.totalCount || 0 }}</span>
                    <span>高风险 {{ dashboard.highRiskCount || 0 }}</span>
                  </div>
                </div>
              </div>
            </div>
            <div class="overview-hero__aside">
              <div v-for="item in overviewDigestItems" :key="item.label" class="overview-digest">
                <span class="overview-digest__label">{{ item.label }}</span>
                <strong class="overview-digest__value">{{ item.value }}</strong>
                <span class="overview-digest__hint">{{ item.hint }}</span>
              </div>
            </div>
          </div>

          <div class="score-grid">
            <article v-for="item in scoreCards" :key="item.label" class="score-card">
              <div class="score-card__value">{{ item.value }}</div>
              <div class="score-card__label">{{ item.label }}</div>
              <div class="score-card__trend">{{ item.trend }}</div>
            </article>
          </div>

          <div class="overview-tags">
            <span v-for="item in overviewTags" :key="item" class="overview-tags__item">{{ item }}</span>
          </div>
        </section>

        <section ref="conclusionSectionRef" class="report-card ai-conclusion-card">
          <div class="report-section-title">
            <span class="report-section-title__bar" />
            <div>
              <h3>AI 监测结论</h3>
              <p>系统基于当前评分维度和近期监测样本自动生成业务结论摘要。</p>
            </div>
          </div>

          <div class="ai-conclusion-body">
            <p><strong>综合判断：</strong>{{ conclusionSummary }}</p>
            <p><strong>主要优势：</strong>{{ conclusionStrength }}</p>
            <p><strong>突出问题：</strong>{{ conclusionWeakness }}</p>
            <p><strong>建议动作：</strong>{{ conclusionAdvice }}</p>
          </div>
        </section>

        <section ref="chartSectionRef" class="report-card">
          <div class="report-section-title">
            <span class="report-section-title__bar" />
            <div>
              <h3>趋势与维度得分</h3>
              <p>结合维度评分和历史趋势，识别薄弱环节与波动方向。</p>
            </div>
          </div>

          <div class="chart-section">
            <div class="chart-card">
              <div class="chart-card__title">各维度风险得分对比</div>
              <div ref="dimensionChartRef" class="chart-box" />
            </div>
            <div class="chart-card">
              <div class="chart-card__title">近 6 次监测得分趋势</div>
              <div ref="trendChartRef" class="chart-box" />
            </div>
          </div>
        </section>

        <section ref="rankingSectionRef" class="report-card">
          <div class="report-section-title">
            <span class="report-section-title__bar" />
            <div>
              <h3>企业风险排名</h3>
              <p>同步展示前位样本和尾部样本，便于横向对照和重点研判。</p>
            </div>
          </div>

          <div class="ranking-summary-strip">
            <div v-for="item in rankingSummaryItems" :key="item.label" class="ranking-summary-strip__item">
              <span class="ranking-summary-strip__label">{{ item.label }}</span>
              <strong class="ranking-summary-strip__value">{{ item.value }}</strong>
              <span class="ranking-summary-strip__hint">{{ item.hint }}</span>
            </div>
          </div>

          <div class="ranking-grid">
            <el-card shadow="never" class="ranking-panel">
              <template #header>
                <div class="ranking-panel__head">
                  <div class="ranking-panel__title">前 5 样本</div>
                  <div class="ranking-panel__desc">作为正向对标和样本口径校准参考。</div>
                </div>
              </template>
              <el-table :data="topRankingList" size="small" empty-text="暂无样本">
                <el-table-column label="序位" width="74">
                  <template #default="scope">
                    <span class="ranking-no ranking-no--good">#{{ scope.row.rankingNo || scope.$index + 1 }}</span>
                  </template>
                </el-table-column>
                <el-table-column label="对象" min-width="180">
                  <template #default="scope">
                    {{ reportDisplayName(scope.row) }}
                  </template>
                </el-table-column>
                <el-table-column label="区域" prop="regionName" min-width="120" />
                <el-table-column label="得分" prop="totalScore" width="86" />
                <el-table-column label="亮点" min-width="180" show-overflow-tooltip>
                  <template #default="scope">
                    {{ buildRankingHighlight(scope.row, 'positive') }}
                  </template>
                </el-table-column>
              </el-table>
            </el-card>

            <el-card shadow="never" class="ranking-panel">
              <template #header>
                <div class="ranking-panel__head">
                  <div class="ranking-panel__title">后 5 样本</div>
                  <div class="ranking-panel__desc">用于锁定低分尾部样本和重点复核对象。</div>
                </div>
              </template>
              <el-table :data="bottomRankingList" size="small" empty-text="暂无尾部样本">
                <el-table-column label="序位" width="74">
                  <template #default="scope">
                    <span class="ranking-no ranking-no--risk">#{{ scope.row.rankingNo || scope.$index + 1 }}</span>
                  </template>
                </el-table-column>
                <el-table-column label="对象" min-width="180">
                  <template #default="scope">
                    {{ reportDisplayName(scope.row) }}
                  </template>
                </el-table-column>
                <el-table-column label="区域" prop="regionName" min-width="120" />
                <el-table-column label="得分" prop="totalScore" width="86" />
                <el-table-column label="风险因子" min-width="180" show-overflow-tooltip>
                  <template #default="scope">
                    {{ buildRankingHighlight(scope.row, 'risk') }}
                  </template>
                </el-table-column>
              </el-table>
            </el-card>
          </div>
        </section>

        <section ref="riskSectionRef" class="report-card">
          <div class="report-section-title">
            <span class="report-section-title__bar" />
            <div>
              <h3>高风险对象与整改建议</h3>
              <p>列出当前高风险对象、主要问题和整改建议，便于重点处置和闭环跟踪。</p>
            </div>
          </div>

          <div class="risk-focus-grid">
            <article v-for="item in riskFocusCards" :key="item.title" class="risk-focus-card">
              <div class="risk-focus-card__head">
                <span class="risk-focus-card__badge">重点对象</span>
                <span class="risk-focus-card__score">{{ item.score }}</span>
              </div>
              <h4>{{ item.title }}</h4>
              <p>{{ item.reason }}</p>
              <div class="risk-focus-card__foot">{{ item.advice }}</div>
            </article>
          </div>

          <el-table :data="highRiskList" size="small" empty-text="暂无高风险对象">
            <el-table-column label="序位" width="74">
              <template #default="scope">
                <span class="ranking-no ranking-no--risk">#{{ scope.row.rankingNo || scope.$index + 1 }}</span>
              </template>
            </el-table-column>
            <el-table-column label="对象" min-width="180">
              <template #default="scope">
                {{ reportDisplayName(scope.row) }}
              </template>
            </el-table-column>
            <el-table-column label="风险等级" width="100">
              <template #default="scope">
                <el-tag :type="riskTagType(scope.row.riskLevel)">{{ riskLevelLabel(scope.row.riskLevel) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="得分" prop="totalScore" width="86" />
            <el-table-column label="主要风险点" min-width="180" show-overflow-tooltip>
              <template #default="scope">
                {{ buildHighRiskReason(scope.row) }}
              </template>
            </el-table-column>
            <el-table-column label="建议措施" min-width="180" show-overflow-tooltip>
              <template #default="scope">
                {{ buildHighRiskAdvice(scope.row) }}
              </template>
            </el-table-column>
          </el-table>
        </section>

        <section ref="reportTableSectionRef" class="report-card">
          <div class="report-card__head report-card__head--table">
            <div>
              <h3>报告样本</h3>
              <p>当前筛选条件下已生成的 AI 报告台账，支持行内切换活动报告。</p>
            </div>
            <div class="report-card__summary">{{ currentConfigSummary }}</div>
          </div>

          <el-table
            v-loading="loading"
            :data="visibleReportList"
            row-key="reportId"
            :row-class-name="reportRowClassName"
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

            <el-table-column label="操作" fixed="right" :width="tableActionWidth" align="center">
              <template #default="scope">
                <el-button link type="info" icon="View" @click.stop="openDetail(scope.row)">详情</el-button>
                <el-button link type="primary" icon="Document" @click.stop="selectAndPreview(scope.row)">预览</el-button>
                <el-button v-if="canGenerate" link type="primary" icon="RefreshRight" @click.stop="openGenerateDialog(scope.row)">重生成</el-button>
              </template>
            </el-table-column>
          </el-table>

          <pagination
            v-show="total > 0"
            v-model:page="queryParams.pageNum"
            v-model:limit="queryParams.pageSize"
            :total="total"
            @pagination="getList"
          />
        </section>
      </main>
    </div>

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
            <el-checkbox v-for="item in dimensionOptions" :key="item.value" :value="item.value">{{ item.label }}</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="generateOpen = false">取消</el-button>
        <el-button type="primary" @click="submitGenerate">确认生成</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="standardOpen" title="AI 监测评分标准" width="760px">
      <el-alert :title="currentConfigSummary" type="info" :closable="false" show-icon style="margin-bottom: 16px;" />
      <el-table :data="standardRows" border>
        <el-table-column label="维度" prop="label" min-width="180" />
        <el-table-column label="权重" prop="weight" width="90" />
        <el-table-column label="目标值" prop="target" width="120" />
        <el-table-column label="说明" prop="remark" min-width="240" />
      </el-table>
    </el-dialog>

    <page-detail-dialog v-model="detailOpen" title="报告详情" width="860px">
      <template v-if="detailDisplayReport">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="报告ID">{{ detailDisplayReport.reportId }}</el-descriptions-item>
          <el-descriptions-item label="模型版本">{{ detailDisplayReport.configVersion || currentConfig.version || '未配置' }}</el-descriptions-item>
          <el-descriptions-item label="区域">{{ detailDisplayReport.regionName }}</el-descriptions-item>
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
          <el-descriptions-item label="预留 PDF 地址" :span="2">{{ detailDisplayReport.reportPdfUrl || '-' }}</el-descriptions-item>
        </el-descriptions>

        <div class="ygb-detail-block">
          <h3>当前视角重点</h3>
          <p class="ygb-detail-focus">{{ detailFocusText }}</p>
        </div>

        <div class="ygb-detail-block">
          <h3>复核提示</h3>
          <div class="ygb-tag-list">
            <el-tag v-for="item in detailHintTags" :key="item.label" :type="item.type" effect="plain">{{ item.label }}</el-tag>
          </div>
        </div>

        <div class="ygb-detail-block">
          <h3>维度明细</h3>
          <el-table :data="detailDimensionItems" size="small" empty-text="暂无维度明细">
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

        <div class="ygb-detail-block" v-if="detailDisplayReport.reportHtml">
          <h3>原始片段</h3>
          <div class="detail-html" v-html="detailDisplayReport.reportHtml" />
        </div>
      </template>
    </page-detail-dialog>
  </div>
</template>

<script setup name="YgbAiReport">
import { computed, getCurrentInstance, nextTick, onBeforeUnmount, onMounted, ref, watch, watchEffect } from 'vue'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'
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
  buildReportSummary,
  currentMonthRange,
  dimensionOptions,
  enterpriseTypeLabel,
  enterpriseTypeOptions,
  formatDate,
  formatDecimal,
  matchReportFocus,
  parseJson,
  prioritizeFocusRows,
  regionNameMap,
  regionOptions as allRegionOptions,
  reportDisplayName,
  reportTypeLabel,
  reportTypeOptions,
  riskLevelLabel,
  riskLevelOptions,
  riskTagType,
  summaryCard,
  useAiReportPage
} from '@/views/aiReport/useAiReportPage'

const { proxy } = getCurrentInstance()
const regionOptions = useAuthorizedRegionOptions(allRegionOptions)
const defaultRegionCode = authorizedDefaultRegionCode('440000')

const route = useRoute()
const router = useRouter()
const { setPageGuide } = useWorkbenchAssist()
const userStore = useUserStore()
const { isReadOnlyRole, isInsurerRole, isBankRole, readOnlyRoleLabel, readOnlyRoleDescription } = useRoleViewMode()
const activeFocusKey = ref('')
const aiReportWorkbenchFields = ['regionCode']

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

function focusQueue(key, title, count, unit, desc, actionText) {
  return { key, title, count, unit, desc, actionText }
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

const navItems = [
  { key: 'filter', label: '监测筛选', icon: '01' },
  { key: 'overview', label: '综合概览', icon: '02' },
  { key: 'conclusion', label: 'AI 结论', icon: '03' },
  { key: 'charts', label: '趋势图表', icon: '04' },
  { key: 'ranking', label: '排名对照', icon: '05' },
  { key: 'risk', label: '高风险对象', icon: '06' },
  { key: 'reports', label: '报告样本', icon: '07' }
]

const {
  loading,
  showSearch,
  total,
  reportList,
  reportDetail,
  reportItems,
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
  selectedDimensionItems,
  detailDimensionItems,
  detailDisplayReport,
  defaultSelectedReport,
  getList,
  loadDashboard,
  loadCurrentConfig,
  handleQuery,
  openGenerateDialog,
  submitGenerate,
  openDetail,
  handleExport
} = useAiReportPage({
  exportFilePrefix: 'ai_report',
  canMutate: () => !isReadOnlyRole.value,
  onBlockedAction(actionLabel) {
    const roleLabel = readOnlyRoleLabel.value || '只读角色'
    proxy?.$modal?.msgWarning?.(`${roleLabel}不支持“${actionLabel}”操作`)
  },
  afterLoad() {
    syncActiveFocus()
  }
})

const standardOpen = ref(false)
const activeSection = ref('filter')
const filterSectionRef = ref(null)
const overviewSectionRef = ref(null)
const conclusionSectionRef = ref(null)
const chartSectionRef = ref(null)
const rankingSectionRef = ref(null)
const riskSectionRef = ref(null)
const reportTableSectionRef = ref(null)
const dimensionChartRef = ref(null)
const trendChartRef = ref(null)

let dimensionChart = null
let trendChart = null

const permissions = computed(() => userStore.permissions || [])
const portalExplanations = computed(() => dashboard.ygbExplanation || [])
const portalExplanationItems = computed(() => decoratePortalExplanationItems(portalExplanations.value, {
  portalCode: 'ygb',
  panelTitle: '530.1 业务影响解释',
  panelDescription: 'AI 报告的业务影响、合规风险和闭环建议统一来自门户解释聚合接口。'
}))
const workbenchContext = computed(() => buildWorkbenchContext(route.query, {
  fields: aiReportWorkbenchFields,
  sourceLabel: '企业办理工作台',
  title: '当前 AI 报告沿用了工作台来源条件',
  description: '当前页面保留了首页或上游工作台带入的区域来源，便于继续核对业务影响、合规风险和闭环建议。',
  fieldLabels: {
    regionCode: '行政区划'
  },
  fieldFormatters: {
    regionCode: value => regionNameMap[value] || value
  }
}))
const roleView = computed(() => {
  if (isReadOnlyRole.value) {
    return 'readonly'
  }
  const roles = userStore.roles || []
  const currentPermissions = permissions.value || []
  if (roles.includes('ygb_hrss_supervisor')) {
    return 'hrss'
  }
  if (roles.includes('ygb_enterprise_operator')) {
    return 'operator'
  }
  if (isFinanceView(roles, currentPermissions)) {
    return 'finance'
  }
  if (roles.includes('ygb_enterprise_admin')) {
    return 'admin'
  }
  return 'default'
})
const canGenerate = computed(() => !isReadOnlyRole.value && hasPermission(permissions.value, ['ygb:aiReport:generate']))
const canExport = computed(() => hasPermission(permissions.value, ['ygb:aiReport:export']))
const selectedReport = computed(() => defaultSelectedReport.value)
const averageScore = computed(() => formatDecimal(dashboard.averageScore))
const scoreCards = computed(() => dashboard.scoreCards || [])
const topRankingList = computed(() => dashboard.topRankingList || [])
const bottomRankingList = computed(() => dashboard.bottomRankingList || [])
const highRiskList = computed(() => dashboard.highRiskList || [])
const trendPoints = computed(() => dashboard.trendPoints || [])

function handlePortalExplanationAction(action) {
  openPortalExplanationAction(router, action)
}

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

function countRows(predicate) {
  return reportList.value.filter(predicate).length
}

function matchWorkbenchFocus(row, focusKey) {
  if (!row || !focusKey) {
    return false
  }
  if (focusKey === 'all') {
    return true
  }
  if (focusKey === 'highRisk' || focusKey === 'lowScore') {
    return matchReportFocus(row, focusKey, dashboard.averageScore)
  }
  if (focusKey === 'bottom') {
    return bottomRankingList.value.some(item => item.reportId === row.reportId)
  }
  if (focusKey === 'preview') {
    return !String(row.reportPdfUrl || '').trim()
  }
  return false
}

function syncActiveFocus() {
  if (!activeFocusKey.value || !focusQueues.value.some(item => item.key === activeFocusKey.value)) {
    activeFocusKey.value = focusQueues.value[0]?.key || ''
  }
}

const roleBadge = computed(() => {
  if (isInsurerRole.value) return '保险协同视角'
  if (isBankRole.value) return '银行协同视角'
  if (roleView.value === 'hrss') return '人社经办视角'
  if (roleView.value === 'operator') return '企业经办视角'
  if (roleView.value === 'finance') return '财务风控视角'
  if (roleView.value === 'admin') return '企业管理员视角'
  return '综合办理视角'
})

const roleTitle = computed(() => {
  if (isInsurerRole.value) return 'AI 监测报告协同看板'
  if (isBankRole.value) return 'AI 监测报告联动看板'
  if (roleView.value === 'hrss') return 'AI 监测报告经办复核台'
  if (roleView.value === 'operator') return 'AI 监测报告办理工作台'
  if (roleView.value === 'finance') return 'AI 低分与风险复核台'
  if (roleView.value === 'admin') return 'AI 监测报告统筹驾驶台'
  return 'AI 监测报告分析台'
})

const roleDescription = computed(() => {
  if (isInsurerRole.value) {
    return '保险协同角色只保留摘要、详情、预览和导出入口，重点查看高风险对象、评分结论和模型口径，不参与生成和重生成动作。'
  }
  if (isBankRole.value) {
    return '银行协同角色聚焦工资支付稳定性、信用风险和区域差异，页面只保留查看与导出能力，便于跨部门联动复核。'
  }
  if (roleView.value === 'hrss') {
    return '人社经办更关注高风险样本、尾部区域和闭环进度，因此页面优先承接风险样本、区域排名和后续复核跳转。'
  }
  if (roleView.value === 'operator') {
    return '企业经办更高频地处理报告生成、预览核对和整改复盘，因此页面把待预览样本、重生成和详情复核排在最前。'
  }
  if (roleView.value === 'finance') {
    return '财务风控更关注低分样本、工资支付和税务关联异常，因此页面优先呈现低分对象、高风险对象和尾部样本。'
  }
  if (roleView.value === 'admin') {
    return '企业管理员需要统筹样本生成、风险承接和跨模块联动，因此页面保持全局概览，同时把重点动作集中到工作台顶部。'
  }
  return '页面以统一 AI 报告数据为底座，按当前角色动态调整关注顺序、办理建议和联动入口。'
})

const roleTip = computed(() => {
  if (isReadOnlyRole.value) {
    return '当前角色仅保留摘要、详情、预览和导出能力，生成、重生成等办理动作已自动收口。'
  }
  if (roleView.value === 'hrss') {
    return '优先承接高风险样本和尾部区域，必要时直接联动预警中心与统计报表。'
  }
  if (roleView.value === 'operator') {
    return '优先完成待预览报告核对，再回看高风险对象，减少生成后遗漏复核。'
  }
  if (roleView.value === 'finance') {
    return '优先处理低分样本和工资、税务关联风险，适合快速定位财务类异常对象。'
  }
  if (roleView.value === 'admin') {
    return '建议先看高风险对象，再看低分和待预览样本，统一安排后续办理动作。'
  }
  return '当前页面支持按焦点队列快速锁定高风险、低分和待预览样本，并联动详情、预览和导出。'
})

const roleFocusTip = computed(() => {
  if (roleView.value === 'hrss') return '把经办侧最先该看的风险样本和尾部区域固定成一组连续办理队列。'
  if (roleView.value === 'operator') return '先处理待预览和待复核样本，减少生成后遗漏 PDF 与摘要核对。'
  if (roleView.value === 'finance') return '先看低分和高风险对象，再回看尾部区域，适合财务异常排查。'
  if (roleView.value === 'admin') return '先抓风险，再统筹低分与待预览对象，便于管理员快速分派后续动作。'
  return '把当前角色最常处理的报告焦点固定出来，避免在全量样本里反复筛找。'
})

const summaryCards = computed(() => {
  if (roleView.value === 'finance') {
    return [
      summaryCard('lowScore', '低分样本', countRows(row => matchWorkbenchFocus(row, 'lowScore')), '份', '低于当前平均分的对象，适合优先排查财务与税务类异常。', 'ygb-summary-card--primary'),
      summaryCard('highRisk', '高风险对象', dashboard.highRiskCount || 0, '份', '已被识别为高风险对象，需要联动风控与后续办理动作。', 'ygb-summary-card--warning'),
      summaryCard('bottom', '尾部样本', bottomRankingList.value.length, '份', '区域或样本排名靠后对象，适合做重点复核。', 'ygb-summary-card--success'),
      summaryCard('configVersion', '模型版本', currentConfig.value.version || 'DEFAULT-STUB', '', '继续使用统一评分配置中心的当前口径。', 'ygb-summary-card--neutral')
    ]
  }
  if (roleView.value === 'hrss') {
    return [
      summaryCard('sampleCount', '报告样本', dashboard.totalCount || total.value, '份', '当前筛选范围内已归集的 AI 监测报告总量。', 'ygb-summary-card--primary'),
      summaryCard('highRisk', '高风险对象', dashboard.highRiskCount || 0, '份', '优先纳入持续复核与预警闭环的重点对象。', 'ygb-summary-card--warning'),
      summaryCard('bottom', '尾部区域', bottomRankingList.value.length, '份', '得分靠后区域或对象，适合继续穿透核查。', 'ygb-summary-card--success'),
      summaryCard('configVersion', '模型版本', currentConfig.value.version || 'DEFAULT-STUB', '', '当前业务解释口径与统一模型配置保持一致。', 'ygb-summary-card--neutral')
    ]
  }
  if (roleView.value === 'operator') {
    return [
      summaryCard('sampleCount', '报告样本', dashboard.totalCount || total.value, '份', '当前经办口径下可直接处理的报告样本总量。', 'ygb-summary-card--primary'),
      summaryCard('preview', '待预览样本', countRows(row => matchWorkbenchFocus(row, 'preview')), '份', '尚未形成 PDF 占位预览的报告，建议优先核对。', 'ygb-summary-card--warning'),
      summaryCard('highRisk', '高风险对象', dashboard.highRiskCount || 0, '份', '需要尽快复核摘要、维度得分与整改建议。', 'ygb-summary-card--success'),
      summaryCard('configVersion', '模型版本', currentConfig.value.version || 'DEFAULT-STUB', '', '模型版本变化会直接影响生成与重生成口径。', 'ygb-summary-card--neutral')
    ]
  }
  return [
    summaryCard('sampleCount', '报告样本', dashboard.totalCount || total.value, '份', '当前筛选范围的监测样本总量。', 'ygb-summary-card--primary'),
    summaryCard('avgScore', '平均得分', averageScore.value, '分', '用于识别低于均值的重点复核对象。', 'ygb-summary-card--success'),
    summaryCard('highRisk', '高风险对象', dashboard.highRiskCount || 0, '份', '建议优先进入详情、预览和后续办理链路。', 'ygb-summary-card--warning'),
    summaryCard('configVersion', '模型版本', currentConfig.value.version || 'DEFAULT-STUB', '', '评分权重与目标值继续由统一模型配置中心提供。', 'ygb-summary-card--neutral')
  ]
})

const focusQueues = computed(() => {
  if (roleView.value === 'finance') {
    return [
      focusQueue('lowScore', '低分样本', countRows(row => matchWorkbenchFocus(row, 'lowScore')), '份', '先看低于平均分的对象，快速定位工资、社保、税务关联异常。', '优先复核'),
      focusQueue('highRisk', '高风险对象', dashboard.highRiskCount || 0, '份', '承接系统已识别的高风险对象，便于联动信用评分与后续办理动作。', '承接风险'),
      focusQueue('bottom', '尾部样本', bottomRankingList.value.length, '份', '回看排名靠后对象，补齐财务风控解释与复核台账。', '回看尾部'),
      focusQueue('all', '全部样本', dashboard.totalCount || total.value, '份', '浏览当前筛选范围下的全部报告样本。', '查看全量')
    ]
  }
  if (roleView.value === 'hrss') {
    return [
      focusQueue('highRisk', '高风险对象', dashboard.highRiskCount || 0, '份', '先承接高风险样本，直接衔接异常复核与预警闭环。', '优先处置'),
      focusQueue('bottom', '尾部区域', bottomRankingList.value.length, '份', '聚焦排名靠后的区域或对象，适合继续穿透复核。', '复核尾部'),
      focusQueue('preview', '待预览样本', countRows(row => matchWorkbenchFocus(row, 'preview')), '份', '尚未形成 PDF 占位预览的样本，适合先核对内容完整性。', '先看预览'),
      focusQueue('all', '全部样本', dashboard.totalCount || total.value, '份', '查看当前经办口径下的全部样本。', '查看全量')
    ]
  }
  if (roleView.value === 'operator') {
    return [
      focusQueue('preview', '待预览样本', countRows(row => matchWorkbenchFocus(row, 'preview')), '份', '先核对尚未形成 PDF 占位预览的报告，减少交付遗漏。', '优先预览'),
      focusQueue('highRisk', '高风险对象', dashboard.highRiskCount || 0, '份', '回看高风险对象的摘要、维度得分和整改建议。', '复核风险'),
      focusQueue('all', '全部样本', dashboard.totalCount || total.value, '份', '浏览全部可处理样本，按需要进入详情或重生成。', '查看全量')
    ]
  }
  return [
    focusQueue('highRisk', '高风险对象', dashboard.highRiskCount || 0, '份', '优先查看高风险对象，决定是否需要继续办理或联动预警。', '先看风险'),
    focusQueue('lowScore', '低分样本', countRows(row => matchWorkbenchFocus(row, 'lowScore')), '份', '回看低于平均分的对象，找出薄弱维度和整改方向。', '回看低分'),
    focusQueue('preview', '待预览样本', countRows(row => matchWorkbenchFocus(row, 'preview')), '份', '查看尚未形成 PDF 占位预览的样本，补齐预览与交付核对。', '核对预览'),
    focusQueue('all', '全部样本', dashboard.totalCount || total.value, '份', '浏览全部 AI 监测样本。', '查看全量')
  ]
})

const activeFocus = computed(() => focusQueues.value.find(item => item.key === activeFocusKey.value) || focusQueues.value[0] || null)
const visibleReportList = computed(() => {
  if (!activeFocus.value) {
    return reportList.value
  }
  return prioritizeFocusRows(reportList.value, row => matchWorkbenchFocus(row, activeFocus.value.key))
})

const selectedReportOverview = computed(() => {
  const current = selectedReport.value
  if (!current) {
    return [
      { label: '当前焦点', value: activeFocus.value?.title || '未选择' },
      { label: '焦点数量', value: activeFocus.value ? `${activeFocus.value.count}${activeFocus.value.unit}` : '-' },
      { label: '筛选口径', value: `${regionNameMap[queryParams.regionCode] || queryParams.regionCode || '-'} / ${reportTypeLabel(queryParams.reportType)}` },
      { label: '模型版本', value: currentConfig.value.version || 'DEFAULT-STUB' }
    ]
  }
  return [
    { label: '报告对象', value: reportDisplayName(current) },
    { label: '统计区间', value: `${formatDate(current.periodStart) || '-'} 至 ${formatDate(current.periodEnd) || '-'}` },
    { label: '综合得分', value: `${current.totalScore ?? '-'} / ${riskLevelLabel(current.riskLevel)}` },
    { label: '当前焦点', value: activeFocus.value?.title || '全部样本' },
    { label: '模型版本', value: current.configVersion || currentConfig.value.version || 'DEFAULT-STUB' }
  ]
})

const workflowSteps = computed(() => {
  const aggregateSteps = buildAggregateWorkflowSteps(portalExplanationItems.value)
  if (aggregateSteps.length) {
    return aggregateSteps
  }
  if (roleView.value === 'finance') {
    return [
      { label: '锁定低分或高风险对象', desc: '先从低分样本、高风险对象和尾部样本切入，快速缩小财务复核范围。' },
      { label: '查看维度原因与摘要', desc: '结合工资、社保、税务等维度得分判断异常来源。' },
      { label: '联动信用或税务模块', desc: '按焦点结果跳转信用评分或税务对比模块继续处置。' }
    ]
  }
  if (roleView.value === 'hrss') {
    return [
      { label: '优先承接高风险对象', desc: '先看高风险对象和尾部区域，把重点样本纳入经办复核视线。' },
      { label: '复核模型摘要与维度', desc: '通过详情查看原因、建议和关键维度，确认是否需要进一步异常处置。' },
      { label: '联动预警与统计', desc: '按当前焦点进入预警中心或统计报表，形成持续跟踪闭环。' }
    ]
  }
  if (roleView.value === 'operator') {
    return [
      { label: '先核对待预览样本', desc: '优先补齐预览和报告核对，避免生成后直接遗漏交付。' },
      { label: '重生成或查看详情', desc: '针对问题样本直接重生成，或进入详情复核维度得分与建议。' },
      { label: '回看高风险对象', desc: '把高风险对象纳入后续整改与复盘台账。' }
    ]
  }
  return [
    { label: '锁定焦点样本', desc: '从高风险、低分或待预览队列中优先选定处理对象。' },
    { label: '生成、预览或查看详情', desc: '根据样本状态直接进入生成、预览或详情复核。' },
    { label: '联动后续办理模块', desc: '把当前报告衔接到预警、信用或配置模块，减少跨页搜索。' }
  ]
})

const activeReportSummary = computed(() => buildReportSummary(selectedReport.value))
const conclusionSummary = computed(() => dashboard.conclusion?.summary || '当前暂无报告数据。')
const conclusionStrength = computed(() => dashboard.conclusion?.strength || '暂无优势维度识别结果。')
const conclusionWeakness = computed(() => dashboard.conclusion?.weakness || '暂无薄弱维度识别结果。')
const conclusionAdvice = computed(() => dashboard.conclusion?.advice || '建议先生成报告样本，再进行对比分析。')

const currentConfigSummary = computed(() => {
  const weights = parseJson(currentConfig.value.dimensionWeights)
  const targets = parseJson(currentConfig.value.targetValues)
  const weightText = ['A', 'B', 'C', 'D', 'E'].map(code => `${code}:${weights[code] || 0}`).join(' / ')
  return `当前模型 ${currentConfig.value.version || 'DEFAULT-STUB'}，权重 ${weightText}，目标值 合同${targets.contractRate || '-'}% / 考勤${targets.attendanceRate || '-'}% / 工资${targets.paySuccessRate || '-'}% / 在线${targets.onlineRate || '-'}% / 工伤${targets.injuryRate || '-'}‰ / 闭环${targets.warningCloseRate || '-'}%。`
})

const selectedReportTitle = computed(() => {
  if (!selectedReport.value) {
    return '当前暂无可分析报告，请先生成或筛选报告样本。'
  }
  return `${selectedReport.value.regionName || '-'} · ${reportTypeLabel(selectedReport.value.reportType)} · ${formatDate(selectedReport.value.periodStart)} 至 ${formatDate(selectedReport.value.periodEnd)}`
})

const overviewDigestItems = computed(() => {
  const best = topRankingList.value[0]
  const weakest = highRiskList.value[0] || bottomRankingList.value[0]
  const scoreGap = best && weakest ? formatDecimal(Number(best.totalScore || 0) - Number(weakest.totalScore || 0)) : '-'
  return [
    { label: '平均得分', value: averageScore.value, hint: '当前筛选范围综合均值' },
    { label: '排名跨度', value: scoreGap, hint: '优良样本与高风险样本分差' },
    { label: '最新模型', value: currentConfig.value.version || 'DEFAULT-STUB', hint: '评分权重口径同步生效' }
  ]
})

const overviewTags = computed(() => {
  const tags = [
    `${reportTypeLabel(queryParams.reportType)}监测`,
    `${regionNameMap[queryParams.regionCode] || queryParams.regionCode || '广东省'}经办视角`,
    `高风险 ${dashboard.highRiskCount || 0} 项`
  ]
  if (queryParams.enterpriseType) {
    tags.push(enterpriseTypeLabel(queryParams.enterpriseType))
  }
  if (queryParams.riskLevel) {
    tags.push(`${riskLevelLabel(queryParams.riskLevel)}筛选`)
  }
  return tags
})

const rankingSummaryItems = computed(() => {
  const best = topRankingList.value[0]
  const weakest = bottomRankingList.value[0]
  return [
    {
      label: '最佳样本',
      value: best ? reportDisplayName(best) : '-',
      hint: best ? `得分 ${best.totalScore}` : '暂无数据'
    },
    {
      label: '重点风险样本',
      value: weakest ? reportDisplayName(weakest) : '-',
      hint: weakest ? `得分 ${weakest.totalScore}` : '暂无数据'
    },
    {
      label: '重点复核对象',
      value: highRiskList.value.length,
      hint: '已纳入高风险处置清单'
    }
  ]
})

const riskFocusCards = computed(() => {
  return highRiskList.value.slice(0, 3).map((row, index) => ({
    title: `${index + 1}. ${reportDisplayName(row)}`,
    score: `${row.totalScore ?? '-'}`,
    reason: buildHighRiskReason(row),
    advice: buildHighRiskAdvice(row)
  }))
})

const currentActionSummary = computed(() => {
  return resolvePortalExplanationSummary(
    portalExplanationItems.value,
    selectedReport.value
      ? '当前 AI 报告的业务影响、合规风险和闭环建议已按 530.1 办理链口径展示。'
      : '当前暂无 AI 报告，可继续通过 530.1 业务影响解释查看主下钻方向。'
  )
})

const portalExplanationSummary = computed(() => resolvePortalExplanationSummary(portalExplanationItems.value, ''))

const currentActionTags = computed(() => {
  const tags = [
    ...buildAggregateHintTags(portalExplanationItems.value, portalExplanationSummary.value),
    ...buildBaseAiReportHintTags(selectedReport.value, {
      items: selectedDimensionItems.value,
      averageScore: dashboard.averageScore,
      highRiskCount: dashboard.highRiskCount,
      currentConfigVersion: currentConfig.value.version
    })
  ]
  if (activeFocus.value?.title) {
    tags.unshift({ label: `当前焦点：${activeFocus.value.title}`, type: 'info' })
  }
  if (roleView.value === 'finance') {
    tags.push({ label: '财务视角优先关注低分样本与工资、税务关联风险。', type: 'info' })
  }
  if (roleView.value === 'hrss') {
    tags.push({ label: '人社经办视角优先承接高风险对象和尾部区域。', type: 'info' })
  }
  if (roleView.value === 'operator') {
    tags.push({ label: '企业经办视角优先完成预览核对，再回看高风险样本。', type: 'info' })
  }
  return uniqueTags(tags).slice(0, 5)
})

const pageHintTags = computed(() => {
  const tags = [...currentActionTags.value]
  if (dashboard.highRiskCount > 0) {
    tags.push({ label: `当前筛选范围内仍有 ${dashboard.highRiskCount} 个高风险对象待继续跟进。`, type: 'warning' })
  }
  const previewCount = countRows(row => matchWorkbenchFocus(row, 'preview'))
  if (previewCount > 0) {
    tags.push({ label: `当前仍有 ${previewCount} 份报告未形成 PDF 占位预览，建议优先核对。`, type: 'info' })
  }
  if (roleView.value === 'finance' && countRows(row => matchWorkbenchFocus(row, 'lowScore')) > 0) {
    tags.push({ label: '低分样本仍需继续排查工资、社保和税务维度的异常来源。', type: 'primary' })
  }
  return uniqueTags(tags).slice(0, 5)
})

watchEffect(() => {
  setPageGuide({
    title: roleTitle.value || 'AI 监测报告工作台',
    description: roleDescription.value || '统一查看当前页的门户解释、焦点对象、当前选中、办理路径与办理提示。',
    portalExplanation: portalExplanationItems.value,
    focus: focusQueues.value,
    selection: [
      ...selectedReportOverview.value,
      { label: '当前办理建议', value: currentActionSummary.value }
    ],
    workflow: workflowSteps.value,
    hints: [...currentActionTags.value, ...pageHintTags.value].slice(0, 6)
  })
})

const detailFocusText = computed(() => {
  if (isReadOnlyRole.value) {
    return '当前为只读协同视角，重点确认摘要、维度明细、模型版本和预览信息是否足以支撑跨部门协同。'
  }
  if (roleView.value === 'finance') {
    return '请重点确认工资、社保、税务相关维度的得分、异常原因和整改建议，判断是否需要继续联动财务风控模块。'
  }
  if (roleView.value === 'hrss') {
    return '请重点查看高风险原因、区域排名和闭环建议，判断是否需要纳入持续复核或预警闭环。'
  }
  if (roleView.value === 'operator') {
    return '请重点核对摘要、维度明细和预览内容，确认是否需要重生成报告或补充整改说明。'
  }
  return '请重点确认当前样本的风险等级、维度短板和模型版本，避免跨模块联动时解释口径不一致。'
})

const detailHintTags = computed(() => uniqueTags([
  ...buildAggregateHintTags(portalExplanationItems.value, portalExplanationSummary.value),
  { label: `当前视角重点：${detailFocusText.value}`, type: 'info' },
  ...buildBaseAiReportHintTags(detailDisplayReport.value, {
    items: detailDimensionItems.value,
    detailMode: true,
    averageScore: dashboard.averageScore,
    currentConfigVersion: currentConfig.value.version
  })
]).slice(0, 5))

const primaryAction = computed(() => {
  if (isReadOnlyRole.value) {
    return { label: '查看详情', action: 'detail' }
  }
  if (!selectedReport.value) {
    return canGenerate.value ? { label: '生成报告', action: 'generate' } : { label: '查看摘要', action: 'detail' }
  }
  if (roleView.value === 'operator') {
    return canGenerate.value ? { label: '按当前样本重生成', action: 'regenerate' } : { label: '预览报告', action: 'preview' }
  }
  if (roleView.value === 'finance') {
    return { label: '预览报告', action: 'preview' }
  }
  if (roleView.value === 'hrss') {
    return { label: '查看详情', action: 'detail' }
  }
  if (canGenerate.value && (matchWorkbenchFocus(selectedReport.value, 'highRisk') || matchWorkbenchFocus(selectedReport.value, 'preview'))) {
    return { label: '按当前样本重生成', action: 'regenerate' }
  }
  return { label: '预览报告', action: 'preview' }
})

const secondaryAction = computed(() => {
  if (roleView.value === 'finance') {
    if (activeFocus.value?.key === 'highRisk') {
      return { label: '查看信用评分', path: '/ygb/creditScore' }
    }
    return { label: '查看税务对比', path: '/ygb/taxCompare' }
  }
  if (roleView.value === 'hrss') {
    if (activeFocus.value?.key === 'highRisk') {
      return { label: '进入预警中心', path: '/ygb/warning' }
    }
    return { label: '查看预警治理月报', path: '/ygb-report/statReport/warning' }
  }
  if (roleView.value === 'operator') {
    return { label: '查看模型配置', path: '/ygb/aiReportConfig' }
  }
  if (activeFocus.value?.key === 'preview') {
    return { label: '查看模型配置', path: '/ygb/aiReportConfig' }
  }
  if (activeFocus.value?.key === 'highRisk') {
    return { label: '进入预警中心', path: '/ygb/warning' }
  }
  return { label: '查看信用评分', path: '/ygb/creditScore' }
})


const tableActionWidth = computed(() => (canGenerate.value ? 220 : 150))

const standardRows = computed(() => {
  const weights = parseJson(currentConfig.value.dimensionWeights)
  const targets = parseJson(currentConfig.value.targetValues)
  return [
    { label: 'A 合同备案合规', weight: weights.A || 0, target: `${targets.contractRate || '-'}%`, remark: '围绕合同备案率、OCR 存证与续签完整性。' },
    { label: 'B 考勤归集合规', weight: weights.B || 0, target: `${targets.attendanceRate || '-'}%`, remark: '围绕考勤核验通过率和归集真实性。' },
    { label: 'C 工资发放合规', weight: weights.C || 0, target: `${targets.paySuccessRate || '-'}%`, remark: '围绕工资发放成功率和银行回盘闭环。' },
    { label: 'D 设备工伤安全', weight: weights.D || 0, target: `${targets.onlineRate || '-'}% / ${targets.injuryRate || '-'}‰`, remark: '围绕设备在线率和工伤发生率。' },
    { label: 'E 预警闭环办理', weight: weights.E || 0, target: `${targets.warningCloseRate || '-'}%`, remark: '围绕预警签收、闭环和超时处置。' }
  ]
})

function buildRankingHighlight(row, mode) {
  if (!row) return '-'
  if (mode === 'positive') {
    return row.highlight || buildReportSummary(row)
  }
  return row.riskReason || buildReportSummary(row)
}

function buildHighRiskReason(row) {
  return row?.riskReason || buildReportSummary(row)
}

function buildHighRiskAdvice(row) {
  return row?.advice || '建议重新生成报告并复核薄弱维度，形成整改办理台账。'
}

function reportRowClassName({ row }) {
  return row.reportId === activeReportId.value ? 'is-dashboard-active' : ''
}

async function handleSelectReport(row) {
  activeReportId.value = row.reportId
  await loadDashboard(row.reportId)
  await nextTick()
  renderCharts()
}

async function selectAndPreview(row) {
  activeReportId.value = row.reportId
  await loadDashboard(row.reportId)
  await handlePreviewReport()
}

async function handleSelectFocus(item) {
  activeFocusKey.value = item.key
  const matched = visibleReportList.value.find(row => matchWorkbenchFocus(row, item.key))
  if (matched?.reportId) {
    activeReportId.value = matched.reportId
    await loadDashboard(matched.reportId)
    await nextTick()
    renderCharts()
  }
}

function openModule(path) {
  if (!path) return
  router.push(path)
}

function goAiTaskPage() {
  if (selectedReport.value?.reportId) {
    router.push({
      path: '/ygb/aiReportTask',
      query: { reportId: String(selectedReport.value.reportId) }
    })
    return
  }
  router.push('/ygb/aiReportTask')
}

function goAiSubscriptionPage() {
  router.push('/ygb/aiReportSubscription')
}

async function handlePrimaryAction() {
  if (primaryAction.value.action === 'generate') {
    openGenerateDialog()
    return
  }
  if (!selectedReport.value) {
    return
  }
  if (primaryAction.value.action === 'regenerate') {
    openGenerateDialog(selectedReport.value)
    return
  }
  if (primaryAction.value.action === 'preview') {
    await selectAndPreview(selectedReport.value)
    return
  }
  await openDetail(selectedReport.value)
}

function handleConfigRefresh() {
  loadCurrentConfig()
}

function resetQuery() {
  proxy.resetForm('queryRef')
  queryParams.pageNum = 1
  queryParams.pageSize = 10
  queryParams.regionCode = defaultRegionCode
  queryParams.reportType = 'MONTHLY'
  queryParams.enterpriseType = ''
  queryParams.riskLevel = undefined
  queryRange.value = currentMonthRange()
  activeReportId.value = undefined
  applyWorkbenchRouteQuery(route.query, queryParams, aiReportWorkbenchFields)
  getList()
}

function clearWorkbenchContext() {
  queryParams.pageNum = 1
  queryParams.pageSize = 10
  queryParams.regionCode = defaultRegionCode
  queryParams.reportType = 'MONTHLY'
  queryParams.enterpriseType = ''
  queryParams.riskLevel = undefined
  queryRange.value = currentMonthRange()
  activeReportId.value = undefined
  router.replace({
    path: route.path,
    query: stripWorkbenchRouteQuery(route.query, aiReportWorkbenchFields)
  })
  getList()
}

function renderDimensionChart() {
  if (!dimensionChartRef.value) return
  if (!dimensionChart) {
    dimensionChart = echarts.init(dimensionChartRef.value)
  }
  const categories = selectedDimensionItems.value.map(item => `${item.dimensionCode} ${item.dimensionName}`)
  const scores = selectedDimensionItems.value.map(item => Number(item.dimensionScore || 0))
  dimensionChart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: '4%', right: '4%', top: 36, bottom: 48, containLabel: true },
    xAxis: {
      type: 'category',
      data: categories,
      axisLabel: {
        color: '#49627a',
        interval: 0,
        rotate: categories.length > 3 ? 18 : 0
      },
      axisLine: { lineStyle: { color: '#c3d3e2' } }
    },
    yAxis: {
      type: 'value',
      min: 0,
      max: 100,
      axisLabel: { color: '#49627a' },
      splitLine: { lineStyle: { color: '#e8eef5' } }
    },
    series: [
      {
        name: '维度得分',
        type: 'bar',
        barWidth: '42%',
        data: scores,
        itemStyle: {
          color: '#c8282d',
          borderRadius: [8, 8, 0, 0]
        }
      },
      {
        name: '达标线',
        type: 'line',
        smooth: true,
        symbol: 'none',
        data: scores.map(() => 85),
        lineStyle: {
          color: '#2c7da0',
          width: 2,
          type: 'dashed'
        }
      }
    ]
  })
}

function renderTrendChart() {
  if (!trendChartRef.value) return
  if (!trendChart) {
    trendChart = echarts.init(trendChartRef.value)
  }
  trendChart.setOption({
    tooltip: { trigger: 'axis' },
    legend: {
      top: 0,
      textStyle: { color: '#49627a' }
    },
    grid: { left: '4%', right: '4%', top: 40, bottom: 48, containLabel: true },
    xAxis: {
      type: 'category',
      data: trendPoints.value.map(item => item.label),
      axisLine: { lineStyle: { color: '#c3d3e2' } },
      axisLabel: { color: '#49627a' }
    },
    yAxis: {
      type: 'value',
      min: 0,
      max: 100,
      axisLabel: { color: '#49627a' },
      splitLine: { lineStyle: { color: '#e8eef5' } }
    },
    series: [
      {
        name: '综合得分',
        type: 'line',
        smooth: true,
        data: trendPoints.value.map(item => Number(item.score || 0)),
        symbolSize: 8,
        itemStyle: { color: '#2c7da0' },
        lineStyle: { width: 3, color: '#2c7da0' }
      }
    ]
  })
}

function renderCharts() {
  renderDimensionChart()
  renderTrendChart()
}

function handleResize() {
  dimensionChart?.resize()
  trendChart?.resize()
}

function getAppScrollContainer() {
  return document.querySelector('.main-container > .app-main')
}

function scrollToSection(key) {
  activeSection.value = key
  const sectionMap = {
    filter: filterSectionRef.value,
    overview: overviewSectionRef.value,
    conclusion: conclusionSectionRef.value,
    charts: chartSectionRef.value,
    ranking: rankingSectionRef.value,
    risk: riskSectionRef.value,
    reports: reportTableSectionRef.value
  }
  const target = sectionMap[key]
  const container = getAppScrollContainer()
  if (!target) return
  if (container) {
    const offset = 20
    const top = target.getBoundingClientRect().top - container.getBoundingClientRect().top + container.scrollTop - offset
    container.scrollTo({ top: Math.max(top, 0), behavior: 'smooth' })
    return
  }
  target.scrollIntoView({ behavior: 'smooth', block: 'start' })
}

function escapeHtml(text) {
  return String(text || '')
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;')
}

function buildPreviewHtml() {
  const current = selectedReport.value
  const detailRows = selectedDimensionItems.value
  const filterPairs = [
    ['监测周期', reportTypeLabel(queryParams.reportType)],
    ['统计区间', `${queryRange.value?.[0] || '-'} 至 ${queryRange.value?.[1] || '-'}`],
    ['行政区划', regionNameMap[queryParams.regionCode] || queryParams.regionCode || '广东省'],
    ['企业类型', enterpriseTypeLabel(queryParams.enterpriseType || 'ALL')],
    ['风险等级', queryParams.riskLevel ? riskLevelLabel(queryParams.riskLevel) : '全部']
  ]
  const filterHtml = filterPairs.map(([label, value]) => `
    <div class="chip">
      <span>${escapeHtml(label)}</span>
      <strong>${escapeHtml(value)}</strong>
    </div>
  `).join('')
  const rankingRows = topRankingList.value.slice(0, 5).map((row, index) => `
    <tr>
      <td>#${escapeHtml(row.rankingNo || index + 1)}</td>
      <td>${escapeHtml(reportDisplayName(row))}</td>
      <td>${escapeHtml(row.regionName)}</td>
      <td>${escapeHtml(row.totalScore)}</td>
    </tr>
  `).join('')
  const riskRows = highRiskList.value.slice(0, 5).map((row, index) => `
    <tr>
      <td>#${escapeHtml(row.rankingNo || index + 1)}</td>
      <td>${escapeHtml(reportDisplayName(row))}</td>
      <td>${escapeHtml(riskLevelLabel(row.riskLevel))}</td>
      <td>${escapeHtml(buildHighRiskReason(row))}</td>
    </tr>
  `).join('')
  const dimensionRows = detailRows.map(item => `
    <tr>
      <td>${escapeHtml(`${item.dimensionCode} ${item.dimensionName}`)}</td>
      <td>${escapeHtml(item.metricLabel)}</td>
      <td>${escapeHtml(item.metricValue)}</td>
      <td>${escapeHtml(item.targetValue)}</td>
      <td>${escapeHtml(item.dimensionScore)}</td>
    </tr>
  `).join('')

  return `<!DOCTYPE html>
  <html lang="zh-CN">
  <head>
    <meta charset="UTF-8" />
    <title>${escapeHtml((current?.regionName || '广东省') + ' AI 监测报告预览')}</title>
    <style>
      body { margin: 0; background: #eef2f6; color: #1d3145; font-family: "Microsoft YaHei", sans-serif; }
      .toolbar { display: flex; justify-content: flex-end; gap: 12px; padding: 20px 24px 0; }
      .toolbar button { border: none; border-radius: 10px; padding: 10px 18px; background: #0f2b3d; color: #fff; cursor: pointer; }
      .report { max-width: 1180px; margin: 20px auto 32px; padding: 28px 32px 40px; background: #fff; border-radius: 24px; box-shadow: 0 12px 32px rgba(15, 43, 61, 0.08); }
      .header { display: flex; justify-content: space-between; gap: 16px; border-bottom: 2px solid #d9e5f0; padding-bottom: 18px; }
      .header h1 { margin: 0; font-size: 28px; color: #0f2b3d; }
      .header p { margin: 8px 0 0; color: #5b7187; font-size: 14px; }
      .badge { padding: 8px 14px; border-radius: 999px; background: #edf4fb; color: #184d72; font-size: 13px; }
      .chips { display: grid; grid-template-columns: repeat(5, minmax(0, 1fr)); gap: 12px; margin-top: 20px; }
      .chip { display: grid; gap: 6px; padding: 14px; border-radius: 14px; background: #f8fbfe; border: 1px solid #e6edf5; }
      .chip span { color: #688095; font-size: 12px; }
      .hero { display: grid; grid-template-columns: 1.4fr 1fr; gap: 18px; margin-top: 22px; }
      .hero-main { padding: 22px 24px; border-radius: 18px; background: linear-gradient(135deg, #0f2b3d 0%, #174969 62%, #eef4f9 62%, #f8fbfe 100%); color: #fff; }
      .hero-score { font-size: 54px; font-weight: 700; line-height: 1; margin-top: 12px; }
      .hero-note { margin-top: 10px; line-height: 1.8; font-size: 14px; }
      .hero-side { display: grid; gap: 12px; }
      .digest { display: grid; gap: 4px; padding: 16px 18px; border-radius: 16px; background: #f8fbfe; border: 1px solid #e4ecf3; }
      .digest span { color: #688095; font-size: 12px; }
      .digest strong { font-size: 22px; color: #17324a; }
      .section-title { margin: 28px 0 12px; font-size: 20px; font-weight: 700; border-left: 4px solid #c8282d; padding-left: 12px; }
      .conclusion { border-left: 6px solid #c8282d; background: #f2f7fb; border-radius: 16px; padding: 18px 20px; line-height: 1.85; }
      table { width: 100%; border-collapse: collapse; font-size: 13px; margin-top: 12px; }
      th, td { border: 1px solid #e4ebf2; padding: 10px 12px; text-align: left; vertical-align: top; }
      th { background: #f7fafc; color: #1f3449; }
      .fragment { margin-top: 14px; padding: 18px 20px; border: 1px solid #e3ebf3; border-radius: 16px; background: #fafcfe; }
      @media print {
        body { background: #fff; }
        .toolbar { display: none; }
        .report { box-shadow: none; border-radius: 0; max-width: none; padding: 18px 24px; margin: 0; }
      }
      @media (max-width: 900px) {
        .chips, .hero { grid-template-columns: 1fr; }
      }
    </style>
  </head>
  <body>
    <div class="toolbar">
      <button onclick="window.print()">打印</button>
      <button onclick="window.close()">关闭</button>
    </div>
    <div class="report">
      <div class="header">
        <div>
          <h1>${escapeHtml((current?.regionName || '广东省') + ' AI 监测报告')}</h1>
          <p>报告类型：${escapeHtml(reportTypeLabel(current?.reportType))} · 统计区间：${escapeHtml(formatDate(current?.periodStart))} 至 ${escapeHtml(formatDate(current?.periodEnd))} · 模型版本：${escapeHtml(current?.configVersion || currentConfig.value.version || 'DEFAULT-STUB')}</p>
        </div>
        <div class="badge">${escapeHtml(riskLevelLabel(current?.riskLevel))}</div>
      </div>
      <div class="chips">${filterHtml}</div>
      <div class="hero">
        <div class="hero-main">
          <div>业务摘要</div>
          <div class="hero-score">${escapeHtml(current?.totalScore || averageScore.value)}</div>
          <div class="hero-note">${escapeHtml(activeReportSummary.value)}</div>
        </div>
        <div class="hero-side">
          ${overviewDigestItems.value.map(item => `
            <div class="digest">
              <span>${escapeHtml(item.label)}</span>
              <strong>${escapeHtml(item.value)}</strong>
              <span>${escapeHtml(item.hint)}</span>
            </div>
          `).join('')}
        </div>
      </div>
      <div class="section-title">一、AI 监测结论</div>
      <div class="conclusion">
        <p><strong>综合判断：</strong>${escapeHtml(conclusionSummary.value)}</p>
        <p><strong>主要优势：</strong>${escapeHtml(conclusionStrength.value)}</p>
        <p><strong>突出问题：</strong>${escapeHtml(conclusionWeakness.value)}</p>
        <p><strong>建议动作：</strong>${escapeHtml(conclusionAdvice.value)}</p>
      </div>
      <div class="section-title">二、风险排名</div>
      <table>
        <thead><tr><th>序位</th><th>对象</th><th>区域</th><th>得分</th></tr></thead>
        <tbody>${rankingRows || '<tr><td colspan="4">暂无样本</td></tr>'}</tbody>
      </table>
      <div class="section-title">三、高风险对象</div>
      <table>
        <thead><tr><th>序位</th><th>对象</th><th>风险等级</th><th>主要风险点</th></tr></thead>
        <tbody>${riskRows || '<tr><td colspan="4">暂无高风险对象</td></tr>'}</tbody>
      </table>
      <div class="section-title">四、维度明细</div>
      <table>
        <thead><tr><th>维度</th><th>指标</th><th>指标值</th><th>目标值</th><th>得分</th></tr></thead>
        <tbody>${dimensionRows || '<tr><td colspan="5">暂无维度明细</td></tr>'}</tbody>
      </table>
      <div class="section-title">五、原始片段</div>
      <div class="fragment">${current?.reportHtml || '暂无原始监测片段。'}</div>
    </div>
  </body>
  </html>`
}

async function handlePreviewReport() {
  if (!selectedReport.value && !reportList.value.length) {
    ElMessage.warning('当前没有可预览的报告样本')
    return
  }
  if (!selectedReport.value && reportList.value[0]?.reportId) {
    await loadDashboard(reportList.value[0].reportId)
  }
  const previewWindow = window.open('', '_blank')
  if (!previewWindow) {
    ElMessage.warning('浏览器拦截了新窗口，请允许弹窗后重试')
    return
  }
  previewWindow.opener = null
  previewWindow.document.write(buildPreviewHtml())
  previewWindow.document.close()
}

watch(
  () => [selectedDimensionItems.value.length, trendPoints.value.length, activeReportId.value],
  async () => {
    await nextTick()
    renderCharts()
  }
)

onMounted(async () => {
  window.addEventListener('resize', handleResize)
  applyWorkbenchRouteQuery(route.query, queryParams, aiReportWorkbenchFields)
  await getList()
  await nextTick()
  renderCharts()
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  dimensionChart?.dispose()
  trendChart?.dispose()
  dimensionChart = null
  trendChart = null
})
</script>

<style scoped lang="scss">
.ai-report-page {
  background: #f3f6fa;
  padding-bottom: 48px;
  min-width: 0;
  max-width: 100%;
}

.ygb-panel-card {
  height: 100%;
}

.ygb-workbench-alert {
  margin-bottom: 18px;
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
  gap: 8px 12px;
  margin-bottom: 12px;
  color: #4a637b;
  line-height: 1.7;
  font-size: 13px;
}

.ygb-workbench-alert__desc strong {
  color: #0f5ea8;
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
.ygb-panel-card,
.report-card {
  border: 1px solid #dbe5f0;
  border-radius: 18px;
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

.ygb-summary-card--primary {
  background: linear-gradient(180deg, #ffffff 0%, #f2f7fd 100%);
}

.ygb-summary-card--success {
  background: linear-gradient(180deg, #ffffff 0%, #f3fbf5 100%);
}

.ygb-summary-card--warning {
  background: linear-gradient(180deg, #ffffff 0%, #fff9ef 100%);
}

.ygb-summary-card--neutral {
  background: linear-gradient(180deg, #ffffff 0%, #f7f9fc 100%);
}

.ygb-panel-card :deep(.el-card__header) {
  padding: 18px 20px 0;
  border-bottom: none;
}

.ygb-panel-card :deep(.el-card__body) {
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

.ygb-focus-list,
.ygb-step-list {
  display: grid;
  gap: 12px;
}

.ygb-data-row,
.ygb-step-item {
  border: 1px solid #dde7f1;
  border-radius: 14px;
  background: #f8fbfd;
}

.ygb-data-row {
  padding: 14px 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  color: #627486;
  font-size: 13px;
}

.ygb-data-row strong {
  color: #13243a;
  font-size: 14px;
  text-align: right;
}

.ygb-step-item {
  display: grid;
  grid-template-columns: 42px minmax(0, 1fr);
  gap: 14px;
  padding: 16px;
}

.ygb-step-item__index {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  height: 34px;
  border-radius: 10px;
  background: #edf5fc;
  color: #0f5ea8;
  font-weight: 700;
}

.ygb-step-item__body strong {
  color: #13243a;
}

.ygb-step-item__body p {
  margin: 8px 0 0;
  color: #5f6f80;
  font-size: 13px;
  line-height: 1.7;
}

.ygb-focus-list--single {
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

.ygb-source-item {
  padding: 14px 16px;
  border: 1px solid #dde7f1;
  border-radius: 14px;
  background: #f8fbfd;
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

.ygb-tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.ai-report-layout {
  display: grid;
  grid-template-columns: 220px minmax(0, 1fr);
  gap: 18px;
  align-items: start;
  min-width: 0;
  max-width: 100%;
}

.section-nav {
  position: sticky;
  top: 18px;
  padding: 18px 14px;
  border-radius: 18px;
  background: linear-gradient(180deg, #102d42 0%, #153c57 100%);
  color: #d9e6ef;
  box-shadow: 0 16px 28px rgba(16, 45, 66, 0.18);
}

.section-nav__title {
  padding: 0 10px 14px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.12);
  font-size: 18px;
  font-weight: 700;
  color: #fff;
}

.section-nav__item {
  width: 100%;
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: 10px;
  padding: 12px 12px;
  border: 0;
  border-radius: 12px;
  background: transparent;
  color: #d9e6ef;
  text-align: left;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.section-nav__item:hover,
.section-nav__item.is-active {
  background: rgba(255, 255, 255, 0.12);
}

.section-nav__index {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 26px;
  height: 26px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.12);
  font-size: 12px;
  font-weight: 700;
}

.report-main {
  display: grid;
  gap: 16px;
  min-width: 0;
  max-width: 100%;
}

.report-card {
  padding: 22px 24px;
  min-width: 0;
  max-width: 100%;
}

.report-card--filter {
  background: linear-gradient(180deg, #ffffff 0%, #f8fbfe 100%);
}

.report-card__head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 16px;
}

.report-card__head h3 {
  margin: 0;
  color: #13243a;
  font-size: 20px;
}

.report-card__head p,
.report-card__summary {
  margin: 6px 0 0;
  color: #667b8f;
  font-size: 13px;
  line-height: 1.7;
}

.report-card__head--table {
  align-items: center;
}

.report-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.report-filter-form {
  margin-bottom: -18px;
  display: flex;
  flex-wrap: wrap;
  gap: 0 12px;
}

.report-filter-form :deep(.el-form-item) {
  margin-right: 0;
}

.report-section-title {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 16px;
}

.report-section-title__bar {
  width: 4px;
  min-height: 44px;
  border-radius: 999px;
  background: linear-gradient(180deg, #c8282d 0%, #e38d4b 100%);
}

.report-section-title h3 {
  margin: 0;
  color: #13243a;
  font-size: 22px;
}

.report-section-title p {
  margin: 6px 0 0;
  color: #667b8f;
  font-size: 13px;
  line-height: 1.7;
}

.overview-hero {
  display: grid;
  grid-template-columns: minmax(0, 1.55fr) minmax(0, 1fr);
  gap: 18px;
  margin-bottom: 18px;
  min-width: 0;
}

.overview-hero__main {
  padding: 22px 24px;
  border-radius: 18px;
  background: linear-gradient(135deg, #0f2b3d 0%, #174969 62%, #eef4f9 62%, #f8fbfe 100%);
  color: #fff;
  min-width: 0;
}

.overview-hero__eyebrow {
  font-size: 12px;
  letter-spacing: 1px;
  text-transform: uppercase;
  color: rgba(255, 255, 255, 0.72);
}

.overview-hero__scoreline {
  display: flex;
  flex-wrap: wrap;
  gap: 18px;
  align-items: flex-end;
  margin-top: 12px;
}

.overview-hero__score {
  font-size: 54px;
  line-height: 1;
  font-weight: 700;
}

.overview-hero__meta {
  display: grid;
  gap: 10px;
  min-width: 0;
  flex: 1 1 220px;
}

.overview-hero__risk {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  align-items: center;
  color: rgba(255, 255, 255, 0.92);
  font-size: 14px;
  line-height: 1.75;
}

.overview-hero__submeta {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
  color: rgba(255, 255, 255, 0.78);
  font-size: 12px;
}

.overview-hero__aside {
  display: grid;
  gap: 12px;
  min-width: 0;
}

.overview-digest {
  display: grid;
  gap: 4px;
  padding: 16px 18px;
  border-radius: 16px;
  border: 1px solid #e4ecf3;
  background: #f8fbfe;
  min-width: 0;
}

.overview-digest__label {
  color: #688095;
  font-size: 12px;
}

.overview-digest__value {
  color: #17324a;
  font-size: 22px;
  font-weight: 700;
  word-break: break-word;
}

.overview-digest__hint {
  color: #8295a7;
  font-size: 12px;
}

.score-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));
  gap: 16px;
  min-width: 0;
}

.score-card {
  border: 1px solid #e3ebf3;
  border-radius: 16px;
  padding: 18px;
  background: #f8fbfe;
  min-width: 0;
}

.score-card__value {
  font-size: 30px;
  font-weight: 700;
  color: #1b3349;
  word-break: break-word;
}

.score-card__label {
  margin-top: 8px;
  color: #4f667c;
  font-size: 14px;
}

.score-card__trend {
  margin-top: 8px;
  color: #6d8093;
  font-size: 12px;
}

.overview-tags {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  margin-top: 16px;
}

.overview-tags__item {
  display: inline-flex;
  align-items: center;
  padding: 7px 12px;
  border-radius: 999px;
  background: #f3f7fb;
  border: 1px solid #e4ecf3;
  color: #4f667c;
  font-size: 12px;
}

.ai-conclusion-body {
  border-left: 6px solid #c8282d;
  background: #f2f7fb;
  border-radius: 16px;
  padding: 18px 20px;
  line-height: 1.85;
}

.chart-section,
.ranking-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.chart-card,
.ranking-panel {
  border: 1px solid #e3ebf3;
  border-radius: 18px;
  background: #fbfdff;
}

.chart-card {
  padding: 18px 20px;
}

.chart-card__title,
.ranking-panel__title {
  color: #17324a;
  font-size: 16px;
  font-weight: 700;
}

.ranking-panel__desc {
  margin-top: 6px;
  color: #6d8093;
  font-size: 12px;
  line-height: 1.7;
}

.chart-box {
  width: 100%;
  min-height: 360px;
  height: 360px;
}

.ranking-summary-strip {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14px;
  margin-bottom: 18px;
}

.ranking-summary-strip__item {
  display: grid;
  gap: 5px;
  padding: 14px 16px;
  border-radius: 16px;
  background: #f8fbfe;
  border: 1px solid #e4ecf3;
}

.ranking-summary-strip__label {
  color: #688095;
  font-size: 12px;
}

.ranking-summary-strip__value {
  color: #17324a;
  font-size: 18px;
  font-weight: 700;
}

.ranking-summary-strip__hint {
  color: #8295a7;
  font-size: 12px;
}

.ranking-no {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 42px;
  padding: 3px 8px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 700;
}

.ranking-no--good {
  background: #edf6f1;
  color: #157347;
}

.ranking-no--risk {
  background: #feeceb;
  color: #b42318;
}

.risk-focus-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14px;
  margin-bottom: 18px;
}

.risk-focus-card {
  padding: 16px 18px;
  border-radius: 18px;
  border: 1px solid #f2d3d0;
  background: linear-gradient(180deg, #fff7f6 0%, #fffdfd 100%);
}

.risk-focus-card__head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

.risk-focus-card__badge {
  display: inline-flex;
  align-items: center;
  padding: 4px 10px;
  border-radius: 999px;
  background: #feeceb;
  color: #b42318;
  font-size: 12px;
  font-weight: 600;
}

.risk-focus-card__score {
  color: #b42318;
  font-size: 24px;
  font-weight: 700;
}

.risk-focus-card h4 {
  margin: 14px 0 8px;
  color: #17324a;
  font-size: 16px;
}

.risk-focus-card p {
  margin: 0;
  color: #5a6f84;
  font-size: 13px;
  line-height: 1.75;
}

.risk-focus-card__foot {
  margin-top: 12px;
  padding-top: 10px;
  border-top: 1px dashed #ead0cd;
  color: #7b5b57;
  font-size: 12px;
  line-height: 1.7;
}

:deep(.el-table .is-dashboard-active) {
  --el-table-tr-bg-color: #f7fbff;
}

.ygb-detail-block {
  margin-top: 20px;
}

.ygb-detail-block h3 {
  margin: 0 0 12px;
  color: #13243a;
  font-size: 16px;
}

.ygb-detail-focus {
  margin: 0;
  padding: 14px 16px;
  border-radius: 14px;
  background: #f8fbfe;
  border: 1px solid #e3ebf3;
  color: #5f6f80;
  line-height: 1.8;
}

.detail-html {
  padding: 18px 20px;
  border-radius: 16px;
  border: 1px solid #e3ebf3;
  background: #fafcfe;
}

@media (max-width: 1400px) {
  .ai-report-layout {
    grid-template-columns: minmax(0, 1fr);
  }

  .section-nav {
    position: static;
    display: grid;
    grid-template-columns: repeat(4, minmax(0, 1fr));
    gap: 10px;
  }

  .section-nav__title {
    grid-column: 1 / -1;
  }

  .ygb-summary-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 1200px) {
  .overview-hero {
    grid-template-columns: minmax(0, 1fr);
  }

  .overview-hero__aside {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}

@media (max-width: 1024px) {
  .overview-hero__aside {
    grid-template-columns: 1fr;
  }

  .overview-hero,
  .chart-section,
  .ranking-grid,
  .ranking-summary-strip,
  .risk-focus-grid,
  .score-grid,
  .ygb-focus-grid,
  .ygb-source-list {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .report-card__head,
  .report-card__head--table {
    display: block;
  }

  .report-actions {
    margin-top: 12px;
  }

  .section-nav {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

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


