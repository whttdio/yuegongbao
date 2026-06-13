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

const submodule = 'backup'
const config = {
  title: '备份恢复记录',
  description: '统一维护备份、恢复演练和故障恢复留痕。',
  permPrefix: 'ygb:platformBackup',
  filePrefix: 'platform_backup',
  defaultCategoryCode: 'backup',
  defaultSourceLabel: '备份恢复',
  recordNameLabel: '备份任务',
  recordNamePlaceholder: '请输入备份任务名称',
  listApi: query => listPlatformModule(submodule, query),
  summaryApi: query => getPlatformModuleSummary(submodule, query),
  detailApi: id => getPlatformModule(submodule, id),
  addApi: data => addPlatformModule(submodule, data),
  updateApi: data => updatePlatformModule(submodule, data),
  deleteApi: ids => delPlatformModule(submodule, ids),
  exportUrl: `ygb/platform/${submodule}/export`
}
</script>
