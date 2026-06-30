<template>
  <business-record-page :config="config" />
</template>

<script setup>
import BusinessRecordPage from '@/views/ygb/shared/BusinessRecordPage.vue'
import { createBusinessRecordPageConfig } from '@/views/ygb/shared/businessRecordPageConfig.js'

const config = createBusinessRecordPageConfig({
  module: 'preventionPublicity',
  title: '工伤预防宣传',
  description: '围绕工伤预防宣传内容和发布记录开展查询、办理、处置跟踪和台账导出。',
  businessNameLabel: '宣传事项',
  businessNamePlaceholder: '请输入宣传事项',
  defaultSourceLabel: '工伤预防宣传',
  filters: ['statMonth', 'regionCode', 'enterpriseId', 'enterpriseName', 'personName', 'businessName', 'riskLevel', 'workflowStatus', 'status']
})
</script>
