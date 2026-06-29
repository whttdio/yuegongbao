<template>
  <business-record-page :config="config" />
</template>

<script setup>
import BusinessRecordPage from '@/views/ygb/shared/BusinessRecordPage.vue'
import { createBusinessRecordPageConfig } from '@/views/ygb/shared/businessRecordPageConfig.js'

const config = createBusinessRecordPageConfig({
  module: 'personExpert',
  title: '工伤预防专家库',
  description: '围绕工伤预防专家资源、专业领域和服务记录管理开展查询、办理、处置跟踪和台账导出。',
  businessNameLabel: '专家名称',
  businessNamePlaceholder: '请输入专家名称',
  defaultSourceLabel: '工伤预防专家库',
  filters: ['statMonth', 'regionCode', 'enterpriseId', 'enterpriseName', 'personName', 'businessName', 'riskLevel', 'workflowStatus', 'status']
})
</script>
