<template>
  <business-record-page :config="config" />
</template>

<script setup>
import BusinessRecordPage from '@/views/ygb/shared/BusinessRecordPage.vue'
import { createBusinessRecordPageConfig } from '@/views/ygb/shared/businessRecordPageConfig.js'

const config = createBusinessRecordPageConfig({
  module: 'newformTraining',
  title: '新业态培训管理',
  description: '围绕新业态专项安全培训、学时和考试结果管理开展查询、办理、处置跟踪和台账导出。',
  businessNameLabel: '培训事项',
  businessNamePlaceholder: '请输入培训事项',
  defaultSourceLabel: '新业态培训管理',
  filters: ['statMonth', 'regionCode', 'enterpriseId', 'enterpriseName', 'personName', 'businessName', 'riskLevel', 'workflowStatus', 'status']
})
</script>
