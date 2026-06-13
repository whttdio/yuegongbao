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

const submodule = 'securityAudit'
const config = {
  title: '安全审计',
  description: '聚合安全审计记录、问题整改状态和人工复核留痕。',
  permPrefix: 'ygb:platformSecurityAudit',
  filePrefix: 'platform_security_audit',
  defaultCategoryCode: 'security_audit',
  defaultSourceLabel: '安全审计中心',
  recordNameLabel: '审计事项',
  recordNamePlaceholder: '请输入审计事项',
  listApi: query => listPlatformModule(submodule, query),
  summaryApi: query => getPlatformModuleSummary(submodule, query),
  detailApi: id => getPlatformModule(submodule, id),
  addApi: data => addPlatformModule(submodule, data),
  updateApi: data => updatePlatformModule(submodule, data),
  deleteApi: ids => delPlatformModule(submodule, ids),
  exportUrl: `ygb/platform/${submodule}/export`
}
</script>
