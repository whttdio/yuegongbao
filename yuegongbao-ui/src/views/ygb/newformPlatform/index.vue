<template>
  <grouped-aggregation-page :config="config" />
</template>

<script setup>
import GroupedAggregationPage from '@/views/ygb/shared/GroupedAggregationPage.vue'
import { getNewformPlatformSummary, listNewformPlatform } from '@/api/ygb/newformWorker'

const config = {
  title: '平台企业聚合',
  eyebrow: '新业态子对象',
  description: '按月汇总新业态平台企业的覆盖、参保、职业伤害参保与预警情况，用同一口径服务监管核对、企业复盘和月度导出。',
  signalTitle: '平台维度总览',
  tip: '直接复用 t_newform_worker 主台账，不额外建设第二套聚合表，仅做平台维度汇总、展示与导出。',
  focusTitle: '重点看什么',
  focusDescription: '这个页面更适合先判断平台覆盖和参保密度，再决定是否下钻主台账核查具体对象。',
  focusBullets: [
    { label: '先看平台覆盖', desc: '平台数和人员数用于确认当前月份的样本规模是否完整。' },
    { label: '再看参保结构', desc: '已参保与职业伤害已参保用于识别平台保障口径是否稳定。' },
    { label: '最后看预警密度', desc: '预警人数更适合作为回到主台账继续下钻的入口。' }
  ],
  tableTitle: '平台聚合结果',
  tableDescription: '列表按月份、区域、平台企业聚合展示当前筛选条件下的台账结果。',
  permPrefix: 'ygb:newformPlatform',
  filePrefix: 'newform_platform',
  filters: ['statMonth', 'regionCode', 'enterpriseId', 'platformName'],
  routeQueryFields: ['statMonth', 'regionCode', 'enterpriseId', 'platformName', 'warningStatus', 'injuryInsuranceStatus'],
  workbenchFields: ['statMonth', 'regionCode', 'enterpriseId', 'platformName', 'warningStatus', 'injuryInsuranceStatus'],
  workbenchTitle: '当前平台企业聚合页沿用了上游筛选条件',
  workbenchDescription: '来自新业态主台账或风险队列的条件会继续保留，便于按同一口径复核平台覆盖和参保情况。',
  workbenchLabels: {
    statMonth: '统计月份',
    regionCode: '区域',
    enterpriseId: '企业',
    platformName: '平台企业',
    warningStatus: '预警状态',
    injuryInsuranceStatus: '职业伤害参保'
  },
  listApi: query => listNewformPlatform(query),
  summaryApi: query => getNewformPlatformSummary(query),
  exportUrl: 'ygb/newform/platform/export',
  columns: [
    { label: '统计月份', prop: 'statMonth', width: 110 },
    { label: '区域', prop: 'regionCode', minWidth: 140, type: 'region' },
    { label: '平台企业', prop: 'platformName', minWidth: 220, showOverflowTooltip: true },
    { label: '人员数', prop: 'workerCount', width: 100 },
    { label: '已参保人数', prop: 'insuredCount', width: 120 },
    { label: '职业伤害已参保', prop: 'injuryInsuredCount', width: 140 },
    { label: '预警人数', prop: 'warningCount', width: 110 },
    { label: '平均收入', prop: 'averageIncome', width: 120, type: 'money' }
  ],
  summaryCards: summary => ([
    { key: 'platformCount', topline: '覆盖面', label: '平台数', value: summary.platformCount ?? 0, unit: '个' },
    { key: 'workerCount', topline: '样本规模', label: '人员数', value: summary.workerCount ?? 0, unit: '人' },
    { key: 'insuredCount', topline: '基础参保', label: '已参保人数', value: summary.insuredCount ?? 0, unit: '人', cardClass: 'ygb-summary-card--success', note: '用于确认平台基础参保覆盖是否连续。' },
    { key: 'injuryInsuredCount', topline: '职业伤害', label: '职业伤害已参保', value: summary.injuryInsuredCount ?? 0, unit: '人', cardClass: 'ygb-summary-card--primary', note: '更适合作为职业伤害保障口径的对照值。' },
    { key: 'warningCount', topline: '异常对象', label: '预警人数', value: summary.warningCount ?? 0, unit: '人', cardClass: 'ygb-summary-card--warning', note: '预警越高，越值得回到主台账核查。' },
    { key: 'averageIncome', topline: '收入口径', label: '平均收入', value: summary.averageIncome ?? 0, unit: '元', note: '反映当前样本收入水平和口径稳定度。' }
  ])
}
</script>
