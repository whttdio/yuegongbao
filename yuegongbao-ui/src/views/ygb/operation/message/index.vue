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

const submodule = 'message'
const config = {
  title: '消息推送',
  description: '基于统一消息对象维护运营消息、内部推送记录和闭环状态。',
  permPrefix: 'ygb:operationMessage',
  filePrefix: 'operation_message',
  defaultCategoryCode: 'operation_message',
  defaultWorkflowStatus: 'draft',
  defaultSourceLabel: '运营消息中心',
  recordNameLabel: '消息标题',
  recordNamePlaceholder: '请输入消息标题',
  listApi: query => listOperationModule(submodule, query),
  summaryApi: query => getOperationModuleSummary(submodule, query),
  detailApi: id => getOperationModule(submodule, id),
  addApi: data => addOperationModule(submodule, data),
  updateApi: data => updateOperationModule(submodule, data),
  deleteApi: ids => delOperationModule(submodule, ids),
  exportUrl: `ygb/operation/${submodule}/export`
}
</script>
