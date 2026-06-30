<template>
  <div class="app-container citizen-overview">
    <section class="overview-hero">
      <div>
        <p class="overview-hero__eyebrow">便民服务运营台</p>
        <h1 class="overview-hero__title">移动端便民服务运营看板</h1>
        <p class="overview-hero__desc">
          汇总暖新地图、培训课程、法规库、互助圈和招聘用工市场的运营状态，PC 端负责配置、处理和联动移动端真实业务入口。
        </p>
      </div>
      <el-button type="primary" plain icon="Refresh" @click="refreshAll">刷新数据</el-button>
    </section>

    <div class="module-grid">
      <button v-for="item in moduleCards" :key="item.key" class="module-card" type="button" @click="openModule(item)">
        <div class="module-card__head">
          <span>{{ item.title }}</span>
          <el-icon><component :is="item.icon" /></el-icon>
        </div>
        <strong>{{ item.value }}</strong>
        <p>{{ item.desc }}</p>
        <small>{{ item.mobile }}</small>
      </button>
    </div>

    <el-row :gutter="16">
      <el-col :span="12">
        <el-card shadow="never" class="overview-panel">
          <template #header>
            <div class="panel-head">
              <strong>移动端业务承接</strong>
              <span>PC 侧配置和处理范围</span>
            </div>
          </template>
          <el-timeline>
            <el-timeline-item
              v-for="item in businessFlows"
              :key="item.title"
              :type="item.type"
              :timestamp="item.path"
            >
              <strong>{{ item.title }}</strong>
              <p>{{ item.desc }}</p>
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="never" class="overview-panel">
          <template #header>
            <div class="panel-head">
              <strong>待跟进事项</strong>
              <span>来自投诉、咨询和岗位运营</span>
            </div>
          </template>
          <div class="todo-list">
            <div v-for="item in todoCards" :key="item.key" class="todo-item">
              <span>{{ item.label }}</span>
              <strong>{{ item.value }}</strong>
              <small>{{ item.hint }}</small>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup name="YgbCitizenServiceOverview">
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import {
  listCitizenComplaint,
  listCitizenLegalConsult,
  listCitizenPortalContent,
  listCitizenWorkerJob
} from '@/api/ygb/citizenService'

const router = useRouter()
const loading = ref(false)
const summary = ref({
  warmMap: 0,
  trainingCourse: 0,
  lawLibrary: 0,
  pendingComplaint: 0,
  pendingLegal: 0,
  openJobs: 0,
  jobTotal: 0
})

const moduleCards = computed(() => [
  {
    key: 'warmMap',
    title: '暖新地图',
    value: summary.value.warmMap,
    desc: '服务点、驿站、工会/法律/就业服务网点',
    mobile: '/pages/job/map',
    icon: 'MapLocation',
    to: '/citizen-service/warmMap'
  },
  {
    key: 'trainingCourse',
    title: '培训课程',
    value: summary.value.trainingCourse,
    desc: '移动端培训课程、学时、适用对象',
    mobile: '/pages/training/index',
    icon: 'Reading',
    to: '/citizen-service/trainingCourse'
  },
  {
    key: 'lawLibrary',
    title: '法规库',
    value: summary.value.lawLibrary,
    desc: '权益法规、问答、案例和法律咨询内容',
    mobile: '/pages/legal/index',
    icon: 'Document',
    to: '/citizen-service/lawLibrary'
  },
  {
    key: 'mutualHelp',
    title: '互助圈',
    value: summary.value.pendingComplaint + summary.value.pendingLegal,
    desc: '待处理投诉举报和待回复法律咨询',
    mobile: '/pages/complaint/index / /pages/legal/index',
    icon: 'ChatLineSquare',
    to: '/citizen-service/mutualHelp'
  },
  {
    key: 'recruitMarket',
    title: '招聘用工市场',
    value: summary.value.openJobs,
    desc: `在招岗位 / 岗位总数 ${summary.value.jobTotal}`,
    mobile: '/pages/job/list',
    icon: 'Briefcase',
    to: '/citizen-service/recruitMarket'
  }
])

const todoCards = computed(() => [
  { key: 'complaint', label: '待处理投诉', value: summary.value.pendingComplaint, hint: '移动端投诉举报提交' },
  { key: 'legal', label: '待回复咨询', value: summary.value.pendingLegal, hint: '移动端法律咨询提交' },
  { key: 'jobs', label: '开放投递岗位', value: summary.value.openJobs, hint: '移动端找工作可见' }
])

const businessFlows = [
  { title: '服务点配置', desc: '运营人员维护服务点类型、地址、经纬度、电话和移动端展示开关。', path: '/citizen-service/warmMap', type: 'primary' },
  { title: '培训与法规内容运营', desc: '课程、法规内容通过 portal_content 发布，移动端培训和法律入口消费。', path: '培训课程、法规库', type: 'success' },
  { title: '权益诉求处理', desc: '投诉举报和法律咨询在 PC 端受理、回复和办结，移动端查询结果。', path: '/citizen-service/mutualHelp', type: 'warning' },
  { title: '岗位发布与投递', desc: '岗位维护复用 worker_job 数据源，确保 PC 与移动端招聘市场一致。', path: '/citizen-service/recruitMarket', type: 'info' }
]

function refreshAll() {
  loading.value = true
  Promise.all([
    countPortalSection('warm_map'),
    countPortalSection('training_course'),
    countPortalSection('law_library'),
    listCitizenComplaint({ pageNum: 1, pageSize: 1, status: '0' }),
    listCitizenLegalConsult({ pageNum: 1, pageSize: 1, status: '0' }),
    listCitizenWorkerJob({ pageNum: 1, pageSize: 1, status: '0' }),
    listCitizenWorkerJob({ pageNum: 1, pageSize: 1 })
  ]).then(([warmMap, trainingCourse, lawLibrary, complaint, legal, openJobs, allJobs]) => {
    summary.value = {
      warmMap,
      trainingCourse,
      lawLibrary,
      pendingComplaint: complaint.total || 0,
      pendingLegal: legal.total || 0,
      openJobs: openJobs.total || 0,
      jobTotal: allJobs.total || 0
    }
  }).finally(() => {
    loading.value = false
  })
}

function countPortalSection(sectionCode) {
  return listCitizenPortalContent({
    pageNum: 1,
    pageSize: 1,
    portalCode: 'ygb',
    sectionCode,
    status: '0'
  }).then(response => response.total || 0)
}

function openModule(item) {
  router.push(item.to)
}

refreshAll()
</script>

<style scoped>
.citizen-overview {
  background: #f6f8fb;
}

.overview-hero {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 24px;
  margin-bottom: 16px;
  padding: 24px 28px;
  border-radius: 8px;
  color: #fff;
  background: linear-gradient(135deg, #0f172a 0%, #155e75 58%, #0f766e 100%);
}

.overview-hero__eyebrow {
  margin: 0 0 8px;
  font-size: 13px;
  opacity: 0.82;
}

.overview-hero__title {
  margin: 0;
  font-size: 26px;
  font-weight: 700;
}

.overview-hero__desc {
  max-width: 760px;
  margin: 10px 0 0;
  font-size: 14px;
  line-height: 1.7;
  opacity: 0.92;
}

.module-grid {
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  gap: 16px;
  margin-bottom: 16px;
}

.module-card {
  min-height: 190px;
  padding: 18px 20px;
  border: none;
  border-radius: 8px;
  background: #fff;
  text-align: left;
  cursor: pointer;
  box-shadow: 0 8px 24px rgba(15, 23, 42, 0.05);
}

.module-card:hover {
  box-shadow: 0 12px 28px rgba(15, 23, 42, 0.1);
}

.module-card__head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  color: #475569;
  font-size: 14px;
}

.module-card strong {
  display: block;
  margin-top: 18px;
  color: #0f172a;
  font-size: 32px;
  line-height: 1;
}

.module-card p {
  min-height: 42px;
  margin: 14px 0 10px;
  color: #334155;
  font-size: 13px;
  line-height: 1.6;
}

.module-card small {
  color: #64748b;
  word-break: break-all;
}

.overview-panel {
  border: none;
  border-radius: 8px;
}

.panel-head strong,
.panel-head span {
  display: block;
}

.panel-head span {
  margin-top: 4px;
  color: #64748b;
  font-size: 13px;
}

.todo-list {
  display: grid;
  grid-template-columns: 1fr;
  gap: 14px;
}

.todo-item {
  padding: 16px 18px;
  border-radius: 8px;
  background: #f8fafc;
}

.todo-item span,
.todo-item small {
  display: block;
  color: #64748b;
}

.todo-item strong {
  display: block;
  margin: 8px 0;
  color: #0f172a;
  font-size: 28px;
}

:deep(.el-timeline-item__content p) {
  margin: 6px 0 0;
  color: #64748b;
  line-height: 1.6;
}

@media (max-width: 1400px) {
  .module-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}

@media (max-width: 900px) {
  .module-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 768px) {
  .overview-hero {
    flex-direction: column;
    align-items: flex-start;
  }

  .module-grid {
    grid-template-columns: 1fr;
  }
}
</style>
