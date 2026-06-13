<template>
  <module-record-page :config="config" />
</template>

<script setup>
import ModuleRecordPage from '@/views/ygb/shared/ModuleRecordPage.vue'
import {
  addEnterpriseSubmodule,
  delEnterpriseSubmodule,
  getEnterpriseSubmodule,
  getEnterpriseSubmoduleSummary,
  listEnterpriseSubmodule,
  updateEnterpriseSubmodule
} from '@/api/ygb/enterprise'

const submodule = 'union'
const config = {
  title: '工会管理',
  description: '复用 typed-record 维护企业工会组织、协同状态、来源和导出能力。',
  permPrefix: 'ygb:enterpriseUnion',
  filePrefix: 'enterprise_union',
  defaultCategoryCode: 'union',
  defaultSourceLabel: '单位管理',
  recordNameLabel: '工会组织名称',
  recordNamePlaceholder: '请输入工会组织名称',
  filters: ['statMonth', 'regionCode', 'enterpriseId', 'enterpriseName', 'recordName', 'workflowStatus', 'status'],
  routeQueryFields: ['statMonth', 'regionCode', 'enterpriseId', 'enterpriseName', 'workflowStatus', 'status'],
  listApi: query => listEnterpriseSubmodule(submodule, query),
  summaryApi: query => getEnterpriseSubmoduleSummary(submodule, query),
  detailApi: id => getEnterpriseSubmodule(submodule, id),
  addApi: data => addEnterpriseSubmodule(submodule, data),
  updateApi: data => updateEnterpriseSubmodule(submodule, data),
  deleteApi: ids => delEnterpriseSubmodule(submodule, ids),
  exportUrl: `ygb/enterprise/${submodule}/export`
}
</script>
