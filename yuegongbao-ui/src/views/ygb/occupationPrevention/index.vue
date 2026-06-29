<template>
  <business-record-page :config="config" />
</template>

<script setup>
import BusinessRecordPage from '@/views/ygb/shared/BusinessRecordPage.vue'
import { createBusinessRecordPageConfig } from '@/views/ygb/shared/businessRecordPageConfig.js'

const config = createBusinessRecordPageConfig({
  module: 'occupationPrevention',
  title: '职业病预防项目',
  description: '围绕职业病预防项目申报、验收和跟踪开展查询、办理、处置跟踪和台账导出。',
  businessNameLabel: '预防项目',
  businessNamePlaceholder: '请输入预防项目',
  defaultSourceLabel: '职业病预防项目',
  filters: ['statMonth', 'regionCode', 'enterpriseId', 'enterpriseName', 'personName', 'businessName', 'riskLevel', 'workflowStatus', 'status']
})
</script>
