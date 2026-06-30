<template>
  <business-record-page :config="config" />
</template>

<script setup>
import BusinessRecordPage from '@/views/ygb/shared/BusinessRecordPage.vue'
import { createBusinessRecordPageConfig } from '@/views/ygb/shared/businessRecordPageConfig.js'

const config = createBusinessRecordPageConfig({
  module: 'unionLegalAid',
  title: '工会法律援助',
  description: '围绕工会法律援助申请和处理跟踪开展查询、办理、处置跟踪和台账导出。',
  businessNameLabel: '援助事项',
  businessNamePlaceholder: '请输入援助事项',
  defaultSourceLabel: '工会法律援助',
  filters: ['statMonth', 'regionCode', 'enterpriseId', 'enterpriseName', 'personName', 'businessName', 'riskLevel', 'workflowStatus', 'status']
})
</script>
