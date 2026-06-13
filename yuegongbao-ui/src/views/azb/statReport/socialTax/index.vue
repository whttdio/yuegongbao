<template>
  <TypedStatReportPage :config="pageConfig" />
</template>

<script setup name="AzbStatReportSocialTax">
import TypedStatReportPage from '@/views/statReport/TypedStatReportPage.vue'
import { resolveStatReportPageConfig } from '@/views/statReport/reportConfigs'

const pageConfig = resolveStatReportPageConfig('azb', 'socialTax')
</script>
