<template>
  <business-record-page :config="config" />
</template>

<script setup>
import BusinessRecordPage from '@/views/ygb/shared/BusinessRecordPage.vue'
import { createBusinessRecordPageConfig } from '@/views/ygb/shared/businessRecordPageConfig.js'

const config = createBusinessRecordPageConfig({
  module: 'enterpriseUnion',
  title: '工会管理',
  description: '围绕工会维权、监督事项和服务记录管理开展查询、办理、处置跟踪和台账导出。',
  businessNameLabel: '工会事项',
  businessNamePlaceholder: '请输入工会事项',
  defaultSourceLabel: '工会管理',
  filters: ['statMonth', 'regionCode', 'enterpriseId', 'enterpriseName', 'personName', 'businessName', 'riskLevel', 'workflowStatus', 'status']
})
</script>
