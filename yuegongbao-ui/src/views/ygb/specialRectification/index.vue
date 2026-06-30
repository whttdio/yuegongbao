<template>
  <business-record-page :config="config" />
</template>

<script setup>
import BusinessRecordPage from '@/views/ygb/shared/BusinessRecordPage.vue'
import { createBusinessRecordPageConfig } from '@/views/ygb/shared/businessRecordPageConfig.js'

const config = createBusinessRecordPageConfig({
  module: 'specialRectification',
  title: '专项整治台账',
  description: '围绕问题整改跟踪及专项治理台账生成开展查询、办理、处置跟踪和台账导出。',
  businessNameLabel: '整改事项',
  businessNamePlaceholder: '请输入整改事项',
  defaultSourceLabel: '专项整治台账',
  filters: ['statMonth', 'regionCode', 'enterpriseId', 'enterpriseName', 'personName', 'businessName', 'riskLevel', 'workflowStatus', 'status']
})
</script>
