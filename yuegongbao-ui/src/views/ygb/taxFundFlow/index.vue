<template>
  <business-record-page :config="config" />
</template>

<script setup>
import BusinessRecordPage from '@/views/ygb/shared/BusinessRecordPage.vue'
import { createBusinessRecordPageConfig } from '@/views/ygb/shared/businessRecordPageConfig.js'

const config = createBusinessRecordPageConfig({
  module: 'taxFundFlow',
  title: '资金流穿透分析',
  description: '围绕空壳过账企业和异常资金流向识别开展查询、办理、处置跟踪和台账导出。',
  businessNameLabel: '资金流线索',
  businessNamePlaceholder: '请输入资金流线索',
  defaultSourceLabel: '资金流穿透分析',
  filters: ['statMonth', 'regionCode', 'enterpriseId', 'enterpriseName', 'personName', 'businessName', 'riskLevel', 'workflowStatus', 'status']
})
</script>
