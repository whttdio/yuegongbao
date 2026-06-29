<template>
  <business-record-page :config="config" />
</template>

<script setup>
import BusinessRecordPage from '@/views/ygb/shared/BusinessRecordPage.vue'
import { createBusinessRecordPageConfig } from '@/views/ygb/shared/businessRecordPageConfig.js'

const config = createBusinessRecordPageConfig({
  module: 'platformBackup',
  title: '数据备份与恢复',
  description: '围绕数据备份、恢复演练和结果记录管理开展查询、办理、处置跟踪和台账导出。',
  businessNameLabel: '备份任务',
  businessNamePlaceholder: '请输入备份任务',
  defaultSourceLabel: '数据备份与恢复',
  filters: ['statMonth', 'regionCode', 'enterpriseId', 'enterpriseName', 'personName', 'businessName', 'riskLevel', 'workflowStatus', 'status']
})
</script>
