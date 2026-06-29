<template>
  <business-record-page :config="config" />
</template>

<script setup>
import BusinessRecordPage from '@/views/ygb/shared/BusinessRecordPage.vue'
import { createBusinessRecordPageConfig } from '@/views/ygb/shared/businessRecordPageConfig.js'

const config = createBusinessRecordPageConfig({
  module: 'occupationHealthArchive',
  title: '职业健康档案',
  description: '围绕体检健康档案、复查提醒和处置记录维护开展查询、办理、处置跟踪和台账导出。',
  businessNameLabel: '健康档案',
  businessNamePlaceholder: '请输入健康档案',
  defaultSourceLabel: '职业健康档案',
  filters: ['statMonth', 'regionCode', 'enterpriseId', 'enterpriseName', 'personName', 'businessName', 'riskLevel', 'workflowStatus', 'status']
})
</script>
