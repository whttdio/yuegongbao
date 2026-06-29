<template>
  <business-record-page :config="config" />
</template>

<script setup>
import BusinessRecordPage from '@/views/ygb/shared/BusinessRecordPage.vue'
import { createBusinessRecordPageConfig } from '@/views/ygb/shared/businessRecordPageConfig.js'

const config = createBusinessRecordPageConfig({
  module: 'enterpriseHighRisk',
  title: '高危企业库管理',
  description: '围绕高危企业库、风险等级和监管措施维护开展查询、办理、处置跟踪和台账导出。',
  businessNameLabel: '高危企业',
  businessNamePlaceholder: '请输入高危企业',
  defaultSourceLabel: '高危企业库管理',
  filters: ['statMonth', 'regionCode', 'enterpriseId', 'enterpriseName', 'personName', 'businessName', 'riskLevel', 'workflowStatus', 'status']
})
</script>
