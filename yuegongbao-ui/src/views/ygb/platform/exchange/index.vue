<template>
  <module-record-page :config="config" />
</template>

<script setup>
import ModuleRecordPage from '@/views/ygb/shared/ModuleRecordPage.vue'
import {
  addPlatformModule,
  delPlatformModule,
  getPlatformModule,
  getPlatformModuleSummary,
  listPlatformModule,
  updatePlatformModule
} from '@/api/ygb/platform'

const submodule = 'exchange'
const config = {
  title: '接口与数据交换监控',
  description: '记录交换任务、异常回写和人工跟踪结果，形成业务治理台账。',
  permPrefix: 'ygb:platformExchange',
  filePrefix: 'platform_exchange',
  defaultCategoryCode: 'exchange',
  defaultSourceLabel: '数据交换监控',
  recordNameLabel: '交换任务',
  recordNamePlaceholder: '请输入交换任务名称',
  listApi: query => listPlatformModule(submodule, query),
  summaryApi: query => getPlatformModuleSummary(submodule, query),
  detailApi: id => getPlatformModule(submodule, id),
  addApi: data => addPlatformModule(submodule, data),
  updateApi: data => updatePlatformModule(submodule, data),
  deleteApi: ids => delPlatformModule(submodule, ids),
  exportUrl: `ygb/platform/${submodule}/export`
}
</script>
