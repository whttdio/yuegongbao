<template>
  <enterprise-subledger-page :config="config" />
</template>

<script setup>
import EnterpriseSubledgerPage from '@/views/ygb/shared/EnterpriseSubledgerPage.vue'

const config = {
  title: '监管单位',
  description: '复用企业主表统一维护监管单位台账，支撑区域筛查、同步校验和主链办理入口。',
  fixedEnterpriseType: '4',
  filePrefix: 'enterprise_regulator'
}
</script>
