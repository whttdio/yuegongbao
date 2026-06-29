<template>
  <TypedStatReportPage :config="pageConfig" />
</template>

<script setup name="YgbStatReportEmployment">
import TypedStatReportPage from '@/views/statReport/TypedStatReportPage.vue'
import { resolveStatReportPageConfig } from '@/views/statReport/reportConfigs'

const pageConfig = resolveStatReportPageConfig('ygb', 'employment')
</script>
