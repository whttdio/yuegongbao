<template>
  <TypedStatReportPage :config="pageConfig" />
</template>

<script setup name="AzbStatReportCustom">
import TypedStatReportPage from '@/views/statReport/TypedStatReportPage.vue'
import { resolveStatReportPageConfig } from '@/views/statReport/reportConfigs'

const pageConfig = resolveStatReportPageConfig('azb', 'custom')
</script>
