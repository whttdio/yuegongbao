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

const submodule = 'highRiskPost'
const config = {
  title: '高危岗位库',
  description: '沉淀高危岗位对象、岗位画像、风险来源和审查状态。',
  permPrefix: 'ygb:personHighRiskPost',
  filePrefix: 'person_high_risk_post',
  defaultCategoryCode: 'high_risk_post',
  defaultSourceLabel: '人员管理',
  recordNameLabel: '高危岗位名称',
  recordNamePlaceholder: '请输入高危岗位名称',
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
