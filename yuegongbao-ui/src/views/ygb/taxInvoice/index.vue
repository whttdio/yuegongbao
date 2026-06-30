<template>
  <business-record-page :config="config" />
</template>

<script setup>
import BusinessRecordPage from '@/views/ygb/shared/BusinessRecordPage.vue'
import { createBusinessRecordPageConfig } from '@/views/ygb/shared/businessRecordPageConfig.js'

const config = createBusinessRecordPageConfig({
  module: 'taxInvoice',
  title: '发票比对',
  description: '围绕开票金额与工资总额偏离分析开展查询、办理、处置跟踪和台账导出。',
  businessNameLabel: '发票比对事项',
  businessNamePlaceholder: '请输入发票比对事项',
  defaultSourceLabel: '发票比对',
  filters: ['statMonth', 'regionCode', 'enterpriseId', 'enterpriseName', 'personName', 'businessName', 'riskLevel', 'workflowStatus', 'status']
})
</script>
