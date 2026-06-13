<template>
  <module-record-page :config="config" />
</template>

<script setup>
import ModuleRecordPage from '@/views/ygb/shared/ModuleRecordPage.vue'
import {
  addPersonSubmodule,
  delPersonSubmodule,
  getPersonSubmodule,
  getPersonSubmoduleSummary,
  listPersonSubmodule,
  updatePersonSubmodule
} from '@/api/ygb/person'

const submodule = 'riskPost'
const config = {
  title: '风险岗位库',
  description: '统一维护风险岗位、适用企业、风险等级和处置进度。',
  permPrefix: 'ygb:personRiskPost',
  filePrefix: 'person_risk_post',
  defaultCategoryCode: 'risk_post',
  defaultSourceLabel: '人员管理',
  recordNameLabel: '风险岗位名称',
  recordNamePlaceholder: '请输入风险岗位名称',
  filters: ['statMonth', 'regionCode', 'enterpriseId', 'personName', 'recordName', 'workflowStatus', 'status'],
  routeQueryFields: ['regionCode', 'enterpriseId', 'personName'],
  listApi: query => listPersonSubmodule(submodule, query),
  summaryApi: query => getPersonSubmoduleSummary(submodule, query),
  detailApi: id => getPersonSubmodule(submodule, id),
  addApi: data => addPersonSubmodule(submodule, data),
  updateApi: data => updatePersonSubmodule(submodule, data),
  deleteApi: ids => delPersonSubmodule(submodule, ids),
  exportUrl: `ygb/person/${submodule}/export`
}
</script>
