<template>
  <business-record-page :config="config" />
</template>

<script setup>
import BusinessRecordPage from '@/views/ygb/shared/BusinessRecordPage.vue'
import { createBusinessRecordPageConfig } from '@/views/ygb/shared/businessRecordPageConfig.js'

const config = createBusinessRecordPageConfig({
  module: 'deviceInspectPlan',
  title: '设备巡检计划',
  description: '围绕设备巡检计划、执行和结果跟踪开展查询、办理、处置跟踪和台账导出。',
  businessNameLabel: '巡检计划',
  businessNamePlaceholder: '请输入巡检计划',
  defaultSourceLabel: '设备巡检计划',
  filters: ['statMonth', 'regionCode', 'enterpriseId', 'enterpriseName', 'personName', 'businessName', 'riskLevel', 'workflowStatus', 'status']
})
</script>
