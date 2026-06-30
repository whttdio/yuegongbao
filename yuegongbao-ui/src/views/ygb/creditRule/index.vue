<template>
  <business-record-page :config="config" />
</template>

<script setup>
import BusinessRecordPage from '@/views/ygb/shared/BusinessRecordPage.vue'
import { createBusinessRecordPageConfig } from '@/views/ygb/shared/businessRecordPageConfig.js'

const config = createBusinessRecordPageConfig({
  module: 'creditRule',
  title: '信用评分规则配置',
  description: '围绕企业信用评分指标、权重和扣分规则维护开展查询、办理、处置跟踪和台账导出。',
  businessNameLabel: '规则名称',
  businessNamePlaceholder: '请输入规则名称',
  defaultSourceLabel: '信用评分规则配置',
  filters: ['statMonth', 'regionCode', 'enterpriseId', 'enterpriseName', 'personName', 'businessName', 'riskLevel', 'workflowStatus', 'status']
})
</script>
