<template>
  <business-record-page :config="config" />
</template>

<script setup>
import BusinessRecordPage from '@/views/ygb/shared/BusinessRecordPage.vue'
import { createBusinessRecordPageConfig } from '@/views/ygb/shared/businessRecordPageConfig.js'

const config = createBusinessRecordPageConfig({
  module: 'socialSupplement',
  title: '社保补缴跟踪',
  description: '围绕断缴整改及补缴进度跟踪开展查询、办理、处置跟踪和台账导出。',
  businessNameLabel: '补缴事项',
  businessNamePlaceholder: '请输入补缴事项、企业或人员',
  defaultSourceLabel: '社保补缴跟踪',
  fieldLabels: {
    amount: '应补缴金额',
    quantity: '补缴人数',
    contentText: '断缴/补缴说明',
    handleResult: '补缴进度与结果',
    ownerName: '跟进人',
    eventTime: '补缴截止时间',
    sourceLabel: '线索来源'
  },
  fieldPlaceholders: {
    amount: '请输入应补缴金额',
    quantity: '请输入补缴人数',
    contentText: '请输入断缴区间、补缴原因和核查说明',
    handleResult: '请输入补缴进度、到账情况或整改结论',
    ownerName: '请输入跟进人',
    sourceLabel: '请输入线索来源'
  },
  filters: ['statMonth', 'regionCode', 'enterpriseId', 'enterpriseName', 'personName', 'businessName', 'riskLevel', 'workflowStatus', 'status']
})
</script>
