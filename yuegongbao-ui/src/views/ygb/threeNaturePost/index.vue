<template>
  <business-record-page :config="config" />
</template>

<script setup>
import BusinessRecordPage from '@/views/ygb/shared/BusinessRecordPage.vue'
import { createBusinessRecordPageConfig } from '@/views/ygb/shared/businessRecordPageConfig.js'

const config = createBusinessRecordPageConfig({
  module: 'threeNaturePost',
  title: '三性岗位审核',
  description: '围绕临时性、辅助性、替代性岗位合规审核开展查询、办理、处置跟踪和台账导出。',
  businessNameLabel: '岗位名称',
  businessNamePlaceholder: '请输入岗位名称',
  defaultSourceLabel: '三性岗位审核',
  filters: ['statMonth', 'regionCode', 'enterpriseId', 'enterpriseName', 'personName', 'businessName', 'riskLevel', 'workflowStatus', 'status']
})
</script>
