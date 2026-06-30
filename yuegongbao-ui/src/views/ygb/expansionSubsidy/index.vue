<template>
  <business-record-page :config="config" />
</template>

<script setup>
import BusinessRecordPage from '@/views/ygb/shared/BusinessRecordPage.vue'
import { createBusinessRecordPageConfig } from '@/views/ygb/shared/businessRecordPageConfig.js'

const config = createBusinessRecordPageConfig({
  module: 'expansionSubsidy',
  title: '参保补贴管理',
  description: '围绕新增参保补贴审核、拨付和绩效跟踪开展查询、办理、处置跟踪和台账导出。',
  businessNameLabel: '补贴事项',
  businessNamePlaceholder: '请输入补贴事项',
  defaultSourceLabel: '参保补贴管理',
  filters: ['statMonth', 'regionCode', 'enterpriseId', 'enterpriseName', 'personName', 'businessName', 'riskLevel', 'workflowStatus', 'status']
})
</script>
