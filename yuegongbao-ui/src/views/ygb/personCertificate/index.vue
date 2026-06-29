<template>
  <business-record-page :config="config" />
</template>

<script setup>
import BusinessRecordPage from '@/views/ygb/shared/BusinessRecordPage.vue'
import { createBusinessRecordPageConfig } from '@/views/ygb/shared/businessRecordPageConfig.js'

const config = createBusinessRecordPageConfig({
  module: 'personCertificate',
  title: '特证管理',
  description: '围绕特种作业证件续期审核和到期提醒开展查询、办理、处置跟踪和台账导出。',
  businessNameLabel: '证件名称',
  businessNamePlaceholder: '请输入证件名称',
  defaultSourceLabel: '特证管理',
  filters: ['statMonth', 'regionCode', 'enterpriseId', 'enterpriseName', 'personName', 'businessName', 'riskLevel', 'workflowStatus', 'status']
})
</script>
