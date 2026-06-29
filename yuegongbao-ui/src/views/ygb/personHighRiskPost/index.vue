<template>
  <business-record-page :config="config" />
</template>

<script setup>
import BusinessRecordPage from '@/views/ygb/shared/BusinessRecordPage.vue'
import { createBusinessRecordPageConfig } from '@/views/ygb/shared/businessRecordPageConfig.js'

const config = createBusinessRecordPageConfig({
  module: 'personHighRiskPost',
  title: '高危岗位库',
  description: '围绕高危岗位台账、风险等级和人员配置维护开展查询、办理、处置跟踪和台账导出。',
  businessNameLabel: '高危岗位',
  businessNamePlaceholder: '请输入高危岗位',
  defaultSourceLabel: '高危岗位库',
  filters: ['statMonth', 'regionCode', 'enterpriseId', 'enterpriseName', 'personName', 'businessName', 'riskLevel', 'workflowStatus', 'status']
})
</script>
