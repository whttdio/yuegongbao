<template>
  <div class="app-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>平台治理总览</span>
          <span class="card-header__tip">业务治理对象聚合到 YGB 视角，底层系统监控继续复用现有 system / monitor 模块。</span>
        </div>
      </template>

      <div class="summary-grid">
        <div v-for="item in summaryCards" :key="item.key" class="summary-card">
          <div class="summary-card__label">{{ item.label }}</div>
          <div class="summary-card__value">{{ item.value }}</div>
          <div class="summary-card__note">{{ item.note }}</div>
        </div>
      </div>

      <div class="runtime-grid" v-if="server">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="CPU 核数">{{ server.cpu?.cpuNum ?? '-' }}</el-descriptions-item>
          <el-descriptions-item label="CPU 使用率">{{ server.cpu?.used ?? '-' }}%</el-descriptions-item>
          <el-descriptions-item label="内存使用率">{{ server.mem?.usage ?? '-' }}%</el-descriptions-item>
          <el-descriptions-item label="JVM 使用率">{{ server.jvm?.usage ?? '-' }}%</el-descriptions-item>
        </el-descriptions>
      </div>

      <div class="entry-grid">
        <button v-for="item in entries" :key="item.path" type="button" class="entry-card" @click="router.push(item.path)">
          <strong>{{ item.title }}</strong>
          <p>{{ item.desc }}</p>
          <span>{{ item.action }}</span>
        </button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getPlatformRuntimeSummary } from '@/api/ygb/platform'

const router = useRouter()
const runtime = ref({})

const server = computed(() => runtime.value.server)
const summaryCards = computed(() => {
  const documentSummary = runtime.value.documentSummary || {}
  const exchangeSummary = runtime.value.exchangeSummary || {}
  const securityAuditSummary = runtime.value.securityAuditSummary || {}
  const backupSummary = runtime.value.backupSummary || {}
  return [
    { key: 'document', label: '文档管理', value: documentSummary.totalCount ?? 0, note: `启用 ${documentSummary.enabledCount ?? 0}` },
    { key: 'exchange', label: '交换监控', value: exchangeSummary.totalCount ?? 0, note: `待处理 ${exchangeSummary.pendingCount ?? 0}` },
    { key: 'security', label: '安全审计', value: securityAuditSummary.totalCount ?? 0, note: `逾期 ${securityAuditSummary.overdueCount ?? 0}` },
    { key: 'backup', label: '备份恢复', value: backupSummary.totalCount ?? 0, note: `已关闭 ${backupSummary.closedCount ?? 0}` },
    { key: 'operLog', label: '操作日志', value: runtime.value.operLogCount ?? 0, note: `异常 ${runtime.value.errorOperLogCount ?? 0}` },
    { key: 'loginInfo', label: '登录日志', value: runtime.value.loginInfoCount ?? 0, note: `失败 ${runtime.value.loginFailCount ?? 0}` }
  ]
})

const entries = [
  { path: '/platform/document', title: '文档管理', desc: '维护上线文档、接口说明、验收交付附件。', action: '进入文档台账' },
  { path: '/platform/exchange', title: '接口与数据交换监控', desc: '跟踪数据交换异常、回写和人工补偿处理。', action: '进入交换监控' },
  { path: '/platform/securityAudit', title: '安全审计', desc: '集中处理安全审计事项和整改闭环。', action: '进入安全审计' },
  { path: '/platform/backup', title: '备份恢复记录', desc: '沉淀备份、恢复和演练记录。', action: '进入备份恢复' }
]

onMounted(() => {
  getPlatformRuntimeSummary().then(response => {
    runtime.value = response.data || {}
  })
})
</script>

<style scoped>
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.card-header__tip {
  color: var(--el-text-color-secondary);
  font-size: 13px;
}

.summary-grid,
.entry-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 12px;
}

.summary-grid {
  margin-bottom: 18px;
}

.summary-card,
.entry-card {
  border: 0;
  border-radius: 12px;
  background: linear-gradient(135deg, #f6f9fc 0%, #eef4fb 100%);
  text-align: left;
  padding: 16px;
}

.summary-card__label,
.entry-card span {
  color: var(--el-text-color-secondary);
  font-size: 13px;
}

.summary-card__value {
  margin-top: 8px;
  font-size: 28px;
  font-weight: 600;
  color: #123b67;
}

.summary-card__note,
.entry-card p {
  margin-top: 8px;
  color: var(--el-text-color-regular);
}

.entry-card strong {
  display: block;
  font-size: 16px;
  color: #123b67;
}

.runtime-grid {
  margin-bottom: 18px;
}
</style>
