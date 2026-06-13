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

const submodule = 'highRisk'
const config = {
  title: '高危企业库',
  description: '复用 typed-record 维护高危企业标识、来源标签、闭环状态和导出能力。',
  permPrefix: 'ygb:enterpriseHighRisk',
  filePrefix: 'enterprise_high_risk',
  defaultCategoryCode: 'high_risk',
  defaultSourceLabel: '单位管理',
  recordNameLabel: '高危标识名称',
  recordNamePlaceholder: '请输入高危企业标识名称',
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
