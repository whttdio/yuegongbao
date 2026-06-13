<template>
  <grouped-aggregation-page :config="config" />
</template>

<script setup>
import GroupedAggregationPage from '@/views/ygb/shared/GroupedAggregationPage.vue'
import { getNewformInjuryMonitorSummary, listNewformInjuryMonitor } from '@/api/ygb/newformWorker'

const config = {
  title: '职业伤害监测',
  eyebrow: '新业态子对象',
  description: '按平台企业聚合职业伤害已参保、未参保、停保和预警对象，先看监测面，再回到主台账核实具体异常来源。',
  signalTitle: '职业伤害监测口径',
  tip: '继续复用 t_newform_worker 主台账聚合，不另建监测主表，重点承接平台维度的职业伤害参保异常核查。',
  focusTitle: '监测焦点',
  focusDescription: '这个页面更偏监测视角，核心不是看平台规模，而是识别哪些平台的职业伤害保障结构失衡。',
  focusBullets: [
    { label: '已参保看底座', desc: '职业伤害已参保人数决定当前平台保障底座是否稳定。' },
    { label: '未参保看缺口', desc: '未参保对象更适合作为回到主台账继续核查的重点。' },
    { label: '停保和预警看风险', desc: '停保与预警一起看，能更快判断当前平台是否存在连续性风险。' }
  ],
  tableTitle: '职业伤害聚合结果',
  tableDescription: '列表按平台企业展示职业伤害参保结构和预警监测结果。',
  permPrefix: 'ygb:newformInjuryMonitor',
  filePrefix: 'newform_injury_monitor',
  filters: ['statMonth', 'regionCode', 'enterpriseId', 'platformName'],
  routeQueryFields: ['statMonth', 'regionCode', 'enterpriseId', 'platformName', 'warningStatus', 'injuryInsuranceStatus'],
  workbenchFields: ['statMonth', 'regionCode', 'enterpriseId', 'platformName', 'warningStatus', 'injuryInsuranceStatus'],
  workbenchTitle: '当前职业伤害监测页沿用了上游筛选条件',
  workbenchDescription: '从新业态主台账或风险队列进入时，会保留月份、企业、平台和状态条件，便于直接核对异常平台。',
  workbenchLabels: {
    statMonth: '统计月份',
    regionCode: '区域',
    enterpriseId: '企业',
    platformName: '平台企业',
    warningStatus: '预警状态',
    injuryInsuranceStatus: '职业伤害参保'
  },
  listApi: query => listNewformInjuryMonitor(query),
  summaryApi: query => getNewformInjuryMonitorSummary(query),
  exportUrl: 'ygb/newform/injuryMonitor/export',
  columns: [
    { label: '统计月份', prop: 'statMonth', width: 110 },
    { label: '区域', prop: 'regionCode', minWidth: 140, type: 'region' },
    { label: '平台企业', prop: 'platformName', minWidth: 220, showOverflowTooltip: true },
    { label: '人员数', prop: 'workerCount', width: 100 },
    { label: '职业伤害已参保', prop: 'injuryInsuredCount', width: 140 },
    { label: '职业伤害未参保', prop: 'injuryUninsuredCount', width: 140 },
    { label: '职业伤害停保', prop: 'injuryStoppedCount', width: 140 },
    { label: '预警人数', prop: 'warningCount', width: 110 }
  ],
  summaryCards: summary => ([
    { key: 'platformCount', topline: '监测范围', label: '平台数', value: summary.platformCount ?? 0, unit: '个' },
    { key: 'workerCount', topline: '样本规模', label: '人员数', value: summary.workerCount ?? 0, unit: '人' },
    { key: 'injuryInsuredCount', topline: '稳定保障', label: '职业伤害已参保', value: summary.injuryInsuredCount ?? 0, unit: '人', cardClass: 'ygb-summary-card--success', note: '用于判断平台职业伤害保障底座是否稳定。' },
    { key: 'injuryUninsuredCount', topline: '保障缺口', label: '职业伤害未参保', value: summary.injuryUninsuredCount ?? 0, unit: '人', cardClass: 'ygb-summary-card--warning', note: '越高越说明平台存在明显保障缺口。' },
    { key: 'injuryStoppedCount', topline: '连续性风险', label: '职业伤害停保', value: summary.injuryStoppedCount ?? 0, unit: '人', cardClass: 'ygb-summary-card--primary', note: '停保对象更值得回到主台账追踪原因。' },
    { key: 'warningCount', topline: '预警联动', label: '预警人数', value: summary.warningCount ?? 0, unit: '人', note: '与停保、未参保一起看更适合识别重点平台。' }
  ])
}
</script>
