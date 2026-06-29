<template>
  <business-record-page :config="config" />
</template>

<script setup>
import BusinessRecordPage from '@/views/ygb/shared/BusinessRecordPage.vue'
import { createBusinessRecordPageConfig } from '@/views/ygb/shared/businessRecordPageConfig.js'

const config = createBusinessRecordPageConfig({
  module: 'enterpriseRelation',
  title: '派遣用工关联关系',
  description: '围绕派遣单位与用工单位关联关系和图谱维护开展查询、办理、处置跟踪和台账导出。',
  businessNameLabel: '关联名称',
  businessNamePlaceholder: '请输入关联名称',
  defaultSourceLabel: '派遣用工关联关系',
  filters: ['statMonth', 'regionCode', 'enterpriseId', 'enterpriseName', 'personName', 'businessName', 'riskLevel', 'workflowStatus', 'status']
})
</script>
