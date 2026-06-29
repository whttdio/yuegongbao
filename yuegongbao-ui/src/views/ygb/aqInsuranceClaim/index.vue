<template>
  <business-record-page :config="config" />
</template>

<script setup>
import BusinessRecordPage from '@/views/ygb/shared/BusinessRecordPage.vue'
import { createBusinessRecordPageConfig } from '@/views/ygb/shared/businessRecordPageConfig.js'

const config = createBusinessRecordPageConfig({
  module: 'aqInsuranceClaim',
  title: '赔付率监控',
  description: '围绕行业企业赔付率统计和异常赔付风险开展查询、办理、处置跟踪和台账导出。',
  businessNameLabel: '赔付监控事项',
  businessNamePlaceholder: '请输入赔付监控事项',
  defaultSourceLabel: '赔付率监控',
  filters: ['statMonth', 'regionCode', 'enterpriseId', 'enterpriseName', 'personName', 'businessName', 'riskLevel', 'workflowStatus', 'status']
})
</script>
