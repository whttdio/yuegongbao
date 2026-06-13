<template>
  <TypedStatReportPage :config="pageConfig" />
</template>

<script setup name="YgbStatReportWarning">
import TypedStatReportPage from '@/views/statReport/TypedStatReportPage.vue'
import { resolveStatReportPageConfig } from '@/views/statReport/reportConfigs'

const pageConfig = resolveStatReportPageConfig('ygb', 'warning')
</script>
