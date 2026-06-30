<template>
  <view class="worker-page worker-page--tab">
    <view class="worker-card worker-hero">
      <view class="section-head">
        <view>
          <view class="worker-title worker-title--display">工作台</view>
          <view class="worker-subtitle">{{ heroSummary }}</view>
        </view>
        <view class="worker-tag worker-tag--notice">培训 {{ trainingProgressText }}</view>
      </view>
      <view class="hero-stat-grid">
        <view class="hero-stat">
          <view class="hero-stat__value">{{ unlockedCount }}</view>
          <view class="hero-stat__label">可用入口</view>
        </view>
        <view class="hero-stat">
          <view class="hero-stat__value">{{ lockedCount }}</view>
          <view class="hero-stat__label">培训锁定</view>
        </view>
        <view class="hero-stat">
          <view class="hero-stat__value">4</view>
          <view class="hero-stat__label">高危补齐</view>
        </view>
      </view>
    </view>

    <view class="worker-card">
      <view class="section-head"><view class="worker-title">高危作业</view></view>
      <view class="entry-grid entry-grid--two">
        <view v-for="item in highRiskEntries" :key="item.key" class="entry-item entry-item--wide" @click="openEntry(item)">
          <view class="entry-item__icon" :class="'entry-item__icon--' + getEntryIcon(item).tone"><text class="entry-item__glyph">{{ getEntryIcon(item).glyph }}</text></view>
          <view class="entry-item__label">{{ item.label }}</view>
        </view>
      </view>
    </view>

    <view class="worker-card">
      <view class="section-head"><view class="worker-title">常用服务</view></view>
      <view v-if="commonEntries.length" class="entry-grid">
        <view v-for="item in commonEntries" :key="item.key" class="entry-item" :class="{ 'entry-item--locked': item.locked }" @click="openEntry(item)">
          <view class="entry-item__icon" :class="'entry-item__icon--' + getEntryIcon(item).tone"><text class="entry-item__glyph">{{ getEntryIcon(item).glyph }}</text></view>
          <view class="entry-item__label">{{ item.label }}</view>
          <view v-if="item.locked" class="entry-item__tip">{{ item.lockReason || '需先完成培训' }}</view>
        </view>
      </view>
      <view v-else class="worker-empty worker-empty--panel">
        <view class="worker-empty__title">常用服务同步中</view>
        <view class="worker-empty__desc">服务入口会在数据返回后展示，暂时可先使用上方高危作业入口。</view>
      </view>
    </view>

    <view class="worker-card">
      <view class="section-head"><view class="worker-title">权益与个人服务</view></view>
      <view v-if="serviceEntries.length" class="entry-grid entry-grid--two">
        <view v-for="item in serviceEntries" :key="item.key" class="entry-item entry-item--wide" @click="openEntry(item)">
          <view class="entry-item__icon" :class="'entry-item__icon--' + getEntryIcon(item).tone"><text class="entry-item__glyph">{{ getEntryIcon(item).glyph }}</text></view>
          <view class="entry-item__label">{{ item.label }}</view>
        </view>
      </view>
      <view v-else class="worker-empty worker-empty--panel">
        <view class="worker-empty__title">权益服务同步中</view>
        <view class="worker-empty__desc">权益、个人服务和内容入口会在后台返回后统一展示。</view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed, onMounted, reactive } from 'vue'
import { getWorkerWorkbench } from '../../api/worker'
import { openPage } from '../../utils/navigation'
import { resolveEntryIcon } from '../../utils/entry-icon'
import { normalizeWorkerJumpTarget, openWorkerJumpTarget } from '../../utils/worker-jump'

const loadFailed = reactive({
  commonEntries: false,
  serviceEntries: false
})

const workbench = reactive({
  trainingProgress: { completed: 0, total: 10 },
  commonEntries: [],
  rightsEntries: [],
  personalEntries: [],
  contentEntries: []
})

const highRiskEntries = [
  { key: 'scan-unlock', label: '扫码开机', path: '/pages/high-risk/scan-unlock' },
  { key: 'outwork-apply', label: '外出作业申请', path: '/pages/high-risk/outwork-apply' },
  { key: 'certificates', label: '证件管理', path: '/pages/high-risk/certificates' },
  { key: 'work-records', label: '作业记录', path: '/pages/high-risk/work-records' }
]

const fallbackCommonEntries = [
  { key: 'attendance', label: '考勤', path: '/pages/attendance/checkin' },
  { key: 'salary', label: '工资', path: '/pages/salary/list', locked: true, lockReason: '请先完成本月培训' },
  { key: 'social', label: '社保', path: '/pages/social/list' },
  { key: 'tax', label: '个税', path: '/pages/tax/list' },
  { key: 'training', label: '培训', path: '/pages/training/index' },
  { key: 'job', label: '找工作', path: '/pages/job/list' }
]

const fallbackServiceEntries = [
  { key: 'complaint', label: '投诉举报', path: '/pages/complaint/index' },
  { key: 'legal', label: '法律咨询', path: '/pages/legal/index' },
  { key: 'union', label: '工会服务', path: '/pages/union/index' },
  { key: 'resume', label: '我的简历', path: '/pages/profile/resume' },
  { key: 'contract', label: '我的合同', path: '/pages/profile/labor-contracts' },
  { key: 'help', label: '帮助中心', path: '/pages/profile/help' }
]

const trainingProgressText = computed(() => `${workbench.trainingProgress?.completed || 0}/${workbench.trainingProgress?.total || 10}`)
const commonEntries = computed(() => {
  if (workbench.commonEntries?.length) {
    return workbench.commonEntries
  }
  return loadFailed.commonEntries ? fallbackCommonEntries : []
})
const serviceEntries = computed(() => {
  const merged = [...(workbench.rightsEntries || []), ...(workbench.personalEntries || []), ...(workbench.contentEntries || [])]
  if (merged.length) {
    return merged
  }
  return loadFailed.serviceEntries ? fallbackServiceEntries : []
})
const lockedCount = computed(() => commonEntries.value.filter((item) => item.locked).length)
const unlockedCount = computed(() => commonEntries.value.length + serviceEntries.value.length + highRiskEntries.length - lockedCount.value)
const heroSummary = computed(() => '统一入口、培训解锁、权益查询与高危补齐都在这里。')

function getEntryIcon(item) {
  return resolveEntryIcon(item)
}

function openEntry(item) {
  if (item.locked) {
    uni.showToast({ title: item.lockReason || '请先完成培训', icon: 'none' })
    return
  }
  const target = normalizeWorkerJumpTarget(item)
  if (target?.path) {
    openWorkerJumpTarget(target)
    return
  }
  openPage(item.path)
}

async function loadWorkbench() {
  try {
    const data = await getWorkerWorkbench()
    Object.assign(workbench, data || {})
    loadFailed.commonEntries = false
    loadFailed.serviceEntries = false
  } catch (error) {
    void error
    loadFailed.commonEntries = true
    loadFailed.serviceEntries = true
  }
}

onMounted(loadWorkbench)
</script>
