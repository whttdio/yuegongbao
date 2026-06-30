<template>
  <business-record-page :config="config" />
</template>

<script setup>
import BusinessRecordPage from '@/views/ygb/shared/BusinessRecordPage.vue'
import { createBusinessRecordPageConfig } from '@/views/ygb/shared/businessRecordPageConfig.js'

const config = createBusinessRecordPageConfig({
  module: 'operationJobCategory',
  title: '职位分类管理',
  description: '围绕招聘职位分类、状态和排序维护开展查询、办理、处置跟踪和台账导出。',
  businessNameLabel: '职位分类',
  businessNamePlaceholder: '请输入职位分类',
  defaultSourceLabel: '职位分类管理',
  filters: ['statMonth', 'regionCode', 'enterpriseId', 'enterpriseName', 'personName', 'businessName', 'riskLevel', 'workflowStatus', 'status']
})
</script>
