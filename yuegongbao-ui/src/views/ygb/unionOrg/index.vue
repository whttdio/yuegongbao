<template>
  <business-record-page :config="config" />
</template>

<script setup>
import BusinessRecordPage from '@/views/ygb/shared/BusinessRecordPage.vue'
import { createBusinessRecordPageConfig } from '@/views/ygb/shared/businessRecordPageConfig.js'

const config = createBusinessRecordPageConfig({
  module: 'unionOrg',
  title: '工会组织',
  description: '围绕工会组织、人员和服务事项维护开展查询、办理、处置跟踪和台账导出。',
  businessNameLabel: '组织名称',
  businessNamePlaceholder: '请输入组织名称',
  defaultSourceLabel: '工会组织',
  filters: ['statMonth', 'regionCode', 'enterpriseId', 'enterpriseName', 'personName', 'businessName', 'riskLevel', 'workflowStatus', 'status']
})
</script>
