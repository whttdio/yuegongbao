<template>
  <TypedStatReportPage :config="pageConfig" />
</template>

<script setup name="AzbStatReportInjury">
import TypedStatReportPage from '@/views/statReport/TypedStatReportPage.vue'
import { resolveStatReportPageConfig } from '@/views/statReport/reportConfigs'

const pageConfig = resolveStatReportPageConfig('azb', 'injury')
</script>
