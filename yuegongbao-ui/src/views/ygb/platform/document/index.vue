<template>
  <business-record-page :config="config" />
</template>

<script setup>
import BusinessRecordPage from '@/views/ygb/shared/BusinessRecordPage.vue'
import { createBusinessRecordPageConfig } from '@/views/ygb/shared/businessRecordPageConfig.js'

const config = createBusinessRecordPageConfig({
  module: 'platformDocument',
  title: '文档管理',
  description: '围绕平台资料、制度文件和归档记录维护开展查询、办理、处置跟踪和台账导出。',
  businessNameLabel: '文档名称',
  businessNamePlaceholder: '请输入文档名称',
  defaultSourceLabel: '文档管理',
  filters: ['statMonth', 'regionCode', 'enterpriseId', 'enterpriseName', 'personName', 'businessName', 'riskLevel', 'workflowStatus', 'status']
})
</script>
