<template>
  <enterprise-subledger-page :config="config" />
</template>

<script setup>
import EnterpriseSubledgerPage from '@/views/ygb/shared/EnterpriseSubledgerPage.vue'

const config = {
  title: '劳务派遣公司',
  description: '聚焦派遣主体主数据，完成查询、维护、导出和主链承接。',
  fixedEnterpriseType: '1',
  filePrefix: 'enterprise_dispatch'
}
</script>
