<template>
  <business-record-page :config="config" />
</template>

<script setup>
import BusinessRecordPage from '@/views/ygb/shared/BusinessRecordPage.vue'
import { createBusinessRecordPageConfig } from '@/views/ygb/shared/businessRecordPageConfig.js'

const config = createBusinessRecordPageConfig({
  module: 'injuryRegionMonitor',
  title: '区域工伤监测',
  description: '围绕区域工伤风险分布和趋势监测开展查询、办理、处置跟踪和台账导出。',
  businessNameLabel: '区域监测',
  businessNamePlaceholder: '请输入区域监测',
  defaultSourceLabel: '区域工伤监测',
  filters: ['statMonth', 'regionCode', 'enterpriseId', 'enterpriseName', 'personName', 'businessName', 'riskLevel', 'workflowStatus', 'status']
})
</script>
