<template>
  <business-record-page :config="config" />
</template>

<script setup>
import BusinessRecordPage from '@/views/ygb/shared/BusinessRecordPage.vue'
import { createBusinessRecordPageConfig } from '@/views/ygb/shared/businessRecordPageConfig.js'

const config = createBusinessRecordPageConfig({
  module: 'deviceGeofence',
  title: '电子围栏',
  description: '围绕设备越界告警、轨迹回放和围栏规则管理开展查询、办理、处置跟踪和台账导出。',
  businessNameLabel: '围栏名称',
  businessNamePlaceholder: '请输入围栏名称',
  defaultSourceLabel: '电子围栏',
  filters: ['statMonth', 'regionCode', 'enterpriseId', 'enterpriseName', 'personName', 'businessName', 'riskLevel', 'workflowStatus', 'status']
})
</script>
