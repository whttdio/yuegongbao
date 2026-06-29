<template>
  <div class="app-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>运营后台总览</span>
          <span class="card-header__tip">集中承接企业入驻、岗位、简历、广告轮播和消息办理，统一走同一门户和权限体系。</span>
        </div>
      </template>

      <div class="summary-grid">
        <div v-for="item in summaryCards" :key="item.key" class="summary-card">
          <div class="summary-card__label">{{ item.label }}</div>
          <div class="summary-card__value">{{ item.value }}</div>
          <div class="summary-card__note">{{ item.note }}</div>
        </div>
      </div>

      <div class="entry-grid">
        <button v-for="item in entries" :key="item.title" type="button" class="entry-card" @click="router.push(item.to)">
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
import { getOperationOverview } from '@/api/ygb/operation'

const router = useRouter()
const overview = ref({})

const summaryCards = computed(() => {
  const enterpriseReviewSummary = overview.value.enterpriseReviewSummary || {}
  const messageSummary = overview.value.messageSummary || {}
  return [
    { key: 'enterpriseReview', label: '企业入驻审核', value: enterpriseReviewSummary.totalCount ?? 0, note: `待处理 ${enterpriseReviewSummary.pendingCount ?? 0}` },
    { key: 'message', label: '运营消息', value: messageSummary.totalCount ?? 0, note: `处理中 ${messageSummary.processingCount ?? 0}` },
    { key: 'job', label: '岗位审核', value: overview.value.jobCount ?? 0, note: '跟踪岗位发布、审核和上下架状态' },
    { key: 'resume', label: '简历管理', value: overview.value.resumeCount ?? 0, note: '跟踪简历投递、审核和流转状态' }
  ]
})

const entries = [
  { to: '/operation-backend/enterpriseReview', title: '企业入驻审核', desc: '查看企业入驻审核台账、状态流转和导出。', action: '进入审核台账' },
  { to: '/operation-backend/jobReview', title: '岗位审核', desc: '复核企业岗位发布信息和招聘状态。', action: '进入岗位审核' },
  { to: '/operation-backend/resume', title: '简历管理', desc: '查看劳动者简历、技能和期望岗位信息。', action: '进入简历管理' },
  { to: { path: '/operation-backend/banner', query: { portalCode: 'ygb', sectionCode: 'banner' } }, title: '广告轮播', desc: '通过门户内容后台维护 banner 栏目。', action: '进入内容管理' },
  { to: '/operation-backend/message', title: '消息推送', desc: '维护运营消息对象和内部推送闭环。', action: '进入消息中心' },
  { to: '/operation-backend/jobCategory', title: '职位分类', desc: '维护工种分类、薪资范围与工作类型字典。', action: '进入职位分类' },
  { to: '/operation-backend/dataAnalysis', title: '招聘数据统计', desc: '岗位发布、投递与匹配成功率分析。', action: '进入招聘统计' },
  { to: '/operation-backend/deviceOperation', title: '设备安装运维', desc: '统一承载安装工单、维修工单、巡检计划和运维统计。', action: '进入工单中心' }
]

onMounted(() => {
  getOperationOverview().then(response => {
    overview.value = response.data || {}
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
</style>
