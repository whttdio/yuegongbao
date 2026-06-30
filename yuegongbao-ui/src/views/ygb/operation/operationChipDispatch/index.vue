<template>
  <business-record-page :config="config" />
</template>

<script setup>
import BusinessRecordPage from '@/views/ygb/shared/BusinessRecordPage.vue'
import { createBusinessRecordPageConfig } from '@/views/ygb/shared/businessRecordPageConfig.js'

const config = createBusinessRecordPageConfig({
  module: 'operationChipDispatch',
  title: '芯片调度',
  description: '围绕芯片调度、领用和库存流转管理开展查询、办理、处置跟踪和台账导出。',
  businessNameLabel: '调度事项',
  businessNamePlaceholder: '请输入调度事项',
  defaultSourceLabel: '芯片调度',
  filters: ['statMonth', 'regionCode', 'enterpriseId', 'enterpriseName', 'personName', 'businessName', 'riskLevel', 'workflowStatus', 'status']
})
</script>
