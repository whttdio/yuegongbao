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

const submodule = 'blacklist'
const config = {
  title: '黑名单',
  description: '维护涉险人员、违规对象和限制进入监管链路的黑名单台账。',
  permPrefix: 'ygb:personBlacklist',
  filePrefix: 'person_blacklist',
  defaultCategoryCode: 'blacklist',
  defaultSourceLabel: '人员管理',
  recordNameLabel: '黑名单主题',
  recordNamePlaceholder: '请输入黑名单主题',
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
