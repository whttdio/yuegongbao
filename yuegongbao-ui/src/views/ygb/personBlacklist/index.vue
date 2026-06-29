<template>
  <business-record-page :config="config" />
</template>

<script setup>
import BusinessRecordPage from '@/views/ygb/shared/BusinessRecordPage.vue'
import { createBusinessRecordPageConfig } from '@/views/ygb/shared/businessRecordPageConfig.js'

const config = createBusinessRecordPageConfig({
  module: 'personBlacklist',
  title: '黑名单管理',
  description: '围绕失信人员黑名单管控、移入移出和处置留痕开展查询、办理、处置跟踪和台账导出。',
  businessNameLabel: '黑名单事项',
  businessNamePlaceholder: '请输入黑名单事项',
  defaultSourceLabel: '黑名单管理',
  filters: ['statMonth', 'regionCode', 'enterpriseId', 'enterpriseName', 'personName', 'businessName', 'riskLevel', 'workflowStatus', 'status']
})
</script>
