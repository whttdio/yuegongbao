<template>
  <business-record-page :config="config" />
</template>

<script setup>
import BusinessRecordPage from '@/views/ygb/shared/BusinessRecordPage.vue'
import { createBusinessRecordPageConfig } from '@/views/ygb/shared/businessRecordPageConfig.js'

const config = createBusinessRecordPageConfig({
  module: 'preventionAi',
  title: '工伤预防AI管控',
  description: '围绕工伤预防AI管控和风险识别开展查询、办理、处置跟踪和台账导出。',
  businessNameLabel: 'AI管控事项',
  businessNamePlaceholder: '请输入AI管控事项',
  defaultSourceLabel: '工伤预防AI管控',
  filters: ['statMonth', 'regionCode', 'enterpriseId', 'enterpriseName', 'personName', 'businessName', 'riskLevel', 'workflowStatus', 'status']
})
</script>
