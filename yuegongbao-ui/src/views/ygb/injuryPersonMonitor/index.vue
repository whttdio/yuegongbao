<template>
  <business-record-page :config="config" />
</template>

<script setup>
import BusinessRecordPage from '@/views/ygb/shared/BusinessRecordPage.vue'
import { createBusinessRecordPageConfig } from '@/views/ygb/shared/businessRecordPageConfig.js'

const config = createBusinessRecordPageConfig({
  module: 'injuryPersonMonitor',
  title: '工伤监测',
  description: '围绕人员、企业、职业病和新业态伤害监测开展查询、办理、处置跟踪和台账导出。',
  businessNameLabel: '监测对象',
  businessNamePlaceholder: '请输入监测对象',
  defaultSourceLabel: '工伤监测',
  filters: ['statMonth', 'regionCode', 'enterpriseId', 'enterpriseName', 'personName', 'businessName', 'riskLevel', 'workflowStatus', 'status']
})
</script>
