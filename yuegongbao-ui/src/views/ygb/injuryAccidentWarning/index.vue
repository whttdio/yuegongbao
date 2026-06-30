<template>
  <business-record-page :config="config" />
</template>

<script setup>
import BusinessRecordPage from '@/views/ygb/shared/BusinessRecordPage.vue'
import { createBusinessRecordPageConfig } from '@/views/ygb/shared/businessRecordPageConfig.js'

const config = createBusinessRecordPageConfig({
  module: 'injuryAccidentWarning',
  title: '工伤事故预警',
  description: '围绕工伤事故风险预警和处置跟踪开展查询、办理、处置跟踪和台账导出。',
  businessNameLabel: '预警事项',
  businessNamePlaceholder: '请输入预警事项',
  defaultSourceLabel: '工伤事故预警',
  filters: ['statMonth', 'regionCode', 'enterpriseId', 'enterpriseName', 'personName', 'businessName', 'riskLevel', 'workflowStatus', 'status']
})
</script>
