<template>
  <TypedStatReportPage :config="pageConfig" />
</template>

<script setup name="AzbStatReportExpansion">
import TypedStatReportPage from '@/views/statReport/TypedStatReportPage.vue'
import { resolveStatReportPageConfig } from '@/views/statReport/reportConfigs'

const pageConfig = resolveStatReportPageConfig('azb', 'expansion')
</script>
