<template>
  <business-record-page :config="config" />
</template>

<script setup>
import BusinessRecordPage from '@/views/ygb/shared/BusinessRecordPage.vue'
import { createBusinessRecordPageConfig } from '@/views/ygb/shared/businessRecordPageConfig.js'

const config = createBusinessRecordPageConfig({
  module: 'preventionTraining',
  title: '工伤预防培训',
  description: '围绕工伤预防培训计划、学时和效果跟踪开展查询、办理、处置跟踪和台账导出。',
  businessNameLabel: '培训事项',
  businessNamePlaceholder: '请输入培训事项',
  defaultSourceLabel: '工伤预防培训',
  filters: ['statMonth', 'regionCode', 'enterpriseId', 'enterpriseName', 'personName', 'businessName', 'riskLevel', 'workflowStatus', 'status']
})
</script>
