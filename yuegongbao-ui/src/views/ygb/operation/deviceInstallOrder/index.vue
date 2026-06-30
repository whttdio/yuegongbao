<template>
  <div class="app-container device-operation-page">
    <div class="module-head">
      <div>
        <div class="module-title">设备安装运维</div>
        <div class="module-desc">按文档二级菜单集中承载安装工单、维修工单、巡检计划和运维统计，保持入口收敛、功能完整。</div>
      </div>
    </div>

    <el-tabs v-model="activeTab" class="operation-tabs">
      <el-tab-pane v-for="item in tabs" :key="item.name" :name="item.name" lazy>
        <template #label>
          <span class="tab-label">
            <el-icon><component :is="item.icon" /></el-icon>
            <span>{{ item.label }}</span>
          </span>
        </template>
        <business-record-page :config="item.config" class="embedded-record-page" />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { DataAnalysis, Finished, SetUp, Tools } from '@element-plus/icons-vue'
import BusinessRecordPage from '@/views/ygb/shared/BusinessRecordPage.vue'
import { createBusinessRecordPageConfig } from '@/views/ygb/shared/businessRecordPageConfig.js'

const commonFilters = ['statMonth', 'regionCode', 'enterpriseId', 'enterpriseName', 'personName', 'businessName', 'riskLevel', 'workflowStatus', 'status']

const activeTab = ref('install')

const tabs = [
  {
    name: 'install',
    label: '安装工单',
    icon: SetUp,
    config: createBusinessRecordPageConfig({
      module: 'deviceInstallOrder',
      title: '安装工单',
      description: '围绕设备安装派单、上门安装、验收流转和结果归档开展查询、办理、处置跟踪和台账导出。',
      businessNameLabel: '安装工单',
      businessNamePlaceholder: '请输入安装工单',
      defaultSourceLabel: '设备安装工单',
      filters: commonFilters
    })
  },
  {
    name: 'repair',
    label: '维修工单',
    icon: Tools,
    config: createBusinessRecordPageConfig({
      module: 'deviceRepairOrder',
      title: '维修工单',
      description: '围绕设备维修报修、故障处理、恢复上线和维修结果归档开展查询、办理、处置跟踪和台账导出。',
      businessNameLabel: '维修工单',
      businessNamePlaceholder: '请输入维修工单',
      defaultSourceLabel: '设备维修工单',
      filters: commonFilters
    })
  },
  {
    name: 'inspect',
    label: '巡检计划',
    icon: Finished,
    config: createBusinessRecordPageConfig({
      module: 'deviceInspectPlan',
      title: '巡检计划',
      description: '围绕设备巡检计划、执行进度、异常问题和巡检结果开展查询、办理、处置跟踪和台账导出。',
      businessNameLabel: '巡检计划',
      businessNamePlaceholder: '请输入巡检计划',
      defaultSourceLabel: '设备巡检计划',
      filters: commonFilters
    })
  },
  {
    name: 'stats',
    label: '运维统计',
    icon: DataAnalysis,
    config: createBusinessRecordPageConfig({
      module: 'operationMaintenanceStats',
      title: '运维统计',
      description: '围绕安装、维修、巡检数据和处理效率开展统计分析、处置跟踪和台账导出。',
      businessNameLabel: '运维指标',
      businessNamePlaceholder: '请输入运维指标',
      defaultSourceLabel: '设备运维统计',
      filters: commonFilters
    })
  }
]
</script>

<style scoped>
.device-operation-page {
  background: #f5f7fb;
}

.module-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 12px;
  padding: 16px 18px;
  border: 1px solid #e4e9f0;
  border-radius: 6px;
  background: #fff;
}

.module-title {
  color: var(--el-text-color-primary);
  font-size: 18px;
  font-weight: 600;
}

.module-desc {
  margin-top: 6px;
  color: var(--el-text-color-secondary);
  font-size: 13px;
  line-height: 20px;
}

.operation-tabs {
  padding: 0 12px 12px;
  border: 1px solid #e4e9f0;
  border-radius: 6px;
  background: #fff;
}

.tab-label {
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

:deep(.embedded-record-page) {
  padding: 0;
}

:deep(.embedded-record-page > .el-card) {
  border: 0;
}

:deep(.embedded-record-page > .el-card > .el-card__header) {
  padding-left: 0;
  padding-right: 0;
}

:deep(.embedded-record-page > .el-card > .el-card__body) {
  padding-left: 0;
  padding-right: 0;
  padding-bottom: 0;
}
</style>
