<template>
  <div class="app-container ygb-page">
    <section class="gov-page-header ygb-page__header">
      <div>
        <p class="ygb-page__eyebrow">企业自助服务</p>
        <h1 class="ygb-page__title">企业后台仪表盘</h1>
        <p class="ygb-page__desc">
          面向企业管理员的一站式自助服务入口，集中展示企业人员、设备、工资、作业、保险、培训、招聘、财务及信用等核心运营数据。
        </p>
      </div>
    </section>

    <el-alert
      v-if="loadError"
      class="enterprise-load-alert"
      :title="loadError"
      type="warning"
      show-icon
      :closable="false"
    />

    <div class="ygb-summary-grid">
      <div class="ygb-summary-card primary">
        <div class="ygb-summary-card__label">企业健康度</div>
        <div class="ygb-summary-card__value">
          {{ dashboard.hero.healthScore }}
          <span class="ygb-summary-card__unit">分</span>
        </div>
        <div class="ygb-summary-card__note">综合信用与安全评分</div>
      </div>
      <div class="ygb-summary-card warning">
        <div class="ygb-summary-card__label">待处理预警</div>
        <div class="ygb-summary-card__value">
          {{ dashboard.hero.warningCount }}
          <span class="ygb-summary-card__unit">条</span>
        </div>
        <div class="ygb-summary-card__note">设备/人员/作业异常</div>
      </div>
      <div class="ygb-summary-card success">
        <div class="ygb-summary-card__label">待办任务</div>
        <div class="ygb-summary-card__value">
          {{ dashboard.hero.taskCount }}
          <span class="ygb-summary-card__unit">项</span>
        </div>
        <div class="ygb-summary-card__note">工资/审批/培训待处理</div>
      </div>
      <div class="ygb-summary-card">
        <div class="ygb-summary-card__label">待跟进事项</div>
        <div class="ygb-summary-card__value">
          {{ dashboard.hero.todoCount }}
          <span class="ygb-summary-card__unit">项</span>
        </div>
        <div class="ygb-summary-card__note">证件/参保/其他待跟进</div>
      </div>
    </div>

    <el-row :gutter="16" class="entry-sections">
      <el-col :span="24">
        <el-card shadow="never" class="dashboard-card">
          <template #header>
            <span>核心数据概览</span>
          </template>
          <div class="data-card-grid">
            <div v-for="item in dashboard.dataCards" :key="item.label" class="data-card">
              <div class="data-card__value">{{ item.value }}</div>
              <div class="data-card__label">{{ item.label }}</div>
              <div class="data-card__note">{{ item.note || item.desc }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="entry-sections">
      <el-col :span="24">
        <el-card shadow="never" class="dashboard-card">
          <template #header>
            <span>快捷入口</span>
          </template>
          <div class="quick-entry-grid">
            <button
              v-for="item in quickEntries"
              :key="item.title"
              type="button"
              class="quick-entry-card"
              @click="router.push(item.to)"
            >
              <strong>{{ item.title }}</strong>
              <p>{{ item.desc }}</p>
              <span>{{ item.action }}</span>
            </button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="entry-sections">
      <el-col :span="24">
        <el-card shadow="never" class="dashboard-card">
          <template #header>
            <span>工作台分区</span>
          </template>
          <div v-for="section in dashboard.workbenchSections" :key="section.title" class="workbench-section">
            <div class="workbench-section__title">
              {{ section.title }}
              <el-tag size="small" :type="section.priority === 'P0' ? 'danger' : 'info'">{{ section.priority }}</el-tag>
            </div>
            <div class="workbench-section__items">
              <div
                v-for="entry in section.entries"
                :key="entry.label"
                class="workbench-section__item"
                @click="handleWorkbenchEntry(entry)"
              >
                <div class="workbench-section__icon" :style="{ backgroundColor: entry.color }">{{ entry.icon }}</div>
                <span>{{ entry.label }}</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="entry-sections">
      <el-col :span="24">
        <el-card shadow="never" class="dashboard-card">
          <template #header>
            <span>预警与提醒</span>
          </template>
          <el-empty v-if="!dashboard.warnings.length" description="暂无预警" />
          <el-timeline v-else>
            <el-timeline-item
              v-for="(item, index) in dashboard.warnings"
              :key="index"
              :type="item.type || 'warning'"
              :timestamp="item.time"
            >
              {{ item.title }}
              <p v-if="item.desc" class="warning-desc">{{ item.desc }}</p>
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup name="YgbEnterprisePortalDashboard">
import { useRouter } from 'vue-router'
import { getEnterpriseHomeDashboard, getEnterpriseWorkbenchDashboard } from '@/api/ygb/enterprisePortal'
import { getEnterpriseCreditScore } from '@/api/ygb/creditScore'
import { lockedEnterpriseId } from '@/utils/enterpriseScope'
import { resolveMenuIcon } from '@/utils/menuIcon'

const router = useRouter()

const loading = ref(false)
const loadError = ref('')
const dashboard = ref({
  hero: {
    healthScore: '--',
    warningCount: '0',
    taskCount: '0',
    todoCount: '0'
  },
  dataCards: [],
  warnings: [],
  workbenchSections: []
})

const DOC_WORKBENCH_KEYS = new Set(['dashboard', 'person', 'device', 'salary', 'work', 'insurance', 'training', 'recruit', 'finance', 'credit'])

const quickEntries = [
  { to: '/enterprise-portal/dashboard', title: '企业仪表盘', desc: '查看企业合规总览和关键指标。', action: '进入总览' },
  { to: '/enterprise-portal/person', title: '企业人员管理', desc: '员工花名册、入离职与证件管理。', action: '进入人员管理' },
  { to: '/enterprise-portal/device', title: '企业设备管理', desc: '本企业设备台账、故障报修与物联卡。', action: '进入设备管理' },
  { to: '/enterprise-portal/salary', title: '企业工资管理', desc: '工资批次确认、明细导入与代发。', action: '进入工资管理' },
  { to: '/enterprise-portal/work', title: '企业作业管理', desc: '高处作业申报、违规记录与审批。', action: '进入作业管理' },
  { to: '/enterprise-portal/insurance', title: '企业保险管理', desc: '工伤保险、安责险投保与事故预防。', action: '进入保险管理' },
  { to: '/enterprise-portal/training', title: '企业培训管理', desc: '培训计划、学习进度与学时证明。', action: '进入培训管理' },
  { to: '/enterprise-portal/recruit', title: '企业招聘管理', desc: '岗位发布、简历查看与面试邀约。', action: '进入招聘管理' },
  { to: '/enterprise-portal/finance', title: '企业财务管理', desc: '工资确认、监管账户与物联卡运维入口。', action: '进入财务管理' },
  { to: '/enterprise-portal/credit', title: '企业信用报告', desc: '查看本企业信用评分与整改建议。', action: '进入信用评价' }
]

const workbenchRouteMap = {
  dashboard: '/enterprise-portal/dashboard',
  person: '/enterprise-portal/person',
  device: '/enterprise-portal/device',
  salary: '/enterprise-portal/salary',
  work: '/enterprise-portal/work',
  insurance: '/enterprise-portal/insurance',
  training: '/enterprise-portal/training',
  recruit: '/enterprise-portal/recruit',
  finance: '/enterprise-portal/finance',
  credit: '/enterprise-portal/credit'
}

function resolveEntryColor(colorName) {
  const colorMap = {
    gold: '#f59e0b',
    teal: '#14b8a6',
    blue: '#3b82f6',
    rose: '#f43f5e',
    green: '#22c55e',
    cyan: '#06b6d4',
    orange: '#f97316',
    indigo: '#6366f1',
    violet: '#8b5cf6'
  }
  return colorMap[colorName] || '#3b82f6'
}

function normalizeWorkbenchKey(entry = {}) {
  const raw = String(entry.key || entry.anchor || entry.path || entry.label || entry.title || '').toLowerCase()
  if (['dashboard', 'overview', 'cockpit', 'home'].includes(raw) || raw.includes('仪表') || raw.includes('总览')) return 'dashboard'
  if (raw.includes('person') || raw.includes('people') || raw.includes('entry') || raw.includes('cert') || raw.includes('保险') || raw.includes('人员')) return 'person'
  if (raw.includes('device') || raw.includes('repair') || raw.includes('iot') || raw.includes('warning') || raw.includes('设备')) return 'device'
  if (raw.includes('salary') || raw.includes('payroll') || raw.includes('工资') || raw.includes('财务')) return 'salary'
  if (raw.includes('work') || raw.includes('approval') || raw.includes('作业')) return 'work'
  if (raw.includes('injury') || raw.includes('aq') || raw.includes('insurance') || raw.includes('社保') || raw.includes('保险')) return 'insurance'
  if (raw.includes('plan') || raw.includes('exam') || raw.includes('training') || raw.includes('培训')) return 'training'
  if (raw.includes('job') || raw.includes('resume') || raw.includes('recruit') || raw.includes('招聘') || raw.includes('岗位')) return 'recruit'
  if (raw.includes('finance') || raw.includes('bill') || raw.includes('财务')) return 'finance'
  if (raw.includes('credit') || raw.includes('信用')) return 'credit'
  return ''
}

function resolveWorkbenchIcon(entry = {}, section = {}) {
  return resolveMenuIcon(
    {
      title: entry.label || entry.title || '',
      icon: entry.icon || entry.glyph || ''
    },
    {
      title: section.title || ''
    }
  )
}

function transformWorkbenchSections(sections = []) {
  return sections.map(section => ({
    title: section.title,
    priority: section.priority,
    entries: (section.entries || section.items || [])
      .map(entry => {
        const normalizedKey = normalizeWorkbenchKey(entry)
        return normalizedKey && DOC_WORKBENCH_KEYS.has(normalizedKey) ? {
          key: normalizedKey,
          label: entry.label || entry.title || '',
          icon: resolveWorkbenchIcon(entry, section),
          color: resolveEntryColor(entry.color || entry.tone),
          anchor: entry.anchor,
          path: entry.path,
          linkUrl: entry.linkUrl
        } : null
      })
      .filter(Boolean)
  }))
}

function handleWorkbenchEntry(entry) {
  if (entry.linkUrl && entry.linkUrl.startsWith('http')) {
    window.open(entry.linkUrl, '_blank')
    return
  }
  const targetPath = workbenchRouteMap[entry.key]
  if (targetPath) {
    router.push(targetPath).catch(() => {})
  }
}

function isUnboundEnterpriseError(error) {
  return String(error?.message || error || '').includes('当前账号未绑定企业权限')
}

async function loadDashboard() {
  loading.value = true
  loadError.value = ''
  try {
    const [homeRes, workbenchRes] = await Promise.all([
      getEnterpriseHomeDashboard(),
      getEnterpriseWorkbenchDashboard()
    ])
    const homeData = homeRes.data || {}
    const workbenchData = workbenchRes.data || {}

    dashboard.value.hero = homeData.hero || dashboard.value.hero
    dashboard.value.dataCards = homeData.dataCards || []
    dashboard.value.warnings = (homeData.warnings || []).map(w => ({
      type: w.type || 'warning',
      time: w.time || w.date || '',
      title: w.title || w.message || '',
      desc: w.desc || ''
    }))
    dashboard.value.workbenchSections = transformWorkbenchSections(workbenchData.sections || [])

    const enterpriseId = lockedEnterpriseId()
    if (enterpriseId) {
      try {
        const creditRes = await getEnterpriseCreditScore(enterpriseId)
        if (creditRes.data && creditRes.data.totalScore != null) {
          dashboard.value.hero.healthScore = String(creditRes.data.totalScore)
        }
      } catch (e) {
        // 信用评分接口失败时使用 home dashboard 默认值
      }
    }
  } catch (error) {
    dashboard.value.dataCards = []
    dashboard.value.warnings = []
    dashboard.value.workbenchSections = []
    if (isUnboundEnterpriseError(error)) {
      loadError.value = '当前账号未绑定企业，企业后台仅展示只读空态。请为账号绑定企业后办理企业侧业务。'
      console.warn('Enterprise portal dashboard is empty because current account is not bound to an enterprise.')
    } else {
      loadError.value = '企业后台数据加载失败，请稍后重试。'
      console.error('Enterprise portal dashboard failed to load:', error)
    }
  } finally {
    loading.value = false
  }
}

loadDashboard()
</script>

<style scoped>
.entry-sections {
  margin-top: 16px;
}

.enterprise-load-alert {
  margin-bottom: 16px;
}

.dashboard-card {
  margin-bottom: 16px;
}

.data-card-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
}

.data-card {
  padding: 18px;
  border: 1px solid var(--el-border-color-light);
  border-radius: 12px;
  background: var(--el-bg-color);
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.data-card:hover {
  border-color: var(--el-color-primary-light-5);
  box-shadow: 0 8px 24px rgba(15, 94, 168, 0.08);
}

.data-card__value {
  font-size: 28px;
  font-weight: 600;
  color: var(--el-text-color-primary);
  line-height: 1.2;
}

.data-card__label {
  margin-top: 8px;
  font-size: 14px;
  color: var(--el-text-color-regular);
}

.data-card__note {
  margin-top: 4px;
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.quick-entry-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
}

.quick-entry-card {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 8px;
  padding: 16px;
  border: 1px solid var(--el-border-color-light);
  border-radius: 12px;
  background: var(--el-bg-color);
  text-align: left;
  cursor: pointer;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.quick-entry-card:hover {
  border-color: var(--el-color-primary-light-5);
  box-shadow: 0 8px 24px rgba(15, 94, 168, 0.08);
}

.quick-entry-card strong {
  font-size: 15px;
  color: var(--el-text-color-primary);
}

.quick-entry-card p {
  margin: 0;
  color: var(--el-text-color-secondary);
  font-size: 13px;
  line-height: 1.5;
}

.quick-entry-card span {
  color: var(--el-color-primary);
  font-size: 13px;
}

.workbench-section {
  margin-bottom: 20px;
}

.workbench-section:last-child {
  margin-bottom: 0;
}

.workbench-section__title {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
  font-size: 15px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.workbench-section__items {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.workbench-section__item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 14px;
  border: 1px solid var(--el-border-color-light);
  border-radius: 8px;
  cursor: pointer;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.workbench-section__item:hover {
  border-color: var(--el-color-primary-light-5);
  box-shadow: 0 4px 12px rgba(15, 94, 168, 0.08);
}

.workbench-section__icon {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 6px;
  color: #fff;
  font-size: 14px;
  font-weight: 600;
}

.warning-desc {
  margin: 4px 0 0 0;
  font-size: 13px;
  color: var(--el-text-color-secondary);
}

@media (max-width: 1200px) {
  .data-card-grid,
  .quick-entry-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 768px) {
  .data-card-grid,
  .quick-entry-grid {
    grid-template-columns: 1fr;
  }
}
</style>
