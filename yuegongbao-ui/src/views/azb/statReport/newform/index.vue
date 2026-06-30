<template>
  <TypedStatReportPage :config="pageConfig" />
</template>

<script setup name="AzbStatReportNewform">
import TypedStatReportPage from '@/views/statReport/TypedStatReportPage.vue'
import { resolveStatReportPageConfig } from '@/views/statReport/reportConfigs'

const pageConfig = resolveStatReportPageConfig('azb', 'newform')
</script>
