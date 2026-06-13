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

const submodule = 'expert'
const config = {
  title: '专家库',
  description: '维护专家信息、专业方向、参与记录和协同处置状态。',
  permPrefix: 'ygb:personExpert',
  filePrefix: 'person_expert',
  defaultCategoryCode: 'expert',
  defaultSourceLabel: '人员管理',
  recordNameLabel: '专家主题',
  recordNamePlaceholder: '请输入专家主题',
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
