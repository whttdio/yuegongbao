<template>
  <module-record-page :config="config" />
</template>

<script setup>
import ModuleRecordPage from '@/views/ygb/shared/ModuleRecordPage.vue'
import {
  addOperationModule,
  delOperationModule,
  getOperationModule,
  getOperationModuleSummary,
  listOperationModule,
  updateOperationModule
} from '@/api/ygb/operation'

const submodule = 'enterpriseReview'
const config = {
  title: '企业入驻审核',
  description: '复用扩展台账对象，承接企业入驻审核闭环、状态跟踪和导出。',
  permPrefix: 'ygb:operationEnterpriseReview',
  filePrefix: 'operation_enterprise_review',
  defaultCategoryCode: 'enterprise_review',
  defaultWorkflowStatus: 'pending',
  defaultSourceLabel: '运营后台',
  listApi: query => listOperationModule(submodule, query),
  summaryApi: query => getOperationModuleSummary(submodule, query),
  detailApi: id => getOperationModule(submodule, id),
  addApi: data => addOperationModule(submodule, data),
  updateApi: data => updateOperationModule(submodule, data),
  deleteApi: ids => delOperationModule(submodule, ids),
  exportUrl: `ygb/operation/${submodule}/export`
}
</script>
