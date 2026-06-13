<template>
  <TypedStatReportPage :config="pageConfig" />
</template>

<script setup name="AzbStatReportSalary">
import TypedStatReportPage from '@/views/statReport/TypedStatReportPage.vue'
import { resolveStatReportPageConfig } from '@/views/statReport/reportConfigs'

const pageConfig = resolveStatReportPageConfig('azb', 'salary')
</script>
