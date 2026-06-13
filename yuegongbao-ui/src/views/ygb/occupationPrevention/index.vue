<template>
  <module-record-page :config="config" />
</template>

<script setup>
import ModuleRecordPage from '@/views/ygb/shared/ModuleRecordPage.vue'
import {
  getOccupationPrevention,
  getOccupationPreventionSummary,
  listOccupationPrevention
} from '@/api/ygb/occupationMonitor'

const config = {
  title: '职业病预防项目',
  description: '以只读台账方式承接职业病预防项目、专题任务和项目归档。',
  permPrefix: 'ygb:occupationPrevention',
  filePrefix: 'occupation_prevention',
  defaultCategoryCode: 'prevention',
  defaultSourceLabel: '职业病监管',
  recordNameLabel: '项目名称',
  recordNamePlaceholder: '请输入项目名称',
  filters: ['statMonth', 'regionCode', 'industryType', 'warningLevel', 'recordName', 'workflowStatus', 'status'],
  routeQueryFields: ['statMonth', 'regionCode', 'industryType', 'warningLevel'],
  readOnly: true,
  listApi: query => listOccupationPrevention(query),
  summaryApi: query => getOccupationPreventionSummary(query),
  detailApi: id => getOccupationPrevention(id),
  exportUrl: 'ygb/occupation/prevention/export'
}
</script>
