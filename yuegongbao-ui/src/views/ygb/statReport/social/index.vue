<template>
  <TypedStatReportPage :config="pageConfig" />
</template>

<script setup name="YgbStatReportSocial">
import TypedStatReportPage from '@/views/statReport/TypedStatReportPage.vue'

const pageConfig = {
  portalCode: 'ygb',
  typeKey: 'social',
  reportCode: 'SOCIAL',
  title: '社保月报',
  description: '按社保缴费和异常口径输出月报。',
  eyebrow: '统计月报',
  surfaceTitle: '用工保障月报台账',
  portalDescription: '按主链对象输出社保月报。',
  primaryText: '#0f5ea8',
  accent: 'rgba(15, 94, 168, 0.10)',
  soft: '#f4f8fc',
  queryFields: [
    { kind: 'text', key: 'keyword', label: '企业关键字', placeholder: '企业名称' },
    { kind: 'range', label: '缴费人数', minKey: 'minMetricCount', maxKey: 'maxMetricCount' }
  ],
  tableColumns: [
    { key: 'statMonth', label: '统计月份', prop: 'statMonth', width: 110 },
    { key: 'reportName', label: '月报名称', prop: 'reportName', minWidth: 180 },
    { key: 'metricCount', label: '缴费人数', prop: 'metricCount', width: 110 },
    { key: 'metricAmount', label: '缴费金额', prop: 'metricAmount', width: 120 },
    { key: 'metricRate', label: '正常率', prop: 'metricRate', width: 110 },
    { key: 'reportStatus', label: '状态', prop: 'reportStatus', width: 100, type: 'status' },
    { key: 'generatedTime', label: '生成时间', width: 180, formatter: (row, helpers) => row.generatedTime ? helpers.parseTime(row.generatedTime, '{y}-{m}-{d} {h}:{i}:{s}') : '-' }
  ],
  summaryMetrics: [
    { key: 'metricCount', label: '缴费人数', unit: '人' },
    { key: 'metricAmount', label: '缴费金额', unit: '元' },
    { key: 'metricRate', label: '正常率', unit: '%', isRate: true }
  ],
  permissionPrefix: 'ygb:statReport:social',
  printTitle: '用工保障月报台账 - 社保月报',
  routePath: '/ygb-report/statReport/social'
}
</script>
