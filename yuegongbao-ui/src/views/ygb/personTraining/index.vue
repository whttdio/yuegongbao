<template>
  <business-record-page :config="config" />
</template>

<script setup>
import BusinessRecordPage from '@/views/ygb/shared/BusinessRecordPage.vue'
import { createBusinessRecordPageConfig } from '@/views/ygb/shared/businessRecordPageConfig.js'

const config = createBusinessRecordPageConfig({
  module: 'personTraining',
  title: '人员培训监督',
  description: '围绕人员培训进度、学时和考试结果跟踪开展查询、办理、处置跟踪和台账导出。',
  businessNameLabel: '培训事项',
  businessNamePlaceholder: '请输入培训事项',
  defaultSourceLabel: '人员培训监督',
  filters: ['statMonth', 'regionCode', 'enterpriseId', 'enterpriseName', 'personName', 'businessName', 'riskLevel', 'workflowStatus', 'status']
})
</script>
