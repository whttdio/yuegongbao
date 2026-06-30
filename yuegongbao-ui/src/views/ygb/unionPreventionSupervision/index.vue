<template>
  <business-record-page :config="config" />
</template>

<script setup>
import BusinessRecordPage from '@/views/ygb/shared/BusinessRecordPage.vue'
import { createBusinessRecordPageConfig } from '@/views/ygb/shared/businessRecordPageConfig.js'

const config = createBusinessRecordPageConfig({
  module: 'unionPreventionSupervision',
  title: '工会预防监督',
  description: '围绕工会参与工伤预防监督和整改跟踪开展查询、办理、处置跟踪和台账导出。',
  businessNameLabel: '监督事项',
  businessNamePlaceholder: '请输入监督事项',
  defaultSourceLabel: '工会预防监督',
  filters: ['statMonth', 'regionCode', 'enterpriseId', 'enterpriseName', 'personName', 'businessName', 'riskLevel', 'workflowStatus', 'status']
})
</script>
