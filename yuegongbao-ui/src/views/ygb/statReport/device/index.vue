<template>
  <TypedStatReportPage :config="pageConfig" />
</template>

<script setup name="YgbStatReportDevice">
import TypedStatReportPage from '@/views/statReport/TypedStatReportPage.vue'
import { resolveStatReportPageConfig } from '@/views/statReport/reportConfigs'

const pageConfig = resolveStatReportPageConfig('ygb', 'device')
</script>
