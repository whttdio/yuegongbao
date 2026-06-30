<template>
  <business-record-page :config="config" />
</template>

<script setup>
import BusinessRecordPage from '@/views/ygb/shared/BusinessRecordPage.vue'
import { createBusinessRecordPageConfig } from '@/views/ygb/shared/businessRecordPageConfig.js'

const config = createBusinessRecordPageConfig({
  module: 'creditSanction',
  title: '联合惩戒推送',
  description: '围绕失信企业联合惩戒推送和反馈开展查询、办理、处置跟踪和台账导出。',
  businessNameLabel: '惩戒事项',
  businessNamePlaceholder: '请输入惩戒事项',
  defaultSourceLabel: '联合惩戒推送',
  filters: ['statMonth', 'regionCode', 'enterpriseId', 'enterpriseName', 'personName', 'businessName', 'riskLevel', 'workflowStatus', 'status']
})
</script>
