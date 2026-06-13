<template>
  <TypedStatReportPage :config="pageConfig" />
</template>

<script setup name="YgbStatReportInjury">
import TypedStatReportPage from '@/views/statReport/TypedStatReportPage.vue'
import { resolveStatReportPageConfig } from '@/views/statReport/reportConfigs'

const pageConfig = resolveStatReportPageConfig('ygb', 'injury')
</script>
