<template>
  <business-record-page :config="config" />
</template>

<script setup>
import BusinessRecordPage from '@/views/ygb/shared/BusinessRecordPage.vue'
import { createBusinessRecordPageConfig } from '@/views/ygb/shared/businessRecordPageConfig.js'

const config = createBusinessRecordPageConfig({
  module: 'operationMaintenanceStats',
  title: '运维统计',
  description: '围绕设备运维工单和处理效率统计开展查询、办理、处置跟踪和台账导出。',
  businessNameLabel: '运维指标',
  businessNamePlaceholder: '请输入运维指标',
  defaultSourceLabel: '运维统计',
  filters: ['statMonth', 'regionCode', 'enterpriseId', 'enterpriseName', 'personName', 'businessName', 'riskLevel', 'workflowStatus', 'status']
})
</script>
