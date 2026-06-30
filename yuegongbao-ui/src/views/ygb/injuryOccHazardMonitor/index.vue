<template>
  <business-record-page :config="config" />
</template>

<script setup>
import BusinessRecordPage from '@/views/ygb/shared/BusinessRecordPage.vue'
import { createBusinessRecordPageConfig } from '@/views/ygb/shared/businessRecordPageConfig.js'

const config = createBusinessRecordPageConfig({
  module: 'injuryOccHazardMonitor',
  title: '职业病伤害监测',
  description: '围绕职业病相关工伤风险和处置跟踪开展查询、办理、处置跟踪和台账导出。',
  businessNameLabel: '监测对象',
  businessNamePlaceholder: '请输入监测对象',
  defaultSourceLabel: '职业病伤害监测',
  filters: ['statMonth', 'regionCode', 'enterpriseId', 'enterpriseName', 'personName', 'businessName', 'riskLevel', 'workflowStatus', 'status']
})
</script>
