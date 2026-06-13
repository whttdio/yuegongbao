<template>
  <TypedStatReportPage :config="pageConfig" />
</template>

<script setup name="YgbStatReportTax">
import TypedStatReportPage from '@/views/statReport/TypedStatReportPage.vue'

const pageConfig = {
  portalCode: 'ygb',
  typeKey: 'tax',
  reportCode: 'TAX',
  title: '税务月报',
  description: '按税务比对和异常差额输出月报。',
  eyebrow: '统计月报',
  surfaceTitle: '用工保障月报台账',
  portalDescription: '按主链对象输出税务月报。',
  primaryText: '#0f5ea8',
  accent: 'rgba(15, 94, 168, 0.10)',
  soft: '#f4f8fc',
  queryFields: [
    { kind: 'text', key: 'keyword', label: '企业关键字', placeholder: '企业名称' },
    { kind: 'range', label: '比对人数', minKey: 'minMetricCount', maxKey: 'maxMetricCount' }
  ],
  tableColumns: [
    { key: 'statMonth', label: '统计月份', prop: 'statMonth', width: 110 },
    { key: 'reportName', label: '月报名称', prop: 'reportName', minWidth: 180 },
    { key: 'metricCount', label: '比对人数', prop: 'metricCount', width: 110 },
    { key: 'metricAmount', label: '差异金额', prop: 'metricAmount', width: 120 },
    { key: 'metricRate', label: '异常率', prop: 'metricRate', width: 110 },
    { key: 'reportStatus', label: '状态', prop: 'reportStatus', width: 100, type: 'status' },
    { key: 'generatedTime', label: '生成时间', width: 180, formatter: (row, helpers) => row.generatedTime ? helpers.parseTime(row.generatedTime, '{y}-{m}-{d} {h}:{i}:{s}') : '-' }
  ],
  summaryMetrics: [
    { key: 'metricCount', label: '比对人数', unit: '人' },
    { key: 'metricAmount', label: '差异金额', unit: '元' },
    { key: 'metricRate', label: '异常率', unit: '%', isRate: true }
  ],
  permissionPrefix: 'ygb:statReport:tax',
  printTitle: '用工保障月报台账 - 税务月报',
  routePath: '/ygb-report/statReport/tax'
}
</script>
