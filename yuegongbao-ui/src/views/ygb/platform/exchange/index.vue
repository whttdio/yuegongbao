<template>
  <business-record-page :config="config" />
</template>

<script setup>
import BusinessRecordPage from '@/views/ygb/shared/BusinessRecordPage.vue'
import { createBusinessRecordPageConfig } from '@/views/ygb/shared/businessRecordPageConfig.js'

const config = createBusinessRecordPageConfig({
  module: 'platformExchange',
  title: '接口与数据交换监管',
  description: '围绕接口调用、数据交换状态和异常记录监控开展查询、办理、处置跟踪和台账导出。',
  businessNameLabel: '接口事项',
  businessNamePlaceholder: '请输入接口事项',
  defaultSourceLabel: '接口与数据交换监管',
  filters: ['statMonth', 'regionCode', 'enterpriseId', 'enterpriseName', 'personName', 'businessName', 'riskLevel', 'workflowStatus', 'status']
})
</script>
