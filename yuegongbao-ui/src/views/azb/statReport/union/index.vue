<template>
  <TypedStatReportPage :config="pageConfig" />
</template>

<script setup name="AzbStatReportUnion">
import TypedStatReportPage from '@/views/statReport/TypedStatReportPage.vue'
import { resolveStatReportPageConfig } from '@/views/statReport/reportConfigs'

const pageConfig = resolveStatReportPageConfig('azb', 'union')
</script>
