<template>
  <enterprise-subledger-page :config="config" />
</template>

<script setup>
import EnterpriseSubledgerPage from '@/views/ygb/shared/EnterpriseSubledgerPage.vue'

const config = {
  title: '用工单位',
  description: '聚焦用工主体主数据，支撑考勤、工资、社保税务等后续链路透传。',
  fixedEnterpriseType: '2',
  filePrefix: 'enterprise_employer'
}
</script>
