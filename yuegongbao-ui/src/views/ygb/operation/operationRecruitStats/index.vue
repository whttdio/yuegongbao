<template>
  <business-record-page :config="config" />
</template>

<script setup>
import BusinessRecordPage from '@/views/ygb/shared/BusinessRecordPage.vue'
import { createBusinessRecordPageConfig } from '@/views/ygb/shared/businessRecordPageConfig.js'

const config = createBusinessRecordPageConfig({
  module: 'operationRecruitStats',
  title: '招聘统计',
  description: '围绕招聘岗位、简历和投递数据统计开展查询、办理、处置跟踪和台账导出。',
  businessNameLabel: '招聘指标',
  businessNamePlaceholder: '请输入招聘指标',
  defaultSourceLabel: '招聘统计',
  filters: ['statMonth', 'regionCode', 'enterpriseId', 'enterpriseName', 'personName', 'businessName', 'riskLevel', 'workflowStatus', 'status']
})
</script>
