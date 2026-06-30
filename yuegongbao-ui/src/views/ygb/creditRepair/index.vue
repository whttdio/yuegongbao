<template>
  <business-record-page :config="config" />
</template>

<script setup>
import BusinessRecordPage from '@/views/ygb/shared/BusinessRecordPage.vue'
import { createBusinessRecordPageConfig } from '@/views/ygb/shared/businessRecordPageConfig.js'

const config = createBusinessRecordPageConfig({
  module: 'creditRepair',
  title: '信用修复申请',
  description: '围绕企业信用修复申请、审核和归档开展查询、办理、处置跟踪和台账导出。',
  businessNameLabel: '修复申请',
  businessNamePlaceholder: '请输入修复申请',
  defaultSourceLabel: '信用修复申请',
  filters: ['statMonth', 'regionCode', 'enterpriseId', 'enterpriseName', 'personName', 'businessName', 'riskLevel', 'workflowStatus', 'status']
})
</script>
