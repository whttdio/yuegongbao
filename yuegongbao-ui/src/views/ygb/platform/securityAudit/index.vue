<template>
  <business-record-page :config="config" />
</template>

<script setup>
import BusinessRecordPage from '@/views/ygb/shared/BusinessRecordPage.vue'
import { createBusinessRecordPageConfig } from '@/views/ygb/shared/businessRecordPageConfig.js'

const config = createBusinessRecordPageConfig({
  module: 'platformSecurityAudit',
  title: '安全审计',
  description: '围绕操作日志、安全事件和整改结果审计开展查询、办理、处置跟踪和台账导出。',
  businessNameLabel: '审计事项',
  businessNamePlaceholder: '请输入审计事项',
  defaultSourceLabel: '安全审计',
  filters: ['statMonth', 'regionCode', 'enterpriseId', 'enterpriseName', 'personName', 'businessName', 'riskLevel', 'workflowStatus', 'status']
})
</script>
