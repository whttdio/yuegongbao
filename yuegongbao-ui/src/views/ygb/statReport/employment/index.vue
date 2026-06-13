<template>
  <TypedStatReportPage :config="pageConfig" />
</template>

<script setup name="YgbStatReportEmployment">
import TypedStatReportPage from '@/views/statReport/TypedStatReportPage.vue'

const pageConfig = {
  portalCode: 'ygb',
  typeKey: 'employment',
  reportCode: 'EMPLOYMENT',
  title: '用工月报',
  description: '按用工比例和企业对象查看主链用工口径。',
  eyebrow: '统计月报',
  surfaceTitle: '用工保障月报台账',
  portalDescription: '按主链对象输出用工月报。',
  primaryText: '#0f5ea8',
  accent: 'rgba(15, 94, 168, 0.10)',
  soft: '#f4f8fc',
  queryFields: [
    { kind: 'text', key: 'keyword', label: '企业关键字', placeholder: '企业名称' },
    { kind: 'range', label: '用工人数', minKey: 'minMetricCount', maxKey: 'maxMetricCount' }
  ],
  tableColumns: [
    { key: 'statMonth', label: '统计月份', prop: 'statMonth', width: 110 },
    { key: 'reportName', label: '月报名称', prop: 'reportName', minWidth: 180 },
    { key: 'metricCount', label: '用工人数', prop: 'metricCount', width: 110 },
    { key: 'metricAmount', label: '正式工人数', prop: 'metricAmount', width: 120 },
    { key: 'metricRate', label: '比例', prop: 'metricRate', width: 110 },
    { key: 'reportStatus', label: '状态', prop: 'reportStatus', width: 100, type: 'status' },
    { key: 'generatedTime', label: '生成时间', width: 180, formatter: (row, helpers) => row.generatedTime ? helpers.parseTime(row.generatedTime, '{y}-{m}-{d} {h}:{i}:{s}') : '-' }
  ],
  summaryMetrics: [
    { key: 'metricCount', label: '用工人数', unit: '人' },
    { key: 'metricAmount', label: '正式工人数', unit: '人' },
    { key: 'metricRate', label: '平均比例', unit: '%', isRate: true }
  ],
  permissionPrefix: 'ygb:statReport:employment',
  printTitle: '用工保障月报台账 - 用工月报',
  routePath: '/ygb-report/statReport/employment'
}
</script>
