<template>
  <module-record-page :config="config" />
</template>

<script setup>
import ModuleRecordPage from '@/views/ygb/shared/ModuleRecordPage.vue'
import {
  getOccupationHealthArchive,
  getOccupationHealthArchiveSummary,
  listOccupationHealthArchive
} from '@/api/ygb/occupationMonitor'

const config = {
  title: '职业健康档案',
  description: '以只读台账方式承接职业健康档案、年度检查留痕和专题回函。',
  permPrefix: 'ygb:occupationHealthArchive',
  filePrefix: 'occupation_health_archive',
  defaultCategoryCode: 'health_archive',
  defaultSourceLabel: '职业病监管',
  recordNameLabel: '档案名称',
  recordNamePlaceholder: '请输入档案名称',
  filters: ['statMonth', 'regionCode', 'industryType', 'warningLevel', 'recordName', 'workflowStatus', 'status'],
  routeQueryFields: ['statMonth', 'regionCode', 'industryType', 'warningLevel'],
  readOnly: true,
  listApi: query => listOccupationHealthArchive(query),
  summaryApi: query => getOccupationHealthArchiveSummary(query),
  detailApi: id => getOccupationHealthArchive(id),
  exportUrl: 'ygb/occupation/healthArchive/export'
}
</script>
