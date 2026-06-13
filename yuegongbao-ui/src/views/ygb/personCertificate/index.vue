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

const submodule = 'certificate'
const config = {
  title: '特证管理',
  description: '复用扩展台账维护人员特种作业证、取证进度、复审节点和闭环状态。',
  permPrefix: 'ygb:personCertificate',
  filePrefix: 'person_certificate',
  defaultCategoryCode: 'certificate',
  defaultSourceLabel: '人员管理',
  recordNameLabel: '证书名称',
  recordNamePlaceholder: '请输入证书名称',
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
