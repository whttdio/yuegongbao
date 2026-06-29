<template>
  <business-record-page :config="config" />
</template>

<script setup>
import BusinessRecordPage from '@/views/ygb/shared/BusinessRecordPage.vue'
import { createBusinessRecordPageConfig } from '@/views/ygb/shared/businessRecordPageConfig.js'

const config = createBusinessRecordPageConfig({
  module: 'operationMessage',
  title: '消息推送管理',
  description: '围绕消息模板、推送对象和发送结果管理开展查询、办理、处置跟踪和台账导出。',
  businessNameLabel: '消息主题',
  businessNamePlaceholder: '请输入消息主题',
  defaultSourceLabel: '消息推送管理',
  filters: ['statMonth', 'regionCode', 'enterpriseId', 'enterpriseName', 'personName', 'businessName', 'riskLevel', 'workflowStatus', 'status']
})
</script>
