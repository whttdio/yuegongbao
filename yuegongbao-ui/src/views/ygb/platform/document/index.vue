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

const submodule = 'document'
const config = {
  title: '文档管理',
  description: '统一维护平台制度、接口文档、交付附件和验收留档。',
  permPrefix: 'ygb:platformDocument',
  filePrefix: 'platform_document',
  defaultCategoryCode: 'document',
  defaultSourceLabel: '平台治理',
  recordNameLabel: '文档名称',
  recordNamePlaceholder: '请输入文档名称',
  listApi: query => listPlatformModule(submodule, query),
  summaryApi: query => getPlatformModuleSummary(submodule, query),
  detailApi: id => getPlatformModule(submodule, id),
  addApi: data => addPlatformModule(submodule, data),
  updateApi: data => updatePlatformModule(submodule, data),
  deleteApi: ids => delPlatformModule(submodule, ids),
  exportUrl: `ygb/platform/${submodule}/export`
}
</script>
