<template>
  <TypedStatReportPage :config="pageConfig" />
</template>

<script setup name="YgbStatReportAqinsurance">
import TypedStatReportPage from '@/views/statReport/TypedStatReportPage.vue'
import { resolveStatReportPageConfig } from '@/views/statReport/reportConfigs'

const pageConfig = resolveStatReportPageConfig('ygb', 'aqInsurance')
</script>
