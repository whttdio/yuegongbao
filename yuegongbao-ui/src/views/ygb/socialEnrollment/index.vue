<template>
  <business-record-page :config="config" />
</template>

<script setup>
import BusinessRecordPage from '@/views/ygb/shared/BusinessRecordPage.vue'
import { createBusinessRecordPageConfig } from '@/views/ygb/shared/businessRecordPageConfig.js'

const config = createBusinessRecordPageConfig({
  module: 'socialEnrollment',
  title: '参保率统计',
  description: '围绕企业参保率排名和趋势统计开展查询、办理、处置跟踪和台账导出。',
  businessNameLabel: '参保统计对象',
  businessNamePlaceholder: '请输入企业、区域或统计对象',
  defaultSourceLabel: '参保率统计',
  fieldLabels: {
    amount: '参保率(%)',
    quantity: '应参保人数',
    contentText: '统计口径',
    handleResult: '整改/督办结果',
    ownerName: '经办人',
    eventTime: '统计时间',
    sourceLabel: '数据来源'
  },
  fieldPlaceholders: {
    amount: '请输入参保率百分比',
    quantity: '请输入应参保人数',
    contentText: '请输入统计口径、参保人数和应参保人数来源',
    handleResult: '请输入低参保率企业督办或整改结果',
    ownerName: '请输入经办人',
    sourceLabel: '请输入数据来源'
  },
  filters: ['statMonth', 'regionCode', 'enterpriseId', 'enterpriseName', 'personName', 'businessName', 'riskLevel', 'workflowStatus', 'status']
})
</script>
