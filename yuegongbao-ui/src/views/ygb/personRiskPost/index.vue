<template>
  <business-record-page :config="config" />
</template>

<script setup>
import BusinessRecordPage from '@/views/ygb/shared/BusinessRecordPage.vue'
import { createBusinessRecordPageConfig } from '@/views/ygb/shared/businessRecordPageConfig.js'

const config = createBusinessRecordPageConfig({
  module: 'personRiskPost',
  title: '风险岗位库',
  description: '围绕风险岗位台账和管控措施维护开展查询、办理、处置跟踪和台账导出。',
  businessNameLabel: '风险岗位',
  businessNamePlaceholder: '请输入风险岗位',
  defaultSourceLabel: '风险岗位库',
  filters: ['statMonth', 'regionCode', 'enterpriseId', 'enterpriseName', 'personName', 'businessName', 'riskLevel', 'workflowStatus', 'status']
})
</script>
