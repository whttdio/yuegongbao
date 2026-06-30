<template>
  <TypedStatReportPage :config="pageConfig" />
</template>

<script setup name="AzbStatReportDevice">
import TypedStatReportPage from '@/views/statReport/TypedStatReportPage.vue'
import { resolveStatReportPageConfig } from '@/views/statReport/reportConfigs'

const pageConfig = resolveStatReportPageConfig('azb', 'device')
</script>
