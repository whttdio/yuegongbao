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

    <div class="azb-ai-report-layout">
      <aside class="azb-section-nav">
        <div class="azb-section-nav__title">AI 治理</div>
        <button
          v-for="item in navItems"
          :key="item.key"
          type="button"
          class="azb-section-nav__item"
          :class="{ 'is-active': item.key === activeSection }"
          @click="scrollToSection(item.key)"
        >
          <span class="azb-section-nav__index">{{ item.icon }}</span>
          <span>{{ item.label }}</span>
        </button>
      </aside>

      <main class="azb-report-main">
        <section ref="filterSectionRef" class="azb-report-card azb-report-card--filter">
          <div class="azb-report-card__head">
            <div>
              <h3>监测筛选</h3>
              <p>按周期、行政区划、企业类型和风险等级刷新当前安责保 AI 治理视图。</p>
            </div>
            <div class="azb-report-actions">
              <el-button type="primary" icon="Search" @click="handleQuery">查询刷新</el-button>
              <el-button type="danger" plain @click="standardOpen = true">AI 评分标准</el-button>
              <el-button type="success" plain @click="handlePreviewReport">预览报告</el-button>
              <el-button plain @click="goAiTaskPage" v-hasPermi="['ygb:aiReportTask:list']">建议任务</el-button>
              <el-button plain @click="goAiSubscriptionPage" v-hasPermi="['ygb:aiReportSubscription:list']">订阅管理</el-button>
            </div>
          </div>

          <el-form ref="queryRef" :model="queryParams" :inline="true" v-show="showSearch" class="azb-report-filter-form">
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
              <el-button v-if="canGenerate" type="primary" plain icon="MagicStick" @click="openGenerateDialog()">生成报告</el-button>
              <el-button v-if="canExport" type="warning" plain icon="Download" @click="handleExport">导出列表</el-button>
              <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
            </el-form-item>
          </el-form>
        </section>

        <section ref="overviewSectionRef" class="azb-report-card">
          <div class="azb-report-section-title">
            <span class="azb-report-section-title__bar" />
            <div>
              <h3>综合评分概览</h3>
              <p>{{ selectedReportTitle }}</p>
            </div>
          </div>

          <div class="azb-overview-hero">
            <div class="azb-overview-hero__main">
              <div class="azb-overview-hero__eyebrow">治理摘要</div>
              <div class="azb-overview-hero__scoreline">
                <div class="azb-overview-hero__score">{{ selectedReport?.totalScore || averageScore }}</div>
                <div class="azb-overview-hero__meta">
                  <div class="azb-overview-hero__risk">
                    <el-tag :type="riskTagType(selectedReport?.riskLevel)">{{ riskLevelLabel(selectedReport?.riskLevel) }}</el-tag>
                    <span>{{ activeReportSummary }}</span>
                  </div>
                  <div class="azb-overview-hero__submeta">
                    <span>当前排名 {{ selectedReport?.rankingNo || '-' }}</span>
                    <span>样本总量 {{ dashboard.totalCount || 0 }}</span>
                    <span>高风险 {{ dashboard.highRiskCount || 0 }}</span>
                  </div>
                </div>
              </div>
            </div>
            <div class="azb-overview-hero__aside">
              <div v-for="item in overviewDigestItems" :key="item.label" class="azb-overview-digest">
                <span class="azb-overview-digest__label">{{ item.label }}</span>
                <strong class="azb-overview-digest__value">{{ item.value }}</strong>
                <span class="azb-overview-digest__hint">{{ item.hint }}</span>
              </div>
            </div>
          </div>

          <div class="azb-score-grid">
            <article v-for="item in scoreCards" :key="item.label" class="azb-score-card">
              <div class="azb-score-card__value">{{ item.value }}</div>
              <div class="azb-score-card__label">{{ resolveAzbDimensionLabel(item.dimensionCode, item.label) }}</div>
              <div class="azb-score-card__trend">{{ item.trend || item.remark || '用于判断当前治理短板。' }}</div>
            </article>
          </div>

          <div class="azb-overview-tags">
            <span v-for="item in overviewTags" :key="item" class="azb-overview-tags__item">{{ item }}</span>
          </div>
        </section>

        <section ref="conclusionSectionRef" class="azb-report-card azb-ai-conclusion-card">
          <div class="azb-report-section-title">
            <span class="azb-report-section-title__bar" />
            <div>
              <h3>AI 监测结论</h3>
              <p>系统基于当前评分维度和近期风险样本自动生成治理结论摘要。</p>
            </div>
          </div>

          <div class="azb-ai-conclusion-body">
            <p><strong>综合判断：</strong>{{ conclusionSummary }}</p>
            <p><strong>主要优势：</strong>{{ conclusionStrength }}</p>
            <p><strong>突出问题：</strong>{{ conclusionWeakness }}</p>
            <p><strong>建议动作：</strong>{{ conclusionAdvice }}</p>
          </div>
        </section>

        <section ref="chartSectionRef" class="azb-report-card">
          <div class="azb-report-section-title">
            <span class="azb-report-section-title__bar" />
            <div>
              <h3>趋势与维度得分</h3>
              <p>结合维度评分和历史趋势，识别隐患压降中的薄弱环节与波动方向。</p>
            </div>
          </div>

          <div class="azb-chart-section">
            <div class="azb-chart-card">
              <div class="azb-chart-card__title">各维度治理得分对比</div>
              <div ref="dimensionChartRef" class="azb-chart-box" />
            </div>
            <div class="azb-chart-card">
              <div class="azb-chart-card__title">近 6 次监测得分趋势</div>
              <div ref="trendChartRef" class="azb-chart-box" />
            </div>
          </div>
        </section>

        <section ref="rankingSectionRef" class="azb-report-card">
          <div class="azb-report-section-title">
            <span class="azb-report-section-title__bar" />
            <div>
              <h3>风险排名对照</h3>
              <p>同步展示前位样本和尾部样本，便于横向对照和重点研判。</p>
            </div>
          </div>

          <div class="azb-ranking-summary-strip">
            <div v-for="item in rankingSummaryItems" :key="item.label" class="azb-ranking-summary-strip__item">
              <span class="azb-ranking-summary-strip__label">{{ item.label }}</span>
              <strong class="azb-ranking-summary-strip__value">{{ item.value }}</strong>
              <span class="azb-ranking-summary-strip__hint">{{ item.hint }}</span>
            </div>
          </div>

          <div class="azb-ranking-grid">
            <el-card shadow="never" class="azb-ranking-panel">
              <template #header>
                <div class="azb-ranking-panel__head">
                  <div class="azb-ranking-panel__title">前 5 样本</div>
                  <div class="azb-ranking-panel__desc">作为正向对标和治理口径校准参考。</div>
                </div>
              </template>
              <el-table :data="topRankingList" size="small" empty-text="暂无样本">
                <el-table-column label="序位" width="74">
                  <template #default="scope">
                    <span class="azb-ranking-no azb-ranking-no--good">#{{ scope.row.rankingNo || scope.$index + 1 }}</span>
                  </template>
                </el-table-column>
                <el-table-column label="对象" min-width="180">
                  <template #default="scope">{{ reportDisplayName(scope.row) }}</template>
                </el-table-column>
                <el-table-column label="区域" prop="regionName" min-width="120" />
                <el-table-column label="得分" prop="totalScore" width="86" />
                <el-table-column label="亮点" min-width="180" show-overflow-tooltip>
                  <template #default="scope">{{ buildRankingHighlight(scope.row, 'positive') }}</template>
                </el-table-column>
              </el-table>
            </el-card>

            <el-card shadow="never" class="azb-ranking-panel">
              <template #header>
                <div class="azb-ranking-panel__head">
                  <div class="azb-ranking-panel__title">后 5 样本</div>
                  <div class="azb-ranking-panel__desc">用于锁定低分尾部样本和重点复核对象。</div>
                </div>
              </template>
              <el-table :data="bottomRankingList" size="small" empty-text="暂无尾部样本">
                <el-table-column label="序位" width="74">
                  <template #default="scope">
                    <span class="azb-ranking-no azb-ranking-no--risk">#{{ scope.row.rankingNo || scope.$index + 1 }}</span>
                  </template>
                </el-table-column>
                <el-table-column label="对象" min-width="180">
                  <template #default="scope">{{ reportDisplayName(scope.row) }}</template>
                </el-table-column>
                <el-table-column label="区域" prop="regionName" min-width="120" />
                <el-table-column label="得分" prop="totalScore" width="86" />
                <el-table-column label="风险因子" min-width="180" show-overflow-tooltip>
                  <template #default="scope">{{ buildRankingHighlight(scope.row, 'risk') }}</template>
                </el-table-column>
              </el-table>
            </el-card>
          </div>
        </section>

        <section ref="riskSectionRef" class="azb-report-card">
          <div class="azb-report-section-title">
            <span class="azb-report-section-title__bar" />
            <div>
              <h3>高风险对象与整改建议</h3>
              <p>列出当前高风险对象、主要问题和整改建议，便于重点处置和闭环跟踪。</p>
            </div>
          </div>

          <div class="azb-risk-focus-grid">
            <article v-for="item in riskFocusCards" :key="item.title" class="azb-risk-focus-card">
              <div class="azb-risk-focus-card__head">
                <span class="azb-risk-focus-card__badge">重点对象</span>
                <span class="azb-risk-focus-card__score">{{ item.score }}</span>
              </div>
              <h4>{{ item.title }}</h4>
              <p>{{ item.reason }}</p>
              <div class="azb-risk-focus-card__foot">{{ item.advice }}</div>
            </article>
          </div>

          <el-table :data="highRiskList" size="small" empty-text="暂无高风险对象">
            <el-table-column label="序位" width="74">
              <template #default="scope">
                <span class="azb-ranking-no azb-ranking-no--risk">#{{ scope.row.rankingNo || scope.$index + 1 }}</span>
              </template>
            </el-table-column>
            <el-table-column label="对象" min-width="180">
              <template #default="scope">{{ reportDisplayName(scope.row) }}</template>
            </el-table-column>
            <el-table-column label="风险等级" width="100">
              <template #default="scope">
                <el-tag :type="riskTagType(scope.row.riskLevel)">{{ riskLevelLabel(scope.row.riskLevel) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="得分" prop="totalScore" width="86" />
            <el-table-column label="主要风险点" min-width="180" show-overflow-tooltip>
              <template #default="scope">{{ buildHighRiskReason(scope.row) }}</template>
            </el-table-column>
            <el-table-column label="建议措施" min-width="180" show-overflow-tooltip>
              <template #default="scope">{{ buildHighRiskAdvice(scope.row) }}</template>
            </el-table-column>
          </el-table>
        </section>

        <section ref="reportTableSectionRef" class="azb-report-card">
          <div class="azb-report-card__head azb-report-card__head--table">
            <div>
              <h3>AI 报告台账</h3>
              <p>{{ focusTableHint }}</p>
            </div>
            <div class="azb-report-card__summary">{{ currentConfigSummary }}</div>
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
              <template #default="scope">{{ reportTypeLabel(scope.row.reportType) }}</template>
            </el-table-column>
            <el-table-column label="区域" min-width="140" prop="regionName" />
            <el-table-column label="统计区间" min-width="220">
              <template #default="scope">
                {{ parseTime(scope.row.periodStart, '{y}-{m}-{d}') }} 至 {{ parseTime(scope.row.periodEnd, '{y}-{m}-{d}') }}
              </template>
            </el-table-column>
            <el-table-column label="企业类型" width="110">
              <template #default="scope">{{ enterpriseTypeLabel(scope.row.enterpriseType) }}</template>
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
              <template #default="scope">{{ parseTime(scope.row.generatedTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</template>
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
            :total="total"
            v-model:page="queryParams.pageNum"
            v-model:limit="queryParams.pageSize"
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
            <el-checkbox v-for="item in azbDimensionOptions" :key="item.value" :value="item.value">{{ item.label }}</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="generateOpen = false">取消</el-button>
        <el-button type="primary" @click="submitGenerate">确认生成</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="standardOpen" title="安责保 AI 监测评分标准" width="760px">
      <el-alert :title="currentConfigSummary" type="info" :closable="false" show-icon style="margin-bottom: 16px;" />
      <el-table :data="standardRows" border>
        <el-table-column label="维度" prop="label" min-width="180" />
        <el-table-column label="权重" prop="weight" width="90" />
        <el-table-column label="目标值" prop="target" min-width="140" />
        <el-table-column label="说明" prop="remark" min-width="220" show-overflow-tooltip />
      </el-table>
      <template #footer>
        <el-button type="primary" @click="standardOpen = false">知道了</el-button>
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
import { computed, getCurrentInstance, nextTick, onBeforeUnmount, onMounted, ref, watch, watchEffect } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'
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
import { parseTime } from '@/utils/yuegongbao'
import {
  buildBaseAiReportHintTags,
  buildReportSummary,
  currentMonthRange,
  dimensionOptions,
  enterpriseTypeLabel,
  enterpriseTypeOptions,
  formatDate,
  focusQueue,
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

const navItems = [
  { key: 'filter', label: '监测筛选', icon: '01' },
  { key: 'overview', label: '综合概览', icon: '02' },
  { key: 'conclusion', label: 'AI 结论', icon: '03' },
  { key: 'charts', label: '趋势图表', icon: '04' },
  { key: 'ranking', label: '排名对照', icon: '05' },
  { key: 'risk', label: '高风险对象', icon: '06' },
  { key: 'reports', label: '报告台账', icon: '07' }
]

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
  selectedDimensionItems,
  detailDimensionItems,
  detailDisplayReport,
  getList,
  loadDashboard,
  loadCurrentConfig,
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

const canGenerate = computed(() => !isReadOnlyRole.value && hasPermission(permissions.value, ['ygb:aiReport:generate']))
const canExport = computed(() => hasPermission(permissions.value, ['ygb:aiReport:export']))
const averageScore = computed(() => formatDecimal(dashboard.averageScore))
const topRankingList = computed(() => dashboard.topRankingList || [])
const bottomRankingList = computed(() => dashboard.bottomRankingList || [])
const highRiskList = computed(() => dashboard.highRiskList || [])
const trendPoints = computed(() => dashboard.trendPoints || [])

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

const scoreCards = computed(() => {
  const rows = dashboard.scoreCards || []
  if (rows.length) {
    return rows.map(item => ({
      ...item,
      label: resolveAzbDimensionLabel(item.dimensionCode, item.label)
    }))
  }
  return selectedDimensionItems.value.map(item => ({
    label: resolveAzbDimensionLabel(item.dimensionCode, item.dimensionName),
    value: formatDecimal(item.dimensionScore),
    trend: item.suggestionText || '用于判断当前治理短板。'
  }))
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

const activeReportSummary = computed(() => buildReportSummary(selectedReport.value).replace('业务', '治理'))
const conclusionSummary = computed(() => dashboard.conclusion?.summary || '当前暂无报告数据。')
const conclusionStrength = computed(() => dashboard.conclusion?.strength || '暂无优势维度识别结果。')
const conclusionWeakness = computed(() => dashboard.conclusion?.weakness || '暂无薄弱维度识别结果。')
const conclusionAdvice = computed(() => dashboard.conclusion?.advice || '建议先生成报告样本，再进行治理对比分析。')

const currentConfigSummary = computed(() => {
  const weights = parseJson(currentConfig.value.dimensionWeights)
  const targets = parseJson(currentConfig.value.targetValues)
  const weightText = ['A', 'B', 'C', 'D', 'E'].map(code => `${code}:${weights[code] || 0}`).join(' / ')
  return `当前模型 ${currentConfig.value.version || 'DEFAULT-STUB'}，权重 ${weightText}，目标值 主体${targets.contractRate || '-'}% / 在岗${targets.attendanceRate || '-'}% / 收入${targets.paySuccessRate || '-'}% / 在线${targets.onlineRate || '-'}% / 工伤${targets.injuryRate || '-'}‰ / 闭环${targets.warningCloseRate || '-'}%。`
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
    { label: '治理模型', value: currentConfig.value.version || 'DEFAULT-STUB', hint: '评分权重口径同步生效' }
  ]
})

const overviewTags = computed(() => {
  const tags = [
    `${reportTypeLabel(queryParams.reportType)}监测`,
    `${regionNameMap[queryParams.regionCode] || queryParams.regionCode || '广东省'}治理视角`,
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
      hint: '已纳入高风险治理清单'
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

const tableActionWidth = computed(() => (canGenerate.value ? 220 : 150))

const standardRows = computed(() => {
  const weights = parseJson(currentConfig.value.dimensionWeights)
  const targets = parseJson(currentConfig.value.targetValues)
  return [
    { label: 'A 主体台账合规', weight: weights.A || 0, target: `${targets.contractRate || '-'}%`, remark: '围绕参保主体、项目主体、人员主体和基础台账完整性。' },
    { label: 'B 在岗留痕合规', weight: weights.B || 0, target: `${targets.attendanceRate || '-'}%`, remark: '围绕在岗记录、培训留痕和现场作业轨迹。' },
    { label: 'C 收入联动合规', weight: weights.C || 0, target: `${targets.paySuccessRate || '-'}%`, remark: '围绕保费、赔付、资金流和银行协同数据一致性。' },
    { label: 'D 设备作业安全', weight: weights.D || 0, target: `${targets.onlineRate || '-'}% / ${targets.injuryRate || '-'}‰`, remark: '围绕设备在线、工伤事件、隐患预警和高危作业风险。' },
    { label: 'E 风险闭环处置', weight: weights.E || 0, target: `${targets.warningCloseRate || '-'}%`, remark: '围绕预警签收、整改闭环、压降效果和协同处置时效。' }
  ]
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
  syncCurrentReport()
  if (activeReportId.value) {
    await loadDashboard(activeReportId.value)
    await nextTick()
    renderCharts()
  }
}

async function handlePrimaryReportAction() {
  if (!selectedReport.value) return
  if (primaryReportAction.value.action === 'generate') {
    if (isReadOnlyRole.value) {
      proxy.$modal.msgWarning(`${readOnlyRoleLabel.value}仅保留摘要、详情和导出，不能生成报告`)
      return
    }
    openGenerateDialog(selectedReport.value)
    return
  }
  await openDetail(selectedReport.value)
}

function reportRowClassName({ row }) {
  return row.reportId === activeReportId.value ? 'is-dashboard-active' : ''
}

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
  return row?.advice || '建议重新生成报告并复核薄弱维度，形成整改治理台账。'
}

function handleConfigRefresh() {
  loadCurrentConfig()
}

function goAiTaskPage() {
  if (selectedReport.value?.reportId) {
    router.push({
      path: '/azb/aiReportTask',
      query: { reportId: String(selectedReport.value.reportId) }
    })
    return
  }
  router.push('/azb/aiReportTask')
}

function goAiSubscriptionPage() {
  router.push('/azb/aiReportSubscription')
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

function renderDimensionChart() {
  if (!dimensionChartRef.value) return
  if (!dimensionChart) {
    dimensionChart = echarts.init(dimensionChartRef.value)
  }
  const categories = selectedDimensionItems.value.map(item => `${item.dimensionCode} ${resolveAzbDimensionLabel(item.dimensionCode, item.dimensionName)}`)
  const scores = selectedDimensionItems.value.map(item => Number(item.dimensionScore || 0))
  dimensionChart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: '4%', right: '4%', top: 36, bottom: 48, containLabel: true },
    xAxis: {
      type: 'category',
      data: categories,
      axisLabel: {
        color: '#48656e',
        interval: 0,
        rotate: categories.length > 3 ? 18 : 0
      },
      axisLine: { lineStyle: { color: '#c8d8de' } }
    },
    yAxis: {
      type: 'value',
      min: 0,
      max: 100,
      axisLabel: { color: '#48656e' },
      splitLine: { lineStyle: { color: '#e5eef2' } }
    },
    series: [
      {
        name: '治理得分',
        type: 'bar',
        barWidth: '42%',
        data: scores,
        itemStyle: {
          color: '#0b6b78',
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
          color: '#d7563f',
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
      textStyle: { color: '#48656e' }
    },
    grid: { left: '4%', right: '4%', top: 40, bottom: 48, containLabel: true },
    xAxis: {
      type: 'category',
      data: trendPoints.value.map(item => item.label),
      axisLine: { lineStyle: { color: '#c8d8de' } },
      axisLabel: { color: '#48656e' }
    },
    yAxis: {
      type: 'value',
      min: 0,
      max: 100,
      axisLabel: { color: '#48656e' },
      splitLine: { lineStyle: { color: '#e5eef2' } }
    },
    series: [
      {
        name: '综合得分',
        type: 'line',
        smooth: true,
        data: trendPoints.value.map(item => Number(item.score || 0)),
        symbolSize: 8,
        itemStyle: { color: '#0b6b78' },
        lineStyle: { width: 3, color: '#0b6b78' }
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
      <td>${escapeHtml(`${item.dimensionCode} ${resolveAzbDimensionLabel(item.dimensionCode, item.dimensionName)}`)}</td>
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
    <title>${escapeHtml((current?.regionName || '广东省') + ' 安责保 AI 监测报告预览')}</title>
    <style>
      body { margin: 0; background: #eef4f5; color: #12333c; font-family: "Microsoft YaHei", sans-serif; }
      .toolbar { display: flex; justify-content: flex-end; gap: 12px; padding: 20px 24px 0; }
      .toolbar button { border: none; border-radius: 10px; padding: 10px 18px; background: #0b3f49; color: #fff; cursor: pointer; }
      .report { max-width: 1180px; margin: 20px auto 32px; padding: 28px 32px 40px; background: #fff; border-radius: 24px; box-shadow: 0 12px 32px rgba(11, 63, 73, 0.1); }
      .header { display: flex; justify-content: space-between; gap: 16px; border-bottom: 2px solid #d7e3e8; padding-bottom: 18px; }
      .header h1 { margin: 0; font-size: 28px; color: #12333c; }
      .header p { margin: 8px 0 0; color: #58707b; font-size: 14px; }
      .badge { padding: 8px 14px; border-radius: 999px; background: #e8f5f4; color: #0b6b78; font-size: 13px; }
      .chips { display: grid; grid-template-columns: repeat(5, minmax(0, 1fr)); gap: 12px; margin-top: 20px; }
      .chip { display: grid; gap: 6px; padding: 14px; border-radius: 14px; background: #f7fafb; border: 1px solid #dde9ed; }
      .chip span { color: #66808a; font-size: 12px; }
      .hero { display: grid; grid-template-columns: 1.4fr 1fr; gap: 18px; margin-top: 22px; }
      .hero-main { padding: 22px 24px; border-radius: 18px; background: linear-gradient(135deg, #0b3f49 0%, #0b6b78 62%, #eef7f6 62%, #f8fbfb 100%); color: #fff; }
      .hero-score { font-size: 54px; font-weight: 700; line-height: 1; margin-top: 12px; }
      .hero-note { margin-top: 10px; line-height: 1.8; font-size: 14px; }
      .hero-side { display: grid; gap: 12px; }
      .digest { display: grid; gap: 4px; padding: 16px 18px; border-radius: 16px; background: #f7fafb; border: 1px solid #dde9ed; }
      .digest span { color: #66808a; font-size: 12px; }
      .digest strong { font-size: 22px; color: #12333c; }
      .section-title { margin: 28px 0 12px; font-size: 20px; font-weight: 700; border-left: 4px solid #d7563f; padding-left: 12px; }
      .conclusion { border-left: 6px solid #0b6b78; background: #f2faf9; border-radius: 16px; padding: 18px 20px; line-height: 1.85; }
      table { width: 100%; border-collapse: collapse; font-size: 13px; margin-top: 12px; }
      th, td { border: 1px solid #dfe9ed; padding: 10px 12px; text-align: left; vertical-align: top; }
      th { background: #f7fafb; color: #12333c; }
      .fragment { margin-top: 14px; padding: 18px 20px; border: 1px solid #dfe9ed; border-radius: 16px; background: #fafcfc; }
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
          <h1>${escapeHtml((current?.regionName || '广东省') + ' 安责保 AI 监测报告')}</h1>
          <p>报告类型：${escapeHtml(reportTypeLabel(current?.reportType))} · 统计区间：${escapeHtml(formatDate(current?.periodStart))} 至 ${escapeHtml(formatDate(current?.periodEnd))} · 模型版本：${escapeHtml(current?.configVersion || currentConfig.value.version || 'DEFAULT-STUB')}</p>
        </div>
        <div class="badge">${escapeHtml(riskLevelLabel(current?.riskLevel))}</div>
      </div>
      <div class="chips">${filterHtml}</div>
      <div class="hero">
        <div class="hero-main">
          <div>治理摘要</div>
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
.azb-ai-report-workbench {
  background: #f3f7f8;
  padding-bottom: 48px;
  min-width: 0;
  max-width: 100%;

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

  .azb-ai-report-layout {
    display: grid;
    grid-template-columns: 220px minmax(0, 1fr);
    gap: 18px;
    align-items: start;
    min-width: 0;
    max-width: 100%;
  }

  .azb-section-nav {
    position: sticky;
    top: 18px;
    padding: 18px 14px;
    border-radius: 18px;
    background: linear-gradient(180deg, #0b3f49 0%, #125b68 100%);
    color: #d8edf0;
    box-shadow: 0 16px 28px rgba(11, 63, 73, 0.18);
  }

  .azb-section-nav__title {
    padding: 0 10px 14px;
    border-bottom: 1px solid rgba(255, 255, 255, 0.14);
    color: #fff;
    font-size: 18px;
    font-weight: 700;
  }

  .azb-section-nav__item {
    width: 100%;
    display: flex;
    align-items: center;
    gap: 10px;
    margin-top: 10px;
    padding: 12px;
    border: 0;
    border-radius: 12px;
    background: transparent;
    color: #d8edf0;
    text-align: left;
    cursor: pointer;
    transition: background-color 0.2s ease;
  }

  .azb-section-nav__item:hover,
  .azb-section-nav__item.is-active {
    background: rgba(255, 255, 255, 0.13);
  }

  .azb-section-nav__index {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    width: 26px;
    height: 26px;
    border-radius: 999px;
    background: rgba(255, 255, 255, 0.13);
    font-size: 12px;
    font-weight: 700;
  }

  .azb-report-main {
    display: grid;
    gap: 16px;
    min-width: 0;
    max-width: 100%;
  }

  .azb-report-card {
    padding: 22px 24px;
    border: 1px solid #d7e3e8;
    border-radius: 18px;
    background: #fff;
    min-width: 0;
    max-width: 100%;
  }

  .azb-report-card--filter {
    background: linear-gradient(180deg, #ffffff 0%, #f7fbfb 100%);
  }

  .azb-report-card__head {
    display: flex;
    align-items: flex-start;
    justify-content: space-between;
    gap: 16px;
    margin-bottom: 16px;
  }

  .azb-report-card__head h3 {
    margin: 0;
    color: #12333c;
    font-size: 20px;
  }

  .azb-report-card__head p,
  .azb-report-card__summary {
    margin: 6px 0 0;
    color: #58707b;
    font-size: 13px;
    line-height: 1.7;
  }

  .azb-report-card__head--table {
    align-items: center;
  }

  .azb-report-actions {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
  }

  .azb-report-filter-form {
    margin-bottom: -18px;
    display: flex;
    flex-wrap: wrap;
    gap: 0 12px;
  }

  .azb-report-filter-form :deep(.el-form-item) {
    margin-right: 0;
  }

  .azb-report-section-title {
    display: flex;
    align-items: flex-start;
    gap: 12px;
    margin-bottom: 16px;
  }

  .azb-report-section-title__bar {
    width: 4px;
    min-height: 44px;
    border-radius: 999px;
    background: linear-gradient(180deg, #0b6b78 0%, #d7563f 100%);
  }

  .azb-report-section-title h3 {
    margin: 0;
    color: #12333c;
    font-size: 22px;
  }

  .azb-report-section-title p {
    margin: 6px 0 0;
    color: #58707b;
    font-size: 13px;
    line-height: 1.7;
  }

  .azb-overview-hero {
    display: grid;
    grid-template-columns: minmax(0, 1.55fr) minmax(0, 1fr);
    gap: 18px;
    margin-bottom: 18px;
    min-width: 0;
  }

  .azb-overview-hero__main {
    padding: 22px 24px;
    border-radius: 18px;
    background: linear-gradient(135deg, #0b3f49 0%, #0b6b78 62%, #eef7f6 62%, #f8fbfb 100%);
    color: #fff;
    min-width: 0;
  }

  .azb-overview-hero__eyebrow {
    color: rgba(255, 255, 255, 0.74);
    font-size: 12px;
    text-transform: uppercase;
  }

  .azb-overview-hero__scoreline {
    display: flex;
    flex-wrap: wrap;
    gap: 18px;
    align-items: flex-end;
    margin-top: 12px;
  }

  .azb-overview-hero__score {
    font-size: 54px;
    line-height: 1;
    font-weight: 700;
  }

  .azb-overview-hero__meta {
    display: grid;
    gap: 10px;
    min-width: 0;
    flex: 1 1 220px;
  }

  .azb-overview-hero__risk,
  .azb-overview-hero__submeta {
    display: flex;
    flex-wrap: wrap;
    gap: 10px 16px;
    align-items: center;
    color: rgba(255, 255, 255, 0.9);
    font-size: 14px;
    line-height: 1.75;
  }

  .azb-overview-hero__submeta {
    color: rgba(255, 255, 255, 0.78);
    font-size: 12px;
  }

  .azb-overview-hero__aside {
    display: grid;
    gap: 12px;
    min-width: 0;
  }

  .azb-overview-digest,
  .azb-score-card,
  .azb-chart-card,
  .azb-ranking-summary-strip__item {
    border: 1px solid #dfe9ed;
    border-radius: 16px;
    background: #f7fafb;
    min-width: 0;
  }

  .azb-overview-digest {
    display: grid;
    gap: 4px;
    padding: 16px 18px;
  }

  .azb-overview-digest__label,
  .azb-ranking-summary-strip__label {
    color: #66808a;
    font-size: 12px;
  }

  .azb-overview-digest__value {
    color: #12333c;
    font-size: 22px;
    font-weight: 700;
    word-break: break-word;
  }

  .azb-overview-digest__hint,
  .azb-ranking-summary-strip__hint {
    color: #7a9099;
    font-size: 12px;
  }

  .azb-score-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));
    gap: 16px;
    min-width: 0;
  }

  .azb-score-card {
    padding: 18px;
  }

  .azb-score-card__value {
    color: #12333c;
    font-size: 30px;
    font-weight: 700;
    word-break: break-word;
  }

  .azb-score-card__label {
    margin-top: 8px;
    color: #45626b;
    font-size: 14px;
  }

  .azb-score-card__trend {
    margin-top: 8px;
    color: #6b828b;
    font-size: 12px;
    line-height: 1.6;
  }

  .azb-overview-tags {
    display: flex;
    gap: 10px;
    flex-wrap: wrap;
    margin-top: 16px;
  }

  .azb-overview-tags__item {
    display: inline-flex;
    align-items: center;
    padding: 7px 12px;
    border-radius: 999px;
    border: 1px solid #dfe9ed;
    background: #f4f8f9;
    color: #45626b;
    font-size: 12px;
  }

  .azb-ai-conclusion-body {
    border-left: 6px solid #0b6b78;
    border-radius: 16px;
    background: #f2faf9;
    padding: 18px 20px;
    color: #48656e;
    line-height: 1.85;
  }

  .azb-chart-section,
  .azb-ranking-grid {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 16px;
  }

  .azb-chart-card {
    padding: 18px 20px;
  }

  .azb-chart-card__title,
  .azb-ranking-panel__title {
    color: #12333c;
    font-size: 16px;
    font-weight: 700;
  }

  .azb-chart-box {
    width: 100%;
    min-height: 360px;
    height: 360px;
  }

  .azb-ranking-summary-strip {
    display: grid;
    grid-template-columns: repeat(3, minmax(0, 1fr));
    gap: 14px;
    margin-bottom: 18px;
  }

  .azb-ranking-summary-strip__item {
    display: grid;
    gap: 5px;
    padding: 14px 16px;
  }

  .azb-ranking-summary-strip__value {
    color: #12333c;
    font-size: 18px;
    font-weight: 700;
    word-break: break-word;
  }

  .azb-ranking-panel {
    border: 1px solid #dfe9ed;
    border-radius: 18px;
    background: #fbfdfd;
  }

  .azb-ranking-panel__desc {
    margin-top: 6px;
    color: #6b828b;
    font-size: 12px;
    line-height: 1.7;
  }

  .azb-risk-focus-grid {
    display: grid;
    grid-template-columns: repeat(3, minmax(0, 1fr));
    gap: 14px;
    margin-bottom: 18px;
  }

  .azb-risk-focus-card {
    padding: 16px 18px;
    border: 1px solid #f1d4cf;
    border-radius: 18px;
    background: linear-gradient(180deg, #fff7f5 0%, #fffdfc 100%);
  }

  .azb-risk-focus-card__head {
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 12px;
  }

  .azb-risk-focus-card__badge {
    display: inline-flex;
    align-items: center;
    padding: 4px 10px;
    border-radius: 999px;
    background: #feeceb;
    color: #b42318;
    font-size: 12px;
    font-weight: 600;
  }

  .azb-risk-focus-card__score {
    color: #b42318;
    font-size: 24px;
    font-weight: 700;
  }

  .azb-risk-focus-card h4 {
    margin: 14px 0 8px;
    color: #12333c;
    font-size: 16px;
  }

  .azb-risk-focus-card p {
    margin: 0;
    color: #58707b;
    font-size: 13px;
    line-height: 1.75;
  }

  .azb-risk-focus-card__foot {
    margin-top: 12px;
    padding-top: 10px;
    border-top: 1px dashed #ead0cd;
    color: #7b5b57;
    font-size: 12px;
    line-height: 1.7;
  }

  :deep(.el-table .is-dashboard-active) {
    --el-table-tr-bg-color: #f5fbfb;
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
    .azb-ai-report-layout {
      grid-template-columns: minmax(0, 1fr);
    }

    .azb-section-nav {
      position: static;
      display: grid;
      grid-template-columns: repeat(4, minmax(0, 1fr));
      gap: 10px;
    }

    .azb-section-nav__title {
      grid-column: 1 / -1;
    }

    .azb-overview-hero {
      grid-template-columns: minmax(0, 1fr);
    }

    .azb-overview-hero__aside {
      grid-template-columns: repeat(3, minmax(0, 1fr));
    }

    .azb-summary-grid,
    .azb-focus-grid,
    .azb-chart-grid {
      grid-template-columns: repeat(2, minmax(0, 1fr));
    }
  }

  @media (max-width: 1024px) {
    .azb-overview-hero__aside,
    .azb-chart-section,
    .azb-ranking-grid,
    .azb-ranking-summary-strip,
    .azb-risk-focus-grid,
    .azb-score-grid {
      grid-template-columns: 1fr;
    }
  }

  @media (max-width: 768px) {
    .azb-report-card__head,
    .azb-report-card__head--table {
      display: block;
    }

    .azb-report-actions {
      margin-top: 12px;
    }

    .azb-section-nav {
      grid-template-columns: repeat(2, minmax(0, 1fr));
    }

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

