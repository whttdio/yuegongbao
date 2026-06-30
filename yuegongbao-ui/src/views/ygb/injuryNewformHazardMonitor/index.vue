<template>
  <business-record-page :config="config" />
</template>

<script setup>
import BusinessRecordPage from '@/views/ygb/shared/BusinessRecordPage.vue'
import { createBusinessRecordPageConfig } from '@/views/ygb/shared/businessRecordPageConfig.js'

const config = createBusinessRecordPageConfig({
  module: 'injuryNewformHazardMonitor',
  title: '新业态伤害监测',
  description: '围绕新业态从业人员职业伤害监测开展查询、办理、处置跟踪和台账导出。',
  businessNameLabel: '监测对象',
  businessNamePlaceholder: '请输入监测对象',
  defaultSourceLabel: '新业态伤害监测',
  filters: ['statMonth', 'regionCode', 'enterpriseId', 'enterpriseName', 'personName', 'businessName', 'riskLevel', 'workflowStatus', 'status']
})
</script>
