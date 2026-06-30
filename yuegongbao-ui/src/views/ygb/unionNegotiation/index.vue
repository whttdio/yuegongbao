<template>
  <business-record-page :config="config" />
</template>

<script setup>
import BusinessRecordPage from '@/views/ygb/shared/BusinessRecordPage.vue'
import { createBusinessRecordPageConfig } from '@/views/ygb/shared/businessRecordPageConfig.js'

const config = createBusinessRecordPageConfig({
  module: 'unionNegotiation',
  title: '集体协商',
  description: '围绕集体协商事项和协议履行跟踪开展查询、办理、处置跟踪和台账导出。',
  businessNameLabel: '协商事项',
  businessNamePlaceholder: '请输入协商事项',
  defaultSourceLabel: '集体协商',
  filters: ['statMonth', 'regionCode', 'enterpriseId', 'enterpriseName', 'personName', 'businessName', 'riskLevel', 'workflowStatus', 'status']
})
</script>
