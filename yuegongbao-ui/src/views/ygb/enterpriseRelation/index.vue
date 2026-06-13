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

const submodule = 'relation'
const config = {
  title: '派遣用工关联',
  description: '通过 typed-record 维护派遣单位与用工单位关联关系、状态和回写记录。',
  permPrefix: 'ygb:enterpriseRelation',
  filePrefix: 'enterprise_relation',
  defaultCategoryCode: 'relation',
  defaultSourceLabel: '单位管理',
  recordNameLabel: '关联关系名称',
  recordNamePlaceholder: '请输入关联关系名称',
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
