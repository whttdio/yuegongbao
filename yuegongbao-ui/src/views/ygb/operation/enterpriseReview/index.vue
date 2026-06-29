<template>
  <business-record-page :config="config" />
</template>

<script setup>
import BusinessRecordPage from '@/views/ygb/shared/BusinessRecordPage.vue'
import { createBusinessRecordPageConfig } from '@/views/ygb/shared/businessRecordPageConfig.js'

const config = createBusinessRecordPageConfig({
  module: 'operationEnterpriseReview',
  title: '企业入驻审核',
  description: '围绕企业入驻资料、资质和处理结论审核开展查询、办理、处置跟踪和台账导出。',
  businessNameLabel: '入驻申请',
  businessNamePlaceholder: '请输入入驻申请',
  defaultSourceLabel: '企业入驻审核',
  filters: ['statMonth', 'regionCode', 'enterpriseId', 'enterpriseName', 'personName', 'businessName', 'riskLevel', 'workflowStatus', 'status']
})
</script>
